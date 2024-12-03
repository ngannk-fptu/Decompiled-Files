/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  org.apache.commons.codec.digest.DigestUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.core.property;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class HashingLongPropertyKeysPluginSettings
implements PluginSettings {
    private final PluginSettings pluginSettings;
    private static final int MAX_KEY_LENGTH = 100;
    private static final Logger LOG = LoggerFactory.getLogger(HashingLongPropertyKeysPluginSettings.class);

    HashingLongPropertyKeysPluginSettings(PluginSettings pluginSettings) {
        this.pluginSettings = pluginSettings;
    }

    public Object get(String key) {
        return this.pluginSettings.get(this.hashKeyIfTooLong(key));
    }

    private String hashKeyIfTooLong(String key) {
        if (key.length() > 100) {
            String keyHash = DigestUtils.md5Hex((String)key);
            String keptOriginalKey = key.substring(0, 100 - keyHash.length());
            LOG.debug("Key '" + key + "' exceeds " + 100 + " characters. Key length is: '" + key.length() + "'. Hashed key value is: '" + keyHash + "'. Using combined original key and hash value '" + keptOriginalKey + keyHash + " as the key.");
            String hashedKey = keptOriginalKey + keyHash;
            this.migrateKey(key, hashedKey);
            return hashedKey;
        }
        return key;
    }

    private void migrateKey(String oldkey, String newKey) {
        if (oldkey.equals(newKey)) {
            return;
        }
        try {
            Object o = this.pluginSettings.get(oldkey);
            if (o != null) {
                this.pluginSettings.put(newKey, o);
                this.pluginSettings.remove(oldkey);
            }
        }
        catch (Exception ex) {
            LOG.debug("Exception thrown when attempting to migrate key '" + oldkey + "' to new key '" + newKey + "', application did never support keys > " + 100, (Throwable)ex);
        }
    }

    public Object put(String key, Object value) {
        String hashKey = this.hashKeyIfTooLong(key);
        if (LOG.isDebugEnabled()) {
            String message = String.format("Putting key [%s] as hashKey [%s] with value [%s]", key, hashKey, value != null ? value.toString() : null);
            LOG.debug(message);
        }
        return this.pluginSettings.put(hashKey, value);
    }

    public Object remove(String key) {
        String hashKey = this.hashKeyIfTooLong(key);
        if (LOG.isDebugEnabled()) {
            String message = String.format("Removing key [%s] as hashKey [%s] with value [%s]", key, hashKey, this.pluginSettings.get(hashKey));
            LOG.debug(message);
        }
        return this.pluginSettings.remove(hashKey);
    }
}

