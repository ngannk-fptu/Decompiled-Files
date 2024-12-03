/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.fortuna.ical4j.model.Component;

public class ComponentList<T extends Component>
extends ArrayList<T>
implements Serializable {
    private static final long serialVersionUID = 7308557606558767449L;

    public ComponentList() {
    }

    public ComponentList(int initialCapacity) {
        super(initialCapacity);
    }

    public ComponentList(ComponentList<? extends T> components) throws ParseException, IOException, URISyntaxException {
        for (Component c : components) {
            this.add(c.copy());
        }
    }

    public ComponentList(List<? extends T> components) {
        this.addAll(components);
    }

    @Override
    public final String toString() {
        return this.stream().map(Component::toString).collect(Collectors.joining(""));
    }

    public final T getComponent(String aName) {
        for (Component c : this) {
            if (!c.getName().equals(aName)) continue;
            return (T)c;
        }
        return null;
    }

    public final <C extends T> ComponentList<C> getComponents(String name) {
        ComponentList<T> components = new ComponentList<T>();
        for (Component c : this) {
            if (!c.getName().equals(name)) continue;
            components.add(c);
        }
        return components;
    }
}

