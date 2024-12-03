/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Native
 *  com.sun.jna.Pointer
 *  com.sun.jna.Structure
 *  com.sun.jna.Structure$FieldOrder
 */
package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public interface Wdm {

    public static abstract class KEY_INFORMATION_CLASS {
        public static final int KeyBasicInformation = 0;
        public static final int KeyNodeInformation = 1;
        public static final int KeyFullInformation = 2;
        public static final int KeyNameInformation = 3;
        public static final int KeyCachedInformation = 4;
        public static final int KeyVirtualizationInformation = 5;
    }

    @Structure.FieldOrder(value={"LastWriteTime", "TitleIndex", "NameLength", "Name"})
    public static class KEY_BASIC_INFORMATION
    extends Structure {
        public long LastWriteTime;
        public int TitleIndex;
        public int NameLength;
        public char[] Name;

        public KEY_BASIC_INFORMATION() {
        }

        public KEY_BASIC_INFORMATION(int size) {
            this.NameLength = size - 16;
            this.Name = new char[this.NameLength];
            this.allocateMemory();
        }

        public KEY_BASIC_INFORMATION(Pointer memory) {
            super(memory);
            this.read();
        }

        public String getName() {
            return Native.toString((char[])this.Name);
        }

        public void read() {
            super.read();
            this.Name = new char[this.NameLength / 2];
            this.readField("Name");
        }
    }
}

