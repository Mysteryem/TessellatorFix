/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.co.mysterymayhem.tessellatorfix;

import com.google.common.eventbus.EventBus;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.relauncher.IFMLCallHook;
import java.util.Arrays;
import java.util.Map;
import net.minecraft.launchwrapper.IClassTransformer;

/**
 *
 * @author Thomas
 */
public class DummyMod extends DummyModContainer implements IFMLCallHook, IClassTransformer {

  public DummyMod() {
    super(new ModMetadata());
    ModMetadata meta = this.getMetadata();
    meta.modId = "myst_tessellator_fix";
    meta.name = "MysteryemTessellatorFix";
    meta.version = "@VERSION@";
    meta.credits = "Thanks to culegooner's ASM guide which in turn thanks AtomicStryker, Pahimar and denoflions.";
    meta.authorList = Arrays.asList("Mysteryem");
    meta.description = "CoreMod to fix Tessellator issues, see MinecraftForge #981";
    meta.url = "http://gamingmasters.org";
    meta.updateUrl = "";
    meta.screenshots = new String[0];
    meta.logoFile = "";
  }

  @Override
  public boolean registerBus(EventBus bus, LoadController controller) {
    bus.register(this);
    return true;
  }
  
  

  @Override
  public void injectData(Map<String, Object> data) {
    // nothing to do here
  }

  @Override
  public Void call() throws Exception {
    // nothign to do here
    return null;
  }

  @Override
  public byte[] transform(String string, String string1, byte[] bytes) {
    return bytes;
  }
  
  
}
