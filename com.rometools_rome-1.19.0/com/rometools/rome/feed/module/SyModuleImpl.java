/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.rometools.utils.Dates
 */
package com.rometools.rome.feed.module;

import com.rometools.rome.feed.CopyFrom;
import com.rometools.rome.feed.impl.CopyFromHelper;
import com.rometools.rome.feed.module.Module;
import com.rometools.rome.feed.module.ModuleImpl;
import com.rometools.rome.feed.module.SyModule;
import com.rometools.utils.Dates;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SyModuleImpl
extends ModuleImpl
implements SyModule {
    private static final long serialVersionUID = 1L;
    private static final Set<String> PERIODS = new HashSet<String>();
    private static final CopyFromHelper COPY_FROM_HELPER;
    private String updatePeriod;
    private int updateFrequency;
    private Date updateBase;

    public SyModuleImpl() {
        super(SyModule.class, "http://purl.org/rss/1.0/modules/syndication/");
    }

    @Override
    public String getUpdatePeriod() {
        return this.updatePeriod;
    }

    @Override
    public void setUpdatePeriod(String updatePeriod) {
        if (!PERIODS.contains(updatePeriod)) {
            throw new IllegalArgumentException("Invalid period [" + updatePeriod + "]");
        }
        this.updatePeriod = updatePeriod;
    }

    @Override
    public int getUpdateFrequency() {
        return this.updateFrequency;
    }

    @Override
    public void setUpdateFrequency(int updateFrequency) {
        this.updateFrequency = updateFrequency;
    }

    @Override
    public Date getUpdateBase() {
        return Dates.copy((Date)this.updateBase);
    }

    @Override
    public void setUpdateBase(Date updateBase) {
        this.updateBase = Dates.copy((Date)updateBase);
    }

    public Class<? extends Module> getInterface() {
        return SyModule.class;
    }

    @Override
    public void copyFrom(CopyFrom obj) {
        COPY_FROM_HELPER.copy(this, obj);
    }

    static {
        PERIODS.add("hourly");
        PERIODS.add("daily");
        PERIODS.add("weekly");
        PERIODS.add("monthly");
        PERIODS.add("yearly");
        HashMap basePropInterfaceMap = new HashMap();
        basePropInterfaceMap.put("updatePeriod", String.class);
        basePropInterfaceMap.put("updateFrequency", Integer.TYPE);
        basePropInterfaceMap.put("updateBase", Date.class);
        Map<Class<? extends CopyFrom>, Class<?>> basePropClassImplMap = Collections.emptyMap();
        COPY_FROM_HELPER = new CopyFromHelper(SyModule.class, basePropInterfaceMap, basePropClassImplMap);
    }
}

