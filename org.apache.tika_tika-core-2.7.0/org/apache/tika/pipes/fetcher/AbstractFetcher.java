/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.pipes.fetcher;

import org.apache.tika.config.Field;
import org.apache.tika.pipes.fetcher.Fetcher;

public abstract class AbstractFetcher
implements Fetcher {
    private String name;

    public AbstractFetcher() {
    }

    public AbstractFetcher(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Field
    public void setName(String name) {
        this.name = name;
    }
}

