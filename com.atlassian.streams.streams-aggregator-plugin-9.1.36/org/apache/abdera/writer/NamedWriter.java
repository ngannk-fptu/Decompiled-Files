/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.writer;

import org.apache.abdera.util.NamedItem;
import org.apache.abdera.writer.Writer;

public interface NamedWriter
extends Writer,
NamedItem {
    public String[] getOutputFormats();

    public boolean outputsFormat(String var1);
}

