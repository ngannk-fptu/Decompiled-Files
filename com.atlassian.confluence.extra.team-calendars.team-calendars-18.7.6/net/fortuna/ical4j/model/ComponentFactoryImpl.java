/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ServiceLoader;
import net.fortuna.ical4j.model.AbstractContentFactory;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentFactory;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.XComponent;

@Deprecated
public final class ComponentFactoryImpl
extends AbstractContentFactory<ComponentFactory> {
    public ComponentFactoryImpl() {
        super(ServiceLoader.load(ComponentFactory.class, ComponentFactory.class.getClassLoader()));
    }

    @Override
    protected boolean factorySupports(ComponentFactory factory, String key) {
        return factory.supports(key);
    }

    public <T extends Component> T createComponent(String name) {
        Object component;
        ComponentFactory factory = (ComponentFactory)this.getFactory(name);
        if (factory != null) {
            component = factory.createComponent();
        } else if (this.isExperimentalName(name)) {
            component = new XComponent(name);
        } else if (this.allowIllegalNames()) {
            component = new XComponent(name);
        } else {
            throw new IllegalArgumentException("Unsupported component [" + name + "]");
        }
        return component;
    }

    public <T extends Component> T createComponent(String name, PropertyList properties) {
        Object component;
        ComponentFactory factory = (ComponentFactory)this.getFactory(name);
        if (factory != null) {
            component = factory.createComponent(properties);
        } else if (this.isExperimentalName(name)) {
            component = new XComponent(name, properties);
        } else if (this.allowIllegalNames()) {
            component = new XComponent(name, properties);
        } else {
            throw new IllegalArgumentException("Unsupported component [" + name + "]");
        }
        return component;
    }

    public <T extends Component> T createComponent(String name, PropertyList properties, ComponentList<? extends Component> components) {
        ComponentFactory factory = (ComponentFactory)this.getFactory(name);
        if (factory == null) {
            throw new IllegalArgumentException("Unsupported component [" + name + "]");
        }
        Object component = factory.createComponent(properties, components);
        return component;
    }

    private boolean isExperimentalName(String name) {
        return name.startsWith("X-") && name.length() > "X-".length();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.factoryLoader = ServiceLoader.load(ComponentFactory.class, ComponentFactory.class.getClassLoader());
    }
}

