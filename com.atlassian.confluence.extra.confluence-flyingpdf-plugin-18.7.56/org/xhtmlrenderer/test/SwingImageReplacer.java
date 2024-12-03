/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.test;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.w3c.dom.Element;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.swing.AWTFSImage;
import org.xhtmlrenderer.swing.EmptyReplacedElement;
import org.xhtmlrenderer.swing.ImageReplacedElement;
import org.xhtmlrenderer.test.ElementReplacer;
import org.xhtmlrenderer.util.ImageUtil;
import org.xhtmlrenderer.util.XRLog;

public class SwingImageReplacer
extends ElementReplacer {
    private final Map imageComponents = new HashMap();

    @Override
    public boolean isElementNameMatch() {
        return true;
    }

    @Override
    public String getElementNameMatch() {
        return "img";
    }

    @Override
    public boolean accept(LayoutContext context, Element element) {
        return context.getNamespaceHandler().isImageElement(element);
    }

    @Override
    public ReplacedElement replace(LayoutContext context, BlockBox box, UserAgentCallback uac, int cssWidth, int cssHeight) {
        return this.replaceImage(uac, context, box.getElement(), cssWidth, cssHeight);
    }

    @Override
    public void clear(Element element) {
        System.out.println("*** cleared image components for element " + element);
        this.imageComponents.remove(element);
    }

    @Override
    public void reset() {
        System.out.println("*** cleared image componentes");
        this.imageComponents.clear();
    }

    protected ReplacedElement replaceImage(UserAgentCallback uac, LayoutContext context, Element elem, int cssWidth, int cssHeight) {
        ReplacedElement re = null;
        re = this.lookupImageReplacedElement(elem);
        if (re == null) {
            BufferedImage im = null;
            String imageSrc = context.getNamespaceHandler().getImageSourceURI(elem);
            if (imageSrc == null || imageSrc.length() == 0) {
                XRLog.layout(Level.WARNING, "No source provided for img element.");
                re = this.newIrreplaceableImageElement(cssWidth, cssHeight);
            } else {
                FSImage fsImage = uac.getImageResource(imageSrc).getImage();
                if (fsImage != null) {
                    im = ((AWTFSImage)fsImage).getImage();
                }
                re = im != null ? new ImageReplacedElement(im, cssWidth, cssHeight) : this.newIrreplaceableImageElement(cssWidth, cssHeight);
            }
            this.storeImageReplacedElement(elem, re);
        }
        return re;
    }

    protected void storeImageReplacedElement(Element e, ReplacedElement cc) {
        System.out.println("\n*** Cached image for element");
        this.imageComponents.put(e, cc);
    }

    protected ReplacedElement lookupImageReplacedElement(Element e) {
        if (this.imageComponents.size() == 0) {
            return null;
        }
        ReplacedElement replacedElement = (ReplacedElement)this.imageComponents.get(e);
        return replacedElement;
    }

    protected ReplacedElement newIrreplaceableImageElement(int cssWidth, int cssHeight) {
        ReplacedElement mre;
        BufferedImage missingImage = null;
        try {
            missingImage = ImageUtil.createCompatibleBufferedImage(cssWidth, cssHeight, 1);
            Graphics2D g = missingImage.createGraphics();
            g.setColor(Color.BLACK);
            g.setBackground(Color.WHITE);
            g.setFont(new Font("Serif", 0, 12));
            g.drawString("Missing", 0, 12);
            g.dispose();
            mre = new ImageReplacedElement(missingImage, cssWidth, cssHeight);
        }
        catch (Exception e) {
            mre = new EmptyReplacedElement(cssWidth < 0 ? 0 : cssWidth, cssHeight < 0 ? 0 : cssHeight);
        }
        return mre;
    }
}

