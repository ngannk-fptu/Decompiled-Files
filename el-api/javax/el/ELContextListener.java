/*
 * Decompiled with CFR 0.152.
 */
package javax.el;

import java.util.EventListener;
import javax.el.ELContextEvent;

public interface ELContextListener
extends EventListener {
    public void contextCreated(ELContextEvent var1);
}

