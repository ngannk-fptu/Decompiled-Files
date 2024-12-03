/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.parser;

import java.util.Calendar;
import org.apache.batik.parser.TimingSpecifierHandler;

public class DefaultTimingSpecifierHandler
implements TimingSpecifierHandler {
    public static final TimingSpecifierHandler INSTANCE = new DefaultTimingSpecifierHandler();

    protected DefaultTimingSpecifierHandler() {
    }

    @Override
    public void offset(float offset) {
    }

    @Override
    public void syncbase(float offset, String syncbaseID, String timeSymbol) {
    }

    @Override
    public void eventbase(float offset, String eventbaseID, String eventType) {
    }

    @Override
    public void repeat(float offset, String syncbaseID) {
    }

    @Override
    public void repeat(float offset, String syncbaseID, int repeatIteration) {
    }

    @Override
    public void accesskey(float offset, char key) {
    }

    @Override
    public void accessKeySVG12(float offset, String keyName) {
    }

    @Override
    public void mediaMarker(String syncbaseID, String markerName) {
    }

    @Override
    public void wallclock(Calendar time) {
    }

    @Override
    public void indefinite() {
    }
}

