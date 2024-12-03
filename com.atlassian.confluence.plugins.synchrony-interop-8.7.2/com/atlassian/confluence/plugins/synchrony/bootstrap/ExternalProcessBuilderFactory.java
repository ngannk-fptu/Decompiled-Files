/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.utils.process.ExternalProcessBuilder
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.synchrony.bootstrap;

import com.atlassian.utils.process.ExternalProcessBuilder;
import org.springframework.stereotype.Component;

@Component
public class ExternalProcessBuilderFactory {
    public ExternalProcessBuilder createBuilder() {
        return new ExternalProcessBuilder();
    }
}

