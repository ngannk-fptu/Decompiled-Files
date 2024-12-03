/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.macro.query;

import com.atlassian.confluence.macro.query.BooleanQueryFactory;
import com.atlassian.confluence.macro.query.SearchQueryInterpreter;
import com.atlassian.confluence.macro.query.SearchQueryInterpreterException;
import com.atlassian.confluence.macro.query.SearchQueryParserException;
import com.atlassian.confluence.search.v2.SearchQuery;
import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

public class SearchQueryParser {
    private static final char OPEN_PAREN_CHAR = '(';
    private static final char CLOSE_PAREN_CHAR = ')';
    private static final char REQ_CHAR = '+';
    private static final char EXCLUDED_CHAR = '-';
    private static final char QUOTE_CHAR = '\"';
    private static final char ESCAPE_CHAR = '\\';
    private static final char COMMA_CHAR = ',';
    private static final char SEMICOLON_CHAR = ';';
    private static final BitSet WHITESPACE = new BitSet();
    private static final BitSet SEPARATOR = new BitSet();
    private static final BitSet RESERVED = new BitSet();
    private static final BitSet UNQUOTED = new BitSet();
    private static final BitSet QUOTED = new BitSet();
    private BitSet separator = new BitSet();
    private BitSet unquoted;

    public SearchQueryParser() {
        this(false);
    }

    public SearchQueryParser(boolean whitespaceSeparator) {
        this.separator.or(SEPARATOR);
        this.unquoted = new BitSet();
        this.unquoted.or(UNQUOTED);
        if (whitespaceSeparator) {
            this.separator.or(WHITESPACE);
            this.unquoted.andNot(WHITESPACE);
        }
    }

    public BooleanQueryFactory parse(String filterValue, SearchQueryInterpreter criterionInterpreter) throws SearchQueryParserException {
        TokenIterator i = new TokenIterator(filterValue.trim());
        BooleanQueryFactory criterion = this.parseList(i, criterionInterpreter);
        if (!i.atEnd()) {
            throw new SearchQueryParserException("Unexpected values at end: '" + i.getSequence(TokenIterator.ALL_CHARS) + "'");
        }
        return criterion;
    }

    private BooleanQueryFactory parseList(TokenIterator i, SearchQueryInterpreter criterionInterpreter) throws SearchQueryParserException {
        HashSet<SearchQuery> optCriteria = new HashSet<SearchQuery>();
        HashSet<SearchQuery> reqCriteria = new HashSet<SearchQuery>();
        HashSet<SearchQuery> excludedCriteria = new HashSet<SearchQuery>();
        boolean continuing = true;
        while (continuing && !i.atEnd()) {
            this.parseListItem(i, criterionInterpreter, optCriteria, reqCriteria, excludedCriteria);
            continuing = i.getSequence(this.separator, 1) != null;
        }
        return new BooleanQueryFactory(optCriteria, reqCriteria, excludedCriteria);
    }

    private void parseListItem(TokenIterator i, SearchQueryInterpreter criterionInterpreter, Set<SearchQuery> optCriteria, Set<SearchQuery> reqCriteria, Set<SearchQuery> excludedCriteria) throws SearchQueryParserException {
        i.getSequence(WHITESPACE);
        BooleanQueryFactory result = null;
        if (i.getToken(43) != null) {
            if (i.matchToken(40)) {
                result = this.parseGroup(i, criterionInterpreter);
            } else {
                reqCriteria.add(this.parseCriterion(i, criterionInterpreter));
            }
        } else if (i.getToken(45) != null) {
            if (i.matchToken(40)) {
                result = this.parseGroup(i, criterionInterpreter);
            } else {
                excludedCriteria.add(this.parseCriterion(i, criterionInterpreter));
            }
        } else if (i.matchToken(40)) {
            result = this.parseGroup(i, criterionInterpreter);
        } else {
            optCriteria.add(this.parseCriterion(i, criterionInterpreter));
        }
        if (result != null) {
            reqCriteria.addAll(result.getMust());
            optCriteria.addAll(result.getShould());
            excludedCriteria.addAll(result.getMustNot());
        }
    }

    private BooleanQueryFactory parseGroup(TokenIterator i, SearchQueryInterpreter criterionInterpreter) throws SearchQueryParserException {
        i.getToken(40);
        BooleanQueryFactory group = this.parseList(i, criterionInterpreter);
        if (i.getToken(41) == null) {
            throw new SearchQueryParserException("Expected ')'");
        }
        return group;
    }

    private SearchQuery parseCriterion(TokenIterator i, SearchQueryInterpreter criterionInterpreter) throws SearchQueryParserException {
        StringBuffer buff = new StringBuffer();
        boolean done = false;
        while (!done) {
            if (i.getToken(34) != null) {
                this.readQuotedCriterion(i, buff);
                continue;
            }
            CharSequence value = i.getSequence(this.unquoted, 1);
            if (value != null) {
                buff.append(value);
                continue;
            }
            done = true;
        }
        try {
            return criterionInterpreter.createSearchQuery(buff.toString());
        }
        catch (SearchQueryInterpreterException sqie) {
            throw new SearchQueryParserException("error parsing search query", sqie);
        }
    }

    private void readQuotedCriterion(TokenIterator i, StringBuffer buff) throws SearchQueryParserException {
        while (!i.atEnd() && !i.matchToken(34)) {
            buff.append(i.getSequence(QUOTED));
            if (i.getToken(92) == null) continue;
            CharSequence escaped = i.getSequence(TokenIterator.ALL_CHARS, 1, 1);
            if (escaped == null) {
                throw new SearchQueryParserException("Expected a character to escape after '\\'");
            }
            buff.append(escaped);
        }
        if (i.getToken(34) == null) {
            throw new SearchQueryParserException("Expected '\"'");
        }
    }

    static {
        WHITESPACE.set(32);
        WHITESPACE.set(9);
        WHITESPACE.set(13);
        WHITESPACE.set(10);
        SEPARATOR.set(44);
        SEPARATOR.set(59);
        RESERVED.set(40);
        RESERVED.set(41);
        RESERVED.set(43);
        RESERVED.set(45);
        RESERVED.set(34);
        UNQUOTED.set(0, 65535, true);
        UNQUOTED.andNot(SEPARATOR);
        UNQUOTED.andNot(RESERVED);
        UNQUOTED.set(45);
        QUOTED.set(0, 65535, true);
        QUOTED.clear(34);
        QUOTED.clear(92);
    }

    private static class ABitSet
    extends BitSet {
        boolean lockable = false;
        boolean locked = false;

        public ABitSet() {
            this(false);
        }

        public ABitSet(boolean lockable) {
            this.lockable = lockable;
        }

        public ABitSet(int nbits, boolean lockable) {
            super(nbits);
            this.lockable = lockable;
        }

        public boolean isLockable() {
            return this.lockable;
        }

        public void lock() {
            if (!this.lockable) {
                throw new UnsupportedOperationException("This ABitSet is not lockable.");
            }
            this.locked = true;
        }

        public void set(String chars) {
            if (chars != null) {
                int length = chars.length();
                for (int i = 0; i < length; ++i) {
                    this.set(chars.charAt(i));
                }
            }
        }

        public void set(int[] bits) {
            if (bits != null) {
                for (int bit : bits) {
                    this.set(bit);
                }
            }
        }

        public void clear(int[] bits) {
            if (bits != null) {
                for (int bit : bits) {
                    this.clear(bit);
                }
            }
        }

        public void clear(String chars) {
            if (chars != null) {
                int length = chars.length();
                for (int i = 0; i < length; ++i) {
                    this.clear(chars.charAt(i));
                }
            }
        }

        public void setRange(int from, int to) {
            if (from > to) {
                int temp = from;
                from = to;
                to = temp;
            }
            for (int i = to; i >= from; --i) {
                this.set(i);
            }
        }

        public void clearRange(int from, int to) {
            if (to < from) {
                int temp = from;
                from = to;
                to = temp;
            }
            for (int i = from; i <= to; ++i) {
                this.clear(i);
            }
        }

        @Override
        public void set(int bitIndex) {
            this.checkLock();
            super.set(bitIndex);
        }

        @Override
        public void clear(int bitIndex) {
            this.checkLock();
            super.clear(bitIndex);
        }

        private void checkLock() {
            if (this.locked) {
                throw new UnsupportedOperationException("The bit set cannot be modified when locked");
            }
        }
    }

    private static class TokenIterator {
        public static final BitSet ALL_CHARS = new ABitSet(65535, true);
        public static final int UNBOUNDED = Integer.MAX_VALUE;
        protected CharacterIterator chars;
        protected int lastIndex = 0;

        public TokenIterator(Reader reader) throws IOException {
            BufferedReader in = reader instanceof BufferedReader ? (BufferedReader)reader : new BufferedReader(reader);
            CharArrayWriter writer = new CharArrayWriter();
            TokenIterator.pipe(in, writer);
            this.chars = new StringCharacterIterator(writer.toString());
        }

        public TokenIterator(CharacterIterator chars) {
            this.chars = chars;
            this.lastIndex = chars.getBeginIndex();
        }

        public TokenIterator(String string) {
            this(new StringCharacterIterator(string));
        }

        public CharSequence getSequence(BitSet charSet, String terminator) {
            StringBuffer buff = new StringBuffer();
            while (!this.matchToken(terminator)) {
                char ch = this.chars.current();
                if (ch == '\uffff' || !charSet.get(ch)) {
                    return null;
                }
                buff.append(ch);
                this.chars.next();
            }
            return buff;
        }

        public CharSequence getSequence(BitSet charSet) {
            return this.getSequence(charSet, 0, Integer.MAX_VALUE);
        }

        public CharSequence getSequence(BitSet charSet, int min) {
            return this.getSequence(charSet, min, Integer.MAX_VALUE);
        }

        public CharSequence getSequence(BitSet charSet, int min, int max) {
            char ch;
            int count;
            StringBuffer buff = new StringBuffer();
            int mark = this.chars.getIndex();
            for (count = 0; count < max && (ch = this.chars.current()) != '\uffff' && charSet.get(ch); ++count) {
                buff.append(ch);
                this.chars.next();
            }
            if (count < min) {
                this.chars.setIndex(mark);
                return null;
            }
            this.lastIndex = mark;
            return buff;
        }

        public boolean matchToken(String token) {
            int mark = this.chars.getIndex();
            boolean match = this.getToken(token) != null;
            this.chars.setIndex(mark);
            return match;
        }

        public boolean matchToken(int ch) {
            char read = this.chars.current();
            return read == ch;
        }

        public CharSequence getToken(String token) {
            return this.getToken(token, false);
        }

        public CharSequence getToken(String token, boolean ignoreCase) {
            int mark = this.chars.getIndex();
            int length = token.length();
            StringBuffer buff = new StringBuffer();
            for (int i = 0; i < length; ++i) {
                int tok = this.getChar(token.charAt(i), ignoreCase);
                if (tok == 65535) {
                    this.chars.setIndex(mark);
                    return null;
                }
                buff.append((char)tok);
            }
            this.lastIndex = mark;
            return buff;
        }

        public CharSequence getToken(int ch) {
            return this.getToken(ch, false);
        }

        public CharSequence getToken(int ch, boolean ignoreCase) {
            int mark = this.chars.getIndex();
            int tok = this.getChar(ch, ignoreCase);
            if (tok == 65535) {
                this.chars.setIndex(mark);
                return null;
            }
            this.lastIndex = mark;
            return String.valueOf((char)tok);
        }

        private int getChar(int ch, boolean ignoreCase) {
            char ch_in = this.chars.current();
            if (ignoreCase && Character.toLowerCase((char)ch) != Character.toLowerCase(ch_in) || !ignoreCase && ch != ch_in) {
                return 65535;
            }
            this.chars.next();
            return ch_in;
        }

        public void rewind() {
            this.chars.setIndex(this.lastIndex);
        }

        public boolean atEnd() {
            return this.chars.getIndex() == this.chars.getEndIndex();
        }

        public boolean atBeginning() {
            return this.chars.getIndex() == this.chars.getBeginIndex();
        }

        public String toString() {
            return this.chars.toString();
        }

        public static int pipe(Reader in, Writer out) throws IOException {
            int size;
            char[] buff = new char[1024];
            int count = 0;
            while ((size = in.read(buff)) != -1) {
                out.write(buff, 0, size);
                count += size;
            }
            return count;
        }

        static {
            ((ABitSet)ALL_CHARS).setRange(0, 65535);
            ((ABitSet)ALL_CHARS).lock();
        }
    }
}

