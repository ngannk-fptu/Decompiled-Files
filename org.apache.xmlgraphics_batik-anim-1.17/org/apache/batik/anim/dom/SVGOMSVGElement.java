/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.dom.svg.AbstractSVGMatrix
 *  org.apache.batik.dom.svg.SVGContext
 *  org.apache.batik.dom.svg.SVGOMAngle
 *  org.apache.batik.dom.svg.SVGOMPoint
 *  org.apache.batik.dom.svg.SVGOMRect
 *  org.apache.batik.dom.svg.SVGOMTransform
 *  org.apache.batik.dom.svg.SVGSVGContext
 *  org.apache.batik.dom.svg.SVGTestsSupport
 *  org.apache.batik.dom.svg.SVGZoomAndPanSupport
 *  org.apache.batik.dom.util.ListNodeList
 *  org.apache.batik.dom.util.XMLSupport
 *  org.apache.batik.util.DoublyIndexedTable
 *  org.w3c.dom.svg.SVGAngle
 *  org.w3c.dom.svg.SVGAnimatedBoolean
 *  org.w3c.dom.svg.SVGAnimatedLength
 *  org.w3c.dom.svg.SVGAnimatedPreserveAspectRatio
 *  org.w3c.dom.svg.SVGAnimatedRect
 *  org.w3c.dom.svg.SVGElement
 *  org.w3c.dom.svg.SVGException
 *  org.w3c.dom.svg.SVGLength
 *  org.w3c.dom.svg.SVGMatrix
 *  org.w3c.dom.svg.SVGNumber
 *  org.w3c.dom.svg.SVGPoint
 *  org.w3c.dom.svg.SVGRect
 *  org.w3c.dom.svg.SVGSVGElement
 *  org.w3c.dom.svg.SVGStringList
 *  org.w3c.dom.svg.SVGTransform
 *  org.w3c.dom.svg.SVGViewSpec
 */
package org.apache.batik.anim.dom;

import java.awt.geom.AffineTransform;
import java.util.List;
import org.apache.batik.anim.dom.AttributeInitializer;
import org.apache.batik.anim.dom.SVGLocatableSupport;
import org.apache.batik.anim.dom.SVGOMAnimatedBoolean;
import org.apache.batik.anim.dom.SVGOMAnimatedLength;
import org.apache.batik.anim.dom.SVGOMAnimatedPreserveAspectRatio;
import org.apache.batik.anim.dom.SVGOMAnimatedRect;
import org.apache.batik.anim.dom.SVGOMLength;
import org.apache.batik.anim.dom.SVGStylableElement;
import org.apache.batik.anim.dom.TraitInformation;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.svg.AbstractSVGMatrix;
import org.apache.batik.dom.svg.SVGContext;
import org.apache.batik.dom.svg.SVGOMAngle;
import org.apache.batik.dom.svg.SVGOMPoint;
import org.apache.batik.dom.svg.SVGOMRect;
import org.apache.batik.dom.svg.SVGOMTransform;
import org.apache.batik.dom.svg.SVGSVGContext;
import org.apache.batik.dom.svg.SVGTestsSupport;
import org.apache.batik.dom.svg.SVGZoomAndPanSupport;
import org.apache.batik.dom.util.ListNodeList;
import org.apache.batik.dom.util.XMLSupport;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.DocumentCSS;
import org.w3c.dom.css.ViewCSS;
import org.w3c.dom.events.DocumentEvent;
import org.w3c.dom.events.Event;
import org.w3c.dom.stylesheets.DocumentStyle;
import org.w3c.dom.stylesheets.StyleSheetList;
import org.w3c.dom.svg.SVGAngle;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGAnimatedPreserveAspectRatio;
import org.w3c.dom.svg.SVGAnimatedRect;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGLength;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGNumber;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGSVGElement;
import org.w3c.dom.svg.SVGStringList;
import org.w3c.dom.svg.SVGTransform;
import org.w3c.dom.svg.SVGViewSpec;
import org.w3c.dom.views.AbstractView;
import org.w3c.dom.views.DocumentView;

public class SVGOMSVGElement
extends SVGStylableElement
implements SVGSVGElement {
    protected static DoublyIndexedTable xmlTraitInformation;
    protected static final AttributeInitializer attributeInitializer;
    protected SVGOMAnimatedLength x;
    protected SVGOMAnimatedLength y;
    protected SVGOMAnimatedLength width;
    protected SVGOMAnimatedLength height;
    protected SVGOMAnimatedBoolean externalResourcesRequired;
    protected SVGOMAnimatedPreserveAspectRatio preserveAspectRatio;
    protected SVGOMAnimatedRect viewBox;

    protected SVGOMSVGElement() {
    }

    public SVGOMSVGElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }

    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }

    private void initializeLiveAttributes() {
        this.x = this.createLiveAnimatedLength(null, "x", "0", (short)2, false);
        this.y = this.createLiveAnimatedLength(null, "y", "0", (short)1, false);
        this.width = this.createLiveAnimatedLength(null, "width", "100%", (short)2, true);
        this.height = this.createLiveAnimatedLength(null, "height", "100%", (short)1, true);
        this.externalResourcesRequired = this.createLiveAnimatedBoolean(null, "externalResourcesRequired", false);
        this.preserveAspectRatio = this.createLiveAnimatedPreserveAspectRatio();
        this.viewBox = this.createLiveAnimatedRect(null, "viewBox", null);
    }

    public String getLocalName() {
        return "svg";
    }

    public SVGAnimatedLength getX() {
        return this.x;
    }

    public SVGAnimatedLength getY() {
        return this.y;
    }

    public SVGAnimatedLength getWidth() {
        return this.width;
    }

    public SVGAnimatedLength getHeight() {
        return this.height;
    }

    public String getContentScriptType() {
        return this.getAttributeNS(null, "contentScriptType");
    }

    public void setContentScriptType(String type) {
        this.setAttributeNS(null, "contentScriptType", type);
    }

    public String getContentStyleType() {
        return this.getAttributeNS(null, "contentStyleType");
    }

    public void setContentStyleType(String type) {
        this.setAttributeNS(null, "contentStyleType", type);
    }

    public SVGRect getViewport() {
        SVGContext ctx = this.getSVGContext();
        return new SVGOMRect(0.0f, 0.0f, ctx.getViewportWidth(), ctx.getViewportHeight());
    }

    public float getPixelUnitToMillimeterX() {
        return this.getSVGContext().getPixelUnitToMillimeter();
    }

    public float getPixelUnitToMillimeterY() {
        return this.getSVGContext().getPixelUnitToMillimeter();
    }

    public float getScreenPixelToMillimeterX() {
        return this.getSVGContext().getPixelUnitToMillimeter();
    }

    public float getScreenPixelToMillimeterY() {
        return this.getSVGContext().getPixelUnitToMillimeter();
    }

    public boolean getUseCurrentView() {
        throw new UnsupportedOperationException("SVGSVGElement.getUseCurrentView is not implemented");
    }

    public void setUseCurrentView(boolean useCurrentView) throws DOMException {
        throw new UnsupportedOperationException("SVGSVGElement.setUseCurrentView is not implemented");
    }

    public SVGViewSpec getCurrentView() {
        throw new UnsupportedOperationException("SVGSVGElement.getCurrentView is not implemented");
    }

    public float getCurrentScale() {
        AffineTransform scrnTrans = this.getSVGContext().getScreenTransform();
        if (scrnTrans != null) {
            return (float)Math.sqrt(scrnTrans.getDeterminant());
        }
        return 1.0f;
    }

    public void setCurrentScale(float currentScale) throws DOMException {
        SVGContext context = this.getSVGContext();
        AffineTransform scrnTrans = context.getScreenTransform();
        float scale = 1.0f;
        if (scrnTrans != null) {
            scale = (float)Math.sqrt(scrnTrans.getDeterminant());
        }
        float delta = currentScale / scale;
        scrnTrans = new AffineTransform(scrnTrans.getScaleX() * (double)delta, scrnTrans.getShearY() * (double)delta, scrnTrans.getShearX() * (double)delta, scrnTrans.getScaleY() * (double)delta, scrnTrans.getTranslateX(), scrnTrans.getTranslateY());
        context.setScreenTransform(scrnTrans);
    }

    public SVGPoint getCurrentTranslate() {
        return new SVGPoint(){

            protected AffineTransform getScreenTransform() {
                SVGContext context = SVGOMSVGElement.this.getSVGContext();
                return context.getScreenTransform();
            }

            public float getX() {
                AffineTransform scrnTrans = this.getScreenTransform();
                return (float)scrnTrans.getTranslateX();
            }

            public float getY() {
                AffineTransform scrnTrans = this.getScreenTransform();
                return (float)scrnTrans.getTranslateY();
            }

            public void setX(float newX) {
                SVGContext context = SVGOMSVGElement.this.getSVGContext();
                AffineTransform scrnTrans = context.getScreenTransform();
                scrnTrans = new AffineTransform(scrnTrans.getScaleX(), scrnTrans.getShearY(), scrnTrans.getShearX(), scrnTrans.getScaleY(), (double)newX, scrnTrans.getTranslateY());
                context.setScreenTransform(scrnTrans);
            }

            public void setY(float newY) {
                SVGContext context = SVGOMSVGElement.this.getSVGContext();
                AffineTransform scrnTrans = context.getScreenTransform();
                scrnTrans = new AffineTransform(scrnTrans.getScaleX(), scrnTrans.getShearY(), scrnTrans.getShearX(), scrnTrans.getScaleY(), scrnTrans.getTranslateX(), (double)newY);
                context.setScreenTransform(scrnTrans);
            }

            public SVGPoint matrixTransform(SVGMatrix mat) {
                AffineTransform scrnTrans = this.getScreenTransform();
                float x = (float)scrnTrans.getTranslateX();
                float y = (float)scrnTrans.getTranslateY();
                float newX = mat.getA() * x + mat.getC() * y + mat.getE();
                float newY = mat.getB() * x + mat.getD() * y + mat.getF();
                return new SVGOMPoint(newX, newY);
            }
        };
    }

    public int suspendRedraw(int max_wait_milliseconds) {
        if (max_wait_milliseconds > 60000) {
            max_wait_milliseconds = 60000;
        } else if (max_wait_milliseconds < 0) {
            max_wait_milliseconds = 0;
        }
        SVGSVGContext ctx = (SVGSVGContext)this.getSVGContext();
        return ctx.suspendRedraw(max_wait_milliseconds);
    }

    public void unsuspendRedraw(int suspend_handle_id) throws DOMException {
        SVGSVGContext ctx = (SVGSVGContext)this.getSVGContext();
        if (!ctx.unsuspendRedraw(suspend_handle_id)) {
            throw this.createDOMException((short)8, "invalid.suspend.handle", new Object[]{suspend_handle_id});
        }
    }

    public void unsuspendRedrawAll() {
        SVGSVGContext ctx = (SVGSVGContext)this.getSVGContext();
        ctx.unsuspendRedrawAll();
    }

    public void forceRedraw() {
        SVGSVGContext ctx = (SVGSVGContext)this.getSVGContext();
        ctx.forceRedraw();
    }

    public void pauseAnimations() {
        SVGSVGContext ctx = (SVGSVGContext)this.getSVGContext();
        ctx.pauseAnimations();
    }

    public void unpauseAnimations() {
        SVGSVGContext ctx = (SVGSVGContext)this.getSVGContext();
        ctx.unpauseAnimations();
    }

    public boolean animationsPaused() {
        SVGSVGContext ctx = (SVGSVGContext)this.getSVGContext();
        return ctx.animationsPaused();
    }

    public float getCurrentTime() {
        SVGSVGContext ctx = (SVGSVGContext)this.getSVGContext();
        return ctx.getCurrentTime();
    }

    public void setCurrentTime(float seconds) {
        SVGSVGContext ctx = (SVGSVGContext)this.getSVGContext();
        ctx.setCurrentTime(seconds);
    }

    public NodeList getIntersectionList(SVGRect rect, SVGElement referenceElement) {
        SVGSVGContext ctx = (SVGSVGContext)this.getSVGContext();
        List list = ctx.getIntersectionList(rect, (Element)referenceElement);
        return new ListNodeList(list);
    }

    public NodeList getEnclosureList(SVGRect rect, SVGElement referenceElement) {
        SVGSVGContext ctx = (SVGSVGContext)this.getSVGContext();
        List list = ctx.getEnclosureList(rect, (Element)referenceElement);
        return new ListNodeList(list);
    }

    public boolean checkIntersection(SVGElement element, SVGRect rect) {
        SVGSVGContext ctx = (SVGSVGContext)this.getSVGContext();
        return ctx.checkIntersection((Element)element, rect);
    }

    public boolean checkEnclosure(SVGElement element, SVGRect rect) {
        SVGSVGContext ctx = (SVGSVGContext)this.getSVGContext();
        return ctx.checkEnclosure((Element)element, rect);
    }

    public void deselectAll() {
        ((SVGSVGContext)this.getSVGContext()).deselectAll();
    }

    public SVGNumber createSVGNumber() {
        return new SVGNumber(){
            protected float value;

            public float getValue() {
                return this.value;
            }

            public void setValue(float f) {
                this.value = f;
            }
        };
    }

    public SVGLength createSVGLength() {
        return new SVGOMLength(this);
    }

    public SVGAngle createSVGAngle() {
        return new SVGOMAngle();
    }

    public SVGPoint createSVGPoint() {
        return new SVGOMPoint(0.0f, 0.0f);
    }

    public SVGMatrix createSVGMatrix() {
        return new AbstractSVGMatrix(){
            protected AffineTransform at = new AffineTransform();

            protected AffineTransform getAffineTransform() {
                return this.at;
            }
        };
    }

    public SVGRect createSVGRect() {
        return new SVGOMRect(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public SVGTransform createSVGTransform() {
        SVGOMTransform ret = new SVGOMTransform();
        ret.setType((short)1);
        return ret;
    }

    public SVGTransform createSVGTransformFromMatrix(SVGMatrix matrix) {
        SVGOMTransform tr = new SVGOMTransform();
        tr.setMatrix(matrix);
        return tr;
    }

    public Element getElementById(String elementId) {
        return this.ownerDocument.getChildElementById((Node)((Object)this), elementId);
    }

    public SVGElement getNearestViewportElement() {
        return SVGLocatableSupport.getNearestViewportElement((Element)((Object)this));
    }

    public SVGElement getFarthestViewportElement() {
        return SVGLocatableSupport.getFarthestViewportElement((Element)((Object)this));
    }

    public SVGRect getBBox() {
        return SVGLocatableSupport.getBBox((Element)((Object)this));
    }

    public SVGMatrix getCTM() {
        return SVGLocatableSupport.getCTM((Element)((Object)this));
    }

    public SVGMatrix getScreenCTM() {
        return SVGLocatableSupport.getScreenCTM((Element)((Object)this));
    }

    public SVGMatrix getTransformToElement(SVGElement element) throws SVGException {
        return SVGLocatableSupport.getTransformToElement((Element)((Object)this), element);
    }

    public DocumentView getDocument() {
        return (DocumentView)((Object)this.getOwnerDocument());
    }

    public CSSStyleDeclaration getComputedStyle(Element elt, String pseudoElt) {
        AbstractView av = ((DocumentView)((Object)this.getOwnerDocument())).getDefaultView();
        return ((ViewCSS)av).getComputedStyle(elt, pseudoElt);
    }

    public Event createEvent(String eventType) throws DOMException {
        return ((DocumentEvent)((Object)this.getOwnerDocument())).createEvent(eventType);
    }

    public boolean canDispatch(String namespaceURI, String type) throws DOMException {
        AbstractDocument doc = (AbstractDocument)this.getOwnerDocument();
        return doc.canDispatch(namespaceURI, type);
    }

    public StyleSheetList getStyleSheets() {
        return ((DocumentStyle)((Object)this.getOwnerDocument())).getStyleSheets();
    }

    public CSSStyleDeclaration getOverrideStyle(Element elt, String pseudoElt) {
        return ((DocumentCSS)((Object)this.getOwnerDocument())).getOverrideStyle(elt, pseudoElt);
    }

    public String getXMLlang() {
        return XMLSupport.getXMLLang((Element)((Object)this));
    }

    public void setXMLlang(String lang) {
        this.setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:lang", lang);
    }

    public String getXMLspace() {
        return XMLSupport.getXMLSpace((Element)((Object)this));
    }

    public void setXMLspace(String space) {
        this.setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:space", space);
    }

    public short getZoomAndPan() {
        return SVGZoomAndPanSupport.getZoomAndPan((Element)((Object)this));
    }

    public void setZoomAndPan(short val) {
        SVGZoomAndPanSupport.setZoomAndPan((Element)((Object)this), (short)val);
    }

    public SVGAnimatedRect getViewBox() {
        return this.viewBox;
    }

    public SVGAnimatedPreserveAspectRatio getPreserveAspectRatio() {
        return this.preserveAspectRatio;
    }

    public SVGAnimatedBoolean getExternalResourcesRequired() {
        return this.externalResourcesRequired;
    }

    public SVGStringList getRequiredFeatures() {
        return SVGTestsSupport.getRequiredFeatures((Element)((Object)this));
    }

    public SVGStringList getRequiredExtensions() {
        return SVGTestsSupport.getRequiredExtensions((Element)((Object)this));
    }

    public SVGStringList getSystemLanguage() {
        return SVGTestsSupport.getSystemLanguage((Element)((Object)this));
    }

    public boolean hasExtension(String extension) {
        return SVGTestsSupport.hasExtension((Element)((Object)this), (String)extension);
    }

    @Override
    protected AttributeInitializer getAttributeInitializer() {
        return attributeInitializer;
    }

    protected Node newNode() {
        return new SVGOMSVGElement();
    }

    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return xmlTraitInformation;
    }

    static {
        DoublyIndexedTable t = new DoublyIndexedTable(SVGStylableElement.xmlTraitInformation);
        t.put(null, (Object)"x", (Object)new TraitInformation(true, 3, 1));
        t.put(null, (Object)"y", (Object)new TraitInformation(true, 3, 2));
        t.put(null, (Object)"width", (Object)new TraitInformation(true, 3, 1));
        t.put(null, (Object)"height", (Object)new TraitInformation(true, 3, 2));
        t.put(null, (Object)"preserveAspectRatio", (Object)new TraitInformation(true, 32));
        t.put(null, (Object)"viewBox", (Object)new TraitInformation(true, 50));
        t.put(null, (Object)"externalResourcesRequired", (Object)new TraitInformation(true, 49));
        xmlTraitInformation = t;
        attributeInitializer = new AttributeInitializer(7);
        attributeInitializer.addAttribute("http://www.w3.org/2000/xmlns/", null, "xmlns", "http://www.w3.org/2000/svg");
        attributeInitializer.addAttribute("http://www.w3.org/2000/xmlns/", "xmlns", "xlink", "http://www.w3.org/1999/xlink");
        attributeInitializer.addAttribute(null, null, "preserveAspectRatio", "xMidYMid meet");
        attributeInitializer.addAttribute(null, null, "zoomAndPan", "magnify");
        attributeInitializer.addAttribute(null, null, "version", "1.0");
        attributeInitializer.addAttribute(null, null, "contentScriptType", "text/ecmascript");
        attributeInitializer.addAttribute(null, null, "contentStyleType", "text/css");
    }
}

