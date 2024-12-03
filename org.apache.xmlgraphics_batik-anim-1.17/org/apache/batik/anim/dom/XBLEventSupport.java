/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractNode
 *  org.apache.batik.dom.events.AbstractEvent
 *  org.apache.batik.dom.events.EventListenerList
 *  org.apache.batik.dom.events.EventListenerList$Entry
 *  org.apache.batik.dom.events.EventSupport
 *  org.apache.batik.dom.events.NodeEventTarget
 *  org.apache.batik.dom.xbl.NodeXBL
 *  org.apache.batik.dom.xbl.ShadowTreeEvent
 */
package org.apache.batik.anim.dom;

import java.util.HashMap;
import java.util.HashSet;
import org.apache.batik.anim.dom.XBLOMHandlerGroupElement;
import org.apache.batik.dom.AbstractNode;
import org.apache.batik.dom.events.AbstractEvent;
import org.apache.batik.dom.events.EventListenerList;
import org.apache.batik.dom.events.EventSupport;
import org.apache.batik.dom.events.NodeEventTarget;
import org.apache.batik.dom.xbl.NodeXBL;
import org.apache.batik.dom.xbl.ShadowTreeEvent;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventException;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.MutationEvent;

public class XBLEventSupport
extends EventSupport {
    protected HashMap<String, EventListenerList> capturingImplementationListeners;
    protected HashMap<String, EventListenerList> bubblingImplementationListeners;
    protected static HashMap<String, String> eventTypeAliases = new HashMap();

    public XBLEventSupport(AbstractNode n) {
        super(n);
    }

    public void addEventListenerNS(String namespaceURI, String type, EventListener listener, boolean useCapture, Object group) {
        String alias;
        super.addEventListenerNS(namespaceURI, type, listener, useCapture, group);
        if ((namespaceURI == null || namespaceURI.equals("http://www.w3.org/2001/xml-events")) && (alias = eventTypeAliases.get(type)) != null) {
            super.addEventListenerNS(namespaceURI, alias, listener, useCapture, group);
        }
    }

    public void removeEventListenerNS(String namespaceURI, String type, EventListener listener, boolean useCapture) {
        String alias;
        super.removeEventListenerNS(namespaceURI, type, listener, useCapture);
        if ((namespaceURI == null || namespaceURI.equals("http://www.w3.org/2001/xml-events")) && (alias = eventTypeAliases.get(type)) != null) {
            super.removeEventListenerNS(namespaceURI, alias, listener, useCapture);
        }
    }

    public void addImplementationEventListenerNS(String namespaceURI, String type, EventListener listener, boolean useCapture) {
        HashMap<String, EventListenerList> listeners;
        if (useCapture) {
            if (this.capturingImplementationListeners == null) {
                this.capturingImplementationListeners = new HashMap();
            }
            listeners = this.capturingImplementationListeners;
        } else {
            if (this.bubblingImplementationListeners == null) {
                this.bubblingImplementationListeners = new HashMap();
            }
            listeners = this.bubblingImplementationListeners;
        }
        EventListenerList list = listeners.get(type);
        if (list == null) {
            list = new EventListenerList();
            listeners.put(type, list);
        }
        list.addListener(namespaceURI, null, listener);
    }

    public void removeImplementationEventListenerNS(String namespaceURI, String type, EventListener listener, boolean useCapture) {
        HashMap<String, EventListenerList> listeners;
        HashMap<String, EventListenerList> hashMap = listeners = useCapture ? this.capturingImplementationListeners : this.bubblingImplementationListeners;
        if (listeners == null) {
            return;
        }
        EventListenerList list = listeners.get(type);
        if (list == null) {
            return;
        }
        list.removeListener(namespaceURI, listener);
        if (list.size() == 0) {
            listeners.remove(type);
        }
    }

    public void moveEventListeners(EventSupport other) {
        super.moveEventListeners(other);
        XBLEventSupport es = (XBLEventSupport)other;
        es.capturingImplementationListeners = this.capturingImplementationListeners;
        es.bubblingImplementationListeners = this.bubblingImplementationListeners;
        this.capturingImplementationListeners = null;
        this.bubblingImplementationListeners = null;
    }

    public boolean dispatchEvent(NodeEventTarget target, Event evt) throws EventException {
        NodeEventTarget node;
        int i;
        if (evt == null) {
            return false;
        }
        if (!(evt instanceof AbstractEvent)) {
            throw this.createEventException((short)9, "unsupported.event", new Object[0]);
        }
        AbstractEvent e = (AbstractEvent)evt;
        String type = e.getType();
        if (type == null || type.length() == 0) {
            throw this.createEventException((short)0, "unspecified.event", new Object[0]);
        }
        this.setTarget(e, target);
        this.stopPropagation(e, false);
        this.stopImmediatePropagation(e, false);
        this.preventDefault(e, false);
        NodeEventTarget[] ancestors = this.getAncestors(target);
        int bubbleLimit = e.getBubbleLimit();
        int minAncestor = 0;
        if (this.isSingleScopeEvent((Event)e)) {
            AbstractNode targetNode = (AbstractNode)target;
            Element boundElement = targetNode.getXblBoundElement();
            if (boundElement != null) {
                AbstractNode ancestorNode;
                for (minAncestor = ancestors.length; minAncestor > 0 && (ancestorNode = (AbstractNode)ancestors[minAncestor - 1]).getXblBoundElement() == boundElement; --minAncestor) {
                }
            }
        } else if (bubbleLimit != 0 && (minAncestor = ancestors.length - bubbleLimit + 1) < 0) {
            minAncestor = 0;
        }
        AbstractEvent[] es = this.getRetargettedEvents(target, ancestors, e);
        boolean preventDefault = false;
        HashSet stoppedGroups = new HashSet();
        HashSet toBeStoppedGroups = new HashSet();
        for (i = 0; i < minAncestor; ++i) {
            node = ancestors[i];
            this.setCurrentTarget(es[i], node);
            this.setEventPhase(es[i], (short)1);
            this.fireImplementationEventListeners(node, es[i], true);
        }
        for (i = minAncestor; i < ancestors.length; ++i) {
            node = ancestors[i];
            this.setCurrentTarget(es[i], node);
            this.setEventPhase(es[i], (short)1);
            this.fireImplementationEventListeners(node, es[i], true);
            this.fireEventListeners(node, es[i], true, stoppedGroups, toBeStoppedGroups);
            this.fireHandlerGroupEventListeners(node, es[i], true, stoppedGroups, toBeStoppedGroups);
            preventDefault = preventDefault || es[i].getDefaultPrevented();
            stoppedGroups.addAll(toBeStoppedGroups);
            toBeStoppedGroups.clear();
        }
        this.setEventPhase(e, (short)2);
        this.setCurrentTarget(e, target);
        this.fireImplementationEventListeners(target, e, false);
        this.fireEventListeners(target, e, false, stoppedGroups, toBeStoppedGroups);
        this.fireHandlerGroupEventListeners((NodeEventTarget)this.node, e, false, stoppedGroups, toBeStoppedGroups);
        stoppedGroups.addAll(toBeStoppedGroups);
        toBeStoppedGroups.clear();
        boolean bl = preventDefault = preventDefault || e.getDefaultPrevented();
        if (e.getBubbles()) {
            for (i = ancestors.length - 1; i >= minAncestor; --i) {
                node = ancestors[i];
                this.setCurrentTarget(es[i], node);
                this.setEventPhase(es[i], (short)3);
                this.fireImplementationEventListeners(node, es[i], false);
                this.fireEventListeners(node, es[i], false, stoppedGroups, toBeStoppedGroups);
                this.fireHandlerGroupEventListeners(node, es[i], false, stoppedGroups, toBeStoppedGroups);
                preventDefault = preventDefault || es[i].getDefaultPrevented();
                stoppedGroups.addAll(toBeStoppedGroups);
                toBeStoppedGroups.clear();
            }
            for (i = minAncestor - 1; i >= 0; --i) {
                node = ancestors[i];
                this.setCurrentTarget(es[i], node);
                this.setEventPhase(es[i], (short)3);
                this.fireImplementationEventListeners(node, es[i], false);
                preventDefault = preventDefault || es[i].getDefaultPrevented();
            }
        }
        if (!preventDefault) {
            this.runDefaultActions(e);
        }
        return preventDefault;
    }

    protected void fireHandlerGroupEventListeners(NodeEventTarget node, AbstractEvent e, boolean useCapture, HashSet stoppedGroups, HashSet toBeStoppedGroups) {
        NodeList defs = ((NodeXBL)node).getXblDefinitions();
        for (int j = 0; j < defs.getLength(); ++j) {
            Node n;
            for (n = defs.item(j).getFirstChild(); n != null && !(n instanceof XBLOMHandlerGroupElement); n = n.getNextSibling()) {
            }
            if (n == null) continue;
            node = (NodeEventTarget)n;
            String type = e.getType();
            EventSupport support = node.getEventSupport();
            if (support == null) continue;
            EventListenerList list = support.getEventListeners(type, useCapture);
            if (list == null) {
                return;
            }
            EventListenerList.Entry[] listeners = list.getEventListeners();
            this.fireEventListeners(node, e, listeners, stoppedGroups, toBeStoppedGroups);
        }
    }

    protected boolean isSingleScopeEvent(Event evt) {
        return evt instanceof MutationEvent || evt instanceof ShadowTreeEvent;
    }

    protected AbstractEvent[] getRetargettedEvents(NodeEventTarget target, NodeEventTarget[] ancestors, AbstractEvent e) {
        boolean singleScope = this.isSingleScopeEvent((Event)e);
        AbstractNode targetNode = (AbstractNode)target;
        AbstractEvent[] es = new AbstractEvent[ancestors.length];
        if (ancestors.length > 0) {
            int index = ancestors.length - 1;
            Element boundElement = targetNode.getXblBoundElement();
            AbstractNode ancestorNode = (AbstractNode)ancestors[index];
            es[index] = !singleScope && ancestorNode.getXblBoundElement() != boundElement ? this.retargetEvent(e, ancestors[index]) : e;
            while (--index >= 0) {
                ancestorNode = (AbstractNode)ancestors[index + 1];
                boundElement = ancestorNode.getXblBoundElement();
                AbstractNode nextAncestorNode = (AbstractNode)ancestors[index];
                Element nextBoundElement = nextAncestorNode.getXblBoundElement();
                if (!singleScope && nextBoundElement != boundElement) {
                    es[index] = this.retargetEvent(es[index + 1], ancestors[index]);
                    continue;
                }
                es[index] = es[index + 1];
            }
        }
        return es;
    }

    protected AbstractEvent retargetEvent(AbstractEvent e, NodeEventTarget target) {
        AbstractEvent clonedEvent = e.cloneEvent();
        this.setTarget(clonedEvent, target);
        return clonedEvent;
    }

    public EventListenerList getImplementationEventListeners(String type, boolean useCapture) {
        HashMap<String, EventListenerList> listeners = useCapture ? this.capturingImplementationListeners : this.bubblingImplementationListeners;
        return listeners != null ? listeners.get(type) : null;
    }

    protected void fireImplementationEventListeners(NodeEventTarget node, AbstractEvent e, boolean useCapture) {
        String type = e.getType();
        XBLEventSupport support = (XBLEventSupport)node.getEventSupport();
        if (support == null) {
            return;
        }
        EventListenerList list = support.getImplementationEventListeners(type, useCapture);
        if (list == null) {
            return;
        }
        EventListenerList.Entry[] listeners = list.getEventListeners();
        this.fireEventListeners(node, e, listeners, null, null);
    }

    static {
        eventTypeAliases.put("SVGLoad", "load");
        eventTypeAliases.put("SVGUnoad", "unload");
        eventTypeAliases.put("SVGAbort", "abort");
        eventTypeAliases.put("SVGError", "error");
        eventTypeAliases.put("SVGResize", "resize");
        eventTypeAliases.put("SVGScroll", "scroll");
        eventTypeAliases.put("SVGZoom", "zoom");
    }
}

