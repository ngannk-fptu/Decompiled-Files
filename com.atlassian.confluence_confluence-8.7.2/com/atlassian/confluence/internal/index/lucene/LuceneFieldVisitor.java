/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.CachingTokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.document.BinaryDocValuesField
 *  org.apache.lucene.document.DoubleField
 *  org.apache.lucene.document.Field
 *  org.apache.lucene.document.Field$Store
 *  org.apache.lucene.document.FieldType
 *  org.apache.lucene.document.FloatField
 *  org.apache.lucene.document.IntField
 *  org.apache.lucene.document.LongField
 *  org.apache.lucene.document.NumericDocValuesField
 *  org.apache.lucene.document.SortedDocValuesField
 *  org.apache.lucene.document.StoredField
 *  org.apache.lucene.document.StringField
 *  org.apache.lucene.document.TextField
 *  org.apache.lucene.index.IndexableField
 *  org.apache.lucene.util.BytesRef
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.index.lucene;

import com.atlassian.confluence.impl.search.v2.mappers.LuceneAnalyzerMapper;
import com.atlassian.confluence.internal.search.SearchLanguageProvider;
import com.atlassian.confluence.plugins.index.api.AnalyzerDescriptorProvider;
import com.atlassian.confluence.plugins.index.api.DocValuesFieldDescriptor;
import com.atlassian.confluence.plugins.index.api.DoubleFieldDescriptor;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.plugins.index.api.FieldVisitor;
import com.atlassian.confluence.plugins.index.api.FloatFieldDescriptor;
import com.atlassian.confluence.plugins.index.api.IntFieldDescriptor;
import com.atlassian.confluence.plugins.index.api.LongFieldDescriptor;
import com.atlassian.confluence.plugins.index.api.MappingAnalyzerDescriptor;
import com.atlassian.confluence.plugins.index.api.NumericDocValuesFieldDescriptor;
import com.atlassian.confluence.plugins.index.api.SortedDocValuesFieldDescriptor;
import com.atlassian.confluence.plugins.index.api.StoredFieldDescriptor;
import com.atlassian.confluence.plugins.index.api.StringFieldDescriptor;
import com.atlassian.confluence.plugins.index.api.TextFieldDescriptor;
import com.atlassian.confluence.search.SearchLanguage;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CachingTokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.BinaryDocValuesField;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.util.BytesRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LuceneFieldVisitor
implements FieldVisitor<IndexableField> {
    private static final Logger log = LoggerFactory.getLogger(LuceneFieldVisitor.class);
    private final LuceneAnalyzerMapper luceneAnalyzerMapper;
    private final SearchLanguageProvider searchLanguageProvider;

    public LuceneFieldVisitor(LuceneAnalyzerMapper luceneAnalyzerMapper, SearchLanguageProvider searchLanguageProvider) {
        this.luceneAnalyzerMapper = Objects.requireNonNull(luceneAnalyzerMapper, "luceneAnalyzerMapper");
        this.searchLanguageProvider = Objects.requireNonNull(searchLanguageProvider, "searchLanguageProvider");
    }

    @Override
    public IndexableField visit(FieldDescriptor fieldDescriptor) {
        FieldType fieldType = new FieldType();
        fieldType.setStored(fieldDescriptor.getStore().isStored());
        fieldType.setIndexed(fieldDescriptor.getIndex() != FieldDescriptor.Index.NO);
        fieldType.setTokenized(fieldDescriptor.getIndex() == FieldDescriptor.Index.ANALYZED);
        return new Field(fieldDescriptor.getName(), fieldDescriptor.getValue(), fieldType);
    }

    @Override
    public IndexableField visit(StringFieldDescriptor stringFieldDescriptor) {
        return new StringField(stringFieldDescriptor.getName(), stringFieldDescriptor.getValue(), this.store(stringFieldDescriptor));
    }

    @Override
    public IndexableField visit(TextFieldDescriptor textFieldDescriptor) {
        SearchLanguage language;
        String text = textFieldDescriptor.getValue();
        TextField field = new TextField(textFieldDescriptor.getName(), text, this.store(textFieldDescriptor));
        AnalyzerDescriptorProvider analyzerDescriptorProvider = textFieldDescriptor.getAnalyzerProvider();
        Optional<MappingAnalyzerDescriptor> analyzerDescriptor = analyzerDescriptorProvider.getAnalyzer(language = this.searchLanguageProvider.get());
        if (analyzerDescriptor.isPresent()) {
            Analyzer analyzer = this.luceneAnalyzerMapper.map(analyzerDescriptor.get());
            TokenStream tokenStream = null;
            try {
                tokenStream = this.cacheTokenStream(analyzer.tokenStream(textFieldDescriptor.getName(), text));
            }
            catch (IOException e) {
                log.error("Error when tokenizing {} using {}", (Object)textFieldDescriptor.getName(), (Object)e);
            }
            field.setTokenStream(tokenStream);
        }
        return field;
    }

    private TokenStream cacheTokenStream(TokenStream tokenStream) throws IOException {
        tokenStream.reset();
        CachingTokenFilter cached = new CachingTokenFilter(tokenStream);
        cached.incrementToken();
        return cached;
    }

    @Override
    public IndexableField visit(IntFieldDescriptor intFieldDescriptor) {
        return new IntField(intFieldDescriptor.getName(), intFieldDescriptor.intValue(), this.store(intFieldDescriptor));
    }

    @Override
    public IndexableField visit(LongFieldDescriptor longFieldDescriptor) {
        return new LongField(longFieldDescriptor.getName(), longFieldDescriptor.longValue(), this.store(longFieldDescriptor));
    }

    @Override
    public IndexableField visit(FloatFieldDescriptor floatFieldDescriptor) {
        return new FloatField(floatFieldDescriptor.getName(), floatFieldDescriptor.floatValue(), this.store(floatFieldDescriptor));
    }

    @Override
    public IndexableField visit(DoubleFieldDescriptor doubleFieldDescriptor) {
        return new DoubleField(doubleFieldDescriptor.getName(), doubleFieldDescriptor.doubleValue(), this.store(doubleFieldDescriptor));
    }

    @Override
    public IndexableField visit(StoredFieldDescriptor storedFieldDescriptor) {
        return new StoredField(storedFieldDescriptor.getName(), storedFieldDescriptor.getValue());
    }

    @Override
    public IndexableField visit(DocValuesFieldDescriptor docValuesFieldDescriptor) {
        return new BinaryDocValuesField(docValuesFieldDescriptor.getName(), new BytesRef(docValuesFieldDescriptor.bytesValue()));
    }

    @Override
    public IndexableField visit(SortedDocValuesFieldDescriptor sortedDocValuesFieldDescriptor) {
        return new SortedDocValuesField(sortedDocValuesFieldDescriptor.getName(), new BytesRef(sortedDocValuesFieldDescriptor.bytesValue()));
    }

    @Override
    public IndexableField visit(NumericDocValuesFieldDescriptor numericDocValuesFieldDescriptor) {
        return new NumericDocValuesField(numericDocValuesFieldDescriptor.getName(), numericDocValuesFieldDescriptor.longValue());
    }

    private Field.Store store(FieldDescriptor fieldDescriptor) {
        return fieldDescriptor.getStore().isStored() ? Field.Store.YES : Field.Store.NO;
    }
}

