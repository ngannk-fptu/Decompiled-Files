/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.util.net;

import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLSession;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.net.SSLUtil;
import org.apache.tomcat.util.net.jsse.JSSEImplementation;
import org.apache.tomcat.util.res.StringManager;

public abstract class SSLImplementation {
    private static final Log logger = LogFactory.getLog(SSLImplementation.class);
    private static final StringManager sm = StringManager.getManager(SSLImplementation.class);

    public static SSLImplementation getInstance(String className) throws ClassNotFoundException {
        if (className == null) {
            return new JSSEImplementation();
        }
        try {
            Class<?> clazz = Class.forName(className);
            return (SSLImplementation)clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
        }
        catch (Exception e) {
            String msg = sm.getString("sslImplementation.cnfe", new Object[]{className});
            if (logger.isDebugEnabled()) {
                logger.debug((Object)msg, (Throwable)e);
            }
            throw new ClassNotFoundException(msg, e);
        }
    }

    public SSLSupport getSSLSupport(SSLSession session, Map<String, List<String>> additionalAttributes) {
        return this.getSSLSupport(session);
    }

    @Deprecated
    public abstract SSLSupport getSSLSupport(SSLSession var1);

    public abstract SSLUtil getSSLUtil(SSLHostConfigCertificate var1);

    public abstract boolean isAlpnSupported();
}

