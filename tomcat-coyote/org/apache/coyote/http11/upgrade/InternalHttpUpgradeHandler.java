/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpUpgradeHandler
 */
package org.apache.coyote.http11.upgrade;

import javax.servlet.http.HttpUpgradeHandler;
import org.apache.coyote.http11.upgrade.UpgradeInfo;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.net.SocketEvent;
import org.apache.tomcat.util.net.SocketWrapperBase;

public interface InternalHttpUpgradeHandler
extends HttpUpgradeHandler {
    public AbstractEndpoint.Handler.SocketState upgradeDispatch(SocketEvent var1);

    public void timeoutAsync(long var1);

    public void setSocketWrapper(SocketWrapperBase<?> var1);

    public void setSslSupport(SSLSupport var1);

    public void pause();

    default public boolean hasAsyncIO() {
        return false;
    }

    default public UpgradeInfo getUpgradeInfo() {
        return null;
    }
}

