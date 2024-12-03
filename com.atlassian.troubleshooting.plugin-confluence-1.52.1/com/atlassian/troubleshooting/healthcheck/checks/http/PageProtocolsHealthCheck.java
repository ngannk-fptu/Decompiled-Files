/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.healthcheck.checks.http;

import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheck;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthStatus;
import com.atlassian.troubleshooting.healthcheck.SupportHealthStatusBuilder;
import com.atlassian.troubleshooting.healthcheck.checks.eol.ClockFactory;
import com.atlassian.troubleshooting.healthcheck.checks.http.NetworkPerformanceStatisticsService;
import com.atlassian.troubleshooting.healthcheck.checks.http.ProtocolsEvent;
import com.atlassian.troubleshooting.healthcheck.checks.http.ProtocolsEventProvider;
import java.io.Serializable;
import java.time.Clock;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class PageProtocolsHealthCheck
implements SupportHealthCheck {
    static final int TC_THRESHOLD_WARNING = 5000;
    static final int TC_THRESHOLD_IGNORE = 2500;
    private static final long EVENT_TTL = Duration.ofMinutes(10L).toMillis();
    private static final Logger LOG = LoggerFactory.getLogger(PageProtocolsHealthCheck.class);
    private final Clock clock;
    private final ProtocolsEventProvider protocolsEventProvider;
    private final SupportHealthStatusBuilder supportHealthStatusBuilder;
    private final NetworkPerformanceStatisticsService networkPerformanceStatisticsService;

    @Autowired
    public PageProtocolsHealthCheck(ClockFactory clockFactory, ProtocolsEventProvider protocolsEventProvider, SupportHealthStatusBuilder supportHealthStatusBuilder, NetworkPerformanceStatisticsService networkPerformanceStatisticsService) {
        this.clock = clockFactory.makeClock();
        this.protocolsEventProvider = protocolsEventProvider;
        this.supportHealthStatusBuilder = supportHealthStatusBuilder;
        this.networkPerformanceStatisticsService = networkPerformanceStatisticsService;
    }

    @Override
    public boolean isNodeSpecific() {
        return false;
    }

    @Override
    public SupportHealthStatus check() {
        SupportHealthStatus.Severity severity = PageProtocolsHealthCheck.determineSeverity(this.networkPerformanceStatisticsService.getRecentTransferCosts());
        if (severity == SupportHealthStatus.Severity.UNDEFINED) {
            return this.passed();
        }
        Optional<ProtocolsEvent> maybeObsoleteProtocols = this.protocolsEventProvider.getEventWithObsoleteProtocols();
        if (!maybeObsoleteProtocols.isPresent()) {
            LOG.debug("Health check passed, no obsolete protocols registered");
            return this.passed();
        }
        ProtocolsEvent obsoleteProtocols = maybeObsoleteProtocols.get();
        Optional<ProtocolsEvent> maybeModernProtocols = this.protocolsEventProvider.getEventWithModernProtocols();
        if (!maybeModernProtocols.isPresent()) {
            LOG.debug("Health check failed as {}, only obsolete protocols registered", (Object)severity);
            return this.failed(severity, obsoleteProtocols);
        }
        if (this.isRecent(obsoleteProtocols)) {
            LOG.debug("Health check failed as {}, obsolete protocols used recently", (Object)severity);
            return this.failed(severity, obsoleteProtocols);
        }
        ProtocolsEvent modernProtocols = maybeModernProtocols.get();
        if (modernProtocols.isAfter(obsoleteProtocols)) {
            LOG.debug("Health check passed, modern protocols used more recently than obsolete ones");
            return this.passed();
        }
        LOG.debug("Health check failed as {}, most recent protocols were obsolete", (Object)severity);
        return this.failed(severity, obsoleteProtocols);
    }

    private static SupportHealthStatus.Severity determineSeverity(Collection<Integer> recentTransferCosts) {
        if (recentTransferCosts.size() < 10) {
            LOG.debug("Health check will not be run, not enough data about the transfer costs.");
            return SupportHealthStatus.Severity.UNDEFINED;
        }
        Map<SupportHealthStatus.Severity, Integer> bucketedCostPercentages = PageProtocolsHealthCheck.bucketPercentages(recentTransferCosts);
        LOG.debug("Transfer cost statistics: {}", bucketedCostPercentages);
        for (SupportHealthStatus.Severity severity : Arrays.asList(SupportHealthStatus.Severity.WARNING, SupportHealthStatus.Severity.MINOR)) {
            if (bucketedCostPercentages.getOrDefault((Object)severity, 0) <= 10) continue;
            return severity;
        }
        LOG.debug("Health check not needed, {}% transfer costs in the fast bucket", (Object)bucketedCostPercentages.get((Object)SupportHealthStatus.Severity.UNDEFINED));
        return SupportHealthStatus.Severity.UNDEFINED;
    }

    static Map<SupportHealthStatus.Severity, Integer> bucketPercentages(Collection<Integer> recentTransferCosts) {
        Map<SupportHealthStatus.Severity, List<Integer>> bucketedCosts = recentTransferCosts.stream().collect(Collectors.groupingBy(PageProtocolsHealthCheck::transferCostThreshold));
        return bucketedCosts.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> 100 * ((List)e.getValue()).size() / recentTransferCosts.size()));
    }

    private static SupportHealthStatus.Severity transferCostThreshold(int tc) {
        if (tc < 2500) {
            return SupportHealthStatus.Severity.UNDEFINED;
        }
        if (tc < 5000) {
            return SupportHealthStatus.Severity.MINOR;
        }
        return SupportHealthStatus.Severity.WARNING;
    }

    private SupportHealthStatus failed(SupportHealthStatus.Severity severity, ProtocolsEvent e) {
        Optional<String> navigationProtocol = e.getNavigationProtocol().map(PageProtocolsHealthCheck::knownProtocolToUpperCase);
        HashSet<String> prettyResourceProtocols = new HashSet<String>(e.getResourceProtocols());
        if (prettyResourceProtocols.isEmpty()) {
            prettyResourceProtocols.add("unknown");
        } else if (prettyResourceProtocols.size() > 1) {
            prettyResourceProtocols.remove("unknown");
        }
        String resourceProtocols = prettyResourceProtocols.stream().map(PageProtocolsHealthCheck::knownProtocolToUpperCase).collect(Collectors.joining(", "));
        return this.supportHealthStatusBuilder.buildStatus(this, severity, "healthcheck.http.warning", new Serializable[]{(Serializable)((Object)navigationProtocol.orElse("unknown")), resourceProtocols});
    }

    private static String knownProtocolToUpperCase(String protocol) {
        return protocol.equals("unknown") ? protocol : protocol.toUpperCase();
    }

    private SupportHealthStatus passed() {
        return this.supportHealthStatusBuilder.ok(this, "healthcheck.http.ok", new Serializable[0]);
    }

    private boolean isRecent(ProtocolsEvent protocolsEvent) {
        return this.clock.millis() - protocolsEvent.getTimestamp() <= EVENT_TTL;
    }
}

