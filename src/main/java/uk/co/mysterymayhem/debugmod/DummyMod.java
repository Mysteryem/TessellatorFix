package uk.co.mysterymayhem.debugmod;

import com.google.common.eventbus.EventBus;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.relauncher.IFMLCallHook;

import java.util.Collections;
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
    meta.modId = "myst_debugcoremod";
    meta.name = "MysteryemDebugCoreMod";
    meta.version = "1.0";
    meta.credits = "Mysteryem";
    meta.authorList = Collections.singletonList(("Mysteryem"));
    meta.description = "CoreMod to help with debugging weird or otherwise strange crashes";
    meta.url = "http://gamingmasters.org";
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
