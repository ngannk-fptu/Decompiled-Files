/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.analytics.client.extractor;

import com.atlassian.analytics.client.extractor.FieldExtractor;
import com.atlassian.analytics.client.extractor.OldFieldExtractor;
import com.atlassian.analytics.client.extractor.PropertyExtractor;
import com.atlassian.analytics.client.extractor.nested.EventNameExcludingFieldExtractor;
import com.atlassian.analytics.client.extractor.nested.NewFieldExtractor;
import com.atlassian.analytics.client.extractor.nested.PropertyExtractorIsExcludedAwareFieldExtractor;
import com.atlassian.analytics.client.logger.EventAnonymizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FieldExtractorConfiguration {
    @Bean
    public FieldExtractor oldFieldExtractor(PropertyExtractor propertyExtractor) {
        return new OldFieldExtractor(propertyExtractor);
    }

    @Bean
    public FieldExtractor newFieldExtractor(PropertyExtractor propertyExtractor, EventAnonymizer eventAnonymizer) {
        return new PropertyExtractorIsExcludedAwareFieldExtractor(new EventNameExcludingFieldExtractor(new NewFieldExtractor(eventAnonymizer)), propertyExtractor);
    }
}

