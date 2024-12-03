/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.instance;

import com.hazelcast.core.HazelcastException;
import com.hazelcast.instance.DefaultNodeExtension;
import com.hazelcast.instance.Node;
import com.hazelcast.instance.NodeExtension;
import com.hazelcast.logging.Logger;
import com.hazelcast.spi.annotation.PrivateApi;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.ServiceLoader;
import java.util.Iterator;
import java.util.List;

@PrivateApi
public final class NodeExtensionFactory {
    private static final String NODE_EXTENSION_FACTORY_ID = "com.hazelcast.instance.NodeExtension";

    private NodeExtensionFactory() {
    }

    public static NodeExtension create(Node node, List<String> extensionPriorityList) {
        try {
            ClassLoader classLoader = node.getConfigClassLoader();
            Class<NodeExtension> chosenExtension = null;
            int chosenPriority = Integer.MAX_VALUE;
            Iterator<Class<NodeExtension>> iter = ServiceLoader.classIterator(NodeExtension.class, NODE_EXTENSION_FACTORY_ID, classLoader);
            while (iter.hasNext()) {
                Class<NodeExtension> currExt = iter.next();
                NodeExtensionFactory.warnIfDuplicate(currExt);
                int currPriority = extensionPriorityList.indexOf(currExt.getName());
                if (currPriority == -1 || currPriority >= chosenPriority) continue;
                chosenPriority = currPriority;
                chosenExtension = currExt;
            }
            if (chosenExtension == null) {
                throw new HazelcastException("ServiceLoader didn't find any services registered under com.hazelcast.instance.NodeExtension");
            }
            return (NodeExtension)chosenExtension.getConstructor(Node.class).newInstance(node);
        }
        catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
    }

    private static void warnIfDuplicate(Class<NodeExtension> klass) {
        if (!klass.equals(DefaultNodeExtension.class) && klass.getName().equals(DefaultNodeExtension.class.getName())) {
            Logger.getLogger(NodeExtensionFactory.class).warning("DefaultNodeExtension class has been loaded by two different class-loaders.\nClassloader 1: " + NodeExtensionFactory.class.getClassLoader() + '\n' + "Classloader 2: " + klass.getClassLoader() + '\n' + "Are you running Hazelcast Jet in an OSGi environment? If so, set the bundle class-loader in the Config using the setClassloader() method");
        }
    }
}

