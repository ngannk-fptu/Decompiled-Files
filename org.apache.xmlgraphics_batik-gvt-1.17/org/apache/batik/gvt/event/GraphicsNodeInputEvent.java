/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.gvt.event;

import java.awt.event.InputEvent;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.event.AWTEventDispatcher;
import org.apache.batik.gvt.event.GraphicsNodeEvent;

public abstract class GraphicsNodeInputEvent
extends GraphicsNodeEvent {
    public static final int SHIFT_MASK = 64;
    public static final int CTRL_MASK = 128;
    public static final int META_MASK = 256;
    public static final int ALT_MASK = 512;
    public static final int ALT_GRAPH_MASK = 8192;
    public static final int BUTTON1_MASK = 1024;
    public static final int BUTTON2_MASK = 2048;
    public static final int BUTTON3_MASK = 4096;
    public static final int CAPS_LOCK_MASK = 1;
    public static final int NUM_LOCK_MASK = 2;
    public static final int SCROLL_LOCK_MASK = 4;
    public static final int KANA_LOCK_MASK = 8;
    long when;
    int modifiers;
    int lockState;

    protected GraphicsNodeInputEvent(GraphicsNode source, int id, long when, int modifiers, int lockState) {
        super(source, id);
        this.when = when;
        this.modifiers = modifiers;
        this.lockState = lockState;
    }

    protected GraphicsNodeInputEvent(GraphicsNode source, InputEvent evt, int lockState) {
        super(source, evt.getID());
        this.when = evt.getWhen();
        this.modifiers = evt.getModifiersEx();
        this.lockState = lockState;
    }

    public boolean isShiftDown() {
        return (this.modifiers & 0x40) != 0;
    }

    public boolean isControlDown() {
        return (this.modifiers & 0x80) != 0;
    }

    public boolean isMetaDown() {
        return AWTEventDispatcher.isMetaDown(this.modifiers);
    }

    public boolean isAltDown() {
        return (this.modifiers & 0x200) != 0;
    }

    public boolean isAltGraphDown() {
        return (this.modifiers & 0x2000) != 0;
    }

    public long getWhen() {
        return this.when;
    }

    public int getModifiers() {
        return this.modifiers;
    }

    public int getLockState() {
        return this.lockState;
    }
}

