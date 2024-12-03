/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.util.XLinkSupport
 *  org.apache.batik.ext.awt.color.NamedProfileCache
 *  org.apache.batik.util.ParsedURL
 *  org.apache.xmlgraphics.java2d.color.ICCColorSpaceWithIntent
 *  org.apache.xmlgraphics.java2d.color.RenderingIntent
 */
package org.apache.batik.bridge;

import java.awt.color.ICC_Profile;
import java.io.IOException;
import org.apache.batik.bridge.AbstractSVGBridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.ErrorConstants;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.ext.awt.color.NamedProfileCache;
import org.apache.batik.util.ParsedURL;
import org.apache.xmlgraphics.java2d.color.ICCColorSpaceWithIntent;
import org.apache.xmlgraphics.java2d.color.RenderingIntent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SVGColorProfileElementBridge
extends AbstractSVGBridge
implements ErrorConstants {
    public NamedProfileCache cache = new NamedProfileCache();

    @Override
    public String getLocalName() {
        return "color-profile";
    }

    public ICCColorSpaceWithIntent createICCColorSpaceWithIntent(BridgeContext ctx, Element paintedElement, String iccProfileName) {
        ICCColorSpaceWithIntent cs = this.cache.request(iccProfileName.toLowerCase());
        if (cs != null) {
            return cs;
        }
        Document doc = paintedElement.getOwnerDocument();
        NodeList list = doc.getElementsByTagNameNS("http://www.w3.org/2000/svg", "color-profile");
        int n = list.getLength();
        Node profile = null;
        for (int i = 0; i < n; ++i) {
            Element profileNode;
            String nameAttr;
            Node node = list.item(i);
            if (node.getNodeType() != 1 || !iccProfileName.equalsIgnoreCase(nameAttr = (profileNode = (Element)node).getAttributeNS(null, "name"))) continue;
            profile = profileNode;
        }
        if (profile == null) {
            return null;
        }
        String href = XLinkSupport.getXLinkHref(profile);
        ICC_Profile p = null;
        if (href != null) {
            ParsedURL purl;
            String baseURI = profile.getBaseURI();
            ParsedURL pDocURL = null;
            if (baseURI != null) {
                pDocURL = new ParsedURL(baseURI);
            }
            if (!(purl = new ParsedURL(pDocURL, href)).complete()) {
                BridgeException be = new BridgeException(ctx, paintedElement, "uri.malformed", new Object[]{href});
                ctx.getUserAgent().displayError(be);
                return null;
            }
            try {
                ctx.getUserAgent().checkLoadExternalResource(purl, pDocURL);
                p = ICC_Profile.getInstance(purl.openStream());
            }
            catch (IOException ioEx) {
                BridgeException be = new BridgeException(ctx, paintedElement, ioEx, "uri.io", new Object[]{href});
                ctx.getUserAgent().displayError(be);
                return null;
            }
            catch (SecurityException secEx) {
                BridgeException be = new BridgeException(ctx, paintedElement, secEx, "uri.unsecure", new Object[]{href});
                ctx.getUserAgent().displayError(be);
                return null;
            }
        }
        if (p == null) {
            return null;
        }
        RenderingIntent intent = SVGColorProfileElementBridge.convertIntent((Element)profile, ctx);
        cs = new ICCColorSpaceWithIntent(p, intent, href, iccProfileName);
        this.cache.put(iccProfileName.toLowerCase(), cs);
        return cs;
    }

    private static RenderingIntent convertIntent(Element profile, BridgeContext ctx) {
        String intent = profile.getAttributeNS(null, "rendering-intent");
        if (intent.length() == 0) {
            return RenderingIntent.AUTO;
        }
        if ("perceptual".equals(intent)) {
            return RenderingIntent.PERCEPTUAL;
        }
        if ("auto".equals(intent)) {
            return RenderingIntent.AUTO;
        }
        if ("relative-colorimetric".equals(intent)) {
            return RenderingIntent.RELATIVE_COLORIMETRIC;
        }
        if ("absolute-colorimetric".equals(intent)) {
            return RenderingIntent.ABSOLUTE_COLORIMETRIC;
        }
        if ("saturation".equals(intent)) {
            return RenderingIntent.SATURATION;
        }
        throw new BridgeException(ctx, profile, "attribute.malformed", new Object[]{"rendering-intent", intent});
    }
}

