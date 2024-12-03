/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.Ordered
 *  org.springframework.lang.NonNull
 */
package org.springframework.beans;

import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.util.Collection;
import org.springframework.beans.BeanInfoFactory;
import org.springframework.beans.PropertyDescriptorUtils;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;

public class SimpleBeanInfoFactory
implements BeanInfoFactory,
Ordered {
    @Override
    @NonNull
    public BeanInfo getBeanInfo(final Class<?> beanClass) throws IntrospectionException {
        final Collection<? extends PropertyDescriptor> pds = PropertyDescriptorUtils.determineBasicProperties(beanClass);
        return new SimpleBeanInfo(){

            @Override
            public BeanDescriptor getBeanDescriptor() {
                return new BeanDescriptor(beanClass);
            }

            @Override
            public PropertyDescriptor[] getPropertyDescriptors() {
                return pds.toArray(PropertyDescriptorUtils.EMPTY_PROPERTY_DESCRIPTOR_ARRAY);
            }
        };
    }

    public int getOrder() {
        return 0x7FFFFFFE;
    }
}

