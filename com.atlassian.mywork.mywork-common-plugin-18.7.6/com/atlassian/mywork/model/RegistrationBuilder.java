/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonNode
 */
package com.atlassian.mywork.model;

import com.atlassian.mywork.model.Registration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.codehaus.jackson.JsonNode;

public class RegistrationBuilder {
    private final Registration.RegistrationId id;
    public Map<String, Map<String, String>> i18n = new HashMap<String, Map<String, String>>();
    public Map<String, String> properties = new HashMap<String, String>();
    public JsonNode actions;
    public String templates;
    private String displayUrl;

    public RegistrationBuilder(Registration.RegistrationId id) {
        this.id = id;
    }

    public RegistrationBuilder addI18n(Locale locale, Map<String, String> properties) {
        this.i18n.put(locale.toString(), properties);
        return this;
    }

    public RegistrationBuilder properties(Map<String, String> properties) {
        this.properties = properties;
        return this;
    }

    public RegistrationBuilder actions(JsonNode actions) {
        this.actions = actions;
        return this;
    }

    public RegistrationBuilder templates(String templates) {
        this.templates = templates;
        return this;
    }

    public RegistrationBuilder displayUrl(String displayUrl) {
        this.displayUrl = displayUrl;
        return this;
    }

    public Registration build() {
        return new Registration(this.id.application, this.id.appId, this.displayUrl, this.i18n, this.actions, this.properties, this.templates);
    }
}

