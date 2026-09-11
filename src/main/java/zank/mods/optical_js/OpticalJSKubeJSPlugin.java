package zank.mods.optical_js;

import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.component.IngredientComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentTypeRegistry;
import dev.latvian.mods.kubejs.recipe.component.TimeComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaRegistry;
import dev.latvian.mods.kubejs.util.TickDuration;
import net.lpcamors.optical.CORecipeTypes;
import net.lpcamors.optical.recipes.FocusingRecipeParams.BeamTypeCondition;

/**
 * @author ZZZank
 */
public class OpticalJSKubeJSPlugin implements KubeJSPlugin {

    @Override
    public void registerRecipeComponents(RecipeComponentTypeRegistry registry) {
        registry.register(OpticalRecipeComponents.PROCESSING_OUTPUT);
        registry.register(OpticalRecipeComponents.BEAM_TYPE);
    }

    @Override
    public void registerRecipeSchemas(RecipeSchemaRegistry registry) {
        registry.register(
            CORecipeTypes.FOCUSING.getId(),
            new RecipeSchema(
                OpticalRecipeComponents.PROCESSING_OUTPUT.instance().asList().outputKey("results"),
                IngredientComponent.INGREDIENT.instance().asList().inputKey("ingredients"),
                TimeComponent.TICKS.otherKey("processing_time").optional(TickDuration.of(40L)),
                // alwaysWrite, because Create Optical's own codec falls back to RADIO (index 0)
                // when "mode" is missing, while every other default in that mod is NONE (index 4)
                OpticalRecipeComponents.BEAM_TYPE.otherKey("mode")
                    .optional(BeamTypeCondition.NONE)
                    .alwaysWrite()
            )
        );
    }
}
