/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.membership.cloud;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import org.apache.catalina.tribes.membership.cloud.AbstractStreamProvider;

public class InsecureStreamProvider
extends AbstractStreamProvider {
    private final SSLSocketFactory factory;

    InsecureStreamProvider() throws Exception {
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, INSECURE_TRUST_MANAGERS, null);
        this.factory = context.getSocketFactory();
    }

    @Override
    protected SSLSocketFactory getSocketFactory() {
        return this.factory;
    }
}

