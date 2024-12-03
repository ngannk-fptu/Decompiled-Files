/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.postingshighlight;

public class PassageScorer {
    final float k1;
    final float b;
    final float pivot;

    public PassageScorer() {
        this(1.2f, 0.75f, 87.0f);
    }

    public PassageScorer(float k1, float b, float pivot) {
        this.k1 = k1;
        this.b = b;
        this.pivot = pivot;
    }

    public float weight(int contentLength, int totalTermFreq) {
        float numDocs = 1.0f + (float)contentLength / this.pivot;
        return (this.k1 + 1.0f) * (float)Math.log(1.0 + ((double)numDocs + 0.5) / ((double)totalTermFreq + 0.5));
    }

    public float tf(int freq, int passageLen) {
        float norm = this.k1 * (1.0f - this.b + this.b * ((float)passageLen / this.pivot));
        return (float)freq / ((float)freq + norm);
    }

    public float norm(int passageStart) {
        return 1.0f + 1.0f / (float)Math.log(this.pivot + (float)passageStart);
    }
}

