/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.OffsetAttribute
 */
package org.apache.lucene.search.highlight;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.search.highlight.DefaultEncoder;
import org.apache.lucene.search.highlight.Encoder;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.FragmentQueue;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.TextFragment;
import org.apache.lucene.search.highlight.TokenGroup;

public class Highlighter {
    public static final int DEFAULT_MAX_CHARS_TO_ANALYZE = 51200;
    private int maxDocCharsToAnalyze = 51200;
    private Formatter formatter;
    private Encoder encoder;
    private Fragmenter textFragmenter = new SimpleFragmenter();
    private Scorer fragmentScorer = null;

    public Highlighter(Scorer fragmentScorer) {
        this(new SimpleHTMLFormatter(), fragmentScorer);
    }

    public Highlighter(Formatter formatter, Scorer fragmentScorer) {
        this(formatter, new DefaultEncoder(), fragmentScorer);
    }

    public Highlighter(Formatter formatter, Encoder encoder, Scorer fragmentScorer) {
        this.formatter = formatter;
        this.encoder = encoder;
        this.fragmentScorer = fragmentScorer;
    }

    public final String getBestFragment(Analyzer analyzer, String fieldName, String text) throws IOException, InvalidTokenOffsetsException {
        TokenStream tokenStream = analyzer.tokenStream(fieldName, text);
        return this.getBestFragment(tokenStream, text);
    }

    public final String getBestFragment(TokenStream tokenStream, String text) throws IOException, InvalidTokenOffsetsException {
        String[] results = this.getBestFragments(tokenStream, text, 1);
        if (results.length > 0) {
            return results[0];
        }
        return null;
    }

    public final String[] getBestFragments(Analyzer analyzer, String fieldName, String text, int maxNumFragments) throws IOException, InvalidTokenOffsetsException {
        TokenStream tokenStream = analyzer.tokenStream(fieldName, text);
        return this.getBestFragments(tokenStream, text, maxNumFragments);
    }

    public final String[] getBestFragments(TokenStream tokenStream, String text, int maxNumFragments) throws IOException, InvalidTokenOffsetsException {
        maxNumFragments = Math.max(1, maxNumFragments);
        TextFragment[] frag = this.getBestTextFragments(tokenStream, text, true, maxNumFragments);
        ArrayList<String> fragTexts = new ArrayList<String>();
        for (int i = 0; i < frag.length; ++i) {
            if (frag[i] == null || !(frag[i].getScore() > 0.0f)) continue;
            fragTexts.add(frag[i].toString());
        }
        return fragTexts.toArray(new String[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final TextFragment[] getBestTextFragments(TokenStream tokenStream, String text, boolean mergeContiguousFragments, int maxNumFragments) throws IOException, InvalidTokenOffsetsException {
        TokenStream newStream;
        ArrayList<TextFragment> docFrags = new ArrayList<TextFragment>();
        StringBuilder newText = new StringBuilder();
        CharTermAttribute termAtt = (CharTermAttribute)tokenStream.addAttribute(CharTermAttribute.class);
        OffsetAttribute offsetAtt = (OffsetAttribute)tokenStream.addAttribute(OffsetAttribute.class);
        tokenStream.reset();
        TextFragment currentFrag2 = new TextFragment(newText, newText.length(), docFrags.size());
        if (this.fragmentScorer instanceof QueryScorer) {
            ((QueryScorer)this.fragmentScorer).setMaxDocCharsToAnalyze(this.maxDocCharsToAnalyze);
        }
        if ((newStream = this.fragmentScorer.init(tokenStream)) != null) {
            tokenStream = newStream;
        }
        this.fragmentScorer.startFragment(currentFrag2);
        docFrags.add(currentFrag2);
        FragmentQueue fragQueue = new FragmentQueue(maxNumFragments);
        try {
            String tokenText;
            int endOffset;
            int startOffset;
            int lastEndOffset = 0;
            this.textFragmenter.start(text, tokenStream);
            TokenGroup tokenGroup = new TokenGroup(tokenStream);
            boolean next = tokenStream.incrementToken();
            while (next && offsetAtt.startOffset() < this.maxDocCharsToAnalyze) {
                if (offsetAtt.endOffset() > text.length() || offsetAtt.startOffset() > text.length()) {
                    throw new InvalidTokenOffsetsException("Token " + termAtt.toString() + " exceeds length of provided text sized " + text.length());
                }
                if (tokenGroup.numTokens > 0 && tokenGroup.isDistinct()) {
                    startOffset = tokenGroup.matchStartOffset;
                    endOffset = tokenGroup.matchEndOffset;
                    tokenText = text.substring(startOffset, endOffset);
                    String markedUpText = this.formatter.highlightTerm(this.encoder.encodeText(tokenText), tokenGroup);
                    if (startOffset > lastEndOffset) {
                        newText.append(this.encoder.encodeText(text.substring(lastEndOffset, startOffset)));
                    }
                    newText.append(markedUpText);
                    lastEndOffset = Math.max(endOffset, lastEndOffset);
                    tokenGroup.clear();
                    if (this.textFragmenter.isNewFragment()) {
                        currentFrag2.setScore(this.fragmentScorer.getFragmentScore());
                        currentFrag2.textEndPos = newText.length();
                        currentFrag2 = new TextFragment(newText, newText.length(), docFrags.size());
                        this.fragmentScorer.startFragment(currentFrag2);
                        docFrags.add(currentFrag2);
                    }
                }
                tokenGroup.addToken(this.fragmentScorer.getTokenScore());
                next = tokenStream.incrementToken();
            }
            currentFrag2.setScore(this.fragmentScorer.getFragmentScore());
            if (tokenGroup.numTokens > 0) {
                startOffset = tokenGroup.matchStartOffset;
                endOffset = tokenGroup.matchEndOffset;
                tokenText = text.substring(startOffset, endOffset);
                String markedUpText = this.formatter.highlightTerm(this.encoder.encodeText(tokenText), tokenGroup);
                if (startOffset > lastEndOffset) {
                    newText.append(this.encoder.encodeText(text.substring(lastEndOffset, startOffset)));
                }
                newText.append(markedUpText);
                lastEndOffset = Math.max(lastEndOffset, endOffset);
            }
            if (lastEndOffset < text.length() && text.length() <= this.maxDocCharsToAnalyze) {
                newText.append(this.encoder.encodeText(text.substring(lastEndOffset)));
            }
            currentFrag2.textEndPos = newText.length();
            for (TextFragment currentFrag2 : docFrags) {
                fragQueue.insertWithOverflow(currentFrag2);
            }
            TextFragment[] frag = new TextFragment[fragQueue.size()];
            for (int i = frag.length - 1; i >= 0; --i) {
                frag[i] = (TextFragment)fragQueue.pop();
            }
            if (mergeContiguousFragments) {
                this.mergeContiguousFragments(frag);
                ArrayList<TextFragment> fragTexts = new ArrayList<TextFragment>();
                for (int i = 0; i < frag.length; ++i) {
                    if (frag[i] == null || !(frag[i].getScore() > 0.0f)) continue;
                    fragTexts.add(frag[i]);
                }
                frag = fragTexts.toArray(new TextFragment[0]);
            }
            TextFragment[] textFragmentArray = frag;
            return textFragmentArray;
        }
        finally {
            if (tokenStream != null) {
                try {
                    tokenStream.end();
                    tokenStream.close();
                }
                catch (Exception exception) {}
            }
        }
    }

    private void mergeContiguousFragments(TextFragment[] frag) {
        if (frag.length > 1) {
            boolean mergingStillBeingDone;
            do {
                mergingStillBeingDone = false;
                block1: for (int i = 0; i < frag.length; ++i) {
                    if (frag[i] == null) continue;
                    for (int x = 0; x < frag.length; ++x) {
                        int worstScoringFragNum;
                        int bestScoringFragNum;
                        if (frag[x] == null) continue;
                        if (frag[i] == null) continue block1;
                        TextFragment frag1 = null;
                        TextFragment frag2 = null;
                        int frag1Num = 0;
                        int frag2Num = 0;
                        if (frag[i].follows(frag[x])) {
                            frag1 = frag[x];
                            frag1Num = x;
                            frag2 = frag[i];
                            frag2Num = i;
                        } else if (frag[x].follows(frag[i])) {
                            frag1 = frag[i];
                            frag1Num = i;
                            frag2 = frag[x];
                            frag2Num = x;
                        }
                        if (frag1 == null) continue;
                        if (frag1.getScore() > frag2.getScore()) {
                            bestScoringFragNum = frag1Num;
                            worstScoringFragNum = frag2Num;
                        } else {
                            bestScoringFragNum = frag2Num;
                            worstScoringFragNum = frag1Num;
                        }
                        frag1.merge(frag2);
                        frag[worstScoringFragNum] = null;
                        mergingStillBeingDone = true;
                        frag[bestScoringFragNum] = frag1;
                    }
                }
            } while (mergingStillBeingDone);
        }
    }

    public final String getBestFragments(TokenStream tokenStream, String text, int maxNumFragments, String separator) throws IOException, InvalidTokenOffsetsException {
        String[] sections = this.getBestFragments(tokenStream, text, maxNumFragments);
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < sections.length; ++i) {
            if (i > 0) {
                result.append(separator);
            }
            result.append(sections[i]);
        }
        return result.toString();
    }

    public int getMaxDocCharsToAnalyze() {
        return this.maxDocCharsToAnalyze;
    }

    public void setMaxDocCharsToAnalyze(int maxDocCharsToAnalyze) {
        this.maxDocCharsToAnalyze = maxDocCharsToAnalyze;
    }

    public Fragmenter getTextFragmenter() {
        return this.textFragmenter;
    }

    public void setTextFragmenter(Fragmenter fragmenter) {
        this.textFragmenter = fragmenter;
    }

    public Scorer getFragmentScorer() {
        return this.fragmentScorer;
    }

    public void setFragmentScorer(Scorer scorer) {
        this.fragmentScorer = scorer;
    }

    public Encoder getEncoder() {
        return this.encoder;
    }

    public void setEncoder(Encoder encoder) {
        this.encoder = encoder;
    }
}

