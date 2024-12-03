/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.template.soy.data.SoyData
 *  com.google.template.soy.data.SoyValue
 *  com.google.template.soy.data.restricted.StringData
 *  com.google.template.soy.internal.base.CharEscapers
 *  com.google.template.soy.jssrc.restricted.JsExpr
 *  com.google.template.soy.jssrc.restricted.SoyJsSrcFunction
 *  com.google.template.soy.shared.restricted.SoyJavaFunction
 *  javax.inject.Inject
 *  javax.inject.Named
 *  javax.inject.Singleton
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.soy.impl.functions;

import com.atlassian.soy.spi.web.WebContextProvider;
import com.google.common.collect.ImmutableSet;
import com.google.template.soy.data.SoyData;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.restricted.StringData;
import com.google.template.soy.internal.base.CharEscapers;
import com.google.template.soy.jssrc.restricted.JsExpr;
import com.google.template.soy.jssrc.restricted.SoyJsSrcFunction;
import com.google.template.soy.shared.restricted.SoyJavaFunction;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.commons.lang3.StringUtils;

@Singleton
public class ContextFunction
implements SoyJsSrcFunction,
SoyJavaFunction {
    public static final String FUNCTION_NAME = "contextPath";
    private final WebContextProvider webContextProvider;
    private final boolean useAjsContextPath;

    @Inject
    public ContextFunction(WebContextProvider webContextProvider, @Named(value="atlassian.soy.functions.context.use.ajs") boolean useAjsContextPath) {
        this.webContextProvider = webContextProvider;
        this.useAjsContextPath = useAjsContextPath;
    }

    private static String stripTrailingSlash(String base) {
        return StringUtils.chomp((String)base, (String)"/");
    }

    public String getName() {
        return FUNCTION_NAME;
    }

    public Set<Integer> getValidArgsSizes() {
        return ImmutableSet.of((Object)0);
    }

    public JsExpr computeForJsSrc(List<JsExpr> args) {
        String expr = this.useAjsContextPath ? "AJS.contextPath()" : '\"' + CharEscapers.javascriptEscaper().escape(this.getContextPath()) + '\"';
        return new JsExpr(expr, Integer.MAX_VALUE);
    }

    public SoyData computeForJava(List<SoyValue> soyDatas) {
        return StringData.forValue((String)this.getContextPath());
    }

    private String getContextPath() {
        return ContextFunction.stripTrailingSlash(this.webContextProvider.getContextPath());
    }
}

