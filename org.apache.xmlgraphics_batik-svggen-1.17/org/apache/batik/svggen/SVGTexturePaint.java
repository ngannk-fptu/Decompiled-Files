/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.g2d.GraphicContext
 */
package org.apache.batik.svggen;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import org.apache.batik.ext.awt.g2d.GraphicContext;
import org.apache.batik.svggen.AbstractSVGConverter;
import org.apache.batik.svggen.SVGDescriptor;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGPaintDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SVGTexturePaint
extends AbstractSVGConverter {
    public SVGTexturePaint(SVGGeneratorContext generatorContext) {
        super(generatorContext);
    }

    @Override
    public SVGDescriptor toSVG(GraphicContext gc) {
        return this.toSVG((TexturePaint)gc.getPaint());
    }

    public SVGPaintDescriptor toSVG(TexturePaint texture) {
        SVGPaintDescriptor patternDesc = (SVGPaintDescriptor)this.descMap.get(texture);
        Document domFactory = this.generatorContext.domFactory;
        if (patternDesc == null) {
            Rectangle2D anchorRect = texture.getAnchorRect();
            Element patternDef = domFactory.createElementNS("http://www.w3.org/2000/svg", "pattern");
            patternDef.setAttributeNS(null, "patternUnits", "userSpaceOnUse");
            patternDef.setAttributeNS(null, "x", this.doubleString(anchorRect.getX()));
            patternDef.setAttributeNS(null, "y", this.doubleString(anchorRect.getY()));
            patternDef.setAttributeNS(null, "width", this.doubleString(anchorRect.getWidth()));
            patternDef.setAttributeNS(null, "height", this.doubleString(anchorRect.getHeight()));
            BufferedImage textureImage = texture.getImage();
            if (textureImage.getWidth() > 0 && textureImage.getHeight() > 0 && ((double)textureImage.getWidth() != anchorRect.getWidth() || (double)textureImage.getHeight() != anchorRect.getHeight()) && anchorRect.getWidth() > 0.0 && anchorRect.getHeight() > 0.0) {
                double scaleX = anchorRect.getWidth() / (double)textureImage.getWidth();
                double scaleY = anchorRect.getHeight() / (double)textureImage.getHeight();
                BufferedImage newImage = new BufferedImage((int)(scaleX * (double)textureImage.getWidth()), (int)(scaleY * (double)textureImage.getHeight()), 2);
                Graphics2D g = newImage.createGraphics();
                g.scale(scaleX, scaleY);
                g.drawImage((Image)textureImage, 0, 0, null);
                g.dispose();
                textureImage = newImage;
            }
            Element patternContent = this.generatorContext.genericImageHandler.createElement(this.generatorContext);
            this.generatorContext.genericImageHandler.handleImage(textureImage, patternContent, 0, 0, textureImage.getWidth(), textureImage.getHeight(), this.generatorContext);
            patternDef.appendChild(patternContent);
            patternDef.setAttributeNS(null, "id", this.generatorContext.idGenerator.generateID("pattern"));
            String patternAttrBuf = "url(#" + patternDef.getAttributeNS(null, "id") + ")";
            patternDesc = new SVGPaintDescriptor(patternAttrBuf, "1", patternDef);
            this.descMap.put(texture, patternDesc);
            this.defSet.add(patternDef);
        }
        return patternDesc;
    }
}

