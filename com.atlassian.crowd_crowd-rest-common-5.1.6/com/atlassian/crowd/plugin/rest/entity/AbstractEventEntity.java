/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.event.Operation
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 */
package com.atlassian.crowd.plugin.rest.entity;

import com.atlassian.crowd.model.event.Operation;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(value=XmlAccessType.FIELD)
public abstract class AbstractEventEntity {
    @XmlElement
    private final Operation operation;

    private AbstractEventEntity() {
        this.operation = null;
    }

    protected AbstractEventEntity(Operation operation) {
        this.operation = operation;
    }
}

