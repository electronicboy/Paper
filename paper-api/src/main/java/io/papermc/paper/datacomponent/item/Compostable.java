package io.papermc.paper.datacomponent.item;

import io.papermc.paper.loot.number.ResolvableInt;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * Describes an item that can be inserted into a composter.
 *
 * @see io.papermc.paper.datacomponent.DataComponentTypes#COMPOSTABLE
 */
@ApiStatus.NonExtendable
public interface Compostable {

    @Contract(value = "_ -> new", pure = true)
    static Compostable compostable(final ResolvableInt layers) {
        return ItemComponentTypesBridge.bridge().compostable(layers);
    }

    /**
     * Gets the number of layers added to the composter when this item is inserted.
     * <p>
     * Vanilla items reference a context provider which resolves to either {@code 0} or {@code 1}
     * based on a chance, such as {@code minecraft:compostable/medium}.
     *
     * @return the number of layers added
     */
    @Contract(pure = true)
    ResolvableInt layers();
}
