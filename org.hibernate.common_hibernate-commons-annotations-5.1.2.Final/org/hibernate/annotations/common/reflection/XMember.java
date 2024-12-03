/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations.common.reflection;

import java.util.Collection;
import org.hibernate.annotations.common.reflection.XAnnotatedElement;
import org.hibernate.annotations.common.reflection.XClass;

public interface XMember
extends XAnnotatedElement {
    public XClass getDeclaringClass();

    public String getName();

    public boolean isCollection();

    public boolean isArray();

    public Class<? extends Collection> getCollectionClass();

    public XClass getType();

    public XClass getElementClass();

    public XClass getClassOrElementClass();

    public XClass getMapKey();

    public int getModifiers();

    public void setAccessible(boolean var1);

    public Object invoke(Object var1, Object ... var2);

    public Object invoke(Object var1);

    public boolean isTypeResolved();
}

