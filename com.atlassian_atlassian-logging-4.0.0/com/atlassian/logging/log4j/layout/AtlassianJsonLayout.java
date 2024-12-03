/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.core.LogEvent
 *  org.apache.logging.log4j.core.config.plugins.Plugin
 *  org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute
 *  org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory
 *  org.apache.logging.log4j.core.layout.AbstractStringLayout
 *  org.apache.logging.log4j.core.layout.AbstractStringLayout$Builder
 *  org.apache.logging.log4j.core.util.Builder
 */
package com.atlassian.logging.log4j.layout;

import com.atlassian.logging.log4j.layout.json.JsonDataProvider;
import com.atlassian.logging.log4j.layout.json.JsonLayoutHelper;
import java.nio.charset.Charset;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;

@Plugin(name="AtlassianJsonLayout", category="Core", elementType="layout", printObject=true)
public class AtlassianJsonLayout
extends AbstractStringLayout {
    protected final JsonLayoutHelper layoutHelper;

    public String toString() {
        return "AtlassianJsonLayout{layoutHelper=" + this.layoutHelper + '}';
    }

    private AtlassianJsonLayout(Charset charset, JsonLayoutHelper layoutHelper) {
        super(charset);
        this.layoutHelper = layoutHelper;
        this.layoutHelper.initialise();
    }

    @PluginBuilderFactory
    public static <B extends Builder<B>> B newBuilder() {
        return (B)((Object)((Builder)new Builder().asBuilder()));
    }

    public String toSerializable(LogEvent event) {
        return this.layoutHelper.format(event);
    }

    public static class Builder<B extends Builder<B>>
    extends AbstractStringLayout.Builder<B>
    implements org.apache.logging.log4j.core.util.Builder<AtlassianJsonLayout> {
        @PluginBuilderAttribute
        private String suppressedFields = "";
        @PluginBuilderAttribute
        private String dataProvider = "";
        @PluginBuilderAttribute
        private boolean filteringApplied = true;
        @PluginBuilderAttribute
        private int minimumLines = 6;
        @PluginBuilderAttribute
        private boolean showEludedSummary = false;
        @PluginBuilderAttribute
        private String filteredFrames = "";
        @PluginBuilderAttribute
        private String environmentConfigFilename = "";
        @PluginBuilderAttribute
        private boolean includeLocation = false;
        @PluginBuilderAttribute
        private String additionalFields = "";

        public B setSuppressedFields(String suppressedFields) {
            this.suppressedFields = suppressedFields;
            return (B)((Object)((Builder)this.asBuilder()));
        }

        public B setDataProvider(String dataProvider) {
            this.dataProvider = dataProvider;
            return (B)((Object)((Builder)this.asBuilder()));
        }

        public B setFilteringApplied(boolean filteringApplied) {
            this.filteringApplied = filteringApplied;
            return (B)((Object)((Builder)this.asBuilder()));
        }

        public B setMinimumLines(int minimumLines) {
            this.minimumLines = minimumLines;
            return (B)((Object)((Builder)this.asBuilder()));
        }

        public B setShowEludedSummary(boolean showEludedSummary) {
            this.showEludedSummary = showEludedSummary;
            return (B)((Object)((Builder)this.asBuilder()));
        }

        public B setFilteredFrames(String filteredFrames) {
            this.filteredFrames = filteredFrames;
            return (B)((Object)((Builder)this.asBuilder()));
        }

        public B setEnvironmentConfigFilename(String environmentConfigFilename) {
            this.environmentConfigFilename = environmentConfigFilename;
            return (B)((Object)((Builder)this.asBuilder()));
        }

        public B setIncludeLocation(boolean includeLocation) {
            this.includeLocation = includeLocation;
            return (B)((Object)((Builder)this.asBuilder()));
        }

        public B setAdditionalFields(String additionalFields) {
            this.additionalFields = additionalFields;
            return (B)((Object)((Builder)this.asBuilder()));
        }

        public AtlassianJsonLayout build() {
            JsonLayoutHelper layoutHelper = new JsonLayoutHelper();
            if (!this.suppressedFields.isEmpty()) {
                layoutHelper.setSuppressedFields(this.suppressedFields);
            }
            if (!this.dataProvider.isEmpty()) {
                this.setDataProviderObject(layoutHelper, this.dataProvider);
            }
            layoutHelper.setFilteringApplied(this.filteringApplied);
            layoutHelper.setMinimumLines(this.minimumLines);
            layoutHelper.setShowEludedSummary(this.showEludedSummary);
            if (!this.filteredFrames.isEmpty()) {
                layoutHelper.setFilteredFrames(this.filteredFrames);
            }
            if (!this.environmentConfigFilename.isEmpty()) {
                layoutHelper.setEnvironmentConfigFilename(this.environmentConfigFilename);
            }
            layoutHelper.setIncludeLocation(this.includeLocation);
            if (!this.additionalFields.isEmpty()) {
                layoutHelper.setAdditionalFields(this.additionalFields);
            }
            return new AtlassianJsonLayout(this.getCharset(), layoutHelper);
        }

        private void setDataProviderObject(JsonLayoutHelper layoutHelper, String dataProviderClazz) {
            try {
                layoutHelper.setDataProvider((JsonDataProvider)Class.forName(dataProviderClazz).newInstance());
            }
            catch (ClassNotFoundException e) {
                throw new RuntimeException("JsonDataProvider implementation not found", e);
            }
            catch (IllegalAccessException | InstantiationException e) {
                throw new RuntimeException("Failed to instantiate JsonDataProvider implementation", e);
            }
        }
    }
}

