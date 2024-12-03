/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.BEROctetString
 *  org.bouncycastle.asn1.BERSet
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERSet
 *  org.bouncycastle.asn1.cms.AuthenticatedData
 *  org.bouncycastle.asn1.cms.CMSObjectIdentifiers
 *  org.bouncycastle.asn1.cms.ContentInfo
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.util.io.TeeOutputStream
 */
package org.bouncycastle.cms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.BERSet;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.AuthenticatedData;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSAuthenticatedData;
import org.bouncycastle.cms.CMSAuthenticatedGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.DefaultAuthenticatedAttributeTableGenerator;
import org.bouncycastle.cms.RecipientInfoGenerator;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.io.TeeOutputStream;

public class CMSAuthenticatedDataGenerator
extends CMSAuthenticatedGenerator {
    public CMSAuthenticatedData generate(CMSTypedData typedData, MacCalculator macCalculator) throws CMSException {
        return this.generate(typedData, macCalculator, null);
    }

    public CMSAuthenticatedData generate(CMSTypedData typedData, MacCalculator macCalculator, final DigestCalculator digestCalculator) throws CMSException {
        AuthenticatedData authData;
        ByteArrayOutputStream bOut;
        ASN1EncodableVector recipientInfos = new ASN1EncodableVector();
        for (RecipientInfoGenerator recipient : this.recipientInfoGenerators) {
            recipientInfos.add((ASN1Encodable)recipient.generate(macCalculator.getKey()));
        }
        if (digestCalculator != null) {
            DEROctetString macResult;
            BEROctetString encContent;
            try {
                bOut = new ByteArrayOutputStream();
                TeeOutputStream out = new TeeOutputStream(digestCalculator.getOutputStream(), (OutputStream)bOut);
                typedData.write((OutputStream)out);
                out.close();
                encContent = new BEROctetString(bOut.toByteArray());
            }
            catch (IOException e) {
                throw new CMSException("unable to perform digest calculation: " + e.getMessage(), e);
            }
            Map parameters = Collections.unmodifiableMap(this.getBaseParameters(typedData.getContentType(), digestCalculator.getAlgorithmIdentifier(), macCalculator.getAlgorithmIdentifier(), digestCalculator.getDigest()));
            if (this.authGen == null) {
                this.authGen = new DefaultAuthenticatedAttributeTableGenerator();
            }
            DERSet authed = new DERSet(this.authGen.getAttributes(parameters).toASN1EncodableVector());
            try {
                OutputStream mOut = macCalculator.getOutputStream();
                mOut.write(authed.getEncoded("DER"));
                mOut.close();
                macResult = new DEROctetString(macCalculator.getMac());
            }
            catch (IOException e) {
                throw new CMSException("unable to perform MAC calculation: " + e.getMessage(), e);
            }
            BERSet unauthed = this.unauthGen != null ? new BERSet(this.unauthGen.getAttributes(parameters).toASN1EncodableVector()) : null;
            ContentInfo eci = new ContentInfo(typedData.getContentType(), (ASN1Encodable)encContent);
            authData = new AuthenticatedData(this.originatorInfo, (ASN1Set)new DERSet(recipientInfos), macCalculator.getAlgorithmIdentifier(), digestCalculator.getAlgorithmIdentifier(), eci, (ASN1Set)authed, (ASN1OctetString)macResult, (ASN1Set)unauthed);
        } else {
            DEROctetString macResult;
            BEROctetString encContent;
            try {
                bOut = new ByteArrayOutputStream();
                TeeOutputStream mOut = new TeeOutputStream((OutputStream)bOut, macCalculator.getOutputStream());
                typedData.write((OutputStream)mOut);
                mOut.close();
                encContent = new BEROctetString(bOut.toByteArray());
                macResult = new DEROctetString(macCalculator.getMac());
            }
            catch (IOException e) {
                throw new CMSException("unable to perform MAC calculation: " + e.getMessage(), e);
            }
            BERSet unauthed = this.unauthGen != null ? new BERSet(this.unauthGen.getAttributes(Collections.EMPTY_MAP).toASN1EncodableVector()) : null;
            ContentInfo eci = new ContentInfo(typedData.getContentType(), (ASN1Encodable)encContent);
            authData = new AuthenticatedData(this.originatorInfo, (ASN1Set)new DERSet(recipientInfos), macCalculator.getAlgorithmIdentifier(), null, eci, null, (ASN1OctetString)macResult, (ASN1Set)unauthed);
        }
        ContentInfo contentInfo = new ContentInfo(CMSObjectIdentifiers.authenticatedData, (ASN1Encodable)authData);
        return new CMSAuthenticatedData(contentInfo, new DigestCalculatorProvider(){

            @Override
            public DigestCalculator get(AlgorithmIdentifier digestAlgorithmIdentifier) throws OperatorCreationException {
                return digestCalculator;
            }
        });
    }
}

