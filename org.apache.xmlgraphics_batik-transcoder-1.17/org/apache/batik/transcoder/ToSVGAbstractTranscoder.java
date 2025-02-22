/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.dom.SVGDOMImplementation
 *  org.apache.batik.svggen.SVGGraphics2D
 *  org.apache.batik.util.Platform
 *  org.apache.batik.util.SVGConstants
 */
package org.apache.batik.transcoder;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.transcoder.AbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.keys.BooleanKey;
import org.apache.batik.transcoder.keys.FloatKey;
import org.apache.batik.transcoder.keys.IntegerKey;
import org.apache.batik.util.Platform;
import org.apache.batik.util.SVGConstants;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.XMLFilter;

public abstract class ToSVGAbstractTranscoder
extends AbstractTranscoder
implements SVGConstants {
    public static float PIXEL_TO_MILLIMETERS = 25.4f / (float)Platform.getScreenResolution();
    public static float PIXEL_PER_INCH = Platform.getScreenResolution();
    public static final int TRANSCODER_ERROR_BASE = 65280;
    public static final int ERROR_NULL_INPUT = 65280;
    public static final int ERROR_INCOMPATIBLE_INPUT_TYPE = 65281;
    public static final int ERROR_INCOMPATIBLE_OUTPUT_TYPE = 65282;
    public static final TranscodingHints.Key KEY_WIDTH = new FloatKey();
    public static final TranscodingHints.Key KEY_HEIGHT = new FloatKey();
    public static final TranscodingHints.Key KEY_INPUT_WIDTH = new IntegerKey();
    public static final TranscodingHints.Key KEY_INPUT_HEIGHT = new IntegerKey();
    public static final TranscodingHints.Key KEY_XOFFSET = new IntegerKey();
    public static final TranscodingHints.Key KEY_YOFFSET = new IntegerKey();
    public static final TranscodingHints.Key KEY_ESCAPED = new BooleanKey();
    protected SVGGraphics2D svgGenerator;

    protected Document createDocument(TranscoderOutput output) {
        Document doc;
        if (output.getDocument() == null) {
            DOMImplementation domImpl = SVGDOMImplementation.getDOMImplementation();
            doc = domImpl.createDocument("http://www.w3.org/2000/svg", "svg", null);
        } else {
            doc = output.getDocument();
        }
        return doc;
    }

    public SVGGraphics2D getGraphics2D() {
        return this.svgGenerator;
    }

    protected void writeSVGToOutput(SVGGraphics2D svgGenerator, Element svgRoot, TranscoderOutput output) throws TranscoderException {
        block10: {
            Document doc = output.getDocument();
            if (doc != null) {
                return;
            }
            XMLFilter xmlFilter = output.getXMLFilter();
            if (xmlFilter != null) {
                this.handler.fatalError(new TranscoderException("65282"));
            }
            try {
                OutputStream os;
                boolean escaped = false;
                if (this.hints.containsKey(KEY_ESCAPED)) {
                    escaped = (Boolean)this.hints.get(KEY_ESCAPED);
                }
                if ((os = output.getOutputStream()) != null) {
                    svgGenerator.stream(svgRoot, (Writer)new OutputStreamWriter(os), false, escaped);
                    return;
                }
                Writer wr = output.getWriter();
                if (wr != null) {
                    svgGenerator.stream(svgRoot, wr, false, escaped);
                    return;
                }
                String uri = output.getURI();
                if (uri == null) break block10;
                try {
                    URL url = new URL(uri);
                    URLConnection urlCnx = url.openConnection();
                    os = urlCnx.getOutputStream();
                    svgGenerator.stream(svgRoot, (Writer)new OutputStreamWriter(os), false, escaped);
                    return;
                }
                catch (MalformedURLException e) {
                    this.handler.fatalError(new TranscoderException(e));
                }
                catch (IOException e) {
                    this.handler.fatalError(new TranscoderException(e));
                }
            }
            catch (IOException e) {
                throw new TranscoderException(e);
            }
        }
        throw new TranscoderException("65282");
    }
}

