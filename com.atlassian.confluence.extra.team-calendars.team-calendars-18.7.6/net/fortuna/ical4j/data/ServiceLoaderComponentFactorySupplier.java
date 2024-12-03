/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.data;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Supplier;
import net.fortuna.ical4j.model.ComponentFactory;

public class ServiceLoaderComponentFactorySupplier
implements Supplier<List<ComponentFactory<?>>> {
    @Override
    public List<ComponentFactory<?>> get() {
        ServiceLoader<ComponentFactory> serviceLoader = ServiceLoader.load(ComponentFactory.class, ComponentFactory.class.getClassLoader());
        ArrayList factories = new ArrayList();
        serviceLoader.forEach(factories::add);
        return factories;
    }
}

