package io.papermc.paper.datacomponent.item;

import com.google.common.base.Preconditions;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.PaperDataComponentType;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.data.util.Conversions;
import io.papermc.paper.registry.set.PaperRegistrySets;
import io.papermc.paper.registry.set.RegistryKeySet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.advancements.predicates.DataComponentMatchers;
import net.minecraft.advancements.predicates.MinMaxBounds;
import net.minecraft.core.component.DataComponentExactPredicate;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.component.predicates.DataComponentPredicate;
import net.minecraft.core.registries.Registries;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.Nullable;

public record PaperItemPredicate(
    net.minecraft.advancements.predicates.ItemPredicate impl
) implements ItemPredicate, Handleable<net.minecraft.advancements.predicates.ItemPredicate> {

    public static net.minecraft.advancements.predicates.ItemPredicate toVanilla(final ItemPredicate predicate) {
        return ((PaperItemPredicate) predicate).impl;
    }

    @Override
    public net.minecraft.advancements.predicates.ItemPredicate getHandle() {
        return this.impl;
    }

    private DataComponentMap exactComponents() {
        return PatchedDataComponentMap.fromPatch(DataComponentMap.EMPTY, this.impl.components().exact().asPatch());
    }

    @Override
    public @Nullable RegistryKeySet<ItemType> items() {
        return this.impl.items().map(set -> PaperRegistrySets.convertToApi(RegistryKey.ITEM, set)).orElse(null);
    }

    @Override
    public @Nullable Integer minCount() {
        return this.impl.count().min().orElse(null);
    }

    @Override
    public @Nullable Integer maxCount() {
        return this.impl.count().max().orElse(null);
    }

    @Override
    public @Unmodifiable Set<DataComponentType> exactComponentTypes() {
        return PaperDataComponentType.minecraftToBukkit(this.exactComponents().keySet());
    }

    @Override
    public <T> @Nullable T exactValue(final DataComponentType.Valued<T> type) {
        return PaperDataComponentType.convertDataComponentValue(this.exactComponents(), (PaperDataComponentType.ValuedImpl<T, ?>) type);
    }

    @Override
    public @Unmodifiable Set<DataComponentType> requiredComponentTypes() {
        final Set<net.minecraft.core.component.DataComponentType<?>> types = new LinkedHashSet<>();
        for (final DataComponentPredicate.Type<?> type : this.impl.components().partial().keySet()) {
            if (type instanceof final DataComponentPredicate.AnyValueType anyValueType) {
                types.add(anyValueType.componentType());
            }
        }
        return PaperDataComponentType.minecraftToBukkit(types);
    }

    @Override
    public boolean test(final ItemStack itemStack) {
        Preconditions.checkArgument(itemStack != null, "itemStack cannot be null");
        return this.impl.test(CraftItemStack.unwrap(itemStack));
    }

    static final class BuilderImpl implements ItemPredicate.Builder {

        private @Nullable RegistryKeySet<ItemType> items;
        private MinMaxBounds.Ints count = MinMaxBounds.Ints.ANY;
        private final DataComponentExactPredicate.Builder exact = DataComponentExactPredicate.builder();
        private final Set<net.minecraft.core.component.DataComponentType<?>> required = new LinkedHashSet<>();

        @Override
        public ItemPredicate.Builder items(final @Nullable RegistryKeySet<ItemType> items) {
            this.items = items;
            return this;
        }

        @Override
        public ItemPredicate.Builder count(final @Nullable Integer min, final @Nullable Integer max) {
            Preconditions.checkArgument(min == null || max == null || min <= max, "min (%s) cannot be greater than max (%s)", min, max);
            if (min != null && max != null) {
                this.count = MinMaxBounds.Ints.between(min, max);
            } else if (min != null) {
                this.count = MinMaxBounds.Ints.atLeast(min);
            } else if (max != null) {
                this.count = MinMaxBounds.Ints.atMost(max);
            } else {
                this.count = MinMaxBounds.Ints.ANY;
            }
            return this;
        }

        @Override
        public <T> ItemPredicate.Builder exactValue(final DataComponentType.Valued<T> type, final T value) {
            Preconditions.checkArgument(value != null, "value cannot be null");
            this.expect((PaperDataComponentType.ValuedImpl<T, ?>) type, value);
            return this;
        }

        @Override
        public ItemPredicate.Builder exactValue(final DataComponentType.NonValued type) {
            this.expect((PaperDataComponentType.NonValuedImpl<?, ?>) type, null);
            return this;
        }

        private <A, V> void expect(final PaperDataComponentType<A, V> type, final @Nullable A value) {
            this.exact.expect(type.getHandle(), type.getAdapter().toVanilla(value, type.getHolder()));
        }

        @Override
        public ItemPredicate.Builder requireComponent(final DataComponentType type) {
            this.required.add(PaperDataComponentType.bukkitToMinecraft(type));
            return this;
        }

        @Override
        public ItemPredicate build() {
            final DataComponentMatchers.Builder components = DataComponentMatchers.Builder.components().exact(this.exact.build());
            this.required.forEach(components::any);
            return new PaperItemPredicate(new net.minecraft.advancements.predicates.ItemPredicate(
                Optional.ofNullable(this.items).map(set -> PaperRegistrySets.convertToNms(Registries.ITEM, Conversions.global().lookup(), set)),
                this.count,
                components.build()
            ));
        }
    }
}
