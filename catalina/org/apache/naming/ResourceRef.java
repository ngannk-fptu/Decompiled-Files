/*
 * Decompiled with CFR 0.152.
 */
package org.apache.naming;

import javax.naming.StringRefAddr;
import org.apache.naming.AbstractRef;

public class ResourceRef
extends AbstractRef {
    private static final long serialVersionUID = 1L;
    public static final String DEFAULT_FACTORY = "org.apache.naming.factory.ResourceFactory";
    public static final String DESCRIPTION = "description";
    public static final String SCOPE = "scope";
    public static final String AUTH = "auth";
    public static final String SINGLETON = "singleton";

    public ResourceRef(String resourceClass, String description, String scope, String auth, boolean singleton) {
        this(resourceClass, description, scope, auth, singleton, null, null);
    }

    public ResourceRef(String resourceClass, String description, String scope, String auth, boolean singleton, String factory, String factoryLocation) {
        super(resourceClass, factory, factoryLocation);
        StringRefAddr refAddr = null;
        if (description != null) {
            refAddr = new StringRefAddr(DESCRIPTION, description);
            this.add(refAddr);
        }
        if (scope != null) {
            refAddr = new StringRefAddr(SCOPE, scope);
            this.add(refAddr);
        }
        if (auth != null) {
            refAddr = new StringRefAddr(AUTH, auth);
            this.add(refAddr);
        }
        refAddr = new StringRefAddr(SINGLETON, Boolean.toString(singleton));
        this.add(refAddr);
    }

    @Override
    protected String getDefaultFactoryClassName() {
        return DEFAULT_FACTORY;
    }
}

