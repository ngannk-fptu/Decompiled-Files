/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DLSequence
 *  org.bouncycastle.asn1.pkcs.AuthenticatedSafe
 *  org.bouncycastle.asn1.pkcs.ContentInfo
 *  org.bouncycastle.asn1.pkcs.MacData
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.pkcs.Pfx
 */
package org.bouncycastle.pkcs;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.asn1.pkcs.AuthenticatedSafe;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import org.bouncycastle.asn1.pkcs.MacData;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.Pfx;
import org.bouncycastle.cms.CMSEncryptedDataGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.pkcs.MacDataGenerator;
import org.bouncycastle.pkcs.PKCS12MacCalculatorBuilder;
import org.bouncycastle.pkcs.PKCS12PfxPdu;
import org.bouncycastle.pkcs.PKCS12SafeBag;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.pkcs.PKCSIOException;

public class PKCS12PfxPduBuilder {
    private ASN1EncodableVector dataVector = new ASN1EncodableVector();

    public PKCS12PfxPduBuilder addData(PKCS12SafeBag data) throws IOException {
        this.dataVector.add((ASN1Encodable)new ContentInfo(PKCSObjectIdentifiers.data, (ASN1Encodable)new DEROctetString(new DLSequence((ASN1Encodable)data.toASN1Structure()).getEncoded())));
        return this;
    }

    public PKCS12PfxPduBuilder addEncryptedData(OutputEncryptor dataEncryptor, PKCS12SafeBag data) throws IOException {
        return this.addEncryptedData(dataEncryptor, (ASN1Sequence)new DERSequence((ASN1Encodable)data.toASN1Structure()));
    }

    public PKCS12PfxPduBuilder addEncryptedData(OutputEncryptor dataEncryptor, PKCS12SafeBag[] data) throws IOException {
        ASN1EncodableVector v = new ASN1EncodableVector();
        for (int i = 0; i != data.length; ++i) {
            v.add((ASN1Encodable)data[i].toASN1Structure());
        }
        return this.addEncryptedData(dataEncryptor, (ASN1Sequence)new DLSequence(v));
    }

    private PKCS12PfxPduBuilder addEncryptedData(OutputEncryptor dataEncryptor, ASN1Sequence data) throws IOException {
        CMSEncryptedDataGenerator envGen = new CMSEncryptedDataGenerator();
        try {
            this.dataVector.add((ASN1Encodable)envGen.generate(new CMSProcessableByteArray(data.getEncoded()), dataEncryptor).toASN1Structure());
        }
        catch (CMSException e) {
            throw new PKCSIOException(e.getMessage(), e.getCause());
        }
        return this;
    }

    public PKCS12PfxPdu build(PKCS12MacCalculatorBuilder macCalcBuilder, char[] password) throws PKCSException {
        byte[] encAuth;
        AuthenticatedSafe auth = AuthenticatedSafe.getInstance((Object)new DLSequence(this.dataVector));
        try {
            encAuth = auth.getEncoded();
        }
        catch (IOException e) {
            throw new PKCSException("unable to encode AuthenticatedSafe: " + e.getMessage(), e);
        }
        ContentInfo mainInfo = new ContentInfo(PKCSObjectIdentifiers.data, (ASN1Encodable)new DEROctetString(encAuth));
        MacData mData = null;
        if (macCalcBuilder != null) {
            MacDataGenerator mdGen = new MacDataGenerator(macCalcBuilder);
            mData = mdGen.build(password, encAuth);
        }
        Pfx pfx = new Pfx(mainInfo, mData);
        return new PKCS12PfxPdu(pfx);
    }
}

