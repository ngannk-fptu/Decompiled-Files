/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.font.encoding;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.font.encoding.Encoding;

public class WinAnsiEncoding
extends Encoding {
    private static final Object[][] WIN_ANSI_ENCODING_TABLE = new Object[][]{{65, "A"}, {198, "AE"}, {193, "Aacute"}, {194, "Acircumflex"}, {196, "Adieresis"}, {192, "Agrave"}, {197, "Aring"}, {195, "Atilde"}, {66, "B"}, {67, "C"}, {199, "Ccedilla"}, {68, "D"}, {69, "E"}, {201, "Eacute"}, {202, "Ecircumflex"}, {203, "Edieresis"}, {200, "Egrave"}, {208, "Eth"}, {128, "Euro"}, {70, "F"}, {71, "G"}, {72, "H"}, {73, "I"}, {205, "Iacute"}, {206, "Icircumflex"}, {207, "Idieresis"}, {204, "Igrave"}, {74, "J"}, {75, "K"}, {76, "L"}, {77, "M"}, {78, "N"}, {209, "Ntilde"}, {79, "O"}, {140, "OE"}, {211, "Oacute"}, {212, "Ocircumflex"}, {214, "Odieresis"}, {210, "Ograve"}, {216, "Oslash"}, {213, "Otilde"}, {80, "P"}, {81, "Q"}, {82, "R"}, {83, "S"}, {138, "Scaron"}, {84, "T"}, {222, "Thorn"}, {85, "U"}, {218, "Uacute"}, {219, "Ucircumflex"}, {220, "Udieresis"}, {217, "Ugrave"}, {86, "V"}, {87, "W"}, {88, "X"}, {89, "Y"}, {221, "Yacute"}, {159, "Ydieresis"}, {90, "Z"}, {142, "Zcaron"}, {97, "a"}, {225, "aacute"}, {226, "acircumflex"}, {180, "acute"}, {228, "adieresis"}, {230, "ae"}, {224, "agrave"}, {38, "ampersand"}, {229, "aring"}, {94, "asciicircum"}, {126, "asciitilde"}, {42, "asterisk"}, {64, "at"}, {227, "atilde"}, {98, "b"}, {92, "backslash"}, {124, "bar"}, {123, "braceleft"}, {125, "braceright"}, {91, "bracketleft"}, {93, "bracketright"}, {166, "brokenbar"}, {149, "bullet"}, {99, "c"}, {231, "ccedilla"}, {184, "cedilla"}, {162, "cent"}, {136, "circumflex"}, {58, "colon"}, {44, "comma"}, {169, "copyright"}, {164, "currency"}, {100, "d"}, {134, "dagger"}, {135, "daggerdbl"}, {176, "degree"}, {168, "dieresis"}, {247, "divide"}, {36, "dollar"}, {101, "e"}, {233, "eacute"}, {234, "ecircumflex"}, {235, "edieresis"}, {232, "egrave"}, {56, "eight"}, {133, "ellipsis"}, {151, "emdash"}, {150, "endash"}, {61, "equal"}, {240, "eth"}, {33, "exclam"}, {161, "exclamdown"}, {102, "f"}, {53, "five"}, {131, "florin"}, {52, "four"}, {103, "g"}, {223, "germandbls"}, {96, "grave"}, {62, "greater"}, {171, "guillemotleft"}, {187, "guillemotright"}, {139, "guilsinglleft"}, {155, "guilsinglright"}, {104, "h"}, {45, "hyphen"}, {105, "i"}, {237, "iacute"}, {238, "icircumflex"}, {239, "idieresis"}, {236, "igrave"}, {106, "j"}, {107, "k"}, {108, "l"}, {60, "less"}, {172, "logicalnot"}, {109, "m"}, {175, "macron"}, {181, "mu"}, {215, "multiply"}, {110, "n"}, {57, "nine"}, {241, "ntilde"}, {35, "numbersign"}, {111, "o"}, {243, "oacute"}, {244, "ocircumflex"}, {246, "odieresis"}, {156, "oe"}, {242, "ograve"}, {49, "one"}, {189, "onehalf"}, {188, "onequarter"}, {185, "onesuperior"}, {170, "ordfeminine"}, {186, "ordmasculine"}, {248, "oslash"}, {245, "otilde"}, {112, "p"}, {182, "paragraph"}, {40, "parenleft"}, {41, "parenright"}, {37, "percent"}, {46, "period"}, {183, "periodcentered"}, {137, "perthousand"}, {43, "plus"}, {177, "plusminus"}, {113, "q"}, {63, "question"}, {191, "questiondown"}, {34, "quotedbl"}, {132, "quotedblbase"}, {147, "quotedblleft"}, {148, "quotedblright"}, {145, "quoteleft"}, {146, "quoteright"}, {130, "quotesinglbase"}, {39, "quotesingle"}, {114, "r"}, {174, "registered"}, {115, "s"}, {154, "scaron"}, {167, "section"}, {59, "semicolon"}, {55, "seven"}, {54, "six"}, {47, "slash"}, {32, "space"}, {163, "sterling"}, {116, "t"}, {254, "thorn"}, {51, "three"}, {190, "threequarters"}, {179, "threesuperior"}, {152, "tilde"}, {153, "trademark"}, {50, "two"}, {178, "twosuperior"}, {117, "u"}, {250, "uacute"}, {251, "ucircumflex"}, {252, "udieresis"}, {249, "ugrave"}, {95, "underscore"}, {118, "v"}, {119, "w"}, {120, "x"}, {121, "y"}, {253, "yacute"}, {255, "ydieresis"}, {165, "yen"}, {122, "z"}, {158, "zcaron"}, {48, "zero"}, {160, "nbspace"}, {173, "sfthyphen"}};
    public static final WinAnsiEncoding INSTANCE = new WinAnsiEncoding();

    public WinAnsiEncoding() {
        for (Object[] encodingEntry : WIN_ANSI_ENCODING_TABLE) {
            this.add((Integer)encodingEntry[0], encodingEntry[1].toString());
        }
        for (int i = 33; i <= 255; ++i) {
            if (this.codeToName.containsKey(i)) continue;
            this.add(i, "bullet");
        }
    }

    @Override
    public COSBase getCOSObject() {
        return COSName.WIN_ANSI_ENCODING;
    }

    @Override
    public String getEncodingName() {
        return "WinAnsiEncoding";
    }
}

