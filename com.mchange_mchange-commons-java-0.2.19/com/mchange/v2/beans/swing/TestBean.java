/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.beans.swing;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class TestBean {
    String s;
    int i;
    float f;
    PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public String getTheString() {
        return this.s;
    }

    public int getTheInt() {
        return this.i;
    }

    public float getTheFloat() {
        return this.f;
    }

    public void setTheString(String string) {
        if (!this.eqOrBothNull(string, this.s)) {
            String string2 = this.s;
            this.s = string;
            this.pcs.firePropertyChange("theString", string2, this.s);
        }
    }

    public void setTheInt(int n) {
        if (n != this.i) {
            int n2 = this.i;
            this.i = n;
            this.pcs.firePropertyChange("theInt", n2, this.i);
        }
    }

    public void setTheFloat(float f) {
        if (f != this.f) {
            float f2 = this.f;
            this.f = f;
            this.pcs.firePropertyChange("theFloat", new Float(f2), new Float(this.f));
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        this.pcs.addPropertyChangeListener(propertyChangeListener);
    }

    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        this.pcs.removePropertyChangeListener(propertyChangeListener);
    }

    private boolean eqOrBothNull(Object object, Object object2) {
        return object == object2 || object != null && object.equals(object2);
    }
}

