/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.zip;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipException;
import org.apache.tools.zip.AsiExtraField;
import org.apache.tools.zip.CentralDirectoryParsingZipExtraField;
import org.apache.tools.zip.JarMarker;
import org.apache.tools.zip.UnicodeCommentExtraField;
import org.apache.tools.zip.UnicodePathExtraField;
import org.apache.tools.zip.UnparseableExtraFieldData;
import org.apache.tools.zip.UnrecognizedExtraField;
import org.apache.tools.zip.Zip64ExtendedInformationExtraField;
import org.apache.tools.zip.ZipExtraField;
import org.apache.tools.zip.ZipShort;

public class ExtraFieldUtils {
    private static final int WORD = 4;
    private static final Map<ZipShort, Class<?>> implementations = new ConcurrentHashMap();

    public static void register(Class<?> c) {
        try {
            ZipExtraField ze = (ZipExtraField)c.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            implementations.put(ze.getHeaderId(), c);
        }
        catch (ClassCastException cc) {
            throw new RuntimeException(c + " doesn't implement ZipExtraField");
        }
        catch (InstantiationException ie) {
            throw new RuntimeException(c + " is not a concrete class");
        }
        catch (IllegalAccessException ie) {
            throw new RuntimeException(c + "'s no-arg constructor is not public");
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException(c + "'s no-arg constructor not found");
        }
        catch (InvocationTargetException e) {
            throw new RuntimeException(c + "'s no-arg constructor threw an exception:" + e.getMessage());
        }
    }

    public static ZipExtraField createExtraField(ZipShort headerId) throws InstantiationException, IllegalAccessException {
        Class<?> c = implementations.get(headerId);
        if (c != null) {
            try {
                return (ZipExtraField)c.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            }
            catch (InvocationTargetException e) {
                throw (InstantiationException)new InstantiationException().initCause(e.getTargetException());
            }
            catch (NoSuchMethodException e) {
                throw (InstantiationException)new InstantiationException().initCause(e);
            }
        }
        UnrecognizedExtraField u = new UnrecognizedExtraField();
        u.setHeaderId(headerId);
        return u;
    }

    public static ZipExtraField[] parse(byte[] data) throws ZipException {
        return ExtraFieldUtils.parse(data, true, UnparseableExtraField.THROW);
    }

    public static ZipExtraField[] parse(byte[] data, boolean local) throws ZipException {
        return ExtraFieldUtils.parse(data, local, UnparseableExtraField.THROW);
    }

    public static ZipExtraField[] parse(byte[] data, boolean local, UnparseableExtraField onUnparseableData) throws ZipException {
        int length;
        ArrayList<ZipExtraField> v = new ArrayList<ZipExtraField>();
        block7: for (int start = 0; start <= data.length - 4; start += length + 4) {
            ZipShort headerId = new ZipShort(data, start);
            length = new ZipShort(data, start + 2).getValue();
            if (start + 4 + length > data.length) {
                switch (onUnparseableData.getKey()) {
                    case 0: {
                        throw new ZipException("bad extra field starting at " + start + ".  Block length of " + length + " bytes exceeds remaining data of " + (data.length - start - 4) + " bytes.");
                    }
                    case 2: {
                        UnparseableExtraFieldData field = new UnparseableExtraFieldData();
                        if (local) {
                            field.parseFromLocalFileData(data, start, data.length - start);
                        } else {
                            field.parseFromCentralDirectoryData(data, start, data.length - start);
                        }
                        v.add(field);
                    }
                    case 1: {
                        break block7;
                    }
                    default: {
                        throw new ZipException("unknown UnparseableExtraField key: " + onUnparseableData.getKey());
                    }
                }
            }
            try {
                ZipExtraField ze = ExtraFieldUtils.createExtraField(headerId);
                if (local || !(ze instanceof CentralDirectoryParsingZipExtraField)) {
                    ze.parseFromLocalFileData(data, start + 4, length);
                } else {
                    ((CentralDirectoryParsingZipExtraField)ze).parseFromCentralDirectoryData(data, start + 4, length);
                }
                v.add(ze);
                continue;
            }
            catch (IllegalAccessException | InstantiationException ie) {
                throw new ZipException(ie.getMessage());
            }
        }
        ZipExtraField[] result = new ZipExtraField[v.size()];
        return v.toArray(result);
    }

    public static byte[] mergeLocalFileDataData(ZipExtraField[] data) {
        boolean lastIsUnparseableHolder = data.length > 0 && data[data.length - 1] instanceof UnparseableExtraFieldData;
        int regularExtraFieldCount = lastIsUnparseableHolder ? data.length - 1 : data.length;
        int sum = 4 * regularExtraFieldCount;
        for (ZipExtraField element : data) {
            sum += element.getLocalFileDataLength().getValue();
        }
        byte[] result = new byte[sum];
        int start = 0;
        for (int i = 0; i < regularExtraFieldCount; ++i) {
            System.arraycopy(data[i].getHeaderId().getBytes(), 0, result, start, 2);
            System.arraycopy(data[i].getLocalFileDataLength().getBytes(), 0, result, start + 2, 2);
            byte[] local = data[i].getLocalFileDataData();
            System.arraycopy(local, 0, result, start + 4, local.length);
            start += local.length + 4;
        }
        if (lastIsUnparseableHolder) {
            byte[] local = data[data.length - 1].getLocalFileDataData();
            System.arraycopy(local, 0, result, start, local.length);
        }
        return result;
    }

    public static byte[] mergeCentralDirectoryData(ZipExtraField[] data) {
        boolean lastIsUnparseableHolder = data.length > 0 && data[data.length - 1] instanceof UnparseableExtraFieldData;
        int regularExtraFieldCount = lastIsUnparseableHolder ? data.length - 1 : data.length;
        int sum = 4 * regularExtraFieldCount;
        for (ZipExtraField element : data) {
            sum += element.getCentralDirectoryLength().getValue();
        }
        byte[] result = new byte[sum];
        int start = 0;
        for (int i = 0; i < regularExtraFieldCount; ++i) {
            System.arraycopy(data[i].getHeaderId().getBytes(), 0, result, start, 2);
            System.arraycopy(data[i].getCentralDirectoryLength().getBytes(), 0, result, start + 2, 2);
            byte[] local = data[i].getCentralDirectoryData();
            System.arraycopy(local, 0, result, start + 4, local.length);
            start += local.length + 4;
        }
        if (lastIsUnparseableHolder) {
            byte[] local = data[data.length - 1].getCentralDirectoryData();
            System.arraycopy(local, 0, result, start, local.length);
        }
        return result;
    }

    static {
        ExtraFieldUtils.register(AsiExtraField.class);
        ExtraFieldUtils.register(JarMarker.class);
        ExtraFieldUtils.register(UnicodePathExtraField.class);
        ExtraFieldUtils.register(UnicodeCommentExtraField.class);
        ExtraFieldUtils.register(Zip64ExtendedInformationExtraField.class);
    }

    public static final class UnparseableExtraField {
        public static final int THROW_KEY = 0;
        public static final int SKIP_KEY = 1;
        public static final int READ_KEY = 2;
        public static final UnparseableExtraField THROW = new UnparseableExtraField(0);
        public static final UnparseableExtraField SKIP = new UnparseableExtraField(1);
        public static final UnparseableExtraField READ = new UnparseableExtraField(2);
        private final int key;

        private UnparseableExtraField(int k) {
            this.key = k;
        }

        public int getKey() {
            return this.key;
        }
    }
}

