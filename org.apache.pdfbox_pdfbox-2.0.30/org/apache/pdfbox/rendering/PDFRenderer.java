/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.rendering;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.blend.BlendMode;
import org.apache.pdfbox.pdmodel.graphics.optionalcontent.PDOptionalContentGroup;
import org.apache.pdfbox.pdmodel.graphics.optionalcontent.PDOptionalContentProperties;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.pdmodel.interactive.annotation.AnnotationFilter;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;
import org.apache.pdfbox.rendering.RenderDestination;

public class PDFRenderer {
    private static final Log LOG = LogFactory.getLog(PDFRenderer.class);
    protected final PDDocument document;
    private AnnotationFilter annotationFilter = new AnnotationFilter(){

        @Override
        public boolean accept(PDAnnotation annotation) {
            return true;
        }
    };
    private boolean subsamplingAllowed = false;
    private RenderDestination defaultDestination;
    private RenderingHints renderingHints = null;
    private BufferedImage pageImage;
    private static boolean kcmsLogged = false;
    private float imageDownscalingOptimizationThreshold = 0.5f;
    private final PDPageTree pageTree;

    public PDFRenderer(PDDocument document) {
        this.document = document;
        this.pageTree = document.getPages();
        if (!kcmsLogged) {
            PDFRenderer.suggestKCMS();
            kcmsLogged = true;
        }
    }

    public AnnotationFilter getAnnotationsFilter() {
        return this.annotationFilter;
    }

    public void setAnnotationsFilter(AnnotationFilter annotationsFilter) {
        this.annotationFilter = annotationsFilter;
    }

    public boolean isSubsamplingAllowed() {
        return this.subsamplingAllowed;
    }

    public void setSubsamplingAllowed(boolean subsamplingAllowed) {
        this.subsamplingAllowed = subsamplingAllowed;
    }

    public RenderDestination getDefaultDestination() {
        return this.defaultDestination;
    }

    public void setDefaultDestination(RenderDestination defaultDestination) {
        this.defaultDestination = defaultDestination;
    }

    public RenderingHints getRenderingHints() {
        return this.renderingHints;
    }

    public void setRenderingHints(RenderingHints renderingHints) {
        this.renderingHints = renderingHints;
    }

    public float getImageDownscalingOptimizationThreshold() {
        return this.imageDownscalingOptimizationThreshold;
    }

    public void setImageDownscalingOptimizationThreshold(float imageDownscalingOptimizationThreshold) {
        this.imageDownscalingOptimizationThreshold = imageDownscalingOptimizationThreshold;
    }

    public BufferedImage renderImage(int pageIndex) throws IOException {
        return this.renderImage(pageIndex, 1.0f);
    }

    public BufferedImage renderImage(int pageIndex, float scale) throws IOException {
        return this.renderImage(pageIndex, scale, ImageType.RGB);
    }

    public BufferedImage renderImageWithDPI(int pageIndex, float dpi) throws IOException {
        return this.renderImage(pageIndex, dpi / 72.0f, ImageType.RGB);
    }

    public BufferedImage renderImageWithDPI(int pageIndex, float dpi, ImageType imageType) throws IOException {
        return this.renderImage(pageIndex, dpi / 72.0f, imageType);
    }

    public BufferedImage renderImage(int pageIndex, float scale, ImageType imageType) throws IOException {
        return this.renderImage(pageIndex, scale, imageType, this.defaultDestination == null ? RenderDestination.EXPORT : this.defaultDestination);
    }

    public BufferedImage renderImage(int pageIndex, float scale, ImageType imageType, RenderDestination destination) throws IOException {
        int heightPx;
        PDPage page = this.pageTree.get(pageIndex);
        PDRectangle cropBox = page.getCropBox();
        float widthPt = cropBox.getWidth();
        float heightPt = cropBox.getHeight();
        int widthPx = (int)Math.max(Math.floor(widthPt * scale), 1.0);
        if ((long)widthPx * (long)(heightPx = (int)Math.max(Math.floor(heightPt * scale), 1.0)) > Integer.MAX_VALUE) {
            throw new IOException("Maximum size of image exceeded (w * h * scale ^ 2) = " + widthPt + " * " + heightPt + " * " + scale + " ^ 2 > " + Integer.MAX_VALUE);
        }
        int rotationAngle = page.getRotation();
        int bimType = imageType != ImageType.ARGB && this.hasBlendMode(page) ? 2 : imageType.toBufferedImageType();
        BufferedImage image = rotationAngle == 90 || rotationAngle == 270 ? new BufferedImage(heightPx, widthPx, bimType) : new BufferedImage(widthPx, heightPx, bimType);
        this.pageImage = image;
        Graphics2D g = image.createGraphics();
        if (image.getType() == 2) {
            g.setBackground(new Color(0, 0, 0, 0));
        } else {
            g.setBackground(Color.WHITE);
        }
        g.clearRect(0, 0, image.getWidth(), image.getHeight());
        this.transform(g, page.getRotation(), cropBox, scale, scale);
        RenderingHints actualRenderingHints = this.renderingHints == null ? this.createDefaultRenderingHints(g) : this.renderingHints;
        PageDrawerParameters parameters = new PageDrawerParameters(this, page, this.subsamplingAllowed, destination, actualRenderingHints, this.imageDownscalingOptimizationThreshold);
        PageDrawer drawer = this.createPageDrawer(parameters);
        drawer.drawPage(g, cropBox);
        g.dispose();
        if (image.getType() != imageType.toBufferedImageType()) {
            BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), imageType.toBufferedImageType());
            Graphics2D dstGraphics = newImage.createGraphics();
            dstGraphics.setBackground(Color.WHITE);
            dstGraphics.clearRect(0, 0, image.getWidth(), image.getHeight());
            dstGraphics.drawImage((Image)image, 0, 0, null);
            dstGraphics.dispose();
            image = newImage;
        }
        return image;
    }

    public void renderPageToGraphics(int pageIndex, Graphics2D graphics) throws IOException {
        this.renderPageToGraphics(pageIndex, graphics, 1.0f);
    }

    public void renderPageToGraphics(int pageIndex, Graphics2D graphics, float scale) throws IOException {
        this.renderPageToGraphics(pageIndex, graphics, scale, scale);
    }

    public void renderPageToGraphics(int pageIndex, Graphics2D graphics, float scaleX, float scaleY) throws IOException {
        this.renderPageToGraphics(pageIndex, graphics, scaleX, scaleY, this.defaultDestination == null ? RenderDestination.VIEW : this.defaultDestination);
    }

    public void renderPageToGraphics(int pageIndex, Graphics2D graphics, float scaleX, float scaleY, RenderDestination destination) throws IOException {
        PDPage page = this.pageTree.get(pageIndex);
        PDRectangle cropBox = page.getCropBox();
        this.transform(graphics, page.getRotation(), cropBox, scaleX, scaleY);
        graphics.clearRect(0, 0, (int)cropBox.getWidth(), (int)cropBox.getHeight());
        RenderingHints actualRenderingHints = this.renderingHints == null ? this.createDefaultRenderingHints(graphics) : this.renderingHints;
        PageDrawerParameters parameters = new PageDrawerParameters(this, page, this.subsamplingAllowed, destination, actualRenderingHints, this.imageDownscalingOptimizationThreshold);
        PageDrawer drawer = this.createPageDrawer(parameters);
        drawer.drawPage(graphics, cropBox);
    }

    public boolean isGroupEnabled(PDOptionalContentGroup group) {
        PDOptionalContentProperties ocProperties = this.document.getDocumentCatalog().getOCProperties();
        return ocProperties == null || ocProperties.isGroupEnabled(group);
    }

    private void transform(Graphics2D graphics, int rotationAngle, PDRectangle cropBox, float scaleX, float scaleY) {
        graphics.scale(scaleX, scaleY);
        if (rotationAngle != 0) {
            float translateX = 0.0f;
            float translateY = 0.0f;
            switch (rotationAngle) {
                case 90: {
                    translateX = cropBox.getHeight();
                    break;
                }
                case 270: {
                    translateY = cropBox.getWidth();
                    break;
                }
                case 180: {
                    translateX = cropBox.getWidth();
                    translateY = cropBox.getHeight();
                    break;
                }
            }
            graphics.translate(translateX, translateY);
            graphics.rotate(Math.toRadians(rotationAngle));
        }
    }

    private boolean isBitonal(Graphics2D graphics) {
        GraphicsConfiguration deviceConfiguration = graphics.getDeviceConfiguration();
        if (deviceConfiguration == null) {
            return false;
        }
        GraphicsDevice device = deviceConfiguration.getDevice();
        if (device == null) {
            return false;
        }
        DisplayMode displayMode = device.getDisplayMode();
        if (displayMode == null) {
            return false;
        }
        return displayMode.getBitDepth() == 1;
    }

    private RenderingHints createDefaultRenderingHints(Graphics2D graphics) {
        boolean isBitonal = this.isBitonal(graphics);
        RenderingHints r = new RenderingHints(null);
        r.put(RenderingHints.KEY_INTERPOLATION, isBitonal ? RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR : RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        r.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        r.put(RenderingHints.KEY_ANTIALIASING, isBitonal ? RenderingHints.VALUE_ANTIALIAS_OFF : RenderingHints.VALUE_ANTIALIAS_ON);
        return r;
    }

    protected PageDrawer createPageDrawer(PageDrawerParameters parameters) throws IOException {
        PageDrawer pageDrawer = new PageDrawer(parameters);
        pageDrawer.setAnnotationFilter(this.annotationFilter);
        return pageDrawer;
    }

    private boolean hasBlendMode(PDPage page) {
        PDResources resources = page.getResources();
        if (resources == null) {
            return false;
        }
        for (COSName name : resources.getExtGStateNames()) {
            BlendMode blendMode;
            PDExtendedGraphicsState extGState = resources.getExtGState(name);
            if (extGState == null || (blendMode = extGState.getBlendMode()) == BlendMode.NORMAL) continue;
            return true;
        }
        return false;
    }

    BufferedImage getPageImage() {
        return this.pageImage;
    }

    private static void suggestKCMS() {
        String cmmProperty = System.getProperty("sun.java2d.cmm");
        if (PDFRenderer.isMinJdk8() && !"sun.java2d.cmm.kcms.KcmsServiceProvider".equals(cmmProperty)) {
            try {
                Class.forName("sun.java2d.cmm.kcms.KcmsServiceProvider");
                String version = System.getProperty("java.version");
                if (version == null || PDFRenderer.isGoodVersion(version, "1.8.0_(\\d+)", 191) || PDFRenderer.isGoodVersion(version, "9.0.(\\d+)", 4)) {
                    return;
                }
                LOG.info((Object)("Your current java version is: " + version));
                LOG.info((Object)"To get higher rendering speed on old java 1.8 or 9 versions,");
                LOG.info((Object)"  update to the latest 1.8 or 9 version (>= 1.8.0_191 or >= 9.0.4),");
                LOG.info((Object)"  or");
                LOG.info((Object)"  use the option -Dsun.java2d.cmm=sun.java2d.cmm.kcms.KcmsServiceProvider");
                LOG.info((Object)"  or call System.setProperty(\"sun.java2d.cmm\", \"sun.java2d.cmm.kcms.KcmsServiceProvider\")");
            }
            catch (ClassNotFoundException classNotFoundException) {
                // empty catch block
            }
        }
    }

    private static boolean isGoodVersion(String version, String regex, int min) {
        Matcher matcher = Pattern.compile(regex).matcher(version);
        if (matcher.matches() && matcher.groupCount() >= 1) {
            try {
                int v = Integer.parseInt(matcher.group(1));
                if (v >= min) {
                    return true;
                }
            }
            catch (NumberFormatException ex) {
                return true;
            }
        }
        return false;
    }

    private static boolean isMinJdk8() {
        String version = System.getProperty("java.specification.version");
        StringTokenizer st = new StringTokenizer(version, ".");
        try {
            int major = Integer.parseInt(st.nextToken());
            int minor = 0;
            if (st.hasMoreTokens()) {
                minor = Integer.parseInt(st.nextToken());
            }
            return major > 1 || major == 1 && minor >= 8;
        }
        catch (NumberFormatException nfe) {
            return true;
        }
    }
}

