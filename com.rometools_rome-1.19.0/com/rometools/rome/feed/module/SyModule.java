/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.rome.feed.module;

import com.rometools.rome.feed.module.Module;
import java.util.Date;

public interface SyModule
extends Module {
    public static final String URI = "http://purl.org/rss/1.0/modules/syndication/";
    public static final String HOURLY = "hourly";
    public static final String DAILY = "daily";
    public static final String WEEKLY = "weekly";
    public static final String MONTHLY = "monthly";
    public static final String YEARLY = "yearly";

    public String getUpdatePeriod();

    public void setUpdatePeriod(String var1);

    public int getUpdateFrequency();

    public void setUpdateFrequency(int var1);

    public Date getUpdateBase();

    public void setUpdateBase(Date var1);
}

