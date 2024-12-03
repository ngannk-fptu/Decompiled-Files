/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.views.jsp.ui;

import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.views.jsp.ui.AbstractUITag;

public class AbstractUITagBeanInfo
extends SimpleBeanInfo {
    private static final Logger LOG = LogManager.getLogger(AbstractUITagBeanInfo.class);

    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        try {
            ArrayList<PropertyDescriptor> descriptors = new ArrayList<PropertyDescriptor>();
            Method classSetter = AbstractUITag.class.getMethod("setCssClass", String.class);
            Method styleSetter = AbstractUITag.class.getMethod("setCssStyle", String.class);
            descriptors.add(new PropertyDescriptor("class", null, classSetter));
            descriptors.add(new PropertyDescriptor("cssClass", null, classSetter));
            descriptors.add(new PropertyDescriptor("style", null, styleSetter));
            descriptors.add(new PropertyDescriptor("cssStyle", null, styleSetter));
            for (Field field : AbstractUITag.class.getDeclaredFields()) {
                String fieldName = field.getName();
                if ("dynamicAttributes".equals(fieldName)) continue;
                String setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                Method setter = AbstractUITag.class.getMethod(setterName, String.class);
                descriptors.add(new PropertyDescriptor(fieldName, null, setter));
            }
            PropertyDescriptor[] array = new PropertyDescriptor[descriptors.size()];
            return descriptors.toArray(array);
        }
        catch (Exception e) {
            LOG.fatal("Could not construct bean info for AbstractUITag. This is very bad.", (Throwable)e);
            return null;
        }
    }
}

