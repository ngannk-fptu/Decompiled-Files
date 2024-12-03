/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.utils;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import org.apache.axis.components.net.TransportClientProperties;
import org.apache.axis.components.net.TransportClientPropertiesFactory;

public class DefaultAuthenticator
extends Authenticator {
    private TransportClientProperties tcp = null;
    private String user;
    private String password;

    public DefaultAuthenticator(String user, String pass) {
        this.user = user;
        this.password = pass;
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        if (this.user == null) {
            this.user = this.getTransportClientProperties().getProxyUser();
        }
        if (this.password == null) {
            this.password = this.getTransportClientProperties().getProxyPassword();
        }
        return new PasswordAuthentication(this.user, this.password.toCharArray());
    }

    private TransportClientProperties getTransportClientProperties() {
        if (this.tcp == null) {
            this.tcp = TransportClientPropertiesFactory.create("http");
        }
        return this.tcp;
    }
}

