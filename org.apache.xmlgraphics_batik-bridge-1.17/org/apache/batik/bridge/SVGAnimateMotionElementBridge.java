/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.AbstractAnimation
 *  org.apache.batik.anim.MotionAnimation
 *  org.apache.batik.anim.dom.AnimatableElement
 *  org.apache.batik.anim.dom.AnimationTarget
 *  org.apache.batik.anim.dom.SVGOMElement
 *  org.apache.batik.anim.dom.SVGOMPathElement
 *  org.apache.batik.anim.values.AnimatableMotionPointValue
 *  org.apache.batik.anim.values.AnimatableValue
 *  org.apache.batik.dom.svg.SVGAnimatedPathDataSupport
 *  org.apache.batik.dom.util.XLinkSupport
 *  org.apache.batik.ext.awt.geom.ExtendedGeneralPath
 *  org.apache.batik.parser.AWTPathProducer
 *  org.apache.batik.parser.AngleHandler
 *  org.apache.batik.parser.AngleParser
 *  org.apache.batik.parser.LengthArrayProducer
 *  org.apache.batik.parser.LengthListHandler
 *  org.apache.batik.parser.LengthPairListParser
 *  org.apache.batik.parser.ParseException
 *  org.apache.batik.parser.PathHandler
 *  org.apache.batik.parser.PathParser
 *  org.w3c.dom.svg.SVGPathSegList
 */
package org.apache.batik.bridge;

import java.util.ArrayList;
import org.apache.batik.anim.AbstractAnimation;
import org.apache.batik.anim.MotionAnimation;
import org.apache.batik.anim.dom.AnimatableElement;
import org.apache.batik.anim.dom.AnimationTarget;
import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.anim.dom.SVGOMPathElement;
import org.apache.batik.anim.values.AnimatableMotionPointValue;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.SVGAnimateElementBridge;
import org.apache.batik.dom.svg.SVGAnimatedPathDataSupport;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.ext.awt.geom.ExtendedGeneralPath;
import org.apache.batik.parser.AWTPathProducer;
import org.apache.batik.parser.AngleHandler;
import org.apache.batik.parser.AngleParser;
import org.apache.batik.parser.LengthArrayProducer;
import org.apache.batik.parser.LengthListHandler;
import org.apache.batik.parser.LengthPairListParser;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PathHandler;
import org.apache.batik.parser.PathParser;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGPathSegList;

public class SVGAnimateMotionElementBridge
extends SVGAnimateElementBridge {
    @Override
    public String getLocalName() {
        return "animateMotion";
    }

    @Override
    public Bridge getInstance() {
        return new SVGAnimateMotionElementBridge();
    }

    @Override
    protected AbstractAnimation createAnimation(AnimationTarget target) {
        this.animationType = (short)2;
        this.attributeLocalName = "motion";
        AnimatableValue from = this.parseLengthPair("from");
        AnimatableValue to = this.parseLengthPair("to");
        AnimatableValue by = this.parseLengthPair("by");
        boolean rotateAuto = false;
        boolean rotateAutoReverse = false;
        float rotateAngle = 0.0f;
        short rotateAngleUnit = 0;
        String rotateString = this.element.getAttributeNS(null, "rotate");
        if (rotateString.length() != 0) {
            if (rotateString.equals("auto")) {
                rotateAuto = true;
            } else if (rotateString.equals("auto-reverse")) {
                rotateAuto = true;
                rotateAutoReverse = true;
            } else {
                AngleParser ap = new AngleParser();
                class Handler
                implements AngleHandler {
                    float theAngle;
                    short theUnit = 1;

                    Handler() {
                    }

                    public void startAngle() throws ParseException {
                    }

                    public void angleValue(float v) throws ParseException {
                        this.theAngle = v;
                    }

                    public void deg() throws ParseException {
                        this.theUnit = (short)2;
                    }

                    public void grad() throws ParseException {
                        this.theUnit = (short)4;
                    }

                    public void rad() throws ParseException {
                        this.theUnit = (short)3;
                    }

                    public void endAngle() throws ParseException {
                    }
                }
                Handler h = new Handler();
                ap.setAngleHandler((AngleHandler)h);
                try {
                    ap.parse(rotateString);
                }
                catch (ParseException pEx) {
                    throw new BridgeException(this.ctx, (Element)this.element, (Exception)((Object)pEx), "attribute.malformed", new Object[]{"rotate", rotateString});
                }
                rotateAngle = h.theAngle;
                rotateAngleUnit = h.theUnit;
            }
        }
        return new MotionAnimation(this.timedElement, (AnimatableElement)this, this.parseCalcMode(), this.parseKeyTimes(), this.parseKeySplines(), this.parseAdditive(), this.parseAccumulate(), this.parseValues(), from, to, by, this.parsePath(), this.parseKeyPoints(), rotateAuto, rotateAutoReverse, rotateAngle, rotateAngleUnit);
    }

    protected ExtendedGeneralPath parsePath() {
        for (Node n = this.element.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() != 1 || !"http://www.w3.org/2000/svg".equals(n.getNamespaceURI()) || !"mpath".equals(n.getLocalName())) continue;
            String uri = XLinkSupport.getXLinkHref((Element)((Element)n));
            Element path = this.ctx.getReferencedElement((Element)this.element, uri);
            if (!"http://www.w3.org/2000/svg".equals(path.getNamespaceURI()) || !"path".equals(path.getLocalName())) {
                throw new BridgeException(this.ctx, (Element)this.element, "uri.badTarget", new Object[]{uri});
            }
            SVGOMPathElement pathElt = (SVGOMPathElement)path;
            AWTPathProducer app = new AWTPathProducer();
            SVGAnimatedPathDataSupport.handlePathSegList((SVGPathSegList)pathElt.getPathSegList(), (PathHandler)app);
            return (ExtendedGeneralPath)app.getShape();
        }
        String pathString = this.element.getAttributeNS(null, "path");
        if (pathString.length() == 0) {
            return null;
        }
        try {
            AWTPathProducer app = new AWTPathProducer();
            PathParser pp = new PathParser();
            pp.setPathHandler((PathHandler)app);
            pp.parse(pathString);
            return (ExtendedGeneralPath)app.getShape();
        }
        catch (ParseException pEx) {
            throw new BridgeException(this.ctx, (Element)this.element, (Exception)((Object)pEx), "attribute.malformed", new Object[]{"path", pathString});
        }
    }

    protected float[] parseKeyPoints() {
        String keyPointsString = this.element.getAttributeNS(null, "keyPoints");
        int len = keyPointsString.length();
        if (len == 0) {
            return null;
        }
        ArrayList<Float> keyPoints = new ArrayList<Float>(7);
        int i = 0;
        int start = 0;
        block2: while (i < len) {
            while (keyPointsString.charAt(i) == ' ') {
                if (++i != len) continue;
                break block2;
            }
            start = i++;
            if (i != len) {
                char c = keyPointsString.charAt(i);
                while (c != ' ' && c != ';' && c != ',' && ++i != len) {
                    c = keyPointsString.charAt(i);
                }
            }
            int end = i++;
            try {
                float keyPointCoord = Float.parseFloat(keyPointsString.substring(start, end));
                keyPoints.add(Float.valueOf(keyPointCoord));
            }
            catch (NumberFormatException nfEx) {
                throw new BridgeException(this.ctx, (Element)this.element, nfEx, "attribute.malformed", new Object[]{"keyPoints", keyPointsString});
            }
        }
        len = keyPoints.size();
        float[] ret = new float[len];
        for (int j = 0; j < len; ++j) {
            ret[j] = ((Float)keyPoints.get(j)).floatValue();
        }
        return ret;
    }

    @Override
    protected int getDefaultCalcMode() {
        return 2;
    }

    @Override
    protected AnimatableValue[] parseValues() {
        String valuesString = this.element.getAttributeNS(null, "values");
        int len = valuesString.length();
        if (len == 0) {
            return null;
        }
        return this.parseValues(valuesString);
    }

    protected AnimatableValue[] parseValues(String s) {
        try {
            LengthPairListParser lplp = new LengthPairListParser();
            LengthArrayProducer lap = new LengthArrayProducer();
            lplp.setLengthListHandler((LengthListHandler)lap);
            lplp.parse(s);
            short[] types = lap.getLengthTypeArray();
            float[] values = lap.getLengthValueArray();
            AnimatableValue[] ret = new AnimatableValue[types.length / 2];
            for (int i = 0; i < types.length; i += 2) {
                float x = this.animationTarget.svgToUserSpace(values[i], types[i], (short)1);
                float y = this.animationTarget.svgToUserSpace(values[i + 1], types[i + 1], (short)2);
                ret[i / 2] = new AnimatableMotionPointValue(this.animationTarget, x, y, 0.0f);
            }
            return ret;
        }
        catch (ParseException pEx) {
            throw new BridgeException(this.ctx, (Element)this.element, (Exception)((Object)pEx), "attribute.malformed", new Object[]{"values", s});
        }
    }

    protected AnimatableValue parseLengthPair(String ln) {
        String s = this.element.getAttributeNS(null, ln);
        if (s.length() == 0) {
            return null;
        }
        return this.parseValues(s)[0];
    }

    @Override
    public AnimatableValue getUnderlyingValue() {
        return new AnimatableMotionPointValue(this.animationTarget, 0.0f, 0.0f, 0.0f);
    }

    @Override
    protected void initializeAnimation() {
        Node t;
        String uri = XLinkSupport.getXLinkHref((Element)this.element);
        if (uri.length() == 0) {
            t = this.element.getParentNode();
        } else {
            t = this.ctx.getReferencedElement((Element)this.element, uri);
            if (t.getOwnerDocument() != this.element.getOwnerDocument()) {
                throw new BridgeException(this.ctx, (Element)this.element, "uri.badTarget", new Object[]{uri});
            }
        }
        this.animationTarget = null;
        if (t instanceof SVGOMElement) {
            this.targetElement = (SVGOMElement)t;
            this.animationTarget = this.targetElement;
        }
        if (this.animationTarget == null) {
            throw new BridgeException(this.ctx, (Element)this.element, "uri.badTarget", new Object[]{uri});
        }
        this.timedElement = this.createTimedElement();
        this.animation = this.createAnimation(this.animationTarget);
        this.eng.addAnimation(this.animationTarget, (short)2, this.attributeNamespaceURI, this.attributeLocalName, this.animation);
    }
}

