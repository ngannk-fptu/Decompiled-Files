/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.parser;

import java.awt.geom.AffineTransform;
import java.io.Reader;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.TransformListHandler;
import org.apache.batik.parser.TransformListParser;

public class AWTTransformProducer
implements TransformListHandler {
    protected AffineTransform affineTransform;

    public static AffineTransform createAffineTransform(Reader r) throws ParseException {
        TransformListParser p = new TransformListParser();
        AWTTransformProducer th = new AWTTransformProducer();
        p.setTransformListHandler(th);
        p.parse(r);
        return th.getAffineTransform();
    }

    public static AffineTransform createAffineTransform(String s) throws ParseException {
        TransformListParser p = new TransformListParser();
        AWTTransformProducer th = new AWTTransformProducer();
        p.setTransformListHandler(th);
        p.parse(s);
        return th.getAffineTransform();
    }

    public AffineTransform getAffineTransform() {
        return this.affineTransform;
    }

    @Override
    public void startTransformList() throws ParseException {
        this.affineTransform = new AffineTransform();
    }

    @Override
    public void matrix(float a, float b, float c, float d, float e, float f) throws ParseException {
        this.affineTransform.concatenate(new AffineTransform(a, b, c, d, e, f));
    }

    @Override
    public void rotate(float theta) throws ParseException {
        this.affineTransform.concatenate(AffineTransform.getRotateInstance(Math.toRadians(theta)));
    }

    @Override
    public void rotate(float theta, float cx, float cy) throws ParseException {
        AffineTransform at = AffineTransform.getRotateInstance(Math.toRadians(theta), cx, cy);
        this.affineTransform.concatenate(at);
    }

    @Override
    public void translate(float tx) throws ParseException {
        AffineTransform at = AffineTransform.getTranslateInstance(tx, 0.0);
        this.affineTransform.concatenate(at);
    }

    @Override
    public void translate(float tx, float ty) throws ParseException {
        AffineTransform at = AffineTransform.getTranslateInstance(tx, ty);
        this.affineTransform.concatenate(at);
    }

    @Override
    public void scale(float sx) throws ParseException {
        this.affineTransform.concatenate(AffineTransform.getScaleInstance(sx, sx));
    }

    @Override
    public void scale(float sx, float sy) throws ParseException {
        this.affineTransform.concatenate(AffineTransform.getScaleInstance(sx, sy));
    }

    @Override
    public void skewX(float skx) throws ParseException {
        this.affineTransform.concatenate(AffineTransform.getShearInstance(Math.tan(Math.toRadians(skx)), 0.0));
    }

    @Override
    public void skewY(float sky) throws ParseException {
        this.affineTransform.concatenate(AffineTransform.getShearInstance(0.0, Math.tan(Math.toRadians(sky))));
    }

    @Override
    public void endTransformList() throws ParseException {
    }
}

