/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.io.Serializable;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;

public interface PropertyGenerator
extends Serializable {
    public String[] getPropertyNames();

    public Class getClass(String var1);

    public boolean canGenerateProperties(Object var1);

    public Object getProperty(String var1, Object var2);

    public Object getProperty(String var1, RenderedOp var2);

    public Object getProperty(String var1, RenderableOp var2);
}

