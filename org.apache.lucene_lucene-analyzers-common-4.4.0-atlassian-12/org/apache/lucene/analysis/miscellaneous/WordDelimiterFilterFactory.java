/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.miscellaneous;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterFilter;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterIterator;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class WordDelimiterFilterFactory
extends TokenFilterFactory
implements ResourceLoaderAware {
    public static final String PROTECTED_TOKENS = "protected";
    public static final String TYPES = "types";
    private final String wordFiles;
    private final String types;
    private final int flags;
    byte[] typeTable = null;
    private CharArraySet protectedWords = null;
    private static Pattern typePattern = Pattern.compile("(.*)\\s*=>\\s*(.*)\\s*$");
    char[] out = new char[256];

    public WordDelimiterFilterFactory(Map<String, String> args) {
        super(args);
        int flags = 0;
        if (this.getInt(args, "generateWordParts", 1) != 0) {
            flags |= 1;
        }
        if (this.getInt(args, "generateNumberParts", 1) != 0) {
            flags |= 2;
        }
        if (this.getInt(args, "catenateWords", 0) != 0) {
            flags |= 4;
        }
        if (this.getInt(args, "catenateNumbers", 0) != 0) {
            flags |= 8;
        }
        if (this.getInt(args, "catenateAll", 0) != 0) {
            flags |= 0x10;
        }
        if (this.getInt(args, "splitOnCaseChange", 1) != 0) {
            flags |= 0x40;
        }
        if (this.getInt(args, "splitOnNumerics", 1) != 0) {
            flags |= 0x80;
        }
        if (this.getInt(args, "preserveOriginal", 0) != 0) {
            flags |= 0x20;
        }
        if (this.getInt(args, "stemEnglishPossessive", 1) != 0) {
            flags |= 0x100;
        }
        this.wordFiles = this.get(args, PROTECTED_TOKENS);
        this.types = this.get(args, TYPES);
        this.flags = flags;
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    @Override
    public void inform(ResourceLoader loader) throws IOException {
        if (this.wordFiles != null) {
            this.protectedWords = this.getWordSet(loader, this.wordFiles, false);
        }
        if (this.types != null) {
            List<String> files = this.splitFileNames(this.types);
            ArrayList<String> wlist = new ArrayList<String>();
            for (String file : files) {
                List<String> lines = this.getLines(loader, file.trim());
                wlist.addAll(lines);
            }
            this.typeTable = this.parseTypes(wlist);
        }
    }

    public WordDelimiterFilter create(TokenStream input) {
        return new WordDelimiterFilter(input, this.typeTable == null ? WordDelimiterIterator.DEFAULT_WORD_DELIM_TABLE : this.typeTable, this.flags, this.protectedWords);
    }

    private byte[] parseTypes(List<String> rules) {
        TreeMap<Character, Byte> typeMap = new TreeMap<Character, Byte>();
        for (String rule : rules) {
            Matcher m = typePattern.matcher(rule);
            if (!m.find()) {
                throw new IllegalArgumentException("Invalid Mapping Rule : [" + rule + "]");
            }
            String lhs = this.parseString(m.group(1).trim());
            Byte rhs = this.parseType(m.group(2).trim());
            if (lhs.length() != 1) {
                throw new IllegalArgumentException("Invalid Mapping Rule : [" + rule + "]. Only a single character is allowed.");
            }
            if (rhs == null) {
                throw new IllegalArgumentException("Invalid Mapping Rule : [" + rule + "]. Illegal type.");
            }
            typeMap.put(Character.valueOf(lhs.charAt(0)), rhs);
        }
        byte[] types = new byte[Math.max(((Character)typeMap.lastKey()).charValue() + '\u0001', WordDelimiterIterator.DEFAULT_WORD_DELIM_TABLE.length)];
        for (int i = 0; i < types.length; ++i) {
            types[i] = WordDelimiterIterator.getType(i);
        }
        for (Map.Entry mapping : typeMap.entrySet()) {
            types[((Character)mapping.getKey()).charValue()] = (Byte)mapping.getValue();
        }
        return types;
    }

    private Byte parseType(String s) {
        if (s.equals("LOWER")) {
            return (byte)1;
        }
        if (s.equals("UPPER")) {
            return (byte)2;
        }
        if (s.equals("ALPHA")) {
            return (byte)3;
        }
        if (s.equals("DIGIT")) {
            return (byte)4;
        }
        if (s.equals("ALPHANUM")) {
            return (byte)7;
        }
        if (s.equals("SUBWORD_DELIM")) {
            return (byte)8;
        }
        return null;
    }

    private String parseString(String s) {
        int readPos = 0;
        int len = s.length();
        int writePos = 0;
        while (readPos < len) {
            int c;
            if ((c = s.charAt(readPos++)) == 92) {
                if (readPos >= len) {
                    throw new IllegalArgumentException("Invalid escaped char in [" + s + "]");
                }
                c = s.charAt(readPos++);
                switch (c) {
                    case 92: {
                        c = 92;
                        break;
                    }
                    case 110: {
                        c = 10;
                        break;
                    }
                    case 116: {
                        c = 9;
                        break;
                    }
                    case 114: {
                        c = 13;
                        break;
                    }
                    case 98: {
                        c = 8;
                        break;
                    }
                    case 102: {
                        c = 12;
                        break;
                    }
                    case 117: {
                        if (readPos + 3 >= len) {
                            throw new IllegalArgumentException("Invalid escaped char in [" + s + "]");
                        }
                        c = (char)Integer.parseInt(s.substring(readPos, readPos + 4), 16);
                        readPos += 4;
                    }
                }
            }
            this.out[writePos++] = c;
        }
        return new String(this.out, 0, writePos);
    }
}

