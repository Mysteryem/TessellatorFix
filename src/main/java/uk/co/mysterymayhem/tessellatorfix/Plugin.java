/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.co.mysterymayhem.tessellatorfix;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import java.util.Map;

/**
 *
 * @author Thomas
 */
@IFMLLoadingPlugin.TransformerExclusions({"uk.co.mysterymayhem.tessellatorfix."})
@IFMLLoadingPlugin.Name(value = "mysttessellatorfixcore")
@IFMLLoadingPlugin.SortingIndex(value = 1001)
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
