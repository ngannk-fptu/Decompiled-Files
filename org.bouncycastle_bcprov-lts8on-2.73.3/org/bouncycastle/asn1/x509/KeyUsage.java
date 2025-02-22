/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;

public class KeyUsage
extends ASN1Object {
    public static final int digitalSignature = 128;
    public static final int nonRepudiation = 64;
    public static final int keyEncipherment = 32;
    public static final int dataEncipherment = 16;
    public static final int keyAgreement = 8;
    public static final int keyCertSign = 4;
    public static final int cRLSign = 2;
    public static final int encipherOnly = 1;
    public static final int decipherOnly = 32768;
    private ASN1BitString bitString;

    public static KeyUsage getInstance(Object obj) {
        if (obj instanceof KeyUsage) {
            return (KeyUsage)obj;
        }
        if (obj != null) {
            return new KeyUsage(ASN1BitString.getInstance(obj));
        }
        return null;
    }

    public static KeyUsage fromExtensions(Extensions extensions) {
        return KeyUsage.getInstance(Extensions.getExtensionParsedValue(extensions, Extension.keyUsage));
    }

    public KeyUsage(int usage) {
        this.bitString = new DERBitString(usage);
    }

    private KeyUsage(ASN1BitString bitString) {
        this.bitString = bitString;
    }

    public boolean hasUsages(int usages) {
        return (this.bitString.intValue() & usages) == usages;
    }

    public byte[] getBytes() {
        return this.bitString.getBytes();
    }

    public int getPadBits() {
        return this.bitString.getPadBits();
    }

    public String toString() {
        byte[] data = this.bitString.getBytes();
        if (data.length == 1) {
            return "KeyUsage: 0x" + Integer.toHexString(data[0] & 0xFF);
        }
        return "KeyUsage: 0x" + Integer.toHexString((data[1] & 0xFF) << 8 | data[0] & 0xFF);
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.bitString;
    }
}

