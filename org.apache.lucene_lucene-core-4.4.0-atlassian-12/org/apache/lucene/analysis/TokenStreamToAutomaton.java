/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.RollingBuffer;
import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.util.automaton.State;
import org.apache.lucene.util.automaton.Transition;

public class TokenStreamToAutomaton {
    private boolean preservePositionIncrements = true;
    public static final int POS_SEP = 256;
    public static final int HOLE = 257;

    public void setPreservePositionIncrements(boolean enablePositionIncrements) {
        this.preservePositionIncrements = enablePositionIncrements;
    }

    protected BytesRef changeToken(BytesRef in) {
        return in;
    }

    public Automaton toAutomaton(TokenStream in) throws IOException {
        Automaton a = new Automaton();
        boolean deterministic = true;
        TermToBytesRefAttribute termBytesAtt = in.addAttribute(TermToBytesRefAttribute.class);
        PositionIncrementAttribute posIncAtt = in.addAttribute(PositionIncrementAttribute.class);
        PositionLengthAttribute posLengthAtt = in.addAttribute(PositionLengthAttribute.class);
        OffsetAttribute offsetAtt = in.addAttribute(OffsetAttribute.class);
        BytesRef term = termBytesAtt.getBytesRef();
        in.reset();
        Positions positions = new Positions();
        int pos = -1;
        Position posData = null;
        int maxOffset = 0;
        while (in.incrementToken()) {
            int posInc = posIncAtt.getPositionIncrement();
            if (!this.preservePositionIncrements && posInc > 1) {
                posInc = 1;
            }
            assert (pos > -1 || posInc > 0);
            if (posInc > 0) {
                posData = (Position)positions.get(pos += posInc);
                assert (posData.leaving == null);
                if (posData.arriving == null) {
                    if (pos == 0) {
                        posData.leaving = a.getInitialState();
                    } else {
                        posData.leaving = new State();
                        TokenStreamToAutomaton.addHoles(a.getInitialState(), positions, pos);
                    }
                } else {
                    posData.leaving = new State();
                    posData.arriving.addTransition(new Transition(256, posData.leaving));
                    if (posInc > 1) {
                        TokenStreamToAutomaton.addHoles(a.getInitialState(), positions, pos);
                    }
                }
                positions.freeBefore(pos);
            } else {
                deterministic = false;
            }
            int endPos = pos + posLengthAtt.getPositionLength();
            termBytesAtt.fillBytesRef();
            BytesRef term2 = this.changeToken(term);
            Position endPosData = (Position)positions.get(endPos);
            if (endPosData.arriving == null) {
                endPosData.arriving = new State();
            }
            State state = posData.leaving;
            for (int byteIDX = 0; byteIDX < term2.length; ++byteIDX) {
                State nextState = byteIDX == term2.length - 1 ? endPosData.arriving : new State();
                state.addTransition(new Transition(term2.bytes[term2.offset + byteIDX] & 0xFF, nextState));
                state = nextState;
            }
            maxOffset = Math.max(maxOffset, offsetAtt.endOffset());
        }
        in.end();
        State endState = null;
        if (offsetAtt.endOffset() > maxOffset) {
            endState = new State();
            endState.setAccept(true);
        }
        ++pos;
        while (pos <= positions.getMaxPos()) {
            posData = (Position)positions.get(pos);
            if (posData.arriving != null) {
                if (endState != null) {
                    posData.arriving.addTransition(new Transition(256, endState));
                } else {
                    posData.arriving.setAccept(true);
                }
            }
            ++pos;
        }
        a.setDeterministic(deterministic);
        return a;
    }

    private static void addHoles(State startState, RollingBuffer<Position> positions, int pos) {
        Position posData = positions.get(pos);
        Position prevPosData = positions.get(pos - 1);
        while (posData.arriving == null || prevPosData.leaving == null) {
            if (posData.arriving == null) {
                posData.arriving = new State();
                posData.arriving.addTransition(new Transition(256, posData.leaving));
            }
            if (prevPosData.leaving == null) {
                prevPosData.leaving = pos == 1 ? startState : new State();
                if (prevPosData.arriving != null) {
                    prevPosData.arriving.addTransition(new Transition(256, prevPosData.leaving));
                }
            }
            prevPosData.leaving.addTransition(new Transition(257, posData.arriving));
            if (--pos <= 0) break;
            posData = prevPosData;
            prevPosData = positions.get(pos - 1);
        }
    }

    private static class Positions
    extends RollingBuffer<Position> {
        private Positions() {
        }

        @Override
        protected Position newInstance() {
            return new Position();
        }
    }

    private static class Position
    implements RollingBuffer.Resettable {
        State arriving;
        State leaving;

        private Position() {
        }

        @Override
        public void reset() {
            this.arriving = null;
            this.leaving = null;
        }
    }
}

