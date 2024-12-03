/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.CollectionImage;
import javax.media.jai.CollectionOp;

public interface CollectionImageFactory {
    public CollectionImage create(ParameterBlock var1, RenderingHints var2);

    public CollectionImage update(ParameterBlock var1, RenderingHints var2, ParameterBlock var3, RenderingHints var4, CollectionImage var5, CollectionOp var6);
}

