/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.data;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Supplier;
import net.fortuna.ical4j.model.ParameterFactory;

public class ServiceLoaderParameterFactorySupplier
implements Supplier<List<ParameterFactory<?>>> {
    @Override
    public List<ParameterFactory<?>> get() {
        ServiceLoader<ParameterFactory> serviceLoader = ServiceLoader.load(ParameterFactory.class, ParameterFactory.class.getClassLoader());
        ArrayList factories = new ArrayList();
        serviceLoader.forEach(factories::add);
        return factories;
    }
}

