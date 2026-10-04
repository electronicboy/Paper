package io.papermc.paper.datacomponent.item;

import io.papermc.paper.loot.number.PaperResolvableFloat;
import io.papermc.paper.loot.number.PaperResolvableInt;
import io.papermc.paper.loot.number.ResolvableFloat;
import io.papermc.paper.loot.number.ResolvableInt;
import org.bukkit.craftbukkit.util.Handleable;

public record PaperCookingFuel(
    net.minecraft.world.item.component.CookingFuel impl
) implements CookingFuel, Handleable<net.minecraft.world.item.component.CookingFuel> {

    @Override
    public net.minecraft.world.item.component.CookingFuel getHandle() {
        return this.impl;
    }

    @Override
    public ResolvableInt burnTime() {
        return PaperResolvableInt.fromVanilla(this.impl.burnTime());
    }

    @Override
    public ResolvableFloat speedMultiplier() {
        return PaperResolvableFloat.fromVanilla(this.impl.speedMultiplier());
    }

    static final class BuilderImpl implements CookingFuel.Builder {

        private net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt burnTime = new net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt.Constant(0);
        private net.minecraft.world.level.storage.loot.providers.number.floats.ResolvableFloat speedMultiplier = new net.minecraft.world.level.storage.loot.providers.number.floats.ResolvableFloat.Constant(1.0F);

        @Override
        public Builder burnTime(final ResolvableInt burnTime) {
            this.burnTime = PaperResolvableInt.toVanilla(burnTime);
            return this;
        }

        @Override
        public Builder speedMultiplier(final ResolvableFloat speedMultiplier) {
            this.speedMultiplier = PaperResolvableFloat.toVanilla(speedMultiplier);
            return this;
        }

        @Override
        public CookingFuel build() {
            return new PaperCookingFuel(new net.minecraft.world.item.component.CookingFuel(this.burnTime, this.speedMultiplier));
        }
    }
}
