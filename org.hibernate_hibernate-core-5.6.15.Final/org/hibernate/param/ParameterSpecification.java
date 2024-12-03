/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.param;

import org.hibernate.param.ParameterBinder;
import org.hibernate.type.Type;

public interface ParameterSpecification
extends ParameterBinder {
    public Type getExpectedType();

    public void setExpectedType(Type var1);

    public String renderDisplayInfo();
}

