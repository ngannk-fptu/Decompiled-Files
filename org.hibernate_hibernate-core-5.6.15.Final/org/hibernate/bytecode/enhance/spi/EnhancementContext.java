/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.bytecode.enhance.spi;

import org.hibernate.bytecode.enhance.spi.UnloadedClass;
import org.hibernate.bytecode.enhance.spi.UnloadedField;

public interface EnhancementContext {
    public ClassLoader getLoadingClassLoader();

    public boolean isEntityClass(UnloadedClass var1);

    public boolean isCompositeClass(UnloadedClass var1);

    public boolean isMappedSuperclassClass(UnloadedClass var1);

    public boolean doBiDirectionalAssociationManagement(UnloadedField var1);

    public boolean doDirtyCheckingInline(UnloadedClass var1);

    public boolean doExtendedEnhancement(UnloadedClass var1);

    public boolean hasLazyLoadableAttributes(UnloadedClass var1);

    public boolean isPersistentField(UnloadedField var1);

    public UnloadedField[] order(UnloadedField[] var1);

    public boolean isLazyLoadable(UnloadedField var1);

    public boolean isMappedCollection(UnloadedField var1);
}

