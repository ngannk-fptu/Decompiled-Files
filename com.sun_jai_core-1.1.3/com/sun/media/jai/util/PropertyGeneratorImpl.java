/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.util;

import com.sun.media.jai.util.JaiI18N;
import javax.media.jai.PropertyGenerator;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;

public abstract class PropertyGeneratorImpl
implements PropertyGenerator {
    private String[] propertyNames;
    private Class[] propertyClasses;
    private Class[] supportedOpClasses;

    protected PropertyGeneratorImpl(String[] propertyNames, Class[] propertyClasses, Class[] supportedOpClasses) {
        if (propertyNames == null || propertyClasses == null || supportedOpClasses == null) {
            throw new IllegalArgumentException(JaiI18N.getString("PropertyGeneratorImpl0"));
        }
        if (propertyNames.length == 0 || propertyClasses.length == 0 || supportedOpClasses.length == 0) {
            throw new IllegalArgumentException(JaiI18N.getString("PropertyGeneratorImpl1"));
        }
        if (propertyNames.length != propertyClasses.length) {
            throw new IllegalArgumentException(JaiI18N.getString("PropertyGeneratorImpl2"));
        }
        for (int i = 0; i < propertyClasses.length; ++i) {
            if (!propertyClasses[i].isPrimitive()) continue;
            throw new IllegalArgumentException(JaiI18N.getString("PropertyGeneratorImpl4"));
        }
        this.propertyNames = propertyNames;
        this.propertyClasses = propertyClasses;
        this.supportedOpClasses = supportedOpClasses;
    }

    public String[] getPropertyNames() {
        return this.propertyNames;
    }

    public Class getClass(String propertyName) {
        if (propertyName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("PropertyGeneratorImpl0"));
        }
        int numProperties = this.propertyNames.length;
        for (int i = 0; i < numProperties; ++i) {
            if (!propertyName.equalsIgnoreCase(this.propertyNames[i])) continue;
            return this.propertyClasses[i];
        }
        return null;
    }

    public boolean canGenerateProperties(Object opNode) {
        if (opNode == null) {
            throw new IllegalArgumentException(JaiI18N.getString("PropertyGeneratorImpl0"));
        }
        int numClasses = this.supportedOpClasses.length;
        if (numClasses == 1) {
            return this.supportedOpClasses[0].isInstance(opNode);
        }
        for (int i = 0; i < numClasses; ++i) {
            if (!this.supportedOpClasses[i].isInstance(opNode)) continue;
            return true;
        }
        return false;
    }

    public abstract Object getProperty(String var1, Object var2);

    public Object getProperty(String name, RenderedOp op) {
        return this.getProperty(name, (Object)op);
    }

    public Object getProperty(String name, RenderableOp op) {
        return this.getProperty(name, (Object)op);
    }

    protected void validate(String name, Object opNode) {
        if (name == null) {
            throw new IllegalArgumentException(JaiI18N.getString("PropertyGeneratorImpl0"));
        }
        if (!this.canGenerateProperties(opNode)) {
            throw new IllegalArgumentException(JaiI18N.getString("PropertyGeneratorImpl3"));
        }
    }
}

