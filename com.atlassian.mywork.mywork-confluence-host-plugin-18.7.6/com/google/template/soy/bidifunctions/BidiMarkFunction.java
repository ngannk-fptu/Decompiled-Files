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
import com.google.template.soy.data.restricted.StringData;
import com.google.template.soy.exprtree.Operator;
import com.google.template.soy.internal.i18n.BidiGlobalDir;
import com.google.template.soy.jssrc.restricted.JsExpr;
import com.google.template.soy.jssrc.restricted.SoyJsSrcFunction;
import com.google.template.soy.shared.restricted.SoyJavaFunction;
import java.util.List;
import java.util.Set;

@Singleton
class BidiMarkFunction
implements SoyJavaFunction,
SoyJsSrcFunction {
    private final Provider<BidiGlobalDir> bidiGlobalDirProvider;

    @Inject
    BidiMarkFunction(Provider<BidiGlobalDir> bidiGlobalDirProvider) {
        this.bidiGlobalDirProvider = bidiGlobalDirProvider;
    }

    @Override
    public String getName() {
        return "bidiMark";
    }

    @Override
    public Set<Integer> getValidArgsSizes() {
        return ImmutableSet.of((Object)0);
    }

    @Override
    public SoyValue computeForJava(List<SoyValue> args) {
        return StringData.forValue(this.bidiGlobalDirProvider.get().getStaticValue() < 0 ? "\u200f" : "\u200e");
    }

    @Override
    public JsExpr computeForJsSrc(List<JsExpr> args) {
        BidiGlobalDir bidiGlobalDir = this.bidiGlobalDirProvider.get();
        if (bidiGlobalDir.isStaticValue()) {
            return new JsExpr(bidiGlobalDir.getStaticValue() < 0 ? "'\\u200F'" : "'\\u200E'", Integer.MAX_VALUE);
        }
        return new JsExpr("(" + bidiGlobalDir.getCodeSnippet() + ") < 0 ? '\\u200F' : '\\u200E'", Operator.CONDITIONAL.getPrecedence());
    }
}

