/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.highlight;

public class TextFragment {
    CharSequence markedUpText;
    int fragNum;
    int textStartPos;
    int textEndPos;
    float score;

    public TextFragment(CharSequence markedUpText, int textStartPos, int fragNum) {
        this.markedUpText = markedUpText;
        this.textStartPos = textStartPos;
        this.fragNum = fragNum;
    }

    void setScore(float score) {
        this.score = score;
    }

    public float getScore() {
        return this.score;
    }

    public void merge(TextFragment frag2) {
        this.textEndPos = frag2.textEndPos;
        this.score = Math.max(this.score, frag2.score);
    }

    public boolean follows(TextFragment fragment) {
        return this.textStartPos == fragment.textEndPos;
    }

    public int getFragNum() {
        return this.fragNum;
    }

    public String toString() {
        return this.markedUpText.subSequence(this.textStartPos, this.textEndPos).toString();
    }
}

