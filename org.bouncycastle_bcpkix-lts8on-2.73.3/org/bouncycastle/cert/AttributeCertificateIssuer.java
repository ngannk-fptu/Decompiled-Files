/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.x500.X500Name
 *  org.bouncycastle.asn1.x509.AttCertIssuer
 *  org.bouncycastle.asn1.x509.GeneralName
 *  org.bouncycastle.asn1.x509.GeneralNames
 *  org.bouncycastle.asn1.x509.V2Form
 *  org.bouncycastle.util.Selector
 */
package org.bouncycastle.cert;

import java.util.ArrayList;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AttCertIssuer;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.V2Form;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.Selector;

public class AttributeCertificateIssuer
implements Selector {
    final ASN1Encodable form;

    public AttributeCertificateIssuer(AttCertIssuer issuer) {
        this.form = issuer.getIssuer();
    }

    public AttributeCertificateIssuer(X500Name principal) {
        this.form = new V2Form(new GeneralNames(new GeneralName(principal)));
    }

    public X500Name[] getNames() {
        GeneralNames name = this.form instanceof V2Form ? ((V2Form)this.form).getIssuerName() : (GeneralNames)this.form;
        GeneralName[] names = name.getNames();
        ArrayList<X500Name> l = new ArrayList<X500Name>(names.length);
        for (int i = 0; i != names.length; ++i) {
            if (names[i].getTagNo() != 4) continue;
            l.add(X500Name.getInstance((Object)names[i].getName()));
        }
        return l.toArray(new X500Name[l.size()]);
    }

    private boolean matchesDN(X500Name subject, GeneralNames targets) {
        GeneralName[] names = targets.getNames();
        for (int i = 0; i != names.length; ++i) {
            GeneralName gn = names[i];
            if (gn.getTagNo() != 4 || !X500Name.getInstance((Object)gn.getName()).equals((Object)subject)) continue;
            return true;
        }
        return false;
    }

    public Object clone() {
        return new AttributeCertificateIssuer(AttCertIssuer.getInstance((Object)this.form));
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AttributeCertificateIssuer)) {
            return false;
        }
        AttributeCertificateIssuer other = (AttributeCertificateIssuer)obj;
        return this.form.equals(other.form);
    }

    public int hashCode() {
        return this.form.hashCode();
    }

    public boolean match(Object obj) {
        if (!(obj instanceof X509CertificateHolder)) {
            return false;
        }
        X509CertificateHolder x509Cert = (X509CertificateHolder)obj;
        if (this.form instanceof V2Form) {
            V2Form issuer = (V2Form)this.form;
            if (issuer.getBaseCertificateID() != null) {
                return issuer.getBaseCertificateID().getSerial().hasValue(x509Cert.getSerialNumber()) && this.matchesDN(x509Cert.getIssuer(), issuer.getBaseCertificateID().getIssuer());
            }
            GeneralNames name = issuer.getIssuerName();
            if (this.matchesDN(x509Cert.getSubject(), name)) {
                return true;
            }
        } else {
            GeneralNames name = (GeneralNames)this.form;
            if (this.matchesDN(x509Cert.getSubject(), name)) {
                return true;
            }
        }
        return false;
    }
}

