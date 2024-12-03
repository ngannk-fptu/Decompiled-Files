/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.internal.search.v2.lucene.LuceneException
 *  com.google.common.collect.ImmutableMap
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.Analyzer$TokenStreamComponents
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.Tokenizer
 *  org.apache.lucene.analysis.charfilter.HTMLStripCharFilter
 *  org.apache.lucene.analysis.charfilter.MappingCharFilter
 *  org.apache.lucene.analysis.charfilter.NormalizeCharMap$Builder
 *  org.apache.lucene.analysis.commongrams.CommonGramsFilter
 *  org.apache.lucene.analysis.compound.DictionaryCompoundWordTokenFilter
 *  org.apache.lucene.analysis.core.KeywordAnalyzer
 *  org.apache.lucene.analysis.core.KeywordTokenizer
 *  org.apache.lucene.analysis.core.LetterTokenizer
 *  org.apache.lucene.analysis.core.LowerCaseFilter
 *  org.apache.lucene.analysis.core.WhitespaceAnalyzer
 *  org.apache.lucene.analysis.core.WhitespaceTokenizer
 *  org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter
 *  org.apache.lucene.analysis.miscellaneous.KeepWordFilter
 *  org.apache.lucene.analysis.ngram.EdgeNGramTokenizer
 *  org.apache.lucene.analysis.ngram.NGramTokenizer
 *  org.apache.lucene.analysis.path.PathHierarchyTokenizer
 *  org.apache.lucene.analysis.pattern.PatternReplaceCharFilter
 *  org.apache.lucene.analysis.pattern.PatternTokenizer
 *  org.apache.lucene.analysis.shingle.ShingleFilter
 *  org.apache.lucene.analysis.standard.ClassicFilter
 *  org.apache.lucene.analysis.standard.ClassicTokenizer
 *  org.apache.lucene.analysis.standard.StandardAnalyzer
 *  org.apache.lucene.analysis.standard.StandardFilter
 *  org.apache.lucene.analysis.standard.StandardTokenizer
 *  org.apache.lucene.analysis.standard.UAX29URLEmailTokenizer
 *  org.apache.lucene.analysis.util.CharArraySet
 *  org.apache.lucene.util.Version
 */
package com.atlassian.confluence.impl.search.v2.mappers;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.impl.search.v2.lucene.analysis.analyzer.exact.ExactAnalyzer;
import com.atlassian.confluence.impl.search.v2.lucene.analysis.analyzer.exact.ExactFilenameAnalyzer;
import com.atlassian.confluence.impl.search.v2.mappers.LuceneAnalyzerMapper;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneException;
import com.atlassian.confluence.plugins.index.api.AnalyzerDescriptor;
import com.atlassian.confluence.plugins.index.api.CharFilterDescriptor;
import com.atlassian.confluence.plugins.index.api.ExactAnalyzerDescriptor;
import com.atlassian.confluence.plugins.index.api.ExactFilenameAnalyzerDescriptor;
import com.atlassian.confluence.plugins.index.api.KeywordAnalyzerDescriptor;
import com.atlassian.confluence.plugins.index.api.MappingAnalyzerDescriptor;
import com.atlassian.confluence.plugins.index.api.StandardAnalyzerDescriptor;
import com.atlassian.confluence.plugins.index.api.TokenFilterDescriptor;
import com.atlassian.confluence.plugins.index.api.TokenizerDescriptor;
import com.atlassian.confluence.plugins.index.api.WhitespaceAnalyzerDescriptor;
import com.atlassian.confluence.search.v2.analysis.ASCIIFoldingTokenFilterDescriptor;
import com.atlassian.confluence.search.v2.analysis.ClassicTokenFilterDescriptor;
import com.atlassian.confluence.search.v2.analysis.ClassicTokenizerDescriptor;
import com.atlassian.confluence.search.v2.analysis.CommonGramsTokenFilterDescriptor;
import com.atlassian.confluence.search.v2.analysis.CompoundWordTokenFilterDescriptor;
import com.atlassian.confluence.search.v2.analysis.HtmlStripCharFilterDescriptor;
import com.atlassian.confluence.search.v2.analysis.KeepWordTokenFilterDescriptor;
import com.atlassian.confluence.search.v2.analysis.KeywordTokenizerDescriptor;
import com.atlassian.confluence.search.v2.analysis.LetterTokenizerDescriptor;
import com.atlassian.confluence.search.v2.analysis.LowerCaseTokenFilterDescriptor;
import com.atlassian.confluence.search.v2.analysis.MappingCharFilterDescriptor;
import com.atlassian.confluence.search.v2.analysis.NGramTokenizerDescriptor;
import com.atlassian.confluence.search.v2.analysis.PathHierarchyTokenizerDescriptor;
import com.atlassian.confluence.search.v2.analysis.PatternReplaceCharFilterDescriptor;
import com.atlassian.confluence.search.v2.analysis.PatternTokenizerDescriptor;
import com.atlassian.confluence.search.v2.analysis.ShingleTokenFilterDescriptor;
import com.atlassian.confluence.search.v2.analysis.StandardTokenFilterDescriptor;
import com.atlassian.confluence.search.v2.analysis.StandardTokenizerDescriptor;
import com.atlassian.confluence.search.v2.analysis.UAXURLEmailTokenizerDescriptor;
import com.atlassian.confluence.search.v2.analysis.WhitespaceTokenizerDescriptor;
import com.google.common.collect.ImmutableMap;
import java.io.Reader;
import java.util.Collection;
import java.util.Map;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.charfilter.HTMLStripCharFilter;
import org.apache.lucene.analysis.charfilter.MappingCharFilter;
import org.apache.lucene.analysis.charfilter.NormalizeCharMap;
import org.apache.lucene.analysis.commongrams.CommonGramsFilter;
import org.apache.lucene.analysis.compound.DictionaryCompoundWordTokenFilter;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.KeywordTokenizer;
import org.apache.lucene.analysis.core.LetterTokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.miscellaneous.KeepWordFilter;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenizer;
import org.apache.lucene.analysis.ngram.NGramTokenizer;
import org.apache.lucene.analysis.path.PathHierarchyTokenizer;
import org.apache.lucene.analysis.pattern.PatternReplaceCharFilter;
import org.apache.lucene.analysis.pattern.PatternTokenizer;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.standard.ClassicFilter;
import org.apache.lucene.analysis.standard.ClassicTokenizer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.standard.UAX29URLEmailTokenizer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

@Internal
public class ConfluenceLuceneAnalyzerMapper
implements LuceneAnalyzerMapper {
    private static final Map<Class<? extends CharFilterDescriptor>, CharFilterFactory> CHAR_FILTER_MAPPER = ImmutableMap.builder().put(HtmlStripCharFilterDescriptor.class, (descriptor, source) -> new HTMLStripCharFilter(source)).put(MappingCharFilterDescriptor.class, (descriptor, source) -> {
        MappingCharFilterDescriptor d = (MappingCharFilterDescriptor)descriptor;
        NormalizeCharMap.Builder builder = new NormalizeCharMap.Builder();
        d.getMap().forEach((arg_0, arg_1) -> ((NormalizeCharMap.Builder)builder).add(arg_0, arg_1));
        return new MappingCharFilter(builder.build(), source);
    }).put(PatternReplaceCharFilterDescriptor.class, (descriptor, source) -> {
        PatternReplaceCharFilterDescriptor d = (PatternReplaceCharFilterDescriptor)descriptor;
        return new PatternReplaceCharFilter(d.getPattern(), d.getReplacement(), source);
    }).build();
    private static final Map<Class<? extends TokenizerDescriptor>, TokenizerFactory> TOKENIZER_MAPPER = ImmutableMap.builder().put(ClassicTokenizerDescriptor.class, (descriptor, source) -> new ClassicTokenizer(Version.LUCENE_44, source)).put(KeywordTokenizerDescriptor.class, (descriptor, source) -> new KeywordTokenizer(source)).put(LetterTokenizerDescriptor.class, (descriptor, source) -> new LetterTokenizer(Version.LUCENE_44, source)).put(NGramTokenizerDescriptor.class, (descriptor, source) -> {
        NGramTokenizerDescriptor d = (NGramTokenizerDescriptor)descriptor;
        return d.isLeadingEdgesOnly() ? new EdgeNGramTokenizer(Version.LUCENE_44, source, d.getMin(), d.getMax()) : new NGramTokenizer(Version.LUCENE_44, source, d.getMin(), d.getMax());
    }).put(PathHierarchyTokenizerDescriptor.class, (descriptor, source) -> {
        PathHierarchyTokenizerDescriptor d = (PathHierarchyTokenizerDescriptor)descriptor;
        return new PathHierarchyTokenizer(source, d.getDelimiter(), d.getReplacement(), d.getSkip());
    }).put(PatternTokenizerDescriptor.class, (descriptor, source) -> {
        PatternTokenizerDescriptor d = (PatternTokenizerDescriptor)descriptor;
        return new PatternTokenizer(source, d.getPattern(), d.getGroup());
    }).put(StandardTokenizerDescriptor.class, (descriptor, source) -> new StandardTokenizer(Version.LUCENE_44, source)).put(UAXURLEmailTokenizerDescriptor.class, (descriptor, source) -> new UAX29URLEmailTokenizer(Version.LUCENE_44, source)).put(WhitespaceTokenizerDescriptor.class, (descriptor, source) -> new WhitespaceTokenizer(Version.LUCENE_44, source)).build();
    private static final Map<Class<? extends TokenFilterDescriptor>, TokenFilterFactory> TOKEN_FILTER_MAPPER = ImmutableMap.builder().put(ASCIIFoldingTokenFilterDescriptor.class, (descriptor, tokenStream) -> new ASCIIFoldingFilter(tokenStream)).put(StandardTokenFilterDescriptor.class, (descriptor, tokenStream) -> new StandardFilter(Version.LUCENE_44, tokenStream)).put(LowerCaseTokenFilterDescriptor.class, (descriptor, tokenStream) -> new LowerCaseFilter(Version.LUCENE_44, tokenStream)).put(ClassicTokenFilterDescriptor.class, (descriptor, tokenStream) -> new ClassicFilter(tokenStream)).put(CommonGramsTokenFilterDescriptor.class, (descriptor, tokenStream) -> {
        CommonGramsTokenFilterDescriptor d = (CommonGramsTokenFilterDescriptor)descriptor;
        return new CommonGramsFilter(Version.LUCENE_44, tokenStream, new CharArraySet(Version.LUCENE_44, d.getCommonWords(), true));
    }).put(CompoundWordTokenFilterDescriptor.class, (descriptor, tokenStream) -> {
        CompoundWordTokenFilterDescriptor d = (CompoundWordTokenFilterDescriptor)descriptor;
        return new DictionaryCompoundWordTokenFilter(Version.LUCENE_44, tokenStream, new CharArraySet(Version.LUCENE_44, d.getDictionary(), true));
    }).put(KeepWordTokenFilterDescriptor.class, (descriptor, tokenStream) -> {
        KeepWordTokenFilterDescriptor d = (KeepWordTokenFilterDescriptor)descriptor;
        return new KeepWordFilter(Version.LUCENE_44, tokenStream, new CharArraySet(Version.LUCENE_44, d.getDictionary(), true));
    }).put(ShingleTokenFilterDescriptor.class, (descriptor, tokenStream) -> {
        ShingleTokenFilterDescriptor d = (ShingleTokenFilterDescriptor)descriptor;
        return new ShingleFilter(tokenStream, d.getMin(), d.getMax());
    }).build();
    private final Map<Class<? extends MappingAnalyzerDescriptor>, Analyzer> analyzerMap;

    public ConfluenceLuceneAnalyzerMapper() {
        this((Map<Class<? extends MappingAnalyzerDescriptor>, Analyzer>)ImmutableMap.of());
    }

    public ConfluenceLuceneAnalyzerMapper(Map<Class<? extends MappingAnalyzerDescriptor>, Analyzer> additionalAnalyzerMap) {
        this.analyzerMap = ImmutableMap.builder().put(StandardAnalyzerDescriptor.class, (Object)new StandardAnalyzer(Version.LUCENE_44)).put(WhitespaceAnalyzerDescriptor.class, (Object)new WhitespaceAnalyzer(Version.LUCENE_44)).put(KeywordAnalyzerDescriptor.class, (Object)new KeywordAnalyzer()).put(ExactFilenameAnalyzerDescriptor.class, (Object)new ExactFilenameAnalyzer(Version.LUCENE_44)).put(ExactAnalyzerDescriptor.class, (Object)new ExactAnalyzer(Version.LUCENE_44)).putAll(additionalAnalyzerMap).build();
    }

    @Override
    public Analyzer map(MappingAnalyzerDescriptor mappingAnalyzerDescriptor) {
        Class<?> clazz = mappingAnalyzerDescriptor.getClass();
        if (clazz.equals(AnalyzerDescriptor.class)) {
            final AnalyzerDescriptor analyzerDescriptor = (AnalyzerDescriptor)mappingAnalyzerDescriptor;
            return new Analyzer(){

                protected Analyzer.TokenStreamComponents createComponents(String fieldName, Reader reader) {
                    Reader source = ConfluenceLuceneAnalyzerMapper.this.applyCharFilters(analyzerDescriptor.getCharFilters(), reader);
                    Tokenizer tokenizer = ConfluenceLuceneAnalyzerMapper.this.createTokenizer(analyzerDescriptor.getTokenizer(), source);
                    return new Analyzer.TokenStreamComponents(tokenizer, ConfluenceLuceneAnalyzerMapper.this.applyTokenFilters(analyzerDescriptor.getTokenFilters(), tokenizer));
                }
            };
        }
        Analyzer analyzer = this.analyzerMap.getOrDefault(clazz, null);
        if (analyzer == null) {
            throw new LuceneException(String.format("There is no Lucene analyzer mapped for the descriptor: %s", mappingAnalyzerDescriptor.getClass()));
        }
        return analyzer;
    }

    private Reader applyCharFilters(Collection<CharFilterDescriptor> descriptors, Reader reader) {
        for (CharFilterDescriptor descriptor : descriptors) {
            CharFilterFactory filterFactory = CHAR_FILTER_MAPPER.get(descriptor.getClass());
            if (filterFactory == null) continue;
            reader = filterFactory.create(descriptor, reader);
        }
        return reader;
    }

    private Tokenizer createTokenizer(TokenizerDescriptor tokenizerDescriptor, Reader source) {
        TokenizerFactory factory = TOKENIZER_MAPPER.get(tokenizerDescriptor.getClass());
        if (factory == null) {
            throw new IllegalArgumentException("No tokenizer mapper for " + tokenizerDescriptor.getClass());
        }
        return factory.create(tokenizerDescriptor, source);
    }

    private TokenStream applyTokenFilters(Collection<TokenFilterDescriptor> descriptors, Tokenizer tokenizer) {
        Tokenizer tokenStream = tokenizer;
        for (TokenFilterDescriptor descriptor : descriptors) {
            TokenFilterFactory filterFactory = TOKEN_FILTER_MAPPER.get(descriptor.getClass());
            if (filterFactory == null) continue;
            tokenStream = filterFactory.create(descriptor, (TokenStream)tokenStream);
        }
        return tokenStream;
    }

    @FunctionalInterface
    private static interface TokenFilterFactory {
        public TokenFilter create(TokenFilterDescriptor var1, TokenStream var2);
    }

    @FunctionalInterface
    private static interface TokenizerFactory {
        public Tokenizer create(TokenizerDescriptor var1, Reader var2);
    }

    @FunctionalInterface
    private static interface CharFilterFactory {
        public Reader create(CharFilterDescriptor var1, Reader var2);
    }
}

