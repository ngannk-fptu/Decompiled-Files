/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation.bytecode.collection;

import java.util.List;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bytecode.StackManipulation;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface CollectionFactory {
    public TypeDescription.Generic getComponentType();

    public StackManipulation withValues(List<? extends StackManipulation> var1);
}

