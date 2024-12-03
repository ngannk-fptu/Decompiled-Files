/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.denormalisedpermissions.DenormalisedPermissionServiceState
 *  com.atlassian.confluence.security.denormalisedpermissions.StateChangeInformation
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonSerialize
 *  org.codehaus.jackson.map.annotate.JsonSerialize$Inclusion
 */
package com.atlassian.confluence.plugins.denormalisedpermissions.state;

import com.atlassian.confluence.plugins.denormalisedpermissions.state.StateChangeInformationJson;
import com.atlassian.confluence.security.denormalisedpermissions.DenormalisedPermissionServiceState;
import com.atlassian.confluence.security.denormalisedpermissions.StateChangeInformation;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class ServiceStateJson {
    private final DenormalisedPermissionServiceState spaceState;
    private final DenormalisedPermissionServiceState contentState;
    private final Long spaceLag;
    private final Long contentLag;
    private final Collection<StateChangeInformationJson> stateChangeLog;

    @JsonCreator
    public ServiceStateJson(@JsonProperty(value="spaceState") DenormalisedPermissionServiceState spaceState, @JsonProperty(value="contentState") DenormalisedPermissionServiceState contentState, @JsonProperty(value="spaceLag") Long spaceLag, @JsonProperty(value="contentLag") Long contentLag, @JsonProperty(value="stateChangeLog") Collection<StateChangeInformationJson> stateChangeLog) {
        this.spaceState = spaceState;
        this.contentState = contentState;
        this.spaceLag = spaceLag;
        this.contentLag = contentLag;
        this.stateChangeLog = stateChangeLog;
    }

    public ServiceStateJson(DenormalisedPermissionServiceState spaceState, DenormalisedPermissionServiceState contentState, Long spaceLag, Long contentLag, List<StateChangeInformation> stateChangeLog) {
        this(spaceState, contentState, spaceLag, contentLag, stateChangeLog.stream().map(StateChangeInformationJson::new).collect(Collectors.toList()));
    }

    @JsonProperty(value="spaceState")
    public DenormalisedPermissionServiceState getSpaceState() {
        return this.spaceState;
    }

    @JsonProperty(value="contentState")
    public DenormalisedPermissionServiceState getContentState() {
        return this.contentState;
    }

    @JsonProperty(value="spaceLag")
    public Long getSpaceLag() {
        return this.spaceLag;
    }

    @JsonProperty(value="contentLag")
    public Long getContentLag() {
        return this.contentLag;
    }

    @JsonProperty(value="stateChangeLog")
    public Collection<StateChangeInformationJson> getStateChangeLog() {
        return this.stateChangeLog;
    }
}

