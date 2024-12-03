/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.i18n.LocalizableSupport
 *  org.apache.batik.parser.ClockHandler
 *  org.apache.batik.parser.ClockParser
 *  org.apache.batik.parser.ParseException
 *  org.apache.batik.util.SMILConstants
 */
package org.apache.batik.anim.timing;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;
import org.apache.batik.anim.AnimationException;
import org.apache.batik.anim.timing.EventLikeTimingSpecifier;
import org.apache.batik.anim.timing.InstanceTime;
import org.apache.batik.anim.timing.Interval;
import org.apache.batik.anim.timing.TimeContainer;
import org.apache.batik.anim.timing.TimedDocumentRoot;
import org.apache.batik.anim.timing.TimingSpecifier;
import org.apache.batik.anim.timing.TimingSpecifierListProducer;
import org.apache.batik.i18n.LocalizableSupport;
import org.apache.batik.parser.ClockHandler;
import org.apache.batik.parser.ClockParser;
import org.apache.batik.parser.ParseException;
import org.apache.batik.util.SMILConstants;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventTarget;

public abstract class TimedElement
implements SMILConstants {
    public static final int FILL_REMOVE = 0;
    public static final int FILL_FREEZE = 1;
    public static final int RESTART_ALWAYS = 0;
    public static final int RESTART_WHEN_NOT_ACTIVE = 1;
    public static final int RESTART_NEVER = 2;
    public static final float INDEFINITE = Float.POSITIVE_INFINITY;
    public static final float UNRESOLVED = Float.NaN;
    protected TimedDocumentRoot root;
    protected TimeContainer parent;
    protected TimingSpecifier[] beginTimes;
    protected TimingSpecifier[] endTimes;
    protected float simpleDur;
    protected boolean durMedia;
    protected float repeatCount;
    protected float repeatDur;
    protected int currentRepeatIteration;
    protected float lastRepeatTime;
    protected int fillMode;
    protected int restartMode;
    protected float min;
    protected boolean minMedia;
    protected float max;
    protected boolean maxMedia;
    protected boolean isActive;
    protected boolean isFrozen;
    protected float lastSampleTime;
    protected float repeatDuration;
    protected List beginInstanceTimes = new ArrayList();
    protected List endInstanceTimes = new ArrayList();
    protected Interval currentInterval;
    protected float lastIntervalEnd;
    protected Interval previousInterval;
    protected LinkedList beginDependents = new LinkedList();
    protected LinkedList endDependents = new LinkedList();
    protected boolean shouldUpdateCurrentInterval = true;
    protected boolean hasParsed;
    protected Map handledEvents = new HashMap();
    protected boolean isSampling;
    protected boolean hasPropagated;
    protected static final String RESOURCES = "org.apache.batik.anim.resources.Messages";
    protected static LocalizableSupport localizableSupport = new LocalizableSupport("org.apache.batik.anim.resources.Messages", TimedElement.class.getClassLoader());

    public TimedElement() {
        this.beginTimes = new TimingSpecifier[0];
        this.endTimes = this.beginTimes;
        this.simpleDur = Float.NaN;
        this.repeatCount = Float.NaN;
        this.repeatDur = Float.NaN;
        this.lastRepeatTime = Float.NaN;
        this.max = Float.POSITIVE_INFINITY;
        this.lastSampleTime = Float.NaN;
        this.lastIntervalEnd = Float.NEGATIVE_INFINITY;
    }

    public TimedDocumentRoot getRoot() {
        return this.root;
    }

    public float getActiveTime() {
        return this.lastSampleTime;
    }

    public float getSimpleTime() {
        return this.lastSampleTime - this.lastRepeatTime;
    }

    protected float addInstanceTime(InstanceTime time, boolean isBegin) {
        this.hasPropagated = true;
        List instanceTimes = isBegin ? this.beginInstanceTimes : this.endInstanceTimes;
        int index = Collections.binarySearch(instanceTimes, time);
        if (index < 0) {
            index = -(index + 1);
        }
        instanceTimes.add(index, time);
        this.shouldUpdateCurrentInterval = true;
        float ret = this.root.isSampling() && !this.isSampling ? this.sampleAt(this.root.getCurrentTime(), this.root.isHyperlinking()) : Float.POSITIVE_INFINITY;
        this.hasPropagated = false;
        this.root.currentIntervalWillUpdate();
        return ret;
    }

    protected float removeInstanceTime(InstanceTime time, boolean isBegin) {
        int index;
        this.hasPropagated = true;
        List instanceTimes = isBegin ? this.beginInstanceTimes : this.endInstanceTimes;
        for (int i = index = Collections.binarySearch(instanceTimes, time); i >= 0; --i) {
            InstanceTime it = (InstanceTime)instanceTimes.get(i);
            if (it == time) {
                instanceTimes.remove(i);
                break;
            }
            if (it.compareTo(time) != 0) break;
        }
        int len = instanceTimes.size();
        for (int i = index + 1; i < len; ++i) {
            InstanceTime it = (InstanceTime)instanceTimes.get(i);
            if (it == time) {
                instanceTimes.remove(i);
                break;
            }
            if (it.compareTo(time) != 0) break;
        }
        this.shouldUpdateCurrentInterval = true;
        float ret = this.root.isSampling() && !this.isSampling ? this.sampleAt(this.root.getCurrentTime(), this.root.isHyperlinking()) : Float.POSITIVE_INFINITY;
        this.hasPropagated = false;
        this.root.currentIntervalWillUpdate();
        return ret;
    }

    protected float instanceTimeChanged(InstanceTime time, boolean isBegin) {
        this.hasPropagated = true;
        this.shouldUpdateCurrentInterval = true;
        float ret = this.root.isSampling() && !this.isSampling ? this.sampleAt(this.root.getCurrentTime(), this.root.isHyperlinking()) : Float.POSITIVE_INFINITY;
        this.hasPropagated = false;
        return ret;
    }

    protected void addDependent(TimingSpecifier dependent, boolean forBegin) {
        if (forBegin) {
            this.beginDependents.add(dependent);
        } else {
            this.endDependents.add(dependent);
        }
    }

    protected void removeDependent(TimingSpecifier dependent, boolean forBegin) {
        if (forBegin) {
            this.beginDependents.remove(dependent);
        } else {
            this.endDependents.remove(dependent);
        }
    }

    public float getSimpleDur() {
        if (this.durMedia) {
            return this.getImplicitDur();
        }
        if (TimedElement.isUnresolved(this.simpleDur)) {
            if (TimedElement.isUnresolved(this.repeatCount) && TimedElement.isUnresolved(this.repeatDur) && this.endTimes.length > 0) {
                return Float.POSITIVE_INFINITY;
            }
            return this.getImplicitDur();
        }
        return this.simpleDur;
    }

    public static boolean isUnresolved(float t) {
        return Float.isNaN(t);
    }

    public float getActiveDur(float B, float end) {
        float IAD;
        float d = this.getSimpleDur();
        if (!TimedElement.isUnresolved(end) && d == Float.POSITIVE_INFINITY) {
            float PAD = this.minusTime(end, B);
            this.repeatDuration = this.minTime(this.max, this.maxTime(this.min, PAD));
            return this.repeatDuration;
        }
        if (d == 0.0f) {
            IAD = 0.0f;
        } else if (TimedElement.isUnresolved(this.repeatDur) && TimedElement.isUnresolved(this.repeatCount)) {
            IAD = d;
        } else {
            float p1 = TimedElement.isUnresolved(this.repeatCount) ? Float.POSITIVE_INFINITY : this.multiplyTime(d, this.repeatCount);
            float p2 = TimedElement.isUnresolved(this.repeatDur) ? Float.POSITIVE_INFINITY : this.repeatDur;
            IAD = this.minTime(this.minTime(p1, p2), Float.POSITIVE_INFINITY);
        }
        float PAD = TimedElement.isUnresolved(end) || end == Float.POSITIVE_INFINITY ? IAD : this.minTime(IAD, this.minusTime(end, B));
        this.repeatDuration = IAD;
        return this.minTime(this.max, this.maxTime(this.min, PAD));
    }

    protected float minusTime(float t1, float t2) {
        if (TimedElement.isUnresolved(t1) || TimedElement.isUnresolved(t2)) {
            return Float.NaN;
        }
        if (t1 == Float.POSITIVE_INFINITY || t2 == Float.POSITIVE_INFINITY) {
            return Float.POSITIVE_INFINITY;
        }
        return t1 - t2;
    }

    protected float multiplyTime(float t, float n) {
        if (TimedElement.isUnresolved(t) || t == Float.POSITIVE_INFINITY) {
            return t;
        }
        return t * n;
    }

    protected float minTime(float t1, float t2) {
        if (t1 == 0.0f || t2 == 0.0f) {
            return 0.0f;
        }
        if ((t1 == Float.POSITIVE_INFINITY || TimedElement.isUnresolved(t1)) && t2 != Float.POSITIVE_INFINITY && !TimedElement.isUnresolved(t2)) {
            return t2;
        }
        if ((t2 == Float.POSITIVE_INFINITY || TimedElement.isUnresolved(t2)) && t1 != Float.POSITIVE_INFINITY && !TimedElement.isUnresolved(t1)) {
            return t1;
        }
        if (t1 == Float.POSITIVE_INFINITY && TimedElement.isUnresolved(t2) || TimedElement.isUnresolved(t1) && t2 == Float.POSITIVE_INFINITY) {
            return Float.POSITIVE_INFINITY;
        }
        if (t1 < t2) {
            return t1;
        }
        return t2;
    }

    protected float maxTime(float t1, float t2) {
        if ((t1 == Float.POSITIVE_INFINITY || TimedElement.isUnresolved(t1)) && t2 != Float.POSITIVE_INFINITY && !TimedElement.isUnresolved(t2)) {
            return t1;
        }
        if ((t2 == Float.POSITIVE_INFINITY || TimedElement.isUnresolved(t2)) && t1 != Float.POSITIVE_INFINITY && !TimedElement.isUnresolved(t1)) {
            return t2;
        }
        if (t1 == Float.POSITIVE_INFINITY && TimedElement.isUnresolved(t2) || TimedElement.isUnresolved(t1) && t2 == Float.POSITIVE_INFINITY) {
            return Float.NaN;
        }
        if (t1 > t2) {
            return t1;
        }
        return t2;
    }

    protected float getImplicitDur() {
        return Float.NaN;
    }

    protected float notifyNewInterval(Interval interval) {
        float t;
        float dependentMinTime = Float.POSITIVE_INFINITY;
        for (TimingSpecifier ts : this.beginDependents) {
            t = ts.newInterval(interval);
            if (!(t < dependentMinTime)) continue;
            dependentMinTime = t;
        }
        for (TimingSpecifier ts : this.endDependents) {
            t = ts.newInterval(interval);
            if (!(t < dependentMinTime)) continue;
            dependentMinTime = t;
        }
        return dependentMinTime;
    }

    protected float notifyRemoveInterval(Interval interval) {
        float t;
        float dependentMinTime = Float.POSITIVE_INFINITY;
        for (TimingSpecifier ts : this.beginDependents) {
            t = ts.removeInterval(interval);
            if (!(t < dependentMinTime)) continue;
            dependentMinTime = t;
        }
        for (TimingSpecifier ts : this.endDependents) {
            t = ts.removeInterval(interval);
            if (!(t < dependentMinTime)) continue;
            dependentMinTime = t;
        }
        return dependentMinTime;
    }

    protected float sampleAt(float parentSimpleTime, boolean hyperlinking) {
        float begin;
        boolean hasEnded;
        float begin2;
        this.isSampling = true;
        float time = parentSimpleTime;
        Iterator iterator = this.handledEvents.entrySet().iterator();
        while (iterator.hasNext()) {
            boolean useEnd;
            boolean useBegin;
            Map.Entry o;
            Map.Entry e = o = iterator.next();
            Event evt = (Event)e.getKey();
            Set ts = (Set)e.getValue();
            Iterator j = ts.iterator();
            boolean hasBegin = false;
            boolean hasEnd = false;
            while (!(!j.hasNext() || hasBegin && hasEnd)) {
                EventLikeTimingSpecifier t = (EventLikeTimingSpecifier)j.next();
                if (t.isBegin()) {
                    hasBegin = true;
                    continue;
                }
                hasEnd = true;
            }
            if (hasBegin && hasEnd) {
                useBegin = !this.isActive || this.restartMode == 0;
                useEnd = !useBegin;
            } else if (hasBegin && (!this.isActive || this.restartMode == 0)) {
                useBegin = true;
                useEnd = false;
            } else {
                if (!hasEnd || !this.isActive) continue;
                useBegin = false;
                useEnd = true;
            }
            for (EventLikeTimingSpecifier t : ts) {
                boolean isBegin = t.isBegin();
                if ((!isBegin || !useBegin) && (isBegin || !useEnd)) continue;
                t.resolve(evt);
                this.shouldUpdateCurrentInterval = true;
            }
        }
        this.handledEvents.clear();
        if (this.currentInterval != null && this.lastSampleTime < (begin2 = this.currentInterval.getBegin()) && time >= begin2) {
            if (!this.isActive) {
                this.toActive(begin2);
            }
            this.isActive = true;
            this.isFrozen = false;
            this.lastRepeatTime = begin2;
            this.fireTimeEvent("beginEvent", this.currentInterval.getBegin(), 0);
        }
        boolean bl = hasEnded = this.currentInterval != null && time >= this.currentInterval.getEnd();
        if (this.currentInterval != null && time >= (begin = this.currentInterval.getBegin())) {
            float d = this.getSimpleDur();
            while (time - this.lastRepeatTime >= d && this.lastRepeatTime + d < begin + this.repeatDuration) {
                this.lastRepeatTime += d;
                ++this.currentRepeatIteration;
                this.fireTimeEvent(this.root.getRepeatEventName(), this.lastRepeatTime, this.currentRepeatIteration);
            }
        }
        float dependentMinTime = Float.POSITIVE_INFINITY;
        if (hyperlinking) {
            this.shouldUpdateCurrentInterval = true;
        }
        while (this.shouldUpdateCurrentInterval || hasEnded) {
            boolean first;
            if (hasEnded) {
                this.previousInterval = this.currentInterval;
                this.isActive = false;
                this.isFrozen = this.fillMode == 1;
                this.toInactive(false, this.isFrozen);
                this.fireTimeEvent("endEvent", this.currentInterval.getEnd(), 0);
            }
            boolean bl2 = first = this.currentInterval == null && this.previousInterval == null;
            if (this.currentInterval != null && hyperlinking) {
                this.isActive = false;
                this.isFrozen = false;
                this.toInactive(false, false);
                this.currentInterval = null;
            }
            if (this.currentInterval == null || hasEnded) {
                if (first || hyperlinking || this.restartMode != 2) {
                    float beginAfter;
                    boolean incl = true;
                    if (first || hyperlinking) {
                        beginAfter = Float.NEGATIVE_INFINITY;
                    } else {
                        beginAfter = this.previousInterval.getEnd();
                        incl = beginAfter != this.previousInterval.getBegin();
                    }
                    Interval interval = this.computeInterval(first, false, beginAfter, incl);
                    if (interval == null) {
                        this.currentInterval = null;
                    } else {
                        float dmt = this.selectNewInterval(time, interval);
                        if (dmt < dependentMinTime) {
                            dependentMinTime = dmt;
                        }
                    }
                } else {
                    this.currentInterval = null;
                }
            } else {
                float currentBegin = this.currentInterval.getBegin();
                if (currentBegin > time) {
                    float beginAfter;
                    boolean incl = true;
                    if (this.previousInterval == null) {
                        beginAfter = Float.NEGATIVE_INFINITY;
                    } else {
                        beginAfter = this.previousInterval.getEnd();
                        incl = beginAfter != this.previousInterval.getBegin();
                    }
                    Interval interval = this.computeInterval(false, false, beginAfter, incl);
                    float dmt = this.notifyRemoveInterval(this.currentInterval);
                    if (dmt < dependentMinTime) {
                        dependentMinTime = dmt;
                    }
                    if (interval == null) {
                        this.currentInterval = null;
                    } else {
                        dmt = this.selectNewInterval(time, interval);
                        if (dmt < dependentMinTime) {
                            dependentMinTime = dmt;
                        }
                    }
                } else {
                    float dmt;
                    Interval interval = this.computeInterval(false, true, currentBegin, true);
                    float newEnd = interval.getEnd();
                    if (this.currentInterval.getEnd() != newEnd && (dmt = this.currentInterval.setEnd(newEnd, interval.getEndInstanceTime())) < dependentMinTime) {
                        dependentMinTime = dmt;
                    }
                }
            }
            this.shouldUpdateCurrentInterval = false;
            hyperlinking = false;
            hasEnded = this.currentInterval != null && time >= this.currentInterval.getEnd();
        }
        float d = this.getSimpleDur();
        if (this.isActive && !this.isFrozen) {
            if (time - this.currentInterval.getBegin() >= this.repeatDuration) {
                this.isFrozen = this.fillMode == 1;
                this.toInactive(true, this.isFrozen);
            } else {
                this.sampledAt(time - this.lastRepeatTime, d, this.currentRepeatIteration);
            }
        }
        if (this.isFrozen) {
            boolean atLast;
            float t;
            if (this.isActive) {
                t = this.currentInterval.getBegin() + this.repeatDuration - this.lastRepeatTime;
                atLast = this.lastRepeatTime + d == this.currentInterval.getBegin() + this.repeatDuration;
            } else {
                t = this.previousInterval.getEnd() - this.lastRepeatTime;
                boolean bl3 = atLast = this.lastRepeatTime + d == this.previousInterval.getEnd();
            }
            if (atLast) {
                this.sampledLastValue(this.currentRepeatIteration);
            } else {
                this.sampledAt(t % d, d, this.currentRepeatIteration);
            }
        } else if (!this.isActive) {
            // empty if block
        }
        this.isSampling = false;
        this.lastSampleTime = time;
        if (this.currentInterval != null) {
            float t = this.currentInterval.getBegin() - time;
            if (t <= 0.0f) {
                float f = t = this.isConstantAnimation() || this.isFrozen ? this.currentInterval.getEnd() - time : 0.0f;
            }
            if (dependentMinTime < t) {
                return dependentMinTime;
            }
            return t;
        }
        return dependentMinTime;
    }

    protected boolean endHasEventConditions() {
        for (TimingSpecifier endTime : this.endTimes) {
            if (!endTime.isEventCondition()) continue;
            return true;
        }
        return false;
    }

    protected float selectNewInterval(float time, Interval interval) {
        this.currentInterval = interval;
        float dmt = this.notifyNewInterval(this.currentInterval);
        float beginEventTime = this.currentInterval.getBegin();
        if (time >= beginEventTime) {
            this.lastRepeatTime = beginEventTime;
            if (beginEventTime < 0.0f) {
                beginEventTime = 0.0f;
            }
            this.toActive(beginEventTime);
            this.isActive = true;
            this.isFrozen = false;
            this.fireTimeEvent("beginEvent", beginEventTime, 0);
            float d = this.getSimpleDur();
            float end = this.currentInterval.getEnd();
            while (time - this.lastRepeatTime >= d && this.lastRepeatTime + d < end) {
                this.lastRepeatTime += d;
                ++this.currentRepeatIteration;
                this.fireTimeEvent(this.root.getRepeatEventName(), this.lastRepeatTime, this.currentRepeatIteration);
            }
        }
        return dmt;
    }

    protected Interval computeInterval(boolean first, boolean fixedBegin, float beginAfter, boolean incl) {
        Iterator beginIterator = this.beginInstanceTimes.iterator();
        Iterator endIterator = this.endInstanceTimes.iterator();
        float parentSimpleDur = this.parent.getSimpleDur();
        InstanceTime endInstanceTime = endIterator.hasNext() ? (InstanceTime)endIterator.next() : null;
        boolean firstEnd = true;
        InstanceTime beginInstanceTime = null;
        InstanceTime nextBeginInstanceTime = null;
        while (true) {
            float tempEnd;
            float tempBegin;
            if (fixedBegin) {
                tempBegin = beginAfter;
                while (beginIterator.hasNext() && !((nextBeginInstanceTime = (InstanceTime)beginIterator.next()).getTime() > tempBegin)) {
                }
            } else {
                while (true) {
                    if (!beginIterator.hasNext()) {
                        return null;
                    }
                    beginInstanceTime = (InstanceTime)beginIterator.next();
                    tempBegin = beginInstanceTime.getTime();
                    if (!(incl && tempBegin >= beginAfter) && (incl || !(tempBegin > beginAfter))) continue;
                    if (!beginIterator.hasNext()) break;
                    nextBeginInstanceTime = (InstanceTime)beginIterator.next();
                    if (beginInstanceTime.getTime() != nextBeginInstanceTime.getTime()) break;
                    nextBeginInstanceTime = null;
                }
            }
            if (tempBegin >= parentSimpleDur) {
                return null;
            }
            if (this.endTimes.length == 0) {
                tempEnd = tempBegin + this.getActiveDur(tempBegin, Float.POSITIVE_INFINITY);
            } else {
                if (this.endInstanceTimes.isEmpty()) {
                    tempEnd = Float.NaN;
                } else {
                    tempEnd = endInstanceTime.getTime();
                    if (first && !firstEnd && tempEnd == tempBegin || !first && this.currentInterval != null && tempEnd == this.currentInterval.getEnd() && (incl && beginAfter >= tempEnd || !incl && beginAfter > tempEnd)) {
                        do {
                            if (endIterator.hasNext()) continue;
                            if (this.endHasEventConditions()) {
                                tempEnd = Float.NaN;
                                break;
                            }
                            return null;
                        } while (!((tempEnd = (endInstanceTime = (InstanceTime)endIterator.next()).getTime()) > tempBegin));
                    }
                    firstEnd = false;
                    while (!(tempEnd >= tempBegin)) {
                        if (!endIterator.hasNext()) {
                            if (this.endHasEventConditions()) {
                                tempEnd = Float.NaN;
                                break;
                            }
                            return null;
                        }
                        endInstanceTime = (InstanceTime)endIterator.next();
                        tempEnd = endInstanceTime.getTime();
                    }
                }
                float ad = this.getActiveDur(tempBegin, tempEnd);
                tempEnd = tempBegin + ad;
            }
            if (!first || tempEnd > 0.0f || tempBegin == 0.0f && tempEnd == 0.0f || TimedElement.isUnresolved(tempEnd)) {
                float nextBegin;
                if (this.restartMode == 0 && nextBeginInstanceTime != null && ((nextBegin = nextBeginInstanceTime.getTime()) < tempEnd || TimedElement.isUnresolved(tempEnd))) {
                    tempEnd = nextBegin;
                    endInstanceTime = nextBeginInstanceTime;
                }
                Interval i = new Interval(tempBegin, tempEnd, beginInstanceTime, endInstanceTime);
                return i;
            }
            if (fixedBegin) {
                return null;
            }
            beginAfter = tempEnd;
        }
    }

    protected void reset(boolean clearCurrentBegin) {
        InstanceTime it;
        Iterator i = this.beginInstanceTimes.iterator();
        while (i.hasNext()) {
            it = (InstanceTime)i.next();
            if (!it.getClearOnReset() || !clearCurrentBegin && this.currentInterval != null && this.currentInterval.getBeginInstanceTime() == it) continue;
            i.remove();
        }
        i = this.endInstanceTimes.iterator();
        while (i.hasNext()) {
            it = (InstanceTime)i.next();
            if (!it.getClearOnReset()) continue;
            i.remove();
        }
        if (this.isFrozen) {
            this.removeFill();
        }
        this.currentRepeatIteration = 0;
        this.lastRepeatTime = Float.NaN;
        this.isActive = false;
        this.isFrozen = false;
        this.lastSampleTime = Float.NaN;
    }

    public void parseAttributes(String begin, String dur, String end, String min, String max, String repeatCount, String repeatDur, String fill, String restart) {
        if (!this.hasParsed) {
            this.parseBegin(begin);
            this.parseDur(dur);
            this.parseEnd(end);
            this.parseMin(min);
            this.parseMax(max);
            if (this.min > this.max) {
                this.min = 0.0f;
                this.max = Float.POSITIVE_INFINITY;
            }
            this.parseRepeatCount(repeatCount);
            this.parseRepeatDur(repeatDur);
            this.parseFill(fill);
            this.parseRestart(restart);
            this.hasParsed = true;
        }
    }

    protected void parseBegin(String begin) {
        try {
            if (begin.length() == 0) {
                begin = "0";
            }
            this.beginTimes = TimingSpecifierListProducer.parseTimingSpecifierList(this, true, begin, this.root.useSVG11AccessKeys, this.root.useSVG12AccessKeys);
        }
        catch (ParseException ex) {
            throw this.createException("attribute.malformed", new Object[]{null, "begin"});
        }
    }

    protected void parseDur(String dur) {
        if (dur.equals("media")) {
            this.durMedia = true;
            this.simpleDur = Float.NaN;
        } else {
            this.durMedia = false;
            if (dur.length() == 0 || dur.equals("indefinite")) {
                this.simpleDur = Float.POSITIVE_INFINITY;
            } else {
                try {
                    this.simpleDur = this.parseClockValue(dur, false);
                }
                catch (ParseException e) {
                    throw this.createException("attribute.malformed", new Object[]{null, "dur"});
                }
                if (this.simpleDur < 0.0f) {
                    this.simpleDur = Float.POSITIVE_INFINITY;
                }
            }
        }
    }

    protected float parseClockValue(String s, boolean parseOffset) throws ParseException {
        ClockParser p = new ClockParser(parseOffset);
        class Handler
        implements ClockHandler {
            protected float v = 0.0f;

            Handler() {
            }

            public void clockValue(float newClockValue) {
                this.v = newClockValue;
            }
        }
        Handler h = new Handler();
        p.setClockHandler((ClockHandler)h);
        p.parse(s);
        return h.v;
    }

    protected void parseEnd(String end) {
        try {
            this.endTimes = TimingSpecifierListProducer.parseTimingSpecifierList(this, false, end, this.root.useSVG11AccessKeys, this.root.useSVG12AccessKeys);
        }
        catch (ParseException ex) {
            throw this.createException("attribute.malformed", new Object[]{null, "end"});
        }
    }

    protected void parseMin(String min) {
        if (min.equals("media")) {
            this.min = 0.0f;
            this.minMedia = true;
        } else {
            this.minMedia = false;
            if (min.length() == 0) {
                this.min = 0.0f;
            } else {
                try {
                    this.min = this.parseClockValue(min, false);
                }
                catch (ParseException ex) {
                    this.min = 0.0f;
                }
                if (this.min < 0.0f) {
                    this.min = 0.0f;
                }
            }
        }
    }

    protected void parseMax(String max) {
        if (max.equals("media")) {
            this.max = Float.POSITIVE_INFINITY;
            this.maxMedia = true;
        } else {
            this.maxMedia = false;
            if (max.length() == 0 || max.equals("indefinite")) {
                this.max = Float.POSITIVE_INFINITY;
            } else {
                try {
                    this.max = this.parseClockValue(max, false);
                }
                catch (ParseException ex) {
                    this.max = Float.POSITIVE_INFINITY;
                }
                if (this.max < 0.0f) {
                    this.max = 0.0f;
                }
            }
        }
    }

    protected void parseRepeatCount(String repeatCount) {
        if (repeatCount.length() == 0) {
            this.repeatCount = Float.NaN;
        } else if (repeatCount.equals("indefinite")) {
            this.repeatCount = Float.POSITIVE_INFINITY;
        } else {
            try {
                this.repeatCount = Float.parseFloat(repeatCount);
                if (this.repeatCount > 0.0f) {
                    return;
                }
            }
            catch (NumberFormatException ex) {
                throw this.createException("attribute.malformed", new Object[]{null, "repeatCount"});
            }
        }
    }

    protected void parseRepeatDur(String repeatDur) {
        try {
            this.repeatDur = repeatDur.length() == 0 ? Float.NaN : (repeatDur.equals("indefinite") ? Float.POSITIVE_INFINITY : this.parseClockValue(repeatDur, false));
        }
        catch (ParseException ex) {
            throw this.createException("attribute.malformed", new Object[]{null, "repeatDur"});
        }
    }

    protected void parseFill(String fill) {
        if (fill.length() == 0 || fill.equals("remove")) {
            this.fillMode = 0;
        } else if (fill.equals("freeze")) {
            this.fillMode = 1;
        } else {
            throw this.createException("attribute.malformed", new Object[]{null, "fill"});
        }
    }

    protected void parseRestart(String restart) {
        if (restart.length() == 0 || restart.equals("always")) {
            this.restartMode = 0;
        } else if (restart.equals("whenNotActive")) {
            this.restartMode = 1;
        } else if (restart.equals("never")) {
            this.restartMode = 2;
        } else {
            throw this.createException("attribute.malformed", new Object[]{null, "restart"});
        }
    }

    public void initialize() {
        for (TimingSpecifier beginTime : this.beginTimes) {
            beginTime.initialize();
        }
        for (TimingSpecifier endTime : this.endTimes) {
            endTime.initialize();
        }
    }

    public void deinitialize() {
        for (TimingSpecifier beginTime : this.beginTimes) {
            beginTime.deinitialize();
        }
        for (TimingSpecifier endTime : this.endTimes) {
            endTime.deinitialize();
        }
    }

    public void beginElement() {
        this.beginElement(0.0f);
    }

    public void beginElement(float offset) {
        float t = this.root.convertWallclockTime(Calendar.getInstance());
        InstanceTime it = new InstanceTime(null, t + offset, true);
        this.addInstanceTime(it, true);
    }

    public void endElement() {
        this.endElement(0.0f);
    }

    public void endElement(float offset) {
        float t = this.root.convertWallclockTime(Calendar.getInstance());
        InstanceTime it = new InstanceTime(null, t + offset, true);
        this.addInstanceTime(it, false);
    }

    public float getLastSampleTime() {
        return this.lastSampleTime;
    }

    public float getCurrentBeginTime() {
        float begin;
        block3: {
            block2: {
                float f;
                if (this.currentInterval == null) break block2;
                begin = this.currentInterval.getBegin();
                if (!(f < this.lastSampleTime)) break block3;
            }
            return Float.NaN;
        }
        return begin;
    }

    public boolean canBegin() {
        return this.currentInterval == null || this.isActive && this.restartMode != 2;
    }

    public boolean canEnd() {
        return this.isActive;
    }

    public float getHyperlinkBeginTime() {
        if (this.isActive) {
            return this.currentInterval.getBegin();
        }
        if (!this.beginInstanceTimes.isEmpty()) {
            return ((InstanceTime)this.beginInstanceTimes.get(0)).getTime();
        }
        return Float.NaN;
    }

    public TimingSpecifier[] getBeginTimingSpecifiers() {
        return (TimingSpecifier[])this.beginTimes.clone();
    }

    public TimingSpecifier[] getEndTimingSpecifiers() {
        return (TimingSpecifier[])this.endTimes.clone();
    }

    protected void fireTimeEvent(String eventType, float time, int detail) {
        Calendar t = (Calendar)this.root.getDocumentBeginTime().clone();
        t.add(14, (int)Math.round((double)time * 1000.0));
        this.fireTimeEvent(eventType, t, detail);
    }

    void eventOccurred(TimingSpecifier t, Event e) {
        HashSet<TimingSpecifier> ts = (HashSet<TimingSpecifier>)this.handledEvents.get(e);
        if (ts == null) {
            ts = new HashSet<TimingSpecifier>();
            this.handledEvents.put(e, ts);
        }
        ts.add(t);
        this.root.currentIntervalWillUpdate();
    }

    protected abstract void fireTimeEvent(String var1, Calendar var2, int var3);

    protected abstract void toActive(float var1);

    protected abstract void toInactive(boolean var1, boolean var2);

    protected abstract void removeFill();

    protected abstract void sampledAt(float var1, float var2, int var3);

    protected abstract void sampledLastValue(int var1);

    protected abstract TimedElement getTimedElementById(String var1);

    protected abstract EventTarget getEventTargetById(String var1);

    protected abstract EventTarget getRootEventTarget();

    public abstract Element getElement();

    protected abstract EventTarget getAnimationEventTarget();

    public abstract boolean isBefore(TimedElement var1);

    protected abstract boolean isConstantAnimation();

    public AnimationException createException(String code, Object[] params) {
        Element e = this.getElement();
        if (e != null) {
            params[0] = e.getNodeName();
        }
        return new AnimationException(this, code, params);
    }

    public static void setLocale(Locale l) {
        localizableSupport.setLocale(l);
    }

    public static Locale getLocale() {
        return localizableSupport.getLocale();
    }

    public static String formatMessage(String key, Object[] args) throws MissingResourceException {
        return localizableSupport.formatMessage(key, args);
    }

    public static String toString(float time) {
        if (Float.isNaN(time)) {
            return "UNRESOLVED";
        }
        if (time == Float.POSITIVE_INFINITY) {
            return "INDEFINITE";
        }
        return Float.toString(time);
    }
}

