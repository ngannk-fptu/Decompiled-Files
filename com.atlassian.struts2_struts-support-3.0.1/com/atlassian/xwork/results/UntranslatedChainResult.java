/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork2.ActionChainResult
 */
package com.atlassian.xwork.results;

import com.opensymphony.xwork2.ActionChainResult;

public class UntranslatedChainResult
extends ActionChainResult {
    protected String translateVariables(String text) {
        return text;
    }
}

