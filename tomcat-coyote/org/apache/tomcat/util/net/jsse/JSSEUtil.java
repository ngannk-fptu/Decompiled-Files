/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.compat.JreVendor
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.util.net.jsse;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.compat.JreVendor;
import org.apache.tomcat.util.net.SSLContext;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.apache.tomcat.util.net.SSLUtilBase;
import org.apache.tomcat.util.net.jsse.JSSESSLContext;
import org.apache.tomcat.util.res.StringManager;

public class JSSEUtil
extends SSLUtilBase {
    private static final Log log = LogFactory.getLog(JSSEUtil.class);
    private static final StringManager sm = StringManager.getManager(JSSEUtil.class);
    private volatile boolean initialized = false;
    private volatile Set<String> implementedProtocols;
    private volatile Set<String> implementedCiphers;

    public JSSEUtil(SSLHostConfigCertificate certificate) {
        this(certificate, true);
    }

    public JSSEUtil(SSLHostConfigCertificate certificate, boolean warnOnSkip) {
        super(certificate, warnOnSkip);
    }

    @Override
    protected Log getLog() {
        return log;
    }

    @Override
    protected Set<String> getImplementedProtocols() {
        this.initialise();
        return this.implementedProtocols;
    }

    @Override
    protected Set<String> getImplementedCiphers() {
        this.initialise();
        return this.implementedCiphers;
    }

    @Override
    protected boolean isTls13RenegAuthAvailable() {
        return false;
    }

    @Override
    public SSLContext createSSLContextInternal(List<String> negotiableProtocols) throws NoSuchAlgorithmException {
        return new JSSESSLContext(this.sslHostConfig.getSslProtocol());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void initialise() {
        if (!this.initialized) {
            JSSEUtil jSSEUtil = this;
            synchronized (jSSEUtil) {
                if (!this.initialized) {
                    JSSESSLContext context;
                    try {
                        context = new JSSESSLContext(this.sslHostConfig.getSslProtocol());
                        context.init(null, null, null);
                    }
                    catch (KeyManagementException | NoSuchAlgorithmException e) {
                        throw new IllegalArgumentException(e);
                    }
                    String[] implementedProtocolsArray = context.getSupportedSSLParameters().getProtocols();
                    this.implementedProtocols = new HashSet<String>(implementedProtocolsArray.length);
                    for (String protocol : implementedProtocolsArray) {
                        String protocolUpper = protocol.toUpperCase(Locale.ENGLISH);
                        if (!"SSLV2HELLO".equals(protocolUpper) && !"SSLV3".equals(protocolUpper) && protocolUpper.contains("SSL")) {
                            log.debug((Object)sm.getString("jsseUtil.excludeProtocol", new Object[]{protocol}));
                            continue;
                        }
                        this.implementedProtocols.add(protocol);
                    }
                    if (this.implementedProtocols.size() == 0) {
                        log.warn((Object)sm.getString("jsseUtil.noDefaultProtocols"));
                    }
                    String[] implementedCipherSuiteArray = context.getSupportedSSLParameters().getCipherSuites();
                    if (JreVendor.IS_IBM_JVM) {
                        this.implementedCiphers = new HashSet<String>(implementedCipherSuiteArray.length * 2);
                        for (String name : implementedCipherSuiteArray) {
                            this.implementedCiphers.add(name);
                            if (!name.startsWith("SSL")) continue;
                            this.implementedCiphers.add("TLS" + name.substring(3));
                        }
                    } else {
                        this.implementedCiphers = new HashSet<String>(Arrays.asList(implementedCipherSuiteArray));
                    }
                    this.initialized = true;
                }
            }
        }
    }
}

