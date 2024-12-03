/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.core.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNodeImpl;
import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;

public class PathQueryNode
extends QueryNodeImpl {
    private List<QueryText> values = null;

    public PathQueryNode(List<QueryText> pathElements) {
        this.values = pathElements;
        if (pathElements.size() <= 1) {
            throw new RuntimeException("PathQuerynode requires more 2 or more path elements.");
        }
    }

    public List<QueryText> getPathElements() {
        return this.values;
    }

    public void setPathElements(List<QueryText> elements) {
        this.values = elements;
    }

    public QueryText getPathElement(int index) {
        return this.values.get(index);
    }

    public CharSequence getFirstPathElement() {
        return this.values.get((int)0).value;
    }

    public List<QueryText> getPathElements(int startIndex) {
        ArrayList<QueryText> rValues = new ArrayList<QueryText>();
        for (int i = startIndex; i < this.values.size(); ++i) {
            try {
                rValues.add(this.values.get(i).clone());
                continue;
            }
            catch (CloneNotSupportedException cloneNotSupportedException) {
                // empty catch block
            }
        }
        return rValues;
    }

    private CharSequence getPathString() {
        StringBuilder path = new StringBuilder();
        for (QueryText pathelement : this.values) {
            path.append("/").append(pathelement.value);
        }
        return path.toString();
    }

    @Override
    public CharSequence toQueryString(EscapeQuerySyntax escaper) {
        StringBuilder path = new StringBuilder();
        path.append("/").append(this.getFirstPathElement());
        for (QueryText pathelement : this.getPathElements(1)) {
            CharSequence value = escaper.escape(pathelement.value, Locale.getDefault(), EscapeQuerySyntax.Type.STRING);
            path.append("/\"").append(value).append("\"");
        }
        return path.toString();
    }

    @Override
    public String toString() {
        QueryText text = this.values.get(0);
        return "<path start='" + text.begin + "' end='" + text.end + "' path='" + this.getPathString() + "'/>";
    }

    @Override
    public QueryNode cloneTree() throws CloneNotSupportedException {
        PathQueryNode clone = (PathQueryNode)super.cloneTree();
        if (this.values != null) {
            ArrayList<QueryText> localValues = new ArrayList<QueryText>();
            for (QueryText value : this.values) {
                localValues.add(value.clone());
            }
            clone.values = localValues;
        }
        return clone;
    }

    public static class QueryText
    implements Cloneable {
        CharSequence value = null;
        int begin;
        int end;

        public QueryText(CharSequence value, int begin, int end) {
            this.value = value;
            this.begin = begin;
            this.end = end;
        }

        public QueryText clone() throws CloneNotSupportedException {
            QueryText clone = (QueryText)super.clone();
            clone.value = this.value;
            clone.begin = this.begin;
            clone.end = this.end;
            return clone;
        }

        public CharSequence getValue() {
            return this.value;
        }

        public int getBegin() {
            return this.begin;
        }

        public int getEnd() {
            return this.end;
        }

        public String toString() {
            return this.value + ", " + this.begin + ", " + this.end;
        }
    }
}

