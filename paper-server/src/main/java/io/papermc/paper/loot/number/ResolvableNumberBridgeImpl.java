package io.papermc.paper.loot.number;

import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.key.Key;
import net.minecraft.core.registries.Registries;

public final class ResolvableNumberBridgeImpl implements ResolvableNumberBridge {

    @Override
    public ResolvableInt.Constant intConstant(final int value) {
        return new PaperResolvableInt.Constant(new net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt.Constant(value));
    }

    @Override
    public ResolvableInt.Reference intReference(final Key key) {
        return new PaperResolvableInt.Reference(new net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt.Reference(PaperAdventure.asVanilla(Registries.CONTEXT_INT_PROVIDER, key)));
    }

    @Override
    public ResolvableFloat.Constant floatConstant(final float value) {
        return new PaperResolvableFloat.Constant(new net.minecraft.world.level.storage.loot.providers.number.floats.ResolvableFloat.Constant(value));
    }

    @Override
    public ResolvableFloat.Reference floatReference(final Key key) {
        return new PaperResolvableFloat.Reference(new net.minecraft.world.level.storage.loot.providers.number.floats.ResolvableFloat.Reference(PaperAdventure.asVanilla(Registries.CONTEXT_FLOAT_PROVIDER, key)));
    }
}
