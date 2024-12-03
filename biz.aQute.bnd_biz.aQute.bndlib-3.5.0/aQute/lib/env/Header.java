/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.env;

import aQute.lib.collections.SortedList;
import aQute.lib.env.Props;
import aQute.libg.generics.Create;
import aQute.libg.qtokens.QuotedTokenizer;
import aQute.service.reporter.Reporter;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class Header
implements Map<String, Props> {
    public static final Pattern TOKEN_P = Pattern.compile("[-a-zA-Z0-9_]+");
    public static final char DUPLICATE_MARKER = '~';
    private LinkedHashMap<String, Props> map;
    static Map<String, Props> EMPTY = Collections.emptyMap();
    String error;

    public Header() {
    }

    public Header(String header) {
        Header.parseHeader(header, null, this);
    }

    public Header(String header, Reporter reporter) {
        Header.parseHeader(header, reporter, this);
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    public void add(String key, Props attrs) {
        while (this.containsKey(key)) {
            key = key + "~";
        }
        this.put(key, attrs);
    }

    public boolean containsKey(String name) {
        if (this.map == null) {
            return false;
        }
        return this.map.containsKey(name);
    }

    @Override
    @Deprecated
    public boolean containsKey(Object name) {
        assert (name instanceof String);
        if (this.map == null) {
            return false;
        }
        return this.map.containsKey(name);
    }

    public boolean containsValue(Props value) {
        if (this.map == null) {
            return false;
        }
        return this.map.containsValue(value);
    }

    @Override
    @Deprecated
    public boolean containsValue(Object value) {
        assert (value instanceof Props);
        if (this.map == null) {
            return false;
        }
        return this.map.containsValue(value);
    }

    @Override
    public Set<Map.Entry<String, Props>> entrySet() {
        if (this.map == null) {
            return EMPTY.entrySet();
        }
        return this.map.entrySet();
    }

    @Override
    @Deprecated
    public Props get(Object key) {
        assert (key instanceof String);
        if (this.map == null) {
            return null;
        }
        return this.map.get(key);
    }

    public Props get(String key) {
        if (this.map == null) {
            return null;
        }
        return this.map.get(key);
    }

    @Override
    public boolean isEmpty() {
        return this.map == null || this.map.isEmpty();
    }

    @Override
    public Set<String> keySet() {
        if (this.map == null) {
            return EMPTY.keySet();
        }
        return this.map.keySet();
    }

    @Override
    public Props put(String key, Props value) {
        assert (key != null);
        assert (value != null);
        if (this.map == null) {
            this.map = new LinkedHashMap();
        }
        return this.map.put(key, value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Props> map) {
        if (this.map == null) {
            if (map.isEmpty()) {
                return;
            }
            this.map = new LinkedHashMap();
        }
        this.map.putAll(map);
    }

    public void putAllIfAbsent(Map<String, ? extends Props> map) {
        for (Map.Entry<String, ? extends Props> entry : map.entrySet()) {
            if (this.containsKey(entry.getKey())) continue;
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    @Deprecated
    public Props remove(Object var0) {
        assert (var0 instanceof String);
        if (this.map == null) {
            return null;
        }
        return (Props)this.map.remove(var0);
    }

    public Props remove(String var0) {
        if (this.map == null) {
            return null;
        }
        return (Props)this.map.remove(var0);
    }

    @Override
    public int size() {
        if (this.map == null) {
            return 0;
        }
        return this.map.size();
    }

    @Override
    public Collection<Props> values() {
        if (this.map == null) {
            return EMPTY.values();
        }
        return this.map.values();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        this.append(sb);
        return sb.toString();
    }

    public void append(StringBuilder sb) {
        String del = "";
        for (Map.Entry<String, Props> s : this.entrySet()) {
            sb.append(del);
            sb.append(Header.removeDuplicateMarker(s.getKey()));
            if (!s.getValue().isEmpty()) {
                sb.append(';');
                s.getValue().append(sb);
            }
            del = ",";
        }
    }

    @Override
    @Deprecated
    public boolean equals(Object other) {
        return super.equals(other);
    }

    @Override
    @Deprecated
    public int hashCode() {
        return super.hashCode();
    }

    public boolean isEqual(Header other) {
        SortedList lo;
        if (this == other) {
            return true;
        }
        if (other == null || this.size() != other.size()) {
            return false;
        }
        if (this.isEmpty()) {
            return true;
        }
        SortedList l = new SortedList(this.keySet());
        if (!l.isEqual(lo = new SortedList(other.keySet()))) {
            return false;
        }
        for (String key : this.keySet()) {
            Props valueo;
            Props value = this.get(key);
            if (value == (valueo = other.get(key)) || value != null && value.isEqual(valueo)) continue;
            return false;
        }
        return true;
    }

    public Map<String, ? extends Map<String, String>> asMapMap() {
        return this;
    }

    public static Header parseHeader(String value) {
        return Header.parseHeader(value, null);
    }

    public static Header parseHeader(String value, Reporter logger) {
        return Header.parseHeader(value, logger, new Header());
    }

    public static Header parseHeader(String value, Reporter logger, Header result) {
        if (value == null || value.trim().length() == 0) {
            return result;
        }
        QuotedTokenizer qt = new QuotedTokenizer(value, ";=,");
        char del = '\u0000';
        do {
            boolean hadAttribute = false;
            Props clause = new Props(new Props[0]);
            List<String> aliases = Create.list();
            String name = qt.nextToken(",;");
            del = qt.getSeparator();
            if (name == null || name.length() == 0) {
                if (logger != null && logger.isPedantic()) {
                    logger.warning("Empty clause, usually caused by repeating a comma without any name field or by having spaces after the backslash of a property file: %s", value);
                }
                if (name != null) continue;
                break;
            }
            name = name.trim();
            aliases.add(name);
            while (del == ';') {
                String adname = qt.nextToken();
                del = qt.getSeparator();
                if (del != '=') {
                    if (hadAttribute && logger != null) {
                        logger.error("Header contains name field after attribute or directive: %s from %s. Name fields must be consecutive, separated by a ';' like a;b;c;x=3;y=4", adname, value);
                    }
                    if (adname == null || adname.length() <= 0) continue;
                    aliases.add(adname.trim());
                    continue;
                }
                String advalue = qt.nextToken();
                if (clause.containsKey(adname) && logger != null && logger.isPedantic()) {
                    logger.warning("Duplicate attribute/directive name %s in %s. This attribute/directive will be ignored", adname, value);
                }
                if (advalue == null) {
                    if (logger != null) {
                        logger.error("No value after '=' sign for attribute %s", adname);
                    }
                    advalue = "";
                }
                clause.put(adname.trim(), advalue);
                del = qt.getSeparator();
                hadAttribute = true;
            }
            for (String clauseName : aliases) {
                if (result.containsKey(clauseName)) {
                    if (logger != null && logger.isPedantic()) {
                        logger.warning("Duplicate name %s used in header: '%s'. Duplicate names are specially marked in Bnd with a ~ at the end (which is stripped at printing time).", clauseName, clauseName);
                    }
                    while (result.containsKey(clauseName)) {
                        clauseName = clauseName + "~";
                    }
                }
                result.put(clauseName, clause);
            }
        } while (del == ',');
        return result;
    }

    public static Props parseProperties(String input) {
        return Header.parseProperties(input, null);
    }

    public static Props parseProperties(String input, Reporter logger) {
        if (input == null || input.trim().length() == 0) {
            return new Props(new Props[0]);
        }
        Props result = new Props(new Props[0]);
        QuotedTokenizer qt = new QuotedTokenizer(input, "=,");
        int del = 44;
        while (del == 44) {
            String key = qt.nextToken(",=");
            String value = "";
            del = qt.getSeparator();
            if (del == 61) {
                value = qt.nextToken(",=");
                if (value == null) {
                    value = "";
                }
                del = qt.getSeparator();
            }
            result.put(key.trim(), value);
        }
        if (del != 0) {
            if (logger == null) {
                throw new IllegalArgumentException("Invalid syntax for properties: " + input);
            }
            logger.error("Invalid syntax for properties: %s", input);
        }
        return result;
    }

    public static String removeDuplicateMarker(String key) {
        int i;
        for (i = key.length() - 1; i >= 0 && key.charAt(i) == '~'; --i) {
        }
        return key.substring(0, i + 1);
    }

    public static boolean isDuplicate(String name) {
        return name.length() > 0 && name.charAt(name.length() - 1) == '~';
    }

    public static boolean quote(Appendable sb, String value) throws IOException {
        boolean clean;
        if (value.startsWith("\\\"")) {
            value = value.substring(2);
        }
        if (value.endsWith("\\\"")) {
            value = value.substring(0, value.length() - 2);
        }
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }
        boolean bl = clean = value.length() >= 2 && value.charAt(0) == '\"' && value.charAt(value.length() - 1) == '\"' || TOKEN_P.matcher(value).matches();
        if (!clean) {
            sb.append("\"");
        }
        block3: for (int i = 0; i < value.length(); ++i) {
            char c = value.charAt(i);
            switch (c) {
                case '\"': {
                    sb.append('\\').append('\"');
                    continue block3;
                }
                default: {
                    sb.append(c);
                }
            }
        }
        if (!clean) {
            sb.append("\"");
        }
        return clean;
    }
}

