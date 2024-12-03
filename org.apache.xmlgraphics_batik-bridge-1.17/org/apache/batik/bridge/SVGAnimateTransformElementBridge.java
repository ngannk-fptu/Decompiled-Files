/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.AbstractAnimation
 *  org.apache.batik.anim.TransformAnimation
 *  org.apache.batik.anim.dom.AnimatableElement
 *  org.apache.batik.anim.dom.AnimationTarget
 *  org.apache.batik.anim.values.AnimatableTransformListValue
 *  org.apache.batik.anim.values.AnimatableValue
 *  org.apache.batik.dom.svg.AbstractSVGTransform
 *  org.apache.batik.dom.svg.SVGOMTransform
 */
package org.apache.batik.bridge;

import java.util.ArrayList;
import org.apache.batik.anim.AbstractAnimation;
import org.apache.batik.anim.TransformAnimation;
import org.apache.batik.anim.dom.AnimatableElement;
import org.apache.batik.anim.dom.AnimationTarget;
import org.apache.batik.anim.values.AnimatableTransformListValue;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.SVGAnimateElementBridge;
import org.apache.batik.dom.svg.AbstractSVGTransform;
import org.apache.batik.dom.svg.SVGOMTransform;
import org.w3c.dom.Element;

public class SVGAnimateTransformElementBridge
extends SVGAnimateElementBridge {
    @Override
    public String getLocalName() {
        return "animateTransform";
    }

    @Override
    public Bridge getInstance() {
        return new SVGAnimateTransformElementBridge();
    }

    @Override
    protected AbstractAnimation createAnimation(AnimationTarget target) {
        short type = this.parseType();
        AnimatableValue from = null;
        AnimatableValue to = null;
        AnimatableValue by = null;
        if (this.element.hasAttributeNS(null, "from")) {
            from = this.parseValue(this.element.getAttributeNS(null, "from"), type, target);
        }
        if (this.element.hasAttributeNS(null, "to")) {
            to = this.parseValue(this.element.getAttributeNS(null, "to"), type, target);
        }
        if (this.element.hasAttributeNS(null, "by")) {
            by = this.parseValue(this.element.getAttributeNS(null, "by"), type, target);
        }
        return new TransformAnimation(this.timedElement, (AnimatableElement)this, this.parseCalcMode(), this.parseKeyTimes(), this.parseKeySplines(), this.parseAdditive(), this.parseAccumulate(), this.parseValues(type, target), from, to, by, type);
    }

    protected short parseType() {
        String typeString = this.element.getAttributeNS(null, "type");
        if (typeString.equals("translate")) {
            return 2;
        }
        if (typeString.equals("scale")) {
            return 3;
        }
        if (typeString.equals("rotate")) {
            return 4;
        }
        if (typeString.equals("skewX")) {
            return 5;
        }
        if (typeString.equals("skewY")) {
            return 6;
        }
        throw new BridgeException(this.ctx, (Element)this.element, "attribute.malformed", new Object[]{"type", typeString});
    }

    protected AnimatableValue parseValue(String s, short type, AnimationTarget target) {
        int i;
        float val2 = 0.0f;
        float val3 = 0.0f;
        int c = 44;
        int len = s.length();
        for (i = 0; i < len && (c = (int)s.charAt(i)) != 32 && c != 44; ++i) {
        }
        float val1 = Float.parseFloat(s.substring(0, i));
        if (i < len) {
            ++i;
        }
        int count = 1;
        if (i < len && c == 32) {
            while (i < len && (c = (int)s.charAt(i)) == 32) {
                ++i;
            }
            if (c == 44) {
                ++i;
            }
        }
        while (i < len && s.charAt(i) == ' ') {
            ++i;
        }
        int j = i;
        if (i < len && type != 5 && type != 6) {
            while (i < len && (c = (int)s.charAt(i)) != 32 && c != 44) {
                ++i;
            }
            val2 = Float.parseFloat(s.substring(j, i));
            if (i < len) {
                ++i;
            }
            ++count;
            if (i < len && c == 32) {
                while (i < len && (c = (int)s.charAt(i)) == 32) {
                    ++i;
                }
                if (c == 44) {
                    ++i;
                }
            }
            while (i < len && s.charAt(i) == ' ') {
                ++i;
            }
            j = i;
            if (i < len && type == 4) {
                while (i < len && (c = (int)s.charAt(i)) != 44 && c != 32) {
                    ++i;
                }
                val3 = Float.parseFloat(s.substring(j, i));
                if (i < len) {
                    ++i;
                }
                ++count;
                while (i < len && s.charAt(i) == ' ') {
                    ++i;
                }
            }
        }
        if (i != len) {
            return null;
        }
        SVGOMTransform t = new SVGOMTransform();
        switch (type) {
            case 2: {
                if (count == 2) {
                    t.setTranslate(val1, val2);
                    break;
                }
                t.setTranslate(val1, 0.0f);
                break;
            }
            case 3: {
                if (count == 2) {
                    t.setScale(val1, val2);
                    break;
                }
                t.setScale(val1, val1);
                break;
            }
            case 4: {
                if (count == 3) {
                    t.setRotate(val1, val2, val3);
                    break;
                }
                t.setRotate(val1, 0.0f, 0.0f);
                break;
            }
            case 5: {
                t.setSkewX(val1);
                break;
            }
            case 6: {
                t.setSkewY(val1);
            }
        }
        return new AnimatableTransformListValue(target, (AbstractSVGTransform)t);
    }

    protected AnimatableValue[] parseValues(short type, AnimationTarget target) {
        String valuesString = this.element.getAttributeNS(null, "values");
        int len = valuesString.length();
        if (len == 0) {
            return null;
        }
        ArrayList<AnimatableValue> values = new ArrayList<AnimatableValue>(7);
        int i = 0;
        int start = 0;
        block0: while (i < len) {
            int end;
            String valueString;
            AnimatableValue value;
            while (valuesString.charAt(i) == ' ') {
                if (++i != len) continue;
                break block0;
            }
            start = i++;
            if (i < len) {
                char c = valuesString.charAt(i);
                while (c != ';' && ++i != len) {
                    c = valuesString.charAt(i);
                }
            }
            if ((value = this.parseValue(valueString = valuesString.substring(start, end = i++), type, target)) == null) {
                throw new BridgeException(this.ctx, (Element)this.element, "attribute.malformed", new Object[]{"values", valuesString});
            }
            values.add(value);
        }
        AnimatableValue[] ret = new AnimatableValue[values.size()];
        return values.toArray(ret);
    }

    @Override
    protected boolean canAnimateType(int type) {
        return type == 9;
    }
}

