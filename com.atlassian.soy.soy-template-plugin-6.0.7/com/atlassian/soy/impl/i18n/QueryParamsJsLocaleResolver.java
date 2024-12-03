/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.soy.impl.functions.LocaleUtils
 *  com.atlassian.soy.renderer.QueryParamsResolver
 *  com.atlassian.soy.spi.i18n.JsLocaleResolver
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.soy.impl.i18n;

import com.atlassian.soy.impl.functions.LocaleUtils;
import com.atlassian.soy.renderer.QueryParamsResolver;
import com.atlassian.soy.spi.i18n.JsLocaleResolver;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;

public class QueryParamsJsLocaleResolver
implements JsLocaleResolver {
    public static final String QUERY_KEY = "locale";
    private final QueryParamsResolver queryParamsResolver;

    public QueryParamsJsLocaleResolver(QueryParamsResolver queryParamsResolver) {
        this.queryParamsResolver = queryParamsResolver;
    }

    public Locale getLocale() {
        String localeKey = this.queryParamsResolver.get().get(QUERY_KEY);
        if (!StringUtils.isBlank((CharSequence)localeKey)) {
            return LocaleUtils.deserialize((String)localeKey);
        }
        return Locale.US;
    }
}

