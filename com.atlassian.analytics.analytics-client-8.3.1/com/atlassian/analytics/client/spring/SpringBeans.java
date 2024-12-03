/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 */
package com.atlassian.analytics.client.spring;

import com.atlassian.analytics.client.logger.AnalyticsLoggerConfiguration;
import com.atlassian.analytics.client.spring.product.BambooBeans;
import com.atlassian.analytics.client.spring.product.BitbucketBeans;
import com.atlassian.analytics.client.spring.product.ConfluenceBeans;
import com.atlassian.analytics.client.spring.product.CrowdBeans;
import com.atlassian.analytics.client.spring.product.FecruBeans;
import com.atlassian.analytics.client.spring.product.JiraBeans;
import com.atlassian.analytics.client.spring.shared.SharedBeans;
import com.atlassian.analytics.client.spring.shared.SharedExports;
import com.atlassian.analytics.client.spring.shared.SharedImports;
import com.atlassian.analytics.client.spring.shared.WhitelistModuleTypeBeans;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value={AnalyticsLoggerConfiguration.class, BambooBeans.class, BitbucketBeans.class, ConfluenceBeans.class, CrowdBeans.class, FecruBeans.class, JiraBeans.class, SharedBeans.class, SharedExports.class, SharedImports.class, WhitelistModuleTypeBeans.class})
public class SpringBeans {
}

