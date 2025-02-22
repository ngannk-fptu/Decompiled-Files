/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.scanner;

import com.hazelcast.org.snakeyaml.engine.v2.common.Anchor;
import com.hazelcast.org.snakeyaml.engine.v2.common.ArrayStack;
import com.hazelcast.org.snakeyaml.engine.v2.common.CharConstants;
import com.hazelcast.org.snakeyaml.engine.v2.common.ScalarStyle;
import com.hazelcast.org.snakeyaml.engine.v2.common.UriEncoder;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.Mark;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.ScannerException;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.YamlEngineException;
import com.hazelcast.org.snakeyaml.engine.v2.scanner.Scanner;
import com.hazelcast.org.snakeyaml.engine.v2.scanner.SimpleKey;
import com.hazelcast.org.snakeyaml.engine.v2.scanner.StreamReader;
import com.hazelcast.org.snakeyaml.engine.v2.tokens.AliasToken;
import com.hazelcast.org.snakeyaml.engine.v2.tokens.AnchorToken;
import com.hazelcast.org.snakeyaml.engine.v2.tokens.BlockEndToken;
import com.hazelcast.org.snakeyaml.engine.v2.tokens.BlockEntryToken;
import com.hazelcast.org.snakeyaml.engine.v2.tokens.BlockMappingStartToken;
import com.hazelcast.org.snakeyaml.engine.v2.tokens.BlockSequenceStartToken;
import com.hazelcast.org.snakeyaml.engine.v2.tokens.DirectiveToken;
import com.hazelcast.org.snakeyaml.engine.v2.tokens.DocumentEndToken;
import com.hazelcast.org.snakeyaml.engine.v2.tokens.DocumentStartToken;
import com.hazelcast.org.snakeyaml.engine.v2.tokens.FlowEntryToken;
import com.hazelcast.org.snakeyaml.engine.v2.tokens.FlowMappingEndToken;
import com.hazelcast.org.snakeyaml.engine.v2.tokens.FlowMappingStartToken;
import com.hazelcast.org.snakeyaml.engine.v2.tokens.FlowSequenceEndToken;
import com.hazelcast.org.snakeyaml.engine.v2.tokens.FlowSequenceStartToken;
import com.hazelcast.org.snakeyaml.engine.v2.tokens.KeyToken;
import com.hazelcast.org.snakeyaml.engine.v2.tokens.ScalarToken;
import com.hazelcast.org.snakeyaml.engine.v2.tokens.StreamEndToken;
import com.hazelcast.org.snakeyaml.engine.v2.tokens.StreamStartToken;
import com.hazelcast.org.snakeyaml.engine.v2.tokens.TagToken;
import com.hazelcast.org.snakeyaml.engine.v2.tokens.TagTuple;
import com.hazelcast.org.snakeyaml.engine.v2.tokens.Token;
import com.hazelcast.org.snakeyaml.engine.v2.tokens.ValueToken;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.regex.Pattern;

public final class ScannerImpl
implements Scanner {
    private static final String DIRECTIVE_PREFIX = "while scanning a directive";
    private static final String EXPECTED_ALPHA_ERROR_PREFIX = "expected alphabetic or numeric character, but found ";
    private static final String SCANNING_SCALAR = "while scanning a block scalar";
    private static final String SCANNING_PREFIX = "while scanning a ";
    private static final Pattern NOT_HEXA = Pattern.compile("[^0-9A-Fa-f]");
    private final StreamReader reader;
    private boolean done = false;
    private int flowLevel = 0;
    private List<Token> tokens;
    private int tokensTaken = 0;
    private int indent = -1;
    private ArrayStack<Integer> indents;
    private boolean allowSimpleKey = true;
    private Map<Integer, SimpleKey> possibleSimpleKeys;

    public ScannerImpl(StreamReader reader) {
        this.reader = reader;
        this.tokens = new ArrayList<Token>(100);
        this.indents = new ArrayStack(10);
        this.possibleSimpleKeys = new LinkedHashMap<Integer, SimpleKey>();
        this.fetchStreamStart();
    }

    @Override
    public boolean checkToken(Token.ID ... choices) {
        while (this.needMoreTokens()) {
            this.fetchMoreTokens();
        }
        if (!this.tokens.isEmpty()) {
            if (choices.length == 0) {
                return true;
            }
            Token firstToken = this.tokens.get(0);
            Token.ID first = firstToken.getTokenId();
            for (int i = 0; i < choices.length; ++i) {
                if (first != choices[i]) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public Token peekToken() {
        while (this.needMoreTokens()) {
            this.fetchMoreTokens();
        }
        return this.tokens.get(0);
    }

    @Override
    public boolean hasNext() {
        return this.checkToken(new Token.ID[0]);
    }

    @Override
    public Token next() {
        ++this.tokensTaken;
        if (this.tokens.isEmpty()) {
            throw new NoSuchElementException("No more Tokens found.");
        }
        return this.tokens.remove(0);
    }

    private boolean needMoreTokens() {
        if (this.done) {
            return false;
        }
        if (this.tokens.isEmpty()) {
            return true;
        }
        this.stalePossibleSimpleKeys();
        return this.nextPossibleSimpleKey() == this.tokensTaken;
    }

    private void fetchMoreTokens() {
        this.scanToNextToken();
        this.stalePossibleSimpleKeys();
        this.unwindIndent(this.reader.getColumn());
        int c = this.reader.peek();
        switch (c) {
            case 0: {
                this.fetchStreamEnd();
                return;
            }
            case 37: {
                if (!this.checkDirective()) break;
                this.fetchDirective();
                return;
            }
            case 45: {
                if (this.checkDocumentStart()) {
                    this.fetchDocumentStart();
                    return;
                }
                if (!this.checkBlockEntry()) break;
                this.fetchBlockEntry();
                return;
            }
            case 46: {
                if (!this.checkDocumentEnd()) break;
                this.fetchDocumentEnd();
                return;
            }
            case 91: {
                this.fetchFlowSequenceStart();
                return;
            }
            case 123: {
                this.fetchFlowMappingStart();
                return;
            }
            case 93: {
                this.fetchFlowSequenceEnd();
                return;
            }
            case 125: {
                this.fetchFlowMappingEnd();
                return;
            }
            case 44: {
                this.fetchFlowEntry();
                return;
            }
            case 63: {
                if (!this.checkKey()) break;
                this.fetchKey();
                return;
            }
            case 58: {
                if (!this.checkValue()) break;
                this.fetchValue();
                return;
            }
            case 42: {
                this.fetchAlias();
                return;
            }
            case 38: {
                this.fetchAnchor();
                return;
            }
            case 33: {
                this.fetchTag();
                return;
            }
            case 124: {
                if (this.flowLevel != 0) break;
                this.fetchLiteral();
                return;
            }
            case 62: {
                if (this.flowLevel != 0) break;
                this.fetchFolded();
                return;
            }
            case 39: {
                this.fetchSingle();
                return;
            }
            case 34: {
                this.fetchDouble();
                return;
            }
        }
        if (this.checkPlain()) {
            this.fetchPlain();
            return;
        }
        String chRepresentation = String.valueOf(Character.toChars(c));
        if (CharConstants.ESCAPES.containsKey(Character.valueOf((char)c))) {
            chRepresentation = "\\" + CharConstants.ESCAPES.get(Character.valueOf((char)c));
        }
        if (c == 9) {
            chRepresentation = chRepresentation + "(TAB)";
        }
        String text = String.format("found character '%s' that cannot start any token. (Do not use %s for indentation)", chRepresentation, chRepresentation);
        throw new ScannerException("while scanning for the next token", Optional.empty(), text, this.reader.getMark());
    }

    private int nextPossibleSimpleKey() {
        if (!this.possibleSimpleKeys.isEmpty()) {
            return this.possibleSimpleKeys.values().iterator().next().getTokenNumber();
        }
        return -1;
    }

    private void stalePossibleSimpleKeys() {
        if (!this.possibleSimpleKeys.isEmpty()) {
            Iterator<SimpleKey> iterator = this.possibleSimpleKeys.values().iterator();
            while (iterator.hasNext()) {
                SimpleKey key = iterator.next();
                if (key.getLine() == this.reader.getLine() && this.reader.getIndex() - key.getIndex() <= 1024) continue;
                if (key.isRequired()) {
                    throw new ScannerException("while scanning a simple key", key.getMark(), "could not find expected ':'", this.reader.getMark());
                }
                iterator.remove();
            }
        }
    }

    private void savePossibleSimpleKey() {
        boolean required;
        boolean bl = required = this.flowLevel == 0 && this.indent == this.reader.getColumn();
        if (!this.allowSimpleKey && required) {
            throw new YamlEngineException("A simple key is required only if it is the first token in the current line");
        }
        if (this.allowSimpleKey) {
            this.removePossibleSimpleKey();
            int tokenNumber = this.tokensTaken + this.tokens.size();
            SimpleKey key = new SimpleKey(tokenNumber, required, this.reader.getIndex(), this.reader.getLine(), this.reader.getColumn(), this.reader.getMark());
            this.possibleSimpleKeys.put(this.flowLevel, key);
        }
    }

    private void removePossibleSimpleKey() {
        SimpleKey key = this.possibleSimpleKeys.remove(this.flowLevel);
        if (key != null && key.isRequired()) {
            throw new ScannerException("while scanning a simple key", key.getMark(), "could not find expected ':'", this.reader.getMark());
        }
    }

    private void unwindIndent(int col) {
        if (this.flowLevel != 0) {
            return;
        }
        while (this.indent > col) {
            Optional<Mark> mark = this.reader.getMark();
            this.indent = this.indents.pop();
            this.tokens.add(new BlockEndToken(mark, mark));
        }
    }

    private boolean addIndent(int column) {
        if (this.indent < column) {
            this.indents.push(this.indent);
            this.indent = column;
            return true;
        }
        return false;
    }

    private void fetchStreamStart() {
        Optional<Mark> mark = this.reader.getMark();
        StreamStartToken token = new StreamStartToken(mark, mark);
        this.tokens.add(token);
    }

    private void fetchStreamEnd() {
        this.unwindIndent(-1);
        this.removePossibleSimpleKey();
        this.allowSimpleKey = false;
        this.possibleSimpleKeys.clear();
        Optional<Mark> mark = this.reader.getMark();
        StreamEndToken token = new StreamEndToken(mark, mark);
        this.tokens.add(token);
        this.done = true;
    }

    private void fetchDirective() {
        this.unwindIndent(-1);
        this.removePossibleSimpleKey();
        this.allowSimpleKey = false;
        Token tok = this.scanDirective();
        this.tokens.add(tok);
    }

    private void fetchDocumentStart() {
        this.fetchDocumentIndicator(true);
    }

    private void fetchDocumentEnd() {
        this.fetchDocumentIndicator(false);
    }

    private void fetchDocumentIndicator(boolean isDocumentStart) {
        this.unwindIndent(-1);
        this.removePossibleSimpleKey();
        this.allowSimpleKey = false;
        Optional<Mark> startMark = this.reader.getMark();
        this.reader.forward(3);
        Optional<Mark> endMark = this.reader.getMark();
        Token token = isDocumentStart ? new DocumentStartToken(startMark, endMark) : new DocumentEndToken(startMark, endMark);
        this.tokens.add(token);
    }

    private void fetchFlowSequenceStart() {
        this.fetchFlowCollectionStart(false);
    }

    private void fetchFlowMappingStart() {
        this.fetchFlowCollectionStart(true);
    }

    private void fetchFlowCollectionStart(boolean isMappingStart) {
        this.savePossibleSimpleKey();
        ++this.flowLevel;
        this.allowSimpleKey = true;
        Optional<Mark> startMark = this.reader.getMark();
        this.reader.forward(1);
        Optional<Mark> endMark = this.reader.getMark();
        Token token = isMappingStart ? new FlowMappingStartToken(startMark, endMark) : new FlowSequenceStartToken(startMark, endMark);
        this.tokens.add(token);
    }

    private void fetchFlowSequenceEnd() {
        this.fetchFlowCollectionEnd(false);
    }

    private void fetchFlowMappingEnd() {
        this.fetchFlowCollectionEnd(true);
    }

    private void fetchFlowCollectionEnd(boolean isMappingEnd) {
        this.removePossibleSimpleKey();
        --this.flowLevel;
        this.allowSimpleKey = false;
        Optional<Mark> startMark = this.reader.getMark();
        this.reader.forward();
        Optional<Mark> endMark = this.reader.getMark();
        Token token = isMappingEnd ? new FlowMappingEndToken(startMark, endMark) : new FlowSequenceEndToken(startMark, endMark);
        this.tokens.add(token);
    }

    private void fetchFlowEntry() {
        this.allowSimpleKey = true;
        this.removePossibleSimpleKey();
        Optional<Mark> startMark = this.reader.getMark();
        this.reader.forward();
        Optional<Mark> endMark = this.reader.getMark();
        FlowEntryToken token = new FlowEntryToken(startMark, endMark);
        this.tokens.add(token);
    }

    private void fetchBlockEntry() {
        if (this.flowLevel == 0) {
            if (!this.allowSimpleKey) {
                throw new ScannerException("", Optional.empty(), "sequence entries are not allowed here", this.reader.getMark());
            }
            if (this.addIndent(this.reader.getColumn())) {
                Optional<Mark> mark = this.reader.getMark();
                this.tokens.add(new BlockSequenceStartToken(mark, mark));
            }
        }
        this.allowSimpleKey = true;
        this.removePossibleSimpleKey();
        Optional<Mark> startMark = this.reader.getMark();
        this.reader.forward();
        Optional<Mark> endMark = this.reader.getMark();
        BlockEntryToken token = new BlockEntryToken(startMark, endMark);
        this.tokens.add(token);
    }

    private void fetchKey() {
        if (this.flowLevel == 0) {
            if (!this.allowSimpleKey) {
                throw new ScannerException("mapping keys are not allowed here", this.reader.getMark());
            }
            if (this.addIndent(this.reader.getColumn())) {
                Optional<Mark> mark = this.reader.getMark();
                this.tokens.add(new BlockMappingStartToken(mark, mark));
            }
        }
        this.allowSimpleKey = this.flowLevel == 0;
        this.removePossibleSimpleKey();
        Optional<Mark> startMark = this.reader.getMark();
        this.reader.forward();
        Optional<Mark> endMark = this.reader.getMark();
        KeyToken token = new KeyToken(startMark, endMark);
        this.tokens.add(token);
    }

    private void fetchValue() {
        SimpleKey key = this.possibleSimpleKeys.remove(this.flowLevel);
        if (key != null) {
            this.tokens.add(key.getTokenNumber() - this.tokensTaken, new KeyToken(key.getMark(), key.getMark()));
            if (this.flowLevel == 0 && this.addIndent(key.getColumn())) {
                this.tokens.add(key.getTokenNumber() - this.tokensTaken, new BlockMappingStartToken(key.getMark(), key.getMark()));
            }
            this.allowSimpleKey = false;
        } else {
            if (this.flowLevel == 0 && !this.allowSimpleKey) {
                throw new ScannerException("mapping values are not allowed here", this.reader.getMark());
            }
            if (this.flowLevel == 0 && this.addIndent(this.reader.getColumn())) {
                Optional<Mark> mark = this.reader.getMark();
                this.tokens.add(new BlockMappingStartToken(mark, mark));
            }
            this.allowSimpleKey = this.flowLevel == 0;
            this.removePossibleSimpleKey();
        }
        Optional<Mark> startMark = this.reader.getMark();
        this.reader.forward();
        Optional<Mark> endMark = this.reader.getMark();
        ValueToken token = new ValueToken(startMark, endMark);
        this.tokens.add(token);
    }

    private void fetchAlias() {
        this.savePossibleSimpleKey();
        this.allowSimpleKey = false;
        Token tok = this.scanAnchor(false);
        this.tokens.add(tok);
    }

    private void fetchAnchor() {
        this.savePossibleSimpleKey();
        this.allowSimpleKey = false;
        Token tok = this.scanAnchor(true);
        this.tokens.add(tok);
    }

    private void fetchTag() {
        this.savePossibleSimpleKey();
        this.allowSimpleKey = false;
        Token tok = this.scanTag();
        this.tokens.add(tok);
    }

    private void fetchLiteral() {
        this.fetchBlockScalar(ScalarStyle.LITERAL);
    }

    private void fetchFolded() {
        this.fetchBlockScalar(ScalarStyle.FOLDED);
    }

    private void fetchBlockScalar(ScalarStyle style) {
        this.allowSimpleKey = true;
        this.removePossibleSimpleKey();
        Token tok = this.scanBlockScalar(style);
        this.tokens.add(tok);
    }

    private void fetchSingle() {
        this.fetchFlowScalar(ScalarStyle.SINGLE_QUOTED);
    }

    private void fetchDouble() {
        this.fetchFlowScalar(ScalarStyle.DOUBLE_QUOTED);
    }

    private void fetchFlowScalar(ScalarStyle style) {
        this.savePossibleSimpleKey();
        this.allowSimpleKey = false;
        Token tok = this.scanFlowScalar(style);
        this.tokens.add(tok);
    }

    private void fetchPlain() {
        this.savePossibleSimpleKey();
        this.allowSimpleKey = false;
        Token tok = this.scanPlain();
        this.tokens.add(tok);
    }

    private boolean checkDirective() {
        return this.reader.getColumn() == 0;
    }

    private boolean checkDocumentStart() {
        if (this.reader.getColumn() == 0) {
            return "---".equals(this.reader.prefix(3)) && CharConstants.NULL_BL_T_LINEBR.has(this.reader.peek(3));
        }
        return false;
    }

    private boolean checkDocumentEnd() {
        if (this.reader.getColumn() == 0) {
            return "...".equals(this.reader.prefix(3)) && CharConstants.NULL_BL_T_LINEBR.has(this.reader.peek(3));
        }
        return false;
    }

    private boolean checkBlockEntry() {
        return CharConstants.NULL_BL_T_LINEBR.has(this.reader.peek(1));
    }

    private boolean checkKey() {
        if (this.flowLevel != 0) {
            return true;
        }
        return CharConstants.NULL_BL_T_LINEBR.has(this.reader.peek(1));
    }

    private boolean checkValue() {
        if (this.flowLevel != 0) {
            return true;
        }
        return CharConstants.NULL_BL_T_LINEBR.has(this.reader.peek(1));
    }

    private boolean checkPlain() {
        int c = this.reader.peek();
        return CharConstants.NULL_BL_T_LINEBR.hasNo(c, "-?:,[]{}#&*!|>'\"%@`") || CharConstants.NULL_BL_T_LINEBR.hasNo(this.reader.peek(1)) && (c == 45 || this.flowLevel == 0 && "?:".indexOf(c) != -1);
    }

    private void scanToNextToken() {
        if (this.reader.getIndex() == 0 && this.reader.peek() == 65279) {
            this.reader.forward();
        }
        boolean found = false;
        while (!found) {
            int ff = 0;
            while (this.reader.peek(ff) == 32 || this.reader.peek(ff) == 9) {
                ++ff;
            }
            if (ff > 0) {
                this.reader.forward(ff);
            }
            if (this.reader.peek() == 35) {
                ff = 0;
                while (CharConstants.NULL_OR_LINEBR.hasNo(this.reader.peek(ff))) {
                    ++ff;
                }
                if (ff > 0) {
                    this.reader.forward(ff);
                }
            }
            if (this.scanLineBreak().length() != 0) {
                if (this.flowLevel != 0) continue;
                this.allowSimpleKey = true;
                continue;
            }
            found = true;
        }
    }

    private Token scanDirective() {
        Optional<Mark> endMark;
        Optional<List<Object>> value;
        Optional<Mark> startMark = this.reader.getMark();
        this.reader.forward();
        String name = this.scanDirectiveName(startMark);
        if ("YAML".equals(name)) {
            value = Optional.of(this.scanYamlDirectiveValue(startMark));
            endMark = this.reader.getMark();
        } else if ("TAG".equals(name)) {
            value = Optional.of(this.scanTagDirectiveValue(startMark));
            endMark = this.reader.getMark();
        } else {
            endMark = this.reader.getMark();
            int ff = 0;
            while (CharConstants.NULL_OR_LINEBR.hasNo(this.reader.peek(ff))) {
                ++ff;
            }
            if (ff > 0) {
                this.reader.forward(ff);
            }
            value = Optional.empty();
        }
        this.scanDirectiveIgnoredLine(startMark);
        return new DirectiveToken(name, value, startMark, endMark);
    }

    private String scanDirectiveName(Optional<Mark> startMark) {
        int length = 0;
        int c = this.reader.peek(length);
        while (CharConstants.ALPHA.has(c)) {
            c = this.reader.peek(++length);
        }
        if (length == 0) {
            String s = String.valueOf(Character.toChars(c));
            throw new ScannerException(DIRECTIVE_PREFIX, startMark, EXPECTED_ALPHA_ERROR_PREFIX + s + "(" + c + ")", this.reader.getMark());
        }
        String value = this.reader.prefixForward(length);
        c = this.reader.peek();
        if (CharConstants.NULL_BL_LINEBR.hasNo(c)) {
            String s = String.valueOf(Character.toChars(c));
            throw new ScannerException(DIRECTIVE_PREFIX, startMark, EXPECTED_ALPHA_ERROR_PREFIX + s + "(" + c + ")", this.reader.getMark());
        }
        return value;
    }

    private List<Integer> scanYamlDirectiveValue(Optional<Mark> startMark) {
        while (this.reader.peek() == 32) {
            this.reader.forward();
        }
        Integer major = this.scanYamlDirectiveNumber(startMark);
        int c = this.reader.peek();
        if (c != 46) {
            String s = String.valueOf(Character.toChars(c));
            throw new ScannerException(DIRECTIVE_PREFIX, startMark, "expected a digit or '.', but found " + s + "(" + c + ")", this.reader.getMark());
        }
        this.reader.forward();
        Integer minor = this.scanYamlDirectiveNumber(startMark);
        c = this.reader.peek();
        if (CharConstants.NULL_BL_LINEBR.hasNo(c)) {
            String s = String.valueOf(Character.toChars(c));
            throw new ScannerException(DIRECTIVE_PREFIX, startMark, "expected a digit or ' ', but found " + s + "(" + c + ")", this.reader.getMark());
        }
        ArrayList<Integer> result = new ArrayList<Integer>(2);
        result.add(major);
        result.add(minor);
        return result;
    }

    private Integer scanYamlDirectiveNumber(Optional<Mark> startMark) {
        int c = this.reader.peek();
        if (!Character.isDigit(c)) {
            String s = String.valueOf(Character.toChars(c));
            throw new ScannerException(DIRECTIVE_PREFIX, startMark, "expected a digit, but found " + s + "(" + c + ")", this.reader.getMark());
        }
        int length = 0;
        while (Character.isDigit(this.reader.peek(length))) {
            ++length;
        }
        return Integer.parseInt(this.reader.prefixForward(length));
    }

    private List<String> scanTagDirectiveValue(Optional<Mark> startMark) {
        while (this.reader.peek() == 32) {
            this.reader.forward();
        }
        String handle = this.scanTagDirectiveHandle(startMark);
        while (this.reader.peek() == 32) {
            this.reader.forward();
        }
        String prefix = this.scanTagDirectivePrefix(startMark);
        ArrayList<String> result = new ArrayList<String>(2);
        result.add(handle);
        result.add(prefix);
        return result;
    }

    private String scanTagDirectiveHandle(Optional<Mark> startMark) {
        String value = this.scanTagHandle("directive", startMark);
        int c = this.reader.peek();
        if (c != 32) {
            String s = String.valueOf(Character.toChars(c));
            throw new ScannerException(DIRECTIVE_PREFIX, startMark, "expected ' ', but found " + s + "(" + c + ")", this.reader.getMark());
        }
        return value;
    }

    private String scanTagDirectivePrefix(Optional<Mark> startMark) {
        String value = this.scanTagUri("directive", startMark);
        int c = this.reader.peek();
        if (CharConstants.NULL_BL_LINEBR.hasNo(c)) {
            String s = String.valueOf(Character.toChars(c));
            throw new ScannerException(DIRECTIVE_PREFIX, startMark, "expected ' ', but found " + s + "(" + c + ")", this.reader.getMark());
        }
        return value;
    }

    private void scanDirectiveIgnoredLine(Optional<Mark> startMark) {
        while (this.reader.peek() == 32) {
            this.reader.forward();
        }
        if (this.reader.peek() == 35) {
            while (CharConstants.NULL_OR_LINEBR.hasNo(this.reader.peek())) {
                this.reader.forward();
            }
        }
        int c = this.reader.peek();
        String lineBreak = this.scanLineBreak();
        if (lineBreak.length() == 0 && c != 0) {
            String s = String.valueOf(Character.toChars(c));
            throw new ScannerException(DIRECTIVE_PREFIX, startMark, "expected a comment or a line break, but found " + s + "(" + c + ")", this.reader.getMark());
        }
    }

    private Token scanAnchor(boolean isAnchor) {
        Optional<Mark> startMark = this.reader.getMark();
        int indicator = this.reader.peek();
        String name = indicator == 42 ? "alias" : "anchor";
        this.reader.forward();
        int length = 0;
        int c = this.reader.peek(length);
        while (CharConstants.NULL_BL_T_LINEBR.hasNo(c, ":,[]{}")) {
            c = this.reader.peek(++length);
        }
        if (length == 0) {
            String s = String.valueOf(Character.toChars(c));
            throw new ScannerException("while scanning an " + name, startMark, "unexpected character found " + s + "(" + c + ")", this.reader.getMark());
        }
        String value = this.reader.prefixForward(length);
        c = this.reader.peek();
        if (CharConstants.NULL_BL_T_LINEBR.hasNo(c, "?:,]}%@`")) {
            String s = String.valueOf(Character.toChars(c));
            throw new ScannerException("while scanning an " + name, startMark, "unexpected character found " + s + "(" + c + ")", this.reader.getMark());
        }
        Optional<Mark> endMark = this.reader.getMark();
        Token tok = isAnchor ? new AnchorToken(new Anchor(value), startMark, endMark) : new AliasToken(new Anchor(value), startMark, endMark);
        return tok;
    }

    private Token scanTag() {
        Optional<Mark> startMark = this.reader.getMark();
        int c = this.reader.peek(1);
        String handle = null;
        String suffix = null;
        if (c == 60) {
            this.reader.forward(2);
            suffix = this.scanTagUri("tag", startMark);
            c = this.reader.peek();
            if (c != 62) {
                String s = String.valueOf(Character.toChars(c));
                throw new ScannerException("while scanning a tag", startMark, "expected '>', but found '" + s + "' (" + c + ")", this.reader.getMark());
            }
            this.reader.forward();
        } else if (CharConstants.NULL_BL_T_LINEBR.has(c)) {
            suffix = "!";
            this.reader.forward();
        } else {
            int length = 1;
            boolean useHandle = false;
            while (CharConstants.NULL_BL_LINEBR.hasNo(c)) {
                if (c == 33) {
                    useHandle = true;
                    break;
                }
                c = this.reader.peek(++length);
            }
            if (useHandle) {
                handle = this.scanTagHandle("tag", startMark);
            } else {
                handle = "!";
                this.reader.forward();
            }
            suffix = this.scanTagUri("tag", startMark);
        }
        c = this.reader.peek();
        if (CharConstants.NULL_BL_LINEBR.hasNo(c)) {
            String s = String.valueOf(Character.toChars(c));
            throw new ScannerException("while scanning a tag", startMark, "expected ' ', but found '" + s + "' (" + c + ")", this.reader.getMark());
        }
        TagTuple value = new TagTuple(handle, suffix);
        Optional<Mark> endMark = this.reader.getMark();
        return new TagToken(value, startMark, endMark);
    }

    private Token scanBlockScalar(ScalarStyle style) {
        int blockIndent;
        Optional endMark;
        String breaks;
        Object[] brme;
        StringBuilder chunks = new StringBuilder();
        Optional<Mark> startMark = this.reader.getMark();
        this.reader.forward();
        Chomping chomping = this.scanBlockScalarIndicators(startMark);
        int increment = chomping.getIncrement();
        this.scanBlockScalarIgnoredLine(startMark);
        int minIndent = this.indent + 1;
        if (minIndent < 1) {
            minIndent = 1;
        }
        if (increment == -1) {
            brme = this.scanBlockScalarIndentation();
            breaks = (String)brme[0];
            int maxIndent = (Integer)brme[1];
            endMark = (Optional)brme[2];
            blockIndent = Math.max(minIndent, maxIndent);
        } else {
            blockIndent = minIndent + increment - 1;
            brme = this.scanBlockScalarBreaks(blockIndent);
            breaks = (String)brme[0];
            endMark = (Optional)brme[1];
        }
        String lineBreak = "";
        while (this.reader.getColumn() == blockIndent && this.reader.peek() != 0) {
            chunks.append(breaks);
            boolean leadingNonSpace = " \t".indexOf(this.reader.peek()) == -1;
            int length = 0;
            while (CharConstants.NULL_OR_LINEBR.hasNo(this.reader.peek(length))) {
                ++length;
            }
            chunks.append(this.reader.prefixForward(length));
            lineBreak = this.scanLineBreak();
            Object[] brme2 = this.scanBlockScalarBreaks(blockIndent);
            breaks = (String)brme2[0];
            endMark = (Optional)brme2[1];
            if (this.reader.getColumn() != blockIndent || this.reader.peek() == 0) break;
            if (style == ScalarStyle.FOLDED && "\n".equals(lineBreak) && leadingNonSpace && " \t".indexOf(this.reader.peek()) == -1) {
                if (breaks.length() != 0) continue;
                chunks.append(" ");
                continue;
            }
            chunks.append(lineBreak);
        }
        if (chomping.chompTailIsNotFalse()) {
            chunks.append(lineBreak);
        }
        if (chomping.chompTailIsTrue()) {
            chunks.append(breaks);
        }
        return new ScalarToken(chunks.toString(), false, style, startMark, endMark);
    }

    private Chomping scanBlockScalarIndicators(Optional<Mark> startMark) {
        String s;
        Boolean chomping = null;
        int increment = -1;
        int c = this.reader.peek();
        if (c == 45 || c == 43) {
            chomping = c == 43 ? Boolean.TRUE : Boolean.FALSE;
            this.reader.forward();
            c = this.reader.peek();
            if (Character.isDigit(c)) {
                s = String.valueOf(Character.toChars(c));
                increment = Integer.parseInt(s);
                if (increment == 0) {
                    throw new ScannerException(SCANNING_SCALAR, startMark, "expected indentation indicator in the range 1-9, but found 0", this.reader.getMark());
                }
                this.reader.forward();
            }
        } else if (Character.isDigit(c)) {
            s = String.valueOf(Character.toChars(c));
            increment = Integer.parseInt(s);
            if (increment == 0) {
                throw new ScannerException(SCANNING_SCALAR, startMark, "expected indentation indicator in the range 1-9, but found 0", this.reader.getMark());
            }
            this.reader.forward();
            c = this.reader.peek();
            if (c == 45 || c == 43) {
                chomping = c == 43 ? Boolean.TRUE : Boolean.FALSE;
                this.reader.forward();
            }
        }
        if (CharConstants.NULL_BL_LINEBR.hasNo(c = this.reader.peek())) {
            s = String.valueOf(Character.toChars(c));
            throw new ScannerException(SCANNING_SCALAR, startMark, "expected chomping or indentation indicators, but found " + s + "(" + c + ")", this.reader.getMark());
        }
        return new Chomping(chomping, increment);
    }

    private String scanBlockScalarIgnoredLine(Optional<Mark> startMark) {
        while (this.reader.peek() == 32) {
            this.reader.forward();
        }
        if (this.reader.peek() == 35) {
            while (CharConstants.NULL_OR_LINEBR.hasNo(this.reader.peek())) {
                this.reader.forward();
            }
        }
        int c = this.reader.peek();
        String lineBreak = this.scanLineBreak();
        if (lineBreak.length() == 0 && c != 0) {
            String s = String.valueOf(Character.toChars(c));
            throw new ScannerException(SCANNING_SCALAR, startMark, "expected a comment or a line break, but found " + s + "(" + c + ")", this.reader.getMark());
        }
        return lineBreak;
    }

    private Object[] scanBlockScalarIndentation() {
        StringBuilder chunks = new StringBuilder();
        int maxIndent = 0;
        Optional<Mark> endMark = this.reader.getMark();
        while (CharConstants.LINEBR.has(this.reader.peek(), " \r")) {
            if (this.reader.peek() != 32) {
                chunks.append(this.scanLineBreak());
                endMark = this.reader.getMark();
                continue;
            }
            this.reader.forward();
            if (this.reader.getColumn() <= maxIndent) continue;
            maxIndent = this.reader.getColumn();
        }
        return new Object[]{chunks.toString(), maxIndent, endMark};
    }

    private Object[] scanBlockScalarBreaks(int indent) {
        int col;
        StringBuilder chunks = new StringBuilder();
        Optional<Mark> endMark = this.reader.getMark();
        for (col = this.reader.getColumn(); col < indent && this.reader.peek() == 32; ++col) {
            this.reader.forward();
        }
        String lineBreak = null;
        while ((lineBreak = this.scanLineBreak()).length() != 0) {
            chunks.append(lineBreak);
            endMark = this.reader.getMark();
            for (col = this.reader.getColumn(); col < indent && this.reader.peek() == 32; ++col) {
                this.reader.forward();
            }
        }
        return new Object[]{chunks.toString(), endMark};
    }

    private Token scanFlowScalar(ScalarStyle style) {
        boolean doubleValue = style == ScalarStyle.DOUBLE_QUOTED;
        StringBuilder chunks = new StringBuilder();
        Optional<Mark> startMark = this.reader.getMark();
        int quote = this.reader.peek();
        this.reader.forward();
        chunks.append(this.scanFlowScalarNonSpaces(doubleValue, startMark));
        while (this.reader.peek() != quote) {
            chunks.append(this.scanFlowScalarSpaces(startMark));
            chunks.append(this.scanFlowScalarNonSpaces(doubleValue, startMark));
        }
        this.reader.forward();
        Optional<Mark> endMark = this.reader.getMark();
        return new ScalarToken(chunks.toString(), false, style, startMark, endMark);
    }

    private String scanFlowScalarNonSpaces(boolean doubleQuoted, Optional<Mark> startMark) {
        StringBuilder chunks;
        block8: {
            int c;
            chunks = new StringBuilder();
            while (true) {
                int length = 0;
                while (CharConstants.NULL_BL_T_LINEBR.hasNo(this.reader.peek(length), "'\"\\")) {
                    ++length;
                }
                if (length != 0) {
                    chunks.append(this.reader.prefixForward(length));
                }
                c = this.reader.peek();
                if (!doubleQuoted && c == 39 && this.reader.peek(1) == 39) {
                    chunks.append("'");
                    this.reader.forward(2);
                    continue;
                }
                if (doubleQuoted && c == 39 || !doubleQuoted && "\"\\".indexOf(c) != -1) {
                    chunks.appendCodePoint(c);
                    this.reader.forward();
                    continue;
                }
                if (!doubleQuoted || c != 92) break block8;
                this.reader.forward();
                c = this.reader.peek();
                if (!Character.isSupplementaryCodePoint(c) && CharConstants.ESCAPE_REPLACEMENTS.containsKey(c)) {
                    chunks.append(CharConstants.ESCAPE_REPLACEMENTS.get(c));
                    this.reader.forward();
                    continue;
                }
                if (!Character.isSupplementaryCodePoint(c) && CharConstants.ESCAPE_CODES.containsKey(Character.valueOf((char)c))) {
                    length = CharConstants.ESCAPE_CODES.get(Character.valueOf((char)c));
                    this.reader.forward();
                    String hex = this.reader.prefix(length);
                    if (NOT_HEXA.matcher(hex).find()) {
                        throw new ScannerException("while scanning a double-quoted scalar", startMark, "expected escape sequence of " + length + " hexadecimal numbers, but found: " + hex, this.reader.getMark());
                    }
                    int decimal = Integer.parseInt(hex, 16);
                    String unicode = new String(Character.toChars(decimal));
                    chunks.append(unicode);
                    this.reader.forward(length);
                    continue;
                }
                if (this.scanLineBreak().length() == 0) break;
                chunks.append(this.scanFlowScalarBreaks(startMark));
            }
            String s = String.valueOf(Character.toChars(c));
            throw new ScannerException("while scanning a double-quoted scalar", startMark, "found unknown escape character " + s + "(" + c + ")", this.reader.getMark());
        }
        return chunks.toString();
    }

    private String scanFlowScalarSpaces(Optional<Mark> startMark) {
        StringBuilder chunks = new StringBuilder();
        int length = 0;
        while (" \t".indexOf(this.reader.peek(length)) != -1) {
            ++length;
        }
        String whitespaces = this.reader.prefixForward(length);
        int c = this.reader.peek();
        if (c == 0) {
            throw new ScannerException("while scanning a quoted scalar", startMark, "found unexpected end of stream", this.reader.getMark());
        }
        String lineBreak = this.scanLineBreak();
        if (lineBreak.length() != 0) {
            String breaks = this.scanFlowScalarBreaks(startMark);
            if (!"\n".equals(lineBreak)) {
                chunks.append(lineBreak);
            } else if (breaks.length() == 0) {
                chunks.append(" ");
            }
            chunks.append(breaks);
        } else {
            chunks.append(whitespaces);
        }
        return chunks.toString();
    }

    private String scanFlowScalarBreaks(Optional<Mark> startMark) {
        StringBuilder chunks = new StringBuilder();
        while (true) {
            String prefix;
            if (("---".equals(prefix = this.reader.prefix(3)) || "...".equals(prefix)) && CharConstants.NULL_BL_T_LINEBR.has(this.reader.peek(3))) {
                throw new ScannerException("while scanning a quoted scalar", startMark, "found unexpected document separator", this.reader.getMark());
            }
            while (" \t".indexOf(this.reader.peek()) != -1) {
                this.reader.forward();
            }
            String lineBreak = this.scanLineBreak();
            if (lineBreak.length() == 0) break;
            chunks.append(lineBreak);
        }
        return chunks.toString();
    }

    private Token scanPlain() {
        Optional<Mark> startMark;
        StringBuilder chunks = new StringBuilder();
        Optional<Mark> endMark = startMark = this.reader.getMark();
        int plainIndent = this.indent + 1;
        String spaces = "";
        do {
            int c;
            int length = 0;
            if (this.reader.peek() == 35) break;
            while (!(CharConstants.NULL_BL_T_LINEBR.has(c = this.reader.peek(length)) || c == 58 && CharConstants.NULL_BL_T_LINEBR.has(this.reader.peek(length + 1), this.flowLevel != 0 ? ",[]{}" : "") || this.flowLevel != 0 && ",?[]{}".indexOf(c) != -1)) {
                ++length;
            }
            if (length == 0) break;
            this.allowSimpleKey = false;
            chunks.append(spaces);
            chunks.append(this.reader.prefixForward(length));
            endMark = this.reader.getMark();
        } while ((spaces = this.scanPlainSpaces()).length() != 0 && this.reader.peek() != 35 && (this.flowLevel != 0 || this.reader.getColumn() >= plainIndent));
        return new ScalarToken(chunks.toString(), true, startMark, endMark);
    }

    private String scanPlainSpaces() {
        int length = 0;
        while (this.reader.peek(length) == 32 || this.reader.peek(length) == 9) {
            ++length;
        }
        String whitespaces = this.reader.prefixForward(length);
        String lineBreak = this.scanLineBreak();
        if (lineBreak.length() != 0) {
            StringBuilder breaks;
            block7: {
                this.allowSimpleKey = true;
                String prefix = this.reader.prefix(3);
                if ("---".equals(prefix) || "...".equals(prefix) && CharConstants.NULL_BL_T_LINEBR.has(this.reader.peek(3))) {
                    return "";
                }
                breaks = new StringBuilder();
                while (true) {
                    if (this.reader.peek() == 32) {
                        this.reader.forward();
                        continue;
                    }
                    String lb = this.scanLineBreak();
                    if (lb.length() == 0) break block7;
                    breaks.append(lb);
                    prefix = this.reader.prefix(3);
                    if ("---".equals(prefix) || "...".equals(prefix) && CharConstants.NULL_BL_T_LINEBR.has(this.reader.peek(3))) break;
                }
                return "";
            }
            if (!"\n".equals(lineBreak)) {
                return lineBreak + breaks;
            }
            if (breaks.length() == 0) {
                return " ";
            }
            return breaks.toString();
        }
        return whitespaces;
    }

    private String scanTagHandle(String name, Optional<Mark> startMark) {
        int c = this.reader.peek();
        if (c != 33) {
            String s = String.valueOf(Character.toChars(c));
            throw new ScannerException(SCANNING_PREFIX + name, startMark, "expected '!', but found " + s + "(" + c + ")", this.reader.getMark());
        }
        int length = 1;
        c = this.reader.peek(length);
        if (c != 32) {
            while (CharConstants.ALPHA.has(c)) {
                c = this.reader.peek(++length);
            }
            if (c != 33) {
                this.reader.forward(length);
                String s = String.valueOf(Character.toChars(c));
                throw new ScannerException(SCANNING_PREFIX + name, startMark, "expected '!', but found " + s + "(" + c + ")", this.reader.getMark());
            }
            ++length;
        }
        return this.reader.prefixForward(length);
    }

    private String scanTagUri(String name, Optional<Mark> startMark) {
        StringBuilder chunks = new StringBuilder();
        int length = 0;
        int c = this.reader.peek(length);
        while (CharConstants.URI_CHARS.has(c)) {
            if (c == 37) {
                chunks.append(this.reader.prefixForward(length));
                length = 0;
                chunks.append(this.scanUriEscapes(name, startMark));
            } else {
                ++length;
            }
            c = this.reader.peek(length);
        }
        if (length != 0) {
            chunks.append(this.reader.prefixForward(length));
        }
        if (chunks.length() == 0) {
            String s = String.valueOf(Character.toChars(c));
            throw new ScannerException(SCANNING_PREFIX + name, startMark, "expected URI, but found " + s + "(" + c + ")", this.reader.getMark());
        }
        return chunks.toString();
    }

    private String scanUriEscapes(String name, Optional<Mark> startMark) {
        int length = 1;
        while (this.reader.peek(length * 3) == 37) {
            ++length;
        }
        Optional<Mark> beginningMark = this.reader.getMark();
        ByteBuffer buff = ByteBuffer.allocate(length);
        while (this.reader.peek() == 37) {
            this.reader.forward();
            try {
                byte code = (byte)Integer.parseInt(this.reader.prefix(2), 16);
                buff.put(code);
            }
            catch (NumberFormatException nfe) {
                int c1 = this.reader.peek();
                String s1 = String.valueOf(Character.toChars(c1));
                int c2 = this.reader.peek(1);
                String s2 = String.valueOf(Character.toChars(c2));
                throw new ScannerException(SCANNING_PREFIX + name, startMark, "expected URI escape sequence of 2 hexadecimal numbers, but found " + s1 + "(" + c1 + ") and " + s2 + "(" + c2 + ")", this.reader.getMark());
            }
            this.reader.forward(2);
        }
        buff.flip();
        try {
            return UriEncoder.decode(buff);
        }
        catch (CharacterCodingException e) {
            throw new ScannerException(SCANNING_PREFIX + name, startMark, "expected URI in UTF-8: " + e.getMessage(), beginningMark);
        }
    }

    private String scanLineBreak() {
        int c = this.reader.peek();
        if (c == 13 || c == 10 || c == 133) {
            if (c == 13 && 10 == this.reader.peek(1)) {
                this.reader.forward(2);
            } else {
                this.reader.forward();
            }
            return "\n";
        }
        if (c == 8232 || c == 8233) {
            this.reader.forward();
            return String.valueOf(Character.toChars(c));
        }
        return "";
    }

    private static class Chomping {
        private final Boolean value;
        private final int increment;

        public Chomping(Boolean value, int increment) {
            this.value = value;
            this.increment = increment;
        }

        public boolean chompTailIsNotFalse() {
            return this.value == null || this.value != false;
        }

        public boolean chompTailIsTrue() {
            return this.value != null && this.value != false;
        }

        public int getIncrement() {
            return this.increment;
        }
    }
}

