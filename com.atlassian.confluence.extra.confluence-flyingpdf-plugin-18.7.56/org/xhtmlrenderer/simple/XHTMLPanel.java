/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.simple;

import java.io.File;
import java.io.InputStream;
import org.w3c.dom.Document;
import org.xhtmlrenderer.extend.NamespaceHandler;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.simple.extend.FormSubmissionListener;
import org.xhtmlrenderer.simple.extend.XhtmlNamespaceHandler;
import org.xhtmlrenderer.swing.BasicPanel;
import org.xhtmlrenderer.swing.CursorListener;
import org.xhtmlrenderer.swing.HoverListener;
import org.xhtmlrenderer.swing.LinkListener;
import org.xhtmlrenderer.util.Configuration;

public class XHTMLPanel
extends BasicPanel {
    private static final long serialVersionUID = 1L;
    private float fontScalingFactor = 1.2f;
    private float minFontScale = 0.5f;
    private float maxFontScale = 3.0f;

    public XHTMLPanel() {
        this.setupListeners();
    }

    public XHTMLPanel(UserAgentCallback uac) {
        super(uac);
        this.setupListeners();
    }

    private void setupListeners() {
        if (Configuration.isTrue("xr.use.listeners", true)) {
            this.addMouseTrackingListener(new HoverListener());
            this.addMouseTrackingListener(new LinkListener());
            this.addMouseTrackingListener(new CursorListener());
            this.setFormSubmissionListener(new FormSubmissionListener(){

                @Override
                public void submit(String query) {
                    XHTMLPanel.this.setDocumentRelative(query);
                }
            });
        }
    }

    private void resetListeners() {
        if (Configuration.isTrue("xr.use.listeners", true)) {
            this.resetMouseTracker();
        }
    }

    @Override
    public void relayout() {
        this.sharedContext.flushFonts();
        super.relayout();
    }

    @Override
    public void setDocument(String uri) {
        this.setDocument(this.loadDocument(uri), uri);
    }

    public void setDocument(Document doc) {
        this.setDocument(doc, "");
    }

    @Override
    public void setDocument(Document doc, String url) {
        this.resetListeners();
        this.setDocument(doc, url, (NamespaceHandler)new XhtmlNamespaceHandler());
    }

    @Override
    public void setDocument(InputStream stream, String url) throws Exception {
        this.resetListeners();
        this.setDocument(stream, url, (NamespaceHandler)new XhtmlNamespaceHandler());
    }

    public void setDocument(File file) throws Exception {
        this.resetListeners();
        File parent = file.getAbsoluteFile().getParentFile();
        String parentURL = parent == null ? "" : parent.toURI().toURL().toExternalForm();
        this.setDocument(this.loadDocument(file.toURI().toURL().toExternalForm()), parentURL);
    }

    @Override
    public void setSharedContext(SharedContext ctx) {
        super.setSharedContext(ctx);
    }

    public void setFontScalingFactor(float scaling) {
        this.fontScalingFactor = scaling;
    }

    public void incrementFontSize() {
        this.scaleFont(this.fontScalingFactor);
    }

    public void resetFontSize() {
        SharedContext rc = this.getSharedContext();
        rc.getTextRenderer().setFontScale(1.0f);
        this.setDocument(this.getDocument());
    }

    public void decrementFontSize() {
        this.scaleFont(1.0f / this.fontScalingFactor);
    }

    private void scaleFont(float scaleBy) {
        SharedContext rc = this.getSharedContext();
        float fs = rc.getTextRenderer().getFontScale() * scaleBy;
        if (fs < this.minFontScale || fs > this.maxFontScale) {
            return;
        }
        rc.getTextRenderer().setFontScale(fs);
        this.setDocument(this.getDocument());
    }

    public float getMaxFontScale() {
        return this.maxFontScale;
    }

    public float getMinFontScale() {
        return this.minFontScale;
    }

    public void setMaxFontScale(float f) {
        this.maxFontScale = f;
    }

    public void setMinFontScale(float f) {
        this.minFontScale = f;
    }
}

