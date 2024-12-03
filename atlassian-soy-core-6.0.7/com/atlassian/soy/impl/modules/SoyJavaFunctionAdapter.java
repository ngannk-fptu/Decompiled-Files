/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.soy.renderer.SoyServerFunction
 *  com.google.common.base.Function
 *  com.google.common.collect.Lists
 *  com.google.template.soy.data.SoyValue
 *  com.google.template.soy.data.SoyValueConverter
 *  com.google.template.soy.data.SoyValueHelper
 *  com.google.template.soy.shared.restricted.SoyJavaFunction
 *  javax.inject.Inject
 */
package com.atlassian.soy.impl.modules;

import com.atlassian.soy.impl.data.SoyValueUtils;
import com.atlassian.soy.renderer.SoyServerFunction;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.SoyValueConverter;
import com.google.template.soy.data.SoyValueHelper;
import com.google.template.soy.shared.restricted.SoyJavaFunction;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;

class SoyJavaFunctionAdapter
implements SoyJavaFunction {
    private final SoyServerFunction<?> soyServerFunction;
    private SoyValueConverter converter;

    public SoyJavaFunctionAdapter(SoyServerFunction<?> soyServerFunction) {
        this.soyServerFunction = soyServerFunction;
    }

    public SoyValue computeForJava(List<SoyValue> args) {
        Object[] pluginArgs = Lists.transform(args, (Function)new Function<SoyValue, Object>(){

            public Object apply(SoyValue from) {
                return SoyValueUtils.fromSoyValue(from);
            }
        }).toArray();
        Object returnValue = this.soyServerFunction.apply(pluginArgs);
        return this.converter.convert(returnValue).resolve();
    }

    public String getName() {
        return this.soyServerFunction.getName();
    }

    public Set<Integer> getValidArgsSizes() {
        return this.soyServerFunction.validArgSizes();
    }

    @Inject
    void setConverter(SoyValueHelper converter) {
        this.converter = converter;
    }
}

