/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  org.springframework.context.annotation.Bean
 */
package com.atlassian.diagnostics.internal.perflog;

import com.atlassian.diagnostics.internal.perflog.IpdLogFileReader;
import com.atlassian.diagnostics.internal.perflog.IpdLogService;
import com.atlassian.diagnostics.internal.perflog.PerformanceLogServletUtils;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.auth.LoginUriProvider;
import org.springframework.context.annotation.Bean;

public class PerfLogSpringConfig {
    @Bean
    public PerformanceLogServletUtils performanceServletUtils(ApplicationProperties applicationProperties, LoginUriProvider loginUriProvider) {
        return new PerformanceLogServletUtils(applicationProperties, loginUriProvider);
    }

    @Bean
    public IpdLogService ipdLogReaderService(IpdLogFileReader ipdLogFileReader) {
        return new IpdLogService(ipdLogFileReader);
    }

    @Bean
    public IpdLogFileReader logFileReader(ApplicationProperties applicationProperties) {
        return new IpdLogFileReader(applicationProperties);
    }
}

