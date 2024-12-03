/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.config;

import java.util.Properties;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.lang.Nullable;

@Deprecated
public class PreferencesPlaceholderConfigurer
extends PropertyPlaceholderConfigurer
implements InitializingBean {
    @Nullable
    private String systemTreePath;
    @Nullable
    private String userTreePath;
    private Preferences systemPrefs = Preferences.systemRoot();
    private Preferences userPrefs = Preferences.userRoot();

    public void setSystemTreePath(String systemTreePath) {
        this.systemTreePath = systemTreePath;
    }

    public void setUserTreePath(String userTreePath) {
        this.userTreePath = userTreePath;
    }

    @Override
    public void afterPropertiesSet() {
        if (this.systemTreePath != null) {
            this.systemPrefs = this.systemPrefs.node(this.systemTreePath);
        }
        if (this.userTreePath != null) {
            this.userPrefs = this.userPrefs.node(this.userTreePath);
        }
    }

    @Override
    protected String resolvePlaceholder(String placeholder, Properties props) {
        String value;
        String path = null;
        String key = placeholder;
        int endOfPath = placeholder.lastIndexOf(47);
        if (endOfPath != -1) {
            path = placeholder.substring(0, endOfPath);
            key = placeholder.substring(endOfPath + 1);
        }
        if ((value = this.resolvePlaceholder(path, key, this.userPrefs)) == null && (value = this.resolvePlaceholder(path, key, this.systemPrefs)) == null) {
            value = props.getProperty(placeholder);
        }
        return value;
    }

    @Nullable
    protected String resolvePlaceholder(@Nullable String path, String key, Preferences preferences) {
        if (path != null) {
            try {
                if (preferences.nodeExists(path)) {
                    return preferences.node(path).get(key, null);
                }
                return null;
            }
            catch (BackingStoreException ex) {
                throw new BeanDefinitionStoreException("Cannot access specified node path [" + path + "]", ex);
            }
        }
        return preferences.get(key, null);
    }
}

