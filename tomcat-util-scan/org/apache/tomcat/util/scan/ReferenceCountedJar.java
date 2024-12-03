/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.Jar
 */
package org.apache.tomcat.util.scan;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.jar.Manifest;
import org.apache.tomcat.Jar;
import org.apache.tomcat.util.scan.JarFactory;

public class ReferenceCountedJar
implements Jar {
    private final URL url;
    private Jar wrappedJar;
    private int referenceCount = 0;

    public ReferenceCountedJar(URL url) throws IOException {
        this.url = url;
        this.open();
    }

    private synchronized ReferenceCountedJar open() throws IOException {
        if (this.wrappedJar == null) {
            this.wrappedJar = JarFactory.newInstance(this.url);
        }
        ++this.referenceCount;
        return this;
    }

    public synchronized void close() {
        --this.referenceCount;
        if (this.referenceCount == 0) {
            this.wrappedJar.close();
            this.wrappedJar = null;
        }
    }

    public URL getJarFileURL() {
        return this.url;
    }

    public InputStream getInputStream(String name) throws IOException {
        try (ReferenceCountedJar jar = this.open();){
            InputStream inputStream = jar.wrappedJar.getInputStream(name);
            return inputStream;
        }
    }

    public long getLastModified(String name) throws IOException {
        try (ReferenceCountedJar jar = this.open();){
            long l = jar.wrappedJar.getLastModified(name);
            return l;
        }
    }

    public boolean exists(String name) throws IOException {
        try (ReferenceCountedJar jar = this.open();){
            boolean bl = jar.wrappedJar.exists(name);
            return bl;
        }
    }

    public void nextEntry() {
        try (ReferenceCountedJar jar = this.open();){
            jar.wrappedJar.nextEntry();
        }
        catch (IOException ioe) {
            throw new IllegalStateException(ioe);
        }
    }

    public String getEntryName() {
        ReferenceCountedJar jar = this.open();
        try {
            String string = jar.wrappedJar.getEntryName();
            if (jar != null) {
                jar.close();
            }
            return string;
        }
        catch (Throwable throwable) {
            try {
                if (jar != null) {
                    try {
                        jar.close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
            catch (IOException ioe) {
                throw new IllegalStateException(ioe);
            }
        }
    }

    public InputStream getEntryInputStream() throws IOException {
        try (ReferenceCountedJar jar = this.open();){
            InputStream inputStream = jar.wrappedJar.getEntryInputStream();
            return inputStream;
        }
    }

    public String getURL(String entry) {
        ReferenceCountedJar jar = this.open();
        try {
            String string = jar.wrappedJar.getURL(entry);
            if (jar != null) {
                jar.close();
            }
            return string;
        }
        catch (Throwable throwable) {
            try {
                if (jar != null) {
                    try {
                        jar.close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
            catch (IOException ioe) {
                throw new IllegalStateException(ioe);
            }
        }
    }

    public Manifest getManifest() throws IOException {
        try (ReferenceCountedJar jar = this.open();){
            Manifest manifest = jar.wrappedJar.getManifest();
            return manifest;
        }
    }

    public void reset() throws IOException {
        try (ReferenceCountedJar jar = this.open();){
            jar.wrappedJar.reset();
        }
    }
}

