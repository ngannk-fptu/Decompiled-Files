/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.dom.SVGOMAnimationElement
 *  org.apache.batik.anim.timing.TimedElement
 *  org.apache.batik.dom.events.DOMTimeEvent
 *  org.apache.batik.dom.svg.IdContainer
 *  org.apache.batik.dom.svg.SVGOMUseShadowRoot
 */
package org.apache.batik.bridge;

import java.util.Calendar;
import org.apache.batik.anim.dom.SVGOMAnimationElement;
import org.apache.batik.anim.timing.TimedElement;
import org.apache.batik.bridge.SVGAnimationElementBridge;
import org.apache.batik.dom.events.DOMTimeEvent;
import org.apache.batik.dom.svg.IdContainer;
import org.apache.batik.dom.svg.SVGOMUseShadowRoot;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.DocumentEvent;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventTarget;

public abstract class AnimationSupport {
    public static void fireTimeEvent(EventTarget target, String eventType, Calendar time, int detail) {
        DocumentEvent de = (DocumentEvent)((Object)((Node)((Object)target)).getOwnerDocument());
        DOMTimeEvent evt = (DOMTimeEvent)de.createEvent("TimeEvent");
        evt.initTimeEventNS("http://www.w3.org/2001/xml-events", eventType, null, detail);
        evt.setTimestamp(time.getTime().getTime());
        target.dispatchEvent((Event)evt);
    }

    public static TimedElement getTimedElementById(String id, Node n) {
        Element e = AnimationSupport.getElementById(id, n);
        if (e instanceof SVGOMAnimationElement) {
            SVGAnimationElementBridge b = (SVGAnimationElementBridge)((SVGOMAnimationElement)e).getSVGContext();
            return b.getTimedElement();
        }
        return null;
    }

    public static EventTarget getEventTargetById(String id, Node n) {
        return (EventTarget)((Object)AnimationSupport.getElementById(id, n));
    }

    protected static Element getElementById(String id, Node n) {
        Node p = n.getParentNode();
        while (p != null) {
            n = p;
            if (n instanceof SVGOMUseShadowRoot) {
                p = ((SVGOMUseShadowRoot)n).getCSSParentNode();
                continue;
            }
            p = n.getParentNode();
        }
        if (n instanceof IdContainer) {
            return ((IdContainer)n).getElementById(id);
        }
        return null;
    }
}

