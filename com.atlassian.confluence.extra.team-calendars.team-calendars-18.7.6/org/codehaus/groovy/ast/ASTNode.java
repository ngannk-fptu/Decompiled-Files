/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast;

import java.util.Collections;
import java.util.Map;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.util.ListHashMap;

public class ASTNode {
    private int lineNumber = -1;
    private int columnNumber = -1;
    private int lastLineNumber = -1;
    private int lastColumnNumber = -1;
    private ListHashMap metaDataMap = null;

    public void visit(GroovyCodeVisitor visitor) {
        throw new RuntimeException("No visit() method implemented for class: " + this.getClass().getName());
    }

    public String getText() {
        return "<not implemented yet for class: " + this.getClass().getName() + ">";
    }

    public int getLineNumber() {
        return this.lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getColumnNumber() {
        return this.columnNumber;
    }

    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }

    public int getLastLineNumber() {
        return this.lastLineNumber;
    }

    public void setLastLineNumber(int lastLineNumber) {
        this.lastLineNumber = lastLineNumber;
    }

    public int getLastColumnNumber() {
        return this.lastColumnNumber;
    }

    public void setLastColumnNumber(int lastColumnNumber) {
        this.lastColumnNumber = lastColumnNumber;
    }

    public void setSourcePosition(ASTNode node) {
        this.columnNumber = node.getColumnNumber();
        this.lastLineNumber = node.getLastLineNumber();
        this.lastColumnNumber = node.getLastColumnNumber();
        this.lineNumber = node.getLineNumber();
    }

    public <T> T getNodeMetaData(Object key) {
        if (this.metaDataMap == null) {
            return null;
        }
        return (T)this.metaDataMap.get(key);
    }

    public void copyNodeMetaData(ASTNode other) {
        if (other.metaDataMap == null) {
            return;
        }
        if (this.metaDataMap == null) {
            this.metaDataMap = new ListHashMap();
        }
        this.metaDataMap.putAll(other.metaDataMap);
    }

    public void setNodeMetaData(Object key, Object value) {
        Object old;
        if (key == null) {
            throw new GroovyBugError("Tried to set meta data with null key on " + this + ".");
        }
        if (this.metaDataMap == null) {
            this.metaDataMap = new ListHashMap();
        }
        if ((old = this.metaDataMap.put(key, value)) != null) {
            throw new GroovyBugError("Tried to overwrite existing meta data " + this + ".");
        }
    }

    public Object putNodeMetaData(Object key, Object value) {
        if (key == null) {
            throw new GroovyBugError("Tried to set meta data with null key on " + this + ".");
        }
        if (this.metaDataMap == null) {
            this.metaDataMap = new ListHashMap();
        }
        return this.metaDataMap.put(key, value);
    }

    public void removeNodeMetaData(Object key) {
        if (key == null) {
            throw new GroovyBugError("Tried to remove meta data with null key " + this + ".");
        }
        if (this.metaDataMap == null) {
            return;
        }
        this.metaDataMap.remove(key);
    }

    public Map<?, ?> getNodeMetaData() {
        if (this.metaDataMap == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(this.metaDataMap);
    }
}

