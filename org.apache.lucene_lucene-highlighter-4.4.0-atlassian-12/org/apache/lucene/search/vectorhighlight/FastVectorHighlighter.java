/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.search.Query
 */
package org.apache.lucene.search.vectorhighlight;

import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Encoder;
import org.apache.lucene.search.vectorhighlight.FieldFragList;
import org.apache.lucene.search.vectorhighlight.FieldPhraseList;
import org.apache.lucene.search.vectorhighlight.FieldQuery;
import org.apache.lucene.search.vectorhighlight.FieldTermStack;
import org.apache.lucene.search.vectorhighlight.FragListBuilder;
import org.apache.lucene.search.vectorhighlight.FragmentsBuilder;
import org.apache.lucene.search.vectorhighlight.ScoreOrderFragmentsBuilder;
import org.apache.lucene.search.vectorhighlight.SimpleFragListBuilder;

public class FastVectorHighlighter {
    public static final boolean DEFAULT_PHRASE_HIGHLIGHT = true;
    public static final boolean DEFAULT_FIELD_MATCH = true;
    private final boolean phraseHighlight;
    private final boolean fieldMatch;
    private final FragListBuilder fragListBuilder;
    private final FragmentsBuilder fragmentsBuilder;
    private int phraseLimit = Integer.MAX_VALUE;

    public FastVectorHighlighter() {
        this(true, true);
    }

    public FastVectorHighlighter(boolean phraseHighlight, boolean fieldMatch) {
        this(phraseHighlight, fieldMatch, new SimpleFragListBuilder(), new ScoreOrderFragmentsBuilder());
    }

    public FastVectorHighlighter(boolean phraseHighlight, boolean fieldMatch, FragListBuilder fragListBuilder, FragmentsBuilder fragmentsBuilder) {
        this.phraseHighlight = phraseHighlight;
        this.fieldMatch = fieldMatch;
        this.fragListBuilder = fragListBuilder;
        this.fragmentsBuilder = fragmentsBuilder;
    }

    public FieldQuery getFieldQuery(Query query) {
        try {
            return new FieldQuery(query, null, this.phraseHighlight, this.fieldMatch);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FieldQuery getFieldQuery(Query query, IndexReader reader) throws IOException {
        return new FieldQuery(query, reader, this.phraseHighlight, this.fieldMatch);
    }

    public final String getBestFragment(FieldQuery fieldQuery, IndexReader reader, int docId, String fieldName, int fragCharSize) throws IOException {
        FieldFragList fieldFragList = this.getFieldFragList(this.fragListBuilder, fieldQuery, reader, docId, fieldName, fragCharSize);
        return this.fragmentsBuilder.createFragment(reader, docId, fieldName, fieldFragList);
    }

    public final String[] getBestFragments(FieldQuery fieldQuery, IndexReader reader, int docId, String fieldName, int fragCharSize, int maxNumFragments) throws IOException {
        FieldFragList fieldFragList = this.getFieldFragList(this.fragListBuilder, fieldQuery, reader, docId, fieldName, fragCharSize);
        return this.fragmentsBuilder.createFragments(reader, docId, fieldName, fieldFragList, maxNumFragments);
    }

    public final String getBestFragment(FieldQuery fieldQuery, IndexReader reader, int docId, String fieldName, int fragCharSize, FragListBuilder fragListBuilder, FragmentsBuilder fragmentsBuilder, String[] preTags, String[] postTags, Encoder encoder) throws IOException {
        FieldFragList fieldFragList = this.getFieldFragList(fragListBuilder, fieldQuery, reader, docId, fieldName, fragCharSize);
        return fragmentsBuilder.createFragment(reader, docId, fieldName, fieldFragList, preTags, postTags, encoder);
    }

    public final String[] getBestFragments(FieldQuery fieldQuery, IndexReader reader, int docId, String fieldName, int fragCharSize, int maxNumFragments, FragListBuilder fragListBuilder, FragmentsBuilder fragmentsBuilder, String[] preTags, String[] postTags, Encoder encoder) throws IOException {
        FieldFragList fieldFragList = this.getFieldFragList(fragListBuilder, fieldQuery, reader, docId, fieldName, fragCharSize);
        return fragmentsBuilder.createFragments(reader, docId, fieldName, fieldFragList, maxNumFragments, preTags, postTags, encoder);
    }

    private FieldFragList getFieldFragList(FragListBuilder fragListBuilder, FieldQuery fieldQuery, IndexReader reader, int docId, String fieldName, int fragCharSize) throws IOException {
        FieldTermStack fieldTermStack = new FieldTermStack(reader, docId, fieldName, fieldQuery);
        FieldPhraseList fieldPhraseList = new FieldPhraseList(fieldTermStack, fieldQuery, this.phraseLimit);
        return fragListBuilder.createFieldFragList(fieldPhraseList, fragCharSize);
    }

    public boolean isPhraseHighlight() {
        return this.phraseHighlight;
    }

    public boolean isFieldMatch() {
        return this.fieldMatch;
    }

    public int getPhraseLimit() {
        return this.phraseLimit;
    }

    public void setPhraseLimit(int phraseLimit) {
        this.phraseLimit = phraseLimit;
    }
}

