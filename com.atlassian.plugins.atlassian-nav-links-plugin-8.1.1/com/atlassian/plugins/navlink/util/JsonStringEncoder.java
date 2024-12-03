/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.commons.text.StringEscapeUtils
 */
package com.atlassian.plugins.navlink.util;

import javax.annotation.Nullable;
import org.apache.commons.text.StringEscapeUtils;

public class JsonStringEncoder {
    @Nullable
    public String asHtml(@Nullable String str) {
        return StringEscapeUtils.escapeJava((String)str);
    }
}

