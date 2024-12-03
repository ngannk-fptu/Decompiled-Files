/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 */
package com.atlassian.sourcemap;

import com.atlassian.sourcemap.Base64VLQ;
import com.atlassian.sourcemap.Mapping;
import com.atlassian.sourcemap.MappingImpl;
import com.atlassian.sourcemap.OrderedSourcesValues;
import com.atlassian.sourcemap.SourceMapJson;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

class SourceMapGenerator {
    private final List<Mapping> mappings = new ArrayList<Mapping>();
    private final OrderedSourcesValues orderedSources = new OrderedSourcesValues();
    private final OrderedSourcesValues orderedSourcesContent = new OrderedSourcesValues();
    private final OrderedSourcesValues orderedNames = new OrderedSourcesValues();
    private final LinkedHashMap<String, String> sourcesRemapper = new LinkedHashMap();
    private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    SourceMapGenerator() {
    }

    public void addSourceAndContents(List<String> sources, List<String> sourcesContent) {
        if (sources.size() != sourcesContent.size()) {
            throw new RuntimeException("The number of sources does not match the number of sourcesContents provided.");
        }
        this.sourcesRemapper.clear();
        for (int i = 0; i < sources.size(); ++i) {
            boolean knownSourceNameButContentDoesNotMatch;
            boolean hasKnownSource;
            String source = sources.get(i);
            String sourceContent = sourcesContent.get(i);
            boolean bl = hasKnownSource = sourceContent != null && this.orderedSourcesContent.hasValue(sourceContent);
            if (hasKnownSource) {
                int sourceIndex = this.orderedSourcesContent.getIndex(sourceContent);
                this.orderedSources.replaceAt(sourceIndex, source);
                continue;
            }
            boolean bl2 = knownSourceNameButContentDoesNotMatch = this.orderedSources.hasValue(source) && (sourceContent == null || !this.orderedSourcesContent.getValueAtIndex(this.orderedSources.getIndex(source)).equals(sourceContent));
            if (knownSourceNameButContentDoesNotMatch) {
                this.remapSource(source, sourceContent);
                continue;
            }
            this.addSourceAndSourceContent(source, sourceContent);
        }
    }

    private void remapSource(String source, String sourceContent) {
        String remappedSource;
        int counter = 1;
        do {
            remappedSource = source + "-uniquified-" + counter;
            ++counter;
        } while (this.orderedSources.hasValue(remappedSource));
        this.sourcesRemapper.put(source, remappedSource);
        this.addSourceAndSourceContent(remappedSource, sourceContent);
    }

    private void addSourceAndSourceContent(String source, String sourceContent) {
        this.orderedSources.add(source);
        this.orderedSourcesContent.add(sourceContent);
    }

    public void addMapping(Mapping mapping) {
        String sourceFileName = mapping.getSourceFileName();
        if (sourceFileName == null) {
            return;
        }
        if (this.sourcesRemapper.containsKey(sourceFileName)) {
            mapping.setSourceFileName(this.sourcesRemapper.get(sourceFileName));
        }
        if (!this.orderedSources.hasValue(mapping.getSourceFileName())) {
            throw new RuntimeException("No source with name '" + mapping.getSourceFileName() + "' exists.");
        }
        String sourceSymbolName = mapping.getSourceSymbolName();
        if (sourceSymbolName != null && !this.orderedNames.hasValue(sourceSymbolName)) {
            this.orderedNames.add(mapping.getSourceSymbolName());
        }
        this.mappings.add(mapping);
    }

    public void addMapping(int generatedLine, int generatedColumn, int sourceLine, int sourceColumn, String sourceFileName, String sourceSymbolName) {
        this.addMapping(new MappingImpl(generatedLine, generatedColumn, sourceLine, sourceColumn, sourceFileName, sourceSymbolName));
    }

    public void addMapping(int generatedLine, int generatedColumn, int sourceLine, int sourceColumn, String sourceFileName) {
        this.addMapping(generatedLine, generatedColumn, sourceLine, sourceColumn, sourceFileName, null);
    }

    public String generate() {
        try {
            StringBuilder mappingsBuilder = new StringBuilder();
            new LineMapper(mappingsBuilder).appendLineMappings();
            SourceMapJson sourceMapJson = new SourceMapJson.Builder().withVersion(3).withSources(this.orderedSources.getValues()).withSourcesContent(this.orderedSourcesContent.getValues()).withNames(this.orderedNames.getValues()).withMappings(mappingsBuilder.toString()).build();
            return this.gson.toJson((Object)sourceMapJson);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int getSourceFileNameIndex(String sourceName) {
        Integer index = this.orderedSources.getIndex(sourceName);
        if (index == null) {
            throw new RuntimeException("source file name " + sourceName + " is unknown!");
        }
        return index;
    }

    private int getSourceSymbolNameIndex(String symbolName) {
        Integer index = this.orderedNames.getIndex(symbolName);
        if (index == null) {
            throw new RuntimeException("source symbol name " + symbolName + " is unknown!");
        }
        return index;
    }

    private class LineMapper {
        private final Appendable out;
        private int previousLine = -1;
        private int previousColumn = 0;
        private int previousSourceFileNameId;
        private int previousSourceLine;
        private int previousSourceColumn;
        private int previousSourceSymbolNameId;

        LineMapper(Appendable out) {
            this.out = out;
        }

        void appendLineMappings() throws IOException {
            for (Mapping mapping : SourceMapGenerator.this.mappings) {
                int generatedLine = mapping.getGeneratedLine();
                int generatedColumn = mapping.getGeneratedColumn();
                if (generatedLine > 0 && this.previousLine != generatedLine) {
                    int start;
                    for (int i = start = this.previousLine == -1 ? 0 : this.previousLine; i < generatedLine; ++i) {
                        this.finishLine();
                    }
                }
                if (this.previousLine != generatedLine) {
                    this.previousColumn = 0;
                } else {
                    this.finishMapping();
                }
                this.writeEntry(mapping, generatedColumn);
                this.previousLine = generatedLine;
                this.previousColumn = generatedColumn;
            }
        }

        private void finishMapping() throws IOException {
            this.out.append(',');
        }

        private void finishLine() throws IOException {
            this.out.append(';');
        }

        void writeEntry(Mapping m, int column) throws IOException {
            Base64VLQ.encode(this.out, column - this.previousColumn);
            this.previousColumn = column;
            if (m != null) {
                int sourceId = SourceMapGenerator.this.getSourceFileNameIndex(m.getSourceFileName());
                Base64VLQ.encode(this.out, sourceId - this.previousSourceFileNameId);
                this.previousSourceFileNameId = sourceId;
                int srcline = m.getSourceLine();
                int srcColumn = m.getSourceColumn();
                Base64VLQ.encode(this.out, srcline - this.previousSourceLine);
                this.previousSourceLine = srcline;
                Base64VLQ.encode(this.out, srcColumn - this.previousSourceColumn);
                this.previousSourceColumn = srcColumn;
                if (m.getSourceSymbolName() != null) {
                    int nameId = SourceMapGenerator.this.getSourceSymbolNameIndex(m.getSourceSymbolName());
                    Base64VLQ.encode(this.out, nameId - this.previousSourceSymbolNameId);
                    this.previousSourceSymbolNameId = nameId;
                }
            }
        }
    }
}

