/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible.PDFTemplateStructure;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible.PDVisibleSignDesigner;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDSignatureField;

public interface PDFTemplateBuilder {
    @Deprecated
    public void createAffineTransform(byte[] var1);

    public void createAffineTransform(AffineTransform var1);

    public void createPage(PDVisibleSignDesigner var1);

    public void createTemplate(PDPage var1) throws IOException;

    public void createAcroForm(PDDocument var1);

    public void createSignatureField(PDAcroForm var1) throws IOException;

    public void createSignature(PDSignatureField var1, PDPage var2, String var3) throws IOException;

    public void createAcroFormDictionary(PDAcroForm var1, PDSignatureField var2) throws IOException;

    public void createSignatureRectangle(PDSignatureField var1, PDVisibleSignDesigner var2) throws IOException;

    public void createProcSetArray();

    public void createSignatureImage(PDDocument var1, BufferedImage var2) throws IOException;

    @Deprecated
    public void createFormatterRectangle(byte[] var1);

    public void createFormatterRectangle(int[] var1);

    public void createHolderFormStream(PDDocument var1);

    public void createHolderFormResources();

    public void createHolderForm(PDResources var1, PDStream var2, PDRectangle var3);

    public void createAppearanceDictionary(PDFormXObject var1, PDSignatureField var2) throws IOException;

    public void createInnerFormStream(PDDocument var1);

    public void createInnerFormResource();

    public void createInnerForm(PDResources var1, PDStream var2, PDRectangle var3);

    public void insertInnerFormToHolderResources(PDFormXObject var1, PDResources var2);

    public void createImageFormStream(PDDocument var1);

    public void createImageFormResources();

    public void createImageForm(PDResources var1, PDResources var2, PDStream var3, PDRectangle var4, AffineTransform var5, PDImageXObject var6) throws IOException;

    public void createBackgroundLayerForm(PDResources var1, PDRectangle var2) throws IOException;

    public void injectProcSetArray(PDFormXObject var1, PDPage var2, PDResources var3, PDResources var4, PDResources var5, COSArray var6);

    public void injectAppearanceStreams(PDStream var1, PDStream var2, PDStream var3, COSName var4, COSName var5, COSName var6, PDVisibleSignDesigner var7) throws IOException;

    public void createVisualSignature(PDDocument var1);

    public void createWidgetDictionary(PDSignatureField var1, PDResources var2) throws IOException;

    public PDFTemplateStructure getStructure();

    public void closeTemplate(PDDocument var1) throws IOException;
}

