/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.imageio.stream.SubImageInputStream
 *  com.twelvemonkeys.lang.StringUtil
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.metadata.psd;

import com.twelvemonkeys.imageio.metadata.Directory;
import com.twelvemonkeys.imageio.metadata.MetadataReader;
import com.twelvemonkeys.imageio.metadata.psd.PSDDirectory;
import com.twelvemonkeys.imageio.metadata.psd.PSDEntry;
import com.twelvemonkeys.imageio.stream.SubImageInputStream;
import com.twelvemonkeys.lang.StringUtil;
import com.twelvemonkeys.lang.Validate;
import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.IIOException;
import javax.imageio.stream.ImageInputStream;

public final class PSDReader
extends MetadataReader {
    @Override
    public Directory read(ImageInputStream imageInputStream) throws IOException {
        Validate.notNull((Object)imageInputStream, (String)"input");
        ArrayList<PSDEntry> arrayList = new ArrayList<PSDEntry>();
        try {
            while (true) {
                int n = imageInputStream.readInt();
                switch (n) {
                    case 943868237: 
                    case 1097287783: 
                    case 1145262930: 
                    case 1298486113: 
                    case 1346917716: {
                        break;
                    }
                    default: {
                        throw new IIOException(String.format("Wrong image resource type, expected '8BIM': '%08x'", n));
                    }
                }
                short s = imageInputStream.readShort();
                PSDResource pSDResource = new PSDResource(s, imageInputStream);
                arrayList.add(new PSDEntry(s, pSDResource.name(), pSDResource.data()));
            }
        }
        catch (EOFException eOFException) {
            return new PSDDirectory(arrayList);
        }
    }

    protected static class PSDResource {
        final short id;
        final String name;
        final long size;
        byte[] data;

        static String readPascalString(DataInput dataInput) throws IOException {
            int n = dataInput.readUnsignedByte();
            if (n == 0) {
                return "";
            }
            byte[] byArray = new byte[n];
            dataInput.readFully(byArray);
            return StringUtil.decode((byte[])byArray, (int)0, (int)byArray.length, (String)"ASCII");
        }

        PSDResource(short s, ImageInputStream imageInputStream) throws IOException {
            this.id = s;
            this.name = PSDResource.readPascalString(imageInputStream);
            int n = this.name.length() + 1;
            if (n % 2 != 0) {
                imageInputStream.readByte();
            }
            this.size = imageInputStream.readUnsignedInt();
            long l = imageInputStream.getStreamPosition();
            this.readData((ImageInputStream)new SubImageInputStream(imageInputStream, this.size));
            if (imageInputStream.getStreamPosition() != l + this.size) {
                imageInputStream.seek(l + this.size);
            }
            if (this.size % 2L != 0L) {
                imageInputStream.read();
            }
        }

        protected void readData(ImageInputStream imageInputStream) throws IOException {
            this.data = new byte[(int)this.size];
            imageInputStream.readFully(this.data);
        }

        public final int id() {
            return this.id;
        }

        public final byte[] data() {
            return this.data;
        }

        public String name() {
            return this.name;
        }

        public String toString() {
            StringBuilder stringBuilder = this.toStringBuilder();
            stringBuilder.append(", data length: ");
            stringBuilder.append(this.size);
            stringBuilder.append("]");
            return stringBuilder.toString();
        }

        protected StringBuilder toStringBuilder() {
            StringBuilder stringBuilder = new StringBuilder(this.getClass().getSimpleName());
            stringBuilder.append("[ID: 0x");
            stringBuilder.append(Integer.toHexString(this.id));
            if (this.name != null && this.name.trim().length() != 0) {
                stringBuilder.append(", name: \"");
                stringBuilder.append(this.name);
                stringBuilder.append("\"");
            }
            return stringBuilder;
        }
    }
}

