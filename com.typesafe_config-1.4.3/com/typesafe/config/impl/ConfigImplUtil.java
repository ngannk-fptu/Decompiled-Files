/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigOrigin;
import com.typesafe.config.ConfigSyntax;
import com.typesafe.config.impl.Path;
import com.typesafe.config.impl.SerializedConfigValue;
import com.typesafe.config.impl.SimpleConfigOrigin;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public final class ConfigImplUtil {
    static boolean equalsHandlingNull(Object a, Object b) {
        if (a == null && b != null) {
            return false;
        }
        if (a != null && b == null) {
            return false;
        }
        if (a == b) {
            return true;
        }
        return a.equals(b);
    }

    static boolean isC0Control(int codepoint) {
        return codepoint >= 0 && codepoint <= 31;
    }

    public static String renderJsonString(String s) {
        StringBuilder sb = new StringBuilder();
        sb.append('\"');
        block9: for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            switch (c) {
                case '\"': {
                    sb.append("\\\"");
                    continue block9;
                }
                case '\\': {
                    sb.append("\\\\");
                    continue block9;
                }
                case '\n': {
                    sb.append("\\n");
                    continue block9;
                }
                case '\b': {
                    sb.append("\\b");
                    continue block9;
                }
                case '\f': {
                    sb.append("\\f");
                    continue block9;
                }
                case '\r': {
                    sb.append("\\r");
                    continue block9;
                }
                case '\t': {
                    sb.append("\\t");
                    continue block9;
                }
                default: {
                    if (ConfigImplUtil.isC0Control(c)) {
                        sb.append(String.format("\\u%04x", c));
                        continue block9;
                    }
                    sb.append(c);
                }
            }
        }
        sb.append('\"');
        return sb.toString();
    }

    static String renderStringUnquotedIfPossible(String s) {
        if (s.length() == 0) {
            return ConfigImplUtil.renderJsonString(s);
        }
        int first = s.codePointAt(0);
        if (Character.isDigit(first) || first == 45) {
            return ConfigImplUtil.renderJsonString(s);
        }
        if (s.startsWith("include") || s.startsWith("true") || s.startsWith("false") || s.startsWith("null") || s.contains("//")) {
            return ConfigImplUtil.renderJsonString(s);
        }
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (Character.isLetter(c) || Character.isDigit(c) || c == '-') continue;
            return ConfigImplUtil.renderJsonString(s);
        }
        return s;
    }

    static boolean isWhitespace(int codepoint) {
        switch (codepoint) {
            case 10: 
            case 32: 
            case 160: 
            case 8199: 
            case 8239: 
            case 65279: {
                return true;
            }
        }
        return Character.isWhitespace(codepoint);
    }

    public static String unicodeTrim(String s) {
        int length = s.length();
        if (length == 0) {
            return s;
        }
        int start = 0;
        while (start < length) {
            char c = s.charAt(start);
            if (c == ' ' || c == '\n') {
                ++start;
                continue;
            }
            int cp = s.codePointAt(start);
            if (!ConfigImplUtil.isWhitespace(cp)) break;
            start += Character.charCount(cp);
        }
        int end = length;
        while (end > start) {
            int delta;
            int cp;
            char c = s.charAt(end - 1);
            if (c == ' ' || c == '\n') {
                --end;
                continue;
            }
            if (Character.isLowSurrogate(c)) {
                cp = s.codePointAt(end - 2);
                delta = 2;
            } else {
                cp = s.codePointAt(end - 1);
                delta = 1;
            }
            if (!ConfigImplUtil.isWhitespace(cp)) break;
            end -= delta;
        }
        return s.substring(start, end);
    }

    public static ConfigException extractInitializerError(ExceptionInInitializerError e) {
        Throwable cause = e.getCause();
        if (cause != null && cause instanceof ConfigException) {
            return (ConfigException)cause;
        }
        throw e;
    }

    static File urlToFile(URL url) {
        try {
            return new File(url.toURI());
        }
        catch (URISyntaxException e) {
            return new File(url.getPath());
        }
        catch (IllegalArgumentException e) {
            return new File(url.getPath());
        }
    }

    public static String joinPath(String ... elements) {
        return new Path(elements).render();
    }

    public static String joinPath(List<String> elements) {
        return ConfigImplUtil.joinPath(elements.toArray(new String[0]));
    }

    public static List<String> splitPath(String path) {
        ArrayList<String> elements = new ArrayList<String>();
        for (Path p = Path.newPath(path); p != null; p = p.remainder()) {
            elements.add(p.first());
        }
        return elements;
    }

    public static ConfigOrigin readOrigin(ObjectInputStream in) throws IOException {
        return SerializedConfigValue.readOrigin(in, null);
    }

    public static void writeOrigin(ObjectOutputStream out, ConfigOrigin origin) throws IOException {
        SerializedConfigValue.writeOrigin(new DataOutputStream(out), (SimpleConfigOrigin)origin, null);
    }

    static String toCamelCase(String originalName) {
        String[] words = originalName.split("-+");
        StringBuilder nameBuilder = new StringBuilder(originalName.length());
        for (String word : words) {
            if (nameBuilder.length() == 0) {
                nameBuilder.append(word);
                continue;
            }
            nameBuilder.append(word.substring(0, 1).toUpperCase());
            nameBuilder.append(word.substring(1));
        }
        return nameBuilder.toString();
    }

    private static char underscoreMappings(int num) {
        switch (num) {
            case 1: {
                return '.';
            }
            case 2: {
                return '-';
            }
            case 3: {
                return '_';
            }
        }
        return '\u0000';
    }

    static String envVariableAsProperty(String variable, String prefix) throws ConfigException {
        StringBuilder builder = new StringBuilder();
        String strippedPrefix = variable.substring(prefix.length(), variable.length());
        int underscores = 0;
        for (char c : strippedPrefix.toCharArray()) {
            if (c == '_') {
                ++underscores;
                continue;
            }
            if (underscores > 0 && underscores < 4) {
                builder.append(ConfigImplUtil.underscoreMappings(underscores));
            } else if (underscores > 3) {
                throw new ConfigException.BadPath(variable, "Environment variable contains an un-mapped number of underscores.");
            }
            underscores = 0;
            builder.append(c);
        }
        if (underscores > 0 && underscores < 4) {
            builder.append(ConfigImplUtil.underscoreMappings(underscores));
        } else if (underscores > 3) {
            throw new ConfigException.BadPath(variable, "Environment variable contains an un-mapped number of underscores.");
        }
        return builder.toString();
    }

    public static ConfigSyntax syntaxFromExtension(String filename) {
        if (filename == null) {
            return null;
        }
        if (filename.endsWith(".json")) {
            return ConfigSyntax.JSON;
        }
        if (filename.endsWith(".conf")) {
            return ConfigSyntax.CONF;
        }
        if (filename.endsWith(".properties")) {
            return ConfigSyntax.PROPERTIES;
        }
        return null;
    }
}

