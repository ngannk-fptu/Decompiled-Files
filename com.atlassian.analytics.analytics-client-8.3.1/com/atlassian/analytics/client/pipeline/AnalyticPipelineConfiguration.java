/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory
 *  com.atlassian.sal.api.web.context.HttpContext
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 *  org.springframework.context.annotation.Primary
 */
package com.atlassian.analytics.client.pipeline;

import com.atlassian.analytics.client.logger.AnalyticsLogger;
import com.atlassian.analytics.client.pipeline.AnalyticsPipeline;
import com.atlassian.analytics.client.pipeline.DefaultAnalyticsPipeline;
import com.atlassian.analytics.client.pipeline.PipelineExecutionService;
import com.atlassian.analytics.client.pipeline.SwitchingAnalyticsPipeline;
import com.atlassian.analytics.client.pipeline.predicate.AllEventsPredicate;
import com.atlassian.analytics.client.pipeline.predicate.AnalyticsEventInterfacePredicate;
import com.atlassian.analytics.client.pipeline.preprocessor.EventPreprocessor;
import com.atlassian.analytics.client.pipeline.preprocessor.EventPreprocessorConfiguration;
import com.atlassian.analytics.client.pipeline.serialize.EventSerializer;
import com.atlassian.analytics.client.pipeline.serialize.EventSerializerConfiguration;
import com.atlassian.analytics.client.report.EventReporter;
import com.atlassian.analytics.client.session.SessionIdProvider;
import com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory;
import com.atlassian.sal.api.web.context.HttpContext;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

@Configuration
@Import(value={EventSerializerConfiguration.class, EventPreprocessorConfiguration.class})
public class AnalyticPipelineConfiguration {
    @Bean
    public PipelineExecutionService pipelineExecutionService(ThreadLocalDelegateExecutorFactory threadLocalDelegateExecutorFactory) {
        return new PipelineExecutionService(threadLocalDelegateExecutorFactory);
    }

    @Bean
    public AnalyticsPipeline whitelistAwareAnalyticPipeline(@Qualifier(value="flatEventSerializer") EventSerializer eventSerializer, @Qualifier(value="whitelistingAndAnonymizingEventProcessor") EventPreprocessor eventPreprocessor, EventReporter eventReporter, SessionIdProvider sessionIdProvider, AnalyticsLogger analyticsLogger, HttpContext httpContext, PipelineExecutionService pipelineExecutionService) {
        return new DefaultAnalyticsPipeline(eventReporter, eventSerializer, eventPreprocessor, sessionIdProvider, analyticsLogger, httpContext, pipelineExecutionService, new AllEventsPredicate());
    }

    @Bean
    public AnalyticsPipeline annotableAnalyticPipeline(@Qualifier(value="nestableEventSerializer") EventSerializer eventSerializer, @Qualifier(value="anonymizingEventProcessor") EventPreprocessor eventPreprocessor, EventReporter eventReporter, SessionIdProvider sessionIdProvider, AnalyticsLogger analyticsLogger, ThreadLocalDelegateExecutorFactory threadLocalDelegateExecutorFactory, HttpContext httpContext, PipelineExecutionService pipelineExecutionService) {
        return new DefaultAnalyticsPipeline(eventReporter, eventSerializer, eventPreprocessor, sessionIdProvider, analyticsLogger, httpContext, pipelineExecutionService, new AnalyticsEventInterfacePredicate());
    }

    @Bean
    @Primary
    public AnalyticsPipeline switchingAnalyticPipeline(@Qualifier(value="whitelistAwareAnalyticPipeline") AnalyticsPipeline oldAnalyticPipeline, @Qualifier(value="annotableAnalyticPipeline") AnalyticsPipeline newAnalyticPipeline) {
        return new SwitchingAnalyticsPipeline(Arrays.asList(newAnalyticPipeline, oldAnalyticPipeline));
    }
}

