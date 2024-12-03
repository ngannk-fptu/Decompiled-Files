/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.codecs.lucene3x.Lucene3xSegmentInfoFormat;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.TrackingDirectoryWrapper;

public final class SegmentInfo {
    public static final int NO = -1;
    public static final int YES = 1;
    public final String name;
    private int docCount;
    public final Directory dir;
    private boolean isCompoundFile;
    private Codec codec;
    private Map<String, String> diagnostics;
    private Map<String, String> attributes;
    private String version;
    private Set<String> setFiles;

    void setDiagnostics(Map<String, String> diagnostics) {
        this.diagnostics = diagnostics;
    }

    public Map<String, String> getDiagnostics() {
        return this.diagnostics;
    }

    public SegmentInfo(Directory dir, String version, String name, int docCount, boolean isCompoundFile, Codec codec, Map<String, String> diagnostics, Map<String, String> attributes) {
        assert (!(dir instanceof TrackingDirectoryWrapper));
        this.dir = dir;
        this.version = version;
        this.name = name;
        this.docCount = docCount;
        this.isCompoundFile = isCompoundFile;
        this.codec = codec;
        this.diagnostics = diagnostics;
        this.attributes = attributes;
    }

    @Deprecated
    boolean hasSeparateNorms() {
        return this.getAttribute(Lucene3xSegmentInfoFormat.NORMGEN_KEY) != null;
    }

    void setUseCompoundFile(boolean isCompoundFile) {
        this.isCompoundFile = isCompoundFile;
    }

    public boolean getUseCompoundFile() {
        return this.isCompoundFile;
    }

    public void setCodec(Codec codec) {
        assert (this.codec == null);
        if (codec == null) {
            throw new IllegalArgumentException("segmentCodecs must be non-null");
        }
        this.codec = codec;
    }

    public Codec getCodec() {
        return this.codec;
    }

    public int getDocCount() {
        if (this.docCount == -1) {
            throw new IllegalStateException("docCount isn't set yet");
        }
        return this.docCount;
    }

    void setDocCount(int docCount) {
        if (this.docCount != -1) {
            throw new IllegalStateException("docCount was already set");
        }
        this.docCount = docCount;
    }

    public Set<String> files() {
        if (this.setFiles == null) {
            throw new IllegalStateException("files were not computed yet");
        }
        return Collections.unmodifiableSet(this.setFiles);
    }

    public String toString() {
        return this.toString(this.dir, 0);
    }

    public String toString(Directory dir, int delCount) {
        StringBuilder s = new StringBuilder();
        s.append(this.name).append('(').append(this.version == null ? "?" : this.version).append(')').append(':');
        char cfs = this.getUseCompoundFile() ? (char)'c' : 'C';
        s.append(cfs);
        if (this.dir != dir) {
            s.append('x');
        }
        s.append(this.docCount);
        if (delCount != 0) {
            s.append('/').append(delCount);
        }
        return s.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof SegmentInfo) {
            SegmentInfo other = (SegmentInfo)obj;
            return other.dir == this.dir && other.name.equals(this.name);
        }
        return false;
    }

    public int hashCode() {
        return this.dir.hashCode() + this.name.hashCode();
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return this.version;
    }

    public void setFiles(Set<String> files) {
        this.checkFileNames(files);
        this.setFiles = files;
    }

    public void addFiles(Collection<String> files) {
        this.checkFileNames(files);
        this.setFiles.addAll(files);
    }

    public void addFile(String file) {
        this.checkFileNames(Collections.singleton(file));
        this.setFiles.add(file);
    }

    private void checkFileNames(Collection<String> files) {
        Matcher m = IndexFileNames.CODEC_FILE_PATTERN.matcher("");
        for (String file : files) {
            m.reset(file);
            if (m.matches()) continue;
            throw new IllegalArgumentException("invalid codec filename '" + file + "', must match: " + IndexFileNames.CODEC_FILE_PATTERN.pattern());
        }
    }

    public String getAttribute(String key) {
        if (this.attributes == null) {
            return null;
        }
        return this.attributes.get(key);
    }

    public String putAttribute(String key, String value) {
        if (this.attributes == null) {
            this.attributes = new HashMap<String, String>();
        }
        return this.attributes.put(key, value);
    }

    public Map<String, String> attributes() {
        return this.attributes;
    }
}

