package io.papermc.paper.block.predicate;

import com.google.common.base.Preconditions;
import io.papermc.paper.math.BlockPosition;
import io.papermc.paper.math.Position;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.data.util.Conversions;
import io.papermc.paper.registry.set.PaperRegistrySets;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.util.MCUtil;
import java.util.List;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.blockpredicates.AllOfPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.AnyOfPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.MatchingBlockTagPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.MatchingBlocksPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.MatchingFluidsPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.NotPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.TrueBlockPredicate;
import org.bukkit.Fluid;
import org.bukkit.block.BlockType;
import org.bukkit.craftbukkit.util.Handleable;

public final class PaperPositionalBlockPredicate {

    private PaperPositionalBlockPredicate() {
    }

    public static BlockPredicate toVanilla(final PositionalBlockPredicate predicate) {
        Preconditions.checkArgument(predicate != null, "predicate cannot be null");
        return switch (predicate) {
            // predicates read from the game convert back to the exact vanilla instance
            case final Handleable<?> handleable -> (BlockPredicate) handleable.getHandle();
            case final PositionalBlockPredicate.MatchingBlocks matching -> new MatchingBlocksPredicate(
                toVanilla(matching.offset()),
                PaperRegistrySets.convertToNms(Registries.BLOCK, Conversions.global().lookup(), matching.blocks())
            );
            case final PositionalBlockPredicate.MatchingFluids matching -> new MatchingFluidsPredicate(
                toVanilla(matching.offset()),
                PaperRegistrySets.convertToNms(Registries.FLUID, Conversions.global().lookup(), matching.fluids())
            );
            case final PositionalBlockPredicate.AnyOf anyOf -> BlockPredicate.anyOf(MCUtil.transformUnmodifiable(anyOf.predicates(), PaperPositionalBlockPredicate::toVanilla));
            case final PositionalBlockPredicate.AllOf allOf -> BlockPredicate.allOf(MCUtil.transformUnmodifiable(allOf.predicates(), PaperPositionalBlockPredicate::toVanilla));
            case final PositionalBlockPredicate.Not not -> BlockPredicate.not(toVanilla(not.predicate()));
            default -> {
                if (predicate == PositionalBlockPredicate.alwaysTrue()) {
                    yield BlockPredicate.alwaysTrue();
                }
                throw new IllegalArgumentException("Unknown PositionalBlockPredicate implementation: " + predicate.getClass());
            }
        };
    }

    public static PositionalBlockPredicate toApi(final BlockPredicate predicate) {
        return switch (predicate) {
            case final TrueBlockPredicate _ -> PositionalBlockPredicate.alwaysTrue();
            case final MatchingBlocksPredicate matching -> new MatchingBlocks(
                matching, toApi(matching.offset), PaperRegistrySets.convertToApi(RegistryKey.BLOCK, matching.blocks)
            );
            case final MatchingBlockTagPredicate matching when BuiltInRegistries.BLOCK.get(matching.tag).isPresent() -> new MatchingBlocks(
                matching, toApi(matching.offset), PaperRegistrySets.convertToApi(RegistryKey.BLOCK, BuiltInRegistries.BLOCK.get(matching.tag).get())
            );
            case final MatchingFluidsPredicate matching -> new MatchingFluids(
                matching, toApi(matching.offset), PaperRegistrySets.convertToApi(RegistryKey.FLUID, matching.fluids)
            );
            case final AnyOfPredicate anyOf -> new AnyOf(anyOf, MCUtil.transformUnmodifiable(anyOf.predicates, PaperPositionalBlockPredicate::toApi));
            case final AllOfPredicate allOf -> new AllOf(allOf, MCUtil.transformUnmodifiable(allOf.predicates, PaperPositionalBlockPredicate::toApi));
            case final NotPredicate not -> new Not(not, toApi(not.predicate));
            default -> new Unmodelled(predicate);
        };
    }

    private static Vec3i toVanilla(final BlockPosition offset) {
        return new Vec3i(offset.blockX(), offset.blockY(), offset.blockZ());
    }

    private static BlockPosition toApi(final Vec3i offset) {
        return Position.block(offset.getX(), offset.getY(), offset.getZ());
    }

    record MatchingBlocks(BlockPredicate handle, BlockPosition offset, RegistryKeySet<BlockType> blocks) implements PositionalBlockPredicate.MatchingBlocks, Handleable<BlockPredicate> {
        @Override
        public BlockPredicate getHandle() {
            return this.handle;
        }
    }

    record MatchingFluids(BlockPredicate handle, BlockPosition offset, RegistryKeySet<Fluid> fluids) implements PositionalBlockPredicate.MatchingFluids, Handleable<BlockPredicate> {
        @Override
        public BlockPredicate getHandle() {
            return this.handle;
        }
    }

    record AnyOf(BlockPredicate handle, List<PositionalBlockPredicate> predicates) implements PositionalBlockPredicate.AnyOf, Handleable<BlockPredicate> {
        @Override
        public BlockPredicate getHandle() {
            return this.handle;
        }
    }

    record AllOf(BlockPredicate handle, List<PositionalBlockPredicate> predicates) implements PositionalBlockPredicate.AllOf, Handleable<BlockPredicate> {
        @Override
        public BlockPredicate getHandle() {
            return this.handle;
        }
    }

    record Not(BlockPredicate handle, PositionalBlockPredicate predicate) implements PositionalBlockPredicate.Not, Handleable<BlockPredicate> {
        @Override
        public BlockPredicate getHandle() {
            return this.handle;
        }
    }

    /**
     * A vanilla predicate type without a dedicated API type, kept so it can be passed back unchanged.
     */
    record Unmodelled(BlockPredicate handle) implements PositionalBlockPredicate, Handleable<BlockPredicate> {
        @Override
        public BlockPredicate getHandle() {
            return this.handle;
        }
    }
}
