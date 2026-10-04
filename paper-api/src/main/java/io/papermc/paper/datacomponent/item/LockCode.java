package io.papermc.paper.datacomponent.item;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * Holds the lock of a container-like block, copied to the block when placed.
 * <p>
 * The container can only be opened while holding an item matching the {@link #predicate()}.
 *
 * @see io.papermc.paper.datacomponent.DataComponentTypes#LOCK
 * @see org.bukkit.block.Lockable
 */
@ApiStatus.Experimental
@ApiStatus.NonExtendable
public interface LockCode {

    @Contract(value = "_ -> new", pure = true)
    static LockCode lockCode(final ItemPredicate predicate) {
        return ItemComponentTypesBridge.bridge().lockCode(predicate);
    }

    /**
     * Gets the predicate an item must match to open the container.
     *
     * @return the key predicate
     */
    @Contract(pure = true)
    ItemPredicate predicate();
}
