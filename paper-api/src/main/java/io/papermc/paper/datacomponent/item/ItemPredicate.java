package io.papermc.paper.datacomponent.item;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.registry.set.RegistryKeySet;
import java.util.Set;
import java.util.function.Predicate;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.Nullable;

/**
 * A predicate matching item stacks by type, count and data components.
 * <p>
 * Vanilla also supports partial component predicates (such as matching damage ranges
 * or a subset of enchantments) which are not yet exposed. Predicates read from the game
 * keep those partial predicates, both when testing and when set back onto an item,
 * apart from the "any value" predicates exposed via {@link #requiredComponentTypes()}.
 */
@ApiStatus.Experimental
@ApiStatus.NonExtendable
public interface ItemPredicate extends Predicate<ItemStack> {

    /**
     * Creates a predicate which matches the type and exact data component patch of the given stack,
     * ignoring its amount.
     *
     * @param itemStack the stack to match
     * @return a new predicate
     */
    @Contract(value = "_ -> new", pure = true)
    static ItemPredicate matching(final ItemStack itemStack) {
        return ItemComponentTypesBridge.bridge().itemPredicateMatching(itemStack);
    }

    @Contract(value = "-> new", pure = true)
    static ItemPredicate.Builder itemPredicate() {
        return ItemComponentTypesBridge.bridge().itemPredicate();
    }

    /**
     * Gets the item types matched by this predicate.
     *
     * @return the item types, or {@code null} if any item type matches
     */
    @Contract(pure = true)
    @Nullable RegistryKeySet<ItemType> items();

    /**
     * Gets the minimum stack amount (inclusive) matched by this predicate.
     *
     * @return the minimum amount, or {@code null} if unbounded
     */
    @Contract(pure = true)
    @Nullable Integer minCount();

    /**
     * Gets the maximum stack amount (inclusive) matched by this predicate.
     *
     * @return the maximum amount, or {@code null} if unbounded
     */
    @Contract(pure = true)
    @Nullable Integer maxCount();

    /**
     * Gets the data component types which must be present with an exact value.
     *
     * @return the component types
     * @see #exactValue(DataComponentType.Valued)
     */
    @Contract(pure = true)
    @Unmodifiable Set<DataComponentType> exactComponentTypes();

    /**
     * Gets the exact value a data component must have to match.
     *
     * @param type the component type
     * @param <T>  the value type
     * @return the expected value, or {@code null} if this predicate doesn't expect an exact value for this type
     */
    @Contract(pure = true)
    <T> @Nullable T exactValue(DataComponentType.Valued<T> type);

    /**
     * Gets the data component types which must be present with any value.
     *
     * @return the component types
     */
    @Contract(pure = true)
    @Unmodifiable Set<DataComponentType> requiredComponentTypes();

    /**
     * Tests the given stack against this predicate.
     *
     * @param itemStack the stack to test
     * @return {@code true} if the stack matches
     */
    @Override
    boolean test(ItemStack itemStack);

    /**
     * Builder for {@link ItemPredicate}.
     */
    @ApiStatus.NonExtendable
    interface Builder {

        /**
         * Sets the item types to match.
         *
         * @param items the item types, or {@code null} to match any type
         * @return the builder for chaining
         * @see #items()
         */
        @Contract(value = "_ -> this", mutates = "this")
        Builder items(@Nullable RegistryKeySet<ItemType> items);

        /**
         * Sets the range of stack amounts to match, both bounds inclusive.
         *
         * @param min the minimum amount, or {@code null} if unbounded
         * @param max the maximum amount, or {@code null} if unbounded
         * @return the builder for chaining
         * @throws IllegalArgumentException if min is greater than max
         */
        @Contract(value = "_, _ -> this", mutates = "this")
        Builder count(@Nullable Integer min, @Nullable Integer max);

        /**
         * Requires a data component to be present with the given value.
         *
         * @param type  the component type
         * @param value the expected value
         * @param <T>   the value type
         * @return the builder for chaining
         */
        @Contract(value = "_, _ -> this", mutates = "this")
        <T> Builder exactValue(DataComponentType.Valued<T> type, T value);

        /**
         * Requires a non-valued data component to be present.
         *
         * @param type the component type
         * @return the builder for chaining
         */
        @Contract(value = "_ -> this", mutates = "this")
        Builder exactValue(DataComponentType.NonValued type);

        /**
         * Requires a data component to be present, with any value.
         *
         * @param type the component type
         * @return the builder for chaining
         */
        @Contract(value = "_ -> this", mutates = "this")
        Builder requireComponent(DataComponentType type);

        /**
         * Builds the predicate.
         *
         * @return a new predicate
         */
        @Contract(value = "-> new", pure = true)
        ItemPredicate build();
    }
}
