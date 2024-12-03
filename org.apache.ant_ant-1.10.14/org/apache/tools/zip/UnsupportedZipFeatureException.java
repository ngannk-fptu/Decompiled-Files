/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.zip;

import java.io.Serializable;
import java.util.zip.ZipException;
import org.apache.tools.zip.ZipEntry;

public class UnsupportedZipFeatureException
extends ZipException {
    private final Feature reason;
    private final transient ZipEntry entry;
    private static final long serialVersionUID = 20161221L;

    public UnsupportedZipFeatureException(Feature reason, ZipEntry entry) {
        super("unsupported feature " + reason + " used in entry " + entry.getName());
        this.reason = reason;
        this.entry = entry;
    }

    public Feature getFeature() {
        return this.reason;
    }

    public ZipEntry getEntry() {
        return this.entry;
    }

    public static class Feature
    implements Serializable {
        public static final Feature ENCRYPTION = new Feature("encryption");
        public static final Feature METHOD = new Feature("compression method");
        public static final Feature DATA_DESCRIPTOR = new Feature("data descriptor");
        private final String name;

        private Feature(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }
}

