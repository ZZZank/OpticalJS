package zank.mods.optical_js;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.kubejs.recipe.component.UniqueIdBuilder;
import dev.latvian.mods.kubejs.util.OpsContainer;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * Accepts an enum as its index, as its (case-insensitive) name, or as the enum value itself,
 * but always writes it back as its index.
 * <p>
 * Create Optical serializes its beam type as an index ({@code mode}), so a plain
 * {@code EnumComponent} - which only knows about names - cannot be used for it.
 *
 * @author ZZZank
 */
public final class IndexableEnumComponent<T extends Enum<T>> implements RecipeComponent<T> {
    public static <T extends Enum<T>> RecipeComponentType<T> of(ResourceLocation id, Class<T> enumClass) {
        return RecipeComponentType.unit(id, type -> new IndexableEnumComponent<>(type, enumClass));
    }

    private final RecipeComponentType<?> type;
    private final Class<T> enumClass;
    private final T[] constants;
    private final Codec<T> codec;
    private final TypeInfo typeInfo;

    private IndexableEnumComponent(RecipeComponentType<?> type, Class<T> enumClass) {
        this.type = type;
        this.enumClass = enumClass;
        this.constants = enumClass.getEnumConstants();
        this.codec = Codec.INT.comapFlatMap(this::byIndex, Enum::ordinal);
        this.typeInfo = TypeInfo.of(enumClass);
    }

    @Override
    public RecipeComponentType<?> type() {
        return type;
    }

    @Override
    public Codec<T> codec() {
        return codec;
    }

    @Override
    public TypeInfo typeInfo() {
        return typeInfo;
    }

    @Override
    public T wrap(RecipeScriptContext cx, Object from) {
        return switch (from) {
            case null -> null;
            case Number n -> byIndexOrThrow(n.intValue());
            case CharSequence s -> byNameOrThrow(s.toString());
            case Enum<?> e when enumClass.isInstance(e) -> enumClass.cast(e);
            default -> byNameOrThrow(String.valueOf(from));
        };
    }

    @Override
    public void buildUniqueId(UniqueIdBuilder builder, T value) {
        builder.append(value.name().toLowerCase(Locale.ROOT));
    }

    @Override
    public String toString(OpsContainer ops, T value) {
        return "'" + value.name().toLowerCase(Locale.ROOT) + "'";
    }

    @Override
    public String toString() {
        return type.toString();
    }

    private DataResult<T> byIndex(int index) {
        return index >= 0 && index < constants.length
            ? DataResult.success(constants[index])
            : DataResult.error(() -> outOfBounds(index));
    }

    private T byIndexOrThrow(int index) {
        if (index < 0 || index >= constants.length) {
            throw new IllegalArgumentException(outOfBounds(index));
        }
        return constants[index];
    }

    private String outOfBounds(int index) {
        return "Index " + index + " is out of bounds for enum " + enumClass.getName();
    }

    private T byNameOrThrow(@Nullable String name) {
        for (var constant : constants) {
            if (constant.name().equalsIgnoreCase(name)) {
                return constant;
            }
        }
        throw new IllegalArgumentException("Enum value '%s' of %s not found".formatted(name, enumClass.getName()));
    }
}
