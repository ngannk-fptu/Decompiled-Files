/*
 * Decompiled with CFR 0.152.
 */
package org.apache.naming;

import javax.naming.StringRefAddr;
import org.apache.naming.AbstractRef;

public class EjbRef
extends AbstractRef {
    private static final long serialVersionUID = 1L;
    public static final String DEFAULT_FACTORY = "org.apache.naming.factory.EjbFactory";
    public static final String TYPE = "type";
    public static final String REMOTE = "remote";
    public static final String LINK = "link";

    public EjbRef(String ejbType, String home, String remote, String link) {
        this(ejbType, home, remote, link, null, null);
    }

    public EjbRef(String ejbType, String home, String remote, String link, String factory, String factoryLocation) {
        super(home, factory, factoryLocation);
        StringRefAddr refAddr = null;
        if (ejbType != null) {
            refAddr = new StringRefAddr(TYPE, ejbType);
            this.add(refAddr);
        }
        if (remote != null) {
            refAddr = new StringRefAddr(REMOTE, remote);
            this.add(refAddr);
        }
        if (link != null) {
            refAddr = new StringRefAddr(LINK, link);
            this.add(refAddr);
        }
    }

    @Override
    protected String getDefaultFactoryClassName() {
        return DEFAULT_FACTORY;
    }
}

