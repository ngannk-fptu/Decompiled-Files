/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.commons.text.lookup;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.lookup.AbstractStringLookup;
import org.apache.commons.text.lookup.IllegalArgumentExceptions;

final class PropertiesStringLookup
extends AbstractStringLookup {
    static final PropertiesStringLookup INSTANCE = new PropertiesStringLookup();
    static final String SEPARATOR = "::";

    static String toPropertyKey(String file, String key) {
        return AbstractStringLookup.toLookupKey(file, SEPARATOR, key);
    }

    private PropertiesStringLookup() {
    }

    @Override
    public String lookup(String key) {
        if (key == null) {
            return null;
        }
        String[] keys = key.split(SEPARATOR);
        int keyLen = keys.length;
        if (keyLen < 2) {
            throw IllegalArgumentExceptions.format("Bad properties key format [%s]; expected format is %s.", key, PropertiesStringLookup.toPropertyKey("DocumentPath", "Key"));
        }
        String documentPath = keys[0];
        String propertyKey = StringUtils.substringAfter((String)key, (String)SEPARATOR);
        try {
            Properties properties = new Properties();
            try (InputStream inputStream = Files.newInputStream(Paths.get(documentPath, new String[0]), new OpenOption[0]);){
                properties.load(inputStream);
            }
            return properties.getProperty(propertyKey);
        }
        catch (Exception e) {
            throw IllegalArgumentExceptions.format(e, "Error looking up properties [%s] and key [%s].", documentPath, propertyKey);
        }
    }
}

