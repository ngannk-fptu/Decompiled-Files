/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.usermodel;

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
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookProvider;

public final class WorkbookFactory {
    private final List<WorkbookProvider> provider = new ArrayList<WorkbookProvider>();

    private WorkbookFactory() {
        ClassLoader cl = WorkbookFactory.class.getClassLoader();
        ServiceLoader.load(WorkbookProvider.class, cl).forEach(this.provider::add);
    }

    public static Workbook create(boolean xssf) throws IOException {
        return WorkbookFactory.wp(xssf ? FileMagic.OOXML : FileMagic.OLE2, WorkbookProvider::create);
    }

    public static Workbook create(POIFSFileSystem fs) throws IOException {
        return WorkbookFactory.create(fs, null);
    }

    private static Workbook create(POIFSFileSystem fs, String password) throws IOException {
        return WorkbookFactory.create(fs.getRoot(), password);
    }

    public static Workbook create(DirectoryNode root) throws IOException {
        return WorkbookFactory.create(root, null);
    }

    public static Workbook create(DirectoryNode root, String password) throws IOException {
        if (root.hasEntry("EncryptedPackage") || root.hasEntry("Package")) {
            return WorkbookFactory.wp(FileMagic.OOXML, w -> w.create(root, password));
        }
        return WorkbookFactory.wp(FileMagic.OLE2, w -> w.create(root, password));
    }

    public static Workbook create(InputStream inp) throws IOException, EncryptedDocumentException {
        return WorkbookFactory.create(inp, null);
    }

    public static Workbook create(InputStream inp, String password) throws IOException, EncryptedDocumentException {
        InputStream is = FileMagic.prepareToCheckMagic(inp);
        byte[] emptyFileCheck = new byte[1];
        is.mark(emptyFileCheck.length);
        if (is.read(emptyFileCheck) < emptyFileCheck.length) {
            throw new EmptyFileException();
        }
        is.reset();
        FileMagic fm = FileMagic.valueOf(is);
        if (FileMagic.OOXML == fm) {
            return WorkbookFactory.wp(fm, w -> w.create(is));
        }
        if (FileMagic.OLE2 != fm) {
            throw new IOException("Can't open workbook - unsupported file type: " + (Object)((Object)fm));
        }
        POIFSFileSystem poifs = new POIFSFileSystem(is);
        DirectoryNode root = poifs.getRoot();
        boolean isOOXML = root.hasEntry("EncryptedPackage") || root.hasEntry("Package");
        return WorkbookFactory.wp(isOOXML ? FileMagic.OOXML : fm, w -> w.create(root, password));
    }

    public static Workbook create(File file) throws IOException, EncryptedDocumentException {
        return WorkbookFactory.create(file, null);
    }

    public static Workbook create(File file, String password) throws IOException, EncryptedDocumentException {
        return WorkbookFactory.create(file, password, false);
    }

    public static Workbook create(File file, String password, boolean readOnly) throws IOException, EncryptedDocumentException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.toString());
        }
        if (file.length() == 0L) {
            throw new EmptyFileException(file);
        }
        FileMagic fm = FileMagic.valueOf(file);
        if (fm == FileMagic.OOXML) {
            return WorkbookFactory.wp(fm, w -> w.create(file, password, readOnly));
        }
        if (fm == FileMagic.OLE2) {
            boolean ooxmlEnc;
            try (POIFSFileSystem fs = new POIFSFileSystem(file, true);){
                DirectoryNode root = fs.getRoot();
                ooxmlEnc = root.hasEntry("EncryptedPackage") || root.hasEntry("Package");
            }
            return WorkbookFactory.wp(ooxmlEnc ? FileMagic.OOXML : fm, w -> w.create(file, password, readOnly));
        }
        throw new IOException("Can't open workbook - unsupported file type: " + (Object)((Object)fm));
    }

    private static Workbook wp(FileMagic fm, ProviderMethod fun) throws IOException {
        for (WorkbookProvider prov : Singleton.INSTANCE.provider) {
            if (!prov.accepts(fm)) continue;
            return fun.create(prov);
        }
        throw new IOException("Your InputStream was neither an OLE2 stream, nor an OOXML stream or you haven't provide the poi-ooxml*.jar in the classpath/modulepath - FileMagic: " + (Object)((Object)fm) + ", having providers: " + Singleton.INSTANCE.provider);
    }

    public static void addProvider(WorkbookProvider provider) {
        Singleton.INSTANCE.provider.add(provider);
    }

    public static void removeProvider(Class<? extends WorkbookProvider> provider) {
        Singleton.INSTANCE.provider.removeIf(p -> p.getClass().isAssignableFrom(provider));
    }

    private static interface ProviderMethod {
        public Workbook create(WorkbookProvider var1) throws IOException;
    }

    private static class Singleton {
        private static final WorkbookFactory INSTANCE = new WorkbookFactory();

        private Singleton() {
        }
    }
}

