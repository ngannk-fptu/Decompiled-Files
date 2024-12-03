/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.cfg;

import com.mchange.v2.cfg.DelayedLogItem;
import com.mchange.v2.cfg.MultiPropertiesConfig;
import com.mchange.v2.cfg.PropertiesConfigSource;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Properties;

public final class BasicPropertiesConfigSource
implements PropertiesConfigSource {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PropertiesConfigSource.Parse propertiesFromSource(String string) throws FileNotFoundException, Exception {
        InputStream inputStream = MultiPropertiesConfig.class.getResourceAsStream(string);
        if (inputStream != null) {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            Properties properties = new Properties();
            LinkedList<DelayedLogItem> linkedList = new LinkedList<DelayedLogItem>();
            try {
                properties.load(bufferedInputStream);
            }
            finally {
                try {
                    if (bufferedInputStream != null) {
                        ((InputStream)bufferedInputStream).close();
                    }
                }
                catch (IOException iOException) {
                    linkedList.add(new DelayedLogItem(DelayedLogItem.Level.WARNING, "An IOException occurred while closing InputStream from resource path '" + string + "'.", iOException));
                }
            }
            return new PropertiesConfigSource.Parse(properties, linkedList);
        }
        throw new FileNotFoundException(String.format("Resource not found at path '%s'.", string));
    }
}

