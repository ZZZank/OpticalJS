package zank.mods.optical_js;

import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import net.lpcamors.optical.recipes.FocusingRecipeParams.BeamTypeCondition;

/**
 * Recipe components used by the recipe schemas registered in {@link OpticalJSKubeJSPlugin}.
 * <p>
 * All of them have to be registered in
 * {@link OpticalJSKubeJSPlugin#registerRecipeComponents}, otherwise KubeJS cannot serialize them
 * back into ids when it dumps its recipe schemas.
 *
 * @author ZZZank
 */
public interface OpticalRecipeComponents {
    /**
     * Create's own result type, i.e. an {@code ItemStack} plus a chance.
     */
    RecipeComponentType<ProcessingOutput> PROCESSING_OUTPUT =
        RecipeComponentType.unit(OpticalJS.id("processing_output"), ProcessingOutputComponent::new);

    /**
     * Create Optical's beam type, serialized as an index but readable by name.
     */
    RecipeComponentType<BeamTypeCondition> BEAM_TYPE =
        IndexableEnumComponent.of(OpticalJS.id("beam_type"), BeamTypeCondition.class);
}
