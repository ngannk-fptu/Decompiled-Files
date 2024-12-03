/*
 * Decompiled with CFR 0.152.
 */
package io.github.classgraph;

import io.github.classgraph.ClassInfo;
import io.github.classgraph.FieldInfo;
import io.github.classgraph.MappableInfoList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import nonapi.io.github.classgraph.utils.LogNode;

public class FieldInfoList
extends MappableInfoList<FieldInfo> {
    private static final long serialVersionUID = 1L;
    static final FieldInfoList EMPTY_LIST = new FieldInfoList();

    public static FieldInfoList emptyList() {
        return EMPTY_LIST;
    }

    public FieldInfoList() {
    }

    public FieldInfoList(int sizeHint) {
        super(sizeHint);
    }

    public FieldInfoList(Collection<FieldInfo> fieldInfoCollection) {
        super(fieldInfoCollection);
    }

    protected void findReferencedClassInfo(Map<String, ClassInfo> classNameToClassInfo, Set<ClassInfo> refdClassInfo, LogNode log) {
        for (FieldInfo fi : this) {
            fi.findReferencedClassInfo(classNameToClassInfo, refdClassInfo, log);
        }
    }

    public FieldInfoList filter(FieldInfoFilter filter) {
        FieldInfoList fieldInfoFiltered = new FieldInfoList();
        for (FieldInfo resource : this) {
            if (!filter.accept(resource)) continue;
            fieldInfoFiltered.add(resource);
        }
        return fieldInfoFiltered;
    }

    static {
        EMPTY_LIST.makeUnmodifiable();
    }

    @FunctionalInterface
    public static interface FieldInfoFilter {
        public boolean accept(FieldInfo var1);
    }
}

