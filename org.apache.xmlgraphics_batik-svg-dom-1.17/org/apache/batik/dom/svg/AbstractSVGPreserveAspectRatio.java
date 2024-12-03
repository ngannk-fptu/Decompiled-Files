/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.parser.DefaultPreserveAspectRatioHandler
 *  org.apache.batik.parser.ParseException
 *  org.apache.batik.parser.PreserveAspectRatioHandler
 *  org.apache.batik.parser.PreserveAspectRatioParser
 *  org.apache.batik.util.SVGConstants
 *  org.w3c.dom.svg.SVGPreserveAspectRatio
 */
package org.apache.batik.dom.svg;

import org.apache.batik.parser.DefaultPreserveAspectRatioHandler;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PreserveAspectRatioHandler;
import org.apache.batik.parser.PreserveAspectRatioParser;
import org.apache.batik.util.SVGConstants;
import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGPreserveAspectRatio;

public abstract class AbstractSVGPreserveAspectRatio
implements SVGPreserveAspectRatio,
SVGConstants {
    protected static final String[] ALIGN_VALUES = new String[]{null, "none", "xMinYMin", "xMidYMin", "xMaxYMin", "xMinYMid", "xMidYMid", "xMaxYMid", "xMinYMax", "xMidYMax", "xMaxYMax"};
    protected static final String[] MEET_OR_SLICE_VALUES = new String[]{null, "meet", "slice"};
    protected short align = (short)6;
    protected short meetOrSlice = 1;

    public static String getValueAsString(short align, short meetOrSlice) {
        if (align < 1 || align > 10) {
            return null;
        }
        String value = ALIGN_VALUES[align];
        if (align == 1) {
            return value;
        }
        if (meetOrSlice < 1 || meetOrSlice > 2) {
            return null;
        }
        return value + ' ' + MEET_OR_SLICE_VALUES[meetOrSlice];
    }

    public short getAlign() {
        return this.align;
    }

    public short getMeetOrSlice() {
        return this.meetOrSlice;
    }

    public void setAlign(short align) {
        this.align = align;
        this.setAttributeValue(this.getValueAsString());
    }

    public void setMeetOrSlice(short meetOrSlice) {
        this.meetOrSlice = meetOrSlice;
        this.setAttributeValue(this.getValueAsString());
    }

    public void reset() {
        this.align = (short)6;
        this.meetOrSlice = 1;
    }

    protected abstract void setAttributeValue(String var1) throws DOMException;

    protected abstract DOMException createDOMException(short var1, String var2, Object[] var3);

    protected void setValueAsString(String value) throws DOMException {
        PreserveAspectRatioParserHandler ph = new PreserveAspectRatioParserHandler();
        try {
            PreserveAspectRatioParser p = new PreserveAspectRatioParser();
            p.setPreserveAspectRatioHandler((PreserveAspectRatioHandler)ph);
            p.parse(value);
            this.align = ph.getAlign();
            this.meetOrSlice = ph.getMeetOrSlice();
        }
        catch (ParseException ex) {
            throw this.createDOMException((short)13, "preserve.aspect.ratio", new Object[]{value});
        }
    }

    public String getValueAsString() {
        if (this.align < 1 || this.align > 10) {
            throw this.createDOMException((short)13, "preserve.aspect.ratio.align", new Object[]{(int)this.align});
        }
        String value = ALIGN_VALUES[this.align];
        if (this.align == 1) {
            return value;
        }
        if (this.meetOrSlice < 1 || this.meetOrSlice > 2) {
            throw this.createDOMException((short)13, "preserve.aspect.ratio.meet.or.slice", new Object[]{(int)this.meetOrSlice});
        }
        return value + ' ' + MEET_OR_SLICE_VALUES[this.meetOrSlice];
    }

    protected static class PreserveAspectRatioParserHandler
    extends DefaultPreserveAspectRatioHandler {
        public short align = (short)6;
        public short meetOrSlice = 1;

        protected PreserveAspectRatioParserHandler() {
        }

        public short getAlign() {
            return this.align;
        }

        public short getMeetOrSlice() {
            return this.meetOrSlice;
        }

        public void none() throws ParseException {
            this.align = 1;
        }

        public void xMaxYMax() throws ParseException {
            this.align = (short)10;
        }

        public void xMaxYMid() throws ParseException {
            this.align = (short)7;
        }

        public void xMaxYMin() throws ParseException {
            this.align = (short)4;
        }

        public void xMidYMax() throws ParseException {
            this.align = (short)9;
        }

        public void xMidYMid() throws ParseException {
            this.align = (short)6;
        }

        public void xMidYMin() throws ParseException {
            this.align = (short)3;
        }

        public void xMinYMax() throws ParseException {
            this.align = (short)8;
        }

        public void xMinYMid() throws ParseException {
            this.align = (short)5;
        }

        public void xMinYMin() throws ParseException {
            this.align = (short)2;
        }

        public void meet() throws ParseException {
            this.meetOrSlice = 1;
        }

        public void slice() throws ParseException {
            this.meetOrSlice = (short)2;
        }
    }
}

