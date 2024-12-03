/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce.provider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.cert.CRL;
import java.security.cert.CRLSelector;
import java.security.cert.CertSelector;
import java.security.cert.CertStoreException;
import java.security.cert.CertStoreParameters;
import java.security.cert.CertStoreSpi;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509CertSelector;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.x509.CertificatePair;
import org.bouncycastle.jce.X509LDAPCertStoreParameters;

public class X509LDAPCertStoreSpi
extends CertStoreSpi {
    private static String[] FILTER_ESCAPE_TABLE = new String[93];
    private static String LDAP_PROVIDER;
    private static String REFERRALS_IGNORE;
    private static final String SEARCH_SECURITY_LEVEL = "none";
    private static final String URL_CONTEXT_PREFIX = "com.sun.jndi.url";
    private X509LDAPCertStoreParameters params;

    public X509LDAPCertStoreSpi(CertStoreParameters params) throws InvalidAlgorithmParameterException {
        super(params);
        if (!(params instanceof X509LDAPCertStoreParameters)) {
            throw new InvalidAlgorithmParameterException(X509LDAPCertStoreSpi.class.getName() + ": parameter must be a " + X509LDAPCertStoreParameters.class.getName() + " object\n" + params.toString());
        }
        this.params = (X509LDAPCertStoreParameters)params;
    }

    private DirContext connectLDAP() throws NamingException {
        Properties props = new Properties();
        props.setProperty("java.naming.factory.initial", LDAP_PROVIDER);
        props.setProperty("java.naming.batchsize", "0");
        props.setProperty("java.naming.provider.url", this.params.getLdapURL());
        props.setProperty("java.naming.factory.url.pkgs", URL_CONTEXT_PREFIX);
        props.setProperty("java.naming.referral", REFERRALS_IGNORE);
        props.setProperty("java.naming.security.authentication", SEARCH_SECURITY_LEVEL);
        InitialDirContext ctx = new InitialDirContext(props);
        return ctx;
    }

    private String parseDN(String subject, String subjectAttributeName) {
        String temp = subject;
        int begin = temp.toLowerCase().indexOf(subjectAttributeName.toLowerCase());
        int end = (temp = temp.substring(begin + subjectAttributeName.length())).indexOf(44);
        if (end == -1) {
            end = temp.length();
        }
        while (temp.charAt(end - 1) == '\\') {
            if ((end = temp.indexOf(44, end + 1)) != -1) continue;
            end = temp.length();
        }
        temp = temp.substring(0, end);
        begin = temp.indexOf(61);
        if ((temp = temp.substring(begin + 1)).charAt(0) == ' ') {
            temp = temp.substring(1);
        }
        if (temp.startsWith("\"")) {
            temp = temp.substring(1);
        }
        if (temp.endsWith("\"")) {
            temp = temp.substring(0, temp.length() - 1);
        }
        return this.filterEncode(temp);
    }

    public Collection engineGetCertificates(CertSelector selector) throws CertStoreException {
        if (!(selector instanceof X509CertSelector)) {
            throw new CertStoreException("selector is not a X509CertSelector");
        }
        X509CertSelector xselector = (X509CertSelector)selector;
        HashSet<Certificate> certSet = new HashSet<Certificate>();
        Set set = this.getEndCertificates(xselector);
        set.addAll(this.getCACertificates(xselector));
        set.addAll(this.getCrossCertificates(xselector));
        Iterator it = set.iterator();
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509", "BC");
            while (it.hasNext()) {
                byte[] bytes = (byte[])it.next();
                if (bytes == null || bytes.length == 0) continue;
                ArrayList<byte[]> bytesList = new ArrayList<byte[]>();
                bytesList.add(bytes);
                try {
                    CertificatePair pair = CertificatePair.getInstance(new ASN1InputStream(bytes).readObject());
                    bytesList.clear();
                    if (pair.getForward() != null) {
                        bytesList.add(pair.getForward().getEncoded());
                    }
                    if (pair.getReverse() != null) {
                        bytesList.add(pair.getReverse().getEncoded());
                    }
                }
                catch (IOException pair) {
                }
                catch (IllegalArgumentException pair) {
                    // empty catch block
                }
                Iterator it2 = bytesList.iterator();
                while (it2.hasNext()) {
                    ByteArrayInputStream bIn = new ByteArrayInputStream((byte[])it2.next());
                    try {
                        Certificate cert = cf.generateCertificate(bIn);
                        if (!xselector.match(cert)) continue;
                        certSet.add(cert);
                    }
                    catch (Exception exception) {}
                }
            }
        }
        catch (Exception e) {
            throw new CertStoreException("certificate cannot be constructed from LDAP result: " + e);
        }
        return certSet;
    }

    private Set certSubjectSerialSearch(X509CertSelector xselector, String[] attrs, String attrName, String subjectAttributeName) throws CertStoreException {
        HashSet set = new HashSet();
        try {
            if (xselector.getSubjectAsBytes() != null || xselector.getSubjectAsString() != null || xselector.getCertificate() != null) {
                String subject = null;
                String serial = null;
                if (xselector.getCertificate() != null) {
                    subject = xselector.getCertificate().getSubjectX500Principal().getName("RFC1779");
                    serial = xselector.getCertificate().getSerialNumber().toString();
                } else {
                    subject = xselector.getSubjectAsBytes() != null ? new X500Principal(xselector.getSubjectAsBytes()).getName("RFC1779") : xselector.getSubjectAsString();
                }
                String attrValue = this.parseDN(subject, subjectAttributeName);
                set.addAll(this.search(attrName, "*" + attrValue + "*", attrs));
                if (serial != null && this.params.getSearchForSerialNumberIn() != null) {
                    attrValue = serial;
                    attrName = this.params.getSearchForSerialNumberIn();
                    set.addAll(this.search(attrName, "*" + attrValue + "*", attrs));
                }
            } else {
                set.addAll(this.search(attrName, "*", attrs));
            }
        }
        catch (IOException e) {
            throw new CertStoreException("exception processing selector: " + e);
        }
        return set;
    }

    private Set getEndCertificates(X509CertSelector xselector) throws CertStoreException {
        String[] attrs = new String[]{this.params.getUserCertificateAttribute()};
        String attrName = this.params.getLdapUserCertificateAttributeName();
        String subjectAttributeName = this.params.getUserCertificateSubjectAttributeName();
        Set set = this.certSubjectSerialSearch(xselector, attrs, attrName, subjectAttributeName);
        return set;
    }

    private Set getCACertificates(X509CertSelector xselector) throws CertStoreException {
        String subjectAttributeName;
        String attrName;
        String[] attrs = new String[]{this.params.getCACertificateAttribute()};
        Set set = this.certSubjectSerialSearch(xselector, attrs, attrName = this.params.getLdapCACertificateAttributeName(), subjectAttributeName = this.params.getCACertificateSubjectAttributeName());
        if (set.isEmpty()) {
            set.addAll(this.search(null, "*", attrs));
        }
        return set;
    }

    private Set getCrossCertificates(X509CertSelector xselector) throws CertStoreException {
        String subjectAttributeName;
        String attrName;
        String[] attrs = new String[]{this.params.getCrossCertificateAttribute()};
        Set set = this.certSubjectSerialSearch(xselector, attrs, attrName = this.params.getLdapCrossCertificateAttributeName(), subjectAttributeName = this.params.getCrossCertificateSubjectAttributeName());
        if (set.isEmpty()) {
            set.addAll(this.search(null, "*", attrs));
        }
        return set;
    }

    public Collection engineGetCRLs(CRLSelector selector) throws CertStoreException {
        String[] attrs = new String[]{this.params.getCertificateRevocationListAttribute()};
        if (!(selector instanceof X509CRLSelector)) {
            throw new CertStoreException("selector is not a X509CRLSelector");
        }
        X509CRLSelector xselector = (X509CRLSelector)selector;
        HashSet<CRL> crlSet = new HashSet<CRL>();
        String attrName = this.params.getLdapCertificateRevocationListAttributeName();
        HashSet set = new HashSet();
        if (xselector.getIssuerNames() != null) {
            for (Object o : xselector.getIssuerNames()) {
                String issuerAttributeName;
                String attrValue = null;
                if (o instanceof String) {
                    issuerAttributeName = this.params.getCertificateRevocationListIssuerAttributeName();
                    attrValue = this.parseDN((String)o, issuerAttributeName);
                } else {
                    issuerAttributeName = this.params.getCertificateRevocationListIssuerAttributeName();
                    attrValue = this.parseDN(new X500Principal((byte[])o).getName("RFC1779"), issuerAttributeName);
                }
                set.addAll(this.search(attrName, "*" + attrValue + "*", attrs));
            }
        } else {
            set.addAll(this.search(attrName, "*", attrs));
        }
        set.addAll(this.search(null, "*", attrs));
        Iterator<Object> it = set.iterator();
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509", "BC");
            while (it.hasNext()) {
                CRL crl = cf.generateCRL(new ByteArrayInputStream((byte[])it.next()));
                if (!xselector.match(crl)) continue;
                crlSet.add(crl);
            }
        }
        catch (Exception e) {
            throw new CertStoreException("CRL cannot be constructed from LDAP result " + e);
        }
        return crlSet;
    }

    private String filterEncode(String value) {
        if (value == null) {
            return null;
        }
        StringBuilder encodedValue = new StringBuilder(value.length() * 2);
        int length = value.length();
        for (int i = 0; i < length; ++i) {
            char c = value.charAt(i);
            if (c < FILTER_ESCAPE_TABLE.length) {
                encodedValue.append(FILTER_ESCAPE_TABLE[c]);
                continue;
            }
            encodedValue.append(c);
        }
        return encodedValue.toString();
    }

    private Set search(String attributeName, String attributeValue, String[] attrs) throws CertStoreException {
        String filter = attributeName + "=" + attributeValue;
        if (attributeName == null) {
            filter = null;
        }
        DirContext ctx = null;
        HashSet set = new HashSet();
        try {
            ctx = this.connectLDAP();
            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(2);
            constraints.setCountLimit(0L);
            for (int i = 0; i < attrs.length; ++i) {
                String[] temp = new String[]{attrs[i]};
                constraints.setReturningAttributes(temp);
                String filter2 = "(&(" + filter + ")(" + temp[0] + "=*))";
                if (filter == null) {
                    filter2 = "(" + temp[0] + "=*)";
                }
                NamingEnumeration<SearchResult> results = ctx.search(this.params.getBaseDN(), filter2, constraints);
                while (results.hasMoreElements()) {
                    SearchResult sr = results.next();
                    NamingEnumeration<?> enumeration = sr.getAttributes().getAll().next().getAll();
                    while (enumeration.hasMore()) {
                        Object o = enumeration.next();
                        set.add(o);
                    }
                }
            }
        }
        catch (Exception e) {
            throw new CertStoreException("Error getting results from LDAP directory " + e);
        }
        finally {
            try {
                if (null != ctx) {
                    ctx.close();
                }
            }
            catch (Exception exception) {}
        }
        return set;
    }

    static {
        for (char c = '\u0000'; c < FILTER_ESCAPE_TABLE.length; c = (char)(c + '\u0001')) {
            X509LDAPCertStoreSpi.FILTER_ESCAPE_TABLE[c] = String.valueOf(c);
        }
        X509LDAPCertStoreSpi.FILTER_ESCAPE_TABLE[42] = "\\2a";
        X509LDAPCertStoreSpi.FILTER_ESCAPE_TABLE[40] = "\\28";
        X509LDAPCertStoreSpi.FILTER_ESCAPE_TABLE[41] = "\\29";
        X509LDAPCertStoreSpi.FILTER_ESCAPE_TABLE[92] = "\\5c";
        X509LDAPCertStoreSpi.FILTER_ESCAPE_TABLE[0] = "\\00";
        LDAP_PROVIDER = "com.sun.jndi.ldap.LdapCtxFactory";
        REFERRALS_IGNORE = "ignore";
    }
}

