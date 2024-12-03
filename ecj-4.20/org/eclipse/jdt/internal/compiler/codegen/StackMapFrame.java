/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.codegen;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jdt.internal.compiler.codegen.VerificationTypeInfo;
import org.eclipse.jdt.internal.compiler.lookup.Scope;

public class StackMapFrame {
    public static final int USED = 1;
    public static final int SAME_FRAME = 0;
    public static final int CHOP_FRAME = 1;
    public static final int APPEND_FRAME = 2;
    public static final int SAME_FRAME_EXTENDED = 3;
    public static final int FULL_FRAME = 4;
    public static final int SAME_LOCALS_1_STACK_ITEMS = 5;
    public static final int SAME_LOCALS_1_STACK_ITEMS_EXTENDED = 6;
    public int pc;
    public int numberOfStackItems;
    private int numberOfLocals;
    public int localIndex;
    public VerificationTypeInfo[] locals;
    public VerificationTypeInfo[] stackItems;
    private int numberOfDifferentLocals = -1;
    public int tagBits;

    public StackMapFrame(int initialLocalSize) {
        this.locals = new VerificationTypeInfo[initialLocalSize];
        this.numberOfLocals = -1;
        this.numberOfDifferentLocals = -1;
    }

    public int getFrameType(StackMapFrame prevFrame) {
        int offsetDelta = this.getOffsetDelta(prevFrame);
        switch (this.numberOfStackItems) {
            case 0: {
                switch (this.numberOfDifferentLocals(prevFrame)) {
                    case 0: {
                        return offsetDelta <= 63 ? 0 : 3;
                    }
                    case 1: 
                    case 2: 
                    case 3: {
                        return 2;
                    }
                    case -3: 
                    case -2: 
                    case -1: {
                        return 1;
                    }
                }
                break;
            }
            case 1: {
                switch (this.numberOfDifferentLocals(prevFrame)) {
                    case 0: {
                        return offsetDelta <= 63 ? 5 : 6;
                    }
                }
            }
        }
        return 4;
    }

    public void addLocal(int resolvedPosition, VerificationTypeInfo info) {
        if (this.locals == null) {
            this.locals = new VerificationTypeInfo[resolvedPosition + 1];
            this.locals[resolvedPosition] = info;
        } else {
            int length = this.locals.length;
            if (resolvedPosition >= length) {
                this.locals = new VerificationTypeInfo[resolvedPosition + 1];
                System.arraycopy(this.locals, 0, this.locals, 0, length);
            }
            this.locals[resolvedPosition] = info;
        }
    }

    public void addStackItem(VerificationTypeInfo info) {
        if (info == null) {
            throw new IllegalArgumentException("info cannot be null");
        }
        if (this.stackItems == null) {
            this.stackItems = new VerificationTypeInfo[1];
            this.stackItems[0] = info;
            this.numberOfStackItems = 1;
        } else {
            int length = this.stackItems.length;
            if (this.numberOfStackItems == length) {
                this.stackItems = new VerificationTypeInfo[length + 1];
                System.arraycopy(this.stackItems, 0, this.stackItems, 0, length);
            }
            this.stackItems[this.numberOfStackItems++] = info;
        }
    }

    public StackMapFrame duplicate() {
        VerificationTypeInfo verificationTypeInfo;
        int i;
        HashMap<VerificationTypeInfo, VerificationTypeInfo> cache = new HashMap<VerificationTypeInfo, VerificationTypeInfo>();
        int length = this.locals.length;
        StackMapFrame result = new StackMapFrame(length);
        result.numberOfLocals = -1;
        result.numberOfDifferentLocals = -1;
        result.pc = this.pc;
        result.numberOfStackItems = this.numberOfStackItems;
        if (length != 0) {
            result.locals = new VerificationTypeInfo[length];
            i = 0;
            while (i < length) {
                verificationTypeInfo = this.locals[i];
                result.locals[i] = StackMapFrame.getCachedValue(cache, verificationTypeInfo);
                ++i;
            }
        }
        if ((length = this.numberOfStackItems) != 0) {
            result.stackItems = new VerificationTypeInfo[length];
            i = 0;
            while (i < length) {
                verificationTypeInfo = this.stackItems[i];
                result.stackItems[i] = StackMapFrame.getCachedValue(cache, verificationTypeInfo);
                ++i;
            }
        }
        return result;
    }

    private static VerificationTypeInfo getCachedValue(Map<VerificationTypeInfo, VerificationTypeInfo> cache, VerificationTypeInfo value) {
        VerificationTypeInfo cachedValue = value;
        if (value != null) {
            if (value.tag == 8 || value.tag == 6) {
                cachedValue = cache.get(value);
                if (cachedValue == null) {
                    cachedValue = value.duplicate();
                    cache.put(value, cachedValue);
                }
            } else {
                cachedValue = value.duplicate();
            }
        }
        return cachedValue;
    }

    public int numberOfDifferentLocals(StackMapFrame prevFrame) {
        if (this.numberOfDifferentLocals != -1) {
            return this.numberOfDifferentLocals;
        }
        if (prevFrame == null) {
            this.numberOfDifferentLocals = 0;
            return 0;
        }
        VerificationTypeInfo[] prevLocals = prevFrame.locals;
        VerificationTypeInfo[] currentLocals = this.locals;
        int prevLocalsLength = prevLocals == null ? 0 : prevLocals.length;
        int currentLocalsLength = currentLocals == null ? 0 : currentLocals.length;
        int prevNumberOfLocals = prevFrame.getNumberOfLocals();
        int currentNumberOfLocals = this.getNumberOfLocals();
        int result = 0;
        if (prevNumberOfLocals == 0) {
            if (currentNumberOfLocals != 0) {
                result = currentNumberOfLocals;
                int counter = 0;
                int i = 0;
                while (i < currentLocalsLength && counter < currentNumberOfLocals) {
                    if (currentLocals[i] != null) {
                        switch (currentLocals[i].id()) {
                            case 7: 
                            case 8: {
                                ++i;
                            }
                        }
                        ++counter;
                    } else {
                        this.numberOfDifferentLocals = result = Integer.MAX_VALUE;
                        return result;
                    }
                    ++i;
                }
            }
        } else if (currentNumberOfLocals == 0) {
            int counter = 0;
            result = -prevNumberOfLocals;
            int i = 0;
            while (i < prevLocalsLength && counter < prevNumberOfLocals) {
                if (prevLocals[i] != null) {
                    switch (prevLocals[i].id()) {
                        case 7: 
                        case 8: {
                            ++i;
                        }
                    }
                    ++counter;
                } else {
                    this.numberOfDifferentLocals = result = Integer.MAX_VALUE;
                    return result;
                }
                ++i;
            }
        } else {
            VerificationTypeInfo currentLocal;
            int indexInPrevLocals = 0;
            int indexInCurrentLocals = 0;
            int currentLocalsCounter = 0;
            int prevLocalsCounter = 0;
            while (indexInCurrentLocals < currentLocalsLength && currentLocalsCounter < currentNumberOfLocals) {
                currentLocal = currentLocals[indexInCurrentLocals];
                if (currentLocal != null) {
                    ++currentLocalsCounter;
                    switch (currentLocal.id()) {
                        case 7: 
                        case 8: {
                            ++indexInCurrentLocals;
                        }
                    }
                }
                if (indexInPrevLocals >= prevLocalsLength || prevLocalsCounter >= prevNumberOfLocals) {
                    if (currentLocal != null) {
                        ++result;
                    } else {
                        this.numberOfDifferentLocals = result = Integer.MAX_VALUE;
                        return result;
                    }
                    ++indexInCurrentLocals;
                    break;
                }
                VerificationTypeInfo prevLocal = prevLocals[indexInPrevLocals];
                if (prevLocal != null) {
                    ++prevLocalsCounter;
                    switch (prevLocal.id()) {
                        case 7: 
                        case 8: {
                            ++indexInPrevLocals;
                        }
                    }
                }
                if (StackMapFrame.equals(prevLocal, currentLocal) && indexInPrevLocals == indexInCurrentLocals) {
                    if (result != 0) {
                        this.numberOfDifferentLocals = result = Integer.MAX_VALUE;
                        return result;
                    }
                } else {
                    this.numberOfDifferentLocals = result = Integer.MAX_VALUE;
                    return result;
                }
                ++indexInPrevLocals;
                ++indexInCurrentLocals;
            }
            if (currentLocalsCounter < currentNumberOfLocals) {
                while (indexInCurrentLocals < currentLocalsLength && currentLocalsCounter < currentNumberOfLocals) {
                    currentLocal = currentLocals[indexInCurrentLocals];
                    if (currentLocal == null) {
                        this.numberOfDifferentLocals = result = Integer.MAX_VALUE;
                        return result;
                    }
                    ++result;
                    ++currentLocalsCounter;
                    switch (currentLocal.id()) {
                        case 7: 
                        case 8: {
                            ++indexInCurrentLocals;
                        }
                    }
                    ++indexInCurrentLocals;
                }
            } else if (prevLocalsCounter < prevNumberOfLocals) {
                result = -result;
                while (indexInPrevLocals < prevLocalsLength && prevLocalsCounter < prevNumberOfLocals) {
                    VerificationTypeInfo prevLocal = prevLocals[indexInPrevLocals];
                    if (prevLocal == null) {
                        this.numberOfDifferentLocals = result = Integer.MAX_VALUE;
                        return result;
                    }
                    --result;
                    ++prevLocalsCounter;
                    switch (prevLocal.id()) {
                        case 7: 
                        case 8: {
                            ++indexInPrevLocals;
                        }
                    }
                    ++indexInPrevLocals;
                }
            }
        }
        this.numberOfDifferentLocals = result;
        return result;
    }

    public int getNumberOfLocals() {
        if (this.numberOfLocals != -1) {
            return this.numberOfLocals;
        }
        int result = 0;
        int length = this.locals == null ? 0 : this.locals.length;
        int i = 0;
        while (i < length) {
            if (this.locals[i] != null) {
                switch (this.locals[i].id()) {
                    case 7: 
                    case 8: {
                        ++i;
                    }
                }
                ++result;
            }
            ++i;
        }
        this.numberOfLocals = result;
        return result;
    }

    public int getOffsetDelta(StackMapFrame prevFrame) {
        if (prevFrame == null) {
            return this.pc;
        }
        return prevFrame.pc == -1 ? this.pc : this.pc - prevFrame.pc - 1;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        this.printFrame(buffer, this);
        return String.valueOf(buffer);
    }

    private void printFrame(StringBuffer buffer, StackMapFrame frame) {
        String pattern = "[pc : {0} locals: {1} stack items: {2}\nlocals: {3}\nstack: {4}\n]";
        int localsLength = frame.locals == null ? 0 : frame.locals.length;
        buffer.append(MessageFormat.format(pattern, Integer.toString(frame.pc), Integer.toString(frame.getNumberOfLocals()), Integer.toString(frame.numberOfStackItems), this.print(frame.locals, localsLength), this.print(frame.stackItems, frame.numberOfStackItems)));
    }

    private String print(VerificationTypeInfo[] infos, int length) {
        StringBuffer buffer = new StringBuffer();
        buffer.append('[');
        if (infos != null) {
            int i = 0;
            while (i < length) {
                VerificationTypeInfo verificationTypeInfo;
                if (i != 0) {
                    buffer.append(',');
                }
                if ((verificationTypeInfo = infos[i]) == null) {
                    buffer.append("top");
                } else {
                    buffer.append(verificationTypeInfo);
                }
                ++i;
            }
        }
        buffer.append(']');
        return String.valueOf(buffer);
    }

    public void putLocal(int resolvedPosition, VerificationTypeInfo info) {
        if (this.locals == null) {
            this.locals = new VerificationTypeInfo[resolvedPosition + 1];
            this.locals[resolvedPosition] = info;
        } else {
            int length = this.locals.length;
            if (resolvedPosition >= length) {
                this.locals = new VerificationTypeInfo[resolvedPosition + 1];
                System.arraycopy(this.locals, 0, this.locals, 0, length);
            }
            this.locals[resolvedPosition] = info;
        }
    }

    public void replaceWithElementType() {
        VerificationTypeInfo info = this.stackItems[this.numberOfStackItems - 1];
        VerificationTypeInfo info2 = info.duplicate();
        info2.replaceWithElementType();
        this.stackItems[this.numberOfStackItems - 1] = info2;
    }

    public int getIndexOfDifferentLocals(int differentLocalsCount) {
        int i = this.locals.length - 1;
        while (i >= 0) {
            VerificationTypeInfo currentLocal = this.locals[i];
            if (currentLocal != null && --differentLocalsCount == 0) {
                return i;
            }
            --i;
        }
        return 0;
    }

    private static boolean equals(VerificationTypeInfo info, VerificationTypeInfo info2) {
        if (info == null) {
            return info2 == null;
        }
        if (info2 == null) {
            return false;
        }
        return info.equals(info2);
    }

    public StackMapFrame merge(StackMapFrame frame, Scope scope) {
        if (frame.pc == -1) {
            return this;
        }
        if (this.numberOfStackItems == frame.numberOfStackItems) {
            int i = 0;
            int max = this.numberOfStackItems;
            while (i < max) {
                this.stackItems[i] = this.stackItems[i].merge(frame.stackItems[i], scope);
                ++i;
            }
        }
        return this;
    }
}

