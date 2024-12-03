/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.cms.CMSObjectIdentifiers
 *  org.bouncycastle.asn1.cms.ContentInfo
 *  org.bouncycastle.asn1.cms.SignedData
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.tsp.ArchiveTimeStamp
 *  org.bouncycastle.asn1.tsp.ArchiveTimeStampChain
 *  org.bouncycastle.asn1.tsp.ArchiveTimeStampSequence
 *  org.bouncycastle.asn1.tsp.EvidenceRecord
 *  org.bouncycastle.asn1.tsp.TSTInfo
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.util.io.Streams
 */
package org.bouncycastle.tsp.ers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.SignedData;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.tsp.ArchiveTimeStamp;
import org.bouncycastle.asn1.tsp.ArchiveTimeStampChain;
import org.bouncycastle.asn1.tsp.ArchiveTimeStampSequence;
import org.bouncycastle.asn1.tsp.EvidenceRecord;
import org.bouncycastle.asn1.tsp.TSTInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampRequest;
import org.bouncycastle.tsp.TimeStampRequestGenerator;
import org.bouncycastle.tsp.TimeStampResponse;
import org.bouncycastle.tsp.ers.ERSArchiveTimeStamp;
import org.bouncycastle.tsp.ers.ERSArchiveTimeStampGenerator;
import org.bouncycastle.tsp.ers.ERSByteData;
import org.bouncycastle.tsp.ers.ERSData;
import org.bouncycastle.tsp.ers.ERSDataGroup;
import org.bouncycastle.tsp.ers.ERSException;
import org.bouncycastle.util.io.Streams;

public class ERSEvidenceRecord {
    private final EvidenceRecord evidenceRecord;
    private final DigestCalculatorProvider digestCalculatorProvider;
    private final ERSArchiveTimeStamp firstArchiveTimeStamp;
    private final ERSArchiveTimeStamp lastArchiveTimeStamp;
    private final byte[] previousChainsDigest;
    private final DigestCalculator digCalc;
    private final ArchiveTimeStamp primaryArchiveTimeStamp;

    public ERSEvidenceRecord(InputStream ersIn, DigestCalculatorProvider digestCalculatorProvider) throws TSPException, ERSException, IOException {
        this(EvidenceRecord.getInstance((Object)Streams.readAll((InputStream)ersIn)), digestCalculatorProvider);
    }

    public ERSEvidenceRecord(byte[] evidenceRecord, DigestCalculatorProvider digestCalculatorProvider) throws TSPException, ERSException {
        this(EvidenceRecord.getInstance((Object)evidenceRecord), digestCalculatorProvider);
    }

    public ERSEvidenceRecord(EvidenceRecord evidenceRecord, DigestCalculatorProvider digestCalculatorProvider) throws TSPException, ERSException {
        this.evidenceRecord = evidenceRecord;
        this.digestCalculatorProvider = digestCalculatorProvider;
        ArchiveTimeStampSequence sequence = evidenceRecord.getArchiveTimeStampSequence();
        ArchiveTimeStampChain[] chains = sequence.getArchiveTimeStampChains();
        this.primaryArchiveTimeStamp = chains[0].getArchiveTimestamps()[0];
        this.validateChains(chains);
        ArchiveTimeStampChain chain = chains[chains.length - 1];
        ArchiveTimeStamp[] archiveTimestamps = chain.getArchiveTimestamps();
        this.lastArchiveTimeStamp = new ERSArchiveTimeStamp(archiveTimestamps[archiveTimestamps.length - 1], digestCalculatorProvider);
        if (chains.length > 1) {
            try {
                ASN1EncodableVector v = new ASN1EncodableVector();
                for (int i = 0; i != chains.length - 1; ++i) {
                    v.add((ASN1Encodable)chains[i]);
                }
                this.digCalc = digestCalculatorProvider.get(this.lastArchiveTimeStamp.getDigestAlgorithmIdentifier());
                OutputStream dOut = this.digCalc.getOutputStream();
                dOut.write(new DERSequence(v).getEncoded("DER"));
                dOut.close();
                this.previousChainsDigest = this.digCalc.getDigest();
            }
            catch (Exception e) {
                throw new ERSException(e.getMessage(), e);
            }
        } else {
            this.digCalc = null;
            this.previousChainsDigest = null;
        }
        this.firstArchiveTimeStamp = new ERSArchiveTimeStamp(this.previousChainsDigest, archiveTimestamps[0], digestCalculatorProvider);
    }

    private void validateChains(ArchiveTimeStampChain[] chains) throws ERSException, TSPException {
        for (int i = 0; i != chains.length; ++i) {
            ArchiveTimeStamp[] archiveTimeStamps = chains[i].getArchiveTimestamps();
            ArchiveTimeStamp prevArchiveTimeStamp = archiveTimeStamps[0];
            AlgorithmIdentifier digAlg = archiveTimeStamps[0].getDigestAlgorithmIdentifier();
            for (int j = 1; j != archiveTimeStamps.length; ++j) {
                ArchiveTimeStamp archiveTimeStamp = archiveTimeStamps[j];
                if (!digAlg.equals((Object)archiveTimeStamp.getDigestAlgorithmIdentifier())) {
                    throw new ERSException("invalid digest algorithm in chain");
                }
                ContentInfo timeStamp = archiveTimeStamp.getTimeStamp();
                if (!timeStamp.getContentType().equals((ASN1Primitive)CMSObjectIdentifiers.signedData)) {
                    throw new TSPException("cannot identify TSTInfo");
                }
                TSTInfo tstData = this.extractTimeStamp(timeStamp);
                try {
                    DigestCalculator digCalc = this.digestCalculatorProvider.get(digAlg);
                    ERSArchiveTimeStamp ersArchiveTimeStamp = new ERSArchiveTimeStamp(archiveTimeStamp, digCalc);
                    ersArchiveTimeStamp.validatePresent(new ERSByteData(prevArchiveTimeStamp.getTimeStamp().getEncoded("DER")), tstData.getGenTime().getDate());
                }
                catch (Exception e) {
                    throw new ERSException("invalid timestamp renewal found: " + e.getMessage(), e);
                }
                prevArchiveTimeStamp = archiveTimeStamp;
            }
        }
    }

    ArchiveTimeStamp[] getArchiveTimeStamps() {
        ArchiveTimeStampSequence sequence = this.evidenceRecord.getArchiveTimeStampSequence();
        ArchiveTimeStampChain[] chains = sequence.getArchiveTimeStampChains();
        ArchiveTimeStampChain chain = chains[chains.length - 1];
        return chain.getArchiveTimestamps();
    }

    public byte[] getPrimaryRootHash() throws TSPException, ERSException {
        ContentInfo timeStamp = this.primaryArchiveTimeStamp.getTimeStamp();
        if (timeStamp.getContentType().equals((ASN1Primitive)CMSObjectIdentifiers.signedData)) {
            TSTInfo tstData = this.extractTimeStamp(timeStamp);
            return tstData.getMessageImprint().getHashedMessage();
        }
        throw new ERSException("cannot identify TSTInfo for digest");
    }

    private TSTInfo extractTimeStamp(ContentInfo timeStamp) throws TSPException {
        SignedData tsData = SignedData.getInstance((Object)timeStamp.getContent());
        if (tsData.getEncapContentInfo().getContentType().equals((ASN1Primitive)PKCSObjectIdentifiers.id_ct_TSTInfo)) {
            TSTInfo tstData = TSTInfo.getInstance((Object)ASN1OctetString.getInstance((Object)tsData.getEncapContentInfo().getContent()).getOctets());
            return tstData;
        }
        throw new TSPException("cannot parse time stamp");
    }

    public boolean isRelatedTo(ERSEvidenceRecord er) {
        return this.primaryArchiveTimeStamp.getTimeStamp().equals((Object)er.primaryArchiveTimeStamp.getTimeStamp());
    }

    public boolean isContaining(ERSData data, Date date) throws ERSException {
        return this.firstArchiveTimeStamp.isContaining(data, date);
    }

    public void validatePresent(ERSData data, Date atDate) throws ERSException {
        this.firstArchiveTimeStamp.validatePresent(data, atDate);
    }

    public void validatePresent(boolean isDataGroup, byte[] hash, Date atDate) throws ERSException {
        this.firstArchiveTimeStamp.validatePresent(isDataGroup, hash, atDate);
    }

    public X509CertificateHolder getSigningCertificate() {
        return this.lastArchiveTimeStamp.getSigningCertificate();
    }

    public void validate(SignerInformationVerifier verifier) throws TSPException {
        if (this.firstArchiveTimeStamp != this.lastArchiveTimeStamp) {
            ArchiveTimeStamp[] archiveTimeStamps = this.getArchiveTimeStamps();
            for (int i = 0; i != archiveTimeStamps.length - 1; ++i) {
                try {
                    this.lastArchiveTimeStamp.validatePresent(new ERSByteData(archiveTimeStamps[i].getTimeStamp().getEncoded("DER")), this.lastArchiveTimeStamp.getGenTime());
                    continue;
                }
                catch (Exception e) {
                    throw new TSPException("unable to process previous ArchiveTimeStamps", e);
                }
            }
        }
        this.lastArchiveTimeStamp.validate(verifier);
    }

    public EvidenceRecord toASN1Structure() {
        return this.evidenceRecord;
    }

    public byte[] getEncoded() throws IOException {
        return this.evidenceRecord.getEncoded();
    }

    public TimeStampRequest generateTimeStampRenewalRequest(TimeStampRequestGenerator tspReqGen) throws TSPException, ERSException {
        return this.generateTimeStampRenewalRequest(tspReqGen, null);
    }

    public TimeStampRequest generateTimeStampRenewalRequest(TimeStampRequestGenerator tspReqGen, BigInteger nonce) throws ERSException, TSPException {
        ERSArchiveTimeStampGenerator atsGen = this.buildTspRenewalGenerator();
        try {
            return atsGen.generateTimeStampRequest(tspReqGen, nonce);
        }
        catch (IOException e) {
            throw new ERSException(e.getMessage(), e);
        }
    }

    public ERSEvidenceRecord renewTimeStamp(TimeStampResponse tspResp) throws ERSException, TSPException {
        ERSArchiveTimeStampGenerator atsGen = this.buildTspRenewalGenerator();
        ArchiveTimeStamp ats = atsGen.generateArchiveTimeStamp(tspResp).toASN1Structure();
        try {
            return new ERSEvidenceRecord(this.evidenceRecord.addArchiveTimeStamp(ats, false), this.digestCalculatorProvider);
        }
        catch (IllegalArgumentException e) {
            throw new ERSException(e.getMessage(), e);
        }
    }

    private ERSArchiveTimeStampGenerator buildTspRenewalGenerator() throws ERSException {
        DigestCalculator digCalc;
        try {
            digCalc = this.digestCalculatorProvider.get(this.lastArchiveTimeStamp.getDigestAlgorithmIdentifier());
        }
        catch (OperatorCreationException e) {
            throw new ERSException(e.getMessage(), e);
        }
        ArchiveTimeStamp[] previous = this.getArchiveTimeStamps();
        if (!digCalc.getAlgorithmIdentifier().equals((Object)previous[0].getDigestAlgorithmIdentifier())) {
            throw new ERSException("digest mismatch for timestamp renewal");
        }
        ERSArchiveTimeStampGenerator atsGen = new ERSArchiveTimeStampGenerator(digCalc);
        ArrayList<ERSData> prevTimes = new ArrayList<ERSData>(previous.length);
        for (int i = 0; i != previous.length; ++i) {
            try {
                prevTimes.add(new ERSByteData(previous[i].getTimeStamp().getEncoded("DER")));
                continue;
            }
            catch (IOException e) {
                throw new ERSException("unable to process previous ArchiveTimeStamps", e);
            }
        }
        ERSDataGroup timestampGroup = new ERSDataGroup(prevTimes);
        atsGen.addData(timestampGroup);
        return atsGen;
    }

    public TimeStampRequest generateHashRenewalRequest(DigestCalculator digCalc, ERSData data, TimeStampRequestGenerator tspReqGen) throws ERSException, TSPException, IOException {
        return this.generateHashRenewalRequest(digCalc, data, tspReqGen, null);
    }

    public TimeStampRequest generateHashRenewalRequest(DigestCalculator digCalc, ERSData data, TimeStampRequestGenerator tspReqGen, BigInteger nonce) throws ERSException, TSPException, IOException {
        try {
            this.firstArchiveTimeStamp.validatePresent(data, new Date());
        }
        catch (Exception e) {
            throw new ERSException("attempt to hash renew on invalid data");
        }
        ERSArchiveTimeStampGenerator atsGen = new ERSArchiveTimeStampGenerator(digCalc);
        atsGen.addData(data);
        atsGen.addPreviousChains(this.evidenceRecord.getArchiveTimeStampSequence());
        return atsGen.generateTimeStampRequest(tspReqGen, nonce);
    }

    public ERSEvidenceRecord renewHash(DigestCalculator digCalc, ERSData data, TimeStampResponse tspResp) throws ERSException, TSPException {
        try {
            this.firstArchiveTimeStamp.validatePresent(data, new Date());
        }
        catch (Exception e) {
            throw new ERSException("attempt to hash renew on invalid data");
        }
        try {
            ERSArchiveTimeStampGenerator atsGen = new ERSArchiveTimeStampGenerator(digCalc);
            atsGen.addData(data);
            atsGen.addPreviousChains(this.evidenceRecord.getArchiveTimeStampSequence());
            ArchiveTimeStamp ats = atsGen.generateArchiveTimeStamp(tspResp).toASN1Structure();
            return new ERSEvidenceRecord(this.evidenceRecord.addArchiveTimeStamp(ats, true), this.digestCalculatorProvider);
        }
        catch (IOException e) {
            throw new ERSException(e.getMessage(), e);
        }
        catch (IllegalArgumentException e) {
            throw new ERSException(e.getMessage(), e);
        }
    }

    DigestCalculatorProvider getDigestAlgorithmProvider() {
        return this.digestCalculatorProvider;
    }
}

