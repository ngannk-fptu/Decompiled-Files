/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.oauth2.client.api.ClientConfiguration
 *  com.atlassian.oauth2.client.api.ClientToken
 *  com.atlassian.oauth2.client.api.lib.event.FlowRequestCompletedEvent
 *  com.atlassian.oauth2.client.api.lib.event.FlowRequestStartedEvent
 *  com.atlassian.oauth2.client.api.lib.event.FlowRequestSuccessfullyCompletedEvent
 *  com.atlassian.oauth2.client.api.lib.event.FlowRequestUnsuccessfullyCompletedEvent
 *  com.atlassian.oauth2.client.api.lib.flow.FlowRequest
 *  com.atlassian.oauth2.client.api.lib.flow.FlowRequestError
 *  com.atlassian.oauth2.client.api.lib.flow.FlowRequestService
 *  com.atlassian.oauth2.client.api.lib.flow.FlowResult
 *  javax.annotation.Nonnull
 *  javax.servlet.http.HttpSession
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.client.lib.flow;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.oauth2.client.api.ClientConfiguration;
import com.atlassian.oauth2.client.api.ClientToken;
import com.atlassian.oauth2.client.api.lib.event.FlowRequestCompletedEvent;
import com.atlassian.oauth2.client.api.lib.event.FlowRequestStartedEvent;
import com.atlassian.oauth2.client.api.lib.event.FlowRequestSuccessfullyCompletedEvent;
import com.atlassian.oauth2.client.api.lib.event.FlowRequestUnsuccessfullyCompletedEvent;
import com.atlassian.oauth2.client.api.lib.flow.FlowRequest;
import com.atlassian.oauth2.client.api.lib.flow.FlowRequestError;
import com.atlassian.oauth2.client.api.lib.flow.FlowRequestService;
import com.atlassian.oauth2.client.api.lib.flow.FlowResult;
import com.atlassian.oauth2.client.lib.flow.FlowRequestData;
import com.atlassian.oauth2.client.lib.flow.FlowRequestImpl;
import com.atlassian.oauth2.client.lib.flow.FlowResultImpl;
import com.atlassian.oauth2.client.lib.flow.RedirectUrlResolver;
import com.atlassian.oauth2.client.lib.flow.ServletFlowRequestService;
import com.atlassian.oauth2.client.properties.SystemProperty;
import com.atlassian.oauth2.client.util.ClientHttpsValidator;
import com.atlassian.oauth2.common.IdGenerator;
import com.atlassian.oauth2.common.concurrent.StripedMonitors;
import com.atlassian.oauth2.common.session.SessionStore;
import java.time.Clock;
import java.time.Duration;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionBasedFlowRequestService
implements FlowRequestService,
ServletFlowRequestService {
    private static final Logger logger = LoggerFactory.getLogger(SessionBasedFlowRequestService.class);
    private static final String COMMON_STORE_PREFIX = "com.atlassian.oauth2.client.lib.flow.SessionBasedFlowRequestService";
    private final RedirectUrlResolver redirectUrlResolver;
    private final IdGenerator idGenerator;
    private final IdGenerator stateGenerator;
    private final ClientHttpsValidator clientHttpsValidator;
    private final EventPublisher eventPublisher;
    private final SessionStore<FlowRequestData> requestByIdStore;
    private final SessionStore<FlowRequestData> requestByStateStore;
    private final SessionStore<FlowResultImpl> resultByIdStore;
    private final SessionStore<FlowState> flowStateByIdStore;
    private final StripedMonitors<HttpSession> monitors = new StripedMonitors(SystemProperty.DEFAULT_MONITOR_STRIPE_COUNT.getValue());

    public SessionBasedFlowRequestService(@Nonnull RedirectUrlResolver redirectUrlResolver, @Nonnull IdGenerator idGenerator, @Nonnull IdGenerator stateGenerator, @Nonnull ClientHttpsValidator clientHttpsValidator, @Nonnull Clock clock, @Nonnull EventPublisher eventPublisher) {
        this.redirectUrlResolver = redirectUrlResolver;
        this.idGenerator = idGenerator;
        this.stateGenerator = stateGenerator;
        this.clientHttpsValidator = clientHttpsValidator;
        this.eventPublisher = eventPublisher;
        Duration maxClockSkew = SystemProperty.MAX_CLOCK_SKEW.getValue();
        Duration maxClientDelay = maxClockSkew.plus(SystemProperty.MAX_CLIENT_DELAY.getValue());
        Duration maxOauthFlowDelay = maxClockSkew.plus(SystemProperty.MAX_SERVER_DELAY.getValue());
        this.requestByIdStore = new SessionStore("com.atlassian.oauth2.client.lib.flow.SessionBasedFlowRequestService.requestById", clock, maxClientDelay);
        this.requestByStateStore = new SessionStore("com.atlassian.oauth2.client.lib.flow.SessionBasedFlowRequestService.requestByState", clock, maxOauthFlowDelay);
        this.resultByIdStore = new SessionStore("com.atlassian.oauth2.client.lib.flow.SessionBasedFlowRequestService.resultById", clock, maxClientDelay);
        this.flowStateByIdStore = new SessionStore("com.atlassian.oauth2.client.lib.flow.SessionBasedFlowRequestService.flowState", clock, maxOauthFlowDelay);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nonnull
    public FlowRequest createFlowRequest(@Nonnull HttpSession session, @Nonnull ClientConfiguration clientConfiguration, @Nonnull Function<String, String> clientRedirectUrlProvider) {
        this.clientHttpsValidator.assertSecure(clientConfiguration);
        String flowRequestId = this.idGenerator.generate();
        FlowRequestData data = new FlowRequestData(clientConfiguration, clientRedirectUrlProvider.apply(flowRequestId), flowRequestId, null);
        Object object = this.monitors.getMonitor(session);
        synchronized (object) {
            this.transition(session, flowRequestId, Objects::isNull, FlowState.CREATED_BY_CLIENT);
            this.requestByIdStore.store(session, flowRequestId, data);
            this.eventPublisher.publish((Object)new FlowRequestStartedEvent(flowRequestId));
            return new FlowRequestImpl(flowRequestId, this.redirectUrlResolver.getInitFlowUrl(flowRequestId));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nonnull
    public FlowResult getFlowResult(@Nonnull HttpSession session, @Nonnull String flowRequestId) {
        Object object = this.monitors.getMonitor(session);
        synchronized (object) {
            this.transition(session, flowRequestId, FlowState.HAS_RESULT::equals, null);
            return this.resultByIdStore.remove(session, flowRequestId).orElseThrow(IllegalArgumentException::new);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Nonnull
    public FlowRequestData fetchFlowRequestDataById(@Nonnull HttpSession session, @Nonnull String flowRequestId) {
        Object object = this.monitors.getMonitor(session);
        synchronized (object) {
            this.transition(session, flowRequestId, FlowState.CREATED_BY_CLIENT::equals, FlowState.FETCHED_BY_ID);
            FlowRequestData data = this.requestByIdStore.remove(session, flowRequestId).orElseThrow(IllegalArgumentException::new);
            FlowRequestData dataWithState = new FlowRequestData(data.getClientConfiguration(), data.getClientRedirectUrl(), data.getFlowRequestId(), this.stateGenerator.generate());
            this.requestByStateStore.store(session, dataWithState.getState(), dataWithState);
            return dataWithState;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Nonnull
    public FlowRequestData fetchFlowRequestDataByState(@Nonnull HttpSession session, @Nonnull String state) {
        Object object = this.monitors.getMonitor(session);
        synchronized (object) {
            FlowRequestData data = this.requestByStateStore.remove(session, state).orElseThrow(IllegalArgumentException::new);
            this.transition(session, data.getFlowRequestId(), FlowState.FETCHED_BY_ID::equals, FlowState.FETCHED_BY_STATE);
            return data;
        }
    }

    @Override
    @Deprecated
    public void updateFlowRequest(@Nonnull HttpSession session, @Nonnull String flowRequestId, @Nonnull ClientToken clientToken) throws IllegalArgumentException {
        FlowRequestCompletedEvent event = new FlowRequestCompletedEvent(flowRequestId);
        this.updateFlowRequest(session, flowRequestId, clientToken, event);
    }

    @Override
    @Deprecated
    public void updateFlowRequest(@Nonnull HttpSession session, @Nonnull String flowRequestId, @Nonnull FlowRequestError error) throws IllegalArgumentException {
        FlowRequestCompletedEvent event = new FlowRequestCompletedEvent(flowRequestId);
        this.updateFlowRequest(session, flowRequestId, error, event);
    }

    @Override
    public void updateFlowRequest(@Nonnull HttpSession session, @Nonnull FlowRequestData flowRequestData, @Nonnull ClientToken clientToken) throws IllegalArgumentException {
        String flowRequestId = flowRequestData.getFlowRequestId();
        ClientConfiguration config = flowRequestData.getClientConfiguration();
        FlowRequestSuccessfullyCompletedEvent event = new FlowRequestSuccessfullyCompletedEvent(flowRequestId, config.getClientId(), config.getProviderType().name());
        this.updateFlowRequest(session, flowRequestId, clientToken, (FlowRequestCompletedEvent)event);
    }

    @Override
    public void updateFlowRequest(@Nonnull HttpSession session, @Nonnull FlowRequestData flowRequestData, @Nonnull FlowRequestError error) throws IllegalArgumentException {
        String flowRequestId = flowRequestData.getFlowRequestId();
        ClientConfiguration config = flowRequestData.getClientConfiguration();
        FlowRequestUnsuccessfullyCompletedEvent event = new FlowRequestUnsuccessfullyCompletedEvent(flowRequestId, config.getClientId(), config.getProviderType().name(), error.getMessage());
        this.updateFlowRequest(session, flowRequestId, error, (FlowRequestCompletedEvent)event);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void updateFlowRequest(@Nonnull HttpSession session, @Nonnull String flowRequestId, @Nonnull ClientToken clientToken, @Nonnull FlowRequestCompletedEvent event) throws IllegalArgumentException {
        Object object = this.monitors.getMonitor(session);
        synchronized (object) {
            this.transition(session, flowRequestId, FlowState.FETCHED_BY_STATE::equals, FlowState.HAS_RESULT);
            this.resultByIdStore.store(session, flowRequestId, new FlowResultImpl(clientToken));
        }
        this.eventPublisher.publish((Object)event);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void updateFlowRequest(@Nonnull HttpSession session, @Nonnull String flowRequestId, @Nonnull FlowRequestError error, @Nonnull FlowRequestCompletedEvent event) throws IllegalArgumentException {
        Object object = this.monitors.getMonitor(session);
        synchronized (object) {
            this.transition(session, flowRequestId, state -> state == FlowState.FETCHED_BY_ID || state == FlowState.FETCHED_BY_STATE, FlowState.HAS_RESULT);
            this.resultByIdStore.store(session, flowRequestId, new FlowResultImpl(error));
        }
        this.eventPublisher.publish((Object)event);
    }

    private void transition(HttpSession session, String flowRequestId, Predicate<FlowState> expectedState, FlowState newState) {
        try {
            if (newState == null) {
                logger.debug("Getting flow result from a session with an id [{}] and request id [{}]", (Object)session.getId(), (Object)flowRequestId);
            } else {
                logger.debug("Making transition for an entry to new state [{}] from a session with an id [{}] and request id [{}]", new Object[]{newState.name(), session.getId(), flowRequestId});
            }
            this.flowStateByIdStore.store(session, flowRequestId, expectedState, newState);
        }
        catch (RuntimeException e) {
            this.cleanup(session, flowRequestId);
            throw e;
        }
    }

    private void cleanup(HttpSession session, String flowRequestId) {
        this.requestByIdStore.removeIfPresent(session, flowRequestId);
        this.resultByIdStore.removeIfPresent(session, flowRequestId);
        this.flowStateByIdStore.removeIfPresent(session, flowRequestId);
    }

    static enum FlowState {
        CREATED_BY_CLIENT,
        FETCHED_BY_ID,
        FETCHED_BY_STATE,
        HAS_RESULT;

    }
}

