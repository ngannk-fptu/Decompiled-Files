/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.Analyzer$TokenStreamComponents
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.Tokenizer
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.synonym;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.synonym.SolrSynonymParser;
import org.apache.lucene.analysis.synonym.SynonymFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.analysis.synonym.WordnetSynonymParser;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.Version;

@Deprecated
final class FSTSynonymFilterFactory
extends TokenFilterFactory
implements ResourceLoaderAware {
    private final boolean ignoreCase;
    private final String tokenizerFactory;
    private final String synonyms;
    private final String format;
    private final boolean expand;
    private final Map<String, String> tokArgs = new HashMap<String, String>();
    private SynonymMap map;

    public FSTSynonymFilterFactory(Map<String, String> args) {
        super(args);
        this.ignoreCase = this.getBoolean(args, "ignoreCase", false);
        this.synonyms = this.require(args, "synonyms");
        this.format = this.get(args, "format");
        this.expand = this.getBoolean(args, "expand", true);
        this.tokenizerFactory = this.get(args, "tokenizerFactory");
        if (this.tokenizerFactory != null) {
            this.assureMatchVersion();
            this.tokArgs.put("luceneMatchVersion", this.getLuceneMatchVersion().toString());
            Iterator<String> itr = args.keySet().iterator();
            while (itr.hasNext()) {
                String key = itr.next();
                this.tokArgs.put(key.replaceAll("^tokenizerFactory\\.", ""), args.get(key));
                itr.remove();
            }
        }
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    @Override
    public TokenStream create(TokenStream input) {
        return this.map.fst == null ? input : new SynonymFilter(input, this.map, this.ignoreCase);
    }

    @Override
    public void inform(ResourceLoader loader) throws IOException {
        block4: {
            final TokenizerFactory factory = this.tokenizerFactory == null ? null : this.loadTokenizerFactory(loader, this.tokenizerFactory);
            Analyzer analyzer = new Analyzer(){

                protected Analyzer.TokenStreamComponents createComponents(String fieldName, Reader reader) {
                    WhitespaceTokenizer tokenizer = factory == null ? new WhitespaceTokenizer(Version.LUCENE_43, reader) : factory.create(reader);
                    Object stream = FSTSynonymFilterFactory.this.ignoreCase ? new LowerCaseFilter(Version.LUCENE_43, (TokenStream)tokenizer) : tokenizer;
                    return new Analyzer.TokenStreamComponents((Tokenizer)tokenizer, (TokenStream)stream);
                }
            };
            try {
                if (this.format == null || this.format.equals("solr")) {
                    this.map = this.loadSolrSynonyms(loader, true, analyzer);
                    break block4;
                }
                if (this.format.equals("wordnet")) {
                    this.map = this.loadWordnetSynonyms(loader, true, analyzer);
                    break block4;
                }
                throw new IllegalArgumentException("Unrecognized synonyms format: " + this.format);
            }
            catch (ParseException e) {
                throw new IOException("Error parsing synonyms file:", e);
            }
        }
    }

    private SynonymMap loadSolrSynonyms(ResourceLoader loader, boolean dedup, Analyzer analyzer) throws IOException, ParseException {
        CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
        SolrSynonymParser parser = new SolrSynonymParser(dedup, this.expand, analyzer);
        File synonymFile = new File(this.synonyms);
        if (synonymFile.exists()) {
            decoder.reset();
            parser.add(new InputStreamReader(loader.openResource(this.synonyms), decoder));
        } else {
            List<String> files = this.splitFileNames(this.synonyms);
            for (String file : files) {
                decoder.reset();
                parser.add(new InputStreamReader(loader.openResource(file), decoder));
            }
        }
        return parser.build();
    }

    private SynonymMap loadWordnetSynonyms(ResourceLoader loader, boolean dedup, Analyzer analyzer) throws IOException, ParseException {
        CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
        WordnetSynonymParser parser = new WordnetSynonymParser(dedup, this.expand, analyzer);
        File synonymFile = new File(this.synonyms);
        if (synonymFile.exists()) {
            decoder.reset();
            parser.add(new InputStreamReader(loader.openResource(this.synonyms), decoder));
        } else {
            List<String> files = this.splitFileNames(this.synonyms);
            for (String file : files) {
                decoder.reset();
                parser.add(new InputStreamReader(loader.openResource(file), decoder));
            }
        }
        return parser.build();
    }

    private TokenizerFactory loadTokenizerFactory(ResourceLoader loader, String cname) throws IOException {
        Class<TokenizerFactory> clazz = loader.findClass(cname, TokenizerFactory.class);
        try {
            TokenizerFactory tokFactory = clazz.getConstructor(Map.class).newInstance(this.tokArgs);
            if (tokFactory instanceof ResourceLoaderAware) {
                ((ResourceLoaderAware)((Object)tokFactory)).inform(loader);
            }
            return tokFactory;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

