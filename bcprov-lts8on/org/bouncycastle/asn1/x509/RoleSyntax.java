/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;

public class RoleSyntax
extends ASN1Object {
    private GeneralNames roleAuthority;
    private GeneralName roleName;

    public static RoleSyntax getInstance(Object obj) {
        if (obj instanceof RoleSyntax) {
            return (RoleSyntax)obj;
        }
        if (obj != null) {
            return new RoleSyntax(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public RoleSyntax(GeneralNames roleAuthority, GeneralName roleName) {
        if (roleName == null || roleName.getTagNo() != 6 || ((ASN1String)((Object)roleName.getName())).getString().equals("")) {
            throw new IllegalArgumentException("the role name MUST be non empty and MUST use the URI option of GeneralName");
        }
        this.roleAuthority = roleAuthority;
        this.roleName = roleName;
    }

    public RoleSyntax(GeneralName roleName) {
        this(null, roleName);
    }

    public RoleSyntax(String roleName) {
        this(new GeneralName(6, roleName == null ? "" : roleName));
    }

    private RoleSyntax(ASN1Sequence seq) {
        if (seq.size() < 1 || seq.size() > 2) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        block4: for (int i = 0; i != seq.size(); ++i) {
            ASN1TaggedObject taggedObject = ASN1TaggedObject.getInstance(seq.getObjectAt(i));
            switch (taggedObject.getTagNo()) {
                case 0: {
                    this.roleAuthority = GeneralNames.getInstance(taggedObject, false);
                    continue block4;
                }
                case 1: {
                    this.roleName = GeneralName.getInstance(taggedObject, true);
                    continue block4;
                }
                default: {
                    throw new IllegalArgumentException("Unknown tag in RoleSyntax");
                }
            }
        }
    }

    public GeneralNames getRoleAuthority() {
        return this.roleAuthority;
    }

    public GeneralName getRoleName() {
        return this.roleName;
    }

    public String getRoleNameAsString() {
        ASN1String str = (ASN1String)((Object)this.roleName.getName());
        return str.getString();
    }

    public String[] getRoleAuthorityAsString() {
        if (this.roleAuthority == null) {
            return new String[0];
        }
        GeneralName[] names = this.roleAuthority.getNames();
        String[] namesString = new String[names.length];
        for (int i = 0; i < names.length; ++i) {
            ASN1Encodable value = names[i].getName();
            namesString[i] = value instanceof ASN1String ? ((ASN1String)((Object)value)).getString() : value.toString();
        }
        return namesString;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        if (this.roleAuthority != null) {
            v.add(new DERTaggedObject(false, 0, (ASN1Encodable)this.roleAuthority));
        }
        v.add(new DERTaggedObject(true, 1, (ASN1Encodable)this.roleName));
        return new DERSequence(v);
    }

    public String toString() {
        StringBuffer buff = new StringBuffer("Name: " + this.getRoleNameAsString() + " - Auth: ");
        if (this.roleAuthority == null || this.roleAuthority.getNames().length == 0) {
            buff.append("N/A");
        } else {
            String[] names = this.getRoleAuthorityAsString();
            buff.append('[').append(names[0]);
            for (int i = 1; i < names.length; ++i) {
                buff.append(", ").append(names[i]);
            }
            buff.append(']');
        }
        return buff.toString();
    }
}

