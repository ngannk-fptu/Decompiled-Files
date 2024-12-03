/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.GadgetParsingException
 *  com.atlassian.gadgets.util.Uri
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.webresource.JavascriptWebResource
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceFormatter
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.atlassian.webresource.api.UrlMode
 *  com.atlassian.webresource.api.assembler.WebResourceAssembler
 *  com.atlassian.webresource.api.assembler.WebResourceAssemblerFactory
 *  com.google.common.base.Preconditions
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.gadgets.publisher.internal.impl;

import com.atlassian.gadgets.GadgetParsingException;
import com.atlassian.gadgets.publisher.internal.GadgetProcessor;
import com.atlassian.gadgets.util.Uri;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.webresource.JavascriptWebResource;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceFormatter;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.webresource.api.assembler.WebResourceAssembler;
import com.atlassian.webresource.api.assembler.WebResourceAssemblerFactory;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GadgetProcessorImpl
implements GadgetProcessor {
    private static final String ATLASSIAN_BASE_URL = "__ATLASSIAN_BASE_URL__";
    private static final WebResourceFormatter JS_FORMATTER = new JavascriptWebResource();
    private static final String VALID_KEY_CHARS_REGEXP = "[^\"/\r\n]+";
    private static final String PLUGIN_KEY_REGEXP = "[^\"/\r\n]+";
    private static final String RESOURCE_PATH_REGEXP = "[^\"/\r\n]+(/[^\"/\r\n]+)*";
    private static final int NOT_FOUND = -1;
    private static final String UNDETERMINED_LOCALE_KEY = "und";
    private static final Pattern INCLUDE_RESOURCES = Pattern.compile("#includeResources(?:\\(\\))?");
    private static final Pattern OAUTH = Pattern.compile("#oauth");
    private final Logger log = LoggerFactory.getLogger(GadgetProcessorImpl.class);
    private final ApplicationProperties applicationProperties;
    private final WebResourceUrlProvider webResourceUrlProvider;
    private final WebResourceAssemblerFactory wrmAssemblerFactory;
    private final LocaleResolver localeResolver;
    private final DarkFeatureManager darkFeatureManager;
    private static final String ACCESS_TOKEN_PATH = "/plugins/servlet/oauth/access-token";
    private static final String REQUEST_TOKEN_PATH = "/plugins/servlet/oauth/request-token";
    private static final String AUTHORIZE_PATH = "/plugins/servlet/oauth/authorize";
    private static final String OAUTH_CALLBACK = "http://oauth.gmodules.com/gadgets/oauthcallback";

    @Autowired
    public GadgetProcessorImpl(@ComponentImport ApplicationProperties applicationProperties, @ComponentImport WebResourceUrlProvider webResourceUrlProvider, @ComponentImport WebResourceAssemblerFactory wrmAssemblerFactory, @ComponentImport LocaleResolver localeResolver, @ComponentImport DarkFeatureManager darkFeatureManager) {
        this.applicationProperties = applicationProperties;
        this.webResourceUrlProvider = webResourceUrlProvider;
        this.wrmAssemblerFactory = wrmAssemblerFactory;
        this.localeResolver = localeResolver;
        this.darkFeatureManager = darkFeatureManager;
    }

    @Override
    public void process(InputStream in, OutputStream out) throws GadgetParsingException {
        Preconditions.checkNotNull((Object)in);
        Preconditions.checkNotNull((Object)out);
        try {
            String gadgetString = IOUtils.toString((InputStream)in, (String)"UTF-8");
            gadgetString = this.processAtlassianBaseUrl(gadgetString);
            gadgetString = this.processGetSupportedLocales(gadgetString);
            gadgetString = this.processIncludeResources(gadgetString);
            gadgetString = this.processStaticResourceUrl(gadgetString);
            gadgetString = this.processOAuth(gadgetString);
            out.write(gadgetString.getBytes("UTF-8"));
        }
        catch (IOException e) {
            throw new GadgetParsingException((Throwable)e);
        }
    }

    private String processAtlassianBaseUrl(String gadgetSpecString) {
        String baseUrl = this.applicationProperties.getBaseUrl(com.atlassian.sal.api.UrlMode.ABSOLUTE);
        if (StringUtils.isNotBlank((CharSequence)baseUrl)) {
            return gadgetSpecString.replace(ATLASSIAN_BASE_URL, baseUrl);
        }
        this.log.warn("GadgetProcessorImpl: empty application base URL; processed gadget spec may not be valid");
        return gadgetSpecString;
    }

    private String processGetSupportedLocales(String gadgetSpecString) throws IOException {
        Matcher matcher = GET_SUPPORTED_LOCALES.PATTERN.matcher(gadgetSpecString);
        if (matcher.find()) {
            Set locales = this.localeResolver.getSupportedLocales();
            StringBuilder processedGadgetSpec = new StringBuilder();
            String prefixes = matcher.group(1);
            StringBuilder supportedLocalesString = new StringBuilder();
            this.createLocaleElement(supportedLocalesString, prefixes, new Locale(""));
            for (Locale locale : locales) {
                this.createLocaleElement(supportedLocalesString, prefixes, locale);
            }
            processedGadgetSpec.append(gadgetSpecString.substring(0, matcher.start())).append((CharSequence)supportedLocalesString).append(gadgetSpecString.substring(matcher.end()));
            return processedGadgetSpec.toString();
        }
        return gadgetSpecString;
    }

    private void createLocaleElement(Appendable supportedLocalesString, String prefixes, Locale locale) throws IOException {
        supportedLocalesString.append("<Locale");
        if (StringUtils.isNotEmpty((CharSequence)locale.getLanguage())) {
            supportedLocalesString.append(" lang=\"");
            supportedLocalesString.append(StringEscapeUtils.escapeXml((String)locale.getLanguage()));
            supportedLocalesString.append("\"");
        }
        if (StringUtils.isNotEmpty((CharSequence)locale.getCountry())) {
            supportedLocalesString.append(" country=\"");
            supportedLocalesString.append(StringEscapeUtils.escapeXml((String)locale.getCountry()));
            supportedLocalesString.append("\"");
        }
        String baseUrl = this.applicationProperties.getBaseUrl(com.atlassian.sal.api.UrlMode.ABSOLUTE);
        String localeString = StringUtils.isBlank((CharSequence)locale.toString()) ? UNDETERMINED_LOCALE_KEY : locale.toString();
        String localeUrl = String.format("%s/rest/gadgets/1.0/g/messagebundle/%s/%s", baseUrl, Uri.encodeUriComponent((String)localeString), Uri.encodeUriComponent((String)prefixes));
        supportedLocalesString.append(" messages=\"").append(localeUrl).append("\"/>");
    }

    private String processIncludeResources(String gadgetSpecString) throws GadgetParsingException {
        StringBuilder processedGadgetSpec = new StringBuilder();
        Matcher matcher = INCLUDE_RESOURCES.matcher(gadgetSpecString);
        int startSearchSpace = 0;
        while (matcher.find()) {
            WebResourceAssembler assembler = this.wrmAssemblerFactory.create().includeSuperbatchResources(false).build();
            String resourceSearchString = gadgetSpecString.substring(startSearchSpace, matcher.start());
            processedGadgetSpec.append(this.processRequireResource(resourceSearchString, assembler));
            Optional.ofNullable(this.applicationProperties.getBaseUrl(com.atlassian.sal.api.UrlMode.ABSOLUTE)).ifPresent(url -> assembler.data().requireData("gadget.base.url", url));
            if (this.darkFeatureManager.getFeaturesEnabledForAllUsers().isFeatureEnabled("jquery.external")) {
                String scriptTag = JS_FORMATTER.formatResource("https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js", Collections.emptyMap());
                processedGadgetSpec.append(scriptTag);
            }
            StringWriter tagWriter = new StringWriter();
            assembler.assembled().drainIncludedResources().writeHtmlTags((Writer)tagWriter, com.atlassian.webresource.api.UrlMode.ABSOLUTE);
            processedGadgetSpec.append(tagWriter.toString());
            startSearchSpace = matcher.end();
        }
        processedGadgetSpec.append(gadgetSpecString.substring(startSearchSpace));
        return processedGadgetSpec.toString();
    }

    private String processRequireResource(String resourceSearchSpace, WebResourceAssembler assembler) throws GadgetParsingException {
        Matcher matcher = REQUIRE_RESOURCE.PATTERN.matcher(resourceSearchSpace);
        int startIndex = -1;
        int endIndex = resourceSearchSpace.length();
        while (matcher.find()) {
            if (startIndex == -1) {
                startIndex = matcher.start();
            }
            endIndex = matcher.end();
            String moduleCompleteKey = matcher.group(2);
            if ("resource".equalsIgnoreCase(matcher.group(1))) {
                assembler.resources().requireWebResource(moduleCompleteKey);
                continue;
            }
            assembler.resources().requireContext(moduleCompleteKey);
        }
        if (startIndex == -1) {
            return resourceSearchSpace;
        }
        StringBuilder processedGadgetSpec = new StringBuilder();
        processedGadgetSpec.append(resourceSearchSpace.substring(0, startIndex)).append(resourceSearchSpace.substring(endIndex));
        return processedGadgetSpec.toString();
    }

    private String processStaticResourceUrl(String gadgetSpecString) throws GadgetParsingException {
        Matcher matcher = STATIC_RESOURCE_URL.PATTERN.matcher(gadgetSpecString);
        int endIndex = 0;
        StringBuilder processedGadgetSpec = new StringBuilder();
        while (matcher.find()) {
            String moduleCompleteKey = matcher.group(1);
            String resourceName = matcher.group(2);
            String staticResourceUrl = this.webResourceUrlProvider.getStaticPluginResourceUrl(moduleCompleteKey, resourceName, UrlMode.ABSOLUTE);
            processedGadgetSpec.append(gadgetSpecString.substring(endIndex, matcher.start()));
            processedGadgetSpec.append(staticResourceUrl);
            endIndex = matcher.end();
        }
        processedGadgetSpec.append(gadgetSpecString.substring(endIndex));
        return processedGadgetSpec.toString();
    }

    private String processOAuth(String gadgetSpec) {
        StringBuilder processedGadgetSpec = new StringBuilder(gadgetSpec);
        Matcher matcher = OAUTH.matcher(gadgetSpec);
        if (matcher.find()) {
            processedGadgetSpec.replace(matcher.start(), matcher.end(), this.createOAuthElement());
        }
        return processedGadgetSpec.toString();
    }

    private String createOAuthElement() {
        String baseUrl = this.applicationProperties.getBaseUrl(com.atlassian.sal.api.UrlMode.ABSOLUTE);
        StringBuilder builder = new StringBuilder("<OAuth><Service>");
        builder.append("<Access url=\"");
        builder.append(baseUrl);
        builder.append(ACCESS_TOKEN_PATH);
        builder.append("\" method=\"POST\" />");
        builder.append("<Request url=\"");
        builder.append(baseUrl);
        builder.append(REQUEST_TOKEN_PATH);
        builder.append("\" method=\"POST\" />");
        builder.append("<Authorization url=\"");
        builder.append(baseUrl);
        builder.append(AUTHORIZE_PATH);
        builder.append("?oauth_callback=");
        builder.append(Uri.encodeUriComponent((String)OAUTH_CALLBACK));
        builder.append("\" /></Service></OAuth>");
        return builder.toString();
    }

    private static class GET_SUPPORTED_LOCALES {
        private static final Pattern PATTERN = Pattern.compile("#supportedLocales\\(\"(([^\"/\r\n]+,*)*)\"\\)");
        static final int MESSAGES_PREFIX = 1;

        private GET_SUPPORTED_LOCALES() {
        }
    }

    private static class STATIC_RESOURCE_URL {
        private static final Pattern PATTERN = Pattern.compile("#staticResourceUrl\\(\"([^\"/\r\n]+)\",\\p{Space}*\"([^\"/\r\n]+(/[^\"/\r\n]+)*)\"\\)");
        static final int RESOURCE_NAME = 2;
        static final int MODULE_COMPLETE_PLUGIN_KEY = 1;

        private STATIC_RESOURCE_URL() {
        }
    }

    private static class REQUIRE_RESOURCE {
        static final Pattern PATTERN = Pattern.compile("#require(Resource|Context)\\(\"([^\"/\r\n]+)\"\\)");
        static final int MODULE_COMPLETE_PLUGIN_KEY = 2;
        static final int RESOURCE_OR_CONTEXT = 1;

        private REQUIRE_RESOURCE() {
        }
    }
}

