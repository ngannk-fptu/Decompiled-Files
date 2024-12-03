/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.applinks.core.rest.model;

import com.atlassian.applinks.core.rest.model.ConsumerEntity;
import java.util.List;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="consumers")
public class ConsumerEntityListEntity {
    @XmlElement(name="consumers")
    private List<? extends ConsumerEntity> consumers;

    public ConsumerEntityListEntity() {
    }

    public ConsumerEntityListEntity(List<ConsumerEntity> consumers) {
        this.consumers = consumers;
    }

    @Nullable
    public List<? extends ConsumerEntity> getConsumers() {
        return this.consumers;
    }
}

