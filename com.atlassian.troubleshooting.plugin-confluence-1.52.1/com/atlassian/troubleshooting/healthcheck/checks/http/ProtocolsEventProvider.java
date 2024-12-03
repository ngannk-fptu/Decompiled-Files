/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.troubleshooting.healthcheck.checks.http;

import com.atlassian.troubleshooting.healthcheck.checks.http.ProtocolsEvent;
import com.atlassian.troubleshooting.healthcheck.checks.http.UserAgentStringUtils;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtocolsEventProvider
implements Consumer<ProtocolsEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProtocolsEventProvider.class);
    private static final Set<String> WARNING_PROTOCOLS = ImmutableSet.of((Object)"http/1.0", (Object)"http/1.1");
    private final AtomicReference<ProtocolsEvent> lastEventWithObsoleteProtocols = new AtomicReference();
    private final AtomicReference<ProtocolsEvent> lastEventWithModernProtocols = new AtomicReference();
    private final AtomicInteger totalEvents = new AtomicInteger();
    private final AtomicInteger eventsWithObsoleteProtocols = new AtomicInteger();

    @Override
    public void accept(ProtocolsEvent protocolsEvent) {
        if (ProtocolsEventProvider.hasNoProtocolInformation(protocolsEvent)) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Ignoring an event without protocol information: {}", (Object)protocolsEvent);
            }
            return;
        }
        this.totalEvents.incrementAndGet();
        if (ProtocolsEventProvider.containsObsoleteProtocols(protocolsEvent)) {
            boolean supportsModernProtocols = UserAgentStringUtils.supportsModernProtocols(protocolsEvent);
            if (supportsModernProtocols) {
                this.eventsWithObsoleteProtocols.incrementAndGet();
                this.lastEventWithObsoleteProtocols.set(protocolsEvent);
            }
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Event received ({}/{}): obsolete protocols, supports modern {}, {}", new Object[]{this.eventsWithObsoleteProtocols.get(), this.totalEvents.get(), supportsModernProtocols, protocolsEvent});
            }
        } else {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Event received ({}/{}): modern protocols {}", new Object[]{this.eventsWithObsoleteProtocols.get(), this.totalEvents.get(), protocolsEvent});
            }
            this.lastEventWithModernProtocols.set(protocolsEvent);
        }
    }

    private static boolean hasNoProtocolInformation(ProtocolsEvent protocolsEvent) {
        Predicate<String> isKnownProtocol = proto -> !proto.equals("unknown");
        Optional<String> knownNavigationProtocol = protocolsEvent.getNavigationProtocol().filter(isKnownProtocol);
        Collection knownResourceProtocols = protocolsEvent.getResourceProtocols().stream().filter(isKnownProtocol).collect(Collectors.toList());
        return !knownNavigationProtocol.isPresent() && knownResourceProtocols.isEmpty();
    }

    private static boolean containsObsoleteProtocols(ProtocolsEvent protocolsEvent) {
        return WARNING_PROTOCOLS.contains(protocolsEvent.getNavigationProtocol().orElse(null)) || WARNING_PROTOCOLS.stream().anyMatch(p -> protocolsEvent.getResourceProtocols().contains(p));
    }

    public Optional<ProtocolsEvent> getEventWithObsoleteProtocols() {
        if (this.totalEvents.get() == 0) {
            return Optional.empty();
        }
        int percentageOfObsoleteProtocols = this.eventsWithObsoleteProtocols.get() * 100 / this.totalEvents.get();
        LOGGER.trace("Percentage of obsolete protocols: {}", (Object)percentageOfObsoleteProtocols);
        return percentageOfObsoleteProtocols < 5 ? Optional.empty() : Optional.ofNullable(this.lastEventWithObsoleteProtocols.get());
    }

    public Optional<ProtocolsEvent> getEventWithModernProtocols() {
        return Optional.ofNullable(this.lastEventWithModernProtocols.get());
    }
}

