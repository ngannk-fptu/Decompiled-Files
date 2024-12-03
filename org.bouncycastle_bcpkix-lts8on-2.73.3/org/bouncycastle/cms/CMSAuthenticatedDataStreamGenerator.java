/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.BERSequenceGenerator
 *  org.bouncycastle.asn1.BERSet
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERSet
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.cms.AuthenticatedData
 *  org.bouncycastle.asn1.cms.CMSObjectIdentifiers
 *  org.bouncycastle.asn1.cms.OriginatorInfo
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.util.io.TeeOutputStream
 */
package org.bouncycastle.cms;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.BERSequenceGenerator;
import org.bouncycastle.asn1.BERSet;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cms.AuthenticatedData;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.OriginatorInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSAuthenticatedGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSUtils;
import org.bouncycastle.cms.DefaultAuthenticatedAttributeTableGenerator;
import org.bouncycastle.cms.RecipientInfoGenerator;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.util.io.TeeOutputStream;

public class CMSAuthenticatedDataStreamGenerator
extends CMSAuthenticatedGenerator {
    private int bufferSize;
    private boolean berEncodeRecipientSet;
    private MacCalculator macCalculator;

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public void setBEREncodeRecipients(boolean useBerEncodingForRecipients) {
        this.berEncodeRecipientSet = useBerEncodingForRecipients;
    }

    public OutputStream open(OutputStream out, MacCalculator macCalculator) throws CMSException {
        return this.open(CMSObjectIdentifiers.data, out, macCalculator);
    }

    public OutputStream open(OutputStream out, MacCalculator macCalculator, DigestCalculator digestCalculator) throws CMSException {
        return this.open(CMSObjectIdentifiers.data, out, macCalculator, digestCalculator);
    }

    public OutputStream open(ASN1ObjectIdentifier dataType, OutputStream out, MacCalculator macCalculator) throws CMSException {
        return this.open(dataType, out, macCalculator, null);
    }

    public OutputStream open(ASN1ObjectIdentifier dataType, OutputStream out, MacCalculator macCalculator, DigestCalculator digestCalculator) throws CMSException {
        this.macCalculator = macCalculator;
        try {
            ASN1EncodableVector recipientInfos = new ASN1EncodableVector();
            for (RecipientInfoGenerator recipient : this.recipientInfoGenerators) {
                recipientInfos.add((ASN1Encodable)recipient.generate(macCalculator.getKey()));
            }
            BERSequenceGenerator cGen = new BERSequenceGenerator(out);
            cGen.addObject((ASN1Primitive)CMSObjectIdentifiers.authenticatedData);
            BERSequenceGenerator authGen = new BERSequenceGenerator(cGen.getRawOutputStream(), 0, true);
            authGen.addObject((ASN1Primitive)new ASN1Integer((long)AuthenticatedData.calculateVersion((OriginatorInfo)this.originatorInfo)));
            if (this.originatorInfo != null) {
                authGen.addObject((ASN1Primitive)new DERTaggedObject(false, 0, (ASN1Encodable)this.originatorInfo));
            }
            if (this.berEncodeRecipientSet) {
                authGen.getRawOutputStream().write(new BERSet(recipientInfos).getEncoded());
            } else {
                authGen.getRawOutputStream().write(new DERSet(recipientInfos).getEncoded());
            }
            AlgorithmIdentifier macAlgId = macCalculator.getAlgorithmIdentifier();
            authGen.getRawOutputStream().write(macAlgId.getEncoded());
            if (digestCalculator != null) {
                authGen.addObject((ASN1Primitive)new DERTaggedObject(false, 1, (ASN1Encodable)digestCalculator.getAlgorithmIdentifier()));
            }
            BERSequenceGenerator eiGen = new BERSequenceGenerator(authGen.getRawOutputStream());
            eiGen.addObject((ASN1Primitive)dataType);
            OutputStream octetStream = CMSUtils.createBEROctetOutputStream(eiGen.getRawOutputStream(), 0, true, this.bufferSize);
            TeeOutputStream mOut = digestCalculator != null ? new TeeOutputStream(octetStream, digestCalculator.getOutputStream()) : new TeeOutputStream(octetStream, macCalculator.getOutputStream());
            return new CmsAuthenticatedDataOutputStream(macCalculator, digestCalculator, dataType, (OutputStream)mOut, cGen, authGen, eiGen);
        }
        catch (IOException e) {
            throw new CMSException("exception decoding algorithm parameters.", e);
        }
    }

    private class CmsAuthenticatedDataOutputStream
    extends OutputStream {
        private OutputStream dataStream;
        private BERSequenceGenerator cGen;
        private BERSequenceGenerator envGen;
        private BERSequenceGenerator eiGen;
        private MacCalculator macCalculator;
        private DigestCalculator digestCalculator;
        private ASN1ObjectIdentifier contentType;

        public CmsAuthenticatedDataOutputStream(MacCalculator macCalculator, DigestCalculator digestCalculator, ASN1ObjectIdentifier contentType, OutputStream dataStream, BERSequenceGenerator cGen, BERSequenceGenerator envGen, BERSequenceGenerator eiGen) {
            this.macCalculator = macCalculator;
            this.digestCalculator = digestCalculator;
            this.contentType = contentType;
            this.dataStream = dataStream;
            this.cGen = cGen;
            this.envGen = envGen;
            this.eiGen = eiGen;
        }

        @Override
        public void write(int b) throws IOException {
            this.dataStream.write(b);
        }

        @Override
        public void write(byte[] bytes, int off, int len) throws IOException {
            this.dataStream.write(bytes, off, len);
        }

        @Override
        public void write(byte[] bytes) throws IOException {
            this.dataStream.write(bytes);
        }

        @Override
        public void close() throws IOException {
            Map parameters;
            this.dataStream.close();
            this.eiGen.close();
            if (this.digestCalculator != null) {
                parameters = Collections.unmodifiableMap(CMSAuthenticatedDataStreamGenerator.this.getBaseParameters(this.contentType, this.digestCalculator.getAlgorithmIdentifier(), this.macCalculator.getAlgorithmIdentifier(), this.digestCalculator.getDigest()));
                if (CMSAuthenticatedDataStreamGenerator.this.authGen == null) {
                    CMSAuthenticatedDataStreamGenerator.this.authGen = new DefaultAuthenticatedAttributeTableGenerator();
                }
                DERSet authed = new DERSet(CMSAuthenticatedDataStreamGenerator.this.authGen.getAttributes(parameters).toASN1EncodableVector());
                OutputStream mOut = this.macCalculator.getOutputStream();
                mOut.write(authed.getEncoded("DER"));
                mOut.close();
                this.envGen.addObject((ASN1Primitive)new DERTaggedObject(false, 2, (ASN1Encodable)authed));
            } else {
                parameters = Collections.EMPTY_MAP;
            }
            this.envGen.addObject((ASN1Primitive)new DEROctetString(this.macCalculator.getMac()));
            if (CMSAuthenticatedDataStreamGenerator.this.unauthGen != null) {
                this.envGen.addObject((ASN1Primitive)new DERTaggedObject(false, 3, (ASN1Encodable)new BERSet(CMSAuthenticatedDataStreamGenerator.this.unauthGen.getAttributes(parameters).toASN1EncodableVector())));
            }
            this.envGen.close();
            this.cGen.close();
        }
    }
}

