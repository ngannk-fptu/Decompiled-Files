/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigOrigin;
import com.typesafe.config.impl.AbstractConfigValue;
import com.typesafe.config.impl.ConfigImplUtil;
import com.typesafe.config.impl.OriginType;
import com.typesafe.config.impl.SerializedConfigValue;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

final class SimpleConfigOrigin
implements ConfigOrigin {
    private final String description;
    private final int lineNumber;
    private final int endLineNumber;
    private final OriginType originType;
    private final String urlOrNull;
    private final String resourceOrNull;
    private final List<String> commentsOrNull;
    static final String MERGE_OF_PREFIX = "merge of ";

    protected SimpleConfigOrigin(String description, int lineNumber, int endLineNumber, OriginType originType, String urlOrNull, String resourceOrNull, List<String> commentsOrNull) {
        if (description == null) {
            throw new ConfigException.BugOrBroken("description may not be null");
        }
        this.description = description;
        this.lineNumber = lineNumber;
        this.endLineNumber = endLineNumber;
        this.originType = originType;
        this.urlOrNull = urlOrNull;
        this.resourceOrNull = resourceOrNull;
        this.commentsOrNull = commentsOrNull;
    }

    static SimpleConfigOrigin newSimple(String description) {
        return new SimpleConfigOrigin(description, -1, -1, OriginType.GENERIC, null, null, null);
    }

    static SimpleConfigOrigin newFile(String filename) {
        String url;
        try {
            url = new File(filename).toURI().toURL().toExternalForm();
        }
        catch (MalformedURLException e) {
            url = null;
        }
        return new SimpleConfigOrigin(filename, -1, -1, OriginType.FILE, url, null, null);
    }

    static SimpleConfigOrigin newURL(URL url) {
        String u = url.toExternalForm();
        return new SimpleConfigOrigin(u, -1, -1, OriginType.URL, u, null, null);
    }

    static SimpleConfigOrigin newResource(String resource, URL url) {
        String desc = url != null ? resource + " @ " + url.toExternalForm() : resource;
        return new SimpleConfigOrigin(desc, -1, -1, OriginType.RESOURCE, url != null ? url.toExternalForm() : null, resource, null);
    }

    static SimpleConfigOrigin newResource(String resource) {
        return SimpleConfigOrigin.newResource(resource, null);
    }

    static SimpleConfigOrigin newEnvVariable(String description) {
        return new SimpleConfigOrigin(description, -1, -1, OriginType.ENV_VARIABLE, null, null, null);
    }

    @Override
    public SimpleConfigOrigin withLineNumber(int lineNumber) {
        if (lineNumber == this.lineNumber && lineNumber == this.endLineNumber) {
            return this;
        }
        return new SimpleConfigOrigin(this.description, lineNumber, lineNumber, this.originType, this.urlOrNull, this.resourceOrNull, this.commentsOrNull);
    }

    SimpleConfigOrigin addURL(URL url) {
        return new SimpleConfigOrigin(this.description, this.lineNumber, this.endLineNumber, this.originType, url != null ? url.toExternalForm() : null, this.resourceOrNull, this.commentsOrNull);
    }

    @Override
    public SimpleConfigOrigin withComments(List<String> comments) {
        if (ConfigImplUtil.equalsHandlingNull(comments, this.commentsOrNull)) {
            return this;
        }
        return new SimpleConfigOrigin(this.description, this.lineNumber, this.endLineNumber, this.originType, this.urlOrNull, this.resourceOrNull, comments);
    }

    SimpleConfigOrigin prependComments(List<String> comments) {
        if (ConfigImplUtil.equalsHandlingNull(comments, this.commentsOrNull) || comments == null) {
            return this;
        }
        if (this.commentsOrNull == null) {
            return this.withComments((List)comments);
        }
        ArrayList<String> merged = new ArrayList<String>(comments.size() + this.commentsOrNull.size());
        merged.addAll(comments);
        merged.addAll(this.commentsOrNull);
        return this.withComments(merged);
    }

    SimpleConfigOrigin appendComments(List<String> comments) {
        if (ConfigImplUtil.equalsHandlingNull(comments, this.commentsOrNull) || comments == null) {
            return this;
        }
        if (this.commentsOrNull == null) {
            return this.withComments((List)comments);
        }
        ArrayList<String> merged = new ArrayList<String>(comments.size() + this.commentsOrNull.size());
        merged.addAll(this.commentsOrNull);
        merged.addAll(comments);
        return this.withComments(merged);
    }

    @Override
    public String description() {
        if (this.lineNumber < 0) {
            return this.description;
        }
        if (this.endLineNumber == this.lineNumber) {
            return this.description + ": " + this.lineNumber;
        }
        return this.description + ": " + this.lineNumber + "-" + this.endLineNumber;
    }

    OriginType originType() {
        return this.originType;
    }

    public boolean equals(Object other) {
        if (other instanceof SimpleConfigOrigin) {
            SimpleConfigOrigin otherOrigin = (SimpleConfigOrigin)other;
            return this.description.equals(otherOrigin.description) && this.lineNumber == otherOrigin.lineNumber && this.endLineNumber == otherOrigin.endLineNumber && this.originType == otherOrigin.originType && ConfigImplUtil.equalsHandlingNull(this.urlOrNull, otherOrigin.urlOrNull) && ConfigImplUtil.equalsHandlingNull(this.resourceOrNull, otherOrigin.resourceOrNull);
        }
        return false;
    }

    public int hashCode() {
        int h = 41 * (41 + this.description.hashCode());
        h = 41 * (h + this.lineNumber);
        h = 41 * (h + this.endLineNumber);
        h = 41 * (h + this.originType.hashCode());
        if (this.urlOrNull != null) {
            h = 41 * (h + this.urlOrNull.hashCode());
        }
        if (this.resourceOrNull != null) {
            h = 41 * (h + this.resourceOrNull.hashCode());
        }
        return h;
    }

    public String toString() {
        return "ConfigOrigin(" + this.description + ")";
    }

    @Override
    public String filename() {
        if (this.originType == OriginType.FILE) {
            return this.description;
        }
        if (this.urlOrNull != null) {
            URL url;
            try {
                url = new URL(this.urlOrNull);
            }
            catch (MalformedURLException e) {
                return null;
            }
            if (url.getProtocol().equals("file")) {
                return url.getFile();
            }
            return null;
        }
        return null;
    }

    @Override
    public URL url() {
        if (this.urlOrNull == null) {
            return null;
        }
        try {
            return new URL(this.urlOrNull);
        }
        catch (MalformedURLException e) {
            return null;
        }
    }

    @Override
    public String resource() {
        return this.resourceOrNull;
    }

    @Override
    public int lineNumber() {
        return this.lineNumber;
    }

    @Override
    public List<String> comments() {
        if (this.commentsOrNull != null) {
            return Collections.unmodifiableList(this.commentsOrNull);
        }
        return Collections.emptyList();
    }

    private static SimpleConfigOrigin mergeTwo(SimpleConfigOrigin a, SimpleConfigOrigin b) {
        List<String> mergedComments;
        int mergedEndLine;
        int mergedStartLine;
        String mergedDesc;
        OriginType mergedType = a.originType == b.originType ? a.originType : OriginType.GENERIC;
        String aDesc = a.description;
        String bDesc = b.description;
        if (aDesc.startsWith(MERGE_OF_PREFIX)) {
            aDesc = aDesc.substring(MERGE_OF_PREFIX.length());
        }
        if (bDesc.startsWith(MERGE_OF_PREFIX)) {
            bDesc = bDesc.substring(MERGE_OF_PREFIX.length());
        }
        if (aDesc.equals(bDesc)) {
            mergedDesc = aDesc;
            mergedStartLine = a.lineNumber < 0 ? b.lineNumber : (b.lineNumber < 0 ? a.lineNumber : Math.min(a.lineNumber, b.lineNumber));
            mergedEndLine = Math.max(a.endLineNumber, b.endLineNumber);
        } else {
            String aFull = a.description();
            String bFull = b.description();
            if (aFull.startsWith(MERGE_OF_PREFIX)) {
                aFull = aFull.substring(MERGE_OF_PREFIX.length());
            }
            if (bFull.startsWith(MERGE_OF_PREFIX)) {
                bFull = bFull.substring(MERGE_OF_PREFIX.length());
            }
            mergedDesc = MERGE_OF_PREFIX + aFull + "," + bFull;
            mergedStartLine = -1;
            mergedEndLine = -1;
        }
        String mergedURL = ConfigImplUtil.equalsHandlingNull(a.urlOrNull, b.urlOrNull) ? a.urlOrNull : null;
        String mergedResource = ConfigImplUtil.equalsHandlingNull(a.resourceOrNull, b.resourceOrNull) ? a.resourceOrNull : null;
        if (ConfigImplUtil.equalsHandlingNull(a.commentsOrNull, b.commentsOrNull)) {
            mergedComments = a.commentsOrNull;
        } else {
            mergedComments = new ArrayList<String>();
            if (a.commentsOrNull != null) {
                mergedComments.addAll(a.commentsOrNull);
            }
            if (b.commentsOrNull != null) {
                mergedComments.addAll(b.commentsOrNull);
            }
        }
        return new SimpleConfigOrigin(mergedDesc, mergedStartLine, mergedEndLine, mergedType, mergedURL, mergedResource, mergedComments);
    }

    private static int similarity(SimpleConfigOrigin a, SimpleConfigOrigin b) {
        int count = 0;
        if (a.originType == b.originType) {
            ++count;
        }
        if (a.description.equals(b.description)) {
            ++count;
            if (a.lineNumber == b.lineNumber) {
                ++count;
            }
            if (a.endLineNumber == b.endLineNumber) {
                ++count;
            }
            if (ConfigImplUtil.equalsHandlingNull(a.urlOrNull, b.urlOrNull)) {
                ++count;
            }
            if (ConfigImplUtil.equalsHandlingNull(a.resourceOrNull, b.resourceOrNull)) {
                ++count;
            }
        }
        return count;
    }

    private static SimpleConfigOrigin mergeThree(SimpleConfigOrigin a, SimpleConfigOrigin b, SimpleConfigOrigin c) {
        if (SimpleConfigOrigin.similarity(a, b) >= SimpleConfigOrigin.similarity(b, c)) {
            return SimpleConfigOrigin.mergeTwo(SimpleConfigOrigin.mergeTwo(a, b), c);
        }
        return SimpleConfigOrigin.mergeTwo(a, SimpleConfigOrigin.mergeTwo(b, c));
    }

    static ConfigOrigin mergeOrigins(ConfigOrigin a, ConfigOrigin b) {
        return SimpleConfigOrigin.mergeTwo((SimpleConfigOrigin)a, (SimpleConfigOrigin)b);
    }

    static ConfigOrigin mergeOrigins(List<? extends AbstractConfigValue> stack) {
        ArrayList<SimpleConfigOrigin> origins = new ArrayList<SimpleConfigOrigin>(stack.size());
        for (AbstractConfigValue abstractConfigValue : stack) {
            origins.add(abstractConfigValue.origin());
        }
        return SimpleConfigOrigin.mergeOrigins(origins);
    }

    static ConfigOrigin mergeOrigins(Collection<? extends ConfigOrigin> stack) {
        if (stack.isEmpty()) {
            throw new ConfigException.BugOrBroken("can't merge empty list of origins");
        }
        if (stack.size() == 1) {
            return stack.iterator().next();
        }
        if (stack.size() == 2) {
            Iterator<? extends ConfigOrigin> i = stack.iterator();
            return SimpleConfigOrigin.mergeTwo((SimpleConfigOrigin)i.next(), (SimpleConfigOrigin)i.next());
        }
        ArrayList<SimpleConfigOrigin> remaining = new ArrayList<SimpleConfigOrigin>(stack.size());
        for (ConfigOrigin configOrigin : stack) {
            remaining.add((SimpleConfigOrigin)configOrigin);
        }
        while (remaining.size() > 2) {
            SimpleConfigOrigin c = (SimpleConfigOrigin)remaining.get(remaining.size() - 1);
            remaining.remove(remaining.size() - 1);
            SimpleConfigOrigin simpleConfigOrigin = (SimpleConfigOrigin)remaining.get(remaining.size() - 1);
            remaining.remove(remaining.size() - 1);
            SimpleConfigOrigin a = (SimpleConfigOrigin)remaining.get(remaining.size() - 1);
            remaining.remove(remaining.size() - 1);
            SimpleConfigOrigin merged = SimpleConfigOrigin.mergeThree(a, simpleConfigOrigin, c);
            remaining.add(merged);
        }
        return SimpleConfigOrigin.mergeOrigins(remaining);
    }

    Map<SerializedConfigValue.SerializedField, Object> toFields() {
        EnumMap<SerializedConfigValue.SerializedField, Object> m = new EnumMap<SerializedConfigValue.SerializedField, Object>(SerializedConfigValue.SerializedField.class);
        m.put(SerializedConfigValue.SerializedField.ORIGIN_DESCRIPTION, this.description);
        if (this.lineNumber >= 0) {
            m.put(SerializedConfigValue.SerializedField.ORIGIN_LINE_NUMBER, Integer.valueOf(this.lineNumber));
        }
        if (this.endLineNumber >= 0) {
            m.put(SerializedConfigValue.SerializedField.ORIGIN_END_LINE_NUMBER, Integer.valueOf(this.endLineNumber));
        }
        m.put(SerializedConfigValue.SerializedField.ORIGIN_TYPE, Integer.valueOf(this.originType.ordinal()));
        if (this.urlOrNull != null) {
            m.put(SerializedConfigValue.SerializedField.ORIGIN_URL, this.urlOrNull);
        }
        if (this.resourceOrNull != null) {
            m.put(SerializedConfigValue.SerializedField.ORIGIN_RESOURCE, this.resourceOrNull);
        }
        if (this.commentsOrNull != null) {
            m.put(SerializedConfigValue.SerializedField.ORIGIN_COMMENTS, this.commentsOrNull);
        }
        return m;
    }

    Map<SerializedConfigValue.SerializedField, Object> toFieldsDelta(SimpleConfigOrigin baseOrigin) {
        Map<SerializedConfigValue.SerializedField, Object> baseFields = baseOrigin != null ? baseOrigin.toFields() : Collections.emptyMap();
        return SimpleConfigOrigin.fieldsDelta(baseFields, this.toFields());
    }

    static Map<SerializedConfigValue.SerializedField, Object> fieldsDelta(Map<SerializedConfigValue.SerializedField, Object> base, Map<SerializedConfigValue.SerializedField, Object> child) {
        EnumMap<SerializedConfigValue.SerializedField, Object> m = new EnumMap<SerializedConfigValue.SerializedField, Object>(child);
        for (Map.Entry<SerializedConfigValue.SerializedField, Object> baseEntry : base.entrySet()) {
            SerializedConfigValue.SerializedField f = baseEntry.getKey();
            if (m.containsKey((Object)f) && ConfigImplUtil.equalsHandlingNull(baseEntry.getValue(), m.get((Object)f))) {
                m.remove((Object)f);
                continue;
            }
            if (m.containsKey((Object)f)) continue;
            switch (f) {
                case ORIGIN_DESCRIPTION: {
                    throw new ConfigException.BugOrBroken("origin missing description field? " + child);
                }
                case ORIGIN_LINE_NUMBER: {
                    m.put(SerializedConfigValue.SerializedField.ORIGIN_LINE_NUMBER, (Object)-1);
                    break;
                }
                case ORIGIN_END_LINE_NUMBER: {
                    m.put(SerializedConfigValue.SerializedField.ORIGIN_END_LINE_NUMBER, (Object)-1);
                    break;
                }
                case ORIGIN_TYPE: {
                    throw new ConfigException.BugOrBroken("should always be an ORIGIN_TYPE field");
                }
                case ORIGIN_URL: {
                    m.put(SerializedConfigValue.SerializedField.ORIGIN_NULL_URL, (Object)"");
                    break;
                }
                case ORIGIN_RESOURCE: {
                    m.put(SerializedConfigValue.SerializedField.ORIGIN_NULL_RESOURCE, (Object)"");
                    break;
                }
                case ORIGIN_COMMENTS: {
                    m.put(SerializedConfigValue.SerializedField.ORIGIN_NULL_COMMENTS, (Object)"");
                    break;
                }
                case ORIGIN_NULL_URL: 
                case ORIGIN_NULL_RESOURCE: 
                case ORIGIN_NULL_COMMENTS: {
                    throw new ConfigException.BugOrBroken("computing delta, base object should not contain " + (Object)((Object)f) + " " + base);
                }
                case END_MARKER: 
                case ROOT_VALUE: 
                case ROOT_WAS_CONFIG: 
                case UNKNOWN: 
                case VALUE_DATA: 
                case VALUE_ORIGIN: {
                    throw new ConfigException.BugOrBroken("should not appear here: " + (Object)((Object)f));
                }
            }
        }
        return m;
    }

    static SimpleConfigOrigin fromFields(Map<SerializedConfigValue.SerializedField, Object> m) throws IOException {
        if (m.isEmpty()) {
            return null;
        }
        String description = (String)m.get((Object)SerializedConfigValue.SerializedField.ORIGIN_DESCRIPTION);
        Integer lineNumber = (Integer)m.get((Object)SerializedConfigValue.SerializedField.ORIGIN_LINE_NUMBER);
        Integer endLineNumber = (Integer)m.get((Object)SerializedConfigValue.SerializedField.ORIGIN_END_LINE_NUMBER);
        Number originTypeOrdinal = (Number)m.get((Object)SerializedConfigValue.SerializedField.ORIGIN_TYPE);
        if (originTypeOrdinal == null) {
            throw new IOException("Missing ORIGIN_TYPE field");
        }
        OriginType originType = originTypeOrdinal.byteValue() < OriginType.values().length ? OriginType.values()[originTypeOrdinal.byteValue()] : OriginType.GENERIC;
        String urlOrNull = (String)m.get((Object)SerializedConfigValue.SerializedField.ORIGIN_URL);
        String resourceOrNull = (String)m.get((Object)SerializedConfigValue.SerializedField.ORIGIN_RESOURCE);
        List commentsOrNull = (List)m.get((Object)SerializedConfigValue.SerializedField.ORIGIN_COMMENTS);
        if (originType == OriginType.RESOURCE && resourceOrNull == null) {
            resourceOrNull = description;
        }
        return new SimpleConfigOrigin(description, lineNumber != null ? lineNumber : -1, endLineNumber != null ? endLineNumber : -1, originType, urlOrNull, resourceOrNull, commentsOrNull);
    }

    static Map<SerializedConfigValue.SerializedField, Object> applyFieldsDelta(Map<SerializedConfigValue.SerializedField, Object> base, Map<SerializedConfigValue.SerializedField, Object> delta) throws IOException {
        EnumMap<SerializedConfigValue.SerializedField, Object> m = new EnumMap<SerializedConfigValue.SerializedField, Object>(delta);
        for (Map.Entry<SerializedConfigValue.SerializedField, Object> baseEntry : base.entrySet()) {
            SerializedConfigValue.SerializedField f = baseEntry.getKey();
            if (delta.containsKey((Object)f)) continue;
            switch (f) {
                case ORIGIN_DESCRIPTION: {
                    m.put(f, base.get((Object)f));
                    break;
                }
                case ORIGIN_URL: {
                    if (delta.containsKey((Object)SerializedConfigValue.SerializedField.ORIGIN_NULL_URL)) {
                        m.remove((Object)SerializedConfigValue.SerializedField.ORIGIN_NULL_URL);
                        break;
                    }
                    m.put(f, base.get((Object)f));
                    break;
                }
                case ORIGIN_RESOURCE: {
                    if (delta.containsKey((Object)SerializedConfigValue.SerializedField.ORIGIN_NULL_RESOURCE)) {
                        m.remove((Object)SerializedConfigValue.SerializedField.ORIGIN_NULL_RESOURCE);
                        break;
                    }
                    m.put(f, base.get((Object)f));
                    break;
                }
                case ORIGIN_COMMENTS: {
                    if (delta.containsKey((Object)SerializedConfigValue.SerializedField.ORIGIN_NULL_COMMENTS)) {
                        m.remove((Object)SerializedConfigValue.SerializedField.ORIGIN_NULL_COMMENTS);
                        break;
                    }
                    m.put(f, base.get((Object)f));
                    break;
                }
                case ORIGIN_NULL_URL: 
                case ORIGIN_NULL_RESOURCE: 
                case ORIGIN_NULL_COMMENTS: {
                    throw new ConfigException.BugOrBroken("applying fields, base object should not contain " + (Object)((Object)f) + " " + base);
                }
                case ORIGIN_LINE_NUMBER: 
                case ORIGIN_END_LINE_NUMBER: 
                case ORIGIN_TYPE: {
                    m.put(f, base.get((Object)f));
                    break;
                }
                case END_MARKER: 
                case ROOT_VALUE: 
                case ROOT_WAS_CONFIG: 
                case UNKNOWN: 
                case VALUE_DATA: 
                case VALUE_ORIGIN: {
                    throw new ConfigException.BugOrBroken("should not appear here: " + (Object)((Object)f));
                }
            }
        }
        return m;
    }

    static SimpleConfigOrigin fromBase(SimpleConfigOrigin baseOrigin, Map<SerializedConfigValue.SerializedField, Object> delta) throws IOException {
        Map<SerializedConfigValue.SerializedField, Object> baseFields = baseOrigin != null ? baseOrigin.toFields() : Collections.emptyMap();
        Map<SerializedConfigValue.SerializedField, Object> fields = SimpleConfigOrigin.applyFieldsDelta(baseFields, delta);
        return SimpleConfigOrigin.fromFields(fields);
    }
}

