/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.core.nodes;

import org.apache.lucene.queryparser.flexible.core.nodes.FieldQueryNode;
import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;

public class FuzzyQueryNode
extends FieldQueryNode {
    private float similarity;
    private int prefixLength;

    public FuzzyQueryNode(CharSequence field, CharSequence term, float minSimilarity, int begin, int end) {
        super(field, term, begin, end);
        this.similarity = minSimilarity;
        this.setLeaf(true);
    }

    public void setPrefixLength(int prefixLength) {
        this.prefixLength = prefixLength;
    }

    public int getPrefixLength() {
        return this.prefixLength;
    }

    @Override
    public CharSequence toQueryString(EscapeQuerySyntax escaper) {
        if (this.isDefaultField(this.field)) {
            return this.getTermEscaped(escaper) + "~" + this.similarity;
        }
        return this.field + ":" + this.getTermEscaped(escaper) + "~" + this.similarity;
    }

    @Override
    public String toString() {
        return "<fuzzy field='" + this.field + "' similarity='" + this.similarity + "' term='" + this.text + "'/>";
    }

    public void setSimilarity(float similarity) {
        this.similarity = similarity;
    }

    @Override
    public FuzzyQueryNode cloneTree() throws CloneNotSupportedException {
        FuzzyQueryNode clone = (FuzzyQueryNode)super.cloneTree();
        clone.similarity = this.similarity;
        return clone;
    }

    public float getSimilarity() {
        return this.similarity;
    }
}

