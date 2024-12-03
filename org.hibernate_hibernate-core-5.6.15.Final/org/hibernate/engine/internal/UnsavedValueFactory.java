/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.internal;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import org.hibernate.InstantiationException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.IdentifierValue;
import org.hibernate.engine.spi.VersionValue;
import org.hibernate.property.access.spi.Getter;
import org.hibernate.type.IdentifierType;
import org.hibernate.type.PrimitiveType;
import org.hibernate.type.Type;
import org.hibernate.type.VersionType;

public class UnsavedValueFactory {
    private static Object instantiate(Constructor constructor) {
        try {
            return constructor.newInstance(new Object[0]);
        }
        catch (Exception e) {
            throw new InstantiationException("could not instantiate test object", constructor.getDeclaringClass(), e);
        }
    }

    public static IdentifierValue getUnsavedIdentifierValue(String unsavedValue, Getter identifierGetter, Type identifierType, Constructor constructor) {
        if (unsavedValue == null) {
            if (identifierGetter != null && constructor != null) {
                Serializable defaultValue = (Serializable)identifierGetter.get(UnsavedValueFactory.instantiate(constructor));
                return new IdentifierValue(defaultValue);
            }
            if (identifierGetter != null && identifierType instanceof PrimitiveType) {
                Serializable defaultValue = ((PrimitiveType)((Object)identifierType)).getDefaultValue();
                return new IdentifierValue(defaultValue);
            }
            return IdentifierValue.NULL;
        }
        if ("null".equals(unsavedValue)) {
            return IdentifierValue.NULL;
        }
        if ("undefined".equals(unsavedValue)) {
            return IdentifierValue.UNDEFINED;
        }
        if ("none".equals(unsavedValue)) {
            return IdentifierValue.NONE;
        }
        if ("any".equals(unsavedValue)) {
            return IdentifierValue.ANY;
        }
        try {
            return new IdentifierValue((Serializable)((IdentifierType)identifierType).stringToObject(unsavedValue));
        }
        catch (ClassCastException cce) {
            throw new MappingException("Bad identifier type: " + identifierType.getName());
        }
        catch (Exception e) {
            throw new MappingException("Could not parse identifier unsaved-value: " + unsavedValue);
        }
    }

    public static VersionValue getUnsavedVersionValue(String versionUnsavedValue, Getter versionGetter, VersionType versionType, Constructor constructor) {
        if (versionUnsavedValue == null) {
            if (constructor != null) {
                Object defaultValue = versionGetter.get(UnsavedValueFactory.instantiate(constructor));
                return versionType.isEqual(versionType.seed(null), defaultValue) ? VersionValue.UNDEFINED : new VersionValue(defaultValue);
            }
            return VersionValue.UNDEFINED;
        }
        if ("undefined".equals(versionUnsavedValue)) {
            return VersionValue.UNDEFINED;
        }
        if ("null".equals(versionUnsavedValue)) {
            return VersionValue.NULL;
        }
        if ("negative".equals(versionUnsavedValue)) {
            return VersionValue.NEGATIVE;
        }
        throw new MappingException("Could not parse version unsaved-value: " + versionUnsavedValue);
    }

    private UnsavedValueFactory() {
    }
}

