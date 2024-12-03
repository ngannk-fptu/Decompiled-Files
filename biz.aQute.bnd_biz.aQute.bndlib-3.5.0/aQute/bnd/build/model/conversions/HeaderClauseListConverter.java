/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.build.model.conversions;

import aQute.bnd.build.model.clauses.HeaderClause;
import aQute.bnd.build.model.conversions.Converter;
import aQute.bnd.header.Attrs;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Processor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HeaderClauseListConverter<R>
implements Converter<List<R>, String> {
    private final Converter<? extends R, ? super HeaderClause> itemConverter;

    public HeaderClauseListConverter(Converter<? extends R, ? super HeaderClause> itemConverter) {
        this.itemConverter = itemConverter;
    }

    @Override
    public List<R> convert(String input) throws IllegalArgumentException {
        if (input == null) {
            return null;
        }
        ArrayList<R> result = new ArrayList<R>();
        Parameters header = new Parameters(input);
        for (Map.Entry<String, Attrs> entry : header.entrySet()) {
            String key = Processor.removeDuplicateMarker(entry.getKey());
            HeaderClause clause = new HeaderClause(key, entry.getValue());
            result.add(this.itemConverter.convert(clause));
        }
        return result;
    }

    @Override
    public List<R> error(String msg) {
        ArrayList<R> l = new ArrayList<R>();
        l.add(this.itemConverter.error(msg));
        return l;
    }
}

