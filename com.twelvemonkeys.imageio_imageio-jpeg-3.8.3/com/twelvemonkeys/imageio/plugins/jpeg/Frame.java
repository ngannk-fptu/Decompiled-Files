/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.imageio.stream.SubImageInputStream
 */
package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.imageio.plugins.jpeg.Segment;
import com.twelvemonkeys.imageio.stream.SubImageInputStream;
import java.io.DataInput;
import java.io.IOException;
import java.util.Arrays;
import javax.imageio.IIOException;
import javax.imageio.stream.ImageInputStream;

final class Frame
extends Segment {
    final int samplePrecision;
    final int lines;
    final int samplesPerLine;
    final Component[] components;

    private Frame(int n, int n2, int n3, int n4, Component[] componentArray) {
        super(n);
        this.samplePrecision = n2;
        this.lines = n3;
        this.samplesPerLine = n4;
        this.components = componentArray;
    }

    int process() {
        return this.marker & 0x3F;
    }

    int componentsInFrame() {
        return this.components.length;
    }

    Component getComponent(int n) {
        for (Component component : this.components) {
            if (component.id != n) continue;
            return component;
        }
        throw new IllegalArgumentException(String.format("No such component id: %d", n));
    }

    public String toString() {
        return String.format("SOF%d[%04x, precision: %d, lines: %d, samples/line: %d, components: %s]", this.process(), this.marker, this.samplePrecision, this.lines, this.samplesPerLine, Arrays.toString(this.components));
    }

    static Frame read(int n, DataInput dataInput, int n2) throws IOException {
        int n3 = dataInput.readUnsignedByte();
        int n4 = dataInput.readUnsignedShort();
        int n5 = dataInput.readUnsignedShort();
        int n6 = dataInput.readUnsignedByte();
        int n7 = 8 + n6 * 3;
        if (n2 != n7) {
            throw new IIOException(String.format("Unexpected SOF length: %d != %d", n2, n7));
        }
        Component[] componentArray = new Component[n6];
        for (int i = 0; i < n6; ++i) {
            int n8 = dataInput.readUnsignedByte();
            int n9 = dataInput.readUnsignedByte();
            int n10 = dataInput.readUnsignedByte();
            componentArray[i] = new Component(n8, (n9 & 0xF0) >> 4, n9 & 0xF, n10);
        }
        return new Frame(n, n3, n4, n5, componentArray);
    }

    static Frame read(int n, ImageInputStream imageInputStream) throws IOException {
        int n2 = imageInputStream.readUnsignedShort();
        return Frame.read(n, (DataInput)new SubImageInputStream(imageInputStream, (long)n2), n2);
    }

    public static final class Component {
        final int id;
        final int hSub;
        final int vSub;
        final int qtSel;

        Component(int n, int n2, int n3, int n4) {
            this.id = n;
            this.hSub = n2;
            this.vSub = n3;
            this.qtSel = n4;
        }

        public String toString() {
            Integer n = this.id >= 97 && this.id <= 122 || this.id >= 65 && this.id <= 90 ? "'" + (char)this.id + "'" : Integer.valueOf(this.id);
            return String.format("id: %s, sub: %d/%d, sel: %d", n, this.hSub, this.vSub, this.qtSel);
        }
    }
}

