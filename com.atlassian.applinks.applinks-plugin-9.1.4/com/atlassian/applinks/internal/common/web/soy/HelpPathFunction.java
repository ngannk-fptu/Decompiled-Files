/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.soy.renderer.JsExpression
 *  com.atlassian.soy.renderer.SoyClientFunction
 *  com.atlassian.soy.renderer.SoyServerFunction
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.applinks.internal.common.web.soy;

import com.atlassian.applinks.internal.common.docs.DocumentationLinker;
import com.atlassian.soy.renderer.JsExpression;
import com.atlassian.soy.renderer.SoyClientFunction;
import com.atlassian.soy.renderer.SoyServerFunction;
import com.google.common.collect.ImmutableSet;
import java.util.Objects;
import java.util.Set;

public class HelpPathFunction
implements SoyServerFunction<String>,
SoyClientFunction {
    private final DocumentationLinker documentationLinker;

    public HelpPathFunction(DocumentationLinker documentationLinker) {
        this.documentationLinker = documentationLinker;
    }

    public JsExpression generate(JsExpression ... args) {
        this.checkArguments(args.length);
        String arg1 = args[0].getText();
        String arg2 = args.length == 2 ? args[1].getText() : "undefined";
        return new JsExpression("require('applinks/common/help-paths').getFullPath(" + arg1 + "," + arg2 + ")");
    }

    public String getName() {
        return "getHelpUrl";
    }

    public String apply(Object ... args) {
        this.checkArguments(args.length);
        Objects.requireNonNull(args[0], "page key");
        if (args.length == 1) {
            return this.documentationLinker.getLink(args[0].toString()).toString();
        }
        Objects.requireNonNull(args[1], "section key");
        return this.documentationLinker.getLink(args[0].toString(), args[1].toString()).toString();
    }

    private void checkArguments(int actual) {
        if (actual < 1 || actual > 2) {
            throw new IllegalArgumentException("Wrong number of arguments: expected <1 or 2>, was: " + actual);
        }
    }

    public Set<Integer> validArgSizes() {
        return ImmutableSet.of((Object)1, (Object)2);
    }
}

