package io.papermc.paper.loot.number;

import io.papermc.paper.adventure.PaperAdventure;
import java.util.Optional;
import net.kyori.adventure.key.Key;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.bukkit.craftbukkit.CraftLootContext;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.loot.LootContext;

public final class PaperResolvableInt {

    private PaperResolvableInt() {
    }

    public static ResolvableInt fromVanilla(final net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt vanilla) {
        return switch (vanilla) {
            case net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt.Constant constant -> new Constant(constant);
            case net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt.Reference reference -> new Reference(reference);
        };
    }

    public static net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt toVanilla(final ResolvableInt api) {
        if (api instanceof final Handleable<?> handleable) {
            return (net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt) handleable.getHandle();
        }
        return switch (api) {
            case final ResolvableInt.Constant constant -> new net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt.Constant(constant.getValue());
            case final ResolvableInt.Reference reference -> new net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt.Reference(PaperAdventure.asVanilla(Registries.CONTEXT_INT_PROVIDER, reference.key()));
            default -> throw new IllegalArgumentException("Unknown ResolvableInt implementation: " + api.getClass());
        };
    }

    public record Constant(net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt.Constant impl) implements ResolvableInt.Constant, Handleable<net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt> {

        @Override
        public int getValue() {
            return this.impl.value();
        }

        @Override
        public net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt getHandle() {
            return this.impl;
        }
    }

    public record Reference(net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt.Reference impl) implements ResolvableInt.Reference, Handleable<net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt> {

        @Override
        public Key key() {
            return PaperAdventure.asAdventureKey(this.impl.key());
        }

        @Override
        public int resolve(final LootContext context, final int defaultValue) {
            final LootParams lootParams = CraftLootContext.createLootParams(context, LootContextParamSets.ALL_PARAMS_OPTIONAL, false);
            final net.minecraft.world.level.storage.loot.LootContext vanillaContext = new net.minecraft.world.level.storage.loot.LootContext.Builder(lootParams).create(Optional.empty());
            return this.impl.get(vanillaContext, defaultValue);
        }

        @Override
        public net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt getHandle() {
            return this.impl;
        }
    }
}
