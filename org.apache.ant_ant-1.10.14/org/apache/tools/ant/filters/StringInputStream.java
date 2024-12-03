/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.filters;

import java.io.Reader;
import java.io.StringReader;
import org.apache.tools.ant.util.ReaderInputStream;

public class StringInputStream
extends ReaderInputStream {
    public StringInputStream(String source) {
        super(new StringReader(source));
    }

    public StringInputStream(String source, String encoding) {
        super((Reader)new StringReader(source), encoding);
    }
}

