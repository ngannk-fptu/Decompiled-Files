/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.osgi;

import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.WriteResource;
import java.io.OutputStream;

public class JarResource
extends WriteResource {
    Jar jar;
    long size = -1L;

    public JarResource(Jar jar) {
        this.jar = jar;
    }

    @Override
    public long lastModified() {
        return this.jar.lastModified();
    }

    @Override
    public void write(OutputStream out) throws Exception {
        try {
            this.jar.write(out);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public Jar getJar() {
        return this.jar;
    }

    public String toString() {
        return ":" + this.jar.getName() + ":";
    }
}

