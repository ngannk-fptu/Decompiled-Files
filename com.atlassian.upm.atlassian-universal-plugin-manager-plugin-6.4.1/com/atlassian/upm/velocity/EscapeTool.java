/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.upm.velocity;

import javax.annotation.Nullable;
import org.apache.commons.text.StringEscapeUtils;

public class EscapeTool {
    @Nullable
    public String javascript(@Nullable Object object) {
        return object == null ? null : StringEscapeUtils.escapeEcmaScript(object.toString());
    }
}

