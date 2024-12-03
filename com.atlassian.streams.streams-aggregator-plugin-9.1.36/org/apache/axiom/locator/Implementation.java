/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.locator;

import java.util.Arrays;
import org.apache.axiom.locator.Feature;
import org.apache.axiom.om.OMMetaFactory;

final class Implementation {
    private final String name;
    private final OMMetaFactory metaFactory;
    private final Feature[] features;

    Implementation(String name, OMMetaFactory metaFactory, Feature[] features) {
        this.name = name;
        this.metaFactory = metaFactory;
        this.features = features;
    }

    String getName() {
        return this.name;
    }

    OMMetaFactory getMetaFactory() {
        return this.metaFactory;
    }

    Feature[] getFeatures() {
        return this.features;
    }

    public String toString() {
        return this.name + "(metaFactory=" + this.metaFactory.getClass().getName() + ",features=" + (this.features != null ? Arrays.asList(this.features) : null) + ")";
    }
}

