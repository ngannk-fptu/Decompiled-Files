/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.charfilter;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.lucene.analysis.charfilter.MappingCharFilter;
import org.apache.lucene.analysis.charfilter.NormalizeCharMap;
import org.apache.lucene.analysis.util.AbstractAnalysisFactory;
import org.apache.lucene.analysis.util.CharFilterFactory;
import org.apache.lucene.analysis.util.MultiTermAwareComponent;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;

public class MappingCharFilterFactory
extends CharFilterFactory
implements ResourceLoaderAware,
MultiTermAwareComponent {
    protected NormalizeCharMap normMap;
    private final String mapping;
    static Pattern p = Pattern.compile("\"(.*)\"\\s*=>\\s*\"(.*)\"\\s*$");
    char[] out = new char[256];

    public MappingCharFilterFactory(Map<String, String> args) {
        super(args);
        this.mapping = this.get(args, "mapping");
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    @Override
    public void inform(ResourceLoader loader) throws IOException {
        if (this.mapping != null) {
            List<String> wlist = null;
            File mappingFile = new File(this.mapping);
            if (mappingFile.exists()) {
                wlist = this.getLines(loader, this.mapping);
            } else {
                List<String> files = this.splitFileNames(this.mapping);
                wlist = new ArrayList<String>();
                for (String file : files) {
                    List<String> lines = this.getLines(loader, file.trim());
                    wlist.addAll(lines);
                }
            }
            NormalizeCharMap.Builder builder = new NormalizeCharMap.Builder();
            this.parseRules(wlist, builder);
            this.normMap = builder.build();
            if (this.normMap.map == null) {
                this.normMap = null;
            }
        }
    }

    @Override
    public Reader create(Reader input) {
        return this.normMap == null ? input : new MappingCharFilter(this.normMap, input);
    }

    protected void parseRules(List<String> rules, NormalizeCharMap.Builder builder) {
        for (String rule : rules) {
            Matcher m = p.matcher(rule);
            if (!m.find()) {
                throw new IllegalArgumentException("Invalid Mapping Rule : [" + rule + "], file = " + this.mapping);
            }
            builder.add(this.parseString(m.group(1)), this.parseString(m.group(2)));
        }
    }

    protected String parseString(String s) {
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
                    case 34: {
                        c = 34;
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

    @Override
    public AbstractAnalysisFactory getMultiTermComponent() {
        return this;
    }
}

