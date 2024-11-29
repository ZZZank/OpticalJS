package zank.mods.optical_js;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.component.TimeComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.RegisterRecipeSchemasEvent;
import net.lpcamors.optical.CORecipeTypes;
import net.lpcamors.optical.recipes.FocusingRecipeParams;

/**
 * @author ZZZank
 */
public class OpticalJSKubeJSPlugin extends KubeJSPlugin {

    @Override
    public void registerRecipeSchemas(RegisterRecipeSchemasEvent event) {
        event.register(
            CORecipeTypes.FOCUSING.getId(),
            new RecipeSchema(
                FocusingRecipeJS.class,
                FocusingRecipeJS::new,
                ItemComponents.OUTPUT_ARRAY.key("results"),
                ItemComponents.INPUT_ARRAY.key("ingredients"),
                TimeComponent.TICKS.key("processingTime").optional(40L),
                NumberComponent.INT.min(0)
                    .max(FocusingRecipeParams.BeamTypeCondition.values().length - 1)
                    .key("required_beam_type")
                    .optional(FocusingRecipeParams.BeamTypeCondition.NONE.getId())
            )
        );
    }
}
