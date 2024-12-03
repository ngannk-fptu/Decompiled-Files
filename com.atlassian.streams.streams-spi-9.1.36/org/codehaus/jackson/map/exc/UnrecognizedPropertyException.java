/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.exc;

import org.codehaus.jackson.JsonLocation;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonMappingException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class UnrecognizedPropertyException
extends JsonMappingException {
    private static final long serialVersionUID = 1L;
    protected final Class<?> _referringClass;
    protected final String _unrecognizedPropertyName;

    public UnrecognizedPropertyException(String msg, JsonLocation loc, Class<?> referringClass, String propName) {
        super(msg, loc);
        this._referringClass = referringClass;
        this._unrecognizedPropertyName = propName;
    }

    public static UnrecognizedPropertyException from(JsonParser jp, Object fromObjectOrClass, String propertyName) {
        if (fromObjectOrClass == null) {
            throw new IllegalArgumentException();
        }
        Class<?> ref = fromObjectOrClass instanceof Class ? (Class<?>)fromObjectOrClass : fromObjectOrClass.getClass();
        String msg = "Unrecognized field \"" + propertyName + "\" (Class " + ref.getName() + "), not marked as ignorable";
        UnrecognizedPropertyException e = new UnrecognizedPropertyException(msg, jp.getCurrentLocation(), ref, propertyName);
        e.prependPath(fromObjectOrClass, propertyName);
        return e;
    }

    public Class<?> getReferringClass() {
        return this._referringClass;
    }

    public String getUnrecognizedPropertyName() {
        return this._unrecognizedPropertyName;
    }
}

