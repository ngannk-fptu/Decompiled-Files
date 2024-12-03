/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1BitString
 *  org.bouncycastle.asn1.DERBitString
 */
package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.DERBitString;

public class PKIFailureInfo
extends DERBitString {
    public static final int badAlg = 128;
    public static final int badMessageCheck = 64;
    public static final int badRequest = 32;
    public static final int badTime = 16;
    public static final int badCertId = 8;
    public static final int badDataFormat = 4;
    public static final int wrongAuthority = 2;
    public static final int incorrectData = 1;
    public static final int missingTimeStamp = 32768;
    public static final int badPOP = 16384;
    public static final int certRevoked = 8192;
    public static final int certConfirmed = 4096;
    public static final int wrongIntegrity = 2048;
    public static final int badRecipientNonce = 1024;
    public static final int timeNotAvailable = 512;
    public static final int unacceptedPolicy = 256;
    public static final int unacceptedExtension = 0x800000;
    public static final int addInfoNotAvailable = 0x400000;
    public static final int badSenderNonce = 0x200000;
    public static final int badCertTemplate = 0x100000;
    public static final int signerNotTrusted = 524288;
    public static final int transactionIdInUse = 262144;
    public static final int unsupportedVersion = 131072;
    public static final int notAuthorized = 65536;
    public static final int systemUnavail = Integer.MIN_VALUE;
    public static final int systemFailure = 0x40000000;
    public static final int duplicateCertReq = 0x20000000;

    public PKIFailureInfo(int info) {
        super(PKIFailureInfo.getBytes((int)info), PKIFailureInfo.getPadBits((int)info));
    }

    public PKIFailureInfo(ASN1BitString info) {
        super(info.getBytes(), info.getPadBits());
    }

    public String toString() {
        return "PKIFailureInfo: 0x" + Integer.toHexString(this.intValue());
    }
}

