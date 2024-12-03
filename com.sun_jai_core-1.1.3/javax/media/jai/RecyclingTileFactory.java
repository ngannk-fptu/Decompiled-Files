/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import com.sun.media.jai.util.DataBufferUtils;
import java.awt.Point;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Observable;
import javax.media.jai.JaiI18N;
import javax.media.jai.TileFactory;
import javax.media.jai.TileRecycler;

public class RecyclingTileFactory
extends Observable
implements TileFactory,
TileRecycler {
    private static final boolean DEBUG = false;
    private HashMap recycledArrays = new HashMap(32);
    private long memoryUsed = 0L;

    private static long getBufferSizeCSM(ComponentSampleModel csm) {
        int scanlineStride;
        int pixelStride;
        int[] bandOffsets = csm.getBandOffsets();
        int maxBandOff = bandOffsets[0];
        for (int i = 1; i < bandOffsets.length; ++i) {
            maxBandOff = Math.max(maxBandOff, bandOffsets[i]);
        }
        long size = 0L;
        if (maxBandOff >= 0) {
            size += (long)(maxBandOff + 1);
        }
        if ((pixelStride = csm.getPixelStride()) > 0) {
            size += (long)(pixelStride * (csm.getWidth() - 1));
        }
        if ((scanlineStride = csm.getScanlineStride()) > 0) {
            size += (long)(scanlineStride * (csm.getHeight() - 1));
        }
        return size;
    }

    private static long getNumBanksCSM(ComponentSampleModel csm) {
        int[] bankIndices = csm.getBankIndices();
        int maxIndex = bankIndices[0];
        for (int i = 1; i < bankIndices.length; ++i) {
            int bankIndex = bankIndices[i];
            if (bankIndex <= maxIndex) continue;
            maxIndex = bankIndex;
        }
        return maxIndex + 1;
    }

    private static SoftReference getBankReference(DataBuffer db) {
        Object array = null;
        switch (db.getDataType()) {
            case 0: {
                array = ((DataBufferByte)db).getBankData();
                break;
            }
            case 1: {
                array = ((DataBufferUShort)db).getBankData();
                break;
            }
            case 2: {
                array = ((DataBufferShort)db).getBankData();
                break;
            }
            case 3: {
                array = ((DataBufferInt)db).getBankData();
                break;
            }
            case 4: {
                array = DataBufferUtils.getBankDataFloat(db);
                break;
            }
            case 5: {
                array = DataBufferUtils.getBankDataDouble(db);
                break;
            }
            default: {
                throw new UnsupportedOperationException(JaiI18N.getString("Generic3"));
            }
        }
        return new SoftReference<float[][]>((float[][])array);
    }

    private static long getDataBankSize(int dataType, int numBanks, int size) {
        int bytesPerElement = 0;
        switch (dataType) {
            case 0: {
                bytesPerElement = 1;
                break;
            }
            case 1: 
            case 2: {
                bytesPerElement = 2;
                break;
            }
            case 3: 
            case 4: {
                bytesPerElement = 4;
                break;
            }
            case 5: {
                bytesPerElement = 8;
                break;
            }
            default: {
                throw new UnsupportedOperationException(JaiI18N.getString("Generic3"));
            }
        }
        return numBanks * size * bytesPerElement;
    }

    public boolean canReclaimMemory() {
        return true;
    }

    public boolean isMemoryCache() {
        return true;
    }

    public long getMemoryUsed() {
        return this.memoryUsed;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void flush() {
        HashMap hashMap = this.recycledArrays;
        synchronized (hashMap) {
            this.recycledArrays.clear();
            this.memoryUsed = 0L;
        }
    }

    public WritableRaster createTile(SampleModel sampleModel, Point location) {
        Object array;
        if (sampleModel == null) {
            throw new IllegalArgumentException("sampleModel == null!");
        }
        if (location == null) {
            location = new Point(0, 0);
        }
        DataBuffer db = null;
        int type = sampleModel.getTransferType();
        long numBanks = 0L;
        long size = 0L;
        if (sampleModel instanceof ComponentSampleModel) {
            ComponentSampleModel csm = (ComponentSampleModel)sampleModel;
            numBanks = RecyclingTileFactory.getNumBanksCSM(csm);
            size = RecyclingTileFactory.getBufferSizeCSM(csm);
        } else if (sampleModel instanceof MultiPixelPackedSampleModel) {
            MultiPixelPackedSampleModel mppsm = (MultiPixelPackedSampleModel)sampleModel;
            numBanks = 1L;
            int dataTypeSize = DataBuffer.getDataTypeSize(type);
            size = mppsm.getScanlineStride() * mppsm.getHeight() + (mppsm.getDataBitOffset() + dataTypeSize - 1) / dataTypeSize;
        } else if (sampleModel instanceof SinglePixelPackedSampleModel) {
            SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)sampleModel;
            numBanks = 1L;
            size = sppsm.getScanlineStride() * (sppsm.getHeight() - 1) + sppsm.getWidth();
        }
        if (size != 0L && (array = this.getRecycledArray(type, numBanks, size)) != null) {
            switch (type) {
                case 0: {
                    byte[][] bankData = (byte[][])array;
                    int i = 0;
                    while ((long)i < numBanks) {
                        Arrays.fill(bankData[i], (byte)0);
                        ++i;
                    }
                    db = new DataBufferByte(bankData, (int)size);
                    break;
                }
                case 1: {
                    short[][] bankData = (short[][])array;
                    int i = 0;
                    while ((long)i < numBanks) {
                        Arrays.fill(bankData[i], (short)0);
                        ++i;
                    }
                    db = new DataBufferUShort(bankData, (int)size);
                    break;
                }
                case 2: {
                    short[][] bankData = (short[][])array;
                    int i = 0;
                    while ((long)i < numBanks) {
                        Arrays.fill(bankData[i], (short)0);
                        ++i;
                    }
                    db = new DataBufferShort(bankData, (int)size);
                    break;
                }
                case 3: {
                    int[][] bankData = (int[][])array;
                    int i = 0;
                    while ((long)i < numBanks) {
                        Arrays.fill(bankData[i], 0);
                        ++i;
                    }
                    db = new DataBufferInt(bankData, (int)size);
                    break;
                }
                case 4: {
                    float[][] bankData = (float[][])array;
                    int i = 0;
                    while ((long)i < numBanks) {
                        Arrays.fill(bankData[i], 0.0f);
                        ++i;
                    }
                    db = DataBufferUtils.createDataBufferFloat(bankData, (int)size);
                    break;
                }
                case 5: {
                    double[][] bankData = (double[][])array;
                    int i = 0;
                    while ((long)i < numBanks) {
                        Arrays.fill(bankData[i], 0.0);
                        ++i;
                    }
                    db = DataBufferUtils.createDataBufferDouble(bankData, (int)size);
                    break;
                }
                default: {
                    throw new IllegalArgumentException(JaiI18N.getString("Generic3"));
                }
            }
        }
        if (db == null) {
            db = sampleModel.createDataBuffer();
        }
        return Raster.createWritableRaster(sampleModel, db, location);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void recycleTile(Raster tile) {
        DataBuffer db = tile.getDataBuffer();
        Long key = new Long((long)db.getDataType() << 56 | (long)db.getNumBanks() << 32 | (long)db.getSize());
        HashMap hashMap = this.recycledArrays;
        synchronized (hashMap) {
            Object value = this.recycledArrays.get(key);
            ArrayList<SoftReference> arrays = null;
            arrays = value != null ? (ArrayList<SoftReference>)value : new ArrayList<SoftReference>();
            this.memoryUsed += RecyclingTileFactory.getDataBankSize(db.getDataType(), db.getNumBanks(), db.getSize());
            arrays.add(RecyclingTileFactory.getBankReference(db));
            if (value == null) {
                this.recycledArrays.put(key, arrays);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Object getRecycledArray(int arrayType, long numBanks, long arrayLength) {
        Long key = new Long((long)arrayType << 56 | numBanks << 32 | arrayLength);
        HashMap hashMap = this.recycledArrays;
        synchronized (hashMap) {
            Object value = this.recycledArrays.get(key);
            if (value != null) {
                ArrayList arrays = (ArrayList)value;
                for (int idx = arrays.size() - 1; idx >= 0; --idx) {
                    Object array;
                    SoftReference bankRef = (SoftReference)arrays.remove(idx);
                    this.memoryUsed -= RecyclingTileFactory.getDataBankSize(arrayType, (int)numBanks, (int)arrayLength);
                    if (idx == 0) {
                        this.recycledArrays.remove(key);
                    }
                    if ((array = bankRef.get()) == null) continue;
                    return array;
                }
            }
        }
        return null;
    }
}

