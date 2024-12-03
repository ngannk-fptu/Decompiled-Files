/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.util;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.util.Version;

public abstract class FilteringTokenFilter
extends TokenFilter {
    protected final Version version;
    private final PositionIncrementAttribute posIncrAtt = (PositionIncrementAttribute)this.addAttribute(PositionIncrementAttribute.class);
    private boolean enablePositionIncrements;
    private boolean first = true;

    private static void checkPositionIncrement(Version version, boolean enablePositionIncrements) {
        if (!enablePositionIncrements && version.onOrAfter(Version.LUCENE_44)) {
            throw new IllegalArgumentException("enablePositionIncrements=false is not supported anymore as of Lucene 4.4 as it can create broken token streams");
        }
    }

    @Deprecated
    public FilteringTokenFilter(Version version, boolean enablePositionIncrements, TokenStream input) {
        this(version, input);
        FilteringTokenFilter.checkPositionIncrement(version, enablePositionIncrements);
        this.enablePositionIncrements = enablePositionIncrements;
    }

    public FilteringTokenFilter(Version version, TokenStream in) {
        super(in);
        this.version = version;
        this.enablePositionIncrements = true;
    }

    protected abstract boolean accept() throws IOException;

    public final boolean incrementToken() throws IOException {
        if (this.enablePositionIncrements) {
            int skippedPositions = 0;
            while (this.input.incrementToken()) {
                if (this.accept()) {
                    if (skippedPositions != 0) {
                        this.posIncrAtt.setPositionIncrement(this.posIncrAtt.getPositionIncrement() + skippedPositions);
                    }
                    return true;
                }
                skippedPositions += this.posIncrAtt.getPositionIncrement();
            }
        } else {
            while (this.input.incrementToken()) {
                if (!this.accept()) continue;
                if (this.first) {
                    if (this.posIncrAtt.getPositionIncrement() == 0) {
                        this.posIncrAtt.setPositionIncrement(1);
                    }
                    this.first = false;
                }
                return true;
            }
        }
        return false;
    }

    public void reset() throws IOException {
        super.reset();
        this.first = true;
    }

    public boolean getEnablePositionIncrements() {
        return this.enablePositionIncrements;
    }

    @Deprecated
    public void setEnablePositionIncrements(boolean enable) {
        FilteringTokenFilter.checkPositionIncrement(this.version, enable);
        this.enablePositionIncrements = enable;
    }
}

