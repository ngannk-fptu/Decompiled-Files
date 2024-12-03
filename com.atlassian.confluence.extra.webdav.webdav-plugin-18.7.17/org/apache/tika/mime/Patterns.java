/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.mime;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.tika.mime.MediaTypeRegistry;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;

class Patterns
implements Serializable {
    private static final long serialVersionUID = -5778015347278111140L;
    private final MediaTypeRegistry registry;
    private final Map<String, MimeType> names = new HashMap<String, MimeType>();
    private final Map<String, MimeType> extensions = new HashMap<String, MimeType>();
    private final SortedMap<String, MimeType> globs = new TreeMap<String, MimeType>(new LengthComparator());
    private int minExtensionLength = Integer.MAX_VALUE;
    private int maxExtensionLength = 0;

    public Patterns(MediaTypeRegistry registry) {
        this.registry = registry;
    }

    public void add(String pattern, MimeType type) throws MimeTypeException {
        this.add(pattern, false, type);
    }

    public void add(String pattern, boolean isJavaRegex, MimeType type) throws MimeTypeException {
        if (pattern == null || type == null) {
            throw new IllegalArgumentException("Pattern and/or mime type is missing");
        }
        if (isJavaRegex) {
            this.addGlob(pattern, type);
        } else if (pattern.indexOf(42) == -1 && pattern.indexOf(63) == -1 && pattern.indexOf(91) == -1) {
            this.addName(pattern, type);
        } else if (pattern.startsWith("*") && pattern.indexOf(42, 1) == -1 && pattern.indexOf(63) == -1 && pattern.indexOf(91) == -1) {
            String extension = pattern.substring(1);
            this.addExtension(extension, type);
            type.addExtension(extension);
        } else {
            this.addGlob(this.compile(pattern), type);
        }
    }

    private void addName(String name, MimeType type) throws MimeTypeException {
        MimeType previous = this.names.get(name);
        if (previous == null || this.registry.isSpecializationOf(previous.getType(), type.getType())) {
            this.names.put(name, type);
        } else if (previous != type && !this.registry.isSpecializationOf(type.getType(), previous.getType())) {
            throw new MimeTypeException("Conflicting name pattern: " + name);
        }
    }

    private void addExtension(String extension, MimeType type) throws MimeTypeException {
        MimeType previous = this.extensions.get(extension);
        if (previous == null || this.registry.isSpecializationOf(previous.getType(), type.getType())) {
            this.extensions.put(extension, type);
            int length = extension.length();
            this.minExtensionLength = Math.min(this.minExtensionLength, length);
            this.maxExtensionLength = Math.max(this.maxExtensionLength, length);
        } else if (previous != type && !this.registry.isSpecializationOf(type.getType(), previous.getType())) {
            throw new MimeTypeException("Conflicting extension pattern: " + extension);
        }
    }

    private void addGlob(String glob, MimeType type) throws MimeTypeException {
        MimeType previous = (MimeType)this.globs.get(glob);
        if (previous == null || this.registry.isSpecializationOf(previous.getType(), type.getType())) {
            this.globs.put(glob, type);
        } else if (previous != type && !this.registry.isSpecializationOf(type.getType(), previous.getType())) {
            throw new MimeTypeException("Conflicting glob pattern: " + glob);
        }
    }

    public MimeType matches(String name) {
        int maxLength;
        if (name == null) {
            throw new IllegalArgumentException("Name is missing");
        }
        if (this.names.containsKey(name)) {
            return this.names.get(name);
        }
        for (int n = maxLength = Math.min(this.maxExtensionLength, name.length()); n >= this.minExtensionLength; --n) {
            String extension = name.substring(name.length() - n);
            if (!this.extensions.containsKey(extension)) continue;
            return this.extensions.get(extension);
        }
        for (Map.Entry<String, MimeType> entry : this.globs.entrySet()) {
            if (!name.matches(entry.getKey())) continue;
            return entry.getValue();
        }
        return null;
    }

    private String compile(String glob) {
        StringBuilder pattern = new StringBuilder();
        pattern.append("\\A");
        for (int i = 0; i < glob.length(); ++i) {
            char ch = glob.charAt(i);
            if (ch == '?') {
                pattern.append('.');
                continue;
            }
            if (ch == '*') {
                pattern.append(".*");
                continue;
            }
            if ("\\[]^.-$+(){}|".indexOf(ch) != -1) {
                pattern.append('\\');
                pattern.append(ch);
                continue;
            }
            pattern.append(ch);
        }
        pattern.append("\\z");
        return pattern.toString();
    }

    private static final class LengthComparator
    implements Comparator<String>,
    Serializable {
        private static final long serialVersionUID = 8468289702915532359L;

        private LengthComparator() {
        }

        @Override
        public int compare(String a, String b) {
            int diff = b.length() - a.length();
            if (diff == 0) {
                diff = a.compareTo(b);
            }
            return diff;
        }
    }
}

