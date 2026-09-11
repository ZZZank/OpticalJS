package zank.mods.optical_js;

import com.google.gson.JsonObject;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.ItemWrapper;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.StringUtilsWrapper;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.kubejs.recipe.component.SimpleRecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.UniqueIdBuilder;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.ItemMatch;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.util.OpsContainer;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

/**
 * Reads and writes Create's {@link ProcessingOutput}, i.e. an item stack with a chance.
 * <p>
 * KubeJS 7 removed {@code OutputItem} and with it {@code RecipeJS#readOutputItem} and
 * {@code ItemStack#withChance}, so chance-bearing results now have to go through the mod's own
 * type - exactly like KubeJS Create does with its {@code create:processing_output} component.
 */
public class ProcessingOutputComponent extends SimpleRecipeComponent<ProcessingOutput> {

    public ProcessingOutputComponent(RecipeComponentType<?> type) {
        super(type, ProcessingOutput.CODEC_NEW, TypeInfo.of(ProcessingOutput.class));
    }

    @Override
    public ProcessingOutput wrap(RecipeScriptContext cx, Object from) {
        return switch (from) {
            case null -> ProcessingOutput.EMPTY;
            case ProcessingOutput output -> output;
            case ItemStack stack -> stack.isEmpty() ? ProcessingOutput.EMPTY : new ProcessingOutput(stack, 1F);
            case JsonObject json when json.has("chance") ->
                fromMapLike(cx.cx(), json, json::get, json.has("output") ? json.get("output") : json);
            case Map<?, ?> map when map.containsKey("chance") ->
                fromMapLike(cx.cx(), map, map::get, map.containsKey("output") ? map.get("output") : map);
            default -> {
                var stack = ItemWrapper.wrap(cx.cx(), from);
                yield stack.isEmpty() ? ProcessingOutput.EMPTY : new ProcessingOutput(stack, 1F);
            }
        };
    }

    @Override
    public boolean hasPriority(RecipeMatchContext cx, Object from) {
        return from instanceof ProcessingOutput || ItemWrapper.isItemStackLike(from);
    }

    @Override
    public boolean matches(RecipeMatchContext cx, ProcessingOutput value, ReplacementMatchInfo match) {
        return match.match() instanceof ItemMatch m && m.matches(cx, value.getStack(), match.exact());
    }

    @Override
    public ProcessingOutput replace(RecipeScriptContext cx, ProcessingOutput original, ReplacementMatchInfo match, Object with) {
        if (!matches(cx, original, match)) {
            return original;
        }

        return switch (with) {
            case ProcessingOutput output -> output;
            case ItemStack stack -> new ProcessingOutput(stack, original.getChance());
            default -> {
                var output = wrap(cx, with);

                if (output != ProcessingOutput.EMPTY && !ItemStack.isSameItemSameComponents(output.getStack(), original.getStack())) {
                    yield new ProcessingOutput(output.getStack(), original.getChance());
                }

                yield original;
            }
        };
    }

    @Override
    public boolean isEmpty(ProcessingOutput value) {
        return value == ProcessingOutput.EMPTY || value.getStack().isEmpty();
    }

    @Override
    public void buildUniqueId(UniqueIdBuilder builder, ProcessingOutput value) {
        if (!isEmpty(value)) {
            builder.append(BuiltInRegistries.ITEM.getKey(value.getStack().getItem()));
        }
    }

    @Override
    public String toString(OpsContainer ops, ProcessingOutput value) {
        if (isEmpty(value)) {
            return "empty";
        }

        var stack = value.getStack();
        var id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return stack.getCount() > 1 ? stack.getCount() + "x " + id : id.toString();
    }

    private static ProcessingOutput fromMapLike(Context cx, Object self, Function<String, Object> getter, @Nullable Object content) {
        var chance = (float) Mth.clamp(StringUtilsWrapper.parseDouble(getter.apply("chance"), 1.0D), 0.0D, 1.0D);
        var stack = ItemWrapper.wrap(cx, content != null ? content : self);
        return stack.isEmpty() ? ProcessingOutput.EMPTY : new ProcessingOutput(stack, chance);
    }
}
