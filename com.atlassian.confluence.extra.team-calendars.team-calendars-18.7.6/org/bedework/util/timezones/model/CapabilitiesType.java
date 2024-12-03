/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.timezones.model;

import java.util.ArrayList;
import java.util.List;
import org.bedework.util.misc.ToString;
import org.bedework.util.timezones.model.BaseResultType;
import org.bedework.util.timezones.model.CapabilitiesActionType;
import org.bedework.util.timezones.model.CapabilitiesInfoType;

public class CapabilitiesType
extends BaseResultType {
    protected int version;
    protected CapabilitiesInfoType info;
    protected List<CapabilitiesActionType> actions;

    public void setVersion(int value) {
        this.version = value;
    }

    public int getVersion() {
        return this.version;
    }

    public void setInfo(CapabilitiesInfoType value) {
        this.info = value;
    }

    public CapabilitiesInfoType getInfo() {
        return this.info;
    }

    public List<CapabilitiesActionType> getActions() {
        if (this.actions == null) {
            this.actions = new ArrayList<CapabilitiesActionType>();
        }
        return this.actions;
    }

    public String toString() {
        ToString ts = new ToString(this);
        ts.append("version", this.getVersion());
        ts.append("info", this.getInfo());
        ts.append("actions", this.getActions(), true);
        return ts.toString();
    }
}

