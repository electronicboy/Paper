package io.papermc.paper.datacomponent.item;

import io.papermc.paper.block.property.BlockProperty;
import io.papermc.paper.datacomponent.DataComponentBuilder;
import java.util.Map;
import org.bukkit.block.BlockType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

/**
 * Holds the block property currently selected by a debug stick, per block type.
 *
 * @see io.papermc.paper.datacomponent.DataComponentTypes#DEBUG_STICK_STATE
 */
@ApiStatus.NonExtendable
public interface DebugStickState {

    @Contract(value = "-> new", pure = true)
    static DebugStickState.Builder debugStickState() {
        return ItemComponentTypesBridge.bridge().debugStickState();
    }

    /**
     * Gets the selected property for each block type.
     *
     * @return the selected properties
     */
    @Contract(pure = true)
    @Unmodifiable Map<BlockType, BlockProperty<?>> properties();

    /**
     * Builder for {@link DebugStickState}.
     */
    @ApiStatus.NonExtendable
    interface Builder extends DataComponentBuilder<DebugStickState> {

        /**
         * Selects a property for a block type.
         *
         * @param blockType the block type
         * @param property  the property to select
         * @return the builder for chaining
         * @throws IllegalArgumentException if the block type does not have the property
         * @see #properties()
         */
        @Contract(value = "_, _ -> this", mutates = "this")
        Builder property(BlockType blockType, BlockProperty<?> property);

        /**
         * Selects properties for multiple block types.
         *
         * @param properties the properties to select, keyed by block type
         * @return the builder for chaining
         * @throws IllegalArgumentException if a block type does not have its property
         * @see #properties()
         */
        @Contract(value = "_ -> this", mutates = "this")
        Builder properties(Map<BlockType, ? extends BlockProperty<?>> properties);
    }
}
