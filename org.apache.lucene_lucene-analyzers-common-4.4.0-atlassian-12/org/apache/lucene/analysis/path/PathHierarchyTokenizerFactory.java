/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Tokenizer
 *  org.apache.lucene.util.AttributeSource$AttributeFactory
 */
package org.apache.lucene.analysis.path;

import java.io.Reader;
import java.util.Map;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.path.PathHierarchyTokenizer;
import org.apache.lucene.analysis.path.ReversePathHierarchyTokenizer;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeSource;

public class PathHierarchyTokenizerFactory
extends TokenizerFactory {
    private final char delimiter;
    private final char replacement;
    private final boolean reverse;
    private final int skip;

    public PathHierarchyTokenizerFactory(Map<String, String> args) {
        super(args);
        this.delimiter = this.getChar(args, "delimiter", '/');
        this.replacement = this.getChar(args, "replace", this.delimiter);
        this.reverse = this.getBoolean(args, "reverse", false);
        this.skip = this.getInt(args, "skip", 0);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    @Override
    public Tokenizer create(AttributeSource.AttributeFactory factory, Reader input) {
        if (this.reverse) {
            return new ReversePathHierarchyTokenizer(factory, input, this.delimiter, this.replacement, this.skip);
        }
        return new PathHierarchyTokenizer(factory, input, this.delimiter, this.replacement, this.skip);
    }
}

