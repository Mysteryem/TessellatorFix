package uk.co.mysterymayhem.debugmod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.network.datasync.DataParameter;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;

/**
 * Created by Mysteryem on 2016-10-25.
 */
@SuppressWarnings({"unchecked", "unused"})
public class Hooks {
    private static final DataParameter<Float> EntityBoat$DAMAGE_TAKEN;
    static {
        Field field = ReflectionHelper.findField(EntityBoat.class, "field_184464_c", "DAMAGE_TAKEN");
        DataParameter<Float> temp;
        try {
            temp = (DataParameter<Float>)field.get(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            temp = null;
        }
        EntityBoat$DAMAGE_TAKEN = temp;
        if (EntityBoat$DAMAGE_TAKEN == null) {
            throw new RuntimeException("[MystDebugMod] EntityBoat$Damage_TAKEN Reflection failed. Cannot continue. Aborting");
        }
    }

    public static void dataManagerDebugHook(DataParameter<?> dataParameter, Object newValue) {
        if (newValue != null) {
            if (dataParameter == EntityBoat$DAMAGE_TAKEN) {
                if (newValue.getClass() != Float.class) {
                    throw new RuntimeException(
                            "[MystDebugMod] Some bad code has tried to store an object that isn't a" +
                                    " Float into an EntityBoat's EntityDataManager's DAMAGE_TAKEN key\n" +
                                    "The object trying to be stored is: " + newValue + ", of class: " + newValue.getClass() + "\n" +
                                    "This exception is being pre-emptively thrown before attempting to get the new value throws a ClassCastException");
                }
            }
        }
    }

//    public static void debugHook(Entity entity, Throwable throwableIn) {
//        FMLLog.info("[MystDebugMod] Caught an entity that crashed in net/minecraft/entity/Entity.readFromNBT (net/minecraft/entity/Entity.func_70020_e)");
//        try {
//            FMLLog.info("Crashing entity is a " + entity.getClass().getName());
//        } catch (Throwable throwable) {
//            //nope
//        }
//        try {
//            FMLLog.info("Crashing entity is at (" + entity.posX + ", " + entity.posY + ", " + entity.posZ);
//        } catch (Throwable throwable) {
//            //nope
//        }
//        try {
//            FMLLog.info("Crashing entity is in world " + entity.worldObj.getWorldInfo().getWorldName());
//        } catch (Throwable throwable) {
//            //nope
//        }
//        FMLLog.info("Printing causing throwable:");
//        StringWriter stringWriter = new StringWriter();
//        PrintWriter printWriter = new PrintWriter(stringWriter);
//        printWriter.append("\n");
//        throwableIn.printStackTrace(printWriter);
//        FMLLog.info(stringWriter.toString());
//        FMLLog.info("Preceding to let minecraft probably crash");
//    }
}
