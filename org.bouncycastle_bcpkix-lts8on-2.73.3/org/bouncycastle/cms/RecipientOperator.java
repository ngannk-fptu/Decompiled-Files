/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.util.io.TeeInputStream
 */
package org.bouncycastle.cms;

import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.operator.InputAEADDecryptor;
import org.bouncycastle.operator.InputDecryptor;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.util.io.TeeInputStream;

public class RecipientOperator {
    private final Object operator;

    public RecipientOperator(InputDecryptor decryptor) {
        this.operator = decryptor;
    }

    public RecipientOperator(MacCalculator macCalculator) {
        this.operator = macCalculator;
    }

    public InputStream getInputStream(InputStream dataIn) {
        if (this.operator instanceof InputDecryptor) {
            return ((InputDecryptor)this.operator).getInputStream(dataIn);
        }
        return new TeeInputStream(dataIn, ((MacCalculator)this.operator).getOutputStream());
    }

    public boolean isAEADBased() {
        return this.operator instanceof InputAEADDecryptor;
    }

    public OutputStream getAADStream() {
        return ((InputAEADDecryptor)this.operator).getAADStream();
    }

    public boolean isMacBased() {
        return this.operator instanceof MacCalculator;
    }

    public byte[] getMac() {
        return ((MacCalculator)this.operator).getMac();
    }
}

