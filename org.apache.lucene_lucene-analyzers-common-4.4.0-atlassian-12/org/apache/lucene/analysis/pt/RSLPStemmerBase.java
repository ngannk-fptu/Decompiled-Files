/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.pt;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StemmerUtil;
import org.apache.lucene.util.Version;

public abstract class RSLPStemmerBase {
    private static final Pattern headerPattern = Pattern.compile("^\\{\\s*\"([^\"]*)\",\\s*([0-9]+),\\s*(0|1),\\s*\\{(.*)\\},\\s*$");
    private static final Pattern stripPattern = Pattern.compile("^\\{\\s*\"([^\"]*)\",\\s*([0-9]+)\\s*\\}\\s*(,|(\\}\\s*;))$");
    private static final Pattern repPattern = Pattern.compile("^\\{\\s*\"([^\"]*)\",\\s*([0-9]+),\\s*\"([^\"]*)\"\\}\\s*(,|(\\}\\s*;))$");
    private static final Pattern excPattern = Pattern.compile("^\\{\\s*\"([^\"]*)\",\\s*([0-9]+),\\s*\"([^\"]*)\",\\s*\\{(.*)\\}\\s*\\}\\s*(,|(\\}\\s*;))$");

    protected static Map<String, Step> parse(Class<? extends RSLPStemmerBase> clazz, String resource) {
        try {
            String step;
            InputStream is = clazz.getResourceAsStream(resource);
            LineNumberReader r = new LineNumberReader(new InputStreamReader(is, "UTF-8"));
            HashMap<String, Step> steps = new HashMap<String, Step>();
            while ((step = RSLPStemmerBase.readLine(r)) != null) {
                Step s = RSLPStemmerBase.parseStep(r, step);
                steps.put(s.name, s);
            }
            r.close();
            return steps;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Step parseStep(LineNumberReader r, String header) throws IOException {
        Matcher matcher = headerPattern.matcher(header);
        if (!matcher.find()) {
            throw new RuntimeException("Illegal Step header specified at line " + r.getLineNumber());
        }
        assert (matcher.groupCount() == 4);
        String name = matcher.group(1);
        int min = Integer.parseInt(matcher.group(2));
        int type = Integer.parseInt(matcher.group(3));
        String[] suffixes = RSLPStemmerBase.parseList(matcher.group(4));
        Rule[] rules = RSLPStemmerBase.parseRules(r, type);
        return new Step(name, rules, min, suffixes);
    }

    private static Rule[] parseRules(LineNumberReader r, int type) throws IOException {
        String line;
        ArrayList<Rule> rules = new ArrayList<Rule>();
        while ((line = RSLPStemmerBase.readLine(r)) != null) {
            Matcher matcher = stripPattern.matcher(line);
            if (matcher.matches()) {
                rules.add(new Rule(matcher.group(1), Integer.parseInt(matcher.group(2)), ""));
            } else {
                matcher = repPattern.matcher(line);
                if (matcher.matches()) {
                    rules.add(new Rule(matcher.group(1), Integer.parseInt(matcher.group(2)), matcher.group(3)));
                } else {
                    matcher = excPattern.matcher(line);
                    if (matcher.matches()) {
                        if (type == 0) {
                            rules.add(new RuleWithSuffixExceptions(matcher.group(1), Integer.parseInt(matcher.group(2)), matcher.group(3), RSLPStemmerBase.parseList(matcher.group(4))));
                        } else {
                            rules.add(new RuleWithSetExceptions(matcher.group(1), Integer.parseInt(matcher.group(2)), matcher.group(3), RSLPStemmerBase.parseList(matcher.group(4))));
                        }
                    } else {
                        throw new RuntimeException("Illegal Step rule specified at line " + r.getLineNumber());
                    }
                }
            }
            if (!line.endsWith(";")) continue;
            return rules.toArray(new Rule[rules.size()]);
        }
        return null;
    }

    private static String[] parseList(String s) {
        if (s.length() == 0) {
            return null;
        }
        String[] list = s.split(",");
        for (int i = 0; i < list.length; ++i) {
            list[i] = RSLPStemmerBase.parseString(list[i].trim());
        }
        return list;
    }

    private static String parseString(String s) {
        return s.substring(1, s.length() - 1);
    }

    private static String readLine(LineNumberReader r) throws IOException {
        String line = null;
        while ((line = r.readLine()) != null) {
            if ((line = line.trim()).length() <= 0 || line.charAt(0) == '#') continue;
            return line;
        }
        return line;
    }

    protected static class Step {
        protected final String name;
        protected final Rule[] rules;
        protected final int min;
        protected final char[][] suffixes;

        public Step(String name, Rule[] rules, int min, String[] suffixes) {
            this.name = name;
            this.rules = rules;
            if (min == 0) {
                min = Integer.MAX_VALUE;
                for (Rule r : rules) {
                    min = Math.min(min, r.min + r.suffix.length);
                }
            }
            this.min = min;
            if (suffixes == null || suffixes.length == 0) {
                this.suffixes = null;
            } else {
                this.suffixes = new char[suffixes.length][];
                for (int i = 0; i < suffixes.length; ++i) {
                    this.suffixes[i] = suffixes[i].toCharArray();
                }
            }
        }

        public int apply(char[] s, int len) {
            if (len < this.min) {
                return len;
            }
            if (this.suffixes != null) {
                boolean found = false;
                for (int i = 0; i < this.suffixes.length; ++i) {
                    if (!StemmerUtil.endsWith(s, len, this.suffixes[i])) continue;
                    found = true;
                    break;
                }
                if (!found) {
                    return len;
                }
            }
            for (int i = 0; i < this.rules.length; ++i) {
                if (!this.rules[i].matches(s, len)) continue;
                return this.rules[i].replace(s, len);
            }
            return len;
        }
    }

    protected static class RuleWithSuffixExceptions
    extends Rule {
        protected final char[][] exceptions;

        public RuleWithSuffixExceptions(String suffix, int min, String replacement, String[] exceptions) {
            super(suffix, min, replacement);
            int i;
            for (i = 0; i < exceptions.length; ++i) {
                if (exceptions[i].endsWith(suffix)) continue;
                throw new RuntimeException("warning: useless exception '" + exceptions[i] + "' does not end with '" + suffix + "'");
            }
            this.exceptions = new char[exceptions.length][];
            for (i = 0; i < exceptions.length; ++i) {
                this.exceptions[i] = exceptions[i].toCharArray();
            }
        }

        @Override
        public boolean matches(char[] s, int len) {
            if (!super.matches(s, len)) {
                return false;
            }
            for (int i = 0; i < this.exceptions.length; ++i) {
                if (!StemmerUtil.endsWith(s, len, this.exceptions[i])) continue;
                return false;
            }
            return true;
        }
    }

    protected static class RuleWithSetExceptions
    extends Rule {
        protected final CharArraySet exceptions;

        public RuleWithSetExceptions(String suffix, int min, String replacement, String[] exceptions) {
            super(suffix, min, replacement);
            for (int i = 0; i < exceptions.length; ++i) {
                if (exceptions[i].endsWith(suffix)) continue;
                throw new RuntimeException("useless exception '" + exceptions[i] + "' does not end with '" + suffix + "'");
            }
            this.exceptions = new CharArraySet(Version.LUCENE_31, Arrays.asList(exceptions), false);
        }

        @Override
        public boolean matches(char[] s, int len) {
            return super.matches(s, len) && !this.exceptions.contains(s, 0, len);
        }
    }

    protected static class Rule {
        protected final char[] suffix;
        protected final char[] replacement;
        protected final int min;

        public Rule(String suffix, int min, String replacement) {
            this.suffix = suffix.toCharArray();
            this.replacement = replacement.toCharArray();
            this.min = min;
        }

        public boolean matches(char[] s, int len) {
            return len - this.suffix.length >= this.min && StemmerUtil.endsWith(s, len, this.suffix);
        }

        public int replace(char[] s, int len) {
            if (this.replacement.length > 0) {
                System.arraycopy(this.replacement, 0, s, len - this.suffix.length, this.replacement.length);
            }
            return len - this.suffix.length + this.replacement.length;
        }
    }
}

