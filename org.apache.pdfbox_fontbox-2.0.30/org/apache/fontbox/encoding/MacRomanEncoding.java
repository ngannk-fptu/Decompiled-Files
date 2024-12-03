/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.encoding;

import org.apache.fontbox.encoding.Encoding;

public class MacRomanEncoding
extends Encoding {
    private static final int CHAR_CODE = 0;
    private static final int CHAR_NAME = 1;
    private static final Object[][] MAC_ROMAN_ENCODING_TABLE = new Object[][]{{65, "A"}, {174, "AE"}, {231, "Aacute"}, {229, "Acircumflex"}, {128, "Adieresis"}, {203, "Agrave"}, {129, "Aring"}, {204, "Atilde"}, {66, "B"}, {67, "C"}, {130, "Ccedilla"}, {68, "D"}, {69, "E"}, {131, "Eacute"}, {230, "Ecircumflex"}, {232, "Edieresis"}, {233, "Egrave"}, {70, "F"}, {71, "G"}, {72, "H"}, {73, "I"}, {234, "Iacute"}, {235, "Icircumflex"}, {236, "Idieresis"}, {237, "Igrave"}, {74, "J"}, {75, "K"}, {76, "L"}, {77, "M"}, {78, "N"}, {132, "Ntilde"}, {79, "O"}, {206, "OE"}, {238, "Oacute"}, {239, "Ocircumflex"}, {133, "Odieresis"}, {241, "Ograve"}, {175, "Oslash"}, {205, "Otilde"}, {80, "P"}, {81, "Q"}, {82, "R"}, {83, "S"}, {84, "T"}, {85, "U"}, {242, "Uacute"}, {243, "Ucircumflex"}, {134, "Udieresis"}, {244, "Ugrave"}, {86, "V"}, {87, "W"}, {88, "X"}, {89, "Y"}, {217, "Ydieresis"}, {90, "Z"}, {97, "a"}, {135, "aacute"}, {137, "acircumflex"}, {171, "acute"}, {138, "adieresis"}, {190, "ae"}, {136, "agrave"}, {38, "ampersand"}, {140, "aring"}, {94, "asciicircum"}, {126, "asciitilde"}, {42, "asterisk"}, {64, "at"}, {139, "atilde"}, {98, "b"}, {92, "backslash"}, {124, "bar"}, {123, "braceleft"}, {125, "braceright"}, {91, "bracketleft"}, {93, "bracketright"}, {249, "breve"}, {165, "bullet"}, {99, "c"}, {255, "caron"}, {141, "ccedilla"}, {252, "cedilla"}, {162, "cent"}, {246, "circumflex"}, {58, "colon"}, {44, "comma"}, {169, "copyright"}, {219, "currency"}, {100, "d"}, {160, "dagger"}, {224, "daggerdbl"}, {161, "degree"}, {172, "dieresis"}, {214, "divide"}, {36, "dollar"}, {250, "dotaccent"}, {245, "dotlessi"}, {101, "e"}, {142, "eacute"}, {144, "ecircumflex"}, {145, "edieresis"}, {143, "egrave"}, {56, "eight"}, {201, "ellipsis"}, {209, "emdash"}, {208, "endash"}, {61, "equal"}, {33, "exclam"}, {193, "exclamdown"}, {102, "f"}, {222, "fi"}, {53, "five"}, {223, "fl"}, {196, "florin"}, {52, "four"}, {218, "fraction"}, {103, "g"}, {167, "germandbls"}, {96, "grave"}, {62, "greater"}, {199, "guillemotleft"}, {200, "guillemotright"}, {220, "guilsinglleft"}, {221, "guilsinglright"}, {104, "h"}, {253, "hungarumlaut"}, {45, "hyphen"}, {105, "i"}, {146, "iacute"}, {148, "icircumflex"}, {149, "idieresis"}, {147, "igrave"}, {106, "j"}, {107, "k"}, {108, "l"}, {60, "less"}, {194, "logicalnot"}, {109, "m"}, {248, "macron"}, {181, "mu"}, {110, "n"}, {57, "nine"}, {150, "ntilde"}, {35, "numbersign"}, {111, "o"}, {151, "oacute"}, {153, "ocircumflex"}, {154, "odieresis"}, {207, "oe"}, {254, "ogonek"}, {152, "ograve"}, {49, "one"}, {187, "ordfeminine"}, {188, "ordmasculine"}, {191, "oslash"}, {155, "otilde"}, {112, "p"}, {166, "paragraph"}, {40, "parenleft"}, {41, "parenright"}, {37, "percent"}, {46, "period"}, {225, "periodcentered"}, {228, "perthousand"}, {43, "plus"}, {177, "plusminus"}, {113, "q"}, {63, "question"}, {192, "questiondown"}, {34, "quotedbl"}, {227, "quotedblbase"}, {210, "quotedblleft"}, {211, "quotedblright"}, {212, "quoteleft"}, {213, "quoteright"}, {226, "quotesinglbase"}, {39, "quotesingle"}, {114, "r"}, {168, "registered"}, {251, "ring"}, {115, "s"}, {164, "section"}, {59, "semicolon"}, {55, "seven"}, {54, "six"}, {47, "slash"}, {32, "space"}, {163, "sterling"}, {116, "t"}, {51, "three"}, {247, "tilde"}, {170, "trademark"}, {50, "two"}, {117, "u"}, {156, "uacute"}, {158, "ucircumflex"}, {159, "udieresis"}, {157, "ugrave"}, {95, "underscore"}, {118, "v"}, {119, "w"}, {120, "x"}, {121, "y"}, {216, "ydieresis"}, {180, "yen"}, {122, "z"}, {48, "zero"}};
    public static final MacRomanEncoding INSTANCE = new MacRomanEncoding();

    public MacRomanEncoding() {
        for (Object[] encodingEntry : MAC_ROMAN_ENCODING_TABLE) {
            this.addCharacterEncoding((Integer)encodingEntry[0], encodingEntry[1].toString());
        }
    }
}

