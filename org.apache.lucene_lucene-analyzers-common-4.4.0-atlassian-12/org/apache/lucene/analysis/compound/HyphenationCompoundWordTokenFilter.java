/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.compound;

import java.io.File;
import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.compound.CompoundWordTokenFilterBase;
import org.apache.lucene.analysis.compound.hyphenation.Hyphenation;
import org.apache.lucene.analysis.compound.hyphenation.HyphenationTree;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;
import org.xml.sax.InputSource;

public class HyphenationCompoundWordTokenFilter
extends CompoundWordTokenFilterBase {
    private HyphenationTree hyphenator;

    public HyphenationCompoundWordTokenFilter(Version matchVersion, TokenStream input, HyphenationTree hyphenator, CharArraySet dictionary) {
        this(matchVersion, input, hyphenator, dictionary, 5, 2, 15, false);
    }

    public HyphenationCompoundWordTokenFilter(Version matchVersion, TokenStream input, HyphenationTree hyphenator, CharArraySet dictionary, int minWordSize, int minSubwordSize, int maxSubwordSize, boolean onlyLongestMatch) {
        super(matchVersion, input, dictionary, minWordSize, minSubwordSize, maxSubwordSize, onlyLongestMatch);
        this.hyphenator = hyphenator;
    }

    public HyphenationCompoundWordTokenFilter(Version matchVersion, TokenStream input, HyphenationTree hyphenator, int minWordSize, int minSubwordSize, int maxSubwordSize) {
        this(matchVersion, input, hyphenator, null, minWordSize, minSubwordSize, maxSubwordSize, false);
    }

    public HyphenationCompoundWordTokenFilter(Version matchVersion, TokenStream input, HyphenationTree hyphenator) {
        this(matchVersion, input, hyphenator, 5, 2, 15);
    }

    public static HyphenationTree getHyphenationTree(String hyphenationFilename) throws IOException {
        return HyphenationCompoundWordTokenFilter.getHyphenationTree(new InputSource(hyphenationFilename));
    }

    public static HyphenationTree getHyphenationTree(File hyphenationFile) throws IOException {
        return HyphenationCompoundWordTokenFilter.getHyphenationTree(new InputSource(hyphenationFile.toURI().toASCIIString()));
    }

    public static HyphenationTree getHyphenationTree(InputSource hyphenationSource) throws IOException {
        HyphenationTree tree = new HyphenationTree();
        tree.loadPatterns(hyphenationSource);
        return tree;
    }

    @Override
    protected void decompose() {
        Hyphenation hyphens = this.hyphenator.hyphenate(this.termAtt.buffer(), 0, this.termAtt.length(), 1, 1);
        if (hyphens == null) {
            return;
        }
        int[] hyp = hyphens.getHyphenationPoints();
        for (int i = 0; i < hyp.length; ++i) {
            int partLength;
            int remaining = hyp.length - i;
            int start = hyp[i];
            CompoundWordTokenFilterBase.CompoundToken longestMatchToken = null;
            for (int j = 1; j < remaining && (partLength = hyp[i + j] - start) <= this.maxSubwordSize; ++j) {
                if (partLength < this.minSubwordSize) continue;
                if (this.dictionary == null || this.dictionary.contains(this.termAtt.buffer(), start, partLength)) {
                    if (this.onlyLongestMatch) {
                        if (longestMatchToken != null) {
                            if (longestMatchToken.txt.length() >= partLength) continue;
                            longestMatchToken = new CompoundWordTokenFilterBase.CompoundToken(this, start, partLength);
                            continue;
                        }
                        longestMatchToken = new CompoundWordTokenFilterBase.CompoundToken(this, start, partLength);
                        continue;
                    }
                    this.tokens.add(new CompoundWordTokenFilterBase.CompoundToken(this, start, partLength));
                    continue;
                }
                if (!this.dictionary.contains(this.termAtt.buffer(), start, partLength - 1)) continue;
                if (this.onlyLongestMatch) {
                    if (longestMatchToken != null) {
                        if (longestMatchToken.txt.length() >= partLength - 1) continue;
                        longestMatchToken = new CompoundWordTokenFilterBase.CompoundToken(this, start, partLength - 1);
                        continue;
                    }
                    longestMatchToken = new CompoundWordTokenFilterBase.CompoundToken(this, start, partLength - 1);
                    continue;
                }
                this.tokens.add(new CompoundWordTokenFilterBase.CompoundToken(this, start, partLength - 1));
            }
            if (!this.onlyLongestMatch || longestMatchToken == null) continue;
            this.tokens.add(longestMatchToken);
        }
    }
}

