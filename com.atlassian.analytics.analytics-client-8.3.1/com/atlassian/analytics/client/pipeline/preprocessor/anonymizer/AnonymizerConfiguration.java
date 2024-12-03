/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.analytics.client.pipeline.preprocessor.anonymizer;

import com.atlassian.analytics.client.logger.EventAnonymizer;
import com.atlassian.analytics.client.pipeline.preprocessor.anonymizer.DefaultMetaAnonymizer;
import com.atlassian.analytics.client.pipeline.preprocessor.anonymizer.MetaAnonymizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AnonymizerConfiguration {
    @Bean
    public MetaAnonymizer defaultMetaAnonymizer(EventAnonymizer eventAnonymizer) {
        return new DefaultMetaAnonymizer(eventAnonymizer);
    }
}

