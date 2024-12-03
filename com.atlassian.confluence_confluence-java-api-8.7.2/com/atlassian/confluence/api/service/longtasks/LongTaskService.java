/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Option
 */
package com.atlassian.confluence.api.service.longtasks;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.longtasks.LongTaskId;
import com.atlassian.confluence.api.model.longtasks.LongTaskStatus;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.util.FugueConversionUtil;
import com.atlassian.fugue.Option;
import java.util.Optional;

public interface LongTaskService {
    @Deprecated
    default public Option<LongTaskStatus> get(LongTaskId id, Expansion ... expansions) {
        return FugueConversionUtil.toComOption(this.getStatus(id, expansions));
    }

    public Optional<LongTaskStatus> getStatus(LongTaskId var1, Expansion ... var2);

    public PageResponse<LongTaskStatus> getAll(PageRequest var1, Expansion ... var2);
}

