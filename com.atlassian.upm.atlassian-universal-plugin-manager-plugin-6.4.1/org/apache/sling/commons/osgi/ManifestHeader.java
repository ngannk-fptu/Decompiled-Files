/*
 * Decompiled with CFR 0.152.
 */
package org.apache.sling.commons.osgi;

import java.util.ArrayList;
import java.util.HashSet;

public class ManifestHeader {
    private Entry[] entries = new Entry[0];
    private static final String CLASS_PATH_SEPARATOR = ",";
    private static final String PACKAGE_SEPARATOR = ";";
    private static final String DIRECTIVE_SEPARATOR = ":=";
    private static final String ATTRIBUTE_SEPARATOR = "=";
    private static final int CHAR = 1;
    private static final int DELIMITER = 2;
    private static final int STARTQUOTE = 4;
    private static final int ENDQUOTE = 8;

    private void add(Entry[] paths) {
        if (paths != null && paths.length > 0) {
            Entry[] copy = new Entry[this.entries.length + paths.length];
            System.arraycopy(this.entries, 0, copy, 0, this.entries.length);
            System.arraycopy(paths, 0, copy, this.entries.length, paths.length);
            this.entries = copy;
        }
    }

    public Entry[] getEntries() {
        return this.entries;
    }

    public static ManifestHeader parse(String header) {
        ManifestHeader entry = new ManifestHeader();
        if (header != null) {
            if (header.length() == 0) {
                throw new IllegalArgumentException("A header cannot be an empty string.");
            }
            String[] clauseStrings = ManifestHeader.parseDelimitedString(header, CLASS_PATH_SEPARATOR);
            if (clauseStrings != null) {
                for (String clause : clauseStrings) {
                    entry.add(ManifestHeader.parseStandardHeaderClause(clause));
                }
            }
        }
        return entry.getEntries().length == 0 ? null : entry;
    }

    private static Entry[] parseStandardHeaderClause(String clauseString) throws IllegalArgumentException {
        String[] pieces = ManifestHeader.parseDelimitedString(clauseString, PACKAGE_SEPARATOR);
        int pathCount = 0;
        for (int pieceIdx = 0; pieceIdx < pieces.length && pieces[pieceIdx].indexOf(61) < 0; ++pieceIdx) {
            ++pathCount;
        }
        if (pathCount == 0) {
            throw new IllegalArgumentException("No paths specified in header: " + clauseString);
        }
        Entry[] paths = new PathImpl[pathCount];
        for (int i = 0; i < pathCount; ++i) {
            paths[i] = new PathImpl(pieces[i]);
        }
        ArrayList<NameValuePair> dirsList = new ArrayList<NameValuePair>();
        HashSet<String> dirsNames = new HashSet<String>();
        ArrayList<NameValuePair> attrsList = new ArrayList<NameValuePair>();
        HashSet<String> attrsNames = new HashSet<String>();
        int idx = -1;
        String sep = null;
        for (int pieceIdx = pathCount; pieceIdx < pieces.length; ++pieceIdx) {
            idx = pieces[pieceIdx].indexOf(DIRECTIVE_SEPARATOR);
            if (idx >= 0) {
                sep = DIRECTIVE_SEPARATOR;
            } else {
                idx = pieces[pieceIdx].indexOf(ATTRIBUTE_SEPARATOR);
                if (idx >= 0) {
                    sep = ATTRIBUTE_SEPARATOR;
                } else {
                    throw new IllegalArgumentException("Not a directive/attribute: " + clauseString);
                }
            }
            String key = pieces[pieceIdx].substring(0, idx).trim();
            String value = pieces[pieceIdx].substring(idx + sep.length()).trim();
            if (value.startsWith("\"") && value.endsWith("\"")) {
                value = value.substring(1, value.length() - 1);
            }
            if (sep.equals(DIRECTIVE_SEPARATOR)) {
                if (dirsNames.contains(key)) {
                    throw new IllegalArgumentException("Duplicate directive: " + key);
                }
                dirsList.add(new NameValuePair(key, value));
                dirsNames.add(key);
                continue;
            }
            if (attrsNames.contains(key)) {
                throw new IllegalArgumentException("Duplicate attribute: " + key);
            }
            attrsList.add(new NameValuePair(key, value));
            attrsNames.add(key);
        }
        NameValuePair[] dirs = dirsList.toArray(new NameValuePair[dirsList.size()]);
        NameValuePair[] attrs = attrsList.toArray(new NameValuePair[attrsList.size()]);
        for (int i = 0; i < pathCount; ++i) {
            ((PathImpl)paths[i]).init(dirs, attrs);
        }
        return paths;
    }

    private static String[] parseDelimitedString(String value, String delim) {
        if (value == null) {
            value = "";
        }
        ArrayList<String> list = new ArrayList<String>();
        StringBuffer sb = new StringBuffer();
        int expecting = 7;
        for (int i = 0; i < value.length(); ++i) {
            boolean isQuote;
            char c = value.charAt(i);
            boolean isDelimiter = delim.indexOf(c) >= 0;
            boolean bl = isQuote = c == '\"';
            if (isDelimiter && (expecting & 2) > 0) {
                list.add(sb.toString().trim());
                sb.delete(0, sb.length());
                expecting = 7;
                continue;
            }
            if (isQuote && (expecting & 4) > 0) {
                sb.append(c);
                expecting = 9;
                continue;
            }
            if (isQuote && (expecting & 8) > 0) {
                sb.append(c);
                expecting = 7;
                continue;
            }
            if ((expecting & 1) > 0) {
                sb.append(c);
                continue;
            }
            throw new IllegalArgumentException("Invalid delimited string: " + value);
        }
        if (sb.length() > 0) {
            list.add(sb.toString().trim());
        }
        if (list.size() == 0) {
            return null;
        }
        return list.toArray(new String[list.size()]);
    }

    protected static final class PathImpl
    implements Entry {
        private final String value;
        private NameValuePair[] attributes;
        private NameValuePair[] directives;

        public PathImpl(String path) {
            this.value = path;
        }

        public void init(NameValuePair[] dirs, NameValuePair[] attrs) {
            this.directives = dirs;
            this.attributes = attrs;
        }

        public NameValuePair[] getAttributes() {
            return this.attributes;
        }

        public NameValuePair[] getDirectives() {
            return this.directives;
        }

        public String getValue() {
            return this.value;
        }

        public String getAttributeValue(String name) {
            String v = null;
            for (int index = 0; v == null && index < this.attributes.length; ++index) {
                if (!this.attributes[index].getName().equals(name)) continue;
                v = this.attributes[index].getValue();
            }
            return v;
        }

        public String getDirectiveValue(String name) {
            String v = null;
            for (int index = 0; v == null && index < this.directives.length; ++index) {
                if (!this.directives[index].getName().equals(name)) continue;
                v = this.directives[index].getValue();
            }
            return v;
        }
    }

    public static final class NameValuePair {
        private final String name;
        private final String value;

        public NameValuePair(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return this.name;
        }

        public String getValue() {
            return this.value;
        }
    }

    public static interface Entry {
        public String getValue();

        public NameValuePair[] getAttributes();

        public NameValuePair[] getDirectives();

        public String getAttributeValue(String var1);

        public String getDirectiveValue(String var1);
    }
}

