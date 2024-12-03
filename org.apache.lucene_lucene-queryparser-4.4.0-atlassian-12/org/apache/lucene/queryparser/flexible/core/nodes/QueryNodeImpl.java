/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.core.nodes;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import org.apache.lucene.queryparser.flexible.core.messages.QueryParserMessages;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.util.StringUtils;
import org.apache.lucene.queryparser.flexible.messages.NLS;

public abstract class QueryNodeImpl
implements QueryNode,
Cloneable {
    public static final String PLAINTEXT_FIELD_NAME = "_plain";
    private boolean isLeaf = true;
    private Hashtable<String, Object> tags = new Hashtable();
    private List<QueryNode> clauses = null;
    private QueryNode parent = null;
    protected boolean toQueryStringIgnoreFields = false;

    protected void allocate() {
        if (this.clauses == null) {
            this.clauses = new ArrayList<QueryNode>();
        } else {
            this.clauses.clear();
        }
    }

    @Override
    public final void add(QueryNode child) {
        if (this.isLeaf() || this.clauses == null || child == null) {
            throw new IllegalArgumentException(NLS.getLocalizedMessage(QueryParserMessages.NODE_ACTION_NOT_SUPPORTED));
        }
        this.clauses.add(child);
        ((QueryNodeImpl)child).setParent(this);
    }

    @Override
    public final void add(List<QueryNode> children) {
        if (this.isLeaf() || this.clauses == null) {
            throw new IllegalArgumentException(NLS.getLocalizedMessage(QueryParserMessages.NODE_ACTION_NOT_SUPPORTED));
        }
        for (QueryNode child : children) {
            this.add(child);
        }
    }

    @Override
    public boolean isLeaf() {
        return this.isLeaf;
    }

    @Override
    public final void set(List<QueryNode> children) {
        if (this.isLeaf() || this.clauses == null) {
            ResourceBundle bundle = ResourceBundle.getBundle("org.apache.lucene.queryParser.messages.QueryParserMessages", Locale.getDefault());
            String message = bundle.getObject("Q0008E.NODE_ACTION_NOT_SUPPORTED").toString();
            throw new IllegalArgumentException(message);
        }
        for (QueryNode child : children) {
            ((QueryNodeImpl)child).setParent(null);
        }
        this.allocate();
        for (QueryNode child : children) {
            this.add(child);
        }
    }

    @Override
    public QueryNode cloneTree() throws CloneNotSupportedException {
        QueryNodeImpl clone = (QueryNodeImpl)super.clone();
        clone.isLeaf = this.isLeaf;
        clone.tags = new Hashtable();
        if (this.clauses != null) {
            ArrayList<QueryNode> localClauses = new ArrayList<QueryNode>();
            for (QueryNode clause : this.clauses) {
                localClauses.add(clause.cloneTree());
            }
            clone.clauses = localClauses;
        }
        return clone;
    }

    public QueryNode clone() throws CloneNotSupportedException {
        return this.cloneTree();
    }

    protected void setLeaf(boolean isLeaf) {
        this.isLeaf = isLeaf;
    }

    @Override
    public final List<QueryNode> getChildren() {
        if (this.isLeaf() || this.clauses == null) {
            return null;
        }
        return this.clauses;
    }

    @Override
    public void setTag(String tagName, Object value) {
        this.tags.put(tagName.toLowerCase(Locale.ROOT), value);
    }

    @Override
    public void unsetTag(String tagName) {
        this.tags.remove(tagName.toLowerCase(Locale.ROOT));
    }

    @Override
    public boolean containsTag(String tagName) {
        return this.tags.containsKey(tagName.toLowerCase(Locale.ROOT));
    }

    @Override
    public Object getTag(String tagName) {
        return this.tags.get(tagName.toLowerCase(Locale.ROOT));
    }

    private void setParent(QueryNode parent) {
        this.parent = parent;
    }

    @Override
    public QueryNode getParent() {
        return this.parent;
    }

    protected boolean isRoot() {
        return this.getParent() == null;
    }

    protected boolean isDefaultField(CharSequence fld) {
        if (this.toQueryStringIgnoreFields) {
            return true;
        }
        if (fld == null) {
            return true;
        }
        return PLAINTEXT_FIELD_NAME.equals(StringUtils.toString(fld));
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public Map<String, Object> getTagMap() {
        return (Map)this.tags.clone();
    }
}

