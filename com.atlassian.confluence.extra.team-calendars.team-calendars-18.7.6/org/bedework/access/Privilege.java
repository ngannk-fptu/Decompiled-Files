/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.access;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.bedework.access.AccessException;
import org.bedework.access.EncodedAcl;
import org.bedework.access.PrivilegeDefs;

public class Privilege
implements PrivilegeDefs {
    private String name;
    private String description;
    private boolean abstractPriv;
    private boolean denial;
    private int index;
    private char encoding;
    private ArrayList<Privilege> containedPrivileges = new ArrayList();

    public Privilege(String name, String description, boolean abstractPriv, boolean denial, int index) {
        this.name = name;
        this.description = description;
        this.abstractPriv = abstractPriv;
        this.denial = denial;
        this.setIndex(index);
    }

    public Privilege(String name, String description, int index) {
        this(name, description, false, false, index);
    }

    public Privilege(String name, String description, boolean denial, int index) {
        this(name, description, false, denial, index);
    }

    public Privilege(String name, String description, boolean denial, int index, Privilege[] contained) {
        this(name, description, false, denial, index);
        for (Privilege p : contained) {
            this.containedPrivileges.add(p);
        }
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean getAbstractPriv() {
        return this.abstractPriv;
    }

    public boolean getDenial() {
        return this.denial;
    }

    public int getIndex() {
        return this.index;
    }

    public Collection<Privilege> getContainedPrivileges() {
        return Collections.unmodifiableCollection(this.containedPrivileges);
    }

    public static Privilege findPriv(Privilege allowedRoot, Privilege deniedRoot, EncodedAcl acl) throws AccessException {
        if (acl.remaining() < 2) {
            return null;
        }
        Privilege p = Privilege.matchDenied(acl) ? Privilege.matchEncoding(deniedRoot, acl) : Privilege.matchEncoding(allowedRoot, acl);
        if (p == null) {
            acl.back();
        }
        return p;
    }

    private static boolean matchDenied(EncodedAcl acl) throws AccessException {
        char c = acl.getChar();
        if (c == 'n' || c == '2') {
            return true;
        }
        if (c == 'y' || c == '3') {
            return false;
        }
        throw AccessException.badACE("privilege flag=" + c + " " + acl.getErrorInfo());
    }

    private static Privilege matchEncoding(Privilege subRoot, EncodedAcl acl) throws AccessException {
        if (acl.remaining() < 1) {
            return null;
        }
        char c = acl.getChar();
        if (subRoot.encoding == c) {
            return subRoot;
        }
        acl.back();
        for (Privilege cp : subRoot.getContainedPrivileges()) {
            Privilege p = Privilege.matchEncoding(cp, acl);
            if (p == null) continue;
            return p;
        }
        return null;
    }

    public void encode(EncodedAcl acl) throws AccessException {
        if (this.denial) {
            acl.addChar('n');
        } else {
            acl.addChar('y');
        }
        acl.addChar(this.encoding);
    }

    public static Privilege cloneDenied(Privilege val) {
        Privilege newval = new Privilege(val.getName(), val.getDescription(), val.getAbstractPriv(), true, val.getIndex());
        for (Privilege p : val.getContainedPrivileges()) {
            newval.containedPrivileges.add(Privilege.cloneDenied(p));
        }
        return newval;
    }

    private void setIndex(int val) {
        this.index = val;
        this.encoding = privEncoding[this.index];
    }

    public String toUserString() {
        StringBuilder sb = new StringBuilder();
        if (this.getDenial()) {
            sb.append("NOT ");
        }
        sb.append(this.getName());
        return sb.toString();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Privilege{name=");
        sb.append(this.name);
        sb.append(", description=");
        sb.append(this.description);
        sb.append(", abstractPriv=");
        sb.append(this.abstractPriv);
        sb.append(", denial=");
        sb.append(this.denial);
        sb.append(", index=");
        sb.append(this.index);
        if (!this.containedPrivileges.isEmpty()) {
            sb.append(",\n   contains ");
            boolean first = true;
            for (Privilege p : this.containedPrivileges) {
                if (!first) {
                    sb.append(", ");
                }
                first = false;
                sb.append(p.getName());
            }
        }
        sb.append("}");
        return sb.toString();
    }

    public Object clone() {
        return new Privilege(this.getName(), this.getDescription(), this.getAbstractPriv(), this.getDenial(), this.getIndex());
    }
}

