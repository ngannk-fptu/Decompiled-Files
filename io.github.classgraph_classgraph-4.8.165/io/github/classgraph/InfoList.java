/*
 * Decompiled with CFR 0.152.
 */
package io.github.classgraph;

import io.github.classgraph.HasName;
import io.github.classgraph.PotentiallyUnmodifiableList;
import io.github.classgraph.ScanResultObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class InfoList<T extends HasName>
extends PotentiallyUnmodifiableList<T> {
    static final long serialVersionUID = 1L;

    InfoList() {
    }

    InfoList(int sizeHint) {
        super(sizeHint);
    }

    InfoList(Collection<T> infoCollection) {
        super(infoCollection);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public List<String> getNames() {
        if (this.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<String> names = new ArrayList<String>(this.size());
        for (HasName i : this) {
            if (i == null) continue;
            names.add(i.getName());
        }
        return names;
    }

    public List<String> getAsStrings() {
        if (this.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<String> toStringVals = new ArrayList<String>(this.size());
        for (HasName i : this) {
            toStringVals.add(i == null ? "null" : i.toString());
        }
        return toStringVals;
    }

    public List<String> getAsStringsWithSimpleNames() {
        if (this.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<String> toStringVals = new ArrayList<String>(this.size());
        for (HasName i : this) {
            toStringVals.add(i == null ? "null" : (i instanceof ScanResultObject ? ((ScanResultObject)((Object)i)).toStringWithSimpleNames() : i.toString()));
        }
        return toStringVals;
    }
}

