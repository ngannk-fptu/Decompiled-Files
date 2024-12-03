/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.config;

import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import com.opensymphony.xwork2.util.location.Location;
import com.opensymphony.xwork2.util.location.LocationImpl;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsException;
import org.apache.struts2.config.Settings;

class PropertiesSettings
implements Settings {
    private static final Logger LOG = LogManager.getLogger(PropertiesSettings.class);
    private LocatableProperties settings;

    public PropertiesSettings(String name) {
        URL settingsUrl = ClassLoaderUtil.getResource(name + ".properties", this.getClass());
        if (settingsUrl == null) {
            LOG.debug("{}.properties missing", (Object)name);
            this.settings = new LocatableProperties();
            return;
        }
        this.settings = new LocatableProperties(new LocationImpl(null, settingsUrl.toString()));
        try (InputStream in = settingsUrl.openStream();){
            this.settings.load(in);
        }
        catch (IOException e) {
            throw new StrutsException("Could not load " + name + ".properties: " + e, e);
        }
    }

    @Override
    public String get(String aName) throws IllegalArgumentException {
        return this.settings.getProperty(aName);
    }

    @Override
    public Location getLocation(String aName) throws IllegalArgumentException {
        return this.settings.getPropertyLocation(aName);
    }

    @Override
    public Iterator list() {
        return this.settings.keySet().iterator();
    }
}

