/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package com.google.template.soy.bidifunctions;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.restricted.IntegerData;
import com.google.template.soy.exprtree.Operator;
import com.google.template.soy.internal.i18n.BidiGlobalDir;
import com.google.template.soy.jssrc.restricted.JsExpr;
import com.google.template.soy.jssrc.restricted.SoyJsSrcFunction;
import com.google.template.soy.shared.restricted.SoyJavaFunction;
import java.util.List;
import java.util.Set;

@Singleton
class BidiGlobalDirFunction
implements SoyJavaFunction,
SoyJsSrcFunction {
    private final Provider<BidiGlobalDir> bidiGlobalDirProvider;

    @Inject
    BidiGlobalDirFunction(Provider<BidiGlobalDir> bidiGlobalDirProvider) {
        this.bidiGlobalDirProvider = bidiGlobalDirProvider;
    }

    @Override
    public String getName() {
        return "bidiGlobalDir";
    }

    @Override
    public Set<Integer> getValidArgsSizes() {
        return ImmutableSet.of((Object)0);
    }

    @Override
    public SoyValue computeForJava(List<SoyValue> args) {
        return IntegerData.forValue(this.bidiGlobalDirProvider.get().getStaticValue());
    }

    @Override
    public JsExpr computeForJsSrc(List<JsExpr> args) {
        BidiGlobalDir bidiGlobalDir = this.bidiGlobalDirProvider.get();
        return new JsExpr(this.bidiGlobalDirProvider.get().getCodeSnippet(), bidiGlobalDir.isStaticValue() ? Integer.MAX_VALUE : Operator.CONDITIONAL.getPrecedence());
    }
}

