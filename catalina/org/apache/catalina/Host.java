/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;
import org.apache.catalina.Container;

public interface Host
extends Container {
    public static final String ADD_ALIAS_EVENT = "addAlias";
    public static final String REMOVE_ALIAS_EVENT = "removeAlias";

    public String getXmlBase();

    public void setXmlBase(String var1);

    public File getConfigBaseFile();

    public String getAppBase();

    public File getAppBaseFile();

    public void setAppBase(String var1);

    public boolean getAutoDeploy();

    public void setAutoDeploy(boolean var1);

    public String getConfigClass();

    public void setConfigClass(String var1);

    public boolean getDeployOnStartup();

    public void setDeployOnStartup(boolean var1);

    public String getDeployIgnore();

    public Pattern getDeployIgnorePattern();

    public void setDeployIgnore(String var1);

    public ExecutorService getStartStopExecutor();

    public boolean getCreateDirs();

    public void setCreateDirs(boolean var1);

    public boolean getUndeployOldVersions();

    public void setUndeployOldVersions(boolean var1);

    public void addAlias(String var1);

    public String[] findAliases();

    public void removeAlias(String var1);
}

