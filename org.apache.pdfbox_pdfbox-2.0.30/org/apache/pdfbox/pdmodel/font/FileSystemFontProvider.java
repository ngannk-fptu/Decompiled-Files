/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.fontbox.FontBoxFont
 *  org.apache.fontbox.cff.CFFCIDFont
 *  org.apache.fontbox.cff.CFFFont
 *  org.apache.fontbox.ttf.NamingTable
 *  org.apache.fontbox.ttf.OS2WindowsMetricsTable
 *  org.apache.fontbox.ttf.OTFParser
 *  org.apache.fontbox.ttf.OpenTypeFont
 *  org.apache.fontbox.ttf.TTFParser
 *  org.apache.fontbox.ttf.TTFTable
 *  org.apache.fontbox.ttf.TrueTypeCollection
 *  org.apache.fontbox.ttf.TrueTypeCollection$TrueTypeFontProcessor
 *  org.apache.fontbox.ttf.TrueTypeFont
 *  org.apache.fontbox.type1.Type1Font
 *  org.apache.fontbox.util.autodetect.FontFileFinder
 */
package org.apache.pdfbox.pdmodel.font;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.AccessControlException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.FontBoxFont;
import org.apache.fontbox.cff.CFFCIDFont;
import org.apache.fontbox.cff.CFFFont;
import org.apache.fontbox.ttf.NamingTable;
import org.apache.fontbox.ttf.OS2WindowsMetricsTable;
import org.apache.fontbox.ttf.OTFParser;
import org.apache.fontbox.ttf.OpenTypeFont;
import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TTFTable;
import org.apache.fontbox.ttf.TrueTypeCollection;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.fontbox.type1.Type1Font;
import org.apache.fontbox.util.autodetect.FontFileFinder;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.font.CIDSystemInfo;
import org.apache.pdfbox.pdmodel.font.FontCache;
import org.apache.pdfbox.pdmodel.font.FontFormat;
import org.apache.pdfbox.pdmodel.font.FontInfo;
import org.apache.pdfbox.pdmodel.font.FontProvider;
import org.apache.pdfbox.pdmodel.font.PDPanoseClassification;
import org.apache.pdfbox.util.Charsets;
import org.apache.pdfbox.util.Hex;

final class FileSystemFontProvider
extends FontProvider {
    private static final Log LOG = LogFactory.getLog(FileSystemFontProvider.class);
    private final List<FSFontInfo> fontInfoList = new ArrayList<FSFontInfo>();
    private final FontCache cache;

    private FSFontInfo createFSIgnored(File file, FontFormat format, String postScriptName) {
        String hash;
        try {
            hash = FileSystemFontProvider.computeHash(FileSystemFontProvider.readAllBytes(file));
        }
        catch (IOException ex) {
            hash = "";
        }
        return new FSFontInfo(file, format, postScriptName, null, 0, 0, 0, 0, 0, null, null, hash, file.lastModified());
    }

    FileSystemFontProvider(FontCache cache) {
        this.cache = cache;
        try {
            if (LOG.isTraceEnabled()) {
                LOG.trace((Object)"Will search the local system for fonts");
            }
            FontFileFinder fontFileFinder = new FontFileFinder();
            List fonts = fontFileFinder.find();
            ArrayList<File> files = new ArrayList<File>(fonts.size());
            for (URI font : fonts) {
                files.add(new File(font));
            }
            if (LOG.isTraceEnabled()) {
                LOG.trace((Object)("Found " + files.size() + " fonts on the local system"));
            }
            if (!files.isEmpty()) {
                List<FSFontInfo> cachedInfos = this.loadDiskCache(files);
                if (cachedInfos != null && !cachedInfos.isEmpty()) {
                    this.fontInfoList.addAll(cachedInfos);
                } else {
                    LOG.warn((Object)"Building on-disk font cache, this may take a while");
                    this.scanFonts(files);
                    this.saveDiskCache();
                    LOG.warn((Object)("Finished building on-disk font cache, found " + this.fontInfoList.size() + " fonts"));
                }
            }
        }
        catch (AccessControlException e) {
            LOG.error((Object)"Error accessing the file system", (Throwable)e);
        }
    }

    private void scanFonts(List<File> files) {
        for (File file : files) {
            try {
                String filePath = file.getPath().toLowerCase();
                if (filePath.endsWith(".ttf") || filePath.endsWith(".otf")) {
                    this.addTrueTypeFont(file);
                    continue;
                }
                if (filePath.endsWith(".ttc") || filePath.endsWith(".otc")) {
                    this.addTrueTypeCollection(file);
                    continue;
                }
                if (!filePath.endsWith(".pfb")) continue;
                this.addType1Font(file);
            }
            catch (IOException e) {
                LOG.warn((Object)("Error parsing font " + file.getPath()), (Throwable)e);
            }
        }
    }

    private File getDiskCacheFile() {
        String path = System.getProperty("pdfbox.fontcache");
        if (FileSystemFontProvider.isBadPath(path) && FileSystemFontProvider.isBadPath(path = System.getProperty("user.home"))) {
            path = System.getProperty("java.io.tmpdir");
        }
        return new File(path, ".pdfbox.cache");
    }

    private static boolean isBadPath(String path) {
        return path == null || !new File(path).isDirectory() || !new File(path).canWrite();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void saveDiskCache() {
        BufferedWriter writer = null;
        try {
            File file = this.getDiskCacheFile();
            writer = new BufferedWriter(new FileWriter(file));
        }
        catch (SecurityException e) {
            IOUtils.closeQuietly(writer);
            return;
        }
        try {
            for (FSFontInfo fontInfo : this.fontInfoList) {
                this.writeFontInfo(writer, fontInfo);
            }
        }
        catch (IOException e) {
            try {
                LOG.warn((Object)"Could not write to font cache", (Throwable)e);
                LOG.warn((Object)"Installed fonts information will have to be reloaded for each start");
                LOG.warn((Object)"You can assign a directory to the 'pdfbox.fontcache' property");
            }
            catch (Throwable throwable) {
                IOUtils.closeQuietly(writer);
                throw throwable;
            }
            IOUtils.closeQuietly(writer);
        }
        IOUtils.closeQuietly(writer);
    }

    private void writeFontInfo(BufferedWriter writer, FSFontInfo fontInfo) throws IOException {
        writer.write(fontInfo.postScriptName.trim());
        writer.write("|");
        writer.write(fontInfo.format.toString());
        writer.write("|");
        if (fontInfo.cidSystemInfo != null) {
            writer.write(fontInfo.cidSystemInfo.getRegistry() + '-' + fontInfo.cidSystemInfo.getOrdering() + '-' + fontInfo.cidSystemInfo.getSupplement());
        }
        writer.write("|");
        if (fontInfo.usWeightClass > -1) {
            writer.write(Integer.toHexString(fontInfo.usWeightClass));
        }
        writer.write("|");
        if (fontInfo.sFamilyClass > -1) {
            writer.write(Integer.toHexString(fontInfo.sFamilyClass));
        }
        writer.write("|");
        writer.write(Integer.toHexString(fontInfo.ulCodePageRange1));
        writer.write("|");
        writer.write(Integer.toHexString(fontInfo.ulCodePageRange2));
        writer.write("|");
        if (fontInfo.macStyle > -1) {
            writer.write(Integer.toHexString(fontInfo.macStyle));
        }
        writer.write("|");
        if (fontInfo.panose != null) {
            byte[] bytes = fontInfo.panose.getBytes();
            for (int i = 0; i < 10; ++i) {
                String str = Integer.toHexString(bytes[i]);
                if (str.length() == 1) {
                    writer.write(48);
                }
                writer.write(str);
            }
        }
        writer.write("|");
        writer.write(fontInfo.file.getAbsolutePath());
        writer.write("|");
        writer.write(fontInfo.hash);
        writer.write("|");
        writer.write(Long.toString(fontInfo.file.lastModified()));
        writer.newLine();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private List<FSFontInfo> loadDiskCache(List<File> files) {
        HashSet<String> pending = new HashSet<String>(files.size());
        for (File file : files) {
            pending.add(file.getAbsolutePath());
        }
        ArrayList<FSFontInfo> results = new ArrayList<FSFontInfo>();
        File diskCacheFile = null;
        boolean fileExists = false;
        try {
            diskCacheFile = this.getDiskCacheFile();
            fileExists = diskCacheFile.exists();
        }
        catch (SecurityException e) {
            LOG.debug((Object)"Error checking for file existence", (Throwable)e);
        }
        if (fileExists) {
            BufferedReader reader = null;
            try {
                String line;
                reader = new BufferedReader(new FileReader(diskCacheFile));
                while ((line = reader.readLine()) != null) {
                    File fontFile;
                    block24: {
                        String[] parts = line.split("\\|", 12);
                        if (parts.length < 10) {
                            LOG.warn((Object)("Incorrect line '" + line + "' in font disk cache is skipped"));
                            continue;
                        }
                        CIDSystemInfo cidSystemInfo = null;
                        int usWeightClass = -1;
                        int sFamilyClass = -1;
                        int macStyle = -1;
                        byte[] panose = null;
                        String hash = "";
                        long lastModified = 0L;
                        String postScriptName = parts[0];
                        FontFormat format = FontFormat.valueOf(parts[1]);
                        if (parts[2].length() > 0) {
                            String[] ros = parts[2].split("-");
                            cidSystemInfo = new CIDSystemInfo(ros[0], ros[1], Integer.parseInt(ros[2]));
                        }
                        if (parts[3].length() > 0) {
                            usWeightClass = (int)Long.parseLong(parts[3], 16);
                        }
                        if (parts[4].length() > 0) {
                            sFamilyClass = (int)Long.parseLong(parts[4], 16);
                        }
                        int ulCodePageRange1 = (int)Long.parseLong(parts[5], 16);
                        int ulCodePageRange2 = (int)Long.parseLong(parts[6], 16);
                        if (parts[7].length() > 0) {
                            macStyle = (int)Long.parseLong(parts[7], 16);
                        }
                        if (parts[8].length() > 0) {
                            panose = new byte[10];
                            for (int i = 0; i < 10; ++i) {
                                String str = parts[8].substring(i * 2, i * 2 + 2);
                                int b = Integer.parseInt(str, 16);
                                panose[i] = (byte)(b & 0xFF);
                            }
                        }
                        fontFile = new File(parts[9]);
                        if (parts.length >= 12 && !parts[10].isEmpty() && !parts[11].isEmpty()) {
                            hash = parts[10];
                            lastModified = Long.parseLong(parts[11]);
                        }
                        if (fontFile.exists()) {
                            boolean keep = false;
                            if (fontFile.lastModified() != lastModified) {
                                String newHash = FileSystemFontProvider.computeHash(FileSystemFontProvider.readAllBytes(fontFile));
                                if (newHash.equals(hash)) {
                                    keep = true;
                                    lastModified = fontFile.lastModified();
                                    hash = newHash;
                                }
                            } else {
                                keep = true;
                            }
                            if (keep) {
                                FSFontInfo info = new FSFontInfo(fontFile, format, postScriptName, cidSystemInfo, usWeightClass, sFamilyClass, ulCodePageRange1, ulCodePageRange2, macStyle, panose, this, hash, lastModified);
                                results.add(info);
                                break block24;
                            } else {
                                LOG.debug((Object)("Font file " + fontFile.getAbsolutePath() + " is different"));
                                continue;
                            }
                        }
                        LOG.debug((Object)("Font file " + fontFile.getAbsolutePath() + " not found, skipped"));
                    }
                    pending.remove(fontFile.getAbsolutePath());
                }
            }
            catch (IOException e) {
                List<FSFontInfo> list;
                try {
                    LOG.warn((Object)"Error loading font cache, will be re-built", (Throwable)e);
                    list = null;
                }
                catch (Throwable throwable) {
                    IOUtils.closeQuietly(reader);
                    throw throwable;
                }
                IOUtils.closeQuietly(reader);
                return list;
            }
            IOUtils.closeQuietly(reader);
        }
        if (!pending.isEmpty()) {
            LOG.warn((Object)(pending.size() + " new fonts found, font cache will be re-built"));
            return null;
        }
        return results;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void addTrueTypeCollection(final File ttcFile) throws IOException {
        TrueTypeCollection ttc = null;
        try {
            ttc = new TrueTypeCollection(ttcFile);
            ttc.processAllFonts(new TrueTypeCollection.TrueTypeFontProcessor(){

                public void process(TrueTypeFont ttf) throws IOException {
                    FileSystemFontProvider.this.addTrueTypeFontImpl(ttf, ttcFile);
                }
            });
        }
        catch (IOException e) {
            LOG.warn((Object)("Could not load font file: " + ttcFile), (Throwable)e);
        }
        finally {
            if (ttc != null) {
                ttc.close();
            }
        }
    }

    private void addTrueTypeFont(File ttfFile) throws IOException {
        FontFormat fontFormat = null;
        try {
            if (ttfFile.getPath().toLowerCase().endsWith(".otf")) {
                fontFormat = FontFormat.OTF;
                OTFParser parser = new OTFParser(false, true);
                OpenTypeFont otf = parser.parse(ttfFile);
                this.addTrueTypeFontImpl((TrueTypeFont)otf, ttfFile);
            } else {
                fontFormat = FontFormat.TTF;
                TTFParser parser = new TTFParser(false, true);
                TrueTypeFont ttf = parser.parse(ttfFile);
                this.addTrueTypeFontImpl(ttf, ttfFile);
            }
        }
        catch (IOException e) {
            LOG.warn((Object)("Could not load font file: " + ttfFile), (Throwable)e);
            this.fontInfoList.add(this.createFSIgnored(ttfFile, fontFormat, "*skipexception*"));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void addTrueTypeFontImpl(TrueTypeFont ttf, File file) throws IOException {
        try {
            if (ttf.getName() != null && ttf.getName().contains("|")) {
                this.fontInfoList.add(this.createFSIgnored(file, FontFormat.TTF, "*skippipeinname*"));
                LOG.warn((Object)("Skipping font with '|' in name " + ttf.getName() + " in file " + file));
            } else if (ttf.getName() != null) {
                NamingTable name;
                CIDSystemInfo ros;
                String format;
                if (ttf.getHeader() == null) {
                    this.fontInfoList.add(this.createFSIgnored(file, FontFormat.TTF, ttf.getName()));
                    return;
                }
                int macStyle = ttf.getHeader().getMacStyle();
                int sFamilyClass = -1;
                int usWeightClass = -1;
                int ulCodePageRange1 = 0;
                int ulCodePageRange2 = 0;
                byte[] panose = null;
                OS2WindowsMetricsTable os2WindowsMetricsTable = ttf.getOS2Windows();
                if (os2WindowsMetricsTable != null) {
                    sFamilyClass = os2WindowsMetricsTable.getFamilyClass();
                    usWeightClass = os2WindowsMetricsTable.getWeightClass();
                    ulCodePageRange1 = (int)os2WindowsMetricsTable.getCodePageRange1();
                    ulCodePageRange2 = (int)os2WindowsMetricsTable.getCodePageRange2();
                    panose = os2WindowsMetricsTable.getPanose();
                }
                InputStream is = ttf.getOriginalData();
                byte[] ba = IOUtils.toByteArray(is);
                is.close();
                String hash = FileSystemFontProvider.computeHash(ba);
                if (ttf instanceof OpenTypeFont && ((OpenTypeFont)ttf).isPostScript()) {
                    CFFFont cff;
                    format = "OTF";
                    ros = null;
                    OpenTypeFont otf = (OpenTypeFont)ttf;
                    if (otf.isSupportedOTF() && otf.getCFF() != null && (cff = otf.getCFF().getFont()) instanceof CFFCIDFont) {
                        CFFCIDFont cidFont = (CFFCIDFont)cff;
                        String registry = cidFont.getRegistry();
                        String ordering = cidFont.getOrdering();
                        int supplement = cidFont.getSupplement();
                        ros = new CIDSystemInfo(registry, ordering, supplement);
                    }
                    this.fontInfoList.add(new FSFontInfo(file, FontFormat.OTF, ttf.getName(), ros, usWeightClass, sFamilyClass, ulCodePageRange1, ulCodePageRange2, macStyle, panose, this, hash, file.lastModified()));
                } else {
                    ros = null;
                    if (ttf.getTableMap().containsKey("gcid")) {
                        byte[] bytes = ttf.getTableBytes((TTFTable)ttf.getTableMap().get("gcid"));
                        String reg = new String(bytes, 10, 64, Charsets.US_ASCII);
                        String registryName = reg.substring(0, reg.indexOf(0));
                        String ord = new String(bytes, 76, 64, Charsets.US_ASCII);
                        String orderName = ord.substring(0, ord.indexOf(0));
                        int supplementVersion = bytes[140] << 8 & (bytes[141] & 0xFF);
                        ros = new CIDSystemInfo(registryName, orderName, supplementVersion);
                    }
                    format = "TTF";
                    this.fontInfoList.add(new FSFontInfo(file, FontFormat.TTF, ttf.getName(), ros, usWeightClass, sFamilyClass, ulCodePageRange1, ulCodePageRange2, macStyle, panose, this, hash, file.lastModified()));
                }
                if (LOG.isTraceEnabled() && (name = ttf.getNaming()) != null) {
                    LOG.trace((Object)(format + ": '" + name.getPostScriptName() + "' / '" + name.getFontFamily() + "' / '" + name.getFontSubFamily() + "'"));
                }
            } else {
                this.fontInfoList.add(this.createFSIgnored(file, FontFormat.TTF, "*skipnoname*"));
                LOG.warn((Object)("Missing 'name' entry for PostScript name in font " + file));
            }
        }
        catch (IOException e) {
            this.fontInfoList.add(this.createFSIgnored(file, FontFormat.TTF, "*skipexception*"));
            LOG.warn((Object)("Could not load font file: " + file), (Throwable)e);
        }
        finally {
            ttf.close();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void addType1Font(File pfbFile) throws IOException {
        FileInputStream input = new FileInputStream(pfbFile);
        try {
            Type1Font type1 = Type1Font.createWithPFB((InputStream)input);
            if (type1.getName() == null) {
                this.fontInfoList.add(this.createFSIgnored(pfbFile, FontFormat.PFB, "*skipnoname*"));
                LOG.warn((Object)("Missing 'name' entry for PostScript name in font " + pfbFile));
                return;
            }
            if (type1.getName().contains("|")) {
                this.fontInfoList.add(this.createFSIgnored(pfbFile, FontFormat.PFB, "*skippipeinname*"));
                LOG.warn((Object)("Skipping font with '|' in name " + type1.getName() + " in file " + pfbFile));
                return;
            }
            String hash = FileSystemFontProvider.computeHash(FileSystemFontProvider.readAllBytes(pfbFile));
            this.fontInfoList.add(new FSFontInfo(pfbFile, FontFormat.PFB, type1.getName(), null, -1, -1, 0, 0, -1, null, this, hash, pfbFile.lastModified()));
            if (LOG.isTraceEnabled()) {
                LOG.trace((Object)("PFB: '" + type1.getName() + "' / '" + type1.getFamilyName() + "' / '" + type1.getWeight() + "'"));
            }
        }
        catch (IOException e) {
            LOG.warn((Object)("Could not load font file: " + pfbFile), (Throwable)e);
        }
        finally {
            ((InputStream)input).close();
        }
    }

    @Override
    public String toDebugString() {
        StringBuilder sb = new StringBuilder();
        for (FSFontInfo info : this.fontInfoList) {
            sb.append((Object)info.getFormat());
            sb.append(": ");
            sb.append(info.getPostScriptName());
            sb.append(": ");
            sb.append(info.file.getPath());
            sb.append('\n');
        }
        return sb.toString();
    }

    @Override
    public List<? extends FontInfo> getFontInfo() {
        return this.fontInfoList;
    }

    private static byte[] readAllBytes(File file) throws IOException {
        byte[] byArray;
        FileInputStream is = null;
        try {
            is = new FileInputStream(file);
            byArray = IOUtils.toByteArray(is);
        }
        catch (Throwable throwable) {
            IOUtils.closeQuietly(is);
            throw throwable;
        }
        IOUtils.closeQuietly(is);
        return byArray;
    }

    private static String computeHash(byte[] ba) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA512");
            byte[] md5 = md.digest(ba);
            return Hex.getString(md5);
        }
        catch (NoSuchAlgorithmException ex) {
            return "";
        }
    }

    private static class FSFontInfo
    extends FontInfo {
        private final String postScriptName;
        private final FontFormat format;
        private final CIDSystemInfo cidSystemInfo;
        private final int usWeightClass;
        private final int sFamilyClass;
        private final int ulCodePageRange1;
        private final int ulCodePageRange2;
        private final int macStyle;
        private final PDPanoseClassification panose;
        private final File file;
        private final FileSystemFontProvider parent;
        private final String hash;
        private final long lastModified;

        private FSFontInfo(File file, FontFormat format, String postScriptName, CIDSystemInfo cidSystemInfo, int usWeightClass, int sFamilyClass, int ulCodePageRange1, int ulCodePageRange2, int macStyle, byte[] panose, FileSystemFontProvider parent, String hash, long lastModified) {
            this.file = file;
            this.format = format;
            this.postScriptName = postScriptName;
            this.cidSystemInfo = cidSystemInfo;
            this.usWeightClass = usWeightClass;
            this.sFamilyClass = sFamilyClass;
            this.ulCodePageRange1 = ulCodePageRange1;
            this.ulCodePageRange2 = ulCodePageRange2;
            this.macStyle = macStyle;
            this.panose = panose != null && panose.length >= 10 ? new PDPanoseClassification(panose) : null;
            this.parent = parent;
            this.hash = hash;
            this.lastModified = lastModified;
        }

        @Override
        public String getPostScriptName() {
            return this.postScriptName;
        }

        @Override
        public FontFormat getFormat() {
            return this.format;
        }

        @Override
        public CIDSystemInfo getCIDSystemInfo() {
            return this.cidSystemInfo;
        }

        @Override
        public synchronized FontBoxFont getFont() {
            Type1Font font;
            FontBoxFont cached = this.parent.cache.getFont(this);
            if (cached != null) {
                return cached;
            }
            switch (this.format) {
                case PFB: {
                    font = this.getType1Font(this.postScriptName, this.file);
                    break;
                }
                case TTF: {
                    font = this.getTrueTypeFont(this.postScriptName, this.file);
                    break;
                }
                case OTF: {
                    font = this.getOTFFont(this.postScriptName, this.file);
                    break;
                }
                default: {
                    throw new RuntimeException("can't happen");
                }
            }
            if (font != null) {
                this.parent.cache.addFont(this, (FontBoxFont)font);
            }
            return font;
        }

        @Override
        public int getFamilyClass() {
            return this.sFamilyClass;
        }

        @Override
        public int getWeightClass() {
            return this.usWeightClass;
        }

        @Override
        public int getCodePageRange1() {
            return this.ulCodePageRange1;
        }

        @Override
        public int getCodePageRange2() {
            return this.ulCodePageRange2;
        }

        @Override
        public int getMacStyle() {
            return this.macStyle;
        }

        @Override
        public PDPanoseClassification getPanose() {
            return this.panose;
        }

        @Override
        public String toString() {
            return super.toString() + " " + this.file + " " + this.hash + " " + this.lastModified;
        }

        private TrueTypeFont getTrueTypeFont(String postScriptName, File file) {
            try {
                TrueTypeFont ttf = this.readTrueTypeFont(postScriptName, file);
                if (LOG.isDebugEnabled()) {
                    LOG.debug((Object)("Loaded " + postScriptName + " from " + file));
                }
                return ttf;
            }
            catch (IOException e) {
                LOG.warn((Object)("Could not load font file: " + file), (Throwable)e);
                return null;
            }
        }

        private TrueTypeFont readTrueTypeFont(String postScriptName, File file) throws IOException {
            if (file.getName().toLowerCase().endsWith(".ttc")) {
                TrueTypeFont ttf;
                TrueTypeCollection ttc = new TrueTypeCollection(file);
                try {
                    ttf = ttc.getFontByName(postScriptName);
                }
                catch (IOException ex) {
                    ttc.close();
                    throw ex;
                }
                if (ttf == null) {
                    ttc.close();
                    throw new IOException("Font " + postScriptName + " not found in " + file);
                }
                return ttf;
            }
            TTFParser ttfParser = new TTFParser(false, true);
            return ttfParser.parse(file);
        }

        private OpenTypeFont getOTFFont(String postScriptName, File file) {
            try {
                if (file.getName().toLowerCase().endsWith(".ttc")) {
                    TrueTypeFont ttf;
                    TrueTypeCollection ttc = new TrueTypeCollection(file);
                    try {
                        ttf = ttc.getFontByName(postScriptName);
                    }
                    catch (IOException ex) {
                        LOG.error((Object)ex.getMessage(), (Throwable)ex);
                        ttc.close();
                        return null;
                    }
                    if (ttf == null) {
                        ttc.close();
                        throw new IOException("Font " + postScriptName + " not found in " + file);
                    }
                    return (OpenTypeFont)ttf;
                }
                OTFParser parser = new OTFParser(false, true);
                OpenTypeFont otf = parser.parse(file);
                if (LOG.isDebugEnabled()) {
                    LOG.debug((Object)("Loaded " + postScriptName + " from " + file));
                }
                return otf;
            }
            catch (IOException e) {
                LOG.warn((Object)("Could not load font file: " + file), (Throwable)e);
                return null;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        private Type1Font getType1Font(String postScriptName, File file) {
            Type1Font type1Font;
            FileInputStream input = null;
            try {
                input = new FileInputStream(file);
                Type1Font type1 = Type1Font.createWithPFB((InputStream)input);
                if (LOG.isDebugEnabled()) {
                    LOG.debug((Object)("Loaded " + postScriptName + " from " + file));
                }
                type1Font = type1;
            }
            catch (IOException e) {
                try {
                    LOG.warn((Object)("Could not load font file: " + file), (Throwable)e);
                }
                catch (Throwable throwable) {
                    IOUtils.closeQuietly(input);
                    throw throwable;
                }
                IOUtils.closeQuietly(input);
                return null;
            }
            IOUtils.closeQuietly(input);
            return type1Font;
        }
    }
}

