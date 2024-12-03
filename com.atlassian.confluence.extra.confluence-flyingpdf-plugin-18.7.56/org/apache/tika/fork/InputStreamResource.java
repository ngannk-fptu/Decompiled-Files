/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.fork;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.fork.ForkResource;

class InputStreamResource
implements ForkResource {
    private final InputStream stream;

    public InputStreamResource(InputStream stream) {
        this.stream = stream;
    }

    @Override
    public Throwable process(DataInputStream input, DataOutputStream output) throws IOException {
        int m;
        int n = input.readInt();
        byte[] buffer = new byte[n];
        try {
            m = this.stream.read(buffer);
        }
        catch (IOException e) {
            e.printStackTrace();
            m = -1;
        }
        output.writeInt(m);
        if (m > 0) {
            output.write(buffer, 0, m);
        }
        output.flush();
        return null;
    }
}

