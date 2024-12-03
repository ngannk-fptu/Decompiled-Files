/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class ImplementationVersion {
    private String versionString;
    private static final ImplementationVersion version = new ImplementationVersion();

    public static String get() {
        return ImplementationVersion.version.versionString;
    }

    private ImplementationVersion() {
        Enumeration<URL> urls;
        try {
            urls = ImplementationVersion.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
        }
        catch (IOException ioe) {
            return;
        }
        while (urls.hasMoreElements()) {
            URL metaUrl = urls.nextElement();
            try {
                InputStream is = metaUrl.openStream();
                Throwable throwable = null;
                try {
                    Manifest mf = new Manifest(is);
                    Attributes attrs = mf.getMainAttributes();
                    if (!"Mozilla Rhino".equals(attrs.getValue("Implementation-Title"))) continue;
                    StringBuilder buf = new StringBuilder(23);
                    buf.append("Rhino ").append(attrs.getValue("Implementation-Version"));
                    String builtDate = attrs.getValue("Built-Date");
                    if (builtDate != null) {
                        builtDate = builtDate.replaceAll("-", " ");
                        buf.append(' ').append(builtDate);
                    }
                    this.versionString = buf.toString();
                    return;
                }
                catch (Throwable throwable2) {
                    throwable = throwable2;
                    throw throwable2;
                }
                finally {
                    if (is == null) continue;
                    if (throwable != null) {
                        try {
                            is.close();
                        }
                        catch (Throwable throwable3) {
                            throwable.addSuppressed(throwable3);
                        }
                        continue;
                    }
                    is.close();
                }
            }
            catch (IOException iOException) {}
        }
        this.versionString = "Rhino Snapshot";
    }
}

