/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.loader;

import java.io.FileNotFoundException;
import javax.xml.transform.Source;
import org.apache.xmlgraphics.image.loader.ImageContext;

public interface ImageSessionContext {
    public ImageContext getParentContext();

    public float getTargetResolution();

    public Source newSource(String var1);

    public Source getSource(String var1);

    public Source needSource(String var1) throws FileNotFoundException;

    public void returnSource(String var1, Source var2);
}

