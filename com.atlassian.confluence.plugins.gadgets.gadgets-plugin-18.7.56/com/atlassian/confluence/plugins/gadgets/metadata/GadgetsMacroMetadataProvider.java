/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.browser.beans.MacroBody
 *  com.atlassian.confluence.macro.browser.beans.MacroCategory
 *  com.atlassian.confluence.macro.browser.beans.MacroFormDetails
 *  com.atlassian.confluence.macro.browser.beans.MacroIcon
 *  com.atlassian.confluence.macro.browser.beans.MacroMetadata
 *  com.atlassian.confluence.macro.browser.beans.MacroParameter
 *  com.atlassian.confluence.macro.browser.beans.MacroParameterType
 *  com.atlassian.confluence.macro.browser.beans.MacroSummary
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.gadgets.GadgetParsingException
 *  com.atlassian.gadgets.GadgetRequestContext
 *  com.atlassian.gadgets.GadgetSpecProvider
 *  com.atlassian.gadgets.directory.spi.ExternalGadgetSpec
 *  com.atlassian.gadgets.directory.spi.ExternalGadgetSpecStore
 *  com.atlassian.gadgets.directory.spi.SubscribedGadgetFeed
 *  com.atlassian.gadgets.directory.spi.SubscribedGadgetFeedStore
 *  com.atlassian.gadgets.feed.GadgetFeedReader
 *  com.atlassian.gadgets.feed.GadgetFeedReaderFactory
 *  com.atlassian.gadgets.spec.DataType
 *  com.atlassian.gadgets.spec.GadgetSpec
 *  com.atlassian.gadgets.spec.GadgetSpecFactory
 *  com.atlassian.gadgets.spec.UserPrefSpec
 *  com.atlassian.sal.api.message.I18nResolver
 *  org.apache.commons.codec.digest.DigestUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.gadgets.metadata;

import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.browser.beans.MacroBody;
import com.atlassian.confluence.macro.browser.beans.MacroCategory;
import com.atlassian.confluence.macro.browser.beans.MacroFormDetails;
import com.atlassian.confluence.macro.browser.beans.MacroIcon;
import com.atlassian.confluence.macro.browser.beans.MacroMetadata;
import com.atlassian.confluence.macro.browser.beans.MacroParameter;
import com.atlassian.confluence.macro.browser.beans.MacroParameterType;
import com.atlassian.confluence.macro.browser.beans.MacroSummary;
import com.atlassian.confluence.plugins.gadgets.events.GadgetMacroMetadataBuildEvent;
import com.atlassian.confluence.plugins.gadgets.metadata.DeprecatedGadgetFilter;
import com.atlassian.confluence.plugins.gadgets.metadata.GadgetMacroMetadata;
import com.atlassian.confluence.plugins.gadgets.metadata.GadgetMacroMetadataBuilder;
import com.atlassian.confluence.plugins.gadgets.metadata.GadgetMacroParameter;
import com.atlassian.confluence.plugins.gadgets.requestcontext.RequestContextBuilder;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.gadgets.GadgetParsingException;
import com.atlassian.gadgets.GadgetRequestContext;
import com.atlassian.gadgets.GadgetSpecProvider;
import com.atlassian.gadgets.directory.spi.ExternalGadgetSpec;
import com.atlassian.gadgets.directory.spi.ExternalGadgetSpecStore;
import com.atlassian.gadgets.directory.spi.SubscribedGadgetFeed;
import com.atlassian.gadgets.directory.spi.SubscribedGadgetFeedStore;
import com.atlassian.gadgets.feed.GadgetFeedReader;
import com.atlassian.gadgets.feed.GadgetFeedReaderFactory;
import com.atlassian.gadgets.spec.DataType;
import com.atlassian.gadgets.spec.GadgetSpec;
import com.atlassian.gadgets.spec.GadgetSpecFactory;
import com.atlassian.gadgets.spec.UserPrefSpec;
import com.atlassian.sal.api.message.I18nResolver;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class GadgetsMacroMetadataProvider {
    private static final Logger log = LoggerFactory.getLogger(GadgetsMacroMetadataProvider.class);
    private final GadgetSpecProvider gadgetSpecProvider;
    private final ExternalGadgetSpecStore gadgetStore;
    private final SubscribedGadgetFeedStore feedStore;
    private final GadgetFeedReaderFactory gadgetFeedReaderFactory;
    private final GadgetSpecFactory gadgetSpecFactory;
    private final RequestContextBuilder requestContextBuilder;
    private final I18nResolver resolver;
    private final EventPublisher eventPublisher;
    private final DeprecatedGadgetFilter deprecatedGadgetFilter;
    private static final String GADGET_MACRO_NAME = "gadget";
    private static final String DUMMY_PLUGIN_KEY = "gadgets";
    private static final String GADGET_PREFIX = "gadget-";
    private static final String IS_CONFIGURED = "isConfigured";

    GadgetsMacroMetadataProvider(GadgetSpecProvider gadgetSpecProvider, ExternalGadgetSpecStore gadgetStore, SubscribedGadgetFeedStore feedStore, GadgetFeedReaderFactory gadgetFeedReaderFactory, GadgetSpecFactory gadgetSpecFactory, RequestContextBuilder requestContextBuilder, I18nResolver resolver, EventPublisher eventPublisher) {
        this.gadgetSpecProvider = gadgetSpecProvider;
        this.gadgetStore = gadgetStore;
        this.feedStore = feedStore;
        this.gadgetFeedReaderFactory = gadgetFeedReaderFactory;
        this.gadgetSpecFactory = gadgetSpecFactory;
        this.requestContextBuilder = requestContextBuilder;
        this.resolver = resolver;
        this.eventPublisher = eventPublisher;
        this.deprecatedGadgetFilter = new DeprecatedGadgetFilter();
    }

    Collection<MacroMetadata> getMacroMetadata(Set<URI> uris) {
        this.eventPublisher.publish((Object)new GadgetMacroMetadataBuildEvent(uris.size()));
        return this.generateMacroMetadata(uris);
    }

    private MacroMetadata convertSpecToMacroMetadata(GadgetSpec spec, boolean isDeprecated) {
        String location;
        MacroParameter urlParam = new MacroParameter(DUMMY_PLUGIN_KEY, GADGET_MACRO_NAME, "url", MacroParameterType.STRING, true, false, spec.getUrl().toString(), true);
        MacroParameter width = new MacroParameter(DUMMY_PLUGIN_KEY, GADGET_MACRO_NAME, "width", MacroParameterType.INT, false, false, "450", false);
        MacroParameter border = new MacroParameter(DUMMY_PLUGIN_KEY, GADGET_MACRO_NAME, "border", MacroParameterType.BOOLEAN, false, false, "true", false);
        MacroParameter author = new MacroParameter(DUMMY_PLUGIN_KEY, GADGET_MACRO_NAME, "author", MacroParameterType.STRING, false, false, spec.getAuthor(), false);
        MacroParameter gadgetPreferences = new MacroParameter(DUMMY_PLUGIN_KEY, GADGET_MACRO_NAME, "preferences", MacroParameterType.STRING, false, false, "", true);
        ArrayList<MacroParameter> macroParams = new ArrayList<MacroParameter>();
        boolean hasNonHiddenPrefs = false;
        boolean needsConfig = false;
        for (UserPrefSpec userPrefSpec : spec.getUserPrefs()) {
            if (userPrefSpec.getDataType() != DataType.HIDDEN) {
                hasNonHiddenPrefs = true;
                macroParams.add(new GadgetMacroParameter(userPrefSpec));
                continue;
            }
            if (!userPrefSpec.getName().equals(IS_CONFIGURED)) continue;
            needsConfig = true;
        }
        macroParams.add(urlParam);
        macroParams.add(width);
        macroParams.add(border);
        macroParams.add(author);
        macroParams.add(gadgetPreferences);
        MacroBody body = new MacroBody(DUMMY_PLUGIN_KEY, GADGET_MACRO_NAME, true);
        body.setBodyType(Macro.BodyType.NONE.toString());
        MacroFormDetails details = MacroFormDetails.builder().macroName(GADGET_MACRO_NAME).parameters(macroParams).body(body).build();
        HashSet<String> categories = new HashSet<String>(Arrays.asList(MacroCategory.EXTERNAL_CONTENT.getName()));
        String title = spec.getDirectoryTitle();
        if (StringUtils.isBlank((CharSequence)title)) {
            title = spec.getTitle();
        }
        MacroIcon macroIcon = null;
        URI thumbnail = spec.getThumbnail();
        if (thumbnail != null && !(location = thumbnail.toString()).equals("")) {
            macroIcon = new MacroIcon(location, !thumbnail.isAbsolute(), 40, 80);
        }
        StringBuilder extendedDescription = new StringBuilder();
        if (StringUtils.isNotBlank((CharSequence)spec.getDescription())) {
            extendedDescription.append(spec.getDescription());
        }
        extendedDescription.append(this.resolver.getText("gadgets.configure.params.below"));
        if (!hasNonHiddenPrefs) {
            extendedDescription.append(this.resolver.getText("gadgets.configure.params.extra"));
        }
        GadgetMacroMetadataBuilder gadgetMacroMetadataBuilder = GadgetMacroMetadata.builder();
        gadgetMacroMetadataBuilder.setNonHiddenUserPrefs(hasNonHiddenPrefs).setNeedsConfig(needsConfig).setAlternativeDescription(extendedDescription.toString()).setMacroName(GADGET_MACRO_NAME).setPluginKey(DUMMY_PLUGIN_KEY).setTitle(title).setIcon(macroIcon).setDescription(spec.getDescription()).setAliases(Collections.emptySet()).setCategories(categories).setBodyDeprecated(false).setHidden(isDeprecated).setFormDetails(details).setAlternateId(GADGET_PREFIX + DigestUtils.md5Hex((String)spec.getUrl().toString()));
        return gadgetMacroMetadataBuilder.build();
    }

    private Collection<MacroMetadata> generateMacroMetadata(Set<URI> uris) {
        HashSet<MacroMetadata> metaData = new HashSet<MacroMetadata>();
        GadgetRequestContext requestContext = this.requestContextBuilder.buildRequestContext(false);
        for (URI uri : uris) {
            log.trace("Fetching gadget spec for gadget {} at URI {}", (Object)uri.getPath(), (Object)uri);
            try {
                GadgetSpec spec = this.gadgetSpecFactory.getGadgetSpec(uri, requestContext);
                metaData.add(this.convertSpecToMacroMetadata(spec, this.deprecatedGadgetFilter.isGadgetDeprecated(uri)));
            }
            catch (GadgetParsingException e) {
                String message = "Unable to parse gadget spec [ " + uri + " ]";
                if (log.isDebugEnabled()) {
                    log.debug(message, (Throwable)e);
                    continue;
                }
                log.warn(message + ": " + e.getMessage());
            }
        }
        return metaData;
    }

    Set<URI> getGadgetUris() {
        HashSet<URI> tempSet = new HashSet<URI>();
        tempSet.addAll(this.getUrisFromGadgetSpecProvider());
        tempSet.addAll(this.getUrisFromGadgetStore());
        tempSet.addAll(this.getUrisFromFeedStore());
        return Collections.unmodifiableSet(tempSet);
    }

    private Set<URI> getUrisFromFeedStore() {
        log.debug("Fetching gadget URIs from subscribed gadget feed store");
        HashSet<URI> uris = new HashSet<URI>();
        for (SubscribedGadgetFeed feed : this.feedStore.getAllFeeds()) {
            try {
                GadgetFeedReader reader = this.gadgetFeedReaderFactory.getFeedReader(feed.getUri());
                reader.entries().forEach(uris::add);
            }
            catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.debug("Error reading gadget feed from {}", (Object)feed.getUri(), (Object)e);
                    continue;
                }
                log.info("Error reading gadget feed from {}: {}", (Object)feed.getUri(), (Object)e.getMessage());
            }
        }
        log.debug("Fetched {} gadget URIs from subscribed gadget feed store", (Object)uris.size());
        return uris;
    }

    private Set<URI> getUrisFromGadgetStore() {
        log.debug("Fetching gadget URIs from external gadget spec store");
        HashSet<URI> uris = new HashSet<URI>();
        for (ExternalGadgetSpec externalGadgetSpec : this.gadgetStore.entries()) {
            uris.add(externalGadgetSpec.getSpecUri());
        }
        log.debug("Fetched {} gadget URIs from external gadget spec store", (Object)uris.size());
        return uris;
    }

    private Set<URI> getUrisFromGadgetSpecProvider() {
        log.debug("Fetching gadget URIs from gadget spec provider");
        HashSet<URI> uris = new HashSet<URI>();
        this.gadgetSpecProvider.entries().forEach(uris::add);
        log.debug("Fetched {} gadget URIs from gadget spec provider", (Object)uris.size());
        return uris;
    }

    static Collection<MacroSummary> getSummaries(Collection<MacroMetadata> macroMetadata) {
        ArrayList<MacroSummary> summaries = new ArrayList<MacroSummary>();
        for (MacroMetadata metadata : macroMetadata) {
            summaries.add(metadata.extractMacroSummary());
        }
        return summaries;
    }

    static MacroMetadata getByMacroNameAndId(String macroName, String alternateId, Collection<MacroMetadata> metadata) {
        return GadgetsMacroMetadataProvider.getByMacroNameAndId(macroName, alternateId, () -> metadata);
    }

    static MacroMetadata getByMacroNameAndId(String macroName, String alternateId, Supplier<Collection<MacroMetadata>> metadataFetcher) {
        if (!GADGET_MACRO_NAME.equals(macroName)) {
            return null;
        }
        return metadataFetcher.get().stream().filter(meta -> macroName.equals(meta.getMacroName())).filter(meta -> GadgetsMacroMetadataProvider.hasMatchingId(alternateId, meta)).findFirst().orElse(null);
    }

    private static boolean hasMatchingId(String alternateId, MacroMetadata macroMetadata) {
        return StringUtils.isEmpty((CharSequence)alternateId) || alternateId.equals(macroMetadata.getAlternateId());
    }
}

