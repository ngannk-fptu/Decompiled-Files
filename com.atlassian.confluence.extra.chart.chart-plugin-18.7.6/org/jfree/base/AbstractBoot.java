/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.base;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import org.jfree.base.BootableProjectInfo;
import org.jfree.base.config.HierarchicalConfiguration;
import org.jfree.base.config.PropertyFileConfiguration;
import org.jfree.base.config.SystemPropertyConfiguration;
import org.jfree.base.modules.PackageManager;
import org.jfree.base.modules.SubSystem;
import org.jfree.util.Configuration;
import org.jfree.util.ExtendedConfiguration;
import org.jfree.util.ExtendedConfigurationWrapper;
import org.jfree.util.Log;
import org.jfree.util.ObjectUtilities;

public abstract class AbstractBoot
implements SubSystem {
    private ExtendedConfigurationWrapper extWrapper;
    private PackageManager packageManager;
    private Configuration globalConfig;
    private boolean bootInProgress;
    private boolean bootDone;
    static /* synthetic */ Class class$org$jfree$base$config$PropertyFileConfiguration;

    protected AbstractBoot() {
    }

    public synchronized PackageManager getPackageManager() {
        if (this.packageManager == null) {
            this.packageManager = PackageManager.createInstance(this);
        }
        return this.packageManager;
    }

    public synchronized Configuration getGlobalConfig() {
        if (this.globalConfig == null) {
            this.globalConfig = this.loadConfiguration();
        }
        return this.globalConfig;
    }

    public final synchronized boolean isBootInProgress() {
        return this.bootInProgress;
    }

    public final synchronized boolean isBootDone() {
        return this.bootDone;
    }

    protected abstract Configuration loadConfiguration();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void start() {
        AbstractBoot abstractBoot = this;
        synchronized (abstractBoot) {
            if (this.isBootDone()) {
                return;
            }
            while (this.isBootInProgress()) {
                try {
                    this.wait();
                }
                catch (InterruptedException e) {}
            }
            if (this.isBootDone()) {
                return;
            }
            this.bootInProgress = true;
        }
        BootableProjectInfo info = this.getProjectInfo();
        if (info != null) {
            BootableProjectInfo[] childs = info.getDependencies();
            for (int i = 0; i < childs.length; ++i) {
                AbstractBoot boot = this.loadBooter(childs[i].getBootClass());
                if (boot == null) continue;
                AbstractBoot abstractBoot2 = boot;
                synchronized (abstractBoot2) {
                    boot.start();
                    while (!boot.isBootDone()) {
                        try {
                            boot.wait();
                        }
                        catch (InterruptedException e) {}
                    }
                    continue;
                }
            }
        }
        this.performBoot();
        if (info != null) {
            Log.info(info.getName() + " " + info.getVersion() + " started.");
        } else {
            Log.info(this.getClass() + " started.");
        }
        AbstractBoot abstractBoot3 = this;
        synchronized (abstractBoot3) {
            this.bootInProgress = false;
            this.bootDone = true;
            this.notifyAll();
        }
    }

    protected abstract void performBoot();

    protected abstract BootableProjectInfo getProjectInfo();

    protected AbstractBoot loadBooter(String classname) {
        if (classname == null) {
            return null;
        }
        try {
            Class<?> c = ObjectUtilities.getClassLoader(this.getClass()).loadClass(classname);
            Method m = c.getMethod("getInstance", null);
            return (AbstractBoot)m.invoke(null, (Object[])null);
        }
        catch (Exception e) {
            Log.info("Unable to boot dependent class: " + classname);
            return null;
        }
    }

    protected Configuration createDefaultHierarchicalConfiguration(String staticConfig, String userConfig, boolean addSysProps) {
        return this.createDefaultHierarchicalConfiguration(staticConfig, userConfig, addSysProps, class$org$jfree$base$config$PropertyFileConfiguration == null ? (class$org$jfree$base$config$PropertyFileConfiguration = AbstractBoot.class$("org.jfree.base.config.PropertyFileConfiguration")) : class$org$jfree$base$config$PropertyFileConfiguration);
    }

    protected Configuration createDefaultHierarchicalConfiguration(String staticConfig, String userConfig, boolean addSysProps, Class source) {
        HierarchicalConfiguration globalConfig = new HierarchicalConfiguration();
        if (staticConfig != null) {
            PropertyFileConfiguration rootProperty = new PropertyFileConfiguration();
            rootProperty.load(staticConfig, this.getClass());
            globalConfig.insertConfiguration(rootProperty);
            globalConfig.insertConfiguration(this.getPackageManager().getPackageConfiguration());
        }
        if (userConfig != null) {
            String userConfigStripped = userConfig.startsWith("/") ? userConfig.substring(1) : userConfig;
            try {
                PropertyFileConfiguration baseProperty;
                Enumeration<URL> userConfigs = ObjectUtilities.getClassLoader(this.getClass()).getResources(userConfigStripped);
                ArrayList<PropertyFileConfiguration> configs = new ArrayList<PropertyFileConfiguration>();
                while (userConfigs.hasMoreElements()) {
                    URL url = userConfigs.nextElement();
                    try {
                        baseProperty = new PropertyFileConfiguration();
                        InputStream in = url.openStream();
                        baseProperty.load(in);
                        in.close();
                        configs.add(baseProperty);
                    }
                    catch (IOException ioe) {
                        Log.warn("Failed to load the user configuration at " + url, ioe);
                    }
                }
                for (int i = configs.size() - 1; i >= 0; --i) {
                    baseProperty = (PropertyFileConfiguration)configs.get(i);
                    globalConfig.insertConfiguration(baseProperty);
                }
            }
            catch (IOException e) {
                Log.warn("Failed to lookup the user configurations.", e);
            }
        }
        if (addSysProps) {
            SystemPropertyConfiguration systemConfig = new SystemPropertyConfiguration();
            globalConfig.insertConfiguration(systemConfig);
        }
        return globalConfig;
    }

    public synchronized ExtendedConfiguration getExtendedConfig() {
        if (this.extWrapper == null) {
            this.extWrapper = new ExtendedConfigurationWrapper(this.getGlobalConfig());
        }
        return this.extWrapper;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

