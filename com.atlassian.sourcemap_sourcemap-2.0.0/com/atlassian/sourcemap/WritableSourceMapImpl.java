/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sourcemap;

import com.atlassian.sourcemap.InternalUtil;
import com.atlassian.sourcemap.Mapping;
import com.atlassian.sourcemap.MappingImpl;
import com.atlassian.sourcemap.SourceMapConsumer;
import com.atlassian.sourcemap.SourceMapGenerator;
import com.atlassian.sourcemap.WritableSourceMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WritableSourceMapImpl
implements WritableSourceMap {
    private final SourceMapGenerator sourceMapGenerator = new SourceMapGenerator();
    private int lastGeneratedLine;
    private int lastGeneratedColumn;

    private WritableSourceMapImpl(List<String> sources, List<String> sourcesContent) {
        this.addSourcesAndContents(sources, sourcesContent);
    }

    @Override
    public void addSourcesAndContents(List<String> sources, List<String> sourcesContent) {
        this.sourceMapGenerator.addSourceAndContents(sources, sourcesContent);
    }

    @Override
    public void addMapping(int generatedLine, int generatedColumn, int sourceLine, int sourceColumn, String sourceFileName) {
        this.addMapping(generatedLine, generatedColumn, sourceLine, sourceColumn, sourceFileName, null);
    }

    @Override
    public void addMapping(int generatedLine, int generatedColumn, int sourceLine, int sourceColumn, String sourceFileName, String sourceSymbolName) {
        this.addMapping(new MappingImpl(generatedLine, generatedColumn, sourceLine, sourceColumn, sourceFileName, sourceSymbolName));
    }

    @Override
    public void addMapping(Mapping mapping) {
        if (this.lastGeneratedLine > mapping.getGeneratedLine()) {
            throw new RuntimeException("Mappings need to be added line by line.\nThe last added line was " + this.lastGeneratedLine + " the current mapping, however, is for the previous line: " + mapping.getGeneratedLine() + ".\nPlease ensure mappings are provided in the proper order!");
        }
        if (this.lastGeneratedLine == mapping.getGeneratedLine() && this.lastGeneratedColumn > mapping.getGeneratedColumn()) {
            throw new RuntimeException("Mappings need to be added line by line and column by column.\nThe last added column for this line was " + this.lastGeneratedColumn + " the current mapping, however, is for the previous column: " + mapping.getGeneratedColumn() + ".\nPlease ensure mappings are provided in the proper order!");
        }
        this.lastGeneratedLine = mapping.getGeneratedLine();
        this.lastGeneratedColumn = mapping.getGeneratedColumn();
        this.sourceMapGenerator.addMapping(mapping);
    }

    @Override
    public String generate() {
        return this.sourceMapGenerator.generate();
    }

    @Override
    public String generateForHumans() {
        SourceMapConsumer sourcemapConsumer = new SourceMapConsumer(this.generate());
        StringBuilder buff = new StringBuilder();
        buff.append("{\n");
        buff.append("  sources  : [\n    ").append(InternalUtil.join(sourcemapConsumer.getSourceFileNames(), "\n    ")).append("\n  ]\n");
        buff.append("  mappings : [\n    ");
        int[] previousLine = new int[]{-1};
        sourcemapConsumer.eachMapping(mapping -> {
            if (mapping.getGeneratedLine() != previousLine[0] && previousLine[0] != -1) {
                buff.append("\n    ");
            } else if (previousLine[0] != -1) {
                buff.append(", ");
            }
            previousLine[0] = mapping.getGeneratedLine();
            String shortName = mapping.getSourceFileName().replaceAll(".*/", "");
            buff.append("(").append(mapping.getGeneratedLine()).append(":").append(mapping.getGeneratedColumn()).append(" -> ").append(shortName).append(":").append(mapping.getSourceLine()).append(":").append(mapping.getSourceColumn()).append(")");
        });
        buff.append("\n  ]\n}");
        return buff.toString();
    }

    public static class Builder {
        private List<String> sources;
        private List<String> sourcesContent;

        public Builder withSourcesAndSourcesContent(List<String> sources, List<String> sourcesContent) {
            this.sources = sources;
            this.sourcesContent = sourcesContent;
            return this;
        }

        public Builder withSources(List<String> sources) {
            this.sources = sources;
            this.sourcesContent = Arrays.asList(new String[sources.size()]);
            return this;
        }

        public Builder empty() {
            this.sources = Collections.emptyList();
            this.sourcesContent = Collections.emptyList();
            return this;
        }

        public WritableSourceMap build() {
            if (this.sources == null) {
                throw new RuntimeException("No sources were specified");
            }
            if (this.sourcesContent == null) {
                throw new RuntimeException("No sourcesContent was specified");
            }
            if (this.sources.size() != this.sourcesContent.size()) {
                throw new RuntimeException("The number of sources does not match the number of sourcesContents provided.");
            }
            return new WritableSourceMapImpl(this.sources, this.sourcesContent);
        }
    }
}

