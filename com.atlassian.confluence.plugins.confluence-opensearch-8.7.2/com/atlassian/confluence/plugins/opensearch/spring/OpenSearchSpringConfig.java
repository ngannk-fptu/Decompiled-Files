/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.ImportResource
 */
package com.atlassian.confluence.plugins.opensearch.spring;

import com.atlassian.confluence.plugins.opensearch.spring.ConditionalForOpenSearch;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ConditionalForOpenSearch
@ImportResource(value={"/META-INF/spring/opensearch-context.xml"})
public class OpenSearchSpringConfig {
}

