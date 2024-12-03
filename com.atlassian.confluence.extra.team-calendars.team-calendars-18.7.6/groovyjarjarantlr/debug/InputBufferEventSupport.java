/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr.debug;

import groovyjarjarantlr.debug.InputBufferEvent;
import groovyjarjarantlr.debug.InputBufferListener;
import groovyjarjarantlr.debug.ListenerBase;
import java.util.Vector;

public class InputBufferEventSupport {
    private Object source;
    private Vector inputBufferListeners;
    private InputBufferEvent inputBufferEvent;
    protected static final int CONSUME = 0;
    protected static final int LA = 1;
    protected static final int MARK = 2;
    protected static final int REWIND = 3;

    public InputBufferEventSupport(Object object) {
        this.inputBufferEvent = new InputBufferEvent(object);
        this.source = object;
    }

    public void addInputBufferListener(InputBufferListener inputBufferListener) {
        if (this.inputBufferListeners == null) {
            this.inputBufferListeners = new Vector();
        }
        this.inputBufferListeners.addElement(inputBufferListener);
    }

    public void fireConsume(char c) {
        this.inputBufferEvent.setValues(0, c, 0);
        this.fireEvents(0, this.inputBufferListeners);
    }

    public void fireEvent(int n, ListenerBase listenerBase) {
        switch (n) {
            case 0: {
                ((InputBufferListener)listenerBase).inputBufferConsume(this.inputBufferEvent);
                break;
            }
            case 1: {
                ((InputBufferListener)listenerBase).inputBufferLA(this.inputBufferEvent);
                break;
            }
            case 2: {
                ((InputBufferListener)listenerBase).inputBufferMark(this.inputBufferEvent);
                break;
            }
            case 3: {
                ((InputBufferListener)listenerBase).inputBufferRewind(this.inputBufferEvent);
                break;
            }
            default: {
                throw new IllegalArgumentException("bad type " + n + " for fireEvent()");
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void fireEvents(int n, Vector vector) {
        Vector vector2 = null;
        ListenerBase listenerBase = null;
        InputBufferEventSupport inputBufferEventSupport = this;
        synchronized (inputBufferEventSupport) {
            if (vector == null) {
                return;
            }
            vector2 = (Vector)vector.clone();
        }
        if (vector2 != null) {
            for (int i = 0; i < vector2.size(); ++i) {
                listenerBase = (ListenerBase)vector2.elementAt(i);
                this.fireEvent(n, listenerBase);
            }
        }
    }

    public void fireLA(char c, int n) {
        this.inputBufferEvent.setValues(1, c, n);
        this.fireEvents(1, this.inputBufferListeners);
    }

    public void fireMark(int n) {
        this.inputBufferEvent.setValues(2, ' ', n);
        this.fireEvents(2, this.inputBufferListeners);
    }

    public void fireRewind(int n) {
        this.inputBufferEvent.setValues(3, ' ', n);
        this.fireEvents(3, this.inputBufferListeners);
    }

    public Vector getInputBufferListeners() {
        return this.inputBufferListeners;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void refresh(Vector vector) {
        Vector vector2;
        Vector vector3 = vector;
        synchronized (vector3) {
            vector2 = (Vector)vector.clone();
        }
        if (vector2 != null) {
            for (int i = 0; i < vector2.size(); ++i) {
                ((ListenerBase)vector2.elementAt(i)).refresh();
            }
        }
    }

    public void refreshListeners() {
        this.refresh(this.inputBufferListeners);
    }

    public void removeInputBufferListener(InputBufferListener inputBufferListener) {
        if (this.inputBufferListeners != null) {
            this.inputBufferListeners.removeElement(inputBufferListener);
        }
    }
}

