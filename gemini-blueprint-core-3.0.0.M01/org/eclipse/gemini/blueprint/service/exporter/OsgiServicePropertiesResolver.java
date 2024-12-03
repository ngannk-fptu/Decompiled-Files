/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.gemini.blueprint.service.exporter;

import java.util.Map;

public interface OsgiServicePropertiesResolver {
    public static final String SPRING_DM_BEAN_NAME_PROPERTY_KEY = "org.springframework.osgi.bean.name";
    public static final String BEAN_NAME_PROPERTY_KEY = "org.eclipse.gemini.blueprint.bean.name";
    public static final String BLUEPRINT_COMP_NAME = "osgi.service.blueprint.compname";

    public Map getServiceProperties(String var1);
}

