/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.net.openssl.OpenSSLConf
 *  org.apache.tomcat.util.net.openssl.OpenSSLConfCmd
 */
package org.apache.catalina.storeconfig;

import java.io.PrintWriter;
import org.apache.catalina.storeconfig.StoreDescription;
import org.apache.catalina.storeconfig.StoreFactoryBase;
import org.apache.tomcat.util.net.openssl.OpenSSLConf;
import org.apache.tomcat.util.net.openssl.OpenSSLConfCmd;

public class OpenSSLConfSF
extends StoreFactoryBase {
    @Override
    public void storeChildren(PrintWriter aWriter, int indent, Object aOpenSSLConf, StoreDescription parentDesc) throws Exception {
        if (aOpenSSLConf instanceof OpenSSLConf) {
            OpenSSLConf openSslConf = (OpenSSLConf)aOpenSSLConf;
            Object[] openSSLConfCmds = openSslConf.getCommands().toArray(new OpenSSLConfCmd[0]);
            this.storeElementArray(aWriter, indent + 2, openSSLConfCmds);
        }
    }
}

