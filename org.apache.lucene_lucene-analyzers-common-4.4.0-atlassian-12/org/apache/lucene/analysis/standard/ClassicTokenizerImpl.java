/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 */
package org.apache.lucene.analysis.standard;

import java.io.IOException;
import java.io.Reader;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.standard.StandardTokenizerInterface;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

class ClassicTokenizerImpl
implements StandardTokenizerInterface {
    public static final int YYEOF = -1;
    private static final int ZZ_BUFFERSIZE = 4096;
    public static final int YYINITIAL = 0;
    private static final int[] ZZ_LEXSTATE = new int[]{0, 0};
    private static final String ZZ_CMAP_PACKED = "\t\u0000\u0001\u0000\u0001\r\u0001\u0000\u0001\u0000\u0001\f\u0012\u0000\u0001\u0000\u0005\u0000\u0001\u0005\u0001\u0003\u0004\u0000\u0001\t\u0001\u0007\u0001\u0004\u0001\t\n\u0002\u0006\u0000\u0001\u0006\u001a\n\u0004\u0000\u0001\b\u0001\u0000\u001a\n/\u0000\u0001\n\n\u0000\u0001\n\u0004\u0000\u0001\n\u0005\u0000\u0017\n\u0001\u0000\u001f\n\u0001\u0000\u0128\n\u0002\u0000\u0012\n\u001c\u0000^\n\u0002\u0000\t\n\u0002\u0000\u0007\n\u000e\u0000\u0002\n\u000e\u0000\u0005\n\t\u0000\u0001\n\u008b\u0000\u0001\n\u000b\u0000\u0001\n\u0001\u0000\u0003\n\u0001\u0000\u0001\n\u0001\u0000\u0014\n\u0001\u0000,\n\u0001\u0000\b\n\u0002\u0000\u001a\n\f\u0000\u0082\n\n\u00009\n\u0002\u0000\u0002\n\u0002\u0000\u0002\n\u0003\u0000&\n\u0002\u0000\u0002\n7\u0000&\n\u0002\u0000\u0001\n\u0007\u0000'\nH\u0000\u001b\n\u0005\u0000\u0003\n.\u0000\u001a\n\u0005\u0000\u000b\n\u0015\u0000\n\u0002\u0007\u0000c\n\u0001\u0000\u0001\n\u000f\u0000\u0002\n\t\u0000\n\u0002\u0003\n\u0013\u0000\u0001\n\u0001\u0000\u001b\nS\u0000&\n\u015f\u00005\n\u0003\u0000\u0001\n\u0012\u0000\u0001\n\u0007\u0000\n\n\u0004\u0000\n\u0002\u0015\u0000\b\n\u0002\u0000\u0002\n\u0002\u0000\u0016\n\u0001\u0000\u0007\n\u0001\u0000\u0001\n\u0003\u0000\u0004\n\"\u0000\u0002\n\u0001\u0000\u0003\n\u0004\u0000\n\u0002\u0002\n\u0013\u0000\u0006\n\u0004\u0000\u0002\n\u0002\u0000\u0016\n\u0001\u0000\u0007\n\u0001\u0000\u0002\n\u0001\u0000\u0002\n\u0001\u0000\u0002\n\u001f\u0000\u0004\n\u0001\u0000\u0001\n\u0007\u0000\n\u0002\u0002\u0000\u0003\n\u0010\u0000\u0007\n\u0001\u0000\u0001\n\u0001\u0000\u0003\n\u0001\u0000\u0016\n\u0001\u0000\u0007\n\u0001\u0000\u0002\n\u0001\u0000\u0005\n\u0003\u0000\u0001\n\u0012\u0000\u0001\n\u000f\u0000\u0001\n\u0005\u0000\n\u0002\u0015\u0000\b\n\u0002\u0000\u0002\n\u0002\u0000\u0016\n\u0001\u0000\u0007\n\u0001\u0000\u0002\n\u0002\u0000\u0004\n\u0003\u0000\u0001\n\u001e\u0000\u0002\n\u0001\u0000\u0003\n\u0004\u0000\n\u0002\u0015\u0000\u0006\n\u0003\u0000\u0003\n\u0001\u0000\u0004\n\u0003\u0000\u0002\n\u0001\u0000\u0001\n\u0001\u0000\u0002\n\u0003\u0000\u0002\n\u0003\u0000\u0003\n\u0003\u0000\b\n\u0001\u0000\u0003\n-\u0000\t\u0002\u0015\u0000\b\n\u0001\u0000\u0003\n\u0001\u0000\u0017\n\u0001\u0000\n\n\u0001\u0000\u0005\n&\u0000\u0002\n\u0004\u0000\n\u0002\u0015\u0000\b\n\u0001\u0000\u0003\n\u0001\u0000\u0017\n\u0001\u0000\n\n\u0001\u0000\u0005\n$\u0000\u0001\n\u0001\u0000\u0002\n\u0004\u0000\n\u0002\u0015\u0000\b\n\u0001\u0000\u0003\n\u0001\u0000\u0017\n\u0001\u0000\u0010\n&\u0000\u0002\n\u0004\u0000\n\u0002\u0015\u0000\u0012\n\u0003\u0000\u0018\n\u0001\u0000\t\n\u0001\u0000\u0001\n\u0002\u0000\u0007\n9\u0000\u0001\u00010\n\u0001\u0001\u0002\n\f\u0001\u0007\n\t\u0001\n\u0002'\u0000\u0002\n\u0001\u0000\u0001\n\u0002\u0000\u0002\n\u0001\u0000\u0001\n\u0002\u0000\u0001\n\u0006\u0000\u0004\n\u0001\u0000\u0007\n\u0001\u0000\u0003\n\u0001\u0000\u0001\n\u0001\u0000\u0001\n\u0002\u0000\u0002\n\u0001\u0000\u0004\n\u0001\u0000\u0002\n\t\u0000\u0001\n\u0002\u0000\u0005\n\u0001\u0000\u0001\n\t\u0000\n\u0002\u0002\u0000\u0002\n\"\u0000\u0001\n\u001f\u0000\n\u0002\u0016\u0000\b\n\u0001\u0000\"\n\u001d\u0000\u0004\nt\u0000\"\n\u0001\u0000\u0005\n\u0001\u0000\u0002\n\u0015\u0000\n\u0002\u0006\u0000\u0006\nJ\u0000&\n\n\u0000'\n\t\u0000Z\n\u0005\u0000D\n\u0005\u0000R\n\u0006\u0000\u0007\n\u0001\u0000?\n\u0001\u0000\u0001\n\u0001\u0000\u0004\n\u0002\u0000\u0007\n\u0001\u0000\u0001\n\u0001\u0000\u0004\n\u0002\u0000'\n\u0001\u0000\u0001\n\u0001\u0000\u0004\n\u0002\u0000\u001f\n\u0001\u0000\u0001\n\u0001\u0000\u0004\n\u0002\u0000\u0007\n\u0001\u0000\u0001\n\u0001\u0000\u0004\n\u0002\u0000\u0007\n\u0001\u0000\u0007\n\u0001\u0000\u0017\n\u0001\u0000\u001f\n\u0001\u0000\u0001\n\u0001\u0000\u0004\n\u0002\u0000\u0007\n\u0001\u0000'\n\u0001\u0000\u0013\n\u000e\u0000\t\u0002.\u0000U\n\f\u0000\u026c\n\u0002\u0000\b\n\n\u0000\u001a\n\u0005\u0000K\n\u0095\u00004\n,\u0000\n\u0002&\u0000\n\u0002\u0006\u0000X\n\b\u0000)\n\u0557\u0000\u009c\n\u0004\u0000Z\n\u0006\u0000\u0016\n\u0002\u0000\u0006\n\u0002\u0000&\n\u0002\u0000\u0006\n\u0002\u0000\b\n\u0001\u0000\u0001\n\u0001\u0000\u0001\n\u0001\u0000\u0001\n\u0001\u0000\u001f\n\u0002\u00005\n\u0001\u0000\u0007\n\u0001\u0000\u0001\n\u0003\u0000\u0003\n\u0001\u0000\u0007\n\u0003\u0000\u0004\n\u0002\u0000\u0006\n\u0004\u0000\r\n\u0005\u0000\u0003\n\u0001\u0000\u0007\n\u0082\u0000\u0001\n\u0082\u0000\u0001\n\u0004\u0000\u0001\n\u0002\u0000\n\n\u0001\u0000\u0001\n\u0003\u0000\u0005\n\u0006\u0000\u0001\n\u0001\u0000\u0001\n\u0001\u0000\u0001\n\u0001\u0000\u0004\n\u0001\u0000\u0003\n\u0001\u0000\u0007\n\u0ecb\u0000\u0002\n*\u0000\u0005\n\n\u0000\u0001\u000bT\u000b\b\u000b\u0002\u000b\u0002\u000bZ\u000b\u0001\u000b\u0003\u000b\u0006\u000b(\u000b\u0003\u000b\u0001\u0000^\n\u0011\u0000\u0018\n8\u0000\u0010\u000b\u0100\u0000\u0080\u000b\u0080\u0000\u19b6\u000b\n\u000b@\u0000\u51a6\u000bZ\u000b\u048d\n\u0773\u0000\u2ba4\n\u215c\u0000\u012e\u000b\u00d2\u000b\u0007\n\f\u0000\u0005\n\u0005\u0000\u0001\n\u0001\u0000\n\n\u0001\u0000\r\n\u0001\u0000\u0005\n\u0001\u0000\u0001\n\u0001\u0000\u0002\n\u0001\u0000\u0002\n\u0001\u0000l\n!\u0000\u016b\n\u0012\u0000@\n\u0002\u00006\n(\u0000\f\nt\u0000\u0003\n\u0001\u0000\u0001\n\u0001\u0000\u0087\n\u0013\u0000\n\u0002\u0007\u0000\u001a\n\u0006\u0000\u001a\n\n\u0000\u0001\u000b:\u000b\u001f\n\u0003\u0000\u0006\n\u0002\u0000\u0006\n\u0002\u0000\u0006\n\u0002\u0000\u0003\n#\u0000";
    private static final char[] ZZ_CMAP = ClassicTokenizerImpl.zzUnpackCMap("\t\u0000\u0001\u0000\u0001\r\u0001\u0000\u0001\u0000\u0001\f\u0012\u0000\u0001\u0000\u0005\u0000\u0001\u0005\u0001\u0003\u0004\u0000\u0001\t\u0001\u0007\u0001\u0004\u0001\t\n\u0002\u0006\u0000\u0001\u0006\u001a\n\u0004\u0000\u0001\b\u0001\u0000\u001a\n/\u0000\u0001\n\n\u0000\u0001\n\u0004\u0000\u0001\n\u0005\u0000\u0017\n\u0001\u0000\u001f\n\u0001\u0000\u0128\n\u0002\u0000\u0012\n\u001c\u0000^\n\u0002\u0000\t\n\u0002\u0000\u0007\n\u000e\u0000\u0002\n\u000e\u0000\u0005\n\t\u0000\u0001\n\u008b\u0000\u0001\n\u000b\u0000\u0001\n\u0001\u0000\u0003\n\u0001\u0000\u0001\n\u0001\u0000\u0014\n\u0001\u0000,\n\u0001\u0000\b\n\u0002\u0000\u001a\n\f\u0000\u0082\n\n\u00009\n\u0002\u0000\u0002\n\u0002\u0000\u0002\n\u0003\u0000&\n\u0002\u0000\u0002\n7\u0000&\n\u0002\u0000\u0001\n\u0007\u0000'\nH\u0000\u001b\n\u0005\u0000\u0003\n.\u0000\u001a\n\u0005\u0000\u000b\n\u0015\u0000\n\u0002\u0007\u0000c\n\u0001\u0000\u0001\n\u000f\u0000\u0002\n\t\u0000\n\u0002\u0003\n\u0013\u0000\u0001\n\u0001\u0000\u001b\nS\u0000&\n\u015f\u00005\n\u0003\u0000\u0001\n\u0012\u0000\u0001\n\u0007\u0000\n\n\u0004\u0000\n\u0002\u0015\u0000\b\n\u0002\u0000\u0002\n\u0002\u0000\u0016\n\u0001\u0000\u0007\n\u0001\u0000\u0001\n\u0003\u0000\u0004\n\"\u0000\u0002\n\u0001\u0000\u0003\n\u0004\u0000\n\u0002\u0002\n\u0013\u0000\u0006\n\u0004\u0000\u0002\n\u0002\u0000\u0016\n\u0001\u0000\u0007\n\u0001\u0000\u0002\n\u0001\u0000\u0002\n\u0001\u0000\u0002\n\u001f\u0000\u0004\n\u0001\u0000\u0001\n\u0007\u0000\n\u0002\u0002\u0000\u0003\n\u0010\u0000\u0007\n\u0001\u0000\u0001\n\u0001\u0000\u0003\n\u0001\u0000\u0016\n\u0001\u0000\u0007\n\u0001\u0000\u0002\n\u0001\u0000\u0005\n\u0003\u0000\u0001\n\u0012\u0000\u0001\n\u000f\u0000\u0001\n\u0005\u0000\n\u0002\u0015\u0000\b\n\u0002\u0000\u0002\n\u0002\u0000\u0016\n\u0001\u0000\u0007\n\u0001\u0000\u0002\n\u0002\u0000\u0004\n\u0003\u0000\u0001\n\u001e\u0000\u0002\n\u0001\u0000\u0003\n\u0004\u0000\n\u0002\u0015\u0000\u0006\n\u0003\u0000\u0003\n\u0001\u0000\u0004\n\u0003\u0000\u0002\n\u0001\u0000\u0001\n\u0001\u0000\u0002\n\u0003\u0000\u0002\n\u0003\u0000\u0003\n\u0003\u0000\b\n\u0001\u0000\u0003\n-\u0000\t\u0002\u0015\u0000\b\n\u0001\u0000\u0003\n\u0001\u0000\u0017\n\u0001\u0000\n\n\u0001\u0000\u0005\n&\u0000\u0002\n\u0004\u0000\n\u0002\u0015\u0000\b\n\u0001\u0000\u0003\n\u0001\u0000\u0017\n\u0001\u0000\n\n\u0001\u0000\u0005\n$\u0000\u0001\n\u0001\u0000\u0002\n\u0004\u0000\n\u0002\u0015\u0000\b\n\u0001\u0000\u0003\n\u0001\u0000\u0017\n\u0001\u0000\u0010\n&\u0000\u0002\n\u0004\u0000\n\u0002\u0015\u0000\u0012\n\u0003\u0000\u0018\n\u0001\u0000\t\n\u0001\u0000\u0001\n\u0002\u0000\u0007\n9\u0000\u0001\u00010\n\u0001\u0001\u0002\n\f\u0001\u0007\n\t\u0001\n\u0002'\u0000\u0002\n\u0001\u0000\u0001\n\u0002\u0000\u0002\n\u0001\u0000\u0001\n\u0002\u0000\u0001\n\u0006\u0000\u0004\n\u0001\u0000\u0007\n\u0001\u0000\u0003\n\u0001\u0000\u0001\n\u0001\u0000\u0001\n\u0002\u0000\u0002\n\u0001\u0000\u0004\n\u0001\u0000\u0002\n\t\u0000\u0001\n\u0002\u0000\u0005\n\u0001\u0000\u0001\n\t\u0000\n\u0002\u0002\u0000\u0002\n\"\u0000\u0001\n\u001f\u0000\n\u0002\u0016\u0000\b\n\u0001\u0000\"\n\u001d\u0000\u0004\nt\u0000\"\n\u0001\u0000\u0005\n\u0001\u0000\u0002\n\u0015\u0000\n\u0002\u0006\u0000\u0006\nJ\u0000&\n\n\u0000'\n\t\u0000Z\n\u0005\u0000D\n\u0005\u0000R\n\u0006\u0000\u0007\n\u0001\u0000?\n\u0001\u0000\u0001\n\u0001\u0000\u0004\n\u0002\u0000\u0007\n\u0001\u0000\u0001\n\u0001\u0000\u0004\n\u0002\u0000'\n\u0001\u0000\u0001\n\u0001\u0000\u0004\n\u0002\u0000\u001f\n\u0001\u0000\u0001\n\u0001\u0000\u0004\n\u0002\u0000\u0007\n\u0001\u0000\u0001\n\u0001\u0000\u0004\n\u0002\u0000\u0007\n\u0001\u0000\u0007\n\u0001\u0000\u0017\n\u0001\u0000\u001f\n\u0001\u0000\u0001\n\u0001\u0000\u0004\n\u0002\u0000\u0007\n\u0001\u0000'\n\u0001\u0000\u0013\n\u000e\u0000\t\u0002.\u0000U\n\f\u0000\u026c\n\u0002\u0000\b\n\n\u0000\u001a\n\u0005\u0000K\n\u0095\u00004\n,\u0000\n\u0002&\u0000\n\u0002\u0006\u0000X\n\b\u0000)\n\u0557\u0000\u009c\n\u0004\u0000Z\n\u0006\u0000\u0016\n\u0002\u0000\u0006\n\u0002\u0000&\n\u0002\u0000\u0006\n\u0002\u0000\b\n\u0001\u0000\u0001\n\u0001\u0000\u0001\n\u0001\u0000\u0001\n\u0001\u0000\u001f\n\u0002\u00005\n\u0001\u0000\u0007\n\u0001\u0000\u0001\n\u0003\u0000\u0003\n\u0001\u0000\u0007\n\u0003\u0000\u0004\n\u0002\u0000\u0006\n\u0004\u0000\r\n\u0005\u0000\u0003\n\u0001\u0000\u0007\n\u0082\u0000\u0001\n\u0082\u0000\u0001\n\u0004\u0000\u0001\n\u0002\u0000\n\n\u0001\u0000\u0001\n\u0003\u0000\u0005\n\u0006\u0000\u0001\n\u0001\u0000\u0001\n\u0001\u0000\u0001\n\u0001\u0000\u0004\n\u0001\u0000\u0003\n\u0001\u0000\u0007\n\u0ecb\u0000\u0002\n*\u0000\u0005\n\n\u0000\u0001\u000bT\u000b\b\u000b\u0002\u000b\u0002\u000bZ\u000b\u0001\u000b\u0003\u000b\u0006\u000b(\u000b\u0003\u000b\u0001\u0000^\n\u0011\u0000\u0018\n8\u0000\u0010\u000b\u0100\u0000\u0080\u000b\u0080\u0000\u19b6\u000b\n\u000b@\u0000\u51a6\u000bZ\u000b\u048d\n\u0773\u0000\u2ba4\n\u215c\u0000\u012e\u000b\u00d2\u000b\u0007\n\f\u0000\u0005\n\u0005\u0000\u0001\n\u0001\u0000\n\n\u0001\u0000\r\n\u0001\u0000\u0005\n\u0001\u0000\u0001\n\u0001\u0000\u0002\n\u0001\u0000\u0002\n\u0001\u0000l\n!\u0000\u016b\n\u0012\u0000@\n\u0002\u00006\n(\u0000\f\nt\u0000\u0003\n\u0001\u0000\u0001\n\u0001\u0000\u0087\n\u0013\u0000\n\u0002\u0007\u0000\u001a\n\u0006\u0000\u001a\n\n\u0000\u0001\u000b:\u000b\u001f\n\u0003\u0000\u0006\n\u0002\u0000\u0006\n\u0002\u0000\u0006\n\u0002\u0000\u0003\n#\u0000");
    private static final int[] ZZ_ACTION = ClassicTokenizerImpl.zzUnpackAction();
    private static final String ZZ_ACTION_PACKED_0 = "\u0001\u0000\u0001\u0001\u0003\u0002\u0001\u0003\u0001\u0001\u000b\u0000\u0001\u0002\u0003\u0004\u0002\u0000\u0001\u0005\u0001\u0000\u0001\u0005\u0003\u0004\u0006\u0005\u0001\u0006\u0001\u0004\u0002\u0007\u0001\b\u0001\u0000\u0001\b\u0003\u0000\u0002\b\u0001\t\u0001\n\u0001\u0004";
    private static final int[] ZZ_ROWMAP = ClassicTokenizerImpl.zzUnpackRowMap();
    private static final String ZZ_ROWMAP_PACKED_0 = "\u0000\u0000\u0000\u000e\u0000\u001c\u0000*\u00008\u0000\u000e\u0000F\u0000T\u0000b\u0000p\u0000~\u0000\u008c\u0000\u009a\u0000\u00a8\u0000\u00b6\u0000\u00c4\u0000\u00d2\u0000\u00e0\u0000\u00ee\u0000\u00fc\u0000\u010a\u0000\u0118\u0000\u0126\u0000\u0134\u0000\u0142\u0000\u0150\u0000\u015e\u0000\u016c\u0000\u017a\u0000\u0188\u0000\u0196\u0000\u01a4\u0000\u01b2\u0000\u01c0\u0000\u01ce\u0000\u01dc\u0000\u01ea\u0000\u01f8\u0000\u00d2\u0000\u0206\u0000\u0214\u0000\u0222\u0000\u0230\u0000\u023e\u0000\u024c\u0000\u025a\u0000T\u0000\u008c\u0000\u0268\u0000\u0276\u0000\u0284";
    private static final int[] ZZ_TRANS = ClassicTokenizerImpl.zzUnpackTrans();
    private static final String ZZ_TRANS_PACKED_0 = "\u0001\u0002\u0001\u0003\u0001\u0004\u0007\u0002\u0001\u0005\u0001\u0006\u0001\u0007\u0001\u0002\u000f\u0000\u0002\u0003\u0001\u0000\u0001\b\u0001\u0000\u0001\t\u0002\n\u0001\u000b\u0001\u0003\u0004\u0000\u0001\u0003\u0001\u0004\u0001\u0000\u0001\f\u0001\u0000\u0001\t\u0002\r\u0001\u000e\u0001\u0004\u0004\u0000\u0001\u0003\u0001\u0004\u0001\u000f\u0001\u0010\u0001\u0011\u0001\u0012\u0002\n\u0001\u000b\u0001\u0013\u0010\u0000\u0001\u0002\u0001\u0000\u0001\u0014\u0001\u0015\u0007\u0000\u0001\u0016\u0004\u0000\u0002\u0017\u0007\u0000\u0001\u0017\u0004\u0000\u0001\u0018\u0001\u0019\u0007\u0000\u0001\u001a\u0005\u0000\u0001\u001b\u0007\u0000\u0001\u000b\u0004\u0000\u0001\u001c\u0001\u001d\u0007\u0000\u0001\u001e\u0004\u0000\u0001\u001f\u0001 \u0007\u0000\u0001!\u0004\u0000\u0001\"\u0001#\u0007\u0000\u0001$\r\u0000\u0001%\u0004\u0000\u0001\u0014\u0001\u0015\u0007\u0000\u0001&\r\u0000\u0001'\u0004\u0000\u0002\u0017\u0007\u0000\u0001(\u0004\u0000\u0001\u0003\u0001\u0004\u0001\u000f\u0001\b\u0001\u0011\u0001\u0012\u0002\n\u0001\u000b\u0001\u0013\u0004\u0000\u0002\u0014\u0001\u0000\u0001)\u0001\u0000\u0001\t\u0002*\u0001\u0000\u0001\u0014\u0004\u0000\u0001\u0014\u0001\u0015\u0001\u0000\u0001+\u0001\u0000\u0001\t\u0002,\u0001-\u0001\u0015\u0004\u0000\u0001\u0014\u0001\u0015\u0001\u0000\u0001)\u0001\u0000\u0001\t\u0002*\u0001\u0000\u0001\u0016\u0004\u0000\u0002\u0017\u0001\u0000\u0001.\u0002\u0000\u0001.\u0002\u0000\u0001\u0017\u0004\u0000\u0002\u0018\u0001\u0000\u0001*\u0001\u0000\u0001\t\u0002*\u0001\u0000\u0001\u0018\u0004\u0000\u0001\u0018\u0001\u0019\u0001\u0000\u0001,\u0001\u0000\u0001\t\u0002,\u0001-\u0001\u0019\u0004\u0000\u0001\u0018\u0001\u0019\u0001\u0000\u0001*\u0001\u0000\u0001\t\u0002*\u0001\u0000\u0001\u001a\u0005\u0000\u0001\u001b\u0001\u0000\u0001-\u0002\u0000\u0003-\u0001\u001b\u0004\u0000\u0002\u001c\u0001\u0000\u0001/\u0001\u0000\u0001\t\u0002\n\u0001\u000b\u0001\u001c\u0004\u0000\u0001\u001c\u0001\u001d\u0001\u0000\u00010\u0001\u0000\u0001\t\u0002\r\u0001\u000e\u0001\u001d\u0004\u0000\u0001\u001c\u0001\u001d\u0001\u0000\u0001/\u0001\u0000\u0001\t\u0002\n\u0001\u000b\u0001\u001e\u0004\u0000\u0002\u001f\u0001\u0000\u0001\n\u0001\u0000\u0001\t\u0002\n\u0001\u000b\u0001\u001f\u0004\u0000\u0001\u001f\u0001 \u0001\u0000\u0001\r\u0001\u0000\u0001\t\u0002\r\u0001\u000e\u0001 \u0004\u0000\u0001\u001f\u0001 \u0001\u0000\u0001\n\u0001\u0000\u0001\t\u0002\n\u0001\u000b\u0001!\u0004\u0000\u0002\"\u0001\u0000\u0001\u000b\u0002\u0000\u0003\u000b\u0001\"\u0004\u0000\u0001\"\u0001#\u0001\u0000\u0001\u000e\u0002\u0000\u0003\u000e\u0001#\u0004\u0000\u0001\"\u0001#\u0001\u0000\u0001\u000b\u0002\u0000\u0003\u000b\u0001$\u0006\u0000\u0001\u000f\u0006\u0000\u0001%\u0004\u0000\u0001\u0014\u0001\u0015\u0001\u0000\u00011\u0001\u0000\u0001\t\u0002*\u0001\u0000\u0001\u0016\u0004\u0000\u0002\u0017\u0001\u0000\u0001.\u0002\u0000\u0001.\u0002\u0000\u0001(\u0004\u0000\u0002\u0014\u0007\u0000\u0001\u0014\u0004\u0000\u0002\u0018\u0007\u0000\u0001\u0018\u0004\u0000\u0002\u001c\u0007\u0000\u0001\u001c\u0004\u0000\u0002\u001f\u0007\u0000\u0001\u001f\u0004\u0000\u0002\"\u0007\u0000\u0001\"\u0004\u0000\u00022\u0007\u0000\u00012\u0004\u0000\u0002\u0014\u0007\u0000\u00013\u0004\u0000\u00022\u0001\u0000\u0001.\u0002\u0000\u0001.\u0002\u0000\u00012\u0004\u0000\u0002\u0014\u0001\u0000\u00011\u0001\u0000\u0001\t\u0002*\u0001\u0000\u0001\u0014\u0003\u0000";
    private static final int ZZ_UNKNOWN_ERROR = 0;
    private static final int ZZ_NO_MATCH = 1;
    private static final int ZZ_PUSHBACK_2BIG = 2;
    private static final String[] ZZ_ERROR_MSG = new String[]{"Unkown internal scanner error", "Error: could not match input", "Error: pushback value was too large"};
    private static final int[] ZZ_ATTRIBUTE = ClassicTokenizerImpl.zzUnpackAttribute();
    private static final String ZZ_ATTRIBUTE_PACKED_0 = "\u0001\u0000\u0001\t\u0003\u0001\u0001\t\u0001\u0001\u000b\u0000\u0004\u0001\u0002\u0000\u0001\u0001\u0001\u0000\u000f\u0001\u0001\u0000\u0001\u0001\u0003\u0000\u0005\u0001";
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
    public static final int ACRONYM_DEP = 8;
    public static final String[] TOKEN_TYPES = StandardTokenizer.TOKEN_TYPES;

    private static int[] zzUnpackAction() {
        int[] result = new int[51];
        int offset = 0;
        offset = ClassicTokenizerImpl.zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
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
        int[] result = new int[51];
        int offset = 0;
        offset = ClassicTokenizerImpl.zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
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
        int[] result = new int[658];
        int offset = 0;
        offset = ClassicTokenizerImpl.zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
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
        int[] result = new int[51];
        int offset = 0;
        offset = ClassicTokenizerImpl.zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
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

    @Override
    public final int yychar() {
        return this.yychar;
    }

    @Override
    public final void getText(CharTermAttribute t) {
        t.copyBuffer(this.zzBuffer, this.zzStartRead, this.zzMarkedPos - this.zzStartRead);
    }

    ClassicTokenizerImpl(Reader in) {
        this.zzReader = in;
    }

    private static char[] zzUnpackCMap(String packed) {
        char[] map = new char[65536];
        int i = 0;
        int j = 0;
        while (i < 1154) {
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

    @Override
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

    @Override
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

    @Override
    public int getNextToken() throws IOException {
        int zzEndReadL = this.zzEndRead;
        char[] zzBufferL = this.zzBuffer;
        char[] zzCMapL = ZZ_CMAP;
        int[] zzTransL = ZZ_TRANS;
        int[] zzRowMapL = ZZ_ROWMAP;
        int[] zzAttrL = ZZ_ATTRIBUTE;
        block22: while (true) {
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
                    continue block22;
                }
                case 11: {
                    continue block22;
                }
                case 2: {
                    return 0;
                }
                case 12: {
                    continue block22;
                }
                case 3: {
                    return 7;
                }
                case 13: {
                    continue block22;
                }
                case 4: {
                    return 5;
                }
                case 14: {
                    continue block22;
                }
                case 5: {
                    return 6;
                }
                case 15: {
                    continue block22;
                }
                case 6: {
                    return 1;
                }
                case 16: {
                    continue block22;
                }
                case 7: {
                    return 3;
                }
                case 17: {
                    continue block22;
                }
                case 8: {
                    return 8;
                }
                case 18: {
                    continue block22;
                }
                case 9: {
                    return 2;
                }
                case 19: {
                    continue block22;
                }
                case 10: {
                    return 4;
                }
                case 20: {
                    continue block22;
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

