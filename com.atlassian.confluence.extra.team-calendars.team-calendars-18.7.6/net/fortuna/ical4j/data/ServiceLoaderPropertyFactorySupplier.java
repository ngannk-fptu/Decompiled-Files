/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.data;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Supplier;
import net.fortuna.ical4j.model.PropertyFactory;

public class ServiceLoaderPropertyFactorySupplier
implements Supplier<List<PropertyFactory<?>>> {
    @Override
    public List<PropertyFactory<?>> get() {
        ServiceLoader<PropertyFactory> serviceLoader = ServiceLoader.load(PropertyFactory.class, PropertyFactory.class.getClassLoader());
        ArrayList factories = new ArrayList();
        serviceLoader.forEach(factories::add);
        return factories;
    }
}

