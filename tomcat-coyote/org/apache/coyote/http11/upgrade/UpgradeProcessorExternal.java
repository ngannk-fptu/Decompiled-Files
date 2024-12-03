/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletInputStream
 *  javax.servlet.ServletOutputStream
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.coyote.http11.upgrade;

import java.io.IOException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import org.apache.coyote.UpgradeToken;
import org.apache.coyote.http11.upgrade.UpgradeGroupInfo;
import org.apache.coyote.http11.upgrade.UpgradeInfo;
import org.apache.coyote.http11.upgrade.UpgradeProcessorBase;
import org.apache.coyote.http11.upgrade.UpgradeServletInputStream;
import org.apache.coyote.http11.upgrade.UpgradeServletOutputStream;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.net.SocketEvent;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.res.StringManager;

public class UpgradeProcessorExternal
extends UpgradeProcessorBase {
    private static final Log log = LogFactory.getLog(UpgradeProcessorExternal.class);
    private static final StringManager sm = StringManager.getManager(UpgradeProcessorExternal.class);
    private final UpgradeServletInputStream upgradeServletInputStream;
    private final UpgradeServletOutputStream upgradeServletOutputStream;
    private final UpgradeInfo upgradeInfo = new UpgradeInfo();

    public UpgradeProcessorExternal(SocketWrapperBase<?> wrapper, UpgradeToken upgradeToken, UpgradeGroupInfo upgradeGroupInfo) {
        super(upgradeToken);
        if (upgradeGroupInfo != null) {
            upgradeGroupInfo.addUpgradeInfo(this.upgradeInfo);
        }
        this.upgradeServletInputStream = new UpgradeServletInputStream(this, wrapper, this.upgradeInfo);
        this.upgradeServletOutputStream = new UpgradeServletOutputStream(this, wrapper, this.upgradeInfo);
        wrapper.setReadTimeout(-1L);
        wrapper.setWriteTimeout(-1L);
    }

    @Override
    protected Log getLog() {
        return log;
    }

    public void close() throws Exception {
        this.upgradeServletInputStream.close();
        this.upgradeServletOutputStream.close();
        this.upgradeInfo.setGroupInfo(null);
    }

    public ServletInputStream getInputStream() throws IOException {
        return this.upgradeServletInputStream;
    }

    public ServletOutputStream getOutputStream() throws IOException {
        return this.upgradeServletOutputStream;
    }

    @Override
    public final AbstractEndpoint.Handler.SocketState dispatch(SocketEvent status) {
        if (status == SocketEvent.OPEN_READ) {
            this.upgradeServletInputStream.onDataAvailable();
        } else if (status == SocketEvent.OPEN_WRITE) {
            this.upgradeServletOutputStream.onWritePossible();
        } else {
            if (status == SocketEvent.STOP) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("upgradeProcessor.stop"));
                }
                try {
                    this.upgradeServletInputStream.close();
                }
                catch (IOException ioe) {
                    log.debug((Object)sm.getString("upgradeProcessor.isCloseFail", new Object[]{ioe}));
                }
                try {
                    this.upgradeServletOutputStream.close();
                }
                catch (IOException ioe) {
                    log.debug((Object)sm.getString("upgradeProcessor.osCloseFail", new Object[]{ioe}));
                }
                return AbstractEndpoint.Handler.SocketState.CLOSED;
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("upgradeProcessor.unexpectedState"));
            }
            return AbstractEndpoint.Handler.SocketState.CLOSED;
        }
        if (this.upgradeServletInputStream.isClosed() && this.upgradeServletOutputStream.isClosed()) {
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("upgradeProcessor.requiredClose", new Object[]{this.upgradeServletInputStream.isClosed(), this.upgradeServletOutputStream.isClosed()}));
            }
            return AbstractEndpoint.Handler.SocketState.CLOSED;
        }
        return AbstractEndpoint.Handler.SocketState.UPGRADED;
    }

    @Override
    public final void setSslSupport(SSLSupport sslSupport) {
    }

    @Override
    public void pause() {
    }
}

