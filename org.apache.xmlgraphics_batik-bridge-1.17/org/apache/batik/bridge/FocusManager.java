/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.events.DOMUIEvent
 *  org.apache.batik.dom.events.NodeEventTarget
 */
package org.apache.batik.bridge;

import org.apache.batik.dom.events.DOMUIEvent;
import org.apache.batik.dom.events.NodeEventTarget;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.events.DocumentEvent;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MouseEvent;

public class FocusManager {
    protected EventTarget lastFocusEventTarget;
    protected Document document;
    protected EventListener mouseclickListener;
    protected EventListener domFocusInListener;
    protected EventListener domFocusOutListener;
    protected EventListener mouseoverListener;
    protected EventListener mouseoutListener;

    public FocusManager(Document doc) {
        this.document = doc;
        this.addEventListeners(doc);
    }

    protected void addEventListeners(Document doc) {
        NodeEventTarget target = (NodeEventTarget)doc;
        this.mouseclickListener = new MouseClickTracker();
        target.addEventListenerNS("http://www.w3.org/2001/xml-events", "click", this.mouseclickListener, true, null);
        this.mouseoverListener = new MouseOverTracker();
        target.addEventListenerNS("http://www.w3.org/2001/xml-events", "mouseover", this.mouseoverListener, true, null);
        this.mouseoutListener = new MouseOutTracker();
        target.addEventListenerNS("http://www.w3.org/2001/xml-events", "mouseout", this.mouseoutListener, true, null);
        this.domFocusInListener = new DOMFocusInTracker();
        target.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMFocusIn", this.domFocusInListener, true, null);
        this.domFocusOutListener = new DOMFocusOutTracker();
        target.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMFocusOut", this.domFocusOutListener, true, null);
    }

    protected void removeEventListeners(Document doc) {
        NodeEventTarget target = (NodeEventTarget)doc;
        target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "click", this.mouseclickListener, true);
        target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "mouseover", this.mouseoverListener, true);
        target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "mouseout", this.mouseoutListener, true);
        target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMFocusIn", this.domFocusInListener, true);
        target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMFocusOut", this.domFocusOutListener, true);
    }

    public EventTarget getCurrentEventTarget() {
        return this.lastFocusEventTarget;
    }

    public void dispose() {
        if (this.document == null) {
            return;
        }
        this.removeEventListeners(this.document);
        this.lastFocusEventTarget = null;
        this.document = null;
    }

    protected void fireDOMFocusInEvent(EventTarget target, EventTarget relatedTarget) {
        DocumentEvent docEvt = (DocumentEvent)((Object)((Element)((Object)target)).getOwnerDocument());
        DOMUIEvent uiEvt = (DOMUIEvent)docEvt.createEvent("UIEvents");
        uiEvt.initUIEventNS("http://www.w3.org/2001/xml-events", "DOMFocusIn", true, false, null, 0);
        target.dispatchEvent((Event)uiEvt);
    }

    protected void fireDOMFocusOutEvent(EventTarget target, EventTarget relatedTarget) {
        DocumentEvent docEvt = (DocumentEvent)((Object)((Element)((Object)target)).getOwnerDocument());
        DOMUIEvent uiEvt = (DOMUIEvent)docEvt.createEvent("UIEvents");
        uiEvt.initUIEventNS("http://www.w3.org/2001/xml-events", "DOMFocusOut", true, false, null, 0);
        target.dispatchEvent((Event)uiEvt);
    }

    protected void fireDOMActivateEvent(EventTarget target, int detailArg) {
        DocumentEvent docEvt = (DocumentEvent)((Object)((Element)((Object)target)).getOwnerDocument());
        DOMUIEvent uiEvt = (DOMUIEvent)docEvt.createEvent("UIEvents");
        uiEvt.initUIEventNS("http://www.w3.org/2001/xml-events", "DOMActivate", true, true, null, 0);
        target.dispatchEvent((Event)uiEvt);
    }

    protected class MouseOutTracker
    implements EventListener {
        protected MouseOutTracker() {
        }

        @Override
        public void handleEvent(Event evt) {
            MouseEvent me = (MouseEvent)evt;
            EventTarget target = evt.getTarget();
            EventTarget relatedTarget = me.getRelatedTarget();
            FocusManager.this.fireDOMFocusOutEvent(target, relatedTarget);
        }
    }

    protected class MouseOverTracker
    implements EventListener {
        protected MouseOverTracker() {
        }

        @Override
        public void handleEvent(Event evt) {
            MouseEvent me = (MouseEvent)evt;
            EventTarget target = evt.getTarget();
            EventTarget relatedTarget = me.getRelatedTarget();
            FocusManager.this.fireDOMFocusInEvent(target, relatedTarget);
        }
    }

    protected class DOMFocusOutTracker
    implements EventListener {
        @Override
        public void handleEvent(Event evt) {
            FocusManager.this.lastFocusEventTarget = null;
        }
    }

    protected class DOMFocusInTracker
    implements EventListener {
        protected DOMFocusInTracker() {
        }

        @Override
        public void handleEvent(Event evt) {
            EventTarget newTarget = evt.getTarget();
            if (FocusManager.this.lastFocusEventTarget != null && FocusManager.this.lastFocusEventTarget != newTarget) {
                FocusManager.this.fireDOMFocusOutEvent(FocusManager.this.lastFocusEventTarget, newTarget);
            }
            FocusManager.this.lastFocusEventTarget = evt.getTarget();
        }
    }

    protected class MouseClickTracker
    implements EventListener {
        protected MouseClickTracker() {
        }

        @Override
        public void handleEvent(Event evt) {
            MouseEvent mevt = (MouseEvent)evt;
            FocusManager.this.fireDOMActivateEvent(evt.getTarget(), mevt.getDetail());
        }
    }
}

