/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.cms.ContentInfo
 *  org.bouncycastle.asn1.cms.DigestedData
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.util.Arrays
 *  org.bouncycastle.util.Encodable
 */
package org.bouncycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.DigestedData;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSUtils;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Encodable;

public class CMSDigestedData
implements Encodable {
    private ContentInfo contentInfo;
    private DigestedData digestedData;

    public CMSDigestedData(byte[] compressedData) throws CMSException {
        this(CMSUtils.readContentInfo(compressedData));
    }

    public CMSDigestedData(InputStream compressedData) throws CMSException {
        this(CMSUtils.readContentInfo(compressedData));
    }

    public CMSDigestedData(ContentInfo contentInfo) throws CMSException {
        this.contentInfo = contentInfo;
        try {
            this.digestedData = DigestedData.getInstance((Object)contentInfo.getContent());
        }
        catch (ClassCastException e) {
            throw new CMSException("Malformed content.", e);
        }
        catch (IllegalArgumentException e) {
            throw new CMSException("Malformed content.", e);
        }
    }

    public ASN1ObjectIdentifier getContentType() {
        return this.contentInfo.getContentType();
    }

    public AlgorithmIdentifier getDigestAlgorithm() {
        return this.digestedData.getDigestAlgorithm();
    }

    public CMSProcessable getDigestedContent() throws CMSException {
        ContentInfo content = this.digestedData.getEncapContentInfo();
        try {
            return new CMSProcessableByteArray(content.getContentType(), ((ASN1OctetString)content.getContent()).getOctets());
        }
        catch (Exception e) {
            throw new CMSException("exception reading digested stream.", e);
        }
    }

    public ContentInfo toASN1Structure() {
        return this.contentInfo;
    }

    public byte[] getEncoded() throws IOException {
        return this.contentInfo.getEncoded();
    }

    public boolean verify(DigestCalculatorProvider calculatorProvider) throws CMSException {
        try {
            ContentInfo content = this.digestedData.getEncapContentInfo();
            DigestCalculator calc = calculatorProvider.get(this.digestedData.getDigestAlgorithm());
            OutputStream dOut = calc.getOutputStream();
            dOut.write(((ASN1OctetString)content.getContent()).getOctets());
            return Arrays.areEqual((byte[])this.digestedData.getDigest(), (byte[])calc.getDigest());
        }
        catch (OperatorCreationException e) {
            throw new CMSException("unable to create digest calculator: " + e.getMessage(), e);
        }
        catch (IOException e) {
            throw new CMSException("unable process content: " + e.getMessage(), e);
        }
    }
}

