/*
 * Decompiled with CFR 0.152.
 */
package org.apache.coyote;

import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.Adapter;
import org.apache.coyote.Processor;
import org.apache.coyote.Request;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler;
import org.apache.tomcat.util.net.SocketWrapperBase;

public interface UpgradeProtocol {
    public String getHttpUpgradeName(boolean var1);

    public byte[] getAlpnIdentifier();

    public String getAlpnName();

    public Processor getProcessor(SocketWrapperBase<?> var1, Adapter var2);

    public InternalHttpUpgradeHandler getInternalUpgradeHandler(SocketWrapperBase<?> var1, Adapter var2, Request var3);

    public boolean accept(Request var1);

    default public void setHttp11Protocol(AbstractHttp11Protocol<?> protocol) {
    }

    @Deprecated
    default public void setHttp11Protocol(AbstractProtocol<?> protocol) {
        if (protocol instanceof AbstractHttp11Protocol) {
            this.setHttp11Protocol((AbstractHttp11Protocol)protocol);
        }
    }
}

