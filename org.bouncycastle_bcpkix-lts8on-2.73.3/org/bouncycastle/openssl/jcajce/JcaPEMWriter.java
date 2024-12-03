/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.util.io.pem.PemGenerationException
 *  org.bouncycastle.util.io.pem.PemObjectGenerator
 *  org.bouncycastle.util.io.pem.PemWriter
 */
package org.bouncycastle.openssl.jcajce;

import java.io.IOException;
import java.io.Writer;
import org.bouncycastle.openssl.PEMEncryptor;
import org.bouncycastle.openssl.jcajce.JcaMiscPEMGenerator;
import org.bouncycastle.util.io.pem.PemGenerationException;
import org.bouncycastle.util.io.pem.PemObjectGenerator;
import org.bouncycastle.util.io.pem.PemWriter;

public class JcaPEMWriter
extends PemWriter {
    public JcaPEMWriter(Writer out) {
        super(out);
    }

    public void writeObject(Object obj) throws IOException {
        this.writeObject(obj, null);
    }

    public void writeObject(Object obj, PEMEncryptor encryptor) throws IOException {
        try {
            super.writeObject((PemObjectGenerator)new JcaMiscPEMGenerator(obj, encryptor));
        }
        catch (PemGenerationException e) {
            if (e.getCause() instanceof IOException) {
                throw (IOException)e.getCause();
            }
            throw e;
        }
    }

    public void writeObject(PemObjectGenerator obj) throws IOException {
        super.writeObject(obj);
    }
}

