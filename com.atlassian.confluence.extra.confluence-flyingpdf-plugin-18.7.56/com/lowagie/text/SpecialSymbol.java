/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text;

import com.lowagie.text.Chunk;
import com.lowagie.text.Font;

public class SpecialSymbol {
    public static int index(String string) {
        int length = string.length();
        for (int i = 0; i < length; ++i) {
            if (SpecialSymbol.getCorrespondingSymbol(string.charAt(i)) == ' ') continue;
            return i;
        }
        return -1;
    }

    public static Chunk get(char c, Font font) {
        char greek = SpecialSymbol.getCorrespondingSymbol(c);
        if (greek == ' ') {
            return new Chunk(String.valueOf(c), font);
        }
        Font symbol = new Font(3, font.getSize(), font.getStyle(), font.getColor());
        String s = String.valueOf(greek);
        return new Chunk(s, symbol);
    }

    public static char getCorrespondingSymbol(char c) {
        switch (c) {
            case '\u0391': {
                return 'A';
            }
            case '\u0392': {
                return 'B';
            }
            case '\u0393': {
                return 'G';
            }
            case '\u0394': {
                return 'D';
            }
            case '\u0395': {
                return 'E';
            }
            case '\u0396': {
                return 'Z';
            }
            case '\u0397': {
                return 'H';
            }
            case '\u0398': {
                return 'Q';
            }
            case '\u0399': {
                return 'I';
            }
            case '\u039a': {
                return 'K';
            }
            case '\u039b': {
                return 'L';
            }
            case '\u039c': {
                return 'M';
            }
            case '\u039d': {
                return 'N';
            }
            case '\u039e': {
                return 'X';
            }
            case '\u039f': {
                return 'O';
            }
            case '\u03a0': {
                return 'P';
            }
            case '\u03a1': {
                return 'R';
            }
            case '\u03a3': {
                return 'S';
            }
            case '\u03a4': {
                return 'T';
            }
            case '\u03a5': {
                return 'U';
            }
            case '\u03a6': {
                return 'F';
            }
            case '\u03a7': {
                return 'C';
            }
            case '\u03a8': {
                return 'Y';
            }
            case '\u03a9': {
                return 'W';
            }
            case '\u03b1': {
                return 'a';
            }
            case '\u03b2': {
                return 'b';
            }
            case '\u03b3': {
                return 'g';
            }
            case '\u03b4': {
                return 'd';
            }
            case '\u03b5': {
                return 'e';
            }
            case '\u03b6': {
                return 'z';
            }
            case '\u03b7': {
                return 'h';
            }
            case '\u03b8': {
                return 'q';
            }
            case '\u03b9': {
                return 'i';
            }
            case '\u03ba': {
                return 'k';
            }
            case '\u03bb': {
                return 'l';
            }
            case '\u03bc': {
                return 'm';
            }
            case '\u03bd': {
                return 'n';
            }
            case '\u03be': {
                return 'x';
            }
            case '\u03bf': {
                return 'o';
            }
            case '\u03c0': {
                return 'p';
            }
            case '\u03c1': {
                return 'r';
            }
            case '\u03c2': {
                return 'V';
            }
            case '\u03c3': {
                return 's';
            }
            case '\u03c4': {
                return 't';
            }
            case '\u03c5': {
                return 'u';
            }
            case '\u03c6': {
                return 'f';
            }
            case '\u03c7': {
                return 'c';
            }
            case '\u03c8': {
                return 'y';
            }
            case '\u03c9': {
                return 'w';
            }
        }
        return ' ';
    }
}

