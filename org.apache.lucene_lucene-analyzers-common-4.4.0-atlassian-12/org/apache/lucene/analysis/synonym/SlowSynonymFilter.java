/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Token
 *  org.apache.lucene.analysis.TokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.OffsetAttribute
 *  org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute
 *  org.apache.lucene.analysis.tokenattributes.TypeAttribute
 *  org.apache.lucene.util.AttributeSource
 */
package org.apache.lucene.analysis.synonym;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.synonym.SlowSynonymMap;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.AttributeSource;

@Deprecated
final class SlowSynonymFilter
extends TokenFilter {
    private final SlowSynonymMap map;
    private Iterator<AttributeSource> replacement;
    private LinkedList<AttributeSource> buffer;
    private LinkedList<AttributeSource> matched;
    private boolean exhausted;

    public SlowSynonymFilter(TokenStream in, SlowSynonymMap map) {
        super(in);
        if (map == null) {
            throw new IllegalArgumentException("map is required");
        }
        this.map = map;
        this.addAttribute(CharTermAttribute.class);
        this.addAttribute(PositionIncrementAttribute.class);
        this.addAttribute(OffsetAttribute.class);
        this.addAttribute(TypeAttribute.class);
    }

    public boolean incrementToken() throws IOException {
        while (true) {
            SlowSynonymMap result;
            if (this.replacement != null && this.replacement.hasNext()) {
                this.copy((AttributeSource)this, this.replacement.next());
                return true;
            }
            AttributeSource firstTok = this.nextTok();
            if (firstTok == null) {
                return false;
            }
            CharTermAttribute termAtt = (CharTermAttribute)firstTok.addAttribute(CharTermAttribute.class);
            SlowSynonymMap slowSynonymMap = result = this.map.submap != null ? this.map.submap.get(termAtt.buffer(), 0, termAtt.length()) : null;
            if (result == null) {
                this.copy((AttributeSource)this, firstTok);
                return true;
            }
            if (firstTok == this) {
                firstTok = this.cloneAttributes();
            }
            this.matched = new LinkedList();
            if ((result = this.match(result)) == null) {
                this.copy((AttributeSource)this, firstTok);
                return true;
            }
            ArrayList<AttributeSource> generated = new ArrayList<AttributeSource>(result.synonyms.length + this.matched.size() + 1);
            AttributeSource lastTok = this.matched.isEmpty() ? firstTok : this.matched.getLast();
            boolean includeOrig = result.includeOrig();
            AttributeSource origTok = includeOrig ? firstTok : null;
            PositionIncrementAttribute firstPosIncAtt = (PositionIncrementAttribute)firstTok.addAttribute(PositionIncrementAttribute.class);
            int origPos = firstPosIncAtt.getPositionIncrement();
            int repPos = 0;
            int pos = 0;
            for (int i = 0; i < result.synonyms.length; ++i) {
                Token repTok = result.synonyms[i];
                AttributeSource newTok = firstTok.cloneAttributes();
                CharTermAttribute newTermAtt = (CharTermAttribute)newTok.addAttribute(CharTermAttribute.class);
                OffsetAttribute newOffsetAtt = (OffsetAttribute)newTok.addAttribute(OffsetAttribute.class);
                PositionIncrementAttribute newPosIncAtt = (PositionIncrementAttribute)newTok.addAttribute(PositionIncrementAttribute.class);
                OffsetAttribute lastOffsetAtt = (OffsetAttribute)lastTok.addAttribute(OffsetAttribute.class);
                newOffsetAtt.setOffset(newOffsetAtt.startOffset(), lastOffsetAtt.endOffset());
                newTermAtt.copyBuffer(repTok.buffer(), 0, repTok.length());
                repPos += repTok.getPositionIncrement();
                if (i == 0) {
                    repPos = origPos;
                }
                while (origTok != null && origPos <= repPos) {
                    PositionIncrementAttribute origPosInc = (PositionIncrementAttribute)origTok.addAttribute(PositionIncrementAttribute.class);
                    origPosInc.setPositionIncrement(origPos - pos);
                    generated.add(origTok);
                    pos += origPosInc.getPositionIncrement();
                    origTok = this.matched.isEmpty() ? null : this.matched.removeFirst();
                    if (origTok == null) continue;
                    origPosInc = (PositionIncrementAttribute)origTok.addAttribute(PositionIncrementAttribute.class);
                    origPos += origPosInc.getPositionIncrement();
                }
                newPosIncAtt.setPositionIncrement(repPos - pos);
                generated.add(newTok);
                pos += newPosIncAtt.getPositionIncrement();
            }
            while (origTok != null) {
                PositionIncrementAttribute origPosInc = (PositionIncrementAttribute)origTok.addAttribute(PositionIncrementAttribute.class);
                origPosInc.setPositionIncrement(origPos - pos);
                generated.add(origTok);
                pos += origPosInc.getPositionIncrement();
                origTok = this.matched.isEmpty() ? null : this.matched.removeFirst();
                if (origTok == null) continue;
                origPosInc = (PositionIncrementAttribute)origTok.addAttribute(PositionIncrementAttribute.class);
                origPos += origPosInc.getPositionIncrement();
            }
            this.replacement = generated.iterator();
        }
    }

    private AttributeSource nextTok() throws IOException {
        if (this.buffer != null && !this.buffer.isEmpty()) {
            return this.buffer.removeFirst();
        }
        if (!this.exhausted && this.input.incrementToken()) {
            return this;
        }
        this.exhausted = true;
        return null;
    }

    private void pushTok(AttributeSource t) {
        if (this.buffer == null) {
            this.buffer = new LinkedList();
        }
        this.buffer.addFirst(t);
    }

    private SlowSynonymMap match(SlowSynonymMap map) throws IOException {
        AttributeSource tok;
        SlowSynonymMap result = null;
        if (map.submap != null && (tok = this.nextTok()) != null) {
            CharTermAttribute termAtt;
            SlowSynonymMap subMap;
            if (tok == this) {
                tok = this.cloneAttributes();
            }
            if ((subMap = map.submap.get((termAtt = (CharTermAttribute)tok.getAttribute(CharTermAttribute.class)).buffer(), 0, termAtt.length())) != null) {
                result = this.match(subMap);
            }
            if (result != null) {
                this.matched.addFirst(tok);
            } else {
                this.pushTok(tok);
            }
        }
        if (result == null && map.synonyms != null) {
            result = map;
        }
        return result;
    }

    private void copy(AttributeSource target, AttributeSource source) {
        if (target != source) {
            source.copyTo(target);
        }
    }

    public void reset() throws IOException {
        this.input.reset();
        this.replacement = null;
        this.exhausted = false;
    }
}

