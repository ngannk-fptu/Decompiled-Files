/*
 * Decompiled with CFR 0.152.
 */
package org.apache.naming;

import javax.naming.StringRefAddr;
import org.apache.naming.AbstractRef;

public class ResourceLinkRef
extends AbstractRef {
    private static final long serialVersionUID = 1L;
    public static final String DEFAULT_FACTORY = "org.apache.naming.factory.ResourceLinkFactory";
    public static final String GLOBALNAME = "globalName";

    public ResourceLinkRef(String resourceClass, String globalName, String factory, String factoryLocation) {
        super(resourceClass, factory, factoryLocation);
        StringRefAddr refAddr = null;
        if (globalName != null) {
            refAddr = new StringRefAddr(GLOBALNAME, globalName);
            this.add(refAddr);
        }
    }

    @Override
    protected String getDefaultFactoryClassName() {
        return DEFAULT_FACTORY;
    }
}

