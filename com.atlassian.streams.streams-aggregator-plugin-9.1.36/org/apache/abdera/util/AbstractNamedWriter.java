/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.util;

import org.apache.abdera.util.AbstractWriter;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.abdera.writer.NamedWriter;

public abstract class AbstractNamedWriter
extends AbstractWriter
implements NamedWriter {
    protected final String name;
    protected final String[] formats;

    protected AbstractNamedWriter(String name, String ... formats) {
        this.name = name;
        this.formats = formats;
    }

    public String getName() {
        return this.name;
    }

    public String[] getOutputFormats() {
        return this.formats;
    }

    public boolean outputsFormat(String mediatype) {
        for (String format : this.formats) {
            if (!MimeTypeHelper.isMatch(format, mediatype)) continue;
            return true;
        }
        return false;
    }
}

