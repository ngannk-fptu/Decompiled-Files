/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.render;

import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.render.LineBox;
import org.xhtmlrenderer.render.StrutMetrics;

public class MarkerData {
    private StrutMetrics _structMetrics;
    private TextMarker _textMarker;
    private GlyphMarker _glyphMarker;
    private ImageMarker _imageMarker;
    private LineBox _referenceLine;
    private LineBox _previousReferenceLine;

    public TextMarker getTextMarker() {
        return this._textMarker;
    }

    public void setTextMarker(TextMarker markerText) {
        this._textMarker = markerText;
    }

    public GlyphMarker getGlyphMarker() {
        return this._glyphMarker;
    }

    public void setGlyphMarker(GlyphMarker glyphMarker) {
        this._glyphMarker = glyphMarker;
    }

    public ImageMarker getImageMarker() {
        return this._imageMarker;
    }

    public void setImageMarker(ImageMarker imageMarker) {
        this._imageMarker = imageMarker;
    }

    public StrutMetrics getStructMetrics() {
        return this._structMetrics;
    }

    public void setStructMetrics(StrutMetrics structMetrics) {
        this._structMetrics = structMetrics;
    }

    public int getLayoutWidth() {
        if (this._textMarker != null) {
            return this._textMarker.getLayoutWidth();
        }
        if (this._glyphMarker != null) {
            return this._glyphMarker.getLayoutWidth();
        }
        if (this._imageMarker != null) {
            return this._imageMarker.getLayoutWidth();
        }
        return 0;
    }

    public LineBox getReferenceLine() {
        return this._referenceLine;
    }

    public void setReferenceLine(LineBox referenceLine) {
        this._previousReferenceLine = this._referenceLine;
        this._referenceLine = referenceLine;
    }

    public void restorePreviousReferenceLine(LineBox current) {
        if (current == this._referenceLine) {
            this._referenceLine = this._previousReferenceLine;
        }
    }

    public static class TextMarker {
        private String _text;
        private int _layoutWidth;

        public String getText() {
            return this._text;
        }

        public void setText(String text) {
            this._text = text;
        }

        public int getLayoutWidth() {
            return this._layoutWidth;
        }

        public void setLayoutWidth(int width) {
            this._layoutWidth = width;
        }
    }

    public static class GlyphMarker {
        private int _diameter;
        private int _layoutWidth;

        public int getDiameter() {
            return this._diameter;
        }

        public void setDiameter(int diameter) {
            this._diameter = diameter;
        }

        public int getLayoutWidth() {
            return this._layoutWidth;
        }

        public void setLayoutWidth(int layoutWidth) {
            this._layoutWidth = layoutWidth;
        }
    }

    public static class ImageMarker {
        private int _layoutWidth;
        private FSImage _image;

        public FSImage getImage() {
            return this._image;
        }

        public void setImage(FSImage image) {
            this._image = image;
        }

        public int getLayoutWidth() {
            return this._layoutWidth;
        }

        public void setLayoutWidth(int layoutWidth) {
            this._layoutWidth = layoutWidth;
        }
    }
}

