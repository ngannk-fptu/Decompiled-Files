/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core.actionresult;

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.actionresult.ActionResultCollection;

public interface ActionResultService {
    public Option<ActionResultCollection> getAndClearResults(String var1);

    public String storeResults(ActionResultCollection var1);
}

