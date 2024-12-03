/*
 * Decompiled with CFR 0.152.
 */
package org.apache.naming;

import org.apache.naming.AbstractRef;

public class TransactionRef
extends AbstractRef {
    private static final long serialVersionUID = 1L;
    public static final String DEFAULT_FACTORY = "org.apache.naming.factory.TransactionFactory";

    public TransactionRef() {
        this((String)null, (String)null);
    }

    public TransactionRef(String factory, String factoryLocation) {
        super("javax.transaction.UserTransaction", factory, factoryLocation);
    }

    @Override
    protected String getDefaultFactoryClassName() {
        return DEFAULT_FACTORY;
    }
}

