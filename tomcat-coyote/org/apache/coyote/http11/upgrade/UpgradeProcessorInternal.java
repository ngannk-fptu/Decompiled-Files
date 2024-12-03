/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletInputStream
 *  javax.servlet.ServletOutputStream
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.coyote.http11.upgrade;

import java.io.IOException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import org.apache.coyote.UpgradeToken;
import org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler;
import org.apache.coyote.http11.upgrade.UpgradeGroupInfo;
import org.apache.coyote.http11.upgrade.UpgradeInfo;
import org.apache.coyote.http11.upgrade.UpgradeProcessorBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.net.SocketEvent;
import org.apache.tomcat.util.net.SocketWrapperBase;

public class UpgradeProcessorInternal
extends UpgradeProcessorBase {
    private static final Log log = LogFactory.getLog(UpgradeProcessorInternal.class);
    private final InternalHttpUpgradeHandler internalHttpUpgradeHandler;

    public UpgradeProcessorInternal(SocketWrapperBase<?> wrapper, UpgradeToken upgradeToken, UpgradeGroupInfo upgradeGroupInfo) {
        super(upgradeToken);
        this.internalHttpUpgradeHandler = (InternalHttpUpgradeHandler)upgradeToken.getHttpUpgradeHandler();
        wrapper.setReadTimeout(-1L);
        wrapper.setWriteTimeout(-1L);
        this.internalHttpUpgradeHandler.setSocketWrapper(wrapper);
        UpgradeInfo upgradeInfo = this.internalHttpUpgradeHandler.getUpgradeInfo();
        if (upgradeInfo != null && upgradeGroupInfo != null) {
            upgradeInfo.setGroupInfo(upgradeGroupInfo);
        }
    }

    @Override
    public AbstractEndpoint.Handler.SocketState dispatch(SocketEvent status) {
        return this.internalHttpUpgradeHandler.upgradeDispatch(status);
    }

    @Override
    public final void setSslSupport(SSLSupport sslSupport) {
        this.internalHttpUpgradeHandler.setSslSupport(sslSupport);
    }

    @Override
    public void pause() {
        this.internalHttpUpgradeHandler.pause();
    }

    @Override
    protected Log getLog() {
        return log;
    }

    @Override
    public void timeoutAsync(long now) {
        this.internalHttpUpgradeHandler.timeoutAsync(now);
    }

    public boolean hasAsyncIO() {
        return this.internalHttpUpgradeHandler.hasAsyncIO();
    }

    public void close() throws Exception {
        UpgradeInfo upgradeInfo = this.internalHttpUpgradeHandler.getUpgradeInfo();
        if (upgradeInfo != null) {
            upgradeInfo.setGroupInfo(null);
        }
        this.internalHttpUpgradeHandler.destroy();
    }

    public ServletInputStream getInputStream() throws IOException {
        return null;
    }

    public ServletOutputStream getOutputStream() throws IOException {
        return null;
    }
}

