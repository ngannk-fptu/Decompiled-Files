/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.xwork.results;

import com.atlassian.xwork.results.ProfiledVelocityResult;

public class XmlVelocityResult
extends ProfiledVelocityResult {
    protected String getContentType(String s) {
        return "text/xml";
    }
}

