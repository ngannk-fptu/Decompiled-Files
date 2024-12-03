/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.sandbox.SandboxSerializers
 */
package com.atlassian.confluence.impl.util.sandbox;

import com.atlassian.confluence.impl.util.sandbox.SandboxMessage;
import com.atlassian.confluence.util.sandbox.SandboxSerializers;
import java.util.List;

enum SandboxMessageType {
    APPLICATION_REQUEST{

        @Override
        byte[] serialize(SandboxMessage message) {
            SandboxMessage.ApplicationPayload payload = (SandboxMessage.ApplicationPayload)message.getPayload();
            return SandboxSerializers.compositeByteArraySerializer().serialize((Object)SandboxSerializers.of((Object)SandboxSerializers.stringSerializer().serialize((Object)this.name()), (Object)SandboxMessage.applicationPayLoadSerializer().serialize(payload)));
        }

        @Override
        SandboxMessage deserialize(byte[] bytes) {
            SandboxMessage.ApplicationPayload payload = SandboxMessage.applicationPayLoadSerializer().deserialize(bytes);
            return new SandboxMessage(this, payload);
        }
    }
    ,
    APPLICATION_RESPONSE{

        @Override
        byte[] serialize(SandboxMessage message) {
            SandboxMessage.ApplicationPayload payload = (SandboxMessage.ApplicationPayload)message.getPayload();
            return SandboxSerializers.compositeByteArraySerializer().serialize((Object)SandboxSerializers.of((Object)SandboxSerializers.stringSerializer().serialize((Object)this.name()), (Object)SandboxMessage.applicationPayLoadSerializer().serialize(payload)));
        }

        @Override
        SandboxMessage deserialize(byte[] bytes) {
            SandboxMessage.ApplicationPayload payload = SandboxMessage.applicationPayLoadSerializer().deserialize(bytes);
            return new SandboxMessage(this, payload);
        }
    }
    ,
    FIND_CLASS_REQUEST{

        @Override
        byte[] serialize(SandboxMessage message) {
            String payload = (String)message.getPayload();
            return SandboxSerializers.compositeByteArraySerializer().serialize((Object)SandboxSerializers.of((Object)SandboxSerializers.stringSerializer().serialize((Object)this.name()), (Object)SandboxSerializers.stringSerializer().serialize((Object)payload)));
        }

        @Override
        SandboxMessage deserialize(byte[] bytes) {
            return new SandboxMessage(this, SandboxSerializers.stringSerializer().deserialize(bytes));
        }
    }
    ,
    FIND_CLASS_RESPONSE{

        @Override
        byte[] serialize(SandboxMessage message) {
            byte[] payload = (byte[])message.getPayload();
            return SandboxSerializers.compositeByteArraySerializer().serialize((Object)SandboxSerializers.of((Object)SandboxSerializers.stringSerializer().serialize((Object)this.name()), (Object)payload));
        }

        @Override
        SandboxMessage deserialize(byte[] bytes) {
            return new SandboxMessage(this, bytes);
        }
    }
    ,
    FIND_RESOURCE_REQUEST{

        @Override
        byte[] serialize(SandboxMessage message) {
            String payload = (String)message.getPayload();
            return SandboxSerializers.compositeByteArraySerializer().serialize((Object)SandboxSerializers.of((Object)SandboxSerializers.stringSerializer().serialize((Object)this.name()), (Object)SandboxSerializers.stringSerializer().serialize((Object)payload)));
        }

        @Override
        SandboxMessage deserialize(byte[] bytes) {
            return new SandboxMessage(this, SandboxSerializers.stringSerializer().deserialize(bytes));
        }
    }
    ,
    FIND_RESOURCE_RESPONSE{

        @Override
        byte[] serialize(SandboxMessage message) {
            byte[] payload = (byte[])message.getPayload();
            return SandboxSerializers.compositeByteArraySerializer().serialize((Object)SandboxSerializers.of((Object)SandboxSerializers.stringSerializer().serialize((Object)this.name()), (Object)payload));
        }

        @Override
        SandboxMessage deserialize(byte[] bytes) {
            return new SandboxMessage(this, bytes);
        }
    }
    ,
    FIND_RESOURCES_REQUEST{

        @Override
        byte[] serialize(SandboxMessage message) {
            String payload = (String)message.getPayload();
            return SandboxSerializers.compositeByteArraySerializer().serialize((Object)SandboxSerializers.of((Object)SandboxSerializers.stringSerializer().serialize((Object)this.name()), (Object)SandboxSerializers.stringSerializer().serialize((Object)payload)));
        }

        @Override
        SandboxMessage deserialize(byte[] bytes) {
            return new SandboxMessage(this, SandboxSerializers.stringSerializer().deserialize(bytes));
        }
    }
    ,
    FIND_RESOURCES_RESPONSE{

        @Override
        byte[] serialize(SandboxMessage message) {
            List payload = (List)message.getPayload();
            return SandboxSerializers.compositeByteArraySerializer().serialize((Object)SandboxSerializers.of((Object)SandboxSerializers.stringSerializer().serialize((Object)this.name()), (Object)SandboxSerializers.compositeByteArraySerializer().serialize((Object)payload)));
        }

        @Override
        SandboxMessage deserialize(byte[] bytes) {
            List payload = (List)SandboxSerializers.compositeByteArraySerializer().deserialize(bytes);
            return new SandboxMessage(this, payload);
        }
    };


    abstract byte[] serialize(SandboxMessage var1);

    abstract SandboxMessage deserialize(byte[] var1);
}

