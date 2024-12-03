/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.internal.serialization.DataSerializerHook
 *  com.hazelcast.internal.serialization.impl.FactoryIdHelper
 *  com.hazelcast.nio.serialization.DataSerializableFactory
 *  com.hazelcast.nio.serialization.IdentifiedDataSerializable
 */
package com.hazelcast.hibernate.serialization;

import com.hazelcast.hibernate.distributed.LockEntryProcessor;
import com.hazelcast.hibernate.distributed.UnlockEntryProcessor;
import com.hazelcast.hibernate.distributed.UpdateEntryProcessor;
import com.hazelcast.hibernate.local.Invalidation;
import com.hazelcast.hibernate.local.Timestamp;
import com.hazelcast.hibernate.serialization.ExpiryMarker;
import com.hazelcast.hibernate.serialization.Value;
import com.hazelcast.internal.serialization.DataSerializerHook;
import com.hazelcast.internal.serialization.impl.FactoryIdHelper;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public class HibernateDataSerializerHook
implements DataSerializerHook {
    public static final int F_ID = FactoryIdHelper.getFactoryId((String)"hazelcast.serialization.ds.hibernate", (int)-2000);
    public static final int VALUE = 0;
    public static final int EXPIRY_MARKER = 1;
    public static final int LOCK = 2;
    public static final int UNLOCK = 3;
    public static final int UPDATE = 4;
    public static final int INVALIDATION = 5;
    public static final int TIMESTAMP = 6;

    public int getFactoryId() {
        return F_ID;
    }

    public DataSerializableFactory createFactory() {
        return new Factory();
    }

    private static class Factory
    implements DataSerializableFactory {
        private Factory() {
        }

        public IdentifiedDataSerializable create(int typeId) {
            Object result;
            switch (typeId) {
                case 0: {
                    result = new Value();
                    break;
                }
                case 1: {
                    result = new ExpiryMarker();
                    break;
                }
                case 2: {
                    result = new LockEntryProcessor();
                    break;
                }
                case 3: {
                    result = new UnlockEntryProcessor();
                    break;
                }
                case 4: {
                    result = new UpdateEntryProcessor();
                    break;
                }
                case 5: {
                    result = new Invalidation();
                    break;
                }
                case 6: {
                    result = new Timestamp();
                    break;
                }
                default: {
                    result = null;
                }
            }
            return result;
        }
    }
}

