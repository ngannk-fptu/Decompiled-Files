/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.renderable;

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.Light;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.renderable.AbstractColorInterpolationRable;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.SpecularLightingRable;
import org.apache.batik.ext.awt.image.rendered.AffineRed;
import org.apache.batik.ext.awt.image.rendered.BumpMap;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.PadRed;
import org.apache.batik.ext.awt.image.rendered.SpecularLightingRed;

public class SpecularLightingRable8Bit
extends AbstractColorInterpolationRable
implements SpecularLightingRable {
    private double surfaceScale;
    private double ks;
    private double specularExponent;
    private Light light;
    private Rectangle2D litRegion;
    private float[] kernelUnitLength = null;

    public SpecularLightingRable8Bit(Filter src, Rectangle2D litRegion, Light light, double ks, double specularExponent, double surfaceScale, double[] kernelUnitLength) {
        super(src, null);
        this.setLight(light);
        this.setKs(ks);
        this.setSpecularExponent(specularExponent);
        this.setSurfaceScale(surfaceScale);
        this.setLitRegion(litRegion);
        this.setKernelUnitLength(kernelUnitLength);
    }

    @Override
    public Filter getSource() {
        return (Filter)this.getSources().get(0);
    }

    @Override
    public void setSource(Filter src) {
        this.init(src, null);
    }

    @Override
    public Rectangle2D getBounds2D() {
        return (Rectangle2D)this.litRegion.clone();
    }

    @Override
    public Rectangle2D getLitRegion() {
        return this.getBounds2D();
    }

    @Override
    public void setLitRegion(Rectangle2D litRegion) {
        this.touch();
        this.litRegion = litRegion;
    }

    @Override
    public Light getLight() {
        return this.light;
    }

    @Override
    public void setLight(Light light) {
        this.touch();
        this.light = light;
    }

    @Override
    public double getSurfaceScale() {
        return this.surfaceScale;
    }

    @Override
    public void setSurfaceScale(double surfaceScale) {
        this.touch();
        this.surfaceScale = surfaceScale;
    }

    @Override
    public double getKs() {
        return this.ks;
    }

    @Override
    public void setKs(double ks) {
        this.touch();
        this.ks = ks;
    }

    @Override
    public double getSpecularExponent() {
        return this.specularExponent;
    }

    @Override
    public void setSpecularExponent(double specularExponent) {
        this.touch();
        this.specularExponent = specularExponent;
    }

    @Override
    public double[] getKernelUnitLength() {
        if (this.kernelUnitLength == null) {
            return null;
        }
        double[] ret = new double[]{this.kernelUnitLength[0], this.kernelUnitLength[1]};
        return ret;
    }

    @Override
    public void setKernelUnitLength(double[] kernelUnitLength) {
        this.touch();
        if (kernelUnitLength == null) {
            this.kernelUnitLength = null;
            return;
        }
        if (this.kernelUnitLength == null) {
            this.kernelUnitLength = new float[2];
        }
        this.kernelUnitLength[0] = (float)kernelUnitLength[0];
        this.kernelUnitLength[1] = (float)kernelUnitLength[1];
    }

    @Override
    public RenderedImage createRendering(RenderContext rc) {
        Shape aoi = rc.getAreaOfInterest();
        if (aoi == null) {
            aoi = this.getBounds2D();
        }
        Rectangle2D aoiR = aoi.getBounds2D();
        Rectangle2D.intersect(aoiR, this.getBounds2D(), aoiR);
        AffineTransform at = rc.getTransform();
        Rectangle devRect = at.createTransformedShape(aoiR).getBounds();
        if (devRect.width == 0 || devRect.height == 0) {
            return null;
        }
        double sx = at.getScaleX();
        double sy = at.getScaleY();
        double shx = at.getShearX();
        double shy = at.getShearY();
        double tx = at.getTranslateX();
        double ty = at.getTranslateY();
        double scaleX = Math.sqrt(sx * sx + shy * shy);
        double scaleY = Math.sqrt(sy * sy + shx * shx);
        if (scaleX == 0.0 || scaleY == 0.0) {
            return null;
        }
        if (this.kernelUnitLength != null) {
            if (scaleX >= (double)(1.0f / this.kernelUnitLength[0])) {
                scaleX = 1.0f / this.kernelUnitLength[0];
            }
            if (scaleY >= (double)(1.0f / this.kernelUnitLength[1])) {
                scaleY = 1.0f / this.kernelUnitLength[1];
            }
        }
        AffineTransform scale = AffineTransform.getScaleInstance(scaleX, scaleY);
        devRect = scale.createTransformedShape(aoiR).getBounds();
        aoiR.setRect(aoiR.getX() - 2.0 / scaleX, aoiR.getY() - 2.0 / scaleY, aoiR.getWidth() + 4.0 / scaleX, aoiR.getHeight() + 4.0 / scaleY);
        rc = (RenderContext)rc.clone();
        rc.setAreaOfInterest(aoiR);
        rc.setTransform(scale);
        CachableRed cr = GraphicsUtil.wrap(this.getSource().createRendering(rc));
        BumpMap bumpMap = new BumpMap(cr, this.surfaceScale, scaleX, scaleY);
        cr = new SpecularLightingRed(this.ks, this.specularExponent, this.light, bumpMap, devRect, 1.0 / scaleX, 1.0 / scaleY, this.isColorSpaceLinear());
        AffineTransform shearAt = new AffineTransform(sx / scaleX, shy / scaleX, shx / scaleY, sy / scaleY, tx, ty);
        if (!shearAt.isIdentity()) {
            RenderingHints rh = rc.getRenderingHints();
            Rectangle padRect = new Rectangle(devRect.x - 1, devRect.y - 1, devRect.width + 2, devRect.height + 2);
            cr = new PadRed(cr, padRect, PadMode.REPLICATE, rh);
            cr = new AffineRed(cr, shearAt, rh);
        }
        return cr;
    }
}

