/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.tsp.Accuracy
 *  org.bouncycastle.asn1.tsp.TSTInfo
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.Extensions
 *  org.bouncycastle.asn1.x509.GeneralName
 */
package org.bouncycastle.tsp;

import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.Date;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.tsp.Accuracy;
import org.bouncycastle.asn1.tsp.TSTInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.tsp.GenTimeAccuracy;
import org.bouncycastle.tsp.TSPException;

public class TimeStampTokenInfo {
    TSTInfo tstInfo;
    Date genTime;

    TimeStampTokenInfo(TSTInfo tstInfo) throws TSPException, IOException {
        this.tstInfo = tstInfo;
        try {
            this.genTime = tstInfo.getGenTime().getDate();
        }
        catch (ParseException e) {
            throw new TSPException("unable to parse genTime field");
        }
    }

    public boolean isOrdered() {
        return this.tstInfo.getOrdering().isTrue();
    }

    public Accuracy getAccuracy() {
        return this.tstInfo.getAccuracy();
    }

    public Date getGenTime() {
        return this.genTime;
    }

    public GenTimeAccuracy getGenTimeAccuracy() {
        if (this.getAccuracy() != null) {
            return new GenTimeAccuracy(this.getAccuracy());
        }
        return null;
    }

    public ASN1ObjectIdentifier getPolicy() {
        return this.tstInfo.getPolicy();
    }

    public BigInteger getSerialNumber() {
        return this.tstInfo.getSerialNumber().getValue();
    }

    public GeneralName getTsa() {
        return this.tstInfo.getTsa();
    }

    public Extensions getExtensions() {
        return this.tstInfo.getExtensions();
    }

    public BigInteger getNonce() {
        if (this.tstInfo.getNonce() != null) {
            return this.tstInfo.getNonce().getValue();
        }
        return null;
    }

    public AlgorithmIdentifier getHashAlgorithm() {
        return this.tstInfo.getMessageImprint().getHashAlgorithm();
    }

    public ASN1ObjectIdentifier getMessageImprintAlgOID() {
        return this.tstInfo.getMessageImprint().getHashAlgorithm().getAlgorithm();
    }

    public byte[] getMessageImprintDigest() {
        return this.tstInfo.getMessageImprint().getHashedMessage();
    }

    public byte[] getEncoded() throws IOException {
        return this.tstInfo.getEncoded();
    }

    public TSTInfo toASN1Structure() {
        return this.tstInfo;
    }
}

