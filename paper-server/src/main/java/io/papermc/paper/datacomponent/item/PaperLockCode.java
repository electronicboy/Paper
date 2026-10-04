package io.papermc.paper.datacomponent.item;

import org.bukkit.craftbukkit.util.Handleable;

public record PaperLockCode(
    net.minecraft.world.LockCode impl
) implements LockCode, Handleable<net.minecraft.world.LockCode> {

    @Override
    public net.minecraft.world.LockCode getHandle() {
        return this.impl;
    }

    @Override
    public ItemPredicate predicate() {
        return new PaperItemPredicate(this.impl.predicate());
    }
}
