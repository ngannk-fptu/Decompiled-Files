/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.path;

import org.apache.avro.path.PathElement;

public class LocationStep
implements PathElement {
    private final String selector;
    private final String propertyName;

    public LocationStep(String selector, String propertyName) {
        this.selector = selector;
        this.propertyName = propertyName;
    }

    public String toString() {
        if (this.propertyName == null || this.propertyName.isEmpty()) {
            return this.selector;
        }
        return this.selector + this.propertyName;
    }
}

