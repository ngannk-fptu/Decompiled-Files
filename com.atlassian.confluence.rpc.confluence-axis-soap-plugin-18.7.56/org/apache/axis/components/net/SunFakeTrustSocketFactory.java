/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.net.ssl.SSLContext
 *  com.sun.net.ssl.TrustManager
 *  com.sun.net.ssl.X509TrustManager
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.components.net;

import com.sun.net.ssl.SSLContext;
import com.sun.net.ssl.TrustManager;
import com.sun.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Hashtable;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.components.net.SunJSSESocketFactory;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public class SunFakeTrustSocketFactory
extends SunJSSESocketFactory {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$components$net$SunFakeTrustSocketFactory == null ? (class$org$apache$axis$components$net$SunFakeTrustSocketFactory = SunFakeTrustSocketFactory.class$("org.apache.axis.components.net.SunFakeTrustSocketFactory")) : class$org$apache$axis$components$net$SunFakeTrustSocketFactory).getName());
    static /* synthetic */ Class class$org$apache$axis$components$net$SunFakeTrustSocketFactory;
    static /* synthetic */ Class class$org$apache$axis$components$net$SunFakeTrustSocketFactory$FakeX509TrustManager;

    public SunFakeTrustSocketFactory(Hashtable attributes) {
        super(attributes);
    }

    protected SSLContext getContext() throws Exception {
        try {
            SSLContext sc = SSLContext.getInstance((String)"SSL");
            sc.init(null, new TrustManager[]{new FakeX509TrustManager()}, new SecureRandom());
            if (log.isDebugEnabled()) {
                log.debug((Object)Messages.getMessage("ftsf00"));
            }
            return sc;
        }
        catch (Exception exc) {
            log.error((Object)Messages.getMessage("ftsf01"), (Throwable)exc);
            throw new Exception(Messages.getMessage("ftsf02"));
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    public static class FakeX509TrustManager
    implements X509TrustManager {
        protected static Log log = LogFactory.getLog((class$org$apache$axis$components$net$SunFakeTrustSocketFactory$FakeX509TrustManager == null ? (class$org$apache$axis$components$net$SunFakeTrustSocketFactory$FakeX509TrustManager = SunFakeTrustSocketFactory.class$("org.apache.axis.components.net.SunFakeTrustSocketFactory$FakeX509TrustManager")) : class$org$apache$axis$components$net$SunFakeTrustSocketFactory$FakeX509TrustManager).getName());

        public boolean isClientTrusted(X509Certificate[] chain) {
            if (log.isDebugEnabled()) {
                log.debug((Object)Messages.getMessage("ftsf03"));
            }
            return true;
        }

        public boolean isServerTrusted(X509Certificate[] chain) {
            if (log.isDebugEnabled()) {
                log.debug((Object)Messages.getMessage("ftsf04"));
            }
            return true;
        }

        public X509Certificate[] getAcceptedIssuers() {
            if (log.isDebugEnabled()) {
                log.debug((Object)Messages.getMessage("ftsf05"));
            }
            return null;
        }
    }
}

