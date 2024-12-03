/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections.ExtendedProperties
 */
package org.apache.velocity.runtime.resource.loader;

import java.io.InputStream;
import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.ResourceCacheImpl;

public abstract class ResourceLoader {
    protected boolean isCachingOn = false;
    protected long modificationCheckInterval = 2L;
    protected String className = null;
    protected RuntimeServices rsvc = null;
    protected Log log = null;

    public void commonInit(RuntimeServices rs, ExtendedProperties configuration) {
        this.rsvc = rs;
        this.log = this.rsvc.getLog();
        try {
            this.isCachingOn = configuration.getBoolean("cache", false);
        }
        catch (Exception e) {
            this.isCachingOn = false;
            String msg = "Exception parsing cache setting: " + configuration.getString("cache");
            this.log.error(msg, e);
            throw new VelocityException(msg, e);
        }
        try {
            this.modificationCheckInterval = configuration.getLong("modificationCheckInterval", 0L);
        }
        catch (Exception e) {
            this.modificationCheckInterval = 0L;
            String msg = "Exception parsing modificationCheckInterval setting: " + configuration.getString("modificationCheckInterval");
            this.log.error(msg, e);
            throw new VelocityException(msg, e);
        }
        this.className = ResourceCacheImpl.class.getName();
        try {
            this.className = configuration.getString("class", this.className);
        }
        catch (Exception e) {
            String msg = "Exception retrieving resource cache class name";
            this.log.error(msg, e);
            throw new VelocityException(msg, e);
        }
    }

    public abstract void init(ExtendedProperties var1);

    public abstract InputStream getResourceStream(String var1) throws ResourceNotFoundException;

    public abstract boolean isSourceModified(Resource var1);

    public abstract long getLastModified(Resource var1);

    public String getClassName() {
        return this.className;
    }

    public void setCachingOn(boolean value) {
        this.isCachingOn = value;
    }

    public boolean isCachingOn() {
        return this.isCachingOn;
    }

    public void setModificationCheckInterval(long modificationCheckInterval) {
        this.modificationCheckInterval = modificationCheckInterval;
    }

    public long getModificationCheckInterval() {
        return this.modificationCheckInterval;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean resourceExists(String resourceName) {
        InputStream is = null;
        try {
            is = this.getResourceStream(resourceName);
        }
        catch (ResourceNotFoundException e) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("Could not load resource '" + resourceName + "' from ResourceLoader " + this.getClass().getName() + ": ", e);
            }
        }
        finally {
            block14: {
                try {
                    if (is != null) {
                        is.close();
                    }
                }
                catch (Exception e) {
                    if (!this.log.isErrorEnabled()) break block14;
                    String msg = "While closing InputStream for resource '" + resourceName + "' from ResourceLoader " + this.getClass().getName();
                    this.log.error(msg, e);
                    throw new VelocityException(msg, e);
                }
            }
        }
        return is != null;
    }
}

