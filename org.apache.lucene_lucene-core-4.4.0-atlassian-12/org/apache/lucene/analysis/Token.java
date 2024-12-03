/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis;

import org.apache.lucene.analysis.tokenattributes.CharTermAttributeImpl;
import org.apache.lucene.analysis.tokenattributes.FlagsAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.Attribute;
import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.AttributeReflector;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.BytesRef;

public class Token
extends CharTermAttributeImpl
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
    private BytesRef payload;
    private int positionIncrement = 1;
    private int positionLength = 1;
    public static final AttributeSource.AttributeFactory TOKEN_ATTRIBUTE_FACTORY = new TokenAttributeFactory(AttributeSource.AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY);

    public Token() {
    }

    public Token(int start, int end) {
        this.checkOffsets(start, end);
        this.startOffset = start;
        this.endOffset = end;
    }

    public Token(int start, int end, String typ) {
        this.checkOffsets(start, end);
        this.startOffset = start;
        this.endOffset = end;
        this.type = typ;
    }

    public Token(int start, int end, int flags) {
        this.checkOffsets(start, end);
        this.startOffset = start;
        this.endOffset = end;
        this.flags = flags;
    }

    public Token(String text, int start, int end) {
        this.checkOffsets(start, end);
        this.append(text);
        this.startOffset = start;
        this.endOffset = end;
    }

    public Token(String text, int start, int end, String typ) {
        this.checkOffsets(start, end);
        this.append(text);
        this.startOffset = start;
        this.endOffset = end;
        this.type = typ;
    }

    public Token(String text, int start, int end, int flags) {
        this.checkOffsets(start, end);
        this.append(text);
        this.startOffset = start;
        this.endOffset = end;
        this.flags = flags;
    }

    public Token(char[] startTermBuffer, int termBufferOffset, int termBufferLength, int start, int end) {
        this.checkOffsets(start, end);
        this.copyBuffer(startTermBuffer, termBufferOffset, termBufferLength);
        this.startOffset = start;
        this.endOffset = end;
    }

    @Override
    public void setPositionIncrement(int positionIncrement) {
        if (positionIncrement < 0) {
            throw new IllegalArgumentException("Increment must be zero or greater: " + positionIncrement);
        }
        this.positionIncrement = positionIncrement;
    }

    @Override
    public int getPositionIncrement() {
        return this.positionIncrement;
    }

    @Override
    public void setPositionLength(int positionLength) {
        this.positionLength = positionLength;
    }

    @Override
    public int getPositionLength() {
        return this.positionLength;
    }

    @Override
    public final int startOffset() {
        return this.startOffset;
    }

    @Override
    public final int endOffset() {
        return this.endOffset;
    }

    @Override
    public void setOffset(int startOffset, int endOffset) {
        this.checkOffsets(startOffset, endOffset);
        this.startOffset = startOffset;
        this.endOffset = endOffset;
    }

    @Override
    public final String type() {
        return this.type;
    }

    @Override
    public final void setType(String type) {
        this.type = type;
    }

    @Override
    public int getFlags() {
        return this.flags;
    }

    @Override
    public void setFlags(int flags) {
        this.flags = flags;
    }

    @Override
    public BytesRef getPayload() {
        return this.payload;
    }

    @Override
    public void setPayload(BytesRef payload) {
        this.payload = payload;
    }

    @Override
    public void clear() {
        super.clear();
        this.payload = null;
        this.positionIncrement = 1;
        this.flags = 0;
        this.endOffset = 0;
        this.startOffset = 0;
        this.type = "word";
    }

    @Override
    public Token clone() {
        Token t = (Token)super.clone();
        if (this.payload != null) {
            t.payload = this.payload.clone();
        }
        return t;
    }

    public Token clone(char[] newTermBuffer, int newTermOffset, int newTermLength, int newStartOffset, int newEndOffset) {
        Token t = new Token(newTermBuffer, newTermOffset, newTermLength, newStartOffset, newEndOffset);
        t.positionIncrement = this.positionIncrement;
        t.flags = this.flags;
        t.type = this.type;
        if (this.payload != null) {
            t.payload = this.payload.clone();
        }
        return t;
    }

    @Override
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

    @Override
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
        this.checkOffsets(newStartOffset, newEndOffset);
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
        this.checkOffsets(newStartOffset, newEndOffset);
        this.clearNoTermBuffer();
        this.copyBuffer(newTermBuffer, newTermOffset, newTermLength);
        this.startOffset = newStartOffset;
        this.endOffset = newEndOffset;
        this.type = "word";
        return this;
    }

    public Token reinit(String newTerm, int newStartOffset, int newEndOffset, String newType) {
        this.checkOffsets(newStartOffset, newEndOffset);
        this.clear();
        this.append(newTerm);
        this.startOffset = newStartOffset;
        this.endOffset = newEndOffset;
        this.type = newType;
        return this;
    }

    public Token reinit(String newTerm, int newTermOffset, int newTermLength, int newStartOffset, int newEndOffset, String newType) {
        this.checkOffsets(newStartOffset, newEndOffset);
        this.clear();
        this.append(newTerm, newTermOffset, newTermOffset + newTermLength);
        this.startOffset = newStartOffset;
        this.endOffset = newEndOffset;
        this.type = newType;
        return this;
    }

    public Token reinit(String newTerm, int newStartOffset, int newEndOffset) {
        this.checkOffsets(newStartOffset, newEndOffset);
        this.clear();
        this.append(newTerm);
        this.startOffset = newStartOffset;
        this.endOffset = newEndOffset;
        this.type = "word";
        return this;
    }

    public Token reinit(String newTerm, int newTermOffset, int newTermLength, int newStartOffset, int newEndOffset) {
        this.checkOffsets(newStartOffset, newEndOffset);
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

    @Override
    public void copyTo(AttributeImpl target) {
        if (target instanceof Token) {
            Token to = (Token)target;
            to.reinit(this);
            if (this.payload != null) {
                to.payload = this.payload.clone();
            }
        } else {
            super.copyTo(target);
            ((OffsetAttribute)((Object)target)).setOffset(this.startOffset, this.endOffset);
            ((PositionIncrementAttribute)((Object)target)).setPositionIncrement(this.positionIncrement);
            ((PayloadAttribute)((Object)target)).setPayload(this.payload == null ? null : this.payload.clone());
            ((FlagsAttribute)((Object)target)).setFlags(this.flags);
            ((TypeAttribute)((Object)target)).setType(this.type);
        }
    }

    @Override
    public void reflectWith(AttributeReflector reflector) {
        super.reflectWith(reflector);
        reflector.reflect(OffsetAttribute.class, "startOffset", this.startOffset);
        reflector.reflect(OffsetAttribute.class, "endOffset", this.endOffset);
        reflector.reflect(PositionIncrementAttribute.class, "positionIncrement", this.positionIncrement);
        reflector.reflect(PayloadAttribute.class, "payload", this.payload);
        reflector.reflect(FlagsAttribute.class, "flags", this.flags);
        reflector.reflect(TypeAttribute.class, "type", this.type);
    }

    private void checkOffsets(int startOffset, int endOffset) {
        if (startOffset < 0 || endOffset < startOffset) {
            throw new IllegalArgumentException("startOffset must be non-negative, and endOffset must be >= startOffset, startOffset=" + startOffset + ",endOffset=" + endOffset);
        }
    }

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

