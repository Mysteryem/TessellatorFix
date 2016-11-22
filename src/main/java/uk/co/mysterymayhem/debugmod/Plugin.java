package uk.co.mysterymayhem.debugmod;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import java.util.Map;

/**
 *
 * @author Thomas
 */
@IFMLLoadingPlugin.TransformerExclusions({"uk.co.mysterymayhem.debugmod."})
@IFMLLoadingPlugin.Name(value = "mystdebugcoremod")
@IFMLLoadingPlugin.SortingIndex(value = 9001)
public class Plugin implements IFMLLoadingPlugin{
  
  static boolean runtimeDeobfEnabled = false;

  @Override
  public String[] getASMTransformerClass() {
    return new String[]{Transformer.class.getName()};
  }

  @Override
  public String getModContainerClass() {
    return DummyMod.class.getName();
  }

  @Override
  public String getSetupClass() {
    return DummyMod.class.getName();
  }

  @Override
  public void injectData(Map<String, Object> data) {
    runtimeDeobfEnabled = (Boolean) data.get("runtimeDeobfuscationEnabled");
  }

  @Override
  public String getAccessTransformerClass() {
    return null;
  }
  
}
