/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.collect.Maps
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.render.xhtml.view.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.JsonUtils;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XhtmlMacroTimeoutEvent;
import com.atlassian.confluence.content.render.xhtml.XhtmlTimeoutException;
import com.atlassian.confluence.content.render.xhtml.compatibility.LegacyV2RendererContextInitialiser;
import com.atlassian.confluence.content.render.xhtml.view.macro.MacroAsyncRenderWhitelist;
import com.atlassian.confluence.content.render.xhtml.view.macro.ViewMacroErrorPlaceholder;
import com.atlassian.confluence.content.render.xhtml.view.macro.ViewMacroWrapper;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.impl.content.render.xhtml.analytics.MarshallerMetricsCollector;
import com.atlassian.confluence.impl.content.render.xhtml.analytics.MarshallerMetricsCollectors;
import com.atlassian.confluence.impl.content.render.xhtml.analytics.MetricsCollectingMarshaller;
import com.atlassian.confluence.impl.macro.schema.MacroSchemaMigrator;
import com.atlassian.confluence.internal.diagnostics.MacroRenderingMonitor;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.macro.StreamableMacro;
import com.atlassian.confluence.macro.browser.MacroMetadataManager;
import com.atlassian.confluence.macro.browser.beans.MacroMetadata;
import com.atlassian.confluence.macro.count.MacroMetricsKey;
import com.atlassian.confluence.macro.xhtml.MacroManager;
import com.atlassian.confluence.macro.xhtml.MacroMigrationPoint;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.renderer.v2.macros.RadeoxCompatibilityMacro;
import com.atlassian.confluence.util.profiling.ConfluenceMonitoring;
import com.atlassian.confluence.util.profiling.Split;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.event.api.EventPublisher;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ViewMacroMarshaller
implements Marshaller<MacroDefinition> {
    private static final Logger log = LoggerFactory.getLogger(ViewMacroMarshaller.class);
    private static final Set<String> excludedMacros = Stream.of("col", "colgroup", "html", "html-xhtml", "td", "th", "tr", "thead", "tbody", "tfoot").collect(Collectors.toCollection(HashSet::new));
    private final MacroManager macroManager;
    private final Marshaller<MacroDefinition> unknownMacroMarshaller;
    private final ViewMacroErrorPlaceholder viewMacroErrorPlaceholder;
    private final LegacyV2RendererContextInitialiser legacyV2RendererConfigurationPropertySetter;
    private final EventPublisher eventPublisher;
    private final ConfluenceMonitoring monitoring;
    private final MacroAsyncRenderWhitelist macroAsyncRenderWhitelist;
    private final MacroMetadataManager macroMetadataManager;
    private final MacroSchemaMigrator macroSchemaMigrator;
    private final ViewMacroWrapper viewMacroWrapper;
    private final MacroRenderingMonitor macroRenderingMonitor;

    public ViewMacroMarshaller(MacroManager macroManager, Marshaller<MacroDefinition> unknownMacroMarshaller, ViewMacroErrorPlaceholder viewMacroErrorPlaceholder, LegacyV2RendererContextInitialiser legacyV2RendererConfigurationPropertySetter, EventPublisher eventPublisher, ConfluenceMonitoring monitoring, MacroAsyncRenderWhitelist macroAsyncRenderWhitelist, MacroMetadataManager macroMetadataManager, MacroSchemaMigrator macroSchemaMigrator, ViewMacroWrapper viewMacroWrapper, MacroRenderingMonitor macroRenderingMonitor) {
        this.macroManager = macroManager;
        this.unknownMacroMarshaller = unknownMacroMarshaller;
        this.viewMacroErrorPlaceholder = viewMacroErrorPlaceholder;
        this.legacyV2RendererConfigurationPropertySetter = legacyV2RendererConfigurationPropertySetter;
        this.eventPublisher = eventPublisher;
        this.monitoring = monitoring;
        this.macroAsyncRenderWhitelist = macroAsyncRenderWhitelist;
        this.macroMetadataManager = macroMetadataManager;
        this.macroSchemaMigrator = macroSchemaMigrator;
        this.viewMacroWrapper = viewMacroWrapper;
        this.macroRenderingMonitor = macroRenderingMonitor;
    }

    @Override
    public Streamable marshal(MacroDefinition macroDefinition, ConversionContext conversionContext) throws XhtmlException {
        Macro macro = this.macroManager.getMacroByName(macroDefinition.getName());
        MacroMetricsKey metricsKey = MacroMetricsKey.createFrom(macroDefinition, macro);
        MarshallerMetricsCollector metricsCollector = MarshallerMetricsCollectors.metricsCollector(conversionContext, metricsKey);
        Marshaller<MacroDefinition> timedMarshaller = MetricsCollectingMarshaller.forMarshaller(metricsCollector, (object, conversionContext1) -> this.marshalInternal(macroDefinition, conversionContext1, macro, metricsCollector));
        try (Split ignored = this.startTimer(macroDefinition);){
            Streamable streamable = timedMarshaller.marshal(macroDefinition, conversionContext);
            return streamable;
        }
    }

    private @NonNull Split startTimer(MacroDefinition macroDefinition) {
        return this.monitoring.startSplit(ViewMacroMarshaller.class.getSimpleName(), Collections.singletonMap("macroName", macroDefinition.getName()));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Streamable marshalInternal(MacroDefinition possiblyUnmigratedMacroDefinition, ConversionContext context, Macro macro, MarshallerMetricsCollector metricsCollector) throws XhtmlException {
        if (macro == null) {
            return this.unknownMacroMarshaller.marshal(possiblyUnmigratedMacroDefinition, context);
        }
        MacroMetadata metadata = this.macroMetadataManager.getMacroMetadataByName(possiblyUnmigratedMacroDefinition.getName());
        MacroDefinition macroDefinition = metadata != null && !metadata.getFormDetails().getExcludedSchemaMigrationPoints().contains((Object)MacroMigrationPoint.VIEW) ? this.macroSchemaMigrator.migrateSchemaIfNecessary(possiblyUnmigratedMacroDefinition, context) : possiblyUnmigratedMacroDefinition;
        Map<String, String> macroParameters = ViewMacroMarshaller.copyOf(macroDefinition.getParameters());
        macroParameters.put(": = | RAW | = :", RadeoxCompatibilityMacro.constructRadeoxRawParams(macroParameters));
        if (macroDefinition.getDefaultParameterValue() != null) {
            macroParameters.put("0", macroDefinition.getDefaultParameterValue());
        }
        this.legacyV2RendererConfigurationPropertySetter.initialise(context);
        String origMacroName = ViewMacroMarshaller.prepareContext(context, macro, macroDefinition, metadata, this.macroAsyncRenderWhitelist);
        try {
            boolean shouldWrap = "display".equals(context.getOutputType()) && !excludedMacros.contains(macroDefinition.getName());
            Streamable result = this.executeMacro(context, macro, metricsCollector, macroDefinition, macroParameters, shouldWrap);
            context.checkTimeout();
            Streamable streamable = result;
            return streamable;
        }
        catch (Throwable t) {
            Streamable streamable = this.handleMacroExecutionException(t, context, macroDefinition);
            return streamable;
        }
        finally {
            ViewMacroMarshaller.cleanContext(context, origMacroName);
        }
    }

    private Streamable handleMacroExecutionException(Throwable t, ConversionContext context, MacroDefinition macroDefinition) throws XhtmlTimeoutException {
        if (t instanceof XhtmlTimeoutException) {
            XhtmlTimeoutException e = (XhtmlTimeoutException)t;
            ContentEntityObject entity = context.getEntity();
            this.eventPublisher.publish((Object)new XhtmlMacroTimeoutEvent(this, macroDefinition.getName(), entity, e.getAllowedTimeInSeconds(), e.getExceededTimeInMilliseconds()));
            log.warn(e.getDetailedTimeoutMessage("Executing the '" + macroDefinition.getName() + "' macro"));
            log.debug(e.getMessage(), (Throwable)e);
            throw e;
        }
        if (t instanceof MacroExecutionException) {
            MacroExecutionException e = (MacroExecutionException)t;
            log.warn("Exception executing macro: {}, with message: {}", (Object)macroDefinition.getName(), (Object)e.getMessage());
            log.debug(e.getMessage(), (Throwable)e);
            return this.errorPlaceHolder(macroDefinition, e.getMessage());
        }
        log.error("Error rendering macro: " + macroDefinition.getName(), t);
        log.debug(t.getMessage(), t);
        return this.errorPlaceHolder(macroDefinition, t.getMessage());
    }

    private static String prepareContext(ConversionContext context, Macro macro, MacroDefinition macroDefinition, MacroMetadata metadata, MacroAsyncRenderWhitelist macroAsyncRenderWhitelist) {
        String origMacroName = (String)context.getProperty("macroName");
        context.setProperty("macroName", macroDefinition.getName());
        context.setProperty("macroDefinition", macroDefinition);
        context.setProperty("macroMetadata", metadata);
        if (!macroAsyncRenderWhitelist.isAsyncRenderSafe(macroDefinition, macro.getClass())) {
            context.disableAsyncRenderSafe();
        }
        return origMacroName;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Streamable executeMacro(ConversionContext context, Macro macro, MarshallerMetricsCollector metricsCollector, MacroDefinition macroDefinition, Map<String, String> macroParameters, boolean shouldWrap) throws MacroExecutionException {
        ContentEntityObject entity = context.getEntity();
        if (entity instanceof Page) {
            ViewMacroMarshaller.addMacroIsOnSpaceHomeMetric(metricsCollector, ((Page)entity).isHomePage());
        } else {
            ViewMacroMarshaller.addMacroIsOnSpaceHomeMetric(metricsCollector, false);
        }
        MacroRenderingMonitor.MacroRendering macroRendering = new MacroRenderingMonitor.MacroRendering(context, macroDefinition, macro);
        try {
            String macroOutput;
            this.macroRenderingMonitor.start(macroRendering);
            if (macro instanceof StreamableMacro) {
                StreamableMacro streamableMacro = (StreamableMacro)macro;
                Streamable countingBody = ViewMacroMarshaller.bodyCountingStreamable(metricsCollector, macroDefinition);
                macroOutput = Streamables.writeToString(streamableMacro.executeToStream(macroParameters, countingBody, context));
            } else {
                String bodyText = StringUtils.defaultString((String)macroDefinition.getBodyText());
                ViewMacroMarshaller.addMacroBodySizeMetric(metricsCollector, bodyText.length());
                macroOutput = macro.execute(macroParameters, bodyText, context);
            }
            if (shouldWrap && !JsonUtils.isJsonFormat(macroOutput)) {
                macroOutput = this.viewMacroWrapper.wrap(context, macro.getOutputType(), macroOutput, macroDefinition);
            }
            Streamable streamable = Streamables.from(macroOutput);
            return streamable;
        }
        finally {
            this.macroRenderingMonitor.stop(macroRendering);
        }
    }

    private static Streamable bodyCountingStreamable(MarshallerMetricsCollector metricsCollector, MacroDefinition macroDefinition) {
        return Streamables.withCountingCharacters(macroDefinition.getBodyStream(), value -> ViewMacroMarshaller.addMacroBodySizeMetric(metricsCollector, value));
    }

    private static void cleanContext(ConversionContext conversionContext, String origMacroName) {
        if (conversionContext != null) {
            if (origMacroName == null) {
                conversionContext.removeProperty("macroName");
            } else {
                conversionContext.setProperty("macroName", origMacroName);
            }
        }
    }

    private Streamable errorPlaceHolder(MacroDefinition macroDefinition, String exceptionMessage) {
        return Streamables.from(this.viewMacroErrorPlaceholder.create(macroDefinition, exceptionMessage));
    }

    private static <K, V> Map<K, V> copyOf(Map<K, V> map) {
        return map == null ? Maps.newLinkedHashMap() : Maps.newLinkedHashMap(map);
    }

    private static void addMacroBodySizeMetric(MarshallerMetricsCollector metricsCollector, long value) {
        metricsCollector.addCustomMetric("macroBodySizeChars", value);
    }

    private static void addMacroIsOnSpaceHomeMetric(MarshallerMetricsCollector metricsCollector, boolean isOnSpaceHome) {
        metricsCollector.addCustomMetric("isOnSpaceHome", isOnSpaceHome ? 1L : 0L);
    }

    public static boolean addMacroNameToExclusionSet(String macroName) {
        return excludedMacros.add(macroName);
    }
}

