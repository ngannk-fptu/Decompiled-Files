/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.codehaus.jackson.JsonNode
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.codehaus.jackson.node.JsonNodeFactory
 */
package com.atlassian.mywork.model;

import com.atlassian.mywork.rest.JsonObject;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.JsonNodeFactory;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Registration
implements JsonObject,
Serializable {
    private static final ResourceBundle.Control CONTROL = new ResourceBundle.Control(){};
    private static final long serialVersionUID = 6384173465975740118L;
    @JsonProperty
    private final String application;
    @JsonProperty
    private final String appId;
    @JsonProperty
    private final String displayURL;
    @JsonProperty
    private final Map<String, Map<String, String>> i18n;
    @JsonProperty
    private final JsonNode actions;
    @JsonProperty
    private final Map<String, String> properties;
    @JsonProperty
    private final String templates;

    public Registration(RegistrationId id) {
        this(id.application, id.appId, null, new HashMap<String, Map<String, String>>(), (JsonNode)JsonNodeFactory.instance.objectNode(), new HashMap<String, String>(), null);
    }

    @JsonCreator
    public Registration(@JsonProperty(value="application") String application, @JsonProperty(value="appId") String appId, @JsonProperty(value="displayURL") String displayURL, @JsonProperty(value="i18n") Map<String, Map<String, String>> i18n, @JsonProperty(value="actions") JsonNode actions, @JsonProperty(value="properties") Map<String, String> properties, @JsonProperty(value="templates") String templates) {
        this.application = application;
        this.appId = appId;
        this.displayURL = displayURL;
        this.actions = actions;
        if (i18n == null) {
            i18n = new HashMap<String, Map<String, String>>();
        }
        this.i18n = i18n;
        if (properties == null) {
            properties = new HashMap<String, String>();
        }
        this.properties = properties;
        this.templates = templates;
    }

    public String getAppId() {
        return this.appId;
    }

    public String getApplication() {
        return this.application;
    }

    public String getDisplayURL() {
        return this.displayURL;
    }

    public Map<String, Map<String, String>> getI18n() {
        return Collections.unmodifiableMap(this.i18n);
    }

    public JsonNode getActions() {
        return this.actions;
    }

    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(this.properties);
    }

    public String getTemplates() {
        return this.templates;
    }

    public Map<String, String> getValues(Locale locale) {
        HashMap<String, String> map = new HashMap<String, String>();
        List<Locale> locales = CONTROL.getCandidateLocales("", locale);
        Collections.reverse(locales);
        for (Locale childLocale : locales) {
            Map<String, String> t = this.i18n.get(childLocale.toString());
            if (t == null) continue;
            map.putAll(t);
        }
        return map;
    }

    public RegistrationId getId() {
        return new RegistrationId(this.application, this.appId);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Registration that = (Registration)o;
        return new EqualsBuilder().append((Object)this.application, (Object)that.application).append((Object)this.appId, (Object)that.appId).append((Object)this.displayURL, (Object)that.displayURL).append(this.i18n, that.i18n).append((Object)this.actions, (Object)that.actions).append(this.properties, that.properties).append((Object)this.templates, (Object)that.templates).isEquals();
    }

    public int hashCode() {
        return Objects.hash(this.application, this.appId, this.displayURL, this.i18n, this.actions, this.properties, this.templates);
    }

    private Object writeReplace() {
        return new SerializationProxy(this);
    }

    private void readObject(ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException("Proxy required");
    }

    private static class SerializationProxy
    implements Serializable {
        private static final ObjectMapper jsonMapper = new ObjectMapper();
        private static final long serialVersionUID = -5794423701183856744L;
        private final String registrationJson;

        SerializationProxy(Registration registration) {
            try {
                this.registrationJson = jsonMapper.writeValueAsString((Object)registration);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private Object readResolve() {
            try {
                return jsonMapper.readValue(this.registrationJson, Registration.class);
            }
            catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static class RegistrationId {
        public final String application;
        public final String appId;

        public RegistrationId(String application, String appId) {
            this.application = application;
            this.appId = appId;
        }

        public String toString() {
            return this.application + ":" + this.appId;
        }
    }
}

