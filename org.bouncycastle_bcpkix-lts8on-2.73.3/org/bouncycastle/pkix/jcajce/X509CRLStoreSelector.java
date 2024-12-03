/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.x509.Extension
 *  org.bouncycastle.util.Arrays
 *  org.bouncycastle.util.Selector
 */
package org.bouncycastle.pkix.jcajce;

import java.io.IOException;
import java.math.BigInteger;
import java.security.cert.CRL;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLSelector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Selector;

class X509CRLStoreSelector
extends X509CRLSelector
implements Selector {
    private boolean deltaCRLIndicator = false;
    private boolean completeCRLEnabled = false;
    private BigInteger maxBaseCRLNumber = null;
    private byte[] issuingDistributionPoint = null;
    private boolean issuingDistributionPointEnabled = false;

    X509CRLStoreSelector() {
    }

    public boolean isIssuingDistributionPointEnabled() {
        return this.issuingDistributionPointEnabled;
    }

    public void setIssuingDistributionPointEnabled(boolean issuingDistributionPointEnabled) {
        this.issuingDistributionPointEnabled = issuingDistributionPointEnabled;
    }

    public boolean match(Object obj) {
        if (!(obj instanceof X509CRL)) {
            return false;
        }
        X509CRL crl = (X509CRL)obj;
        ASN1Integer dci = null;
        try {
            byte[] bytes = crl.getExtensionValue(Extension.deltaCRLIndicator.getId());
            if (bytes != null) {
                dci = ASN1Integer.getInstance((Object)JcaX509ExtensionUtils.parseExtensionValue(bytes));
            }
        }
        catch (Exception e) {
            return false;
        }
        if (this.isDeltaCRLIndicatorEnabled() && dci == null) {
            return false;
        }
        if (this.isCompleteCRLEnabled() && dci != null) {
            return false;
        }
        if (dci != null && this.maxBaseCRLNumber != null && dci.getPositiveValue().compareTo(this.maxBaseCRLNumber) == 1) {
            return false;
        }
        if (this.issuingDistributionPointEnabled) {
            byte[] idp = crl.getExtensionValue(Extension.issuingDistributionPoint.getId());
            if (this.issuingDistributionPoint == null ? idp != null : !Arrays.areEqual((byte[])idp, (byte[])this.issuingDistributionPoint)) {
                return false;
            }
        }
        return super.match((X509CRL)obj);
    }

    @Override
    public boolean match(CRL crl) {
        return this.match((Object)crl);
    }

    public boolean isDeltaCRLIndicatorEnabled() {
        return this.deltaCRLIndicator;
    }

    public void setDeltaCRLIndicatorEnabled(boolean deltaCRLIndicator) {
        this.deltaCRLIndicator = deltaCRLIndicator;
    }

    public static X509CRLStoreSelector getInstance(X509CRLSelector selector) {
        if (selector == null) {
            throw new IllegalArgumentException("cannot create from null selector");
        }
        X509CRLStoreSelector cs = new X509CRLStoreSelector();
        cs.setCertificateChecking(selector.getCertificateChecking());
        cs.setDateAndTime(selector.getDateAndTime());
        try {
            cs.setIssuerNames(selector.getIssuerNames());
        }
        catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        cs.setIssuers(selector.getIssuers());
        cs.setMaxCRLNumber(selector.getMaxCRL());
        cs.setMinCRLNumber(selector.getMinCRL());
        return cs;
    }

    @Override
    public Object clone() {
        X509CRLStoreSelector sel = X509CRLStoreSelector.getInstance(this);
        sel.deltaCRLIndicator = this.deltaCRLIndicator;
        sel.completeCRLEnabled = this.completeCRLEnabled;
        sel.maxBaseCRLNumber = this.maxBaseCRLNumber;
        sel.issuingDistributionPointEnabled = this.issuingDistributionPointEnabled;
        sel.issuingDistributionPoint = Arrays.clone((byte[])this.issuingDistributionPoint);
        return sel;
    }

    public boolean isCompleteCRLEnabled() {
        return this.completeCRLEnabled;
    }

    public void setCompleteCRLEnabled(boolean completeCRLEnabled) {
        this.completeCRLEnabled = completeCRLEnabled;
    }

    public BigInteger getMaxBaseCRLNumber() {
        return this.maxBaseCRLNumber;
    }

    public void setMaxBaseCRLNumber(BigInteger maxBaseCRLNumber) {
        this.maxBaseCRLNumber = maxBaseCRLNumber;
    }

    public byte[] getIssuingDistributionPoint() {
        return Arrays.clone((byte[])this.issuingDistributionPoint);
    }

    public void setIssuingDistributionPoint(byte[] issuingDistributionPoint) {
        this.issuingDistributionPoint = Arrays.clone((byte[])issuingDistributionPoint);
    }
}

