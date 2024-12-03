/*
 * Decompiled with CFR 0.152.
 */
package org.apache.naming;

import javax.naming.StringRefAddr;
import org.apache.naming.AbstractRef;

public class LookupRef
extends AbstractRef {
    private static final long serialVersionUID = 1L;
    public static final String LOOKUP_NAME = "lookup-name";

    public LookupRef(String resourceType, String lookupName) {
        this(resourceType, (String)null, (String)null, lookupName);
    }

    public LookupRef(String resourceType, String factory, String factoryLocation, String lookupName) {
        super(resourceType, factory, factoryLocation);
        if (lookupName != null && !lookupName.equals("")) {
            StringRefAddr ref = new StringRefAddr(LOOKUP_NAME, lookupName);
            this.add(ref);
        }
    }

    @Override
    protected String getDefaultFactoryClassName() {
        return "org.apache.naming.factory.LookupFactory";
    }
}

