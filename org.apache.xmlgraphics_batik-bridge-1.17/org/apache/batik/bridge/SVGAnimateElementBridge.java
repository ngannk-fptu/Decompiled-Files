/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.AbstractAnimation
 *  org.apache.batik.anim.SimpleAnimation
 *  org.apache.batik.anim.dom.AnimatableElement
 *  org.apache.batik.anim.dom.AnimationTarget
 *  org.apache.batik.anim.values.AnimatableValue
 */
package org.apache.batik.bridge;

import java.util.ArrayList;
import org.apache.batik.anim.AbstractAnimation;
import org.apache.batik.anim.SimpleAnimation;
import org.apache.batik.anim.dom.AnimatableElement;
import org.apache.batik.anim.dom.AnimationTarget;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.SVGAnimationElementBridge;
import org.w3c.dom.Element;

public class SVGAnimateElementBridge
extends SVGAnimationElementBridge {
    @Override
    public String getLocalName() {
        return "animate";
    }

    @Override
    public Bridge getInstance() {
        return new SVGAnimateElementBridge();
    }

    @Override
    protected AbstractAnimation createAnimation(AnimationTarget target) {
        AnimatableValue from = this.parseAnimatableValue("from");
        AnimatableValue to = this.parseAnimatableValue("to");
        AnimatableValue by = this.parseAnimatableValue("by");
        return new SimpleAnimation(this.timedElement, (AnimatableElement)this, this.parseCalcMode(), this.parseKeyTimes(), this.parseKeySplines(), this.parseAdditive(), this.parseAccumulate(), this.parseValues(), from, to, by);
    }

    protected int parseCalcMode() {
        if (this.animationType == 1 && !this.targetElement.isPropertyAdditive(this.attributeLocalName) || this.animationType == 0 && !this.targetElement.isAttributeAdditive(this.attributeNamespaceURI, this.attributeLocalName)) {
            return 0;
        }
        String calcModeString = this.element.getAttributeNS(null, "calcMode");
        if (calcModeString.length() == 0) {
            return this.getDefaultCalcMode();
        }
        if (calcModeString.equals("linear")) {
            return 1;
        }
        if (calcModeString.equals("discrete")) {
            return 0;
        }
        if (calcModeString.equals("paced")) {
            return 2;
        }
        if (calcModeString.equals("spline")) {
            return 3;
        }
        throw new BridgeException(this.ctx, (Element)this.element, "attribute.malformed", new Object[]{"calcMode", calcModeString});
    }

    protected boolean parseAdditive() {
        String additiveString = this.element.getAttributeNS(null, "additive");
        if (additiveString.length() == 0 || additiveString.equals("replace")) {
            return false;
        }
        if (additiveString.equals("sum")) {
            return true;
        }
        throw new BridgeException(this.ctx, (Element)this.element, "attribute.malformed", new Object[]{"additive", additiveString});
    }

    protected boolean parseAccumulate() {
        String accumulateString = this.element.getAttributeNS(null, "accumulate");
        if (accumulateString.length() == 0 || accumulateString.equals("none")) {
            return false;
        }
        if (accumulateString.equals("sum")) {
            return true;
        }
        throw new BridgeException(this.ctx, (Element)this.element, "attribute.malformed", new Object[]{"accumulate", accumulateString});
    }

    protected AnimatableValue[] parseValues() {
        boolean isCSS = this.animationType == 1;
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
            AnimatableValue val;
            while (valuesString.charAt(i) == ' ') {
                if (++i != len) continue;
                break block0;
            }
            start = i++;
            if (i != len) {
                char c = valuesString.charAt(i);
                while (c != ';' && ++i != len) {
                    c = valuesString.charAt(i);
                }
            }
            if (!this.checkValueType(val = this.eng.parseAnimatableValue((Element)this.element, this.animationTarget, this.attributeNamespaceURI, this.attributeLocalName, isCSS, valuesString.substring(start, end = i++)))) {
                throw new BridgeException(this.ctx, (Element)this.element, "attribute.malformed", new Object[]{"values", valuesString});
            }
            values.add(val);
        }
        AnimatableValue[] ret = new AnimatableValue[values.size()];
        return values.toArray(ret);
    }

    protected float[] parseKeyTimes() {
        String keyTimesString = this.element.getAttributeNS(null, "keyTimes");
        int len = keyTimesString.length();
        if (len == 0) {
            return null;
        }
        ArrayList<Float> keyTimes = new ArrayList<Float>(7);
        int i = 0;
        int start = 0;
        block2: while (i < len) {
            while (keyTimesString.charAt(i) == ' ') {
                if (++i != len) continue;
                break block2;
            }
            start = i++;
            if (i != len) {
                char c = keyTimesString.charAt(i);
                while (c != ' ' && c != ';' && ++i != len) {
                    c = keyTimesString.charAt(i);
                }
            }
            int end = i++;
            try {
                float keyTime = Float.parseFloat(keyTimesString.substring(start, end));
                keyTimes.add(Float.valueOf(keyTime));
            }
            catch (NumberFormatException nfEx) {
                throw new BridgeException(this.ctx, (Element)this.element, nfEx, "attribute.malformed", new Object[]{"keyTimes", keyTimesString});
            }
        }
        len = keyTimes.size();
        float[] ret = new float[len];
        for (int j = 0; j < len; ++j) {
            ret[j] = ((Float)keyTimes.get(j)).floatValue();
        }
        return ret;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected float[] parseKeySplines() {
        String keySplinesString = this.element.getAttributeNS(null, "keySplines");
        int len = keySplinesString.length();
        if (len == 0) {
            return null;
        }
        ArrayList<Float> keySplines = new ArrayList<Float>(7);
        int count = 0;
        int i = 0;
        int start = 0;
        block2: while (i < len) {
            int end;
            while (keySplinesString.charAt(i) == ' ') {
                if (++i != len) continue;
                break block2;
            }
            start = i++;
            if (i != len) {
                char c = keySplinesString.charAt(i);
                while (c != ' ' && c != ',' && c != ';' && ++i != len) {
                    c = keySplinesString.charAt(i);
                }
                end = i++;
                if (c == ' ') {
                    while (i != len && (c = keySplinesString.charAt(i++)) == ' ') {
                    }
                    if (c != ';' && c != ',') {
                        --i;
                    }
                }
                if (c == ';') {
                    if (count != 3) throw new BridgeException(this.ctx, (Element)this.element, "attribute.malformed", new Object[]{"keySplines", keySplinesString});
                    count = 0;
                } else {
                    ++count;
                }
            } else {
                end = i++;
            }
            try {
                float keySplineValue = Float.parseFloat(keySplinesString.substring(start, end));
                keySplines.add(Float.valueOf(keySplineValue));
            }
            catch (NumberFormatException nfEx) {
                throw new BridgeException(this.ctx, (Element)this.element, nfEx, "attribute.malformed", new Object[]{"keySplines", keySplinesString});
            }
        }
        len = keySplines.size();
        float[] ret = new float[len];
        for (int j = 0; j < len; ++j) {
            ret[j] = ((Float)keySplines.get(j)).floatValue();
        }
        return ret;
    }

    protected int getDefaultCalcMode() {
        return 1;
    }

    @Override
    protected boolean canAnimateType(int type) {
        return true;
    }
}

