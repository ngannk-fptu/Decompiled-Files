/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.jmx;

import org.bedework.util.jmx.BaseMBean;
import org.bedework.util.jmx.MBeanInfo;

public interface ConfBaseMBean
extends BaseMBean {
    public void setStatus(String var1);

    @Override
    @MBeanInfo(value="Stopped, Done, Running")
    public String getStatus();

    public void setConfigName(String var1);

    @MBeanInfo(value="Application name: identifies configuration")
    public String getConfigName();

    @MBeanInfo(value="Save the configuration")
    public String saveConfig();
}

