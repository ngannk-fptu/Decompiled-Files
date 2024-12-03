/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  io.atlassian.util.concurrent.LazyReference
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.parsers;

import com.atlassian.plugin.parsers.SafeModeCommandLineArguments;
import com.atlassian.plugin.parsers.SafeModeCommandLineArgumentsFactory;
import com.google.common.annotations.VisibleForTesting;
import io.atlassian.util.concurrent.LazyReference;
import javax.annotation.Nonnull;

public class DefaultSafeModeCommandLineArgumentsFactory
implements SafeModeCommandLineArgumentsFactory {
    private static final String PARAMETER_SYSTEM_PROPERTY = "atlassian.plugins.startup.options";
    private LazyReference<SafeModeCommandLineArguments> safeModeCommandLineArguments = new LazyReference<SafeModeCommandLineArguments>(){

        protected SafeModeCommandLineArguments create() throws Exception {
            return new SafeModeCommandLineArguments(System.getProperty(DefaultSafeModeCommandLineArgumentsFactory.PARAMETER_SYSTEM_PROPERTY, ""));
        }
    };

    @Nonnull
    @VisibleForTesting
    String getParameterSystemProperty() {
        return PARAMETER_SYSTEM_PROPERTY;
    }

    @Override
    public SafeModeCommandLineArguments get() {
        return (SafeModeCommandLineArguments)this.safeModeCommandLineArguments.get();
    }
}

