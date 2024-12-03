/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.server.sysinterface;

import java.io.Serializable;
import org.bedework.util.jmx.MBeanInfo;

public interface CalDAVSystemProperties
extends Serializable {
    public void setFeatureFlags(String var1);

    @MBeanInfo(value="Feature flags - see documentation")
    public String getFeatureFlags();

    public void setAdminContact(String var1);

    @MBeanInfo(value="Administrator contact property")
    public String getAdminContact();

    public void setTzServeruri(String var1);

    @MBeanInfo(value="the timezones server uri")
    public String getTzServeruri();

    public void setTimezonesByReference(boolean var1);

    @MBeanInfo(value="true if we are NOT including the full tz specification in iCalendar output")
    public boolean getTimezonesByReference();

    public void setIscheduleURI(String var1);

    @MBeanInfo(value="ischedule service uri - null for no ischedule service")
    public String getIscheduleURI();

    public void setFburlServiceURI(String var1);

    @MBeanInfo(value="Free busy service uri - null for no freebusy service")
    public String getFburlServiceURI();

    public void setWebcalServiceURI(String var1);

    @MBeanInfo(value="Web calendar service uri - null for no web calendar service")
    public String getWebcalServiceURI();

    public void setCalSoapWsURI(String var1);

    @MBeanInfo(value="Calws soap web service uri - null for no service")
    public String getCalSoapWsURI();

    public void setVpollMaxItems(Integer var1);

    @MBeanInfo(value="Max number of items per vpoll. null for no limit")
    public Integer getVpollMaxItems();

    public void setVpollMaxActive(Integer var1);

    @MBeanInfo(value="Max number of voters per vpolls. null for no limit")
    public Integer getVpollMaxActive();

    public void setVpollMaxVoters(Integer var1);

    @MBeanInfo(value="Max number of voters per vpolls. null for no limit")
    public Integer getVpollMaxVoters();
}

