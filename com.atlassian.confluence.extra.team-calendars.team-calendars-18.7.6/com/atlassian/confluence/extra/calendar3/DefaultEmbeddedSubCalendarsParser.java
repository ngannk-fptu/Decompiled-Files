/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.macro.browser.beans.MacroMetadata
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.plugin.descriptor.CustomMacroModuleDescriptor
 *  com.atlassian.confluence.plugin.descriptor.XhtmlMacroModuleDescriptor
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 *  com.atlassian.confluence.xhtml.api.MacroDefinitionHandler
 *  com.atlassian.confluence.xhtml.api.XhtmlContent
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.renderer.RenderContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.extra.calendar3.CalendarMacro;
import com.atlassian.confluence.extra.calendar3.EmbeddedSubCalendarsParser;
import com.atlassian.confluence.macro.browser.beans.MacroMetadata;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.plugin.descriptor.CustomMacroModuleDescriptor;
import com.atlassian.confluence.plugin.descriptor.XhtmlMacroModuleDescriptor;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.confluence.xhtml.api.MacroDefinitionHandler;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.renderer.RenderContext;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultEmbeddedSubCalendarsParser
implements EmbeddedSubCalendarsParser {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultEmbeddedSubCalendarsParser.class);
    private final PluginAccessor pluginAccessor;
    private final XhtmlContent xhtmlContent;

    @Autowired
    public DefaultEmbeddedSubCalendarsParser(@ComponentImport PluginAccessor pluginAccessor, @ComponentImport XhtmlContent xhtmlContent) {
        this.pluginAccessor = pluginAccessor;
        this.xhtmlContent = xhtmlContent;
    }

    @Override
    public Collection<String> getEmbeddedSubCalendarIds(ContentEntityObject contentEntity) {
        if (this.isContentTypeSupported(contentEntity)) {
            ICalendarMacroDefinitionHandler calendarMacroDefinitionHandler = this.createCalMacDefHandler();
            try {
                this.xhtmlContent.handleMacroDefinitions(contentEntity.getBodyAsString(), (ConversionContext)new DefaultConversionContext((RenderContext)contentEntity.toPageContext()), (MacroDefinitionHandler)calendarMacroDefinitionHandler);
                return calendarMacroDefinitionHandler.getCollectedSubCalendarIds();
            }
            catch (XhtmlException xhtmlError) {
                LOG.warn(String.format("Unable to parse sub-calendars embedded in %s", contentEntity), (Throwable)xhtmlError);
            }
        }
        return Collections.emptySet();
    }

    public ICalendarMacroDefinitionHandler createCalMacDefHandler() {
        return new CalendarMacroDefinitionHandler(this.getCalendarMacroNames());
    }

    private boolean isContentTypeSupported(ContentEntityObject contentEntity) {
        return contentEntity instanceof AbstractPage || contentEntity instanceof Comment;
    }

    private Collection<String> getCalendarMacroNames() {
        List xhtmlMacroModuleDescriptors;
        HashSet<String> macroNames = new HashSet<String>();
        List customMacroModuleDescriptors = this.pluginAccessor.getEnabledModuleDescriptorsByClass(CustomMacroModuleDescriptor.class);
        if (null != customMacroModuleDescriptors) {
            for (CustomMacroModuleDescriptor customMacroModuleDescriptor : customMacroModuleDescriptors) {
                if (!CalendarMacro.class.equals((Object)customMacroModuleDescriptor.getModuleClass())) continue;
                macroNames.addAll(this.getMacroNamesFromPluginModuleDescriptor((AbstractModuleDescriptor)customMacroModuleDescriptor, customMacroModuleDescriptor.getMacroMetadata()));
            }
        }
        if (null != (xhtmlMacroModuleDescriptors = this.pluginAccessor.getEnabledModuleDescriptorsByClass(XhtmlMacroModuleDescriptor.class))) {
            for (XhtmlMacroModuleDescriptor xhtmlMacroModuleDescriptor : xhtmlMacroModuleDescriptors) {
                if (!CalendarMacro.class.equals((Object)xhtmlMacroModuleDescriptor.getModuleClass())) continue;
                macroNames.addAll(this.getMacroNamesFromPluginModuleDescriptor((AbstractModuleDescriptor)xhtmlMacroModuleDescriptor, xhtmlMacroModuleDescriptor.getMacroMetadata()));
            }
        }
        return macroNames;
    }

    private Set<String> getMacroNamesFromPluginModuleDescriptor(AbstractModuleDescriptor abstractModuleDescriptor, MacroMetadata macroMetadata) {
        HashSet<String> macroNames = new HashSet<String>();
        macroNames.add(abstractModuleDescriptor.getName());
        if (null != macroMetadata) {
            macroNames.add(macroMetadata.getMacroName());
            Set macroAliases = macroMetadata.getAliases();
            if (null != macroAliases) {
                macroNames.addAll(macroAliases);
            }
        }
        return macroNames;
    }

    private static class CalendarMacroDefinitionHandler
    implements ICalendarMacroDefinitionHandler {
        private final Collection<String> calendarMacroNames;
        private final Set<String> collectedSubCalendarIds;

        public CalendarMacroDefinitionHandler(Collection<String> calendarMacroNames) {
            this.calendarMacroNames = calendarMacroNames;
            this.collectedSubCalendarIds = new HashSet<String>();
        }

        @Override
        public Set<String> getCollectedSubCalendarIds() {
            return this.collectedSubCalendarIds;
        }

        public void handle(MacroDefinition macroDefinition) {
            Map macroParameters;
            String[] subCalendarIds;
            if (this.calendarMacroNames.contains(macroDefinition.getName()) && null != (subCalendarIds = StringUtils.split(StringUtils.defaultString(StringUtils.defaultIfEmpty((String)(macroParameters = macroDefinition.getParameters()).get("id"), (String)macroParameters.get("subCalendars"))), ",; ")) && subCalendarIds.length > 0) {
                this.collectedSubCalendarIds.addAll(Arrays.asList(subCalendarIds));
            }
        }
    }

    static interface ICalendarMacroDefinitionHandler
    extends MacroDefinitionHandler {
        public Set<String> getCollectedSubCalendarIds();
    }
}

