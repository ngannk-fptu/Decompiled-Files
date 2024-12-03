/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.BERSequenceGenerator
 *  org.bouncycastle.asn1.BERSet
 *  org.bouncycastle.asn1.DERSet
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.DLSet
 *  org.bouncycastle.asn1.cms.AttributeTable
 *  org.bouncycastle.asn1.cms.CMSObjectIdentifiers
 *  org.bouncycastle.asn1.cms.EnvelopedData
 *  org.bouncycastle.asn1.cms.OriginatorInfo
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.cms;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.BERSequenceGenerator;
import org.bouncycastle.asn1.BERSet;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DLSet;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.EnvelopedData;
import org.bouncycastle.asn1.cms.OriginatorInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSEnvelopedGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSUtils;
import org.bouncycastle.cms.RecipientInfoGenerator;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OutputAEADEncryptor;
import org.bouncycastle.operator.OutputEncryptor;

public class CMSEnvelopedDataStreamGenerator
extends CMSEnvelopedGenerator {
    private int _bufferSize;
    private boolean _berEncodeRecipientSet;

    public void setBufferSize(int bufferSize) {
        this._bufferSize = bufferSize;
    }

    public void setBEREncodeRecipients(boolean berEncodeRecipientSet) {
        this._berEncodeRecipientSet = berEncodeRecipientSet;
    }

    private ASN1Integer getVersion(ASN1EncodableVector recipientInfos) {
        if (this.unprotectedAttributeGenerator != null) {
            return new ASN1Integer((long)EnvelopedData.calculateVersion((OriginatorInfo)this.originatorInfo, (ASN1Set)new DLSet(recipientInfos), (ASN1Set)new DLSet()));
        }
        return new ASN1Integer((long)EnvelopedData.calculateVersion((OriginatorInfo)this.originatorInfo, (ASN1Set)new DLSet(recipientInfos), null));
    }

    private OutputStream doOpen(ASN1ObjectIdentifier dataType, OutputStream out, OutputEncryptor encryptor) throws IOException, CMSException {
        ASN1EncodableVector recipientInfos = new ASN1EncodableVector();
        GenericKey encKey = encryptor.getKey();
        for (RecipientInfoGenerator recipient : this.recipientInfoGenerators) {
            recipientInfos.add((ASN1Encodable)recipient.generate(encKey));
        }
        return this.open(dataType, out, recipientInfos, encryptor);
    }

    protected OutputStream open(ASN1ObjectIdentifier dataType, OutputStream out, ASN1EncodableVector recipientInfos, OutputEncryptor encryptor) throws IOException {
        BERSequenceGenerator cGen = new BERSequenceGenerator(out);
        cGen.addObject((ASN1Primitive)CMSObjectIdentifiers.envelopedData);
        BERSequenceGenerator envGen = new BERSequenceGenerator(cGen.getRawOutputStream(), 0, true);
        envGen.addObject((ASN1Primitive)this.getVersion(recipientInfos));
        if (this.originatorInfo != null) {
            envGen.addObject((ASN1Primitive)new DERTaggedObject(false, 0, (ASN1Encodable)this.originatorInfo));
        }
        if (this._berEncodeRecipientSet) {
            envGen.getRawOutputStream().write(new BERSet(recipientInfos).getEncoded());
        } else {
            envGen.getRawOutputStream().write(new DERSet(recipientInfos).getEncoded());
        }
        BERSequenceGenerator eiGen = new BERSequenceGenerator(envGen.getRawOutputStream());
        eiGen.addObject((ASN1Primitive)dataType);
        AlgorithmIdentifier encAlgId = encryptor.getAlgorithmIdentifier();
        eiGen.getRawOutputStream().write(encAlgId.getEncoded());
        OutputStream octetStream = CMSUtils.createBEROctetOutputStream(eiGen.getRawOutputStream(), 0, false, this._bufferSize);
        return new CmsEnvelopedDataOutputStream(encryptor, octetStream, cGen, envGen, eiGen);
    }

    protected OutputStream open(OutputStream out, ASN1EncodableVector recipientInfos, OutputEncryptor encryptor) throws CMSException {
        try {
            BERSequenceGenerator cGen = new BERSequenceGenerator(out);
            cGen.addObject((ASN1Primitive)CMSObjectIdentifiers.envelopedData);
            BERSequenceGenerator envGen = new BERSequenceGenerator(cGen.getRawOutputStream(), 0, true);
            Object recipients = this._berEncodeRecipientSet ? new BERSet(recipientInfos) : new DERSet(recipientInfos);
            envGen.addObject((ASN1Primitive)this.getVersion(recipientInfos));
            if (this.originatorInfo != null) {
                envGen.addObject((ASN1Primitive)new DERTaggedObject(false, 0, (ASN1Encodable)this.originatorInfo));
            }
            envGen.getRawOutputStream().write(recipients.getEncoded());
            BERSequenceGenerator eiGen = new BERSequenceGenerator(envGen.getRawOutputStream());
            eiGen.addObject((ASN1Primitive)CMSObjectIdentifiers.data);
            AlgorithmIdentifier encAlgId = encryptor.getAlgorithmIdentifier();
            eiGen.getRawOutputStream().write(encAlgId.getEncoded());
            OutputStream octetStream = CMSUtils.createBEROctetOutputStream(eiGen.getRawOutputStream(), 0, false, this._bufferSize);
            return new CmsEnvelopedDataOutputStream(encryptor, octetStream, cGen, envGen, eiGen);
        }
        catch (IOException e) {
            throw new CMSException("exception decoding algorithm parameters.", e);
        }
    }

    public OutputStream open(OutputStream out, OutputEncryptor encryptor) throws CMSException, IOException {
        return this.doOpen(new ASN1ObjectIdentifier(CMSObjectIdentifiers.data.getId()), out, encryptor);
    }

    public OutputStream open(ASN1ObjectIdentifier dataType, OutputStream out, OutputEncryptor encryptor) throws CMSException, IOException {
        return this.doOpen(dataType, out, encryptor);
    }

    private class CmsEnvelopedDataOutputStream
    extends OutputStream {
        private final OutputEncryptor _encryptor;
        private final OutputStream _cOut;
        private OutputStream _octetStream;
        private BERSequenceGenerator _cGen;
        private BERSequenceGenerator _envGen;
        private BERSequenceGenerator _eiGen;

        public CmsEnvelopedDataOutputStream(OutputEncryptor encryptor, OutputStream octetStream, BERSequenceGenerator cGen, BERSequenceGenerator envGen, BERSequenceGenerator eiGen) {
            this._encryptor = encryptor;
            this._octetStream = octetStream;
            this._cOut = encryptor.getOutputStream(octetStream);
            this._cGen = cGen;
            this._envGen = envGen;
            this._eiGen = eiGen;
        }

        @Override
        public void write(int b) throws IOException {
            this._cOut.write(b);
        }

        @Override
        public void write(byte[] bytes, int off, int len) throws IOException {
            this._cOut.write(bytes, off, len);
        }

        @Override
        public void write(byte[] bytes) throws IOException {
            this._cOut.write(bytes);
        }

        @Override
        public void close() throws IOException {
            this._cOut.close();
            if (this._encryptor instanceof OutputAEADEncryptor) {
                this._octetStream.write(((OutputAEADEncryptor)this._encryptor).getMAC());
                this._octetStream.close();
            }
            this._eiGen.close();
            if (CMSEnvelopedDataStreamGenerator.this.unprotectedAttributeGenerator != null) {
                AttributeTable attrTable = CMSEnvelopedDataStreamGenerator.this.unprotectedAttributeGenerator.getAttributes(Collections.EMPTY_MAP);
                BERSet unprotectedAttrs = new BERSet(attrTable.toASN1EncodableVector());
                this._envGen.addObject((ASN1Primitive)new DERTaggedObject(false, 1, (ASN1Encodable)unprotectedAttrs));
            }
            this._envGen.close();
            this._cGen.close();
        }
    }
}

