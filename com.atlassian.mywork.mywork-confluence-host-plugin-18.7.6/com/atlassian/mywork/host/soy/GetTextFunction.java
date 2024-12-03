/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.mywork.host.soy;

import com.atlassian.mywork.host.soy.SoyUtils;
import com.atlassian.sal.api.message.LocaleResolver;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Singleton;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.data.SoyData;
import com.google.template.soy.data.restricted.StringData;
import com.google.template.soy.tofu.restricted.SoyTofuFunction;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class GetTextFunction
implements SoyTofuFunction {
    private static final Logger LOG = LoggerFactory.getLogger(GetTextFunction.class);
    private static final ResourceBundle.Control CONTROL = new ResourceBundle.Control(){};
    private static final Set<Integer> ARGS_SIZES;
    private final LocaleResolver localeResolver;
    private final Map<String, Map<String, String>> i18n;

    @Inject
    public GetTextFunction(LocaleResolver localeResolver, Map<String, Map<String, String>> i18n) {
        this.localeResolver = localeResolver;
        this.i18n = i18n;
    }

    @Override
    public String getName() {
        return "getText";
    }

    @Override
    public Set<Integer> getValidArgsSizes() {
        return ARGS_SIZES;
    }

    @Override
    public SoyData computeForTofu(List<SoyData> args) {
        String text;
        SoyData data = args.get(0);
        if (!(data instanceof StringData)) {
            throw new SoySyntaxException("Argument to getText() is not a literal string");
        }
        List<SoyData> params = args.subList(1, args.size());
        StringData stringData = (StringData)data;
        String pattern = this.getValues(this.localeResolver.getLocale()).get(stringData.getValue());
        if (pattern != null) {
            text = MessageFormat.format(pattern, SoyUtils.toSerializableArray(params));
        } else {
            LOG.debug("Could not find key {}", (Object)stringData);
            text = "";
        }
        return StringData.forValue(text);
    }

    private Map<String, String> getValues(Locale locale) {
        HashMap<String, String> map = new HashMap<String, String>();
        List<Locale> locales = CONTROL.getCandidateLocales("", locale);
        Collections.reverse(locales);
        for (Locale childLocale : locales) {
            Map<String, String> t = this.i18n.get(childLocale.toString());
            if (t == null) continue;
            map.putAll(t);
        }
        return map;
    }

    static {
        ImmutableSet.Builder args = ImmutableSet.builder();
        for (int i = 1; i < 20; ++i) {
            args.add((Object)i);
        }
        ARGS_SIZES = args.build();
    }
}

