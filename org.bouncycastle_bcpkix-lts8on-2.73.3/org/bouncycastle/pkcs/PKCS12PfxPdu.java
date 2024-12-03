/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.pkcs.ContentInfo
 *  org.bouncycastle.asn1.pkcs.MacData
 *  org.bouncycastle.asn1.pkcs.PKCS12PBEParams
 *  org.bouncycastle.asn1.pkcs.Pfx
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.pkcs;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import org.bouncycastle.asn1.pkcs.MacData;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.pkcs.Pfx;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.pkcs.MacDataGenerator;
import org.bouncycastle.pkcs.PKCS12MacCalculatorBuilderProvider;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.pkcs.PKCSIOException;
import org.bouncycastle.util.Arrays;

public class PKCS12PfxPdu {
    private Pfx pfx;

    private static Pfx parseBytes(byte[] pfxEncoding) throws IOException {
        try {
            return Pfx.getInstance((Object)ASN1Primitive.fromByteArray((byte[])pfxEncoding));
        }
        catch (ClassCastException e) {
            throw new PKCSIOException("malformed data: " + e.getMessage(), e);
        }
        catch (IllegalArgumentException e) {
            throw new PKCSIOException("malformed data: " + e.getMessage(), e);
        }
    }

    public PKCS12PfxPdu(Pfx pfx) {
        this.pfx = pfx;
    }

    public PKCS12PfxPdu(byte[] pfx) throws IOException {
        this(PKCS12PfxPdu.parseBytes(pfx));
    }

    public ContentInfo[] getContentInfos() {
        ASN1Sequence seq = ASN1Sequence.getInstance((Object)ASN1OctetString.getInstance((Object)this.pfx.getAuthSafe().getContent()).getOctets());
        ContentInfo[] content = new ContentInfo[seq.size()];
        for (int i = 0; i != seq.size(); ++i) {
            content[i] = ContentInfo.getInstance((Object)seq.getObjectAt(i));
        }
        return content;
    }

    public boolean hasMac() {
        return this.pfx.getMacData() != null;
    }

    public AlgorithmIdentifier getMacAlgorithmID() {
        MacData md = this.pfx.getMacData();
        if (md != null) {
            return md.getMac().getAlgorithmId();
        }
        return null;
    }

    public boolean isMacValid(PKCS12MacCalculatorBuilderProvider macCalcProviderBuilder, char[] password) throws PKCSException {
        if (this.hasMac()) {
            MacData pfxmData = this.pfx.getMacData();
            MacDataGenerator mdGen = new MacDataGenerator(macCalcProviderBuilder.get(new AlgorithmIdentifier(pfxmData.getMac().getAlgorithmId().getAlgorithm(), (ASN1Encodable)new PKCS12PBEParams(pfxmData.getSalt(), pfxmData.getIterationCount().intValue()))));
            try {
                MacData mData = mdGen.build(password, ASN1OctetString.getInstance((Object)this.pfx.getAuthSafe().getContent()).getOctets());
                return Arrays.constantTimeAreEqual((byte[])mData.getEncoded(), (byte[])this.pfx.getMacData().getEncoded());
            }
            catch (IOException e) {
                throw new PKCSException("unable to process AuthSafe: " + e.getMessage());
            }
        }
        throw new IllegalStateException("no MAC present on PFX");
    }

    public Pfx toASN1Structure() {
        return this.pfx;
    }

    public byte[] getEncoded() throws IOException {
        return this.toASN1Structure().getEncoded();
    }

    public byte[] getEncoded(String encoding) throws IOException {
        return this.toASN1Structure().getEncoded(encoding);
    }
}

