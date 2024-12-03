/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 */
package org.apache.lucene.analysis.synonym;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.synonym.SlowSynonymFilter;
import org.apache.lucene.analysis.synonym.SlowSynonymMap;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;
import org.apache.lucene.analysis.util.TokenizerFactory;

@Deprecated
final class SlowSynonymFilterFactory
extends TokenFilterFactory
implements ResourceLoaderAware {
    private final String synonyms;
    private final boolean ignoreCase;
    private final boolean expand;
    private final String tf;
    private final Map<String, String> tokArgs = new HashMap<String, String>();
    private SlowSynonymMap synMap;

    public SlowSynonymFilterFactory(Map<String, String> args) {
        super(args);
        this.synonyms = this.require(args, "synonyms");
        this.ignoreCase = this.getBoolean(args, "ignoreCase", false);
        this.expand = this.getBoolean(args, "expand", true);
        this.tf = this.get(args, "tokenizerFactory");
        if (this.tf != null) {
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
    public void inform(ResourceLoader loader) throws IOException {
        TokenizerFactory tokFactory = null;
        if (this.tf != null) {
            tokFactory = this.loadTokenizerFactory(loader, this.tf);
        }
        Iterable<String> wlist = this.loadRules(this.synonyms, loader);
        this.synMap = new SlowSynonymMap(this.ignoreCase);
        SlowSynonymFilterFactory.parseRules(wlist, this.synMap, "=>", ",", this.expand, tokFactory);
    }

    protected Iterable<String> loadRules(String synonyms, ResourceLoader loader) throws IOException {
        List<String> wlist = null;
        File synonymFile = new File(synonyms);
        if (synonymFile.exists()) {
            wlist = this.getLines(loader, synonyms);
        } else {
            List<String> files = this.splitFileNames(synonyms);
            wlist = new ArrayList<String>();
            for (String file : files) {
                List<String> lines = this.getLines(loader, file.trim());
                wlist.addAll(lines);
            }
        }
        return wlist;
    }

    static void parseRules(Iterable<String> rules, SlowSynonymMap map, String mappingSep, String synSep, boolean expansion, TokenizerFactory tokFactory) throws IOException {
        int count = 0;
        for (String rule : rules) {
            List<List<String>> target;
            List<List<String>> source;
            List<String> mapping = SlowSynonymFilterFactory.splitSmart(rule, mappingSep, false);
            if (mapping.size() > 2) {
                throw new IllegalArgumentException("Invalid Synonym Rule:" + rule);
            }
            if (mapping.size() == 2) {
                source = SlowSynonymFilterFactory.getSynList(mapping.get(0), synSep, tokFactory);
                target = SlowSynonymFilterFactory.getSynList(mapping.get(1), synSep, tokFactory);
            } else {
                source = SlowSynonymFilterFactory.getSynList(mapping.get(0), synSep, tokFactory);
                if (expansion) {
                    target = source;
                } else {
                    target = new ArrayList<List<String>>(1);
                    target.add(source.get(0));
                }
            }
            boolean includeOrig = false;
            for (List<String> fromToks : source) {
                ++count;
                for (List<String> toToks : target) {
                    map.add(fromToks, SlowSynonymMap.makeTokens(toToks), includeOrig, true);
                }
            }
        }
    }

    private static List<List<String>> getSynList(String str, String separator, TokenizerFactory tokFactory) throws IOException {
        List<String> strList = SlowSynonymFilterFactory.splitSmart(str, separator, false);
        ArrayList<List<String>> synList = new ArrayList<List<String>>();
        for (String toks : strList) {
            List<String> tokList = tokFactory == null ? SlowSynonymFilterFactory.splitWS(toks, true) : SlowSynonymFilterFactory.splitByTokenizer(toks, tokFactory);
            synList.add(tokList);
        }
        return synList;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static List<String> splitByTokenizer(String source, TokenizerFactory tokFactory) throws IOException {
        StringReader reader = new StringReader(source);
        TokenStream ts = SlowSynonymFilterFactory.loadTokenizer(tokFactory, reader);
        ArrayList<String> tokList = new ArrayList<String>();
        try {
            CharTermAttribute termAtt = (CharTermAttribute)ts.addAttribute(CharTermAttribute.class);
            ts.reset();
            while (ts.incrementToken()) {
                if (termAtt.length() <= 0) continue;
                tokList.add(termAtt.toString());
            }
        }
        finally {
            reader.close();
        }
        return tokList;
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

    private static TokenStream loadTokenizer(TokenizerFactory tokFactory, Reader reader) {
        return tokFactory.create(reader);
    }

    public SlowSynonymMap getSynonymMap() {
        return this.synMap;
    }

    public SlowSynonymFilter create(TokenStream input) {
        return new SlowSynonymFilter(input, this.synMap);
    }

    public static List<String> splitWS(String s, boolean decode) {
        ArrayList<String> lst = new ArrayList<String>(2);
        StringBuilder sb = new StringBuilder();
        int pos = 0;
        int end = s.length();
        while (pos < end) {
            int ch;
            if (Character.isWhitespace((char)(ch = s.charAt(pos++)))) {
                if (sb.length() <= 0) continue;
                lst.add(sb.toString());
                sb = new StringBuilder();
                continue;
            }
            if (ch == 92) {
                if (!decode) {
                    sb.append((char)ch);
                }
                if (pos >= end) break;
                ch = s.charAt(pos++);
                if (decode) {
                    switch (ch) {
                        case 110: {
                            ch = 10;
                            break;
                        }
                        case 116: {
                            ch = 9;
                            break;
                        }
                        case 114: {
                            ch = 13;
                            break;
                        }
                        case 98: {
                            ch = 8;
                            break;
                        }
                        case 102: {
                            ch = 12;
                        }
                    }
                }
            }
            sb.append((char)ch);
        }
        if (sb.length() > 0) {
            lst.add(sb.toString());
        }
        return lst;
    }

    public static List<String> splitSmart(String s, String separator, boolean decode) {
        ArrayList<String> lst = new ArrayList<String>(2);
        StringBuilder sb = new StringBuilder();
        int pos = 0;
        int end = s.length();
        while (pos < end) {
            int ch;
            if (s.startsWith(separator, pos)) {
                if (sb.length() > 0) {
                    lst.add(sb.toString());
                    sb = new StringBuilder();
                }
                pos += separator.length();
                continue;
            }
            if ((ch = s.charAt(pos++)) == 92) {
                if (!decode) {
                    sb.append((char)ch);
                }
                if (pos >= end) break;
                ch = s.charAt(pos++);
                if (decode) {
                    switch (ch) {
                        case 110: {
                            ch = 10;
                            break;
                        }
                        case 116: {
                            ch = 9;
                            break;
                        }
                        case 114: {
                            ch = 13;
                            break;
                        }
                        case 98: {
                            ch = 8;
                            break;
                        }
                        case 102: {
                            ch = 12;
                        }
                    }
                }
            }
            sb.append((char)ch);
        }
        if (sb.length() > 0) {
            lst.add(sb.toString());
        }
        return lst;
    }
}

