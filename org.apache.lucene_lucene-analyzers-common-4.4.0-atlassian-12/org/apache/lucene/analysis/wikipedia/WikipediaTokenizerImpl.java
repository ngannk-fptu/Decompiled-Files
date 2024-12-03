/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 */
package org.apache.lucene.analysis.wikipedia;

import java.io.IOException;
import java.io.Reader;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.wikipedia.WikipediaTokenizer;

class WikipediaTokenizerImpl {
    public static final int YYEOF = -1;
    private static final int ZZ_BUFFERSIZE = 4096;
    public static final int YYINITIAL = 0;
    public static final int CATEGORY_STATE = 2;
    public static final int INTERNAL_LINK_STATE = 4;
    public static final int EXTERNAL_LINK_STATE = 6;
    public static final int TWO_SINGLE_QUOTES_STATE = 8;
    public static final int THREE_SINGLE_QUOTES_STATE = 10;
    public static final int FIVE_SINGLE_QUOTES_STATE = 12;
    public static final int DOUBLE_EQUALS_STATE = 14;
    public static final int DOUBLE_BRACE_STATE = 16;
    public static final int STRING = 18;
    private static final int[] ZZ_LEXSTATE = new int[]{0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9};
    private static final String ZZ_CMAP_PACKED = "\t\u0000\u0001\u0014\u0001\u0013\u0001\u0000\u0001\u0014\u0001\u0012\u0012\u0000\u0001\u0014\u0001\u0000\u0001\n\u0001+\u0002\u0000\u0001\u0003\u0001\u0001\u0004\u0000\u0001\f\u0001\u0005\u0001\u0002\u0001\b\n\u000e\u0001\u0017\u0001\u0000\u0001\u0007\u0001\t\u0001\u000b\u0001+\u0001\u0004\u0002\r\u0001\u0018\u0005\r\u0001!\u0011\r\u0001\u0015\u0001\u0000\u0001\u0016\u0001\u0000\u0001\u0006\u0001\u0000\u0001\u0019\u0001#\u0002\r\u0001\u001b\u0001 \u0001\u001c\u0001(\u0001!\u0004\r\u0001\"\u0001\u001d\u0001)\u0001\r\u0001\u001e\u0001*\u0001\u001a\u0003\r\u0001$\u0001\u001f\u0001\r\u0001%\u0001'\u0001&B\u0000\u0017\r\u0001\u0000\u001f\r\u0001\u0000\u0568\r\n\u000f\u0086\r\n\u000f\u026c\r\n\u000fv\r\n\u000fv\r\n\u000fv\r\n\u000fv\r\n\u000fw\r\t\u000fv\r\n\u000fv\r\n\u000fv\r\n\u000f\u00e0\r\n\u000fv\r\n\u000f\u0166\r\n\u000f\u00b6\r\u0100\r\u0e00\r\u1040\u0000\u0150\u0011`\u0000\u0010\u0011\u0100\u0000\u0080\u0011\u0080\u0000\u19c0\u0011@\u0000\u5200\u0011\u0c00\u0000\u2bb0\u0010\u2150\u0000\u0200\u0011\u0465\u0000;\u0011=\r#\u0000";
    private static final char[] ZZ_CMAP = WikipediaTokenizerImpl.zzUnpackCMap("\t\u0000\u0001\u0014\u0001\u0013\u0001\u0000\u0001\u0014\u0001\u0012\u0012\u0000\u0001\u0014\u0001\u0000\u0001\n\u0001+\u0002\u0000\u0001\u0003\u0001\u0001\u0004\u0000\u0001\f\u0001\u0005\u0001\u0002\u0001\b\n\u000e\u0001\u0017\u0001\u0000\u0001\u0007\u0001\t\u0001\u000b\u0001+\u0001\u0004\u0002\r\u0001\u0018\u0005\r\u0001!\u0011\r\u0001\u0015\u0001\u0000\u0001\u0016\u0001\u0000\u0001\u0006\u0001\u0000\u0001\u0019\u0001#\u0002\r\u0001\u001b\u0001 \u0001\u001c\u0001(\u0001!\u0004\r\u0001\"\u0001\u001d\u0001)\u0001\r\u0001\u001e\u0001*\u0001\u001a\u0003\r\u0001$\u0001\u001f\u0001\r\u0001%\u0001'\u0001&B\u0000\u0017\r\u0001\u0000\u001f\r\u0001\u0000\u0568\r\n\u000f\u0086\r\n\u000f\u026c\r\n\u000fv\r\n\u000fv\r\n\u000fv\r\n\u000fv\r\n\u000fw\r\t\u000fv\r\n\u000fv\r\n\u000fv\r\n\u000f\u00e0\r\n\u000fv\r\n\u000f\u0166\r\n\u000f\u00b6\r\u0100\r\u0e00\r\u1040\u0000\u0150\u0011`\u0000\u0010\u0011\u0100\u0000\u0080\u0011\u0080\u0000\u19c0\u0011@\u0000\u5200\u0011\u0c00\u0000\u2bb0\u0010\u2150\u0000\u0200\u0011\u0465\u0000;\u0011=\r#\u0000");
    private static final int[] ZZ_ACTION = WikipediaTokenizerImpl.zzUnpackAction();
    private static final String ZZ_ACTION_PACKED_0 = "\n\u0000\u0004\u0001\u0004\u0002\u0001\u0003\u0001\u0001\u0001\u0004\u0001\u0001\u0002\u0005\u0001\u0006\u0002\u0005\u0001\u0007\u0001\u0005\u0002\b\u0001\t\u0001\n\u0001\t\u0001\u000b\u0001\f\u0001\b\u0001\r\u0001\u000e\u0001\r\u0001\u000f\u0001\u0010\u0001\b\u0001\u0011\u0001\b\u0004\u0012\u0001\u0013\u0001\u0012\u0001\u0014\u0001\u0015\u0001\u0016\u0003\u0000\u0001\u0017\f\u0000\u0001\u0018\u0001\u0019\u0001\u001a\u0001\u001b\u0001\t\u0001\u0000\u0001\u001c\u0001\u001d\u0001\u001e\u0001\u0000\u0001\u001f\u0001\u0000\u0001 \u0003\u0000\u0001!\u0001\"\u0002#\u0001\"\u0002$\u0002\u0000\u0001#\u0001\u0000\f#\u0001\"\u0003\u0000\u0001\t\u0001%\u0003\u0000\u0001&\u0001'\u0005\u0000\u0001(\u0004\u0000\u0001(\u0002\u0000\u0002(\u0002\u0000\u0001\t\u0005\u0000\u0001\u0019\u0001\"\u0001#\u0001)\u0003\u0000\u0001\t\u0002\u0000\u0001*\u0018\u0000\u0001+\u0002\u0000\u0001,\u0001-\u0001.";
    private static final int[] ZZ_ROWMAP = WikipediaTokenizerImpl.zzUnpackRowMap();
    private static final String ZZ_ROWMAP_PACKED_0 = "\u0000\u0000\u0000,\u0000X\u0000\u0084\u0000\u00b0\u0000\u00dc\u0000\u0108\u0000\u0134\u0000\u0160\u0000\u018c\u0000\u01b8\u0000\u01e4\u0000\u0210\u0000\u023c\u0000\u0268\u0000\u0294\u0000\u02c0\u0000\u02ec\u0000\u01b8\u0000\u0318\u0000\u0344\u0000\u0370\u0000\u01b8\u0000\u039c\u0000\u03c8\u0000\u03f4\u0000\u0420\u0000\u044c\u0000\u0478\u0000\u01b8\u0000\u039c\u0000\u04a4\u0000\u01b8\u0000\u04d0\u0000\u04fc\u0000\u0528\u0000\u0554\u0000\u0580\u0000\u05ac\u0000\u05d8\u0000\u0604\u0000\u0630\u0000\u065c\u0000\u0688\u0000\u06b4\u0000\u01b8\u0000\u06e0\u0000\u039c\u0000\u070c\u0000\u0738\u0000\u0764\u0000\u0790\u0000\u01b8\u0000\u01b8\u0000\u07bc\u0000\u07e8\u0000\u0814\u0000\u01b8\u0000\u0840\u0000\u086c\u0000\u0898\u0000\u08c4\u0000\u08f0\u0000\u091c\u0000\u0948\u0000\u0974\u0000\u09a0\u0000\u09cc\u0000\u09f8\u0000\u0a24\u0000\u0a50\u0000\u0a7c\u0000\u01b8\u0000\u01b8\u0000\u0aa8\u0000\u0ad4\u0000\u0b00\u0000\u0b00\u0000\u01b8\u0000\u0b2c\u0000\u0b58\u0000\u0b84\u0000\u0bb0\u0000\u0bdc\u0000\u0c08\u0000\u0c34\u0000\u0c60\u0000\u0c8c\u0000\u0cb8\u0000\u0ce4\u0000\u0d10\u0000\u0898\u0000\u0d3c\u0000\u0d68\u0000\u0d94\u0000\u0dc0\u0000\u0dec\u0000\u0e18\u0000\u0e44\u0000\u0e70\u0000\u0e9c\u0000\u0ec8\u0000\u0ef4\u0000\u0f20\u0000\u0f4c\u0000\u0f78\u0000\u0fa4\u0000\u0fd0\u0000\u0ffc\u0000\u1028\u0000\u1054\u0000\u1080\u0000\u10ac\u0000\u10d8\u0000\u01b8\u0000\u1104\u0000\u1130\u0000\u115c\u0000\u1188\u0000\u01b8\u0000\u11b4\u0000\u11e0\u0000\u120c\u0000\u1238\u0000\u1264\u0000\u1290\u0000\u12bc\u0000\u12e8\u0000\u1314\u0000\u1340\u0000\u136c\u0000\u1398\u0000\u13c4\u0000\u086c\u0000\u09f8\u0000\u13f0\u0000\u141c\u0000\u1448\u0000\u1474\u0000\u14a0\u0000\u14cc\u0000\u14f8\u0000\u1524\u0000\u01b8\u0000\u1550\u0000\u157c\u0000\u15a8\u0000\u15d4\u0000\u1600\u0000\u162c\u0000\u1658\u0000\u1684\u0000\u16b0\u0000\u01b8\u0000\u16dc\u0000\u1708\u0000\u1734\u0000\u1760\u0000\u178c\u0000\u17b8\u0000\u17e4\u0000\u1810\u0000\u183c\u0000\u1868\u0000\u1894\u0000\u18c0\u0000\u18ec\u0000\u1918\u0000\u1944\u0000\u1970\u0000\u199c\u0000\u19c8\u0000\u19f4\u0000\u1a20\u0000\u1a4c\u0000\u1a78\u0000\u1aa4\u0000\u1ad0\u0000\u1afc\u0000\u1b28\u0000\u1b54\u0000\u01b8\u0000\u01b8\u0000\u01b8";
    private static final int[] ZZ_TRANS = WikipediaTokenizerImpl.zzUnpackTrans();
    private static final String ZZ_TRANS_PACKED_0 = "\u0001\u000b\u0001\f\u0005\u000b\u0001\r\u0001\u000b\u0001\u000e\u0003\u000b\u0001\u000f\u0001\u0010\u0001\u0011\u0001\u0012\u0001\u0013\u0001\u0014\u0002\u000b\u0001\u0015\u0002\u000b\r\u000f\u0001\u0016\u0002\u000b\u0003\u000f\u0001\u000b\u0007\u0017\u0001\u0018\u0005\u0017\u0004\u0019\u0001\u0017\u0001\u001a\u0003\u0017\u0001\u001b\u0001\u0017\r\u0019\u0003\u0017\u0003\u0019\b\u0017\u0001\u0018\u0005\u0017\u0004\u001c\u0001\u0017\u0001\u001a\u0003\u0017\u0001\u001d\u0001\u0017\r\u001c\u0003\u0017\u0003\u001c\u0001\u0017\u0007\u001e\u0001\u001f\u0005\u001e\u0004 \u0001\u001e\u0001\u001a\u0002\u0017\u0001\u001e\u0001!\u0001\u001e\r \u0003\u001e\u0001\"\u0002 \u0002\u001e\u0001#\u0005\u001e\u0001\u001f\u0005\u001e\u0004$\u0001\u001e\u0001%\u0002\u001e\u0001&\u0002\u001e\r$\u0003\u001e\u0003$\b\u001e\u0001\u001f\u0005\u001e\u0004'\u0001\u001e\u0001%\u0002\u001e\u0001&\u0002\u001e\r'\u0003\u001e\u0003'\b\u001e\u0001\u001f\u0005\u001e\u0004'\u0001\u001e\u0001%\u0002\u001e\u0001(\u0002\u001e\r'\u0003\u001e\u0003'\b\u001e\u0001\u001f\u0001\u001e\u0001)\u0003\u001e\u0004*\u0001\u001e\u0001%\u0005\u001e\r*\u0003\u001e\u0003*\b\u001e\u0001+\u0005\u001e\u0004,\u0001\u001e\u0001%\u0005\u001e\r,\u0001\u001e\u0001-\u0001\u001e\u0003,\u0001\u001e\u0001.\u0001/\u0005.\u00010\u0001.\u00011\u0003.\u00042\u0001.\u00013\u0002.\u00014\u0002.\r2\u0002.\u00015\u00032\u0001.-\u0000\u000162\u0000\u00017\u0004\u0000\u00048\u0007\u0000\u00068\u00019\u00068\u0003\u0000\u00038\n\u0000\u0001:#\u0000\u0001;\u0001<\u0001=\u0001>\u0002?\u0001\u0000\u0001@\u0003\u0000\u0001@\u0001\u000f\u0001\u0010\u0001\u0011\u0001\u0012\u0007\u0000\r\u000f\u0003\u0000\u0003\u000f\u0003\u0000\u0001A\u0001\u0000\u0001B\u0002C\u0001\u0000\u0001D\u0003\u0000\u0001D\u0003\u0010\u0001\u0012\u0007\u0000\r\u0010\u0003\u0000\u0003\u0010\u0002\u0000\u0001;\u0001E\u0001=\u0001>\u0002C\u0001\u0000\u0001D\u0003\u0000\u0001D\u0001\u0011\u0001\u0010\u0001\u0011\u0001\u0012\u0007\u0000\r\u0011\u0003\u0000\u0003\u0011\u0003\u0000\u0001F\u0001\u0000\u0001B\u0002?\u0001\u0000\u0001@\u0003\u0000\u0001@\u0004\u0012\u0007\u0000\r\u0012\u0003\u0000\u0003\u0012\u0014\u0000\u0001\u000b-\u0000\u0001G;\u0000\u0001H\u000e\u0000\u00017\u0004\u0000\u00048\u0007\u0000\r8\u0003\u0000\u00038\u000e\u0000\u0004\u0019\u0007\u0000\r\u0019\u0003\u0000\u0003\u0019\u0014\u0000\u0001\u0017.\u0000\u0001I\"\u0000\u0004\u001c\u0007\u0000\r\u001c\u0003\u0000\u0003\u001c\u0017\u0000\u0001J\"\u0000\u0004 \u0007\u0000\r \u0003\u0000\u0003 \u000e\u0000\u0004 \u0007\u0000\u0002 \u0001K\n \u0003\u0000\u0003 \u0002\u0000\u0001L7\u0000\u0004$\u0007\u0000\r$\u0003\u0000\u0003$\u0014\u0000\u0001\u001e-\u0000\u0001M#\u0000\u0004'\u0007\u0000\r'\u0003\u0000\u0003'\u0016\u0000\u0001N\u001f\u0000\u0001O/\u0000\u0004*\u0007\u0000\r*\u0003\u0000\u0003*\t\u0000\u0001P\u0004\u0000\u00048\u0007\u0000\r8\u0003\u0000\u00038\u000e\u0000\u0004,\u0007\u0000\r,\u0003\u0000\u0003,'\u0000\u0001O\u0006\u0000\u0001Q3\u0000\u0001R/\u0000\u00042\u0007\u0000\r2\u0003\u0000\u00032\u0014\u0000\u0001.-\u0000\u0001S#\u0000\u00048\u0007\u0000\r8\u0003\u0000\u00038\f\u0000\u0001\u001e\u0001\u0000\u0004T\u0001\u0000\u0003U\u0003\u0000\rT\u0003\u0000\u0003T\f\u0000\u0001\u001e\u0001\u0000\u0004T\u0001\u0000\u0003U\u0003\u0000\u0003T\u0001V\tT\u0003\u0000\u0003T\u000e\u0000\u0001W\u0001\u0000\u0001W\b\u0000\rW\u0003\u0000\u0003W\u000e\u0000\u0001X\u0001Y\u0001Z\u0001[\u0007\u0000\rX\u0003\u0000\u0003X\u000e\u0000\u0001\\\u0001\u0000\u0001\\\b\u0000\r\\\u0003\u0000\u0003\\\u000e\u0000\u0001]\u0001^\u0001]\u0001^\u0007\u0000\r]\u0003\u0000\u0003]\u000e\u0000\u0001_\u0002`\u0001a\u0007\u0000\r_\u0003\u0000\u0003_\u000e\u0000\u0001@\u0002b\b\u0000\r@\u0003\u0000\u0003@\u000e\u0000\u0001c\u0002d\u0001e\u0007\u0000\rc\u0003\u0000\u0003c\u000e\u0000\u0004^\u0007\u0000\r^\u0003\u0000\u0003^\u000e\u0000\u0001f\u0002g\u0001h\u0007\u0000\rf\u0003\u0000\u0003f\u000e\u0000\u0001i\u0002j\u0001k\u0007\u0000\ri\u0003\u0000\u0003i\u000e\u0000\u0001l\u0001d\u0001m\u0001e\u0007\u0000\rl\u0003\u0000\u0003l\u000e\u0000\u0001n\u0002Y\u0001[\u0007\u0000\rn\u0003\u0000\u0003n\u0018\u0000\u0001o\u0001p4\u0000\u0001q\u0017\u0000\u0004 \u0007\u0000\u0002 \u0001r\n \u0003\u0000\u0003 \u0002\u0000\u0001sA\u0000\u0001t\u0001u \u0000\u00048\u0007\u0000\u00068\u0001v\u00068\u0003\u0000\u00038\u0002\u0000\u0001w3\u0000\u0001x9\u0000\u0001y\u0001z\u001c\u0000\u0001{\u0001\u0000\u0001\u001e\u0001\u0000\u0004T\u0001\u0000\u0003U\u0003\u0000\rT\u0003\u0000\u0003T\u000e\u0000\u0004|\u0001\u0000\u0003U\u0003\u0000\r|\u0003\u0000\u0003|\n\u0000\u0001{\u0001\u0000\u0001\u001e\u0001\u0000\u0004T\u0001\u0000\u0003U\u0003\u0000\bT\u0001}\u0004T\u0003\u0000\u0003T\u0002\u0000\u0001;\u000b\u0000\u0001W\u0001\u0000\u0001W\b\u0000\rW\u0003\u0000\u0003W\u0003\u0000\u0001~\u0001\u0000\u0001B\u0002\u007f\u0006\u0000\u0001X\u0001Y\u0001Z\u0001[\u0007\u0000\rX\u0003\u0000\u0003X\u0003\u0000\u0001\u0080\u0001\u0000\u0001B\u0002\u0081\u0001\u0000\u0001\u0082\u0003\u0000\u0001\u0082\u0003Y\u0001[\u0007\u0000\rY\u0003\u0000\u0003Y\u0003\u0000\u0001\u0083\u0001\u0000\u0001B\u0002\u0081\u0001\u0000\u0001\u0082\u0003\u0000\u0001\u0082\u0001Z\u0001Y\u0001Z\u0001[\u0007\u0000\rZ\u0003\u0000\u0003Z\u0003\u0000\u0001\u0084\u0001\u0000\u0001B\u0002\u007f\u0006\u0000\u0004[\u0007\u0000\r[\u0003\u0000\u0003[\u0003\u0000\u0001\u0085\u0002\u0000\u0001\u0085\u0007\u0000\u0001]\u0001^\u0001]\u0001^\u0007\u0000\r]\u0003\u0000\u0003]\u0003\u0000\u0001\u0085\u0002\u0000\u0001\u0085\u0007\u0000\u0004^\u0007\u0000\r^\u0003\u0000\u0003^\u0003\u0000\u0001\u007f\u0001\u0000\u0001B\u0002\u007f\u0006\u0000\u0001_\u0002`\u0001a\u0007\u0000\r_\u0003\u0000\u0003_\u0003\u0000\u0001\u0081\u0001\u0000\u0001B\u0002\u0081\u0001\u0000\u0001\u0082\u0003\u0000\u0001\u0082\u0003`\u0001a\u0007\u0000\r`\u0003\u0000\u0003`\u0003\u0000\u0001\u007f\u0001\u0000\u0001B\u0002\u007f\u0006\u0000\u0004a\u0007\u0000\ra\u0003\u0000\u0003a\u0003\u0000\u0001\u0082\u0002\u0000\u0002\u0082\u0001\u0000\u0001\u0082\u0003\u0000\u0001\u0082\u0003b\b\u0000\rb\u0003\u0000\u0003b\u0003\u0000\u0001F\u0001\u0000\u0001B\u0002?\u0001\u0000\u0001@\u0003\u0000\u0001@\u0001c\u0002d\u0001e\u0007\u0000\rc\u0003\u0000\u0003c\u0003\u0000\u0001A\u0001\u0000\u0001B\u0002C\u0001\u0000\u0001D\u0003\u0000\u0001D\u0003d\u0001e\u0007\u0000\rd\u0003\u0000\u0003d\u0003\u0000\u0001F\u0001\u0000\u0001B\u0002?\u0001\u0000\u0001@\u0003\u0000\u0001@\u0004e\u0007\u0000\re\u0003\u0000\u0003e\u0003\u0000\u0001?\u0001\u0000\u0001B\u0002?\u0001\u0000\u0001@\u0003\u0000\u0001@\u0001f\u0002g\u0001h\u0007\u0000\rf\u0003\u0000\u0003f\u0003\u0000\u0001C\u0001\u0000\u0001B\u0002C\u0001\u0000\u0001D\u0003\u0000\u0001D\u0003g\u0001h\u0007\u0000\rg\u0003\u0000\u0003g\u0003\u0000\u0001?\u0001\u0000\u0001B\u0002?\u0001\u0000\u0001@\u0003\u0000\u0001@\u0004h\u0007\u0000\rh\u0003\u0000\u0003h\u0003\u0000\u0001@\u0002\u0000\u0002@\u0001\u0000\u0001@\u0003\u0000\u0001@\u0001i\u0002j\u0001k\u0007\u0000\ri\u0003\u0000\u0003i\u0003\u0000\u0001D\u0002\u0000\u0002D\u0001\u0000\u0001D\u0003\u0000\u0001D\u0003j\u0001k\u0007\u0000\rj\u0003\u0000\u0003j\u0003\u0000\u0001@\u0002\u0000\u0002@\u0001\u0000\u0001@\u0003\u0000\u0001@\u0004k\u0007\u0000\rk\u0003\u0000\u0003k\u0003\u0000\u0001\u0086\u0001\u0000\u0001B\u0002?\u0001\u0000\u0001@\u0003\u0000\u0001@\u0001l\u0001d\u0001m\u0001e\u0007\u0000\rl\u0003\u0000\u0003l\u0003\u0000\u0001\u0087\u0001\u0000\u0001B\u0002C\u0001\u0000\u0001D\u0003\u0000\u0001D\u0001m\u0001d\u0001m\u0001e\u0007\u0000\rm\u0003\u0000\u0003m\u0003\u0000\u0001\u0084\u0001\u0000\u0001B\u0002\u007f\u0006\u0000\u0001n\u0002Y\u0001[\u0007\u0000\rn\u0003\u0000\u0003n\u0019\u0000\u0001p,\u0000\u0001\u00884\u0000\u0001\u0089\u0016\u0000\u0004 \u0007\u0000\r \u0003\u0000\u0001 \u0001\u008a\u0001 \u0019\u0000\u0001u,\u0000\u0001\u008b\u001d\u0000\u0001\u001e\u0001\u0000\u0004T\u0001\u0000\u0003U\u0003\u0000\u0003T\u0001\u008c\tT\u0003\u0000\u0003T\u0002\u0000\u0001\u008dB\u0000\u0001z,\u0000\u0001\u008e\u001c\u0000\u0001\u008f*\u0000\u0001{\u0003\u0000\u0004|\u0007\u0000\r|\u0003\u0000\u0003|\n\u0000\u0001{\u0001\u0000\u0001\u0090\u0001\u0000\u0004T\u0001\u0000\u0003U\u0003\u0000\rT\u0003\u0000\u0003T\u000e\u0000\u0001\u0091\u0001[\u0001\u0091\u0001[\u0007\u0000\r\u0091\u0003\u0000\u0003\u0091\u000e\u0000\u0004a\u0007\u0000\ra\u0003\u0000\u0003a\u000e\u0000\u0004e\u0007\u0000\re\u0003\u0000\u0003e\u000e\u0000\u0004h\u0007\u0000\rh\u0003\u0000\u0003h\u000e\u0000\u0004k\u0007\u0000\rk\u0003\u0000\u0003k\u000e\u0000\u0001\u0092\u0001e\u0001\u0092\u0001e\u0007\u0000\r\u0092\u0003\u0000\u0003\u0092\u000e\u0000\u0004[\u0007\u0000\r[\u0003\u0000\u0003[\u000e\u0000\u0004\u0093\u0007\u0000\r\u0093\u0003\u0000\u0003\u0093\u001b\u0000\u0001\u00941\u0000\u0001\u0095\u0018\u0000\u0004 \u0006\u0000\u0001\u0096\r \u0003\u0000\u0002 \u0001\u0097\u001b\u0000\u0001\u0098\u001a\u0000\u0001{\u0001\u0000\u0001\u001e\u0001\u0000\u0004T\u0001\u0000\u0003U\u0003\u0000\bT\u0001\u0099\u0004T\u0003\u0000\u0003T\u0002\u0000\u0001\u009aD\u0000\u0001\u009b\u001e\u0000\u0004\u009c\u0007\u0000\r\u009c\u0003\u0000\u0003\u009c\u0003\u0000\u0001~\u0001\u0000\u0001B\u0002\u007f\u0006\u0000\u0001\u0091\u0001[\u0001\u0091\u0001[\u0007\u0000\r\u0091\u0003\u0000\u0003\u0091\u0003\u0000\u0001\u0086\u0001\u0000\u0001B\u0002?\u0001\u0000\u0001@\u0003\u0000\u0001@\u0001\u0092\u0001e\u0001\u0092\u0001e\u0007\u0000\r\u0092\u0003\u0000\u0003\u0092\u0003\u0000\u0001\u0085\u0002\u0000\u0001\u0085\u0007\u0000\u0004\u0093\u0007\u0000\r\u0093\u0003\u0000\u0003\u0093\u001c\u0000\u0001\u009d-\u0000\u0001\u009e\u0016\u0000\u0001\u009f0\u0000\u0004 \u0006\u0000\u0001\u0096\r \u0003\u0000\u0003 \u001c\u0000\u0001\u00a0\u0019\u0000\u0001{\u0001\u0000\u0001O\u0001\u0000\u0004T\u0001\u0000\u0003U\u0003\u0000\rT\u0003\u0000\u0003T\u001c\u0000\u0001\u00a1\u001a\u0000\u0001\u00a2\u0002\u0000\u0004\u009c\u0007\u0000\r\u009c\u0003\u0000\u0003\u009c\u001d\u0000\u0001\u00a32\u0000\u0001\u00a4\u0010\u0000\u0001\u00a5?\u0000\u0001\u00a6+\u0000\u0001\u00a7\u001a\u0000\u0001\u001e\u0001\u0000\u0004|\u0001\u0000\u0003U\u0003\u0000\r|\u0003\u0000\u0003|\u001e\u0000\u0001\u00a8+\u0000\u0001\u00a9\u001b\u0000\u0004\u00aa\u0007\u0000\r\u00aa\u0003\u0000\u0003\u00aa\u001e\u0000\u0001\u00ab+\u0000\u0001\u00ac,\u0000\u0001\u00ad1\u0000\u0001\u00ae\t\u0000\u0001\u00af\n\u0000\u0004\u00aa\u0007\u0000\r\u00aa\u0003\u0000\u0003\u00aa\u001f\u0000\u0001\u00b0+\u0000\u0001\u00b1,\u0000\u0001\u00b2\u0012\u0000\u0001\u000b2\u0000\u0004\u00b3\u0007\u0000\r\u00b3\u0003\u0000\u0003\u00b3 \u0000\u0001\u00b4+\u0000\u0001\u00b5#\u0000\u0001\u00b6\u0016\u0000\u0002\u00b3\u0001\u0000\u0002\u00b3\u0001\u0000\u0002\u00b3\u0002\u0000\u0005\u00b3\u0007\u0000\r\u00b3\u0003\u0000\u0004\u00b3\u0017\u0000\u0001\u00b7+\u0000\u0001\u00b8\u0014\u0000";
    private static final int ZZ_UNKNOWN_ERROR = 0;
    private static final int ZZ_NO_MATCH = 1;
    private static final int ZZ_PUSHBACK_2BIG = 2;
    private static final String[] ZZ_ERROR_MSG = new String[]{"Unkown internal scanner error", "Error: could not match input", "Error: pushback value was too large"};
    private static final int[] ZZ_ATTRIBUTE = WikipediaTokenizerImpl.zzUnpackAttribute();
    private static final String ZZ_ATTRIBUTE_PACKED_0 = "\n\u0000\u0001\t\u0007\u0001\u0001\t\u0003\u0001\u0001\t\u0006\u0001\u0001\t\u0002\u0001\u0001\t\f\u0001\u0001\t\u0006\u0001\u0002\t\u0003\u0000\u0001\t\f\u0000\u0002\u0001\u0002\t\u0001\u0001\u0001\u0000\u0002\u0001\u0001\t\u0001\u0000\u0001\u0001\u0001\u0000\u0001\u0001\u0003\u0000\u0007\u0001\u0002\u0000\u0001\u0001\u0001\u0000\r\u0001\u0003\u0000\u0001\u0001\u0001\t\u0003\u0000\u0001\u0001\u0001\t\u0005\u0000\u0001\u0001\u0004\u0000\u0001\u0001\u0002\u0000\u0002\u0001\u0002\u0000\u0001\u0001\u0005\u0000\u0001\t\u0003\u0001\u0003\u0000\u0001\u0001\u0002\u0000\u0001\t\u0018\u0000\u0001\u0001\u0002\u0000\u0003\t";
    private Reader zzReader;
    private int zzState;
    private int zzLexicalState = 0;
    private char[] zzBuffer = new char[4096];
    private int zzMarkedPos;
    private int zzCurrentPos;
    private int zzStartRead;
    private int zzEndRead;
    private int yyline;
    private int yychar;
    private int yycolumn;
    private boolean zzAtBOL = true;
    private boolean zzAtEOF;
    private boolean zzEOFDone;
    public static final int ALPHANUM = 0;
    public static final int APOSTROPHE = 1;
    public static final int ACRONYM = 2;
    public static final int COMPANY = 3;
    public static final int EMAIL = 4;
    public static final int HOST = 5;
    public static final int NUM = 6;
    public static final int CJ = 7;
    public static final int INTERNAL_LINK = 8;
    public static final int EXTERNAL_LINK = 9;
    public static final int CITATION = 10;
    public static final int CATEGORY = 11;
    public static final int BOLD = 12;
    public static final int ITALICS = 13;
    public static final int BOLD_ITALICS = 14;
    public static final int HEADING = 15;
    public static final int SUB_HEADING = 16;
    public static final int EXTERNAL_LINK_URL = 17;
    private int currentTokType;
    private int numBalanced = 0;
    private int positionInc = 1;
    private int numLinkToks = 0;
    private int numWikiTokensSeen = 0;
    public static final String[] TOKEN_TYPES = WikipediaTokenizer.TOKEN_TYPES;

    private static int[] zzUnpackAction() {
        int[] result = new int[184];
        int offset = 0;
        offset = WikipediaTokenizerImpl.zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
        return result;
    }

    private static int zzUnpackAction(String packed, int offset, int[] result) {
        int i = 0;
        int j = offset;
        int l = packed.length();
        while (i < l) {
            int count = packed.charAt(i++);
            char value = packed.charAt(i++);
            do {
                result[j++] = value;
            } while (--count > 0);
        }
        return j;
    }

    private static int[] zzUnpackRowMap() {
        int[] result = new int[184];
        int offset = 0;
        offset = WikipediaTokenizerImpl.zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
        return result;
    }

    private static int zzUnpackRowMap(String packed, int offset, int[] result) {
        int i = 0;
        int j = offset;
        int l = packed.length();
        while (i < l) {
            int high = packed.charAt(i++) << 16;
            result[j++] = high | packed.charAt(i++);
        }
        return j;
    }

    private static int[] zzUnpackTrans() {
        int[] result = new int[7040];
        int offset = 0;
        offset = WikipediaTokenizerImpl.zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
        return result;
    }

    private static int zzUnpackTrans(String packed, int offset, int[] result) {
        int i = 0;
        int j = offset;
        int l = packed.length();
        while (i < l) {
            int count = packed.charAt(i++);
            int value = packed.charAt(i++);
            do {
                result[j++] = --value;
            } while (--count > 0);
        }
        return j;
    }

    private static int[] zzUnpackAttribute() {
        int[] result = new int[184];
        int offset = 0;
        offset = WikipediaTokenizerImpl.zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
        return result;
    }

    private static int zzUnpackAttribute(String packed, int offset, int[] result) {
        int i = 0;
        int j = offset;
        int l = packed.length();
        while (i < l) {
            int count = packed.charAt(i++);
            char value = packed.charAt(i++);
            do {
                result[j++] = value;
            } while (--count > 0);
        }
        return j;
    }

    public final int getNumWikiTokensSeen() {
        return this.numWikiTokensSeen;
    }

    public final int yychar() {
        return this.yychar;
    }

    public final int getPositionIncrement() {
        return this.positionInc;
    }

    final void getText(CharTermAttribute t) {
        t.copyBuffer(this.zzBuffer, this.zzStartRead, this.zzMarkedPos - this.zzStartRead);
    }

    final int setText(StringBuilder buffer) {
        int length = this.zzMarkedPos - this.zzStartRead;
        buffer.append(this.zzBuffer, this.zzStartRead, length);
        return length;
    }

    final void reset() {
        this.currentTokType = 0;
        this.numBalanced = 0;
        this.positionInc = 1;
        this.numLinkToks = 0;
        this.numWikiTokensSeen = 0;
    }

    WikipediaTokenizerImpl(Reader in) {
        this.zzReader = in;
    }

    private static char[] zzUnpackCMap(String packed) {
        char[] map = new char[65536];
        int i = 0;
        int j = 0;
        while (i < 230) {
            int count = packed.charAt(i++);
            char value = packed.charAt(i++);
            do {
                map[j++] = value;
            } while (--count > 0);
        }
        return map;
    }

    private boolean zzRefill() throws IOException {
        int numRead;
        if (this.zzStartRead > 0) {
            System.arraycopy(this.zzBuffer, this.zzStartRead, this.zzBuffer, 0, this.zzEndRead - this.zzStartRead);
            this.zzEndRead -= this.zzStartRead;
            this.zzCurrentPos -= this.zzStartRead;
            this.zzMarkedPos -= this.zzStartRead;
            this.zzStartRead = 0;
        }
        if (this.zzCurrentPos >= this.zzBuffer.length) {
            char[] newBuffer = new char[this.zzCurrentPos * 2];
            System.arraycopy(this.zzBuffer, 0, newBuffer, 0, this.zzBuffer.length);
            this.zzBuffer = newBuffer;
        }
        if ((numRead = this.zzReader.read(this.zzBuffer, this.zzEndRead, this.zzBuffer.length - this.zzEndRead)) > 0) {
            this.zzEndRead += numRead;
            return false;
        }
        if (numRead == 0) {
            int c = this.zzReader.read();
            if (c == -1) {
                return true;
            }
            this.zzBuffer[this.zzEndRead++] = (char)c;
            return false;
        }
        return true;
    }

    public final void yyclose() throws IOException {
        this.zzAtEOF = true;
        this.zzEndRead = this.zzStartRead;
        if (this.zzReader != null) {
            this.zzReader.close();
        }
    }

    public final void yyreset(Reader reader) {
        this.zzReader = reader;
        this.zzAtBOL = true;
        this.zzAtEOF = false;
        this.zzEOFDone = false;
        this.zzStartRead = 0;
        this.zzEndRead = 0;
        this.zzMarkedPos = 0;
        this.zzCurrentPos = 0;
        this.yycolumn = 0;
        this.yychar = 0;
        this.yyline = 0;
        this.zzLexicalState = 0;
        if (this.zzBuffer.length > 4096) {
            this.zzBuffer = new char[4096];
        }
    }

    public final int yystate() {
        return this.zzLexicalState;
    }

    public final void yybegin(int newState) {
        this.zzLexicalState = newState;
    }

    public final String yytext() {
        return new String(this.zzBuffer, this.zzStartRead, this.zzMarkedPos - this.zzStartRead);
    }

    public final char yycharat(int pos) {
        return this.zzBuffer[this.zzStartRead + pos];
    }

    public final int yylength() {
        return this.zzMarkedPos - this.zzStartRead;
    }

    private void zzScanError(int errorCode) {
        String message;
        try {
            message = ZZ_ERROR_MSG[errorCode];
        }
        catch (ArrayIndexOutOfBoundsException e) {
            message = ZZ_ERROR_MSG[0];
        }
        throw new Error(message);
    }

    public void yypushback(int number) {
        if (number > this.yylength()) {
            this.zzScanError(2);
        }
        this.zzMarkedPos -= number;
    }

    public int getNextToken() throws IOException {
        int zzEndReadL = this.zzEndRead;
        char[] zzBufferL = this.zzBuffer;
        char[] zzCMapL = ZZ_CMAP;
        int[] zzTransL = ZZ_TRANS;
        int[] zzRowMapL = ZZ_ROWMAP;
        int[] zzAttrL = ZZ_ATTRIBUTE;
        block94: while (true) {
            int zzInput;
            int zzMarkedPosL = this.zzMarkedPos;
            this.yychar += zzMarkedPosL - this.zzStartRead;
            int zzAction = -1;
            this.zzCurrentPos = this.zzStartRead = zzMarkedPosL;
            int zzCurrentPosL = this.zzStartRead;
            this.zzState = ZZ_LEXSTATE[this.zzLexicalState];
            int zzAttributes = zzAttrL[this.zzState];
            if ((zzAttributes & 1) == 1) {
                zzAction = this.zzState;
            }
            while (true) {
                if (zzCurrentPosL < zzEndReadL) {
                    zzInput = zzBufferL[zzCurrentPosL++];
                } else {
                    if (this.zzAtEOF) {
                        zzInput = -1;
                        break;
                    }
                    this.zzCurrentPos = zzCurrentPosL;
                    this.zzMarkedPos = zzMarkedPosL;
                    boolean eof = this.zzRefill();
                    zzCurrentPosL = this.zzCurrentPos;
                    zzMarkedPosL = this.zzMarkedPos;
                    zzBufferL = this.zzBuffer;
                    zzEndReadL = this.zzEndRead;
                    if (eof) {
                        zzInput = -1;
                        break;
                    }
                    zzInput = zzBufferL[zzCurrentPosL++];
                }
                int zzNext = zzTransL[zzRowMapL[this.zzState] + zzCMapL[zzInput]];
                if (zzNext == -1) break;
                this.zzState = zzNext;
                zzAttributes = zzAttrL[this.zzState];
                if ((zzAttributes & 1) != 1) continue;
                zzAction = this.zzState;
                zzMarkedPosL = zzCurrentPosL;
                if ((zzAttributes & 8) == 8) break;
            }
            this.zzMarkedPos = zzMarkedPosL;
            switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
                case 1: {
                    this.numWikiTokensSeen = 0;
                    this.positionInc = 1;
                    continue block94;
                }
                case 47: {
                    continue block94;
                }
                case 2: {
                    this.positionInc = 1;
                    return 0;
                }
                case 48: {
                    continue block94;
                }
                case 3: {
                    this.positionInc = 1;
                    return 7;
                }
                case 49: {
                    continue block94;
                }
                case 4: {
                    this.numWikiTokensSeen = 0;
                    this.positionInc = 1;
                    this.currentTokType = 17;
                    this.yybegin(6);
                    continue block94;
                }
                case 50: {
                    continue block94;
                }
                case 5: {
                    this.positionInc = 1;
                    continue block94;
                }
                case 51: {
                    continue block94;
                }
                case 6: {
                    this.yybegin(2);
                    ++this.numWikiTokensSeen;
                    return this.currentTokType;
                }
                case 52: {
                    continue block94;
                }
                case 7: {
                    this.yybegin(4);
                    ++this.numWikiTokensSeen;
                    return this.currentTokType;
                }
                case 53: {
                    continue block94;
                }
                case 8: {
                    continue block94;
                }
                case 54: {
                    continue block94;
                }
                case 9: {
                    this.positionInc = this.numLinkToks == 0 ? 0 : 1;
                    ++this.numWikiTokensSeen;
                    this.currentTokType = 9;
                    this.yybegin(6);
                    ++this.numLinkToks;
                    return this.currentTokType;
                }
                case 55: {
                    continue block94;
                }
                case 10: {
                    this.numLinkToks = 0;
                    this.positionInc = 0;
                    this.yybegin(0);
                    continue block94;
                }
                case 56: {
                    continue block94;
                }
                case 11: {
                    this.currentTokType = 12;
                    this.yybegin(10);
                    continue block94;
                }
                case 57: {
                    continue block94;
                }
                case 12: {
                    this.currentTokType = 13;
                    ++this.numWikiTokensSeen;
                    this.yybegin(18);
                    return this.currentTokType;
                }
                case 58: {
                    continue block94;
                }
                case 13: {
                    this.currentTokType = 9;
                    this.numWikiTokensSeen = 0;
                    this.yybegin(6);
                    continue block94;
                }
                case 59: {
                    continue block94;
                }
                case 14: {
                    this.yybegin(18);
                    ++this.numWikiTokensSeen;
                    return this.currentTokType;
                }
                case 60: {
                    continue block94;
                }
                case 15: {
                    this.currentTokType = 16;
                    this.numWikiTokensSeen = 0;
                    this.yybegin(18);
                    continue block94;
                }
                case 61: {
                    continue block94;
                }
                case 16: {
                    this.currentTokType = 15;
                    this.yybegin(14);
                    ++this.numWikiTokensSeen;
                    return this.currentTokType;
                }
                case 62: {
                    continue block94;
                }
                case 17: {
                    this.yybegin(16);
                    this.numWikiTokensSeen = 0;
                    return this.currentTokType;
                }
                case 63: {
                    continue block94;
                }
                case 18: {
                    continue block94;
                }
                case 64: {
                    continue block94;
                }
                case 19: {
                    this.yybegin(18);
                    ++this.numWikiTokensSeen;
                    return this.currentTokType;
                }
                case 65: {
                    continue block94;
                }
                case 20: {
                    this.numBalanced = 0;
                    this.numWikiTokensSeen = 0;
                    this.currentTokType = 9;
                    this.yybegin(6);
                    continue block94;
                }
                case 66: {
                    continue block94;
                }
                case 21: {
                    this.yybegin(18);
                    return this.currentTokType;
                }
                case 67: {
                    continue block94;
                }
                case 22: {
                    this.numWikiTokensSeen = 0;
                    this.positionInc = 1;
                    if (this.numBalanced == 0) {
                        ++this.numBalanced;
                        this.yybegin(8);
                        continue block94;
                    }
                    this.numBalanced = 0;
                    continue block94;
                }
                case 68: {
                    continue block94;
                }
                case 23: {
                    this.numWikiTokensSeen = 0;
                    this.positionInc = 1;
                    this.yybegin(14);
                    continue block94;
                }
                case 69: {
                    continue block94;
                }
                case 24: {
                    this.numWikiTokensSeen = 0;
                    this.positionInc = 1;
                    this.currentTokType = 8;
                    this.yybegin(4);
                    continue block94;
                }
                case 70: {
                    continue block94;
                }
                case 25: {
                    this.numWikiTokensSeen = 0;
                    this.positionInc = 1;
                    this.currentTokType = 10;
                    this.yybegin(16);
                    continue block94;
                }
                case 71: {
                    continue block94;
                }
                case 26: {
                    this.yybegin(0);
                    continue block94;
                }
                case 72: {
                    continue block94;
                }
                case 27: {
                    this.numLinkToks = 0;
                    this.yybegin(0);
                    continue block94;
                }
                case 73: {
                    continue block94;
                }
                case 28: {
                    this.currentTokType = 8;
                    this.numWikiTokensSeen = 0;
                    this.yybegin(4);
                    continue block94;
                }
                case 74: {
                    continue block94;
                }
                case 29: {
                    this.currentTokType = 8;
                    this.numWikiTokensSeen = 0;
                    this.yybegin(4);
                    continue block94;
                }
                case 75: {
                    continue block94;
                }
                case 30: {
                    this.yybegin(0);
                    continue block94;
                }
                case 76: {
                    continue block94;
                }
                case 31: {
                    this.numBalanced = 0;
                    this.currentTokType = 0;
                    this.yybegin(0);
                    continue block94;
                }
                case 77: {
                    continue block94;
                }
                case 32: {
                    this.numBalanced = 0;
                    this.numWikiTokensSeen = 0;
                    this.currentTokType = 8;
                    this.yybegin(4);
                    continue block94;
                }
                case 78: {
                    continue block94;
                }
                case 33: {
                    this.positionInc = 1;
                    return 1;
                }
                case 79: {
                    continue block94;
                }
                case 34: {
                    this.positionInc = 1;
                    return 5;
                }
                case 80: {
                    continue block94;
                }
                case 35: {
                    this.positionInc = 1;
                    return 6;
                }
                case 81: {
                    continue block94;
                }
                case 36: {
                    this.positionInc = 1;
                    return 3;
                }
                case 82: {
                    continue block94;
                }
                case 37: {
                    this.currentTokType = 14;
                    this.yybegin(12);
                    continue block94;
                }
                case 83: {
                    continue block94;
                }
                case 38: {
                    this.numBalanced = 0;
                    this.currentTokType = 0;
                    this.yybegin(0);
                    continue block94;
                }
                case 84: {
                    continue block94;
                }
                case 39: {
                    this.numBalanced = 0;
                    this.currentTokType = 0;
                    this.yybegin(0);
                    continue block94;
                }
                case 85: {
                    continue block94;
                }
                case 40: {
                    this.positionInc = 1;
                    return 2;
                }
                case 86: {
                    continue block94;
                }
                case 41: {
                    this.positionInc = 1;
                    return 4;
                }
                case 87: {
                    continue block94;
                }
                case 42: {
                    this.numBalanced = 0;
                    this.currentTokType = 0;
                    this.yybegin(0);
                    continue block94;
                }
                case 88: {
                    continue block94;
                }
                case 43: {
                    this.positionInc = 1;
                    ++this.numWikiTokensSeen;
                    this.yybegin(6);
                    return this.currentTokType;
                }
                case 89: {
                    continue block94;
                }
                case 44: {
                    this.numWikiTokensSeen = 0;
                    this.positionInc = 1;
                    this.currentTokType = 11;
                    this.yybegin(2);
                    continue block94;
                }
                case 90: {
                    continue block94;
                }
                case 45: {
                    this.currentTokType = 11;
                    this.numWikiTokensSeen = 0;
                    this.yybegin(2);
                    continue block94;
                }
                case 91: {
                    continue block94;
                }
                case 46: {
                    this.numBalanced = 0;
                    this.numWikiTokensSeen = 0;
                    this.currentTokType = 11;
                    this.yybegin(2);
                    continue block94;
                }
                case 92: {
                    continue block94;
                }
            }
            if (zzInput == -1 && this.zzStartRead == this.zzCurrentPos) {
                this.zzAtEOF = true;
                return -1;
            }
            this.zzScanError(1);
        }
    }
}

