/*
 * Decompiled with CFR 0.152.
 */
package org.jvnet.fastinfoset;

import org.jvnet.fastinfoset.Vocabulary;

public class ExternalVocabulary {
    public final String URI;
    public final Vocabulary vocabulary;

    public ExternalVocabulary(String URI2, Vocabulary vocabulary) {
        if (URI2 == null || vocabulary == null) {
            throw new IllegalArgumentException();
        }
        this.URI = URI2;
        this.vocabulary = vocabulary;
    }
}

