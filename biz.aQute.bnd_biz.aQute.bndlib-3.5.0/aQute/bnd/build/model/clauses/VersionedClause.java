/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.build.model.clauses;

import aQute.bnd.build.model.clauses.HeaderClause;
import aQute.bnd.header.Attrs;

public class VersionedClause
extends HeaderClause
implements Cloneable {
    public VersionedClause(String name, Attrs attribs) {
        super(name, attribs);
    }

    public String getVersionRange() {
        return this.attribs.get("version");
    }

    public void setVersionRange(String versionRangeString) {
        this.attribs.put("version", versionRangeString);
    }

    @Override
    public VersionedClause clone() {
        VersionedClause clone = (VersionedClause)super.clone();
        clone.name = this.name;
        clone.attribs = new Attrs(this.attribs);
        return clone;
    }

    public static VersionedClause error(String msg) {
        Attrs a = new Attrs();
        a.put("PARSE ERROR", msg);
        return new VersionedClause("ERROR", a);
    }
}

