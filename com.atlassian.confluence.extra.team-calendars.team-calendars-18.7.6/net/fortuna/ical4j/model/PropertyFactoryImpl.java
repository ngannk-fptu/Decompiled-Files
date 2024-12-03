/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ServiceLoader;
import net.fortuna.ical4j.model.AbstractContentFactory;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.model.property.XProperty;

@Deprecated
public class PropertyFactoryImpl
extends AbstractContentFactory<PropertyFactory<? extends Property>> {
    private static final long serialVersionUID = -7174232004486979641L;

    protected PropertyFactoryImpl() {
        super(ServiceLoader.load(PropertyFactory.class, PropertyFactory.class.getClassLoader()));
    }

    @Override
    protected boolean factorySupports(PropertyFactory factory, String key) {
        return factory.supports(key);
    }

    public Property createProperty(String name) {
        PropertyFactory factory = (PropertyFactory)this.getFactory(name);
        if (factory != null) {
            return factory.createProperty();
        }
        if (this.isExperimentalName(name)) {
            return new XProperty(name);
        }
        if (this.allowIllegalNames()) {
            return new XProperty(name);
        }
        throw new IllegalArgumentException("Illegal property [" + name + "]");
    }

    public Property createProperty(String name, ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
        PropertyFactory factory = (PropertyFactory)this.getFactory(name);
        if (factory != null) {
            return factory.createProperty(parameters, value);
        }
        if (this.isExperimentalName(name)) {
            return new XProperty(name, parameters, value);
        }
        if (this.allowIllegalNames()) {
            return new XProperty(name, parameters, value);
        }
        throw new IllegalArgumentException("Illegal property [" + name + "]");
    }

    private boolean isExperimentalName(String name) {
        return name.startsWith("X-") && name.length() > "X-".length();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.factoryLoader = ServiceLoader.load(PropertyFactory.class, PropertyFactory.class.getClassLoader());
    }
}

