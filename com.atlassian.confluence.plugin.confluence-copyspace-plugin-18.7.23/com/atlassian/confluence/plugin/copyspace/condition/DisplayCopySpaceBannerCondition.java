/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.Spaced
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.Condition
 */
package com.atlassian.confluence.plugin.copyspace.condition;

import com.atlassian.confluence.plugin.copyspace.service.CopySpaceProgressBarCacheService;
import com.atlassian.confluence.spaces.Spaced;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import java.util.Map;

public class DisplayCopySpaceBannerCondition
implements Condition {
    private final CopySpaceProgressBarCacheService copySpaceProgressBarCacheService;

    public DisplayCopySpaceBannerCondition(CopySpaceProgressBarCacheService copySpaceProgressBarCacheService) {
        this.copySpaceProgressBarCacheService = copySpaceProgressBarCacheService;
    }

    public void init(Map<String, String> params) throws PluginParseException {
    }

    public boolean shouldDisplay(Map<String, Object> context) {
        Object action = context.get("action");
        if (action instanceof Spaced) {
            Spaced spaced = (Spaced)action;
            return this.copySpaceProgressBarCacheService.isCopySpaceInProgress(spaced.getSpace().getKey());
        }
        return false;
    }
}

