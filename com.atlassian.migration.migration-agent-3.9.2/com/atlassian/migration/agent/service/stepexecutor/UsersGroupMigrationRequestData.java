/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.service.stepexecutor;

import com.atlassian.migration.agent.service.user.request.v2.UsersMigrationV2FilePayload;
import com.atlassian.migration.agent.service.user.request.v2.UsersMigrationV2Request;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class UsersGroupMigrationRequestData {
    private final UsersMigrationV2FilePayload filePayload;
    private final UsersMigrationV2Request usersMigrationV2Request;

    @JsonCreator
    public UsersGroupMigrationRequestData(@JsonProperty(value="filePayload") UsersMigrationV2FilePayload filePayload, @JsonProperty(value="usersMigrationV2Request") UsersMigrationV2Request usersMigrationV2Request) {
        this.filePayload = filePayload;
        this.usersMigrationV2Request = usersMigrationV2Request;
    }

    @Generated
    public UsersMigrationV2FilePayload getFilePayload() {
        return this.filePayload;
    }

    @Generated
    public UsersMigrationV2Request getUsersMigrationV2Request() {
        return this.usersMigrationV2Request;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof UsersGroupMigrationRequestData)) {
            return false;
        }
        UsersGroupMigrationRequestData other = (UsersGroupMigrationRequestData)o;
        if (!other.canEqual(this)) {
            return false;
        }
        UsersMigrationV2FilePayload this$filePayload = this.getFilePayload();
        UsersMigrationV2FilePayload other$filePayload = other.getFilePayload();
        if (this$filePayload == null ? other$filePayload != null : !((Object)this$filePayload).equals(other$filePayload)) {
            return false;
        }
        UsersMigrationV2Request this$usersMigrationV2Request = this.getUsersMigrationV2Request();
        UsersMigrationV2Request other$usersMigrationV2Request = other.getUsersMigrationV2Request();
        return !(this$usersMigrationV2Request == null ? other$usersMigrationV2Request != null : !((Object)this$usersMigrationV2Request).equals(other$usersMigrationV2Request));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof UsersGroupMigrationRequestData;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        UsersMigrationV2FilePayload $filePayload = this.getFilePayload();
        result = result * 59 + ($filePayload == null ? 43 : ((Object)$filePayload).hashCode());
        UsersMigrationV2Request $usersMigrationV2Request = this.getUsersMigrationV2Request();
        result = result * 59 + ($usersMigrationV2Request == null ? 43 : ((Object)$usersMigrationV2Request).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "UsersGroupMigrationRequestData(filePayload=" + this.getFilePayload() + ", usersMigrationV2Request=" + this.getUsersMigrationV2Request() + ")";
    }
}

