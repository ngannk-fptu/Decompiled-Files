/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 */
package com.atlassian.diagnostics.internal.spring;

import com.atlassian.diagnostics.internal.perflog.PerfLogSpringConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value={PerfLogSpringConfig.class})
public class RootSpringConfig {
}

