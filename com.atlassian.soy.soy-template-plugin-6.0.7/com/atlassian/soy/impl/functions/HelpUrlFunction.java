/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.tenancy.TenancyScope
 *  com.atlassian.annotations.tenancy.TenantAware
 *  com.atlassian.sal.api.message.HelpPathResolver
 *  com.atlassian.soy.renderer.JsExpression
 *  com.atlassian.soy.renderer.SoyClientFunction
 *  com.atlassian.soy.renderer.SoyServerFunction
 *  com.google.common.base.Preconditions
 *  org.apache.commons.lang3.StringEscapeUtils
 */
package com.atlassian.soy.impl.functions;

import com.atlassian.annotations.tenancy.TenancyScope;
import com.atlassian.annotations.tenancy.TenantAware;
import com.atlassian.sal.api.message.HelpPathResolver;
import com.atlassian.soy.renderer.JsExpression;
import com.atlassian.soy.renderer.SoyClientFunction;
import com.atlassian.soy.renderer.SoyServerFunction;
import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringEscapeUtils;

@TenantAware(value=TenancyScope.TENANTLESS)
public class HelpUrlFunction
implements SoyClientFunction,
SoyServerFunction<String> {
    private static final Set<Integer> ARGS_SIZES = Collections.singleton(1);
    private static final Pattern SOY_STRING_PATTERN = Pattern.compile("^'(.*)'$");
    private final HelpPathResolver helpPathResolver;

    public HelpUrlFunction(HelpPathResolver helpPathResolver) {
        this.helpPathResolver = (HelpPathResolver)Preconditions.checkNotNull((Object)helpPathResolver);
    }

    public String getName() {
        return "helpUrl";
    }

    public Set<Integer> validArgSizes() {
        return ARGS_SIZES;
    }

    public JsExpression generate(JsExpression ... args) {
        Matcher matcher = SOY_STRING_PATTERN.matcher(args[0].getText());
        Preconditions.checkArgument((boolean)matcher.matches(), (Object)"The help key name should be a string literal");
        String helpUrl = this.getHelpUrl(matcher.group(1));
        return new JsExpression('\"' + StringEscapeUtils.escapeEcmaScript((String)helpUrl) + '\"');
    }

    public String apply(Object ... args) {
        String helpKey = (String)args[0];
        return this.getHelpUrl(helpKey);
    }

    private String getHelpUrl(String helpKey) {
        return this.helpPathResolver.getHelpPath(helpKey).getUrl();
    }
}

