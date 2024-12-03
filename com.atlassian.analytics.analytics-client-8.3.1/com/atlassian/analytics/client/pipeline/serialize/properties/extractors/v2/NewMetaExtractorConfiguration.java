/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.analytics.client.pipeline.serialize.properties.extractors.v2;

import com.atlassian.analytics.client.pipeline.serialize.properties.extractors.v2.NewMetaExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NewMetaExtractorConfiguration {
    @Bean
    public NewMetaExtractor newMetaExtractor() {
        return new NewMetaExtractor();
    }
}

