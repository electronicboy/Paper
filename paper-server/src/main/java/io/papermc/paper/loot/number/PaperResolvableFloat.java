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

public final class PaperResolvableFloat {

    private PaperResolvableFloat() {
    }

    public static ResolvableFloat fromVanilla(final net.minecraft.world.level.storage.loot.providers.number.floats.ResolvableFloat vanilla) {
        return switch (vanilla) {
            case net.minecraft.world.level.storage.loot.providers.number.floats.ResolvableFloat.Constant constant -> new Constant(constant);
            case net.minecraft.world.level.storage.loot.providers.number.floats.ResolvableFloat.Reference reference -> new Reference(reference);
        };
    }

    public static net.minecraft.world.level.storage.loot.providers.number.floats.ResolvableFloat toVanilla(final ResolvableFloat api) {
        if (api instanceof final Handleable<?> handleable) {
            return (net.minecraft.world.level.storage.loot.providers.number.floats.ResolvableFloat) handleable.getHandle();
        }
        return switch (api) {
            case final ResolvableFloat.Constant constant -> new net.minecraft.world.level.storage.loot.providers.number.floats.ResolvableFloat.Constant(constant.getValue());
            case final ResolvableFloat.Reference reference -> new net.minecraft.world.level.storage.loot.providers.number.floats.ResolvableFloat.Reference(PaperAdventure.asVanilla(Registries.CONTEXT_FLOAT_PROVIDER, reference.key()));
            default -> throw new IllegalArgumentException("Unknown ResolvableFloat implementation: " + api.getClass());
        };
    }

    public record Constant(net.minecraft.world.level.storage.loot.providers.number.floats.ResolvableFloat.Constant impl) implements ResolvableFloat.Constant, Handleable<net.minecraft.world.level.storage.loot.providers.number.floats.ResolvableFloat> {

        @Override
        public float getValue() {
            return this.impl.value();
        }

        @Override
        public net.minecraft.world.level.storage.loot.providers.number.floats.ResolvableFloat getHandle() {
            return this.impl;
        }
    }

    public record Reference(net.minecraft.world.level.storage.loot.providers.number.floats.ResolvableFloat.Reference impl) implements ResolvableFloat.Reference, Handleable<net.minecraft.world.level.storage.loot.providers.number.floats.ResolvableFloat> {

        @Override
        public Key key() {
            return PaperAdventure.asAdventureKey(this.impl.key());
        }

        @Override
        public float resolve(final LootContext context, final float defaultValue) {
            final LootParams lootParams = CraftLootContext.createLootParams(context, LootContextParamSets.ALL_PARAMS_OPTIONAL, false);
            final net.minecraft.world.level.storage.loot.LootContext vanillaContext = new net.minecraft.world.level.storage.loot.LootContext.Builder(lootParams).create(Optional.empty());
            return this.impl.get(vanillaContext, defaultValue);
        }

        @Override
        public net.minecraft.world.level.storage.loot.providers.number.floats.ResolvableFloat getHandle() {
            return this.impl;
        }
    }
}
