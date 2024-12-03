/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom.events;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.AbstractNode;
import org.apache.batik.dom.events.AbstractEvent;
import org.apache.batik.dom.events.EventListenerList;
import org.apache.batik.dom.events.NodeEventTarget;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventException;
import org.w3c.dom.events.EventListener;

public class EventSupport {
    protected HashMap<String, EventListenerList> capturingListeners;
    protected HashMap<String, EventListenerList> bubblingListeners;
    protected AbstractNode node;

    public EventSupport(AbstractNode n) {
        this.node = n;
    }

    public void addEventListener(String type, EventListener listener, boolean useCapture) {
        this.addEventListenerNS(null, type, listener, useCapture, null);
    }

    public void addEventListenerNS(String namespaceURI, String type, EventListener listener, boolean useCapture, Object group) {
        HashMap<String, EventListenerList> listeners;
        if (useCapture) {
            if (this.capturingListeners == null) {
                this.capturingListeners = new HashMap();
            }
            listeners = this.capturingListeners;
        } else {
            if (this.bubblingListeners == null) {
                this.bubblingListeners = new HashMap();
            }
            listeners = this.bubblingListeners;
        }
        EventListenerList list = listeners.get(type);
        if (list == null) {
            list = new EventListenerList();
            listeners.put(type, list);
        }
        list.addListener(namespaceURI, group, listener);
    }

    public void removeEventListener(String type, EventListener listener, boolean useCapture) {
        this.removeEventListenerNS(null, type, listener, useCapture);
    }

    public void removeEventListenerNS(String namespaceURI, String type, EventListener listener, boolean useCapture) {
        HashMap<String, EventListenerList> listeners = useCapture ? this.capturingListeners : this.bubblingListeners;
        if (listeners == null) {
            return;
        }
        EventListenerList list = listeners.get(type);
        if (list != null) {
            list.removeListener(namespaceURI, listener);
            if (list.size() == 0) {
                listeners.remove(type);
            }
        }
    }

    public void moveEventListeners(EventSupport other) {
        other.capturingListeners = this.capturingListeners;
        other.bubblingListeners = this.bubblingListeners;
        this.capturingListeners = null;
        this.bubblingListeners = null;
    }

    public boolean dispatchEvent(NodeEventTarget target, Event evt) throws EventException {
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
        e.setTarget(target);
        e.stopPropagation(false);
        e.stopImmediatePropagation(false);
        e.preventDefault(false);
        NodeEventTarget[] ancestors = this.getAncestors(target);
        e.setEventPhase((short)1);
        HashSet stoppedGroups = new HashSet();
        HashSet toBeStoppedGroups = new HashSet();
        for (NodeEventTarget node : ancestors) {
            e.setCurrentTarget(node);
            this.fireEventListeners(node, e, true, stoppedGroups, toBeStoppedGroups);
            stoppedGroups.addAll(toBeStoppedGroups);
            toBeStoppedGroups.clear();
        }
        e.setEventPhase((short)2);
        e.setCurrentTarget(target);
        this.fireEventListeners(target, e, false, stoppedGroups, toBeStoppedGroups);
        stoppedGroups.addAll(toBeStoppedGroups);
        toBeStoppedGroups.clear();
        if (e.getBubbles()) {
            e.setEventPhase((short)3);
            for (int i = ancestors.length - 1; i >= 0; --i) {
                NodeEventTarget node = ancestors[i];
                e.setCurrentTarget(node);
                this.fireEventListeners(node, e, false, stoppedGroups, toBeStoppedGroups);
                stoppedGroups.addAll(toBeStoppedGroups);
                toBeStoppedGroups.clear();
            }
        }
        if (!e.getDefaultPrevented()) {
            this.runDefaultActions(e);
        }
        return e.getDefaultPrevented();
    }

    protected void runDefaultActions(AbstractEvent e) {
        List runables = e.getDefaultActions();
        if (runables != null) {
            for (Object runable : runables) {
                Runnable r = (Runnable)runable;
                r.run();
            }
        }
    }

    protected void fireEventListeners(NodeEventTarget node, AbstractEvent e, EventListenerList.Entry[] listeners, HashSet stoppedGroups, HashSet toBeStoppedGroups) {
        if (listeners == null) {
            return;
        }
        String eventNS = e.getNamespaceURI();
        for (EventListenerList.Entry listener : listeners) {
            try {
                String listenerNS = listener.getNamespaceURI();
                if (listenerNS != null && eventNS != null && !listenerNS.equals(eventNS)) continue;
                Object group = listener.getGroup();
                if (stoppedGroups != null && stoppedGroups.contains(group)) continue;
                listener.getListener().handleEvent(e);
                if (e.getStopImmediatePropagation()) {
                    if (stoppedGroups != null) {
                        stoppedGroups.add(group);
                    }
                    e.stopImmediatePropagation(false);
                    continue;
                }
                if (!e.getStopPropagation()) continue;
                if (toBeStoppedGroups != null) {
                    toBeStoppedGroups.add(group);
                }
                e.stopPropagation(false);
            }
            catch (ThreadDeath td) {
                throw td;
            }
            catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    protected void fireEventListeners(NodeEventTarget node, AbstractEvent e, boolean useCapture, HashSet stoppedGroups, HashSet toBeStoppedGroups) {
        String type = e.getType();
        EventSupport support = node.getEventSupport();
        if (support == null) {
            return;
        }
        EventListenerList list = support.getEventListeners(type, useCapture);
        if (list == null) {
            return;
        }
        EventListenerList.Entry[] listeners = list.getEventListeners();
        this.fireEventListeners(node, e, listeners, stoppedGroups, toBeStoppedGroups);
    }

    protected NodeEventTarget[] getAncestors(NodeEventTarget node) {
        node = node.getParentNodeEventTarget();
        int nancestors = 0;
        NodeEventTarget n = node;
        while (n != null) {
            n = n.getParentNodeEventTarget();
            ++nancestors;
        }
        NodeEventTarget[] ancestors = new NodeEventTarget[nancestors];
        int i = nancestors - 1;
        while (i >= 0) {
            ancestors[i] = node;
            --i;
            node = node.getParentNodeEventTarget();
        }
        return ancestors;
    }

    public boolean hasEventListenerNS(String namespaceURI, String type) {
        EventListenerList ell;
        if (this.capturingListeners != null && (ell = this.capturingListeners.get(type)) != null && ell.hasEventListener(namespaceURI)) {
            return true;
        }
        if (this.bubblingListeners != null && (ell = this.capturingListeners.get(type)) != null) {
            return ell.hasEventListener(namespaceURI);
        }
        return false;
    }

    public EventListenerList getEventListeners(String type, boolean useCapture) {
        HashMap<String, EventListenerList> listeners;
        HashMap<String, EventListenerList> hashMap = listeners = useCapture ? this.capturingListeners : this.bubblingListeners;
        if (listeners == null) {
            return null;
        }
        return listeners.get(type);
    }

    protected EventException createEventException(short code, String key, Object[] args) {
        try {
            AbstractDocument doc = (AbstractDocument)this.node.getOwnerDocument();
            return new EventException(code, doc.formatMessage(key, args));
        }
        catch (Exception e) {
            return new EventException(code, key);
        }
    }

    protected void setTarget(AbstractEvent e, NodeEventTarget target) {
        e.setTarget(target);
    }

    protected void stopPropagation(AbstractEvent e, boolean b) {
        e.stopPropagation(b);
    }

    protected void stopImmediatePropagation(AbstractEvent e, boolean b) {
        e.stopImmediatePropagation(b);
    }

    protected void preventDefault(AbstractEvent e, boolean b) {
        e.preventDefault(b);
    }

    protected void setCurrentTarget(AbstractEvent e, NodeEventTarget target) {
        e.setCurrentTarget(target);
    }

    protected void setEventPhase(AbstractEvent e, short phase) {
        e.setEventPhase(phase);
    }

    public static Event getUltimateOriginalEvent(Event evt) {
        AbstractEvent origEvt;
        AbstractEvent e = (AbstractEvent)evt;
        while ((origEvt = (AbstractEvent)e.getOriginalEvent()) != null) {
            e = origEvt;
        }
        return e;
    }
}

