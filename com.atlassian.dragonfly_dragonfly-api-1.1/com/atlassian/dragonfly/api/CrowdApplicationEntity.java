/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.application.ApplicationType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlElementWrapper
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 */
package com.atlassian.dragonfly.api;

import com.atlassian.crowd.model.application.ApplicationType;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name="application")
public class CrowdApplicationEntity {
    @XmlElement
    private String name;
    @XmlElement
    @XmlJavaTypeAdapter(value=ApplicationTypeAdapter.class)
    private ApplicationType type;
    @XmlElement
    private String description;
    @XmlElement
    private boolean active;
    @XmlElementWrapper(name="password")
    @XmlElement(name="value")
    private List<String> password;

    public CrowdApplicationEntity() {
    }

    public CrowdApplicationEntity(ApplicationType type, String name, String password, String description, boolean active) {
        this.type = type;
        this.active = active;
        this.description = description;
        this.name = name;
        this.password = Collections.singletonList(password);
    }

    public String getName() {
        return this.name;
    }

    public String getPassword() {
        if (this.password == null || this.password.size() != 1) {
            throw new IllegalStateException("Invalid password: " + this.password);
        }
        return this.password.get(0);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class ApplicationTypeAdapter
    extends XmlAdapter<String, ApplicationType> {
        private ApplicationTypeAdapter() {
        }

        public String marshal(ApplicationType v) throws Exception {
            return v.getDisplayName();
        }

        public ApplicationType unmarshal(String v) throws Exception {
            for (ApplicationType type : ApplicationType.values()) {
                if (!type.getDisplayName().equals(v)) continue;
                return type;
            }
            return null;
        }
    }
}

