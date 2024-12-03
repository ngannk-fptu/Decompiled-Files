/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.xsd;

import javax.xml.validation.Schema;

public final class XsdDescriptor {
    private final String localResourceName;
    private final String namespaceUri;
    private final String version;
    private final Schema schema;

    XsdDescriptor(String localResourceName, Schema schema, String version, String namespaceUri) {
        this.localResourceName = localResourceName;
        this.schema = schema;
        this.version = version;
        this.namespaceUri = namespaceUri;
    }

    public String getLocalResourceName() {
        return this.localResourceName;
    }

    public String getNamespaceUri() {
        return this.namespaceUri;
    }

    public String getVersion() {
        return this.version;
    }

    public Schema getSchema() {
        return this.schema;
    }
}

