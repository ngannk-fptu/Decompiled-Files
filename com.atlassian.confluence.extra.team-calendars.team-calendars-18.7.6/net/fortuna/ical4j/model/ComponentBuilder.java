/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model;

import java.util.ArrayList;
import java.util.List;
import net.fortuna.ical4j.model.AbstractContentBuilder;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentFactory;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.XComponent;

public class ComponentBuilder<T extends Component>
extends AbstractContentBuilder {
    private final List<ComponentFactory<?>> factories = new ArrayList();
    private String name;
    private PropertyList<Property> properties = new PropertyList();
    private ComponentList<Component> subComponents = new ComponentList();

    public ComponentBuilder<?> factories(List<ComponentFactory<?>> factories) {
        this.factories.addAll(factories);
        return this;
    }

    public ComponentBuilder<?> name(String name) {
        this.name = name.toUpperCase();
        return this;
    }

    public ComponentBuilder<?> property(Property property) {
        this.properties.add(property);
        return this;
    }

    public ComponentBuilder<?> subComponent(Component subComponent) {
        this.subComponents.add(subComponent);
        return this;
    }

    public T build() {
        XComponent component = null;
        for (ComponentFactory<?> factory : this.factories) {
            if (!factory.supports(this.name)) continue;
            if (!this.subComponents.isEmpty()) {
                component = factory.createComponent(this.properties, this.subComponents);
                continue;
            }
            component = factory.createComponent(this.properties);
        }
        if (component == null) {
            if (this.isExperimentalName(this.name)) {
                component = new XComponent(this.name, (PropertyList)this.properties);
            } else if (this.allowIllegalNames()) {
                component = new XComponent(this.name, (PropertyList)this.properties);
            } else {
                throw new IllegalArgumentException("Unsupported component [" + this.name + "]");
            }
        }
        return (T)component;
    }
}

