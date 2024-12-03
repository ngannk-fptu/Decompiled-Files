/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.content.render.xhtml.migration;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;

public class BatchException
extends Exception {
    private static final long serialVersionUID = 1L;
    private final List<Exception> causes = new ArrayList<Exception>();

    public BatchException(List<? extends Exception> exs) {
        this.causes.addAll(exs);
    }

    public List<Exception> getBatchExceptions() {
        return ImmutableList.copyOf(this.causes);
    }
}

