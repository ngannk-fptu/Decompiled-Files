/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.search.DocIdSetIterator;
import java.io.IOException;

public abstract class DocIdSet {
    public static final DocIdSet EMPTY_DOCIDSET = new DocIdSet(){
        private final DocIdSetIterator iterator = new DocIdSetIterator(){

            public int advance(int target) throws IOException {
                return Integer.MAX_VALUE;
            }

            public int docID() {
                return Integer.MAX_VALUE;
            }

            public int nextDoc() throws IOException {
                return Integer.MAX_VALUE;
            }
        };

        public DocIdSetIterator iterator() {
            return this.iterator;
        }

        public boolean isCacheable() {
            return true;
        }
    };

    public abstract DocIdSetIterator iterator() throws IOException;

    public boolean isCacheable() {
        return false;
    }
}

