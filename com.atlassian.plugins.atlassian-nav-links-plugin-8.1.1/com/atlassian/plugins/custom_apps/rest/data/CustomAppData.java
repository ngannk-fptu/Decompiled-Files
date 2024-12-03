/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.plugins.custom_apps.rest.data;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CustomAppData {
    public String id;
    public String url;
    public String displayName;
    public String applicationType;
    public Boolean hide;
    public boolean editable;
    public List<String> allowedGroups;
    public String sourceApplicationUrl;
    public String sourceApplicationName;
    public boolean self;

    public CustomAppData(String id, String displayName, String url, String applicationType, Boolean hide, boolean editable, List<String> allowedGroups, String sourceApplicationUrl, String sourceApplicationName, boolean self) {
        this.id = id;
        this.displayName = displayName;
        this.url = url;
        this.applicationType = applicationType;
        this.hide = hide;
        this.editable = editable;
        this.allowedGroups = allowedGroups;
        this.sourceApplicationUrl = sourceApplicationUrl;
        this.sourceApplicationName = sourceApplicationName;
        this.self = self;
    }

    public CustomAppData() {
    }
}

