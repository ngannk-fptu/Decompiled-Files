/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.webhooks.Webhook
 *  com.atlassian.webhooks.WebhookCreateRequest
 *  com.atlassian.webhooks.WebhookCreateRequest$Builder
 *  com.atlassian.webhooks.WebhookEvent
 *  com.atlassian.webhooks.WebhookScope
 *  com.atlassian.webhooks.WebhookService
 *  com.atlassian.webhooks.module.WebhookModuleDescriptor
 *  com.google.common.base.CharMatcher
 *  com.google.common.collect.ListMultimap
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.MultimapBuilder
 *  javax.annotation.Nonnull
 *  javax.validation.ConstraintViolationException
 *  org.dom4j.Attribute
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.webhooks.internal.module;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.webhooks.Webhook;
import com.atlassian.webhooks.WebhookCreateRequest;
import com.atlassian.webhooks.WebhookEvent;
import com.atlassian.webhooks.WebhookScope;
import com.atlassian.webhooks.WebhookService;
import com.atlassian.webhooks.internal.Validator;
import com.atlassian.webhooks.internal.model.SimpleWebhook;
import com.atlassian.webhooks.internal.model.SimpleWebhookScope;
import com.atlassian.webhooks.internal.model.UnknownWebhookEvent;
import com.atlassian.webhooks.module.WebhookModuleDescriptor;
import com.google.common.base.CharMatcher;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.validation.ConstraintViolationException;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleWebhookModuleDescriptor
extends AbstractModuleDescriptor<Webhook>
implements WebhookModuleDescriptor {
    private static final AtomicInteger idGenerator = new AtomicInteger(-1);
    private static final Logger log = LoggerFactory.getLogger(SimpleWebhookModuleDescriptor.class);
    private static final CharMatcher SLASH = CharMatcher.is((char)'/');
    private final ApplicationProperties applicationProperties;
    private final int id;
    private final Validator validator;
    private final WebhookService webhookService;
    private WebhookCreateRequest request;
    private volatile Webhook webhook;

    public SimpleWebhookModuleDescriptor(ApplicationProperties applicationProperties, ModuleFactory moduleFactory, Validator validator, WebhookService webhookService) {
        super(moduleFactory);
        this.applicationProperties = applicationProperties;
        this.validator = validator;
        this.webhookService = webhookService;
        this.id = idGenerator.decrementAndGet();
    }

    public Webhook getModule() {
        return this.getOrCreateWebhook();
    }

    public void disabled() {
        super.disabled();
        this.webhook = null;
    }

    public void init(@Nonnull Plugin plugin, @Nonnull Element element) throws PluginParseException {
        super.init(plugin, element);
        this.request = ((WebhookCreateRequest.Builder)((WebhookCreateRequest.Builder)((WebhookCreateRequest.Builder)((WebhookCreateRequest.Builder)((WebhookCreateRequest.Builder)((WebhookCreateRequest.Builder)WebhookCreateRequest.builder().name(this.getKey())).configuration("pluginKey", this.getPluginKey())).configuration(this.getConfiguration(element))).event(this.getEvents(SimpleWebhookModuleDescriptor.getAttributeOrThrow(element, "event")))).scope(this.getScope(element.element("scope")))).url(this.toUrl(SimpleWebhookModuleDescriptor.getAttributeOrThrow(element, "url")))).build();
    }

    private static Optional<String> getAttribute(Element element, String name) {
        Attribute attribute = element.attribute(name);
        return attribute == null ? Optional.empty() : Optional.ofNullable(attribute.getStringValue());
    }

    private static String getAttributeOrThrow(Element element, String name) throws PluginParseException {
        return SimpleWebhookModuleDescriptor.getAttribute(element, name).orElseThrow(() -> new PluginParseException("Required attribute '" + name + "' on '" + element.getName() + "' is missing"));
    }

    private Map<String, String> getConfiguration(Element element) {
        HashMap<String, String> result = new HashMap<String, String>();
        Multimap<String, String> parameters = this.getParameters(element);
        this.getExcludeBody(element, parameters).ifPresent(value -> result.put("excludeBody", value.toString()));
        this.getFilter(element, parameters).ifPresent(value -> result.put("filter", (String)value));
        parameters.asMap().forEach((key, values) -> result.put((String)key, values.stream().collect(Collectors.joining(","))));
        return result;
    }

    private Set<WebhookEvent> getEvents(String eventId) {
        if (eventId.endsWith("*")) {
            String prefix = eventId.substring(0, eventId.length() - 1);
            Set<WebhookEvent> events = this.webhookService.getEvents().stream().filter(event -> event.getId().startsWith(prefix)).collect(Collectors.toSet());
            if (!events.isEmpty()) {
                return events;
            }
        }
        WebhookEvent event2 = this.webhookService.getEvent(eventId).orElseGet(() -> new UnknownWebhookEvent(eventId));
        return Collections.singleton(event2);
    }

    private Optional<Boolean> getExcludeBody(Element element, Multimap<String, String> parameters) {
        Optional<Boolean> excludeBodyAttr = SimpleWebhookModuleDescriptor.getAttribute(element, "excludeBody").map(Boolean::valueOf);
        Collection excludeBodyParam = parameters.removeAll((Object)"excludeBody");
        Collection excludeIssueDetailsParam = parameters.removeAll((Object)"excludeIssueDetails");
        if (excludeBodyAttr.isPresent() || !excludeBodyParam.isEmpty() || !excludeIssueDetailsParam.isEmpty()) {
            Boolean excludeBody = excludeBodyAttr.orElse(null);
            if (excludeBodyParam != null) {
                for (String param : excludeBodyParam) {
                    excludeBody = this.merge("excludeBody", excludeBody, Boolean.valueOf(param));
                }
            }
            if (excludeIssueDetailsParam != null) {
                for (String param : excludeIssueDetailsParam) {
                    excludeBody = this.merge("excludeBody", excludeBody, Boolean.valueOf(param));
                }
            }
            return Optional.of(excludeBody);
        }
        return Optional.empty();
    }

    private Optional<String> getFilter(Element element, Multimap<String, String> parameters) {
        Optional<String> filterAttr = SimpleWebhookModuleDescriptor.getAttribute(element, "filter");
        Collection filterParam = parameters.removeAll((Object)"filter");
        Collection jqlParam = parameters.removeAll((Object)"jql");
        if (filterAttr.isPresent() || !filterParam.isEmpty() || !jqlParam.isEmpty()) {
            String filter = filterAttr.orElse(null);
            if (filterParam != null) {
                for (String param : filterParam) {
                    filter = this.merge("filter", filter, param);
                }
            }
            if (jqlParam != null) {
                for (String param : jqlParam) {
                    filter = this.merge("filter", filter, param);
                }
            }
            return Optional.of(filter);
        }
        return Optional.empty();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Webhook getOrCreateWebhook() {
        if (this.webhook != null) {
            return this.webhook;
        }
        SimpleWebhookModuleDescriptor simpleWebhookModuleDescriptor = this;
        synchronized (simpleWebhookModuleDescriptor) {
            boolean valid;
            if (this.webhook != null) {
                return this.webhook;
            }
            try {
                this.validator.validate(this.request);
                valid = true;
            }
            catch (ConstraintViolationException e) {
                String violations = e.getConstraintViolations().stream().map(violation -> violation.getPropertyPath().toString() + ": " + violation.getMessage()).collect(Collectors.joining(", "));
                log.warn("De-activating webhook '{}' to '{}' because it's descriptor is invalid ({}).", new Object[]{this.getCompleteKey(), this.request.getUrl(), violations});
                valid = false;
            }
            this.webhook = SimpleWebhook.builder().id(this.id).name(this.request.getName()).active(valid && this.request.isActive()).configuration(this.request.getConfiguration()).event(this.request.getEvents()).scope(this.request.getScope()).url(this.request.getUrl()).build();
        }
        return this.webhook;
    }

    private Multimap<String, String> getParameters(Element element) {
        ListMultimap result = MultimapBuilder.hashKeys().arrayListValues().build();
        for (Object paramElem : element.elements("param")) {
            Element param = (Element)paramElem;
            String name = SimpleWebhookModuleDescriptor.getAttributeOrThrow(param, "name");
            result.put((Object)name, (Object)SimpleWebhookModuleDescriptor.getAttribute(param, "value").orElse(param.getStringValue()));
        }
        return result;
    }

    private WebhookScope getScope(Element scopeElement) {
        if (scopeElement == null) {
            return WebhookScope.GLOBAL;
        }
        return new SimpleWebhookScope(SimpleWebhookModuleDescriptor.getAttributeOrThrow(scopeElement, "type"), scopeElement.getStringValue());
    }

    private <T> T merge(String paramName, T object1, T object2) {
        if (object1 == null) {
            return object2;
        }
        if (object2 == null) {
            return object1;
        }
        if (object1.equals(object2)) {
            return object1;
        }
        throw new PluginParseException(paramName + " was defined multiple times with different values");
    }

    private String toUrl(String value) {
        try {
            URI uri = new URI(value);
            if (!uri.isAbsolute()) {
                return SLASH.trimTrailingFrom((CharSequence)this.applicationProperties.getBaseUrl(UrlMode.CANONICAL)) + '/' + SLASH.trimLeadingFrom((CharSequence)value);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return value;
    }
}

