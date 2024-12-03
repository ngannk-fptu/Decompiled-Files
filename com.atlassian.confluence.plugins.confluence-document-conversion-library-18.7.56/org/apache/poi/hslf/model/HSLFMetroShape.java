/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.model;

import java.io.IOException;
import java.util.Iterator;
import java.util.ServiceLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ddf.AbstractEscherOptRecord;
import org.apache.poi.ddf.EscherComplexProperty;
import org.apache.poi.ddf.EscherPropertyTypes;
import org.apache.poi.ddf.EscherTertiaryOptRecord;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.sl.usermodel.MetroShapeProvider;
import org.apache.poi.sl.usermodel.Shape;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.sl.usermodel.TextRun;
import org.apache.poi.util.Internal;

@Internal
public class HSLFMetroShape<S extends Shape<S, P>, P extends TextParagraph<S, P, ? extends TextRun>> {
    private static final Logger LOGGER = LogManager.getLogger(HSLFMetroShape.class);
    private final HSLFShape shape;

    public HSLFMetroShape(HSLFShape shape) {
        this.shape = shape;
    }

    public byte[] getMetroBytes() {
        EscherComplexProperty ep = this.getMetroProp();
        return ep == null ? null : ep.getComplexData();
    }

    public boolean hasMetroBlob() {
        return this.getMetroProp() != null;
    }

    private EscherComplexProperty getMetroProp() {
        AbstractEscherOptRecord opt = (AbstractEscherOptRecord)this.shape.getEscherChild(EscherTertiaryOptRecord.RECORD_ID);
        return opt == null ? null : (EscherComplexProperty)opt.lookup(EscherPropertyTypes.GROUPSHAPE__METROBLOB.propNumber);
    }

    public Shape<S, P> getShape() {
        byte[] metroBytes = this.getMetroBytes();
        if (metroBytes == null) {
            return null;
        }
        ClassLoader cl = HSLFMetroShape.class.getClassLoader();
        IOException lastError = null;
        Iterator<MetroShapeProvider> iterator = ServiceLoader.load(MetroShapeProvider.class, cl).iterator();
        if (iterator.hasNext()) {
            MetroShapeProvider msp = iterator.next();
            try {
                return msp.parseShape(metroBytes);
            }
            catch (IOException ex) {
                lastError = ex;
            }
        }
        LOGGER.atError().withThrowable(lastError).log("can't process metro blob, check if all dependencies for POI OOXML are in the classpath.");
        return null;
    }
}

