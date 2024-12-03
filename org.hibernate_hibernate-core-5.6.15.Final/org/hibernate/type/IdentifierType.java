/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import org.hibernate.type.Type;

public interface IdentifierType<T>
extends Type {
    public T stringToObject(String var1) throws Exception;
}

