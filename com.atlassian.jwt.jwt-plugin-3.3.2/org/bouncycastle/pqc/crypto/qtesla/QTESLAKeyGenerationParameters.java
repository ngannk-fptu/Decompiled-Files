/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.qtesla;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.qtesla.QTESLASecurityCategory;

public class QTESLAKeyGenerationParameters
extends KeyGenerationParameters {
    private final int securityCategory;

    public QTESLAKeyGenerationParameters(int n, SecureRandom secureRandom) {
        super(secureRandom, -1);
        QTESLASecurityCategory.getPrivateSize(n);
        this.securityCategory = n;
    }

    public int getSecurityCategory() {
        return this.securityCategory;
    }
}

