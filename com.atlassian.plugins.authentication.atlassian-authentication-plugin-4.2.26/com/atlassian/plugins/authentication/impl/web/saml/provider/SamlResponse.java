/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.authentication.impl.web.saml.provider;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class SamlResponse {
    private final String nameId;
    private final Map<String, List<String>> attributes;
    private final String assertionId;
    private final List<Instant> notOnOrAfters;

    public SamlResponse(String nameId, Map<String, List<String>> attributes, String assertionId, List<Instant> notOnOrAfters) {
        this.nameId = nameId;
        this.attributes = attributes;
        this.assertionId = assertionId;
        this.notOnOrAfters = notOnOrAfters;
    }

    public String getNameId() {
        return this.nameId;
    }

    public Iterable<String> getAttribute(String attributeName) {
        return this.attributes.get(attributeName);
    }

    public String getAssertionId() {
        return this.assertionId;
    }

    public List<Instant> getNotOnOrAfters() {
        return this.notOnOrAfters;
    }
}

