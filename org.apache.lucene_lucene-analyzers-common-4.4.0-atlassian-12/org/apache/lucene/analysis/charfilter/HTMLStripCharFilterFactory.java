/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.charfilter;

import java.io.Reader;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.lucene.analysis.charfilter.HTMLStripCharFilter;
import org.apache.lucene.analysis.util.CharFilterFactory;

public class HTMLStripCharFilterFactory
extends CharFilterFactory {
    final Set<String> escapedTags;
    static final Pattern TAG_NAME_PATTERN = Pattern.compile("[^\\s,]+");

    public HTMLStripCharFilterFactory(Map<String, String> args) {
        super(args);
        this.escapedTags = this.getSet(args, "escapedTags");
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    public HTMLStripCharFilter create(Reader input) {
        HTMLStripCharFilter charFilter = null == this.escapedTags ? new HTMLStripCharFilter(input) : new HTMLStripCharFilter(input, this.escapedTags);
        return charFilter;
    }
}

