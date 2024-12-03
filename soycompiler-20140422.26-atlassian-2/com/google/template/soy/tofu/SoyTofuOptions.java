/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.tofu;

public class SoyTofuOptions
implements Cloneable {
    private boolean useCaching = false;

    public void setUseCaching(boolean useCaching) {
        this.useCaching = useCaching;
    }

    public boolean useCaching() {
        return this.useCaching;
    }

    public SoyTofuOptions clone() {
        try {
            return (SoyTofuOptions)super.clone();
        }
        catch (CloneNotSupportedException cnse) {
            throw new RuntimeException("Cloneable interface removed from SoyTofuOptions.");
        }
    }
}

