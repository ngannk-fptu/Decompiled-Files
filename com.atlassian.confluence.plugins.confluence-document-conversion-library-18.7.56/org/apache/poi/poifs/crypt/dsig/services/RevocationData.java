/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.poifs.crypt.dsig.services;

import java.security.cert.CRLException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RevocationData {
    private final List<byte[]> crls = new ArrayList<byte[]>();
    private final List<byte[]> ocsps = new ArrayList<byte[]>();
    private final List<X509Certificate> x509chain = new ArrayList<X509Certificate>();

    public void addCRL(byte[] encodedCrl) {
        if (this.crls.stream().noneMatch(by -> Arrays.equals(by, encodedCrl))) {
            this.crls.add(encodedCrl);
        }
    }

    public void addCRL(X509CRL crl) {
        try {
            this.addCRL(crl.getEncoded());
        }
        catch (CRLException e) {
            throw new IllegalArgumentException("CRL coding error: " + e.getMessage(), e);
        }
    }

    public void addOCSP(byte[] encodedOcsp) {
        this.ocsps.add(encodedOcsp);
    }

    public void addCertificate(X509Certificate x509) {
        this.x509chain.add(x509);
    }

    public List<byte[]> getCRLs() {
        return this.crls;
    }

    public List<byte[]> getOCSPs() {
        return this.ocsps;
    }

    public boolean hasOCSPs() {
        return !this.ocsps.isEmpty();
    }

    public boolean hasCRLs() {
        return !this.crls.isEmpty();
    }

    public boolean hasRevocationDataEntries() {
        return this.hasOCSPs() || this.hasCRLs();
    }

    public List<X509Certificate> getX509chain() {
        return this.x509chain;
    }
}

