/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible.PDFTemplateBuilder;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible.PDFTemplateStructure;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible.PDVisibleSignDesigner;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDSignatureField;

public class PDVisibleSigBuilder
implements PDFTemplateBuilder {
    private final PDFTemplateStructure pdfStructure = new PDFTemplateStructure();
    private static final Log LOG = LogFactory.getLog(PDVisibleSigBuilder.class);

    public PDVisibleSigBuilder() {
        LOG.info((Object)"PDF Structure has been created");
    }

    @Override
    public void createPage(PDVisibleSignDesigner properties) {
        PDPage page = new PDPage(new PDRectangle(properties.getPageWidth(), properties.getPageHeight()));
        this.pdfStructure.setPage(page);
        LOG.info((Object)"PDF page has been created");
    }

    @Override
    public void createTemplate(PDPage page) throws IOException {
        PDDocument template = new PDDocument();
        template.addPage(page);
        this.pdfStructure.setTemplate(template);
    }

    @Override
    public void createAcroForm(PDDocument template) {
        PDAcroForm theAcroForm = new PDAcroForm(template);
        template.getDocumentCatalog().setAcroForm(theAcroForm);
        this.pdfStructure.setAcroForm(theAcroForm);
        LOG.info((Object)"AcroForm has been created");
    }

    @Override
    public PDFTemplateStructure getStructure() {
        return this.pdfStructure;
    }

    @Override
    public void createSignatureField(PDAcroForm acroForm) throws IOException {
        PDSignatureField sf = new PDSignatureField(acroForm);
        this.pdfStructure.setSignatureField(sf);
        LOG.info((Object)"Signature field has been created");
    }

    @Override
    public void createSignature(PDSignatureField pdSignatureField, PDPage page, String signerName) throws IOException {
        PDSignature pdSignature = new PDSignature();
        PDAnnotationWidget widget = pdSignatureField.getWidgets().get(0);
        pdSignatureField.setValue(pdSignature);
        widget.setPage(page);
        page.getAnnotations().add(widget);
        if (!signerName.isEmpty()) {
            pdSignature.setName(signerName);
        }
        this.pdfStructure.setPdSignature(pdSignature);
        LOG.info((Object)"PDSignature has been created");
    }

    @Override
    public void createAcroFormDictionary(PDAcroForm acroForm, PDSignatureField signatureField) throws IOException {
        List<PDField> acroFormFields = acroForm.getFields();
        COSDictionary acroFormDict = acroForm.getCOSObject();
        acroForm.setSignaturesExist(true);
        acroForm.setAppendOnly(true);
        acroFormDict.setDirect(true);
        acroFormFields.add(signatureField);
        acroForm.setDefaultAppearance("/sylfaen 0 Tf 0 g");
        this.pdfStructure.setAcroFormFields(acroFormFields);
        this.pdfStructure.setAcroFormDictionary(acroFormDict);
        LOG.info((Object)"AcroForm dictionary has been created");
    }

    @Override
    public void createSignatureRectangle(PDSignatureField signatureField, PDVisibleSignDesigner properties) throws IOException {
        PDRectangle rect = new PDRectangle();
        rect.setUpperRightX(properties.getxAxis() + properties.getWidth());
        rect.setUpperRightY(properties.getTemplateHeight() - properties.getyAxis());
        rect.setLowerLeftY(properties.getTemplateHeight() - properties.getyAxis() - properties.getHeight());
        rect.setLowerLeftX(properties.getxAxis());
        signatureField.getWidgets().get(0).setRectangle(rect);
        this.pdfStructure.setSignatureRectangle(rect);
        LOG.info((Object)"Signature rectangle has been created");
    }

    @Override
    @Deprecated
    public void createAffineTransform(byte[] params) {
        AffineTransform transform = new AffineTransform(params[0], params[1], params[2], params[3], params[4], params[5]);
        this.pdfStructure.setAffineTransform(transform);
        LOG.info((Object)"Matrix has been added");
    }

    @Override
    public void createAffineTransform(AffineTransform affineTransform) {
        this.pdfStructure.setAffineTransform(affineTransform);
        LOG.info((Object)"Matrix has been added");
    }

    @Override
    public void createProcSetArray() {
        COSArray procSetArr = new COSArray();
        procSetArr.add(COSName.getPDFName("PDF"));
        procSetArr.add(COSName.getPDFName("Text"));
        procSetArr.add(COSName.getPDFName("ImageB"));
        procSetArr.add(COSName.getPDFName("ImageC"));
        procSetArr.add(COSName.getPDFName("ImageI"));
        this.pdfStructure.setProcSet(procSetArr);
        LOG.info((Object)"ProcSet array has been created");
    }

    @Override
    public void createSignatureImage(PDDocument template, BufferedImage image) throws IOException {
        this.pdfStructure.setImage(LosslessFactory.createFromImage(template, image));
        LOG.info((Object)"Visible Signature Image has been created");
    }

    @Override
    @Deprecated
    public void createFormatterRectangle(byte[] params) {
        PDRectangle formatterRectangle = new PDRectangle();
        formatterRectangle.setLowerLeftX(Math.min(params[0], params[2]));
        formatterRectangle.setLowerLeftY(Math.min(params[1], params[3]));
        formatterRectangle.setUpperRightX(Math.max(params[0], params[2]));
        formatterRectangle.setUpperRightY(Math.max(params[1], params[3]));
        this.pdfStructure.setFormatterRectangle(formatterRectangle);
        LOG.info((Object)"Formatter rectangle has been created");
    }

    @Override
    public void createFormatterRectangle(int[] params) {
        PDRectangle formatterRectangle = new PDRectangle();
        formatterRectangle.setLowerLeftX(Math.min(params[0], params[2]));
        formatterRectangle.setLowerLeftY(Math.min(params[1], params[3]));
        formatterRectangle.setUpperRightX(Math.max(params[0], params[2]));
        formatterRectangle.setUpperRightY(Math.max(params[1], params[3]));
        this.pdfStructure.setFormatterRectangle(formatterRectangle);
        LOG.info((Object)"Formatter rectangle has been created");
    }

    @Override
    public void createHolderFormStream(PDDocument template) {
        PDStream holderForm = new PDStream(template);
        this.pdfStructure.setHolderFormStream(holderForm);
        LOG.info((Object)"Holder form stream has been created");
    }

    @Override
    public void createHolderFormResources() {
        PDResources holderFormResources = new PDResources();
        this.pdfStructure.setHolderFormResources(holderFormResources);
        LOG.info((Object)"Holder form resources have been created");
    }

    @Override
    public void createHolderForm(PDResources holderFormResources, PDStream holderFormStream, PDRectangle bbox) {
        PDFormXObject holderForm = new PDFormXObject(holderFormStream);
        holderForm.setResources(holderFormResources);
        holderForm.setBBox(bbox);
        holderForm.setFormType(1);
        this.pdfStructure.setHolderForm(holderForm);
        LOG.info((Object)"Holder form has been created");
    }

    @Override
    public void createAppearanceDictionary(PDFormXObject holderForml, PDSignatureField signatureField) throws IOException {
        PDAppearanceDictionary appearance = new PDAppearanceDictionary();
        appearance.getCOSObject().setDirect(true);
        PDAppearanceStream appearanceStream = new PDAppearanceStream(holderForml.getCOSObject());
        appearance.setNormalAppearance(appearanceStream);
        signatureField.getWidgets().get(0).setAppearance(appearance);
        this.pdfStructure.setAppearanceDictionary(appearance);
        LOG.info((Object)"PDF appearance dictionary has been created");
    }

    @Override
    public void createInnerFormStream(PDDocument template) {
        PDStream innerFormStream = new PDStream(template);
        this.pdfStructure.setInnterFormStream(innerFormStream);
        LOG.info((Object)"Stream of another form (inner form - it will be inside holder form) has been created");
    }

    @Override
    public void createInnerFormResource() {
        PDResources innerFormResources = new PDResources();
        this.pdfStructure.setInnerFormResources(innerFormResources);
        LOG.info((Object)"Resources of another form (inner form - it will be inside holder form)have been created");
    }

    @Override
    public void createInnerForm(PDResources innerFormResources, PDStream innerFormStream, PDRectangle bbox) {
        PDFormXObject innerForm = new PDFormXObject(innerFormStream);
        innerForm.setResources(innerFormResources);
        innerForm.setBBox(bbox);
        innerForm.setFormType(1);
        this.pdfStructure.setInnerForm(innerForm);
        LOG.info((Object)"Another form (inner form - it will be inside holder form) has been created");
    }

    @Override
    public void insertInnerFormToHolderResources(PDFormXObject innerForm, PDResources holderFormResources) {
        holderFormResources.put(COSName.FRM, innerForm);
        this.pdfStructure.setInnerFormName(COSName.FRM);
        LOG.info((Object)"Now inserted inner form inside holder form");
    }

    @Override
    public void createImageFormStream(PDDocument template) {
        PDStream imageFormStream = new PDStream(template);
        this.pdfStructure.setImageFormStream(imageFormStream);
        LOG.info((Object)"Created image form stream");
    }

    @Override
    public void createImageFormResources() {
        PDResources imageFormResources = new PDResources();
        this.pdfStructure.setImageFormResources(imageFormResources);
        LOG.info((Object)"Created image form resources");
    }

    @Override
    public void createImageForm(PDResources imageFormResources, PDResources innerFormResource, PDStream imageFormStream, PDRectangle bbox, AffineTransform at, PDImageXObject img) throws IOException {
        PDFormXObject imageForm = new PDFormXObject(imageFormStream);
        imageForm.setBBox(bbox);
        imageForm.setMatrix(at);
        imageForm.setResources(imageFormResources);
        imageForm.setFormType(1);
        imageFormResources.getCOSObject().setDirect(true);
        COSName imageFormName = COSName.getPDFName("n2");
        innerFormResource.put(imageFormName, imageForm);
        COSName imageName = imageFormResources.add(img, "img");
        this.pdfStructure.setImageForm(imageForm);
        this.pdfStructure.setImageFormName(imageFormName);
        this.pdfStructure.setImageName(imageName);
        LOG.info((Object)"Created image form");
    }

    @Override
    public void createBackgroundLayerForm(PDResources innerFormResource, PDRectangle bbox) throws IOException {
        PDFormXObject n0Form = new PDFormXObject(this.pdfStructure.getTemplate().getDocument().createCOSStream());
        n0Form.setBBox(bbox);
        n0Form.setResources(new PDResources());
        n0Form.setFormType(1);
        innerFormResource.put(COSName.getPDFName("n0"), n0Form);
        LOG.info((Object)"Created background layer form");
    }

    @Override
    public void injectProcSetArray(PDFormXObject innerForm, PDPage page, PDResources innerFormResources, PDResources imageFormResources, PDResources holderFormResources, COSArray procSet) {
        innerForm.getResources().getCOSObject().setItem(COSName.PROC_SET, (COSBase)procSet);
        page.getCOSObject().setItem(COSName.PROC_SET, (COSBase)procSet);
        innerFormResources.getCOSObject().setItem(COSName.PROC_SET, (COSBase)procSet);
        imageFormResources.getCOSObject().setItem(COSName.PROC_SET, (COSBase)procSet);
        holderFormResources.getCOSObject().setItem(COSName.PROC_SET, (COSBase)procSet);
        LOG.info((Object)"Inserted ProcSet to PDF");
    }

    @Override
    public void injectAppearanceStreams(PDStream holderFormStream, PDStream innerFormStream, PDStream imageFormStream, COSName imageFormName, COSName imageName, COSName innerFormName, PDVisibleSignDesigner properties) throws IOException {
        int width = (int)this.getStructure().getFormatterRectangle().getWidth();
        int height = (int)this.getStructure().getFormatterRectangle().getHeight();
        String imgFormContent = "q " + width + " 0 0 " + height + " 0 0 cm /" + imageName.getName() + " Do Q\n";
        String holderFormContent = "q 1 0 0 1 0 0 cm /" + innerFormName.getName() + " Do Q\n";
        String innerFormContent = "q 1 0 0 1 0 0 cm /n0 Do Q q 1 0 0 1 0 0 cm /" + imageFormName.getName() + " Do Q\n";
        this.appendRawCommands(this.pdfStructure.getHolderFormStream().createOutputStream(), holderFormContent);
        this.appendRawCommands(this.pdfStructure.getInnerFormStream().createOutputStream(), innerFormContent);
        this.appendRawCommands(this.pdfStructure.getImageFormStream().createOutputStream(), imgFormContent);
        LOG.info((Object)"Injected appearance stream to pdf");
    }

    public void appendRawCommands(OutputStream os, String commands) throws IOException {
        os.write(commands.getBytes("UTF-8"));
        os.close();
    }

    @Override
    public void createVisualSignature(PDDocument template) {
        this.pdfStructure.setVisualSignature(template.getDocument());
        LOG.info((Object)"Visible signature has been created");
    }

    @Override
    public void createWidgetDictionary(PDSignatureField signatureField, PDResources holderFormResources) throws IOException {
        COSDictionary widgetDict = signatureField.getWidgets().get(0).getCOSObject();
        widgetDict.setNeedToBeUpdated(true);
        widgetDict.setItem(COSName.DR, (COSBase)holderFormResources.getCOSObject());
        this.pdfStructure.setWidgetDictionary(widgetDict);
        LOG.info((Object)"WidgetDictionary has been created");
    }

    @Override
    public void closeTemplate(PDDocument template) throws IOException {
        template.close();
        this.pdfStructure.getTemplate().close();
    }
}

