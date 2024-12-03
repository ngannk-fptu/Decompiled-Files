/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.util;

public final class XmlChars {
    static final int SIZE = 394;
    static final int[] sXml10StartChars = new int[394];
    static final int[] sXml10Chars;

    private XmlChars() {
    }

    public static final boolean is10NameStartChar(char c) {
        if (c > '\u312c') {
            if (c < '\uac00') {
                return c >= '\u4e00' && c <= '\u9fa5';
            }
            if (c <= '\ud7a3') {
                return true;
            }
            return c <= '\udbff' && c >= '\ud800';
        }
        char ix = c;
        return (sXml10StartChars[ix >> 5] & 1 << (ix & 0x1F)) != 0;
    }

    public static final boolean is10NameChar(char c) {
        if (c > '\u312c') {
            if (c < '\uac00') {
                return c >= '\u4e00' && c <= '\u9fa5';
            }
            if (c <= '\ud7a3') {
                return true;
            }
            return c >= '\ud800' && c <= '\udfff';
        }
        char ix = c;
        return (sXml10Chars[ix >> 5] & 1 << (ix & 0x1F)) != 0;
    }

    public static final boolean is11NameStartChar(char c) {
        if (c <= '\u2fef') {
            if (c < '\u0300') {
                if (c < '\u00c0') {
                    return false;
                }
                return c != '\u00d7' && c != '\u00f7';
            }
            if (c >= '\u2c00') {
                return true;
            }
            if (c < '\u0370' || c > '\u218f') {
                return false;
            }
            if (c < '\u2000') {
                return c != '\u037e';
            }
            if (c >= '\u2070') {
                return c <= '\u218f';
            }
            return c == '\u200c' || c == '\u200d';
        }
        if (c >= '\u3001') {
            if (c <= '\udbff') {
                return true;
            }
            if (c >= '\uf900' && c <= '\ufffd') {
                return c <= '\ufdcf' || c >= '\ufdf0';
            }
        }
        return false;
    }

    public static final boolean is11NameChar(char c) {
        if (c <= '\u2fef') {
            if (c < '\u2000') {
                return c >= '\u00c0' && c != '\u037e' || c == '\u00b7';
            }
            if (c >= '\u2c00') {
                return true;
            }
            if (c < '\u200c' || c > '\u218f') {
                return false;
            }
            if (c >= '\u2070') {
                return true;
            }
            return c == '\u200c' || c == '\u200d' || c == '\u203f' || c == '\u2040';
        }
        if (c >= '\u3001') {
            if (c <= '\udfff') {
                return true;
            }
            if (c >= '\uf900' && c <= '\ufffd') {
                return c <= '\ufdcf' || c >= '\ufdf0';
            }
        }
        return false;
    }

    private static void SETBITS(int[] array, int start, int end) {
        int bit1 = start & 0x1F;
        int bit2 = end & 0x1F;
        if ((start >>= 5) == (end >>= 5)) {
            while (bit1 <= bit2) {
                int n = start;
                array[n] = array[n] | 1 << bit1;
                ++bit1;
            }
        } else {
            int bit;
            for (bit = bit1; bit <= 31; ++bit) {
                int n = start;
                array[n] = array[n] | 1 << bit;
            }
            while (++start < end) {
                array[start] = -1;
            }
            for (bit = 0; bit <= bit2; ++bit) {
                int n = end;
                array[n] = array[n] | 1 << bit;
            }
        }
    }

    private static void SETBITS(int[] array, int point) {
        int ix = point >> 5;
        int bit = point & 0x1F;
        int n = ix;
        array[n] = array[n] | 1 << bit;
    }

    static {
        XmlChars.SETBITS(sXml10StartChars, 192, 214);
        XmlChars.SETBITS(sXml10StartChars, 216, 246);
        XmlChars.SETBITS(sXml10StartChars, 248, 255);
        XmlChars.SETBITS(sXml10StartChars, 256, 305);
        XmlChars.SETBITS(sXml10StartChars, 308, 318);
        XmlChars.SETBITS(sXml10StartChars, 321, 328);
        XmlChars.SETBITS(sXml10StartChars, 330, 382);
        XmlChars.SETBITS(sXml10StartChars, 384, 451);
        XmlChars.SETBITS(sXml10StartChars, 461, 496);
        XmlChars.SETBITS(sXml10StartChars, 500, 501);
        XmlChars.SETBITS(sXml10StartChars, 506, 535);
        XmlChars.SETBITS(sXml10StartChars, 592, 680);
        XmlChars.SETBITS(sXml10StartChars, 699, 705);
        XmlChars.SETBITS(sXml10StartChars, 902);
        XmlChars.SETBITS(sXml10StartChars, 904, 906);
        XmlChars.SETBITS(sXml10StartChars, 908);
        XmlChars.SETBITS(sXml10StartChars, 910, 929);
        XmlChars.SETBITS(sXml10StartChars, 931, 974);
        XmlChars.SETBITS(sXml10StartChars, 976, 982);
        XmlChars.SETBITS(sXml10StartChars, 986);
        XmlChars.SETBITS(sXml10StartChars, 988);
        XmlChars.SETBITS(sXml10StartChars, 990);
        XmlChars.SETBITS(sXml10StartChars, 992);
        XmlChars.SETBITS(sXml10StartChars, 994, 1011);
        XmlChars.SETBITS(sXml10StartChars, 1025, 1036);
        XmlChars.SETBITS(sXml10StartChars, 1038, 1103);
        XmlChars.SETBITS(sXml10StartChars, 1105, 1116);
        XmlChars.SETBITS(sXml10StartChars, 1118, 1153);
        XmlChars.SETBITS(sXml10StartChars, 1168, 1220);
        XmlChars.SETBITS(sXml10StartChars, 1223, 1224);
        XmlChars.SETBITS(sXml10StartChars, 1227, 1228);
        XmlChars.SETBITS(sXml10StartChars, 1232, 1259);
        XmlChars.SETBITS(sXml10StartChars, 1262, 1269);
        XmlChars.SETBITS(sXml10StartChars, 1272, 1273);
        XmlChars.SETBITS(sXml10StartChars, 1329, 1366);
        XmlChars.SETBITS(sXml10StartChars, 1369);
        XmlChars.SETBITS(sXml10StartChars, 1377, 1414);
        XmlChars.SETBITS(sXml10StartChars, 1488, 1514);
        XmlChars.SETBITS(sXml10StartChars, 1520, 1522);
        XmlChars.SETBITS(sXml10StartChars, 1569, 1594);
        XmlChars.SETBITS(sXml10StartChars, 1601, 1610);
        XmlChars.SETBITS(sXml10StartChars, 1649, 1719);
        XmlChars.SETBITS(sXml10StartChars, 1722, 1726);
        XmlChars.SETBITS(sXml10StartChars, 1728, 1742);
        XmlChars.SETBITS(sXml10StartChars, 1744, 1747);
        XmlChars.SETBITS(sXml10StartChars, 1749);
        XmlChars.SETBITS(sXml10StartChars, 1765, 1766);
        XmlChars.SETBITS(sXml10StartChars, 2309, 2361);
        XmlChars.SETBITS(sXml10StartChars, 2365);
        XmlChars.SETBITS(sXml10StartChars, 2392, 2401);
        XmlChars.SETBITS(sXml10StartChars, 2437, 2444);
        XmlChars.SETBITS(sXml10StartChars, 2447, 2448);
        XmlChars.SETBITS(sXml10StartChars, 2451, 2472);
        XmlChars.SETBITS(sXml10StartChars, 2474, 2480);
        XmlChars.SETBITS(sXml10StartChars, 2482);
        XmlChars.SETBITS(sXml10StartChars, 2486, 2489);
        XmlChars.SETBITS(sXml10StartChars, 2524);
        XmlChars.SETBITS(sXml10StartChars, 2525);
        XmlChars.SETBITS(sXml10StartChars, 2527, 2529);
        XmlChars.SETBITS(sXml10StartChars, 2544);
        XmlChars.SETBITS(sXml10StartChars, 2545);
        XmlChars.SETBITS(sXml10StartChars, 2565, 2570);
        XmlChars.SETBITS(sXml10StartChars, 2575);
        XmlChars.SETBITS(sXml10StartChars, 2576);
        XmlChars.SETBITS(sXml10StartChars, 2579, 2600);
        XmlChars.SETBITS(sXml10StartChars, 2602, 2608);
        XmlChars.SETBITS(sXml10StartChars, 2610);
        XmlChars.SETBITS(sXml10StartChars, 2611);
        XmlChars.SETBITS(sXml10StartChars, 2613);
        XmlChars.SETBITS(sXml10StartChars, 2614);
        XmlChars.SETBITS(sXml10StartChars, 2616);
        XmlChars.SETBITS(sXml10StartChars, 2617);
        XmlChars.SETBITS(sXml10StartChars, 2649, 2652);
        XmlChars.SETBITS(sXml10StartChars, 2654);
        XmlChars.SETBITS(sXml10StartChars, 2674, 2676);
        XmlChars.SETBITS(sXml10StartChars, 2693, 2699);
        XmlChars.SETBITS(sXml10StartChars, 2701);
        XmlChars.SETBITS(sXml10StartChars, 2703, 2705);
        XmlChars.SETBITS(sXml10StartChars, 2707, 2728);
        XmlChars.SETBITS(sXml10StartChars, 2730, 2736);
        XmlChars.SETBITS(sXml10StartChars, 2738, 2739);
        XmlChars.SETBITS(sXml10StartChars, 2741, 2745);
        XmlChars.SETBITS(sXml10StartChars, 2749);
        XmlChars.SETBITS(sXml10StartChars, 2784);
        XmlChars.SETBITS(sXml10StartChars, 2821, 2828);
        XmlChars.SETBITS(sXml10StartChars, 2831);
        XmlChars.SETBITS(sXml10StartChars, 2832);
        XmlChars.SETBITS(sXml10StartChars, 2835, 2856);
        XmlChars.SETBITS(sXml10StartChars, 2858, 2864);
        XmlChars.SETBITS(sXml10StartChars, 2866);
        XmlChars.SETBITS(sXml10StartChars, 2867);
        XmlChars.SETBITS(sXml10StartChars, 2870, 2873);
        XmlChars.SETBITS(sXml10StartChars, 2877);
        XmlChars.SETBITS(sXml10StartChars, 2908);
        XmlChars.SETBITS(sXml10StartChars, 2909);
        XmlChars.SETBITS(sXml10StartChars, 2911, 2913);
        XmlChars.SETBITS(sXml10StartChars, 2949, 2954);
        XmlChars.SETBITS(sXml10StartChars, 2958, 2960);
        XmlChars.SETBITS(sXml10StartChars, 2962, 2965);
        XmlChars.SETBITS(sXml10StartChars, 2969, 2970);
        XmlChars.SETBITS(sXml10StartChars, 2972);
        XmlChars.SETBITS(sXml10StartChars, 2974);
        XmlChars.SETBITS(sXml10StartChars, 2975);
        XmlChars.SETBITS(sXml10StartChars, 2979);
        XmlChars.SETBITS(sXml10StartChars, 2980);
        XmlChars.SETBITS(sXml10StartChars, 2984, 2986);
        XmlChars.SETBITS(sXml10StartChars, 2990, 2997);
        XmlChars.SETBITS(sXml10StartChars, 2999, 3001);
        XmlChars.SETBITS(sXml10StartChars, 3077, 3084);
        XmlChars.SETBITS(sXml10StartChars, 3086, 3088);
        XmlChars.SETBITS(sXml10StartChars, 3090, 3112);
        XmlChars.SETBITS(sXml10StartChars, 3114, 3123);
        XmlChars.SETBITS(sXml10StartChars, 3125, 3129);
        XmlChars.SETBITS(sXml10StartChars, 3168);
        XmlChars.SETBITS(sXml10StartChars, 3169);
        XmlChars.SETBITS(sXml10StartChars, 3205, 3212);
        XmlChars.SETBITS(sXml10StartChars, 3214, 3216);
        XmlChars.SETBITS(sXml10StartChars, 3218, 3240);
        XmlChars.SETBITS(sXml10StartChars, 3242, 3251);
        XmlChars.SETBITS(sXml10StartChars, 3253, 3257);
        XmlChars.SETBITS(sXml10StartChars, 3294);
        XmlChars.SETBITS(sXml10StartChars, 3296);
        XmlChars.SETBITS(sXml10StartChars, 3297);
        XmlChars.SETBITS(sXml10StartChars, 3333, 3340);
        XmlChars.SETBITS(sXml10StartChars, 3342, 3344);
        XmlChars.SETBITS(sXml10StartChars, 3346, 3368);
        XmlChars.SETBITS(sXml10StartChars, 3370, 3385);
        XmlChars.SETBITS(sXml10StartChars, 3424);
        XmlChars.SETBITS(sXml10StartChars, 3425);
        XmlChars.SETBITS(sXml10StartChars, 3585, 3630);
        XmlChars.SETBITS(sXml10StartChars, 3632);
        XmlChars.SETBITS(sXml10StartChars, 3634);
        XmlChars.SETBITS(sXml10StartChars, 3635);
        XmlChars.SETBITS(sXml10StartChars, 3648, 3653);
        XmlChars.SETBITS(sXml10StartChars, 3713);
        XmlChars.SETBITS(sXml10StartChars, 3714);
        XmlChars.SETBITS(sXml10StartChars, 3716);
        XmlChars.SETBITS(sXml10StartChars, 3719);
        XmlChars.SETBITS(sXml10StartChars, 3720);
        XmlChars.SETBITS(sXml10StartChars, 3722);
        XmlChars.SETBITS(sXml10StartChars, 3725);
        XmlChars.SETBITS(sXml10StartChars, 3732, 3735);
        XmlChars.SETBITS(sXml10StartChars, 3737, 3743);
        XmlChars.SETBITS(sXml10StartChars, 3745, 3747);
        XmlChars.SETBITS(sXml10StartChars, 3749);
        XmlChars.SETBITS(sXml10StartChars, 3751);
        XmlChars.SETBITS(sXml10StartChars, 3754);
        XmlChars.SETBITS(sXml10StartChars, 3755);
        XmlChars.SETBITS(sXml10StartChars, 3757);
        XmlChars.SETBITS(sXml10StartChars, 3758);
        XmlChars.SETBITS(sXml10StartChars, 3760);
        XmlChars.SETBITS(sXml10StartChars, 3762);
        XmlChars.SETBITS(sXml10StartChars, 3763);
        XmlChars.SETBITS(sXml10StartChars, 3773);
        XmlChars.SETBITS(sXml10StartChars, 3776, 3780);
        XmlChars.SETBITS(sXml10StartChars, 3904, 3911);
        XmlChars.SETBITS(sXml10StartChars, 3913, 3945);
        XmlChars.SETBITS(sXml10StartChars, 4256, 4293);
        XmlChars.SETBITS(sXml10StartChars, 4304, 4342);
        XmlChars.SETBITS(sXml10StartChars, 4352);
        XmlChars.SETBITS(sXml10StartChars, 4354, 4355);
        XmlChars.SETBITS(sXml10StartChars, 4357, 4359);
        XmlChars.SETBITS(sXml10StartChars, 4361);
        XmlChars.SETBITS(sXml10StartChars, 4363, 4364);
        XmlChars.SETBITS(sXml10StartChars, 4366, 4370);
        XmlChars.SETBITS(sXml10StartChars, 4412);
        XmlChars.SETBITS(sXml10StartChars, 4414);
        XmlChars.SETBITS(sXml10StartChars, 4416);
        XmlChars.SETBITS(sXml10StartChars, 4428);
        XmlChars.SETBITS(sXml10StartChars, 4430);
        XmlChars.SETBITS(sXml10StartChars, 4432);
        XmlChars.SETBITS(sXml10StartChars, 4436, 4437);
        XmlChars.SETBITS(sXml10StartChars, 4441);
        XmlChars.SETBITS(sXml10StartChars, 4447, 4449);
        XmlChars.SETBITS(sXml10StartChars, 4451);
        XmlChars.SETBITS(sXml10StartChars, 4453);
        XmlChars.SETBITS(sXml10StartChars, 4455);
        XmlChars.SETBITS(sXml10StartChars, 4457);
        XmlChars.SETBITS(sXml10StartChars, 4461, 4462);
        XmlChars.SETBITS(sXml10StartChars, 4466, 4467);
        XmlChars.SETBITS(sXml10StartChars, 4469);
        XmlChars.SETBITS(sXml10StartChars, 4510);
        XmlChars.SETBITS(sXml10StartChars, 4520);
        XmlChars.SETBITS(sXml10StartChars, 4523);
        XmlChars.SETBITS(sXml10StartChars, 4526, 4527);
        XmlChars.SETBITS(sXml10StartChars, 4535, 4536);
        XmlChars.SETBITS(sXml10StartChars, 4538);
        XmlChars.SETBITS(sXml10StartChars, 4540, 4546);
        XmlChars.SETBITS(sXml10StartChars, 4587);
        XmlChars.SETBITS(sXml10StartChars, 4592);
        XmlChars.SETBITS(sXml10StartChars, 4601);
        XmlChars.SETBITS(sXml10StartChars, 7680, 7835);
        XmlChars.SETBITS(sXml10StartChars, 7840, 7929);
        XmlChars.SETBITS(sXml10StartChars, 7936, 7957);
        XmlChars.SETBITS(sXml10StartChars, 7960, 7965);
        XmlChars.SETBITS(sXml10StartChars, 7968, 8005);
        XmlChars.SETBITS(sXml10StartChars, 8008, 8013);
        XmlChars.SETBITS(sXml10StartChars, 8016, 8023);
        XmlChars.SETBITS(sXml10StartChars, 8025);
        XmlChars.SETBITS(sXml10StartChars, 8027);
        XmlChars.SETBITS(sXml10StartChars, 8029);
        XmlChars.SETBITS(sXml10StartChars, 8031, 8061);
        XmlChars.SETBITS(sXml10StartChars, 8064, 8116);
        XmlChars.SETBITS(sXml10StartChars, 8118, 8124);
        XmlChars.SETBITS(sXml10StartChars, 8126);
        XmlChars.SETBITS(sXml10StartChars, 8130, 8132);
        XmlChars.SETBITS(sXml10StartChars, 8134, 8140);
        XmlChars.SETBITS(sXml10StartChars, 8144, 8147);
        XmlChars.SETBITS(sXml10StartChars, 8150, 8155);
        XmlChars.SETBITS(sXml10StartChars, 8160, 8172);
        XmlChars.SETBITS(sXml10StartChars, 8178, 8180);
        XmlChars.SETBITS(sXml10StartChars, 8182, 8188);
        XmlChars.SETBITS(sXml10StartChars, 8486);
        XmlChars.SETBITS(sXml10StartChars, 8490, 8491);
        XmlChars.SETBITS(sXml10StartChars, 8494);
        XmlChars.SETBITS(sXml10StartChars, 8576, 8578);
        XmlChars.SETBITS(sXml10StartChars, 12353, 12436);
        XmlChars.SETBITS(sXml10StartChars, 12449, 12538);
        XmlChars.SETBITS(sXml10StartChars, 12549, 12588);
        XmlChars.SETBITS(sXml10StartChars, 12295);
        XmlChars.SETBITS(sXml10StartChars, 12321, 12329);
        sXml10Chars = new int[394];
        System.arraycopy(sXml10StartChars, 0, sXml10Chars, 0, 394);
        XmlChars.SETBITS(sXml10Chars, 768, 837);
        XmlChars.SETBITS(sXml10Chars, 864, 865);
        XmlChars.SETBITS(sXml10Chars, 1155, 1158);
        XmlChars.SETBITS(sXml10Chars, 1425, 1441);
        XmlChars.SETBITS(sXml10Chars, 1443, 1465);
        XmlChars.SETBITS(sXml10Chars, 1467, 1469);
        XmlChars.SETBITS(sXml10Chars, 1471);
        XmlChars.SETBITS(sXml10Chars, 1473, 1474);
        XmlChars.SETBITS(sXml10Chars, 1476);
        XmlChars.SETBITS(sXml10Chars, 1611, 1618);
        XmlChars.SETBITS(sXml10Chars, 1648);
        XmlChars.SETBITS(sXml10Chars, 1750, 1756);
        XmlChars.SETBITS(sXml10Chars, 1757, 1759);
        XmlChars.SETBITS(sXml10Chars, 1760, 1764);
        XmlChars.SETBITS(sXml10Chars, 1767, 1768);
        XmlChars.SETBITS(sXml10Chars, 1770, 1773);
        XmlChars.SETBITS(sXml10Chars, 2305, 2307);
        XmlChars.SETBITS(sXml10Chars, 2364);
        XmlChars.SETBITS(sXml10Chars, 2366, 2380);
        XmlChars.SETBITS(sXml10Chars, 2381);
        XmlChars.SETBITS(sXml10Chars, 2385, 2388);
        XmlChars.SETBITS(sXml10Chars, 2402);
        XmlChars.SETBITS(sXml10Chars, 2403);
        XmlChars.SETBITS(sXml10Chars, 2433, 2435);
        XmlChars.SETBITS(sXml10Chars, 2492);
        XmlChars.SETBITS(sXml10Chars, 2494);
        XmlChars.SETBITS(sXml10Chars, 2495);
        XmlChars.SETBITS(sXml10Chars, 2496, 2500);
        XmlChars.SETBITS(sXml10Chars, 2503);
        XmlChars.SETBITS(sXml10Chars, 2504);
        XmlChars.SETBITS(sXml10Chars, 2507, 2509);
        XmlChars.SETBITS(sXml10Chars, 2519);
        XmlChars.SETBITS(sXml10Chars, 2530);
        XmlChars.SETBITS(sXml10Chars, 2531);
        XmlChars.SETBITS(sXml10Chars, 2562);
        XmlChars.SETBITS(sXml10Chars, 2620);
        XmlChars.SETBITS(sXml10Chars, 2622);
        XmlChars.SETBITS(sXml10Chars, 2623);
        XmlChars.SETBITS(sXml10Chars, 2624, 2626);
        XmlChars.SETBITS(sXml10Chars, 2631);
        XmlChars.SETBITS(sXml10Chars, 2632);
        XmlChars.SETBITS(sXml10Chars, 2635, 2637);
        XmlChars.SETBITS(sXml10Chars, 2672);
        XmlChars.SETBITS(sXml10Chars, 2673);
        XmlChars.SETBITS(sXml10Chars, 2689, 2691);
        XmlChars.SETBITS(sXml10Chars, 2748);
        XmlChars.SETBITS(sXml10Chars, 2750, 2757);
        XmlChars.SETBITS(sXml10Chars, 2759, 2761);
        XmlChars.SETBITS(sXml10Chars, 2763, 2765);
        XmlChars.SETBITS(sXml10Chars, 2817, 2819);
        XmlChars.SETBITS(sXml10Chars, 2876);
        XmlChars.SETBITS(sXml10Chars, 2878, 2883);
        XmlChars.SETBITS(sXml10Chars, 2887);
        XmlChars.SETBITS(sXml10Chars, 2888);
        XmlChars.SETBITS(sXml10Chars, 2891, 2893);
        XmlChars.SETBITS(sXml10Chars, 2902);
        XmlChars.SETBITS(sXml10Chars, 2903);
        XmlChars.SETBITS(sXml10Chars, 2946);
        XmlChars.SETBITS(sXml10Chars, 2947);
        XmlChars.SETBITS(sXml10Chars, 3006, 3010);
        XmlChars.SETBITS(sXml10Chars, 3014, 3016);
        XmlChars.SETBITS(sXml10Chars, 3018, 3021);
        XmlChars.SETBITS(sXml10Chars, 3031);
        XmlChars.SETBITS(sXml10Chars, 3073, 3075);
        XmlChars.SETBITS(sXml10Chars, 3134, 3140);
        XmlChars.SETBITS(sXml10Chars, 3142, 3144);
        XmlChars.SETBITS(sXml10Chars, 3146, 3149);
        XmlChars.SETBITS(sXml10Chars, 3157, 3158);
        XmlChars.SETBITS(sXml10Chars, 3202, 3203);
        XmlChars.SETBITS(sXml10Chars, 3262, 3268);
        XmlChars.SETBITS(sXml10Chars, 3270, 3272);
        XmlChars.SETBITS(sXml10Chars, 3274, 3277);
        XmlChars.SETBITS(sXml10Chars, 3285, 3286);
        XmlChars.SETBITS(sXml10Chars, 3330, 3331);
        XmlChars.SETBITS(sXml10Chars, 3390, 3395);
        XmlChars.SETBITS(sXml10Chars, 3398, 3400);
        XmlChars.SETBITS(sXml10Chars, 3402, 3405);
        XmlChars.SETBITS(sXml10Chars, 3415);
        XmlChars.SETBITS(sXml10Chars, 3633);
        XmlChars.SETBITS(sXml10Chars, 3636, 3642);
        XmlChars.SETBITS(sXml10Chars, 3655, 3662);
        XmlChars.SETBITS(sXml10Chars, 3761);
        XmlChars.SETBITS(sXml10Chars, 3764, 3769);
        XmlChars.SETBITS(sXml10Chars, 3771, 3772);
        XmlChars.SETBITS(sXml10Chars, 3784, 3789);
        XmlChars.SETBITS(sXml10Chars, 3864, 3865);
        XmlChars.SETBITS(sXml10Chars, 3893);
        XmlChars.SETBITS(sXml10Chars, 3895);
        XmlChars.SETBITS(sXml10Chars, 3897);
        XmlChars.SETBITS(sXml10Chars, 3902);
        XmlChars.SETBITS(sXml10Chars, 3903);
        XmlChars.SETBITS(sXml10Chars, 3953, 3972);
        XmlChars.SETBITS(sXml10Chars, 3974, 3979);
        XmlChars.SETBITS(sXml10Chars, 3984, 3989);
        XmlChars.SETBITS(sXml10Chars, 3991);
        XmlChars.SETBITS(sXml10Chars, 3993, 4013);
        XmlChars.SETBITS(sXml10Chars, 4017, 4023);
        XmlChars.SETBITS(sXml10Chars, 4025);
        XmlChars.SETBITS(sXml10Chars, 8400, 8412);
        XmlChars.SETBITS(sXml10Chars, 8417);
        XmlChars.SETBITS(sXml10Chars, 12330, 12335);
        XmlChars.SETBITS(sXml10Chars, 12441);
        XmlChars.SETBITS(sXml10Chars, 12442);
        XmlChars.SETBITS(sXml10Chars, 1632, 1641);
        XmlChars.SETBITS(sXml10Chars, 1776, 1785);
        XmlChars.SETBITS(sXml10Chars, 2406, 2415);
        XmlChars.SETBITS(sXml10Chars, 2534, 2543);
        XmlChars.SETBITS(sXml10Chars, 2662, 2671);
        XmlChars.SETBITS(sXml10Chars, 2790, 2799);
        XmlChars.SETBITS(sXml10Chars, 2918, 2927);
        XmlChars.SETBITS(sXml10Chars, 3047, 3055);
        XmlChars.SETBITS(sXml10Chars, 3174, 3183);
        XmlChars.SETBITS(sXml10Chars, 3302, 3311);
        XmlChars.SETBITS(sXml10Chars, 3430, 3439);
        XmlChars.SETBITS(sXml10Chars, 3664, 3673);
        XmlChars.SETBITS(sXml10Chars, 3792, 3801);
        XmlChars.SETBITS(sXml10Chars, 3872, 3881);
        XmlChars.SETBITS(sXml10Chars, 183);
        XmlChars.SETBITS(sXml10Chars, 720);
        XmlChars.SETBITS(sXml10Chars, 721);
        XmlChars.SETBITS(sXml10Chars, 903);
        XmlChars.SETBITS(sXml10Chars, 1600);
        XmlChars.SETBITS(sXml10Chars, 3654);
        XmlChars.SETBITS(sXml10Chars, 3782);
        XmlChars.SETBITS(sXml10Chars, 12293);
        XmlChars.SETBITS(sXml10Chars, 12337, 12341);
        XmlChars.SETBITS(sXml10Chars, 12445, 12446);
        XmlChars.SETBITS(sXml10Chars, 12540, 12542);
    }
}

