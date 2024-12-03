/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.impl.health.checks;

import com.atlassian.confluence.impl.health.HealthCheckMessage;
import com.atlassian.confluence.impl.health.checks.HomeHealthCheckFailure;
import org.checkerframework.checker.nullness.qual.NonNull;

public class HomeHealthCheckMessageFactory {
    public @NonNull HealthCheckMessage getMessage(HomeHealthCheckFailure failure) {
        HealthCheckMessage.Builder msgBuilder = new HealthCheckMessage.Builder();
        switch (failure.getReason()) {
            case NOT_CONFIGURED: {
                return msgBuilder.withHeading("We can't locate your Confluence home directory.").append("You'll need to specify a home directory. Confluence can't start without this.").lineBreak().append("See our documentation for more information on setting your home directory.").build();
            }
            case PATH_NOT_ABSOLUTE: {
                return msgBuilder.withHeading("Your Confluence home path isn't absolute").append("The path to your home directory ").tag("code", failure.getConfiguredHome()).append(" isn't an absolute path. This can cause problems when using Confluence, including page not found errors.").lineBreak().append("See our documentation for more information on setting your home directory.").build();
            }
            case NOT_A_DIR: {
                return msgBuilder.withHeading("Your Confluence home isn't a directory").append("The path specified to your home directory ").tag("code", failure.getConfiguredHome()).append(" is not a valid directory.").lineBreak().append("See our documentation for more information on setting your home directory.").build();
            }
            case CREATION_FAILED_WRITE_PERMISSION: {
                return msgBuilder.withHeading("We can't write to your home directory").append("Your dedicated Confluence user needs read, write and execute permissions to the Confluence home directory ").tag("code", failure.getConfiguredHome()).lineBreak().append("See our documentation for more information on setting your home directory.").build();
            }
        }
        throw new UnsupportedOperationException("Cannot generate a message for " + failure.getReason());
    }
}

