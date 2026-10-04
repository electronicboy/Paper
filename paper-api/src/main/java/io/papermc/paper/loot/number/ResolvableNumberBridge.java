package io.papermc.paper.loot.number;

import java.util.Optional;
import java.util.ServiceLoader;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
interface ResolvableNumberBridge {

    static ResolvableNumberBridge bridge() {
        final class Holder {
            static final Optional<ResolvableNumberBridge> INSTANCE = ServiceLoader.load(ResolvableNumberBridge.class, ResolvableNumberBridge.class.getClassLoader()).findFirst();
        }

        return Holder.INSTANCE.orElseThrow();
    }

    ResolvableInt.Constant intConstant(int value);

    ResolvableInt.Reference intReference(Key key);

    ResolvableFloat.Constant floatConstant(float value);

    ResolvableFloat.Reference floatReference(Key key);
}
