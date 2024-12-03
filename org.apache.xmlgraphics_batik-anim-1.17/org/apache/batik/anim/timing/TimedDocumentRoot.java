/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.util.DoublyIndexedSet
 */
package org.apache.batik.anim.timing;

import java.util.Calendar;
import java.util.LinkedList;
import org.apache.batik.anim.timing.InstanceTime;
import org.apache.batik.anim.timing.Interval;
import org.apache.batik.anim.timing.TimeContainer;
import org.apache.batik.anim.timing.TimedElement;
import org.apache.batik.anim.timing.TimegraphListener;
import org.apache.batik.anim.timing.TimingSpecifier;
import org.apache.batik.util.DoublyIndexedSet;

public abstract class TimedDocumentRoot
extends TimeContainer {
    protected Calendar documentBeginTime;
    protected boolean useSVG11AccessKeys;
    protected boolean useSVG12AccessKeys;
    protected DoublyIndexedSet propagationFlags = new DoublyIndexedSet();
    protected LinkedList listeners = new LinkedList();
    protected boolean isSampling;
    protected boolean isHyperlinking;

    public TimedDocumentRoot(boolean useSVG11AccessKeys, boolean useSVG12AccessKeys) {
        this.root = this;
        this.useSVG11AccessKeys = useSVG11AccessKeys;
        this.useSVG12AccessKeys = useSVG12AccessKeys;
    }

    @Override
    protected float getImplicitDur() {
        return Float.POSITIVE_INFINITY;
    }

    @Override
    public float getDefaultBegin(TimedElement child) {
        return 0.0f;
    }

    public float getCurrentTime() {
        return this.lastSampleTime;
    }

    public boolean isSampling() {
        return this.isSampling;
    }

    public boolean isHyperlinking() {
        return this.isHyperlinking;
    }

    public float seekTo(float time, boolean hyperlinking) {
        boolean needsUpdates;
        TimedElement[] es;
        this.isSampling = true;
        this.lastSampleTime = time;
        this.isHyperlinking = hyperlinking;
        this.propagationFlags.clear();
        float mint = Float.POSITIVE_INFINITY;
        for (TimedElement e1 : es = this.getChildren()) {
            float t = e1.sampleAt(time, hyperlinking);
            if (!(t < mint)) continue;
            mint = t;
        }
        do {
            needsUpdates = false;
            for (TimedElement e : es) {
                if (!e.shouldUpdateCurrentInterval) continue;
                needsUpdates = true;
                float t = e.sampleAt(time, hyperlinking);
                if (!(t < mint)) continue;
                mint = t;
            }
        } while (needsUpdates);
        this.isSampling = false;
        if (hyperlinking) {
            this.root.currentIntervalWillUpdate();
        }
        return mint;
    }

    public void resetDocument(Calendar documentBeginTime) {
        this.documentBeginTime = documentBeginTime == null ? Calendar.getInstance() : documentBeginTime;
        this.reset(true);
    }

    public Calendar getDocumentBeginTime() {
        return this.documentBeginTime;
    }

    public float convertEpochTime(long t) {
        long begin = this.documentBeginTime.getTime().getTime();
        return (float)(t - begin) / 1000.0f;
    }

    public float convertWallclockTime(Calendar time) {
        long begin = this.documentBeginTime.getTime().getTime();
        long t = time.getTime().getTime();
        return (float)(t - begin) / 1000.0f;
    }

    public void addTimegraphListener(TimegraphListener l) {
        this.listeners.add(l);
    }

    public void removeTimegraphListener(TimegraphListener l) {
        this.listeners.remove(l);
    }

    void fireElementAdded(TimedElement e) {
        for (Object listener : this.listeners) {
            ((TimegraphListener)listener).elementAdded(e);
        }
    }

    void fireElementRemoved(TimedElement e) {
        for (Object listener : this.listeners) {
            ((TimegraphListener)listener).elementRemoved(e);
        }
    }

    boolean shouldPropagate(Interval i, TimingSpecifier ts, boolean isBegin) {
        InstanceTime it;
        InstanceTime instanceTime = it = isBegin ? i.getBeginInstanceTime() : i.getEndInstanceTime();
        if (this.propagationFlags.contains((Object)it, (Object)ts)) {
            return false;
        }
        this.propagationFlags.add((Object)it, (Object)ts);
        return true;
    }

    protected void currentIntervalWillUpdate() {
    }

    protected abstract String getEventNamespaceURI(String var1);

    protected abstract String getEventType(String var1);

    protected abstract String getRepeatEventName();
}

