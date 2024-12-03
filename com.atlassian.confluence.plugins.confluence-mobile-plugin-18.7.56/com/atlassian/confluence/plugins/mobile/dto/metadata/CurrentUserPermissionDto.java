/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.mobile.dto.metadata;

import org.codehaus.jackson.annotate.JsonProperty;

public class CurrentUserPermissionDto {
    @JsonProperty
    private final boolean update;
    @JsonProperty
    private final boolean delete;
    @JsonProperty
    private final boolean setPermissions;

    public CurrentUserPermissionDto() {
        this(false, false, false);
    }

    public CurrentUserPermissionDto(boolean update, boolean delete, boolean setPermission) {
        this.update = update;
        this.delete = delete;
        this.setPermissions = setPermission;
    }

    public boolean isUpdate() {
        return this.update;
    }

    public boolean isDelete() {
        return this.delete;
    }

    public boolean isSetPermissions() {
        return this.setPermissions;
    }

    public int hashCode() {
        int result = Boolean.hashCode(this.update);
        return 31 * (result += Boolean.hashCode(this.delete)) + Boolean.hashCode(this.delete);
    }

    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (!(object instanceof CurrentUserPermissionDto)) {
            return false;
        }
        CurrentUserPermissionDto that = (CurrentUserPermissionDto)object;
        return this.isUpdate() == that.isUpdate() && this.isDelete() == that.isDelete() && this.isSetPermissions() == that.isSetPermissions();
    }
}

