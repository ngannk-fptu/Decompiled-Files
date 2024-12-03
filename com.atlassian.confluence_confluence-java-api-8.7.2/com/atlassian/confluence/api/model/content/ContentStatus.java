/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.codehaus.jackson.annotate.JsonCreator
 */
package com.atlassian.confluence.api.model.content;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.BaseApiEnum;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.codehaus.jackson.annotate.JsonCreator;

@ExperimentalApi
public final class ContentStatus
extends BaseApiEnum {
    public static final ContentStatus CURRENT = new ContentStatus("current");
    public static final ContentStatus TRASHED = new ContentStatus("trashed");
    public static final ContentStatus HISTORICAL = new ContentStatus("historical");
    public static final ContentStatus DRAFT = new ContentStatus("draft");
    public static final List<ContentStatus> BUILT_IN = Collections.unmodifiableList(Arrays.asList(CURRENT, TRASHED, HISTORICAL, DRAFT));

    @JsonCreator
    public static ContentStatus valueOf(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        for (ContentStatus status : BUILT_IN) {
            if (!status.getValue().equals(str)) continue;
            return status;
        }
        return new ContentStatus(str);
    }

    private ContentStatus(String status) {
        super(status);
    }
}

