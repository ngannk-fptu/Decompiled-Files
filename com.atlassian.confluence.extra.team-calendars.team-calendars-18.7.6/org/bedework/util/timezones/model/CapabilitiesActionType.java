/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package org.bedework.util.timezones.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import org.bedework.util.misc.ToString;
import org.bedework.util.timezones.model.CapabilitiesAcceptParameterType;

public class CapabilitiesActionType {
    protected String name;
    protected String uriTemplate;
    protected List<CapabilitiesAcceptParameterType> parameters;

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    @JsonProperty(value="uri-template")
    public String getUriTemplate() {
        return this.uriTemplate;
    }

    public void setUriTemplate(String value) {
        this.uriTemplate = value;
    }

    public List<CapabilitiesAcceptParameterType> getParameters() {
        if (this.parameters == null) {
            this.parameters = new ArrayList<CapabilitiesAcceptParameterType>();
        }
        return this.parameters;
    }

    public String toString() {
        ToString ts = new ToString(this);
        ts.append("name", this.getName());
        ts.append("uriTemplate", this.getUriTemplate());
        ts.append("parameters", this.getParameters(), true);
        return ts.toString();
    }
}

