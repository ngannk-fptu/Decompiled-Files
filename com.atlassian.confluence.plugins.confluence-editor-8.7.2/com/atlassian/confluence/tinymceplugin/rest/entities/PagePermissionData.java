/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.tinymceplugin.rest.entities;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PagePermissionData {
    @XmlElement
    private String viewPermissionsUsers;
    @XmlElement
    private String editPermissionsUsers;
    @XmlElement
    private String viewPermissionsGroups;
    @XmlElement
    private String editPermissionsGroups;

    public PagePermissionData() {
    }

    public PagePermissionData(String viewPermissionsUsers, String editPermissionsUsers, String viewPermissionsGroups, String editPermissionsGroups) {
        this.viewPermissionsUsers = viewPermissionsUsers;
        this.editPermissionsUsers = editPermissionsUsers;
        this.viewPermissionsGroups = viewPermissionsGroups;
        this.editPermissionsGroups = editPermissionsGroups;
    }

    public String getViewPermissionsUsers() {
        return this.viewPermissionsUsers;
    }

    public String getEditPermissionsUsers() {
        return this.editPermissionsUsers;
    }

    public String getViewPermissionsGroups() {
        return this.viewPermissionsGroups;
    }

    public String getEditPermissionsGroups() {
        return this.editPermissionsGroups;
    }

    public void setViewPermissionsUsers(String viewPermissionsUsers) {
        this.viewPermissionsUsers = viewPermissionsUsers;
    }

    public void setEditPermissionsUsers(String editPermissionsUsers) {
        this.editPermissionsUsers = editPermissionsUsers;
    }

    public void setViewPermissionsGroups(String viewPermissionsGroups) {
        this.viewPermissionsGroups = viewPermissionsGroups;
    }

    public void setEditPermissionsGroups(String editPermissionsGroups) {
        this.editPermissionsGroups = editPermissionsGroups;
    }
}

