/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.io.Serializable;
import org.hibernate.type.LiteralType;

public interface PrimitiveType<T>
extends LiteralType<T> {
    public Class getPrimitiveClass();

    public String toString(T var1);

    public Serializable getDefaultValue();
}

