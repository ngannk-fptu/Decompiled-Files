/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.compat.JrePlatform
 *  org.apache.tomcat.util.http.RequestUtil
 */
package org.apache.catalina.webresources;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.webresources.AbstractResourceSet;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.compat.JrePlatform;
import org.apache.tomcat.util.http.RequestUtil;

public abstract class AbstractFileResourceSet
extends AbstractResourceSet {
    private static final Log log = LogFactory.getLog(AbstractFileResourceSet.class);
    protected static final String[] EMPTY_STRING_ARRAY = new String[0];
    private File fileBase;
    private String absoluteBase;
    private String canonicalBase;
    private boolean readOnly = false;

    protected AbstractFileResourceSet(String internalPath) {
        this.setInternalPath(internalPath);
    }

    protected final File getFileBase() {
        return this.fileBase;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    @Override
    public boolean isReadOnly() {
        return this.readOnly;
    }

    protected final File file(String name, boolean mustExist) {
        if (name.equals("/")) {
            name = "";
        }
        File file = new File(this.fileBase, name);
        if (name.endsWith("/") && file.isFile()) {
            return null;
        }
        if (mustExist && !file.canRead()) {
            return null;
        }
        if (this.getRoot().getAllowLinking()) {
            return file;
        }
        if (JrePlatform.IS_WINDOWS && this.isInvalidWindowsFilename(name)) {
            return null;
        }
        String canPath = null;
        try {
            canPath = file.getCanonicalPath();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        if (canPath == null || !canPath.startsWith(this.canonicalBase)) {
            return null;
        }
        String absPath = this.normalize(file.getAbsolutePath());
        if (absPath == null || this.absoluteBase.length() > absPath.length()) {
            return null;
        }
        absPath = absPath.substring(this.absoluteBase.length());
        if ((canPath = canPath.substring(this.canonicalBase.length())).length() > 0 && canPath.charAt(0) != File.separatorChar) {
            return null;
        }
        if (canPath.length() > 0) {
            canPath = this.normalize(canPath);
        }
        if (!canPath.equals(absPath)) {
            if (!canPath.equalsIgnoreCase(absPath)) {
                this.logIgnoredSymlink(this.getRoot().getContext().getName(), absPath, canPath);
            }
            return null;
        }
        return file;
    }

    protected void logIgnoredSymlink(String contextPath, String absPath, String canPath) {
        String msg = sm.getString("abstractFileResourceSet.canonicalfileCheckFailed", new Object[]{contextPath, absPath, canPath});
        if (absPath.startsWith("/META-INF/") || absPath.startsWith("/WEB-INF/")) {
            log.error((Object)msg);
        } else {
            log.warn((Object)msg);
        }
    }

    private boolean isInvalidWindowsFilename(String name) {
        int len = name.length();
        if (len == 0) {
            return false;
        }
        for (int i = 0; i < len; ++i) {
            char c = name.charAt(i);
            if (c != '\"' && c != '<' && c != '>' && c != ':') continue;
            return true;
        }
        return name.charAt(len - 1) == ' ';
    }

    private String normalize(String path) {
        return RequestUtil.normalize((String)path, (File.separatorChar == '\\' ? 1 : 0) != 0);
    }

    @Override
    public URL getBaseUrl() {
        try {
            return this.getFileBase().toURI().toURL();
        }
        catch (MalformedURLException e) {
            return null;
        }
    }

    @Override
    public void gc() {
    }

    @Override
    protected void initInternal() throws LifecycleException {
        this.fileBase = new File(this.getBase(), this.getInternalPath());
        this.checkType(this.fileBase);
        this.absoluteBase = this.normalize(this.fileBase.getAbsolutePath());
        try {
            this.canonicalBase = this.fileBase.getCanonicalPath();
        }
        catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        if ("/".equals(this.absoluteBase)) {
            this.absoluteBase = "";
        }
        if ("/".equals(this.canonicalBase)) {
            this.canonicalBase = "";
        }
    }

    protected abstract void checkType(File var1);
}

