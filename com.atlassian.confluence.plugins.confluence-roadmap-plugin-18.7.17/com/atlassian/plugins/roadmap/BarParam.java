/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.roadmap;

import com.atlassian.plugins.roadmap.NumberUtil;
import java.util.Map;

public class BarParam {
    Long contentId;
    String roadmapHash;
    int version = -1;
    String barId;
    Boolean updateRoadmap;

    public static BarParam fromMap(Map<String, ?> context) {
        BarParam barParam = new BarParam();
        if (context.get("roadmapContentId") != null) {
            barParam.contentId = NumberUtil.parseLongString(context.get("roadmapContentId").toString());
        }
        if (context.get("version") != null) {
            barParam.version = Integer.valueOf(context.get("version").toString());
        }
        barParam.roadmapHash = String.valueOf(context.get("roadmapHash"));
        barParam.barId = context.get("roadmapBarId").toString();
        barParam.updateRoadmap = Boolean.parseBoolean(String.valueOf(context.get("updateRoadmap")));
        return barParam;
    }
}

