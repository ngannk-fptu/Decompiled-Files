/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.dom.SAXSVGDocumentFactory
 *  org.apache.batik.anim.dom.SVGDOMImplementation
 *  org.apache.batik.anim.dom.SVGOMDocument
 *  org.apache.batik.bridge.BaseScriptingEnvironment
 *  org.apache.batik.bridge.BridgeContext
 *  org.apache.batik.bridge.BridgeException
 *  org.apache.batik.bridge.DefaultScriptSecurity
 *  org.apache.batik.bridge.ExternalResourceSecurity
 *  org.apache.batik.bridge.GVTBuilder
 *  org.apache.batik.bridge.NoLoadScriptSecurity
 *  org.apache.batik.bridge.RelaxedExternalResourceSecurity
 *  org.apache.batik.bridge.RelaxedScriptSecurity
 *  org.apache.batik.bridge.SVGUtilities
 *  org.apache.batik.bridge.ScriptSecurity
 *  org.apache.batik.bridge.UserAgent
 *  org.apache.batik.bridge.UserAgentAdapter
 *  org.apache.batik.bridge.ViewBox
 *  org.apache.batik.bridge.svg12.SVG12BridgeContext
 *  org.apache.batik.dom.util.DOMUtilities
 *  org.apache.batik.dom.util.DocumentFactory
 *  org.apache.batik.gvt.CanvasGraphicsNode
 *  org.apache.batik.gvt.CompositeGraphicsNode
 *  org.apache.batik.gvt.GraphicsNode
 *  org.apache.batik.util.ParsedURL
 *  org.w3c.dom.svg.SVGSVGElement
 */
package org.apache.batik.transcoder;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.bridge.BaseScriptingEnvironment;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.DefaultScriptSecurity;
import org.apache.batik.bridge.ExternalResourceSecurity;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.NoLoadScriptSecurity;
import org.apache.batik.bridge.RelaxedExternalResourceSecurity;
import org.apache.batik.bridge.RelaxedScriptSecurity;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.bridge.ScriptSecurity;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.bridge.ViewBox;
import org.apache.batik.bridge.svg12.SVG12BridgeContext;
import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.dom.util.DocumentFactory;
import org.apache.batik.gvt.CanvasGraphicsNode;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.XMLAbstractTranscoder;
import org.apache.batik.transcoder.keys.BooleanKey;
import org.apache.batik.transcoder.keys.FloatKey;
import org.apache.batik.transcoder.keys.LengthKey;
import org.apache.batik.transcoder.keys.Rectangle2DKey;
import org.apache.batik.transcoder.keys.StringKey;
import org.apache.batik.util.ParsedURL;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGSVGElement;

public abstract class SVGAbstractTranscoder
extends XMLAbstractTranscoder {
    public static final String DEFAULT_DEFAULT_FONT_FAMILY = "Arial, Helvetica, sans-serif";
    protected Rectangle2D curAOI;
    protected AffineTransform curTxf;
    protected GraphicsNode root;
    protected BridgeContext ctx;
    protected GVTBuilder builder;
    protected float width = 400.0f;
    protected float height = 400.0f;
    protected UserAgent userAgent = this.createUserAgent();
    public static final TranscodingHints.Key KEY_WIDTH = new LengthKey();
    public static final TranscodingHints.Key KEY_HEIGHT = new LengthKey();
    public static final TranscodingHints.Key KEY_MAX_WIDTH = new LengthKey();
    public static final TranscodingHints.Key KEY_MAX_HEIGHT = new LengthKey();
    public static final TranscodingHints.Key KEY_AOI = new Rectangle2DKey();
    public static final TranscodingHints.Key KEY_LANGUAGE = new StringKey();
    public static final TranscodingHints.Key KEY_MEDIA = new StringKey();
    public static final TranscodingHints.Key KEY_DEFAULT_FONT_FAMILY = new StringKey();
    public static final TranscodingHints.Key KEY_ALTERNATE_STYLESHEET = new StringKey();
    public static final TranscodingHints.Key KEY_USER_STYLESHEET_URI = new StringKey();
    public static final TranscodingHints.Key KEY_PIXEL_UNIT_TO_MILLIMETER;
    public static final TranscodingHints.Key KEY_PIXEL_TO_MM;
    public static final TranscodingHints.Key KEY_EXECUTE_ONLOAD;
    public static final TranscodingHints.Key KEY_SNAPSHOT_TIME;
    public static final TranscodingHints.Key KEY_ALLOWED_SCRIPT_TYPES;
    public static final String DEFAULT_ALLOWED_SCRIPT_TYPES = "text/ecmascript, application/ecmascript, text/javascript, application/javascript, application/java-archive";
    public static final TranscodingHints.Key KEY_CONSTRAIN_SCRIPT_ORIGIN;
    public static final TranscodingHints.Key KEY_ALLOW_EXTERNAL_RESOURCES;

    protected SVGAbstractTranscoder() {
        this.hints.put(KEY_DOCUMENT_ELEMENT_NAMESPACE_URI, "http://www.w3.org/2000/svg");
        this.hints.put(KEY_DOCUMENT_ELEMENT, "svg");
        this.hints.put(KEY_DOM_IMPLEMENTATION, SVGDOMImplementation.getDOMImplementation());
        this.hints.put(KEY_MEDIA, "screen");
        this.hints.put(KEY_DEFAULT_FONT_FAMILY, DEFAULT_DEFAULT_FONT_FAMILY);
        this.hints.put(KEY_EXECUTE_ONLOAD, Boolean.FALSE);
        this.hints.put(KEY_ALLOWED_SCRIPT_TYPES, DEFAULT_ALLOWED_SCRIPT_TYPES);
    }

    protected UserAgent createUserAgent() {
        return new SVGAbstractTranscoderUserAgent();
    }

    @Override
    protected DocumentFactory createDocumentFactory(DOMImplementation domImpl, String parserClassname) {
        return new SAXSVGDocumentFactory(parserClassname);
    }

    @Override
    public void transcode(TranscoderInput input, TranscoderOutput output) throws TranscoderException {
        super.transcode(input, output);
        if (this.ctx != null) {
            this.ctx.dispose();
        }
    }

    @Override
    protected void transcode(Document document, String uri, TranscoderOutput output) throws TranscoderException {
        AffineTransform Px;
        GraphicsNode gvtRoot;
        if (document != null && !(document.getImplementation() instanceof SVGDOMImplementation)) {
            DOMImplementation impl = (DOMImplementation)this.hints.get(KEY_DOM_IMPLEMENTATION);
            document = DOMUtilities.deepCloneDocument((Document)document, (DOMImplementation)impl);
            if (uri != null) {
                ParsedURL url = new ParsedURL(uri);
                ((SVGOMDocument)document).setParsedURL(url);
            }
        }
        if (this.hints.containsKey(KEY_WIDTH)) {
            this.width = ((Float)this.hints.get(KEY_WIDTH)).floatValue();
        }
        if (this.hints.containsKey(KEY_HEIGHT)) {
            this.height = ((Float)this.hints.get(KEY_HEIGHT)).floatValue();
        }
        SVGOMDocument svgDoc = (SVGOMDocument)document;
        SVGSVGElement root = svgDoc.getRootElement();
        this.ctx = this.createBridgeContext(svgDoc);
        this.builder = new GVTBuilder();
        boolean isDynamic = this.hints.containsKey(KEY_EXECUTE_ONLOAD) && (Boolean)this.hints.get(KEY_EXECUTE_ONLOAD) != false;
        try {
            if (isDynamic) {
                this.ctx.setDynamicState(2);
            }
            gvtRoot = this.builder.build(this.ctx, (Document)svgDoc);
            if (this.ctx.isDynamic()) {
                float t;
                BaseScriptingEnvironment se = new BaseScriptingEnvironment(this.ctx);
                se.loadScripts();
                se.dispatchSVGLoadEvent();
                if (this.hints.containsKey(KEY_SNAPSHOT_TIME)) {
                    t = ((Float)this.hints.get(KEY_SNAPSHOT_TIME)).floatValue();
                    this.ctx.getAnimationEngine().setCurrentTime(t);
                } else if (this.ctx.isSVG12()) {
                    t = SVGUtilities.convertSnapshotTime((Element)root, null);
                    this.ctx.getAnimationEngine().setCurrentTime(t);
                }
            }
        }
        catch (BridgeException ex) {
            throw new TranscoderException((Exception)((Object)ex));
        }
        float docWidth = (float)this.ctx.getDocumentSize().getWidth();
        float docHeight = (float)this.ctx.getDocumentSize().getHeight();
        this.setImageSize(docWidth, docHeight);
        if (this.hints.containsKey(KEY_AOI)) {
            Rectangle2D aoi = (Rectangle2D)this.hints.get(KEY_AOI);
            Px = new AffineTransform();
            double sx = (double)this.width / aoi.getWidth();
            double sy = (double)this.height / aoi.getHeight();
            double scale = Math.min(sx, sy);
            Px.scale(scale, scale);
            double tx = -aoi.getX() + ((double)this.width / scale - aoi.getWidth()) / 2.0;
            double ty = -aoi.getY() + ((double)this.height / scale - aoi.getHeight()) / 2.0;
            Px.translate(tx, ty);
            this.curAOI = aoi;
        } else {
            String ref = new ParsedURL(uri).getRef();
            String viewBox = root.getAttributeNS(null, "viewBox");
            if (ref != null && ref.length() != 0) {
                Px = ViewBox.getViewTransform((String)ref, (Element)root, (float)this.width, (float)this.height, (BridgeContext)this.ctx);
            } else if (viewBox != null && viewBox.length() != 0) {
                String aspectRatio = root.getAttributeNS(null, "preserveAspectRatio");
                Px = ViewBox.getPreserveAspectRatioTransform((Element)root, (String)viewBox, (String)aspectRatio, (float)this.width, (float)this.height, (BridgeContext)this.ctx);
            } else {
                float xscale = this.width / docWidth;
                float yscale = this.height / docHeight;
                float scale = Math.min(xscale, yscale);
                Px = AffineTransform.getScaleInstance(scale, scale);
            }
            this.curAOI = new Rectangle2D.Float(0.0f, 0.0f, this.width, this.height);
        }
        CanvasGraphicsNode cgn = this.getCanvasGraphicsNode(gvtRoot);
        if (cgn != null) {
            cgn.setViewingTransform(Px);
            this.curTxf = new AffineTransform();
        } else {
            this.curTxf = Px;
        }
        this.root = gvtRoot;
    }

    protected CanvasGraphicsNode getCanvasGraphicsNode(GraphicsNode gn) {
        if (!(gn instanceof CompositeGraphicsNode)) {
            return null;
        }
        CompositeGraphicsNode cgn = (CompositeGraphicsNode)gn;
        List children = cgn.getChildren();
        if (children.size() == 0) {
            return null;
        }
        gn = (GraphicsNode)children.get(0);
        if (!(gn instanceof CanvasGraphicsNode)) {
            return null;
        }
        return (CanvasGraphicsNode)gn;
    }

    protected BridgeContext createBridgeContext(SVGOMDocument doc) {
        return this.createBridgeContext(doc.isSVG12() ? "1.2" : "1.x");
    }

    protected BridgeContext createBridgeContext() {
        return this.createBridgeContext("1.x");
    }

    protected BridgeContext createBridgeContext(String svgVersion) {
        if ("1.2".equals(svgVersion)) {
            return new SVG12BridgeContext(this.userAgent);
        }
        return new BridgeContext(this.userAgent);
    }

    protected void setImageSize(float docWidth, float docHeight) {
        float imgWidth = -1.0f;
        if (this.hints.containsKey(KEY_WIDTH)) {
            imgWidth = ((Float)this.hints.get(KEY_WIDTH)).floatValue();
        }
        float imgHeight = -1.0f;
        if (this.hints.containsKey(KEY_HEIGHT)) {
            imgHeight = ((Float)this.hints.get(KEY_HEIGHT)).floatValue();
        }
        if (imgWidth > 0.0f && imgHeight > 0.0f) {
            this.width = imgWidth;
            this.height = imgHeight;
        } else if (imgHeight > 0.0f) {
            this.width = docWidth * imgHeight / docHeight;
            this.height = imgHeight;
        } else if (imgWidth > 0.0f) {
            this.width = imgWidth;
            this.height = docHeight * imgWidth / docWidth;
        } else {
            this.width = docWidth;
            this.height = docHeight;
        }
        float imgMaxWidth = -1.0f;
        if (this.hints.containsKey(KEY_MAX_WIDTH)) {
            imgMaxWidth = ((Float)this.hints.get(KEY_MAX_WIDTH)).floatValue();
        }
        float imgMaxHeight = -1.0f;
        if (this.hints.containsKey(KEY_MAX_HEIGHT)) {
            imgMaxHeight = ((Float)this.hints.get(KEY_MAX_HEIGHT)).floatValue();
        }
        if (imgMaxHeight > 0.0f && this.height > imgMaxHeight) {
            this.width = docWidth * imgMaxHeight / docHeight;
            this.height = imgMaxHeight;
        }
        if (imgMaxWidth > 0.0f && this.width > imgMaxWidth) {
            this.width = imgMaxWidth;
            this.height = docHeight * imgMaxWidth / docWidth;
        }
    }

    static {
        KEY_PIXEL_TO_MM = KEY_PIXEL_UNIT_TO_MILLIMETER = new FloatKey();
        KEY_EXECUTE_ONLOAD = new BooleanKey();
        KEY_SNAPSHOT_TIME = new FloatKey();
        KEY_ALLOWED_SCRIPT_TYPES = new StringKey();
        KEY_CONSTRAIN_SCRIPT_ORIGIN = new BooleanKey();
        KEY_ALLOW_EXTERNAL_RESOURCES = new BooleanKey();
    }

    protected class SVGAbstractTranscoderUserAgent
    extends UserAgentAdapter {
        protected List scripts;

        public SVGAbstractTranscoderUserAgent() {
            this.addStdFeatures();
        }

        public AffineTransform getTransform() {
            return SVGAbstractTranscoder.this.curTxf;
        }

        public void setTransform(AffineTransform at) {
            SVGAbstractTranscoder.this.curTxf = at;
        }

        public Dimension2D getViewportSize() {
            return new Dimension((int)SVGAbstractTranscoder.this.width, (int)SVGAbstractTranscoder.this.height);
        }

        public void displayError(String message) {
            try {
                SVGAbstractTranscoder.this.handler.error(new TranscoderException(message));
            }
            catch (TranscoderException ex) {
                throw new RuntimeException(ex.getMessage());
            }
        }

        public void displayError(Exception e) {
            try {
                e.printStackTrace();
                SVGAbstractTranscoder.this.handler.error(new TranscoderException(e));
            }
            catch (TranscoderException ex) {
                throw new RuntimeException(ex.getMessage());
            }
        }

        public void displayMessage(String message) {
            try {
                SVGAbstractTranscoder.this.handler.warning(new TranscoderException(message));
            }
            catch (TranscoderException ex) {
                throw new RuntimeException(ex.getMessage());
            }
        }

        public float getPixelUnitToMillimeter() {
            Object obj = SVGAbstractTranscoder.this.hints.get(KEY_PIXEL_UNIT_TO_MILLIMETER);
            if (obj != null) {
                return ((Float)obj).floatValue();
            }
            return super.getPixelUnitToMillimeter();
        }

        public String getLanguages() {
            if (SVGAbstractTranscoder.this.hints.containsKey(KEY_LANGUAGE)) {
                return (String)SVGAbstractTranscoder.this.hints.get(KEY_LANGUAGE);
            }
            return super.getLanguages();
        }

        public String getMedia() {
            String s = (String)SVGAbstractTranscoder.this.hints.get(KEY_MEDIA);
            if (s != null) {
                return s;
            }
            return super.getMedia();
        }

        public String getDefaultFontFamily() {
            String s = (String)SVGAbstractTranscoder.this.hints.get(KEY_DEFAULT_FONT_FAMILY);
            if (s != null) {
                return s;
            }
            return super.getDefaultFontFamily();
        }

        public String getAlternateStyleSheet() {
            String s = (String)SVGAbstractTranscoder.this.hints.get(KEY_ALTERNATE_STYLESHEET);
            if (s != null) {
                return s;
            }
            return super.getAlternateStyleSheet();
        }

        public String getUserStyleSheetURI() {
            String s = (String)SVGAbstractTranscoder.this.hints.get(KEY_USER_STYLESHEET_URI);
            if (s != null) {
                return s;
            }
            return super.getUserStyleSheetURI();
        }

        public String getXMLParserClassName() {
            String s = (String)SVGAbstractTranscoder.this.hints.get(XMLAbstractTranscoder.KEY_XML_PARSER_CLASSNAME);
            if (s != null) {
                return s;
            }
            return super.getXMLParserClassName();
        }

        public boolean isXMLParserValidating() {
            Boolean b = (Boolean)SVGAbstractTranscoder.this.hints.get(XMLAbstractTranscoder.KEY_XML_PARSER_VALIDATING);
            if (b != null) {
                return b;
            }
            return super.isXMLParserValidating();
        }

        public ScriptSecurity getScriptSecurity(String scriptType, ParsedURL scriptPURL, ParsedURL docPURL) {
            if (this.scripts == null) {
                this.computeAllowedScripts();
            }
            if (!this.scripts.contains(scriptType)) {
                return new NoLoadScriptSecurity(scriptType);
            }
            boolean constrainOrigin = true;
            if (SVGAbstractTranscoder.this.hints.containsKey(KEY_CONSTRAIN_SCRIPT_ORIGIN)) {
                constrainOrigin = (Boolean)SVGAbstractTranscoder.this.hints.get(KEY_CONSTRAIN_SCRIPT_ORIGIN);
            }
            if (constrainOrigin) {
                return new DefaultScriptSecurity(scriptType, scriptPURL, docPURL);
            }
            return new RelaxedScriptSecurity(scriptType, scriptPURL, docPURL);
        }

        protected void computeAllowedScripts() {
            this.scripts = new LinkedList();
            if (!SVGAbstractTranscoder.this.hints.containsKey(KEY_ALLOWED_SCRIPT_TYPES)) {
                return;
            }
            String allowedScripts = (String)SVGAbstractTranscoder.this.hints.get(KEY_ALLOWED_SCRIPT_TYPES);
            StringTokenizer st = new StringTokenizer(allowedScripts, ",");
            while (st.hasMoreTokens()) {
                this.scripts.add(st.nextToken());
            }
        }

        public ExternalResourceSecurity getExternalResourceSecurity(ParsedURL resourceURL, ParsedURL docURL) {
            if (this.isAllowExternalResources()) {
                return new RelaxedExternalResourceSecurity(resourceURL, docURL);
            }
            return super.getExternalResourceSecurity(resourceURL, docURL);
        }

        public boolean isAllowExternalResources() {
            Boolean b = (Boolean)SVGAbstractTranscoder.this.hints.get(KEY_ALLOW_EXTERNAL_RESOURCES);
            if (b != null) {
                return b;
            }
            return false;
        }
    }
}

