/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.storeconfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Timestamp;
import org.apache.tomcat.util.res.StringManager;

public class StoreFileMover {
    protected static final StringManager sm = StringManager.getManager((String)"org.apache.catalina.storeconfig");
    private String filename = "conf/server.xml";
    private String encoding = "UTF-8";
    private String basename = System.getProperty("catalina.base");
    private File configOld;
    private File configNew;
    private File configSave;

    public File getConfigNew() {
        return this.configNew;
    }

    public File getConfigOld() {
        return this.configOld;
    }

    public File getConfigSave() {
        return this.configSave;
    }

    public String getBasename() {
        return this.basename;
    }

    public void setBasename(String basename) {
        this.basename = basename;
    }

    public String getFilename() {
        return this.filename;
    }

    public void setFilename(String string) {
        this.filename = string;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void setEncoding(String string) {
        this.encoding = string;
    }

    public StoreFileMover(String basename, String filename, String encoding) {
        this.setBasename(basename);
        this.setEncoding(encoding);
        this.setFilename(filename);
        this.init();
    }

    public StoreFileMover() {
        this.init();
    }

    public void init() {
        String configFile = this.getFilename();
        this.configOld = new File(configFile);
        if (!this.configOld.isAbsolute()) {
            this.configOld = new File(this.getBasename(), configFile);
        }
        this.configNew = new File(configFile + ".new");
        if (!this.configNew.isAbsolute()) {
            this.configNew = new File(this.getBasename(), configFile + ".new");
        }
        if (!this.configNew.getParentFile().exists() && !this.configNew.getParentFile().mkdirs()) {
            throw new IllegalStateException(sm.getString("storeFileMover.directoryCreationError", new Object[]{this.configNew}));
        }
        String sb = this.getTimeTag();
        this.configSave = new File(configFile + sb);
        if (!this.configSave.isAbsolute()) {
            this.configSave = new File(this.getBasename(), configFile + sb);
        }
    }

    public void move() throws IOException {
        if (this.configOld.renameTo(this.configSave)) {
            if (!this.configNew.renameTo(this.configOld)) {
                this.configSave.renameTo(this.configOld);
                throw new IOException(sm.getString("storeFileMover.renameError", new Object[]{this.configNew.getAbsolutePath(), this.configOld.getAbsolutePath()}));
            }
        } else if (!this.configOld.exists()) {
            if (!this.configNew.renameTo(this.configOld)) {
                throw new IOException(sm.getString("storeFileMover.renameError", new Object[]{this.configNew.getAbsolutePath(), this.configOld.getAbsolutePath()}));
            }
        } else {
            throw new IOException(sm.getString("storeFileMover.renameError", new Object[]{this.configOld.getAbsolutePath(), this.configSave.getAbsolutePath()}));
        }
    }

    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(new OutputStreamWriter((OutputStream)new FileOutputStream(this.configNew), this.getEncoding()));
    }

    protected String getTimeTag() {
        String ts = new Timestamp(System.currentTimeMillis()).toString();
        StringBuilder sb = new StringBuilder(".");
        sb.append(ts, 0, 10);
        sb.append('.');
        sb.append(ts, 11, 13);
        sb.append('-');
        sb.append(ts, 14, 16);
        sb.append('-');
        sb.append(ts, 17, 19);
        return sb.toString();
    }
}

