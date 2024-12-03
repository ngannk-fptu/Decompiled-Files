/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.sl.draw.geom;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.sl.draw.geom.AdjustPoint;
import org.apache.poi.sl.draw.geom.AdjustValue;
import org.apache.poi.sl.draw.geom.AdjustValueIf;
import org.apache.poi.sl.draw.geom.ArcToCommand;
import org.apache.poi.sl.draw.geom.ClosePathCommand;
import org.apache.poi.sl.draw.geom.ConnectionSite;
import org.apache.poi.sl.draw.geom.CurveToCommand;
import org.apache.poi.sl.draw.geom.CustomGeometry;
import org.apache.poi.sl.draw.geom.Guide;
import org.apache.poi.sl.draw.geom.LineToCommand;
import org.apache.poi.sl.draw.geom.MoveToCommand;
import org.apache.poi.sl.draw.geom.Path;
import org.apache.poi.sl.draw.geom.PolarAdjustHandle;
import org.apache.poi.sl.draw.geom.QuadToCommand;
import org.apache.poi.sl.draw.geom.XYAdjustHandle;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.util.Internal;

@Internal
class PresetParser {
    private static final Logger LOG = LogManager.getLogger(PresetParser.class);
    private Mode mode;
    private final Map<String, CustomGeometry> geom = new HashMap<String, CustomGeometry>();
    private CustomGeometry customGeometry;
    private boolean useAdjustValue;
    private Path path;

    PresetParser(Mode mode) {
        this.mode = mode;
        if (mode == Mode.SHAPE) {
            this.customGeometry = new CustomGeometry();
            this.geom.put("custom", this.customGeometry);
        }
    }

    void parse(XMLStreamReader sr) throws XMLStreamException {
        block5: while (sr.hasNext()) {
            switch (sr.next()) {
                case 1: {
                    this.mode.handler.update(this, sr);
                    continue block5;
                }
                case 2: {
                    this.endContext();
                    continue block5;
                }
                case 8: {
                    return;
                }
            }
        }
    }

    Map<String, CustomGeometry> getGeom() {
        return this.geom;
    }

    private void updateFile(XMLStreamReader sr) {
        String name = sr.getLocalName();
        assert ("presetShapeDefinitons".equals(name));
        this.mode = Mode.SHAPE_LST;
    }

    private void updateShapeList(XMLStreamReader sr) {
        String name = sr.getLocalName();
        this.customGeometry = new CustomGeometry();
        if (this.geom.containsKey(name)) {
            LOG.atWarn().log("Duplicate definition of {}", (Object)name);
        }
        this.geom.put(name, this.customGeometry);
        this.mode = Mode.SHAPE;
    }

    private void updateShape(XMLStreamReader sr) throws XMLStreamException {
        String name;
        switch (name = sr.getLocalName()) {
            case "avLst": {
                this.useAdjustValue = true;
                this.mode = Mode.GUIDE_LST;
                break;
            }
            case "gdLst": {
                this.useAdjustValue = false;
                this.mode = Mode.GUIDE_LST;
                break;
            }
            case "ahLst": {
                this.mode = Mode.AH_LST;
                break;
            }
            case "cxnLst": {
                this.mode = Mode.CXN_LST;
                break;
            }
            case "rect": {
                this.addRectangle(sr);
                break;
            }
            case "pathLst": {
                this.mode = Mode.PATH_LST;
            }
        }
    }

    private void updateGuideList(XMLStreamReader sr) throws XMLStreamException {
        Guide gd;
        String name = sr.getLocalName();
        assert ("gd".equals(name));
        if (this.useAdjustValue) {
            gd = new AdjustValue();
            this.customGeometry.addAdjustGuide((AdjustValueIf)((Object)gd));
        } else {
            gd = new Guide();
            this.customGeometry.addGeomGuide(gd);
        }
        this.parseAttributes(sr, (key, val) -> {
            switch (key) {
                case "name": {
                    gd.setName(PresetParser.collapseString(val));
                    break;
                }
                case "fmla": {
                    gd.setFmla((String)val);
                }
            }
        });
        int tag = PresetParser.nextTag(sr);
        assert (tag == 2);
    }

    private void updateAhList(XMLStreamReader sr) throws XMLStreamException {
        String name;
        switch (name = sr.getLocalName()) {
            case "ahXY": {
                this.addXY(sr);
                break;
            }
            case "ahPolar": {
                this.addPolar(sr);
            }
        }
    }

    private void addXY(XMLStreamReader sr) throws XMLStreamException {
        XYAdjustHandle ahXY = new XYAdjustHandle();
        this.customGeometry.addAdjustHandle(ahXY);
        this.parseAttributes(sr, (key, val) -> {
            switch (key) {
                case "gdRefX": {
                    ahXY.setGdRefX(PresetParser.collapseString(val));
                    break;
                }
                case "minX": {
                    ahXY.setMinX((String)val);
                    break;
                }
                case "maxX": {
                    ahXY.setMaxX((String)val);
                    break;
                }
                case "gdRefY": {
                    ahXY.setGdRefY(PresetParser.collapseString(val));
                    break;
                }
                case "minY": {
                    ahXY.setMinY((String)val);
                    break;
                }
                case "maxY": {
                    ahXY.setMaxY((String)val);
                }
            }
        });
        ahXY.setPos(this.parsePosPoint(sr));
    }

    private void addPolar(XMLStreamReader sr) throws XMLStreamException {
        PolarAdjustHandle ahPolar = new PolarAdjustHandle();
        this.customGeometry.addAdjustHandle(ahPolar);
        this.parseAttributes(sr, (key, val) -> {
            switch (key) {
                case "gdRefR": {
                    ahPolar.setGdRefR(PresetParser.collapseString(val));
                    break;
                }
                case "minR": {
                    ahPolar.setMinR((String)val);
                    break;
                }
                case "maxR": {
                    ahPolar.setMaxR((String)val);
                    break;
                }
                case "gdRefAng": {
                    ahPolar.setGdRefAng(PresetParser.collapseString(val));
                    break;
                }
                case "minAng": {
                    ahPolar.setMinAng((String)val);
                    break;
                }
                case "maxAng": {
                    ahPolar.setMaxAng((String)val);
                }
            }
        });
        ahPolar.setPos(this.parsePosPoint(sr));
    }

    private void updateCxnList(XMLStreamReader sr) throws XMLStreamException {
        String name = sr.getLocalName();
        assert ("cxn".equals(name));
        ConnectionSite cxn = new ConnectionSite();
        this.customGeometry.addConnectionSite(cxn);
        this.parseAttributes(sr, (key, val) -> {
            if ("ang".equals(key)) {
                cxn.setAng((String)val);
            }
        });
        cxn.setPos(this.parsePosPoint(sr));
    }

    private void updatePathLst(XMLStreamReader sr) {
        String name = sr.getLocalName();
        assert ("path".equals(name));
        this.path = new Path();
        this.customGeometry.addPath(this.path);
        this.parseAttributes(sr, (key, val) -> {
            switch (key) {
                case "w": {
                    this.path.setW(Long.parseLong(val));
                    break;
                }
                case "h": {
                    this.path.setH(Long.parseLong(val));
                    break;
                }
                case "fill": {
                    this.path.setFill(PresetParser.mapFill(val));
                    break;
                }
                case "stroke": {
                    this.path.setStroke(Boolean.parseBoolean(val));
                    break;
                }
                case "extrusionOk": {
                    this.path.setExtrusionOk(Boolean.parseBoolean(val));
                }
            }
        });
        this.mode = Mode.PATH;
    }

    private static PaintStyle.PaintModifier mapFill(String fill) {
        switch (fill) {
            default: {
                return PaintStyle.PaintModifier.NONE;
            }
            case "norm": {
                return PaintStyle.PaintModifier.NORM;
            }
            case "lighten": {
                return PaintStyle.PaintModifier.LIGHTEN;
            }
            case "lightenLess": {
                return PaintStyle.PaintModifier.LIGHTEN_LESS;
            }
            case "darken": {
                return PaintStyle.PaintModifier.DARKEN;
            }
            case "darkenLess": 
        }
        return PaintStyle.PaintModifier.DARKEN_LESS;
    }

    private void updatePath(XMLStreamReader sr) throws XMLStreamException {
        String name;
        switch (name = sr.getLocalName()) {
            case "close": {
                this.closePath(sr);
                break;
            }
            case "moveTo": {
                this.moveTo(sr);
                break;
            }
            case "lnTo": {
                this.lineTo(sr);
                break;
            }
            case "arcTo": {
                this.arcTo(sr);
                break;
            }
            case "quadBezTo": {
                this.quadBezTo(sr);
                break;
            }
            case "cubicBezTo": {
                this.cubicBezTo(sr);
            }
        }
    }

    private void closePath(XMLStreamReader sr) throws XMLStreamException {
        this.path.addCommand(new ClosePathCommand());
        int tag = PresetParser.nextTag(sr);
        assert (tag == 2);
    }

    private void moveTo(XMLStreamReader sr) throws XMLStreamException {
        MoveToCommand cmd = new MoveToCommand();
        this.path.addCommand(cmd);
        AdjustPoint pt = this.parsePtPoint(sr, true);
        assert (pt != null);
        cmd.setPt(pt);
    }

    private void lineTo(XMLStreamReader sr) throws XMLStreamException {
        LineToCommand cmd = new LineToCommand();
        this.path.addCommand(cmd);
        AdjustPoint pt = this.parsePtPoint(sr, true);
        assert (pt != null);
        cmd.setPt(pt);
    }

    private void arcTo(XMLStreamReader sr) throws XMLStreamException {
        ArcToCommand cmd = new ArcToCommand();
        this.path.addCommand(cmd);
        this.parseAttributes(sr, (key, val) -> {
            switch (key) {
                case "wR": {
                    cmd.setWR((String)val);
                    break;
                }
                case "hR": {
                    cmd.setHR((String)val);
                    break;
                }
                case "stAng": {
                    cmd.setStAng((String)val);
                    break;
                }
                case "swAng": {
                    cmd.setSwAng((String)val);
                }
            }
        });
        int tag = PresetParser.nextTag(sr);
        assert (tag == 2);
    }

    private void quadBezTo(XMLStreamReader sr) throws XMLStreamException {
        QuadToCommand cmd = new QuadToCommand();
        this.path.addCommand(cmd);
        AdjustPoint pt1 = this.parsePtPoint(sr, false);
        AdjustPoint pt2 = this.parsePtPoint(sr, true);
        assert (pt1 != null && pt2 != null);
        cmd.setPt1(pt1);
        cmd.setPt2(pt2);
    }

    private void cubicBezTo(XMLStreamReader sr) throws XMLStreamException {
        CurveToCommand cmd = new CurveToCommand();
        this.path.addCommand(cmd);
        AdjustPoint pt1 = this.parsePtPoint(sr, false);
        AdjustPoint pt2 = this.parsePtPoint(sr, false);
        AdjustPoint pt3 = this.parsePtPoint(sr, true);
        assert (pt1 != null && pt2 != null && pt3 != null);
        cmd.setPt1(pt1);
        cmd.setPt2(pt2);
        cmd.setPt3(pt3);
    }

    private void addRectangle(XMLStreamReader sr) throws XMLStreamException {
        String[] ltrb = new String[4];
        this.parseAttributes(sr, (key, val) -> {
            switch (key) {
                case "l": {
                    ltrb[0] = val;
                    break;
                }
                case "t": {
                    ltrb[1] = val;
                    break;
                }
                case "r": {
                    ltrb[2] = val;
                    break;
                }
                case "b": {
                    ltrb[3] = val;
                }
            }
        });
        this.customGeometry.setTextBounds(ltrb[0], ltrb[1], ltrb[2], ltrb[3]);
        int tag = PresetParser.nextTag(sr);
        assert (tag == 2);
    }

    private void endContext() {
        switch (this.mode) {
            case FILE: 
            case SHAPE_LST: {
                this.mode = Mode.FILE;
                break;
            }
            case SHAPE: {
                this.mode = Mode.SHAPE_LST;
                break;
            }
            case CXN_LST: 
            case AH_LST: 
            case GUIDE_LST: 
            case PATH_LST: {
                this.useAdjustValue = false;
                this.path = null;
                this.mode = Mode.SHAPE;
                break;
            }
            case PATH: {
                this.path = null;
                this.mode = Mode.PATH_LST;
            }
        }
    }

    private AdjustPoint parsePosPoint(XMLStreamReader sr) throws XMLStreamException {
        return this.parseAdjPoint(sr, true, "pos");
    }

    private AdjustPoint parsePtPoint(XMLStreamReader sr, boolean closeOuter) throws XMLStreamException {
        return this.parseAdjPoint(sr, closeOuter, "pt");
    }

    private AdjustPoint parseAdjPoint(XMLStreamReader sr, boolean closeOuter, String name) throws XMLStreamException {
        int tag = PresetParser.nextTag(sr);
        if (tag == 2) {
            return null;
        }
        assert (name.equals(sr.getLocalName()));
        AdjustPoint pos = new AdjustPoint();
        this.parseAttributes(sr, (key, val) -> {
            switch (key) {
                case "x": {
                    pos.setX((String)val);
                    break;
                }
                case "y": {
                    pos.setY((String)val);
                }
            }
        });
        tag = PresetParser.nextTag(sr);
        assert (tag == 2);
        if (closeOuter) {
            tag = PresetParser.nextTag(sr);
            assert (tag == 2);
        }
        return pos;
    }

    private void parseAttributes(XMLStreamReader sr, BiConsumer<String, String> c) {
        for (int i = 0; i < sr.getAttributeCount(); ++i) {
            c.accept(sr.getAttributeLocalName(i), sr.getAttributeValue(i));
        }
    }

    private static int nextTag(XMLStreamReader sr) throws XMLStreamException {
        int tag;
        while ((tag = sr.next()) != 1 && tag != 2 && tag != 8) {
        }
        return tag;
    }

    private static String collapseString(String text) {
        int s;
        if (text == null) {
            return null;
        }
        int len = text.length();
        for (s = 0; s < len && !PresetParser.isWhiteSpace(text.charAt(s)); ++s) {
        }
        if (s == len) {
            return text;
        }
        StringBuilder result = new StringBuilder(len);
        if (s != 0) {
            for (int i = 0; i < s; ++i) {
                result.append(text.charAt(i));
            }
            result.append(' ');
        }
        boolean inStripMode = true;
        for (int i = s + 1; i < len; ++i) {
            char ch = text.charAt(i);
            boolean b = PresetParser.isWhiteSpace(ch);
            if (inStripMode && b) continue;
            inStripMode = b;
            result.append(inStripMode ? (char)' ' : (char)ch);
        }
        len = result.length();
        if (len > 0 && result.charAt(len - 1) == ' ') {
            result.setLength(len - 1);
        }
        return result.toString();
    }

    private static boolean isWhiteSpace(char ch) {
        return ch == '\t' || ch == '\n' || ch == '\r' || ch == ' ';
    }

    static enum Mode {
        FILE((rec$, x$0) -> PresetParser.access$700(rec$, x$0)),
        SHAPE_LST((rec$, x$0) -> PresetParser.access$600(rec$, x$0)),
        SHAPE((rec$, x$0) -> PresetParser.access$500(rec$, x$0)),
        GUIDE_LST((rec$, x$0) -> PresetParser.access$400(rec$, x$0)),
        AH_LST((rec$, x$0) -> PresetParser.access$300(rec$, x$0)),
        CXN_LST((rec$, x$0) -> PresetParser.access$200(rec$, x$0)),
        PATH_LST((rec$, x$0) -> PresetParser.access$100(rec$, x$0)),
        PATH((rec$, x$0) -> PresetParser.access$000(rec$, x$0));

        final Handler handler;

        private Mode(Handler handler) {
            this.handler = handler;
        }

        static interface Handler {
            public void update(PresetParser var1, XMLStreamReader var2) throws XMLStreamException;
        }
    }
}

