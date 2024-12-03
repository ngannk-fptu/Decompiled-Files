/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.internal.rest.model.applink;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.internal.common.rest.model.applink.RestApplicationLink;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;

public class RestExtendedApplicationLink
extends RestApplicationLink {
    public static final String PROPERTIES = "properties";
    public static final String DATA = "data";
    private Map<String, Object> properties;
    private Map<String, Object> data;

    public RestExtendedApplicationLink() {
    }

    public RestExtendedApplicationLink(@Nonnull ApplicationLink link) {
        super(link);
    }

    public RestExtendedApplicationLink(@Nonnull ApplicationLink link, @Nonnull Set<String> propertyKeys, @Nonnull Map<String, Object> data) {
        super(link);
        this.properties = RestExtendedApplicationLink.getProperties(link, propertyKeys);
        this.data = data;
    }

    private static Map<String, Object> getProperties(ApplicationLink link, Set<String> propertyKeys) {
        LinkedHashMap<String, Object> properties = new LinkedHashMap<String, Object>();
        for (String key : propertyKeys) {
            properties.put(key, link.getProperty(key));
        }
        return properties;
    }
}

