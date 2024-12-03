/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.osgi.resource;

import aQute.bnd.version.VersionRange;
import aQute.libg.filters.AndFilter;
import aQute.libg.filters.Filter;
import aQute.libg.filters.NotFilter;
import aQute.libg.filters.Operator;
import aQute.libg.filters.SimpleFilter;

public class Filters {
    public static final String DEFAULT_VERSION_ATTR = "version";

    public static String fromVersionRange(String range) throws IllegalArgumentException {
        return Filters.fromVersionRange(range, DEFAULT_VERSION_ATTR);
    }

    public static String fromVersionRange(String range, String versionAttr) throws IllegalArgumentException {
        if (range == null) {
            return null;
        }
        VersionRange parsedRange = new VersionRange(range);
        Filter left = parsedRange.includeLow() ? new SimpleFilter(versionAttr, Operator.GreaterThanOrEqual, parsedRange.getLow().toString()) : new NotFilter(new SimpleFilter(versionAttr, Operator.LessThanOrEqual, parsedRange.getLow().toString()));
        Filter right = !parsedRange.isRange() ? null : (parsedRange.includeHigh() ? new SimpleFilter(versionAttr, Operator.LessThanOrEqual, parsedRange.getHigh().toString()) : new NotFilter(new SimpleFilter(versionAttr, Operator.GreaterThanOrEqual, parsedRange.getHigh().toString())));
        Filter result = right != null ? new AndFilter().addChild(left).addChild(right) : left;
        return result.toString();
    }
}

