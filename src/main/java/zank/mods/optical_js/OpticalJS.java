package zank.mods.optical_js;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraftforge.fml.common.Mod;

/**
 * @author ZZZank
 */
@Mod(OpticalJS.MOD_ID)
public class OpticalJS {
    public static final String MOD_ID = "optical_js";
    public static final Gson GSON = new GsonBuilder()
        .setLenient()
        .create();
}
