/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.io.IOException;
import java.math.BigInteger;
import java.security.cert.CRLException;
import java.security.cert.X509CRLEntry;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.CRLReason;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.TBSCertList;
import org.bouncycastle.util.Strings;

class X509CRLEntryObject
extends X509CRLEntry {
    private TBSCertList.CRLEntry c;
    private X500Name certificateIssuer;
    private volatile boolean hashValueSet;
    private volatile int hashValue;

    protected X509CRLEntryObject(TBSCertList.CRLEntry c) {
        this.c = c;
        this.certificateIssuer = null;
    }

    protected X509CRLEntryObject(TBSCertList.CRLEntry c, boolean isIndirect, X500Name previousCertificateIssuer) {
        this.c = c;
        this.certificateIssuer = this.loadCertificateIssuer(isIndirect, previousCertificateIssuer);
    }

    @Override
    public boolean hasUnsupportedCriticalExtension() {
        Set extns = this.getCriticalExtensionOIDs();
        return extns != null && !extns.isEmpty();
    }

    private X500Name loadCertificateIssuer(boolean isIndirect, X500Name previousCertificateIssuer) {
        if (!isIndirect) {
            return null;
        }
        Extension ext = this.getExtension(Extension.certificateIssuer);
        if (ext == null) {
            return previousCertificateIssuer;
        }
        try {
            GeneralName[] names = GeneralNames.getInstance(ext.getParsedValue()).getNames();
            for (int i = 0; i < names.length; ++i) {
                if (names[i].getTagNo() != 4) continue;
                return X500Name.getInstance(names[i].getName());
            }
            return null;
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    public X500Principal getCertificateIssuer() {
        if (this.certificateIssuer == null) {
            return null;
        }
        try {
            return new X500Principal(this.certificateIssuer.getEncoded());
        }
        catch (IOException e) {
            return null;
        }
    }

    private Set getExtensionOIDs(boolean critical) {
        Extensions extensions = this.c.getExtensions();
        if (extensions != null) {
            HashSet<String> set = new HashSet<String>();
            Enumeration e = extensions.oids();
            while (e.hasMoreElements()) {
                ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier)e.nextElement();
                Extension ext = extensions.getExtension(oid);
                if (critical != ext.isCritical()) continue;
                set.add(oid.getId());
            }
            return set;
        }
        return null;
    }

    public Set getCriticalExtensionOIDs() {
        return this.getExtensionOIDs(true);
    }

    public Set getNonCriticalExtensionOIDs() {
        return this.getExtensionOIDs(false);
    }

    private Extension getExtension(ASN1ObjectIdentifier oid) {
        Extensions exts = this.c.getExtensions();
        if (exts != null) {
            return exts.getExtension(oid);
        }
        return null;
    }

    @Override
    public byte[] getExtensionValue(String oid) {
        Extension ext = this.getExtension(new ASN1ObjectIdentifier(oid));
        if (ext != null) {
            try {
                return ext.getExtnValue().getEncoded();
            }
            catch (Exception e) {
                throw new IllegalStateException("Exception encoding: " + e.toString());
            }
        }
        return null;
    }

    @Override
    public int hashCode() {
        if (!this.hashValueSet) {
            this.hashValue = super.hashCode();
            this.hashValueSet = true;
        }
        return this.hashValue;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof X509CRLEntryObject) {
            X509CRLEntryObject otherBC = (X509CRLEntryObject)other;
            if (this.hashValueSet && otherBC.hashValueSet && this.hashValue != otherBC.hashValue) {
                return false;
            }
            return this.c.equals(otherBC.c);
        }
        return super.equals(this);
    }

    @Override
    public byte[] getEncoded() throws CRLException {
        try {
            return this.c.getEncoded("DER");
        }
        catch (IOException e) {
            throw new CRLException(e.toString());
        }
    }

    @Override
    public BigInteger getSerialNumber() {
        return this.c.getUserCertificate().getValue();
    }

    @Override
    public Date getRevocationDate() {
        return this.c.getRevocationDate().getDate();
    }

    @Override
    public boolean hasExtensions() {
        return this.c.getExtensions() != null;
    }

    @Override
    public String toString() {
        Enumeration e;
        StringBuffer buf = new StringBuffer();
        String nl = Strings.lineSeparator();
        buf.append("      userCertificate: ").append(this.getSerialNumber()).append(nl);
        buf.append("       revocationDate: ").append(this.getRevocationDate()).append(nl);
        buf.append("       certificateIssuer: ").append(this.getCertificateIssuer()).append(nl);
        Extensions extensions = this.c.getExtensions();
        if (extensions != null && (e = extensions.oids()).hasMoreElements()) {
            buf.append("   crlEntryExtensions:").append(nl);
            while (e.hasMoreElements()) {
                ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier)e.nextElement();
                Extension ext = extensions.getExtension(oid);
                if (ext.getExtnValue() != null) {
                    byte[] octs = ext.getExtnValue().getOctets();
                    ASN1InputStream dIn = new ASN1InputStream(octs);
                    buf.append("                       critical(").append(ext.isCritical()).append(") ");
                    try {
                        if (oid.equals(Extension.reasonCode)) {
                            buf.append(CRLReason.getInstance(ASN1Enumerated.getInstance(dIn.readObject()))).append(nl);
                            continue;
                        }
                        if (oid.equals(Extension.certificateIssuer)) {
                            buf.append("Certificate issuer: ").append(GeneralNames.getInstance(dIn.readObject())).append(nl);
                            continue;
                        }
                        buf.append(oid.getId());
                        buf.append(" value = ").append(ASN1Dump.dumpAsString(dIn.readObject())).append(nl);
                    }
                    catch (Exception ex) {
                        buf.append(oid.getId());
                        buf.append(" value = ").append("*****").append(nl);
                    }
                    continue;
                }
                buf.append(nl);
            }
        }
        return buf.toString();
    }
}

