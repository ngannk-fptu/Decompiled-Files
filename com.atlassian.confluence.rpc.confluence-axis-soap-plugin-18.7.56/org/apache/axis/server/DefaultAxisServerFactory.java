/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.server;

import java.io.File;
import java.util.Map;
import org.apache.axis.AxisFault;
import org.apache.axis.AxisProperties;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.server.AxisServer;
import org.apache.axis.server.AxisServerFactory;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public class DefaultAxisServerFactory
implements AxisServerFactory {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$server$DefaultAxisServerFactory == null ? (class$org$apache$axis$server$DefaultAxisServerFactory = DefaultAxisServerFactory.class$("org.apache.axis.server.DefaultAxisServerFactory")) : class$org$apache$axis$server$DefaultAxisServerFactory).getName());
    static /* synthetic */ Class class$org$apache$axis$server$DefaultAxisServerFactory;

    public AxisServer getServer(Map environment) throws AxisFault {
        log.debug((Object)"Enter: DefaultAxisServerFactory::getServer");
        AxisServer ret = DefaultAxisServerFactory.createServer(environment);
        if (ret != null) {
            File attdirFile;
            String attachmentsdir;
            if (environment != null) {
                ret.setOptionDefault("attachments.Directory", (String)environment.get("axis.attachments.Directory"));
                ret.setOptionDefault("attachments.Directory", (String)environment.get("servlet.realpath"));
            }
            if ((attachmentsdir = (String)ret.getOption("attachments.Directory")) != null && !(attdirFile = new File(attachmentsdir)).isDirectory()) {
                attdirFile.mkdirs();
            }
        }
        log.debug((Object)"Exit: DefaultAxisServerFactory::getServer");
        return ret;
    }

    private static AxisServer createServer(Map environment) {
        EngineConfiguration config = DefaultAxisServerFactory.getEngineConfiguration(environment);
        return config == null ? new AxisServer() : new AxisServer(config);
    }

    private static EngineConfiguration getEngineConfiguration(Map environment) {
        String configClass;
        log.debug((Object)"Enter: DefaultAxisServerFactory::getEngineConfiguration");
        EngineConfiguration config = null;
        if (environment != null) {
            try {
                config = (EngineConfiguration)environment.get("engineConfig");
            }
            catch (ClassCastException e) {
                log.warn((Object)Messages.getMessage("engineConfigWrongClass00"), (Throwable)e);
            }
        }
        if (config == null && (configClass = AxisProperties.getProperty("axis.engineConfigClass")) != null) {
            try {
                Class cls = ClassUtils.forName(configClass);
                config = (EngineConfiguration)cls.newInstance();
            }
            catch (ClassNotFoundException e) {
                log.warn((Object)Messages.getMessage("engineConfigNoClass00", configClass), (Throwable)e);
            }
            catch (InstantiationException e) {
                log.warn((Object)Messages.getMessage("engineConfigNoInstance00", configClass), (Throwable)e);
            }
            catch (IllegalAccessException e) {
                log.warn((Object)Messages.getMessage("engineConfigIllegalAccess00", configClass), (Throwable)e);
            }
            catch (ClassCastException e) {
                log.warn((Object)Messages.getMessage("engineConfigWrongClass01", configClass), (Throwable)e);
            }
        }
        log.debug((Object)"Exit: DefaultAxisServerFactory::getEngineConfiguration");
        return config;
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

