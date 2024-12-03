/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.soy.renderer.JsExpression
 *  com.atlassian.soy.renderer.SoyClientFunction
 *  com.google.common.base.Function
 *  com.google.common.collect.Lists
 *  com.google.template.soy.jssrc.restricted.JsExpr
 *  com.google.template.soy.jssrc.restricted.SoyJsSrcFunction
 */
package com.atlassian.soy.impl.modules;

import com.atlassian.soy.renderer.JsExpression;
import com.atlassian.soy.renderer.SoyClientFunction;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.template.soy.jssrc.restricted.JsExpr;
import com.google.template.soy.jssrc.restricted.SoyJsSrcFunction;
import java.util.List;
import java.util.Set;

class SoyJsSrcFunctionAdapter
implements SoyJsSrcFunction {
    private final SoyClientFunction function;

    public SoyJsSrcFunctionAdapter(SoyClientFunction function) {
        this.function = function;
    }

    public JsExpr computeForJsSrc(List<JsExpr> args) {
        return new JsExpr(this.function.generate(Lists.transform(args, (Function)new Function<JsExpr, JsExpression>(){

            public JsExpression apply(JsExpr from) {
                return new JsExpression(from.getText());
            }
        }).toArray(new JsExpression[args.size()])).getText(), Integer.MAX_VALUE);
    }

    public String getName() {
        return this.function.getName();
    }

    public Set<Integer> getValidArgsSizes() {
        return this.function.validArgSizes();
    }
}

