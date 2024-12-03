/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.x500.AttributeTypeAndValue
 *  org.bouncycastle.asn1.x500.RDN
 *  org.bouncycastle.asn1.x500.X500Name
 *  org.bouncycastle.asn1.x500.style.BCStyle
 *  org.bouncycastle.util.Strings
 *  org.bouncycastle.util.encoders.Hex
 */
package org.bouncycastle.est.jcajce;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLSession;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x500.AttributeTypeAndValue;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.est.ESTException;
import org.bouncycastle.est.jcajce.JsseHostnameAuthorizer;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

public class JsseDefaultHostnameAuthorizer
implements JsseHostnameAuthorizer {
    private static Logger LOG = Logger.getLogger(JsseDefaultHostnameAuthorizer.class.getName());
    private final Set<String> knownSuffixes;

    public JsseDefaultHostnameAuthorizer(Set<String> knownSuffixes) {
        this.knownSuffixes = knownSuffixes;
    }

    @Override
    public boolean verified(String name, SSLSession context) throws IOException {
        try {
            CertificateFactory fac = CertificateFactory.getInstance("X509");
            X509Certificate cert = (X509Certificate)fac.generateCertificate(new ByteArrayInputStream(context.getPeerCertificates()[0].getEncoded()));
            return this.verify(name, cert);
        }
        catch (Exception ex) {
            if (ex instanceof ESTException) {
                throw (ESTException)ex;
            }
            throw new ESTException(ex.getMessage(), ex);
        }
    }

    public boolean verify(String name, X509Certificate cert) throws IOException {
        try {
            Collection<List<?>> n = cert.getSubjectAlternativeNames();
            if (n != null) {
                block6: for (List<?> l : n) {
                    int type = ((Number)l.get(0)).intValue();
                    switch (type) {
                        case 2: {
                            if (!JsseDefaultHostnameAuthorizer.isValidNameMatch(name, l.get(1).toString(), this.knownSuffixes)) continue block6;
                            return true;
                        }
                        case 7: {
                            if (!InetAddress.getByName(name).equals(InetAddress.getByName(l.get(1).toString()))) continue block6;
                            return true;
                        }
                    }
                    if (!LOG.isLoggable(Level.INFO)) continue;
                    String value = l.get(1) instanceof byte[] ? Hex.toHexString((byte[])((byte[])l.get(1))) : l.get(1).toString();
                    LOG.log(Level.INFO, "ignoring type " + type + " value = " + value);
                }
                return false;
            }
        }
        catch (Exception ex) {
            throw new ESTException(ex.getMessage(), ex);
        }
        if (cert.getSubjectX500Principal() == null) {
            return false;
        }
        RDN[] rdNs = X500Name.getInstance((Object)cert.getSubjectX500Principal().getEncoded()).getRDNs();
        for (int i = rdNs.length - 1; i >= 0; --i) {
            RDN rdn = rdNs[i];
            AttributeTypeAndValue[] typesAndValues = rdn.getTypesAndValues();
            for (int j = 0; j != typesAndValues.length; ++j) {
                AttributeTypeAndValue atv = typesAndValues[j];
                if (!atv.getType().equals((ASN1Primitive)BCStyle.CN)) continue;
                return JsseDefaultHostnameAuthorizer.isValidNameMatch(name, atv.getValue().toString(), this.knownSuffixes);
            }
        }
        return false;
    }

    public static boolean isValidNameMatch(String name, String dnsName, Set<String> suffixes) throws IOException {
        if (dnsName.contains("*")) {
            int wildIndex = dnsName.indexOf(42);
            if (wildIndex == dnsName.lastIndexOf("*")) {
                if (dnsName.contains("..") || dnsName.charAt(dnsName.length() - 1) == '*') {
                    return false;
                }
                int dnsDotIndex = dnsName.indexOf(46, wildIndex);
                if (suffixes != null && suffixes.contains(Strings.toLowerCase((String)dnsName.substring(dnsDotIndex)))) {
                    throw new IOException("Wildcard `" + dnsName + "` matches known public suffix.");
                }
                String end = Strings.toLowerCase((String)dnsName.substring(wildIndex + 1));
                String loweredName = Strings.toLowerCase((String)name);
                if (loweredName.equals(end)) {
                    return false;
                }
                if (end.length() > loweredName.length()) {
                    return false;
                }
                if (wildIndex > 0) {
                    if (loweredName.startsWith(dnsName.substring(0, wildIndex)) && loweredName.endsWith(end)) {
                        return loweredName.substring(wildIndex, loweredName.length() - end.length()).indexOf(46) < 0;
                    }
                    return false;
                }
                String prefix = loweredName.substring(0, loweredName.length() - end.length());
                if (prefix.indexOf(46) > 0) {
                    return false;
                }
                return loweredName.endsWith(end);
            }
            return false;
        }
        return name.equalsIgnoreCase(dnsName);
    }
}

