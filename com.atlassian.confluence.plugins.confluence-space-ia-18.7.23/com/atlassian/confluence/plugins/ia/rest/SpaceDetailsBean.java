/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.SpaceLogo
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.ia.rest;

import com.atlassian.confluence.spaces.SpaceLogo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SpaceDetailsBean {
    @XmlElement
    private final String name;
    @XmlElement
    private final String logoDownloadPath;

    public SpaceDetailsBean(String name, SpaceLogo spaceLogo) {
        this.name = name;
        this.logoDownloadPath = spaceLogo.getDownloadPath();
    }
}

