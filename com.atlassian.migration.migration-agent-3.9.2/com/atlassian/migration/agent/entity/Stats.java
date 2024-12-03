/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Column
 *  javax.persistence.Entity
 *  javax.persistence.EnumType
 *  javax.persistence.Enumerated
 *  javax.persistence.Id
 *  javax.persistence.IdClass
 *  javax.persistence.Table
 */
package com.atlassian.migration.agent.entity;

import com.atlassian.migration.agent.entity.StatsKey;
import com.atlassian.migration.agent.entity.StatsType;
import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Table(name="MIG_STATS")
@Entity
@IdClass(value=StatsKey.class)
public class Stats {
    @Id
    @Column(name="statType", nullable=false)
    @Enumerated(value=EnumType.STRING)
    private StatsType type;
    @Id
    @Column(name="statName", nullable=false)
    private String name;
    @Column(name="collectedTime", nullable=false)
    private Instant collectedTime;
    @Column(name="statValue")
    private long value;

    public Stats(String name, long value) {
        this(name, value, Instant.now());
    }

    public Stats(String name, long value, Instant collectedTime) {
        this(StatsType.SITE, name, value, collectedTime);
    }

    public Stats(StatsType type, String name, long value, Instant collectedTime) {
        this.type = type;
        this.name = name;
        this.value = value;
        this.collectedTime = collectedTime;
    }

    private Stats() {
    }

    public Instant getCollectedTime() {
        return this.collectedTime;
    }

    public void setCollectedTime(Instant collectedTime) {
        this.collectedTime = collectedTime;
    }

    public StatsType getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public long getValue() {
        return this.value;
    }
}

