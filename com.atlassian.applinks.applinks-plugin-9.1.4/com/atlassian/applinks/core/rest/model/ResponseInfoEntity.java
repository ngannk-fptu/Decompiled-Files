/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.applinks.core.rest.model;

import java.util.Map;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="responseInfo")
public class ResponseInfoEntity {
    @XmlElement(name="warning")
    private String warning;
    @XmlElement(name="code")
    private String code;
    private Map<String, String> params;

    public ResponseInfoEntity() {
    }

    public ResponseInfoEntity(String code, String warning) {
        this.warning = warning;
        this.code = code;
    }

    public ResponseInfoEntity(String code, String warning, Map<String, String> params) {
        this.warning = warning;
        this.code = code;
        this.params = params;
    }

    public String getWarning() {
        return this.warning;
    }

    public String getCode() {
        return this.code;
    }

    public Map<String, String> getParams() {
        return this.params;
    }
}

