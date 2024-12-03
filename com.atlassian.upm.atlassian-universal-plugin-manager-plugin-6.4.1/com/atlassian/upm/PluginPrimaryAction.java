/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm;

public enum PluginPrimaryAction {
    INCOMPATIBLE_WITH_UPDATE,
    INCOMPATIBLE_WITH_PAID_UPDATE,
    INCOMPATIBLE_WITHOUT_UPDATE,
    INCOMPATIBLE_REQUESTED_UPDATE,
    INCOMPATIBLE_WITH_HOST_APPLICATION,
    INCOMPATIBLE_DATA_CENTER_WITHOUT_UPDATE,
    INCOMPATIBLE_DATA_CENTER_REQUESTED_UPDATE,
    INCOMPATIBLE_DATA_CENTER_WITH_UPDATE,
    INCOMPATIBLE_DATA_CENTER_WITH_PAID_UPDATE,
    INCOMPATIBLE_LEGACY_DATA_CENTER_COMPATIBLE,
    LICENSE_INCOMPATIBLE,
    WRONG_APP_TYPE_WITH_UPDATE,
    WRONG_APP_TYPE,
    UPGRADABLE,
    EVAL_RECENTLY_EXPIRED,
    LICENSE_RECENTLY_EXPIRED,
    MAINTENANCE_RECENTLY_EXPIRED,
    UPGRADE_NEARLY_REQUIRED,
    EVAL_NEARLY_EXPIRED,
    LICENSE_NEARLY_EXPIRING,
    MAINTENANCE_NEARLY_EXPIRING,
    LICENSE_FUTURE_INCOMPATIBLE,
    UPDATABLE,
    UPDATABLE_TO_PAID,
    UPDATABLE_NONDEPLOYABLE;


    public int getPriority() {
        return this.ordinal();
    }

    public boolean isIncompatible() {
        switch (this) {
            case INCOMPATIBLE_WITHOUT_UPDATE: 
            case INCOMPATIBLE_WITH_UPDATE: 
            case INCOMPATIBLE_WITH_PAID_UPDATE: 
            case INCOMPATIBLE_REQUESTED_UPDATE: 
            case INCOMPATIBLE_WITH_HOST_APPLICATION: {
                return true;
            }
        }
        return false;
    }

    public boolean isNonDataCenterApproved() {
        switch (this) {
            case INCOMPATIBLE_DATA_CENTER_WITHOUT_UPDATE: 
            case INCOMPATIBLE_DATA_CENTER_REQUESTED_UPDATE: 
            case INCOMPATIBLE_DATA_CENTER_WITH_UPDATE: 
            case INCOMPATIBLE_LEGACY_DATA_CENTER_COMPATIBLE: {
                return true;
            }
        }
        return false;
    }

    public boolean isLicenseIncompatibleInDataCenter() {
        switch (this) {
            case LICENSE_FUTURE_INCOMPATIBLE: 
            case LICENSE_INCOMPATIBLE: {
                return true;
            }
        }
        return false;
    }

    public boolean isUpdatable() {
        switch (this) {
            case WRONG_APP_TYPE_WITH_UPDATE: 
            case UPDATABLE: 
            case UPDATABLE_TO_PAID: 
            case UPDATABLE_NONDEPLOYABLE: {
                return true;
            }
        }
        return false;
    }

    public boolean canRequestUpdateFromVendor() {
        switch (this) {
            case INCOMPATIBLE_WITHOUT_UPDATE: 
            case INCOMPATIBLE_DATA_CENTER_WITHOUT_UPDATE: 
            case INCOMPATIBLE_LEGACY_DATA_CENTER_COMPATIBLE: {
                return true;
            }
        }
        return false;
    }
}

