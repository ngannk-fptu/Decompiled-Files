/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

public class IOUtil {
    private static final Path tmpdir = Paths.get(System.getProperty("java.io.tmpdir"), new String[0]);

    public static void copyCompletely(InputStream input, OutputStream output) throws IOException {
        int length;
        if (output instanceof FileOutputStream && input instanceof FileInputStream) {
            try {
                FileChannel target = ((FileOutputStream)output).getChannel();
                FileChannel source = ((FileInputStream)input).getChannel();
                source.transferTo(0L, Integer.MAX_VALUE, target);
                source.close();
                target.close();
                return;
            }
            catch (Exception target) {
                // empty catch block
            }
        }
        byte[] buf = new byte[8192];
        while ((length = input.read(buf)) >= 0) {
            output.write(buf, 0, length);
        }
        try {
            input.close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        try {
            output.close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public static void copyCompletely(Reader input, Writer output) throws IOException {
        int length;
        char[] buf = new char[8192];
        while ((length = input.read(buf)) >= 0) {
            output.write(buf, 0, length);
        }
        try {
            input.close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        try {
            output.close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public static void copyCompletely(URI input, URI output) throws IOException {
        File out = new File(output);
        File dir = out.getParentFile();
        dir.mkdirs();
        try (InputStream in = IOUtil.urlToStream(input);
             FileOutputStream os = new FileOutputStream(out);){
            IOUtil.copyCompletely(in, os);
        }
        catch (IllegalArgumentException e) {
            throw new IOException("Cannot copy to " + output);
        }
    }

    private static InputStream urlToStream(URI input) throws IOException {
        try {
            File f = new File(input);
            if (f.exists()) {
                return new FileInputStream(f);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return input.toURL().openStream();
    }

    public static File createDir(File rootdir, String subdir) {
        boolean created;
        File newdir = subdir == null ? rootdir : new File(rootdir, subdir);
        boolean bl = created = newdir.exists() && newdir.isDirectory() || newdir.mkdirs();
        assert (created) : "Could not create " + newdir.getAbsolutePath();
        return newdir;
    }

    public static Path getTempDir() {
        return tmpdir;
    }
}

