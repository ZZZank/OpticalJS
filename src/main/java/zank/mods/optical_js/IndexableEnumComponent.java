package zank.mods.optical_js;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.typings.desc.DescriptionContext;
import dev.latvian.mods.kubejs.typings.desc.PrimitiveDescJS;
import dev.latvian.mods.kubejs.typings.desc.TypeDescJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import lombok.val;

import java.util.Arrays;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * receive integer and string but only write string
 *
 * @author ZZZank
 */
public record IndexableEnumComponent<T extends Enum<T>>(
    Class<T> type,
    Function<T, JsonPrimitive> toJson,
    BiFunction<Class<T>, JsonPrimitive, T> fromJson
) implements RecipeComponent<T> {
    public IndexableEnumComponent(Class<T> enumType) {
        this(enumType, defaultToJson(), defaultFromJson());
    }

    private static final Function<Enum<?>, JsonPrimitive> DEFAULT_TO_JSON =
        e -> new JsonPrimitive(e.name().toLowerCase(Locale.ROOT));

    public static <T extends Enum<T>> Function<T, JsonPrimitive> defaultToJson() {
        return UtilsJS.cast(DEFAULT_TO_JSON);
    }

    private static final BiFunction<Class<? extends Enum<?>>, JsonPrimitive, Enum<?>> DEFAULT_FROM_JSON =
        (type, json) -> {
            if (json.isNumber()) {
                val index = json.getAsInt();
                val constants = type.getEnumConstants();
                if (index >= 0 && index < constants.length) {
                    return constants[index];
                }
            } else if (json.isString()) {
                val name = json.getAsString();
                for (val e : type.getEnumConstants()) {
                    if (e.name().equalsIgnoreCase(name)) {
                        return e;
                    }
                }
            }
            return null;
        };

    public static <T extends Enum<T>> BiFunction<Class<T>, JsonPrimitive, T> defaultFromJson() {
        return UtilsJS.cast(DEFAULT_FROM_JSON);
    }

    @Override
    public Class<?> componentClass() {
        return type;
    }

    @Override
    public JsonElement write(RecipeJS recipe, T value) {
        return toJson.apply(value);
    }

    @Override
    public T read(RecipeJS recipe, Object from) {
        if (type.isInstance(from)) {
            return (T) from;
        }
        if (from == null) {
            return null;
        }
        val e = fromJson.apply(type, obj2Primitive(from));

        if (e == null) {
            throw new RecipeExceptionJS("Enum value '%s' of %s not found".formatted(from, type.getName()));
        }

        return e;
    }

    public static JsonPrimitive obj2Primitive(Object from) {
        if (from instanceof JsonPrimitive p) {
            return p;
        } else if (from instanceof Number n) {
            return new JsonPrimitive(n);
        }
        return new JsonPrimitive(String.valueOf(from));
    }

    @Override
    public TypeDescJS constructorDescription(DescriptionContext ctx) {
        val constants = type.getEnumConstants();
        val descAll = Stream.concat(
                IntStream.range(0, constants.length).boxed(),
                Arrays.stream(constants)
                    .map(Enum::name)
                    .map(e -> e.toLowerCase(Locale.ROOT))
            )
            .map(OpticalJS.GSON::toJson)
            .map(PrimitiveDescJS::new)
            .toArray(TypeDescJS[]::new);
        return TypeDescJS.any(descAll);
    }
}
