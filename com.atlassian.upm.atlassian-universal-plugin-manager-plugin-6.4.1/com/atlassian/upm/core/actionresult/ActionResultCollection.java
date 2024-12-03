/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core.actionresult;

import com.atlassian.upm.core.actionresult.ActionResult;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ActionResultCollection
implements Serializable {
    private final List<ActionResult> sortedResults;

    public ActionResultCollection(List<ActionResult> results) {
        this.sortedResults = results.stream().sorted(Comparator.comparingInt(res -> res.getType().getOrdering())).collect(Collectors.toList());
    }

    public List<ActionResult> get() {
        return this.sortedResults;
    }
}

