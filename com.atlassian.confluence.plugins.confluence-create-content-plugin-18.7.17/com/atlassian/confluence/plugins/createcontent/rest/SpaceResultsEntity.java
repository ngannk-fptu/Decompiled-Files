/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.createcontent.rest;

import com.atlassian.confluence.plugins.createcontent.rest.entities.SpaceEntity;
import java.util.Collection;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(value=XmlAccessType.FIELD)
public class SpaceResultsEntity {
    @XmlElement
    private Collection<SpaceEntity> spaces;
    @XmlElement
    private final int resultsLimit;
    @XmlElement
    private boolean resultsTruncated;

    public SpaceResultsEntity(Collection<SpaceEntity> spaces, int resultsLimit, boolean resultsTruncated) {
        this.spaces = spaces;
        this.resultsLimit = resultsLimit;
        this.resultsTruncated = resultsTruncated;
    }

    public Collection<SpaceEntity> getSpaces() {
        return this.spaces;
    }

    public boolean isResultsTruncated() {
        return this.resultsTruncated;
    }

    public int getResultsLimit() {
        return this.resultsLimit;
    }
}

