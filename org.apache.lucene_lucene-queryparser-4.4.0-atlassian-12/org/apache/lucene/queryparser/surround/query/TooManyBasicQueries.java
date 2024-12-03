/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.surround.query;

import java.io.IOException;

public class TooManyBasicQueries
extends IOException {
    public TooManyBasicQueries(int maxBasicQueries) {
        super("Exceeded maximum of " + maxBasicQueries + " basic queries.");
    }
}

