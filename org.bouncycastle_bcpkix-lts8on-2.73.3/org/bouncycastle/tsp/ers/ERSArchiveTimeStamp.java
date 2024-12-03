/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.tsp.ArchiveTimeStamp
 *  org.bouncycastle.asn1.tsp.PartialHashtree
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.util.Arrays
 *  org.bouncycastle.util.Selector
 *  org.bouncycastle.util.Store
 */
package org.bouncycastle.tsp.ers;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import org.bouncycastle.asn1.tsp.ArchiveTimeStamp;
import org.bouncycastle.asn1.tsp.PartialHashtree;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.tsp.ers.ArchiveTimeStampValidationException;
import org.bouncycastle.tsp.ers.BinaryTreeRootCalculator;
import org.bouncycastle.tsp.ers.ERSData;
import org.bouncycastle.tsp.ers.ERSDataGroup;
import org.bouncycastle.tsp.ers.ERSException;
import org.bouncycastle.tsp.ers.ERSRootNodeCalculator;
import org.bouncycastle.tsp.ers.ERSUtil;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;

public class ERSArchiveTimeStamp {
    private final ArchiveTimeStamp archiveTimeStamp;
    private final DigestCalculator digCalc;
    private final TimeStampToken timeStampToken;
    private final byte[] previousChainsDigest;
    private ERSRootNodeCalculator rootNodeCalculator = new BinaryTreeRootCalculator();

    public ERSArchiveTimeStamp(byte[] archiveTimeStamp, DigestCalculatorProvider digCalcProv) throws TSPException, ERSException {
        this(ArchiveTimeStamp.getInstance((Object)archiveTimeStamp), digCalcProv);
    }

    public ERSArchiveTimeStamp(ArchiveTimeStamp archiveTimeStamp, DigestCalculatorProvider digCalcProv) throws TSPException, ERSException {
        this.previousChainsDigest = null;
        try {
            this.archiveTimeStamp = archiveTimeStamp;
            this.timeStampToken = new TimeStampToken(archiveTimeStamp.getTimeStamp());
            this.digCalc = digCalcProv.get(archiveTimeStamp.getDigestAlgorithmIdentifier());
        }
        catch (IOException e) {
            throw new ERSException(e.getMessage(), e);
        }
        catch (OperatorCreationException e) {
            throw new ERSException(e.getMessage(), e);
        }
    }

    ERSArchiveTimeStamp(ArchiveTimeStamp archiveTimeStamp, DigestCalculator digCalc) throws TSPException, ERSException {
        this.previousChainsDigest = null;
        try {
            this.archiveTimeStamp = archiveTimeStamp;
            this.timeStampToken = new TimeStampToken(archiveTimeStamp.getTimeStamp());
            this.digCalc = digCalc;
        }
        catch (IOException e) {
            throw new ERSException(e.getMessage(), e);
        }
    }

    ERSArchiveTimeStamp(byte[] previousChainsDigest, ArchiveTimeStamp archiveTimeStamp, DigestCalculatorProvider digCalcProv) throws TSPException, ERSException {
        this.previousChainsDigest = previousChainsDigest;
        try {
            this.archiveTimeStamp = archiveTimeStamp;
            this.timeStampToken = new TimeStampToken(archiveTimeStamp.getTimeStamp());
            this.digCalc = digCalcProv.get(archiveTimeStamp.getDigestAlgorithmIdentifier());
        }
        catch (IOException e) {
            throw new ERSException(e.getMessage(), e);
        }
        catch (OperatorCreationException e) {
            throw new ERSException(e.getMessage(), e);
        }
    }

    public AlgorithmIdentifier getDigestAlgorithmIdentifier() {
        return this.archiveTimeStamp.getDigestAlgorithmIdentifier();
    }

    public void validatePresent(ERSData data, Date atDate) throws ERSException {
        this.validatePresent(data instanceof ERSDataGroup, data.getHash(this.digCalc, this.previousChainsDigest), atDate);
    }

    public boolean isContaining(ERSData data, Date atDate) throws ERSException {
        if (this.timeStampToken.getTimeStampInfo().getGenTime().after(atDate)) {
            throw new ArchiveTimeStampValidationException("timestamp generation time is in the future");
        }
        try {
            this.validatePresent(data, atDate);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public void validatePresent(boolean isDataGroup, byte[] hash, Date atDate) throws ERSException {
        if (this.timeStampToken.getTimeStampInfo().getGenTime().after(atDate)) {
            throw new ArchiveTimeStampValidationException("timestamp generation time is in the future");
        }
        this.checkContainsHashValue(isDataGroup, hash, this.digCalc);
        PartialHashtree[] partialTree = this.archiveTimeStamp.getReducedHashTree();
        byte[] rootHash = partialTree != null ? this.rootNodeCalculator.recoverRootHash(this.digCalc, this.archiveTimeStamp.getReducedHashTree()) : hash;
        this.checkTimeStampValid(this.timeStampToken, rootHash);
    }

    public TimeStampToken getTimeStampToken() {
        return this.timeStampToken;
    }

    public X509CertificateHolder getSigningCertificate() {
        Collection certs;
        Store<X509CertificateHolder> certificateStore = this.timeStampToken.getCertificates();
        if (certificateStore != null && !(certs = certificateStore.getMatches((Selector)this.timeStampToken.getSID())).isEmpty()) {
            return (X509CertificateHolder)certs.iterator().next();
        }
        return null;
    }

    public void validate(SignerInformationVerifier verifier) throws TSPException {
        this.timeStampToken.validate(verifier);
    }

    void checkContainsHashValue(boolean isGroup, byte[] hash, DigestCalculator digCalc) throws ArchiveTimeStampValidationException {
        PartialHashtree[] reducedHashTree = this.archiveTimeStamp.getReducedHashTree();
        if (reducedHashTree != null) {
            PartialHashtree current = reducedHashTree[0];
            if (!isGroup && current.containsHash(hash)) {
                return;
            }
            if (current.getValueCount() > 1 && Arrays.areEqual((byte[])hash, (byte[])ERSUtil.calculateBranchHash(digCalc, current.getValues()))) {
                return;
            }
            throw new ArchiveTimeStampValidationException("object hash not found");
        }
        if (!Arrays.areEqual((byte[])hash, (byte[])this.timeStampToken.getTimeStampInfo().getMessageImprintDigest())) {
            throw new ArchiveTimeStampValidationException("object hash not found in wrapped timestamp");
        }
    }

    void checkTimeStampValid(TimeStampToken timeStampToken, byte[] hash) throws ArchiveTimeStampValidationException {
        if (hash != null && !Arrays.areEqual((byte[])hash, (byte[])timeStampToken.getTimeStampInfo().getMessageImprintDigest())) {
            throw new ArchiveTimeStampValidationException("timestamp hash does not match root");
        }
    }

    public Date getGenTime() {
        return this.timeStampToken.getTimeStampInfo().getGenTime();
    }

    public Date getExpiryTime() {
        X509CertificateHolder crtHolder = this.getSigningCertificate();
        if (crtHolder != null) {
            return crtHolder.getNotAfter();
        }
        return null;
    }

    public ArchiveTimeStamp toASN1Structure() {
        return this.archiveTimeStamp;
    }

    public byte[] getEncoded() throws IOException {
        return this.archiveTimeStamp.getEncoded();
    }

    public static ERSArchiveTimeStamp fromTimeStampToken(TimeStampToken tspToken, DigestCalculatorProvider digCalcProv) throws TSPException, ERSException {
        return new ERSArchiveTimeStamp(new ArchiveTimeStamp(tspToken.toCMSSignedData().toASN1Structure()), digCalcProv);
    }
}

