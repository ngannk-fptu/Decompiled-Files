/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.svg.AbstractSVGTransform
 *  org.apache.batik.dom.svg.SVGOMTransform
 *  org.w3c.dom.svg.SVGMatrix
 */
package org.apache.batik.anim.values;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.apache.batik.anim.dom.AnimationTarget;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.dom.svg.AbstractSVGTransform;
import org.apache.batik.dom.svg.SVGOMTransform;
import org.w3c.dom.svg.SVGMatrix;

public class AnimatableTransformListValue
extends AnimatableValue {
    protected static SVGOMTransform IDENTITY_SKEWX = new SVGOMTransform();
    protected static SVGOMTransform IDENTITY_SKEWY = new SVGOMTransform();
    protected static SVGOMTransform IDENTITY_SCALE = new SVGOMTransform();
    protected static SVGOMTransform IDENTITY_ROTATE = new SVGOMTransform();
    protected static SVGOMTransform IDENTITY_TRANSLATE = new SVGOMTransform();
    protected Vector transforms;

    protected AnimatableTransformListValue(AnimationTarget target) {
        super(target);
    }

    public AnimatableTransformListValue(AnimationTarget target, AbstractSVGTransform t) {
        super(target);
        this.transforms = new Vector();
        this.transforms.add(t);
    }

    public AnimatableTransformListValue(AnimationTarget target, List transforms) {
        super(target);
        this.transforms = new Vector(transforms);
    }

    @Override
    public AnimatableValue interpolate(AnimatableValue result, AnimatableValue to, float interpolation, AnimatableValue accumulation, int multiplier) {
        AnimatableTransformListValue res;
        AnimatableTransformListValue toTransformList = (AnimatableTransformListValue)to;
        AnimatableTransformListValue accTransformList = (AnimatableTransformListValue)accumulation;
        int accSize = accumulation == null ? 0 : accTransformList.transforms.size();
        int newSize = this.transforms.size() + accSize * multiplier;
        if (result == null) {
            res = new AnimatableTransformListValue(this.target);
            res.transforms = new Vector(newSize);
            res.transforms.setSize(newSize);
        } else {
            res = (AnimatableTransformListValue)result;
            if (res.transforms == null) {
                res.transforms = new Vector(newSize);
                res.transforms.setSize(newSize);
            } else if (res.transforms.size() != newSize) {
                res.transforms.setSize(newSize);
            }
        }
        int index = 0;
        for (int j = 0; j < multiplier; ++j) {
            int i = 0;
            while (i < accSize) {
                res.transforms.setElementAt(accTransformList.transforms.elementAt(i), index);
                ++i;
                ++index;
            }
        }
        int i = 0;
        while (i < this.transforms.size() - 1) {
            res.transforms.setElementAt(this.transforms.elementAt(i), index);
            ++i;
            ++index;
        }
        if (to != null) {
            short type;
            AbstractSVGTransform tt = (AbstractSVGTransform)toTransformList.transforms.lastElement();
            AbstractSVGTransform ft = null;
            if (this.transforms.isEmpty()) {
                type = tt.getType();
                switch (type) {
                    case 5: {
                        ft = IDENTITY_SKEWX;
                        break;
                    }
                    case 6: {
                        ft = IDENTITY_SKEWY;
                        break;
                    }
                    case 3: {
                        ft = IDENTITY_SCALE;
                        break;
                    }
                    case 4: {
                        ft = IDENTITY_ROTATE;
                        break;
                    }
                    case 2: {
                        ft = IDENTITY_TRANSLATE;
                    }
                }
            } else {
                ft = (AbstractSVGTransform)this.transforms.lastElement();
                type = ft.getType();
            }
            if (type == tt.getType()) {
                SVGOMTransform t;
                if (res.transforms.isEmpty()) {
                    t = new SVGOMTransform();
                    res.transforms.add(t);
                } else {
                    t = (AbstractSVGTransform)res.transforms.elementAt(index);
                    if (t == null) {
                        t = new SVGOMTransform();
                        res.transforms.setElementAt(t, index);
                    }
                }
                float r = 0.0f;
                switch (type) {
                    case 5: 
                    case 6: {
                        r = ft.getAngle();
                        r += interpolation * (tt.getAngle() - r);
                        if (type == 5) {
                            t.setSkewX(r);
                            break;
                        }
                        if (type != 6) break;
                        t.setSkewY(r);
                        break;
                    }
                    case 3: {
                        SVGMatrix fm = ft.getMatrix();
                        SVGMatrix tm = tt.getMatrix();
                        float x = fm.getA();
                        float y = fm.getD();
                        x += interpolation * (tm.getA() - x);
                        y += interpolation * (tm.getD() - y);
                        t.setScale(x, y);
                        break;
                    }
                    case 4: {
                        float x = ft.getX();
                        float y = ft.getY();
                        x += interpolation * (tt.getX() - x);
                        y += interpolation * (tt.getY() - y);
                        r = ft.getAngle();
                        r += interpolation * (tt.getAngle() - r);
                        t.setRotate(r, x, y);
                        break;
                    }
                    case 2: {
                        SVGMatrix fm = ft.getMatrix();
                        SVGMatrix tm = tt.getMatrix();
                        float x = fm.getE();
                        float y = fm.getF();
                        x += interpolation * (tm.getE() - x);
                        y += interpolation * (tm.getF() - y);
                        t.setTranslate(x, y);
                        break;
                    }
                }
            }
        } else {
            AbstractSVGTransform ft = (AbstractSVGTransform)this.transforms.lastElement();
            AbstractSVGTransform t = (AbstractSVGTransform)res.transforms.elementAt(index);
            if (t == null) {
                t = new SVGOMTransform();
                res.transforms.setElementAt(t, index);
            }
            t.assign(ft);
        }
        res.hasChanged = true;
        return res;
    }

    public static AnimatableTransformListValue interpolate(AnimatableTransformListValue res, AnimatableTransformListValue value1, AnimatableTransformListValue value2, AnimatableTransformListValue to1, AnimatableTransformListValue to2, float interpolation1, float interpolation2, AnimatableTransformListValue accumulation, int multiplier) {
        float y;
        float x;
        short type;
        int accSize = accumulation == null ? 0 : accumulation.transforms.size();
        int newSize = accSize * multiplier + 1;
        if (res == null) {
            res = new AnimatableTransformListValue(to1.target);
            res.transforms = new Vector(newSize);
            res.transforms.setSize(newSize);
        } else if (res.transforms == null) {
            res.transforms = new Vector(newSize);
            res.transforms.setSize(newSize);
        } else if (res.transforms.size() != newSize) {
            res.transforms.setSize(newSize);
        }
        int index = 0;
        for (int j = 0; j < multiplier; ++j) {
            int i = 0;
            while (i < accSize) {
                res.transforms.setElementAt(accumulation.transforms.elementAt(i), index);
                ++i;
                ++index;
            }
        }
        AbstractSVGTransform ft1 = (AbstractSVGTransform)value1.transforms.lastElement();
        AbstractSVGTransform ft2 = (AbstractSVGTransform)value2.transforms.lastElement();
        AbstractSVGTransform t = (AbstractSVGTransform)res.transforms.elementAt(index);
        if (t == null) {
            t = new SVGOMTransform();
            res.transforms.setElementAt(t, index);
        }
        if ((type = ft1.getType()) == 3) {
            x = ft1.getMatrix().getA();
            y = ft2.getMatrix().getD();
        } else {
            x = ft1.getMatrix().getE();
            y = ft2.getMatrix().getF();
        }
        if (to1 != null) {
            AbstractSVGTransform tt1 = (AbstractSVGTransform)to1.transforms.lastElement();
            AbstractSVGTransform tt2 = (AbstractSVGTransform)to2.transforms.lastElement();
            if (type == 3) {
                x += interpolation1 * (tt1.getMatrix().getA() - x);
                y += interpolation2 * (tt2.getMatrix().getD() - y);
            } else {
                x += interpolation1 * (tt1.getMatrix().getE() - x);
                y += interpolation2 * (tt2.getMatrix().getF() - y);
            }
        }
        if (type == 3) {
            t.setScale(x, y);
        } else {
            t.setTranslate(x, y);
        }
        res.hasChanged = true;
        return res;
    }

    public static AnimatableTransformListValue interpolate(AnimatableTransformListValue res, AnimatableTransformListValue value1, AnimatableTransformListValue value2, AnimatableTransformListValue value3, AnimatableTransformListValue to1, AnimatableTransformListValue to2, AnimatableTransformListValue to3, float interpolation1, float interpolation2, float interpolation3, AnimatableTransformListValue accumulation, int multiplier) {
        int accSize = accumulation == null ? 0 : accumulation.transforms.size();
        int newSize = accSize * multiplier + 1;
        if (res == null) {
            res = new AnimatableTransformListValue(to1.target);
            res.transforms = new Vector(newSize);
            res.transforms.setSize(newSize);
        } else if (res.transforms == null) {
            res.transforms = new Vector(newSize);
            res.transforms.setSize(newSize);
        } else if (res.transforms.size() != newSize) {
            res.transforms.setSize(newSize);
        }
        int index = 0;
        for (int j = 0; j < multiplier; ++j) {
            int i = 0;
            while (i < accSize) {
                res.transforms.setElementAt(accumulation.transforms.elementAt(i), index);
                ++i;
                ++index;
            }
        }
        AbstractSVGTransform ft1 = (AbstractSVGTransform)value1.transforms.lastElement();
        AbstractSVGTransform ft2 = (AbstractSVGTransform)value2.transforms.lastElement();
        AbstractSVGTransform ft3 = (AbstractSVGTransform)value3.transforms.lastElement();
        AbstractSVGTransform t = (AbstractSVGTransform)res.transforms.elementAt(index);
        if (t == null) {
            t = new SVGOMTransform();
            res.transforms.setElementAt(t, index);
        }
        float r = ft1.getAngle();
        float x = ft2.getX();
        float y = ft3.getY();
        if (to1 != null) {
            AbstractSVGTransform tt1 = (AbstractSVGTransform)to1.transforms.lastElement();
            AbstractSVGTransform tt2 = (AbstractSVGTransform)to2.transforms.lastElement();
            AbstractSVGTransform tt3 = (AbstractSVGTransform)to3.transforms.lastElement();
            r += interpolation1 * (tt1.getAngle() - r);
            x += interpolation2 * (tt2.getX() - x);
            y += interpolation3 * (tt3.getY() - y);
        }
        t.setRotate(r, x, y);
        res.hasChanged = true;
        return res;
    }

    public Iterator getTransforms() {
        return this.transforms.iterator();
    }

    @Override
    public boolean canPace() {
        return true;
    }

    @Override
    public float distanceTo(AnimatableValue other) {
        AnimatableTransformListValue o = (AnimatableTransformListValue)other;
        if (this.transforms.isEmpty() || o.transforms.isEmpty()) {
            return 0.0f;
        }
        AbstractSVGTransform t1 = (AbstractSVGTransform)this.transforms.lastElement();
        AbstractSVGTransform t2 = (AbstractSVGTransform)o.transforms.lastElement();
        short type1 = t1.getType();
        if (type1 != t2.getType()) {
            return 0.0f;
        }
        SVGMatrix m1 = t1.getMatrix();
        SVGMatrix m2 = t2.getMatrix();
        switch (type1) {
            case 2: {
                return Math.abs(m1.getE() - m2.getE()) + Math.abs(m1.getF() - m2.getF());
            }
            case 3: {
                return Math.abs(m1.getA() - m2.getA()) + Math.abs(m1.getD() - m2.getD());
            }
            case 4: 
            case 5: 
            case 6: {
                return Math.abs(t1.getAngle() - t2.getAngle());
            }
        }
        return 0.0f;
    }

    public float distanceTo1(AnimatableValue other) {
        AnimatableTransformListValue o = (AnimatableTransformListValue)other;
        if (this.transforms.isEmpty() || o.transforms.isEmpty()) {
            return 0.0f;
        }
        AbstractSVGTransform t1 = (AbstractSVGTransform)this.transforms.lastElement();
        AbstractSVGTransform t2 = (AbstractSVGTransform)o.transforms.lastElement();
        short type1 = t1.getType();
        if (type1 != t2.getType()) {
            return 0.0f;
        }
        SVGMatrix m1 = t1.getMatrix();
        SVGMatrix m2 = t2.getMatrix();
        switch (type1) {
            case 2: {
                return Math.abs(m1.getE() - m2.getE());
            }
            case 3: {
                return Math.abs(m1.getA() - m2.getA());
            }
            case 4: 
            case 5: 
            case 6: {
                return Math.abs(t1.getAngle() - t2.getAngle());
            }
        }
        return 0.0f;
    }

    public float distanceTo2(AnimatableValue other) {
        AnimatableTransformListValue o = (AnimatableTransformListValue)other;
        if (this.transforms.isEmpty() || o.transforms.isEmpty()) {
            return 0.0f;
        }
        AbstractSVGTransform t1 = (AbstractSVGTransform)this.transforms.lastElement();
        AbstractSVGTransform t2 = (AbstractSVGTransform)o.transforms.lastElement();
        short type1 = t1.getType();
        if (type1 != t2.getType()) {
            return 0.0f;
        }
        SVGMatrix m1 = t1.getMatrix();
        SVGMatrix m2 = t2.getMatrix();
        switch (type1) {
            case 2: {
                return Math.abs(m1.getF() - m2.getF());
            }
            case 3: {
                return Math.abs(m1.getD() - m2.getD());
            }
            case 4: {
                return Math.abs(t1.getX() - t2.getX());
            }
        }
        return 0.0f;
    }

    public float distanceTo3(AnimatableValue other) {
        AnimatableTransformListValue o = (AnimatableTransformListValue)other;
        if (this.transforms.isEmpty() || o.transforms.isEmpty()) {
            return 0.0f;
        }
        AbstractSVGTransform t1 = (AbstractSVGTransform)this.transforms.lastElement();
        AbstractSVGTransform t2 = (AbstractSVGTransform)o.transforms.lastElement();
        short type1 = t1.getType();
        if (type1 != t2.getType()) {
            return 0.0f;
        }
        if (type1 == 4) {
            return Math.abs(t1.getY() - t2.getY());
        }
        return 0.0f;
    }

    @Override
    public AnimatableValue getZeroValue() {
        return new AnimatableTransformListValue(this.target, new Vector(5));
    }

    @Override
    public String toStringRep() {
        StringBuffer sb = new StringBuffer();
        Iterator i = this.transforms.iterator();
        while (i.hasNext()) {
            AbstractSVGTransform t = (AbstractSVGTransform)i.next();
            if (t == null) {
                sb.append("null");
            } else {
                SVGMatrix m = t.getMatrix();
                switch (t.getType()) {
                    case 2: {
                        sb.append("translate(");
                        sb.append(m.getE());
                        sb.append(',');
                        sb.append(m.getF());
                        sb.append(')');
                        break;
                    }
                    case 3: {
                        sb.append("scale(");
                        sb.append(m.getA());
                        sb.append(',');
                        sb.append(m.getD());
                        sb.append(')');
                        break;
                    }
                    case 5: {
                        sb.append("skewX(");
                        sb.append(t.getAngle());
                        sb.append(')');
                        break;
                    }
                    case 6: {
                        sb.append("skewY(");
                        sb.append(t.getAngle());
                        sb.append(')');
                        break;
                    }
                    case 4: {
                        sb.append("rotate(");
                        sb.append(t.getAngle());
                        sb.append(',');
                        sb.append(t.getX());
                        sb.append(',');
                        sb.append(t.getY());
                        sb.append(')');
                    }
                }
            }
            if (!i.hasNext()) continue;
            sb.append(' ');
        }
        return sb.toString();
    }

    static {
        IDENTITY_SKEWX.setSkewX(0.0f);
        IDENTITY_SKEWY.setSkewY(0.0f);
        IDENTITY_SCALE.setScale(0.0f, 0.0f);
        IDENTITY_ROTATE.setRotate(0.0f, 0.0f, 0.0f);
        IDENTITY_TRANSLATE.setTranslate(0.0f, 0.0f);
    }
}

