/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.osgi;

import org.codehaus.stax2.XMLInputFactory2;

public interface Stax2InputFactoryProvider {
    public static final String OSGI_SVC_PROP_IMPL_NAME = "org.codehaus.stax2.implName";
    public static final String OSGI_SVC_PROP_IMPL_VERSION = "org.codehaus.stax2.implVersion";

    public XMLInputFactory2 createInputFactory();
}

