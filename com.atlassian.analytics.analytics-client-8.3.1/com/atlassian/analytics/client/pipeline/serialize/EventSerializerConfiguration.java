/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 *  org.springframework.context.annotation.Primary
 */
package com.atlassian.analytics.client.pipeline.serialize;

import com.atlassian.analytics.client.extractor.FieldExtractor;
import com.atlassian.analytics.client.extractor.FieldExtractorConfiguration;
import com.atlassian.analytics.client.pipeline.serialize.DefaultEventSerializer;
import com.atlassian.analytics.client.pipeline.serialize.EventSerializer;
import com.atlassian.analytics.client.pipeline.serialize.properties.ExtractionSupplier;
import com.atlassian.analytics.client.pipeline.serialize.properties.MetaPropertyExtractorConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

@Configuration
@Import(value={MetaPropertyExtractorConfiguration.class, FieldExtractorConfiguration.class})
public class EventSerializerConfiguration {
    @Primary
    @Bean(value={"flatEventSerializer", "eventSerializer"})
    EventSerializer flatEventSerializer(@Qualifier(value="oldFieldExtractor") FieldExtractor oldFieldExtractor, @Qualifier(value="mauAwareExtractionSupplier") ExtractionSupplier oldExtractionSupplier) {
        return new DefaultEventSerializer(oldFieldExtractor, oldExtractionSupplier);
    }

    @Bean
    EventSerializer nestableEventSerializer(@Qualifier(value="newFieldExtractor") FieldExtractor newFieldExtractor, @Qualifier(value="newExtractionSupplier") ExtractionSupplier newExtractionSupplier) {
        return new DefaultEventSerializer(newFieldExtractor, newExtractionSupplier);
    }
}

