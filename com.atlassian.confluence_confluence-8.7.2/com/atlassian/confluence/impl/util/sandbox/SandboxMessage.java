/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.sandbox.SandboxSerializer
 *  com.atlassian.confluence.util.sandbox.SandboxSerializers
 */
package com.atlassian.confluence.impl.util.sandbox;

import com.atlassian.confluence.impl.util.sandbox.SandboxMessageType;
import com.atlassian.confluence.util.sandbox.SandboxSerializer;
import com.atlassian.confluence.util.sandbox.SandboxSerializers;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class SandboxMessage {
    private static final int[] START_MARKER = new int[]{42, 132};
    private final SandboxMessageType type;
    private final Object payload;

    SandboxMessage(SandboxMessageType type, Object payload) {
        this.type = type;
        this.payload = payload;
    }

    static SandboxMessage receiveMessage(InputStream inputStream) throws IOException {
        int n;
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        int len = dataInputStream.readInt();
        byte[] buff = new byte[len];
        for (int off = 0; off < len; off += n) {
            n = dataInputStream.read(buff, off, len - off);
            if (n != -1) continue;
            throw new EOFException("error deserialize sandbox message");
        }
        List composite = (List)SandboxSerializers.compositeByteArraySerializer().deserialize(buff);
        SandboxMessageType messageType = SandboxMessageType.valueOf((String)SandboxSerializers.stringSerializer().deserialize((byte[])composite.get(0)));
        return messageType.deserialize((byte[])composite.get(1));
    }

    static void sendMessage(SandboxMessage message, OutputStream outputStream) throws IOException {
        byte[] buff = message.getType().serialize(message);
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.writeInt(buff.length);
        dataOutputStream.write(buff);
        dataOutputStream.flush();
    }

    static void sendStartMarker(OutputStream outputStream) throws IOException {
        for (int b : START_MARKER) {
            outputStream.write(b);
        }
        outputStream.flush();
    }

    static void waitForStartMarker(InputStream inputStream) throws IOException {
        int i = 0;
        while (i < START_MARKER.length) {
            int b = inputStream.read();
            if (b == -1) {
                throw new EOFException("Error when waiting for a marker indicating the start of the message stream");
            }
            if (b == START_MARKER[i]) {
                ++i;
                continue;
            }
            i = 0;
        }
    }

    static ApplicationPayLoadSerializer applicationPayLoadSerializer() {
        return ApplicationPayLoadSerializer.instance;
    }

    SandboxMessageType getType() {
        return this.type;
    }

    Object getPayload() {
        return this.payload;
    }

    static final class ApplicationPayLoadSerializer
    implements SandboxSerializer<ApplicationPayload> {
        static final ApplicationPayLoadSerializer instance = new ApplicationPayLoadSerializer();

        ApplicationPayLoadSerializer() {
        }

        public byte[] serialize(ApplicationPayload payload) {
            ArrayList<byte[]> fields = new ArrayList<byte[]>();
            fields.add(SandboxSerializers.stringSerializer().serialize((Object)payload.getClassLoaderUid()));
            fields.add(SandboxSerializers.stringSerializer().serialize((Object)payload.getClassName()));
            fields.add(payload.getData());
            return SandboxSerializers.compositeByteArraySerializer().serialize(fields);
        }

        public ApplicationPayload deserialize(byte[] bytes) {
            List composite = (List)SandboxSerializers.compositeByteArraySerializer().deserialize(bytes);
            return new ApplicationPayload((String)SandboxSerializers.stringSerializer().deserialize((byte[])composite.get(0)), (String)SandboxSerializers.stringSerializer().deserialize((byte[])composite.get(1)), (byte[])composite.get(2));
        }
    }

    static class ApplicationPayload {
        private String classLoaderUid;
        private String className;
        private byte[] data;

        public static ApplicationPayload withSpecifiedClassloader(UUID classLoaderUid, String className, byte[] data) {
            return new ApplicationPayload(classLoaderUid.toString(), className, data);
        }

        public static ApplicationPayload withUnspecifiedClassloader(String className, byte[] data) {
            return new ApplicationPayload(null, className, data);
        }

        private ApplicationPayload(String classLoaderUid, String className, byte[] data) {
            this.classLoaderUid = classLoaderUid;
            this.className = className;
            this.data = data;
        }

        public String getClassLoaderUid() {
            return this.classLoaderUid;
        }

        public String getClassName() {
            return this.className;
        }

        public byte[] getData() {
            return this.data;
        }
    }
}

