/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.net.SSLHostConfig
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.security;

import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.res.StringManager;

public class TLSCertificateReloadListener
implements LifecycleListener {
    private static final Log log = LogFactory.getLog(TLSCertificateReloadListener.class);
    private static final StringManager sm = StringManager.getManager(TLSCertificateReloadListener.class);
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
    private int checkPeriod = 86400;
    private int daysBefore = 14;
    private Calendar nextCheck = Calendar.getInstance();

    public int getCheckPeriod() {
        return this.checkPeriod;
    }

    public void setCheckPeriod(int checkPeriod) {
        this.checkPeriod = checkPeriod;
    }

    public int getDaysBefore() {
        return this.daysBefore;
    }

    public void setDaysBefore(int daysBefore) {
        this.daysBefore = daysBefore;
    }

    @Override
    public void lifecycleEvent(LifecycleEvent event) {
        if (event.getType().equals("periodic")) {
            if (!(event.getSource() instanceof Server)) {
                return;
            }
            Server server = (Server)event.getSource();
            this.checkCertificatesForRenewal(server);
        } else if (event.getType().equals("before_init") && !(event.getLifecycle() instanceof Server)) {
            log.warn((Object)sm.getString("listener.notServer", new Object[]{event.getLifecycle().getClass().getSimpleName()}));
        }
    }

    private void checkCertificatesForRenewal(Server server) {
        Service[] services;
        Calendar calendar = Calendar.getInstance();
        if (calendar.compareTo(this.nextCheck) <= 0) {
            return;
        }
        this.nextCheck.add(13, this.getCheckPeriod());
        calendar.add(5, this.getDaysBefore());
        for (Service service : services = server.findServices()) {
            Connector[] connectors;
            for (Connector connector : connectors = service.findConnectors()) {
                SSLHostConfig[] sslHostConfigs;
                for (SSLHostConfig sslHostConfig : sslHostConfigs = connector.findSslHostConfigs()) {
                    if (sslHostConfig.certificatesExpiringBefore(calendar.getTime()).isEmpty()) continue;
                    try {
                        connector.getProtocolHandler().addSslHostConfig(sslHostConfig, true);
                        Set expiringCertificates = sslHostConfig.certificatesExpiringBefore(calendar.getTime());
                        log.info((Object)sm.getString("tlsCertRenewalListener.reloadSuccess", new Object[]{connector, sslHostConfig.getHostName()}));
                        if (expiringCertificates.isEmpty()) continue;
                        for (X509Certificate expiringCertificate : expiringCertificates) {
                            log.warn((Object)sm.getString("tlsCertRenewalListener.notRenewed", new Object[]{connector, sslHostConfig.getHostName(), expiringCertificate.getSubjectX500Principal().getName(), this.dateFormat.format(expiringCertificate.getNotAfter())}));
                        }
                    }
                    catch (IllegalArgumentException iae) {
                        log.error((Object)sm.getString("tlsCertRenewalListener.reloadFailed", new Object[]{connector, sslHostConfig.getHostName()}), (Throwable)iae);
                    }
                }
            }
        }
    }
}

