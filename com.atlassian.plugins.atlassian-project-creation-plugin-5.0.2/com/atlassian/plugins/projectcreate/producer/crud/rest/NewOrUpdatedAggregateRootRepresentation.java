/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.plugins.projectcreate.producer.crud.rest;

import java.util.Map;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class NewOrUpdatedAggregateRootRepresentation {
    @XmlElement
    public String label;
    @XmlElement
    public String subtype;
    @XmlElement
    public Map<String, String> context;
}

