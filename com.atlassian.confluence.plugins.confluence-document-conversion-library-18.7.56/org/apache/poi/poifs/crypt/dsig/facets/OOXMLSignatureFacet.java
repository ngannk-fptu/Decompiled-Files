/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.poifs.crypt.dsig.facets;

import com.microsoft.schemas.office.x2006.digsig.CTSignatureInfoV1;
import com.microsoft.schemas.office.x2006.digsig.SignatureInfoV1Document;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.crypto.URIReference;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.Manifest;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureProperties;
import javax.xml.crypto.dsig.SignatureProperty;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import org.apache.poi.openxml4j.opc.TargetMode;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.poifs.crypt.dsig.SignatureConfig;
import org.apache.poi.poifs.crypt.dsig.SignatureInfo;
import org.apache.poi.poifs.crypt.dsig.facets.SignatureFacet;
import org.apache.poi.poifs.crypt.dsig.facets.SignatureFacetHelper;
import org.apache.poi.poifs.crypt.dsig.services.RelationshipTransformService;
import org.openxmlformats.schemas.xpackage.x2006.digitalSignature.CTSignatureTime;
import org.openxmlformats.schemas.xpackage.x2006.digitalSignature.SignatureTimeDocument;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class OOXMLSignatureFacet
implements SignatureFacet {
    private static final Logger LOG = LogManager.getLogger(OOXMLSignatureFacet.class);
    private static final String ID_PACKAGE_OBJECT = "idPackageObject";
    private static final Set<String> signed = Stream.of("activeXControlBinary", "aFChunk", "attachedTemplate", "attachedToolbars", "audio", "calcChain", "chart", "chartColorStyle", "chartLayout", "chartsheet", "chartStyle", "chartUserShapes", "commentAuthors", "comments", "connections", "connectorXml", "control", "ctrlProp", "customData", "customData", "customProperty", "customXml", "diagram", "diagramColors", "diagramColorsHeader", "diagramData", "diagramDrawing", "diagramLayout", "diagramLayoutHeader", "diagramQuickStyle", "diagramQuickStyleHeader", "dialogsheet", "dictionary", "documentParts", "downRev", "drawing", "endnotes", "externalLink", "externalLinkPath", "font", "fontTable", "footer", "footnotes", "functionPrototypes", "glossaryDocument", "graphicFrameDoc", "groupShapeXml", "handoutMaster", "hdphoto", "header", "hyperlink", "image", "ink", "inkXml", "keyMapCustomizations", "legacyDiagramText", "legacyDocTextInfo", "mailMergeHeaderSource", "mailMergeRecipientData", "mailMergeSource", "media", "notesMaster", "notesSlide", "numbering", "officeDocument", "officeDocument", "oleObject", "package", "pictureXml", "pivotCacheDefinition", "pivotCacheRecords", "pivotTable", "powerPivotData", "presProps", "printerSettings", "queryTable", "recipientData", "settings", "shapeXml", "sharedStrings", "sheetMetadata", "slicer", "slicer", "slicerCache", "slicerCache", "slide", "slideLayout", "slideMaster", "slideUpdateInfo", "slideUpdateUrl", "smartTags", "styles", "stylesWithEffects", "table", "tableSingleCells", "tableStyles", "tags", "theme", "themeOverride", "timeline", "timelineCache", "transform", "ui/altText", "ui/buttonSize", "ui/controlID", "ui/description", "ui/enabled", "ui/extensibility", "ui/extensibility", "ui/helperText", "ui/imageID", "ui/imageMso", "ui/keyTip", "ui/label", "ui/lcid", "ui/loud", "ui/pressed", "ui/progID", "ui/ribbonID", "ui/showImage", "ui/showLabel", "ui/supertip", "ui/target", "ui/text", "ui/title", "ui/tooltip", "ui/userCustomization", "ui/visible", "userXmlData", "vbaProject", "video", "viewProps", "vmlDrawing", "volatileDependencies", "webSettings", "wordVbaData", "worksheet", "wsSortMap", "xlBinaryIndex", "xlExternalLinkPath/xlAlternateStartup", "xlExternalLinkPath/xlLibrary", "xlExternalLinkPath/xlPathMissing", "xlExternalLinkPath/xlStartup", "xlIntlMacrosheet", "xlMacrosheet", "xmlMaps").collect(Collectors.toSet());

    @Override
    public void preSign(SignatureInfo signatureInfo, Document document, List<Reference> references, List<XMLObject> objects) throws XMLSignatureException {
        LOG.atDebug().log("pre sign");
        this.addManifestObject(signatureInfo, document, references, objects);
        this.addSignatureInfo(signatureInfo, document, references, objects);
    }

    protected void addManifestObject(SignatureInfo signatureInfo, Document document, List<Reference> references, List<XMLObject> objects) throws XMLSignatureException {
        XMLSignatureFactory sigFac = signatureInfo.getSignatureFactory();
        ArrayList<Reference> manifestReferences = new ArrayList<Reference>();
        this.addManifestReferences(signatureInfo, manifestReferences);
        Manifest manifest = sigFac.newManifest(manifestReferences);
        ArrayList<XMLStructure> objectContent = new ArrayList<XMLStructure>();
        objectContent.add(manifest);
        this.addSignatureTime(signatureInfo, document, objectContent);
        XMLObject xo = sigFac.newXMLObject(objectContent, ID_PACKAGE_OBJECT, null, null);
        objects.add(xo);
        Reference reference = SignatureFacetHelper.newReference(signatureInfo, "#idPackageObject", null, "http://www.w3.org/2000/09/xmldsig#Object");
        references.add(reference);
    }

    protected void addManifestReferences(SignatureInfo signatureInfo, List<Reference> manifestReferences) throws XMLSignatureException {
        OPCPackage opcPackage = signatureInfo.getOpcPackage();
        ArrayList<PackagePart> relsEntryNames = opcPackage.getPartsByContentType("application/vnd.openxmlformats-package.relationships+xml");
        HashSet<String> digestedPartNames = new HashSet<String>();
        for (PackagePart pp : relsEntryNames) {
            PackageRelationshipCollection prc;
            String baseUri = pp.getPartName().getName().replaceFirst("(.*)/_rels/.*", "$1");
            try {
                prc = new PackageRelationshipCollection(opcPackage);
                prc.parseRelationshipsPart(pp);
            }
            catch (InvalidFormatException e) {
                throw new XMLSignatureException("Invalid relationship descriptor: " + pp.getPartName().getName(), e);
            }
            RelationshipTransformService.RelationshipTransformParameterSpec parameterSpec = new RelationshipTransformService.RelationshipTransformParameterSpec();
            for (PackageRelationship relationship : prc) {
                String contentType;
                String relationshipType = relationship.getRelationshipType();
                if (TargetMode.EXTERNAL == relationship.getTargetMode()) {
                    parameterSpec.addRelationshipReference(relationship.getId());
                    continue;
                }
                if (!OOXMLSignatureFacet.isSignedRelationship(relationshipType)) continue;
                parameterSpec.addRelationshipReference(relationship.getId());
                String partName = OOXMLSignatureFacet.normalizePartName(relationship.getTargetURI(), baseUri);
                if (digestedPartNames.contains(partName)) continue;
                digestedPartNames.add(partName);
                try {
                    PackagePartName relName = PackagingURIHelper.createPartName(partName);
                    PackagePart pp2 = opcPackage.getPart(relName);
                    contentType = pp2.getContentType();
                }
                catch (InvalidFormatException e) {
                    throw new XMLSignatureException(e);
                }
                if (relationshipType.endsWith("customXml") && !contentType.equals("inkml+xml") && !contentType.equals("text/xml")) {
                    LOG.atDebug().log("skipping customXml with content type: {}", (Object)contentType);
                    continue;
                }
                String uri = partName + "?ContentType=" + contentType;
                Reference reference = SignatureFacetHelper.newReference(signatureInfo, uri, null, null);
                manifestReferences.add(reference);
            }
            if (!parameterSpec.hasSourceIds()) continue;
            ArrayList<Transform> transforms = new ArrayList<Transform>();
            transforms.add(SignatureFacetHelper.newTransform(signatureInfo, "http://schemas.openxmlformats.org/package/2006/RelationshipTransform", parameterSpec));
            transforms.add(SignatureFacetHelper.newTransform(signatureInfo, "http://www.w3.org/TR/2001/REC-xml-c14n-20010315"));
            String uri = OOXMLSignatureFacet.normalizePartName(pp.getPartName().getURI(), baseUri) + "?ContentType=application/vnd.openxmlformats-package.relationships+xml";
            Reference reference = SignatureFacetHelper.newReference(signatureInfo, uri, transforms, null);
            manifestReferences.add(reference);
        }
        manifestReferences.sort(Comparator.comparing(URIReference::getURI));
    }

    private static String normalizePartName(URI partName, String baseUri) throws XMLSignatureException {
        String pn = partName.toASCIIString();
        if (!pn.startsWith(baseUri)) {
            pn = baseUri + pn;
        }
        try {
            pn = new URI(pn).normalize().getPath().replace('\\', '/');
            LOG.atDebug().log("part name: {}", (Object)pn);
        }
        catch (URISyntaxException e) {
            throw new XMLSignatureException(e);
        }
        return pn;
    }

    protected void addSignatureTime(SignatureInfo signatureInfo, Document document, List<XMLStructure> objectContent) {
        SignatureConfig signatureConfig = signatureInfo.getSignatureConfig();
        XMLSignatureFactory sigFac = signatureInfo.getSignatureFactory();
        SignatureTimeDocument sigTime = SignatureTimeDocument.Factory.newInstance();
        CTSignatureTime ctTime = sigTime.addNewSignatureTime();
        ctTime.setFormat("YYYY-MM-DDThh:mm:ssTZD");
        ctTime.setValue(signatureConfig.formatExecutionTime());
        LOG.atDebug().log("execution time: {}", (Object)ctTime.getValue());
        Element n = (Element)document.importNode(ctTime.getDomNode(), true);
        ArrayList<DOMStructure> signatureTimeContent = new ArrayList<DOMStructure>();
        signatureTimeContent.add(new DOMStructure(n));
        SignatureProperty signatureTimeSignatureProperty = sigFac.newSignatureProperty(signatureTimeContent, "#" + signatureConfig.getPackageSignatureId(), "idSignatureTime");
        ArrayList<SignatureProperty> signaturePropertyContent = new ArrayList<SignatureProperty>();
        signaturePropertyContent.add(signatureTimeSignatureProperty);
        SignatureProperties signatureProperties = sigFac.newSignatureProperties(signaturePropertyContent, null);
        objectContent.add(signatureProperties);
    }

    protected void addSignatureInfo(SignatureInfo signatureInfo, Document document, List<Reference> references, List<XMLObject> objects) throws XMLSignatureException {
        byte[] imageInvalid;
        SignatureConfig signatureConfig = signatureInfo.getSignatureConfig();
        XMLSignatureFactory sigFac = signatureInfo.getSignatureFactory();
        ArrayList<SignatureProperties> objectContent = new ArrayList<SignatureProperties>();
        SignatureInfoV1Document sigV1 = this.createSignatureInfoV1(signatureInfo);
        Element n = (Element)document.importNode(sigV1.getSignatureInfoV1().getDomNode(), true);
        ArrayList<DOMStructure> signatureInfoContent = new ArrayList<DOMStructure>();
        signatureInfoContent.add(new DOMStructure(n));
        SignatureProperty signatureInfoSignatureProperty = sigFac.newSignatureProperty(signatureInfoContent, "#" + signatureConfig.getPackageSignatureId(), "idOfficeV1Details");
        ArrayList<SignatureProperty> signaturePropertyContent = new ArrayList<SignatureProperty>();
        signaturePropertyContent.add(signatureInfoSignatureProperty);
        SignatureProperties signatureProperties = sigFac.newSignatureProperties(signaturePropertyContent, null);
        objectContent.add(signatureProperties);
        String objectId = "idOfficeObject";
        objects.add(sigFac.newXMLObject(objectContent, objectId, null, null));
        Reference reference = SignatureFacetHelper.newReference(signatureInfo, "#" + objectId, null, "http://www.w3.org/2000/09/xmldsig#Object");
        references.add(reference);
        Base64.Encoder enc = Base64.getEncoder();
        byte[] imageValid = signatureConfig.getSignatureImageValid();
        if (imageValid != null) {
            objectId = "idValidSigLnImg";
            DOMStructure tn = new DOMStructure(document.createTextNode(enc.encodeToString(imageValid)));
            objects.add(sigFac.newXMLObject(Collections.singletonList(tn), objectId, null, null));
            reference = SignatureFacetHelper.newReference(signatureInfo, "#" + objectId, null, "http://www.w3.org/2000/09/xmldsig#Object");
            references.add(reference);
        }
        if ((imageInvalid = signatureConfig.getSignatureImageInvalid()) != null) {
            objectId = "idInvalidSigLnImg";
            DOMStructure tn = new DOMStructure(document.createTextNode(enc.encodeToString(imageInvalid)));
            objects.add(sigFac.newXMLObject(Collections.singletonList(tn), objectId, null, null));
            reference = SignatureFacetHelper.newReference(signatureInfo, "#" + objectId, null, "http://www.w3.org/2000/09/xmldsig#Object");
            references.add(reference);
        }
    }

    protected SignatureInfoV1Document createSignatureInfoV1(SignatureInfo signatureInfo) {
        byte[] image;
        String desc;
        SignatureConfig signatureConfig = signatureInfo.getSignatureConfig();
        SignatureInfoV1Document sigV1 = SignatureInfoV1Document.Factory.newInstance();
        CTSignatureInfoV1 ctSigV1 = sigV1.addNewSignatureInfoV1();
        if (signatureConfig.getDigestAlgo() != HashAlgorithm.sha1) {
            ctSigV1.setManifestHashAlgorithm(signatureConfig.getDigestMethodUri());
        }
        if ((desc = signatureConfig.getSignatureDescription()) != null) {
            ctSigV1.setSignatureComments(desc);
        }
        if ((image = signatureConfig.getSignatureImage()) == null) {
            ctSigV1.setSignatureType(1);
        } else {
            ctSigV1.setSetupID(signatureConfig.getSignatureImageSetupId().toString());
            ctSigV1.setSignatureImage(image);
            ctSigV1.setSignatureType(2);
        }
        return sigV1;
    }

    protected static String getRelationshipReferenceURI(String zipEntryName) {
        return "/" + zipEntryName + "?ContentType=application/vnd.openxmlformats-package.relationships+xml";
    }

    protected static String getResourceReferenceURI(String resourceName, String contentType) {
        return "/" + resourceName + "?ContentType=" + contentType;
    }

    protected static boolean isSignedRelationship(String relationshipType) {
        LOG.atDebug().log("relationship type: {}", (Object)relationshipType);
        String rt = relationshipType.replaceFirst(".*/relationships/", "");
        return signed.contains(rt) || rt.endsWith("customXml");
    }
}

