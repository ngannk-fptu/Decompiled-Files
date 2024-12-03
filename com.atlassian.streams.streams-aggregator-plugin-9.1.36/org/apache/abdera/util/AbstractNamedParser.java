/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.util;

import org.apache.abdera.Abdera;
import org.apache.abdera.parser.NamedParser;
import org.apache.abdera.util.AbstractParser;
import org.apache.abdera.util.MimeTypeHelper;

public abstract class AbstractNamedParser
extends AbstractParser
implements NamedParser {
    protected final String name;
    protected final String[] formats;

    protected AbstractNamedParser(Abdera abdera, String name, String ... formats) {
        super(abdera);
        this.name = name;
        this.formats = formats;
    }

    public String getName() {
        return this.name;
    }

    public String[] getInputFormats() {
        return this.formats;
    }

    public boolean parsesFormat(String mediatype) {
        for (String format : this.formats) {
            if (!MimeTypeHelper.isMatch(format, mediatype)) continue;
            return true;
        }
        return false;
    }
}

