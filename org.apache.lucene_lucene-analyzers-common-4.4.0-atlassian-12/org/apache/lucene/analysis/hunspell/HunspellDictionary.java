/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.hunspell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.apache.lucene.analysis.hunspell.HunspellAffix;
import org.apache.lucene.analysis.hunspell.HunspellWord;
import org.apache.lucene.analysis.util.CharArrayMap;
import org.apache.lucene.util.Version;

public class HunspellDictionary {
    static final HunspellWord NOFLAGS = new HunspellWord();
    private static final String ALIAS_KEY = "AF";
    private static final String PREFIX_KEY = "PFX";
    private static final String SUFFIX_KEY = "SFX";
    private static final String FLAG_KEY = "FLAG";
    private static final String NUM_FLAG_TYPE = "num";
    private static final String UTF8_FLAG_TYPE = "UTF-8";
    private static final String LONG_FLAG_TYPE = "long";
    private static final String PREFIX_CONDITION_REGEX_PATTERN = "%s.*";
    private static final String SUFFIX_CONDITION_REGEX_PATTERN = ".*%s";
    private static final boolean IGNORE_CASE_DEFAULT = false;
    private static final boolean STRICT_AFFIX_PARSING_DEFAULT = true;
    private CharArrayMap<List<HunspellWord>> words;
    private CharArrayMap<List<HunspellAffix>> prefixes;
    private CharArrayMap<List<HunspellAffix>> suffixes;
    private FlagParsingStrategy flagParsingStrategy = new SimpleFlagParsingStrategy();
    private boolean ignoreCase = false;
    private final Version version;
    private String[] aliases;
    private int aliasCount = 0;

    public HunspellDictionary(InputStream affix, InputStream dictionary, Version version) throws IOException, ParseException {
        this(affix, Arrays.asList(dictionary), version, false);
    }

    public HunspellDictionary(InputStream affix, InputStream dictionary, Version version, boolean ignoreCase) throws IOException, ParseException {
        this(affix, Arrays.asList(dictionary), version, ignoreCase);
    }

    public HunspellDictionary(InputStream affix, List<InputStream> dictionaries, Version version, boolean ignoreCase) throws IOException, ParseException {
        this(affix, dictionaries, version, ignoreCase, true);
    }

    public HunspellDictionary(InputStream affix, List<InputStream> dictionaries, Version version, boolean ignoreCase, boolean strictAffixParsing) throws IOException, ParseException {
        this.version = version;
        this.ignoreCase = ignoreCase;
        String encoding = this.getDictionaryEncoding(affix);
        CharsetDecoder decoder = this.getJavaEncoding(encoding);
        this.readAffixFile(affix, decoder, strictAffixParsing);
        this.words = new CharArrayMap(version, 65535, this.ignoreCase);
        for (InputStream dictionary : dictionaries) {
            this.readDictionaryFile(dictionary, decoder);
        }
    }

    public List<HunspellWord> lookupWord(char[] word, int offset, int length) {
        return this.words.get(word, offset, length);
    }

    public List<HunspellAffix> lookupPrefix(char[] word, int offset, int length) {
        return this.prefixes.get(word, offset, length);
    }

    public List<HunspellAffix> lookupSuffix(char[] word, int offset, int length) {
        return this.suffixes.get(word, offset, length);
    }

    private void readAffixFile(InputStream affixStream, CharsetDecoder decoder, boolean strict) throws IOException, ParseException {
        this.prefixes = new CharArrayMap(this.version, 8, this.ignoreCase);
        this.suffixes = new CharArrayMap(this.version, 8, this.ignoreCase);
        LineNumberReader reader = new LineNumberReader(new InputStreamReader(affixStream, decoder));
        String line = null;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith(ALIAS_KEY)) {
                this.parseAlias(line);
                continue;
            }
            if (line.startsWith(PREFIX_KEY)) {
                this.parseAffix(this.prefixes, line, reader, PREFIX_CONDITION_REGEX_PATTERN, strict);
                continue;
            }
            if (line.startsWith(SUFFIX_KEY)) {
                this.parseAffix(this.suffixes, line, reader, SUFFIX_CONDITION_REGEX_PATTERN, strict);
                continue;
            }
            if (!line.startsWith(FLAG_KEY)) continue;
            this.flagParsingStrategy = this.getFlagParsingStrategy(line);
        }
    }

    private void parseAffix(CharArrayMap<List<HunspellAffix>> affixes, String header, LineNumberReader reader, String conditionPattern, boolean strict) throws IOException, ParseException {
        String[] args = header.split("\\s+");
        boolean crossProduct = args[2].equals("Y");
        int numLines = Integer.parseInt(args[3]);
        for (int i = 0; i < numLines; ++i) {
            String line = reader.readLine();
            String[] ruleArgs = line.split("\\s+");
            if (ruleArgs.length < 5) {
                if (!strict) continue;
                throw new ParseException("The affix file contains a rule with less than five elements", reader.getLineNumber());
            }
            HunspellAffix affix = new HunspellAffix();
            affix.setFlag(this.flagParsingStrategy.parseFlag(ruleArgs[1]));
            affix.setStrip(ruleArgs[2].equals("0") ? "" : ruleArgs[2]);
            String affixArg = ruleArgs[3];
            int flagSep = affixArg.lastIndexOf(47);
            if (flagSep != -1) {
                String flagPart = affixArg.substring(flagSep + 1);
                if (this.aliasCount > 0) {
                    flagPart = this.getAliasValue(Integer.parseInt(flagPart));
                }
                char[] appendFlags = this.flagParsingStrategy.parseFlags(flagPart);
                Arrays.sort(appendFlags);
                affix.setAppendFlags(appendFlags);
                affix.setAppend(affixArg.substring(0, flagSep));
            } else {
                affix.setAppend(affixArg);
            }
            String condition = ruleArgs[4];
            affix.setCondition(condition, String.format(Locale.ROOT, conditionPattern, condition));
            affix.setCrossProduct(crossProduct);
            List<HunspellAffix> list = affixes.get(affix.getAppend());
            if (list == null) {
                list = new ArrayList<HunspellAffix>();
                affixes.put(affix.getAppend(), list);
            }
            list.add(affix);
        }
    }

    private String getDictionaryEncoding(InputStream affix) throws IOException, ParseException {
        StringBuilder encoding;
        block3: {
            int ch;
            encoding = new StringBuilder();
            do {
                encoding.setLength(0);
                while ((ch = affix.read()) >= 0 && ch != 10) {
                    if (ch == 13) continue;
                    encoding.append((char)ch);
                }
                if (encoding.length() != 0 && encoding.charAt(0) != '#' && encoding.toString().trim().length() != 0) break block3;
            } while (ch >= 0);
            throw new ParseException("Unexpected end of affix file.", 0);
        }
        if ("SET ".equals(encoding.substring(0, 4))) {
            return encoding.substring(4).trim();
        }
        throw new ParseException("The first non-comment line in the affix file must be a 'SET charset', was: '" + encoding + "'", 0);
    }

    private CharsetDecoder getJavaEncoding(String encoding) {
        Charset charset = Charset.forName(encoding);
        return charset.newDecoder();
    }

    private FlagParsingStrategy getFlagParsingStrategy(String flagLine) {
        String flagType = flagLine.substring(5);
        if (NUM_FLAG_TYPE.equals(flagType)) {
            return new NumFlagParsingStrategy();
        }
        if (UTF8_FLAG_TYPE.equals(flagType)) {
            return new SimpleFlagParsingStrategy();
        }
        if (LONG_FLAG_TYPE.equals(flagType)) {
            return new DoubleASCIIFlagParsingStrategy();
        }
        throw new IllegalArgumentException("Unknown flag type: " + flagType);
    }

    private void readDictionaryFile(InputStream dictionary, CharsetDecoder decoder) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(dictionary, decoder));
        String line = reader.readLine();
        int numEntries = Integer.parseInt(line);
        while ((line = reader.readLine()) != null) {
            String entry;
            HunspellWord wordForm;
            int flagSep = line.lastIndexOf(47);
            if (flagSep == -1) {
                wordForm = NOFLAGS;
                entry = line;
            } else {
                int end = line.indexOf(9, flagSep);
                if (end == -1) {
                    end = line.length();
                }
                String flagPart = line.substring(flagSep + 1, end);
                if (this.aliasCount > 0) {
                    flagPart = this.getAliasValue(Integer.parseInt(flagPart));
                }
                wordForm = new HunspellWord(this.flagParsingStrategy.parseFlags(flagPart));
                Arrays.sort(wordForm.getFlags());
                entry = line.substring(0, flagSep);
            }
            if (this.ignoreCase) {
                entry = entry.toLowerCase(Locale.ROOT);
            }
            ArrayList<HunspellWord> entries = new ArrayList<HunspellWord>();
            entries.add(wordForm);
            this.words.put(entry, (List<HunspellWord>)entries);
        }
    }

    public Version getVersion() {
        return this.version;
    }

    private void parseAlias(String line) {
        String[] ruleArgs = line.split("\\s+");
        if (this.aliases == null) {
            int count = Integer.parseInt(ruleArgs[1]);
            this.aliases = new String[count];
        } else {
            this.aliases[this.aliasCount++] = ruleArgs[1];
        }
    }

    private String getAliasValue(int id) {
        try {
            return this.aliases[id - 1];
        }
        catch (IndexOutOfBoundsException ex) {
            throw new IllegalArgumentException("Bad flag alias number:" + id, ex);
        }
    }

    public boolean isIgnoreCase() {
        return this.ignoreCase;
    }

    private static class DoubleASCIIFlagParsingStrategy
    extends FlagParsingStrategy {
        private DoubleASCIIFlagParsingStrategy() {
        }

        @Override
        public char[] parseFlags(String rawFlags) {
            if (rawFlags.length() == 0) {
                return new char[0];
            }
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < rawFlags.length(); i += 2) {
                char cookedFlag = (char)(rawFlags.charAt(i) + rawFlags.charAt(i + 1));
                builder.append(cookedFlag);
            }
            char[] flags = new char[builder.length()];
            builder.getChars(0, builder.length(), flags, 0);
            return flags;
        }
    }

    private static class NumFlagParsingStrategy
    extends FlagParsingStrategy {
        private NumFlagParsingStrategy() {
        }

        @Override
        public char[] parseFlags(String rawFlags) {
            String[] rawFlagParts = rawFlags.trim().split(",");
            char[] flags = new char[rawFlagParts.length];
            for (int i = 0; i < rawFlagParts.length; ++i) {
                flags[i] = (char)Integer.parseInt(rawFlagParts[i].replaceAll("[^0-9]", ""));
            }
            return flags;
        }
    }

    private static class SimpleFlagParsingStrategy
    extends FlagParsingStrategy {
        private SimpleFlagParsingStrategy() {
        }

        @Override
        public char[] parseFlags(String rawFlags) {
            return rawFlags.toCharArray();
        }
    }

    private static abstract class FlagParsingStrategy {
        private FlagParsingStrategy() {
        }

        char parseFlag(String rawFlag) {
            return this.parseFlags(rawFlag)[0];
        }

        abstract char[] parseFlags(String var1);
    }
}

