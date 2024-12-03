/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.jms.ConnectionFactory
 *  javax.jms.QueueConnectionFactory
 *  javax.jms.TopicConnectionFactory
 */
package org.apache.axis.components.jms;

import java.util.HashMap;
import javax.jms.ConnectionFactory;
import javax.jms.QueueConnectionFactory;
import javax.jms.TopicConnectionFactory;
import org.apache.axis.components.jms.JMSVendorAdapter;
import org.apache.axis.utils.BeanPropertyDescriptor;
import org.apache.axis.utils.BeanUtils;
import org.apache.axis.utils.ClassUtils;

public abstract class BeanVendorAdapter
extends JMSVendorAdapter {
    protected static final String CONNECTION_FACTORY_CLASS = "transport.jms.ConnectionFactoryClass";

    public QueueConnectionFactory getQueueConnectionFactory(HashMap cfConfig) throws Exception {
        return (QueueConnectionFactory)this.getConnectionFactory(cfConfig);
    }

    public TopicConnectionFactory getTopicConnectionFactory(HashMap cfConfig) throws Exception {
        return (TopicConnectionFactory)this.getConnectionFactory(cfConfig);
    }

    private ConnectionFactory getConnectionFactory(HashMap cfConfig) throws Exception {
        String classname = (String)cfConfig.get(CONNECTION_FACTORY_CLASS);
        if (classname == null || classname.trim().length() == 0) {
            throw new IllegalArgumentException("noCFClass");
        }
        Class factoryClass = ClassUtils.forName(classname);
        ConnectionFactory factory = (ConnectionFactory)factoryClass.newInstance();
        this.callSetters(cfConfig, factoryClass, factory);
        return factory;
    }

    private void callSetters(HashMap cfConfig, Class factoryClass, ConnectionFactory factory) throws Exception {
        BeanPropertyDescriptor[] bpd = BeanUtils.getPd(factoryClass);
        for (int i = 0; i < bpd.length; ++i) {
            Object value;
            BeanPropertyDescriptor thisBPD = bpd[i];
            String propName = thisBPD.getName();
            if (!cfConfig.containsKey(propName) || (value = cfConfig.get(propName)) == null) continue;
            String validType = thisBPD.getType().getName();
            if (!value.getClass().getName().equals(validType)) {
                throw new IllegalArgumentException("badType");
            }
            if (!thisBPD.isWriteable()) {
                throw new IllegalArgumentException("notWriteable");
            }
            if (thisBPD.isIndexed()) {
                throw new IllegalArgumentException("noIndexedSupport");
            }
            thisBPD.set(factory, value);
        }
    }
}

