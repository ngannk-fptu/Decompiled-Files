/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Tokenizer
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.OffsetAttribute
 *  org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute
 *  org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute
 *  org.apache.lucene.analysis.util.RollingCharBuffer
 *  org.apache.lucene.util.ArrayUtil
 *  org.apache.lucene.util.AttributeSource$AttributeFactory
 *  org.apache.lucene.util.IntsRef
 *  org.apache.lucene.util.RamUsageEstimator
 *  org.apache.lucene.util.fst.FST$Arc
 *  org.apache.lucene.util.fst.FST$BytesReader
 */
package org.apache.lucene.analysis.ja;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.ja.GraphvizFormatter;
import org.apache.lucene.analysis.ja.Token;
import org.apache.lucene.analysis.ja.dict.CharacterDefinition;
import org.apache.lucene.analysis.ja.dict.ConnectionCosts;
import org.apache.lucene.analysis.ja.dict.Dictionary;
import org.apache.lucene.analysis.ja.dict.TokenInfoDictionary;
import org.apache.lucene.analysis.ja.dict.TokenInfoFST;
import org.apache.lucene.analysis.ja.dict.UnknownDictionary;
import org.apache.lucene.analysis.ja.dict.UserDictionary;
import org.apache.lucene.analysis.ja.tokenattributes.BaseFormAttribute;
import org.apache.lucene.analysis.ja.tokenattributes.InflectionAttribute;
import org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute;
import org.apache.lucene.analysis.ja.tokenattributes.ReadingAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.analysis.util.RollingCharBuffer;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.IntsRef;
import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.util.fst.FST;

public final class JapaneseTokenizer
extends Tokenizer {
    public static final Mode DEFAULT_MODE = Mode.SEARCH;
    private static final boolean VERBOSE = false;
    private static final int SEARCH_MODE_KANJI_LENGTH = 2;
    private static final int SEARCH_MODE_OTHER_LENGTH = 7;
    private static final int SEARCH_MODE_KANJI_PENALTY = 3000;
    private static final int SEARCH_MODE_OTHER_PENALTY = 1700;
    private static final int MAX_UNKNOWN_WORD_LENGTH = 1024;
    private static final int MAX_BACKTRACE_GAP = 1024;
    private final EnumMap<Type, Dictionary> dictionaryMap = new EnumMap(Type.class);
    private final TokenInfoFST fst;
    private final TokenInfoDictionary dictionary;
    private final UnknownDictionary unkDictionary;
    private final ConnectionCosts costs;
    private final UserDictionary userDictionary;
    private final CharacterDefinition characterDefinition;
    private final FST.Arc<Long> arc = new FST.Arc();
    private final FST.BytesReader fstReader;
    private final IntsRef wordIdRef = new IntsRef();
    private final FST.BytesReader userFSTReader;
    private final TokenInfoFST userFST;
    private final RollingCharBuffer buffer = new RollingCharBuffer();
    private final WrappedPositionArray positions = new WrappedPositionArray();
    private final boolean discardPunctuation;
    private final boolean searchMode;
    private final boolean extendedMode;
    private final boolean outputCompounds;
    private int unknownWordEndIndex = -1;
    private boolean end;
    private int lastBackTracePos;
    private int lastTokenPos;
    private int pos;
    private final List<Token> pending = new ArrayList<Token>();
    private final CharTermAttribute termAtt = (CharTermAttribute)this.addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAtt = (OffsetAttribute)this.addAttribute(OffsetAttribute.class);
    private final PositionIncrementAttribute posIncAtt = (PositionIncrementAttribute)this.addAttribute(PositionIncrementAttribute.class);
    private final PositionLengthAttribute posLengthAtt = (PositionLengthAttribute)this.addAttribute(PositionLengthAttribute.class);
    private final BaseFormAttribute basicFormAtt = (BaseFormAttribute)this.addAttribute(BaseFormAttribute.class);
    private final PartOfSpeechAttribute posAtt = (PartOfSpeechAttribute)this.addAttribute(PartOfSpeechAttribute.class);
    private final ReadingAttribute readingAtt = (ReadingAttribute)this.addAttribute(ReadingAttribute.class);
    private final InflectionAttribute inflectionAtt = (InflectionAttribute)this.addAttribute(InflectionAttribute.class);
    private GraphvizFormatter dotOut;

    public JapaneseTokenizer(Reader input, UserDictionary userDictionary, boolean discardPunctuation, Mode mode) {
        this(AttributeSource.AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY, input, userDictionary, discardPunctuation, mode);
    }

    public JapaneseTokenizer(AttributeSource.AttributeFactory factory, Reader input, UserDictionary userDictionary, boolean discardPunctuation, Mode mode) {
        super(factory, input);
        this.dictionary = TokenInfoDictionary.getInstance();
        this.fst = this.dictionary.getFST();
        this.unkDictionary = UnknownDictionary.getInstance();
        this.characterDefinition = this.unkDictionary.getCharacterDefinition();
        this.userDictionary = userDictionary;
        this.costs = ConnectionCosts.getInstance();
        this.fstReader = this.fst.getBytesReader();
        if (userDictionary != null) {
            this.userFST = userDictionary.getFST();
            this.userFSTReader = this.userFST.getBytesReader();
        } else {
            this.userFST = null;
            this.userFSTReader = null;
        }
        this.discardPunctuation = discardPunctuation;
        switch (mode) {
            case SEARCH: {
                this.searchMode = true;
                this.extendedMode = false;
                this.outputCompounds = true;
                break;
            }
            case EXTENDED: {
                this.searchMode = true;
                this.extendedMode = true;
                this.outputCompounds = false;
                break;
            }
            default: {
                this.searchMode = false;
                this.extendedMode = false;
                this.outputCompounds = false;
            }
        }
        this.buffer.reset(null);
        this.resetState();
        this.dictionaryMap.put(Type.KNOWN, this.dictionary);
        this.dictionaryMap.put(Type.UNKNOWN, this.unkDictionary);
        this.dictionaryMap.put(Type.USER, userDictionary);
    }

    public void setGraphvizFormatter(GraphvizFormatter dotOut) {
        this.dotOut = dotOut;
    }

    public void reset() throws IOException {
        this.buffer.reset(this.input);
        this.resetState();
    }

    private void resetState() {
        this.positions.reset();
        this.unknownWordEndIndex = -1;
        this.pos = 0;
        this.end = false;
        this.lastBackTracePos = 0;
        this.lastTokenPos = -1;
        this.pending.clear();
        this.positions.get(0).add(0, 0, -1, -1, -1, Type.KNOWN);
    }

    public void end() {
        int finalOffset = this.correctOffset(this.pos);
        this.offsetAtt.setOffset(finalOffset, finalOffset);
    }

    private int computeSecondBestThreshold(int pos, int length) throws IOException {
        return this.computePenalty(pos, length);
    }

    private int computePenalty(int pos, int length) throws IOException {
        if (length > 2) {
            boolean allKanji = true;
            int endPos = pos + length;
            for (int pos2 = pos; pos2 < endPos; ++pos2) {
                if (this.characterDefinition.isKanji((char)this.buffer.get(pos2))) continue;
                allKanji = false;
                break;
            }
            if (allKanji) {
                return (length - 2) * 3000;
            }
            if (length > 7) {
                return (length - 7) * 1700;
            }
        }
        return 0;
    }

    private void add(Dictionary dict, Position fromPosData, int endPos, int wordID, Type type, boolean addPenalty) throws IOException {
        int wordCost = dict.getWordCost(wordID);
        int leftID = dict.getLeftId(wordID);
        int leastCost = Integer.MAX_VALUE;
        int leastIDX = -1;
        assert (fromPosData.count > 0);
        for (int idx = 0; idx < fromPosData.count; ++idx) {
            int cost = fromPosData.costs[idx] + this.costs.get(fromPosData.lastRightID[idx], leftID);
            if (cost >= leastCost) continue;
            leastCost = cost;
            leastIDX = idx;
        }
        leastCost += wordCost;
        if ((addPenalty || !this.outputCompounds && this.searchMode) && type != Type.USER) {
            int penalty = this.computePenalty(fromPosData.pos, endPos - fromPosData.pos);
            leastCost += penalty;
        }
        assert (leftID == dict.getRightId(wordID));
        this.positions.get(endPos).add(leastCost, leftID, fromPosData.pos, leastIDX, wordID, type);
    }

    public boolean incrementToken() throws IOException {
        while (this.pending.size() == 0) {
            if (this.end) {
                return false;
            }
            this.parse();
        }
        Token token = this.pending.remove(this.pending.size() - 1);
        int position = token.getPosition();
        int length = token.getLength();
        this.clearAttributes();
        assert (length > 0);
        this.termAtt.copyBuffer(token.getSurfaceForm(), token.getOffset(), length);
        this.offsetAtt.setOffset(this.correctOffset(position), this.correctOffset(position + length));
        this.basicFormAtt.setToken(token);
        this.posAtt.setToken(token);
        this.readingAtt.setToken(token);
        this.inflectionAtt.setToken(token);
        if (token.getPosition() == this.lastTokenPos) {
            this.posIncAtt.setPositionIncrement(0);
            this.posLengthAtt.setPositionLength(token.getPositionLength());
        } else {
            assert (token.getPosition() > this.lastTokenPos);
            this.posIncAtt.setPositionIncrement(1);
            this.posLengthAtt.setPositionLength(1);
        }
        this.lastTokenPos = token.getPosition();
        return true;
    }

    private void parse() throws IOException {
        int leastIDX;
        while (this.buffer.get(this.pos) != -1) {
            int ch;
            int output;
            boolean isFrontier;
            Position posData = this.positions.get(this.pos);
            boolean bl = isFrontier = this.positions.getNextPos() == this.pos + 1;
            if (posData.count == 0) {
                ++this.pos;
                continue;
            }
            if (this.pos > this.lastBackTracePos && posData.count == 1 && isFrontier) {
                this.backtrace(posData, 0);
                posData.costs[0] = 0;
                if (this.pending.size() != 0) {
                    return;
                }
            }
            if (this.pos - this.lastBackTracePos >= 1024) {
                Position posData2;
                int pos2;
                leastIDX = -1;
                int leastCost = Integer.MAX_VALUE;
                Position leastPosData = null;
                for (pos2 = this.pos; pos2 < this.positions.getNextPos(); ++pos2) {
                    posData2 = this.positions.get(pos2);
                    for (int idx = 0; idx < posData2.count; ++idx) {
                        int cost = posData2.costs[idx];
                        if (cost >= leastCost) continue;
                        leastCost = cost;
                        leastIDX = idx;
                        leastPosData = posData2;
                    }
                }
                assert (leastIDX != -1);
                for (pos2 = this.pos; pos2 < this.positions.getNextPos(); ++pos2) {
                    posData2 = this.positions.get(pos2);
                    if (posData2 != leastPosData) {
                        posData2.reset();
                        continue;
                    }
                    if (leastIDX != 0) {
                        posData2.costs[0] = posData2.costs[leastIDX];
                        posData2.lastRightID[0] = posData2.lastRightID[leastIDX];
                        posData2.backPos[0] = posData2.backPos[leastIDX];
                        posData2.backIndex[0] = posData2.backIndex[leastIDX];
                        posData2.backID[0] = posData2.backID[leastIDX];
                        posData2.backType[0] = posData2.backType[leastIDX];
                    }
                    posData2.count = 1;
                }
                this.backtrace(leastPosData, 0);
                Arrays.fill(leastPosData.costs, 0, leastPosData.count, 0);
                if (this.pos != leastPosData.pos) {
                    assert (this.pos < leastPosData.pos);
                    this.pos = leastPosData.pos;
                }
                if (this.pending.size() == 0) continue;
                return;
            }
            boolean anyMatches = false;
            if (this.userFST != null) {
                this.userFST.getFirstArc(this.arc);
                output = 0;
                int posAhead = posData.pos;
                while ((ch = this.buffer.get(posAhead)) != -1 && this.userFST.findTargetArc(ch, this.arc, this.arc, posAhead == posData.pos, this.userFSTReader) != null) {
                    output += ((Long)this.arc.output).intValue();
                    if (this.arc.isFinal()) {
                        this.add(this.userDictionary, posData, posAhead + 1, output + ((Long)this.arc.nextFinalOutput).intValue(), Type.USER, false);
                        anyMatches = true;
                    }
                    ++posAhead;
                }
            }
            if (!anyMatches) {
                this.fst.getFirstArc(this.arc);
                output = 0;
                int posAhead = posData.pos;
                while ((ch = this.buffer.get(posAhead)) != -1 && this.fst.findTargetArc(ch, this.arc, this.arc, posAhead == posData.pos, this.fstReader) != null) {
                    output += ((Long)this.arc.output).intValue();
                    if (this.arc.isFinal()) {
                        this.dictionary.lookupWordIds(output + ((Long)this.arc.nextFinalOutput).intValue(), this.wordIdRef);
                        for (int ofs = 0; ofs < this.wordIdRef.length; ++ofs) {
                            this.add(this.dictionary, posData, posAhead + 1, this.wordIdRef.ints[this.wordIdRef.offset + ofs], Type.KNOWN, false);
                            anyMatches = true;
                        }
                    }
                    ++posAhead;
                }
            }
            if (!this.searchMode && this.unknownWordEndIndex > posData.pos) {
                ++this.pos;
                continue;
            }
            char firstCharacter = (char)this.buffer.get(this.pos);
            if (!anyMatches || this.characterDefinition.isInvoke(firstCharacter)) {
                int unknownWordLength;
                byte characterId = this.characterDefinition.getCharacterClass(firstCharacter);
                boolean isPunct = JapaneseTokenizer.isPunctuation(firstCharacter);
                if (!this.characterDefinition.isGroup(firstCharacter)) {
                    unknownWordLength = 1;
                } else {
                    int ch2;
                    unknownWordLength = 1;
                    int posAhead = this.pos + 1;
                    while (unknownWordLength < 1024 && (ch2 = this.buffer.get(posAhead)) != -1 && characterId == this.characterDefinition.getCharacterClass((char)ch2) && JapaneseTokenizer.isPunctuation((char)ch2) == isPunct) {
                        ++unknownWordLength;
                        ++posAhead;
                    }
                }
                this.unkDictionary.lookupWordIds(characterId, this.wordIdRef);
                for (int ofs = 0; ofs < this.wordIdRef.length; ++ofs) {
                    this.add(this.unkDictionary, posData, posData.pos + unknownWordLength, this.wordIdRef.ints[this.wordIdRef.offset + ofs], Type.UNKNOWN, false);
                }
                this.unknownWordEndIndex = posData.pos + unknownWordLength;
            }
            ++this.pos;
        }
        this.end = true;
        if (this.pos > 0) {
            Position endPosData = this.positions.get(this.pos);
            int leastCost = Integer.MAX_VALUE;
            leastIDX = -1;
            for (int idx = 0; idx < endPosData.count; ++idx) {
                int cost = endPosData.costs[idx] + this.costs.get(endPosData.lastRightID[idx], 0);
                if (cost >= leastCost) continue;
                leastCost = cost;
                leastIDX = idx;
            }
            this.backtrace(endPosData, leastIDX);
        }
    }

    private void pruneAndRescore(int startPos, int endPos, int bestStartIDX) throws IOException {
        Position posData;
        int pos;
        for (pos = endPos; pos > startPos; --pos) {
            posData = this.positions.get(pos);
            for (int arcIDX = 0; arcIDX < posData.count; ++arcIDX) {
                int backPos = posData.backPos[arcIDX];
                if (backPos < startPos) continue;
                this.positions.get(backPos).addForward(pos, arcIDX, posData.backID[arcIDX], posData.backType[arcIDX]);
            }
            if (pos == startPos) continue;
            posData.count = 0;
        }
        for (pos = startPos; pos < endPos; ++pos) {
            posData = this.positions.get(pos);
            if (posData.count == 0) {
                posData.forwardCount = 0;
                continue;
            }
            if (pos == startPos) {
                int rightID = startPos == 0 ? 0 : this.getDict(posData.backType[bestStartIDX]).getRightId(posData.backID[bestStartIDX]);
                int pathCost = posData.costs[bestStartIDX];
                for (int forwardArcIDX = 0; forwardArcIDX < posData.forwardCount; ++forwardArcIDX) {
                    Type forwardType = posData.forwardType[forwardArcIDX];
                    Dictionary dict2 = this.getDict(forwardType);
                    int wordID = posData.forwardID[forwardArcIDX];
                    int toPos = posData.forwardPos[forwardArcIDX];
                    int newCost = pathCost + dict2.getWordCost(wordID) + this.costs.get(rightID, dict2.getLeftId(wordID)) + this.computePenalty(pos, toPos - pos);
                    this.positions.get(toPos).add(newCost, dict2.getRightId(wordID), pos, bestStartIDX, wordID, forwardType);
                }
            } else {
                for (int forwardArcIDX = 0; forwardArcIDX < posData.forwardCount; ++forwardArcIDX) {
                    Type forwardType = posData.forwardType[forwardArcIDX];
                    int toPos = posData.forwardPos[forwardArcIDX];
                    this.add(this.getDict(forwardType), posData, toPos, posData.forwardID[forwardArcIDX], forwardType, true);
                }
            }
            posData.forwardCount = 0;
        }
    }

    private void backtrace(Position endPosData, int fromIDX) throws IOException {
        int endPos = endPosData.pos;
        char[] fragment = this.buffer.get(this.lastBackTracePos, endPos - this.lastBackTracePos);
        if (this.dotOut != null) {
            this.dotOut.onBacktrace(this, this.positions, this.lastBackTracePos, endPosData, fromIDX, fragment, this.end);
        }
        int pos = endPos;
        int bestIDX = fromIDX;
        Token altToken = null;
        int lastLeftWordID = -1;
        int backCount = 0;
        while (pos > this.lastBackTracePos) {
            int penalty;
            Position posData = this.positions.get(pos);
            assert (bestIDX < posData.count);
            int backPos = posData.backPos[bestIDX];
            assert (backPos >= this.lastBackTracePos) : "backPos=" + backPos + " vs lastBackTracePos=" + this.lastBackTracePos;
            int length = pos - backPos;
            Type backType = posData.backType[bestIDX];
            int backID = posData.backID[bestIDX];
            int nextBestIDX = posData.backIndex[bestIDX];
            if (this.outputCompounds && this.searchMode && altToken == null && backType != Type.USER && (penalty = this.computeSecondBestThreshold(backPos, pos - backPos)) > 0) {
                int maxCost = posData.costs[bestIDX] + penalty;
                if (lastLeftWordID != -1) {
                    maxCost += this.costs.get(this.getDict(backType).getRightId(backID), lastLeftWordID);
                }
                this.pruneAndRescore(backPos, pos, posData.backIndex[bestIDX]);
                int leastCost = Integer.MAX_VALUE;
                int leastIDX = -1;
                for (int idx = 0; idx < posData.count; ++idx) {
                    int cost = posData.costs[idx];
                    if (lastLeftWordID != -1) {
                        cost += this.costs.get(this.getDict(posData.backType[idx]).getRightId(posData.backID[idx]), lastLeftWordID);
                    }
                    if (cost >= leastCost) continue;
                    leastCost = cost;
                    leastIDX = idx;
                }
                if (leastIDX != -1 && leastCost <= maxCost && posData.backPos[leastIDX] != backPos) {
                    assert (posData.backPos[leastIDX] != backPos);
                    altToken = new Token(backID, fragment, backPos - this.lastBackTracePos, length, backType, backPos, this.getDict(backType));
                    bestIDX = leastIDX;
                    nextBestIDX = posData.backIndex[bestIDX];
                    backPos = posData.backPos[bestIDX];
                    length = pos - backPos;
                    backType = posData.backType[bestIDX];
                    backID = posData.backID[bestIDX];
                    backCount = 0;
                }
            }
            int offset = backPos - this.lastBackTracePos;
            assert (offset >= 0);
            if (altToken != null && altToken.getPosition() >= backPos) {
                assert (altToken.getPosition() == backPos) : altToken.getPosition() + " vs " + backPos;
                if (backCount > 0) {
                    altToken.setPositionLength(++backCount);
                    this.pending.add(altToken);
                } else assert (this.discardPunctuation);
                altToken = null;
            }
            Dictionary dict = this.getDict(backType);
            if (backType == Type.USER) {
                int[] wordIDAndLength = this.userDictionary.lookupSegmentation(backID);
                int wordID = wordIDAndLength[0];
                int current = 0;
                for (int j = 1; j < wordIDAndLength.length; ++j) {
                    int len = wordIDAndLength[j];
                    this.pending.add(new Token(wordID + j - 1, fragment, current + offset, len, Type.USER, current + backPos, dict));
                    current += len;
                }
                Collections.reverse(this.pending.subList(this.pending.size() - (wordIDAndLength.length - 1), this.pending.size()));
                backCount += wordIDAndLength.length - 1;
            } else if (this.extendedMode && backType == Type.UNKNOWN) {
                int unigramTokenCount = 0;
                for (int i = length - 1; i >= 0; --i) {
                    int charLen = 1;
                    if (i > 0 && Character.isLowSurrogate(fragment[offset + i])) {
                        --i;
                        charLen = 2;
                    }
                    if (this.discardPunctuation && JapaneseTokenizer.isPunctuation(fragment[offset + i])) continue;
                    this.pending.add(new Token(CharacterDefinition.NGRAM, fragment, offset + i, charLen, Type.UNKNOWN, backPos + i, this.unkDictionary));
                    ++unigramTokenCount;
                }
                backCount += unigramTokenCount;
            } else if (!this.discardPunctuation || length == 0 || !JapaneseTokenizer.isPunctuation(fragment[offset])) {
                this.pending.add(new Token(backID, fragment, offset, length, backType, backPos, dict));
                ++backCount;
            }
            lastLeftWordID = dict.getLeftId(backID);
            pos = backPos;
            bestIDX = nextBestIDX;
        }
        this.lastBackTracePos = endPos;
        this.buffer.freeBefore(endPos);
        this.positions.freeBefore(endPos);
    }

    Dictionary getDict(Type type) {
        return this.dictionaryMap.get((Object)type);
    }

    private static boolean isPunctuation(char ch) {
        switch (Character.getType(ch)) {
            case 12: 
            case 13: 
            case 14: 
            case 15: 
            case 16: 
            case 20: 
            case 21: 
            case 22: 
            case 23: 
            case 24: 
            case 25: 
            case 26: 
            case 27: 
            case 28: 
            case 29: 
            case 30: {
                return true;
            }
        }
        return false;
    }

    static final class WrappedPositionArray {
        private Position[] positions = new Position[8];
        private int nextWrite;
        private int nextPos;
        private int count;

        public WrappedPositionArray() {
            for (int i = 0; i < this.positions.length; ++i) {
                this.positions[i] = new Position();
            }
        }

        public void reset() {
            --this.nextWrite;
            while (this.count > 0) {
                if (this.nextWrite == -1) {
                    this.nextWrite = this.positions.length - 1;
                }
                this.positions[this.nextWrite--].reset();
                --this.count;
            }
            this.nextWrite = 0;
            this.nextPos = 0;
            this.count = 0;
        }

        public Position get(int pos) {
            while (pos >= this.nextPos) {
                if (this.count == this.positions.length) {
                    Position[] newPositions = new Position[ArrayUtil.oversize((int)(1 + this.count), (int)RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
                    System.arraycopy(this.positions, this.nextWrite, newPositions, 0, this.positions.length - this.nextWrite);
                    System.arraycopy(this.positions, 0, newPositions, this.positions.length - this.nextWrite, this.nextWrite);
                    for (int i = this.positions.length; i < newPositions.length; ++i) {
                        newPositions[i] = new Position();
                    }
                    this.nextWrite = this.positions.length;
                    this.positions = newPositions;
                }
                if (this.nextWrite == this.positions.length) {
                    this.nextWrite = 0;
                }
                assert (this.positions[this.nextWrite].count == 0);
                ++this.nextWrite;
                ++this.nextPos;
                this.positions[this.nextWrite].pos = this.positions[this.nextWrite].pos;
                ++this.count;
            }
            assert (this.inBounds(pos));
            int index = this.getIndex(pos);
            assert (this.positions[index].pos == pos);
            return this.positions[index];
        }

        public int getNextPos() {
            return this.nextPos;
        }

        private boolean inBounds(int pos) {
            return pos < this.nextPos && pos >= this.nextPos - this.count;
        }

        private int getIndex(int pos) {
            int index = this.nextWrite - (this.nextPos - pos);
            if (index < 0) {
                index += this.positions.length;
            }
            return index;
        }

        public void freeBefore(int pos) {
            int toFree = this.count - (this.nextPos - pos);
            assert (toFree >= 0);
            assert (toFree <= this.count);
            int index = this.nextWrite - this.count;
            if (index < 0) {
                index += this.positions.length;
            }
            for (int i = 0; i < toFree; ++i) {
                if (index == this.positions.length) {
                    index = 0;
                }
                this.positions[index].reset();
                ++index;
            }
            this.count -= toFree;
        }
    }

    static final class Position {
        int pos;
        int count;
        int[] costs = new int[8];
        int[] lastRightID = new int[8];
        int[] backPos = new int[8];
        int[] backIndex = new int[8];
        int[] backID = new int[8];
        Type[] backType = new Type[8];
        int forwardCount;
        int[] forwardPos = new int[8];
        int[] forwardID = new int[8];
        int[] forwardIndex = new int[8];
        Type[] forwardType = new Type[8];

        Position() {
        }

        public void grow() {
            this.costs = ArrayUtil.grow((int[])this.costs, (int)(1 + this.count));
            this.lastRightID = ArrayUtil.grow((int[])this.lastRightID, (int)(1 + this.count));
            this.backPos = ArrayUtil.grow((int[])this.backPos, (int)(1 + this.count));
            this.backIndex = ArrayUtil.grow((int[])this.backIndex, (int)(1 + this.count));
            this.backID = ArrayUtil.grow((int[])this.backID, (int)(1 + this.count));
            Type[] newBackType = new Type[this.backID.length];
            System.arraycopy(this.backType, 0, newBackType, 0, this.backType.length);
            this.backType = newBackType;
        }

        public void growForward() {
            this.forwardPos = ArrayUtil.grow((int[])this.forwardPos, (int)(1 + this.forwardCount));
            this.forwardID = ArrayUtil.grow((int[])this.forwardID, (int)(1 + this.forwardCount));
            this.forwardIndex = ArrayUtil.grow((int[])this.forwardIndex, (int)(1 + this.forwardCount));
            Type[] newForwardType = new Type[this.forwardPos.length];
            System.arraycopy(this.forwardType, 0, newForwardType, 0, this.forwardType.length);
            this.forwardType = newForwardType;
        }

        public void add(int cost, int lastRightID, int backPos, int backIndex, int backID, Type backType) {
            if (this.count == this.costs.length) {
                this.grow();
            }
            this.costs[this.count] = cost;
            this.lastRightID[this.count] = lastRightID;
            this.backPos[this.count] = backPos;
            this.backIndex[this.count] = backIndex;
            this.backID[this.count] = backID;
            this.backType[this.count] = backType;
            ++this.count;
        }

        public void addForward(int forwardPos, int forwardIndex, int forwardID, Type forwardType) {
            if (this.forwardCount == this.forwardID.length) {
                this.growForward();
            }
            this.forwardPos[this.forwardCount] = forwardPos;
            this.forwardIndex[this.forwardCount] = forwardIndex;
            this.forwardID[this.forwardCount] = forwardID;
            this.forwardType[this.forwardCount] = forwardType;
            ++this.forwardCount;
        }

        public void reset() {
            this.count = 0;
            assert (this.forwardCount == 0) : "pos=" + this.pos + " forwardCount=" + this.forwardCount;
        }
    }

    public static enum Type {
        KNOWN,
        UNKNOWN,
        USER;

    }

    public static enum Mode {
        NORMAL,
        SEARCH,
        EXTENDED;

    }
}

