/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util.fst;

import com.atlassian.lucene36.util.ArrayUtil;
import com.atlassian.lucene36.util.IntsRef;
import com.atlassian.lucene36.util.fst.FST;
import com.atlassian.lucene36.util.fst.FSTEnum;
import java.io.IOException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class IntsRefFSTEnum<T>
extends FSTEnum<T> {
    private final IntsRef current = new IntsRef(10);
    private final InputOutput<T> result = new InputOutput();
    private IntsRef target;

    public IntsRefFSTEnum(FST<T> fst) {
        super(fst);
        this.result.input = this.current;
        this.current.offset = 1;
    }

    public InputOutput<T> current() {
        return this.result;
    }

    public InputOutput<T> next() throws IOException {
        this.doNext();
        return this.setResult();
    }

    public InputOutput<T> seekCeil(IntsRef target) throws IOException {
        this.target = target;
        this.targetLength = target.length;
        super.doSeekCeil();
        return this.setResult();
    }

    public InputOutput<T> seekFloor(IntsRef target) throws IOException {
        this.target = target;
        this.targetLength = target.length;
        super.doSeekFloor();
        return this.setResult();
    }

    public InputOutput<T> seekExact(IntsRef target) throws IOException {
        this.target = target;
        this.targetLength = target.length;
        if (super.doSeekExact()) {
            assert (this.upto == 1 + target.length);
            return this.setResult();
        }
        return null;
    }

    @Override
    protected int getTargetLabel() {
        if (this.upto - 1 == this.target.length) {
            return -1;
        }
        return this.target.ints[this.target.offset + this.upto - 1];
    }

    @Override
    protected int getCurrentLabel() {
        return this.current.ints[this.upto];
    }

    @Override
    protected void setCurrentLabel(int label) {
        this.current.ints[this.upto] = label;
    }

    @Override
    protected void grow() {
        this.current.ints = ArrayUtil.grow(this.current.ints, this.upto + 1);
    }

    private InputOutput<T> setResult() {
        if (this.upto == 0) {
            return null;
        }
        this.current.length = this.upto - 1;
        this.result.output = this.output[this.upto];
        return this.result;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class InputOutput<T> {
        public IntsRef input;
        public T output;
    }
}

