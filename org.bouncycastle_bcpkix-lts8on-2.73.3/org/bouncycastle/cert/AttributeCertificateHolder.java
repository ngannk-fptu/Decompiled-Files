/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.x500.X500Name
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.GeneralName
 *  org.bouncycastle.asn1.x509.GeneralNames
 *  org.bouncycastle.asn1.x509.Holder
 *  org.bouncycastle.asn1.x509.IssuerSerial
 *  org.bouncycastle.asn1.x509.ObjectDigestInfo
 *  org.bouncycastle.util.Arrays
 *  org.bouncycastle.util.Selector
 */
package org.bouncycastle.cert;

import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.Holder;
import org.bouncycastle.asn1.x509.IssuerSerial;
import org.bouncycastle.asn1.x509.ObjectDigestInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Selector;

public class AttributeCertificateHolder
implements Selector {
    private static DigestCalculatorProvider digestCalculatorProvider;
    final Holder holder;

    AttributeCertificateHolder(ASN1Sequence seq) {
        this.holder = Holder.getInstance((Object)seq);
    }

    public AttributeCertificateHolder(X500Name issuerName, BigInteger serialNumber) {
        this.holder = new Holder(new IssuerSerial(this.generateGeneralNames(issuerName), new ASN1Integer(serialNumber)));
    }

    public AttributeCertificateHolder(X509CertificateHolder cert) {
        this.holder = new Holder(new IssuerSerial(this.generateGeneralNames(cert.getIssuer()), new ASN1Integer(cert.getSerialNumber())));
    }

    public AttributeCertificateHolder(X500Name principal) {
        this.holder = new Holder(this.generateGeneralNames(principal));
    }

    public AttributeCertificateHolder(int digestedObjectType, ASN1ObjectIdentifier digestAlgorithm, ASN1ObjectIdentifier otherObjectTypeID, byte[] objectDigest) {
        this.holder = new Holder(new ObjectDigestInfo(digestedObjectType, otherObjectTypeID, new AlgorithmIdentifier(digestAlgorithm), Arrays.clone((byte[])objectDigest)));
    }

    public int getDigestedObjectType() {
        if (this.holder.getObjectDigestInfo() != null) {
            return this.holder.getObjectDigestInfo().getDigestedObjectType().intValueExact();
        }
        return -1;
    }

    public AlgorithmIdentifier getDigestAlgorithm() {
        if (this.holder.getObjectDigestInfo() != null) {
            return this.holder.getObjectDigestInfo().getDigestAlgorithm();
        }
        return null;
    }

    public byte[] getObjectDigest() {
        if (this.holder.getObjectDigestInfo() != null) {
            return this.holder.getObjectDigestInfo().getObjectDigest().getBytes();
        }
        return null;
    }

    public ASN1ObjectIdentifier getOtherObjectTypeID() {
        if (this.holder.getObjectDigestInfo() != null) {
            new ASN1ObjectIdentifier(this.holder.getObjectDigestInfo().getOtherObjectTypeID().getId());
        }
        return null;
    }

    private GeneralNames generateGeneralNames(X500Name principal) {
        return new GeneralNames(new GeneralName(principal));
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

    private X500Name[] getPrincipals(GeneralName[] names) {
        ArrayList<X500Name> l = new ArrayList<X500Name>(names.length);
        for (int i = 0; i != names.length; ++i) {
            if (names[i].getTagNo() != 4) continue;
            l.add(X500Name.getInstance((Object)names[i].getName()));
        }
        return l.toArray(new X500Name[l.size()]);
    }

    public X500Name[] getEntityNames() {
        if (this.holder.getEntityName() != null) {
            return this.getPrincipals(this.holder.getEntityName().getNames());
        }
        return null;
    }

    public X500Name[] getIssuer() {
        if (this.holder.getBaseCertificateID() != null) {
            return this.getPrincipals(this.holder.getBaseCertificateID().getIssuer().getNames());
        }
        return null;
    }

    public BigInteger getSerialNumber() {
        if (this.holder.getBaseCertificateID() != null) {
            return this.holder.getBaseCertificateID().getSerial().getValue();
        }
        return null;
    }

    public Object clone() {
        return new AttributeCertificateHolder((ASN1Sequence)this.holder.toASN1Primitive());
    }

    public boolean match(Object obj) {
        if (!(obj instanceof X509CertificateHolder)) {
            return false;
        }
        X509CertificateHolder x509Cert = (X509CertificateHolder)obj;
        if (this.holder.getBaseCertificateID() != null) {
            return this.holder.getBaseCertificateID().getSerial().hasValue(x509Cert.getSerialNumber()) && this.matchesDN(x509Cert.getIssuer(), this.holder.getBaseCertificateID().getIssuer());
        }
        if (this.holder.getEntityName() != null && this.matchesDN(x509Cert.getSubject(), this.holder.getEntityName())) {
            return true;
        }
        if (this.holder.getObjectDigestInfo() != null) {
            try {
                DigestCalculator digCalc = digestCalculatorProvider.get(this.holder.getObjectDigestInfo().getDigestAlgorithm());
                OutputStream digOut = digCalc.getOutputStream();
                switch (this.getDigestedObjectType()) {
                    case 0: {
                        digOut.write(x509Cert.getSubjectPublicKeyInfo().getEncoded());
                        break;
                    }
                    case 1: {
                        digOut.write(x509Cert.getEncoded());
                    }
                }
                digOut.close();
                if (Arrays.areEqual((byte[])digCalc.getDigest(), (byte[])this.getObjectDigest())) {
                    return true;
                }
            }
            catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AttributeCertificateHolder)) {
            return false;
        }
        AttributeCertificateHolder other = (AttributeCertificateHolder)obj;
        return this.holder.equals((Object)other.holder);
    }

    public int hashCode() {
        return this.holder.hashCode();
    }

    public static void setDigestCalculatorProvider(DigestCalculatorProvider digCalcProvider) {
        digestCalculatorProvider = digCalcProvider;
    }
}

