/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.compat;

import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;

@Deprecated
public class TLS {
    private static final boolean tlsv13Available;

    public static boolean isTlsv13Available() {
        return tlsv13Available;
    }

    static {
        boolean ok = false;
        try {
            SSLContext.getInstance("TLSv1.3");
            ok = true;
        }
        catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            // empty catch block
        }
        tlsv13Available = ok;
    }
}

