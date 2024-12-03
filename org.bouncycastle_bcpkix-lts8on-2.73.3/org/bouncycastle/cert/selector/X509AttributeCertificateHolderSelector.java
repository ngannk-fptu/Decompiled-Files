/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x509.Extension
 *  org.bouncycastle.asn1.x509.GeneralName
 *  org.bouncycastle.asn1.x509.Target
 *  org.bouncycastle.asn1.x509.TargetInformation
 *  org.bouncycastle.asn1.x509.Targets
 *  org.bouncycastle.util.Selector
 */
package org.bouncycastle.cert.selector;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.Target;
import org.bouncycastle.asn1.x509.TargetInformation;
import org.bouncycastle.asn1.x509.Targets;
import org.bouncycastle.cert.AttributeCertificateHolder;
import org.bouncycastle.cert.AttributeCertificateIssuer;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.util.Selector;

public class X509AttributeCertificateHolderSelector
implements Selector {
    private final AttributeCertificateHolder holder;
    private final AttributeCertificateIssuer issuer;
    private final BigInteger serialNumber;
    private final Date attributeCertificateValid;
    private final X509AttributeCertificateHolder attributeCert;
    private final Collection targetNames;
    private final Collection targetGroups;

    X509AttributeCertificateHolderSelector(AttributeCertificateHolder holder, AttributeCertificateIssuer issuer, BigInteger serialNumber, Date attributeCertificateValid, X509AttributeCertificateHolder attributeCert, Collection targetNames, Collection targetGroups) {
        this.holder = holder;
        this.issuer = issuer;
        this.serialNumber = serialNumber;
        this.attributeCertificateValid = attributeCertificateValid;
        this.attributeCert = attributeCert;
        this.targetNames = targetNames;
        this.targetGroups = targetGroups;
    }

    public boolean match(Object obj) {
        Extension targetInfoExt;
        if (!(obj instanceof X509AttributeCertificateHolder)) {
            return false;
        }
        X509AttributeCertificateHolder attrCert = (X509AttributeCertificateHolder)obj;
        if (this.attributeCert != null && !this.attributeCert.equals(attrCert)) {
            return false;
        }
        if (this.serialNumber != null && !attrCert.getSerialNumber().equals(this.serialNumber)) {
            return false;
        }
        if (this.holder != null && !attrCert.getHolder().equals(this.holder)) {
            return false;
        }
        if (this.issuer != null && !attrCert.getIssuer().equals(this.issuer)) {
            return false;
        }
        if (this.attributeCertificateValid != null && !attrCert.isValidOn(this.attributeCertificateValid)) {
            return false;
        }
        if (!(this.targetNames.isEmpty() && this.targetGroups.isEmpty() || (targetInfoExt = attrCert.getExtension(Extension.targetInformation)) == null)) {
            int j;
            Target[] targets;
            Targets t;
            int i;
            boolean found;
            TargetInformation targetinfo;
            try {
                targetinfo = TargetInformation.getInstance((Object)targetInfoExt.getParsedValue());
            }
            catch (IllegalArgumentException e) {
                return false;
            }
            Targets[] targetss = targetinfo.getTargetsObjects();
            if (!this.targetNames.isEmpty()) {
                found = false;
                block2: for (i = 0; i < targetss.length; ++i) {
                    t = targetss[i];
                    targets = t.getTargets();
                    for (j = 0; j < targets.length; ++j) {
                        if (!this.targetNames.contains(GeneralName.getInstance((Object)targets[j].getTargetName()))) continue;
                        found = true;
                        continue block2;
                    }
                }
                if (!found) {
                    return false;
                }
            }
            if (!this.targetGroups.isEmpty()) {
                found = false;
                block4: for (i = 0; i < targetss.length; ++i) {
                    t = targetss[i];
                    targets = t.getTargets();
                    for (j = 0; j < targets.length; ++j) {
                        if (!this.targetGroups.contains(GeneralName.getInstance((Object)targets[j].getTargetGroup()))) continue;
                        found = true;
                        continue block4;
                    }
                }
                if (!found) {
                    return false;
                }
            }
        }
        return true;
    }

    public Object clone() {
        X509AttributeCertificateHolderSelector sel = new X509AttributeCertificateHolderSelector(this.holder, this.issuer, this.serialNumber, this.attributeCertificateValid, this.attributeCert, this.targetNames, this.targetGroups);
        return sel;
    }

    public X509AttributeCertificateHolder getAttributeCert() {
        return this.attributeCert;
    }

    public Date getAttributeCertificateValid() {
        if (this.attributeCertificateValid != null) {
            return new Date(this.attributeCertificateValid.getTime());
        }
        return null;
    }

    public AttributeCertificateHolder getHolder() {
        return this.holder;
    }

    public AttributeCertificateIssuer getIssuer() {
        return this.issuer;
    }

    public BigInteger getSerialNumber() {
        return this.serialNumber;
    }

    public Collection getTargetNames() {
        return this.targetNames;
    }

    public Collection getTargetGroups() {
        return this.targetGroups;
    }
}

