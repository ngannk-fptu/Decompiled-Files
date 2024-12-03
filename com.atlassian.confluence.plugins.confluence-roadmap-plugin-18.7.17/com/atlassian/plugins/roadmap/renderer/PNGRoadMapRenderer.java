/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.binary.Base64
 */
package com.atlassian.plugins.roadmap.renderer;

import com.atlassian.plugins.roadmap.models.TimelinePlanner;
import com.atlassian.plugins.roadmap.renderer.AbstractTimelinePlannerRenderer;
import com.atlassian.plugins.roadmap.renderer.RenderedImageInfoEnricher;
import com.atlassian.plugins.roadmap.renderer.enricher.NoopInfoEnricher;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;
import javax.imageio.ImageIO;
import org.apache.commons.codec.binary.Base64;

public class PNGRoadMapRenderer
extends AbstractTimelinePlannerRenderer {
    private BufferedImage img;
    private Graphics2D graphics2D;

    public BufferedImage renderAsImage(TimelinePlanner roadmap, Optional<Integer> widthOption, Optional<Integer> heightOption, boolean isPlaceHolder) throws IOException {
        this.drawImage(roadmap, widthOption, heightOption, isPlaceHolder);
        this.graphics2D.dispose();
        this.graphics2D = null;
        BufferedImage ret = this.img;
        this.img = null;
        return ret;
    }

    public byte[] renderAsBytes(TimelinePlanner roadmap) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ImageIO.write((RenderedImage)this.renderAsImage(roadmap, Optional.empty(), Optional.empty(), false), "png", buffer);
        return buffer.toByteArray();
    }

    public String renderAsBase64(TimelinePlanner roadmap) throws IOException {
        return Base64.encodeBase64String((byte[])this.renderAsBytes(roadmap));
    }

    @Override
    protected Graphics2D createDummyGraphics2D() {
        BufferedImage img = new BufferedImage(1, 1, 2);
        return img.createGraphics();
    }

    @Override
    protected Graphics2D createGraphics2D(int width, int height) {
        this.img = new BufferedImage(width, height, 2);
        this.graphics2D = this.img.createGraphics();
        return this.graphics2D;
    }

    @Override
    protected RenderedImageInfoEnricher createEnricher() {
        return new NoopInfoEnricher();
    }
}

