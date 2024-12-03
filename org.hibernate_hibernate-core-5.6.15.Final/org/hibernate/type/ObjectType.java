/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import org.hibernate.type.AnyType;
import org.hibernate.type.BasicType;
import org.hibernate.type.SerializableType;
import org.hibernate.type.StringType;

public class ObjectType
extends AnyType
implements BasicType {
    public static final ObjectType INSTANCE = new ObjectType();

    private ObjectType() {
        super(StringType.INSTANCE, SerializableType.INSTANCE);
    }

    @Override
    public String getName() {
        return "object";
    }

    @Override
    public String[] getRegistrationKeys() {
        return new String[]{this.getName(), Object.class.getName()};
    }
}

