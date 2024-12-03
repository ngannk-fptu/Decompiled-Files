/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.Container
 *  org.apache.catalina.core.StandardContext
 *  org.apache.catalina.core.StandardHost
 */
package org.apache.catalina.storeconfig;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import org.apache.catalina.Container;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.storeconfig.StoreAppender;
import org.apache.catalina.storeconfig.StoreDescription;

public class StoreContextAppender
extends StoreAppender {
    @Override
    protected void printAttribute(PrintWriter writer, int indent, Object bean, StoreDescription desc, String attributeName, Object bean2, Object value) {
        if (this.isPrintValue(bean, bean2, attributeName, desc)) {
            String docBase;
            if (attributeName.equals("docBase") && bean instanceof StandardContext && (docBase = ((StandardContext)bean).getOriginalDocBase()) != null) {
                value = docBase;
            }
            this.printValue(writer, indent, attributeName, value);
        }
    }

    @Override
    public boolean isPrintValue(Object bean, Object bean2, String attrName, StoreDescription desc) {
        boolean isPrint = super.isPrintValue(bean, bean2, attrName, desc);
        if (isPrint) {
            Container host;
            StandardContext context = (StandardContext)bean;
            if ("workDir".equals(attrName)) {
                String defaultWorkDir = this.getDefaultWorkDir(context);
                if (defaultWorkDir != null) {
                    isPrint = !defaultWorkDir.equals(context.getWorkDir());
                }
            } else if ("path".equals(attrName)) {
                isPrint = desc.isStoreSeparate() && desc.isExternalAllowed() && context.getConfigFile() == null;
            } else if ("docBase".equals(attrName) && (host = context.getParent()) instanceof StandardHost) {
                File docBase;
                File appBase = this.getAppBase((StandardHost)host);
                isPrint = !appBase.equals((docBase = this.getDocBase(context, appBase)).getParentFile());
            }
        }
        return isPrint;
    }

    protected File getAppBase(StandardHost host) {
        File appBase;
        File file = new File(host.getAppBase());
        if (!file.isAbsolute()) {
            file = new File(System.getProperty("catalina.base"), host.getAppBase());
        }
        try {
            appBase = file.getCanonicalFile();
        }
        catch (IOException e) {
            appBase = file;
        }
        return appBase;
    }

    protected File getDocBase(StandardContext context, File appBase) {
        File docBase;
        File file;
        String contextDocBase = context.getOriginalDocBase();
        if (contextDocBase == null) {
            contextDocBase = context.getDocBase();
        }
        if (!(file = new File(contextDocBase)).isAbsolute()) {
            file = new File(appBase, contextDocBase);
        }
        try {
            docBase = file.getCanonicalFile();
        }
        catch (IOException e) {
            docBase = file;
        }
        return docBase;
    }

    protected String getDefaultWorkDir(StandardContext context) {
        Container host;
        String defaultWorkDir = null;
        String contextWorkDir = context.getName();
        if (contextWorkDir.length() == 0) {
            contextWorkDir = "_";
        }
        if (contextWorkDir.startsWith("/")) {
            contextWorkDir = contextWorkDir.substring(1);
        }
        if ((host = context.getParent()) instanceof StandardHost) {
            String hostWorkDir = ((StandardHost)host).getWorkDir();
            if (hostWorkDir != null) {
                defaultWorkDir = hostWorkDir + File.separator + contextWorkDir;
            } else {
                String engineName = context.getParent().getParent().getName();
                String hostName = context.getParent().getName();
                defaultWorkDir = "work" + File.separator + engineName + File.separator + hostName + File.separator + contextWorkDir;
            }
        }
        return defaultWorkDir;
    }

    @Override
    public Object defaultInstance(Object bean) throws ReflectiveOperationException {
        if (bean instanceof StandardContext) {
            StandardContext defaultContext = new StandardContext();
            return defaultContext;
        }
        return super.defaultInstance(bean);
    }
}

