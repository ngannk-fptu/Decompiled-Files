/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.component;

import aQute.bnd.osgi.WriteResource;
import aQute.lib.io.IO;
import aQute.lib.tag.Tag;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class TagResource
extends WriteResource {
    final Tag tag;

    public TagResource(Tag tag) {
        this.tag = tag;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void write(OutputStream out) throws IOException {
        PrintWriter pw = IO.writer(out, StandardCharsets.UTF_8);
        pw.print("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        try {
            this.tag.print(0, pw);
        }
        finally {
            pw.flush();
        }
    }

    @Override
    public long lastModified() {
        return 0L;
    }
}

