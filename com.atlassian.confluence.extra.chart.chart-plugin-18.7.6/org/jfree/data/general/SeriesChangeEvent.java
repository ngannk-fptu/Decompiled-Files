/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.general;

import java.io.Serializable;
import java.util.EventObject;

public class SeriesChangeEvent
extends EventObject
implements Serializable {
    private static final long serialVersionUID = 1593866085210089052L;

    public SeriesChangeEvent(Object source) {
        super(source);
    }
}

