/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.dispatcher.HttpParameters
 *  org.apache.struts2.dispatcher.Parameter
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.confluence.user.UserDetailsManager;
import com.atlassian.user.User;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.dispatcher.Parameter;

public class UserDetailsMap {
    public static final String PREFIX = "userparam-";
    private final Map<String, String> userDetails = new HashMap<String, String>();
    private final User user;
    private final UserDetailsManager userDetailsManager;

    public UserDetailsMap(User user, UserDetailsManager userDetailsManager) {
        this.user = user;
        this.userDetailsManager = userDetailsManager;
    }

    public void copyPropertiesToManager() {
        for (Map.Entry<String, String> entry : this.userDetails.entrySet()) {
            String value = entry.getValue();
            if (StringUtils.isBlank((CharSequence)value)) {
                this.userDetailsManager.removeProperty(this.user, entry.getKey());
                continue;
            }
            this.userDetailsManager.setStringProperty(this.user, entry.getKey(), value);
        }
    }

    public String getProperty(String key) {
        String value = this.userDetails.get(key);
        return StringUtils.isNotBlank((CharSequence)value) ? value : this.userDetailsManager.getStringProperty(this.user, key);
    }

    public void setProperty(String key, String value) {
        this.userDetails.put(key, value);
    }

    @Deprecated(since="8.6")
    public void setParameters(Map<String, Object> map) {
        this.setParameters(HttpParameters.create(map).build());
    }

    public void setParameters(HttpParameters parameters) {
        for (Map.Entry entry : parameters.entrySet()) {
            String[] allVals = ((Parameter)entry.getValue()).getMultipleValues();
            if (!((String)entry.getKey()).startsWith(PREFIX) || allVals.length <= 0) continue;
            this.userDetails.put(((String)entry.getKey()).substring(PREFIX.length()), allVals[0]);
        }
    }
}

