/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.Validate
 */
package net.fortuna.ical4j.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Supplier;
import net.fortuna.ical4j.util.CompatibilityHints;
import org.apache.commons.lang3.Validate;

@Deprecated
public abstract class AbstractContentFactory<T>
implements Serializable,
Supplier<List<T>> {
    private final Map<String, T> extendedFactories = new HashMap<String, T>();
    protected transient ServiceLoader factoryLoader;

    public AbstractContentFactory(ServiceLoader factoryLoader) {
        this.factoryLoader = factoryLoader;
    }

    @Deprecated
    protected final void registerExtendedFactory(String key, T factory) {
        this.extendedFactories.put(key, factory);
    }

    protected abstract boolean factorySupports(T var1, String var2);

    protected final T getFactory(String key) {
        Validate.notBlank((CharSequence)key, (String)"Invalid factory key: [%s]", (Object[])new Object[]{key});
        T factory = null;
        for (Object candidate : this.factoryLoader) {
            if (!this.factorySupports(candidate, key)) continue;
            factory = (T)candidate;
            break;
        }
        if (factory == null) {
            factory = this.extendedFactories.get(key);
        }
        return factory;
    }

    protected boolean allowIllegalNames() {
        return CompatibilityHints.isHintEnabled("ical4j.parsing.relaxed");
    }

    @Override
    public List<T> get() {
        ArrayList<Object> factories = new ArrayList<Object>();
        for (Object candidate : this.factoryLoader) {
            factories.add(candidate);
        }
        factories.addAll(this.extendedFactories.values());
        return factories;
    }
}

