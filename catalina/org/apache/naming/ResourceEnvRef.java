/*
 * Decompiled with CFR 0.152.
 */
package org.apache.naming;

import org.apache.naming.AbstractRef;

public class ResourceEnvRef
extends AbstractRef {
    private static final long serialVersionUID = 1L;
    public static final String DEFAULT_FACTORY = "org.apache.naming.factory.ResourceEnvFactory";

    public ResourceEnvRef(String resourceType) {
        super(resourceType);
    }

    @Override
    protected String getDefaultFactoryClassName() {
        return DEFAULT_FACTORY;
    }
}

