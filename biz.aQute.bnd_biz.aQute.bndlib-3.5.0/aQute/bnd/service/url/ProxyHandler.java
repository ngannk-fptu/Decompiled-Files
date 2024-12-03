/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.service.url;

import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;

public interface ProxyHandler {
    public ProxySetup forURL(URL var1) throws Exception;

    public static class ProxySetup {
        public Proxy proxy;
        public PasswordAuthentication authentication;

        public String toString() {
            return "Proxy [proxy=" + this.proxy + ", authentication=" + (this.authentication == null ? null : this.authentication.getUserName()) + "]";
        }
    }
}

