/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.sl.usermodel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import org.apache.poi.EmptyFileException;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.sl.usermodel.SlideShow;
import org.apache.poi.sl.usermodel.SlideShowProvider;

public final class SlideShowFactory {
    private final List<SlideShowProvider<?, ?>> provider = new ArrayList();

    private SlideShowFactory() {
        ClassLoader cl = SlideShowFactory.class.getClassLoader();
        ServiceLoader.load(SlideShowProvider.class, cl).forEach(this.provider::add);
    }

    public static SlideShow<?, ?> create(boolean XSLF) throws IOException {
        return SlideShowFactory.wp(XSLF ? FileMagic.OOXML : FileMagic.OLE2, SlideShowProvider::create);
    }

    public static SlideShow<?, ?> create(POIFSFileSystem fs) throws IOException {
        return SlideShowFactory.create(fs, null);
    }

    private static SlideShow<?, ?> create(POIFSFileSystem fs, String password) throws IOException {
        return SlideShowFactory.create(fs.getRoot(), password);
    }

    public static SlideShow<?, ?> create(DirectoryNode root) throws IOException {
        return SlideShowFactory.create(root, null);
    }

    public static SlideShow<?, ?> create(DirectoryNode root, String password) throws IOException {
        if (root.hasEntry("EncryptedPackage") || root.hasEntry("Package")) {
            return SlideShowFactory.wp(FileMagic.OOXML, w -> w.create(root, password));
        }
        return SlideShowFactory.wp(FileMagic.OLE2, w -> w.create(root, password));
    }

    public static SlideShow<?, ?> create(InputStream inp) throws IOException, EncryptedDocumentException {
        return SlideShowFactory.create(inp, null);
    }

    public static SlideShow<?, ?> create(InputStream inp, String password) throws IOException, EncryptedDocumentException {
        InputStream is = FileMagic.prepareToCheckMagic(inp);
        byte[] emptyFileCheck = new byte[1];
        is.mark(emptyFileCheck.length);
        if (is.read(emptyFileCheck) < emptyFileCheck.length) {
            throw new EmptyFileException();
        }
        is.reset();
        FileMagic fm = FileMagic.valueOf(is);
        if (FileMagic.OOXML == fm) {
            return SlideShowFactory.wp(fm, w -> w.create(is));
        }
        if (FileMagic.OLE2 != fm) {
            throw new IOException("Can't open slideshow - unsupported file type: " + (Object)((Object)fm));
        }
        POIFSFileSystem poifs = new POIFSFileSystem(is);
        DirectoryNode root = poifs.getRoot();
        boolean isOOXML = root.hasEntry("EncryptedPackage") || root.hasEntry("Package");
        return SlideShowFactory.wp(isOOXML ? FileMagic.OOXML : fm, w -> w.create(poifs.getRoot(), password));
    }

    public static SlideShow<?, ?> create(File file) throws IOException, EncryptedDocumentException {
        return SlideShowFactory.create(file, null);
    }

    public static SlideShow<?, ?> create(File file, String password) throws IOException, EncryptedDocumentException {
        return SlideShowFactory.create(file, password, false);
    }

    public static SlideShow<?, ?> create(File file, String password, boolean readOnly) throws IOException, EncryptedDocumentException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.toString());
        }
        if (file.length() == 0L) {
            throw new EmptyFileException(file);
        }
        FileMagic fm = FileMagic.valueOf(file);
        if (fm == FileMagic.OOXML) {
            return SlideShowFactory.wp(fm, w -> w.create(file, password, readOnly));
        }
        if (fm == FileMagic.OLE2) {
            boolean ooxmlEnc;
            try (POIFSFileSystem fs = new POIFSFileSystem(file, true);){
                DirectoryNode root = fs.getRoot();
                ooxmlEnc = root.hasEntry("EncryptedPackage") || root.hasEntry("Package");
            }
            return SlideShowFactory.wp(ooxmlEnc ? FileMagic.OOXML : fm, w -> w.create(file, password, readOnly));
        }
        throw new IOException("Can't open slideshow - unsupported file type: " + (Object)((Object)fm));
    }

    private static SlideShow<?, ?> wp(FileMagic fm, ProviderMethod fun) throws IOException {
        for (SlideShowProvider<?, ?> prov : Singleton.INSTANCE.provider) {
            if (!prov.accepts(fm)) continue;
            return fun.create(prov);
        }
        throw new IOException("Your InputStream was neither an OLE2 stream, nor an OOXML stream or you haven't provide the poi-ooxml*.jar in the classpath/modulepath - FileMagic: " + (Object)((Object)fm));
    }

    public static void addProvider(SlideShowProvider<?, ?> provider) {
        Singleton.INSTANCE.provider.add(provider);
    }

    public static void removeProvider(Class<? extends SlideShowProvider<?, ?>> provider) {
        Singleton.INSTANCE.provider.removeIf(p -> p.getClass().isAssignableFrom(provider));
    }

    private static interface ProviderMethod {
        public SlideShow<?, ?> create(SlideShowProvider<?, ?> var1) throws IOException;
    }

    private static class Singleton {
        private static final SlideShowFactory INSTANCE = new SlideShowFactory();

        private Singleton() {
        }
    }
}

