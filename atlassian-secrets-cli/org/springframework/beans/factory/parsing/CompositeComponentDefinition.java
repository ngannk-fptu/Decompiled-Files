/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.parsing;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.parsing.AbstractComponentDefinition;
import org.springframework.beans.factory.parsing.ComponentDefinition;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class CompositeComponentDefinition
extends AbstractComponentDefinition {
    private final String name;
    @Nullable
    private final Object source;
    private final List<ComponentDefinition> nestedComponents = new ArrayList<ComponentDefinition>();

    public CompositeComponentDefinition(String name, @Nullable Object source) {
        Assert.notNull((Object)name, "Name must not be null");
        this.name = name;
        this.source = source;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    @Nullable
    public Object getSource() {
        return this.source;
    }

    public void addNestedComponent(ComponentDefinition component) {
        Assert.notNull((Object)component, "ComponentDefinition must not be null");
        this.nestedComponents.add(component);
    }

    public ComponentDefinition[] getNestedComponents() {
        return this.nestedComponents.toArray(new ComponentDefinition[0]);
    }
}

