/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.modeler;

import javax.management.MBeanParameterInfo;
import org.apache.tomcat.util.modeler.FeatureInfo;

public class ParameterInfo
extends FeatureInfo {
    private static final long serialVersionUID = 2222796006787664020L;

    public MBeanParameterInfo createParameterInfo() {
        if (this.info == null) {
            this.info = new MBeanParameterInfo(this.getName(), this.getType(), this.getDescription());
        }
        return (MBeanParameterInfo)this.info;
    }
}

