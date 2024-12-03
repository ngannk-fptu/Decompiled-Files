/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.jcp.xml.dsig.internal.dom.DOMReference
 *  org.apache.jcp.xml.dsig.internal.dom.DOMSignedInfo
 *  org.apache.jcp.xml.dsig.internal.dom.DOMSubTreeData
 *  org.apache.xml.security.Init
 *  org.apache.xml.security.utils.XMLUtils
 */
package org.apache.poi.poifs.crypt.dsig;

import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Provider;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Stream;
import javax.xml.crypto.Data;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.Manifest;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.TransformException;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.XMLSignContext;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import org.apache.jcp.xml.dsig.internal.dom.DOMReference;
import org.apache.jcp.xml.dsig.internal.dom.DOMSignedInfo;
import org.apache.jcp.xml.dsig.internal.dom.DOMSubTreeData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import org.apache.poi.ooxml.util.DocumentHelper;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import org.apache.poi.openxml4j.opc.TargetMode;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.poifs.crypt.dsig.DSigRelation;
import org.apache.poi.poifs.crypt.dsig.DigestOutputStream;
import org.apache.poi.poifs.crypt.dsig.OOXMLURIDereferencer;
import org.apache.poi.poifs.crypt.dsig.SignatureConfig;
import org.apache.poi.poifs.crypt.dsig.SignatureMarshalListener;
import org.apache.poi.poifs.crypt.dsig.SignatureOutputStream;
import org.apache.poi.poifs.crypt.dsig.SignaturePart;
import org.apache.poi.poifs.crypt.dsig.facets.SignatureFacet;
import org.apache.poi.poifs.crypt.dsig.services.RelationshipTransformService;
import org.apache.xml.security.Init;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xmlbeans.XmlOptions;
import org.w3.x2000.x09.xmldsig.SignatureDocument;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MutationEvent;

public class SignatureInfo {
    private static final Logger LOG = LogManager.getLogger(SignatureInfo.class);
    private SignatureConfig signatureConfig;
    private OPCPackage opcPackage;
    private Provider provider;
    private XMLSignatureFactory signatureFactory;
    private KeyInfoFactory keyInfoFactory;
    private URIDereferencer uriDereferencer;

    public SignatureConfig getSignatureConfig() {
        return this.signatureConfig;
    }

    public void setSignatureConfig(SignatureConfig signatureConfig) {
        this.signatureConfig = signatureConfig;
    }

    public void setOpcPackage(OPCPackage opcPackage) {
        this.opcPackage = opcPackage;
    }

    public OPCPackage getOpcPackage() {
        return this.opcPackage;
    }

    public URIDereferencer getUriDereferencer() {
        return this.uriDereferencer;
    }

    public void setUriDereferencer(URIDereferencer uriDereferencer) {
        this.uriDereferencer = uriDereferencer;
    }

    public boolean verifySignature() {
        this.initXmlProvider();
        Iterator<SignaturePart> iter = this.getSignatureParts().iterator();
        return iter.hasNext() && iter.next().validate();
    }

    public void confirmSignature() throws XMLSignatureException, MarshalException {
        this.initXmlProvider();
        Document document = DocumentHelper.createDocument();
        DOMSignContext xmlSignContext = this.createXMLSignContext(document);
        DOMSignedInfo signedInfo = this.preSign(xmlSignContext);
        String signatureValue = this.signDigest(xmlSignContext, signedInfo);
        this.postSign(xmlSignContext, signatureValue);
    }

    public DOMSignContext createXMLSignContext(Document document) {
        this.initXmlProvider();
        return new DOMSignContext(this.signatureConfig.getKey(), (Node)document);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public String signDigest(DOMSignContext xmlSignContext, DOMSignedInfo signedInfo) {
        this.initXmlProvider();
        PrivateKey key = this.signatureConfig.getKey();
        HashAlgorithm algo = this.signatureConfig.getDigestAlgo();
        int BASE64DEFAULTLENGTH = 76;
        if (algo.hashSize * 4 / 3 > 76 && !XMLUtils.ignoreLineBreaks()) {
            throw new EncryptedDocumentException("The hash size of the chosen hash algorithm (" + (Object)((Object)algo) + " = " + algo.hashSize + " bytes), will motivate XmlSec to add linebreaks to the generated digest, which results in an invalid signature (... at least for Office) - please persuade it otherwise by adding '-Dorg.apache.xml.security.ignoreLineBreaks=true' to the JVM system properties.");
        }
        try (DigestOutputStream dos = SignatureInfo.getDigestStream(algo, key);){
            dos.init();
            Document document = (Document)xmlSignContext.getParent();
            Element el = this.getDsigElement(document, "SignedInfo");
            DOMSubTreeData subTree = new DOMSubTreeData((Node)el, true);
            signedInfo.getCanonicalizationMethod().transform((Data)subTree, xmlSignContext, dos);
            String string = Base64.getEncoder().encodeToString(dos.sign());
            return string;
        }
        catch (IOException | GeneralSecurityException | TransformException e) {
            throw new EncryptedDocumentException(e);
        }
    }

    private static DigestOutputStream getDigestStream(HashAlgorithm algo, PrivateKey key) {
        switch (algo) {
            case md2: 
            case md5: 
            case sha1: 
            case sha256: 
            case sha384: 
            case sha512: {
                return new SignatureOutputStream(algo, key);
            }
        }
        return new DigestOutputStream(algo, key);
    }

    public Iterable<SignaturePart> getSignatureParts() {
        this.initXmlProvider();
        return () -> new SignaturePartIterator();
    }

    public DOMSignedInfo preSign(DOMSignContext xmlSignContext) throws XMLSignatureException, MarshalException {
        SignedInfo signedInfo;
        Document document = (Document)xmlSignContext.getParent();
        this.registerEventListener(document);
        if (this.uriDereferencer != null) {
            xmlSignContext.setURIDereferencer(this.uriDereferencer);
        }
        this.signatureConfig.getNamespacePrefixes().forEach(xmlSignContext::putNamespacePrefix);
        xmlSignContext.setDefaultNamespacePrefix("");
        ArrayList<Reference> references = new ArrayList<Reference>();
        ArrayList<XMLObject> objects = new ArrayList<XMLObject>();
        for (SignatureFacet signatureFacet : this.signatureConfig.getSignatureFacets()) {
            LOG.atDebug().log("invoking signature facet: {}", (Object)signatureFacet.getClass().getSimpleName());
            signatureFacet.preSign(this, document, references, objects);
        }
        try {
            SignatureMethod signatureMethod = this.signatureFactory.newSignatureMethod(this.signatureConfig.getSignatureMethodUri(), null);
            CanonicalizationMethod canonicalizationMethod = this.signatureFactory.newCanonicalizationMethod(this.signatureConfig.getCanonicalizationMethod(), (C14NMethodParameterSpec)null);
            signedInfo = this.signatureFactory.newSignedInfo(canonicalizationMethod, signatureMethod, references);
        }
        catch (GeneralSecurityException e) {
            throw new XMLSignatureException(e);
        }
        String signatureValueId = this.signatureConfig.getPackageSignatureId() + "-signature-value";
        XMLSignature xmlSignature = this.signatureFactory.newXMLSignature(signedInfo, null, objects, this.signatureConfig.getPackageSignatureId(), signatureValueId);
        xmlSignature.sign(xmlSignContext);
        for (XMLObject object : objects) {
            LOG.atDebug().log("object java type: {}", (Object)object.getClass().getName());
            List<XMLStructure> objectContentList = object.getContent();
            for (XMLStructure objectContent : objectContentList) {
                LOG.atDebug().log("object content java type: {}", (Object)objectContent.getClass().getName());
                if (!(objectContent instanceof Manifest)) continue;
                Manifest manifest = (Manifest)objectContent;
                List<Reference> manifestReferences = manifest.getReferences();
                for (Reference manifestReference : manifestReferences) {
                    if (manifestReference.getDigestValue() != null) continue;
                    DOMReference manifestDOMReference = (DOMReference)manifestReference;
                    manifestDOMReference.digest((XMLSignContext)xmlSignContext);
                }
            }
        }
        List<Reference> signedInfoReferences = signedInfo.getReferences();
        for (Reference signedInfoReference : signedInfoReferences) {
            DOMReference domReference = (DOMReference)signedInfoReference;
            if (domReference.getDigestValue() != null) continue;
            domReference.digest((XMLSignContext)xmlSignContext);
        }
        return (DOMSignedInfo)signedInfo;
    }

    protected void registerEventListener(Document document) {
        SignatureMarshalListener sml = this.signatureConfig.getSignatureMarshalListener();
        if (sml == null) {
            return;
        }
        EventListener[] el = new EventListener[]{null};
        EventTarget eventTarget = (EventTarget)((Object)document);
        String eventType = "DOMSubtreeModified";
        boolean DONT_USE_CAPTURE = false;
        el[0] = e -> {
            if (e instanceof MutationEvent && e.getTarget() instanceof Document) {
                eventTarget.removeEventListener("DOMSubtreeModified", el[0], false);
                sml.handleElement(this, document, eventTarget, el[0]);
                eventTarget.addEventListener("DOMSubtreeModified", el[0], false);
            }
        };
        eventTarget.addEventListener("DOMSubtreeModified", el[0], false);
    }

    public void postSign(DOMSignContext xmlSignContext, String signatureValue) throws MarshalException {
        LOG.atDebug().log("postSign");
        Document document = (Document)xmlSignContext.getParent();
        String signatureId = this.signatureConfig.getPackageSignatureId();
        if (!signatureId.equals(document.getDocumentElement().getAttribute("Id"))) {
            throw new RuntimeException("ds:Signature not found for @Id: " + signatureId);
        }
        Element signatureNode = this.getDsigElement(document, "SignatureValue");
        if (signatureNode == null) {
            throw new RuntimeException("preSign has to be called before postSign");
        }
        signatureNode.setTextContent(signatureValue);
        for (SignatureFacet signatureFacet : this.signatureConfig.getSignatureFacets()) {
            signatureFacet.postSign(this, document);
        }
        this.writeDocument(document);
    }

    protected void writeDocument(Document document) throws MarshalException {
        XmlOptions xo = new XmlOptions();
        HashMap<String, String> namespaceMap = new HashMap<String, String>();
        this.signatureConfig.getNamespacePrefixes().forEach((k, v) -> namespaceMap.put((String)v, (String)k));
        xo.setSaveSuggestedPrefixes(namespaceMap);
        xo.setUseDefaultNamespace();
        LOG.atDebug().log("output signed Office OpenXML document");
        try {
            PackagePartName sigPartName;
            PackagePart sigPart;
            DSigRelation originDesc = DSigRelation.ORIGIN_SIGS;
            PackagePartName originPartName = PackagingURIHelper.createPartName(originDesc.getFileName(0));
            PackagePart originPart = this.opcPackage.getPart(originPartName);
            if (originPart == null) {
                originPart = this.opcPackage.createPart(originPartName, originDesc.getContentType());
                this.opcPackage.addRelationship(originPartName, TargetMode.INTERNAL, originDesc.getRelation());
            }
            DSigRelation sigDesc = DSigRelation.SIG;
            int nextSigIdx = this.opcPackage.getUnusedPartIndex(sigDesc.getDefaultFileName());
            if (!this.signatureConfig.isAllowMultipleSignatures()) {
                PackageRelationshipCollection prc = originPart.getRelationshipsByType(sigDesc.getRelation());
                for (int i = 2; i < nextSigIdx; ++i) {
                    PackagePartName pn = PackagingURIHelper.createPartName(sigDesc.getFileName(i));
                    for (PackageRelationship rel : prc) {
                        PackagePart pp = originPart.getRelatedPart(rel);
                        if (!pp.getPartName().equals(pn)) continue;
                        originPart.removeRelationship(rel.getId());
                        prc.removeRelationship(rel.getId());
                        break;
                    }
                    this.opcPackage.removePart(this.opcPackage.getPart(pn));
                }
                nextSigIdx = 1;
            }
            if ((sigPart = this.opcPackage.getPart(sigPartName = PackagingURIHelper.createPartName(sigDesc.getFileName(nextSigIdx)))) == null) {
                sigPart = this.opcPackage.createPart(sigPartName, sigDesc.getContentType());
                originPart.addRelationship(sigPartName, TargetMode.INTERNAL, sigDesc.getRelation());
            } else {
                sigPart.clear();
            }
            OutputStream os = sigPart.getOutputStream();
            Object object = null;
            try {
                SignatureDocument sigDoc = (SignatureDocument)SignatureDocument.Factory.parse(document, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
                sigDoc.save(os, xo);
            }
            catch (Throwable throwable) {
                object = throwable;
                throw throwable;
            }
            finally {
                if (os != null) {
                    if (object != null) {
                        try {
                            os.close();
                        }
                        catch (Throwable throwable) {
                            ((Throwable)object).addSuppressed(throwable);
                        }
                    } else {
                        os.close();
                    }
                }
            }
        }
        catch (Exception e) {
            throw new MarshalException("Unable to write signature document", e);
        }
    }

    private Element getDsigElement(Document document, String localName) {
        NodeList sigValNl = document.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", localName);
        if (sigValNl.getLength() == 1) {
            return (Element)sigValNl.item(0);
        }
        LOG.atWarn().log("Signature element '{}' was {}", (Object)localName, (Object)(sigValNl.getLength() == 0 ? "not found" : "multiple times"));
        return null;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public void setSignatureFactory(XMLSignatureFactory signatureFactory) {
        this.signatureFactory = signatureFactory;
    }

    public XMLSignatureFactory getSignatureFactory() {
        return this.signatureFactory;
    }

    public void setKeyInfoFactory(KeyInfoFactory keyInfoFactory) {
        this.keyInfoFactory = keyInfoFactory;
    }

    public KeyInfoFactory getKeyInfoFactory() {
        return this.keyInfoFactory;
    }

    protected void initXmlProvider() {
        if (this.opcPackage == null) {
            this.opcPackage = this.signatureConfig.getOpcPackage();
        }
        if (this.provider == null) {
            this.provider = this.signatureConfig.getProvider();
            if (this.provider == null) {
                this.provider = XmlProviderInitSingleton.getInstance().findProvider();
            }
        }
        if (this.signatureFactory == null) {
            this.signatureFactory = this.signatureConfig.getSignatureFactory();
            if (this.signatureFactory == null) {
                this.signatureFactory = XMLSignatureFactory.getInstance("DOM", this.provider);
            }
        }
        if (this.keyInfoFactory == null) {
            this.keyInfoFactory = this.signatureConfig.getKeyInfoFactory();
            if (this.keyInfoFactory == null) {
                this.keyInfoFactory = KeyInfoFactory.getInstance("DOM", this.provider);
            }
        }
        if (this.uriDereferencer == null) {
            this.uriDereferencer = this.signatureConfig.getUriDereferencer();
            if (this.uriDereferencer == null) {
                this.uriDereferencer = new OOXMLURIDereferencer();
            }
        }
        if (this.uriDereferencer instanceof OOXMLURIDereferencer) {
            ((OOXMLURIDereferencer)this.uriDereferencer).setSignatureInfo(this);
        }
    }

    private static final class XmlProviderInitSingleton {
        public static XmlProviderInitSingleton getInstance() {
            return SingletonHelper.INSTANCE;
        }

        private XmlProviderInitSingleton() {
            try {
                Init.init();
                RelationshipTransformService.registerDsigProvider();
                CryptoFunctions.registerBouncyCastle();
            }
            catch (Exception e) {
                throw new RuntimeException("Xml & BouncyCastle-Provider initialization failed", e);
            }
        }

        public Provider findProvider() {
            return Stream.of(SignatureConfig.getProviderNames()).map(this::getProvider).filter(Objects::nonNull).findFirst().orElseThrow(this::providerNotFound);
        }

        private Provider getProvider(String className) {
            try {
                return (Provider)Class.forName(className).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            }
            catch (Exception e) {
                LOG.atDebug().log("XMLDsig-Provider '{}' can't be found - trying next.", (Object)className);
                return null;
            }
        }

        private RuntimeException providerNotFound() {
            return new RuntimeException("JRE doesn't support default xml signature provider - set jsr105Provider system property!");
        }

        private static class SingletonHelper {
            private static final XmlProviderInitSingleton INSTANCE = new XmlProviderInitSingleton();

            private SingletonHelper() {
            }
        }
    }

    private final class SignaturePartIterator
    implements Iterator<SignaturePart> {
        Iterator<PackageRelationship> sigOrigRels;
        private Iterator<PackageRelationship> sigRels;
        private PackagePart sigPart;

        private SignaturePartIterator() {
            this.sigOrigRels = SignatureInfo.this.opcPackage.getRelationshipsByType("http://schemas.openxmlformats.org/package/2006/relationships/digital-signature/origin").iterator();
        }

        @Override
        public boolean hasNext() {
            while (this.sigRels == null || !this.sigRels.hasNext()) {
                if (!this.sigOrigRels.hasNext()) {
                    return false;
                }
                this.sigPart = SignatureInfo.this.opcPackage.getPart(this.sigOrigRels.next());
                LOG.atDebug().log("Digital Signature Origin part: {}", (Object)this.sigPart);
                try {
                    this.sigRels = this.sigPart.getRelationshipsByType("http://schemas.openxmlformats.org/package/2006/relationships/digital-signature/signature").iterator();
                }
                catch (InvalidFormatException e) {
                    LOG.atWarn().withThrowable(e).log("Reference to signature is invalid.");
                }
            }
            return true;
        }

        @Override
        public SignaturePart next() {
            PackagePart sigRelPart = null;
            do {
                try {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    sigRelPart = this.sigPart.getRelatedPart(this.sigRels.next());
                    LOG.atDebug().log("XML Signature part: {}", (Object)sigRelPart);
                }
                catch (InvalidFormatException e) {
                    LOG.atWarn().withThrowable(e).log("Reference to signature is invalid.");
                }
            } while (sigRelPart == null);
            return new SignaturePart(sigRelPart, SignatureInfo.this);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

