/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.build.model.clauses;

import aQute.bnd.build.model.clauses.VersionedClause;
import aQute.bnd.header.Attrs;

public class ImportPattern
extends VersionedClause
implements Cloneable {
    public ImportPattern(String pattern, Attrs attributes) {
        super(pattern, attributes);
    }

    public boolean isOptional() {
        String resolution = this.attribs.get("resolution:");
        return "optional".equals(resolution);
    }

    public void setOptional(boolean optional) {
        if (optional) {
            this.attribs.put("resolution:", "optional");
        } else {
            this.attribs.remove("resolution:");
        }
    }

    @Override
    public ImportPattern clone() {
        return new ImportPattern(this.name, new Attrs(this.attribs));
    }

    public static ImportPattern error(String msg) {
        Attrs a = new Attrs();
        return new ImportPattern(msg, null);
    }
}

