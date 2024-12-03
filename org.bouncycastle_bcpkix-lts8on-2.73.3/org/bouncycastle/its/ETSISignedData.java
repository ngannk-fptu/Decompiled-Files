/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.oer.Element
 *  org.bouncycastle.oer.OEREncoder
 *  org.bouncycastle.oer.OERInputStream
 *  org.bouncycastle.oer.its.etsi103097.EtsiTs103097DataSigned
 *  org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Content
 *  org.bouncycastle.oer.its.ieee1609dot2.Opaque
 *  org.bouncycastle.oer.its.ieee1609dot2.SignedData
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.Signature
 *  org.bouncycastle.oer.its.template.etsi103097.EtsiTs103097Module
 *  org.bouncycastle.oer.its.template.ieee1609dot2.IEEE1609dot2
 */
package org.bouncycastle.its;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.its.operator.ECDSAEncoder;
import org.bouncycastle.its.operator.ITSContentVerifierProvider;
import org.bouncycastle.oer.Element;
import org.bouncycastle.oer.OEREncoder;
import org.bouncycastle.oer.OERInputStream;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097DataSigned;
import org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Content;
import org.bouncycastle.oer.its.ieee1609dot2.Opaque;
import org.bouncycastle.oer.its.ieee1609dot2.SignedData;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Signature;
import org.bouncycastle.oer.its.template.etsi103097.EtsiTs103097Module;
import org.bouncycastle.oer.its.template.ieee1609dot2.IEEE1609dot2;
import org.bouncycastle.operator.ContentVerifier;

public class ETSISignedData {
    private final SignedData signedData;
    private static final Element oerDef = EtsiTs103097Module.EtsiTs103097Data_Signed.build();

    public ETSISignedData(Opaque opaque) throws IOException {
        this(opaque.getInputStream());
    }

    public ETSISignedData(byte[] oerEncoded) throws IOException {
        this(new ByteArrayInputStream(oerEncoded));
    }

    public ETSISignedData(InputStream str) throws IOException {
        OERInputStream oerIn = str instanceof OERInputStream ? (OERInputStream)str : new OERInputStream(str);
        ASN1Object asn1 = oerIn.parse(oerDef);
        Ieee1609Dot2Content content = EtsiTs103097DataSigned.getInstance((Object)asn1).getContent();
        if (content.getChoice() != 1) {
            throw new IllegalStateException("EtsiTs103097Data-Signed did not have signed data content");
        }
        this.signedData = SignedData.getInstance((Object)content.getIeee1609Dot2Content());
    }

    public ETSISignedData(EtsiTs103097DataSigned etsiTs103097Data_signed) {
        Ieee1609Dot2Content content = etsiTs103097Data_signed.getContent();
        if (content.getChoice() != 1) {
            throw new IllegalStateException("EtsiTs103097Data-Signed did not have signed data content");
        }
        this.signedData = SignedData.getInstance((Object)etsiTs103097Data_signed.getContent());
    }

    public ETSISignedData(SignedData signedData) {
        this.signedData = signedData;
    }

    public boolean signatureValid(ITSContentVerifierProvider verifierProvider) throws Exception {
        Signature sig = this.signedData.getSignature();
        ContentVerifier verifier = verifierProvider.get(sig.getChoice());
        OutputStream os = verifier.getOutputStream();
        os.write(OEREncoder.toByteArray((ASN1Encodable)this.signedData.getTbsData(), (Element)IEEE1609dot2.ToBeSignedData.build()));
        os.close();
        return verifier.verify(ECDSAEncoder.toX962(this.signedData.getSignature()));
    }

    public byte[] getEncoded() {
        return OEREncoder.toByteArray((ASN1Encodable)new EtsiTs103097DataSigned(Ieee1609Dot2Content.signedData((SignedData)this.signedData)), (Element)EtsiTs103097Module.EtsiTs103097Data_Signed.build());
    }

    public SignedData getSignedData() {
        return this.signedData;
    }
}

