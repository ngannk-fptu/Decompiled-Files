/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.license.LicenseHandler
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.whisper.plugin.api.HashCalculator
 *  com.atlassian.whisper.plugin.api.Message
 *  com.atlassian.whisper.plugin.api.MessageFetchService
 *  com.atlassian.whisper.plugin.api.MessagesExpiryService
 *  com.atlassian.whisper.plugin.api.MessagesManager
 *  com.atlassian.whisper.plugin.api.UserMessage
 *  com.atlassian.whisper.plugin.api.WhisperStatusService
 *  com.google.common.annotations.VisibleForTesting
 *  javax.inject.Inject
 *  javax.inject.Named
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.core.UriBuilder
 *  org.codehaus.jettison.json.JSONArray
 *  org.codehaus.jettison.json.JSONException
 *  org.codehaus.jettison.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.whisper.plugin.impl;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.whisper.plugin.api.HashCalculator;
import com.atlassian.whisper.plugin.api.Message;
import com.atlassian.whisper.plugin.api.MessageFetchService;
import com.atlassian.whisper.plugin.api.MessagesExpiryService;
import com.atlassian.whisper.plugin.api.MessagesManager;
import com.atlassian.whisper.plugin.api.UserMessage;
import com.atlassian.whisper.plugin.api.WhisperStatusService;
import com.atlassian.whisper.plugin.fetch.events.FetchDisabledAnalyticsEvent;
import com.atlassian.whisper.plugin.fetch.events.FetchFailedAnalyticsEvent;
import com.atlassian.whisper.plugin.fetch.events.FetchSuccessAnalyticsEvent;
import com.atlassian.whisper.plugin.impl.signature.SignatureVerificationException;
import com.atlassian.whisper.plugin.impl.signature.SignatureVerifier;
import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@ExportAsService
public class DefaultMessageFetchService
implements MessageFetchService {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultMessageFetchService.class);
    private static final Properties PROPERTIES = new Properties();
    @VisibleForTesting
    static final String FETCH_TIMEOUT_PROPERTY = "atlassian.whisper.fetch.timeout";
    private static final int DEFAULT_FETCH_TIMEOUT_MS = 30000;
    static final String DISABLE_SIGNATURES_VERIFICATIONS_PROPERTY = "atlassian.whisper.disable.signatures.verification";
    public static final String ETAG_PREFIX = "com.atlassian.whisper.plugin:ETag";
    private final RequestFactory<Request<?, Response>> requestFactory;
    private final HashCalculator hashCalculator;
    private final MessagesManager messagesManager;
    private final EventPublisher eventPublisher;
    private final MessagesExpiryService messagesExpiryService;
    private final PluginSettings pluginSettings;
    private final LicenseHandler licenseHandler;
    private final ApplicationProperties applicationProperties;
    private final WhisperStatusService whisperStatusService;
    private final SignatureVerifier signatureVerifier;

    @Inject
    public DefaultMessageFetchService(@ComponentImport RequestFactory<Request<?, Response>> requestFactory, @ComponentImport EventPublisher eventPublisher, @ComponentImport PluginSettingsFactory pluginSettingsFactory, @ComponentImport LicenseHandler licenseHandler, @ComponentImport ApplicationProperties applicationProperties, HashCalculator hashCalculator, MessagesManager messagesManager, MessagesExpiryService messagesExpiryService, WhisperStatusService whisperStatusService, SignatureVerifier signatureVerifier) {
        this.requestFactory = requestFactory;
        this.licenseHandler = licenseHandler;
        this.applicationProperties = applicationProperties;
        this.hashCalculator = hashCalculator;
        this.messagesManager = messagesManager;
        this.eventPublisher = eventPublisher;
        this.messagesExpiryService = messagesExpiryService;
        this.pluginSettings = pluginSettingsFactory.createGlobalSettings();
        this.whisperStatusService = whisperStatusService;
        this.signatureVerifier = signatureVerifier;
    }

    public boolean fetch() {
        return this.handleMessagesExpiry(this.doFetch());
    }

    private boolean doFetch() {
        if (!this.whisperStatusService.isEnabled()) {
            LOG.debug("Cannot fetch messages because whisper is disabled");
            this.eventPublisher.publish((Object)new FetchDisabledAnalyticsEvent());
            return false;
        }
        LOG.debug("Fetching latest messages and mappings.");
        try {
            long fetchTS = System.currentTimeMillis();
            Optional<JSONObject> responseMessages = this.doRequest("messages", false);
            Optional<JSONObject> responseUserMessages = this.doRequest("usermessages", responseMessages.isPresent());
            if (responseUserMessages.isPresent() && !responseMessages.isPresent()) {
                responseMessages = this.doRequest("messages", true);
            }
            if (!responseMessages.isPresent() && !responseUserMessages.isPresent()) {
                LOG.debug("Done fetching, delivery returned not modified response.");
                this.eventPublisher.publish((Object)new FetchSuccessAnalyticsEvent(false, 0, 0, System.currentTimeMillis() - fetchTS, 0L));
                return true;
            }
            JSONArray jsonMessages = responseMessages.get().getJSONArray("items");
            JSONArray jsonMappings = responseUserMessages.get().getJSONArray("items");
            long signatureVerificationTS = System.currentTimeMillis();
            this.verifyMessagesSignaturesIfEnabled(responseMessages.get());
            long storageTS = System.currentTimeMillis();
            Set<UserMessage> usersMessages = this.storeMessagesAndMappings(jsonMessages, jsonMappings);
            long finishTS = System.currentTimeMillis();
            long fetchTime = signatureVerificationTS - fetchTS;
            long signatureVerificationTime = storageTS - signatureVerificationTS;
            long storageTime = finishTS - storageTS;
            long totalTime = finishTS - fetchTS;
            LOG.debug("Done fetching: {} messages, {} mappings. Fetch time = {}ms, storage time = {}ms, signature verification time = {}ms, total time = {}ms", new Object[]{jsonMessages.length(), usersMessages.size(), fetchTime, storageTime, signatureVerificationTime, totalTime});
            this.eventPublisher.publish((Object)new FetchSuccessAnalyticsEvent(true, jsonMessages.length(), usersMessages.size(), fetchTime, storageTime));
            return true;
        }
        catch (ResponseException e) {
            LOG.debug("Error fetching.", (Throwable)e);
            this.eventPublisher.publish((Object)FetchFailedAnalyticsEvent.fetch());
        }
        catch (JSONException e) {
            LOG.debug("Error parsing.", (Throwable)e);
            this.eventPublisher.publish((Object)FetchFailedAnalyticsEvent.parse());
        }
        catch (SignatureVerificationException e) {
            LOG.debug("Error verifying messages signatures", (Throwable)e);
            this.eventPublisher.publish((Object)FetchFailedAnalyticsEvent.signaturesVerification());
        }
        catch (Exception e) {
            LOG.debug("General error.", (Throwable)e);
            this.eventPublisher.publish((Object)FetchFailedAnalyticsEvent.general());
        }
        return false;
    }

    private void verifyMessagesSignaturesIfEnabled(JSONObject messagesResponse) throws JSONException, SignatureVerificationException {
        if (!this.isSignaturesVerificationDisabled()) {
            JSONArray jsonMessages = messagesResponse.getJSONArray("items");
            JSONObject jsonMessagesSignatures = messagesResponse.getJSONObject("signatures");
            this.signatureVerifier.verifyMessages(jsonMessages, jsonMessagesSignatures);
        } else {
            LOG.debug("Signatures verification disabled");
        }
    }

    private Set<UserMessage> storeMessagesAndMappings(JSONArray jsonMessages, JSONArray jsonMappings) throws JSONException {
        this.messagesManager.removeAllMessagesAndMappings();
        this.insertMessages(jsonMessages);
        return this.updateMappings(jsonMappings);
    }

    private void insertMessages(JSONArray jsonMessages) throws JSONException {
        for (int i = 0; i < jsonMessages.length(); ++i) {
            JSONObject jsonMessage = jsonMessages.getJSONObject(i);
            String messageId = (String)jsonMessage.get("id");
            Message message = new Message(messageId, jsonMessage.toString());
            this.messagesManager.addMessage(message);
        }
    }

    private Set<UserMessage> updateMappings(JSONArray jsonMappings) throws JSONException {
        HashSet<UserMessage> usersMessages = new HashSet<UserMessage>();
        for (int i = 0; i < jsonMappings.length(); ++i) {
            JSONObject userMapping = jsonMappings.getJSONObject(i);
            String userHash = userMapping.getString("userHash");
            JSONArray jsonUserMessages = userMapping.getJSONArray("messageIds");
            for (int k = 0; k < jsonUserMessages.length(); ++k) {
                usersMessages.add(new UserMessage(userHash, jsonUserMessages.getString(k)));
            }
        }
        this.messagesManager.addUsersMessages(usersMessages);
        return usersMessages;
    }

    private boolean handleMessagesExpiry(boolean success) {
        try {
            if (success) {
                this.messagesExpiryService.notifySuccessfulFetch();
            } else if (this.messagesExpiryService.areMessagesExpired()) {
                LOG.debug("Removing expired messages");
                this.messagesManager.removeAllMessagesAndMappings();
                this.messagesExpiryService.reset();
                this.eventPublisher.publish((Object)FetchFailedAnalyticsEvent.expired());
            }
        }
        catch (Exception ex) {
            LOG.debug("Failed handling messages expiry", (Throwable)ex);
        }
        return success;
    }

    String getRequestUri(String type) {
        return UriBuilder.fromUri((String)this.getEndpoint()).path(type).queryParam("server", new Object[]{Optional.ofNullable(this.licenseHandler.getServerId()).orElse("")}).queryParam("instancehash", new Object[]{this.hashCalculator.calculateInstanceHash()}).queryParam("sen", this.licenseHandler.getAllSupportEntitlementNumbers().toArray()).queryParam("platform", new Object[]{this.applicationProperties.getPlatformId()}).queryParam("version", new Object[]{this.applicationProperties.getVersion()}).queryParam("whisperVersion", new Object[]{this.getWhisperVersion()}).build(new Object[0]).toString();
    }

    String getEndpoint() {
        return System.getProperty("atlassian.whisper.delivery.endpoint", PROPERTIES.getProperty("whisper.delivery.endpoint"));
    }

    String getWhisperVersion() {
        return PROPERTIES.getProperty("whisper.version", "");
    }

    private int getFetchTimeout() {
        return Integer.getInteger(FETCH_TIMEOUT_PROPERTY, 30000);
    }

    private boolean isSignaturesVerificationDisabled() {
        return Boolean.getBoolean(DISABLE_SIGNATURES_VERIFICATIONS_PROPERTY);
    }

    private Optional<JSONObject> doRequest(String type, boolean noCache) throws ResponseException, JSONException {
        String typeETag = String.format("%s:%s", ETAG_PREFIX, type);
        String requestUri = this.getRequestUri(type);
        LOG.debug("Requesting {} {} cache headers.", (Object)requestUri, (Object)(noCache ? "without" : "with"));
        Request request = this.requestFactory.createRequest(Request.MethodType.GET, requestUri);
        if (!noCache) {
            Optional.ofNullable((String)this.pluginSettings.get(typeETag)).ifPresent(etag -> request.addHeader("If-None-Match", etag));
        } else {
            request.addHeader("Cache-Control", "no-cache");
        }
        int fetchTimeout = this.getFetchTimeout();
        request.setSoTimeout(fetchTimeout);
        request.setConnectionTimeout(fetchTimeout);
        Optional optional = (Optional)request.executeAndReturn(response -> {
            if (response.getStatusCode() == Response.Status.NOT_MODIFIED.getStatusCode()) {
                return Optional.empty();
            }
            this.pluginSettings.put(typeETag, (Object)response.getHeader("ETag"));
            return Optional.of(response.getResponseBodyAsString());
        });
        return optional.isPresent() ? Optional.of(new JSONObject((String)optional.get())) : Optional.empty();
    }

    static {
        try (InputStream is = DefaultMessageFetchService.class.getClassLoader().getResourceAsStream("whisper.properties");){
            PROPERTIES.load(is);
        }
        catch (IOException e) {
            LOG.error("Unable to load whisper.properties file", (Throwable)e);
        }
    }
}

