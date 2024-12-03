/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonParseException
 */
package com.atlassian.sourcemap;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SourceMapJson {
    private int version;
    private String sourceRoot;
    private List<String> sources;
    private List<String> sourcesContent;
    private List<String> names;
    private String mappings;

    private void ensureSourceContents() {
        if (this.sourcesContent == null || this.sourcesContent.size() != this.sources.size()) {
            this.sourcesContent = Arrays.asList(new String[this.sources.size()]);
        }
    }

    private void applyAndVoidRoot() {
        if (this.sourceRoot != null && !this.sourceRoot.equals("")) {
            this.sources = this.sources.stream().map(source -> this.sourceRoot + source).collect(Collectors.toList());
        }
        this.sourceRoot = null;
    }

    public static SourceMapJson parse(String sourceMapData) {
        SourceMapJson sourceMapRoot;
        try {
            sourceMapRoot = (SourceMapJson)new Gson().fromJson(sourceMapData, SourceMapJson.class);
        }
        catch (JsonParseException e) {
            throw new RuntimeException(e);
        }
        sourceMapRoot.ensureSourceContents();
        sourceMapRoot.applyAndVoidRoot();
        return sourceMapRoot;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getSourceRoot() {
        return this.sourceRoot;
    }

    public void setSourceRoot(String sourceRoot) {
        this.sourceRoot = sourceRoot;
    }

    public List<String> getSources() {
        return this.sources;
    }

    public void setSources(List<String> sources) {
        this.sources = sources;
    }

    public List<String> getSourcesContent() {
        return this.sourcesContent;
    }

    public void setSourcesContent(List<String> sourcesContent) {
        this.sourcesContent = sourcesContent;
    }

    public List<String> getNames() {
        return this.names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public String getMappings() {
        return this.mappings;
    }

    public void setMappings(String mappings) {
        this.mappings = mappings;
    }

    public static class Builder {
        private final SourceMapJson sourceMapJson = new SourceMapJson();

        public Builder withVersion(int version) {
            this.sourceMapJson.version = version;
            return this;
        }

        public Builder withSources(List<String> sources) {
            this.sourceMapJson.sources = sources;
            return this;
        }

        public Builder withSourcesContent(List<String> sourcesContent) {
            this.sourceMapJson.sourcesContent = sourcesContent;
            return this;
        }

        public Builder withNames(List<String> names) {
            this.sourceMapJson.names = names;
            return this;
        }

        public Builder withMappings(String mappings) {
            this.sourceMapJson.mappings = mappings;
            return this;
        }

        public SourceMapJson build() {
            if (this.sourceMapJson.version == 0) {
                this.sourceMapJson.version = 3;
            }
            return this.sourceMapJson;
        }
    }
}

