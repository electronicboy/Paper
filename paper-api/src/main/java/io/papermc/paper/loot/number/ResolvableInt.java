package io.papermc.paper.loot.number;

import net.kyori.adventure.key.Key;
import org.bukkit.loot.LootContext;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * Represents an integer number that can be resolved against a {@link LootContext}.
 * <p>
 * This is either a {@link Constant} or a {@link Reference} to a context provider
 * defined in the {@code context_int_provider} registry.
 */
@ApiStatus.NonExtendable
public interface ResolvableInt {

    /**
     * Creates a constant {@link ResolvableInt}.
     *
     * @param value the constant value
     * @return the constant
     */
    @Contract(value = "_ -> new", pure = true)
    static Constant constant(final int value) {
        return ResolvableNumberBridge.bridge().intConstant(value);
    }

    /**
     * Creates a {@link ResolvableInt} referencing a context provider in the {@code context_int_provider} registry.
     * <p>
     * The key is not validated against the registry, references to missing providers
     * resolve to the default value passed to {@link #resolve(LootContext, int)}.
     *
     * @param key the key of the context provider
     * @return the reference
     */
    @Contract(value = "_ -> new", pure = true)
    static Reference reference(final Key key) {
        return ResolvableNumberBridge.bridge().intReference(key);
    }

    /**
     * Resolves this number against the given loot context.
     *
     * @param context      the loot context to resolve against.
     * @param defaultValue the value used if this number cannot be resolved
     * @return the resolved number
     */
    int resolve(LootContext context, int defaultValue);

    /**
     * Represents a constant {@link ResolvableInt}.
     */
    @ApiStatus.NonExtendable
    interface Constant extends ResolvableInt {

        /**
         * @return the constant value
         */
        @Contract(pure = true)
        int getValue();

        /**
         * {@inheritDoc}
         */
        @Override
        default int resolve(final LootContext context, final int defaultValue) {
            return this.getValue();
        }
    }

    /**
     * Represents a reference to a context provider in the {@code context_int_provider} registry.
     */
    @ApiStatus.NonExtendable
    interface Reference extends ResolvableInt {

        /**
         * @return the key of the referenced context provider
         */
        @Contract(pure = true)
        Key key();
    }
}
