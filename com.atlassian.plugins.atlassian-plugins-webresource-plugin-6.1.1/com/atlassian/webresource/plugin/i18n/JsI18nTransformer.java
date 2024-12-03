/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.html.encode.JavascriptEncoder
 *  com.atlassian.plugin.elements.ResourceLocation
 *  com.atlassian.plugin.servlet.DownloadException
 *  com.atlassian.plugin.servlet.DownloadableResource
 *  com.atlassian.plugin.webresource.Flags
 *  com.atlassian.plugin.webresource.QueryParams
 *  com.atlassian.plugin.webresource.WebResourceIntegration
 *  com.atlassian.plugin.webresource.transformer.AbstractTransformedDownloadableResource
 *  com.atlassian.plugin.webresource.transformer.CharSequenceDownloadableResource
 *  com.atlassian.plugin.webresource.transformer.TransformableResource
 *  com.atlassian.plugin.webresource.transformer.TransformerParameters
 *  com.atlassian.plugin.webresource.transformer.TwoPhaseResourceTransformer
 *  com.atlassian.plugin.webresource.transformer.UrlReadingWebResourceTransformer
 *  com.atlassian.plugin.webresource.url.UrlBuilder
 *  com.atlassian.webresource.api.prebake.Coordinate
 *  com.atlassian.webresource.api.prebake.DimensionAwareTransformerUrlBuilder
 *  com.atlassian.webresource.api.prebake.DimensionAwareWebResourceTransformerFactory
 *  com.atlassian.webresource.api.prebake.Dimensions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.webresource.plugin.i18n;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.html.encode.JavascriptEncoder;
import com.atlassian.plugin.elements.ResourceLocation;
import com.atlassian.plugin.servlet.DownloadException;
import com.atlassian.plugin.servlet.DownloadableResource;
import com.atlassian.plugin.webresource.Flags;
import com.atlassian.plugin.webresource.QueryParams;
import com.atlassian.plugin.webresource.WebResourceIntegration;
import com.atlassian.plugin.webresource.transformer.AbstractTransformedDownloadableResource;
import com.atlassian.plugin.webresource.transformer.CharSequenceDownloadableResource;
import com.atlassian.plugin.webresource.transformer.TransformableResource;
import com.atlassian.plugin.webresource.transformer.TransformerParameters;
import com.atlassian.plugin.webresource.transformer.TwoPhaseResourceTransformer;
import com.atlassian.plugin.webresource.transformer.UrlReadingWebResourceTransformer;
import com.atlassian.plugin.webresource.url.UrlBuilder;
import com.atlassian.webresource.api.prebake.Coordinate;
import com.atlassian.webresource.api.prebake.DimensionAwareTransformerUrlBuilder;
import com.atlassian.webresource.api.prebake.DimensionAwareWebResourceTransformerFactory;
import com.atlassian.webresource.api.prebake.Dimensions;
import com.atlassian.webresource.plugin.i18n.LocaleUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsI18nTransformer
implements DimensionAwareWebResourceTransformerFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsI18nTransformer.class);
    @VisibleForTesting
    static final String TWO_PHASE_JS_I18N_DISABLED = "atlassian.webresource.twophase.js.i18n.disabled";
    private static final Pattern I18N_GET_TEXT_PATTERN = Pattern.compile("(?:\\.I18n|\\['I18n']|\\[\"I18n\"])\\.getText\\(\\s*(['\"])([\\w.-]+)\\1\\s*([),])");
    private static final int LONGEST_ACCEPTED_IDENTIFIER = 120;
    private static final Pattern SINGLE_IDENTIFIER_PATTERN = Pattern.compile("((?:\\p{Alpha}|[$_])(?:\\p{Alnum}|[$_]){0,100}(?:\\[['\"]default['\"]])?(?:\\.default)?)$");
    private static final int LONGEST_PROP_ACCESSOR = 20;
    private static final Pattern LONG_NAMESPACE_PROP_ACCESSOR_PATTERN = Pattern.compile("(([.\\]]\\p{Space}{0,19})|([])$_\\p{Alnum}]\\p{Space}{0,9}\\n\\p{Space}{0,9}))$");
    private static final String I18N_CONSOLE_WARN = "console.warn('The I18n web-resource is missing, please add com.atlassian.plugins.atlassian-plugins-webresource-plugin:i18n as a dependency to the web-resource. Learn more: https://developer.atlassian.com/server/framework/atlassian-sdk/internationalising-your-plugin-javascript');";
    private static final String FUNCTION_ARGS_SEPARATOR = ",";
    private static final String HASH_KEY = "locale-hash";
    private static final String QUERY_KEY = "locale";
    private final WebResourceIntegration webResourceIntegration;

    public JsI18nTransformer(WebResourceIntegration webResourceIntegration) {
        this.webResourceIntegration = webResourceIntegration;
    }

    private boolean isTwoPhaseJsI18nDisabled() {
        if (this.webResourceIntegration.getDarkFeatureManager() == null) {
            return false;
        }
        return this.webResourceIntegration.getDarkFeatureManager().isEnabledForAllUsers(TWO_PHASE_JS_I18N_DISABLED).orElse(false);
    }

    private static Locale getLocaleFromQueryParams(QueryParams params) {
        String localeKey = params.get(QUERY_KEY);
        if (localeKey == null || localeKey.trim().isEmpty()) {
            return Locale.US;
        }
        return LocaleUtils.deserialize(localeKey);
    }

    private static String jsEncode(String stringToEncode) {
        try {
            StringWriter writer = new StringWriter();
            JavascriptEncoder.escape((Writer)writer, (String)stringToEncode);
            return writer.toString();
        }
        catch (IOException e) {
            LOGGER.error("Error during javascript encoding", (Throwable)e);
            return "";
        }
    }

    private static void limitMatcherUptoStartOfAPreviousMatch(Matcher previousMatch, Matcher matcherToLimit, int matchLimit) {
        matcherToLimit.region(Math.max(0, previousMatch.toMatchResult().start() - matchLimit), previousMatch.toMatchResult().start());
    }

    private static void removeI18nGetTextCall(Matcher i18nMatcher, StringBuffer outputToReturn) {
        i18nMatcher.appendReplacement(outputToReturn, "");
    }

    private static void replaceNameSpaceAndI18nGetTextCallWithJustTheFormattedTranslationString(Matcher i18nMatcher, Matcher singleIdentifierMatcher, StringBuffer outputToReturn, String translationKey, StringBuilder replacedTextStringBuilder, Locale locale, WebResourceIntegration webResourceIntegration) {
        JsI18nTransformer.removeI18nGetTextCall(i18nMatcher, outputToReturn);
        String formattedTranslation = webResourceIntegration.getI18nText(locale, translationKey);
        replacedTextStringBuilder.append("\"").append(JsI18nTransformer.jsEncode(formattedTranslation)).append("\"");
        outputToReturn.delete(outputToReturn.length() - singleIdentifierMatcher.group(0).length(), outputToReturn.length());
    }

    private static void replaceI18nGetTextCallWithACallToWrmFormat(Matcher i18nMatcher, StringBuffer outputToReturn, String translationKey, StringBuilder replacedTextStringBuilder, Locale locale, WebResourceIntegration webResourceIntegration, boolean moreFormatFnArgs) {
        JsI18nTransformer.removeI18nGetTextCall(i18nMatcher, outputToReturn);
        String rawTranslation = webResourceIntegration.getI18nRawText(locale, translationKey);
        replacedTextStringBuilder.append(".format(\"").append(JsI18nTransformer.jsEncode(rawTranslation)).append("\"").append(moreFormatFnArgs ? FUNCTION_ARGS_SEPARATOR : ")");
    }

    public Dimensions computeDimensions() {
        List locales = StreamSupport.stream(this.webResourceIntegration.getSupportedLocales().spliterator(), false).map(LocaleUtils::serialize).collect(Collectors.toList());
        return Dimensions.empty().andExactly(QUERY_KEY, locales);
    }

    public DimensionAwareTransformerUrlBuilder makeUrlBuilder(TransformerParameters params) {
        return new JsI18nTransformerUrlBuilder();
    }

    public UrlReadingWebResourceTransformer makeResourceTransformer(TransformerParameters params) {
        return new JsI18nUrlReadingWebResourceTransformer();
    }

    private final class JsI18nTransformerUrlBuilder
    implements DimensionAwareTransformerUrlBuilder {
        private JsI18nTransformerUrlBuilder() {
        }

        public void addToUrl(UrlBuilder urlBuilder) {
            String locale = LocaleUtils.serialize(JsI18nTransformer.this.webResourceIntegration.getLocale());
            this.addToUrl(urlBuilder, locale);
        }

        public void addToUrl(UrlBuilder urlBuilder, Coordinate coord) {
            String locale = coord.get(JsI18nTransformer.QUERY_KEY);
            this.addToUrl(urlBuilder, locale);
        }

        private void addToUrl(UrlBuilder urlBuilder, String locale) {
            urlBuilder.addToQueryString(JsI18nTransformer.QUERY_KEY, locale);
            urlBuilder.addToHash(JsI18nTransformer.HASH_KEY, (Object)JsI18nTransformer.this.webResourceIntegration.getI18nStateHash());
        }
    }

    private final class JsI18nUrlReadingWebResourceTransformer
    implements UrlReadingWebResourceTransformer,
    TwoPhaseResourceTransformer {
        private Map<String, String> twoPhaseProps;

        private JsI18nUrlReadingWebResourceTransformer() {
        }

        public boolean hasTwoPhaseProperties() {
            return this.twoPhaseProps != null;
        }

        public void loadTwoPhaseProperties(ResourceLocation resourceLocation, Function<String, InputStream> loadFromFile) {
            this.twoPhaseProps = null;
            if (JsI18nTransformer.this.isTwoPhaseJsI18nDisabled()) {
                return;
            }
            String filepath = resourceLocation.getLocation();
            if (filepath == null) {
                LOGGER.debug("ResourceLocation has no location value. This should never happen.");
                return;
            }
            String filePropertiesPath = filepath.replaceAll("[-.]min\\.js$", ".js") + ".i18n.properties";
            try {
                InputStream postProcessedPropertiesStream = loadFromFile.apply(filePropertiesPath);
                if (null != postProcessedPropertiesStream) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    Properties postProcessedProperties = new Properties();
                    postProcessedProperties.load(postProcessedPropertiesStream);
                    for (Map.Entry<Object, Object> entry : postProcessedProperties.entrySet()) {
                        map.put((String)entry.getKey(), (String)entry.getValue());
                    }
                    this.twoPhaseProps = Collections.unmodifiableMap(map);
                }
            }
            catch (IOException e) {
                LOGGER.debug("Resource `{}` has no backing `{}` file", (Object)filepath, (Object)filePropertiesPath);
            }
        }

        public DownloadableResource transform(TransformableResource transformableResource, QueryParams params) {
            final Locale locale = JsI18nTransformer.getLocaleFromQueryParams(params);
            final DownloadableResource original = transformableResource.nextResource();
            if (this.hasTwoPhaseProperties()) {
                final Set<String> usedI18nKeys = this.twoPhaseProps.keySet();
                if (usedI18nKeys.isEmpty()) {
                    return original;
                }
                return new AbstractTransformedDownloadableResource(original){

                    public void streamResource(OutputStream out) throws DownloadException {
                        StringBuilder builder = new StringBuilder();
                        if (Flags.isDevMode()) {
                            builder.append("if (!(window.WRM && window.WRM.I18n )) {");
                            builder.append("\n");
                            builder.append(JsI18nTransformer.I18N_CONSOLE_WARN);
                            builder.append("\n");
                            builder.append("}");
                            builder.append("\n");
                        }
                        builder.append("(k=>{");
                        usedI18nKeys.stream().sorted().forEach(key -> {
                            String val = JsI18nTransformer.this.webResourceIntegration.getI18nRawText(locale, key);
                            builder.append("k.set('").append(JsI18nTransformer.jsEncode(key)).append("','").append(JsI18nTransformer.jsEncode(val)).append("');");
                        });
                        builder.append("})(WRM.I18n.km);\n");
                        try {
                            out.write(builder.toString().getBytes(StandardCharsets.UTF_8));
                            original.streamResource(out);
                        }
                        catch (IOException e) {
                            throw new DownloadException("Could not prepend i18n keys", (Exception)e);
                        }
                    }
                };
            }
            return new CharSequenceDownloadableResource(original){

                protected CharSequence transform(CharSequence input) {
                    Matcher i18nMatcher = I18N_GET_TEXT_PATTERN.matcher(input);
                    Matcher singleIdentifierMatcher = SINGLE_IDENTIFIER_PATTERN.matcher(input);
                    Matcher longNamespaceMatcher = LONG_NAMESPACE_PROP_ACCESSOR_PATTERN.matcher(input);
                    StringBuffer outputToReturn = new StringBuffer();
                    while (i18nMatcher.find()) {
                        JsI18nTransformer.limitMatcherUptoStartOfAPreviousMatch(i18nMatcher, singleIdentifierMatcher, 120);
                        if (!singleIdentifierMatcher.find()) continue;
                        String translationKey = i18nMatcher.group(2);
                        boolean formatFnNeeded = JsI18nTransformer.FUNCTION_ARGS_SEPARATOR.equals(i18nMatcher.group(3));
                        StringBuilder replacedTextStringBuilder = new StringBuilder();
                        if (formatFnNeeded) {
                            JsI18nTransformer.replaceI18nGetTextCallWithACallToWrmFormat(i18nMatcher, outputToReturn, translationKey, replacedTextStringBuilder, locale, JsI18nTransformer.this.webResourceIntegration, formatFnNeeded);
                        } else {
                            JsI18nTransformer.limitMatcherUptoStartOfAPreviousMatch(singleIdentifierMatcher, longNamespaceMatcher, 20);
                            if (longNamespaceMatcher.find()) {
                                JsI18nTransformer.replaceI18nGetTextCallWithACallToWrmFormat(i18nMatcher, outputToReturn, translationKey, replacedTextStringBuilder, locale, JsI18nTransformer.this.webResourceIntegration, formatFnNeeded);
                            } else {
                                JsI18nTransformer.replaceNameSpaceAndI18nGetTextCallWithJustTheFormattedTranslationString(i18nMatcher, singleIdentifierMatcher, outputToReturn, translationKey, replacedTextStringBuilder, locale, JsI18nTransformer.this.webResourceIntegration);
                            }
                        }
                        outputToReturn.append((CharSequence)replacedTextStringBuilder);
                    }
                    i18nMatcher.appendTail(outputToReturn);
                    return outputToReturn;
                }
            };
        }
    }
}

