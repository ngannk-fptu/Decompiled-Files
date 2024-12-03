/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.zip.GZIPInputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Expand;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.bzip2.CBZip2InputStream;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;

public class Untar
extends Expand {
    private UntarCompressionMethod compression = new UntarCompressionMethod();

    public Untar() {
        super(null);
    }

    public void setCompression(UntarCompressionMethod method) {
        this.compression = method;
    }

    @Override
    public void setScanForUnicodeExtraFields(boolean b) {
        throw new BuildException("The " + this.getTaskName() + " task doesn't support the encoding attribute", this.getLocation());
    }

    @Override
    protected void expandFile(FileUtils fileUtils, File srcF, File dir) {
        if (!srcF.exists()) {
            throw new BuildException("Unable to untar " + srcF + " as the file does not exist", this.getLocation());
        }
        try (InputStream fis = Files.newInputStream(srcF.toPath(), new OpenOption[0]);){
            this.expandStream(srcF.getPath(), fis, dir);
        }
        catch (IOException ioe) {
            throw new BuildException("Error while expanding " + srcF.getPath() + "\n" + ioe.toString(), ioe, this.getLocation());
        }
    }

    @Override
    protected void expandResource(Resource srcR, File dir) {
        if (!srcR.isExists()) {
            throw new BuildException("Unable to untar " + srcR.getName() + " as the it does not exist", this.getLocation());
        }
        try (InputStream i = srcR.getInputStream();){
            this.expandStream(srcR.getName(), i, dir);
        }
        catch (IOException ioe) {
            throw new BuildException("Error while expanding " + srcR.getName(), ioe, this.getLocation());
        }
    }

    private void expandStream(String name, InputStream stream, File dir) throws IOException {
        try (TarInputStream tis = new TarInputStream(this.compression.decompress(name, new BufferedInputStream(stream)), this.getEncoding());){
            TarEntry te;
            this.log("Expanding: " + name + " into " + dir, 2);
            boolean empty = true;
            FileNameMapper mapper = this.getMapper();
            while ((te = tis.getNextEntry()) != null) {
                empty = false;
                this.extractFile(FileUtils.getFileUtils(), null, dir, tis, te.getName(), te.getModTime(), te.isDirectory(), mapper);
            }
            if (empty && this.getFailOnEmptyArchive()) {
                throw new BuildException("archive '%s' is empty", name);
            }
            this.log("expand complete", 3);
        }
    }

    public static final class UntarCompressionMethod
    extends EnumeratedAttribute {
        private static final String NONE = "none";
        private static final String GZIP = "gzip";
        private static final String BZIP2 = "bzip2";
        private static final String XZ = "xz";

        public UntarCompressionMethod() {
            this.setValue(NONE);
        }

        @Override
        public String[] getValues() {
            return new String[]{NONE, GZIP, BZIP2, XZ};
        }

        public InputStream decompress(String name, InputStream istream) throws IOException, BuildException {
            String v = this.getValue();
            if (GZIP.equals(v)) {
                return new GZIPInputStream(istream);
            }
            if (XZ.equals(v)) {
                return UntarCompressionMethod.newXZInputStream(istream);
            }
            if (BZIP2.equals(v)) {
                char[] magic;
                for (char c : magic = new char[]{'B', 'Z'}) {
                    if (istream.read() == c) continue;
                    throw new BuildException("Invalid bz2 file." + name);
                }
                return new CBZip2InputStream(istream);
            }
            return istream;
        }

        private static InputStream newXZInputStream(InputStream istream) throws BuildException {
            try {
                Class<InputStream> clazz = Class.forName("org.tukaani.xz.XZInputStream").asSubclass(InputStream.class);
                Constructor<InputStream> c = clazz.getConstructor(InputStream.class);
                return c.newInstance(istream);
            }
            catch (ClassNotFoundException ex) {
                throw new BuildException("xz decompression requires the XZ for Java library", ex);
            }
            catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException ex) {
                throw new BuildException("failed to create XZInputStream", ex);
            }
        }
    }
}

