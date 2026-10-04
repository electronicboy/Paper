package io.papermc.paper.block.predicate;

import io.papermc.paper.block.stateprovider.RuleBasedBlockStateProvider;
import io.papermc.paper.datacomponent.item.blocktransformer.BlockTransformer;
import io.papermc.paper.math.Position;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.keys.BlockTransformerKeys;
import io.papermc.paper.registry.keys.BlockTypeKeys;
import io.papermc.paper.registry.keys.tags.BlockTypeTagKeys;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import io.papermc.paper.registry.tag.Tag;
import java.util.List;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.MatchingBlocksPredicate;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockType;
import org.bukkit.support.environment.AllFeatures;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@AllFeatures
class PositionalBlockPredicateTest {

    private static final RegistryKeySet<BlockType> STONE = RegistrySet.keySet(RegistryKey.BLOCK, BlockTypeKeys.STONE);

    @ParameterizedTest
    @ValueSource(strings = {"shovel", "axe", "hoe"})
    void testReadVanillaTransformers(final String name) {
        final BlockTransformer transformer = RegistryAccess.registryAccess().getRegistry(RegistryKey.BLOCK_TRANSFORMER)
            .getOrThrow(net.kyori.adventure.key.Key.key(name));
        Assertions.assertDoesNotThrow(() -> transformer.transforms().forEach(data -> data.blockStateProvider()));
    }

    @Test
    void testVanillaHoeRulesExposeTagsAndOffsets() {
        final BlockTransformer hoe = RegistryAccess.registryAccess().getRegistry(RegistryKey.BLOCK_TRANSFORMER).getOrThrow(BlockTransformerKeys.HOE);
        final RuleBasedBlockStateProvider provider = Assertions.assertInstanceOf(RuleBasedBlockStateProvider.class, hoe.transforms().getFirst().blockStateProvider());
        final PositionalBlockPredicate.AllOf allOf = Assertions.assertInstanceOf(PositionalBlockPredicate.AllOf.class, provider.rules().getFirst().ifTrue());

        final PositionalBlockPredicate.MatchingBlocks self = Assertions.assertInstanceOf(PositionalBlockPredicate.MatchingBlocks.class, allOf.predicates().get(0));
        Assertions.assertEquals(Position.BLOCK_ZERO, self.offset());
        Assertions.assertEquals(BlockTypeTagKeys.TURNS_INTO_FARMLAND, Assertions.assertInstanceOf(Tag.class, self.blocks()).tagKey());

        final PositionalBlockPredicate.MatchingBlocks above = Assertions.assertInstanceOf(PositionalBlockPredicate.MatchingBlocks.class, allOf.predicates().get(1));
        Assertions.assertEquals(Position.block(0, 1, 0), above.offset());
    }

    @Test
    void testReadPredicateConvertsBackToSameInstance() {
        final BlockPredicate vanilla = BlockPredicate.allOf(BlockPredicate.matchesBlocks(Blocks.STONE), BlockPredicate.not(BlockPredicate.solid()));
        Assertions.assertSame(vanilla, PaperPositionalBlockPredicate.toVanilla(PaperPositionalBlockPredicate.toApi(vanilla)));
    }

    @Test
    void testUnmodelledPredicateIsKept() {
        final BlockPredicate solid = BlockPredicate.solid();
        final PositionalBlockPredicate api = PaperPositionalBlockPredicate.toApi(solid);

        Assertions.assertFalse(api instanceof PositionalBlockPredicate.MatchingBlocks);
        Assertions.assertSame(solid, PaperPositionalBlockPredicate.toVanilla(api));
    }

    @Test
    void testFaceIsConvertedToOffset() {
        final BlockPredicate vanilla = PaperPositionalBlockPredicate.toVanilla(PositionalBlockPredicate.matchingBlocks(BlockFace.UP, STONE));
        Assertions.assertEquals(new Vec3i(0, 1, 0), Assertions.assertInstanceOf(MatchingBlocksPredicate.class, vanilla).offset);
    }

    @Test
    void testOffsetIsNotRoundedToFace() {
        final BlockPredicate vanilla = BlockPredicate.matchesBlocks(new Vec3i(0, -2, 0), List.of(Blocks.STONE));
        final PositionalBlockPredicate.MatchingBlocks api = Assertions.assertInstanceOf(PositionalBlockPredicate.MatchingBlocks.class, PaperPositionalBlockPredicate.toApi(vanilla));
        Assertions.assertEquals(Position.block(0, -2, 0), api.offset());
    }

    @Test
    void testCompositesFromApi() {
        final BlockPredicate vanilla = PaperPositionalBlockPredicate.toVanilla(PositionalBlockPredicate.anyOf(
            PositionalBlockPredicate.alwaysTrue(),
            PositionalBlockPredicate.not(PositionalBlockPredicate.matchingBlocks(STONE))
        ));
        final PositionalBlockPredicate.AnyOf anyOf = Assertions.assertInstanceOf(PositionalBlockPredicate.AnyOf.class, PaperPositionalBlockPredicate.toApi(vanilla));
        Assertions.assertSame(PositionalBlockPredicate.alwaysTrue(), anyOf.predicates().get(0));
        final PositionalBlockPredicate.Not not = Assertions.assertInstanceOf(PositionalBlockPredicate.Not.class, anyOf.predicates().get(1));
        Assertions.assertEquals(STONE.values(), Assertions.assertInstanceOf(PositionalBlockPredicate.MatchingBlocks.class, not.predicate()).blocks().values());
    }
}
