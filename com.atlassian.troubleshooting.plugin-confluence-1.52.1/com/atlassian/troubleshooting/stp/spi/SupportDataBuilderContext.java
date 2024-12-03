/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.stp.spi;

import com.atlassian.troubleshooting.stp.spi.SupportDataDetail;
import java.util.Date;

public interface SupportDataBuilderContext {
    public boolean removeProperty(String var1);

    public int getProperty(String var1, int var2);

    public long getProperty(String var1, long var2);

    public float getProperty(String var1, float var2);

    public double getProperty(String var1, double var2);

    public boolean getProperty(String var1, boolean var2);

    public <T> T getProperty(String var1, T var2);

    public SupportDataDetail getRequestDetail();

    public Date getStart();

    public void setProperty(String var1, int var2);

    public void setProperty(String var1, long var2);

    public void setProperty(String var1, float var2);

    public void setProperty(String var1, double var2);

    public void setProperty(String var1, boolean var2);

    public <T> void setProperty(String var1, T var2);
}

