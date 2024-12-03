/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.OperationRegistry;
import javax.media.jai.PropertyChangeEmitter;
import javax.media.jai.PropertyGenerator;
import javax.media.jai.PropertySource;

public interface OperationNode
extends PropertySource,
PropertyChangeEmitter {
    public String getRegistryModeName();

    public String getOperationName();

    public void setOperationName(String var1);

    public OperationRegistry getRegistry();

    public void setRegistry(OperationRegistry var1);

    public ParameterBlock getParameterBlock();

    public void setParameterBlock(ParameterBlock var1);

    public RenderingHints getRenderingHints();

    public void setRenderingHints(RenderingHints var1);

    public Object getDynamicProperty(String var1);

    public void addPropertyGenerator(PropertyGenerator var1);

    public void copyPropertyFromSource(String var1, int var2);

    public void suppressProperty(String var1);
}

