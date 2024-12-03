/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.http.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.apache.tomcat.util.http.parser.SkipResult;

public class Ranges {
    private final String units;
    private final List<Entry> entries;

    private Ranges(String units, List<Entry> entries) {
        this.units = units == null ? null : units.toLowerCase(Locale.ENGLISH);
        this.entries = Collections.unmodifiableList(entries);
    }

    public List<Entry> getEntries() {
        return this.entries;
    }

    public String getUnits() {
        return this.units;
    }

    public static Ranges parse(StringReader input) throws IOException {
        SkipResult skipResult;
        String units = HttpParser.readToken(input);
        if (units == null || units.length() == 0) {
            return null;
        }
        if (HttpParser.skipConstant(input, "=") != SkipResult.FOUND) {
            return null;
        }
        ArrayList<Entry> entries = new ArrayList<Entry>();
        do {
            long start = HttpParser.readLong(input);
            if (HttpParser.skipConstant(input, "-") != SkipResult.FOUND) {
                return null;
            }
            long end = HttpParser.readLong(input);
            if (start == -1L && end == -1L) {
                return null;
            }
            entries.add(new Entry(start, end));
            skipResult = HttpParser.skipConstant(input, ",");
            if (skipResult != SkipResult.NOT_FOUND) continue;
            return null;
        } while (skipResult == SkipResult.FOUND);
        return new Ranges(units, entries);
    }

    public static class Entry {
        private final long start;
        private final long end;

        public Entry(long start, long end) {
            this.start = start;
            this.end = end;
        }

        public long getStart() {
            return this.start;
        }

        public long getEnd() {
            return this.end;
        }
    }
}

