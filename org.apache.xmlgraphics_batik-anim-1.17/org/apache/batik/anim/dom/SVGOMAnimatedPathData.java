/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.svg.AbstractSVGList
 *  org.apache.batik.dom.svg.AbstractSVGNormPathSegList
 *  org.apache.batik.dom.svg.AbstractSVGPathSegList
 *  org.apache.batik.dom.svg.AbstractSVGPathSegList$SVGPathSegArcItem
 *  org.apache.batik.dom.svg.AbstractSVGPathSegList$SVGPathSegCurvetoCubicItem
 *  org.apache.batik.dom.svg.AbstractSVGPathSegList$SVGPathSegCurvetoCubicSmoothItem
 *  org.apache.batik.dom.svg.AbstractSVGPathSegList$SVGPathSegCurvetoQuadraticItem
 *  org.apache.batik.dom.svg.AbstractSVGPathSegList$SVGPathSegCurvetoQuadraticSmoothItem
 *  org.apache.batik.dom.svg.AbstractSVGPathSegList$SVGPathSegLinetoHorizontalItem
 *  org.apache.batik.dom.svg.AbstractSVGPathSegList$SVGPathSegLinetoVerticalItem
 *  org.apache.batik.dom.svg.AbstractSVGPathSegList$SVGPathSegMovetoLinetoItem
 *  org.apache.batik.dom.svg.ListBuilder
 *  org.apache.batik.dom.svg.ListHandler
 *  org.apache.batik.dom.svg.LiveAttributeException
 *  org.apache.batik.dom.svg.SVGAnimatedPathDataSupport
 *  org.apache.batik.dom.svg.SVGItem
 *  org.apache.batik.dom.svg.SVGPathSegItem
 *  org.apache.batik.parser.ParseException
 *  org.apache.batik.parser.PathArrayProducer
 *  org.apache.batik.parser.PathHandler
 *  org.w3c.dom.svg.SVGAnimatedPathData
 *  org.w3c.dom.svg.SVGException
 *  org.w3c.dom.svg.SVGPathSeg
 *  org.w3c.dom.svg.SVGPathSegList
 */
package org.apache.batik.anim.dom;

import java.util.ArrayList;
import java.util.Iterator;
import org.apache.batik.anim.dom.AbstractElement;
import org.apache.batik.anim.dom.AbstractSVGAnimatedValue;
import org.apache.batik.anim.dom.AnimationTarget;
import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.anim.values.AnimatablePathDataValue;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.dom.svg.AbstractSVGList;
import org.apache.batik.dom.svg.AbstractSVGNormPathSegList;
import org.apache.batik.dom.svg.AbstractSVGPathSegList;
import org.apache.batik.dom.svg.ListBuilder;
import org.apache.batik.dom.svg.ListHandler;
import org.apache.batik.dom.svg.LiveAttributeException;
import org.apache.batik.dom.svg.SVGAnimatedPathDataSupport;
import org.apache.batik.dom.svg.SVGItem;
import org.apache.batik.dom.svg.SVGPathSegItem;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PathArrayProducer;
import org.apache.batik.parser.PathHandler;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAnimatedPathData;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGPathSeg;
import org.w3c.dom.svg.SVGPathSegList;

public class SVGOMAnimatedPathData
extends AbstractSVGAnimatedValue
implements SVGAnimatedPathData {
    protected boolean changing;
    protected BaseSVGPathSegList pathSegs;
    protected NormalizedBaseSVGPathSegList normalizedPathSegs;
    protected AnimSVGPathSegList animPathSegs;
    protected String defaultValue;

    public SVGOMAnimatedPathData(AbstractElement elt, String ns, String ln, String defaultValue) {
        super(elt, ns, ln);
        this.defaultValue = defaultValue;
    }

    public SVGPathSegList getAnimatedNormalizedPathSegList() {
        throw new UnsupportedOperationException("SVGAnimatedPathData.getAnimatedNormalizedPathSegList is not implemented");
    }

    public SVGPathSegList getAnimatedPathSegList() {
        if (this.animPathSegs == null) {
            this.animPathSegs = new AnimSVGPathSegList();
        }
        return this.animPathSegs;
    }

    public SVGPathSegList getNormalizedPathSegList() {
        if (this.normalizedPathSegs == null) {
            this.normalizedPathSegs = new NormalizedBaseSVGPathSegList();
        }
        return this.normalizedPathSegs;
    }

    public SVGPathSegList getPathSegList() {
        if (this.pathSegs == null) {
            this.pathSegs = new BaseSVGPathSegList();
        }
        return this.pathSegs;
    }

    public void check() {
        if (!this.hasAnimVal) {
            if (this.pathSegs == null) {
                this.pathSegs = new BaseSVGPathSegList();
            }
            this.pathSegs.revalidate();
            if (this.pathSegs.missing) {
                throw new LiveAttributeException((Element)((Object)this.element), this.localName, 0, null);
            }
            if (this.pathSegs.malformed) {
                throw new LiveAttributeException((Element)((Object)this.element), this.localName, 1, this.pathSegs.getValueAsString());
            }
        }
    }

    @Override
    public AnimatableValue getUnderlyingValue(AnimationTarget target) {
        SVGPathSegList psl = this.getPathSegList();
        PathArrayProducer pp = new PathArrayProducer();
        SVGAnimatedPathDataSupport.handlePathSegList((SVGPathSegList)psl, (PathHandler)pp);
        return new AnimatablePathDataValue(target, pp.getPathCommands(), pp.getPathParameters());
    }

    @Override
    protected void updateAnimatedValue(AnimatableValue val) {
        if (val == null) {
            this.hasAnimVal = false;
        } else {
            this.hasAnimVal = true;
            AnimatablePathDataValue animPath = (AnimatablePathDataValue)val;
            if (this.animPathSegs == null) {
                this.animPathSegs = new AnimSVGPathSegList();
            }
            this.animPathSegs.setAnimatedValue(animPath.getCommands(), animPath.getParameters());
        }
        this.fireAnimatedAttributeListeners();
    }

    public void attrAdded(Attr node, String newv) {
        if (!this.changing) {
            if (this.pathSegs != null) {
                this.pathSegs.invalidate();
            }
            if (this.normalizedPathSegs != null) {
                this.normalizedPathSegs.invalidate();
            }
        }
        this.fireBaseAttributeListeners();
        if (!this.hasAnimVal) {
            this.fireAnimatedAttributeListeners();
        }
    }

    public void attrModified(Attr node, String oldv, String newv) {
        if (!this.changing) {
            if (this.pathSegs != null) {
                this.pathSegs.invalidate();
            }
            if (this.normalizedPathSegs != null) {
                this.normalizedPathSegs.invalidate();
            }
        }
        this.fireBaseAttributeListeners();
        if (!this.hasAnimVal) {
            this.fireAnimatedAttributeListeners();
        }
    }

    public void attrRemoved(Attr node, String oldv) {
        if (!this.changing) {
            if (this.pathSegs != null) {
                this.pathSegs.invalidate();
            }
            if (this.normalizedPathSegs != null) {
                this.normalizedPathSegs.invalidate();
            }
        }
        this.fireBaseAttributeListeners();
        if (!this.hasAnimVal) {
            this.fireAnimatedAttributeListeners();
        }
    }

    public class AnimSVGPathSegList
    extends AbstractSVGPathSegList {
        private int[] parameterIndex = new int[1];

        public AnimSVGPathSegList() {
            this.itemList = new ArrayList(1);
        }

        protected DOMException createDOMException(short type, String key, Object[] args) {
            return SVGOMAnimatedPathData.this.element.createDOMException(type, key, args);
        }

        protected SVGException createSVGException(short type, String key, Object[] args) {
            return ((SVGOMElement)SVGOMAnimatedPathData.this.element).createSVGException(type, key, args);
        }

        public int getNumberOfItems() {
            if (SVGOMAnimatedPathData.this.hasAnimVal) {
                return super.getNumberOfItems();
            }
            return SVGOMAnimatedPathData.this.getPathSegList().getNumberOfItems();
        }

        public SVGPathSeg getItem(int index) throws DOMException {
            if (SVGOMAnimatedPathData.this.hasAnimVal) {
                return super.getItem(index);
            }
            return SVGOMAnimatedPathData.this.getPathSegList().getItem(index);
        }

        protected String getValueAsString() {
            if (this.itemList.size() == 0) {
                return "";
            }
            StringBuffer sb = new StringBuffer(this.itemList.size() * 8);
            Iterator i = this.itemList.iterator();
            if (i.hasNext()) {
                sb.append(((SVGItem)i.next()).getValueAsString());
            }
            while (i.hasNext()) {
                sb.append(this.getItemSeparator());
                sb.append(((SVGItem)i.next()).getValueAsString());
            }
            return sb.toString();
        }

        protected void setAttributeValue(String value) {
        }

        public void clear() throws DOMException {
            throw SVGOMAnimatedPathData.this.element.createDOMException((short)7, "readonly.pathseg.list", null);
        }

        public SVGPathSeg initialize(SVGPathSeg newItem) throws DOMException, SVGException {
            throw SVGOMAnimatedPathData.this.element.createDOMException((short)7, "readonly.pathseg.list", null);
        }

        public SVGPathSeg insertItemBefore(SVGPathSeg newItem, int index) throws DOMException, SVGException {
            throw SVGOMAnimatedPathData.this.element.createDOMException((short)7, "readonly.pathseg.list", null);
        }

        public SVGPathSeg replaceItem(SVGPathSeg newItem, int index) throws DOMException, SVGException {
            throw SVGOMAnimatedPathData.this.element.createDOMException((short)7, "readonly.pathseg.list", null);
        }

        public SVGPathSeg removeItem(int index) throws DOMException {
            throw SVGOMAnimatedPathData.this.element.createDOMException((short)7, "readonly.pathseg.list", null);
        }

        public SVGPathSeg appendItem(SVGPathSeg newItem) throws DOMException {
            throw SVGOMAnimatedPathData.this.element.createDOMException((short)7, "readonly.pathseg.list", null);
        }

        protected SVGPathSegItem newItem(short command, float[] parameters, int[] j) {
            switch (command) {
                case 10: 
                case 11: {
                    int n = j[0];
                    j[0] = n + 1;
                    int n2 = j[0];
                    j[0] = n2 + 1;
                    int n3 = j[0];
                    j[0] = n3 + 1;
                    int n4 = j[0];
                    j[0] = n4 + 1;
                    int n5 = j[0];
                    j[0] = n5 + 1;
                    int n6 = j[0];
                    j[0] = n6 + 1;
                    int n7 = j[0];
                    j[0] = n7 + 1;
                    return new AbstractSVGPathSegList.SVGPathSegArcItem(command, PATHSEG_LETTERS[command], parameters[n], parameters[n2], parameters[n3], parameters[n4] != 0.0f, parameters[n5] != 0.0f, parameters[n6], parameters[n7]);
                }
                case 1: {
                    return new SVGPathSegItem(command, PATHSEG_LETTERS[command]);
                }
                case 6: 
                case 7: {
                    int n = j[0];
                    j[0] = n + 1;
                    int n8 = j[0];
                    j[0] = n8 + 1;
                    int n9 = j[0];
                    j[0] = n9 + 1;
                    int n10 = j[0];
                    j[0] = n10 + 1;
                    int n11 = j[0];
                    j[0] = n11 + 1;
                    int n12 = j[0];
                    j[0] = n12 + 1;
                    return new AbstractSVGPathSegList.SVGPathSegCurvetoCubicItem(command, PATHSEG_LETTERS[command], parameters[n], parameters[n8], parameters[n9], parameters[n10], parameters[n11], parameters[n12]);
                }
                case 16: 
                case 17: {
                    int n = j[0];
                    j[0] = n + 1;
                    int n13 = j[0];
                    j[0] = n13 + 1;
                    int n14 = j[0];
                    j[0] = n14 + 1;
                    int n15 = j[0];
                    j[0] = n15 + 1;
                    return new AbstractSVGPathSegList.SVGPathSegCurvetoCubicSmoothItem(command, PATHSEG_LETTERS[command], parameters[n], parameters[n13], parameters[n14], parameters[n15]);
                }
                case 8: 
                case 9: {
                    int n = j[0];
                    j[0] = n + 1;
                    int n16 = j[0];
                    j[0] = n16 + 1;
                    int n17 = j[0];
                    j[0] = n17 + 1;
                    int n18 = j[0];
                    j[0] = n18 + 1;
                    return new AbstractSVGPathSegList.SVGPathSegCurvetoQuadraticItem(command, PATHSEG_LETTERS[command], parameters[n], parameters[n16], parameters[n17], parameters[n18]);
                }
                case 18: 
                case 19: {
                    int n = j[0];
                    j[0] = n + 1;
                    int n19 = j[0];
                    j[0] = n19 + 1;
                    return new AbstractSVGPathSegList.SVGPathSegCurvetoQuadraticSmoothItem(command, PATHSEG_LETTERS[command], parameters[n], parameters[n19]);
                }
                case 2: 
                case 3: 
                case 4: 
                case 5: {
                    int n = j[0];
                    j[0] = n + 1;
                    int n20 = j[0];
                    j[0] = n20 + 1;
                    return new AbstractSVGPathSegList.SVGPathSegMovetoLinetoItem(command, PATHSEG_LETTERS[command], parameters[n], parameters[n20]);
                }
                case 12: 
                case 13: {
                    int n = j[0];
                    j[0] = n + 1;
                    return new AbstractSVGPathSegList.SVGPathSegLinetoHorizontalItem(command, PATHSEG_LETTERS[command], parameters[n]);
                }
                case 14: 
                case 15: {
                    int n = j[0];
                    j[0] = n + 1;
                    return new AbstractSVGPathSegList.SVGPathSegLinetoVerticalItem(command, PATHSEG_LETTERS[command], parameters[n]);
                }
            }
            return null;
        }

        protected void setAnimatedValue(short[] commands, float[] parameters) {
            int i;
            int size = this.itemList.size();
            int[] j = this.parameterIndex;
            j[0] = 0;
            block11: for (i = 0; i < size && i < commands.length; ++i) {
                SVGPathSeg s = (SVGPathSeg)this.itemList.get(i);
                if (s.getPathSegType() != commands[i]) {
                    s = this.newItem(commands[i], parameters, j);
                    continue;
                }
                switch (commands[i]) {
                    case 10: 
                    case 11: {
                        AbstractSVGPathSegList.SVGPathSegArcItem ps = (AbstractSVGPathSegList.SVGPathSegArcItem)s;
                        int n = j[0];
                        j[0] = n + 1;
                        ps.setR1(parameters[n]);
                        int n2 = j[0];
                        j[0] = n2 + 1;
                        ps.setR2(parameters[n2]);
                        int n3 = j[0];
                        j[0] = n3 + 1;
                        ps.setAngle(parameters[n3]);
                        int n4 = j[0];
                        j[0] = n4 + 1;
                        ps.setLargeArcFlag(parameters[n4] != 0.0f);
                        int n5 = j[0];
                        j[0] = n5 + 1;
                        ps.setSweepFlag(parameters[n5] != 0.0f);
                        int n6 = j[0];
                        j[0] = n6 + 1;
                        ps.setX(parameters[n6]);
                        int n7 = j[0];
                        j[0] = n7 + 1;
                        ps.setY(parameters[n7]);
                        continue block11;
                    }
                    case 1: {
                        continue block11;
                    }
                    case 6: 
                    case 7: {
                        AbstractSVGPathSegList.SVGPathSegArcItem ps = (AbstractSVGPathSegList.SVGPathSegCurvetoCubicItem)s;
                        int n = j[0];
                        j[0] = n + 1;
                        ps.setX1(parameters[n]);
                        int n8 = j[0];
                        j[0] = n8 + 1;
                        ps.setY1(parameters[n8]);
                        int n9 = j[0];
                        j[0] = n9 + 1;
                        ps.setX2(parameters[n9]);
                        int n10 = j[0];
                        j[0] = n10 + 1;
                        ps.setY2(parameters[n10]);
                        int n11 = j[0];
                        j[0] = n11 + 1;
                        ps.setX(parameters[n11]);
                        int n12 = j[0];
                        j[0] = n12 + 1;
                        ps.setY(parameters[n12]);
                        continue block11;
                    }
                    case 16: 
                    case 17: {
                        AbstractSVGPathSegList.SVGPathSegArcItem ps = (AbstractSVGPathSegList.SVGPathSegCurvetoCubicSmoothItem)s;
                        int n = j[0];
                        j[0] = n + 1;
                        ps.setX2(parameters[n]);
                        int n13 = j[0];
                        j[0] = n13 + 1;
                        ps.setY2(parameters[n13]);
                        int n14 = j[0];
                        j[0] = n14 + 1;
                        ps.setX(parameters[n14]);
                        int n15 = j[0];
                        j[0] = n15 + 1;
                        ps.setY(parameters[n15]);
                        continue block11;
                    }
                    case 8: 
                    case 9: {
                        AbstractSVGPathSegList.SVGPathSegArcItem ps = (AbstractSVGPathSegList.SVGPathSegCurvetoQuadraticItem)s;
                        int n = j[0];
                        j[0] = n + 1;
                        ps.setX1(parameters[n]);
                        int n16 = j[0];
                        j[0] = n16 + 1;
                        ps.setY1(parameters[n16]);
                        int n17 = j[0];
                        j[0] = n17 + 1;
                        ps.setX(parameters[n17]);
                        int n18 = j[0];
                        j[0] = n18 + 1;
                        ps.setY(parameters[n18]);
                        continue block11;
                    }
                    case 18: 
                    case 19: {
                        AbstractSVGPathSegList.SVGPathSegArcItem ps = (AbstractSVGPathSegList.SVGPathSegCurvetoQuadraticSmoothItem)s;
                        int n = j[0];
                        j[0] = n + 1;
                        ps.setX(parameters[n]);
                        int n19 = j[0];
                        j[0] = n19 + 1;
                        ps.setY(parameters[n19]);
                        continue block11;
                    }
                    case 2: 
                    case 3: 
                    case 4: 
                    case 5: {
                        AbstractSVGPathSegList.SVGPathSegArcItem ps = (AbstractSVGPathSegList.SVGPathSegMovetoLinetoItem)s;
                        int n = j[0];
                        j[0] = n + 1;
                        ps.setX(parameters[n]);
                        int n20 = j[0];
                        j[0] = n20 + 1;
                        ps.setY(parameters[n20]);
                        continue block11;
                    }
                    case 12: 
                    case 13: {
                        AbstractSVGPathSegList.SVGPathSegArcItem ps = (AbstractSVGPathSegList.SVGPathSegLinetoHorizontalItem)s;
                        int n = j[0];
                        j[0] = n + 1;
                        ps.setX(parameters[n]);
                        continue block11;
                    }
                    case 14: 
                    case 15: {
                        AbstractSVGPathSegList.SVGPathSegArcItem ps = (AbstractSVGPathSegList.SVGPathSegLinetoVerticalItem)s;
                        int n = j[0];
                        j[0] = n + 1;
                        ps.setY(parameters[n]);
                        continue block11;
                    }
                }
            }
            while (i < commands.length) {
                this.appendItemImpl(this.newItem(commands[i], parameters, j));
                ++i;
            }
            while (size > commands.length) {
                this.removeItemImpl(--size);
            }
        }

        protected void resetAttribute() {
        }

        protected void resetAttribute(SVGItem item) {
        }

        protected void revalidate() {
            this.valid = true;
        }
    }

    public class NormalizedBaseSVGPathSegList
    extends AbstractSVGNormPathSegList {
        protected boolean missing;
        protected boolean malformed;

        protected DOMException createDOMException(short type, String key, Object[] args) {
            return SVGOMAnimatedPathData.this.element.createDOMException(type, key, args);
        }

        protected SVGException createSVGException(short type, String key, Object[] args) {
            return ((SVGOMElement)SVGOMAnimatedPathData.this.element).createSVGException(type, key, args);
        }

        protected String getValueAsString() throws SVGException {
            Attr attr = SVGOMAnimatedPathData.this.element.getAttributeNodeNS(SVGOMAnimatedPathData.this.namespaceURI, SVGOMAnimatedPathData.this.localName);
            if (attr == null) {
                return SVGOMAnimatedPathData.this.defaultValue;
            }
            return attr.getValue();
        }

        protected void setAttributeValue(String value) {
            try {
                SVGOMAnimatedPathData.this.changing = true;
                SVGOMAnimatedPathData.this.element.setAttributeNS(SVGOMAnimatedPathData.this.namespaceURI, SVGOMAnimatedPathData.this.localName, value);
            }
            finally {
                SVGOMAnimatedPathData.this.changing = false;
            }
        }

        protected void revalidate() {
            if (this.valid) {
                return;
            }
            this.valid = true;
            this.missing = false;
            this.malformed = false;
            String s = this.getValueAsString();
            if (s == null) {
                this.missing = true;
                return;
            }
            try {
                ListBuilder builder = new ListBuilder((AbstractSVGList)this);
                this.doParse(s, (ListHandler)builder);
                if (builder.getList() != null) {
                    this.clear(this.itemList);
                }
                this.itemList = builder.getList();
            }
            catch (ParseException e) {
                this.itemList = new ArrayList(1);
                this.malformed = true;
            }
        }
    }

    public class BaseSVGPathSegList
    extends AbstractSVGPathSegList {
        protected boolean missing;
        protected boolean malformed;

        protected DOMException createDOMException(short type, String key, Object[] args) {
            return SVGOMAnimatedPathData.this.element.createDOMException(type, key, args);
        }

        protected SVGException createSVGException(short type, String key, Object[] args) {
            return ((SVGOMElement)SVGOMAnimatedPathData.this.element).createSVGException(type, key, args);
        }

        protected String getValueAsString() {
            Attr attr = SVGOMAnimatedPathData.this.element.getAttributeNodeNS(SVGOMAnimatedPathData.this.namespaceURI, SVGOMAnimatedPathData.this.localName);
            if (attr == null) {
                return SVGOMAnimatedPathData.this.defaultValue;
            }
            return attr.getValue();
        }

        protected void setAttributeValue(String value) {
            try {
                SVGOMAnimatedPathData.this.changing = true;
                SVGOMAnimatedPathData.this.element.setAttributeNS(SVGOMAnimatedPathData.this.namespaceURI, SVGOMAnimatedPathData.this.localName, value);
            }
            finally {
                SVGOMAnimatedPathData.this.changing = false;
            }
        }

        protected void resetAttribute() {
            super.resetAttribute();
            this.missing = false;
            this.malformed = false;
        }

        protected void resetAttribute(SVGItem item) {
            super.resetAttribute(item);
            this.missing = false;
            this.malformed = false;
        }

        protected void revalidate() {
            if (this.valid) {
                return;
            }
            this.valid = true;
            this.missing = false;
            this.malformed = false;
            String s = this.getValueAsString();
            if (s == null) {
                this.missing = true;
                return;
            }
            try {
                ListBuilder builder = new ListBuilder((AbstractSVGList)this);
                this.doParse(s, (ListHandler)builder);
                if (builder.getList() != null) {
                    this.clear(this.itemList);
                }
                this.itemList = builder.getList();
            }
            catch (ParseException e) {
                this.itemList = new ArrayList(1);
                this.malformed = true;
            }
        }
    }
}

