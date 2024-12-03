/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import org.apache.lucene.search.Explanation;

public class ComplexExplanation
extends Explanation {
    private Boolean match;

    public ComplexExplanation() {
    }

    public ComplexExplanation(boolean match, float value, String description) {
        super(value, description);
        this.match = match;
    }

    public Boolean getMatch() {
        return this.match;
    }

    public void setMatch(Boolean match) {
        this.match = match;
    }

    @Override
    public boolean isMatch() {
        Boolean m = this.getMatch();
        return null != m ? m.booleanValue() : super.isMatch();
    }

    @Override
    protected String getSummary() {
        if (null == this.getMatch()) {
            return super.getSummary();
        }
        return this.getValue() + " = " + (this.isMatch() ? "(MATCH) " : "(NON-MATCH) ") + this.getDescription();
    }
}

