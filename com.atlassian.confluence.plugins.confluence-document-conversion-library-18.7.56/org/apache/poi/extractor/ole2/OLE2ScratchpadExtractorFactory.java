/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.input.UnsynchronizedByteArrayInputStream
 */
package org.apache.poi.extractor.ole2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.StreamSupport;
import org.apache.commons.io.input.UnsynchronizedByteArrayInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.extractor.ExtractorProvider;
import org.apache.poi.extractor.POIOLE2TextExtractor;
import org.apache.poi.extractor.POITextExtractor;
import org.apache.poi.hdgf.extractor.VisioTextExtractor;
import org.apache.poi.hpbf.extractor.PublisherTextExtractor;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.hsmf.MAPIMessage;
import org.apache.poi.hsmf.datatypes.AttachmentChunks;
import org.apache.poi.hsmf.extractor.OutlookTextExtractor;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.hwpf.OldWordFileFormatException;
import org.apache.poi.hwpf.extractor.Word6Extractor;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.Entry;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.sl.extractor.SlideShowExtractor;
import org.apache.poi.sl.usermodel.SlideShowFactory;

public class OLE2ScratchpadExtractorFactory
implements ExtractorProvider {
    private static final Logger LOG = LogManager.getLogger(OLE2ScratchpadExtractorFactory.class);
    private static final String[] OUTLOOK_ENTRY_NAMES = new String[]{"__substg1.0_1000001E", "__substg1.0_1000001F", "__substg1.0_0047001E", "__substg1.0_0047001F", "__substg1.0_0037001E", "__substg1.0_0037001F"};

    @Override
    public boolean accepts(FileMagic fm) {
        return FileMagic.OLE2 == fm;
    }

    @Override
    public POITextExtractor create(File file, String password) throws IOException {
        return this.create(new POIFSFileSystem(file, true).getRoot(), password);
    }

    @Override
    public POITextExtractor create(InputStream inputStream, String password) throws IOException {
        return this.create(new POIFSFileSystem(inputStream).getRoot(), password);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    @Override
    public POITextExtractor create(DirectoryNode poifsDir, String password) throws IOException {
        block16: {
            String oldPW = Biff8EncryptionKey.getCurrentUserPassword();
            try {
                Biff8EncryptionKey.setCurrentUserPassword(password);
                if (poifsDir.hasEntry("WordDocument")) {
                    try {
                        WordExtractor wordExtractor = new WordExtractor(poifsDir);
                        return wordExtractor;
                    }
                    catch (OldWordFileFormatException e) {
                        Word6Extractor word6Extractor = new Word6Extractor(poifsDir);
                        Biff8EncryptionKey.setCurrentUserPassword(oldPW);
                        return word6Extractor;
                    }
                }
                if (poifsDir.hasEntry("PowerPoint Document") || poifsDir.hasEntry("PP97_DUALSTORAGE")) {
                    SlideShowExtractor<HSLFShape, HSLFTextParagraph> slideShowExtractor = new SlideShowExtractor<HSLFShape, HSLFTextParagraph>((HSLFSlideShow)SlideShowFactory.create(poifsDir));
                    return slideShowExtractor;
                }
                if (poifsDir.hasEntry("VisioDocument")) {
                    VisioTextExtractor visioTextExtractor = new VisioTextExtractor(poifsDir);
                    return visioTextExtractor;
                }
                if (poifsDir.hasEntry("Quill")) {
                    PublisherTextExtractor publisherTextExtractor = new PublisherTextExtractor(poifsDir);
                    return publisherTextExtractor;
                }
                for (String entryName : OUTLOOK_ENTRY_NAMES) {
                    if (!poifsDir.hasEntry(entryName)) continue;
                    OutlookTextExtractor outlookTextExtractor = new OutlookTextExtractor(poifsDir);
                    return outlookTextExtractor;
                }
                {
                    break block16;
                    catch (Throwable throwable) {
                        throw throwable;
                    }
                }
            }
            finally {
                Biff8EncryptionKey.setCurrentUserPassword(oldPW);
            }
        }
        return null;
    }

    @Override
    public void identifyEmbeddedResources(POIOLE2TextExtractor ext, List<Entry> dirs, List<InputStream> nonPOIFS) {
        DirectoryEntry root = ext.getRoot();
        if (root == null) {
            throw new IllegalStateException("The extractor didn't know which POIFS it came from!");
        }
        if (ext instanceof ExcelExtractor) {
            StreamSupport.stream(root.spliterator(), false).filter(entry -> entry.getName().startsWith("MBD")).forEach(dirs::add);
        } else if (ext instanceof WordExtractor) {
            try {
                DirectoryEntry op = (DirectoryEntry)root.getEntry("ObjectPool");
                StreamSupport.stream(op.spliterator(), false).filter(entry -> entry.getName().startsWith("_")).forEach(dirs::add);
            }
            catch (FileNotFoundException e) {
                LOG.atInfo().withThrowable(e).log("Ignoring FileNotFoundException while extracting Word document");
            }
        } else if (ext instanceof OutlookTextExtractor) {
            MAPIMessage msg = ((OutlookTextExtractor)ext).getMAPIMessage();
            for (AttachmentChunks attachment : msg.getAttachmentFiles()) {
                if (attachment.getAttachData() != null) {
                    byte[] data = attachment.getAttachData().getValue();
                    nonPOIFS.add((InputStream)new UnsynchronizedByteArrayInputStream(data));
                    continue;
                }
                if (attachment.getAttachmentDirectory() == null) continue;
                dirs.add(attachment.getAttachmentDirectory().getDirectory());
            }
        }
    }
}

