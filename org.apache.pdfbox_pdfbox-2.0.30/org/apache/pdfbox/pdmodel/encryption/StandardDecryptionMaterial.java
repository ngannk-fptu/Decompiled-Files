/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.encryption;

import org.apache.pdfbox.pdmodel.encryption.DecryptionMaterial;

public class StandardDecryptionMaterial
extends DecryptionMaterial {
    private final String password;

    public StandardDecryptionMaterial(String pwd) {
        this.password = pwd;
    }

    public String getPassword() {
        return this.password;
    }
}

