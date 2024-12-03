/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import org.aspectj.weaver.BCException;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.patterns.AndSignaturePattern;
import org.aspectj.weaver.patterns.ISignaturePattern;
import org.aspectj.weaver.patterns.NotSignaturePattern;
import org.aspectj.weaver.patterns.OrSignaturePattern;
import org.aspectj.weaver.patterns.SignaturePattern;

public abstract class AbstractSignaturePattern
implements ISignaturePattern {
    protected void writePlaceholderLocation(CompressingDataOutputStream s) throws IOException {
        s.writeInt(0);
        s.writeInt(0);
    }

    public static ISignaturePattern readCompoundSignaturePattern(VersionedDataInputStream s, ISourceContext context) throws IOException {
        byte key = s.readByte();
        switch (key) {
            case 1: {
                return SignaturePattern.read(s, context);
            }
            case 4: {
                return AndSignaturePattern.readAndSignaturePattern(s, context);
            }
            case 3: {
                return OrSignaturePattern.readOrSignaturePattern(s, context);
            }
            case 2: {
                return NotSignaturePattern.readNotSignaturePattern(s, context);
            }
        }
        throw new BCException("unknown SignatureTypePattern kind: " + key);
    }

    public static void writeCompoundSignaturePattern(CompressingDataOutputStream s, ISignaturePattern sigPattern) throws IOException {
        if (sigPattern instanceof SignaturePattern) {
            s.writeByte(1);
            ((SignaturePattern)sigPattern).write(s);
        } else if (sigPattern instanceof AndSignaturePattern) {
            AndSignaturePattern andSignaturePattern = (AndSignaturePattern)sigPattern;
            s.writeByte(4);
            AbstractSignaturePattern.writeCompoundSignaturePattern(s, andSignaturePattern.getLeft());
            AbstractSignaturePattern.writeCompoundSignaturePattern(s, andSignaturePattern.getRight());
            s.writeInt(0);
            s.writeInt(0);
        } else if (sigPattern instanceof OrSignaturePattern) {
            OrSignaturePattern orSignaturePattern = (OrSignaturePattern)sigPattern;
            s.writeByte(3);
            AbstractSignaturePattern.writeCompoundSignaturePattern(s, orSignaturePattern.getLeft());
            AbstractSignaturePattern.writeCompoundSignaturePattern(s, orSignaturePattern.getRight());
            s.writeInt(0);
            s.writeInt(0);
        } else {
            NotSignaturePattern notSignaturePattern = (NotSignaturePattern)sigPattern;
            s.writeByte(2);
            AbstractSignaturePattern.writeCompoundSignaturePattern(s, notSignaturePattern.getNegated());
            s.writeInt(0);
            s.writeInt(0);
        }
    }
}

