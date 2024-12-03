/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.access;

import org.bedework.access.Access;
import org.bedework.access.AccessException;
import org.bedework.access.EncodedAcl;
import org.bedework.access.WhoDefs;
import org.bedework.util.caching.ObjectPool;

public final class AceWho
implements WhoDefs,
Comparable<AceWho> {
    private String who;
    private int whoType;
    private boolean notWho;
    private static ObjectPool<String> whos = new ObjectPool();
    private static ObjectPool<AceWho> aceWhos = new ObjectPool();
    private static boolean poolAceWhos = true;
    public static final AceWho all = AceWho.getAceWho(null, 10, false);
    public static final AceWho owner = AceWho.getAceWho(null, 0, false);
    public static final AceWho other = AceWho.getAceWho(null, 9, false);
    public static final AceWho unauthenticated = AceWho.getAceWho(null, 7, false);

    public static AceWho getAceWho(String who, int whoType, boolean notWho) {
        if (poolAceWhos) {
            return aceWhos.get(new AceWho(who, whoType, notWho));
        }
        return new AceWho(who, whoType, notWho);
    }

    private AceWho() {
    }

    private AceWho(String who, int whoType, boolean notWho) {
        this.who = whos.get(who);
        this.notWho = notWho;
        this.whoType = whoType;
    }

    public String getWho() {
        return this.who;
    }

    public boolean getNotWho() {
        return this.notWho;
    }

    public int getWhoType() {
        return this.whoType;
    }

    public boolean whoMatch(Access.AccessCb cb, String pref) throws AccessException {
        if (pref == null && this.getWho() == null) {
            return !this.getNotWho();
        }
        if (pref == null || this.getWho() == null) {
            return this.getNotWho();
        }
        boolean match = pref.equals(cb.makeHref(this.getWho(), this.whoType));
        if (this.getNotWho()) {
            match = !match;
        }
        return match;
    }

    public void encode(EncodedAcl acl) throws AccessException {
        if (this.notWho) {
            acl.addChar('N');
        } else {
            acl.addChar('W');
        }
        acl.addChar(whoTypeFlags[this.whoType]);
        acl.encodeString(this.who);
    }

    public static void skip(EncodedAcl acl) throws AccessException {
        acl.getChar();
        acl.getChar();
        acl.skipString();
    }

    public static AceWho decode(EncodedAcl acl) throws AccessException {
        int whoType;
        boolean notWho;
        block6: {
            char c = acl.getChar();
            if (c == 'N') {
                notWho = true;
            } else if (c == 'W') {
                notWho = false;
            } else {
                throw AccessException.badACE("who/notWho flag");
            }
            c = acl.getChar();
            for (whoType = 0; whoType < whoTypeFlags.length; ++whoType) {
                if (c != whoTypeFlags[whoType]) {
                    continue;
                }
                break block6;
            }
            throw AccessException.badACE("who type");
        }
        return AceWho.getAceWho(acl.getString(), whoType, notWho);
    }

    public String toUserString() {
        StringBuffer sb = new StringBuffer();
        sb.append(whoTypeNames[this.whoType]);
        if (this.notWho) {
            sb.append("NOT ");
        }
        sb.append(whoTypeNames[this.whoType]);
        if (whoTypeNamed[this.whoType]) {
            sb.append("=");
            sb.append(this.getWho());
        }
        return sb.toString();
    }

    @Override
    public int compareTo(AceWho that) {
        if (this == that) {
            return 0;
        }
        if (this.notWho != that.notWho) {
            if (this.notWho) {
                return -1;
            }
            return 1;
        }
        if (this.whoType < that.whoType) {
            return -1;
        }
        if (this.whoType > that.whoType) {
            return 1;
        }
        if (!whoTypeNamed[this.whoType]) {
            return 0;
        }
        return this.compareWho(this.who, that.who);
    }

    public int hashCode() {
        int hc = 7;
        if (this.who != null) {
            hc *= this.who.hashCode();
        }
        if (this.notWho) {
            hc *= 2;
        }
        return hc *= this.whoType;
    }

    public boolean equals(Object o) {
        return this.compareTo((AceWho)o) == 0;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("AceWho{who=");
        sb.append(this.who);
        sb.append(", notWho=");
        sb.append(this.notWho);
        sb.append(", whoType=");
        sb.append(whoTypeNames[this.whoType]);
        sb.append("(");
        sb.append(this.whoType);
        sb.append(")");
        sb.append("}");
        return sb.toString();
    }

    private int compareWho(String who1, String who2) {
        if (who1 == null && who2 == null) {
            return 0;
        }
        if (who1 == null) {
            return -1;
        }
        if (who2 == null) {
            return 1;
        }
        return who1.compareTo(who2);
    }
}

