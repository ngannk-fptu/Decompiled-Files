/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.parser.DefaultTimingSpecifierListHandler
 *  org.apache.batik.parser.TimingSpecifierListHandler
 *  org.apache.batik.parser.TimingSpecifierListParser
 */
package org.apache.batik.anim.timing;

import java.util.Calendar;
import java.util.LinkedList;
import org.apache.batik.anim.timing.AccesskeyTimingSpecifier;
import org.apache.batik.anim.timing.EventbaseTimingSpecifier;
import org.apache.batik.anim.timing.IndefiniteTimingSpecifier;
import org.apache.batik.anim.timing.MediaMarkerTimingSpecifier;
import org.apache.batik.anim.timing.OffsetTimingSpecifier;
import org.apache.batik.anim.timing.RepeatTimingSpecifier;
import org.apache.batik.anim.timing.SyncbaseTimingSpecifier;
import org.apache.batik.anim.timing.TimedElement;
import org.apache.batik.anim.timing.TimingSpecifier;
import org.apache.batik.anim.timing.WallclockTimingSpecifier;
import org.apache.batik.parser.DefaultTimingSpecifierListHandler;
import org.apache.batik.parser.TimingSpecifierListHandler;
import org.apache.batik.parser.TimingSpecifierListParser;

public class TimingSpecifierListProducer
extends DefaultTimingSpecifierListHandler {
    protected LinkedList timingSpecifiers = new LinkedList();
    protected TimedElement owner;
    protected boolean isBegin;

    public TimingSpecifierListProducer(TimedElement owner, boolean isBegin) {
        this.owner = owner;
        this.isBegin = isBegin;
    }

    public TimingSpecifier[] getTimingSpecifiers() {
        return this.timingSpecifiers.toArray(new TimingSpecifier[this.timingSpecifiers.size()]);
    }

    public static TimingSpecifier[] parseTimingSpecifierList(TimedElement owner, boolean isBegin, String spec, boolean useSVG11AccessKeys, boolean useSVG12AccessKeys) {
        TimingSpecifierListParser p = new TimingSpecifierListParser(useSVG11AccessKeys, useSVG12AccessKeys);
        TimingSpecifierListProducer pp = new TimingSpecifierListProducer(owner, isBegin);
        p.setTimingSpecifierListHandler((TimingSpecifierListHandler)pp);
        p.parse(spec);
        TimingSpecifier[] specs = pp.getTimingSpecifiers();
        return specs;
    }

    public void offset(float offset) {
        OffsetTimingSpecifier ts = new OffsetTimingSpecifier(this.owner, this.isBegin, offset);
        this.timingSpecifiers.add(ts);
    }

    public void syncbase(float offset, String syncbaseID, String timeSymbol) {
        SyncbaseTimingSpecifier ts = new SyncbaseTimingSpecifier(this.owner, this.isBegin, offset, syncbaseID, timeSymbol.charAt(0) == 'b');
        this.timingSpecifiers.add(ts);
    }

    public void eventbase(float offset, String eventbaseID, String eventType) {
        EventbaseTimingSpecifier ts = new EventbaseTimingSpecifier(this.owner, this.isBegin, offset, eventbaseID, eventType);
        this.timingSpecifiers.add(ts);
    }

    public void repeat(float offset, String syncbaseID) {
        RepeatTimingSpecifier ts = new RepeatTimingSpecifier(this.owner, this.isBegin, offset, syncbaseID);
        this.timingSpecifiers.add(ts);
    }

    public void repeat(float offset, String syncbaseID, int repeatIteration) {
        RepeatTimingSpecifier ts = new RepeatTimingSpecifier(this.owner, this.isBegin, offset, syncbaseID, repeatIteration);
        this.timingSpecifiers.add(ts);
    }

    public void accesskey(float offset, char key) {
        AccesskeyTimingSpecifier ts = new AccesskeyTimingSpecifier(this.owner, this.isBegin, offset, key);
        this.timingSpecifiers.add(ts);
    }

    public void accessKeySVG12(float offset, String keyName) {
        AccesskeyTimingSpecifier ts = new AccesskeyTimingSpecifier(this.owner, this.isBegin, offset, keyName);
        this.timingSpecifiers.add(ts);
    }

    public void mediaMarker(String syncbaseID, String markerName) {
        MediaMarkerTimingSpecifier ts = new MediaMarkerTimingSpecifier(this.owner, this.isBegin, syncbaseID, markerName);
        this.timingSpecifiers.add(ts);
    }

    public void wallclock(Calendar time) {
        WallclockTimingSpecifier ts = new WallclockTimingSpecifier(this.owner, this.isBegin, time);
        this.timingSpecifiers.add(ts);
    }

    public void indefinite() {
        IndefiniteTimingSpecifier ts = new IndefiniteTimingSpecifier(this.owner, this.isBegin);
        this.timingSpecifiers.add(ts);
    }
}

