/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.impl.provider.entity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Inflector {
    private static transient Inflector instance = null;
    private List<Replacer> plurals = new LinkedList<Replacer>();
    private List<Replacer> singulars = new ArrayList<Replacer>();
    private List uncountables = new LinkedList();

    private Inflector() {
        this.addPlural("$", "s", false);
        this.addPlural("(.*)$", "\\1s");
        this.addPlural("(.*)(ax|test)is$", "\\1\\2es");
        this.addPlural("(.*)(octop|vir)us$", "\\1\\2i");
        this.addPlural("(.*)(alias|status)$", "\\1\\2es");
        this.addPlural("(.*)(bu)s$", "\\1\\2ses");
        this.addPlural("(.*)(buffal|tomat)o$", "\\1\\2oes");
        this.addPlural("(.*)([ti])um$", "\\1\\2a");
        this.addPlural("(.*)sis$", "\\1ses");
        this.addPlural("(.*)(?:([^f])fe|([lr])f)$", "\\1\\3ves");
        this.addPlural("(.*)(hive)$", "\\1\\2s");
        this.addPlural("(.*)(tive)$", "\\1\\2s");
        this.addPlural("(.*)([^aeiouy]|qu)y$", "\\1\\2ies");
        this.addPlural("(.*)(series)$", "\\1\\2");
        this.addPlural("(.*)(movie)$", "\\1\\2s");
        this.addPlural("(.*)(x|ch|ss|sh)$", "\\1\\2es");
        this.addPlural("(.*)(matr|vert|ind)ix|ex$", "\\1\\2ices");
        this.addPlural("(.*)(o)$", "\\1\\2es");
        this.addPlural("(.*)(shoe)$", "\\1\\2s");
        this.addPlural("(.*)([m|l])ouse$", "\\1\\2ice");
        this.addPlural("^(ox)$", "\\1en");
        this.addPlural("(.*)(vert|ind)ex$", "\\1\\2ices");
        this.addPlural("(.*)(matr)ix$", "\\1\\2ices");
        this.addPlural("(.*)(quiz)$", "\\1\\2zes");
        this.addSingular("(.*)s$", "\\1");
        this.addSingular("(.*)(n)ews$", "\\1\\2ews");
        this.addSingular("(.*)([ti])a$", "\\1\\2um");
        this.addSingular("(.*)((a)naly|(b)a|(d)iagno|(p)arenthe|(p)rogno|(s)ynop|(t)he)ses$", "\\1\\2sis");
        this.addSingular("(.*)(^analy)ses$", "\\1\\2sis");
        this.addSingular("(.*)([^f])ves$", "\\1\\2fe");
        this.addSingular("(.*)(hive)s$", "\\1\\2");
        this.addSingular("(.*)(tive)s$", "\\1\\2");
        this.addSingular("(.*)([lr])ves$", "\\1\\2f");
        this.addSingular("(.*)([^aeiouy]|qu)ies$", "\\1\\2y");
        this.addSingular("(.*)(s)eries$", "\\1\\2eries");
        this.addSingular("(.*)(m)ovies$", "\\1\\2ovie");
        this.addSingular("(.*)(x|ch|ss|sh)es$", "\\1\\2");
        this.addSingular("(.*)([m|l])ice$", "\\1\\2ouse");
        this.addSingular("(.*)(bus)es$", "\\1\\2");
        this.addSingular("(.*)(o)es$", "\\1\\2");
        this.addSingular("(.*)(shoe)s$", "\\1\\2");
        this.addSingular("(.*)(cris|ax|test)es$", "\\1\\2is");
        this.addSingular("(.*)(octop|vir)i$", "\\1\\2us");
        this.addSingular("(.*)(alias|status)es$", "\\1\\2");
        this.addSingular("^(ox)en", "\\1");
        this.addSingular("(.*)(vert|ind)ices$", "\\1\\2ex");
        this.addSingular("(.*)(matr)ices$", "\\1\\2ix");
        this.addSingular("(.*)(quiz)zes$", "\\1\\2");
        this.addIrregular("child", "children");
        this.addIrregular("man", "men");
        this.addIrregular("move", "moves");
        this.addIrregular("person", "people");
        this.addIrregular("sex", "sexes");
        this.addUncountable("equipment");
        this.addUncountable("fish");
        this.addUncountable("information");
        this.addUncountable("money");
        this.addUncountable("rice");
        this.addUncountable("series");
        this.addUncountable("sheep");
        this.addUncountable("species");
    }

    public static Inflector getInstance() {
        if (instance == null) {
            instance = new Inflector();
        }
        return instance;
    }

    public String camelize(String word) {
        return this.camelize(word, false);
    }

    public String camelize(String word, boolean flag) {
        if (word.length() == 0) {
            return word;
        }
        StringBuffer sb = new StringBuffer(word.length());
        if (flag) {
            sb.append(Character.toLowerCase(word.charAt(0)));
        } else {
            sb.append(Character.toUpperCase(word.charAt(0)));
        }
        boolean capitalize = false;
        for (int i = 1; i < word.length(); ++i) {
            char ch = word.charAt(i);
            if (capitalize) {
                sb.append(Character.toUpperCase(ch));
                capitalize = false;
                continue;
            }
            if (ch == '_') {
                capitalize = true;
                continue;
            }
            if (ch == '/') {
                capitalize = true;
                sb.append('.');
                continue;
            }
            sb.append(ch);
        }
        return sb.toString();
    }

    public String classify(String tableName) {
        int period = tableName.lastIndexOf(46);
        if (period >= 0) {
            tableName = tableName.substring(period + 1);
        }
        return this.camelize(this.singularize(tableName));
    }

    public String dasherize(String word) {
        return word.replace('_', '-');
    }

    public String demodulize(String className) {
        int period = className.lastIndexOf(46);
        if (period >= 0) {
            return className.substring(period + 1);
        }
        return className;
    }

    public String foreignKey(String className) {
        return this.foreignKey(className, true);
    }

    public String foreignKey(String className, boolean underscore) {
        return this.underscore(this.demodulize(className) + (underscore ? "_id" : "id"));
    }

    public String humanize(String words) {
        if (words.endsWith("_id")) {
            words = words.substring(0, words.length() - 3);
        }
        StringBuffer sb = new StringBuffer(words.length());
        sb.append(Character.toUpperCase(words.charAt(0)));
        for (int i = 1; i < words.length(); ++i) {
            char ch = words.charAt(i);
            if (ch == '_') {
                sb.append(' ');
                continue;
            }
            sb.append(ch);
        }
        return sb.toString();
    }

    public String ordinalize(int number) {
        int modulo = number % 100;
        if (modulo >= 11 && modulo <= 13) {
            return "" + number + "th";
        }
        switch (number % 10) {
            case 1: {
                return "" + number + "st";
            }
            case 2: {
                return "" + number + "nd";
            }
            case 3: {
                return "" + number + "rd";
            }
        }
        return "" + number + "th";
    }

    public String pluralize(String word) {
        int i;
        for (i = 0; i < this.uncountables.size(); ++i) {
            if (!this.uncountables.get(i).equals(word)) continue;
            return word;
        }
        for (i = 0; i < this.plurals.size(); ++i) {
            String replacement = this.plurals.get(i).replacement(word);
            if (replacement == null) continue;
            return replacement;
        }
        return word;
    }

    public String singularize(String word) {
        int i;
        for (i = 0; i < this.uncountables.size(); ++i) {
            if (!this.uncountables.get(i).equals(word)) continue;
            return word;
        }
        for (i = 0; i < this.singulars.size(); ++i) {
            String replacement = this.singulars.get(i).replacement(word);
            if (replacement == null) continue;
            return replacement;
        }
        return word;
    }

    public String tableize(String className) {
        return this.pluralize(this.underscore(className));
    }

    public String titleize(String words) {
        StringBuffer sb = new StringBuffer(words.length());
        boolean capitalize = true;
        for (int i = 0; i < words.length(); ++i) {
            char ch = words.charAt(i);
            if (Character.isWhitespace(ch)) {
                sb.append(' ');
                capitalize = true;
                continue;
            }
            if (ch == '-') {
                sb.append(' ');
                capitalize = true;
                continue;
            }
            if (capitalize) {
                sb.append(Character.toUpperCase(ch));
                capitalize = false;
                continue;
            }
            sb.append(ch);
        }
        return sb.toString();
    }

    public String decapitalize(String word) {
        if (word == null || word.length() < 1) {
            return word;
        }
        char first = word.charAt(0);
        if (Character.isLowerCase(first)) {
            return word;
        }
        StringBuilder sb = new StringBuilder(word.length());
        sb.append(Character.toLowerCase(first));
        sb.append(word.substring(1));
        return sb.toString();
    }

    public String underscore(String word) {
        StringBuffer sb = new StringBuffer(word.length() + 5);
        boolean uncapitalize = false;
        for (int i = 0; i < word.length(); ++i) {
            char ch = word.charAt(i);
            if (uncapitalize) {
                sb.append(Character.toLowerCase(ch));
                uncapitalize = false;
                continue;
            }
            if (ch == '.') {
                sb.append('/');
                uncapitalize = true;
                continue;
            }
            if (Character.isUpperCase(ch)) {
                if (i > 0) {
                    sb.append('_');
                }
                sb.append(Character.toLowerCase(ch));
                continue;
            }
            sb.append(ch);
        }
        return sb.toString();
    }

    public void addIrregular(String singular, String plural) {
        this.addPlural("(.*)(" + singular.substring(0, 1) + ")" + singular.substring(1) + "$", "\\1\\2" + plural.substring(1));
        this.addSingular("(.*)(" + plural.substring(0, 1) + ")" + plural.substring(1) + "$", "\\1\\2" + singular.substring(1));
    }

    public void addPlural(String match, String rule) {
        this.addPlural(match, rule, true);
    }

    public void addPlural(String match, String rule, boolean insensitive) {
        this.plurals.add(0, new Replacer(match, rule, insensitive));
    }

    public void addSingular(String match, String rule) {
        this.addSingular(match, rule, true);
    }

    public void addSingular(String match, String rule, boolean insensitive) {
        this.singulars.add(0, new Replacer(match, rule, insensitive));
    }

    public void addUncountable(String word) {
        this.uncountables.add(0, word.toLowerCase());
    }

    private class Replacer {
        private Pattern pattern = null;
        private String rule = null;

        public Replacer(String match, String rule, boolean insensitive) {
            this.pattern = Pattern.compile(match, insensitive ? 2 : 0);
            this.rule = rule;
        }

        public String replacement(String input) {
            Matcher matcher = this.pattern.matcher(input);
            if (matcher.matches()) {
                StringBuffer sb = new StringBuffer();
                boolean group = false;
                for (int i = 0; i < this.rule.length(); ++i) {
                    char ch = this.rule.charAt(i);
                    if (group) {
                        sb.append(matcher.group(Character.digit(ch, 10)));
                        group = false;
                        continue;
                    }
                    if (ch == '\\') {
                        group = true;
                        continue;
                    }
                    sb.append(ch);
                }
                return sb.toString();
            }
            return null;
        }
    }
}

