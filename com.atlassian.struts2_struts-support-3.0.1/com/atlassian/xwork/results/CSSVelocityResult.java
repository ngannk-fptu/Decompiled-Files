/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.struts2.views.velocity.result.VelocityResult
 */
package com.atlassian.xwork.results;

import org.apache.struts2.views.velocity.result.VelocityResult;

public class CSSVelocityResult
extends VelocityResult {
    protected String getContentType(String s) {
        return "text/css";
    }
}

