/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.IntrospectionUtils
 */
package org.apache.catalina.storeconfig;

import java.beans.PropertyDescriptor;
import org.apache.catalina.storeconfig.StoreAppender;
import org.apache.catalina.storeconfig.StoreDescription;
import org.apache.tomcat.util.IntrospectionUtils;

public class CertificateStoreAppender
extends StoreAppender {
    @Override
    protected Object checkAttribute(StoreDescription desc, PropertyDescriptor descriptor, String attributeName, Object bean, Object bean2) {
        if (attributeName.equals("type")) {
            return IntrospectionUtils.getProperty((Object)bean, (String)descriptor.getName());
        }
        return super.checkAttribute(desc, descriptor, attributeName, bean, bean2);
    }
}

