/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.ssl;

import java.util.ArrayList;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.http.ssl.TlsVersionParser;
import org.apache.hc.core5.util.Tokenizer;

public enum TLS {
    V_1_0("TLSv1", new ProtocolVersion("TLS", 1, 0)),
    V_1_1("TLSv1.1", new ProtocolVersion("TLS", 1, 1)),
    V_1_2("TLSv1.2", new ProtocolVersion("TLS", 1, 2)),
    V_1_3("TLSv1.3", new ProtocolVersion("TLS", 1, 3));

    public final String id;
    public final ProtocolVersion version;

    private TLS(String id, ProtocolVersion version) {
        this.id = id;
        this.version = version;
    }

    public boolean isSame(ProtocolVersion protocolVersion) {
        return this.version.equals(protocolVersion);
    }

    public boolean isComparable(ProtocolVersion protocolVersion) {
        return this.version.isComparable(protocolVersion);
    }

    public String getId() {
        return this.id;
    }

    public ProtocolVersion getVersion() {
        return this.version;
    }

    public boolean greaterEquals(ProtocolVersion protocolVersion) {
        return this.version.greaterEquals(protocolVersion);
    }

    public boolean lessEquals(ProtocolVersion protocolVersion) {
        return this.version.lessEquals(protocolVersion);
    }

    public static ProtocolVersion parse(String s) throws ParseException {
        if (s == null) {
            return null;
        }
        Tokenizer.Cursor cursor = new Tokenizer.Cursor(0, s.length());
        return TlsVersionParser.INSTANCE.parse(s, cursor, null);
    }

    public static String[] excludeWeak(String ... protocols) {
        if (protocols == null) {
            return null;
        }
        ArrayList<String> enabledProtocols = new ArrayList<String>();
        for (String protocol : protocols) {
            if (!TLS.isSecure(protocol)) continue;
            enabledProtocols.add(protocol);
        }
        if (enabledProtocols.isEmpty()) {
            enabledProtocols.add(TLS.V_1_2.id);
        }
        return enabledProtocols.toArray(new String[0]);
    }

    public static boolean isSecure(String protocol) {
        return !protocol.startsWith("SSL") && !protocol.equals(TLS.V_1_0.id) && !protocol.equals(TLS.V_1_1.id);
    }
}

