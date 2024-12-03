/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.jce.provider.BouncyCastleProvider
 */
package org.apache.pdfbox.pdmodel.encryption;

import java.io.IOException;
import java.security.Provider;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class SecurityProvider {
    private static Provider provider = null;

    private SecurityProvider() {
    }

    public static Provider getProvider() throws IOException {
        if (provider == null) {
            provider = new BouncyCastleProvider();
        }
        return provider;
    }

    public static void setProvider(Provider provider) {
        SecurityProvider.provider = provider;
    }
}

