/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.authentication.CookieConfiguration
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.crowd.plugin.rest.entity.admin;

import com.atlassian.crowd.model.authentication.CookieConfiguration;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="cookie-config")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class CookieConfigEntity {
    @XmlElement(name="domain")
    private final String domain;
    @XmlElement(name="secure")
    private final boolean secure;
    @XmlElement(name="name")
    private final String name;

    private CookieConfigEntity() {
        this.domain = null;
        this.secure = false;
        this.name = null;
    }

    public CookieConfigEntity(String domain, boolean secure, String name) {
        this.domain = domain;
        this.secure = secure;
        this.name = name;
    }

    public CookieConfigEntity(CookieConfiguration cookieConfiguration) {
        this.domain = cookieConfiguration.getDomain();
        this.secure = cookieConfiguration.isSecure();
        this.name = cookieConfiguration.getName();
    }

    public String getDomain() {
        return this.domain;
    }

    public boolean isSecure() {
        return this.secure;
    }

    public String getName() {
        return this.name;
    }
}

