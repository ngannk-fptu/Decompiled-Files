/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream
 */
package org.apache.poi.poifs.crypt.dsig;

import com.microsoft.schemas.office.office.CTSignatureLine;
import com.microsoft.schemas.vml.CTGroup;
import com.microsoft.schemas.vml.CTImageData;
import com.microsoft.schemas.vml.CTShape;
import com.microsoft.schemas.vml.STExt;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.UUID;
import javax.imageio.ImageIO;
import javax.xml.namespace.QName;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.poi.common.usermodel.PictureType;
import org.apache.poi.hpsf.ClassID;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.crypt.dsig.SignatureConfig;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.sl.draw.DrawPictureShape;
import org.apache.poi.sl.draw.ImageRenderer;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STTrueFalse;

public abstract class SignatureLine {
    private static final String MS_OFFICE_URN = "urn:schemas-microsoft-com:office:office";
    protected static final QName QNAME_SIGNATURE_LINE = new QName("urn:schemas-microsoft-com:office:office", "signatureline");
    private ClassID setupId;
    private Boolean allowComments;
    private String signingInstructions = "Before signing the document, verify that the content you are signing is correct.";
    private String suggestedSigner;
    private String suggestedSigner2;
    private String suggestedSignerEmail;
    private String caption;
    private String invalidStamp = "invalid";
    private byte[] plainSignature;
    private String contentType;
    private CTShape signatureShape;

    public ClassID getSetupId() {
        return this.setupId;
    }

    public void setSetupId(ClassID setupId) {
        this.setupId = setupId;
    }

    public Boolean getAllowComments() {
        return this.allowComments;
    }

    public void setAllowComments(Boolean allowComments) {
        this.allowComments = allowComments;
    }

    public String getSigningInstructions() {
        return this.signingInstructions;
    }

    public void setSigningInstructions(String signingInstructions) {
        this.signingInstructions = signingInstructions;
    }

    public String getSuggestedSigner() {
        return this.suggestedSigner;
    }

    public void setSuggestedSigner(String suggestedSigner) {
        this.suggestedSigner = suggestedSigner;
    }

    public String getSuggestedSigner2() {
        return this.suggestedSigner2;
    }

    public void setSuggestedSigner2(String suggestedSigner2) {
        this.suggestedSigner2 = suggestedSigner2;
    }

    public String getSuggestedSignerEmail() {
        return this.suggestedSignerEmail;
    }

    public void setSuggestedSignerEmail(String suggestedSignerEmail) {
        this.suggestedSignerEmail = suggestedSignerEmail;
    }

    public String getDefaultCaption() {
        return this.suggestedSigner + "\n" + this.suggestedSigner2 + "\n" + this.suggestedSignerEmail;
    }

    public String getCaption() {
        return this.caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getInvalidStamp() {
        return this.invalidStamp;
    }

    public void setInvalidStamp(String invalidStamp) {
        this.invalidStamp = invalidStamp;
    }

    public byte[] getPlainSignature() {
        return this.plainSignature;
    }

    public void setPlainSignature(byte[] plainSignature) {
        this.plainSignature = plainSignature;
        this.contentType = null;
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public CTShape getSignatureShape() {
        return this.signatureShape;
    }

    public void setSignatureShape(CTShape signatureShape) {
        this.signatureShape = signatureShape;
    }

    public void setSignatureShape(CTSignatureLine signatureLine) {
        try (XmlCursor cur = signatureLine.newCursor();){
            cur.toParent();
            this.signatureShape = (CTShape)cur.getObject();
        }
    }

    public void updateSignatureConfig(SignatureConfig config) throws IOException {
        if (this.plainSignature == null) {
            throw new IllegalStateException("Plain signature not initialized");
        }
        if (this.contentType == null) {
            this.determineContentType();
        }
        byte[] signValid = this.generateImage(true, false);
        byte[] signInvalid = this.generateImage(true, true);
        config.setSignatureImageSetupId(this.getSetupId());
        config.setSignatureImage(this.plainPng());
        config.setSignatureImageValid(signValid);
        config.setSignatureImageInvalid(signInvalid);
    }

    protected void parse() {
        if (this.signatureShape == null) {
            return;
        }
        CTSignatureLine signatureLine = this.signatureShape.getSignaturelineArray(0);
        this.setSetupId(new ClassID(signatureLine.getId()));
        this.setAllowComments(signatureLine.isSetAllowcomments() ? Boolean.valueOf(STTrueFalse.TRUE.equals(signatureLine.getAllowcomments())) : null);
        this.setSuggestedSigner(signatureLine.getSuggestedsigner());
        this.setSuggestedSigner2(signatureLine.getSuggestedsigner2());
        this.setSuggestedSignerEmail(signatureLine.getSuggestedsigneremail());
        try (XmlCursor cur = signatureLine.newCursor();){
            this.setSigningInstructions(cur.getAttributeText(new QName(MS_OFFICE_URN, "signinginstructions")));
        }
    }

    protected abstract void setRelationId(CTImageData var1, String var2);

    protected void add(XmlObject signatureContainer, AddPictureData addPictureData) {
        try {
            byte[] inputImage = this.generateImage(false, false);
            CTGroup grp = CTGroup.Factory.newInstance();
            grp.addNewShape();
            try (XmlCursor contCur = signatureContainer.newCursor();){
                contCur.toEndToken();
                try (XmlCursor otherC = grp.newCursor();){
                    otherC.copyXmlContents(contCur);
                }
                contCur.toPrevSibling();
                this.signatureShape = (CTShape)contCur.getObject();
            }
            this.signatureShape.setAlt("Microsoft Office Signature Line...");
            this.signatureShape.setStyle("width:191.95pt;height:96.05pt");
            this.signatureShape.setType("rect");
            String relationId = addPictureData.addPictureData(inputImage, PictureType.PNG);
            CTImageData imgData = this.signatureShape.addNewImagedata();
            this.setRelationId(imgData, relationId);
            imgData.setTitle("");
            CTSignatureLine xsl = this.signatureShape.addNewSignatureline();
            if (this.suggestedSigner != null) {
                xsl.setSuggestedsigner(this.suggestedSigner);
            }
            if (this.suggestedSigner2 != null) {
                xsl.setSuggestedsigner2(this.suggestedSigner2);
            }
            if (this.suggestedSignerEmail != null) {
                xsl.setSuggestedsigneremail(this.suggestedSignerEmail);
            }
            if (this.setupId == null) {
                this.setupId = new ClassID("{" + UUID.randomUUID() + "}");
            }
            xsl.setId(this.setupId.toString());
            xsl.setAllowcomments(STTrueFalse.T);
            xsl.setIssignatureline(STTrueFalse.T);
            xsl.setProvid("{00000000-0000-0000-0000-000000000000}");
            xsl.setExt(STExt.EDIT);
            xsl.setSigninginstructionsset(STTrueFalse.T);
            try (XmlCursor cur = xsl.newCursor();){
                cur.setAttributeText(new QName(MS_OFFICE_URN, "signinginstructions"), this.signingInstructions);
            }
        }
        catch (IOException | InvalidFormatException e) {
            throw new POIXMLException("Can't generate signature line image", e);
        }
    }

    protected void update() {
    }

    protected byte[] plainPng() throws IOException {
        byte[] plain = this.getPlainSignature();
        PictureType pictureType = PictureType.valueOf(FileMagic.valueOf(plain));
        if (pictureType == PictureType.UNKNOWN) {
            throw new IllegalArgumentException("Unsupported picture format");
        }
        ImageRenderer rnd = DrawPictureShape.getImageRenderer(null, pictureType.contentType);
        if (rnd == null) {
            throw new UnsupportedOperationException((Object)((Object)pictureType) + " can't be rendered - did you provide poi-scratchpad and its dependencies (batik et al.)");
        }
        rnd.loadImage(this.getPlainSignature(), pictureType.contentType);
        Dimension2D dim = rnd.getDimension();
        int defaultWidth = 300;
        int defaultHeight = (int)((double)defaultWidth * dim.getHeight() / dim.getWidth());
        BufferedImage bi = new BufferedImage(defaultWidth, defaultHeight, 2);
        Graphics2D gfx = bi.createGraphics();
        gfx.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        gfx.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gfx.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        rnd.drawImage(gfx, new Rectangle2D.Double(0.0, 0.0, defaultWidth, defaultHeight));
        gfx.dispose();
        UnsynchronizedByteArrayOutputStream bos = new UnsynchronizedByteArrayOutputStream();
        ImageIO.write((RenderedImage)bi, "PNG", (OutputStream)bos);
        return bos.toByteArray();
    }

    protected byte[] generateImage(boolean showSignature, boolean showInvalidStamp) throws IOException {
        BufferedImage bi = new BufferedImage(400, 150, 2);
        Graphics2D gfx = bi.createGraphics();
        gfx.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        gfx.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gfx.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        String markX = "X\n";
        String lineX = new String(new char[500]).replace("\u0000", " ") + "\n";
        String cap = this.getCaption() == null ? this.getDefaultCaption() : this.getCaption();
        String text = markX + lineX + cap.replaceAll("(?m)^", "    ");
        AttributedString as = new AttributedString(text);
        as.addAttribute(TextAttribute.FAMILY, "SansSerif");
        as.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON, markX.length(), text.indexOf(10, markX.length()));
        as.addAttribute(TextAttribute.SIZE, 15, 0, markX.length());
        as.addAttribute(TextAttribute.SIZE, 12, markX.length(), text.length());
        gfx.setColor(Color.BLACK);
        AttributedCharacterIterator chIter = as.getIterator();
        FontRenderContext frc = gfx.getFontRenderContext();
        LineBreakMeasurer measurer = new LineBreakMeasurer(chIter, frc);
        float y = 80.0f;
        float x = 5.0f;
        int lineNr = 0;
        while (measurer.getPosition() < chIter.getEndIndex()) {
            int mpos = measurer.getPosition();
            int limit = text.indexOf(10, mpos);
            limit = limit == -1 ? text.length() : limit + 1;
            TextLayout textLayout = measurer.nextLayout((float)bi.getWidth() - 10.0f, limit, false);
            if (lineNr != 1) {
                y += textLayout.getAscent();
            }
            textLayout.draw(gfx, x, y);
            y += textLayout.getDescent() + textLayout.getLeading();
            ++lineNr;
        }
        if (showSignature && this.plainSignature != null && this.contentType != null) {
            ImageRenderer renderer = DrawPictureShape.getImageRenderer(gfx, this.contentType);
            renderer.loadImage(this.plainSignature, this.contentType);
            double targetX = 10.0;
            double targetY = 100.0;
            double targetWidth = (double)bi.getWidth() - targetX;
            double targetHeight = targetY - 5.0;
            Dimension2D dim = renderer.getDimension();
            double scale = Math.min(targetWidth / dim.getWidth(), targetHeight / dim.getHeight());
            double effWidth = dim.getWidth() * scale;
            double effHeight = dim.getHeight() * scale;
            renderer.drawImage(gfx, new Rectangle2D.Double(targetX + ((double)bi.getWidth() - effWidth) / 2.0, targetY - effHeight, effWidth, effHeight));
        }
        if (showInvalidStamp && this.invalidStamp != null && !this.invalidStamp.isEmpty()) {
            gfx.setFont(new Font("Lucida Bright", 2, 60));
            gfx.rotate(Math.toRadians(-15.0), (double)bi.getWidth() / 2.0, (double)bi.getHeight() / 2.0);
            TextLayout tl = new TextLayout(this.invalidStamp, gfx.getFont(), gfx.getFontRenderContext());
            Rectangle2D bounds = tl.getBounds();
            x = (float)(((double)bi.getWidth() - bounds.getWidth()) / 2.0 - bounds.getX());
            y = (float)(((double)bi.getHeight() - bounds.getHeight()) / 2.0 - bounds.getY());
            Shape outline = tl.getOutline(AffineTransform.getTranslateInstance(x + 2.0f, y + 1.0f));
            gfx.setComposite(AlphaComposite.getInstance(3, 0.3f));
            gfx.setPaint(Color.RED);
            gfx.draw(outline);
            gfx.setPaint(new GradientPaint(0.0f, 0.0f, Color.RED, 30.0f, 20.0f, new Color(128, 128, 255), true));
            tl.draw(gfx, x, y);
        }
        gfx.dispose();
        UnsynchronizedByteArrayOutputStream bos = new UnsynchronizedByteArrayOutputStream();
        ImageIO.write((RenderedImage)bi, "PNG", (OutputStream)bos);
        return bos.toByteArray();
    }

    private void determineContentType() {
        FileMagic fm = FileMagic.valueOf(this.plainSignature);
        PictureType type = PictureType.valueOf(fm);
        if (type == PictureType.UNKNOWN) {
            throw new IllegalArgumentException("unknown image type");
        }
        this.contentType = type.contentType;
    }

    protected static interface AddPictureData {
        public String addPictureData(byte[] var1, PictureType var2) throws InvalidFormatException;
    }
}

