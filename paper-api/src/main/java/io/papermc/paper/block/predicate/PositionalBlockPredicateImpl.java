package io.papermc.paper.block.predicate;

import io.papermc.paper.math.BlockPosition;
import io.papermc.paper.registry.set.RegistryKeySet;
import java.util.List;
import org.bukkit.Fluid;
import org.bukkit.block.BlockType;

final class PositionalBlockPredicateImpl {

    private PositionalBlockPredicateImpl() {
    }

    enum AlwaysTrueImpl implements PositionalBlockPredicate {
        INSTANCE
    }

    record MatchingBlocksImpl(BlockPosition offset, RegistryKeySet<BlockType> blocks) implements PositionalBlockPredicate.MatchingBlocks {
    }

    record MatchingFluidsImpl(BlockPosition offset, RegistryKeySet<Fluid> fluids) implements PositionalBlockPredicate.MatchingFluids {
    }

    record AnyOfImpl(List<PositionalBlockPredicate> predicates) implements PositionalBlockPredicate.AnyOf {
    }

    record AllOfImpl(List<PositionalBlockPredicate> predicates) implements PositionalBlockPredicate.AllOf {
    }

    record NotImpl(PositionalBlockPredicate predicate) implements PositionalBlockPredicate.Not {
    }
}
