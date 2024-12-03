/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.swing;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.JComponent;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.extend.ReplacedElementFactory;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.resource.ImageResource;
import org.xhtmlrenderer.simple.extend.DefaultFormSubmissionListener;
import org.xhtmlrenderer.simple.extend.FormSubmissionListener;
import org.xhtmlrenderer.simple.extend.XhtmlForm;
import org.xhtmlrenderer.simple.extend.form.FormField;
import org.xhtmlrenderer.swing.AWTFSImage;
import org.xhtmlrenderer.swing.DeferredImageReplacedElement;
import org.xhtmlrenderer.swing.EmptyReplacedElement;
import org.xhtmlrenderer.swing.ImageReplacedElement;
import org.xhtmlrenderer.swing.ImageResourceLoader;
import org.xhtmlrenderer.swing.RepaintListener;
import org.xhtmlrenderer.swing.SwingReplacedElement;
import org.xhtmlrenderer.util.ImageUtil;
import org.xhtmlrenderer.util.XRLog;

public class SwingReplacedElementFactory
implements ReplacedElementFactory {
    protected Map imageComponents;
    protected LinkedHashMap forms;
    private FormSubmissionListener formSubmissionListener;
    protected final RepaintListener repaintListener;
    private ImageResourceLoader imageResourceLoader;

    public SwingReplacedElementFactory() {
        this(ImageResourceLoader.NO_OP_REPAINT_LISTENER);
    }

    public SwingReplacedElementFactory(RepaintListener repaintListener) {
        this(repaintListener, new ImageResourceLoader());
    }

    public SwingReplacedElementFactory(RepaintListener listener, ImageResourceLoader irl) {
        this.repaintListener = listener;
        this.imageResourceLoader = irl;
        this.formSubmissionListener = new DefaultFormSubmissionListener();
    }

    @Override
    public ReplacedElement createReplacedElement(LayoutContext context, BlockBox box, UserAgentCallback uac, int cssWidth, int cssHeight) {
        FormField formField;
        Element e = box.getElement();
        if (e == null) {
            return null;
        }
        if (context.getNamespaceHandler().isImageElement(e)) {
            return this.replaceImage(uac, context, e, cssWidth, cssHeight);
        }
        Element parentForm = this.getParentForm(e, context);
        XhtmlForm form = this.getForm(parentForm);
        if (form == null) {
            form = new XhtmlForm(uac, parentForm, this.formSubmissionListener);
            this.addForm(parentForm, form);
        }
        if ((formField = form.addComponent(e, context, box)) == null) {
            return null;
        }
        JComponent cc = formField.getComponent();
        if (cc == null) {
            return new EmptyReplacedElement(0, 0);
        }
        SwingReplacedElement result = new SwingReplacedElement(cc);
        result.setIntrinsicSize(formField.getIntrinsicSize());
        if (context.isInteractive()) {
            ((Container)((Object)context.getCanvas())).add(cc);
        }
        return result;
    }

    protected ReplacedElement replaceImage(UserAgentCallback uac, LayoutContext context, Element elem, int cssWidth, int cssHeight) {
        ReplacedElement re = null;
        String imageSrc = context.getNamespaceHandler().getImageSourceURI(elem);
        if (imageSrc == null || imageSrc.length() == 0) {
            XRLog.layout(Level.WARNING, "No source provided for img element.");
            re = this.newIrreplaceableImageElement(cssWidth, cssHeight);
        } else if (ImageUtil.isEmbeddedBase64Image(imageSrc)) {
            BufferedImage image = ImageUtil.loadEmbeddedBase64Image(imageSrc);
            if (image != null) {
                re = new ImageReplacedElement(image, cssWidth, cssHeight);
            }
        } else {
            String ruri = uac.resolveURI(imageSrc);
            re = this.lookupImageReplacedElement(elem, ruri, cssWidth, cssHeight);
            if (re == null) {
                XRLog.load(Level.FINE, "Swing: Image " + ruri + " requested at  to " + cssWidth + ", " + cssHeight);
                ImageResource imageResource = this.imageResourceLoader.get(ruri, cssWidth, cssHeight);
                re = imageResource.isLoaded() ? new ImageReplacedElement(((AWTFSImage)imageResource.getImage()).getImage(), cssWidth, cssHeight) : new DeferredImageReplacedElement(imageResource, this.repaintListener, cssWidth, cssHeight);
                this.storeImageReplacedElement(elem, re, ruri, cssWidth, cssHeight);
            }
        }
        return re;
    }

    private ReplacedElement lookupImageReplacedElement(Element elem, String ruri, int cssWidth, int cssHeight) {
        if (this.imageComponents == null) {
            return null;
        }
        CacheKey key = new CacheKey(elem, ruri, cssWidth, cssHeight);
        return (ReplacedElement)this.imageComponents.get(key);
    }

    protected ReplacedElement newIrreplaceableImageElement(int cssWidth, int cssHeight) {
        ReplacedElement mre;
        try {
            BufferedImage missingImage = ImageUtil.createCompatibleBufferedImage(cssWidth, cssHeight, 1);
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

    protected void storeImageReplacedElement(Element e, ReplacedElement cc, String uri, int cssWidth, int cssHeight) {
        if (this.imageComponents == null) {
            this.imageComponents = new HashMap();
        }
        CacheKey key = new CacheKey(e, uri, cssWidth, cssHeight);
        this.imageComponents.put(key, cc);
    }

    protected ReplacedElement lookupImageReplacedElement(Element e, String uri) {
        return this.lookupImageReplacedElement(e, uri, -1, -1);
    }

    protected void addForm(Element e, XhtmlForm f) {
        if (this.forms == null) {
            this.forms = new LinkedHashMap();
        }
        this.forms.put(e, f);
    }

    protected XhtmlForm getForm(Element e) {
        if (this.forms == null) {
            return null;
        }
        return (XhtmlForm)this.forms.get(e);
    }

    protected Element getParentForm(Element e, LayoutContext context) {
        Node node = e;
        while ((node = node.getParentNode()).getNodeType() == 1 && !context.getNamespaceHandler().isFormElement((Element)node)) {
        }
        if (node.getNodeType() != 1) {
            return null;
        }
        return (Element)node;
    }

    @Override
    public void reset() {
        this.forms = null;
    }

    @Override
    public void remove(Element e) {
        if (this.forms != null) {
            this.forms.remove(e);
        }
        if (this.imageComponents != null) {
            this.imageComponents.remove(e);
        }
    }

    @Override
    public void setFormSubmissionListener(FormSubmissionListener fsl) {
        this.formSubmissionListener = fsl;
    }

    private static class CacheKey {
        final Element elem;
        final String uri;
        final int width;
        final int height;

        public CacheKey(Element elem, String uri, int width, int height) {
            this.uri = uri;
            this.width = width;
            this.height = height;
            this.elem = elem;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof CacheKey)) {
                return false;
            }
            CacheKey cacheKey = (CacheKey)o;
            if (this.height != cacheKey.height) {
                return false;
            }
            if (this.width != cacheKey.width) {
                return false;
            }
            if (!this.elem.equals(cacheKey.elem)) {
                return false;
            }
            return this.uri.equals(cacheKey.uri);
        }

        public int hashCode() {
            int result = this.elem.hashCode();
            result = 31 * result + this.uri.hashCode();
            result = 31 * result + this.width;
            result = 31 * result + this.height;
            return result;
        }
    }
}

