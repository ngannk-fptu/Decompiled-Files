/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.util.tomcat.TomcatConfigHelper
 */
package com.atlassian.confluence.impl.health.checks.rules;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.impl.health.ErrorMessageProvider;
import com.atlassian.confluence.impl.health.checks.rules.AbstractHealthCheckRule;
import com.atlassian.confluence.internal.health.JohnsonEventType;
import com.atlassian.confluence.util.tomcat.TomcatConfigHelper;
import com.atlassian.confluence.web.UrlBuilder;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TomcatHttpMaxThreadsRule
extends AbstractHealthCheckRule {
    @VisibleForTesting
    static final URL KB_URL = UrlBuilder.createURL("https://confluence.atlassian.com/confkb/startup-check-http-maxthreads-configuration-939930122.html?utm_source=Install&utm_medium=in-product&utm_campaign=csseng_fy18_q3_server_confluence_errorstate");
    @VisibleForTesting
    static final String FAILURE_CAUSE = "http-max-threads-incorrect-configuration";
    @VisibleForTesting
    static final String UNCONFIGURED_FAILURE_MESSAGE_KEY = "johnson.message.unconfigured.http.max.threads";
    @VisibleForTesting
    static final String MINIMUM_NOT_SATISFIED_FAILURE_MESSAGE_KEY = "johnson.message.minimum.not.satisfied.http.max.threads";
    @VisibleForTesting
    static final int RECOMMENDED_HTTP_MAX_THREAD_SIZE = 48;
    private final TomcatConfigHelper tomcatConfigHelper;

    public TomcatHttpMaxThreadsRule(TomcatConfigHelper tomcatConfigHelper, ErrorMessageProvider errorMessageProvider) {
        super(errorMessageProvider, KB_URL, FAILURE_CAUSE, JohnsonEventType.TOMCAT);
        this.tomcatConfigHelper = Objects.requireNonNull(tomcatConfigHelper);
    }

    @Override
    protected Optional<String> doValidation() {
        List allMaxHttpThreads = this.tomcatConfigHelper.getAllMaxHttpThreads();
        if (!allMaxHttpThreads.stream().allMatch(Optional::isPresent)) {
            return Optional.of(this.getErrorMessage(UNCONFIGURED_FAILURE_MESSAGE_KEY, 48));
        }
        return allMaxHttpThreads.stream().map(Optional::get).anyMatch(maxHttpThreads -> maxHttpThreads < 48) ? Optional.of(this.getErrorMessage(MINIMUM_NOT_SATISFIED_FAILURE_MESSAGE_KEY, 48)) : Optional.empty();
    }
}

