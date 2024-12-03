/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sourcemap;

import com.atlassian.sourcemap.Base64VLQ;
import com.atlassian.sourcemap.Mapping;
import com.atlassian.sourcemap.MappingImpl;
import com.atlassian.sourcemap.SourceMapJson;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

class SourceMapConsumer {
    static final int UNMAPPED = -1;
    private List<String> sourceFileNames;
    private List<String> sourceSymbolNames;
    private List<String> sourcesContent;
    private ArrayList<ArrayList<Mapping>> lines = null;

    public SourceMapConsumer(String sourceMapData) {
        this.parse(sourceMapData);
    }

    private void parse(String sourceMapData) {
        SourceMapJson sourceMapBean = SourceMapJson.parse(sourceMapData);
        int version = sourceMapBean.getVersion();
        if (version != 3) {
            throw new RuntimeException("Unknown version: " + version);
        }
        this.sourceFileNames = sourceMapBean.getSources();
        this.sourceSymbolNames = sourceMapBean.getNames();
        this.sourcesContent = sourceMapBean.getSourcesContent();
        this.lines = new ArrayList();
        new MappingBuilder(sourceMapBean.getMappings()).build();
    }

    public Mapping getMapping(int lineNumber, int column) {
        if (lineNumber >= this.lines.size()) {
            return null;
        }
        if (lineNumber < 0) {
            throw new RuntimeException("invalid line number!");
        }
        if (column < 0) {
            throw new RuntimeException("invalid column number!");
        }
        if (this.lines.get(lineNumber) == null) {
            return this.getPreviousMapping(lineNumber);
        }
        ArrayList<Mapping> entries = this.lines.get(lineNumber);
        if (entries.isEmpty()) {
            throw new RuntimeException("empty list of entries!");
        }
        if (entries.get(0).getGeneratedColumn() > column) {
            return this.getPreviousMapping(lineNumber);
        }
        int index = this.search(entries, column, 0, entries.size() - 1);
        if (index < 0) {
            throw new RuntimeException("can't find entry!");
        }
        return this.getMappingForEntry(entries.get(index));
    }

    public void eachMapping(Consumer<Mapping> cb) {
        for (List list : this.lines) {
            if (list == null) continue;
            for (Mapping mapping : list) {
                if (mapping.getSourceFileName() == null) continue;
                cb.accept(mapping);
            }
        }
    }

    public void recalculateWithOffset(int offset) {
        if (offset < 1) {
            return;
        }
        this.eachMapping(mapping -> mapping.setGeneratedLine(mapping.getGeneratedLine() + offset));
        this.lines.addAll(0, Collections.nCopies(offset, null));
    }

    public List<String> getSourceFileNames() {
        return this.sourceFileNames;
    }

    public List<String> getSourceSymbolNames() {
        return this.sourceSymbolNames;
    }

    public List<String> getSourcesContent() {
        return this.sourcesContent;
    }

    private int search(ArrayList<Mapping> entries, int target, int start, int end) {
        int mid;
        int compare;
        do {
            if ((compare = this.compareEntry(entries, mid = (end - start) / 2 + start, target)) != 0) continue;
            return mid;
        } while (!(compare < 0 ? (start = mid + 1) > end : (end = mid - 1) < start));
        return end;
    }

    private int compareEntry(ArrayList<Mapping> entries, int entry, int target) {
        return entries.get(entry).getGeneratedColumn() - target;
    }

    private Mapping getPreviousMapping(int lineNumber) {
        do {
            if (lineNumber != 0) continue;
            return null;
        } while (this.lines.get(--lineNumber) == null);
        ArrayList<Mapping> entries = this.lines.get(lineNumber);
        return this.getMappingForEntry(entries.get(entries.size() - 1));
    }

    private Mapping getMappingForEntry(Mapping entry) {
        return entry.getSourceFileName() == null ? null : entry;
    }

    private static class StringCharIterator
    implements Base64VLQ.CharIterator {
        final String content;
        final int length;
        int current = 0;

        StringCharIterator(String content) {
            this.content = content;
            this.length = content.length();
        }

        @Override
        public char next() {
            return this.content.charAt(this.current++);
        }

        char peek() {
            return this.content.charAt(this.current);
        }

        @Override
        public boolean hasNext() {
            return this.current < this.length;
        }
    }

    private class MappingBuilder {
        private static final int MAX_ENTRY_VALUES = 5;
        private final StringCharIterator content;
        private int line = 0;
        private int previousCol = 0;
        private int previousSrcId = 0;
        private int previousSrcLine = 0;
        private int previousSrcColumn = 0;
        private int previousNameId = 0;

        MappingBuilder(String lineMap) {
            this.content = new StringCharIterator(lineMap);
        }

        void build() {
            int[] temp = new int[5];
            ArrayList<Mapping> entries = new ArrayList<Mapping>();
            while (this.content.hasNext()) {
                if (this.tryConsumeToken(';')) {
                    this.completeLine(entries);
                    if (entries.isEmpty()) continue;
                    entries = new ArrayList();
                    continue;
                }
                int entryValues = 0;
                while (!this.entryComplete()) {
                    temp[entryValues] = this.nextValue();
                    ++entryValues;
                }
                Mapping entry = this.decodeEntry(this.line, temp, entryValues);
                entries.add(entry);
                this.tryConsumeToken(',');
            }
            if (!entries.isEmpty()) {
                this.completeLine(entries);
            }
        }

        private void completeLine(ArrayList<Mapping> entries) {
            if (!entries.isEmpty()) {
                SourceMapConsumer.this.lines.add(entries);
            } else {
                SourceMapConsumer.this.lines.add(null);
            }
            ++this.line;
            this.previousCol = 0;
        }

        private Mapping decodeEntry(int generatedLine, int[] vals, int entryValues) {
            switch (entryValues) {
                case 1: {
                    MappingImpl entry = new MappingImpl(generatedLine, vals[0] + this.previousCol, -1, -1, null, null);
                    this.previousCol = entry.getGeneratedColumn();
                    return entry;
                }
                case 4: {
                    int sourceFileNameIndex = vals[1] + this.previousSrcId;
                    String sourceFileName = (String)SourceMapConsumer.this.sourceFileNames.get(sourceFileNameIndex);
                    MappingImpl entry = new MappingImpl(generatedLine, vals[0] + this.previousCol, vals[2] + this.previousSrcLine, vals[3] + this.previousSrcColumn, sourceFileName, null);
                    this.previousCol = entry.getGeneratedColumn();
                    this.previousSrcLine = entry.getSourceLine();
                    this.previousSrcColumn = entry.getSourceColumn();
                    this.previousSrcId = sourceFileNameIndex;
                    return entry;
                }
                case 5: {
                    int sourceFileNameIndex = vals[1] + this.previousSrcId;
                    String sourceFileName = (String)SourceMapConsumer.this.sourceFileNames.get(sourceFileNameIndex);
                    int sourceSymbolNameIndex = vals[4] + this.previousNameId;
                    String sourceSymbolName = (String)SourceMapConsumer.this.sourceSymbolNames.get(sourceSymbolNameIndex);
                    MappingImpl entry = new MappingImpl(generatedLine, vals[0] + this.previousCol, vals[2] + this.previousSrcLine, vals[3] + this.previousSrcColumn, sourceFileName, sourceSymbolName);
                    this.previousCol = entry.getGeneratedColumn();
                    this.previousSrcLine = entry.getSourceLine();
                    this.previousSrcColumn = entry.getSourceColumn();
                    this.previousSrcId = sourceFileNameIndex;
                    this.previousNameId = sourceSymbolNameIndex;
                    return entry;
                }
            }
            throw new IllegalStateException("Unexpected number of values for entry:" + entryValues);
        }

        private boolean tryConsumeToken(char token) {
            if (this.content.hasNext() && this.content.peek() == token) {
                this.content.next();
                return true;
            }
            return false;
        }

        private boolean entryComplete() {
            if (!this.content.hasNext()) {
                return true;
            }
            char c = this.content.peek();
            return c == ';' || c == ',';
        }

        private int nextValue() {
            return Base64VLQ.decode(this.content);
        }
    }
}

