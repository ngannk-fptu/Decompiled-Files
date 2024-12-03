/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.rest.representations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class PurchasedPluginUpdateResultCollectionRepresentation {
    @JsonProperty
    private final Collection<String> updatedLicensePluginKeys;
    @JsonProperty
    private final String error;

    @JsonCreator
    public PurchasedPluginUpdateResultCollectionRepresentation(@JsonProperty(value="updatedLicensePluginKeys") Collection<String> updatedLicensePluginKeys, @JsonProperty(value="error") String error) {
        this.updatedLicensePluginKeys = Collections.unmodifiableList(new ArrayList<String>(updatedLicensePluginKeys));
        this.error = error;
    }

    public PurchasedPluginUpdateResultCollectionRepresentation(Collection<String> updatedLicensePluginKeys) {
        this(updatedLicensePluginKeys, null);
    }

    public PurchasedPluginUpdateResultCollectionRepresentation(String error) {
        this(Collections.emptyList(), error);
    }

    public Collection<String> getUpdatedLicensePluginKeys() {
        return this.updatedLicensePluginKeys;
    }

    public String getError() {
        return this.error;
    }
}

