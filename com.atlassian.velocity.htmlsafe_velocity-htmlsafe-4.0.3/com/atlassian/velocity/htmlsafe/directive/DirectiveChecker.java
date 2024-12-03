/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.Template
 */
package com.atlassian.velocity.htmlsafe.directive;

import org.apache.velocity.Template;

public interface DirectiveChecker {
    public boolean isPresent(String var1, Template var2);
}

