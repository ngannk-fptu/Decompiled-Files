/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.typehandling;

public class GroovyCastException
extends ClassCastException {
    public GroovyCastException(Object objectToCast, Class classToCastTo, Exception cause) {
        super(GroovyCastException.makeMessage(objectToCast, classToCastTo) + " due to: " + cause.getClass().getName() + (cause.getMessage() == null ? "" : ": " + cause.getMessage()));
    }

    public GroovyCastException(Object objectToCast, Class classToCastTo) {
        super(GroovyCastException.makeMessage(objectToCast, classToCastTo));
    }

    public GroovyCastException(String message) {
        super(message);
    }

    private static String makeMessage(Object objectToCast, Class classToCastTo) {
        String classToCastFrom;
        Object msgObject = objectToCast;
        if (objectToCast != null) {
            classToCastFrom = objectToCast.getClass().getName();
        } else {
            msgObject = "null";
            classToCastFrom = "null";
        }
        String msg = "Cannot cast object '" + msgObject + "' with class '" + classToCastFrom + "' to class '" + classToCastTo.getName() + "'";
        if (objectToCast == null) {
            msg = msg + GroovyCastException.getWrapper(classToCastTo);
        }
        return msg;
    }

    private static String getWrapper(Class cls) {
        Class ncls = cls;
        if (cls == Byte.TYPE) {
            ncls = Byte.class;
        } else if (cls == Short.TYPE) {
            ncls = Short.class;
        } else if (cls == Character.TYPE) {
            ncls = Character.class;
        } else if (cls == Integer.TYPE) {
            ncls = Integer.class;
        } else if (cls == Long.TYPE) {
            ncls = Long.class;
        } else if (cls == Float.TYPE) {
            ncls = Float.class;
        } else if (cls == Double.TYPE) {
            ncls = Double.class;
        }
        if (cls != null && ncls != cls) {
            String msg = ". Try '" + ncls.getName() + "' instead";
            return msg;
        }
        return "";
    }
}

