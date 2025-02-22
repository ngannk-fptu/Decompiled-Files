/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.universalchardet.prober.statemachine;

import org.mozilla.universalchardet.Constants;
import org.mozilla.universalchardet.prober.statemachine.PkgInt;
import org.mozilla.universalchardet.prober.statemachine.SMModel;

public class UTF8SMModel
extends SMModel {
    public static final int UTF8_CLASS_FACTOR = 16;
    private static int[] utf8ClassTable = new int[]{PkgInt.pack4bits(1, 1, 1, 1, 1, 1, 1, 1), PkgInt.pack4bits(1, 1, 1, 1, 1, 1, 0, 0), PkgInt.pack4bits(1, 1, 1, 1, 1, 1, 1, 1), PkgInt.pack4bits(1, 1, 1, 0, 1, 1, 1, 1), PkgInt.pack4bits(1, 1, 1, 1, 1, 1, 1, 1), PkgInt.pack4bits(1, 1, 1, 1, 1, 1, 1, 1), PkgInt.pack4bits(1, 1, 1, 1, 1, 1, 1, 1), PkgInt.pack4bits(1, 1, 1, 1, 1, 1, 1, 1), PkgInt.pack4bits(1, 1, 1, 1, 1, 1, 1, 1), PkgInt.pack4bits(1, 1, 1, 1, 1, 1, 1, 1), PkgInt.pack4bits(1, 1, 1, 1, 1, 1, 1, 1), PkgInt.pack4bits(1, 1, 1, 1, 1, 1, 1, 1), PkgInt.pack4bits(1, 1, 1, 1, 1, 1, 1, 1), PkgInt.pack4bits(1, 1, 1, 1, 1, 1, 1, 1), PkgInt.pack4bits(1, 1, 1, 1, 1, 1, 1, 1), PkgInt.pack4bits(1, 1, 1, 1, 1, 1, 1, 1), PkgInt.pack4bits(2, 2, 2, 2, 3, 3, 3, 3), PkgInt.pack4bits(4, 4, 4, 4, 4, 4, 4, 4), PkgInt.pack4bits(4, 4, 4, 4, 4, 4, 4, 4), PkgInt.pack4bits(4, 4, 4, 4, 4, 4, 4, 4), PkgInt.pack4bits(5, 5, 5, 5, 5, 5, 5, 5), PkgInt.pack4bits(5, 5, 5, 5, 5, 5, 5, 5), PkgInt.pack4bits(5, 5, 5, 5, 5, 5, 5, 5), PkgInt.pack4bits(5, 5, 5, 5, 5, 5, 5, 5), PkgInt.pack4bits(0, 0, 6, 6, 6, 6, 6, 6), PkgInt.pack4bits(6, 6, 6, 6, 6, 6, 6, 6), PkgInt.pack4bits(6, 6, 6, 6, 6, 6, 6, 6), PkgInt.pack4bits(6, 6, 6, 6, 6, 6, 6, 6), PkgInt.pack4bits(7, 8, 8, 8, 8, 8, 8, 8), PkgInt.pack4bits(8, 8, 8, 8, 8, 9, 8, 8), PkgInt.pack4bits(10, 11, 11, 11, 11, 11, 11, 11), PkgInt.pack4bits(12, 13, 13, 13, 14, 15, 0, 0)};
    private static int[] utf8StateTable = new int[]{PkgInt.pack4bits(1, 0, 1, 1, 1, 1, 12, 10), PkgInt.pack4bits(9, 11, 8, 7, 6, 5, 4, 3), PkgInt.pack4bits(1, 1, 1, 1, 1, 1, 1, 1), PkgInt.pack4bits(1, 1, 1, 1, 1, 1, 1, 1), PkgInt.pack4bits(2, 2, 2, 2, 2, 2, 2, 2), PkgInt.pack4bits(2, 2, 2, 2, 2, 2, 2, 2), PkgInt.pack4bits(1, 1, 5, 5, 5, 5, 1, 1), PkgInt.pack4bits(1, 1, 1, 1, 1, 1, 1, 1), PkgInt.pack4bits(1, 1, 1, 5, 5, 5, 1, 1), PkgInt.pack4bits(1, 1, 1, 1, 1, 1, 1, 1), PkgInt.pack4bits(1, 1, 7, 7, 7, 7, 1, 1), PkgInt.pack4bits(1, 1, 1, 1, 1, 1, 1, 1), PkgInt.pack4bits(1, 1, 1, 1, 7, 7, 1, 1), PkgInt.pack4bits(1, 1, 1, 1, 1, 1, 1, 1), PkgInt.pack4bits(1, 1, 9, 9, 9, 9, 1, 1), PkgInt.pack4bits(1, 1, 1, 1, 1, 1, 1, 1), PkgInt.pack4bits(1, 1, 1, 1, 9, 9, 1, 1), PkgInt.pack4bits(1, 1, 1, 1, 1, 1, 1, 1), PkgInt.pack4bits(1, 1, 12, 12, 12, 12, 1, 1), PkgInt.pack4bits(1, 1, 1, 1, 1, 1, 1, 1), PkgInt.pack4bits(1, 1, 1, 1, 1, 12, 1, 1), PkgInt.pack4bits(1, 1, 1, 1, 1, 1, 1, 1), PkgInt.pack4bits(1, 1, 12, 12, 12, 1, 1, 1), PkgInt.pack4bits(1, 1, 1, 1, 1, 1, 1, 1), PkgInt.pack4bits(1, 1, 0, 0, 0, 0, 1, 1), PkgInt.pack4bits(1, 1, 1, 1, 1, 1, 1, 1)};
    private static int[] utf8CharLenTable = new int[]{0, 1, 0, 0, 0, 0, 2, 3, 3, 3, 4, 4, 5, 5, 6, 6};

    public UTF8SMModel() {
        super(new PkgInt(3, 7, 2, 15, utf8ClassTable), 16, new PkgInt(3, 7, 2, 15, utf8StateTable), utf8CharLenTable, Constants.CHARSET_UTF_8);
    }
}

