/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.exception.ResourceNotFoundException
 *  org.apache.velocity.runtime.log.Log
 */
package org.apache.velocity.tools.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.tools.ConversionUtils;
import org.apache.velocity.tools.config.FactoryConfiguration;

public abstract class FileFactoryConfiguration
extends FactoryConfiguration {
    protected FileFactoryConfiguration(Class clazz, String id) {
        super(clazz, id);
    }

    public abstract void read(InputStream var1) throws IOException;

    public void read(String path) {
        this.read(path, true);
    }

    public void read(URL url) {
        this.read(url, true);
    }

    public void read(String path, boolean required) {
        this.read(path, required, null);
    }

    public void read(URL url, boolean required) {
        this.read(url, required, null);
    }

    public void read(String path, boolean required, Log log) {
        URL url;
        if (path == null) {
            throw new NullPointerException("Path value cannot be null");
        }
        if (log != null && log.isTraceEnabled()) {
            log.trace((Object)("Attempting to read configuration file at: " + path));
        }
        if ((url = this.findURL(path)) != null) {
            this.read(url, required, log);
        } else {
            String msg = "Could not find configuration file at: " + path;
            if (log != null) {
                log.debug((Object)msg);
            }
            if (required) {
                throw new ResourceNotFoundException(msg);
            }
        }
    }

    protected URL findURL(String path) {
        return ConversionUtils.toURL(path, this);
    }

    protected void read(URL url, boolean required, Log log) {
        block3: {
            try {
                this.read(url, url.openStream(), required, log);
                this.addSource("    .read(" + url.toString() + ")");
            }
            catch (IOException ioe) {
                String msg = "Could not open stream from: " + url;
                if (log != null) {
                    log.debug((Object)msg, (Throwable)ioe);
                }
                if (!required) break block3;
                throw new RuntimeException(msg, ioe);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void read(Object source, InputStream inputStream, boolean required, Log log) {
        try {
            this.read(inputStream);
        }
        catch (IOException ioe) {
            String msg = "InputStream could not be read from: " + source;
            if (log != null) {
                log.debug((Object)msg, (Throwable)ioe);
            }
            if (required) {
                throw new RuntimeException(msg, ioe);
            }
        }
        finally {
            block15: {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
                catch (IOException ioe) {
                    if (log == null) break block15;
                    log.error((Object)("Failed to close input stream for " + source), (Throwable)ioe);
                }
            }
        }
    }
}

