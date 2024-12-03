/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.soy.renderer.SoyClientFunction
 *  com.atlassian.soy.renderer.SoyServerFunction
 *  com.google.common.base.Objects
 *  com.google.common.base.Preconditions
 *  com.google.template.soy.data.SoyValue
 *  com.google.template.soy.data.SoyValueHelper
 *  com.google.template.soy.jssrc.restricted.JsExpr
 *  com.google.template.soy.jssrc.restricted.SoyJsSrcFunction
 *  com.google.template.soy.shared.restricted.SoyJavaFunction
 *  javax.inject.Inject
 */
package com.atlassian.soy.impl.modules;

import com.atlassian.soy.impl.modules.SoyJavaFunctionAdapter;
import com.atlassian.soy.impl.modules.SoyJsSrcFunctionAdapter;
import com.atlassian.soy.renderer.SoyClientFunction;
import com.atlassian.soy.renderer.SoyServerFunction;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.SoyValueHelper;
import com.google.template.soy.jssrc.restricted.JsExpr;
import com.google.template.soy.jssrc.restricted.SoyJsSrcFunction;
import com.google.template.soy.shared.restricted.SoyJavaFunction;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;

class CompositeFunctionAdaptor
implements SoyJsSrcFunction,
SoyJavaFunction {
    private final SoyJavaFunctionAdapter serverAdaptor;
    private final SoyJsSrcFunctionAdapter clientAdaptor;

    public CompositeFunctionAdaptor(SoyServerFunction<?> serverFunction, SoyClientFunction clientFunction) {
        Preconditions.checkArgument((serverFunction == clientFunction || Objects.equal((Object)serverFunction.validArgSizes(), (Object)clientFunction.validArgSizes()) && Objects.equal((Object)serverFunction.getName(), (Object)clientFunction.getName()) ? 1 : 0) != 0, (Object)"the supplied soy functions are not compatible with each other");
        this.serverAdaptor = new SoyJavaFunctionAdapter(serverFunction);
        this.clientAdaptor = new SoyJsSrcFunctionAdapter(clientFunction);
    }

    public JsExpr computeForJsSrc(List<JsExpr> args) {
        return this.clientAdaptor.computeForJsSrc(args);
    }

    public SoyValue computeForJava(List<SoyValue> args) {
        return this.serverAdaptor.computeForJava(args);
    }

    public String getName() {
        return this.serverAdaptor.getName();
    }

    public Set<Integer> getValidArgsSizes() {
        return this.serverAdaptor.getValidArgsSizes();
    }

    @Inject
    void setConverter(SoyValueHelper converter) {
        this.serverAdaptor.setConverter(converter);
    }
}

