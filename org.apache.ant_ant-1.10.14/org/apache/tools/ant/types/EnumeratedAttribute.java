/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types;

import org.apache.tools.ant.BuildException;

public abstract class EnumeratedAttribute {
    protected String value;
    private int index = -1;

    public abstract String[] getValues();

    protected EnumeratedAttribute() {
    }

    public static EnumeratedAttribute getInstance(Class<? extends EnumeratedAttribute> clazz, String value) throws BuildException {
        EnumeratedAttribute ea;
        if (!EnumeratedAttribute.class.isAssignableFrom(clazz)) {
            throw new BuildException("You have to provide a subclass from EnumeratedAttribute as clazz-parameter.");
        }
        try {
            ea = clazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        }
        catch (Exception e) {
            throw new BuildException(e);
        }
        ea.setValue(value);
        return ea;
    }

    public void setValue(String value) throws BuildException {
        int idx = this.indexOfValue(value);
        if (idx == -1) {
            throw new BuildException(value + " is not a legal value for this attribute");
        }
        this.index = idx;
        this.value = value;
    }

    public final boolean containsValue(String value) {
        return this.indexOfValue(value) != -1;
    }

    public final int indexOfValue(String value) {
        String[] values = this.getValues();
        if (values == null || value == null) {
            return -1;
        }
        for (int i = 0; i < values.length; ++i) {
            if (!value.equals(values[i])) continue;
            return i;
        }
        return -1;
    }

    public final String getValue() {
        return this.value;
    }

    public final int getIndex() {
        return this.index;
    }

    public String toString() {
        return this.getValue();
    }
}

