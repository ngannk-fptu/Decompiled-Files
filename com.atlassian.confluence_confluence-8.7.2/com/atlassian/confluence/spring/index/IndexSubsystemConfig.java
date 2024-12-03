/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 *  org.springframework.context.annotation.ImportResource
 */
package com.atlassian.confluence.spring.index;

import com.atlassian.confluence.spring.index.LuceneSubsystemConfig;
import com.atlassian.confluence.spring.index.OpenSearchSubsystemConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource(value={"classpath:/index/indexingSubsystemContext.xml", "classpath:/index/searchSubsystemContext.xml"})
@Import(value={LuceneSubsystemConfig.class, OpenSearchSubsystemConfig.class})
public class IndexSubsystemConfig {
}

