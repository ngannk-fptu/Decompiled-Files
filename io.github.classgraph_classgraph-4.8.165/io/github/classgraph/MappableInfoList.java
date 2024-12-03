/*
 * Decompiled with CFR 0.152.
 */
package io.github.classgraph;

import io.github.classgraph.HasName;
import io.github.classgraph.InfoList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MappableInfoList<T extends HasName>
extends InfoList<T> {
    private static final long serialVersionUID = 1L;

    MappableInfoList() {
    }

    MappableInfoList(int sizeHint) {
        super(sizeHint);
    }

    MappableInfoList(Collection<T> infoCollection) {
        super(infoCollection);
    }

    public Map<String, T> asMap() {
        HashMap<String, HasName> nameToInfoObject = new HashMap<String, HasName>();
        for (HasName i : this) {
            if (i == null) continue;
            nameToInfoObject.put(i.getName(), i);
        }
        return nameToInfoObject;
    }

    public boolean containsName(String name) {
        for (HasName i : this) {
            if (i == null || !i.getName().equals(name)) continue;
            return true;
        }
        return false;
    }

    public T get(String name) {
        for (HasName i : this) {
            if (i == null || !i.getName().equals(name)) continue;
            return (T)i;
        }
        return null;
    }
}

