/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.build.model.conversions;

import aQute.bnd.build.model.clauses.HeaderClause;
import aQute.bnd.build.model.clauses.VersionedClause;
import aQute.bnd.build.model.conversions.Converter;

public class VersionedClauseConverter
implements Converter<VersionedClause, HeaderClause> {
    @Override
    public VersionedClause convert(HeaderClause input) throws IllegalArgumentException {
        if (input == null) {
            return null;
        }
        return new VersionedClause(input.getName(), input.getAttribs());
    }

    @Override
    public VersionedClause error(String msg) {
        return VersionedClause.error(msg);
    }
}

