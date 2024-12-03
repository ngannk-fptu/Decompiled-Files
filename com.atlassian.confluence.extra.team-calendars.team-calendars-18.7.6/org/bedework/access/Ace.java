/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.access;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.bedework.access.Access;
import org.bedework.access.AccessException;
import org.bedework.access.AceWho;
import org.bedework.access.Acl;
import org.bedework.access.EncodedAcl;
import org.bedework.access.Privilege;
import org.bedework.access.PrivilegeDefs;
import org.bedework.access.PrivilegeSet;
import org.bedework.access.Privileges;
import org.bedework.access.WhoDefs;
import org.bedework.util.caching.ObjectPool;

public final class Ace
implements PrivilegeDefs,
WhoDefs,
Comparable<Ace> {
    private static transient Logger log;
    private AceWho who;
    private PrivilegeSet how;
    private Collection<Privilege> privs;
    private String inheritedFrom;
    private String encoding;
    private char[] encodingChars;
    private static ObjectPool<String> inheritedFroms;
    private static Map<String, Ace> aceCache;
    private static Access.AccessStatsEntry aceCacheSize;
    private static Access.AccessStatsEntry aceCacheHits;
    private static Access.AccessStatsEntry aceCacheMisses;

    public static Ace makeAce(AceWho who, Collection<Privilege> privs, String inheritedFrom) throws AccessException {
        Ace ace = new Ace(who, privs, inheritedFrom);
        Ace cace = aceCache.get(ace.encoding);
        if (cace == null) {
            aceCache.put(ace.encoding, ace);
            Ace.aceCacheSize.count = aceCache.size();
            cace = ace;
        }
        return cace;
    }

    private Ace(AceWho who, Collection<Privilege> privs, String inheritedFrom) throws AccessException {
        this.who = who;
        this.how = new PrivilegeSet();
        this.privs = new ArrayList<Privilege>();
        if (privs != null) {
            for (Privilege p : privs) {
                this.privs.add(p);
                this.how = PrivilegeSet.addPrivilege(this.how, p);
            }
        }
        this.inheritedFrom = inheritedFrom == null ? null : inheritedFroms.get(inheritedFrom);
        this.encode();
    }

    public static Collection<Access.AccessStatsEntry> getStatistics() {
        ArrayList<Access.AccessStatsEntry> stats = new ArrayList<Access.AccessStatsEntry>();
        stats.add(aceCacheSize);
        stats.add(aceCacheHits);
        stats.add(aceCacheMisses);
        return stats;
    }

    public AceWho getWho() {
        return this.who;
    }

    public PrivilegeSet getHow() {
        if (this.how == null) {
            this.how = new PrivilegeSet();
        }
        return this.how;
    }

    public Collection<Privilege> getPrivs() {
        if (this.privs == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableCollection(this.privs);
    }

    public String getInheritedFrom() {
        return this.inheritedFrom;
    }

    public static PrivilegeSet findMergedPrivilege(Acl acl, Access.AccessCb cb, String name, int whoType) throws AccessException {
        PrivilegeSet privileges = null;
        for (Ace ace : acl.getAces()) {
            if (whoType != ace.who.getWhoType() || whoType != 7 && whoType != 8 && whoType != 10 && whoType != 0 && !ace.getWho().whoMatch(cb, name)) continue;
            privileges = PrivilegeSet.mergePrivileges(privileges, ace.getHow(), ace.getInheritedFrom() != null);
        }
        return privileges;
    }

    public static Ace decode(EncodedAcl acl, String path) throws AccessException {
        String enc;
        int pos = acl.getPos();
        AceWho.skip(acl);
        Privileges.skip(acl);
        acl.back();
        boolean hasInherited = false;
        if (acl.getChar() == 'I') {
            hasInherited = true;
            acl.skipString();
            if (acl.getChar() != ' ') {
                throw new AccessException("malformedAcl");
            }
        }
        if (hasInherited || path == null) {
            enc = acl.getString(pos);
        } else {
            acl.back();
            StringBuilder sb = new StringBuilder(acl.getString(pos));
            acl.getChar();
            sb.append('I');
            sb.append(EncodedAcl.encodedString(path));
            sb.append(' ');
            enc = sb.toString();
        }
        Ace ace = aceCache.get(enc);
        if (ace != null) {
            ++Ace.aceCacheHits.count;
            return ace;
        }
        ++Ace.aceCacheMisses.count;
        acl.setPos(pos);
        AceWho who = AceWho.decode(acl);
        Collection<Privilege> privs = Privileges.getPrivs(acl);
        acl.back();
        String inheritedFrom = null;
        if (acl.getChar() == 'I') {
            inheritedFrom = acl.getString();
        } else {
            acl.back();
        }
        if (acl.getChar() != ' ') {
            throw new AccessException("malformedAcl");
        }
        if (inheritedFrom == null) {
            inheritedFrom = path;
        }
        ace = Ace.makeAce(who, privs, inheritedFrom);
        return ace;
    }

    public void encode(EncodedAcl acl) throws AccessException {
        if (this.encoding == null) {
            this.encode();
        }
        acl.addChar(this.encodingChars);
    }

    private void encode() throws AccessException {
        EncodedAcl eacl = new EncodedAcl();
        eacl.startEncoding();
        this.getWho().encode(eacl);
        for (Privilege p : this.privs) {
            p.encode(eacl);
        }
        if (this.inheritedFrom != null) {
            eacl.addChar('I');
            eacl.encodeString(this.inheritedFrom);
        }
        eacl.addChar(' ');
        this.encodingChars = eacl.getEncoding();
        this.encoding = new String(this.encodingChars);
    }

    public String toUserString() {
        StringBuilder sb = new StringBuilder("(");
        sb.append(this.getWho().toUserString());
        sb.append(" ");
        for (Privilege p : this.privs) {
            sb.append(p.toUserString());
            sb.append(" ");
        }
        sb.append(")");
        return sb.toString();
    }

    protected static Logger getLog() {
        if (log == null) {
            log = Logger.getLogger(Ace.class);
        }
        return log;
    }

    protected static void debugMsg(String msg) {
        Ace.getLog().debug(msg);
    }

    @Override
    public int compareTo(Ace that) {
        if (this == that) {
            return 0;
        }
        int res = this.getWho().compareTo(that.getWho());
        if (res == 0) {
            res = this.getHow().compareTo(that.getHow());
        }
        return res;
    }

    public int hashCode() {
        int hc = 7;
        if (this.who != null) {
            hc *= this.who.hashCode();
        }
        return hc *= this.getHow().hashCode();
    }

    public boolean equals(Object o) {
        return this.compareTo((Ace)o) == 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Ace{");
        sb.append(this.getWho().toString());
        if (this.how != null) {
            sb.append(", how=");
            sb.append(this.how);
        }
        if (this.getInheritedFrom() != null) {
            sb.append(", \ninherited from \"");
            sb.append(this.getInheritedFrom());
            sb.append("\"");
        }
        sb.append(", \nprivs=[");
        for (Privilege p : this.privs) {
            sb.append(p.toString());
            sb.append("\n");
        }
        sb.append("]");
        sb.append("}");
        return sb.toString();
    }

    static {
        inheritedFroms = new ObjectPool();
        aceCache = new HashMap<String, Ace>();
        aceCacheSize = new Access.AccessStatsEntry("ACE cache size");
        aceCacheHits = new Access.AccessStatsEntry("ACE cache hits");
        aceCacheMisses = new Access.AccessStatsEntry("ACE cache misses");
    }
}

