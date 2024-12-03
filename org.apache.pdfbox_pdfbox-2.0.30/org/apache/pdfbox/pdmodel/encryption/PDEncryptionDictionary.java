/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.encryption;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.encryption.PDEncryption;

@Deprecated
public class PDEncryptionDictionary
extends PDEncryption {
    public PDEncryptionDictionary() {
    }

    public PDEncryptionDictionary(COSDictionary dictionary) {
        super(dictionary);
    }
}

