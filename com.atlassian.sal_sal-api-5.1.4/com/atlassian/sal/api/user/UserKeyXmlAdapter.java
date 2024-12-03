/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.sal.api.user;

import com.atlassian.sal.api.user.UserKey;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.apache.commons.lang3.StringUtils;

public class UserKeyXmlAdapter
extends XmlAdapter<String, UserKey> {
    @Nullable
    public UserKey unmarshal(String stringValue) {
        if (StringUtils.isNotBlank((CharSequence)stringValue)) {
            return new UserKey(stringValue);
        }
        return null;
    }

    @Nullable
    public String marshal(UserKey userKey) {
        if (userKey != null) {
            return userKey.getStringValue();
        }
        return null;
    }
}

