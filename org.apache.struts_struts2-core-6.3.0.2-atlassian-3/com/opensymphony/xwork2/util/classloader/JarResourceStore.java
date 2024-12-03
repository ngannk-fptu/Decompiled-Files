/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.util.classloader;

import com.opensymphony.xwork2.util.classloader.AbstractResourceStore;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JarResourceStore
extends AbstractResourceStore {
    private static final Logger LOG = LogManager.getLogger(JarResourceStore.class);

    public JarResourceStore(File file) {
        super(file);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    @Override
    public byte[] read(String pResourceName) {
        byte[] byArray;
        Throwable throwable;
        ZipFile jarFile;
        InputStream in;
        block17: {
            block18: {
                in = null;
                jarFile = new ZipFile(this.file);
                throwable = null;
                ZipEntry entry = jarFile.getEntry(pResourceName);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                in = jarFile.getInputStream(entry);
                JarResourceStore.copy(in, out);
                byArray = out.toByteArray();
                if (jarFile == null) break block17;
                if (throwable == null) break block18;
                try {
                    jarFile.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                break block17;
            }
            jarFile.close();
        }
        this.closeQuietly(in);
        return byArray;
        {
            catch (Throwable throwable3) {
                try {
                    try {
                        try {
                            throwable = throwable3;
                            throw throwable3;
                        }
                        catch (Throwable throwable4) {
                            if (jarFile != null) {
                                if (throwable != null) {
                                    try {
                                        jarFile.close();
                                    }
                                    catch (Throwable throwable5) {
                                        throwable.addSuppressed(throwable5);
                                    }
                                } else {
                                    jarFile.close();
                                }
                            }
                            throw throwable4;
                        }
                    }
                    catch (Exception e) {
                        LOG.debug("Unable to read file [{}] from [{}]", (Object)pResourceName, (Object)this.file.getName(), (Object)e);
                        throwable = null;
                        return throwable;
                    }
                }
                catch (Throwable throwable6) {
                    throw throwable6;
                }
                finally {
                    this.closeQuietly(in);
                }
            }
        }
    }

    public static long copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[4096];
        long count = 0L;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += (long)n;
        }
        return count;
    }
}

