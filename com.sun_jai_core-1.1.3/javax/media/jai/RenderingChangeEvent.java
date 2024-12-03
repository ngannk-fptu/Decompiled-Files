/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.Shape;
import javax.media.jai.PlanarImage;
import javax.media.jai.PropertyChangeEventJAI;
import javax.media.jai.RenderedOp;

public class RenderingChangeEvent
extends PropertyChangeEventJAI {
    private Shape invalidRegion;

    public RenderingChangeEvent(RenderedOp source, PlanarImage oldRendering, PlanarImage newRendering, Shape invalidRegion) {
        super(source, "Rendering", oldRendering, newRendering);
        this.invalidRegion = invalidRegion;
    }

    public Shape getInvalidRegion() {
        return this.invalidRegion;
    }
}

