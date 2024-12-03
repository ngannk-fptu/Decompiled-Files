/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.cms.AttributeTable
 *  org.bouncycastle.asn1.cms.ContentInfo
 *  org.bouncycastle.asn1.cms.Evidence
 *  org.bouncycastle.asn1.cms.TimeStampAndCRL
 *  org.bouncycastle.asn1.cms.TimeStampedData
 *  org.bouncycastle.asn1.cms.TimeStampedDataParser
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.tsp.cms;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.Evidence;
import org.bouncycastle.asn1.cms.TimeStampAndCRL;
import org.bouncycastle.asn1.cms.TimeStampedData;
import org.bouncycastle.asn1.cms.TimeStampedDataParser;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.tsp.TimeStampTokenInfo;
import org.bouncycastle.tsp.cms.ImprintDigestInvalidException;
import org.bouncycastle.tsp.cms.MetaDataUtil;
import org.bouncycastle.util.Arrays;

class TimeStampDataUtil {
    private final TimeStampAndCRL[] timeStamps;
    private final MetaDataUtil metaDataUtil;

    TimeStampDataUtil(TimeStampedData timeStampedData) {
        this.metaDataUtil = new MetaDataUtil(timeStampedData.getMetaData());
        Evidence evidence = timeStampedData.getTemporalEvidence();
        this.timeStamps = evidence.getTstEvidence().toTimeStampAndCRLArray();
    }

    TimeStampDataUtil(TimeStampedDataParser timeStampedData) throws IOException {
        this.metaDataUtil = new MetaDataUtil(timeStampedData.getMetaData());
        Evidence evidence = timeStampedData.getTemporalEvidence();
        this.timeStamps = evidence.getTstEvidence().toTimeStampAndCRLArray();
    }

    TimeStampToken getTimeStampToken(TimeStampAndCRL timeStampAndCRL) throws CMSException {
        ContentInfo timeStampToken = timeStampAndCRL.getTimeStampToken();
        try {
            TimeStampToken token = new TimeStampToken(timeStampToken);
            return token;
        }
        catch (IOException e) {
            throw new CMSException("unable to parse token data: " + e.getMessage(), e);
        }
        catch (TSPException e) {
            if (e.getCause() instanceof CMSException) {
                throw (CMSException)e.getCause();
            }
            throw new CMSException("token data invalid: " + e.getMessage(), e);
        }
        catch (IllegalArgumentException e) {
            throw new CMSException("token data invalid: " + e.getMessage(), e);
        }
    }

    void initialiseMessageImprintDigestCalculator(DigestCalculator calculator) throws CMSException {
        this.metaDataUtil.initialiseMessageImprintDigestCalculator(calculator);
    }

    DigestCalculator getMessageImprintDigestCalculator(DigestCalculatorProvider calculatorProvider) throws OperatorCreationException {
        try {
            TimeStampToken token = this.getTimeStampToken(this.timeStamps[0]);
            TimeStampTokenInfo info = token.getTimeStampInfo();
            ASN1ObjectIdentifier algOID = info.getMessageImprintAlgOID();
            DigestCalculator calc = calculatorProvider.get(new AlgorithmIdentifier(algOID));
            this.initialiseMessageImprintDigestCalculator(calc);
            return calc;
        }
        catch (CMSException e) {
            throw new OperatorCreationException("unable to extract algorithm ID: " + e.getMessage(), e);
        }
    }

    TimeStampToken[] getTimeStampTokens() throws CMSException {
        TimeStampToken[] tokens = new TimeStampToken[this.timeStamps.length];
        for (int i = 0; i < this.timeStamps.length; ++i) {
            tokens[i] = this.getTimeStampToken(this.timeStamps[i]);
        }
        return tokens;
    }

    TimeStampAndCRL[] getTimeStamps() {
        return this.timeStamps;
    }

    byte[] calculateNextHash(DigestCalculator calculator) throws CMSException {
        TimeStampAndCRL tspToken = this.timeStamps[this.timeStamps.length - 1];
        OutputStream out = calculator.getOutputStream();
        try {
            out.write(tspToken.getEncoded("DER"));
            out.close();
            return calculator.getDigest();
        }
        catch (IOException e) {
            throw new CMSException("exception calculating hash: " + e.getMessage(), e);
        }
    }

    void validate(DigestCalculatorProvider calculatorProvider, byte[] dataDigest) throws ImprintDigestInvalidException, CMSException {
        byte[] currentDigest = dataDigest;
        for (int i = 0; i < this.timeStamps.length; ++i) {
            try {
                TimeStampToken token = this.getTimeStampToken(this.timeStamps[i]);
                if (i > 0) {
                    TimeStampTokenInfo info = token.getTimeStampInfo();
                    DigestCalculator calculator = calculatorProvider.get(info.getHashAlgorithm());
                    calculator.getOutputStream().write(this.timeStamps[i - 1].getEncoded("DER"));
                    currentDigest = calculator.getDigest();
                }
                this.compareDigest(token, currentDigest);
                continue;
            }
            catch (IOException e) {
                throw new CMSException("exception calculating hash: " + e.getMessage(), e);
            }
            catch (OperatorCreationException e) {
                throw new CMSException("cannot create digest: " + e.getMessage(), e);
            }
        }
    }

    void validate(DigestCalculatorProvider calculatorProvider, byte[] dataDigest, TimeStampToken timeStampToken) throws ImprintDigestInvalidException, CMSException {
        byte[] encToken;
        byte[] currentDigest = dataDigest;
        try {
            encToken = timeStampToken.getEncoded();
        }
        catch (IOException e) {
            throw new CMSException("exception encoding timeStampToken: " + e.getMessage(), e);
        }
        for (int i = 0; i < this.timeStamps.length; ++i) {
            try {
                TimeStampToken token = this.getTimeStampToken(this.timeStamps[i]);
                if (i > 0) {
                    TimeStampTokenInfo info = token.getTimeStampInfo();
                    DigestCalculator calculator = calculatorProvider.get(info.getHashAlgorithm());
                    calculator.getOutputStream().write(this.timeStamps[i - 1].getEncoded("DER"));
                    currentDigest = calculator.getDigest();
                }
                this.compareDigest(token, currentDigest);
                if (!Arrays.areEqual((byte[])token.getEncoded(), (byte[])encToken)) continue;
                return;
            }
            catch (IOException e) {
                throw new CMSException("exception calculating hash: " + e.getMessage(), e);
            }
            catch (OperatorCreationException e) {
                throw new CMSException("cannot create digest: " + e.getMessage(), e);
            }
        }
        throw new ImprintDigestInvalidException("passed in token not associated with timestamps present", timeStampToken);
    }

    private void compareDigest(TimeStampToken timeStampToken, byte[] digest) throws ImprintDigestInvalidException {
        TimeStampTokenInfo info = timeStampToken.getTimeStampInfo();
        byte[] tsrMessageDigest = info.getMessageImprintDigest();
        if (!Arrays.areEqual((byte[])digest, (byte[])tsrMessageDigest)) {
            throw new ImprintDigestInvalidException("hash calculated is different from MessageImprintDigest found in TimeStampToken", timeStampToken);
        }
    }

    String getFileName() {
        return this.metaDataUtil.getFileName();
    }

    String getMediaType() {
        return this.metaDataUtil.getMediaType();
    }

    AttributeTable getOtherMetaData() {
        return new AttributeTable(this.metaDataUtil.getOtherMetaData());
    }
}

