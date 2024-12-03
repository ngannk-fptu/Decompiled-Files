/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 */
package com.atlassian.confluence.tinymceplugin.rest.entities;

import javax.xml.bind.annotation.XmlElement;

public class UserAction {
    @XmlElement
    private String id;
    @XmlElement
    private String label;
    @XmlElement
    private String tooltip;
    @XmlElement
    private String url;
    @XmlElement
    private String style;

    public UserAction() {
    }

    public UserAction(String id, String label, String tooltip, String url, String style) {
        this.id = id;
        this.label = label;
        this.tooltip = tooltip;
        this.url = url;
        this.style = style;
    }
}

