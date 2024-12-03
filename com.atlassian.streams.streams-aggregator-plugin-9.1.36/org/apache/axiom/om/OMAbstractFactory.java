/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om;

import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMMetaFactoryLocator;
import org.apache.axiom.soap.SOAPFactory;

public class OMAbstractFactory {
    public static final String META_FACTORY_NAME_PROPERTY = "org.apache.axiom.om.OMMetaFactory";
    public static final String FEATURE_DEFAULT = "default";
    public static final String FEATURE_DOM = "dom";
    private static final String DEFAULT_LOCATOR_CLASS_NAME = "org.apache.axiom.locator.DefaultOMMetaFactoryLocator";
    private static final OMMetaFactoryLocator defaultMetaFactoryLocator;
    private static volatile OMMetaFactoryLocator metaFactoryLocator;

    private OMAbstractFactory() {
    }

    public static void setMetaFactoryLocator(OMMetaFactoryLocator locator) {
        metaFactoryLocator = locator;
    }

    public static OMMetaFactory getMetaFactory() {
        return OMAbstractFactory.getMetaFactory(FEATURE_DEFAULT);
    }

    public static OMMetaFactory getMetaFactory(String feature) {
        OMMetaFactory metaFactory;
        OMMetaFactoryLocator locator = metaFactoryLocator;
        if (locator == null) {
            locator = defaultMetaFactoryLocator;
        }
        if ((metaFactory = locator.getOMMetaFactory(feature)) == null) {
            String jarHint = feature.equals(FEATURE_DEFAULT) ? "axiom-impl.jar" : (feature.equals(FEATURE_DOM) ? "axiom-dom.jar" : null);
            StringBuilder buffer = new StringBuilder();
            buffer.append("No meta factory found for feature '").append(feature).append("'");
            if (jarHint != null) {
                buffer.append("; this usually means that ").append(jarHint).append(" is not in the classpath");
            }
            throw new OMException(buffer.toString());
        }
        return metaFactory;
    }

    public static OMFactory getOMFactory() {
        return OMAbstractFactory.getMetaFactory().getOMFactory();
    }

    public static SOAPFactory getSOAP11Factory() {
        return OMAbstractFactory.getMetaFactory().getSOAP11Factory();
    }

    public static SOAPFactory getSOAP12Factory() {
        return OMAbstractFactory.getMetaFactory().getSOAP12Factory();
    }

    static {
        try {
            defaultMetaFactoryLocator = (OMMetaFactoryLocator)Class.forName(DEFAULT_LOCATOR_CLASS_NAME).newInstance();
        }
        catch (InstantiationException ex) {
            throw new InstantiationError(ex.getMessage());
        }
        catch (IllegalAccessException ex) {
            throw new IllegalAccessError(ex.getMessage());
        }
        catch (ClassNotFoundException ex) {
            throw new NoClassDefFoundError(ex.getMessage());
        }
    }
}

