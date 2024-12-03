/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.asn1.x500.style.RFC4519Style;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralSubtree;
import org.bouncycastle.asn1.x509.NameConstraintValidator;
import org.bouncycastle.asn1.x509.NameConstraintValidatorException;
import org.bouncycastle.asn1.x509.OtherName;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

public class PKIXNameConstraintValidator
implements NameConstraintValidator {
    private Set excludedSubtreesDN = new HashSet();
    private Set excludedSubtreesDNS = new HashSet();
    private Set excludedSubtreesEmail = new HashSet();
    private Set excludedSubtreesURI = new HashSet();
    private Set excludedSubtreesIP = new HashSet();
    private Set excludedSubtreesOtherName = new HashSet();
    private Set permittedSubtreesDN;
    private Set permittedSubtreesDNS;
    private Set permittedSubtreesEmail;
    private Set permittedSubtreesURI;
    private Set permittedSubtreesIP;
    private Set permittedSubtreesOtherName;

    @Override
    public void checkPermitted(GeneralName name) throws NameConstraintValidatorException {
        switch (name.getTagNo()) {
            case 0: {
                this.checkPermittedOtherName(this.permittedSubtreesOtherName, OtherName.getInstance(name.getName()));
                break;
            }
            case 1: {
                this.checkPermittedEmail(this.permittedSubtreesEmail, this.extractNameAsString(name));
                break;
            }
            case 2: {
                this.checkPermittedDNS(this.permittedSubtreesDNS, this.extractNameAsString(name));
                break;
            }
            case 4: {
                this.checkPermittedDN(X500Name.getInstance(name.getName()));
                break;
            }
            case 6: {
                this.checkPermittedURI(this.permittedSubtreesURI, this.extractNameAsString(name));
                break;
            }
            case 7: {
                this.checkPermittedIP(this.permittedSubtreesIP, ASN1OctetString.getInstance(name.getName()).getOctets());
                break;
            }
        }
    }

    @Override
    public void checkExcluded(GeneralName name) throws NameConstraintValidatorException {
        switch (name.getTagNo()) {
            case 0: {
                this.checkExcludedOtherName(this.excludedSubtreesOtherName, OtherName.getInstance(name.getName()));
                break;
            }
            case 1: {
                this.checkExcludedEmail(this.excludedSubtreesEmail, this.extractNameAsString(name));
                break;
            }
            case 2: {
                this.checkExcludedDNS(this.excludedSubtreesDNS, this.extractNameAsString(name));
                break;
            }
            case 4: {
                this.checkExcludedDN(X500Name.getInstance(name.getName()));
                break;
            }
            case 6: {
                this.checkExcludedURI(this.excludedSubtreesURI, this.extractNameAsString(name));
                break;
            }
            case 7: {
                this.checkExcludedIP(this.excludedSubtreesIP, ASN1OctetString.getInstance(name.getName()).getOctets());
                break;
            }
        }
    }

    @Override
    public void intersectPermittedSubtree(GeneralSubtree permitted) {
        this.intersectPermittedSubtree(new GeneralSubtree[]{permitted});
    }

    @Override
    public void intersectPermittedSubtree(GeneralSubtree[] permitted) {
        HashMap subtreesMap = new HashMap();
        for (int i = 0; i != permitted.length; ++i) {
            GeneralSubtree subtree = permitted[i];
            Integer tagNo = Integers.valueOf(subtree.getBase().getTagNo());
            if (subtreesMap.get(tagNo) == null) {
                subtreesMap.put(tagNo, new HashSet());
            }
            ((Set)subtreesMap.get(tagNo)).add(subtree);
        }
        block9: for (Map.Entry entry : subtreesMap.entrySet()) {
            int nameType = (Integer)entry.getKey();
            switch (nameType) {
                case 0: {
                    this.permittedSubtreesOtherName = this.intersectOtherName(this.permittedSubtreesOtherName, (Set)entry.getValue());
                    continue block9;
                }
                case 1: {
                    this.permittedSubtreesEmail = this.intersectEmail(this.permittedSubtreesEmail, (Set)entry.getValue());
                    continue block9;
                }
                case 2: {
                    this.permittedSubtreesDNS = this.intersectDNS(this.permittedSubtreesDNS, (Set)entry.getValue());
                    continue block9;
                }
                case 4: {
                    this.permittedSubtreesDN = this.intersectDN(this.permittedSubtreesDN, (Set)entry.getValue());
                    continue block9;
                }
                case 6: {
                    this.permittedSubtreesURI = this.intersectURI(this.permittedSubtreesURI, (Set)entry.getValue());
                    continue block9;
                }
                case 7: {
                    this.permittedSubtreesIP = this.intersectIP(this.permittedSubtreesIP, (Set)entry.getValue());
                    continue block9;
                }
            }
            throw new IllegalStateException("Unknown tag encountered: " + nameType);
        }
    }

    @Override
    public void intersectEmptyPermittedSubtree(int nameType) {
        switch (nameType) {
            case 0: {
                this.permittedSubtreesOtherName = new HashSet();
                break;
            }
            case 1: {
                this.permittedSubtreesEmail = new HashSet();
                break;
            }
            case 2: {
                this.permittedSubtreesDNS = new HashSet();
                break;
            }
            case 4: {
                this.permittedSubtreesDN = new HashSet();
                break;
            }
            case 6: {
                this.permittedSubtreesURI = new HashSet();
                break;
            }
            case 7: {
                this.permittedSubtreesIP = new HashSet();
                break;
            }
            default: {
                throw new IllegalStateException("Unknown tag encountered: " + nameType);
            }
        }
    }

    @Override
    public void addExcludedSubtree(GeneralSubtree subtree) {
        GeneralName base = subtree.getBase();
        switch (base.getTagNo()) {
            case 0: {
                this.excludedSubtreesOtherName = this.unionOtherName(this.excludedSubtreesOtherName, OtherName.getInstance(base.getName()));
                break;
            }
            case 1: {
                this.excludedSubtreesEmail = this.unionEmail(this.excludedSubtreesEmail, this.extractNameAsString(base));
                break;
            }
            case 2: {
                this.excludedSubtreesDNS = this.unionDNS(this.excludedSubtreesDNS, this.extractNameAsString(base));
                break;
            }
            case 4: {
                this.excludedSubtreesDN = this.unionDN(this.excludedSubtreesDN, (ASN1Sequence)base.getName().toASN1Primitive());
                break;
            }
            case 6: {
                this.excludedSubtreesURI = this.unionURI(this.excludedSubtreesURI, this.extractNameAsString(base));
                break;
            }
            case 7: {
                this.excludedSubtreesIP = this.unionIP(this.excludedSubtreesIP, ASN1OctetString.getInstance(base.getName()).getOctets());
                break;
            }
            default: {
                throw new IllegalStateException("Unknown tag encountered: " + base.getTagNo());
            }
        }
    }

    public int hashCode() {
        return this.hashCollection(this.excludedSubtreesDN) + this.hashCollection(this.excludedSubtreesDNS) + this.hashCollection(this.excludedSubtreesEmail) + this.hashCollection(this.excludedSubtreesIP) + this.hashCollection(this.excludedSubtreesURI) + this.hashCollection(this.excludedSubtreesOtherName) + this.hashCollection(this.permittedSubtreesDN) + this.hashCollection(this.permittedSubtreesDNS) + this.hashCollection(this.permittedSubtreesEmail) + this.hashCollection(this.permittedSubtreesIP) + this.hashCollection(this.permittedSubtreesURI) + this.hashCollection(this.permittedSubtreesOtherName);
    }

    public boolean equals(Object o) {
        if (!(o instanceof PKIXNameConstraintValidator)) {
            return false;
        }
        PKIXNameConstraintValidator constraintValidator = (PKIXNameConstraintValidator)o;
        return this.collectionsAreEqual(constraintValidator.excludedSubtreesDN, this.excludedSubtreesDN) && this.collectionsAreEqual(constraintValidator.excludedSubtreesDNS, this.excludedSubtreesDNS) && this.collectionsAreEqual(constraintValidator.excludedSubtreesEmail, this.excludedSubtreesEmail) && this.collectionsAreEqual(constraintValidator.excludedSubtreesIP, this.excludedSubtreesIP) && this.collectionsAreEqual(constraintValidator.excludedSubtreesURI, this.excludedSubtreesURI) && this.collectionsAreEqual(constraintValidator.excludedSubtreesOtherName, this.excludedSubtreesOtherName) && this.collectionsAreEqual(constraintValidator.permittedSubtreesDN, this.permittedSubtreesDN) && this.collectionsAreEqual(constraintValidator.permittedSubtreesDNS, this.permittedSubtreesDNS) && this.collectionsAreEqual(constraintValidator.permittedSubtreesEmail, this.permittedSubtreesEmail) && this.collectionsAreEqual(constraintValidator.permittedSubtreesIP, this.permittedSubtreesIP) && this.collectionsAreEqual(constraintValidator.permittedSubtreesURI, this.permittedSubtreesURI) && this.collectionsAreEqual(constraintValidator.permittedSubtreesOtherName, this.permittedSubtreesOtherName);
    }

    public void checkPermittedDN(X500Name dns) throws NameConstraintValidatorException {
        this.checkPermittedDN(this.permittedSubtreesDN, ASN1Sequence.getInstance(dns.toASN1Primitive()));
    }

    public void checkExcludedDN(X500Name dns) throws NameConstraintValidatorException {
        this.checkExcludedDN(this.excludedSubtreesDN, ASN1Sequence.getInstance(dns));
    }

    private static boolean withinDNSubtree(ASN1Sequence dns, ASN1Sequence subtree) {
        int j;
        if (subtree.size() < 1) {
            return false;
        }
        if (subtree.size() > dns.size()) {
            return false;
        }
        int start = 0;
        RDN subtreeRdnStart = RDN.getInstance(subtree.getObjectAt(0));
        for (j = 0; j < dns.size(); ++j) {
            start = j;
            RDN dnsRdn = RDN.getInstance(dns.getObjectAt(j));
            if (IETFUtils.rDNAreEqual(subtreeRdnStart, dnsRdn)) break;
        }
        if (subtree.size() > dns.size() - start) {
            return false;
        }
        for (j = 0; j < subtree.size(); ++j) {
            RDN subtreeRdn = RDN.getInstance(subtree.getObjectAt(j));
            RDN dnsRdn = RDN.getInstance(dns.getObjectAt(start + j));
            if (subtreeRdn.size() == dnsRdn.size()) {
                if (!subtreeRdn.getFirst().getType().equals(dnsRdn.getFirst().getType())) {
                    return false;
                }
                if (!(subtreeRdn.size() == 1 && subtreeRdn.getFirst().getType().equals(RFC4519Style.serialNumber) ? !dnsRdn.getFirst().getValue().toString().startsWith(subtreeRdn.getFirst().getValue().toString()) : !IETFUtils.rDNAreEqual(subtreeRdn, dnsRdn))) continue;
                return false;
            }
            return false;
        }
        return true;
    }

    private void checkPermittedDN(Set permitted, ASN1Sequence dns) throws NameConstraintValidatorException {
        if (permitted == null) {
            return;
        }
        if (permitted.isEmpty() && dns.size() == 0) {
            return;
        }
        for (ASN1Sequence subtree : permitted) {
            if (!PKIXNameConstraintValidator.withinDNSubtree(dns, subtree)) continue;
            return;
        }
        throw new NameConstraintValidatorException("Subject distinguished name is not from a permitted subtree");
    }

    private void checkExcludedDN(Set excluded, ASN1Sequence dns) throws NameConstraintValidatorException {
        if (excluded.isEmpty()) {
            return;
        }
        for (ASN1Sequence subtree : excluded) {
            if (!PKIXNameConstraintValidator.withinDNSubtree(dns, subtree)) continue;
            throw new NameConstraintValidatorException("Subject distinguished name is from an excluded subtree");
        }
    }

    private Set intersectDN(Set permitted, Set dns) {
        HashSet<ASN1Sequence> intersect = new HashSet<ASN1Sequence>();
        Iterator it = dns.iterator();
        while (it.hasNext()) {
            ASN1Sequence dn = ASN1Sequence.getInstance(((GeneralSubtree)it.next()).getBase().getName().toASN1Primitive());
            if (permitted == null) {
                if (dn == null) continue;
                intersect.add(dn);
                continue;
            }
            for (ASN1Sequence subtree : permitted) {
                if (PKIXNameConstraintValidator.withinDNSubtree(dn, subtree)) {
                    intersect.add(dn);
                    continue;
                }
                if (!PKIXNameConstraintValidator.withinDNSubtree(subtree, dn)) continue;
                intersect.add(subtree);
            }
        }
        return intersect;
    }

    private Set unionDN(Set excluded, ASN1Sequence dn) {
        if (excluded.isEmpty()) {
            if (dn == null) {
                return excluded;
            }
            excluded.add(dn);
            return excluded;
        }
        HashSet<ASN1Sequence> intersect = new HashSet<ASN1Sequence>();
        Iterator it = excluded.iterator();
        while (it.hasNext()) {
            ASN1Sequence subtree = ASN1Sequence.getInstance(it.next());
            if (PKIXNameConstraintValidator.withinDNSubtree(dn, subtree)) {
                intersect.add(subtree);
                continue;
            }
            if (PKIXNameConstraintValidator.withinDNSubtree(subtree, dn)) {
                intersect.add(dn);
                continue;
            }
            intersect.add(subtree);
            intersect.add(dn);
        }
        return intersect;
    }

    private Set intersectOtherName(Set permitted, Set otherNames) {
        HashSet<OtherName> intersect = new HashSet<OtherName>();
        Iterator it = otherNames.iterator();
        while (it.hasNext()) {
            OtherName otName1 = OtherName.getInstance(((GeneralSubtree)it.next()).getBase().getName());
            if (permitted == null) {
                if (otName1 == null) continue;
                intersect.add(otName1);
                continue;
            }
            Iterator it2 = permitted.iterator();
            while (it2.hasNext()) {
                OtherName otName2 = OtherName.getInstance(it2.next());
                this.intersectOtherName(otName1, otName2, intersect);
            }
        }
        return intersect;
    }

    private void intersectOtherName(OtherName otName1, OtherName otName2, Set intersect) {
        if (otName1.equals(otName2)) {
            intersect.add(otName1);
        }
    }

    private Set unionOtherName(Set permitted, OtherName otherName) {
        HashSet<OtherName> union = permitted != null ? new HashSet<OtherName>(permitted) : new HashSet();
        union.add(otherName);
        return union;
    }

    private Set intersectEmail(Set permitted, Set emails) {
        HashSet<String> intersect = new HashSet<String>();
        Iterator it = emails.iterator();
        while (it.hasNext()) {
            String email = this.extractNameAsString(((GeneralSubtree)it.next()).getBase());
            if (permitted == null) {
                if (email == null) continue;
                intersect.add(email);
                continue;
            }
            for (String _permitted : permitted) {
                this.intersectEmail(email, _permitted, intersect);
            }
        }
        return intersect;
    }

    private Set unionEmail(Set excluded, String email) {
        if (excluded.isEmpty()) {
            if (email == null) {
                return excluded;
            }
            excluded.add(email);
            return excluded;
        }
        HashSet union = new HashSet();
        for (String _excluded : excluded) {
            this.unionEmail(_excluded, email, union);
        }
        return union;
    }

    private Set intersectIP(Set permitted, Set ips) {
        HashSet<byte[]> intersect = new HashSet<byte[]>();
        Iterator it = ips.iterator();
        while (it.hasNext()) {
            byte[] ip = ASN1OctetString.getInstance(((GeneralSubtree)it.next()).getBase().getName()).getOctets();
            if (permitted == null) {
                if (ip == null) continue;
                intersect.add(ip);
                continue;
            }
            for (byte[] _permitted : permitted) {
                intersect.addAll(this.intersectIPRange(_permitted, ip));
            }
        }
        return intersect;
    }

    private Set unionIP(Set excluded, byte[] ip) {
        if (excluded.isEmpty()) {
            if (ip == null) {
                return excluded;
            }
            excluded.add(ip);
            return excluded;
        }
        HashSet union = new HashSet();
        for (byte[] _excluded : excluded) {
            union.addAll(this.unionIPRange(_excluded, ip));
        }
        return union;
    }

    private Set unionIPRange(byte[] ipWithSubmask1, byte[] ipWithSubmask2) {
        HashSet<byte[]> set = new HashSet<byte[]>();
        if (Arrays.areEqual(ipWithSubmask1, ipWithSubmask2)) {
            set.add(ipWithSubmask1);
        } else {
            set.add(ipWithSubmask1);
            set.add(ipWithSubmask2);
        }
        return set;
    }

    private Set intersectIPRange(byte[] ipWithSubmask1, byte[] ipWithSubmask2) {
        if (ipWithSubmask1.length != ipWithSubmask2.length) {
            return Collections.EMPTY_SET;
        }
        byte[][] temp = this.extractIPsAndSubnetMasks(ipWithSubmask1, ipWithSubmask2);
        byte[] ip1 = temp[0];
        byte[] subnetmask1 = temp[1];
        byte[] ip2 = temp[2];
        byte[] subnetmask2 = temp[3];
        byte[][] minMax = this.minMaxIPs(ip1, subnetmask1, ip2, subnetmask2);
        byte[] max = PKIXNameConstraintValidator.min(minMax[1], minMax[3]);
        byte[] min = PKIXNameConstraintValidator.max(minMax[0], minMax[2]);
        if (PKIXNameConstraintValidator.compareTo(min, max) == 1) {
            return Collections.EMPTY_SET;
        }
        byte[] ip = PKIXNameConstraintValidator.or(minMax[0], minMax[2]);
        byte[] subnetmask = PKIXNameConstraintValidator.or(subnetmask1, subnetmask2);
        return Collections.singleton(this.ipWithSubnetMask(ip, subnetmask));
    }

    private byte[] ipWithSubnetMask(byte[] ip, byte[] subnetMask) {
        int ipLength = ip.length;
        byte[] temp = new byte[ipLength * 2];
        System.arraycopy(ip, 0, temp, 0, ipLength);
        System.arraycopy(subnetMask, 0, temp, ipLength, ipLength);
        return temp;
    }

    private byte[][] extractIPsAndSubnetMasks(byte[] ipWithSubmask1, byte[] ipWithSubmask2) {
        int ipLength = ipWithSubmask1.length / 2;
        byte[] ip1 = new byte[ipLength];
        byte[] subnetmask1 = new byte[ipLength];
        System.arraycopy(ipWithSubmask1, 0, ip1, 0, ipLength);
        System.arraycopy(ipWithSubmask1, ipLength, subnetmask1, 0, ipLength);
        byte[] ip2 = new byte[ipLength];
        byte[] subnetmask2 = new byte[ipLength];
        System.arraycopy(ipWithSubmask2, 0, ip2, 0, ipLength);
        System.arraycopy(ipWithSubmask2, ipLength, subnetmask2, 0, ipLength);
        return new byte[][]{ip1, subnetmask1, ip2, subnetmask2};
    }

    private byte[][] minMaxIPs(byte[] ip1, byte[] subnetmask1, byte[] ip2, byte[] subnetmask2) {
        int ipLength = ip1.length;
        byte[] min1 = new byte[ipLength];
        byte[] max1 = new byte[ipLength];
        byte[] min2 = new byte[ipLength];
        byte[] max2 = new byte[ipLength];
        for (int i = 0; i < ipLength; ++i) {
            min1[i] = (byte)(ip1[i] & subnetmask1[i]);
            max1[i] = (byte)(ip1[i] & subnetmask1[i] | ~subnetmask1[i]);
            min2[i] = (byte)(ip2[i] & subnetmask2[i]);
            max2[i] = (byte)(ip2[i] & subnetmask2[i] | ~subnetmask2[i]);
        }
        return new byte[][]{min1, max1, min2, max2};
    }

    private void checkPermittedEmail(Set permitted, String email) throws NameConstraintValidatorException {
        if (permitted == null) {
            return;
        }
        for (String str : permitted) {
            if (!this.emailIsConstrained(email, str)) continue;
            return;
        }
        if (email.length() == 0 && permitted.size() == 0) {
            return;
        }
        throw new NameConstraintValidatorException("Subject email address is not from a permitted subtree.");
    }

    private void checkPermittedOtherName(Set permitted, OtherName name) throws NameConstraintValidatorException {
        if (permitted == null) {
            return;
        }
        Iterator it = permitted.iterator();
        while (it.hasNext()) {
            OtherName str = OtherName.getInstance(it.next());
            if (!this.otherNameIsConstrained(name, str)) continue;
            return;
        }
        throw new NameConstraintValidatorException("Subject OtherName is not from a permitted subtree.");
    }

    private void checkExcludedOtherName(Set excluded, OtherName name) throws NameConstraintValidatorException {
        if (excluded.isEmpty()) {
            return;
        }
        Iterator it = excluded.iterator();
        while (it.hasNext()) {
            OtherName str = OtherName.getInstance(it.next());
            if (!this.otherNameIsConstrained(name, str)) continue;
            throw new NameConstraintValidatorException("OtherName is from an excluded subtree.");
        }
    }

    private void checkExcludedEmail(Set excluded, String email) throws NameConstraintValidatorException {
        if (excluded.isEmpty()) {
            return;
        }
        for (String str : excluded) {
            if (!this.emailIsConstrained(email, str)) continue;
            throw new NameConstraintValidatorException("Email address is from an excluded subtree.");
        }
    }

    private void checkPermittedIP(Set permitted, byte[] ip) throws NameConstraintValidatorException {
        if (permitted == null) {
            return;
        }
        for (byte[] ipWithSubnet : permitted) {
            if (!this.isIPConstrained(ip, ipWithSubnet)) continue;
            return;
        }
        if (ip.length == 0 && permitted.size() == 0) {
            return;
        }
        throw new NameConstraintValidatorException("IP is not from a permitted subtree.");
    }

    private void checkExcludedIP(Set excluded, byte[] ip) throws NameConstraintValidatorException {
        if (excluded.isEmpty()) {
            return;
        }
        for (byte[] ipWithSubnet : excluded) {
            if (!this.isIPConstrained(ip, ipWithSubnet)) continue;
            throw new NameConstraintValidatorException("IP is from an excluded subtree.");
        }
    }

    private boolean isIPConstrained(byte[] ip, byte[] constraint) {
        int ipLength = ip.length;
        if (ipLength != constraint.length / 2) {
            return false;
        }
        byte[] subnetMask = new byte[ipLength];
        System.arraycopy(constraint, ipLength, subnetMask, 0, ipLength);
        byte[] permittedSubnetAddress = new byte[ipLength];
        byte[] ipSubnetAddress = new byte[ipLength];
        for (int i = 0; i < ipLength; ++i) {
            permittedSubnetAddress[i] = (byte)(constraint[i] & subnetMask[i]);
            ipSubnetAddress[i] = (byte)(ip[i] & subnetMask[i]);
        }
        return Arrays.areEqual(permittedSubnetAddress, ipSubnetAddress);
    }

    private boolean otherNameIsConstrained(OtherName name, OtherName constraint) {
        return constraint.equals(name);
    }

    private boolean emailIsConstrained(String email, String constraint) {
        String sub = email.substring(email.indexOf(64) + 1);
        if (constraint.indexOf(64) != -1) {
            if (email.equalsIgnoreCase(constraint)) {
                return true;
            }
            if (sub.equalsIgnoreCase(constraint.substring(1))) {
                return true;
            }
        } else if (constraint.charAt(0) != '.' ? sub.equalsIgnoreCase(constraint) : this.withinDomain(sub, constraint)) {
            return true;
        }
        return false;
    }

    private boolean withinDomain(String testDomain, String domain) {
        String tempDomain = domain;
        if (tempDomain.startsWith(".")) {
            tempDomain = tempDomain.substring(1);
        }
        String[] domainParts = Strings.split(tempDomain, '.');
        String[] testDomainParts = Strings.split(testDomain, '.');
        if (testDomainParts.length <= domainParts.length) {
            return false;
        }
        int d = testDomainParts.length - domainParts.length;
        for (int i = -1; i < domainParts.length; ++i) {
            if (!(i == -1 ? testDomainParts[i + d].equals("") : !domainParts[i].equalsIgnoreCase(testDomainParts[i + d]))) continue;
            return false;
        }
        return true;
    }

    private void checkPermittedDNS(Set permitted, String dns) throws NameConstraintValidatorException {
        if (permitted == null) {
            return;
        }
        for (String str : permitted) {
            if (!this.withinDomain(dns, str) && !dns.equalsIgnoreCase(str)) continue;
            return;
        }
        if (dns.length() == 0 && permitted.size() == 0) {
            return;
        }
        throw new NameConstraintValidatorException("DNS is not from a permitted subtree.");
    }

    private void checkExcludedDNS(Set excluded, String dns) throws NameConstraintValidatorException {
        if (excluded.isEmpty()) {
            return;
        }
        for (String str : excluded) {
            if (!this.withinDomain(dns, str) && !dns.equalsIgnoreCase(str)) continue;
            throw new NameConstraintValidatorException("DNS is from an excluded subtree.");
        }
    }

    private void unionEmail(String email1, String email2, Set union) {
        if (email1.indexOf(64) != -1) {
            String _sub = email1.substring(email1.indexOf(64) + 1);
            if (email2.indexOf(64) != -1) {
                if (email1.equalsIgnoreCase(email2)) {
                    union.add(email1);
                } else {
                    union.add(email1);
                    union.add(email2);
                }
            } else if (email2.startsWith(".")) {
                if (this.withinDomain(_sub, email2)) {
                    union.add(email2);
                } else {
                    union.add(email1);
                    union.add(email2);
                }
            } else if (_sub.equalsIgnoreCase(email2)) {
                union.add(email2);
            } else {
                union.add(email1);
                union.add(email2);
            }
        } else if (email1.startsWith(".")) {
            if (email2.indexOf(64) != -1) {
                String _sub = email2.substring(email1.indexOf(64) + 1);
                if (this.withinDomain(_sub, email1)) {
                    union.add(email1);
                } else {
                    union.add(email1);
                    union.add(email2);
                }
            } else if (email2.startsWith(".")) {
                if (this.withinDomain(email1, email2) || email1.equalsIgnoreCase(email2)) {
                    union.add(email2);
                } else if (this.withinDomain(email2, email1)) {
                    union.add(email1);
                } else {
                    union.add(email1);
                    union.add(email2);
                }
            } else if (this.withinDomain(email2, email1)) {
                union.add(email1);
            } else {
                union.add(email1);
                union.add(email2);
            }
        } else if (email2.indexOf(64) != -1) {
            String _sub = email2.substring(email1.indexOf(64) + 1);
            if (_sub.equalsIgnoreCase(email1)) {
                union.add(email1);
            } else {
                union.add(email1);
                union.add(email2);
            }
        } else if (email2.startsWith(".")) {
            if (this.withinDomain(email1, email2)) {
                union.add(email2);
            } else {
                union.add(email1);
                union.add(email2);
            }
        } else if (email1.equalsIgnoreCase(email2)) {
            union.add(email1);
        } else {
            union.add(email1);
            union.add(email2);
        }
    }

    private void unionURI(String email1, String email2, Set union) {
        if (email1.indexOf(64) != -1) {
            String _sub = email1.substring(email1.indexOf(64) + 1);
            if (email2.indexOf(64) != -1) {
                if (email1.equalsIgnoreCase(email2)) {
                    union.add(email1);
                } else {
                    union.add(email1);
                    union.add(email2);
                }
            } else if (email2.startsWith(".")) {
                if (this.withinDomain(_sub, email2)) {
                    union.add(email2);
                } else {
                    union.add(email1);
                    union.add(email2);
                }
            } else if (_sub.equalsIgnoreCase(email2)) {
                union.add(email2);
            } else {
                union.add(email1);
                union.add(email2);
            }
        } else if (email1.startsWith(".")) {
            if (email2.indexOf(64) != -1) {
                String _sub = email2.substring(email1.indexOf(64) + 1);
                if (this.withinDomain(_sub, email1)) {
                    union.add(email1);
                } else {
                    union.add(email1);
                    union.add(email2);
                }
            } else if (email2.startsWith(".")) {
                if (this.withinDomain(email1, email2) || email1.equalsIgnoreCase(email2)) {
                    union.add(email2);
                } else if (this.withinDomain(email2, email1)) {
                    union.add(email1);
                } else {
                    union.add(email1);
                    union.add(email2);
                }
            } else if (this.withinDomain(email2, email1)) {
                union.add(email1);
            } else {
                union.add(email1);
                union.add(email2);
            }
        } else if (email2.indexOf(64) != -1) {
            String _sub = email2.substring(email1.indexOf(64) + 1);
            if (_sub.equalsIgnoreCase(email1)) {
                union.add(email1);
            } else {
                union.add(email1);
                union.add(email2);
            }
        } else if (email2.startsWith(".")) {
            if (this.withinDomain(email1, email2)) {
                union.add(email2);
            } else {
                union.add(email1);
                union.add(email2);
            }
        } else if (email1.equalsIgnoreCase(email2)) {
            union.add(email1);
        } else {
            union.add(email1);
            union.add(email2);
        }
    }

    private Set intersectDNS(Set permitted, Set dnss) {
        HashSet<String> intersect = new HashSet<String>();
        Iterator it = dnss.iterator();
        while (it.hasNext()) {
            String dns = this.extractNameAsString(((GeneralSubtree)it.next()).getBase());
            if (permitted == null) {
                if (dns == null) continue;
                intersect.add(dns);
                continue;
            }
            for (String _permitted : permitted) {
                if (this.withinDomain(_permitted, dns)) {
                    intersect.add(_permitted);
                    continue;
                }
                if (!this.withinDomain(dns, _permitted)) continue;
                intersect.add(dns);
            }
        }
        return intersect;
    }

    private Set unionDNS(Set excluded, String dns) {
        if (excluded.isEmpty()) {
            if (dns == null) {
                return excluded;
            }
            excluded.add(dns);
            return excluded;
        }
        HashSet<String> union = new HashSet<String>();
        for (String _permitted : excluded) {
            if (this.withinDomain(_permitted, dns)) {
                union.add(dns);
                continue;
            }
            if (this.withinDomain(dns, _permitted)) {
                union.add(_permitted);
                continue;
            }
            union.add(_permitted);
            union.add(dns);
        }
        return union;
    }

    private void intersectEmail(String email1, String email2, Set intersect) {
        if (email1.indexOf(64) != -1) {
            String _sub = email1.substring(email1.indexOf(64) + 1);
            if (email2.indexOf(64) != -1) {
                if (email1.equalsIgnoreCase(email2)) {
                    intersect.add(email1);
                }
            } else if (email2.startsWith(".")) {
                if (this.withinDomain(_sub, email2)) {
                    intersect.add(email1);
                }
            } else if (_sub.equalsIgnoreCase(email2)) {
                intersect.add(email1);
            }
        } else if (email1.startsWith(".")) {
            if (email2.indexOf(64) != -1) {
                String _sub = email2.substring(email1.indexOf(64) + 1);
                if (this.withinDomain(_sub, email1)) {
                    intersect.add(email2);
                }
            } else if (email2.startsWith(".")) {
                if (this.withinDomain(email1, email2) || email1.equalsIgnoreCase(email2)) {
                    intersect.add(email1);
                } else if (this.withinDomain(email2, email1)) {
                    intersect.add(email2);
                }
            } else if (this.withinDomain(email2, email1)) {
                intersect.add(email2);
            }
        } else if (email2.indexOf(64) != -1) {
            String _sub = email2.substring(email2.indexOf(64) + 1);
            if (_sub.equalsIgnoreCase(email1)) {
                intersect.add(email2);
            }
        } else if (email2.startsWith(".")) {
            if (this.withinDomain(email1, email2)) {
                intersect.add(email1);
            }
        } else if (email1.equalsIgnoreCase(email2)) {
            intersect.add(email1);
        }
    }

    private void checkExcludedURI(Set excluded, String uri) throws NameConstraintValidatorException {
        if (excluded.isEmpty()) {
            return;
        }
        for (String str : excluded) {
            if (!this.isUriConstrained(uri, str)) continue;
            throw new NameConstraintValidatorException("URI is from an excluded subtree.");
        }
    }

    private Set intersectURI(Set permitted, Set uris) {
        HashSet<String> intersect = new HashSet<String>();
        Iterator it = uris.iterator();
        while (it.hasNext()) {
            String uri = this.extractNameAsString(((GeneralSubtree)it.next()).getBase());
            if (permitted == null) {
                if (uri == null) continue;
                intersect.add(uri);
                continue;
            }
            for (String _permitted : permitted) {
                this.intersectURI(_permitted, uri, intersect);
            }
        }
        return intersect;
    }

    private Set unionURI(Set excluded, String uri) {
        if (excluded.isEmpty()) {
            if (uri == null) {
                return excluded;
            }
            excluded.add(uri);
            return excluded;
        }
        HashSet union = new HashSet();
        for (String _excluded : excluded) {
            this.unionURI(_excluded, uri, union);
        }
        return union;
    }

    private void intersectURI(String email1, String email2, Set intersect) {
        if (email1.indexOf(64) != -1) {
            String _sub = email1.substring(email1.indexOf(64) + 1);
            if (email2.indexOf(64) != -1) {
                if (email1.equalsIgnoreCase(email2)) {
                    intersect.add(email1);
                }
            } else if (email2.startsWith(".")) {
                if (this.withinDomain(_sub, email2)) {
                    intersect.add(email1);
                }
            } else if (_sub.equalsIgnoreCase(email2)) {
                intersect.add(email1);
            }
        } else if (email1.startsWith(".")) {
            if (email2.indexOf(64) != -1) {
                String _sub = email2.substring(email1.indexOf(64) + 1);
                if (this.withinDomain(_sub, email1)) {
                    intersect.add(email2);
                }
            } else if (email2.startsWith(".")) {
                if (this.withinDomain(email1, email2) || email1.equalsIgnoreCase(email2)) {
                    intersect.add(email1);
                } else if (this.withinDomain(email2, email1)) {
                    intersect.add(email2);
                }
            } else if (this.withinDomain(email2, email1)) {
                intersect.add(email2);
            }
        } else if (email2.indexOf(64) != -1) {
            String _sub = email2.substring(email2.indexOf(64) + 1);
            if (_sub.equalsIgnoreCase(email1)) {
                intersect.add(email2);
            }
        } else if (email2.startsWith(".")) {
            if (this.withinDomain(email1, email2)) {
                intersect.add(email1);
            }
        } else if (email1.equalsIgnoreCase(email2)) {
            intersect.add(email1);
        }
    }

    private void checkPermittedURI(Set permitted, String uri) throws NameConstraintValidatorException {
        if (permitted == null) {
            return;
        }
        for (String str : permitted) {
            if (!this.isUriConstrained(uri, str)) continue;
            return;
        }
        if (uri.length() == 0 && permitted.size() == 0) {
            return;
        }
        throw new NameConstraintValidatorException("URI is not from a permitted subtree.");
    }

    private boolean isUriConstrained(String uri, String constraint) {
        String host = PKIXNameConstraintValidator.extractHostFromURL(uri);
        return !constraint.startsWith(".") ? host.equalsIgnoreCase(constraint) : this.withinDomain(host, constraint);
    }

    private static String extractHostFromURL(String url) {
        String sub = url.substring(url.indexOf(58) + 1);
        if (sub.indexOf("//") != -1) {
            sub = sub.substring(sub.indexOf("//") + 2);
        }
        if (sub.lastIndexOf(58) != -1) {
            sub = sub.substring(0, sub.lastIndexOf(58));
        }
        sub = sub.substring(sub.indexOf(58) + 1);
        if ((sub = sub.substring(sub.indexOf(64) + 1)).indexOf(47) != -1) {
            sub = sub.substring(0, sub.indexOf(47));
        }
        return sub;
    }

    private String extractNameAsString(GeneralName name) {
        return ASN1IA5String.getInstance(name.getName()).getString();
    }

    private static byte[] max(byte[] ip1, byte[] ip2) {
        for (int i = 0; i < ip1.length; ++i) {
            if ((ip1[i] & 0xFFFF) <= (ip2[i] & 0xFFFF)) continue;
            return ip1;
        }
        return ip2;
    }

    private static byte[] min(byte[] ip1, byte[] ip2) {
        for (int i = 0; i < ip1.length; ++i) {
            if ((ip1[i] & 0xFFFF) >= (ip2[i] & 0xFFFF)) continue;
            return ip1;
        }
        return ip2;
    }

    private static int compareTo(byte[] ip1, byte[] ip2) {
        if (Arrays.areEqual(ip1, ip2)) {
            return 0;
        }
        if (Arrays.areEqual(PKIXNameConstraintValidator.max(ip1, ip2), ip1)) {
            return 1;
        }
        return -1;
    }

    private static byte[] or(byte[] ip1, byte[] ip2) {
        byte[] temp = new byte[ip1.length];
        for (int i = 0; i < ip1.length; ++i) {
            temp[i] = (byte)(ip1[i] | ip2[i]);
        }
        return temp;
    }

    private int hashCollection(Collection coll) {
        if (coll == null) {
            return 0;
        }
        int hash = 0;
        for (Object o : coll) {
            if (o instanceof byte[]) {
                hash += Arrays.hashCode((byte[])o);
                continue;
            }
            hash += o.hashCode();
        }
        return hash;
    }

    private boolean collectionsAreEqual(Collection coll1, Collection coll2) {
        if (coll1 == coll2) {
            return true;
        }
        if (coll1 == null || coll2 == null) {
            return false;
        }
        if (coll1.size() != coll2.size()) {
            return false;
        }
        for (Object a : coll1) {
            Iterator it2 = coll2.iterator();
            boolean found = false;
            while (it2.hasNext()) {
                Object b = it2.next();
                if (!this.equals(a, b)) continue;
                found = true;
                break;
            }
            if (found) continue;
            return false;
        }
        return true;
    }

    private boolean equals(Object o1, Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null || o2 == null) {
            return false;
        }
        if (o1 instanceof byte[] && o2 instanceof byte[]) {
            return Arrays.areEqual((byte[])o1, (byte[])o2);
        }
        return o1.equals(o2);
    }

    private String stringifyIP(byte[] ip) {
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < ip.length / 2; ++i) {
            if (temp.length() > 0) {
                temp.append(".");
            }
            temp.append(Integer.toString(ip[i] & 0xFF));
        }
        temp.append("/");
        boolean first = true;
        for (int i = ip.length / 2; i < ip.length; ++i) {
            if (first) {
                first = false;
            } else {
                temp.append(".");
            }
            temp.append(Integer.toString(ip[i] & 0xFF));
        }
        return temp.toString();
    }

    private String stringifyIPCollection(Set ips) {
        StringBuilder temp = new StringBuilder();
        temp.append("[");
        Iterator it = ips.iterator();
        while (it.hasNext()) {
            if (temp.length() > 1) {
                temp.append(",");
            }
            temp.append(this.stringifyIP((byte[])it.next()));
        }
        temp.append("]");
        return temp.toString();
    }

    private String stringifyOtherNameCollection(Set otherNames) {
        StringBuilder temp = new StringBuilder();
        temp.append("[");
        Iterator it = otherNames.iterator();
        while (it.hasNext()) {
            if (temp.length() > 1) {
                temp.append(",");
            }
            OtherName name = OtherName.getInstance(it.next());
            temp.append(name.getTypeID().getId());
            temp.append(":");
            try {
                temp.append(Hex.toHexString(name.getValue().toASN1Primitive().getEncoded()));
            }
            catch (IOException e) {
                temp.append(e.toString());
            }
        }
        temp.append("]");
        return temp.toString();
    }

    private final void addLine(StringBuilder sb, String str) {
        sb.append(str).append(Strings.lineSeparator());
    }

    public String toString() {
        StringBuilder temp = new StringBuilder();
        this.addLine(temp, "permitted:");
        if (this.permittedSubtreesDN != null) {
            this.addLine(temp, "DN:");
            this.addLine(temp, this.permittedSubtreesDN.toString());
        }
        if (this.permittedSubtreesDNS != null) {
            this.addLine(temp, "DNS:");
            this.addLine(temp, this.permittedSubtreesDNS.toString());
        }
        if (this.permittedSubtreesEmail != null) {
            this.addLine(temp, "Email:");
            this.addLine(temp, this.permittedSubtreesEmail.toString());
        }
        if (this.permittedSubtreesURI != null) {
            this.addLine(temp, "URI:");
            this.addLine(temp, this.permittedSubtreesURI.toString());
        }
        if (this.permittedSubtreesIP != null) {
            this.addLine(temp, "IP:");
            this.addLine(temp, this.stringifyIPCollection(this.permittedSubtreesIP));
        }
        if (this.permittedSubtreesOtherName != null) {
            this.addLine(temp, "OtherName:");
            this.addLine(temp, this.stringifyOtherNameCollection(this.permittedSubtreesOtherName));
        }
        this.addLine(temp, "excluded:");
        if (!this.excludedSubtreesDN.isEmpty()) {
            this.addLine(temp, "DN:");
            this.addLine(temp, this.excludedSubtreesDN.toString());
        }
        if (!this.excludedSubtreesDNS.isEmpty()) {
            this.addLine(temp, "DNS:");
            this.addLine(temp, this.excludedSubtreesDNS.toString());
        }
        if (!this.excludedSubtreesEmail.isEmpty()) {
            this.addLine(temp, "Email:");
            this.addLine(temp, this.excludedSubtreesEmail.toString());
        }
        if (!this.excludedSubtreesURI.isEmpty()) {
            this.addLine(temp, "URI:");
            this.addLine(temp, this.excludedSubtreesURI.toString());
        }
        if (!this.excludedSubtreesIP.isEmpty()) {
            this.addLine(temp, "IP:");
            this.addLine(temp, this.stringifyIPCollection(this.excludedSubtreesIP));
        }
        if (!this.excludedSubtreesOtherName.isEmpty()) {
            this.addLine(temp, "OtherName:");
            this.addLine(temp, this.stringifyOtherNameCollection(this.excludedSubtreesOtherName));
        }
        return temp.toString();
    }
}

