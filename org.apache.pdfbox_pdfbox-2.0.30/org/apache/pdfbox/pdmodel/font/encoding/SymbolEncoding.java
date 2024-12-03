/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.font.encoding;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.font.encoding.Encoding;

public class SymbolEncoding
extends Encoding {
    private static final Object[][] SYMBOL_ENCODING_TABLE = new Object[][]{{65, "Alpha"}, {66, "Beta"}, {67, "Chi"}, {68, "Delta"}, {69, "Epsilon"}, {72, "Eta"}, {160, "Euro"}, {71, "Gamma"}, {193, "Ifraktur"}, {73, "Iota"}, {75, "Kappa"}, {76, "Lambda"}, {77, "Mu"}, {78, "Nu"}, {87, "Omega"}, {79, "Omicron"}, {70, "Phi"}, {80, "Pi"}, {89, "Psi"}, {194, "Rfraktur"}, {82, "Rho"}, {83, "Sigma"}, {84, "Tau"}, {81, "Theta"}, {85, "Upsilon"}, {161, "Upsilon1"}, {88, "Xi"}, {90, "Zeta"}, {192, "aleph"}, {97, "alpha"}, {38, "ampersand"}, {208, "angle"}, {225, "angleleft"}, {241, "angleright"}, {187, "approxequal"}, {171, "arrowboth"}, {219, "arrowdblboth"}, {223, "arrowdbldown"}, {220, "arrowdblleft"}, {222, "arrowdblright"}, {221, "arrowdblup"}, {175, "arrowdown"}, {190, "arrowhorizex"}, {172, "arrowleft"}, {174, "arrowright"}, {173, "arrowup"}, {189, "arrowvertex"}, {42, "asteriskmath"}, {124, "bar"}, {98, "beta"}, {123, "braceleft"}, {125, "braceright"}, {236, "bracelefttp"}, {237, "braceleftmid"}, {238, "braceleftbt"}, {252, "bracerighttp"}, {253, "bracerightmid"}, {254, "bracerightbt"}, {239, "braceex"}, {91, "bracketleft"}, {93, "bracketright"}, {233, "bracketlefttp"}, {234, "bracketleftex"}, {235, "bracketleftbt"}, {249, "bracketrighttp"}, {250, "bracketrightex"}, {251, "bracketrightbt"}, {183, "bullet"}, {191, "carriagereturn"}, {99, "chi"}, {196, "circlemultiply"}, {197, "circleplus"}, {167, "club"}, {58, "colon"}, {44, "comma"}, {64, "congruent"}, {227, "copyrightsans"}, {211, "copyrightserif"}, {176, "degree"}, {100, "delta"}, {168, "diamond"}, {184, "divide"}, {215, "dotmath"}, {56, "eight"}, {206, "element"}, {188, "ellipsis"}, {198, "emptyset"}, {101, "epsilon"}, {61, "equal"}, {186, "equivalence"}, {104, "eta"}, {33, "exclam"}, {36, "existential"}, {53, "five"}, {166, "florin"}, {52, "four"}, {164, "fraction"}, {103, "gamma"}, {209, "gradient"}, {62, "greater"}, {179, "greaterequal"}, {169, "heart"}, {165, "infinity"}, {242, "integral"}, {243, "integraltp"}, {244, "integralex"}, {245, "integralbt"}, {199, "intersection"}, {105, "iota"}, {107, "kappa"}, {108, "lambda"}, {60, "less"}, {163, "lessequal"}, {217, "logicaland"}, {216, "logicalnot"}, {218, "logicalor"}, {224, "lozenge"}, {45, "minus"}, {162, "minute"}, {109, "mu"}, {180, "multiply"}, {57, "nine"}, {207, "notelement"}, {185, "notequal"}, {203, "notsubset"}, {110, "nu"}, {35, "numbersign"}, {119, "omega"}, {118, "omega1"}, {111, "omicron"}, {49, "one"}, {40, "parenleft"}, {41, "parenright"}, {230, "parenlefttp"}, {231, "parenleftex"}, {232, "parenleftbt"}, {246, "parenrighttp"}, {247, "parenrightex"}, {248, "parenrightbt"}, {182, "partialdiff"}, {37, "percent"}, {46, "period"}, {94, "perpendicular"}, {102, "phi"}, {106, "phi1"}, {112, "pi"}, {43, "plus"}, {177, "plusminus"}, {213, "product"}, {204, "propersubset"}, {201, "propersuperset"}, {181, "proportional"}, {121, "psi"}, {63, "question"}, {214, "radical"}, {96, "radicalex"}, {205, "reflexsubset"}, {202, "reflexsuperset"}, {226, "registersans"}, {210, "registerserif"}, {114, "rho"}, {178, "second"}, {59, "semicolon"}, {55, "seven"}, {115, "sigma"}, {86, "sigma1"}, {126, "similar"}, {54, "six"}, {47, "slash"}, {32, "space"}, {170, "spade"}, {39, "suchthat"}, {229, "summation"}, {116, "tau"}, {92, "therefore"}, {113, "theta"}, {74, "theta1"}, {51, "three"}, {228, "trademarksans"}, {212, "trademarkserif"}, {50, "two"}, {95, "underscore"}, {200, "union"}, {34, "universal"}, {117, "upsilon"}, {195, "weierstrass"}, {120, "xi"}, {48, "zero"}, {122, "zeta"}};
    public static final SymbolEncoding INSTANCE = new SymbolEncoding();

    public SymbolEncoding() {
        for (Object[] encodingEntry : SYMBOL_ENCODING_TABLE) {
            this.add((Integer)encodingEntry[0], encodingEntry[1].toString());
        }
    }

    @Override
    public COSBase getCOSObject() {
        return COSName.getPDFName("SymbolEncoding");
    }

    @Override
    public String getEncodingName() {
        return "SymbolEncoding";
    }
}

