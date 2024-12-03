/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.util.Arrays;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
class LongArray
implements Cloneable {
    private static final short[] INTERLEAVE2_TABLE = new short[]{0, 1, 4, 5, 16, 17, 20, 21, 64, 65, 68, 69, 80, 81, 84, 85, 256, 257, 260, 261, 272, 273, 276, 277, 320, 321, 324, 325, 336, 337, 340, 341, 1024, 1025, 1028, 1029, 1040, 1041, 1044, 1045, 1088, 1089, 1092, 1093, 1104, 1105, 1108, 1109, 1280, 1281, 1284, 1285, 1296, 1297, 1300, 1301, 1344, 1345, 1348, 1349, 1360, 1361, 1364, 1365, 4096, 4097, 4100, 4101, 4112, 4113, 4116, 4117, 4160, 4161, 4164, 4165, 4176, 4177, 4180, 4181, 4352, 4353, 4356, 4357, 4368, 4369, 4372, 4373, 4416, 4417, 4420, 4421, 4432, 4433, 4436, 4437, 5120, 5121, 5124, 5125, 5136, 5137, 5140, 5141, 5184, 5185, 5188, 5189, 5200, 5201, 5204, 5205, 5376, 5377, 5380, 5381, 5392, 5393, 5396, 5397, 5440, 5441, 5444, 5445, 5456, 5457, 5460, 5461, 16384, 16385, 16388, 16389, 16400, 16401, 16404, 16405, 16448, 16449, 16452, 16453, 16464, 16465, 16468, 16469, 16640, 16641, 16644, 16645, 16656, 16657, 16660, 16661, 16704, 16705, 16708, 16709, 16720, 16721, 16724, 16725, 17408, 17409, 17412, 17413, 17424, 17425, 17428, 17429, 17472, 17473, 17476, 17477, 17488, 17489, 17492, 17493, 17664, 17665, 17668, 17669, 17680, 17681, 17684, 17685, 17728, 17729, 17732, 17733, 17744, 17745, 17748, 17749, 20480, 20481, 20484, 20485, 20496, 20497, 20500, 20501, 20544, 20545, 20548, 20549, 20560, 20561, 20564, 20565, 20736, 20737, 20740, 20741, 20752, 20753, 20756, 20757, 20800, 20801, 20804, 20805, 20816, 20817, 20820, 20821, 21504, 21505, 21508, 21509, 21520, 21521, 21524, 21525, 21568, 21569, 21572, 21573, 21584, 21585, 21588, 21589, 21760, 21761, 21764, 21765, 21776, 21777, 21780, 21781, 21824, 21825, 21828, 21829, 21840, 21841, 21844, 21845};
    private static final int[] INTERLEAVE3_TABLE = new int[]{0, 1, 8, 9, 64, 65, 72, 73, 512, 513, 520, 521, 576, 577, 584, 585, 4096, 4097, 4104, 4105, 4160, 4161, 4168, 4169, 4608, 4609, 4616, 4617, 4672, 4673, 4680, 4681, 32768, 32769, 32776, 32777, 32832, 32833, 32840, 32841, 33280, 33281, 33288, 33289, 33344, 33345, 33352, 33353, 36864, 36865, 36872, 36873, 36928, 36929, 36936, 36937, 37376, 37377, 37384, 37385, 37440, 37441, 37448, 37449, 262144, 262145, 262152, 262153, 262208, 262209, 262216, 262217, 262656, 262657, 262664, 262665, 262720, 262721, 262728, 262729, 266240, 266241, 266248, 266249, 266304, 266305, 266312, 266313, 266752, 266753, 266760, 266761, 266816, 266817, 266824, 266825, 294912, 294913, 294920, 294921, 294976, 294977, 294984, 294985, 295424, 295425, 295432, 295433, 295488, 295489, 295496, 295497, 299008, 299009, 299016, 299017, 299072, 299073, 299080, 299081, 299520, 299521, 299528, 299529, 299584, 299585, 299592, 299593};
    private static final int[] INTERLEAVE4_TABLE = new int[]{0, 1, 16, 17, 256, 257, 272, 273, 4096, 4097, 4112, 4113, 4352, 4353, 4368, 4369, 65536, 65537, 65552, 65553, 65792, 65793, 65808, 65809, 69632, 69633, 69648, 69649, 69888, 69889, 69904, 69905, 0x100000, 0x100001, 0x100010, 0x100011, 0x100100, 0x100101, 0x100110, 0x100111, 0x101000, 0x101001, 0x101010, 0x101011, 0x101100, 0x101101, 0x101110, 0x101111, 0x110000, 0x110001, 0x110010, 0x110011, 0x110100, 0x110101, 0x110110, 0x110111, 0x111000, 0x111001, 0x111010, 0x111011, 0x111100, 0x111101, 0x111110, 0x111111, 0x1000000, 0x1000001, 0x1000010, 0x1000011, 0x1000100, 0x1000101, 0x1000110, 0x1000111, 0x1001000, 0x1001001, 0x1001010, 0x1001011, 0x1001100, 0x1001101, 0x1001110, 0x1001111, 0x1010000, 0x1010001, 0x1010010, 0x1010011, 0x1010100, 0x1010101, 0x1010110, 0x1010111, 0x1011000, 0x1011001, 0x1011010, 0x1011011, 0x1011100, 0x1011101, 0x1011110, 0x1011111, 0x1100000, 0x1100001, 0x1100010, 0x1100011, 0x1100100, 0x1100101, 0x1100110, 0x1100111, 0x1101000, 0x1101001, 0x1101010, 0x1101011, 0x1101100, 0x1101101, 0x1101110, 0x1101111, 0x1110000, 0x1110001, 0x1110010, 0x1110011, 0x1110100, 0x1110101, 0x1110110, 0x1110111, 0x1111000, 0x1111001, 0x1111010, 0x1111011, 0x1111100, 0x1111101, 0x1111110, 0x1111111, 0x10000000, 0x10000001, 0x10000010, 0x10000011, 0x10000100, 0x10000101, 0x10000110, 0x10000111, 0x10001000, 0x10001001, 0x10001010, 0x10001011, 0x10001100, 0x10001101, 0x10001110, 0x10001111, 0x10010000, 0x10010001, 0x10010010, 0x10010011, 0x10010100, 0x10010101, 0x10010110, 0x10010111, 0x10011000, 0x10011001, 0x10011010, 0x10011011, 0x10011100, 0x10011101, 0x10011110, 0x10011111, 0x10100000, 0x10100001, 0x10100010, 0x10100011, 0x10100100, 0x10100101, 0x10100110, 0x10100111, 0x10101000, 0x10101001, 0x10101010, 0x10101011, 0x10101100, 0x10101101, 0x10101110, 0x10101111, 0x10110000, 0x10110001, 0x10110010, 0x10110011, 0x10110100, 0x10110101, 0x10110110, 0x10110111, 0x10111000, 0x10111001, 0x10111010, 0x10111011, 0x10111100, 0x10111101, 0x10111110, 0x10111111, 0x11000000, 0x11000001, 0x11000010, 0x11000011, 0x11000100, 0x11000101, 0x11000110, 0x11000111, 0x11001000, 0x11001001, 0x11001010, 0x11001011, 0x11001100, 0x11001101, 0x11001110, 0x11001111, 0x11010000, 0x11010001, 0x11010010, 0x11010011, 0x11010100, 0x11010101, 0x11010110, 0x11010111, 0x11011000, 0x11011001, 0x11011010, 0x11011011, 0x11011100, 0x11011101, 0x11011110, 0x11011111, 0x11100000, 0x11100001, 0x11100010, 0x11100011, 0x11100100, 0x11100101, 0x11100110, 0x11100111, 0x11101000, 0x11101001, 0x11101010, 0x11101011, 0x11101100, 0x11101101, 0x11101110, 0x11101111, 0x11110000, 0x11110001, 0x11110010, 0x11110011, 0x11110100, 0x11110101, 0x11110110, 0x11110111, 0x11111000, 0x11111001, 0x11111010, 0x11111011, 0x11111100, 0x11111101, 0x11111110, 0x11111111};
    private static final int[] INTERLEAVE5_TABLE = new int[]{0, 1, 32, 33, 1024, 1025, 1056, 1057, 32768, 32769, 32800, 32801, 33792, 33793, 33824, 33825, 0x100000, 0x100001, 0x100020, 0x100021, 0x100400, 0x100401, 1049632, 1049633, 0x108000, 0x108001, 1081376, 1081377, 1082368, 1082369, 1082400, 1082401, 0x2000000, 0x2000001, 0x2000020, 0x2000021, 0x2000400, 33555457, 0x2000420, 33555489, 0x2008000, 33587201, 0x2008020, 33587233, 33588224, 33588225, 33588256, 33588257, 0x2100000, 0x2100001, 0x2100020, 0x2100021, 34604032, 34604033, 34604064, 34604065, 34635776, 34635777, 34635808, 34635809, 34636800, 34636801, 34636832, 34636833, 0x40000000, 0x40000001, 0x40000020, 1073741857, 0x40000400, 0x40000401, 0x40000420, 1073742881, 0x40008000, 1073774593, 1073774624, 1073774625, 0x40008400, 1073775617, 1073775648, 1073775649, 0x40100000, 0x40100001, 1074790432, 1074790433, 0x40100400, 0x40100401, 1074791456, 1074791457, 1074823168, 1074823169, 1074823200, 1074823201, 1074824192, 1074824193, 1074824224, 1074824225, 0x42000000, 1107296257, 0x42000020, 1107296289, 0x42000400, 1107297281, 0x42000420, 1107297313, 1107329024, 1107329025, 1107329056, 1107329057, 1107330048, 1107330049, 1107330080, 1107330081, 1108344832, 1108344833, 1108344864, 1108344865, 1108345856, 1108345857, 1108345888, 1108345889, 1108377600, 1108377601, 1108377632, 1108377633, 1108378624, 1108378625, 1108378656, 1108378657};
    private static final long[] INTERLEAVE7_TABLE = new long[]{0L, 1L, 128L, 129L, 16384L, 16385L, 16512L, 16513L, 0x200000L, 0x200001L, 0x200080L, 2097281L, 0x204000L, 2113537L, 2113664L, 2113665L, 0x10000000L, 0x10000001L, 0x10000080L, 0x10000081L, 0x10004000L, 0x10004001L, 268451968L, 268451969L, 0x10200000L, 0x10200001L, 270532736L, 270532737L, 270548992L, 270548993L, 270549120L, 270549121L, 0x800000000L, 0x800000001L, 0x800000080L, 0x800000081L, 0x800004000L, 34359754753L, 0x800004080L, 34359754881L, 0x800200000L, 34361835521L, 0x800200080L, 34361835649L, 34361851904L, 34361851905L, 34361852032L, 34361852033L, 0x810000000L, 0x810000001L, 0x810000080L, 0x810000081L, 34628190208L, 34628190209L, 34628190336L, 34628190337L, 34630270976L, 34630270977L, 34630271104L, 34630271105L, 34630287360L, 34630287361L, 34630287488L, 34630287489L, 0x40000000000L, 0x40000000001L, 0x40000000080L, 4398046511233L, 0x40000004000L, 0x40000004001L, 0x40000004080L, 4398046527617L, 0x40000200000L, 4398048608257L, 4398048608384L, 4398048608385L, 0x40000204000L, 4398048624641L, 4398048624768L, 4398048624769L, 0x40010000000L, 0x40010000001L, 4398314946688L, 4398314946689L, 0x40010004000L, 0x40010004001L, 4398314963072L, 4398314963073L, 4398317043712L, 4398317043713L, 4398317043840L, 4398317043841L, 4398317060096L, 4398317060097L, 4398317060224L, 4398317060225L, 0x40800000000L, 4432406249473L, 0x40800000080L, 4432406249601L, 0x40800004000L, 4432406265857L, 0x40800004080L, 4432406265985L, 4432408346624L, 4432408346625L, 4432408346752L, 4432408346753L, 4432408363008L, 4432408363009L, 4432408363136L, 4432408363137L, 4432674684928L, 4432674684929L, 4432674685056L, 4432674685057L, 4432674701312L, 4432674701313L, 4432674701440L, 4432674701441L, 4432676782080L, 4432676782081L, 4432676782208L, 4432676782209L, 4432676798464L, 4432676798465L, 4432676798592L, 4432676798593L, 0x2000000000000L, 0x2000000000001L, 0x2000000000080L, 562949953421441L, 0x2000000004000L, 562949953437697L, 562949953437824L, 562949953437825L, 0x2000000200000L, 0x2000000200001L, 0x2000000200080L, 562949955518593L, 0x2000000204000L, 562949955534849L, 562949955534976L, 562949955534977L, 0x2000010000000L, 0x2000010000001L, 562950221856896L, 562950221856897L, 562950221873152L, 562950221873153L, 562950221873280L, 562950221873281L, 0x2000010200000L, 0x2000010200001L, 562950223954048L, 562950223954049L, 562950223970304L, 562950223970305L, 562950223970432L, 562950223970433L, 0x2000800000000L, 562984313159681L, 0x2000800000080L, 562984313159809L, 562984313176064L, 562984313176065L, 562984313176192L, 562984313176193L, 0x2000800200000L, 562984315256833L, 0x2000800200080L, 562984315256961L, 562984315273216L, 562984315273217L, 562984315273344L, 562984315273345L, 562984581595136L, 562984581595137L, 562984581595264L, 562984581595265L, 562984581611520L, 562984581611521L, 562984581611648L, 562984581611649L, 562984583692288L, 562984583692289L, 562984583692416L, 562984583692417L, 562984583708672L, 562984583708673L, 562984583708800L, 562984583708801L, 0x2040000000000L, 567347999932417L, 567347999932544L, 567347999932545L, 0x2040000004000L, 567347999948801L, 567347999948928L, 567347999948929L, 0x2040000200000L, 567348002029569L, 567348002029696L, 567348002029697L, 0x2040000204000L, 567348002045953L, 567348002046080L, 567348002046081L, 567348268367872L, 567348268367873L, 567348268368000L, 567348268368001L, 567348268384256L, 567348268384257L, 567348268384384L, 567348268384385L, 567348270465024L, 567348270465025L, 567348270465152L, 567348270465153L, 567348270481408L, 567348270481409L, 567348270481536L, 567348270481537L, 567382359670784L, 567382359670785L, 567382359670912L, 567382359670913L, 567382359687168L, 567382359687169L, 567382359687296L, 567382359687297L, 567382361767936L, 567382361767937L, 567382361768064L, 567382361768065L, 567382361784320L, 567382361784321L, 567382361784448L, 567382361784449L, 567382628106240L, 567382628106241L, 567382628106368L, 567382628106369L, 567382628122624L, 567382628122625L, 567382628122752L, 567382628122753L, 567382630203392L, 567382630203393L, 567382630203520L, 567382630203521L, 567382630219776L, 567382630219777L, 567382630219904L, 567382630219905L, 0x100000000000000L, 0x100000000000001L, 0x100000000000080L, 0x100000000000081L, 0x100000000004000L, 0x100000000004001L, 72057594037944448L, 72057594037944449L, 0x100000000200000L, 0x100000000200001L, 72057594040025216L, 72057594040025217L, 72057594040041472L, 72057594040041473L, 72057594040041600L, 72057594040041601L, 0x100000010000000L, 0x100000010000001L, 0x100000010000080L, 0x100000010000081L, 0x100000010004000L, 0x100000010004001L, 72057594306379904L, 72057594306379905L, 0x100000010200000L, 0x100000010200001L, 72057594308460672L, 72057594308460673L, 72057594308476928L, 72057594308476929L, 72057594308477056L, 72057594308477057L, 0x100000800000000L, 0x100000800000001L, 0x100000800000080L, 0x100000800000081L, 72057628397682688L, 72057628397682689L, 72057628397682816L, 72057628397682817L, 72057628399763456L, 72057628399763457L, 72057628399763584L, 72057628399763585L, 72057628399779840L, 72057628399779841L, 72057628399779968L, 72057628399779969L, 0x100000810000000L, 0x100000810000001L, 0x100000810000080L, 0x100000810000081L, 72057628666118144L, 72057628666118145L, 72057628666118272L, 72057628666118273L, 72057628668198912L, 72057628668198913L, 72057628668199040L, 72057628668199041L, 72057628668215296L, 72057628668215297L, 72057628668215424L, 72057628668215425L, 0x100040000000000L, 0x100040000000001L, 72061992084439168L, 72061992084439169L, 0x100040000004000L, 0x100040000004001L, 72061992084455552L, 72061992084455553L, 72061992086536192L, 72061992086536193L, 72061992086536320L, 72061992086536321L, 72061992086552576L, 72061992086552577L, 72061992086552704L, 72061992086552705L, 0x100040010000000L, 0x100040010000001L, 72061992352874624L, 72061992352874625L, 0x100040010004000L, 0x100040010004001L, 72061992352891008L, 72061992352891009L, 72061992354971648L, 72061992354971649L, 72061992354971776L, 72061992354971777L, 72061992354988032L, 72061992354988033L, 72061992354988160L, 72061992354988161L, 72062026444177408L, 72062026444177409L, 72062026444177536L, 72062026444177537L, 72062026444193792L, 72062026444193793L, 72062026444193920L, 72062026444193921L, 72062026446274560L, 72062026446274561L, 72062026446274688L, 72062026446274689L, 72062026446290944L, 72062026446290945L, 72062026446291072L, 72062026446291073L, 72062026712612864L, 72062026712612865L, 72062026712612992L, 72062026712612993L, 72062026712629248L, 72062026712629249L, 72062026712629376L, 72062026712629377L, 72062026714710016L, 72062026714710017L, 72062026714710144L, 72062026714710145L, 72062026714726400L, 72062026714726401L, 72062026714726528L, 72062026714726529L, 0x102000000000000L, 0x102000000000001L, 72620543991349376L, 72620543991349377L, 72620543991365632L, 72620543991365633L, 72620543991365760L, 72620543991365761L, 0x102000000200000L, 0x102000000200001L, 72620543993446528L, 72620543993446529L, 72620543993462784L, 72620543993462785L, 72620543993462912L, 72620543993462913L, 0x102000010000000L, 0x102000010000001L, 72620544259784832L, 72620544259784833L, 72620544259801088L, 72620544259801089L, 72620544259801216L, 72620544259801217L, 0x102000010200000L, 0x102000010200001L, 72620544261881984L, 72620544261881985L, 72620544261898240L, 72620544261898241L, 72620544261898368L, 72620544261898369L, 72620578351087616L, 72620578351087617L, 72620578351087744L, 72620578351087745L, 72620578351104000L, 72620578351104001L, 72620578351104128L, 72620578351104129L, 72620578353184768L, 72620578353184769L, 72620578353184896L, 72620578353184897L, 72620578353201152L, 72620578353201153L, 72620578353201280L, 72620578353201281L, 72620578619523072L, 72620578619523073L, 72620578619523200L, 72620578619523201L, 72620578619539456L, 72620578619539457L, 72620578619539584L, 72620578619539585L, 72620578621620224L, 72620578621620225L, 72620578621620352L, 72620578621620353L, 72620578621636608L, 72620578621636609L, 72620578621636736L, 72620578621636737L, 72624942037860352L, 72624942037860353L, 72624942037860480L, 72624942037860481L, 72624942037876736L, 72624942037876737L, 72624942037876864L, 72624942037876865L, 72624942039957504L, 72624942039957505L, 72624942039957632L, 72624942039957633L, 72624942039973888L, 72624942039973889L, 72624942039974016L, 72624942039974017L, 72624942306295808L, 72624942306295809L, 72624942306295936L, 72624942306295937L, 72624942306312192L, 72624942306312193L, 72624942306312320L, 72624942306312321L, 72624942308392960L, 72624942308392961L, 72624942308393088L, 72624942308393089L, 72624942308409344L, 72624942308409345L, 72624942308409472L, 72624942308409473L, 72624976397598720L, 72624976397598721L, 72624976397598848L, 72624976397598849L, 72624976397615104L, 72624976397615105L, 72624976397615232L, 72624976397615233L, 72624976399695872L, 72624976399695873L, 72624976399696000L, 72624976399696001L, 72624976399712256L, 72624976399712257L, 72624976399712384L, 72624976399712385L, 72624976666034176L, 72624976666034177L, 72624976666034304L, 72624976666034305L, 72624976666050560L, 72624976666050561L, 72624976666050688L, 72624976666050689L, 72624976668131328L, 72624976668131329L, 72624976668131456L, 72624976668131457L, 72624976668147712L, 72624976668147713L, 72624976668147840L, 72624976668147841L};
    private static final String ZEROES = "0000000000000000000000000000000000000000000000000000000000000000";
    static final byte[] bitLengths = new byte[]{0, 1, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8};
    private long[] m_ints;

    public LongArray(int intLen) {
        this.m_ints = new long[intLen];
    }

    public LongArray(long[] ints) {
        this.m_ints = ints;
    }

    public LongArray(long[] ints, int off, int len) {
        if (off == 0 && len == ints.length) {
            this.m_ints = ints;
        } else {
            this.m_ints = new long[len];
            System.arraycopy(ints, off, this.m_ints, 0, len);
        }
    }

    public LongArray(BigInteger bigInt) {
        int barrI;
        if (bigInt == null || bigInt.signum() < 0) {
            throw new IllegalArgumentException("invalid F2m field value");
        }
        if (bigInt.signum() == 0) {
            this.m_ints = new long[]{0L};
            return;
        }
        byte[] barr = bigInt.toByteArray();
        int barrLen = barr.length;
        int barrStart = 0;
        if (barr[0] == 0) {
            --barrLen;
            barrStart = 1;
        }
        int intLen = (barrLen + 7) / 8;
        this.m_ints = new long[intLen];
        int iarrJ = intLen - 1;
        int rem = barrLen % 8 + barrStart;
        long temp = 0L;
        if (barrStart < rem) {
            for (barrI = barrStart; barrI < rem; ++barrI) {
                temp <<= 8;
                int barrBarrI = barr[barrI] & 0xFF;
                temp |= (long)barrBarrI;
            }
            this.m_ints[iarrJ--] = temp;
        }
        while (iarrJ >= 0) {
            temp = 0L;
            for (int i = 0; i < 8; ++i) {
                temp <<= 8;
                int barrBarrI = barr[barrI++] & 0xFF;
                temp |= (long)barrBarrI;
            }
            this.m_ints[iarrJ] = temp;
            --iarrJ;
        }
    }

    void copyTo(long[] z, int zOff) {
        System.arraycopy(this.m_ints, 0, z, zOff, this.m_ints.length);
    }

    public boolean isOne() {
        long[] a = this.m_ints;
        if (a[0] != 1L) {
            return false;
        }
        for (int i = 1; i < a.length; ++i) {
            if (a[i] == 0L) continue;
            return false;
        }
        return true;
    }

    public boolean isZero() {
        long[] a = this.m_ints;
        for (int i = 0; i < a.length; ++i) {
            if (a[i] == 0L) continue;
            return false;
        }
        return true;
    }

    public int getUsedLength() {
        return this.getUsedLengthFrom(this.m_ints.length);
    }

    public int getUsedLengthFrom(int from) {
        long[] a = this.m_ints;
        if ((from = Math.min(from, a.length)) < 1) {
            return 0;
        }
        if (a[0] != 0L) {
            while (a[--from] == 0L) {
            }
            return from + 1;
        }
        do {
            if (a[--from] == 0L) continue;
            return from + 1;
        } while (from > 0);
        return 0;
    }

    public int degree() {
        long w;
        int i = this.m_ints.length;
        do {
            if (i != 0) continue;
            return 0;
        } while ((w = this.m_ints[--i]) == 0L);
        return (i << 6) + LongArray.bitLength(w);
    }

    private int degreeFrom(int limit) {
        long w;
        int i = limit + 62 >>> 6;
        do {
            if (i != 0) continue;
            return 0;
        } while ((w = this.m_ints[--i]) == 0L);
        return (i << 6) + LongArray.bitLength(w);
    }

    private static int bitLength(long w) {
        int v;
        int b;
        int u = (int)(w >>> 32);
        if (u == 0) {
            u = (int)w;
            b = 0;
        } else {
            b = 32;
        }
        int t = u >>> 16;
        int k = t == 0 ? ((t = u >>> 8) == 0 ? bitLengths[u] : 8 + bitLengths[t]) : ((v = t >>> 8) == 0 ? 16 + bitLengths[t] : 24 + bitLengths[v]);
        return b + k;
    }

    private long[] resizedInts(int newLen) {
        long[] newInts = new long[newLen];
        System.arraycopy(this.m_ints, 0, newInts, 0, Math.min(this.m_ints.length, newLen));
        return newInts;
    }

    public BigInteger toBigInteger() {
        int usedLen = this.getUsedLength();
        if (usedLen == 0) {
            return ECConstants.ZERO;
        }
        long highestInt = this.m_ints[usedLen - 1];
        byte[] temp = new byte[8];
        int barrI = 0;
        boolean trailingZeroBytesDone = false;
        for (int j = 7; j >= 0; --j) {
            byte thisByte = (byte)(highestInt >>> 8 * j);
            if (!trailingZeroBytesDone && thisByte == 0) continue;
            trailingZeroBytesDone = true;
            temp[barrI++] = thisByte;
        }
        int barrLen = 8 * (usedLen - 1) + barrI;
        byte[] barr = new byte[barrLen];
        for (int j = 0; j < barrI; ++j) {
            barr[j] = temp[j];
        }
        for (int iarrJ = usedLen - 2; iarrJ >= 0; --iarrJ) {
            long mi = this.m_ints[iarrJ];
            for (int j = 7; j >= 0; --j) {
                barr[barrI++] = (byte)(mi >>> 8 * j);
            }
        }
        return new BigInteger(1, barr);
    }

    private static long shiftUp(long[] x, int xOff, int count, int shift) {
        int shiftInv = 64 - shift;
        long prev = 0L;
        for (int i = 0; i < count; ++i) {
            long next = x[xOff + i];
            x[xOff + i] = next << shift | prev;
            prev = next >>> shiftInv;
        }
        return prev;
    }

    private static long shiftUp(long[] x, int xOff, long[] z, int zOff, int count, int shift) {
        int shiftInv = 64 - shift;
        long prev = 0L;
        for (int i = 0; i < count; ++i) {
            long next = x[xOff + i];
            z[zOff + i] = next << shift | prev;
            prev = next >>> shiftInv;
        }
        return prev;
    }

    public LongArray addOne() {
        if (this.m_ints.length == 0) {
            return new LongArray(new long[]{1L});
        }
        int resultLen = Math.max(1, this.getUsedLength());
        long[] ints = this.resizedInts(resultLen);
        ints[0] = ints[0] ^ 1L;
        return new LongArray(ints);
    }

    private void addShiftedByBitsSafe(LongArray other, int otherDegree, int bits) {
        int otherLen = otherDegree + 63 >>> 6;
        int words = bits >>> 6;
        int shift = bits & 0x3F;
        if (shift == 0) {
            LongArray.add(this.m_ints, words, other.m_ints, 0, otherLen);
            return;
        }
        long carry = LongArray.addShiftedUp(this.m_ints, words, other.m_ints, 0, otherLen, shift);
        if (carry != 0L) {
            int n = otherLen + words;
            this.m_ints[n] = this.m_ints[n] ^ carry;
        }
    }

    private static long addShiftedUp(long[] x, int xOff, long[] y, int yOff, int count, int shift) {
        int shiftInv = 64 - shift;
        long prev = 0L;
        for (int i = 0; i < count; ++i) {
            long next = y[yOff + i];
            int n = xOff + i;
            x[n] = x[n] ^ (next << shift | prev);
            prev = next >>> shiftInv;
        }
        return prev;
    }

    private static long addShiftedDown(long[] x, int xOff, long[] y, int yOff, int count, int shift) {
        int shiftInv = 64 - shift;
        long prev = 0L;
        int i = count;
        while (--i >= 0) {
            long next = y[yOff + i];
            int n = xOff + i;
            x[n] = x[n] ^ (next >>> shift | prev);
            prev = next << shiftInv;
        }
        return prev;
    }

    public void addShiftedByWords(LongArray other, int words) {
        int otherUsedLen = other.getUsedLength();
        if (otherUsedLen == 0) {
            return;
        }
        int minLen = otherUsedLen + words;
        if (minLen > this.m_ints.length) {
            this.m_ints = this.resizedInts(minLen);
        }
        LongArray.add(this.m_ints, words, other.m_ints, 0, otherUsedLen);
    }

    private static void add(long[] x, int xOff, long[] y, int yOff, int count) {
        for (int i = 0; i < count; ++i) {
            int n = xOff + i;
            x[n] = x[n] ^ y[yOff + i];
        }
    }

    private static void add(long[] x, int xOff, long[] y, int yOff, long[] z, int zOff, int count) {
        for (int i = 0; i < count; ++i) {
            z[zOff + i] = x[xOff + i] ^ y[yOff + i];
        }
    }

    private static void addBoth(long[] x, int xOff, long[] y1, int y1Off, long[] y2, int y2Off, int count) {
        for (int i = 0; i < count; ++i) {
            int n = xOff + i;
            x[n] = x[n] ^ (y1[y1Off + i] ^ y2[y2Off + i]);
        }
    }

    private static void distribute(long[] x, int src, int dst1, int dst2, int count) {
        for (int i = 0; i < count; ++i) {
            long v = x[src + i];
            int n = dst1 + i;
            x[n] = x[n] ^ v;
            int n2 = dst2 + i;
            x[n2] = x[n2] ^ v;
        }
    }

    public int getLength() {
        return this.m_ints.length;
    }

    private static void flipWord(long[] buf, int off, int bit, long word) {
        int n = off + (bit >>> 6);
        int shift = bit & 0x3F;
        if (shift == 0) {
            int n2 = n;
            buf[n2] = buf[n2] ^ word;
        } else {
            int n3 = n++;
            buf[n3] = buf[n3] ^ word << shift;
            if ((word >>>= 64 - shift) != 0L) {
                int n4 = n;
                buf[n4] = buf[n4] ^ word;
            }
        }
    }

    public boolean testBitZero() {
        return this.m_ints.length > 0 && (this.m_ints[0] & 1L) != 0L;
    }

    private static boolean testBit(long[] buf, int off, int n) {
        int theInt = n >>> 6;
        int theBit = n & 0x3F;
        long tester = 1L << theBit;
        return (buf[off + theInt] & tester) != 0L;
    }

    private static void flipBit(long[] buf, int off, int n) {
        int theInt = n >>> 6;
        int theBit = n & 0x3F;
        long flipper = 1L << theBit;
        int n2 = off + theInt;
        buf[n2] = buf[n2] ^ flipper;
    }

    private static void multiplyWord(long a, long[] b, int bLen, long[] c, int cOff) {
        if ((a & 1L) != 0L) {
            LongArray.add(c, cOff, b, 0, bLen);
        }
        int k = 1;
        while ((a >>>= 1) != 0L) {
            long carry;
            if ((a & 1L) != 0L && (carry = LongArray.addShiftedUp(c, cOff, b, 0, bLen, k)) != 0L) {
                int n = cOff + bLen;
                c[n] = c[n] ^ carry;
            }
            ++k;
        }
    }

    public LongArray modMultiplyLD(LongArray other, int m, int[] ks) {
        int v;
        int u;
        int aVal;
        int j;
        int k;
        int tOff;
        int aDeg = this.degree();
        if (aDeg == 0) {
            return this;
        }
        int bDeg = other.degree();
        if (bDeg == 0) {
            return other;
        }
        LongArray A = this;
        LongArray B = other;
        if (aDeg > bDeg) {
            A = other;
            B = this;
            int tmp = aDeg;
            aDeg = bDeg;
            bDeg = tmp;
        }
        int aLen = aDeg + 63 >>> 6;
        int bLen = bDeg + 63 >>> 6;
        int cLen = aDeg + bDeg + 62 >>> 6;
        if (aLen == 1) {
            long a0 = A.m_ints[0];
            if (a0 == 1L) {
                return B;
            }
            long[] c0 = new long[cLen];
            LongArray.multiplyWord(a0, B.m_ints, bLen, c0, 0);
            return LongArray.reduceResult(c0, 0, cLen, m, ks);
        }
        int bMax = bDeg + 7 + 63 >>> 6;
        int[] ti = new int[16];
        long[] T0 = new long[bMax << 4];
        ti[1] = tOff = bMax;
        System.arraycopy(B.m_ints, 0, T0, tOff, bLen);
        for (int i = 2; i < 16; ++i) {
            ti[i] = tOff += bMax;
            if ((i & 1) == 0) {
                LongArray.shiftUp(T0, tOff >>> 1, T0, tOff, bMax, 1);
                continue;
            }
            LongArray.add(T0, bMax, T0, tOff - bMax, T0, tOff, bMax);
        }
        long[] T1 = new long[T0.length];
        LongArray.shiftUp(T0, 0, T1, 0, T0.length, 4);
        long[] a = A.m_ints;
        long[] c = new long[cLen];
        int MASK = 15;
        for (k = 56; k >= 0; k -= 8) {
            for (j = 1; j < aLen; j += 2) {
                aVal = (int)(a[j] >>> k);
                u = aVal & MASK;
                v = aVal >>> 4 & MASK;
                LongArray.addBoth(c, j - 1, T0, ti[u], T1, ti[v], bMax);
            }
            LongArray.shiftUp(c, 0, cLen, 8);
        }
        for (k = 56; k >= 0; k -= 8) {
            for (j = 0; j < aLen; j += 2) {
                aVal = (int)(a[j] >>> k);
                u = aVal & MASK;
                v = aVal >>> 4 & MASK;
                LongArray.addBoth(c, j, T0, ti[u], T1, ti[v], bMax);
            }
            if (k <= 0) continue;
            LongArray.shiftUp(c, 0, cLen, 8);
        }
        return LongArray.reduceResult(c, 0, cLen, m, ks);
    }

    public LongArray modMultiply(LongArray other, int m, int[] ks) {
        int tOff;
        int aDeg = this.degree();
        if (aDeg == 0) {
            return this;
        }
        int bDeg = other.degree();
        if (bDeg == 0) {
            return other;
        }
        LongArray A = this;
        LongArray B = other;
        if (aDeg > bDeg) {
            A = other;
            B = this;
            int tmp = aDeg;
            aDeg = bDeg;
            bDeg = tmp;
        }
        int aLen = aDeg + 63 >>> 6;
        int bLen = bDeg + 63 >>> 6;
        int cLen = aDeg + bDeg + 62 >>> 6;
        if (aLen == 1) {
            long a0 = A.m_ints[0];
            if (a0 == 1L) {
                return B;
            }
            long[] c0 = new long[cLen];
            LongArray.multiplyWord(a0, B.m_ints, bLen, c0, 0);
            return LongArray.reduceResult(c0, 0, cLen, m, ks);
        }
        int bMax = bDeg + 7 + 63 >>> 6;
        int[] ti = new int[16];
        long[] T0 = new long[bMax << 4];
        ti[1] = tOff = bMax;
        System.arraycopy(B.m_ints, 0, T0, tOff, bLen);
        for (int i = 2; i < 16; ++i) {
            ti[i] = tOff += bMax;
            if ((i & 1) == 0) {
                LongArray.shiftUp(T0, tOff >>> 1, T0, tOff, bMax, 1);
                continue;
            }
            LongArray.add(T0, bMax, T0, tOff - bMax, T0, tOff, bMax);
        }
        long[] T1 = new long[T0.length];
        LongArray.shiftUp(T0, 0, T1, 0, T0.length, 4);
        long[] a = A.m_ints;
        long[] c = new long[cLen << 3];
        int MASK = 15;
        block1: for (int aPos = 0; aPos < aLen; ++aPos) {
            long aVal = a[aPos];
            int cOff = aPos;
            while (true) {
                int u = (int)aVal & MASK;
                int v = (int)(aVal >>>= 4) & MASK;
                LongArray.addBoth(c, cOff, T0, ti[u], T1, ti[v], bMax);
                if ((aVal >>>= 4) == 0L) continue block1;
                cOff += cLen;
            }
        }
        int cOff = c.length;
        while ((cOff -= cLen) != 0) {
            LongArray.addShiftedUp(c, cOff - cLen, c, cOff, cLen, 8);
        }
        return LongArray.reduceResult(c, 0, cLen, m, ks);
    }

    public LongArray modMultiplyAlt(LongArray other, int m, int[] ks) {
        int cTotal;
        int aDeg = this.degree();
        if (aDeg == 0) {
            return this;
        }
        int bDeg = other.degree();
        if (bDeg == 0) {
            return other;
        }
        LongArray A = this;
        LongArray B = other;
        if (aDeg > bDeg) {
            A = other;
            B = this;
            int tmp = aDeg;
            aDeg = bDeg;
            bDeg = tmp;
        }
        int aLen = aDeg + 63 >>> 6;
        int bLen = bDeg + 63 >>> 6;
        int cLen = aDeg + bDeg + 62 >>> 6;
        if (aLen == 1) {
            long a0 = A.m_ints[0];
            if (a0 == 1L) {
                return B;
            }
            long[] c0 = new long[cLen];
            LongArray.multiplyWord(a0, B.m_ints, bLen, c0, 0);
            return LongArray.reduceResult(c0, 0, cLen, m, ks);
        }
        int width = 4;
        int positions = 16;
        int top = 64;
        int banks = 8;
        int shifts = top < 64 ? positions : positions - 1;
        int bMax = bDeg + shifts + 63 >>> 6;
        int bTotal = bMax * banks;
        int stride = width * banks;
        int[] ci = new int[1 << width];
        ci[0] = cTotal = aLen;
        ci[1] = cTotal += bTotal;
        for (int i = 2; i < ci.length; ++i) {
            ci[i] = cTotal += cLen;
        }
        cTotal += cLen;
        long[] c = new long[++cTotal];
        LongArray.interleave(A.m_ints, 0, c, 0, aLen, width);
        int bOff = aLen;
        System.arraycopy(B.m_ints, 0, c, bOff, bLen);
        for (int bank = 1; bank < banks; ++bank) {
            LongArray.shiftUp(c, aLen, c, bOff += bMax, bMax, bank);
        }
        int MASK = (1 << width) - 1;
        int k = 0;
        while (true) {
            int aPos = 0;
            block3: do {
                long aVal = c[aPos] >>> k;
                int bank = 0;
                int bOff2 = aLen;
                while (true) {
                    int index;
                    if ((index = (int)aVal & MASK) != 0) {
                        LongArray.add(c, aPos + ci[index], c, bOff2, bMax);
                    }
                    if (++bank == banks) continue block3;
                    bOff2 += bMax;
                    aVal >>>= width;
                }
            } while (++aPos < aLen);
            if ((k += stride) >= top) {
                if (k >= 64) break;
                k = 64 - width;
                MASK &= MASK << top - k;
            }
            LongArray.shiftUp(c, aLen, bTotal, banks);
        }
        int ciPos = ci.length;
        while (--ciPos > 1) {
            if (((long)ciPos & 1L) == 0L) {
                LongArray.addShiftedUp(c, ci[ciPos >>> 1], c, ci[ciPos], cLen, positions);
                continue;
            }
            LongArray.distribute(c, ci[ciPos], ci[ciPos - 1], ci[1], cLen);
        }
        return LongArray.reduceResult(c, ci[1], cLen, m, ks);
    }

    public LongArray modReduce(int m, int[] ks) {
        long[] buf = Arrays.clone(this.m_ints);
        int rLen = LongArray.reduceInPlace(buf, 0, buf.length, m, ks);
        return new LongArray(buf, 0, rLen);
    }

    public LongArray multiply(LongArray other, int m, int[] ks) {
        int tOff;
        int aDeg = this.degree();
        if (aDeg == 0) {
            return this;
        }
        int bDeg = other.degree();
        if (bDeg == 0) {
            return other;
        }
        LongArray A = this;
        LongArray B = other;
        if (aDeg > bDeg) {
            A = other;
            B = this;
            int tmp = aDeg;
            aDeg = bDeg;
            bDeg = tmp;
        }
        int aLen = aDeg + 63 >>> 6;
        int bLen = bDeg + 63 >>> 6;
        int cLen = aDeg + bDeg + 62 >>> 6;
        if (aLen == 1) {
            long a0 = A.m_ints[0];
            if (a0 == 1L) {
                return B;
            }
            long[] c0 = new long[cLen];
            LongArray.multiplyWord(a0, B.m_ints, bLen, c0, 0);
            return new LongArray(c0, 0, cLen);
        }
        int bMax = bDeg + 7 + 63 >>> 6;
        int[] ti = new int[16];
        long[] T0 = new long[bMax << 4];
        ti[1] = tOff = bMax;
        System.arraycopy(B.m_ints, 0, T0, tOff, bLen);
        for (int i = 2; i < 16; ++i) {
            ti[i] = tOff += bMax;
            if ((i & 1) == 0) {
                LongArray.shiftUp(T0, tOff >>> 1, T0, tOff, bMax, 1);
                continue;
            }
            LongArray.add(T0, bMax, T0, tOff - bMax, T0, tOff, bMax);
        }
        long[] T1 = new long[T0.length];
        LongArray.shiftUp(T0, 0, T1, 0, T0.length, 4);
        long[] a = A.m_ints;
        long[] c = new long[cLen << 3];
        int MASK = 15;
        block1: for (int aPos = 0; aPos < aLen; ++aPos) {
            long aVal = a[aPos];
            int cOff = aPos;
            while (true) {
                int u = (int)aVal & MASK;
                int v = (int)(aVal >>>= 4) & MASK;
                LongArray.addBoth(c, cOff, T0, ti[u], T1, ti[v], bMax);
                if ((aVal >>>= 4) == 0L) continue block1;
                cOff += cLen;
            }
        }
        int cOff = c.length;
        while ((cOff -= cLen) != 0) {
            LongArray.addShiftedUp(c, cOff - cLen, c, cOff, cLen, 8);
        }
        return new LongArray(c, 0, cLen);
    }

    public void reduce(int m, int[] ks) {
        long[] buf = this.m_ints;
        int rLen = LongArray.reduceInPlace(buf, 0, buf.length, m, ks);
        if (rLen < buf.length) {
            this.m_ints = new long[rLen];
            System.arraycopy(buf, 0, this.m_ints, 0, rLen);
        }
    }

    private static LongArray reduceResult(long[] buf, int off, int len, int m, int[] ks) {
        int rLen = LongArray.reduceInPlace(buf, off, len, m, ks);
        return new LongArray(buf, off, rLen);
    }

    private static int reduceInPlace(long[] buf, int off, int len, int m, int[] ks) {
        int excessBits;
        int mLen = m + 63 >>> 6;
        if (len < mLen) {
            return len;
        }
        int numBits = Math.min(len << 6, (m << 1) - 1);
        for (excessBits = (len << 6) - numBits; excessBits >= 64; excessBits -= 64) {
            --len;
        }
        int kLen = ks.length;
        int kMax = ks[kLen - 1];
        int kNext = kLen > 1 ? ks[kLen - 2] : 0;
        int wordWiseLimit = Math.max(m, kMax + 64);
        int vectorableWords = excessBits + Math.min(numBits - wordWiseLimit, m - kNext) >> 6;
        if (vectorableWords > 1) {
            int vectorWiseWords = len - vectorableWords;
            LongArray.reduceVectorWise(buf, off, len, vectorWiseWords, m, ks);
            while (len > vectorWiseWords) {
                buf[off + --len] = 0L;
            }
            numBits = vectorWiseWords << 6;
        }
        if (numBits > wordWiseLimit) {
            LongArray.reduceWordWise(buf, off, len, wordWiseLimit, m, ks);
            numBits = wordWiseLimit;
        }
        if (numBits > m) {
            LongArray.reduceBitWise(buf, off, numBits, m, ks);
        }
        return mLen;
    }

    private static void reduceBitWise(long[] buf, int off, int bitlength, int m, int[] ks) {
        while (--bitlength >= m) {
            if (!LongArray.testBit(buf, off, bitlength)) continue;
            LongArray.reduceBit(buf, off, bitlength, m, ks);
        }
    }

    private static void reduceBit(long[] buf, int off, int bit, int m, int[] ks) {
        LongArray.flipBit(buf, off, bit);
        int n = bit - m;
        int j = ks.length;
        while (--j >= 0) {
            LongArray.flipBit(buf, off, ks[j] + n);
        }
        LongArray.flipBit(buf, off, n);
    }

    private static void reduceWordWise(long[] buf, int off, int len, int toBit, int m, int[] ks) {
        int toPos = toBit >>> 6;
        while (--len > toPos) {
            long word = buf[off + len];
            if (word == 0L) continue;
            buf[off + len] = 0L;
            LongArray.reduceWord(buf, off, len << 6, word, m, ks);
        }
        int partial = toBit & 0x3F;
        long word = buf[off + toPos] >>> partial;
        if (word != 0L) {
            int n = off + toPos;
            buf[n] = buf[n] ^ word << partial;
            LongArray.reduceWord(buf, off, toBit, word, m, ks);
        }
    }

    private static void reduceWord(long[] buf, int off, int bit, long word, int m, int[] ks) {
        int offset = bit - m;
        int j = ks.length;
        while (--j >= 0) {
            LongArray.flipWord(buf, off, offset + ks[j], word);
        }
        LongArray.flipWord(buf, off, offset, word);
    }

    private static void reduceVectorWise(long[] buf, int off, int len, int words, int m, int[] ks) {
        int baseBit = (words << 6) - m;
        int j = ks.length;
        while (--j >= 0) {
            LongArray.flipVector(buf, off, buf, off + words, len - words, baseBit + ks[j]);
        }
        LongArray.flipVector(buf, off, buf, off + words, len - words, baseBit);
    }

    private static void flipVector(long[] x, int xOff, long[] y, int yOff, int yLen, int bits) {
        xOff += bits >>> 6;
        if ((bits &= 0x3F) == 0) {
            LongArray.add(x, xOff, y, yOff, yLen);
        } else {
            long carry = LongArray.addShiftedDown(x, xOff + 1, y, yOff, yLen, 64 - bits);
            int n = xOff;
            x[n] = x[n] ^ carry;
        }
    }

    public LongArray modSquare(int m, int[] ks) {
        int len = this.getUsedLength();
        if (len == 0) {
            return this;
        }
        int _2len = len << 1;
        long[] r = new long[_2len];
        int pos = 0;
        while (pos < _2len) {
            long mi = this.m_ints[pos >>> 1];
            r[pos++] = LongArray.interleave2_32to64((int)mi);
            r[pos++] = LongArray.interleave2_32to64((int)(mi >>> 32));
        }
        return new LongArray(r, 0, LongArray.reduceInPlace(r, 0, r.length, m, ks));
    }

    public LongArray modSquareN(int n, int m, int[] ks) {
        int len = this.getUsedLength();
        if (len == 0) {
            return this;
        }
        int mLen = m + 63 >>> 6;
        long[] r = new long[mLen << 1];
        System.arraycopy(this.m_ints, 0, r, 0, len);
        while (--n >= 0) {
            LongArray.squareInPlace(r, len, m, ks);
            len = LongArray.reduceInPlace(r, 0, r.length, m, ks);
        }
        return new LongArray(r, 0, len);
    }

    public LongArray square(int m, int[] ks) {
        int len = this.getUsedLength();
        if (len == 0) {
            return this;
        }
        int _2len = len << 1;
        long[] r = new long[_2len];
        int pos = 0;
        while (pos < _2len) {
            long mi = this.m_ints[pos >>> 1];
            r[pos++] = LongArray.interleave2_32to64((int)mi);
            r[pos++] = LongArray.interleave2_32to64((int)(mi >>> 32));
        }
        return new LongArray(r, 0, r.length);
    }

    private static void squareInPlace(long[] x, int xLen, int m, int[] ks) {
        int pos = xLen << 1;
        while (--xLen >= 0) {
            long xVal = x[xLen];
            x[--pos] = LongArray.interleave2_32to64((int)(xVal >>> 32));
            x[--pos] = LongArray.interleave2_32to64((int)xVal);
        }
    }

    private static void interleave(long[] x, int xOff, long[] z, int zOff, int count, int width) {
        switch (width) {
            case 3: {
                LongArray.interleave3(x, xOff, z, zOff, count);
                break;
            }
            case 5: {
                LongArray.interleave5(x, xOff, z, zOff, count);
                break;
            }
            case 7: {
                LongArray.interleave7(x, xOff, z, zOff, count);
                break;
            }
            default: {
                LongArray.interleave2_n(x, xOff, z, zOff, count, bitLengths[width] - 1);
            }
        }
    }

    private static void interleave3(long[] x, int xOff, long[] z, int zOff, int count) {
        for (int i = 0; i < count; ++i) {
            z[zOff + i] = LongArray.interleave3(x[xOff + i]);
        }
    }

    private static long interleave3(long x) {
        long z = x & Long.MIN_VALUE;
        return z | LongArray.interleave3_21to63((int)x & 0x1FFFFF) | LongArray.interleave3_21to63((int)(x >>> 21) & 0x1FFFFF) << 1 | LongArray.interleave3_21to63((int)(x >>> 42) & 0x1FFFFF) << 2;
    }

    private static long interleave3_21to63(int x) {
        int r00 = INTERLEAVE3_TABLE[x & 0x7F];
        int r21 = INTERLEAVE3_TABLE[x >>> 7 & 0x7F];
        int r42 = INTERLEAVE3_TABLE[x >>> 14];
        return ((long)r42 & 0xFFFFFFFFL) << 42 | ((long)r21 & 0xFFFFFFFFL) << 21 | (long)r00 & 0xFFFFFFFFL;
    }

    private static void interleave5(long[] x, int xOff, long[] z, int zOff, int count) {
        for (int i = 0; i < count; ++i) {
            z[zOff + i] = LongArray.interleave5(x[xOff + i]);
        }
    }

    private static long interleave5(long x) {
        return LongArray.interleave3_13to65((int)x & 0x1FFF) | LongArray.interleave3_13to65((int)(x >>> 13) & 0x1FFF) << 1 | LongArray.interleave3_13to65((int)(x >>> 26) & 0x1FFF) << 2 | LongArray.interleave3_13to65((int)(x >>> 39) & 0x1FFF) << 3 | LongArray.interleave3_13to65((int)(x >>> 52) & 0x1FFF) << 4;
    }

    private static long interleave3_13to65(int x) {
        int r00 = INTERLEAVE5_TABLE[x & 0x7F];
        int r35 = INTERLEAVE5_TABLE[x >>> 7];
        return ((long)r35 & 0xFFFFFFFFL) << 35 | (long)r00 & 0xFFFFFFFFL;
    }

    private static void interleave7(long[] x, int xOff, long[] z, int zOff, int count) {
        for (int i = 0; i < count; ++i) {
            z[zOff + i] = LongArray.interleave7(x[xOff + i]);
        }
    }

    private static long interleave7(long x) {
        long z = x & Long.MIN_VALUE;
        return z | INTERLEAVE7_TABLE[(int)x & 0x1FF] | INTERLEAVE7_TABLE[(int)(x >>> 9) & 0x1FF] << 1 | INTERLEAVE7_TABLE[(int)(x >>> 18) & 0x1FF] << 2 | INTERLEAVE7_TABLE[(int)(x >>> 27) & 0x1FF] << 3 | INTERLEAVE7_TABLE[(int)(x >>> 36) & 0x1FF] << 4 | INTERLEAVE7_TABLE[(int)(x >>> 45) & 0x1FF] << 5 | INTERLEAVE7_TABLE[(int)(x >>> 54) & 0x1FF] << 6;
    }

    private static void interleave2_n(long[] x, int xOff, long[] z, int zOff, int count, int rounds) {
        for (int i = 0; i < count; ++i) {
            z[zOff + i] = LongArray.interleave2_n(x[xOff + i], rounds);
        }
    }

    private static long interleave2_n(long x, int rounds) {
        while (rounds > 1) {
            rounds -= 2;
            x = LongArray.interleave4_16to64((int)x & 0xFFFF) | LongArray.interleave4_16to64((int)(x >>> 16) & 0xFFFF) << 1 | LongArray.interleave4_16to64((int)(x >>> 32) & 0xFFFF) << 2 | LongArray.interleave4_16to64((int)(x >>> 48) & 0xFFFF) << 3;
        }
        if (rounds > 0) {
            x = LongArray.interleave2_32to64((int)x) | LongArray.interleave2_32to64((int)(x >>> 32)) << 1;
        }
        return x;
    }

    private static long interleave4_16to64(int x) {
        int r00 = INTERLEAVE4_TABLE[x & 0xFF];
        int r32 = INTERLEAVE4_TABLE[x >>> 8];
        return ((long)r32 & 0xFFFFFFFFL) << 32 | (long)r00 & 0xFFFFFFFFL;
    }

    private static long interleave2_32to64(int x) {
        int r00 = INTERLEAVE2_TABLE[x & 0xFF] | INTERLEAVE2_TABLE[x >>> 8 & 0xFF] << 16;
        int r32 = INTERLEAVE2_TABLE[x >>> 16 & 0xFF] | INTERLEAVE2_TABLE[x >>> 24] << 16;
        return ((long)r32 & 0xFFFFFFFFL) << 32 | (long)r00 & 0xFFFFFFFFL;
    }

    public LongArray modInverse(int m, int[] ks) {
        int uzDegree = this.degree();
        if (uzDegree == 0) {
            throw new IllegalStateException();
        }
        if (uzDegree == 1) {
            return this;
        }
        LongArray uz = (LongArray)this.clone();
        int t = m + 63 >>> 6;
        LongArray vz = new LongArray(t);
        LongArray.reduceBit(vz.m_ints, 0, m, m, ks);
        LongArray g1z = new LongArray(t);
        g1z.m_ints[0] = 1L;
        LongArray g2z = new LongArray(t);
        int[] uvDeg = new int[]{uzDegree, m + 1};
        LongArray[] uv = new LongArray[]{uz, vz};
        int[] ggDeg = new int[]{1, 0};
        LongArray[] gg = new LongArray[]{g1z, g2z};
        int b = 1;
        int duv1 = uvDeg[b];
        int dgg1 = ggDeg[b];
        int j = duv1 - uvDeg[1 - b];
        while (true) {
            if (j < 0) {
                j = -j;
                uvDeg[b] = duv1;
                ggDeg[b] = dgg1;
                b = 1 - b;
                duv1 = uvDeg[b];
                dgg1 = ggDeg[b];
            }
            uv[b].addShiftedByBitsSafe(uv[1 - b], uvDeg[1 - b], j);
            int duv2 = uv[b].degreeFrom(duv1);
            if (duv2 == 0) {
                return gg[1 - b];
            }
            int dgg2 = ggDeg[1 - b];
            gg[b].addShiftedByBitsSafe(gg[1 - b], dgg2, j);
            if ((dgg2 += j) > dgg1) {
                dgg1 = dgg2;
            } else if (dgg2 == dgg1) {
                dgg1 = gg[b].degreeFrom(dgg1);
            }
            j += duv2 - duv1;
            duv1 = duv2;
        }
    }

    public boolean equals(Object o) {
        if (!(o instanceof LongArray)) {
            return false;
        }
        LongArray other = (LongArray)o;
        int usedLen = this.getUsedLength();
        if (other.getUsedLength() != usedLen) {
            return false;
        }
        for (int i = 0; i < usedLen; ++i) {
            if (this.m_ints[i] == other.m_ints[i]) continue;
            return false;
        }
        return true;
    }

    public int hashCode() {
        int usedLen = this.getUsedLength();
        int hash = 1;
        for (int i = 0; i < usedLen; ++i) {
            long mi = this.m_ints[i];
            hash *= 31;
            hash ^= (int)mi;
            hash *= 31;
            hash ^= (int)(mi >>> 32);
        }
        return hash;
    }

    public Object clone() {
        return new LongArray(Arrays.clone(this.m_ints));
    }

    public String toString() {
        int i = this.getUsedLength();
        if (i == 0) {
            return "0";
        }
        StringBuffer sb = new StringBuffer(Long.toBinaryString(this.m_ints[--i]));
        while (--i >= 0) {
            String s = Long.toBinaryString(this.m_ints[i]);
            int len = s.length();
            if (len < 64) {
                sb.append(ZEROES.substring(len));
            }
            sb.append(s);
        }
        return sb.toString();
    }
}

