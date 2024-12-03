/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.util.DoublyIndexedTable
 *  org.apache.batik.util.DoublyIndexedTable$Entry
 */
package org.apache.batik.anim;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.batik.anim.AbstractAnimation;
import org.apache.batik.anim.dom.AnimationTarget;
import org.apache.batik.anim.dom.AnimationTargetListener;
import org.apache.batik.anim.timing.TimedDocumentRoot;
import org.apache.batik.anim.timing.TimedElement;
import org.apache.batik.anim.timing.TimegraphListener;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.Document;

public abstract class AnimationEngine {
    public static final short ANIM_TYPE_XML = 0;
    public static final short ANIM_TYPE_CSS = 1;
    public static final short ANIM_TYPE_OTHER = 2;
    protected Document document;
    protected TimedDocumentRoot timedDocumentRoot;
    protected long pauseTime;
    protected HashMap targets = new HashMap();
    protected HashMap animations = new HashMap();
    protected Listener targetListener = new Listener();
    protected static final Map.Entry[] MAP_ENTRY_ARRAY = new Map.Entry[0];

    public AnimationEngine(Document doc) {
        this.document = doc;
        this.timedDocumentRoot = this.createDocumentRoot();
    }

    public void dispose() {
        Iterator iterator = this.targets.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry o;
            Map.Entry e = o = iterator.next();
            AnimationTarget target = (AnimationTarget)e.getKey();
            TargetInfo info = (TargetInfo)e.getValue();
            for (DoublyIndexedTable.Entry entry : info.xmlAnimations) {
                String namespaceURI = (String)entry.getKey1();
                String localName = (String)entry.getKey2();
                Sandwich sandwich = (Sandwich)entry.getValue();
                if (!sandwich.listenerRegistered) continue;
                target.removeTargetListener(namespaceURI, localName, false, this.targetListener);
            }
            for (Map.Entry entry : info.cssAnimations.entrySet()) {
                String propertyName = (String)entry.getKey();
                Sandwich sandwich = (Sandwich)entry.getValue();
                if (!sandwich.listenerRegistered) continue;
                target.removeTargetListener(null, propertyName, true, this.targetListener);
            }
        }
    }

    public void pause() {
        if (this.pauseTime == 0L) {
            this.pauseTime = System.currentTimeMillis();
        }
    }

    public void unpause() {
        if (this.pauseTime != 0L) {
            Calendar begin = this.timedDocumentRoot.getDocumentBeginTime();
            int dt = (int)(System.currentTimeMillis() - this.pauseTime);
            begin.add(14, dt);
            this.pauseTime = 0L;
        }
    }

    public boolean isPaused() {
        return this.pauseTime != 0L;
    }

    public float getCurrentTime() {
        return this.timedDocumentRoot.getCurrentTime();
    }

    public float setCurrentTime(float t) {
        boolean p = this.pauseTime != 0L;
        this.unpause();
        Calendar begin = this.timedDocumentRoot.getDocumentBeginTime();
        float now = this.timedDocumentRoot.convertEpochTime(System.currentTimeMillis());
        begin.add(14, (int)((now - t) * 1000.0f));
        if (p) {
            this.pause();
        }
        return this.tick(t, true);
    }

    public void addAnimation(AnimationTarget target, short type, String ns, String an, AbstractAnimation anim) {
        this.timedDocumentRoot.addChild(anim.getTimedElement());
        AnimationInfo animInfo = this.getAnimationInfo(anim);
        animInfo.type = type;
        animInfo.attributeNamespaceURI = ns;
        animInfo.attributeLocalName = an;
        animInfo.target = target;
        this.animations.put(anim, animInfo);
        Sandwich sandwich = this.getSandwich(target, type, ns, an);
        if (sandwich.animation == null) {
            anim.lowerAnimation = null;
            anim.higherAnimation = null;
        } else {
            sandwich.animation.higherAnimation = anim;
            anim.lowerAnimation = sandwich.animation;
            anim.higherAnimation = null;
        }
        sandwich.animation = anim;
        if (anim.lowerAnimation == null) {
            sandwich.lowestAnimation = anim;
        }
    }

    public void removeAnimation(AbstractAnimation anim) {
        this.timedDocumentRoot.removeChild(anim.getTimedElement());
        AbstractAnimation nextHigher = anim.higherAnimation;
        if (nextHigher != null) {
            nextHigher.markDirty();
        }
        this.moveToBottom(anim);
        if (anim.higherAnimation != null) {
            anim.higherAnimation.lowerAnimation = null;
        }
        AnimationInfo animInfo = this.getAnimationInfo(anim);
        Sandwich sandwich = this.getSandwich(animInfo.target, animInfo.type, animInfo.attributeNamespaceURI, animInfo.attributeLocalName);
        if (sandwich.animation == anim) {
            sandwich.animation = null;
            sandwich.lowestAnimation = null;
            sandwich.shouldUpdate = true;
        }
    }

    protected Sandwich getSandwich(AnimationTarget target, short type, String ns, String an) {
        Sandwich sandwich;
        TargetInfo info = this.getTargetInfo(target);
        if (type == 0) {
            sandwich = (Sandwich)info.xmlAnimations.get((Object)ns, (Object)an);
            if (sandwich == null) {
                sandwich = new Sandwich();
                info.xmlAnimations.put((Object)ns, (Object)an, (Object)sandwich);
            }
        } else if (type == 1) {
            sandwich = (Sandwich)info.cssAnimations.get(an);
            if (sandwich == null) {
                sandwich = new Sandwich();
                info.cssAnimations.put(an, sandwich);
            }
        } else {
            sandwich = (Sandwich)info.otherAnimations.get(an);
            if (sandwich == null) {
                sandwich = new Sandwich();
                info.otherAnimations.put(an, sandwich);
            }
        }
        return sandwich;
    }

    protected TargetInfo getTargetInfo(AnimationTarget target) {
        TargetInfo info = (TargetInfo)this.targets.get(target);
        if (info == null) {
            info = new TargetInfo();
            this.targets.put(target, info);
        }
        return info;
    }

    protected AnimationInfo getAnimationInfo(AbstractAnimation anim) {
        AnimationInfo info = (AnimationInfo)this.animations.get(anim);
        if (info == null) {
            info = new AnimationInfo();
            this.animations.put(anim, info);
        }
        return info;
    }

    protected float tick(float time, boolean hyperlinking) {
        Map.Entry[] targetEntries;
        float waitTime = this.timedDocumentRoot.seekTo(time, hyperlinking);
        for (Map.Entry e : targetEntries = this.targets.entrySet().toArray(MAP_ENTRY_ARRAY)) {
            AnimatableValue av;
            Sandwich sandwich;
            AnimationTarget target = (AnimationTarget)e.getKey();
            TargetInfo info = (TargetInfo)e.getValue();
            for (DoublyIndexedTable.Entry entry : info.xmlAnimations) {
                String namespaceURI = (String)entry.getKey1();
                String localName = (String)entry.getKey2();
                Sandwich sandwich2 = (Sandwich)entry.getValue();
                if (!sandwich2.shouldUpdate && (sandwich2.animation == null || !sandwich2.animation.isDirty)) continue;
                AnimatableValue av2 = null;
                boolean usesUnderlying = false;
                AbstractAnimation anim = sandwich2.animation;
                if (anim != null) {
                    av2 = anim.getComposedValue();
                    usesUnderlying = sandwich2.lowestAnimation.usesUnderlyingValue();
                    anim.isDirty = false;
                }
                if (usesUnderlying && !sandwich2.listenerRegistered) {
                    target.addTargetListener(namespaceURI, localName, false, this.targetListener);
                    sandwich2.listenerRegistered = true;
                } else if (!usesUnderlying && sandwich2.listenerRegistered) {
                    target.removeTargetListener(namespaceURI, localName, false, this.targetListener);
                    sandwich2.listenerRegistered = false;
                }
                target.updateAttributeValue(namespaceURI, localName, av2);
                sandwich2.shouldUpdate = false;
            }
            for (Map.Entry entry : info.cssAnimations.entrySet()) {
                String propertyName = (String)entry.getKey();
                sandwich = (Sandwich)entry.getValue();
                if (!sandwich.shouldUpdate && (sandwich.animation == null || !sandwich.animation.isDirty)) continue;
                av = null;
                boolean usesUnderlying = false;
                AbstractAnimation anim = sandwich.animation;
                if (anim != null) {
                    av = anim.getComposedValue();
                    usesUnderlying = sandwich.lowestAnimation.usesUnderlyingValue();
                    anim.isDirty = false;
                }
                if (usesUnderlying && !sandwich.listenerRegistered) {
                    target.addTargetListener(null, propertyName, true, this.targetListener);
                    sandwich.listenerRegistered = true;
                } else if (!usesUnderlying && sandwich.listenerRegistered) {
                    target.removeTargetListener(null, propertyName, true, this.targetListener);
                    sandwich.listenerRegistered = false;
                }
                if (usesUnderlying) {
                    target.updatePropertyValue(propertyName, null);
                }
                if (!usesUnderlying || av != null) {
                    target.updatePropertyValue(propertyName, av);
                }
                sandwich.shouldUpdate = false;
            }
            for (Map.Entry entry : info.otherAnimations.entrySet()) {
                String type = (String)entry.getKey();
                sandwich = (Sandwich)entry.getValue();
                if (!sandwich.shouldUpdate && (sandwich.animation == null || !sandwich.animation.isDirty)) continue;
                av = null;
                AbstractAnimation anim = sandwich.animation;
                if (anim != null) {
                    av = sandwich.animation.getComposedValue();
                    anim.isDirty = false;
                }
                target.updateOtherValue(type, av);
                sandwich.shouldUpdate = false;
            }
        }
        return waitTime;
    }

    public void toActive(AbstractAnimation anim, float begin) {
        this.moveToTop(anim);
        anim.isActive = true;
        anim.beginTime = begin;
        anim.isFrozen = false;
        this.pushDown(anim);
        anim.markDirty();
    }

    protected void pushDown(AbstractAnimation anim) {
        TimedElement e = anim.getTimedElement();
        AbstractAnimation top = null;
        boolean moved = false;
        while (anim.lowerAnimation != null && (anim.lowerAnimation.isActive || anim.lowerAnimation.isFrozen) && (anim.lowerAnimation.beginTime > anim.beginTime || anim.lowerAnimation.beginTime == anim.beginTime && e.isBefore(anim.lowerAnimation.getTimedElement()))) {
            AbstractAnimation higher = anim.higherAnimation;
            AbstractAnimation lower = anim.lowerAnimation;
            AbstractAnimation lowerLower = lower.lowerAnimation;
            if (higher != null) {
                higher.lowerAnimation = lower;
            }
            if (lowerLower != null) {
                lowerLower.higherAnimation = anim;
            }
            lower.lowerAnimation = anim;
            lower.higherAnimation = higher;
            anim.lowerAnimation = lowerLower;
            anim.higherAnimation = lower;
            if (moved) continue;
            top = lower;
            moved = true;
        }
        if (moved) {
            AnimationInfo animInfo = this.getAnimationInfo(anim);
            Sandwich sandwich = this.getSandwich(animInfo.target, animInfo.type, animInfo.attributeNamespaceURI, animInfo.attributeLocalName);
            if (sandwich.animation == anim) {
                sandwich.animation = top;
            }
            if (anim.lowerAnimation == null) {
                sandwich.lowestAnimation = anim;
            }
        }
    }

    public void toInactive(AbstractAnimation anim, boolean isFrozen) {
        anim.isActive = false;
        anim.isFrozen = isFrozen;
        anim.markDirty();
        if (!isFrozen) {
            anim.value = null;
            anim.beginTime = Float.NEGATIVE_INFINITY;
            this.moveToBottom(anim);
        }
    }

    public void removeFill(AbstractAnimation anim) {
        anim.isActive = false;
        anim.isFrozen = false;
        anim.value = null;
        anim.markDirty();
        this.moveToBottom(anim);
    }

    protected void moveToTop(AbstractAnimation anim) {
        AnimationInfo animInfo = this.getAnimationInfo(anim);
        Sandwich sandwich = this.getSandwich(animInfo.target, animInfo.type, animInfo.attributeNamespaceURI, animInfo.attributeLocalName);
        sandwich.shouldUpdate = true;
        if (anim.higherAnimation == null) {
            return;
        }
        if (anim.lowerAnimation == null) {
            sandwich.lowestAnimation = anim.higherAnimation;
        } else {
            anim.lowerAnimation.higherAnimation = anim.higherAnimation;
        }
        anim.higherAnimation.lowerAnimation = anim.lowerAnimation;
        if (sandwich.animation != null) {
            sandwich.animation.higherAnimation = anim;
        }
        anim.lowerAnimation = sandwich.animation;
        anim.higherAnimation = null;
        sandwich.animation = anim;
    }

    protected void moveToBottom(AbstractAnimation anim) {
        if (anim.lowerAnimation == null) {
            return;
        }
        AnimationInfo animInfo = this.getAnimationInfo(anim);
        Sandwich sandwich = this.getSandwich(animInfo.target, animInfo.type, animInfo.attributeNamespaceURI, animInfo.attributeLocalName);
        AbstractAnimation nextLower = anim.lowerAnimation;
        nextLower.markDirty();
        anim.lowerAnimation.higherAnimation = anim.higherAnimation;
        if (anim.higherAnimation != null) {
            anim.higherAnimation.lowerAnimation = anim.lowerAnimation;
        } else {
            sandwich.animation = nextLower;
            sandwich.shouldUpdate = true;
        }
        sandwich.lowestAnimation.lowerAnimation = anim;
        anim.higherAnimation = sandwich.lowestAnimation;
        anim.lowerAnimation = null;
        sandwich.lowestAnimation = anim;
        if (sandwich.animation.isDirty) {
            sandwich.shouldUpdate = true;
        }
    }

    public void addTimegraphListener(TimegraphListener l) {
        this.timedDocumentRoot.addTimegraphListener(l);
    }

    public void removeTimegraphListener(TimegraphListener l) {
        this.timedDocumentRoot.removeTimegraphListener(l);
    }

    public void sampledAt(AbstractAnimation anim, float simpleTime, float simpleDur, int repeatIteration) {
        anim.sampledAt(simpleTime, simpleDur, repeatIteration);
    }

    public void sampledLastValue(AbstractAnimation anim, int repeatIteration) {
        anim.sampledLastValue(repeatIteration);
    }

    protected abstract TimedDocumentRoot createDocumentRoot();

    protected static class AnimationInfo {
        public AnimationTarget target;
        public short type;
        public String attributeNamespaceURI;
        public String attributeLocalName;

        protected AnimationInfo() {
        }
    }

    protected static class Sandwich {
        public AbstractAnimation animation;
        public AbstractAnimation lowestAnimation;
        public boolean shouldUpdate;
        public boolean listenerRegistered;

        protected Sandwich() {
        }
    }

    protected static class TargetInfo {
        public DoublyIndexedTable xmlAnimations = new DoublyIndexedTable();
        public HashMap cssAnimations = new HashMap();
        public HashMap otherAnimations = new HashMap();

        protected TargetInfo() {
        }
    }

    protected class Listener
    implements AnimationTargetListener {
        protected Listener() {
        }

        @Override
        public void baseValueChanged(AnimationTarget t, String ns, String ln, boolean isCSS) {
            short type = isCSS ? (short)1 : 0;
            Sandwich sandwich = AnimationEngine.this.getSandwich(t, type, ns, ln);
            sandwich.shouldUpdate = true;
            AbstractAnimation anim = sandwich.animation;
            while (anim.lowerAnimation != null) {
                anim = anim.lowerAnimation;
            }
            anim.markDirty();
        }
    }
}

