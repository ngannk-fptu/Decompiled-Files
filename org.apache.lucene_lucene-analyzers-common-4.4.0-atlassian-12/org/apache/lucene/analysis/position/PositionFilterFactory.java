/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.position;

import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.position.PositionFilter;
import org.apache.lucene.analysis.util.TokenFilterFactory;
import org.apache.lucene.util.Version;

@Deprecated
public class PositionFilterFactory
extends TokenFilterFactory {
    private final int positionIncrement;

    public PositionFilterFactory(Map<String, String> args) {
        super(args);
        this.positionIncrement = this.getInt(args, "positionIncrement", 0);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
        if (this.luceneMatchVersion != null && this.luceneMatchVersion.onOrAfter(Version.LUCENE_44)) {
            throw new IllegalArgumentException("PositionFilter is deprecated as of Lucene 4.4. You should either fix your code to not use it or use Lucene 4.3 version compatibility");
        }
    }

    public PositionFilter create(TokenStream input) {
        return new PositionFilter(input, this.positionIncrement);
    }
}

