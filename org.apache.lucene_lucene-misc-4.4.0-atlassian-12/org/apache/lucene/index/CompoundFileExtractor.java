/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.store.CompoundFileDirectory
 *  org.apache.lucene.store.Directory
 *  org.apache.lucene.store.FSDirectory
 *  org.apache.lucene.store.IOContext
 *  org.apache.lucene.store.IndexInput
 *  org.apache.lucene.util.ArrayUtil
 *  org.apache.lucene.util.CommandLineUtil
 */
package org.apache.lucene.index;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.lucene.store.CompoundFileDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.CommandLineUtil;

public class CompoundFileExtractor {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void main(String[] args) {
        String filename = null;
        boolean extract = false;
        String dirImpl = null;
        for (int j = 0; j < args.length; ++j) {
            String arg = args[j];
            if ("-extract".equals(arg)) {
                extract = true;
                continue;
            }
            if ("-dir-impl".equals(arg)) {
                if (j == args.length - 1) {
                    System.out.println("ERROR: missing value for -dir-impl option");
                    System.exit(1);
                }
                dirImpl = args[++j];
                continue;
            }
            if (filename != null) continue;
            filename = arg;
        }
        if (filename == null) {
            System.out.println("Usage: org.apache.lucene.index.CompoundFileExtractor [-extract] [-dir-impl X] <cfsfile>");
            return;
        }
        FSDirectory dir = null;
        CompoundFileDirectory cfr = null;
        IOContext context = IOContext.READ;
        try {
            File file = new File(filename);
            String dirname = file.getAbsoluteFile().getParent();
            filename = file.getName();
            dir = dirImpl == null ? FSDirectory.open((File)new File(dirname)) : CommandLineUtil.newFSDirectory((String)dirImpl, (File)new File(dirname));
            cfr = new CompoundFileDirectory((Directory)dir, filename, IOContext.DEFAULT, false);
            String[] files = cfr.listAll();
            ArrayUtil.timSort((Comparable[])files);
            for (int i = 0; i < files.length; ++i) {
                long len;
                if (extract) {
                    int bufLen;
                    System.out.println("extract " + files[i] + " with " + len + " bytes to local directory...");
                    IndexInput ii = cfr.openInput(files[i], context);
                    FileOutputStream f = new FileOutputStream(files[i]);
                    byte[] buffer = new byte[1024];
                    int chunk = buffer.length;
                    for (len = cfr.fileLength(files[i]); len > 0L; len -= (long)bufLen) {
                        bufLen = (int)Math.min((long)chunk, len);
                        ii.readBytes(buffer, 0, bufLen);
                        f.write(buffer, 0, bufLen);
                    }
                    f.close();
                    ii.close();
                    continue;
                }
                System.out.println(files[i] + ": " + len + " bytes");
            }
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        finally {
            try {
                if (dir != null) {
                    dir.close();
                }
                if (cfr != null) {
                    cfr.close();
                }
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}

