/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.parser;

import java.io.IOException;
import org.apache.batik.parser.DefaultPointsHandler;
import org.apache.batik.parser.NumberParser;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PointsHandler;

public class PointsParser
extends NumberParser {
    protected PointsHandler pointsHandler = DefaultPointsHandler.INSTANCE;
    protected boolean eRead;

    public void setPointsHandler(PointsHandler handler) {
        this.pointsHandler = handler;
    }

    public PointsHandler getPointsHandler() {
        return this.pointsHandler;
    }

    @Override
    protected void doParse() throws ParseException, IOException {
        this.pointsHandler.startPoints();
        this.current = this.reader.read();
        this.skipSpaces();
        while (this.current != -1) {
            float x = this.parseFloat();
            this.skipCommaSpaces();
            float y = this.parseFloat();
            this.pointsHandler.point(x, y);
            this.skipCommaSpaces();
        }
        this.pointsHandler.endPoints();
    }
}

