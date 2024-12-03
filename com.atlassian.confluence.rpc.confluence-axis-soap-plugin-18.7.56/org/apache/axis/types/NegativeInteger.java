/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.types;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Random;
import org.apache.axis.types.NonPositiveInteger;
import org.apache.axis.utils.Messages;

public class NegativeInteger
extends NonPositiveInteger {
    private BigInteger zero = new BigInteger("0");

    public NegativeInteger(byte[] val) {
        super(val);
        this.checkValidity();
    }

    public NegativeInteger(int signum, byte[] magnitude) {
        super(signum, magnitude);
        this.checkValidity();
    }

    public NegativeInteger(int bitLength, int certainty, Random rnd) {
        super(bitLength, certainty, rnd);
        this.checkValidity();
    }

    public NegativeInteger(int numBits, Random rnd) {
        super(numBits, rnd);
        this.checkValidity();
    }

    public NegativeInteger(String val) {
        super(val);
        this.checkValidity();
    }

    public NegativeInteger(String val, int radix) {
        super(val, radix);
        this.checkValidity();
    }

    private void checkValidity() {
        if (this.compareTo(this.zero) >= 0) {
            throw new NumberFormatException(Messages.getMessage("badnegInt00") + ":  " + this);
        }
    }

    public Object writeReplace() throws ObjectStreamException {
        return new BigIntegerRep(this.toByteArray());
    }

    protected static class BigIntegerRep
    implements Serializable {
        private byte[] array;

        protected BigIntegerRep(byte[] array) {
            this.array = array;
        }

        protected Object readResolve() throws ObjectStreamException {
            return new NegativeInteger(this.array);
        }
    }
}

