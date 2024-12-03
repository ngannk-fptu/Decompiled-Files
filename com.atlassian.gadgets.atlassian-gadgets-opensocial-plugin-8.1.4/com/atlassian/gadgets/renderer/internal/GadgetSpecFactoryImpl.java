/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.GadgetParsingException
 *  com.atlassian.gadgets.GadgetRequestContext
 *  com.atlassian.gadgets.GadgetState
 *  com.atlassian.gadgets.spec.DataType
 *  com.atlassian.gadgets.spec.Feature
 *  com.atlassian.gadgets.spec.GadgetSpec
 *  com.atlassian.gadgets.spec.GadgetSpecFactory
 *  com.atlassian.gadgets.spec.UserPrefSpec
 *  com.atlassian.gadgets.util.Uri
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Maps
 *  com.google.inject.Injector
 *  com.google.inject.Provider
 *  org.apache.shindig.common.uri.Uri
 *  org.apache.shindig.gadgets.GadgetContext
 *  org.apache.shindig.gadgets.GadgetException
 *  org.apache.shindig.gadgets.GadgetFeatureRegistry
 *  org.apache.shindig.gadgets.GadgetSpecFactory
 *  org.apache.shindig.gadgets.RenderingContext
 *  org.apache.shindig.gadgets.UserPrefs
 *  org.apache.shindig.gadgets.spec.Feature
 *  org.apache.shindig.gadgets.spec.GadgetSpec
 *  org.apache.shindig.gadgets.spec.ModulePrefs
 *  org.apache.shindig.gadgets.spec.UserPref
 *  org.apache.shindig.gadgets.spec.UserPref$EnumValuePair
 *  org.apache.shindig.gadgets.variables.VariableSubstituter
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.gadgets.renderer.internal;

import com.atlassian.gadgets.GadgetParsingException;
import com.atlassian.gadgets.GadgetRequestContext;
import com.atlassian.gadgets.GadgetState;
import com.atlassian.gadgets.renderer.internal.FeatureImpl;
import com.atlassian.gadgets.spec.DataType;
import com.atlassian.gadgets.spec.Feature;
import com.atlassian.gadgets.spec.GadgetSpec;
import com.atlassian.gadgets.spec.UserPrefSpec;
import com.atlassian.gadgets.util.Uri;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.inject.Injector;
import com.google.inject.Provider;
import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import org.apache.shindig.gadgets.GadgetContext;
import org.apache.shindig.gadgets.GadgetException;
import org.apache.shindig.gadgets.GadgetFeatureRegistry;
import org.apache.shindig.gadgets.GadgetSpecFactory;
import org.apache.shindig.gadgets.RenderingContext;
import org.apache.shindig.gadgets.UserPrefs;
import org.apache.shindig.gadgets.spec.ModulePrefs;
import org.apache.shindig.gadgets.spec.UserPref;
import org.apache.shindig.gadgets.variables.VariableSubstituter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="gadgetSpecFactory")
@ExportAsService(value={com.atlassian.gadgets.spec.GadgetSpecFactory.class})
public class GadgetSpecFactoryImpl
implements com.atlassian.gadgets.spec.GadgetSpecFactory {
    private static final Logger logger = LoggerFactory.getLogger(GadgetSpecFactoryImpl.class);
    private final ApplicationProperties applicationProperties;
    private final GadgetSpecFactory shindigFactory;
    private final VariableSubstituter substituter;
    private GadgetFeatureRegistry gadgetFeatureRegistry;

    @Autowired
    public GadgetSpecFactoryImpl(Provider<Injector> provider, @ComponentImport ApplicationProperties applicationProperties) {
        Preconditions.checkNotNull(provider, (Object)"provider");
        Preconditions.checkNotNull((Object)applicationProperties, (Object)"applicationProperties");
        this.applicationProperties = applicationProperties;
        this.shindigFactory = (GadgetSpecFactory)((Injector)provider.get()).getInstance(GadgetSpecFactory.class);
        this.substituter = (VariableSubstituter)((Injector)provider.get()).getInstance(VariableSubstituter.class);
        this.gadgetFeatureRegistry = (GadgetFeatureRegistry)((Injector)provider.get()).getInstance(GadgetFeatureRegistry.class);
    }

    public GadgetSpec getGadgetSpec(GadgetState gadgetState, GadgetRequestContext gadgetRequestContext) throws GadgetParsingException {
        return this.getGadgetSpec(gadgetState.getGadgetSpecUri(), gadgetState.getUserPrefs(), gadgetRequestContext);
    }

    public GadgetSpec getGadgetSpec(URI uri, GadgetRequestContext gadgetRequestContext) throws GadgetParsingException {
        return this.getGadgetSpec(uri, Collections.emptyMap(), gadgetRequestContext);
    }

    private GadgetSpec getGadgetSpec(URI specUri, final Map<String, String> userPrefs, final GadgetRequestContext gadgetRequestContext) throws GadgetParsingException {
        final URI absoluteSpecUri = Uri.resolveUriAgainstBase((String)this.applicationProperties.getBaseUrl(), (URI)specUri);
        GadgetContext gadgetContext = new GadgetContext(){

            public URI getUrl() {
                return absoluteSpecUri;
            }

            public boolean getIgnoreCache() {
                return gadgetRequestContext.getIgnoreCache();
            }

            public RenderingContext getRenderingContext() {
                return RenderingContext.CONTAINER;
            }

            public UserPrefs getUserPrefs() {
                return new UserPrefs(userPrefs);
            }

            public Locale getLocale() {
                if (gadgetRequestContext.getLocale() != null) {
                    return gadgetRequestContext.getLocale();
                }
                return new Locale("");
            }

            public boolean getDebug() {
                return gadgetRequestContext.isDebuggingEnabled();
            }
        };
        try {
            org.apache.shindig.gadgets.spec.GadgetSpec shindigGadgetSpec = this.substituter.substitute(gadgetContext, this.shindigFactory.getGadgetSpec(gadgetContext));
            ModulePrefs prefs = shindigGadgetSpec.getModulePrefs();
            return GadgetSpec.gadgetSpec((URI)specUri).userPrefs(Iterables.transform((Iterable)shindigGadgetSpec.getUserPrefs(), (Function)UserPrefToUserPrefSpec.FUNCTION)).viewsNames(shindigGadgetSpec.getViews().keySet()).scrolling(prefs.getScrolling()).height(prefs.getHeight()).width(prefs.getWidth()).title(prefs.getTitle()).titleUrl(this.nullSafeToJavaUri(prefs.getTitleUrl())).thumbnail(this.nullSafeToJavaUri(prefs.getThumbnail())).author(prefs.getAuthor()).authorEmail(prefs.getAuthorEmail()).description(prefs.getDescription()).directoryTitle(prefs.getDirectoryTitle()).features(Collections.unmodifiableMap(Maps.transformValues((Map)prefs.getFeatures(), (Function)ShindigFeatureToFeature.FUNCTION))).unsupportedFeatureNames(this.getUnsupportedFeatureNames(prefs)).build();
        }
        catch (GadgetException e) {
            logger.warn("Error occurred while retrieving gadget spec for " + specUri);
            if (logger.isDebugEnabled()) {
                logger.warn("Full stack trace: ", (Throwable)e);
            }
            throw new GadgetParsingException((Throwable)e);
        }
    }

    private Iterable<String> getUnsupportedFeatureNames(ModulePrefs prefs) {
        LinkedList unsupportedFeatures = new LinkedList();
        LinkedList requiredFeatures = new LinkedList();
        Map shindigFeatures = prefs.getFeatures();
        for (Map.Entry shindigFeature : shindigFeatures.entrySet()) {
            if (!((org.apache.shindig.gadgets.spec.Feature)shindigFeature.getValue()).getRequired()) continue;
            requiredFeatures.add(shindigFeature.getKey());
        }
        this.gadgetFeatureRegistry.getFeatures(requiredFeatures, unsupportedFeatures);
        return Collections.unmodifiableCollection(unsupportedFeatures);
    }

    private URI nullSafeToJavaUri(org.apache.shindig.common.uri.Uri shindigUri) {
        if (shindigUri != null) {
            return shindigUri.toJavaUri();
        }
        return null;
    }

    private static enum UserPrefToUserPrefSpec implements Function<UserPref, UserPrefSpec>
    {
        FUNCTION;


        public UserPrefSpec apply(UserPref userPref) {
            LinkedHashMap<String, String> enumValues = new LinkedHashMap<String, String>();
            for (UserPref.EnumValuePair enumValue : userPref.getOrderedEnumValues()) {
                enumValues.put(enumValue.getValue(), enumValue.getDisplayValue());
            }
            return UserPrefSpec.userPrefSpec((String)userPref.getName()).displayName(userPref.getDisplayName() != null ? userPref.getDisplayName() : userPref.getName()).required(userPref.getRequired()).dataType(DataType.parse((String)userPref.getDataType().toString())).enumValues(Collections.unmodifiableMap(enumValues)).defaultValue(userPref.getDefaultValue()).build();
        }
    }

    private static enum ShindigFeatureToFeature implements Function<org.apache.shindig.gadgets.spec.Feature, Feature>
    {
        FUNCTION;


        public Feature apply(org.apache.shindig.gadgets.spec.Feature feature) {
            return new FeatureImpl(feature);
        }
    }
}

