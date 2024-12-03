/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.JaiI18N;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.OpImage;

public final class TranslateIntOpImage
extends OpImage {
    private int transX;
    private int transY;

    private static ImageLayout layoutHelper(RenderedImage source, int transX, int transY) {
        ImageLayout layout = new ImageLayout(source.getMinX() + transX, source.getMinY() + transY, source.getWidth(), source.getHeight(), source.getTileGridXOffset() + transX, source.getTileGridYOffset() + transY, source.getTileWidth(), source.getTileHeight(), source.getSampleModel(), source.getColorModel());
        return layout;
    }

    private static Map configHelper(Map configuration) {
        Map config;
        if (configuration == null) {
            config = new RenderingHints(JAI.KEY_REPLACE_INDEX_COLOR_MODEL, Boolean.FALSE);
        } else {
            config = configuration;
            if (!config.containsKey(JAI.KEY_REPLACE_INDEX_COLOR_MODEL)) {
                RenderingHints hints = (RenderingHints)configuration;
                config = (RenderingHints)hints.clone();
                config.put(JAI.KEY_REPLACE_INDEX_COLOR_MODEL, Boolean.FALSE);
                config.remove(JAI.KEY_TILE_CACHE);
            } else if (config.containsKey(JAI.KEY_TILE_CACHE)) {
                RenderingHints hints = (RenderingHints)configuration;
                config = (RenderingHints)hints.clone();
                config.remove(JAI.KEY_TILE_CACHE);
            }
        }
        return config;
    }

    public TranslateIntOpImage(RenderedImage source, Map config, int transX, int transY) {
        super(TranslateIntOpImage.vectorize(source), TranslateIntOpImage.layoutHelper(source, transX, transY), TranslateIntOpImage.configHelper(config), false);
        this.transX = transX;
        this.transY = transY;
    }

    public boolean computesUniqueTiles() {
        return false;
    }

    public Raster computeTile(int tileX, int tileY) {
        return this.getTile(tileX, tileY);
    }

    public Raster getTile(int tileX, int tileY) {
        Raster tile = this.getSource(0).getTile(tileX, tileY);
        if (tile == null) {
            return null;
        }
        return tile.createTranslatedChild(this.tileXToX(tileX), this.tileYToY(tileY));
    }

    public Rectangle mapSourceRect(Rectangle sourceRect, int sourceIndex) {
        if (sourceRect == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (sourceIndex < 0 || sourceIndex >= this.getNumSources()) {
            throw new IllegalArgumentException(JaiI18N.getString("TranslateIntOpImage0"));
        }
        Rectangle r = new Rectangle(sourceRect);
        r.translate(this.transX, this.transY);
        return r;
    }

    public Rectangle mapDestRect(Rectangle destRect, int sourceIndex) {
        if (destRect == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (sourceIndex < 0 || sourceIndex >= this.getNumSources()) {
            throw new IllegalArgumentException(JaiI18N.getString("TranslateIntOpImage0"));
        }
        Rectangle r = new Rectangle(destRect);
        r.translate(-this.transX, -this.transY);
        return r;
    }
}

