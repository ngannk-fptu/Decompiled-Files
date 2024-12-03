/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.annotate.JsonTypeInfo
 *  org.codehaus.jackson.annotate.JsonTypeInfo$Id
 */
package com.atlassian.troubleshooting.stp.action;

import com.atlassian.troubleshooting.stp.action.DefaultMessage;
import java.io.Serializable;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;

@JsonTypeInfo(defaultImpl=DefaultMessage.class, use=JsonTypeInfo.Id.CLASS)
public interface Message
extends Serializable {
    @JsonProperty
    public String getName();

    @JsonProperty
    public String getBody();

    @JsonProperty
    public String getURL();

    public boolean hasURL();
}

