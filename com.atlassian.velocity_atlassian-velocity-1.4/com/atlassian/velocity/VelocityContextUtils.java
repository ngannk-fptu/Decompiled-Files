/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.util.TextUtils
 */
package com.atlassian.velocity;

import com.atlassian.velocity.VelocityHelper;
import com.opensymphony.util.TextUtils;
import java.util.Map;

public class VelocityContextUtils {
    private static final TextUtils TEXT_UTILS = new TextUtils();
    private static final VelocityHelper VELOCITY_HELPER = new VelocityHelper();

    public static Map<String, Object> getContextParamsBody(Map<String, Object> contextParams) {
        contextParams.put("velocityhelper", VELOCITY_HELPER);
        contextParams.put("textutils", TEXT_UTILS);
        return contextParams;
    }
}

