/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.base.log;

import org.jfree.base.log.DefaultLog;
import org.jfree.base.log.LogConfiguration;
import org.jfree.base.modules.AbstractModule;
import org.jfree.base.modules.ModuleInitializeException;
import org.jfree.base.modules.SubSystem;
import org.jfree.util.Log;
import org.jfree.util.PrintStreamLogTarget;

public class DefaultLogModule
extends AbstractModule {
    static /* synthetic */ Class class$org$jfree$util$PrintStreamLogTarget;

    public DefaultLogModule() throws ModuleInitializeException {
        this.loadModuleInfo();
    }

    public void initialize(SubSystem subSystem) throws ModuleInitializeException {
        if (LogConfiguration.isDisableLogging()) {
            return;
        }
        if (LogConfiguration.getLogTarget().equals((class$org$jfree$util$PrintStreamLogTarget == null ? (class$org$jfree$util$PrintStreamLogTarget = DefaultLogModule.class$("org.jfree.util.PrintStreamLogTarget")) : class$org$jfree$util$PrintStreamLogTarget).getName())) {
            DefaultLog.installDefaultLog();
            Log.getInstance().addTarget(new PrintStreamLogTarget());
            if ("true".equals(subSystem.getGlobalConfig().getConfigProperty("org.jfree.base.LogAutoInit"))) {
                Log.getInstance().init();
            }
            Log.info("Default log target started ... previous log messages could have been ignored.");
        }
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

