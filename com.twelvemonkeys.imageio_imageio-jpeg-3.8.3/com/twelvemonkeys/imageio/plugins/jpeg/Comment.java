/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.imageio.plugins.jpeg.Segment;
import java.io.DataInput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

class Comment
extends Segment {
    final String comment;

    private Comment(String string) {
        super(65534);
        this.comment = string;
    }

    public String toString() {
        return "COM[" + this.comment + "]";
    }

    public static Segment read(DataInput dataInput, int n) throws IOException {
        byte[] byArray = new byte[n - 2];
        dataInput.readFully(byArray);
        return new Comment(new String(byArray, StandardCharsets.UTF_8));
    }
}

