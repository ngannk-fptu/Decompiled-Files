/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina;

import java.io.File;
import java.util.concurrent.ScheduledExecutorService;
import javax.naming.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.Service;
import org.apache.catalina.deploy.NamingResourcesImpl;
import org.apache.catalina.startup.Catalina;

public interface Server
extends Lifecycle {
    public NamingResourcesImpl getGlobalNamingResources();

    public void setGlobalNamingResources(NamingResourcesImpl var1);

    public Context getGlobalNamingContext();

    public int getPort();

    public void setPort(int var1);

    public int getPortOffset();

    public void setPortOffset(int var1);

    public int getPortWithOffset();

    public String getAddress();

    public void setAddress(String var1);

    public String getShutdown();

    public void setShutdown(String var1);

    public ClassLoader getParentClassLoader();

    public void setParentClassLoader(ClassLoader var1);

    public Catalina getCatalina();

    public void setCatalina(Catalina var1);

    public File getCatalinaBase();

    public void setCatalinaBase(File var1);

    public File getCatalinaHome();

    public void setCatalinaHome(File var1);

    public int getUtilityThreads();

    public void setUtilityThreads(int var1);

    public void addService(Service var1);

    public void await();

    public Service findService(String var1);

    public Service[] findServices();

    public void removeService(Service var1);

    public Object getNamingToken();

    public ScheduledExecutorService getUtilityExecutor();
}

