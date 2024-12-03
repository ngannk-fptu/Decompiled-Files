/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hemf.record.emfplus;

import java.io.IOException;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.hemf.draw.HemfGraphics;
import org.apache.poi.hemf.record.emf.HemfRecord;
import org.apache.poi.hemf.record.emfplus.HemfPlusRecord;
import org.apache.poi.hemf.record.emfplus.HemfPlusRecordType;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.GenericRecordJsonWriter;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndianInputStream;

@Internal
public class HemfPlusHeader
implements HemfPlusRecord {
    private static final int[] FLAGS_MASK = new int[]{0, 1};
    private static final String[] FLAGS_NAMES = new String[]{"EMF_PLUS_MODE", "DUAL_MODE"};
    private static final int[] EMFFLAGS_MASK = new int[]{0, 1};
    private static final String[] EMFFLAGS_NAMES = new String[]{"CONTEXT_PRINTER", "CONTEXT_VIDEO"};
    private int flags;
    private final EmfPlusGraphicsVersion version = new EmfPlusGraphicsVersion();
    private long emfPlusFlags;
    private long logicalDpiX;
    private long logicalDpiY;

    @Override
    public HemfPlusRecordType getEmfPlusRecordType() {
        return HemfPlusRecordType.header;
    }

    @Override
    public int getFlags() {
        return this.flags;
    }

    @Override
    public long init(LittleEndianInputStream leis, long dataSize, long recordId, int flags) throws IOException {
        this.flags = flags;
        this.version.init(leis);
        assert (this.version.getMetafileSignature() == 900097 && this.version.getGraphicsVersion() != null);
        this.emfPlusFlags = leis.readUInt();
        this.logicalDpiX = leis.readUInt();
        this.logicalDpiY = leis.readUInt();
        return 16L;
    }

    public EmfPlusGraphicsVersion getVersion() {
        return this.version;
    }

    public boolean isEmfPlusDualMode() {
        return (this.flags & 1) == 1;
    }

    public long getEmfPlusFlags() {
        return this.emfPlusFlags;
    }

    public long getLogicalDpiX() {
        return this.logicalDpiX;
    }

    public long getLogicalDpiY() {
        return this.logicalDpiY;
    }

    @Override
    public void draw(HemfGraphics ctx) {
        ctx.setRenderState(HemfGraphics.EmfRenderState.EMF_DCONTEXT);
    }

    @Override
    public void calcBounds(HemfRecord.RenderBounds holder) {
        holder.setState(HemfGraphics.EmfRenderState.EMF_DCONTEXT);
    }

    public String toString() {
        return GenericRecordJsonWriter.marshal(this);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("flags", GenericRecordUtil.getEnumBitsAsString(this::getFlags, FLAGS_MASK, FLAGS_NAMES), "version", this::getVersion, "emfPlusFlags", GenericRecordUtil.getEnumBitsAsString(this::getEmfPlusFlags, EMFFLAGS_MASK, EMFFLAGS_NAMES), "logicalDpiX", this::getLogicalDpiX, "logicalDpiY", this::getLogicalDpiY);
    }

    public static class EmfPlusGraphicsVersion
    implements GenericRecord {
        private static final BitField METAFILE_SIGNATURE = BitFieldFactory.getInstance(-4096);
        private static final BitField GRAPHICS_VERSION = BitFieldFactory.getInstance(4095);
        private int metafileSignature;
        private GraphicsVersion graphicsVersion;

        public int getMetafileSignature() {
            return this.metafileSignature;
        }

        public GraphicsVersion getGraphicsVersion() {
            return this.graphicsVersion;
        }

        public long init(LittleEndianInputStream leis) throws IOException {
            int val = leis.readInt();
            this.metafileSignature = METAFILE_SIGNATURE.getValue(val);
            this.graphicsVersion = GraphicsVersion.valueOf(GRAPHICS_VERSION.getValue(val));
            return 4L;
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("metafileSignature", this::getMetafileSignature, "graphicsVersion", this::getGraphicsVersion);
        }
    }

    public static enum GraphicsVersion {
        V1(1),
        V1_1(2);

        public final int id;

        private GraphicsVersion(int id) {
            this.id = id;
        }

        public static GraphicsVersion valueOf(int id) {
            for (GraphicsVersion wrt : GraphicsVersion.values()) {
                if (wrt.id != id) continue;
                return wrt;
            }
            return null;
        }
    }
}

