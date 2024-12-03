/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.server.sysinterface;

import java.io.Serializable;
import org.bedework.util.jmx.MBeanInfo;

public interface CalDAVAuthProperties
extends Serializable {
    public void setMaxUserEntitySize(Integer var1);

    @MBeanInfo(value="Max entity length for users. Probably an estimate. null for no limit")
    public Integer getMaxUserEntitySize();

    public void setMaxInstances(Integer var1);

    @MBeanInfo(value="Max number recurrence instances. null for no limit")
    public Integer getMaxInstances();

    public void setMaxAttendeesPerInstance(Integer var1);

    @MBeanInfo(value="Max number attendees per instance. null for no limit")
    public Integer getMaxAttendeesPerInstance();

    public void setMinDateTime(String var1);

    @MBeanInfo(value="Minimum date time allowed. null for no limit")
    public String getMinDateTime();

    public void setMaxDateTime(String var1);

    @MBeanInfo(value="Maximum date time allowed. null for no limit")
    public String getMaxDateTime();

    public void setDefaultFBPeriod(Integer var1);

    @MBeanInfo(value="Default freebusy fetch period. null for no limit")
    public Integer getDefaultFBPeriod();

    public void setMaxFBPeriod(Integer var1);

    @MBeanInfo(value="Maximum freebusy fetch period.")
    public Integer getMaxFBPeriod();

    public void setDefaultWebCalPeriod(Integer var1);

    @MBeanInfo(value="Default webcal fetch period. null for no limit")
    public Integer getDefaultWebCalPeriod();

    public void setMaxWebCalPeriod(Integer var1);

    @MBeanInfo(value="Maximum webcal fetch period. null for no limit")
    public Integer getMaxWebCalPeriod();

    public void setDirectoryBrowsingDisallowed(boolean var1);

    @MBeanInfo(value="true if directory browsing is NOT allowed")
    public boolean getDirectoryBrowsingDisallowed();
}

