/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xslf.util;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Dimension2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.sl.draw.Drawable;
import org.apache.poi.sl.draw.EmbeddedExtractor;
import org.apache.poi.util.Dimension2DDouble;
import org.apache.poi.util.GenericRecordJsonWriter;
import org.apache.poi.util.LocaleUtil;
import org.apache.poi.xslf.util.BitmapFormat;
import org.apache.poi.xslf.util.DummyFormat;
import org.apache.poi.xslf.util.EMFHandler;
import org.apache.poi.xslf.util.MFProxy;
import org.apache.poi.xslf.util.OutputFormat;
import org.apache.poi.xslf.util.PDFFormat;
import org.apache.poi.xslf.util.PPTHandler;
import org.apache.poi.xslf.util.SVGFormat;
import org.apache.poi.xslf.util.WMFHandler;

public final class PPTX2PNG {
    private static final Logger LOG = LogManager.getLogger(PPTX2PNG.class);
    private static final String INPUT_PAT_REGEX = "(?<slideno>[^|]+)\\|(?<format>[^|]+)\\|(?<basename>.+)\\.(?<ext>[^.]++)";
    private static final Pattern INPUT_PATTERN = Pattern.compile("(?<slideno>[^|]+)\\|(?<format>[^|]+)\\|(?<basename>.+)\\.(?<ext>[^.]++)");
    private static final String OUTPUT_PAT_REGEX = "${basename}-${slideno}.${format}";
    private String slidenumStr = "-1";
    private float scale = 1.0f;
    private File file = null;
    private String format = "png";
    private File outdir = null;
    private String outfile = null;
    private boolean quiet = false;
    private String outPattern = "${basename}-${slideno}.${format}";
    private File dumpfile = null;
    private String fixSide = "scale";
    private boolean ignoreParse = false;
    private boolean extractEmbedded = false;
    private FileMagic defaultFileType = FileMagic.OLE2;
    private boolean textAsShapes = false;
    private Charset charset = LocaleUtil.CHARSET_1252;
    private boolean emfHeaderBounds = false;
    private String fontDir = null;
    private String fontTtf = null;
    private String fontMap = null;

    private static void usage(String error) {
        String msg = "Usage: PPTX2PNG [options] <.ppt/.pptx/.emf/.wmf file or 'stdin'>\n" + (error == null ? "" : "Error: " + error + "\n") + "Options:\n    -scale <float>    scale factor\n    -fixSide <side>   specify side (long,short,width,height) to fix - use <scale> as amount of pixels\n    -slide <integer>  1-based index of a slide to render\n    -format <type>    png,gif,jpg,svg,pdf (,log,null for testing)\n    -outdir <dir>     output directory, defaults to origin of the ppt/pptx file\n    -outfile <file>   output filename, defaults to '" + OUTPUT_PAT_REGEX + "'\n    -outpat <pattern> output filename pattern, defaults to '" + OUTPUT_PAT_REGEX + "'\n                      patterns: basename, slideno, format, ext\n    -dump <file>      dump the annotated records to a file\n    -quiet            do not write to console (for normal processing)\n    -ignoreParse      ignore parsing error and continue with the records read until the error\n    -extractEmbedded  extract embedded parts\n    -inputType <type> default input file type (OLE2,WMF,EMF), default is OLE2 = Powerpoint\n                      some files (usually wmf) don't have a header, i.e. an identifiable file magic\n    -textAsShapes     text elements are saved as shapes in SVG, necessary for variable spacing\n                      often found in math formulas\n    -charset <cs>     sets the default charset to be used, defaults to Windows-1252\n    -emfHeaderBounds  force the usage of the emf header bounds to calculate the bounding box\n    -fontdir <dir>    (PDF only) font directories separated by \";\" - use $HOME for current users home dir\n                      defaults to the usual plattform directories\n    -fontTtf <regex>  (PDF only) regex to match the .ttf filenames\n    -fontMap <map>    \";\"-separated list of font mappings <typeface from>:<typeface to>";
        System.out.println(msg);
    }

    public static void main(String[] args) throws Exception {
        PPTX2PNG p2p = new PPTX2PNG();
        if (p2p.parseCommandLine(args)) {
            p2p.processFile();
        }
    }

    private PPTX2PNG() {
    }

    private boolean parseCommandLine(String[] args) {
        boolean isStdin;
        if (args.length == 0) {
            PPTX2PNG.usage(null);
            return false;
        }
        block40: for (int i = 0; i < args.length; ++i) {
            String opt = i + 1 < args.length ? args[i + 1] : null;
            switch (args[i].toLowerCase(Locale.ROOT)) {
                case "-scale": {
                    if (opt == null) continue block40;
                    this.scale = Float.parseFloat(opt);
                    ++i;
                    continue block40;
                }
                case "-slide": {
                    this.slidenumStr = opt;
                    ++i;
                    continue block40;
                }
                case "-format": {
                    this.format = opt;
                    ++i;
                    continue block40;
                }
                case "-outdir": {
                    if (opt == null) continue block40;
                    this.outdir = new File(opt);
                    ++i;
                    continue block40;
                }
                case "-outfile": {
                    this.outfile = opt;
                    ++i;
                    continue block40;
                }
                case "-outpat": {
                    this.outPattern = opt;
                    ++i;
                    continue block40;
                }
                case "-quiet": {
                    this.quiet = true;
                    continue block40;
                }
                case "-dump": {
                    if (opt != null) {
                        this.dumpfile = new File(opt);
                        ++i;
                        continue block40;
                    }
                    this.dumpfile = new File("pptx2png.dump");
                    continue block40;
                }
                case "-fixside": {
                    if (opt != null) {
                        this.fixSide = opt.toLowerCase(Locale.ROOT);
                        ++i;
                        continue block40;
                    }
                    this.fixSide = "long";
                    continue block40;
                }
                case "-inputtype": {
                    if (opt != null) {
                        this.defaultFileType = FileMagic.valueOf(opt);
                        ++i;
                        continue block40;
                    }
                    this.defaultFileType = FileMagic.OLE2;
                    continue block40;
                }
                case "-textasshapes": {
                    this.textAsShapes = true;
                    continue block40;
                }
                case "-ignoreparse": {
                    this.ignoreParse = true;
                    continue block40;
                }
                case "-extractembedded": {
                    this.extractEmbedded = true;
                    continue block40;
                }
                case "-charset": {
                    if (opt != null) {
                        this.charset = Charset.forName(opt);
                        ++i;
                        continue block40;
                    }
                    this.charset = LocaleUtil.CHARSET_1252;
                    continue block40;
                }
                case "-emfheaderbounds": {
                    this.emfHeaderBounds = true;
                    continue block40;
                }
                case "-fontdir": {
                    if (opt != null) {
                        this.fontDir = opt;
                        ++i;
                        continue block40;
                    }
                    this.fontDir = null;
                    continue block40;
                }
                case "-fontttf": {
                    if (opt != null) {
                        this.fontTtf = opt;
                        ++i;
                        continue block40;
                    }
                    this.fontTtf = null;
                    continue block40;
                }
                case "-fontmap": {
                    if (opt != null) {
                        this.fontMap = opt;
                        ++i;
                        continue block40;
                    }
                    this.fontMap = null;
                    continue block40;
                }
                default: {
                    this.file = new File(args[i]);
                }
            }
        }
        boolean bl = isStdin = this.file != null && "stdin".equalsIgnoreCase(this.file.getName());
        if (!(isStdin || this.file != null && this.file.exists())) {
            PPTX2PNG.usage("File not specified or it doesn't exist");
            return false;
        }
        if (this.format == null || !this.format.matches("^(png|gif|jpg|null|svg|pdf|log)$")) {
            PPTX2PNG.usage("Invalid format given");
            return false;
        }
        if (this.outdir == null) {
            if (isStdin) {
                PPTX2PNG.usage("When reading from STDIN, you need to specify an outdir.");
                return false;
            }
            this.outdir = this.file.getAbsoluteFile().getParentFile();
        }
        if (!this.outdir.exists()) {
            PPTX2PNG.usage("Outdir doesn't exist");
            return false;
        }
        if (!("null".equals(this.format) || this.outdir != null && this.outdir.exists() && this.outdir.isDirectory())) {
            PPTX2PNG.usage("Output directory doesn't exist");
            return false;
        }
        if (this.scale < 0.0f) {
            PPTX2PNG.usage("Invalid scale given");
            return false;
        }
        if (!"long,short,width,height,scale".contains(this.fixSide)) {
            PPTX2PNG.usage("<fixside> must be one of long / short / width / height / scale");
            return false;
        }
        return true;
    }

    private void processFile() throws IOException {
        if (!this.quiet) {
            System.out.println("Processing " + this.file);
        }
        try (MFProxy proxy = this.initProxy(this.file);){
            Set<Integer> slidenum = proxy.slideIndexes(this.slidenumStr);
            if (slidenum.isEmpty()) {
                PPTX2PNG.usage("slidenum must be either -1 (for all) or within range: [1.." + proxy.getSlideCount() + "] for " + this.file);
                return;
            }
            Dimension2DDouble dim = new Dimension2DDouble();
            double lenSide = this.getDimensions(proxy, dim);
            int width = Math.max((int)Math.rint(((Dimension2D)dim).getWidth()), 1);
            int height = Math.max((int)Math.rint(((Dimension2D)dim).getHeight()), 1);
            try (OutputFormat outputFormat = this.getOutput();){
                for (int slideNo : slidenum) {
                    proxy.setSlideNo(slideNo);
                    if (!this.quiet) {
                        String title = proxy.getTitle();
                        System.out.println("Rendering slide " + slideNo + (title == null ? "" : ": " + title.trim()));
                    }
                    this.dumpRecords(proxy);
                    this.extractEmbedded(proxy, slideNo);
                    Graphics2D graphics = outputFormat.addSlide(width, height);
                    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
                    graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                    graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
                    graphics.setRenderingHint(Drawable.DEFAULT_CHARSET, this.getDefaultCharset());
                    graphics.setRenderingHint(Drawable.EMF_FORCE_HEADER_BOUNDS, this.emfHeaderBounds);
                    if (this.fontMap != null) {
                        Map<String, String> fmap = Arrays.stream(this.fontMap.split(";")).map(s -> s.split(":")).collect(Collectors.toMap(s -> s[0], s -> s[1]));
                        graphics.setRenderingHint(Drawable.FONT_MAP, fmap);
                    }
                    graphics.scale((double)this.scale / lenSide, (double)this.scale / lenSide);
                    graphics.setComposite(AlphaComposite.Clear);
                    graphics.fillRect(0, 0, width, height);
                    graphics.setComposite(AlphaComposite.SrcOver);
                    proxy.draw(graphics);
                    outputFormat.writeSlide(proxy, new File(this.outdir, this.calcOutFile(proxy, slideNo)));
                }
                outputFormat.writeDocument(proxy, new File(this.outdir, this.calcOutFile(proxy, 0)));
            }
        }
        catch (NoScratchpadException e) {
            PPTX2PNG.usage("'" + this.file.getName() + "': Format not supported - try to include poi-scratchpad.jar into the CLASSPATH.");
            return;
        }
        if (!this.quiet) {
            System.out.println("Done");
        }
    }

    private OutputFormat getOutput() {
        switch (this.format) {
            case "svg": {
                try {
                    return new SVGFormat(this.textAsShapes);
                }
                catch (Exception | NoClassDefFoundError e) {
                    LOG.atError().withThrowable(e).log("Batik is not not added to/working on the module-path. Use classpath mode instead of JPMS. Fallback to PNG.");
                    return new BitmapFormat("png");
                }
            }
            case "pdf": {
                return new PDFFormat(this.textAsShapes, this.fontDir, this.fontTtf);
            }
            case "log": {
                return new DummyFormat();
            }
        }
        return new BitmapFormat(this.format);
    }

    private double getDimensions(MFProxy proxy, Dimension2D dim) {
        double lenSide;
        Dimension2D pgsize = proxy.getSize();
        switch (this.fixSide) {
            default: {
                lenSide = 1.0;
                break;
            }
            case "long": {
                lenSide = Math.max(pgsize.getWidth(), pgsize.getHeight());
                break;
            }
            case "short": {
                lenSide = Math.min(pgsize.getWidth(), pgsize.getHeight());
                break;
            }
            case "width": {
                lenSide = pgsize.getWidth();
                break;
            }
            case "height": {
                lenSide = pgsize.getHeight();
            }
        }
        dim.setSize(pgsize.getWidth() * (double)this.scale / lenSide, pgsize.getHeight() * (double)this.scale / lenSide);
        return lenSide;
    }

    private void dumpRecords(MFProxy proxy) throws IOException {
        if (this.dumpfile == null || "null".equals(this.dumpfile.getPath())) {
            return;
        }
        GenericRecord gr = proxy.getRoot();
        try (GenericRecordJsonWriter fw = new GenericRecordJsonWriter(this.dumpfile){

            @Override
            protected boolean printBytes(String name, Object o) {
                return false;
            }
        };){
            if (gr == null) {
                fw.writeError(this.file.getName() + " doesn't support GenericRecord interface and can't be dumped to a file.");
            } else {
                fw.write(gr);
            }
        }
    }

    private void extractEmbedded(MFProxy proxy, int slideNo) throws IOException {
        if (!this.extractEmbedded) {
            return;
        }
        for (EmbeddedExtractor.EmbeddedPart ep : proxy.getEmbeddings(slideNo)) {
            String filename = ep.getName();
            filename = new File(filename == null ? "dummy.dat" : filename).getName();
            filename = this.calcOutFile(proxy, slideNo).replaceFirst("\\.\\w+$", "") + "_" + filename;
            FileOutputStream fos = new FileOutputStream(new File(this.outdir, filename));
            Throwable throwable = null;
            try {
                fos.write(ep.getData().get());
            }
            catch (Throwable throwable2) {
                throwable = throwable2;
                throw throwable2;
            }
            finally {
                if (fos == null) continue;
                if (throwable != null) {
                    try {
                        fos.close();
                    }
                    catch (Throwable throwable3) {
                        throwable.addSuppressed(throwable3);
                    }
                    continue;
                }
                fos.close();
            }
        }
    }

    private MFProxy initProxy(File file) throws IOException {
        MFProxy proxy;
        ProxyConsumer con;
        FileMagic fm;
        String fileName = file.getName().toLowerCase(Locale.ROOT);
        if ("stdin".equals(fileName)) {
            InputStream bis = FileMagic.prepareToCheckMagic(System.in);
            fm = FileMagic.valueOf(bis);
            con = p -> p.parse(bis);
        } else {
            fm = FileMagic.valueOf(file);
            con = p -> p.parse(file);
        }
        if (fm == FileMagic.UNKNOWN) {
            fm = this.defaultFileType;
        }
        switch (fm) {
            case EMF: {
                proxy = new EMFHandler();
                break;
            }
            case WMF: {
                proxy = new WMFHandler();
                break;
            }
            default: {
                proxy = new PPTHandler();
            }
        }
        proxy.setIgnoreParse(this.ignoreParse);
        proxy.setQuiet(this.quiet);
        con.parse(proxy);
        proxy.setDefaultCharset(this.charset);
        return proxy;
    }

    private String calcOutFile(MFProxy proxy, int slideNo) {
        if (this.outfile != null) {
            return this.outfile;
        }
        String fileName = this.file.getName();
        if ("stdin".equals(fileName)) {
            fileName = fileName + ".ext";
        }
        String inname = String.format(Locale.ROOT, "%04d|%s|%s", slideNo, this.format, fileName);
        String outpat = proxy.getSlideCount() > 1 && slideNo > 0 ? this.outPattern : this.outPattern.replaceAll("-?\\$\\{slideno}", "");
        return INPUT_PATTERN.matcher(inname).replaceAll(outpat);
    }

    private Charset getDefaultCharset() {
        return this.charset;
    }

    static class NoScratchpadException
    extends IOException {
        NoScratchpadException() {
        }

        NoScratchpadException(Throwable cause) {
            super(cause);
        }
    }

    private static interface ProxyConsumer {
        public void parse(MFProxy var1) throws IOException;
    }
}

