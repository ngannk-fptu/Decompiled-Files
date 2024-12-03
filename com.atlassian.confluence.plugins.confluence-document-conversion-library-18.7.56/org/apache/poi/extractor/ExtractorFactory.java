/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.extractor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.EmptyFileException;
import org.apache.poi.extractor.ExtractorProvider;
import org.apache.poi.extractor.POIOLE2TextExtractor;
import org.apache.poi.extractor.POITextExtractor;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.Entry;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.IOUtils;

public final class ExtractorFactory {
    public static final String OOXML_PACKAGE = "Package";
    private static final Logger LOGGER = LogManager.getLogger(ExtractorFactory.class);
    private static final ThreadLocal<Boolean> threadPreferEventExtractors = ThreadLocal.withInitial(() -> Boolean.FALSE);
    private static Boolean allPreferEventExtractors;
    private final List<ExtractorProvider> provider = new ArrayList<ExtractorProvider>();

    private ExtractorFactory() {
        ClassLoader cl = ExtractorFactory.class.getClassLoader();
        ServiceLoader.load(ExtractorProvider.class, cl).forEach(this.provider::add);
    }

    public static boolean getThreadPrefersEventExtractors() {
        return threadPreferEventExtractors.get();
    }

    public static Boolean getAllThreadsPreferEventExtractors() {
        return allPreferEventExtractors;
    }

    public static void setThreadPrefersEventExtractors(boolean preferEventExtractors) {
        threadPreferEventExtractors.set(preferEventExtractors);
    }

    public static void setAllThreadsPreferEventExtractors(Boolean preferEventExtractors) {
        allPreferEventExtractors = preferEventExtractors;
    }

    public static boolean getPreferEventExtractor() {
        return allPreferEventExtractors != null ? allPreferEventExtractors.booleanValue() : threadPreferEventExtractors.get().booleanValue();
    }

    public static POITextExtractor createExtractor(POIFSFileSystem fs) throws IOException {
        return ExtractorFactory.createExtractor(fs, Biff8EncryptionKey.getCurrentUserPassword());
    }

    public static POITextExtractor createExtractor(POIFSFileSystem fs, String password) throws IOException {
        return ExtractorFactory.createExtractor(fs.getRoot(), password);
    }

    public static POITextExtractor createExtractor(InputStream input) throws IOException {
        return ExtractorFactory.createExtractor(input, Biff8EncryptionKey.getCurrentUserPassword());
    }

    public static POITextExtractor createExtractor(InputStream input, String password) throws IOException {
        InputStream is = FileMagic.prepareToCheckMagic(input);
        byte[] emptyFileCheck = new byte[1];
        is.mark(emptyFileCheck.length);
        if (is.read(emptyFileCheck) < emptyFileCheck.length) {
            throw new EmptyFileException();
        }
        is.reset();
        FileMagic fm = FileMagic.valueOf(is);
        if (FileMagic.OOXML == fm) {
            return ExtractorFactory.wp(fm, w -> w.create(is, password));
        }
        if (FileMagic.OLE2 != fm) {
            throw new IOException("Can't create extractor - unsupported file type: " + (Object)((Object)fm));
        }
        POIFSFileSystem poifs = new POIFSFileSystem(is);
        DirectoryNode root = poifs.getRoot();
        boolean isOOXML = root.hasEntry("EncryptedPackage") || root.hasEntry(OOXML_PACKAGE);
        return ExtractorFactory.wp(isOOXML ? FileMagic.OOXML : fm, w -> w.create(root, password));
    }

    public static POITextExtractor createExtractor(File file) throws IOException {
        return ExtractorFactory.createExtractor(file, Biff8EncryptionKey.getCurrentUserPassword());
    }

    public static POITextExtractor createExtractor(File file, String password) throws IOException {
        if (file.length() == 0L) {
            throw new EmptyFileException(file);
        }
        FileMagic fm = FileMagic.valueOf(file);
        if (FileMagic.OOXML == fm) {
            return ExtractorFactory.wp(fm, w -> w.create(file, password));
        }
        if (FileMagic.OLE2 != fm) {
            throw new IOException("Can't create extractor - unsupported file type: " + (Object)((Object)fm));
        }
        POIFSFileSystem poifs = null;
        try {
            poifs = new POIFSFileSystem(file, true);
            DirectoryNode root = poifs.getRoot();
            boolean isOOXML = root.hasEntry("EncryptedPackage") || root.hasEntry(OOXML_PACKAGE);
            return ExtractorFactory.wp(isOOXML ? FileMagic.OOXML : fm, w -> w.create(root, password));
        }
        catch (IOException | RuntimeException e) {
            IOUtils.closeQuietly(poifs);
            throw e;
        }
    }

    public static POITextExtractor createExtractor(DirectoryNode root) throws IOException {
        return ExtractorFactory.createExtractor(root, Biff8EncryptionKey.getCurrentUserPassword());
    }

    public static POITextExtractor createExtractor(DirectoryNode root, String password) throws IOException {
        if (root.hasEntry("EncryptedPackage") || root.hasEntry(OOXML_PACKAGE)) {
            return ExtractorFactory.wp(FileMagic.OOXML, w -> w.create(root, password));
        }
        return ExtractorFactory.wp(FileMagic.OLE2, w -> w.create(root, password));
    }

    public static POITextExtractor[] getEmbeddedDocsTextExtractors(POIOLE2TextExtractor ext) throws IOException {
        if (ext == null) {
            throw new IllegalStateException("extractor must be given");
        }
        ArrayList<Entry> dirs = new ArrayList<Entry>();
        ArrayList<InputStream> nonPOIFS = new ArrayList<InputStream>();
        DirectoryEntry root = ext.getRoot();
        if (root == null) {
            throw new IllegalStateException("The extractor didn't know which POIFS it came from!");
        }
        if (ext instanceof ExcelExtractor) {
            StreamSupport.stream(root.spliterator(), false).filter(entry -> entry.getName().startsWith("MBD")).forEach(dirs::add);
        } else {
            for (ExtractorProvider prov : Singleton.INSTANCE.provider) {
                if (!prov.accepts(FileMagic.OLE2)) continue;
                prov.identifyEmbeddedResources(ext, dirs, nonPOIFS);
                break;
            }
        }
        if (dirs.isEmpty() && nonPOIFS.isEmpty()) {
            return new POITextExtractor[0];
        }
        ArrayList<POITextExtractor> textExtractors = new ArrayList<POITextExtractor>();
        for (Entry dir : dirs) {
            textExtractors.add(ExtractorFactory.createExtractor((DirectoryNode)dir));
        }
        for (InputStream stream : nonPOIFS) {
            try {
                textExtractors.add(ExtractorFactory.createExtractor(stream));
            }
            catch (IOException e) {
                LOGGER.atInfo().log("Format not supported yet ({})", (Object)e.getLocalizedMessage());
            }
        }
        return textExtractors.toArray(new POITextExtractor[0]);
    }

    private static POITextExtractor wp(FileMagic fm, ProviderMethod fun) throws IOException {
        for (ExtractorProvider prov : Singleton.INSTANCE.provider) {
            POITextExtractor ext;
            if (!prov.accepts(fm) || (ext = fun.create(prov)) == null) continue;
            return ext;
        }
        throw new IOException("Your InputStream was neither an OLE2 stream, nor an OOXML stream or you haven't provide the poi-ooxml*.jar and/or poi-scratchpad*.jar in the classpath/modulepath - FileMagic: " + (Object)((Object)fm) + ", providers: " + Singleton.INSTANCE.provider);
    }

    public static void addProvider(ExtractorProvider provider) {
        Singleton.INSTANCE.provider.add(provider);
    }

    public static void removeProvider(Class<? extends ExtractorProvider> provider) {
        Singleton.INSTANCE.provider.removeIf(p -> p.getClass().isAssignableFrom(provider));
    }

    private static interface ProviderMethod {
        public POITextExtractor create(ExtractorProvider var1) throws IOException;
    }

    private static class Singleton {
        private static final ExtractorFactory INSTANCE = new ExtractorFactory();

        private Singleton() {
        }
    }
}

