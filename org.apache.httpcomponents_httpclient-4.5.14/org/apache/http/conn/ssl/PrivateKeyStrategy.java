/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.conn.ssl;

import java.net.Socket;
import java.util.Map;
import org.apache.http.conn.ssl.PrivateKeyDetails;

@Deprecated
public interface PrivateKeyStrategy {
    public String chooseAlias(Map<String, PrivateKeyDetails> var1, Socket var2);
}

