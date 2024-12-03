/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.Template
 */
package com.atlassian.velocity.htmlsafe.directive;

import com.atlassian.velocity.htmlsafe.directive.DirectiveChecker;
import com.atlassian.velocity.htmlsafe.directive.Directives;
import org.apache.velocity.Template;

public class DefaultDirectiveChecker
implements DirectiveChecker {
    @Override
    public boolean isPresent(String directiveName, Template template) {
        return Directives.isPresent(directiveName, template);
    }
}

