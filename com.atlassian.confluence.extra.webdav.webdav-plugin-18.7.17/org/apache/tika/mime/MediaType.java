/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.mime;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MediaType
implements Comparable<MediaType>,
Serializable {
    private static final long serialVersionUID = -3831000556189036392L;
    private static final Pattern SPECIAL = Pattern.compile("[\\(\\)<>@,;:\\\\\"/\\[\\]\\?=]");
    private static final Pattern SPECIAL_OR_WHITESPACE = Pattern.compile("[\\(\\)<>@,;:\\\\\"/\\[\\]\\?=\\s]");
    private static final String VALID_CHARS = "([^\\c\\(\\)<>@,;:\\\\\"/\\[\\]\\?=\\s]+)";
    private static final Pattern TYPE_PATTERN = Pattern.compile("(?s)\\s*([^\\c\\(\\)<>@,;:\\\\\"/\\[\\]\\?=\\s]+)\\s*/\\s*([^\\c\\(\\)<>@,;:\\\\\"/\\[\\]\\?=\\s]+)\\s*($|;.*)");
    private static final Pattern CHARSET_FIRST_PATTERN = Pattern.compile("(?is)\\s*(charset\\s*=\\s*[^\\c;\\s]+)\\s*;\\s*([^\\c\\(\\)<>@,;:\\\\\"/\\[\\]\\?=\\s]+)\\s*/\\s*([^\\c\\(\\)<>@,;:\\\\\"/\\[\\]\\?=\\s]+)\\s*");
    private static final Map<String, MediaType> SIMPLE_TYPES = new HashMap<String, MediaType>();
    public static final MediaType OCTET_STREAM = MediaType.parse("application/octet-stream");
    public static final MediaType EMPTY = MediaType.parse("application/x-empty");
    public static final MediaType TEXT_PLAIN = MediaType.parse("text/plain");
    public static final MediaType TEXT_HTML = MediaType.parse("text/html");
    public static final MediaType APPLICATION_XML = MediaType.parse("application/xml");
    public static final MediaType APPLICATION_ZIP = MediaType.parse("application/zip");
    private final String string;
    private final int slash;
    private final int semicolon;
    private final Map<String, String> parameters;

    public MediaType(String type, String subtype, Map<String, String> parameters) {
        type = type.trim().toLowerCase(Locale.ENGLISH);
        subtype = subtype.trim().toLowerCase(Locale.ENGLISH);
        this.slash = type.length();
        this.semicolon = this.slash + 1 + subtype.length();
        if (parameters.isEmpty()) {
            this.parameters = Collections.emptyMap();
            this.string = type + '/' + subtype;
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append(type);
            builder.append('/');
            builder.append(subtype);
            TreeMap<String, String> map = new TreeMap<String, String>();
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                String key = entry.getKey().trim().toLowerCase(Locale.ENGLISH);
                map.put(key, entry.getValue());
            }
            for (Map.Entry<String, String> entry : map.entrySet()) {
                builder.append("; ");
                builder.append(entry.getKey());
                builder.append("=");
                String value = entry.getValue();
                if (SPECIAL_OR_WHITESPACE.matcher(value).find()) {
                    builder.append('\"');
                    builder.append(SPECIAL.matcher(value).replaceAll("\\\\$0"));
                    builder.append('\"');
                    continue;
                }
                builder.append(value);
            }
            this.string = builder.toString();
            this.parameters = Collections.unmodifiableSortedMap(map);
        }
    }

    public MediaType(String type, String subtype) {
        this(type, subtype, Collections.emptyMap());
    }

    private MediaType(String string, int slash) {
        assert (slash != -1);
        assert (string.charAt(slash) == '/');
        assert (MediaType.isSimpleName(string.substring(0, slash)));
        assert (MediaType.isSimpleName(string.substring(slash + 1)));
        this.string = string;
        this.slash = slash;
        this.semicolon = string.length();
        this.parameters = Collections.emptyMap();
    }

    public MediaType(MediaType type, Map<String, String> parameters) {
        this(type.getType(), type.getSubtype(), MediaType.union(type.parameters, parameters));
    }

    public MediaType(MediaType type, String name, String value) {
        this(type, Collections.singletonMap(name, value));
    }

    public MediaType(MediaType type, Charset charset) {
        this(type, "charset", charset.name());
    }

    public static MediaType application(String type) {
        return MediaType.parse("application/" + type);
    }

    public static MediaType audio(String type) {
        return MediaType.parse("audio/" + type);
    }

    public static MediaType image(String type) {
        return MediaType.parse("image/" + type);
    }

    public static MediaType text(String type) {
        return MediaType.parse("text/" + type);
    }

    public static MediaType video(String type) {
        return MediaType.parse("video/" + type);
    }

    public static Set<MediaType> set(MediaType ... types) {
        HashSet<MediaType> set = new HashSet<MediaType>();
        for (MediaType type : types) {
            if (type == null) continue;
            set.add(type);
        }
        return Collections.unmodifiableSet(set);
    }

    public static Set<MediaType> set(String ... types) {
        HashSet<MediaType> set = new HashSet<MediaType>();
        for (String type : types) {
            MediaType mt = MediaType.parse(type);
            if (mt == null) continue;
            set.add(mt);
        }
        return Collections.unmodifiableSet(set);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static MediaType parse(String string) {
        if (string == null) {
            return null;
        }
        Map<String, MediaType> map = SIMPLE_TYPES;
        synchronized (map) {
            MediaType type = SIMPLE_TYPES.get(string);
            if (type == null) {
                int slash = string.indexOf(47);
                if (slash == -1) {
                    return null;
                }
                if (SIMPLE_TYPES.size() < 10000 && MediaType.isSimpleName(string.substring(0, slash)) && MediaType.isSimpleName(string.substring(slash + 1))) {
                    type = new MediaType(string, slash);
                    SIMPLE_TYPES.put(string, type);
                }
            }
            if (type != null) {
                return type;
            }
        }
        Matcher matcher = TYPE_PATTERN.matcher(string);
        if (matcher.matches()) {
            return new MediaType(matcher.group(1), matcher.group(2), MediaType.parseParameters(matcher.group(3)));
        }
        matcher = CHARSET_FIRST_PATTERN.matcher(string);
        if (matcher.matches()) {
            return new MediaType(matcher.group(2), matcher.group(3), MediaType.parseParameters(matcher.group(1)));
        }
        return null;
    }

    private static boolean isSimpleName(String name) {
        for (int i = 0; i < name.length(); ++i) {
            char c = name.charAt(i);
            if (c == '-' || c == '+' || c == '.' || c == '_' || '0' <= c && c <= '9' || 'a' <= c && c <= 'z') continue;
            return false;
        }
        return name.length() > 0;
    }

    private static Map<String, String> parseParameters(String string) {
        if (string.length() == 0) {
            return Collections.emptyMap();
        }
        HashMap<String, String> parameters = new HashMap<String, String>();
        while (string.length() > 0) {
            String key = string;
            String value = "";
            int semicolon = string.indexOf(59);
            if (semicolon != -1) {
                key = string.substring(0, semicolon);
                string = string.substring(semicolon + 1);
            } else {
                string = "";
            }
            int equals = key.indexOf(61);
            if (equals != -1) {
                value = key.substring(equals + 1);
                key = key.substring(0, equals);
            }
            if ((key = key.trim()).length() <= 0) continue;
            parameters.put(key, MediaType.unquote(value.trim()));
        }
        return parameters;
    }

    private static String unquote(String s) {
        while (s.startsWith("\"") || s.startsWith("'")) {
            s = s.substring(1);
        }
        while (s.endsWith("\"") || s.endsWith("'")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    private static Map<String, String> union(Map<String, String> a, Map<String, String> b) {
        if (a.isEmpty()) {
            return b;
        }
        if (b.isEmpty()) {
            return a;
        }
        HashMap<String, String> union = new HashMap<String, String>();
        union.putAll(a);
        union.putAll(b);
        return union;
    }

    public MediaType getBaseType() {
        if (this.parameters.isEmpty()) {
            return this;
        }
        return MediaType.parse(this.string.substring(0, this.semicolon));
    }

    public String getType() {
        return this.string.substring(0, this.slash);
    }

    public String getSubtype() {
        return this.string.substring(this.slash + 1, this.semicolon);
    }

    public boolean hasParameters() {
        return !this.parameters.isEmpty();
    }

    public Map<String, String> getParameters() {
        return this.parameters;
    }

    public String toString() {
        return this.string;
    }

    public boolean equals(Object object) {
        if (object instanceof MediaType) {
            MediaType that = (MediaType)object;
            return this.string.equals(that.string);
        }
        return false;
    }

    public int hashCode() {
        return this.string.hashCode();
    }

    @Override
    public int compareTo(MediaType that) {
        return this.string.compareTo(that.string);
    }
}

