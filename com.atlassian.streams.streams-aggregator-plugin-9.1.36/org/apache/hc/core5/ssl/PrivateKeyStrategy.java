/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.ssl;

import java.util.Map;
import javax.net.ssl.SSLParameters;
import org.apache.hc.core5.ssl.PrivateKeyDetails;

public interface PrivateKeyStrategy {
    public String chooseAlias(Map<String, PrivateKeyDetails> var1, SSLParameters var2);
}

