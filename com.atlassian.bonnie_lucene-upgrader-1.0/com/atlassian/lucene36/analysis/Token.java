/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis;

import com.atlassian.lucene36.analysis.tokenattributes.FlagsAttribute;
import com.atlassian.lucene36.analysis.tokenattributes.OffsetAttribute;
import com.atlassian.lucene36.analysis.tokenattributes.PayloadAttribute;
import com.atlassian.lucene36.analysis.tokenattributes.PositionIncrementAttribute;
import com.atlassian.lucene36.analysis.tokenattributes.PositionLengthAttribute;
import com.atlassian.lucene36.analysis.tokenattributes.TermAttributeImpl;
import com.atlassian.lucene36.analysis.tokenattributes.TypeAttribute;
import com.atlassian.lucene36.index.Payload;
import com.atlassian.lucene36.util.Attribute;
import com.atlassian.lucene36.util.AttributeImpl;
import com.atlassian.lucene36.util.AttributeReflector;
import com.atlassian.lucene36.util.AttributeSource;

public class Token
extends TermAttributeImpl
implements TypeAttribute,
PositionIncrementAttribute,
FlagsAttribute,
OffsetAttribute,
PayloadAttribute,
PositionLengthAttribute {
    private int startOffset;
    private int endOffset;
    private String type = "word";
    private int flags;
    private Payload payload;
    private int positionIncrement = 1;
    private int positionLength = 1;
    public static final AttributeSource.AttributeFactory TOKEN_ATTRIBUTE_FACTORY = new TokenAttributeFactory(AttributeSource.AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY);

    public Token() {
    }

    public Token(int start, int end) {
        this.startOffset = start;
        this.endOffset = end;
    }

    public Token(int start, int end, String typ) {
        this.startOffset = start;
        this.endOffset = end;
        this.type = typ;
    }

    public Token(int start, int end, int flags) {
        this.startOffset = start;
        this.endOffset = end;
        this.flags = flags;
    }

    public Token(String text, int start, int end) {
        this.append(text);
        this.startOffset = start;
        this.endOffset = end;
    }

    public Token(String text, int start, int end, String typ) {
        this.append(text);
        this.startOffset = start;
        this.endOffset = end;
        this.type = typ;
    }

    public Token(String text, int start, int end, int flags) {
        this.append(text);
        this.startOffset = start;
        this.endOffset = end;
        this.flags = flags;
    }

    public Token(char[] startTermBuffer, int termBufferOffset, int termBufferLength, int start, int end) {
        this.copyBuffer(startTermBuffer, termBufferOffset, termBufferLength);
        this.startOffset = start;
        this.endOffset = end;
    }

    public void setPositionIncrement(int positionIncrement) {
        if (positionIncrement < 0) {
            throw new IllegalArgumentException("Increment must be zero or greater: " + positionIncrement);
        }
        this.positionIncrement = positionIncrement;
    }

    public int getPositionIncrement() {
        return this.positionIncrement;
    }

    public void setPositionLength(int positionLength) {
        this.positionLength = positionLength;
    }

    public int getPositionLength() {
        return this.positionLength;
    }

    public final int startOffset() {
        return this.startOffset;
    }

    public void setStartOffset(int offset) {
        this.startOffset = offset;
    }

    public final int endOffset() {
        return this.endOffset;
    }

    public void setEndOffset(int offset) {
        this.endOffset = offset;
    }

    public void setOffset(int startOffset, int endOffset) {
        this.startOffset = startOffset;
        this.endOffset = endOffset;
    }

    public final String type() {
        return this.type;
    }

    public final void setType(String type) {
        this.type = type;
    }

    public int getFlags() {
        return this.flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public Payload getPayload() {
        return this.payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    public void clear() {
        super.clear();
        this.payload = null;
        this.positionIncrement = 1;
        this.flags = 0;
        this.endOffset = 0;
        this.startOffset = 0;
        this.type = "word";
    }

    public Object clone() {
        Token t = (Token)super.clone();
        if (this.payload != null) {
            t.payload = (Payload)this.payload.clone();
        }
        return t;
    }

    public Token clone(char[] newTermBuffer, int newTermOffset, int newTermLength, int newStartOffset, int newEndOffset) {
        Token t = new Token(newTermBuffer, newTermOffset, newTermLength, newStartOffset, newEndOffset);
        t.positionIncrement = this.positionIncrement;
        t.flags = this.flags;
        t.type = this.type;
        if (this.payload != null) {
            t.payload = (Payload)this.payload.clone();
        }
        return t;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Token) {
            Token other = (Token)obj;
            return this.startOffset == other.startOffset && this.endOffset == other.endOffset && this.flags == other.flags && this.positionIncrement == other.positionIncrement && (this.type == null ? other.type == null : this.type.equals(other.type)) && (this.payload == null ? other.payload == null : this.payload.equals(other.payload)) && super.equals(obj);
        }
        return false;
    }

    public int hashCode() {
        int code = super.hashCode();
        code = code * 31 + this.startOffset;
        code = code * 31 + this.endOffset;
        code = code * 31 + this.flags;
        code = code * 31 + this.positionIncrement;
        if (this.type != null) {
            code = code * 31 + this.type.hashCode();
        }
        if (this.payload != null) {
            code = code * 31 + this.payload.hashCode();
        }
        return code;
    }

    private void clearNoTermBuffer() {
        this.payload = null;
        this.positionIncrement = 1;
        this.flags = 0;
        this.endOffset = 0;
        this.startOffset = 0;
        this.type = "word";
    }

    public Token reinit(char[] newTermBuffer, int newTermOffset, int newTermLength, int newStartOffset, int newEndOffset, String newType) {
        this.clearNoTermBuffer();
        this.copyBuffer(newTermBuffer, newTermOffset, newTermLength);
        this.payload = null;
        this.positionIncrement = 1;
        this.startOffset = newStartOffset;
        this.endOffset = newEndOffset;
        this.type = newType;
        return this;
    }

    public Token reinit(char[] newTermBuffer, int newTermOffset, int newTermLength, int newStartOffset, int newEndOffset) {
        this.clearNoTermBuffer();
        this.copyBuffer(newTermBuffer, newTermOffset, newTermLength);
        this.startOffset = newStartOffset;
        this.endOffset = newEndOffset;
        this.type = "word";
        return this;
    }

    public Token reinit(String newTerm, int newStartOffset, int newEndOffset, String newType) {
        this.clear();
        this.append(newTerm);
        this.startOffset = newStartOffset;
        this.endOffset = newEndOffset;
        this.type = newType;
        return this;
    }

    public Token reinit(String newTerm, int newTermOffset, int newTermLength, int newStartOffset, int newEndOffset, String newType) {
        this.clear();
        this.append(newTerm, newTermOffset, newTermOffset + newTermLength);
        this.startOffset = newStartOffset;
        this.endOffset = newEndOffset;
        this.type = newType;
        return this;
    }

    public Token reinit(String newTerm, int newStartOffset, int newEndOffset) {
        this.clear();
        this.append(newTerm);
        this.startOffset = newStartOffset;
        this.endOffset = newEndOffset;
        this.type = "word";
        return this;
    }

    public Token reinit(String newTerm, int newTermOffset, int newTermLength, int newStartOffset, int newEndOffset) {
        this.clear();
        this.append(newTerm, newTermOffset, newTermOffset + newTermLength);
        this.startOffset = newStartOffset;
        this.endOffset = newEndOffset;
        this.type = "word";
        return this;
    }

    public void reinit(Token prototype) {
        this.copyBuffer(prototype.buffer(), 0, prototype.length());
        this.positionIncrement = prototype.positionIncrement;
        this.flags = prototype.flags;
        this.startOffset = prototype.startOffset;
        this.endOffset = prototype.endOffset;
        this.type = prototype.type;
        this.payload = prototype.payload;
    }

    public void reinit(Token prototype, String newTerm) {
        this.setEmpty().append(newTerm);
        this.positionIncrement = prototype.positionIncrement;
        this.flags = prototype.flags;
        this.startOffset = prototype.startOffset;
        this.endOffset = prototype.endOffset;
        this.type = prototype.type;
        this.payload = prototype.payload;
    }

    public void reinit(Token prototype, char[] newTermBuffer, int offset, int length) {
        this.copyBuffer(newTermBuffer, offset, length);
        this.positionIncrement = prototype.positionIncrement;
        this.flags = prototype.flags;
        this.startOffset = prototype.startOffset;
        this.endOffset = prototype.endOffset;
        this.type = prototype.type;
        this.payload = prototype.payload;
    }

    public void copyTo(AttributeImpl target) {
        if (target instanceof Token) {
            Token to = (Token)target;
            to.reinit(this);
            if (this.payload != null) {
                to.payload = (Payload)this.payload.clone();
            }
        } else {
            super.copyTo(target);
            ((OffsetAttribute)((Object)target)).setOffset(this.startOffset, this.endOffset);
            ((PositionIncrementAttribute)((Object)target)).setPositionIncrement(this.positionIncrement);
            ((PayloadAttribute)((Object)target)).setPayload(this.payload == null ? null : (Payload)this.payload.clone());
            ((FlagsAttribute)((Object)target)).setFlags(this.flags);
            ((TypeAttribute)((Object)target)).setType(this.type);
        }
    }

    public void reflectWith(AttributeReflector reflector) {
        super.reflectWith(reflector);
        reflector.reflect(OffsetAttribute.class, "startOffset", this.startOffset);
        reflector.reflect(OffsetAttribute.class, "endOffset", this.endOffset);
        reflector.reflect(PositionIncrementAttribute.class, "positionIncrement", this.positionIncrement);
        reflector.reflect(PayloadAttribute.class, "payload", this.payload);
        reflector.reflect(FlagsAttribute.class, "flags", this.flags);
        reflector.reflect(TypeAttribute.class, "type", this.type);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static final class TokenAttributeFactory
    extends AttributeSource.AttributeFactory {
        private final AttributeSource.AttributeFactory delegate;

        public TokenAttributeFactory(AttributeSource.AttributeFactory delegate) {
            this.delegate = delegate;
        }

        @Override
        public AttributeImpl createAttributeInstance(Class<? extends Attribute> attClass) {
            return attClass.isAssignableFrom(Token.class) ? new Token() : this.delegate.createAttributeInstance(attClass);
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other instanceof TokenAttributeFactory) {
                TokenAttributeFactory af = (TokenAttributeFactory)other;
                return this.delegate.equals(af.delegate);
            }
            return false;
        }

        public int hashCode() {
            return this.delegate.hashCode() ^ 0xA45AA31;
        }
    }
}

