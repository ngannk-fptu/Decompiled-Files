/*
 * Decompiled with CFR 0.152.
 */
package com.sun.syndication.feed.module;

import com.sun.syndication.feed.module.Module;
import java.util.Date;

public interface SyModule
extends Module {
    public static final String URI = "http://purl.org/rss/1.0/modules/syndication/";
    public static final String HOURLY = new String("hourly");
    public static final String DAILY = new String("daily");
    public static final String WEEKLY = new String("weekly");
    public static final String MONTHLY = new String("monthly");
    public static final String YEARLY = new String("yearly");

    public String getUpdatePeriod();

    public void setUpdatePeriod(String var1);

    public int getUpdateFrequency();

    public void setUpdateFrequency(int var1);

    public Date getUpdateBase();

    public void setUpdateBase(Date var1);
}

