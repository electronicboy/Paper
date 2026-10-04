package io.papermc.paper.datacomponent.item;

import io.papermc.paper.loot.number.PaperResolvableInt;
import io.papermc.paper.loot.number.ResolvableInt;
import org.bukkit.craftbukkit.util.Handleable;

public record PaperCompostable(
    net.minecraft.world.item.component.Compostable impl
) implements Compostable, Handleable<net.minecraft.world.item.component.Compostable> {

    @Override
    public net.minecraft.world.item.component.Compostable getHandle() {
        return this.impl;
    }

    @Override
    public ResolvableInt layers() {
        return PaperResolvableInt.fromVanilla(this.impl.layers());
    }
}
