/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.plugins.jpeg;

import com.twelvemonkeys.imageio.plugins.jpeg.Application;
import com.twelvemonkeys.imageio.plugins.jpeg.Comment;
import com.twelvemonkeys.imageio.plugins.jpeg.Frame;
import com.twelvemonkeys.imageio.plugins.jpeg.HuffmanTable;
import com.twelvemonkeys.imageio.plugins.jpeg.QuantizationTable;
import com.twelvemonkeys.imageio.plugins.jpeg.RestartInterval;
import com.twelvemonkeys.imageio.plugins.jpeg.Scan;
import com.twelvemonkeys.imageio.plugins.jpeg.Unknown;
import com.twelvemonkeys.lang.Validate;
import java.io.DataInput;
import java.io.IOException;

abstract class Segment {
    final int marker;

    protected Segment(int n) {
        this.marker = (Integer)Validate.isTrue((n >> 8 == 255 ? 1 : 0) != 0, (Object)n, (String)"Unknown JPEG marker: 0x%04x");
    }

    static Segment read(int n, String string, int n2, DataInput dataInput) throws IOException {
        switch (n) {
            case 65476: {
                return HuffmanTable.read(dataInput, n2);
            }
            case 65499: {
                return QuantizationTable.read(dataInput, n2);
            }
            case 65472: 
            case 65473: 
            case 65474: 
            case 65475: 
            case 65477: 
            case 65478: 
            case 65479: 
            case 65481: 
            case 65482: 
            case 65483: 
            case 65485: 
            case 65486: 
            case 65487: {
                return Frame.read(n, dataInput, n2);
            }
            case 65498: {
                return Scan.read(dataInput, n2);
            }
            case 65534: {
                return Comment.read(dataInput, n2);
            }
            case 65501: {
                return RestartInterval.read(dataInput, n2);
            }
            case 65504: 
            case 65505: 
            case 65506: 
            case 65507: 
            case 65508: 
            case 65509: 
            case 65510: 
            case 65511: 
            case 65512: 
            case 65513: 
            case 65514: 
            case 65515: 
            case 65516: 
            case 65517: 
            case 65518: 
            case 65519: {
                return Application.read(n, string, dataInput, n2);
            }
        }
        return Unknown.read(n, n2, dataInput);
    }
}

