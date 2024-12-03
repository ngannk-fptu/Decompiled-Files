/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.fork;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import org.apache.tika.fork.ForkResource;

class ClassLoaderResource
implements ForkResource {
    private final ClassLoader loader;

    public ClassLoaderResource(ClassLoader loader) {
        this.loader = loader;
    }

    @Override
    public Throwable process(DataInputStream input, DataOutputStream output) throws IOException {
        byte type = input.readByte();
        String name = input.readUTF();
        if (type == 1) {
            InputStream stream = this.loader.getResourceAsStream(name);
            if (stream != null) {
                output.writeBoolean(true);
                this.writeAndCloseStream(output, stream);
            } else {
                output.writeBoolean(false);
            }
        } else if (type == 2) {
            Enumeration<URL> resources = this.loader.getResources(name);
            while (resources.hasMoreElements()) {
                output.writeBoolean(true);
                InputStream stream = resources.nextElement().openStream();
                this.writeAndCloseStream(output, stream);
            }
            output.writeBoolean(false);
        }
        output.flush();
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void writeAndCloseStream(DataOutputStream output, InputStream stream) throws IOException {
        try {
            int n;
            byte[] buffer = new byte[65535];
            while ((n = stream.read(buffer)) != -1) {
                output.writeShort(n);
                output.write(buffer, 0, n);
            }
            output.writeShort(0);
        }
        finally {
            stream.close();
        }
    }
}

