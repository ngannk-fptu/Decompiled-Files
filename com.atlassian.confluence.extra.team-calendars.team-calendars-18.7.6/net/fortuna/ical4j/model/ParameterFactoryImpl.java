/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URISyntaxException;
import java.util.ServiceLoader;
import net.fortuna.ical4j.model.AbstractContentFactory;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactory;
import net.fortuna.ical4j.model.parameter.XParameter;

@Deprecated
public class ParameterFactoryImpl
extends AbstractContentFactory<ParameterFactory<? extends Parameter>> {
    private static final long serialVersionUID = -4034423507432249165L;

    protected ParameterFactoryImpl() {
        super(ServiceLoader.load(ParameterFactory.class, ParameterFactory.class.getClassLoader()));
    }

    @Override
    protected boolean factorySupports(ParameterFactory factory, String key) {
        return factory.supports(key);
    }

    public Parameter createParameter(String name, String value) throws URISyntaxException {
        Object parameter;
        ParameterFactory factory = (ParameterFactory)this.getFactory(name);
        if (factory != null) {
            parameter = factory.createParameter(value);
        } else if (this.isExperimentalName(name)) {
            parameter = new XParameter(name, value);
        } else if (this.allowIllegalNames()) {
            parameter = new XParameter(name, value);
        } else {
            throw new IllegalArgumentException(String.format("Unsupported parameter name: %s", name));
        }
        return parameter;
    }

    private boolean isExperimentalName(String name) {
        return name.startsWith("X-") && name.length() > "X-".length();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.factoryLoader = ServiceLoader.load(ParameterFactory.class, ParameterFactory.class.getClassLoader());
    }
}

