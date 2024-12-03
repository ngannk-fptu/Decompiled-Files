/*
 * Decompiled with CFR 0.152.
 */
package javax.el;

import java.util.EventObject;
import javax.el.ELContext;

public class ELContextEvent
extends EventObject {
    private static final long serialVersionUID = 1255131906285426769L;

    public ELContextEvent(ELContext source) {
        super(source);
    }

    public ELContext getELContext() {
        return (ELContext)this.getSource();
    }
}

