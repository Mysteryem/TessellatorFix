/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.co.mysterymayhem.tessellatorfix;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.function.Function;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import net.minecraftforge.fml.common.FMLLog;
import org.objectweb.asm.util.TraceClassVisitor;

/**
 * @author Mysteryem
 */
public class Transformer implements IClassTransformer {

    private static final String CLASS_NAME = "net.minecraft.client.renderer.Tessellator";

    private static final HashMap<String, Function<byte[], byte[]>> classToMethodMap = new HashMap<>();

    static {
        classToMethodMap.put("net.minecraft.entity.Entity$1", Transformer::patchEntity$1);
        classToMethodMap.put("net.minecraft.entity.Entity", Transformer::patchEntity);
    }

//  private int rawBufferIndex  = 0;
//  private double xOffset = 0;
//  private double yOffset = 0;
//  private double zOffset = 0;
//  private int[] rawBuffer = null;
//  private int vertexCount = 0;
//  private boolean hasTexture = false;
//  private boolean hasBrightness = false;
//  private boolean hasNormals = false;
//  private boolean hasColor = false;

    private static void printClassToFMLLogger(byte[] bytes) {
        ClassNode classNode2 = new ClassNode();
        ClassReader classReader2 = new ClassReader(bytes);
        StringWriter stringWriter = new StringWriter();
        stringWriter.append("\n");
        PrintWriter writer = new PrintWriter(stringWriter);
        TraceClassVisitor traceClassVisitor = new TraceClassVisitor(classNode2, writer);
        classReader2.accept(traceClassVisitor, 0);

        FMLLog.info(stringWriter.toString());
    }

    private static byte[] patchEntity$1(byte[] bytes) {
        FMLLog.info("[MystDebugMod] Printing Entity$1 to console/log:");
        printClassToFMLLogger(bytes);
        FMLLog.info("[MystDebugMod] Printed Entity$1 to constole/log");
        return bytes;
    }

    private static byte[] patchEntity(byte[] bytes) {
        FMLLog.info("[MystDebugMod] Patching Entity class");
        final String readFromNBT_methodName = Plugin.runtimeDeobfEnabled ? "func_70020_e" : "readFromNBT";
        final String makeCrashReport_name = Plugin.runtimeDeobfEnabled ? "func_85055_a" : "makeCrashReport";

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        boolean patchSuccessful = false;

        outerfor:
        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(readFromNBT_methodName)) {
                for (ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator(); iterator.hasNext(); ) {
                    AbstractInsnNode next = iterator.next();
                    if (next instanceof MethodInsnNode) {
                        MethodInsnNode methodInsnNode = (MethodInsnNode)next;
                        if (methodInsnNode.name.equals(makeCrashReport_name)) {
                            iterator.add(new VarInsnNode(Opcodes.ALOAD, 0)); //this
                            iterator.add(new VarInsnNode(Opcodes.ALOAD, 2)); //this, throwable
                            iterator.add(new MethodInsnNode(
                                    Opcodes.INVOKESTATIC,
                                    "uk/co/mysterymayhem/tessellatorfix/Hooks",
                                    "debugHook",
                                    "(Lnet/minecraft/entity/Entity;Ljava/lang/Throwable;)V", false));
                            patchSuccessful = true;
                            break outerfor;
                        }
                    }
                }
            }
        }

        if (!patchSuccessful) {
            throw new RuntimeException("Failed to patch Entity class");
        }

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(classWriter);

        FMLLog.info("[MystDebugMod] Patched Entity class");
        return classWriter.toByteArray();
    }

    public static byte[] patchEntityDataManager(byte[] bytes) {
        String targetMethodName = Plugin.runtimeDeobfEnabled ? "func_187227_b" : "set";

        FMLLog.info("[MystDebugMod] Patching EntityDataManager class");

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        boolean patchSuccessful = false;

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(targetMethodName)) {
                for (ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator(); iterator.hasNext(); ) {
                    AbstractInsnNode next = iterator.next();
                    if (!(next instanceof LabelNode) && !(next instanceof LineNumberNode)) {
                        // Want to add instructions right at the beginning of the method
                        iterator.previous();
                        iterator.add(new VarInsnNode(Opcodes.ALOAD, 1)); // DataParameter<T> key
                        iterator.add(new VarInsnNode(Opcodes.ALOAD, 2)); // key, T value
                        iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "owner", "name", "desc", false));
                    }
                }
            }
        }




        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(classWriter);

        FMLLog.info("[MystDebugMod] Patched EntityDataManager class");
        return classWriter.toByteArray();
    }

    private static byte[] patchTessellatorClass(byte[] bytes) {
        String targetMethodName;

        if (Plugin.runtimeDeobfEnabled) {
            targetMethodName = "func_147564_a";
        }
        else {
            targetMethodName = "getVertexState";
        }

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        Iterator<MethodNode> methods = classNode.methods.iterator();
        while (methods.hasNext()) {
            MethodNode m = methods.next();
            if ((m.name.equals(targetMethodName) && m.desc.equals("(FFF)Lnet/minecraft/client/shader/TesselatorVertexState;"))) {
                FMLLog.info("Inside target Tessellator method");

                InsnList toInject = new InsnList();

                // Insertion of "if (this.rawBufferIndex < 1) return"
                LabelNode labelNode = new LabelNode();

                toInject.add(new VarInsnNode(Opcodes.ALOAD, 0));
                String fieldName;
                if (Plugin.runtimeDeobfEnabled) {
                    fieldName = "field_147569_p";
                }
                else {
                    fieldName = "rawBufferIndex";
                }
                toInject.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/Tessellator", fieldName, "I"));
                toInject.add(new InsnNode(Opcodes.ICONST_1));
                toInject.add(new JumpInsnNode(Opcodes.IF_ICMPGE, labelNode));
                toInject.add(new InsnNode(Opcodes.ACONST_NULL));
                toInject.add(new InsnNode(Opcodes.ARETURN));
                toInject.add(labelNode);


                // Insert after
                m.instructions.insert(toInject);

                ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
                classNode.accept(writer);
                FMLLog.info("Exiting target Tessellator method");
                return writer.toByteArray();
            }
        }

        FMLLog.warning("Could not find Tessellator method out of:");
        StringBuilder builder = new StringBuilder();
        for (MethodNode methodNode : classNode.methods) {
            builder.append(methodNode.name).append(":").append(methodNode.desc).append("\n");
        }
        FMLLog.info(builder.toString());

        return bytes;
    }

    @Override
    public byte[] transform(String className, String transformedClassName, byte[] bytes) {
//        if (!transformedClassName.equals("net.minecraftforge.fml.common.FMLLog")) {
//            FMLLog.info("Transforming %s", transformedClassName);
//        }
//        try {
//
//        } catch (ClassCircularityError error) {
//            //oops
//        }

        Function<byte[], byte[]> function = classToMethodMap.get(transformedClassName);

        if (function != null) {
            bytes = function.apply(bytes);
        }
//        if (transformedClassName.equals(CLASS_NAME)) {
//            return patchTessellatorClass(bytes);
//        }

        return bytes;
    }

//  // Used to work out the correct bytecode to insert
//  
//  public TesselatorVertexState getVertexState(float p_147564_1_, float p_147564_2_, float p_147564_3_)
//    {
//        int[] aint = new int[this.rawBufferIndex];
//        PriorityQueue priorityqueue = new PriorityQueue(this.rawBufferIndex, new QuadComparator(this.rawBuffer, p_147564_1_ + (float)this.xOffset, p_147564_2_ + (float)this.yOffset, p_147564_3_ + (float)this.zOffset));
//        byte b0 = 32;
//        int i;
//
//        for (i = 0; i < this.rawBufferIndex; i += b0)
//        {
//            priorityqueue.add(Integer.valueOf(i));
//        }
//
//        for (i = 0; !priorityqueue.isEmpty(); i += b0)
//        {
//            int j = ((Integer)priorityqueue.remove()).intValue();
//
//            for (int k = 0; k < b0; ++k)
//            {
//                aint[i + k] = this.rawBuffer[j + k];
//            }
//        }
//
//        System.arraycopy(aint, 0, this.rawBuffer, 0, aint.length);
//        return new TesselatorVertexState(aint, this.rawBufferIndex, this.vertexCount, this.hasTexture, this.hasBrightness, this.hasNormals, this.hasColor);
//    }
//  
//  public TesselatorVertexState getVertexStatePatched(float p_147564_1_, float p_147564_2_, float p_147564_3_)
//    {
//        if(this.rawBufferIndex < 1) return null;
//        int[] aint = new int[this.rawBufferIndex];
//        PriorityQueue priorityqueue = new PriorityQueue(this.rawBufferIndex, new QuadComparator(this.rawBuffer, p_147564_1_ + (float)this.xOffset, p_147564_2_ + (float)this.yOffset, p_147564_3_ + (float)this.zOffset));
//        byte b0 = 32;
//        int i;
//
//        for (i = 0; i < this.rawBufferIndex; i += b0)
//        {
//            priorityqueue.add(Integer.valueOf(i));
//        }
//
//        for (i = 0; !priorityqueue.isEmpty(); i += b0)
//        {
//            int j = ((Integer)priorityqueue.remove()).intValue();
//
//            for (int k = 0; k < b0; ++k)
//            {
//                aint[i + k] = this.rawBuffer[j + k];
//            }
//        }
//
//        System.arraycopy(aint, 0, this.rawBuffer, 0, aint.length);
//        return new TesselatorVertexState(aint, this.rawBufferIndex, this.vertexCount, this.hasTexture, this.hasBrightness, this.hasNormals, this.hasColor);
//    }

}
