/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.util.IOUtils
 */
package org.apache.lucene.analysis.hunspell;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.hunspell.HunspellDictionary;
import org.apache.lucene.analysis.hunspell.HunspellStemFilter;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;
import org.apache.lucene.util.IOUtils;

public class HunspellStemFilterFactory
extends TokenFilterFactory
implements ResourceLoaderAware {
    private static final String PARAM_DICTIONARY = "dictionary";
    private static final String PARAM_AFFIX = "affix";
    private static final String PARAM_IGNORE_CASE = "ignoreCase";
    private static final String PARAM_STRICT_AFFIX_PARSING = "strictAffixParsing";
    private static final String PARAM_RECURSION_CAP = "recursionCap";
    private final String dictionaryArg;
    private final String affixFile;
    private final boolean ignoreCase;
    private final boolean strictAffixParsing;
    private HunspellDictionary dictionary;
    private int recursionCap;

    public HunspellStemFilterFactory(Map<String, String> args) {
        super(args);
        this.assureMatchVersion();
        this.dictionaryArg = this.require(args, PARAM_DICTIONARY);
        this.affixFile = this.get(args, PARAM_AFFIX);
        this.ignoreCase = this.getBoolean(args, PARAM_IGNORE_CASE, false);
        this.strictAffixParsing = this.getBoolean(args, PARAM_STRICT_AFFIX_PARSING, true);
        this.recursionCap = this.getInt(args, PARAM_RECURSION_CAP, 2);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    @Override
    public void inform(ResourceLoader loader) throws IOException {
        String[] dictionaryFiles = this.dictionaryArg.split(",");
        InputStream affix = null;
        ArrayList<InputStream> dictionaries = new ArrayList<InputStream>();
        try {
            dictionaries = new ArrayList();
            for (String file : dictionaryFiles) {
                dictionaries.add(loader.openResource(file));
            }
            affix = loader.openResource(this.affixFile);
            this.dictionary = new HunspellDictionary(affix, dictionaries, this.luceneMatchVersion, this.ignoreCase, this.strictAffixParsing);
        }
        catch (ParseException e) {
            try {
                throw new IOException("Unable to load hunspell data! [dictionary=" + this.dictionaryArg + ",affix=" + this.affixFile + "]", e);
            }
            catch (Throwable throwable) {
                IOUtils.closeWhileHandlingException((Closeable[])new Closeable[]{affix});
                IOUtils.closeWhileHandlingException(dictionaries);
                throw throwable;
            }
        }
        IOUtils.closeWhileHandlingException((Closeable[])new Closeable[]{affix});
        IOUtils.closeWhileHandlingException(dictionaries);
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new HunspellStemFilter(tokenStream, this.dictionary, this.recursionCap);
    }
}

