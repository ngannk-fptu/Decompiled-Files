/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Property;

public class IndexedComponentList<T extends Component> {
    private final ComponentList<T> EMPTY_LIST = new ComponentList();
    private Map<String, ComponentList<T>> index;

    public IndexedComponentList(ComponentList<T> list, String propertyName) {
        HashMap indexedComponents = new HashMap();
        for (Component component : list) {
            for (Property property : component.getProperties(propertyName)) {
                ComponentList components = (ComponentList)indexedComponents.get(property.getValue());
                if (components == null) {
                    components = new ComponentList();
                    indexedComponents.put(property.getValue(), components);
                }
                components.add(component);
            }
        }
        this.index = Collections.unmodifiableMap(indexedComponents);
    }

    public ComponentList<T> getComponents(String propertyValue) {
        ComponentList<T> components = this.index.get(propertyValue);
        if (components == null) {
            components = this.EMPTY_LIST;
        }
        return components;
    }

    public T getComponent(String propertyValue) {
        ComponentList<T> components = this.getComponents(propertyValue);
        if (!components.isEmpty()) {
            return (T)((Component)components.get(0));
        }
        return null;
    }
}

