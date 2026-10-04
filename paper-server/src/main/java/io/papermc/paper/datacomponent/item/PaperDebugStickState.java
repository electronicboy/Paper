package io.papermc.paper.datacomponent.item;

import com.google.common.base.Preconditions;
import io.papermc.paper.block.property.BlockProperty;
import io.papermc.paper.block.property.PaperBlockProperties;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import org.bukkit.block.BlockType;
import org.bukkit.craftbukkit.block.CraftBlockType;
import org.bukkit.craftbukkit.util.Handleable;
import org.jetbrains.annotations.Unmodifiable;

public record PaperDebugStickState(
    net.minecraft.world.item.component.DebugStickState impl
) implements DebugStickState, Handleable<net.minecraft.world.item.component.DebugStickState> {

    @Override
    public net.minecraft.world.item.component.DebugStickState getHandle() {
        return this.impl;
    }

    @Override
    public @Unmodifiable Map<BlockType, BlockProperty<?>> properties() {
        final Map<BlockType, BlockProperty<?>> properties = new LinkedHashMap<>(this.impl.properties().size());
        this.impl.properties().forEach((block, property) -> {
            properties.put(CraftBlockType.minecraftToBukkitNew(block.value()), PaperBlockProperties.convertToPaperProperty(property));
        });
        return Collections.unmodifiableMap(properties);
    }

    static final class BuilderImpl implements DebugStickState.Builder {

        private final Map<Holder<Block>, Property<?>> properties = new IdentityHashMap<>();

        @Override
        public DebugStickState.Builder property(final BlockType blockType, final BlockProperty<?> property) {
            Preconditions.checkArgument(blockType != null, "blockType cannot be null");
            Preconditions.checkArgument(property != null, "property cannot be null");
            final Block block = CraftBlockType.bukkitToMinecraftNew(blockType);
            // look up by name on the block's own state definition to get the exact instance vanilla uses
            final Property<?> nmsProperty = block.getStateDefinition().getProperty(property.name());
            Preconditions.checkArgument(
                nmsProperty != null && PaperBlockProperties.convertToPaperProperty(nmsProperty) == property,
                "%s does not have property %s", blockType.key(), property.name()
            );
            this.properties.put(block.builtInRegistryHolder(), nmsProperty);
            return this;
        }

        @Override
        public DebugStickState.Builder properties(final Map<BlockType, ? extends BlockProperty<?>> properties) {
            properties.forEach(this::property);
            return this;
        }

        @Override
        public DebugStickState build() {
            if (this.properties.isEmpty()) {
                return new PaperDebugStickState(net.minecraft.world.item.component.DebugStickState.EMPTY);
            }
            return new PaperDebugStickState(new net.minecraft.world.item.component.DebugStickState(Map.copyOf(this.properties)));
        }
    }
}
