/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.ASdebug.ASDebugStream;
import groovyjarjarantlr.ASdebug.IASDebugStream;
import groovyjarjarantlr.ASdebug.TokenOffsetInfo;
import groovyjarjarantlr.Token;
import groovyjarjarantlr.TokenStream;
import groovyjarjarantlr.TokenStreamException;
import groovyjarjarantlr.TokenWithIndex;
import groovyjarjarantlr.collections.impl.BitSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TokenStreamRewriteEngine
implements TokenStream,
IASDebugStream {
    public static final int MIN_TOKEN_INDEX = 0;
    public static final String DEFAULT_PROGRAM_NAME = "default";
    public static final int PROGRAM_INIT_SIZE = 100;
    protected List tokens;
    protected Map programs = null;
    protected Map lastRewriteTokenIndexes = null;
    protected int index = 0;
    protected TokenStream stream;
    protected BitSet discardMask = new BitSet();

    public TokenStreamRewriteEngine(TokenStream tokenStream) {
        this(tokenStream, 1000);
    }

    public TokenStreamRewriteEngine(TokenStream tokenStream, int n) {
        this.stream = tokenStream;
        this.tokens = new ArrayList(n);
        this.programs = new HashMap();
        this.programs.put(DEFAULT_PROGRAM_NAME, new ArrayList(100));
        this.lastRewriteTokenIndexes = new HashMap();
    }

    public Token nextToken() throws TokenStreamException {
        TokenWithIndex tokenWithIndex;
        do {
            if ((tokenWithIndex = (TokenWithIndex)this.stream.nextToken()) == null) continue;
            tokenWithIndex.setIndex(this.index);
            if (tokenWithIndex.getType() != 1) {
                this.tokens.add(tokenWithIndex);
            }
            ++this.index;
        } while (tokenWithIndex != null && this.discardMask.member(tokenWithIndex.getType()));
        return tokenWithIndex;
    }

    public void rollback(int n) {
        this.rollback(DEFAULT_PROGRAM_NAME, n);
    }

    public void rollback(String string, int n) {
        List list = (List)this.programs.get(string);
        if (list != null) {
            this.programs.put(string, list.subList(0, n));
        }
    }

    public void deleteProgram() {
        this.deleteProgram(DEFAULT_PROGRAM_NAME);
    }

    public void deleteProgram(String string) {
        this.rollback(string, 0);
    }

    protected void addToSortedRewriteList(RewriteOperation rewriteOperation) {
        this.addToSortedRewriteList(DEFAULT_PROGRAM_NAME, rewriteOperation);
    }

    protected void addToSortedRewriteList(String string, RewriteOperation rewriteOperation) {
        Comparator comparator;
        List list = this.getProgram(string);
        int n = Collections.binarySearch(list, rewriteOperation, comparator = new Comparator(){

            public int compare(Object object, Object object2) {
                RewriteOperation rewriteOperation = (RewriteOperation)object;
                RewriteOperation rewriteOperation2 = (RewriteOperation)object2;
                if (rewriteOperation.index < rewriteOperation2.index) {
                    return -1;
                }
                if (rewriteOperation.index > rewriteOperation2.index) {
                    return 1;
                }
                return 0;
            }
        });
        if (n >= 0) {
            while (n >= 0) {
                RewriteOperation rewriteOperation2 = (RewriteOperation)list.get(n);
                if (rewriteOperation2.index < rewriteOperation.index) break;
                --n;
            }
            ++n;
            if (rewriteOperation instanceof ReplaceOp) {
                int n2;
                boolean bl = false;
                for (n2 = n; n2 < list.size(); ++n2) {
                    RewriteOperation rewriteOperation3 = (RewriteOperation)list.get(n);
                    if (rewriteOperation3.index != rewriteOperation.index) break;
                    if (!(rewriteOperation3 instanceof ReplaceOp)) continue;
                    list.set(n, rewriteOperation);
                    bl = true;
                    break;
                }
                if (!bl) {
                    list.add(n2, rewriteOperation);
                }
            } else {
                list.add(n, rewriteOperation);
            }
        } else {
            list.add(-n - 1, rewriteOperation);
        }
    }

    public void insertAfter(Token token, String string) {
        this.insertAfter(DEFAULT_PROGRAM_NAME, token, string);
    }

    public void insertAfter(int n, String string) {
        this.insertAfter(DEFAULT_PROGRAM_NAME, n, string);
    }

    public void insertAfter(String string, Token token, String string2) {
        this.insertAfter(string, ((TokenWithIndex)token).getIndex(), string2);
    }

    public void insertAfter(String string, int n, String string2) {
        this.insertBefore(string, n + 1, string2);
    }

    public void insertBefore(Token token, String string) {
        this.insertBefore(DEFAULT_PROGRAM_NAME, token, string);
    }

    public void insertBefore(int n, String string) {
        this.insertBefore(DEFAULT_PROGRAM_NAME, n, string);
    }

    public void insertBefore(String string, Token token, String string2) {
        this.insertBefore(string, ((TokenWithIndex)token).getIndex(), string2);
    }

    public void insertBefore(String string, int n, String string2) {
        this.addToSortedRewriteList(string, new InsertBeforeOp(n, string2));
    }

    public void replace(int n, String string) {
        this.replace(DEFAULT_PROGRAM_NAME, n, n, string);
    }

    public void replace(int n, int n2, String string) {
        this.replace(DEFAULT_PROGRAM_NAME, n, n2, string);
    }

    public void replace(Token token, String string) {
        this.replace(DEFAULT_PROGRAM_NAME, token, token, string);
    }

    public void replace(Token token, Token token2, String string) {
        this.replace(DEFAULT_PROGRAM_NAME, token, token2, string);
    }

    public void replace(String string, int n, int n2, String string2) {
        this.addToSortedRewriteList(new ReplaceOp(n, n2, string2));
    }

    public void replace(String string, Token token, Token token2, String string2) {
        this.replace(string, ((TokenWithIndex)token).getIndex(), ((TokenWithIndex)token2).getIndex(), string2);
    }

    public void delete(int n) {
        this.delete(DEFAULT_PROGRAM_NAME, n, n);
    }

    public void delete(int n, int n2) {
        this.delete(DEFAULT_PROGRAM_NAME, n, n2);
    }

    public void delete(Token token) {
        this.delete(DEFAULT_PROGRAM_NAME, token, token);
    }

    public void delete(Token token, Token token2) {
        this.delete(DEFAULT_PROGRAM_NAME, token, token2);
    }

    public void delete(String string, int n, int n2) {
        this.replace(string, n, n2, null);
    }

    public void delete(String string, Token token, Token token2) {
        this.replace(string, token, token2, null);
    }

    public void discard(int n) {
        this.discardMask.add(n);
    }

    public TokenWithIndex getToken(int n) {
        return (TokenWithIndex)this.tokens.get(n);
    }

    public int getTokenStreamSize() {
        return this.tokens.size();
    }

    public String toOriginalString() {
        return this.toOriginalString(0, this.getTokenStreamSize() - 1);
    }

    public String toOriginalString(int n, int n2) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = n; i >= 0 && i <= n2 && i < this.tokens.size(); ++i) {
            stringBuffer.append(this.getToken(i).getText());
        }
        return stringBuffer.toString();
    }

    public String toString() {
        return this.toString(0, this.getTokenStreamSize() - 1);
    }

    public String toString(String string) {
        return this.toString(string, 0, this.getTokenStreamSize() - 1);
    }

    public String toString(int n, int n2) {
        return this.toString(DEFAULT_PROGRAM_NAME, n, n2);
    }

    public String toString(String string, int n, int n2) {
        List list = (List)this.programs.get(string);
        if (list == null || list.size() == 0) {
            return this.toOriginalString(n, n2);
        }
        StringBuffer stringBuffer = new StringBuffer();
        int n3 = 0;
        int n4 = n;
        while (n4 >= 0 && n4 <= n2 && n4 < this.tokens.size()) {
            if (n3 < list.size()) {
                RewriteOperation rewriteOperation = (RewriteOperation)list.get(n3);
                while (rewriteOperation.index < n4 && n3 < list.size()) {
                    if (++n3 >= list.size()) continue;
                    rewriteOperation = (RewriteOperation)list.get(n3);
                }
                while (n4 == rewriteOperation.index && n3 < list.size()) {
                    n4 = rewriteOperation.execute(stringBuffer);
                    if (++n3 >= list.size()) continue;
                    rewriteOperation = (RewriteOperation)list.get(n3);
                }
            }
            if (n4 > n2) continue;
            stringBuffer.append(this.getToken(n4).getText());
            ++n4;
        }
        for (int i = n3; i < list.size(); ++i) {
            RewriteOperation rewriteOperation = (RewriteOperation)list.get(i);
            if (rewriteOperation.index < this.size()) continue;
            rewriteOperation.execute(stringBuffer);
        }
        return stringBuffer.toString();
    }

    public String toDebugString() {
        return this.toDebugString(0, this.getTokenStreamSize() - 1);
    }

    public String toDebugString(int n, int n2) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = n; i >= 0 && i <= n2 && i < this.tokens.size(); ++i) {
            stringBuffer.append(this.getToken(i));
        }
        return stringBuffer.toString();
    }

    public int getLastRewriteTokenIndex() {
        return this.getLastRewriteTokenIndex(DEFAULT_PROGRAM_NAME);
    }

    protected int getLastRewriteTokenIndex(String string) {
        Integer n = (Integer)this.lastRewriteTokenIndexes.get(string);
        if (n == null) {
            return -1;
        }
        return n;
    }

    protected void setLastRewriteTokenIndex(String string, int n) {
        this.lastRewriteTokenIndexes.put(string, new Integer(n));
    }

    protected List getProgram(String string) {
        List list = (List)this.programs.get(string);
        if (list == null) {
            list = this.initializeProgram(string);
        }
        return list;
    }

    private List initializeProgram(String string) {
        ArrayList arrayList = new ArrayList(100);
        this.programs.put(string, arrayList);
        return arrayList;
    }

    public int size() {
        return this.tokens.size();
    }

    public int index() {
        return this.index;
    }

    public String getEntireText() {
        return ASDebugStream.getEntireText(this.stream);
    }

    public TokenOffsetInfo getOffsetInfo(Token token) {
        return ASDebugStream.getOffsetInfo(this.stream, token);
    }

    static class DeleteOp
    extends ReplaceOp {
        public DeleteOp(int n, int n2) {
            super(n, n2, null);
        }
    }

    static class ReplaceOp
    extends RewriteOperation {
        protected int lastIndex;

        public ReplaceOp(int n, int n2, String string) {
            super(n, string);
            this.lastIndex = n2;
        }

        public int execute(StringBuffer stringBuffer) {
            if (this.text != null) {
                stringBuffer.append(this.text);
            }
            return this.lastIndex + 1;
        }
    }

    static class InsertBeforeOp
    extends RewriteOperation {
        public InsertBeforeOp(int n, String string) {
            super(n, string);
        }

        public int execute(StringBuffer stringBuffer) {
            stringBuffer.append(this.text);
            return this.index;
        }
    }

    static class RewriteOperation {
        protected int index;
        protected String text;

        protected RewriteOperation(int n, String string) {
            this.index = n;
            this.text = string;
        }

        public int execute(StringBuffer stringBuffer) {
            return this.index;
        }

        public String toString() {
            String string = this.getClass().getName();
            int n = string.indexOf(36);
            string = string.substring(n + 1, string.length());
            return string + "@" + this.index + '\"' + this.text + '\"';
        }
    }
}

