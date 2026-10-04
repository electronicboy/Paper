package io.papermc.paper.block.predicate;

import io.papermc.paper.annotation.MinecraftVersionDependent;
import io.papermc.paper.math.BlockPosition;
import io.papermc.paper.math.Position;
import io.papermc.paper.registry.set.RegistryKeySet;
import java.util.List;
import org.bukkit.Fluid;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

/**
 * A predicate testing the world relative to a position, such as checking the block above it.
 * <p>
 * This is distinct from {@link io.papermc.paper.block.BlockPredicate}, which tests a single block
 * (as used by {@link io.papermc.paper.datacomponent.item.ItemAdventurePredicate}).
 * <p>
 * Only some vanilla predicate types are modelled by sub-interfaces. Predicates of other types read from
 * the game are exposed as plain {@link PositionalBlockPredicate} instances, which can still be passed
 * back to the API unchanged.
 */
@MinecraftVersionDependent
@ApiStatus.NonExtendable
public interface PositionalBlockPredicate {

    /**
     * Gets a predicate which always matches.
     *
     * @return the predicate
     */
    @Contract(pure = true)
    static PositionalBlockPredicate alwaysTrue() {
        return PositionalBlockPredicateImpl.AlwaysTrueImpl.INSTANCE;
    }

    /**
     * Creates a predicate matching the block at the tested position.
     *
     * @param blocks the blocks to match, may be a tag
     * @return a new predicate
     */
    @Contract(value = "_ -> new", pure = true)
    static MatchingBlocks matchingBlocks(final RegistryKeySet<BlockType> blocks) {
        return matchingBlocks(Position.BLOCK_ZERO, blocks);
    }

    /**
     * Creates a predicate matching the block adjacent to the tested position.
     *
     * @param face   the direction of the block to test
     * @param blocks the blocks to match, may be a tag
     * @return a new predicate
     */
    @Contract(value = "_, _ -> new", pure = true)
    static MatchingBlocks matchingBlocks(final BlockFace face, final RegistryKeySet<BlockType> blocks) {
        return matchingBlocks(Position.BLOCK_ZERO.offset(face), blocks);
    }

    /**
     * Creates a predicate matching the block at an offset from the tested position.
     *
     * @param offset the offset of the block to test
     * @param blocks the blocks to match, may be a tag
     * @return a new predicate
     */
    @Contract(value = "_, _ -> new", pure = true)
    static MatchingBlocks matchingBlocks(final BlockPosition offset, final RegistryKeySet<BlockType> blocks) {
        return new PositionalBlockPredicateImpl.MatchingBlocksImpl(offset, blocks);
    }

    /**
     * Creates a predicate matching the fluid at the tested position.
     *
     * @param fluids the fluids to match, may be a tag
     * @return a new predicate
     */
    @Contract(value = "_ -> new", pure = true)
    static MatchingFluids matchingFluids(final RegistryKeySet<Fluid> fluids) {
        return matchingFluids(Position.BLOCK_ZERO, fluids);
    }

    /**
     * Creates a predicate matching the fluid adjacent to the tested position.
     *
     * @param face   the direction of the fluid to test
     * @param fluids the fluids to match, may be a tag
     * @return a new predicate
     */
    @Contract(value = "_, _ -> new", pure = true)
    static MatchingFluids matchingFluids(final BlockFace face, final RegistryKeySet<Fluid> fluids) {
        return matchingFluids(Position.BLOCK_ZERO.offset(face), fluids);
    }

    /**
     * Creates a predicate matching the fluid at an offset from the tested position.
     *
     * @param offset the offset of the fluid to test
     * @param fluids the fluids to match, may be a tag
     * @return a new predicate
     */
    @Contract(value = "_, _ -> new", pure = true)
    static MatchingFluids matchingFluids(final BlockPosition offset, final RegistryKeySet<Fluid> fluids) {
        return new PositionalBlockPredicateImpl.MatchingFluidsImpl(offset, fluids);
    }

    /**
     * Creates a predicate matching if any of the given predicates match.
     *
     * @param predicates the predicates
     * @return a new predicate
     */
    @Contract(value = "_ -> new", pure = true)
    static AnyOf anyOf(final List<? extends PositionalBlockPredicate> predicates) {
        return new PositionalBlockPredicateImpl.AnyOfImpl(List.copyOf(predicates));
    }

    /**
     * Creates a predicate matching if any of the given predicates match.
     *
     * @param predicates the predicates
     * @return a new predicate
     */
    @Contract(value = "_ -> new", pure = true)
    static AnyOf anyOf(final PositionalBlockPredicate... predicates) {
        return anyOf(List.of(predicates));
    }

    /**
     * Creates a predicate matching if all the given predicates match.
     *
     * @param predicates the predicates
     * @return a new predicate
     */
    @Contract(value = "_ -> new", pure = true)
    static AllOf allOf(final List<? extends PositionalBlockPredicate> predicates) {
        return new PositionalBlockPredicateImpl.AllOfImpl(List.copyOf(predicates));
    }

    /**
     * Creates a predicate matching if all the given predicates match.
     *
     * @param predicates the predicates
     * @return a new predicate
     */
    @Contract(value = "_ -> new", pure = true)
    static AllOf allOf(final PositionalBlockPredicate... predicates) {
        return allOf(List.of(predicates));
    }

    /**
     * Creates a predicate matching if the given predicate does not match.
     *
     * @param predicate the predicate to invert
     * @return a new predicate
     */
    @Contract(value = "_ -> new", pure = true)
    static Not not(final PositionalBlockPredicate predicate) {
        return new PositionalBlockPredicateImpl.NotImpl(predicate);
    }

    /**
     * A predicate matching the block at an offset from the tested position.
     */
    @ApiStatus.NonExtendable
    interface MatchingBlocks extends PositionalBlockPredicate {

        /**
         * Gets the offset from the tested position of the block to match.
         *
         * @return the offset
         */
        @Contract(pure = true)
        BlockPosition offset();

        /**
         * Gets the blocks to match.
         *
         * @return the blocks, may be a tag
         */
        @Contract(pure = true)
        RegistryKeySet<BlockType> blocks();
    }

    /**
     * A predicate matching the fluid at an offset from the tested position.
     */
    @ApiStatus.NonExtendable
    interface MatchingFluids extends PositionalBlockPredicate {

        /**
         * Gets the offset from the tested position of the fluid to match.
         *
         * @return the offset
         */
        @Contract(pure = true)
        BlockPosition offset();

        /**
         * Gets the fluids to match.
         *
         * @return the fluids, may be a tag
         */
        @Contract(pure = true)
        RegistryKeySet<Fluid> fluids();
    }

    /**
     * A predicate matching if any of its predicates match.
     */
    @ApiStatus.NonExtendable
    interface AnyOf extends PositionalBlockPredicate {

        /**
         * Gets the predicates.
         *
         * @return the predicates
         */
        @Contract(pure = true)
        @Unmodifiable List<PositionalBlockPredicate> predicates();
    }

    /**
     * A predicate matching if all its predicates match.
     */
    @ApiStatus.NonExtendable
    interface AllOf extends PositionalBlockPredicate {

        /**
         * Gets the predicates.
         *
         * @return the predicates
         */
        @Contract(pure = true)
        @Unmodifiable List<PositionalBlockPredicate> predicates();
    }

    /**
     * A predicate matching if its predicate does not match.
     */
    @ApiStatus.NonExtendable
    interface Not extends PositionalBlockPredicate {

        /**
         * Gets the inverted predicate.
         *
         * @return the predicate
         */
        @Contract(pure = true)
        PositionalBlockPredicate predicate();
    }
}
