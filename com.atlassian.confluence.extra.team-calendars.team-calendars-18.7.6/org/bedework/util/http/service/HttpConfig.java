/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.http.service;

import java.io.Serializable;
import org.bedework.util.config.ConfInfo;
import org.bedework.util.jmx.MBeanInfo;

@ConfInfo(elementName="http-properties")
public interface HttpConfig
extends Serializable {
    public void setMaxConnections(int var1);

    @MBeanInfo(value="Max connections.")
    public int getMaxConnections();

    public void setDefaultMaxPerRoute(int var1);

    @MBeanInfo(value="Maximum allowable per route.")
    public int getDefaultMaxPerRoute();

    @MBeanInfo(value="Disable ssl for testing.")
    public void disableSSL();
}

