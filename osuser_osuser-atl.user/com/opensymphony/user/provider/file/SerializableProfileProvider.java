/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.user.provider.file;

import com.opensymphony.user.provider.file.FileProfileProvider;
import com.opensymphony.user.provider.file.SerializablePropertySetCache;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SerializableProfileProvider
extends FileProfileProvider {
    protected static final Log log = LogFactory.getLog((Class)(class$com$opensymphony$user$provider$file$SerializableProfileProvider == null ? (class$com$opensymphony$user$provider$file$SerializableProfileProvider = SerializableProfileProvider.class$("com.opensymphony.user.provider.file.SerializableProfileProvider")) : class$com$opensymphony$user$provider$file$SerializableProfileProvider));
    static /* synthetic */ Class class$com$opensymphony$user$provider$file$SerializableProfileProvider;

    public boolean init(Properties properties) {
        boolean retVal = super.init(properties);
        if (retVal) {
            this.propertySetCache = new SerializablePropertySetCache(properties.getProperty("storeFile"), properties.getProperty("storeFileType"));
            return true;
        }
        return false;
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

