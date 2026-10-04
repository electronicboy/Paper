package io.papermc.paper.datacomponent.item;

import io.papermc.paper.loot.number.PaperResolvableFloat;
import io.papermc.paper.loot.number.PaperResolvableInt;
import io.papermc.paper.loot.number.ResolvableFloat;
import io.papermc.paper.loot.number.ResolvableInt;
import org.bukkit.craftbukkit.util.Handleable;

public record PaperBrewingFuel(
    net.minecraft.world.item.component.BrewingFuel impl
) implements BrewingFuel, Handleable<net.minecraft.world.item.component.BrewingFuel> {

    @Override
    public net.minecraft.world.item.component.BrewingFuel getHandle() {
        return this.impl;
    }

    @Override
    public ResolvableInt uses() {
        return PaperResolvableInt.fromVanilla(this.impl.uses());
    }

    @Override
    public ResolvableFloat speedMultiplier() {
        return PaperResolvableFloat.fromVanilla(this.impl.speedMultiplier());
    }

    static final class BuilderImpl implements BrewingFuel.Builder {

        private net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt uses = new net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt.Constant(0);
        private net.minecraft.world.level.storage.loot.providers.number.floats.ResolvableFloat speedMultiplier = new net.minecraft.world.level.storage.loot.providers.number.floats.ResolvableFloat.Constant(1.0F);

        @Override
        public Builder uses(final ResolvableInt uses) {
            this.uses = PaperResolvableInt.toVanilla(uses);
            return this;
        }

        @Override
        public Builder speedMultiplier(final ResolvableFloat speedMultiplier) {
            this.speedMultiplier = PaperResolvableFloat.toVanilla(speedMultiplier);
            return this;
        }

        @Override
        public BrewingFuel build() {
            return new PaperBrewingFuel(new net.minecraft.world.item.component.BrewingFuel(this.uses, this.speedMultiplier));
        }
    }
}
