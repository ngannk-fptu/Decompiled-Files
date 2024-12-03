/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.user.provider.file;

import com.opensymphony.user.provider.file.FileAccessProvider;
import com.opensymphony.user.provider.file.SerializableGroupsCache;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SerializableAccessProvider
extends FileAccessProvider {
    protected static final Log log = LogFactory.getLog((Class)(class$com$opensymphony$user$provider$file$SerializableAccessProvider == null ? (class$com$opensymphony$user$provider$file$SerializableAccessProvider = SerializableAccessProvider.class$("com.opensymphony.user.provider.file.SerializableAccessProvider")) : class$com$opensymphony$user$provider$file$SerializableAccessProvider));
    static /* synthetic */ Class class$com$opensymphony$user$provider$file$SerializableAccessProvider;

    public boolean init(Properties properties) {
        boolean retVal = super.init(properties);
        if (retVal) {
            this.groupCache = new SerializableGroupsCache(properties.getProperty("storeFile"), properties.getProperty("storeFileType"));
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

