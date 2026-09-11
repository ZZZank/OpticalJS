package zank.mods.optical_js;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.common.Mod;

/**
 * @author ZZZank
 */
@Mod(OpticalJS.MOD_ID)
public class OpticalJS {
    public static final String MOD_ID = "optical_js";

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
