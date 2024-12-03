/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.struts2.action.ParametersAware
 *  org.apache.struts2.dispatcher.HttpParameters
 */
package org.apache.struts2.interceptor;

import java.util.Map;
import org.apache.struts2.action.ParametersAware;
import org.apache.struts2.dispatcher.HttpParameters;

@Deprecated(since="1.0.0", forRemoval=true)
public interface ParameterAware
extends ParametersAware {
    public void setParameters(Map var1);

    default public void withParameters(HttpParameters parameters) {
        this.setParameters((Map)parameters);
    }
}

