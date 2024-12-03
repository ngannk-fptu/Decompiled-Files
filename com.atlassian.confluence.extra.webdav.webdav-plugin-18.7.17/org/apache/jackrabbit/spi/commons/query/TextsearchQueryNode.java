/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query;

import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.commons.conversion.MalformedPathException;
import org.apache.jackrabbit.spi.commons.name.PathBuilder;
import org.apache.jackrabbit.spi.commons.query.QueryNode;
import org.apache.jackrabbit.spi.commons.query.QueryNodeVisitor;

public class TextsearchQueryNode
extends QueryNode {
    private final String query;
    private Path relPath;
    private boolean propertyRef;

    protected TextsearchQueryNode(QueryNode parent, String query) {
        super(parent);
        this.query = query;
        this.relPath = null;
        this.propertyRef = false;
    }

    @Override
    public Object accept(QueryNodeVisitor visitor, Object data) throws RepositoryException {
        return visitor.visit(this, data);
    }

    @Override
    public int getType() {
        return 4;
    }

    public String getQuery() {
        return this.query;
    }

    public Name getPropertyName() {
        return this.relPath == null ? null : this.relPath.getName();
    }

    public void setPropertyName(Name property) {
        PathBuilder builder = new PathBuilder();
        builder.addLast(property);
        try {
            this.relPath = builder.getPath();
            this.propertyRef = true;
        }
        catch (MalformedPathException malformedPathException) {
            // empty catch block
        }
    }

    public Path getRelativePath() {
        return this.relPath;
    }

    public void setRelativePath(Path relPath) {
        if (relPath != null && relPath.isAbsolute()) {
            throw new IllegalArgumentException("relPath must be relative");
        }
        this.relPath = relPath;
        if (relPath == null) {
            this.propertyRef = false;
        }
    }

    public void addPathElement(Path.Element element) {
        PathBuilder builder = new PathBuilder();
        if (this.relPath != null) {
            builder.addAll(this.relPath.getElements());
        }
        builder.addLast(element);
        try {
            this.relPath = builder.getPath();
        }
        catch (MalformedPathException malformedPathException) {
            // empty catch block
        }
    }

    public boolean getReferencesProperty() {
        return this.propertyRef;
    }

    public void setReferencesProperty(boolean b) {
        this.propertyRef = b;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TextsearchQueryNode) {
            TextsearchQueryNode other = (TextsearchQueryNode)obj;
            return (this.query == null ? other.query == null : this.query.equals(other.query)) && (this.relPath == null ? other.relPath == null : this.relPath.equals(other.relPath) && this.propertyRef == other.propertyRef);
        }
        return false;
    }

    @Override
    public boolean needsSystemTree() {
        return false;
    }
}

