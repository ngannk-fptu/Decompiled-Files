/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.net.SSLHostConfig
 *  org.apache.tomcat.util.net.SSLHostConfigCertificate
 *  org.apache.tomcat.util.net.SSLHostConfigCertificate$Type
 *  org.apache.tomcat.util.net.openssl.OpenSSLConf
 */
package org.apache.catalina.storeconfig;

import java.io.PrintWriter;
import java.util.ArrayList;
import org.apache.catalina.storeconfig.StoreDescription;
import org.apache.catalina.storeconfig.StoreFactoryBase;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.apache.tomcat.util.net.openssl.OpenSSLConf;

public class SSLHostConfigSF
extends StoreFactoryBase {
    @Override
    public void storeChildren(PrintWriter aWriter, int indent, Object aSSLHostConfig, StoreDescription parentDesc) throws Exception {
        if (aSSLHostConfig instanceof SSLHostConfig) {
            SSLHostConfig sslHostConfig = (SSLHostConfig)aSSLHostConfig;
            Object[] hostConfigsCertificates = sslHostConfig.getCertificates().toArray(new SSLHostConfigCertificate[0]);
            if (hostConfigsCertificates.length > 1) {
                ArrayList<Object> certificates = new ArrayList<Object>();
                for (Object certificate : hostConfigsCertificates) {
                    if (SSLHostConfigCertificate.Type.UNDEFINED == certificate.getType()) continue;
                    certificates.add(certificate);
                }
                hostConfigsCertificates = certificates.toArray(new SSLHostConfigCertificate[0]);
            }
            this.storeElementArray(aWriter, indent, hostConfigsCertificates);
            OpenSSLConf openSslConf = sslHostConfig.getOpenSslConf();
            this.storeElement(aWriter, indent, openSslConf);
        }
    }
}

