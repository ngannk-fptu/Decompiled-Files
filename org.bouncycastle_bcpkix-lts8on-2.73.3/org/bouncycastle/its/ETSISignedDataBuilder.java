/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.oer.Element
 *  org.bouncycastle.oer.OEREncoder
 *  org.bouncycastle.oer.its.ieee1609dot2.Certificate
 *  org.bouncycastle.oer.its.ieee1609dot2.HashedData
 *  org.bouncycastle.oer.its.ieee1609dot2.HeaderInfo
 *  org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Content
 *  org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Data
 *  org.bouncycastle.oer.its.ieee1609dot2.Opaque
 *  org.bouncycastle.oer.its.ieee1609dot2.SequenceOfCertificate
 *  org.bouncycastle.oer.its.ieee1609dot2.SignedData
 *  org.bouncycastle.oer.its.ieee1609dot2.SignedDataPayload
 *  org.bouncycastle.oer.its.ieee1609dot2.SignerIdentifier
 *  org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedData
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.Psid
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.Signature
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.Time64
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8
 *  org.bouncycastle.oer.its.template.ieee1609dot2.IEEE1609dot2
 */
package org.bouncycastle.its;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.its.ETSISignedData;
import org.bouncycastle.its.ITSAlgorithmUtils;
import org.bouncycastle.its.ITSCertificate;
import org.bouncycastle.its.operator.ECDSAEncoder;
import org.bouncycastle.its.operator.ITSContentSigner;
import org.bouncycastle.oer.Element;
import org.bouncycastle.oer.OEREncoder;
import org.bouncycastle.oer.its.ieee1609dot2.Certificate;
import org.bouncycastle.oer.its.ieee1609dot2.HashedData;
import org.bouncycastle.oer.its.ieee1609dot2.HeaderInfo;
import org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Content;
import org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Data;
import org.bouncycastle.oer.its.ieee1609dot2.Opaque;
import org.bouncycastle.oer.its.ieee1609dot2.SequenceOfCertificate;
import org.bouncycastle.oer.its.ieee1609dot2.SignedData;
import org.bouncycastle.oer.its.ieee1609dot2.SignedDataPayload;
import org.bouncycastle.oer.its.ieee1609dot2.SignerIdentifier;
import org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedData;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Psid;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Signature;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Time64;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;
import org.bouncycastle.oer.its.template.ieee1609dot2.IEEE1609dot2;

public class ETSISignedDataBuilder {
    private static final Element def = IEEE1609dot2.ToBeSignedData.build();
    private final HeaderInfo headerInfo;
    private Ieee1609Dot2Data data;
    private HashedData extDataHash;

    private ETSISignedDataBuilder(Psid psid) {
        this(HeaderInfo.builder().setPsid(psid).setGenerationTime(Time64.now()).createHeaderInfo());
    }

    private ETSISignedDataBuilder(HeaderInfo headerInfo) {
        this.headerInfo = headerInfo;
    }

    public static ETSISignedDataBuilder builder(Psid psid) {
        return new ETSISignedDataBuilder(psid);
    }

    public static ETSISignedDataBuilder builder(HeaderInfo headerInfo) {
        return new ETSISignedDataBuilder(headerInfo);
    }

    public ETSISignedDataBuilder setData(Ieee1609Dot2Content data) {
        this.data = Ieee1609Dot2Data.builder().setProtocolVersion(new UINT8(3)).setContent(data).createIeee1609Dot2Data();
        return this;
    }

    public ETSISignedDataBuilder setUnsecuredData(byte[] data) {
        this.data = Ieee1609Dot2Data.builder().setProtocolVersion(new UINT8(3)).setContent(Ieee1609Dot2Content.unsecuredData((Opaque)new Opaque(data))).createEtsiTs103097Data();
        return this;
    }

    public ETSISignedDataBuilder setExtDataHash(HashedData extDataHash) {
        this.extDataHash = extDataHash;
        return this;
    }

    private ToBeSignedData getToBeSignedData() {
        SignedDataPayload signedDataPayload = new SignedDataPayload(this.data, this.extDataHash);
        return ToBeSignedData.builder().setPayload(signedDataPayload).setHeaderInfo(this.headerInfo).createToBeSignedData();
    }

    public ETSISignedData build(ITSContentSigner signer) {
        ToBeSignedData toBeSignedData = this.getToBeSignedData();
        ETSISignedDataBuilder.write(signer.getOutputStream(), OEREncoder.toByteArray((ASN1Encodable)toBeSignedData, (Element)def));
        Signature signature = ECDSAEncoder.toITS(signer.getCurveID(), signer.getSignature());
        return new ETSISignedData(SignedData.builder().setHashId(ITSAlgorithmUtils.getHashAlgorithm(signer.getDigestAlgorithm().getAlgorithm())).setTbsData(toBeSignedData).setSigner(SignerIdentifier.self()).setSignature(signature).createSignedData());
    }

    public ETSISignedData build(ITSContentSigner signer, List<ITSCertificate> certificateList) {
        ToBeSignedData toBeSignedData = this.getToBeSignedData();
        ETSISignedDataBuilder.write(signer.getOutputStream(), OEREncoder.toByteArray((ASN1Encodable)toBeSignedData, (Element)def));
        ArrayList<Certificate> certificates = new ArrayList<Certificate>();
        for (ITSCertificate certificate : certificateList) {
            certificates.add(Certificate.getInstance((Object)certificate.toASN1Structure()));
        }
        Signature signature = ECDSAEncoder.toITS(signer.getCurveID(), signer.getSignature());
        return new ETSISignedData(SignedData.builder().setHashId(ITSAlgorithmUtils.getHashAlgorithm(signer.getDigestAlgorithm().getAlgorithm())).setTbsData(toBeSignedData).setSigner(SignerIdentifier.certificate((SequenceOfCertificate)new SequenceOfCertificate(certificates))).setSignature(signature).createSignedData());
    }

    public ETSISignedData build(ITSContentSigner signer, HashedId8 identifier) {
        ToBeSignedData toBeSignedData = this.getToBeSignedData();
        ETSISignedDataBuilder.write(signer.getOutputStream(), OEREncoder.toByteArray((ASN1Encodable)toBeSignedData, (Element)def));
        Signature signature = ECDSAEncoder.toITS(signer.getCurveID(), signer.getSignature());
        return new ETSISignedData(SignedData.builder().setHashId(ITSAlgorithmUtils.getHashAlgorithm(signer.getDigestAlgorithm().getAlgorithm())).setTbsData(toBeSignedData).setSigner(SignerIdentifier.digest((HashedId8)identifier)).setSignature(signature).createSignedData());
    }

    private static void write(OutputStream os, byte[] data) {
        try {
            os.write(data);
            os.flush();
            os.close();
        }
        catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
}

