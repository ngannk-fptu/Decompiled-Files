/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.lock;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.DefaultObjectNamespace;
import com.hazelcast.spi.DistributedObjectNamespace;
import com.hazelcast.spi.ObjectNamespace;
import java.io.IOException;

public final class ObjectNamespaceSerializationHelper {
    private ObjectNamespaceSerializationHelper() {
    }

    public static void writeNamespaceCompatibly(ObjectNamespace namespace, ObjectDataOutput out) throws IOException {
        if (namespace.getClass() == DefaultObjectNamespace.class) {
            out.writeObject(new DistributedObjectNamespace(namespace));
        } else {
            out.writeObject(namespace);
        }
    }

    public static ObjectNamespace readNamespaceCompatibly(ObjectDataInput in) throws IOException {
        ObjectNamespace namespace = (ObjectNamespace)in.readObject();
        if (namespace.getClass() == DefaultObjectNamespace.class) {
            namespace = new DistributedObjectNamespace(namespace);
        }
        return namespace;
    }
}

