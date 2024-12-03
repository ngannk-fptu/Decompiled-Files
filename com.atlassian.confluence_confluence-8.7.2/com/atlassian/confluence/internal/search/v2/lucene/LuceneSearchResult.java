/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  org.apache.lucene.document.Document
 *  org.apache.lucene.index.IndexableField
 */
package com.atlassian.confluence.internal.search.v2.lucene;

import com.atlassian.confluence.core.persistence.hibernate.HibernateHandle;
import com.atlassian.confluence.search.summary.HitHighlighter;
import com.atlassian.confluence.search.v2.AbstractSearchResult;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;

public class LuceneSearchResult
extends AbstractSearchResult {
    private final Document document;
    private final Optional<HitHighlighter> maybeHighlighter;
    private final Optional<String> maybeExplanation;
    private final AbstractSearchResult.AlternateFieldNames alternateNames = new AbstractSearchResult.AlternateFieldNames(){

        @Override
        protected boolean fieldExists(String name) {
            return LuceneSearchResult.this.document.getField(name) != null;
        }
    };

    public LuceneSearchResult(Document document, Optional<HitHighlighter> maybeHighlighter, Function<String, ConfluenceUser> userLookup) {
        this(document, maybeHighlighter, Optional.empty(), userLookup);
    }

    public LuceneSearchResult(Document document, Optional<HitHighlighter> maybeHighlighter, Optional<String> maybeExplanation, Function<String, ConfluenceUser> userLookup) {
        super(userLookup);
        this.document = Objects.requireNonNull(document);
        this.maybeHighlighter = Objects.requireNonNull(maybeHighlighter);
        this.maybeExplanation = Objects.requireNonNull(maybeExplanation);
    }

    @Override
    @HtmlSafe
    public String getResultExcerptWithHighlights() {
        return this.maybeHighlighter.map(hitHighlighter -> hitHighlighter.getSummary(this.getSanitisedContent())).orElseGet(() -> super.getResultExcerptWithHighlights());
    }

    @Override
    @HtmlSafe
    public String getDisplayTitleWithHighlights() {
        return this.maybeHighlighter.map(highlighter -> highlighter.highlightText(this.getDisplayTitle())).orElseGet(() -> super.getDisplayTitleWithHighlights());
    }

    @Override
    public Set<String> getFieldNames() {
        return this.alternateNames.expand(this.document.getFields().stream().map(IndexableField::name).collect(Collectors.toSet()));
    }

    @Override
    public String getFieldValue(String fieldName) {
        return this.document.get(this.alternateNames.resolve(fieldName));
    }

    @Override
    public long getHandleId() {
        return ((HibernateHandle)this.getHandle()).getId();
    }

    @Override
    public Set<String> getFieldValues(String fieldName) {
        return new HashSet<String>(Arrays.asList(this.document.getValues(this.alternateNames.resolve(fieldName))));
    }

    @Override
    public Optional<String> getExplain() {
        return this.maybeExplanation;
    }
}

