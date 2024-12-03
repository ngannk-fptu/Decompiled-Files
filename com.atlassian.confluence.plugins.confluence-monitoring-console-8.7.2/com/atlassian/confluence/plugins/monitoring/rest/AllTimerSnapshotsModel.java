/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.monitoring.rest;

import com.atlassian.confluence.plugins.monitoring.rest.TimerSnapshotModel;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="message")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class AllTimerSnapshotsModel {
    @XmlElement
    private final int iTotalRecords;
    @XmlElement
    private final int iTotalDisplayRecords;
    @XmlElement
    private final List<TimerSnapshotModel> aaData;

    public AllTimerSnapshotsModel() {
        this(new ArrayList<TimerSnapshotModel>());
    }

    public AllTimerSnapshotsModel(List<TimerSnapshotModel> aaData) {
        this.aaData = aaData;
        this.iTotalDisplayRecords = aaData.size();
        this.iTotalRecords = aaData.size();
    }
}

