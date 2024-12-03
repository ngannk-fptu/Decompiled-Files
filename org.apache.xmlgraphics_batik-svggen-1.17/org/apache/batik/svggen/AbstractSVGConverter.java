/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.batik.svggen.ErrorConstants;
import org.apache.batik.svggen.SVGConverter;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2DRuntimeException;

public abstract class AbstractSVGConverter
implements SVGConverter,
ErrorConstants {
    protected SVGGeneratorContext generatorContext;
    protected Map descMap = new HashMap();
    protected List defSet = new LinkedList();

    public AbstractSVGConverter(SVGGeneratorContext generatorContext) {
        if (generatorContext == null) {
            throw new SVGGraphics2DRuntimeException("generatorContext should not be null");
        }
        this.generatorContext = generatorContext;
    }

    @Override
    public List getDefinitionSet() {
        return this.defSet;
    }

    public final String doubleString(double value) {
        return this.generatorContext.doubleString(value);
    }
}

