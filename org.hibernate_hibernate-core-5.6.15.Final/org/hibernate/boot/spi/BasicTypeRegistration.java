/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.spi;

import org.hibernate.type.BasicType;
import org.hibernate.type.CompositeCustomType;
import org.hibernate.type.CustomType;
import org.hibernate.usertype.CompositeUserType;
import org.hibernate.usertype.UserType;

public class BasicTypeRegistration {
    private final BasicType basicType;
    private final String[] registrationKeys;

    public BasicTypeRegistration(BasicType basicType) {
        this(basicType, basicType.getRegistrationKeys());
    }

    public BasicTypeRegistration(BasicType basicType, String[] registrationKeys) {
        this.basicType = basicType;
        this.registrationKeys = registrationKeys;
    }

    public BasicTypeRegistration(UserType type, String[] keys) {
        this(new CustomType(type, keys), keys);
    }

    public BasicTypeRegistration(CompositeUserType type, String[] keys) {
        this(new CompositeCustomType(type, keys), keys);
    }

    public BasicType getBasicType() {
        return this.basicType;
    }

    public String[] getRegistrationKeys() {
        return this.registrationKeys;
    }
}

