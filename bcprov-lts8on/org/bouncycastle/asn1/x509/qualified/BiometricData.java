/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509.qualified;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.qualified.TypeOfBiometricData;

public class BiometricData
extends ASN1Object {
    private TypeOfBiometricData typeOfBiometricData;
    private AlgorithmIdentifier hashAlgorithm;
    private ASN1OctetString biometricDataHash;
    private ASN1IA5String sourceDataUri;

    public static BiometricData getInstance(Object obj) {
        if (obj instanceof BiometricData) {
            return (BiometricData)obj;
        }
        if (obj != null) {
            return new BiometricData(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private BiometricData(ASN1Sequence seq) {
        Enumeration e = seq.getObjects();
        this.typeOfBiometricData = TypeOfBiometricData.getInstance(e.nextElement());
        this.hashAlgorithm = AlgorithmIdentifier.getInstance(e.nextElement());
        this.biometricDataHash = ASN1OctetString.getInstance(e.nextElement());
        if (e.hasMoreElements()) {
            this.sourceDataUri = ASN1IA5String.getInstance(e.nextElement());
        }
    }

    public BiometricData(TypeOfBiometricData typeOfBiometricData, AlgorithmIdentifier hashAlgorithm, ASN1OctetString biometricDataHash, ASN1IA5String sourceDataUri) {
        this.typeOfBiometricData = typeOfBiometricData;
        this.hashAlgorithm = hashAlgorithm;
        this.biometricDataHash = biometricDataHash;
        this.sourceDataUri = sourceDataUri;
    }

    public BiometricData(TypeOfBiometricData typeOfBiometricData, AlgorithmIdentifier hashAlgorithm, ASN1OctetString biometricDataHash) {
        this.typeOfBiometricData = typeOfBiometricData;
        this.hashAlgorithm = hashAlgorithm;
        this.biometricDataHash = biometricDataHash;
        this.sourceDataUri = null;
    }

    public TypeOfBiometricData getTypeOfBiometricData() {
        return this.typeOfBiometricData;
    }

    public AlgorithmIdentifier getHashAlgorithm() {
        return this.hashAlgorithm;
    }

    public ASN1OctetString getBiometricDataHash() {
        return this.biometricDataHash;
    }

    public ASN1IA5String getSourceDataUriIA5() {
        return this.sourceDataUri;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector seq = new ASN1EncodableVector(4);
        seq.add(this.typeOfBiometricData);
        seq.add(this.hashAlgorithm);
        seq.add(this.biometricDataHash);
        if (this.sourceDataUri != null) {
            seq.add(this.sourceDataUri);
        }
        return new DERSequence(seq);
    }
}

