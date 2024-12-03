/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.timezones.model;

import java.util.ArrayList;
import java.util.List;
import org.bedework.util.misc.ToString;

public class CapabilitiesAcceptParameterType {
    protected String name;
    protected boolean required;
    protected boolean multi;
    protected List<String> values;

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public boolean isRequired() {
        return this.required;
    }

    public void setRequired(boolean value) {
        this.required = value;
    }

    public boolean isMulti() {
        return this.multi;
    }

    public void setMulti(boolean value) {
        this.multi = value;
    }

    public List<String> getValues() {
        return this.values;
    }

    public void addValue(String value) {
        if (this.values == null) {
            this.values = new ArrayList<String>();
        }
        this.values.add(value);
    }

    public String toString() {
        ToString ts = new ToString(this);
        ts.append("name", this.getName());
        ts.append("required", this.isRequired());
        ts.append("multi", this.isMulti());
        ts.append("values", this.getValues());
        return ts.toString();
    }
}

