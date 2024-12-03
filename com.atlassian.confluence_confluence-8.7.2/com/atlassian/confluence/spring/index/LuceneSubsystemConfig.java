/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.ImportResource
 */
package com.atlassian.confluence.spring.index;

import com.atlassian.confluence.internal.index.config.ConditionalOnSearchPlatform;
import com.atlassian.confluence.search.SearchPlatform;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ConditionalOnSearchPlatform(value=SearchPlatform.LUCENE)
@ImportResource(value={"classpath:/index/luceneSubsystemContext.xml"})
public class LuceneSubsystemConfig {
}

