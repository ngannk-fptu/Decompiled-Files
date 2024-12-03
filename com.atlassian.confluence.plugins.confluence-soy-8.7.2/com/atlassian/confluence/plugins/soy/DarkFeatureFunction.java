/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.settings.DarkFeatures
 *  com.atlassian.soy.renderer.JsExpression
 *  com.atlassian.soy.renderer.SoyClientFunction
 *  com.atlassian.soy.renderer.SoyServerFunction
 */
package com.atlassian.confluence.plugins.soy;

import com.atlassian.confluence.setup.settings.DarkFeatures;
import com.atlassian.soy.renderer.JsExpression;
import com.atlassian.soy.renderer.SoyClientFunction;
import com.atlassian.soy.renderer.SoyServerFunction;
import java.util.Collections;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DarkFeatureFunction
implements SoyServerFunction<Boolean>,
SoyClientFunction {
    static final Pattern STRING_ARG = Pattern.compile("^'(.*)'$");
    private Set<Integer> argSizes = Collections.singleton(1);

    public String getName() {
        return "isDarkFeatureEnabled";
    }

    public Boolean apply(Object ... args) {
        return DarkFeatures.isDarkFeatureEnabled((String)((String)args[0]));
    }

    public Set<Integer> validArgSizes() {
        return this.argSizes;
    }

    public JsExpression generate(JsExpression ... args) {
        JsExpression keyExpr = args[0];
        Matcher m = STRING_ARG.matcher(keyExpr.getText());
        if (!m.matches()) {
            throw new IllegalArgumentException("Argument to isDarkFeatureEnabled() is not a literal string: " + keyExpr.getText());
        }
        String key = m.group(1);
        return new JsExpression(Boolean.toString(DarkFeatures.isDarkFeatureEnabled((String)key)) + " == true");
    }
}

