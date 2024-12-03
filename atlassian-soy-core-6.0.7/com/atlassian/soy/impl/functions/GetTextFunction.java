/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.inject.Singleton
 *  com.google.template.soy.base.SoySyntaxException
 *  com.google.template.soy.data.SoyData
 *  com.google.template.soy.data.SoyValue
 *  com.google.template.soy.data.restricted.NullData
 *  com.google.template.soy.data.restricted.StringData
 *  com.google.template.soy.internal.base.CharEscaper
 *  com.google.template.soy.internal.base.CharEscapers
 *  com.google.template.soy.jssrc.restricted.JsExpr
 *  com.google.template.soy.jssrc.restricted.SoyJsSrcFunction
 *  com.google.template.soy.shared.restricted.SoyJavaFunction
 *  javax.inject.Inject
 *  javax.inject.Named
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.soy.impl.functions;

import com.atlassian.soy.impl.data.SoyValueUtils;
import com.atlassian.soy.spi.i18n.I18nResolver;
import com.atlassian.soy.spi.i18n.JsLocaleResolver;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Singleton;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.data.SoyData;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.restricted.NullData;
import com.google.template.soy.data.restricted.StringData;
import com.google.template.soy.internal.base.CharEscaper;
import com.google.template.soy.internal.base.CharEscapers;
import com.google.template.soy.jssrc.restricted.JsExpr;
import com.google.template.soy.jssrc.restricted.SoyJsSrcFunction;
import com.google.template.soy.shared.restricted.SoyJavaFunction;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class GetTextFunction
implements SoyJsSrcFunction,
SoyJavaFunction {
    public static final String FUNCTION_NAME = "getText";
    private static final Logger log = LoggerFactory.getLogger(GetTextFunction.class);
    private static final Pattern STRING_ARG = Pattern.compile("^'(.*)'$");
    private static final Function<SoyValue, Serializable> SOY_DATA_TO_SERIALIZABLE_FUNCTION = new Function<SoyValue, Serializable>(){

        public Serializable apply(SoyValue fromSoyData) {
            Object convertedSoyData = SoyValueUtils.fromSoyValue(fromSoyData);
            if (convertedSoyData instanceof Serializable) {
                return (Serializable)convertedSoyData;
            }
            if (convertedSoyData == null) {
                return NullData.INSTANCE.toString();
            }
            if (log.isDebugEnabled()) {
                log.debug("Conversion of {} from {} is not a Serializable, defaulting to toString() invocation.", (Object)convertedSoyData.getClass().getName(), (Object)fromSoyData.getClass().getName());
            }
            return convertedSoyData.toString();
        }
    };
    private static final Set<Integer> ARGS_SIZES;
    private final JsLocaleResolver jsLocaleResolver;
    private final I18nResolver i18nResolver;
    private boolean useAjsI18n;

    @Inject
    public GetTextFunction(JsLocaleResolver jsLocaleResolver, I18nResolver i18nResolver, @Named(value="atlassian.soy.functions.text.use.ajs") boolean useAjsI18n) {
        this.jsLocaleResolver = jsLocaleResolver;
        this.i18nResolver = i18nResolver;
        this.useAjsI18n = useAjsI18n;
    }

    public String getName() {
        return FUNCTION_NAME;
    }

    public Set<Integer> getValidArgsSizes() {
        return ARGS_SIZES;
    }

    public JsExpr computeForJsSrc(List<JsExpr> args) {
        JsExpr keyExpr = args.get(0);
        Matcher m = STRING_ARG.matcher(keyExpr.getText());
        if (!m.matches()) {
            throw SoySyntaxException.createWithoutMetaInfo((String)("Argument to getText() is not a literal string: " + keyExpr.getText()));
        }
        String key = m.group(1);
        List<Object> i18nArgs = Lists.newArrayList();
        if (args.size() > 1) {
            i18nArgs = args.subList(1, args.size()).stream().map(JsExpr::getText).collect(Collectors.toList());
        }
        String output = this.useAjsI18n ? this.getAjsI18nJsOutput(key, i18nArgs) : this.getJsOutput(key, i18nArgs);
        return new JsExpr(output, Integer.MAX_VALUE);
    }

    public SoyData computeForJava(List<SoyValue> args) {
        SoyValue data = args.get(0);
        if (!(data instanceof StringData)) {
            throw SoySyntaxException.createWithoutMetaInfo((String)"Argument to getText() is not a literal string");
        }
        List<SoyValue> params = args.subList(1, args.size());
        StringData stringData = (StringData)data;
        String text = this.i18nResolver.getText(stringData.getValue(), this.transformSoyDataListToSerializableArray(params));
        return StringData.forValue((String)text);
    }

    private String getJsOutput(String key, List<String> args) {
        CharEscaper jsEscaper = CharEscapers.javascriptEscaper();
        if (args.isEmpty()) {
            return "'" + jsEscaper.escape(this.i18nResolver.getText(this.jsLocaleResolver.getLocale(), key)) + "'";
        }
        StringBuilder call = new StringBuilder();
        String msg = this.i18nResolver.getRawText(this.jsLocaleResolver.getLocale(), key);
        call.append("AJS.format(");
        call.append("'").append(jsEscaper.escape(msg)).append("'");
        args.forEach(arg -> call.append(",").append((String)arg));
        return call.append(")").toString();
    }

    private String getAjsI18nJsOutput(String key, List<String> args) {
        StringBuilder call = new StringBuilder().append(String.format("AJS.I18n.getText('%s'", key));
        args.forEach(arg -> call.append(",").append((String)arg));
        return call.append(")").toString();
    }

    private Serializable[] transformSoyDataListToSerializableArray(List<SoyValue> params) {
        return (Serializable[])Iterables.toArray((Iterable)Iterables.transform(params, SOY_DATA_TO_SERIALIZABLE_FUNCTION), Serializable.class);
    }

    static {
        ImmutableSet.Builder args = ImmutableSet.builder();
        for (int i = 1; i < 20; ++i) {
            args.add((Object)i);
        }
        ARGS_SIZES = args.build();
    }
}

