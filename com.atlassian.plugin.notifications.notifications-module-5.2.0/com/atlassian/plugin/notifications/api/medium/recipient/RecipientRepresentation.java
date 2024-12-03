/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.builder.ToStringBuilder
 *  org.apache.commons.lang.builder.ToStringStyle
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.plugin.notifications.api.medium.recipient;

import com.atlassian.plugin.notifications.api.medium.recipient.GroupRecipient;
import com.atlassian.plugin.notifications.api.medium.recipient.ParameterConfig;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class RecipientRepresentation
implements Comparable<RecipientRepresentation>,
GroupRecipient {
    @JsonProperty
    private final int id;
    @JsonProperty
    private final boolean individual;
    @JsonProperty
    private final String type;
    @JsonProperty
    private final String name;
    @JsonProperty
    private final int serverId;
    @JsonProperty
    private final String paramValue;
    @JsonProperty
    private final String paramDisplay;
    @JsonProperty
    private ParameterConfig parameterConfig;

    @JsonCreator
    public RecipientRepresentation(@JsonProperty(value="id") int id, @JsonProperty(value="individual") boolean individual, @JsonProperty(value="type") String type, @JsonProperty(value="name") String name, @JsonProperty(value="serverId") int serverId, @JsonProperty(value="paramValue") String paramValue, @JsonProperty(value="paramDisplay") String paramDisplay) {
        this.id = id;
        this.individual = individual;
        this.type = type;
        this.name = name;
        this.serverId = serverId;
        this.paramValue = paramValue;
        this.paramDisplay = paramDisplay;
    }

    public int getId() {
        return this.id;
    }

    public boolean isIndividual() {
        return this.individual;
    }

    public String getType() {
        return this.type;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getServerId() {
        return this.serverId;
    }

    @Override
    public String getParamValue() {
        return this.paramValue;
    }

    @Override
    public String getParamDisplay() {
        return this.paramDisplay;
    }

    public void setParameterConfig(ParameterConfig parameterConfig) {
        this.parameterConfig = parameterConfig;
    }

    public String getUniqueKey() {
        StringBuilder builder = new StringBuilder();
        if (this.individual) {
            builder.append(this.type);
        } else {
            builder.append(this.serverId);
        }
        if (StringUtils.isNotBlank((CharSequence)this.paramValue)) {
            builder.append("_").append(this.paramValue);
        }
        return builder.toString();
    }

    @Override
    public int compareTo(RecipientRepresentation o) {
        if (o != null) {
            return this.getName().compareTo(o.getName());
        }
        return 0;
    }

    public String toString() {
        return "Recipient (" + new ToStringBuilder((Object)this, ToStringStyle.SIMPLE_STYLE).append("id", this.id).append("individual", this.individual).append("type", (Object)this.type).append("name", (Object)this.name).append("serverId", this.serverId).append("paramValue", (Object)this.paramValue).append("paramDisplay", (Object)this.paramDisplay).append("parameterConfig", (Object)this.parameterConfig).toString() + ")";
    }
}

