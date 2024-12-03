/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.messages.Message
 *  com.atlassian.confluence.api.model.messages.SimpleMessage
 *  com.atlassian.querylang.fields.ValueType
 *  com.google.common.base.MoreObjects
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonDeserialize
 */
package com.atlassian.confluence.plugins.cql.rest.model;

import com.atlassian.confluence.api.model.messages.Message;
import com.atlassian.confluence.api.model.messages.SimpleMessage;
import com.atlassian.querylang.fields.ValueType;
import com.google.common.base.MoreObjects;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

public class RestUiSupport {
    @JsonProperty
    @JsonDeserialize(as=SimpleMessage.class)
    private final Message label;
    @JsonProperty
    @JsonDeserialize(as=SimpleMessage.class)
    private final Message tooltip;
    @JsonProperty
    private final String valueType;
    @JsonProperty
    private final String dataUri;

    @JsonCreator
    private RestUiSupport() {
        this(RestUiSupport.builder());
    }

    private RestUiSupport(RestUiSupportBuilder builder) {
        this.label = builder.label;
        this.tooltip = builder.tooltip;
        this.valueType = builder.valueType.toString();
        this.dataUri = builder.dataUri;
    }

    public static RestUiSupportBuilder builder() {
        return new RestUiSupportBuilder();
    }

    public Message getLabel() {
        return this.label;
    }

    public Message getTooltip() {
        return this.tooltip;
    }

    public String getValueType() {
        return this.valueType;
    }

    public String getDataUri() {
        return this.dataUri;
    }

    public boolean equals(Object otherObj) {
        if (!(otherObj instanceof RestUiSupport)) {
            return false;
        }
        RestUiSupport other = (RestUiSupport)otherObj;
        return Objects.equals(this.label, other.label) && Objects.equals(this.tooltip, other.tooltip) && Objects.equals(this.valueType, other.valueType) && Objects.equals(this.dataUri, other.dataUri);
    }

    public int hashCode() {
        return Objects.hash(this.label, this.tooltip, this.valueType, this.dataUri);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("label", (Object)this.label).add("tooltip", (Object)this.tooltip).add("valueType", (Object)this.valueType).add("dataUri", (Object)this.dataUri).toString();
    }

    public static class RestUiSupportBuilder {
        private Message label;
        private Message tooltip;
        private ValueType valueType = ValueType.NOT_SPECIFIED;
        private String dataUri;

        private RestUiSupportBuilder() {
        }

        public RestUiSupportBuilder label(Message label) {
            this.label = label;
            return this;
        }

        public RestUiSupportBuilder tooltip(Message tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public RestUiSupportBuilder valueType(ValueType valueType) {
            this.valueType = valueType;
            return this;
        }

        public RestUiSupportBuilder dataUri(String dataUri) {
            this.dataUri = dataUri;
            return this;
        }

        public RestUiSupport build() {
            return new RestUiSupport(this);
        }
    }
}

