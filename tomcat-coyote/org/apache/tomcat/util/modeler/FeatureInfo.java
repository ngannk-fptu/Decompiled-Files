/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.modeler;

import java.io.Serializable;
import javax.management.MBeanFeatureInfo;

public class FeatureInfo
implements Serializable {
    private static final long serialVersionUID = -911529176124712296L;
    protected String description = null;
    protected String name = null;
    protected MBeanFeatureInfo info = null;
    protected String type = null;

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

