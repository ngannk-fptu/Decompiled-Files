/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.SpaceGroup
 */
package com.atlassian.confluence.rpc.soap.beans;

import com.atlassian.confluence.spaces.SpaceGroup;

@Deprecated
public class RemoteSpaceGroup {
    private String name;
    private String key;
    private String licenseKey;
    private String creatorName;
    public static final String __PARANAMER_DATA = "<init> com.atlassian.confluence.spaces.SpaceGroup spaceGroup \nsetCreatorName java.lang.String creatorName \nsetKey java.lang.String key \nsetLicenseKey java.lang.String licenseKey \nsetName java.lang.String name \n";

    public RemoteSpaceGroup() {
    }

    public RemoteSpaceGroup(SpaceGroup spaceGroup) {
        this.key = spaceGroup.getKey();
        this.name = spaceGroup.getName();
        this.licenseKey = spaceGroup.getLicenseKey();
        this.creatorName = spaceGroup.getCreatorName();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLicenseKey() {
        return this.licenseKey;
    }

    public void setLicenseKey(String licenseKey) {
        this.licenseKey = licenseKey;
    }

    public String getCreatorName() {
        return this.creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }
}

