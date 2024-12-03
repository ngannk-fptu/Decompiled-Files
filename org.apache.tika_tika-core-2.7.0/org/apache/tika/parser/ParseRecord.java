/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.parser;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.tika.metadata.Metadata;

public class ParseRecord {
    private static int MAX_PARSERS = 100;
    private static final int MAX_EXCEPTIONS = 100;
    private static final int MAX_WARNINGS = 100;
    private static final int MAX_METADATA_LIST_SIZE = 100;
    private int depth = 0;
    private final Set<String> parsers = new LinkedHashSet<String>();
    private final List<Exception> exceptions = new ArrayList<Exception>();
    private final List<String> warnings = new ArrayList<String>();
    private final List<Metadata> metadataList = new ArrayList<Metadata>();
    private boolean writeLimitReached = false;

    void beforeParse() {
        ++this.depth;
    }

    void afterParse() {
        --this.depth;
    }

    public int getDepth() {
        return this.depth;
    }

    public String[] getParsers() {
        return this.parsers.toArray(new String[0]);
    }

    void addParserClass(String parserClass) {
        if (this.parsers.size() < MAX_PARSERS) {
            this.parsers.add(parserClass);
        }
    }

    public void addException(Exception e) {
        if (this.exceptions.size() < 100) {
            this.exceptions.add(e);
        }
    }

    public void addWarning(String msg) {
        if (this.warnings.size() < 100) {
            this.warnings.add(msg);
        }
    }

    public void addMetadata(Metadata metadata) {
        if (this.metadataList.size() < 100) {
            this.metadataList.add(metadata);
        }
    }

    public void setWriteLimitReached(boolean writeLimitReached) {
        this.writeLimitReached = writeLimitReached;
    }

    public List<Exception> getExceptions() {
        return this.exceptions;
    }

    public List<String> getWarnings() {
        return this.warnings;
    }

    public boolean isWriteLimitReached() {
        return this.writeLimitReached;
    }

    public List<Metadata> getMetadataList() {
        return this.metadataList;
    }
}

