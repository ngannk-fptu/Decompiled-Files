/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.AttributeSource$AttributeFactory
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.in;

import java.io.Reader;
import org.apache.lucene.analysis.util.CharTokenizer;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.Version;

@Deprecated
public final class IndicTokenizer
extends CharTokenizer {
    public IndicTokenizer(Version matchVersion, AttributeSource.AttributeFactory factory, Reader input) {
        super(matchVersion, factory, input);
    }

    public IndicTokenizer(Version matchVersion, Reader input) {
        super(matchVersion, input);
    }

    @Override
    protected boolean isTokenChar(int c) {
        return Character.isLetter(c) || Character.getType(c) == 6 || Character.getType(c) == 16 || Character.getType(c) == 8;
    }
}

