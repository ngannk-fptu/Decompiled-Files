/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1IA5String
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.x509.Attribute
 *  org.bouncycastle.asn1.x509.IetfAttrSyntax
 */
package org.bouncycastle.voms;

import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.Attribute;
import org.bouncycastle.asn1.x509.IetfAttrSyntax;
import org.bouncycastle.cert.X509AttributeCertificateHolder;

public class VOMSAttribute {
    public static final String VOMS_ATTR_OID = "1.3.6.1.4.1.8005.100.100.4";
    private X509AttributeCertificateHolder myAC;
    private String myHostPort;
    private String myVo;
    private List myStringList = new ArrayList();
    private List myFQANs = new ArrayList();

    public VOMSAttribute(X509AttributeCertificateHolder ac) {
        if (ac == null) {
            throw new IllegalArgumentException("VOMSAttribute: AttributeCertificate is NULL");
        }
        this.myAC = ac;
        Attribute[] l = ac.getAttributes(new ASN1ObjectIdentifier(VOMS_ATTR_OID));
        if (l == null) {
            return;
        }
        try {
            for (int i = 0; i != l.length; ++i) {
                IetfAttrSyntax attr = IetfAttrSyntax.getInstance((Object)l[i].getAttributeValues()[0]);
                String url = ((ASN1IA5String)attr.getPolicyAuthority().getNames()[0].getName()).getString();
                int idx = url.indexOf("://");
                if (idx < 0 || idx == url.length() - 1) {
                    throw new IllegalArgumentException("Bad encoding of VOMS policyAuthority : [" + url + "]");
                }
                this.myVo = url.substring(0, idx);
                this.myHostPort = url.substring(idx + 3);
                if (attr.getValueType() != 1) {
                    throw new IllegalArgumentException("VOMS attribute values are not encoded as octet strings, policyAuthority = " + url);
                }
                ASN1OctetString[] values = (ASN1OctetString[])attr.getValues();
                for (int j = 0; j != values.length; ++j) {
                    String fqan = new String(values[j].getOctets());
                    FQAN f = new FQAN(fqan);
                    if (this.myStringList.contains(fqan) || !fqan.startsWith("/" + this.myVo + "/")) continue;
                    this.myStringList.add(fqan);
                    this.myFQANs.add(f);
                }
            }
        }
        catch (IllegalArgumentException ie) {
            throw ie;
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Badly encoded VOMS extension in AC issued by " + ac.getIssuer());
        }
    }

    public X509AttributeCertificateHolder getAC() {
        return this.myAC;
    }

    public List getFullyQualifiedAttributes() {
        return this.myStringList;
    }

    public List getListOfFQAN() {
        return this.myFQANs;
    }

    public String getHostPort() {
        return this.myHostPort;
    }

    public String getVO() {
        return this.myVo;
    }

    public String toString() {
        return "VO      :" + this.myVo + "\nHostPort:" + this.myHostPort + "\nFQANs   :" + this.myFQANs;
    }

    public static class FQAN {
        String fqan;
        String group;
        String role;
        String capability;

        public FQAN(String fqan) {
            this.fqan = fqan;
        }

        public FQAN(String group, String role, String capability) {
            this.group = group;
            this.role = role;
            this.capability = capability;
        }

        public String getFQAN() {
            if (this.fqan != null) {
                return this.fqan;
            }
            this.fqan = this.group + "/Role=" + (this.role != null ? this.role : "") + (this.capability != null ? "/Capability=" + this.capability : "");
            return this.fqan;
        }

        protected void split() {
            int len = this.fqan.length();
            int i = this.fqan.indexOf("/Role=");
            if (i < 0) {
                return;
            }
            this.group = this.fqan.substring(0, i);
            int j = this.fqan.indexOf("/Capability=", i + 6);
            String s = j < 0 ? this.fqan.substring(i + 6) : this.fqan.substring(i + 6, j);
            this.role = s.length() == 0 ? null : s;
            s = j < 0 ? null : this.fqan.substring(j + 12);
            this.capability = s == null || s.length() == 0 ? null : s;
        }

        public String getGroup() {
            if (this.group == null && this.fqan != null) {
                this.split();
            }
            return this.group;
        }

        public String getRole() {
            if (this.group == null && this.fqan != null) {
                this.split();
            }
            return this.role;
        }

        public String getCapability() {
            if (this.group == null && this.fqan != null) {
                this.split();
            }
            return this.capability;
        }

        public String toString() {
            return this.getFQAN();
        }
    }
}

