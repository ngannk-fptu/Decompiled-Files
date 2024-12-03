/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.digester.Rule
 *  org.apache.tomcat.util.net.SSLHostConfig
 *  org.apache.tomcat.util.net.SSLHostConfigCertificate
 *  org.apache.tomcat.util.net.SSLHostConfigCertificate$Type
 */
package org.apache.catalina.startup;

import org.apache.tomcat.util.digester.Rule;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.xml.sax.Attributes;

public class CertificateCreateRule
extends Rule {
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        SSLHostConfig sslHostConfig = (SSLHostConfig)this.digester.peek();
        String typeValue = attributes.getValue("type");
        SSLHostConfigCertificate.Type type = typeValue == null || typeValue.length() == 0 ? SSLHostConfigCertificate.Type.UNDEFINED : SSLHostConfigCertificate.Type.valueOf((String)typeValue);
        SSLHostConfigCertificate certificate = new SSLHostConfigCertificate(sslHostConfig, type);
        this.digester.push((Object)certificate);
        StringBuilder code = this.digester.getGeneratedCode();
        if (code != null) {
            code.append(SSLHostConfigCertificate.class.getName()).append(' ').append(this.digester.toVariableName((Object)certificate));
            code.append(" = new ").append(SSLHostConfigCertificate.class.getName());
            code.append('(').append(this.digester.toVariableName((Object)sslHostConfig));
            code.append(", ").append(SSLHostConfigCertificate.Type.class.getName().replace('$', '.')).append('.').append(type).append(");");
            code.append(System.lineSeparator());
        }
    }

    public void end(String namespace, String name) throws Exception {
        this.digester.pop();
    }
}

