/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  javax.activation.DataSource
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.dailysummary.content;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.activation.DataSource;
import org.apache.commons.lang3.StringUtils;

public class SummaryEmailPanelData {
    private final boolean hasContent;
    private final List<DataSource> imageDatasources;
    private final String webPanelKey;

    SummaryEmailPanelData(Builder builder) {
        this.hasContent = builder.hasContent;
        this.imageDatasources = builder.imageDatasources.build();
        this.webPanelKey = builder.webPanelKey;
    }

    public boolean hasContent() {
        return this.hasContent;
    }

    public List<DataSource> getImageDatasources() {
        return this.imageDatasources;
    }

    public String getWebPanelKey() {
        return this.webPanelKey;
    }

    public static Builder builder(String webPanelKey) {
        return new Builder(webPanelKey);
    }

    public static class Builder {
        private boolean hasContent;
        private ImmutableList.Builder<DataSource> imageDatasources = ImmutableList.builder();
        private String webPanelKey;

        public SummaryEmailPanelData build() {
            return new SummaryEmailPanelData(this);
        }

        public Builder(String webPanelKey) {
            if (StringUtils.isBlank((CharSequence)webPanelKey)) {
                throw new IllegalArgumentException("Web panel key should not be null for SummaryEmailPanelData");
            }
            this.webPanelKey = webPanelKey;
        }

        public Builder hasContent(boolean hasContent) {
            this.hasContent = hasContent;
            return this;
        }

        public Builder addImageDataSources(Iterable<DataSource> imageDatasources) {
            for (DataSource ds : imageDatasources) {
                this.imageDatasources.add((Object)ds);
            }
            return this;
        }

        public Builder addImageDataSource(DataSource imageDatasource) {
            this.imageDatasources.add((Object)imageDatasource);
            return this;
        }
    }
}

