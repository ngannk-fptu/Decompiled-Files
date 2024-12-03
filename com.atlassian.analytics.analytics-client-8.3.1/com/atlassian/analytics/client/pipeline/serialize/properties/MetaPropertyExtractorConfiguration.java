/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 */
package com.atlassian.analytics.client.pipeline.serialize.properties;

import com.atlassian.analytics.client.pipeline.serialize.properties.ExtractionSupplier;
import com.atlassian.analytics.client.pipeline.serialize.properties.MauAwareExtractorSupplier;
import com.atlassian.analytics.client.pipeline.serialize.properties.NewExtractorSupplier;
import com.atlassian.analytics.client.pipeline.serialize.properties.extractors.general.GeneralExtractorConfiguration;
import com.atlassian.analytics.client.pipeline.serialize.properties.extractors.general.MetaPropertyExtractor;
import com.atlassian.analytics.client.pipeline.serialize.properties.extractors.mau.MauAwareMetaPropertyExtractorConfiguration;
import com.atlassian.analytics.client.pipeline.serialize.properties.extractors.mau.MauAwarePropertyExtractor;
import com.atlassian.analytics.client.pipeline.serialize.properties.extractors.v2.NewMetaExtractor;
import com.atlassian.analytics.client.pipeline.serialize.properties.extractors.v2.NewMetaExtractorConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value={MauAwareMetaPropertyExtractorConfiguration.class, GeneralExtractorConfiguration.class, NewMetaExtractorConfiguration.class})
public class MetaPropertyExtractorConfiguration {
    private MetaPropertyExtractor metaPropertyExtractor;
    private MauAwarePropertyExtractor mauAwarePropertyExtractor;
    private NewMetaExtractor newMetaExtractor;

    public MetaPropertyExtractorConfiguration(MetaPropertyExtractor metaPropertyExtractor, MauAwarePropertyExtractor mauAwarePropertyExtractor, NewMetaExtractor newMetaExtractor) {
        this.metaPropertyExtractor = metaPropertyExtractor;
        this.mauAwarePropertyExtractor = mauAwarePropertyExtractor;
        this.newMetaExtractor = newMetaExtractor;
    }

    @Bean
    ExtractionSupplier mauAwareExtractionSupplier() {
        return new MauAwareExtractorSupplier(this.metaPropertyExtractor, this.mauAwarePropertyExtractor);
    }

    @Bean
    ExtractionSupplier newExtractionSupplier() {
        return new NewExtractorSupplier(this.metaPropertyExtractor, this.newMetaExtractor);
    }
}

