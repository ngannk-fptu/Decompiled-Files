/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 */
package com.atlassian.migration.agent;

import com.atlassian.migration.agent.AppBeanConfiguration;
import com.atlassian.migration.agent.AttachmentStepExecutorBeanConfiguration;
import com.atlassian.migration.agent.ExportBeanConfiguration;
import com.atlassian.migration.agent.ExportOsgiServiceBeans;
import com.atlassian.migration.agent.GlobalEntitiesStepExecutorBeanConfiguration;
import com.atlassian.migration.agent.ImportedOsgiServiceBeans;
import com.atlassian.migration.agent.MapiBeanConfiguration;
import com.atlassian.migration.agent.PrcBeanConfiguration;
import com.atlassian.migration.agent.PreflightCheckBeanConfiguration;
import com.atlassian.migration.agent.SpaceStepExecutorBeanConfiguration;
import com.atlassian.migration.agent.rest.IncompatibleDarkFeatureExceptionMapper;
import com.atlassian.migration.agent.rest.InvalidPlanExceptionMapper;
import com.atlassian.migration.agent.rest.JsonMappingExceptionMapper;
import com.atlassian.migration.agent.rest.QueryFailedExceptionMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value={ImportedOsgiServiceBeans.class, ExportOsgiServiceBeans.class, AppBeanConfiguration.class, PreflightCheckBeanConfiguration.class, ExportBeanConfiguration.class, GlobalEntitiesStepExecutorBeanConfiguration.class, AttachmentStepExecutorBeanConfiguration.class, SpaceStepExecutorBeanConfiguration.class, MapiBeanConfiguration.class, PrcBeanConfiguration.class, IncompatibleDarkFeatureExceptionMapper.class, InvalidPlanExceptionMapper.class, JsonMappingExceptionMapper.class, QueryFailedExceptionMapper.class})
public class MainSpringConfiguration {
}

