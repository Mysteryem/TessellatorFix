/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.co.mysterymayhem.tessellatorfix;

import java.util.Iterator;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import cpw.mods.fml.common.FMLLog;
/**
 *
 * @author Thomas
 */
public class Transformer implements IClassTransformer{
  
  private static final String CLASS_NAME = "net.minecraft.client.renderer.Tessellator";
  
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

  @Override
  public byte[] transform(String className, String transformedClassName, byte[] bytes) {
    
    if (bytes == null) {
      return null;
    }
    
    if (transformedClassName.equals(CLASS_NAME)) {
      return patchTessellatorClass(bytes);
    }
    
    return bytes;
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
