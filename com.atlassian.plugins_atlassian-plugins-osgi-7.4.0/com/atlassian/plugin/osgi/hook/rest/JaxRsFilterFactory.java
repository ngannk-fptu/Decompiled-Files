/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.dom4j.Document
 *  org.dom4j.DocumentException
 *  org.dom4j.Element
 *  org.dom4j.io.SAXReader
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.wiring.BundleCapability
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.osgi.hook.rest;

import com.atlassian.plugin.osgi.hook.rest.RestVersionUtils;
import java.net.URL;
import java.util.function.Predicate;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleCapability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JaxRsFilterFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(JaxRsFilterFactory.class);
    public static final Predicate<BundleCapability> FILTER_REST_V1 = bundleCapability -> RestVersionUtils.isCapabilityWithMajorVersion(bundleCapability, 1);
    public static final Predicate<BundleCapability> FILTER_REST_V2 = bundleCapability -> RestVersionUtils.isCapabilityWithMajorVersion(bundleCapability, 2);

    public Predicate<BundleCapability> getFilter(Bundle bundle) {
        URL pluginDescriptor = bundle.getResource("atlassian-plugin.xml");
        Document read = null;
        try {
            read = new SAXReader().read(pluginDescriptor);
            return this.getFilter(read);
        }
        catch (DocumentException e) {
            LOGGER.warn(String.format("Cannot parse plugin descriptor for bundle %s; no filtering of JAX-RS package", bundle.getSymbolicName()), (Throwable)e);
            return ignored -> true;
        }
    }

    public Predicate<BundleCapability> getFilter(Document pluginXml) {
        Element restMigration = pluginXml.getRootElement().element("rest-migration");
        boolean hasRestV2 = restMigration != null && restMigration.element("rest-v2") != null;
        return hasRestV2 ? FILTER_REST_V2 : FILTER_REST_V1;
    }
}

