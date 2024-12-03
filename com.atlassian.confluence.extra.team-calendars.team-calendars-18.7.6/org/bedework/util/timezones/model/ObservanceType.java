/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package org.bedework.util.timezones.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bedework.util.misc.ToString;

public class ObservanceType {
    protected String name;
    protected String onset;
    protected int utcOffsetFrom;
    protected int utcOffsetTo;

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getOnset() {
        return this.onset;
    }

    public void setOnset(String value) {
        this.onset = value;
    }

    @JsonProperty(value="utc-offset-from")
    public int getUtcOffsetFrom() {
        return this.utcOffsetFrom;
    }

    public void setUtcOffsetFrom(int value) {
        this.utcOffsetFrom = value;
    }

    @JsonProperty(value="utc-offset-to")
    public int getUtcOffsetTo() {
        return this.utcOffsetTo;
    }

    public void setUtcOffsetTo(int value) {
        this.utcOffsetTo = value;
    }

    public String toString() {
        ToString ts = new ToString(this);
        ts.append("name", this.getName());
        ts.append("onset", this.getOnset());
        ts.append("offset-from", this.getUtcOffsetFrom());
        ts.append("offset-to", this.getUtcOffsetTo());
        return ts.toString();
    }
}

