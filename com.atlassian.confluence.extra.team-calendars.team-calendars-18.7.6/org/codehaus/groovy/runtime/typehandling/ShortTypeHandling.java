/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.typehandling;

import groovy.lang.GString;
import org.codehaus.groovy.runtime.typehandling.GroovyCastException;

public class ShortTypeHandling {
    public static Class castToClass(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Class) {
            return (Class)object;
        }
        try {
            return Class.forName(object.toString());
        }
        catch (Exception e) {
            throw new GroovyCastException(object, Class.class, e);
        }
    }

    public static String castToString(Object object) {
        if (object == null) {
            return null;
        }
        return object.toString();
    }

    public static Enum castToEnum(Object object, Class<? extends Enum> type) {
        if (object == null) {
            return null;
        }
        if (type.isInstance(object)) {
            return (Enum)object;
        }
        if (object instanceof String || object instanceof GString) {
            return Enum.valueOf(type, object.toString());
        }
        throw new GroovyCastException(object, type);
    }

    public static Character castToChar(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Character) {
            return (Character)object;
        }
        if (object instanceof Number) {
            Number value = (Number)object;
            return Character.valueOf((char)value.intValue());
        }
        String text = object.toString();
        if (text.length() == 1) {
            return Character.valueOf(text.charAt(0));
        }
        throw new GroovyCastException((Object)text, Character.TYPE);
    }
}

