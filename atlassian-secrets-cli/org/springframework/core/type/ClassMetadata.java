/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.type;

import org.springframework.lang.Nullable;

public interface ClassMetadata {
    public String getClassName();

    public boolean isInterface();

    public boolean isAnnotation();

    public boolean isAbstract();

    public boolean isConcrete();

    public boolean isFinal();

    public boolean isIndependent();

    public boolean hasEnclosingClass();

    @Nullable
    public String getEnclosingClassName();

    public boolean hasSuperClass();

    @Nullable
    public String getSuperClassName();

    public String[] getInterfaceNames();

    public String[] getMemberClassNames();
}

