/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.service.blueprint.container;

import java.util.Collection;
import java.util.Set;
import org.osgi.service.blueprint.reflect.ComponentMetadata;

public interface BlueprintContainer {
    public Set getComponentIds();

    public Object getComponentInstance(String var1);

    public ComponentMetadata getComponentMetadata(String var1);

    public Collection getMetadata(Class var1);
}

