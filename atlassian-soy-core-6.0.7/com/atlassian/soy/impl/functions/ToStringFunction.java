/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.tenancy.TenancyScope
 *  com.atlassian.annotations.tenancy.TenantAware
 *  com.atlassian.soy.renderer.JsExpression
 *  com.atlassian.soy.renderer.SoyClientFunction
 *  com.atlassian.soy.renderer.SoyServerFunction
 */
package com.atlassian.soy.impl.functions;

import com.atlassian.annotations.tenancy.TenancyScope;
import com.atlassian.annotations.tenancy.TenantAware;
import com.atlassian.soy.renderer.JsExpression;
import com.atlassian.soy.renderer.SoyClientFunction;
import com.atlassian.soy.renderer.SoyServerFunction;
import java.util.Collections;
import java.util.Set;

@TenantAware(value=TenancyScope.TENANTLESS)
public class ToStringFunction
implements SoyServerFunction<String>,
SoyClientFunction {
    private static final Set<Integer> VALID_ARG_SIZES = Collections.singleton(1);

    public String getName() {
        return "toString";
    }

    public Set<Integer> validArgSizes() {
        return VALID_ARG_SIZES;
    }

    public JsExpression generate(JsExpression ... args) {
        return new JsExpression("'' + " + args[0].getText());
    }

    public String apply(Object ... args) {
        return String.valueOf(args[0]);
    }
}

