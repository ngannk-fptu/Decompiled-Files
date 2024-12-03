/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.marketplace.client.api;

import com.atlassian.marketplace.client.MpacException;
import com.atlassian.marketplace.client.api.Page;
import com.atlassian.marketplace.client.api.PageReference;
import java.io.InputStream;

public abstract class PageReader<T> {
    public abstract Page<T> readPage(PageReference<T> var1, InputStream var2) throws MpacException;

    public static <T> PageReader<T> stub() {
        return new PageReader<T>(){

            @Override
            public Page<T> readPage(PageReference<T> ref, InputStream in) {
                return Page.empty();
            }
        };
    }
}

