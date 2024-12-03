/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1InputStream
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.tsp.TimeStampReq
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.Extension
 *  org.bouncycastle.asn1.x509.Extensions
 */
package org.bouncycastle.tsp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.tsp.TimeStampReq;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TSPUtil;
import org.bouncycastle.tsp.TSPValidationException;

public class TimeStampRequest {
    private static Set EMPTY_SET = Collections.unmodifiableSet(new HashSet());
    private TimeStampReq req;
    private Extensions extensions;

    public TimeStampRequest(TimeStampReq req) {
        this.req = req;
        this.extensions = req.getExtensions();
    }

    public TimeStampRequest(byte[] req) throws IOException {
        this(new ByteArrayInputStream(req));
    }

    public TimeStampRequest(InputStream in) throws IOException {
        this(TimeStampRequest.loadRequest(in));
    }

    private static TimeStampReq loadRequest(InputStream in) throws IOException {
        try {
            return TimeStampReq.getInstance((Object)new ASN1InputStream(in).readObject());
        }
        catch (ClassCastException e) {
            throw new IOException("malformed request: " + e);
        }
        catch (IllegalArgumentException e) {
            throw new IOException("malformed request: " + e);
        }
    }

    public int getVersion() {
        return this.req.getVersion().intValueExact();
    }

    public ASN1ObjectIdentifier getMessageImprintAlgOID() {
        return this.req.getMessageImprint().getHashAlgorithm().getAlgorithm();
    }

    public AlgorithmIdentifier getMessageImprintAlgID() {
        return this.req.getMessageImprint().getHashAlgorithm();
    }

    public byte[] getMessageImprintDigest() {
        return this.req.getMessageImprint().getHashedMessage();
    }

    public ASN1ObjectIdentifier getReqPolicy() {
        if (this.req.getReqPolicy() != null) {
            return this.req.getReqPolicy();
        }
        return null;
    }

    public BigInteger getNonce() {
        if (this.req.getNonce() != null) {
            return this.req.getNonce().getValue();
        }
        return null;
    }

    public boolean getCertReq() {
        if (this.req.getCertReq() != null) {
            return this.req.getCertReq().isTrue();
        }
        return false;
    }

    public void validate(Set algorithms, Set policies, Set extensions) throws TSPException {
        int digestLength;
        algorithms = this.convert(algorithms);
        policies = this.convert(policies);
        extensions = this.convert(extensions);
        if (!algorithms.contains(this.getMessageImprintAlgOID())) {
            throw new TSPValidationException("request contains unknown algorithm", 128);
        }
        if (policies != null && this.getReqPolicy() != null && !policies.contains(this.getReqPolicy())) {
            throw new TSPValidationException("request contains unknown policy", 256);
        }
        if (this.getExtensions() != null && extensions != null) {
            Enumeration en = this.getExtensions().oids();
            while (en.hasMoreElements()) {
                ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier)en.nextElement();
                if (extensions.contains(oid)) continue;
                throw new TSPValidationException("request contains unknown extension", 0x800000);
            }
        }
        if ((digestLength = TSPUtil.getDigestLength(this.getMessageImprintAlgOID().getId())) != this.getMessageImprintDigest().length) {
            throw new TSPValidationException("imprint digest the wrong length", 4);
        }
    }

    public byte[] getEncoded() throws IOException {
        return this.req.getEncoded();
    }

    Extensions getExtensions() {
        return this.extensions;
    }

    public boolean hasExtensions() {
        return this.extensions != null;
    }

    public Extension getExtension(ASN1ObjectIdentifier oid) {
        if (this.extensions != null) {
            return this.extensions.getExtension(oid);
        }
        return null;
    }

    public List getExtensionOIDs() {
        return TSPUtil.getExtensionOIDs(this.extensions);
    }

    public Set getNonCriticalExtensionOIDs() {
        if (this.extensions == null) {
            return EMPTY_SET;
        }
        return Collections.unmodifiableSet(new HashSet<ASN1ObjectIdentifier>(Arrays.asList(this.extensions.getNonCriticalExtensionOIDs())));
    }

    public Set getCriticalExtensionOIDs() {
        if (this.extensions == null) {
            return EMPTY_SET;
        }
        return Collections.unmodifiableSet(new HashSet<ASN1ObjectIdentifier>(Arrays.asList(this.extensions.getCriticalExtensionOIDs())));
    }

    private Set convert(Set orig) {
        if (orig == null) {
            return orig;
        }
        HashSet<Object> con = new HashSet<Object>(orig.size());
        for (Object o : orig) {
            if (o instanceof String) {
                con.add(new ASN1ObjectIdentifier((String)o));
                continue;
            }
            con.add(o);
        }
        return con;
    }
}

