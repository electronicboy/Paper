package io.papermc.paper.loot.number;

import net.kyori.adventure.key.Key;
import org.bukkit.loot.LootContext;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * Represents a floating point number that can be resolved against a {@link LootContext}.
 * <p>
 * This is either a {@link Constant} or a {@link Reference} to a context provider
 * defined in the {@code context_float_provider} registry.
 */
@ApiStatus.NonExtendable
public interface ResolvableFloat {

    /**
     * Creates a constant {@link ResolvableFloat}.
     *
     * @param value the constant value
     * @return the constant
     */
    @Contract(value = "_ -> new", pure = true)
    static Constant constant(final float value) {
        return ResolvableNumberBridge.bridge().floatConstant(value);
    }

    /**
     * Creates a {@link ResolvableFloat} referencing a context provider in the {@code context_float_provider} registry.
     * <p>
     * The key is not validated against the registry, references to missing providers
     * resolve to the default value passed to {@link #resolve(LootContext, float)}.
     *
     * @param key the key of the context provider
     * @return the reference
     */
    @Contract(value = "_ -> new", pure = true)
    static Reference reference(final Key key) {
        return ResolvableNumberBridge.bridge().floatReference(key);
    }

    /**
     * Resolves this number against the given loot context.
     *
     * @param context      the loot context to resolve against.
     * @param defaultValue the value used if this number cannot be resolved
     * @return the resolved number
     */
    float resolve(LootContext context, float defaultValue);

    /**
     * Represents a constant {@link ResolvableFloat}.
     */
    @ApiStatus.NonExtendable
    interface Constant extends ResolvableFloat {

        /**
         * @return the constant value
         */
        @Contract(pure = true)
        float getValue();

        /**
         * {@inheritDoc}
         */
        @Override
        default float resolve(final LootContext context, final float defaultValue) {
            return this.getValue();
        }
    }

    /**
     * Represents a reference to a context provider in the {@code context_float_provider} registry.
     */
    @ApiStatus.NonExtendable
    interface Reference extends ResolvableFloat {

        /**
         * @return the key of the referenced context provider
         */
        @Contract(pure = true)
        Key key();
    }
}
