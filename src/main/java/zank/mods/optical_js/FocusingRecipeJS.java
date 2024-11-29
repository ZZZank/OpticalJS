package zank.mods.optical_js;

import com.google.gson.JsonObject;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import lombok.val;

/**
 * @author ZZZank
 */
public class FocusingRecipeJS extends RecipeJS {

    /**
     * @see com.simibubi.create.content.processing.recipe.ProcessingRecipe#getRollableResults()
     */
    @Override
    public OutputItem readOutputItem(Object from) {
        if (from instanceof ProcessingOutput output) {
            return OutputItem.of(output.getStack(), output.getChance());
        }
        val outputItem = super.readOutputItem(from);
        if (from instanceof JsonObject j && j.has("chance")) {
            return outputItem.withChance(j.get("chance").getAsFloat());
        }
        return outputItem;
    }
}
