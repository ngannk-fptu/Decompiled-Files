/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.dvcs.DVCSRequestInformation
 *  org.bouncycastle.asn1.dvcs.DVCSTime
 *  org.bouncycastle.asn1.x509.GeneralNames
 *  org.bouncycastle.asn1.x509.PolicyInformation
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.dvcs;

import java.math.BigInteger;
import java.util.Date;
import org.bouncycastle.asn1.dvcs.DVCSRequestInformation;
import org.bouncycastle.asn1.dvcs.DVCSTime;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.dvcs.DVCSParsingException;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.util.Arrays;

public class DVCSRequestInfo {
    private DVCSRequestInformation data;

    public DVCSRequestInfo(byte[] in) {
        this(DVCSRequestInformation.getInstance((Object)in));
    }

    public DVCSRequestInfo(DVCSRequestInformation data) {
        this.data = data;
    }

    public DVCSRequestInformation toASN1Structure() {
        return this.data;
    }

    public int getVersion() {
        return this.data.getVersion();
    }

    public int getServiceType() {
        return this.data.getService().getValue().intValue();
    }

    public BigInteger getNonce() {
        return this.data.getNonce();
    }

    public Date getRequestTime() throws DVCSParsingException {
        DVCSTime time = this.data.getRequestTime();
        if (time == null) {
            return null;
        }
        try {
            if (time.getGenTime() != null) {
                return time.getGenTime().getDate();
            }
            TimeStampToken token = new TimeStampToken(time.getTimeStampToken());
            return token.getTimeStampInfo().getGenTime();
        }
        catch (Exception e) {
            throw new DVCSParsingException("unable to extract time: " + e.getMessage(), e);
        }
    }

    public GeneralNames getRequester() {
        return this.data.getRequester();
    }

    public PolicyInformation getRequestPolicy() {
        if (this.data.getRequestPolicy() != null) {
            return this.data.getRequestPolicy();
        }
        return null;
    }

    public GeneralNames getDVCSNames() {
        return this.data.getDVCS();
    }

    public GeneralNames getDataLocations() {
        return this.data.getDataLocations();
    }

    public static boolean validate(DVCSRequestInfo requestInfo, DVCSRequestInfo responseInfo) {
        DVCSRequestInformation clientInfo = requestInfo.data;
        DVCSRequestInformation serverInfo = responseInfo.data;
        if (clientInfo.getVersion() != serverInfo.getVersion()) {
            return false;
        }
        if (!DVCSRequestInfo.clientEqualsServer(clientInfo.getService(), serverInfo.getService())) {
            return false;
        }
        if (!DVCSRequestInfo.clientEqualsServer(clientInfo.getRequestTime(), serverInfo.getRequestTime())) {
            return false;
        }
        if (!DVCSRequestInfo.clientEqualsServer(clientInfo.getRequestPolicy(), serverInfo.getRequestPolicy())) {
            return false;
        }
        if (!DVCSRequestInfo.clientEqualsServer(clientInfo.getExtensions(), serverInfo.getExtensions())) {
            return false;
        }
        if (clientInfo.getNonce() != null) {
            if (serverInfo.getNonce() == null) {
                return false;
            }
            byte[] clientNonce = clientInfo.getNonce().toByteArray();
            byte[] serverNonce = serverInfo.getNonce().toByteArray();
            if (serverNonce.length < clientNonce.length) {
                return false;
            }
            if (!Arrays.areEqual((byte[])clientNonce, (byte[])Arrays.copyOfRange((byte[])serverNonce, (int)0, (int)clientNonce.length))) {
                return false;
            }
        }
        return true;
    }

    private static boolean clientEqualsServer(Object client, Object server) {
        return client == null && server == null || client != null && client.equals(server);
    }
}

