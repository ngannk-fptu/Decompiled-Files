/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.HashMap;
import java.util.Map;

@Deprecated
public abstract class Parameter
implements Serializable {
    static Map<String, Parameter> allParameters = new HashMap<String, Parameter>();
    private String name;

    protected Parameter(String name) {
        this.name = name;
        String key = this.makeKey(name);
        if (allParameters.containsKey(key)) {
            throw new IllegalArgumentException("Parameter name " + key + " already used!");
        }
        allParameters.put(key, this);
    }

    private String makeKey(String name) {
        return this.getClass() + " " + name;
    }

    public String toString() {
        return this.name;
    }

    protected Object readResolve() throws ObjectStreamException {
        Parameter par = allParameters.get(this.makeKey(this.name));
        if (par == null) {
            throw new StreamCorruptedException("Unknown parameter value: " + this.name);
        }
        return par;
    }
}

