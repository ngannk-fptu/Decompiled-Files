/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.DefaultMessageTaskFactoryProvider;
import com.hazelcast.client.impl.protocol.MessageTaskFactory;
import com.hazelcast.client.impl.protocol.MessageTaskFactoryProvider;
import com.hazelcast.client.impl.protocol.task.MessageTask;
import com.hazelcast.client.impl.protocol.task.NoSuchMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.ServiceLoader;
import java.lang.reflect.Constructor;
import java.util.Iterator;

public class CompositeMessageTaskFactory
implements MessageTaskFactory {
    private static final String FACTORY_ID = "com.hazelcast.client.impl.protocol.MessageTaskFactoryProvider";
    private final Node node;
    private final NodeEngine nodeEngine;
    private final MessageTaskFactory[] factories = new MessageTaskFactory[Short.MAX_VALUE];

    public CompositeMessageTaskFactory(NodeEngine nodeEngine) {
        this.nodeEngine = nodeEngine;
        this.node = ((NodeEngineImpl)nodeEngine).getNode();
        this.loadProvider(new DefaultMessageTaskFactoryProvider(this.nodeEngine));
        this.loadServices();
    }

    private void loadProvider(MessageTaskFactoryProvider provider) {
        MessageTaskFactory[] providerFactories = provider.getFactories();
        for (int idx = 0; idx < providerFactories.length; ++idx) {
            if (providerFactories[idx] == null) continue;
            this.factories[idx] = providerFactories[idx];
        }
    }

    private void loadServices() {
        try {
            ClassLoader classLoader = this.node.getConfigClassLoader();
            Iterator<Class<MessageTaskFactoryProvider>> iter = ServiceLoader.classIterator(MessageTaskFactoryProvider.class, FACTORY_ID, classLoader);
            while (iter.hasNext()) {
                Class<MessageTaskFactoryProvider> clazz = iter.next();
                Constructor<MessageTaskFactoryProvider> constructor = clazz.getDeclaredConstructor(NodeEngine.class);
                MessageTaskFactoryProvider messageTaskProvider = constructor.newInstance(this.nodeEngine);
                this.loadProvider(messageTaskProvider);
            }
        }
        catch (Throwable e) {
            throw ExceptionUtil.rethrow(e);
        }
    }

    @Override
    public MessageTask create(ClientMessage clientMessage, Connection connection) {
        try {
            MessageTaskFactory factory = this.factories[clientMessage.getMessageType()];
            if (factory != null) {
                return factory.create(clientMessage, connection);
            }
        }
        catch (Exception e) {
            ExceptionUtil.rethrow(e);
        }
        return new NoSuchMessageTask(clientMessage, this.node, connection);
    }
}

