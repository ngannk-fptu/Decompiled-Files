/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.base;

import org.jfree.JCommon;
import org.jfree.base.AbstractBoot;
import org.jfree.base.BootableProjectInfo;
import org.jfree.base.config.ModifiableConfiguration;
import org.jfree.util.Configuration;
import org.jfree.util.ObjectUtilities;

public class BaseBoot
extends AbstractBoot {
    private static BaseBoot singleton;
    private BootableProjectInfo bootableProjectInfo = JCommon.INFO;
    static /* synthetic */ Class class$org$jfree$base$BaseBoot;
    static /* synthetic */ Class class$org$jfree$base$log$DefaultLogModule;

    private BaseBoot() {
    }

    public static ModifiableConfiguration getConfiguration() {
        return (ModifiableConfiguration)BaseBoot.getInstance().getGlobalConfig();
    }

    protected synchronized Configuration loadConfiguration() {
        return this.createDefaultHierarchicalConfiguration("/org/jfree/base/jcommon.properties", "/jcommon.properties", true, class$org$jfree$base$BaseBoot == null ? (class$org$jfree$base$BaseBoot = BaseBoot.class$("org.jfree.base.BaseBoot")) : class$org$jfree$base$BaseBoot);
    }

    public static synchronized AbstractBoot getInstance() {
        if (singleton == null) {
            singleton = new BaseBoot();
        }
        return singleton;
    }

    protected void performBoot() {
        ObjectUtilities.setClassLoaderSource(BaseBoot.getConfiguration().getConfigProperty("org.jfree.ClassLoader"));
        this.getPackageManager().addModule((class$org$jfree$base$log$DefaultLogModule == null ? (class$org$jfree$base$log$DefaultLogModule = BaseBoot.class$("org.jfree.base.log.DefaultLogModule")) : class$org$jfree$base$log$DefaultLogModule).getName());
        this.getPackageManager().load("org.jfree.jcommon.modules.");
        this.getPackageManager().initializeModules();
    }

    protected BootableProjectInfo getProjectInfo() {
        return this.bootableProjectInfo;
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

