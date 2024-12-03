/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.parser;

import java.awt.Shape;
import java.io.IOException;
import java.io.Reader;
import org.apache.batik.parser.AWTPolylineProducer;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PointsParser;

public class AWTPolygonProducer
extends AWTPolylineProducer {
    public static Shape createShape(Reader r, int wr) throws IOException, ParseException {
        PointsParser p = new PointsParser();
        AWTPolygonProducer ph = new AWTPolygonProducer();
        ph.setWindingRule(wr);
        p.setPointsHandler(ph);
        p.parse(r);
        return ph.getShape();
    }

    @Override
    public void endPoints() throws ParseException {
        this.path.closePath();
    }
}

