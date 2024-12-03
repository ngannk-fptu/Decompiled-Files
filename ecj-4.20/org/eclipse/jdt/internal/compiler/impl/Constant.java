/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.impl;

import org.eclipse.jdt.internal.compiler.ast.OperatorIds;
import org.eclipse.jdt.internal.compiler.impl.BooleanConstant;
import org.eclipse.jdt.internal.compiler.impl.ByteConstant;
import org.eclipse.jdt.internal.compiler.impl.CharConstant;
import org.eclipse.jdt.internal.compiler.impl.DoubleConstant;
import org.eclipse.jdt.internal.compiler.impl.FloatConstant;
import org.eclipse.jdt.internal.compiler.impl.IntConstant;
import org.eclipse.jdt.internal.compiler.impl.LongConstant;
import org.eclipse.jdt.internal.compiler.impl.ShortConstant;
import org.eclipse.jdt.internal.compiler.impl.StringConstant;
import org.eclipse.jdt.internal.compiler.lookup.TypeIds;
import org.eclipse.jdt.internal.compiler.problem.ShouldNotImplement;
import org.eclipse.jdt.internal.compiler.util.Messages;

public abstract class Constant
implements TypeIds,
OperatorIds {
    public static final Constant NotAConstant = DoubleConstant.fromValue(Double.NaN);
    public static final Constant[] NotAConstantList = new Constant[]{DoubleConstant.fromValue(Double.NaN)};

    public boolean booleanValue() {
        throw new ShouldNotImplement(Messages.bind(Messages.constant_cannotCastedInto, new String[]{this.typeName(), "boolean"}));
    }

    public byte byteValue() {
        throw new ShouldNotImplement(Messages.bind(Messages.constant_cannotCastedInto, new String[]{this.typeName(), "byte"}));
    }

    public final Constant castTo(int conversionToTargetType) {
        if (this == NotAConstant) {
            return NotAConstant;
        }
        switch (conversionToTargetType) {
            case 0: {
                return this;
            }
            case 51: {
                return this;
            }
            case 55: {
                return ByteConstant.fromValue((byte)this.longValue());
            }
            case 52: {
                return ByteConstant.fromValue((byte)this.shortValue());
            }
            case 56: {
                return ByteConstant.fromValue((byte)this.doubleValue());
            }
            case 57: {
                return ByteConstant.fromValue((byte)this.floatValue());
            }
            case 50: {
                return ByteConstant.fromValue((byte)this.charValue());
            }
            case 58: {
                return ByteConstant.fromValue((byte)this.intValue());
            }
            case 115: {
                return LongConstant.fromValue(this.byteValue());
            }
            case 119: {
                return this;
            }
            case 116: {
                return LongConstant.fromValue(this.shortValue());
            }
            case 120: {
                return LongConstant.fromValue((long)this.doubleValue());
            }
            case 121: {
                return LongConstant.fromValue((long)this.floatValue());
            }
            case 114: {
                return LongConstant.fromValue(this.charValue());
            }
            case 122: {
                return LongConstant.fromValue(this.intValue());
            }
            case 67: {
                return ShortConstant.fromValue(this.byteValue());
            }
            case 71: {
                return ShortConstant.fromValue((short)this.longValue());
            }
            case 68: {
                return this;
            }
            case 72: {
                return ShortConstant.fromValue((short)this.doubleValue());
            }
            case 73: {
                return ShortConstant.fromValue((short)this.floatValue());
            }
            case 66: {
                return ShortConstant.fromValue((short)this.charValue());
            }
            case 74: {
                return ShortConstant.fromValue((short)this.intValue());
            }
            case 187: {
                return this;
            }
            case 131: {
                return DoubleConstant.fromValue(this.byteValue());
            }
            case 135: {
                return DoubleConstant.fromValue(this.longValue());
            }
            case 132: {
                return DoubleConstant.fromValue(this.shortValue());
            }
            case 136: {
                return this;
            }
            case 137: {
                return DoubleConstant.fromValue(this.floatValue());
            }
            case 130: {
                return DoubleConstant.fromValue(this.charValue());
            }
            case 138: {
                return DoubleConstant.fromValue(this.intValue());
            }
            case 147: {
                return FloatConstant.fromValue(this.byteValue());
            }
            case 151: {
                return FloatConstant.fromValue(this.longValue());
            }
            case 148: {
                return FloatConstant.fromValue(this.shortValue());
            }
            case 152: {
                return FloatConstant.fromValue((float)this.doubleValue());
            }
            case 153: {
                return this;
            }
            case 146: {
                return FloatConstant.fromValue(this.charValue());
            }
            case 154: {
                return FloatConstant.fromValue(this.intValue());
            }
            case 85: {
                return this;
            }
            case 35: {
                return CharConstant.fromValue((char)this.byteValue());
            }
            case 39: {
                return CharConstant.fromValue((char)this.longValue());
            }
            case 36: {
                return CharConstant.fromValue((char)this.shortValue());
            }
            case 40: {
                return CharConstant.fromValue((char)this.doubleValue());
            }
            case 41: {
                return CharConstant.fromValue((char)this.floatValue());
            }
            case 34: {
                return this;
            }
            case 42: {
                return CharConstant.fromValue((char)this.intValue());
            }
            case 163: {
                return IntConstant.fromValue(this.byteValue());
            }
            case 167: {
                return IntConstant.fromValue((int)this.longValue());
            }
            case 164: {
                return IntConstant.fromValue(this.shortValue());
            }
            case 168: {
                return IntConstant.fromValue((int)this.doubleValue());
            }
            case 169: {
                return IntConstant.fromValue((int)this.floatValue());
            }
            case 162: {
                return IntConstant.fromValue(this.charValue());
            }
            case 170: {
                return this;
            }
        }
        return NotAConstant;
    }

    public char charValue() {
        throw new ShouldNotImplement(Messages.bind(Messages.constant_cannotCastedInto, new String[]{this.typeName(), "char"}));
    }

    public static final Constant computeConstantOperation(Constant cst, int id, int operator) {
        switch (operator) {
            case 11: {
                return BooleanConstant.fromValue(!cst.booleanValue());
            }
            case 14: {
                return Constant.computeConstantOperationPLUS(IntConstant.fromValue(0), 10, cst, id);
            }
            case 13: {
                switch (id) {
                    case 9: {
                        float f = cst.floatValue();
                        if (f != 0.0f) break;
                        if (Float.floatToIntBits(f) == 0) {
                            return FloatConstant.fromValue(-0.0f);
                        }
                        return FloatConstant.fromValue(0.0f);
                    }
                    case 8: {
                        double d = cst.doubleValue();
                        if (d != 0.0) break;
                        if (Double.doubleToLongBits(d) == 0L) {
                            return DoubleConstant.fromValue(-0.0);
                        }
                        return DoubleConstant.fromValue(0.0);
                    }
                }
                return Constant.computeConstantOperationMINUS(IntConstant.fromValue(0), 10, cst, id);
            }
            case 12: {
                switch (id) {
                    case 2: {
                        return IntConstant.fromValue(~cst.charValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(~cst.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(~cst.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(~cst.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue(cst.longValue() ^ 0xFFFFFFFFFFFFFFFFL);
                    }
                }
                return NotAConstant;
            }
        }
        return NotAConstant;
    }

    public static final Constant computeConstantOperation(Constant left, int leftId, int operator, Constant right, int rightId) {
        switch (operator) {
            case 2: {
                return Constant.computeConstantOperationAND(left, leftId, right, rightId);
            }
            case 0: {
                return Constant.computeConstantOperationAND_AND(left, leftId, right, rightId);
            }
            case 9: {
                return Constant.computeConstantOperationDIVIDE(left, leftId, right, rightId);
            }
            case 6: {
                return Constant.computeConstantOperationGREATER(left, leftId, right, rightId);
            }
            case 7: {
                return Constant.computeConstantOperationGREATER_EQUAL(left, leftId, right, rightId);
            }
            case 10: {
                return Constant.computeConstantOperationLEFT_SHIFT(left, leftId, right, rightId);
            }
            case 4: {
                return Constant.computeConstantOperationLESS(left, leftId, right, rightId);
            }
            case 5: {
                return Constant.computeConstantOperationLESS_EQUAL(left, leftId, right, rightId);
            }
            case 13: {
                return Constant.computeConstantOperationMINUS(left, leftId, right, rightId);
            }
            case 15: {
                return Constant.computeConstantOperationMULTIPLY(left, leftId, right, rightId);
            }
            case 3: {
                return Constant.computeConstantOperationOR(left, leftId, right, rightId);
            }
            case 1: {
                return Constant.computeConstantOperationOR_OR(left, leftId, right, rightId);
            }
            case 14: {
                return Constant.computeConstantOperationPLUS(left, leftId, right, rightId);
            }
            case 16: {
                return Constant.computeConstantOperationREMAINDER(left, leftId, right, rightId);
            }
            case 17: {
                return Constant.computeConstantOperationRIGHT_SHIFT(left, leftId, right, rightId);
            }
            case 19: {
                return Constant.computeConstantOperationUNSIGNED_RIGHT_SHIFT(left, leftId, right, rightId);
            }
            case 8: {
                return Constant.computeConstantOperationXOR(left, leftId, right, rightId);
            }
        }
        return NotAConstant;
    }

    public static final Constant computeConstantOperationAND(Constant left, int leftId, Constant right, int rightId) {
        switch (leftId) {
            case 5: {
                return BooleanConstant.fromValue(left.booleanValue() & right.booleanValue());
            }
            case 2: {
                switch (rightId) {
                    case 2: {
                        return IntConstant.fromValue(left.charValue() & right.charValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(left.charValue() & right.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(left.charValue() & right.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(left.charValue() & right.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue((long)left.charValue() & right.longValue());
                    }
                }
                break;
            }
            case 3: {
                switch (rightId) {
                    case 2: {
                        return IntConstant.fromValue(left.byteValue() & right.charValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(left.byteValue() & right.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(left.byteValue() & right.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(left.byteValue() & right.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue((long)left.byteValue() & right.longValue());
                    }
                }
                break;
            }
            case 4: {
                switch (rightId) {
                    case 2: {
                        return IntConstant.fromValue(left.shortValue() & right.charValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(left.shortValue() & right.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(left.shortValue() & right.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(left.shortValue() & right.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue((long)left.shortValue() & right.longValue());
                    }
                }
                break;
            }
            case 10: {
                switch (rightId) {
                    case 2: {
                        return IntConstant.fromValue(left.intValue() & right.charValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(left.intValue() & right.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(left.intValue() & right.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(left.intValue() & right.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue((long)left.intValue() & right.longValue());
                    }
                }
                break;
            }
            case 7: {
                switch (rightId) {
                    case 2: {
                        return LongConstant.fromValue(left.longValue() & (long)right.charValue());
                    }
                    case 3: {
                        return LongConstant.fromValue(left.longValue() & (long)right.byteValue());
                    }
                    case 4: {
                        return LongConstant.fromValue(left.longValue() & (long)right.shortValue());
                    }
                    case 10: {
                        return LongConstant.fromValue(left.longValue() & (long)right.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue(left.longValue() & right.longValue());
                    }
                }
            }
        }
        return NotAConstant;
    }

    public static final Constant computeConstantOperationAND_AND(Constant left, int leftId, Constant right, int rightId) {
        return BooleanConstant.fromValue(left.booleanValue() && right.booleanValue());
    }

    public static final Constant computeConstantOperationDIVIDE(Constant left, int leftId, Constant right, int rightId) {
        switch (leftId) {
            case 2: {
                switch (rightId) {
                    case 2: {
                        return IntConstant.fromValue(left.charValue() / right.charValue());
                    }
                    case 9: {
                        return FloatConstant.fromValue((float)left.charValue() / right.floatValue());
                    }
                    case 8: {
                        return DoubleConstant.fromValue((double)left.charValue() / right.doubleValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(left.charValue() / right.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(left.charValue() / right.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(left.charValue() / right.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue((long)left.charValue() / right.longValue());
                    }
                }
                break;
            }
            case 9: {
                switch (rightId) {
                    case 2: {
                        return FloatConstant.fromValue(left.floatValue() / (float)right.charValue());
                    }
                    case 9: {
                        return FloatConstant.fromValue(left.floatValue() / right.floatValue());
                    }
                    case 8: {
                        return DoubleConstant.fromValue((double)left.floatValue() / right.doubleValue());
                    }
                    case 3: {
                        return FloatConstant.fromValue(left.floatValue() / (float)right.byteValue());
                    }
                    case 4: {
                        return FloatConstant.fromValue(left.floatValue() / (float)right.shortValue());
                    }
                    case 10: {
                        return FloatConstant.fromValue(left.floatValue() / (float)right.intValue());
                    }
                    case 7: {
                        return FloatConstant.fromValue(left.floatValue() / (float)right.longValue());
                    }
                }
                break;
            }
            case 8: {
                switch (rightId) {
                    case 2: {
                        return DoubleConstant.fromValue(left.doubleValue() / (double)right.charValue());
                    }
                    case 9: {
                        return DoubleConstant.fromValue(left.doubleValue() / (double)right.floatValue());
                    }
                    case 8: {
                        return DoubleConstant.fromValue(left.doubleValue() / right.doubleValue());
                    }
                    case 3: {
                        return DoubleConstant.fromValue(left.doubleValue() / (double)right.byteValue());
                    }
                    case 4: {
                        return DoubleConstant.fromValue(left.doubleValue() / (double)right.shortValue());
                    }
                    case 10: {
                        return DoubleConstant.fromValue(left.doubleValue() / (double)right.intValue());
                    }
                    case 7: {
                        return DoubleConstant.fromValue(left.doubleValue() / (double)right.longValue());
                    }
                }
                break;
            }
            case 3: {
                switch (rightId) {
                    case 2: {
                        return IntConstant.fromValue(left.byteValue() / right.charValue());
                    }
                    case 9: {
                        return FloatConstant.fromValue((float)left.byteValue() / right.floatValue());
                    }
                    case 8: {
                        return DoubleConstant.fromValue((double)left.byteValue() / right.doubleValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(left.byteValue() / right.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(left.byteValue() / right.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(left.byteValue() / right.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue((long)left.byteValue() / right.longValue());
                    }
                }
                break;
            }
            case 4: {
                switch (rightId) {
                    case 2: {
                        return IntConstant.fromValue(left.shortValue() / right.charValue());
                    }
                    case 9: {
                        return FloatConstant.fromValue((float)left.shortValue() / right.floatValue());
                    }
                    case 8: {
                        return DoubleConstant.fromValue((double)left.shortValue() / right.doubleValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(left.shortValue() / right.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(left.shortValue() / right.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(left.shortValue() / right.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue((long)left.shortValue() / right.longValue());
                    }
                }
                break;
            }
            case 10: {
                switch (rightId) {
                    case 2: {
                        return IntConstant.fromValue(left.intValue() / right.charValue());
                    }
                    case 9: {
                        return FloatConstant.fromValue((float)left.intValue() / right.floatValue());
                    }
                    case 8: {
                        return DoubleConstant.fromValue((double)left.intValue() / right.doubleValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(left.intValue() / right.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(left.intValue() / right.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(left.intValue() / right.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue((long)left.intValue() / right.longValue());
                    }
                }
                break;
            }
            case 7: {
                switch (rightId) {
                    case 2: {
                        return LongConstant.fromValue(left.longValue() / (long)right.charValue());
                    }
                    case 9: {
                        return FloatConstant.fromValue((float)left.longValue() / right.floatValue());
                    }
                    case 8: {
                        return DoubleConstant.fromValue((double)left.longValue() / right.doubleValue());
                    }
                    case 3: {
                        return LongConstant.fromValue(left.longValue() / (long)right.byteValue());
                    }
                    case 4: {
                        return LongConstant.fromValue(left.longValue() / (long)right.shortValue());
                    }
                    case 10: {
                        return LongConstant.fromValue(left.longValue() / (long)right.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue(left.longValue() / right.longValue());
                    }
                }
            }
        }
        return NotAConstant;
    }

    public static final Constant computeConstantOperationEQUAL_EQUAL(Constant left, int leftId, Constant right, int rightId) {
        switch (leftId) {
            case 5: {
                if (rightId != 5) break;
                return BooleanConstant.fromValue(left.booleanValue() == right.booleanValue());
            }
            case 2: {
                switch (rightId) {
                    case 2: {
                        return BooleanConstant.fromValue(left.charValue() == right.charValue());
                    }
                    case 9: {
                        return BooleanConstant.fromValue((float)left.charValue() == right.floatValue());
                    }
                    case 8: {
                        return BooleanConstant.fromValue((double)left.charValue() == right.doubleValue());
                    }
                    case 3: {
                        return BooleanConstant.fromValue(left.charValue() == right.byteValue());
                    }
                    case 4: {
                        return BooleanConstant.fromValue(left.charValue() == right.shortValue());
                    }
                    case 10: {
                        return BooleanConstant.fromValue(left.charValue() == right.intValue());
                    }
                    case 7: {
                        return BooleanConstant.fromValue((long)left.charValue() == right.longValue());
                    }
                }
                break;
            }
            case 9: {
                switch (rightId) {
                    case 2: {
                        return BooleanConstant.fromValue(left.floatValue() == (float)right.charValue());
                    }
                    case 9: {
                        return BooleanConstant.fromValue(left.floatValue() == right.floatValue());
                    }
                    case 8: {
                        return BooleanConstant.fromValue((double)left.floatValue() == right.doubleValue());
                    }
                    case 3: {
                        return BooleanConstant.fromValue(left.floatValue() == (float)right.byteValue());
                    }
                    case 4: {
                        return BooleanConstant.fromValue(left.floatValue() == (float)right.shortValue());
                    }
                    case 10: {
                        return BooleanConstant.fromValue(left.floatValue() == (float)right.intValue());
                    }
                    case 7: {
                        return BooleanConstant.fromValue(left.floatValue() == (float)right.longValue());
                    }
                }
                break;
            }
            case 8: {
                switch (rightId) {
                    case 2: {
                        return BooleanConstant.fromValue(left.doubleValue() == (double)right.charValue());
                    }
                    case 9: {
                        return BooleanConstant.fromValue(left.doubleValue() == (double)right.floatValue());
                    }
                    case 8: {
                        return BooleanConstant.fromValue(left.doubleValue() == right.doubleValue());
                    }
                    case 3: {
                        return BooleanConstant.fromValue(left.doubleValue() == (double)right.byteValue());
                    }
                    case 4: {
                        return BooleanConstant.fromValue(left.doubleValue() == (double)right.shortValue());
                    }
                    case 10: {
                        return BooleanConstant.fromValue(left.doubleValue() == (double)right.intValue());
                    }
                    case 7: {
                        return BooleanConstant.fromValue(left.doubleValue() == (double)right.longValue());
                    }
                }
                break;
            }
            case 3: {
                switch (rightId) {
                    case 2: {
                        return BooleanConstant.fromValue(left.byteValue() == right.charValue());
                    }
                    case 9: {
                        return BooleanConstant.fromValue((float)left.byteValue() == right.floatValue());
                    }
                    case 8: {
                        return BooleanConstant.fromValue((double)left.byteValue() == right.doubleValue());
                    }
                    case 3: {
                        return BooleanConstant.fromValue(left.byteValue() == right.byteValue());
                    }
                    case 4: {
                        return BooleanConstant.fromValue(left.byteValue() == right.shortValue());
                    }
                    case 10: {
                        return BooleanConstant.fromValue(left.byteValue() == right.intValue());
                    }
                    case 7: {
                        return BooleanConstant.fromValue((long)left.byteValue() == right.longValue());
                    }
                }
                break;
            }
            case 4: {
                switch (rightId) {
                    case 2: {
                        return BooleanConstant.fromValue(left.shortValue() == right.charValue());
                    }
                    case 9: {
                        return BooleanConstant.fromValue((float)left.shortValue() == right.floatValue());
                    }
                    case 8: {
                        return BooleanConstant.fromValue((double)left.shortValue() == right.doubleValue());
                    }
                    case 3: {
                        return BooleanConstant.fromValue(left.shortValue() == right.byteValue());
                    }
                    case 4: {
                        return BooleanConstant.fromValue(left.shortValue() == right.shortValue());
                    }
                    case 10: {
                        return BooleanConstant.fromValue(left.shortValue() == right.intValue());
                    }
                    case 7: {
                        return BooleanConstant.fromValue((long)left.shortValue() == right.longValue());
                    }
                }
                break;
            }
            case 10: {
                switch (rightId) {
                    case 2: {
                        return BooleanConstant.fromValue(left.intValue() == right.charValue());
                    }
                    case 9: {
                        return BooleanConstant.fromValue((float)left.intValue() == right.floatValue());
                    }
                    case 8: {
                        return BooleanConstant.fromValue((double)left.intValue() == right.doubleValue());
                    }
                    case 3: {
                        return BooleanConstant.fromValue(left.intValue() == right.byteValue());
                    }
                    case 4: {
                        return BooleanConstant.fromValue(left.intValue() == right.shortValue());
                    }
                    case 10: {
                        return BooleanConstant.fromValue(left.intValue() == right.intValue());
                    }
                    case 7: {
                        return BooleanConstant.fromValue((long)left.intValue() == right.longValue());
                    }
                }
                break;
            }
            case 7: {
                switch (rightId) {
                    case 2: {
                        return BooleanConstant.fromValue(left.longValue() == (long)right.charValue());
                    }
                    case 9: {
                        return BooleanConstant.fromValue((float)left.longValue() == right.floatValue());
                    }
                    case 8: {
                        return BooleanConstant.fromValue((double)left.longValue() == right.doubleValue());
                    }
                    case 3: {
                        return BooleanConstant.fromValue(left.longValue() == (long)right.byteValue());
                    }
                    case 4: {
                        return BooleanConstant.fromValue(left.longValue() == (long)right.shortValue());
                    }
                    case 10: {
                        return BooleanConstant.fromValue(left.longValue() == (long)right.intValue());
                    }
                    case 7: {
                        return BooleanConstant.fromValue(left.longValue() == right.longValue());
                    }
                }
                break;
            }
            case 11: {
                if (rightId != 11) break;
                return BooleanConstant.fromValue(((StringConstant)left).hasSameValue(right));
            }
            case 12: {
                if (rightId == 11) {
                    return BooleanConstant.fromValue(false);
                }
                if (rightId != 12) break;
                return BooleanConstant.fromValue(true);
            }
        }
        return BooleanConstant.fromValue(false);
    }

    public static final Constant computeConstantOperationGREATER(Constant left, int leftId, Constant right, int rightId) {
        switch (leftId) {
            case 2: {
                switch (rightId) {
                    case 2: {
                        return BooleanConstant.fromValue(left.charValue() > right.charValue());
                    }
                    case 9: {
                        return BooleanConstant.fromValue((float)left.charValue() > right.floatValue());
                    }
                    case 8: {
                        return BooleanConstant.fromValue((double)left.charValue() > right.doubleValue());
                    }
                    case 3: {
                        return BooleanConstant.fromValue(left.charValue() > right.byteValue());
                    }
                    case 4: {
                        return BooleanConstant.fromValue(left.charValue() > right.shortValue());
                    }
                    case 10: {
                        return BooleanConstant.fromValue(left.charValue() > right.intValue());
                    }
                    case 7: {
                        return BooleanConstant.fromValue((long)left.charValue() > right.longValue());
                    }
                }
                break;
            }
            case 9: {
                switch (rightId) {
                    case 2: {
                        return BooleanConstant.fromValue(left.floatValue() > (float)right.charValue());
                    }
                    case 9: {
                        return BooleanConstant.fromValue(left.floatValue() > right.floatValue());
                    }
                    case 8: {
                        return BooleanConstant.fromValue((double)left.floatValue() > right.doubleValue());
                    }
                    case 3: {
                        return BooleanConstant.fromValue(left.floatValue() > (float)right.byteValue());
                    }
                    case 4: {
                        return BooleanConstant.fromValue(left.floatValue() > (float)right.shortValue());
                    }
                    case 10: {
                        return BooleanConstant.fromValue(left.floatValue() > (float)right.intValue());
                    }
                    case 7: {
                        return BooleanConstant.fromValue(left.floatValue() > (float)right.longValue());
                    }
                }
                break;
            }
            case 8: {
                switch (rightId) {
                    case 2: {
                        return BooleanConstant.fromValue(left.doubleValue() > (double)right.charValue());
                    }
                    case 9: {
                        return BooleanConstant.fromValue(left.doubleValue() > (double)right.floatValue());
                    }
                    case 8: {
                        return BooleanConstant.fromValue(left.doubleValue() > right.doubleValue());
                    }
                    case 3: {
                        return BooleanConstant.fromValue(left.doubleValue() > (double)right.byteValue());
                    }
                    case 4: {
                        return BooleanConstant.fromValue(left.doubleValue() > (double)right.shortValue());
                    }
                    case 10: {
                        return BooleanConstant.fromValue(left.doubleValue() > (double)right.intValue());
                    }
                    case 7: {
                        return BooleanConstant.fromValue(left.doubleValue() > (double)right.longValue());
                    }
                }
                break;
            }
            case 3: {
                switch (rightId) {
                    case 2: {
                        return BooleanConstant.fromValue(left.byteValue() > right.charValue());
                    }
                    case 9: {
                        return BooleanConstant.fromValue((float)left.byteValue() > right.floatValue());
                    }
                    case 8: {
                        return BooleanConstant.fromValue((double)left.byteValue() > right.doubleValue());
                    }
                    case 3: {
                        return BooleanConstant.fromValue(left.byteValue() > right.byteValue());
                    }
                    case 4: {
                        return BooleanConstant.fromValue(left.byteValue() > right.shortValue());
                    }
                    case 10: {
                        return BooleanConstant.fromValue(left.byteValue() > right.intValue());
                    }
                    case 7: {
                        return BooleanConstant.fromValue((long)left.byteValue() > right.longValue());
                    }
                }
                break;
            }
            case 4: {
                switch (rightId) {
                    case 2: {
                        return BooleanConstant.fromValue(left.shortValue() > right.charValue());
                    }
                    case 9: {
                        return BooleanConstant.fromValue((float)left.shortValue() > right.floatValue());
                    }
                    case 8: {
                        return BooleanConstant.fromValue((double)left.shortValue() > right.doubleValue());
                    }
                    case 3: {
                        return BooleanConstant.fromValue(left.shortValue() > right.byteValue());
                    }
                    case 4: {
                        return BooleanConstant.fromValue(left.shortValue() > right.shortValue());
                    }
                    case 10: {
                        return BooleanConstant.fromValue(left.shortValue() > right.intValue());
                    }
                    case 7: {
                        return BooleanConstant.fromValue((long)left.shortValue() > right.longValue());
                    }
                }
                break;
            }
            case 10: {
                switch (rightId) {
                    case 2: {
                        return BooleanConstant.fromValue(left.intValue() > right.charValue());
                    }
                    case 9: {
                        return BooleanConstant.fromValue((float)left.intValue() > right.floatValue());
                    }
                    case 8: {
                        return BooleanConstant.fromValue((double)left.intValue() > right.doubleValue());
                    }
                    case 3: {
                        return BooleanConstant.fromValue(left.intValue() > right.byteValue());
                    }
                    case 4: {
                        return BooleanConstant.fromValue(left.intValue() > right.shortValue());
                    }
                    case 10: {
                        return BooleanConstant.fromValue(left.intValue() > right.intValue());
                    }
                    case 7: {
                        return BooleanConstant.fromValue((long)left.intValue() > right.longValue());
                    }
                }
                break;
            }
            case 7: {
                switch (rightId) {
                    case 2: {
                        return BooleanConstant.fromValue(left.longValue() > (long)right.charValue());
                    }
                    case 9: {
                        return BooleanConstant.fromValue((float)left.longValue() > right.floatValue());
                    }
                    case 8: {
                        return BooleanConstant.fromValue((double)left.longValue() > right.doubleValue());
                    }
                    case 3: {
                        return BooleanConstant.fromValue(left.longValue() > (long)right.byteValue());
                    }
                    case 4: {
                        return BooleanConstant.fromValue(left.longValue() > (long)right.shortValue());
                    }
                    case 10: {
                        return BooleanConstant.fromValue(left.longValue() > (long)right.intValue());
                    }
                    case 7: {
                        return BooleanConstant.fromValue(left.longValue() > right.longValue());
                    }
                }
            }
        }
        return NotAConstant;
    }

    public static final Constant computeConstantOperationGREATER_EQUAL(Constant left, int leftId, Constant right, int rightId) {
        switch (leftId) {
            case 2: {
                switch (rightId) {
                    case 2: {
                        return BooleanConstant.fromValue(left.charValue() >= right.charValue());
                    }
                    case 9: {
                        return BooleanConstant.fromValue((float)left.charValue() >= right.floatValue());
                    }
                    case 8: {
                        return BooleanConstant.fromValue((double)left.charValue() >= right.doubleValue());
                    }
                    case 3: {
                        return BooleanConstant.fromValue(left.charValue() >= right.byteValue());
                    }
                    case 4: {
                        return BooleanConstant.fromValue(left.charValue() >= right.shortValue());
                    }
                    case 10: {
                        return BooleanConstant.fromValue(left.charValue() >= right.intValue());
                    }
                    case 7: {
                        return BooleanConstant.fromValue((long)left.charValue() >= right.longValue());
                    }
                }
                break;
            }
            case 9: {
                switch (rightId) {
                    case 2: {
                        return BooleanConstant.fromValue(left.floatValue() >= (float)right.charValue());
                    }
                    case 9: {
                        return BooleanConstant.fromValue(left.floatValue() >= right.floatValue());
                    }
                    case 8: {
                        return BooleanConstant.fromValue((double)left.floatValue() >= right.doubleValue());
                    }
                    case 3: {
                        return BooleanConstant.fromValue(left.floatValue() >= (float)right.byteValue());
                    }
                    case 4: {
                        return BooleanConstant.fromValue(left.floatValue() >= (float)right.shortValue());
                    }
                    case 10: {
                        return BooleanConstant.fromValue(left.floatValue() >= (float)right.intValue());
                    }
                    case 7: {
                        return BooleanConstant.fromValue(left.floatValue() >= (float)right.longValue());
                    }
                }
                break;
            }
            case 8: {
                switch (rightId) {
                    case 2: {
                        return BooleanConstant.fromValue(left.doubleValue() >= (double)right.charValue());
                    }
                    case 9: {
                        return BooleanConstant.fromValue(left.doubleValue() >= (double)right.floatValue());
                    }
                    case 8: {
                        return BooleanConstant.fromValue(left.doubleValue() >= right.doubleValue());
                    }
                    case 3: {
                        return BooleanConstant.fromValue(left.doubleValue() >= (double)right.byteValue());
                    }
                    case 4: {
                        return BooleanConstant.fromValue(left.doubleValue() >= (double)right.shortValue());
                    }
                    case 10: {
                        return BooleanConstant.fromValue(left.doubleValue() >= (double)right.intValue());
                    }
                    case 7: {
                        return BooleanConstant.fromValue(left.doubleValue() >= (double)right.longValue());
                    }
                }
                break;
            }
            case 3: {
                switch (rightId) {
                    case 2: {
                        return BooleanConstant.fromValue(left.byteValue() >= right.charValue());
                    }
                    case 9: {
                        return BooleanConstant.fromValue((float)left.byteValue() >= right.floatValue());
                    }
                    case 8: {
                        return BooleanConstant.fromValue((double)left.byteValue() >= right.doubleValue());
                    }
                    case 3: {
                        return BooleanConstant.fromValue(left.byteValue() >= right.byteValue());
                    }
                    case 4: {
                        return BooleanConstant.fromValue(left.byteValue() >= right.shortValue());
                    }
                    case 10: {
                        return BooleanConstant.fromValue(left.byteValue() >= right.intValue());
                    }
                    case 7: {
                        return BooleanConstant.fromValue((long)left.byteValue() >= right.longValue());
                    }
                }
                break;
            }
            case 4: {
                switch (rightId) {
                    case 2: {
                        return BooleanConstant.fromValue(left.shortValue() >= right.charValue());
                    }
                    case 9: {
                        return BooleanConstant.fromValue((float)left.shortValue() >= right.floatValue());
                    }
                    case 8: {
                        return BooleanConstant.fromValue((double)left.shortValue() >= right.doubleValue());
                    }
                    case 3: {
                        return BooleanConstant.fromValue(left.shortValue() >= right.byteValue());
                    }
                    case 4: {
                        return BooleanConstant.fromValue(left.shortValue() >= right.shortValue());
                    }
                    case 10: {
                        return BooleanConstant.fromValue(left.shortValue() >= right.intValue());
                    }
                    case 7: {
                        return BooleanConstant.fromValue((long)left.shortValue() >= right.longValue());
                    }
                }
                break;
            }
            case 10: {
                switch (rightId) {
                    case 2: {
                        return BooleanConstant.fromValue(left.intValue() >= right.charValue());
                    }
                    case 9: {
                        return BooleanConstant.fromValue((float)left.intValue() >= right.floatValue());
                    }
                    case 8: {
                        return BooleanConstant.fromValue((double)left.intValue() >= right.doubleValue());
                    }
                    case 3: {
                        return BooleanConstant.fromValue(left.intValue() >= right.byteValue());
                    }
                    case 4: {
                        return BooleanConstant.fromValue(left.intValue() >= right.shortValue());
                    }
                    case 10: {
                        return BooleanConstant.fromValue(left.intValue() >= right.intValue());
                    }
                    case 7: {
                        return BooleanConstant.fromValue((long)left.intValue() >= right.longValue());
                    }
                }
                break;
            }
            case 7: {
                switch (rightId) {
                    case 2: {
                        return BooleanConstant.fromValue(left.longValue() >= (long)right.charValue());
                    }
                    case 9: {
                        return BooleanConstant.fromValue((float)left.longValue() >= right.floatValue());
                    }
                    case 8: {
                        return BooleanConstant.fromValue((double)left.longValue() >= right.doubleValue());
                    }
                    case 3: {
                        return BooleanConstant.fromValue(left.longValue() >= (long)right.byteValue());
                    }
                    case 4: {
                        return BooleanConstant.fromValue(left.longValue() >= (long)right.shortValue());
                    }
                    case 10: {
                        return BooleanConstant.fromValue(left.longValue() >= (long)right.intValue());
                    }
                    case 7: {
                        return BooleanConstant.fromValue(left.longValue() >= right.longValue());
                    }
                }
            }
        }
        return NotAConstant;
    }

    public static final Constant computeConstantOperationLEFT_SHIFT(Constant left, int leftId, Constant right, int rightId) {
        switch (leftId) {
            case 2: {
                switch (rightId) {
                    case 2: {
                        return IntConstant.fromValue(left.charValue() << right.charValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(left.charValue() << right.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(left.charValue() << right.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(left.charValue() << right.intValue());
                    }
                    case 7: {
                        return IntConstant.fromValue(left.charValue() << (int)right.longValue());
                    }
                }
                break;
            }
            case 3: {
                switch (rightId) {
                    case 2: {
                        return IntConstant.fromValue(left.byteValue() << right.charValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(left.byteValue() << right.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(left.byteValue() << right.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(left.byteValue() << right.intValue());
                    }
                    case 7: {
                        return IntConstant.fromValue(left.byteValue() << (int)right.longValue());
                    }
                }
                break;
            }
            case 4: {
                switch (rightId) {
                    case 2: {
                        return IntConstant.fromValue(left.shortValue() << right.charValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(left.shortValue() << right.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(left.shortValue() << right.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(left.shortValue() << right.intValue());
                    }
                    case 7: {
                        return IntConstant.fromValue(left.shortValue() << (int)right.longValue());
                    }
                }
                break;
            }
            case 10: {
                switch (rightId) {
                    case 2: {
                        return IntConstant.fromValue(left.intValue() << right.charValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(left.intValue() << right.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(left.intValue() << right.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(left.intValue() << right.intValue());
                    }
                    case 7: {
                        return IntConstant.fromValue(left.intValue() << (int)right.longValue());
                    }
                }
                break;
            }
            case 7: {
                switch (rightId) {
                    case 2: {
                        return LongConstant.fromValue(left.longValue() << right.charValue());
                    }
                    case 3: {
                        return LongConstant.fromValue(left.longValue() << right.byteValue());
                    }
                    case 4: {
                        return LongConstant.fromValue(left.longValue() << right.shortValue());
                    }
                    case 10: {
                        return LongConstant.fromValue(left.longValue() << right.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue(left.longValue() << (int)right.longValue());
                    }
                }
            }
        }
        return NotAConstant;
    }

    public static final Constant computeConstantOperationLESS(Constant left, int leftId, Constant right, int rightId) {
        switch (leftId) {
            case 2: {
                switch (rightId) {
                    case 2: {
                        return BooleanConstant.fromValue(left.charValue() < right.charValue());
                    }
                    case 9: {
                        return BooleanConstant.fromValue((float)left.charValue() < right.floatValue());
                    }
                    case 8: {
                        return BooleanConstant.fromValue((double)left.charValue() < right.doubleValue());
                    }
                    case 3: {
                        return BooleanConstant.fromValue(left.charValue() < right.byteValue());
                    }
                    case 4: {
                        return BooleanConstant.fromValue(left.charValue() < right.shortValue());
                    }
                    case 10: {
                        return BooleanConstant.fromValue(left.charValue() < right.intValue());
                    }
                    case 7: {
                        return BooleanConstant.fromValue((long)left.charValue() < right.longValue());
                    }
                }
                break;
            }
            case 9: {
                switch (rightId) {
                    case 2: {
                        return BooleanConstant.fromValue(left.floatValue() < (float)right.charValue());
                    }
                    case 9: {
                        return BooleanConstant.fromValue(left.floatValue() < right.floatValue());
                    }
                    case 8: {
                        return BooleanConstant.fromValue((double)left.floatValue() < right.doubleValue());
                    }
                    case 3: {
                        return BooleanConstant.fromValue(left.floatValue() < (float)right.byteValue());
                    }
                    case 4: {
                        return BooleanConstant.fromValue(left.floatValue() < (float)right.shortValue());
                    }
                    case 10: {
                        return BooleanConstant.fromValue(left.floatValue() < (float)right.intValue());
                    }
                    case 7: {
                        return BooleanConstant.fromValue(left.floatValue() < (float)right.longValue());
                    }
                }
                break;
            }
            case 8: {
                switch (rightId) {
                    case 2: {
                        return BooleanConstant.fromValue(left.doubleValue() < (double)right.charValue());
                    }
                    case 9: {
                        return BooleanConstant.fromValue(left.doubleValue() < (double)right.floatValue());
                    }
                    case 8: {
                        return BooleanConstant.fromValue(left.doubleValue() < right.doubleValue());
                    }
                    case 3: {
                        return BooleanConstant.fromValue(left.doubleValue() < (double)right.byteValue());
                    }
                    case 4: {
                        return BooleanConstant.fromValue(left.doubleValue() < (double)right.shortValue());
                    }
                    case 10: {
                        return BooleanConstant.fromValue(left.doubleValue() < (double)right.intValue());
                    }
                    case 7: {
                        return BooleanConstant.fromValue(left.doubleValue() < (double)right.longValue());
                    }
                }
                break;
            }
            case 3: {
                switch (rightId) {
                    case 2: {
                        return BooleanConstant.fromValue(left.byteValue() < right.charValue());
                    }
                    case 9: {
                        return BooleanConstant.fromValue((float)left.byteValue() < right.floatValue());
                    }
                    case 8: {
                        return BooleanConstant.fromValue((double)left.byteValue() < right.doubleValue());
                    }
                    case 3: {
                        return BooleanConstant.fromValue(left.byteValue() < right.byteValue());
                    }
                    case 4: {
                        return BooleanConstant.fromValue(left.byteValue() < right.shortValue());
                    }
                    case 10: {
                        return BooleanConstant.fromValue(left.byteValue() < right.intValue());
                    }
                    case 7: {
                        return BooleanConstant.fromValue((long)left.byteValue() < right.longValue());
                    }
                }
                break;
            }
            case 4: {
                switch (rightId) {
                    case 2: {
                        return BooleanConstant.fromValue(left.shortValue() < right.charValue());
                    }
                    case 9: {
                        return BooleanConstant.fromValue((float)left.shortValue() < right.floatValue());
                    }
                    case 8: {
                        return BooleanConstant.fromValue((double)left.shortValue() < right.doubleValue());
                    }
                    case 3: {
                        return BooleanConstant.fromValue(left.shortValue() < right.byteValue());
                    }
                    case 4: {
                        return BooleanConstant.fromValue(left.shortValue() < right.shortValue());
                    }
                    case 10: {
                        return BooleanConstant.fromValue(left.shortValue() < right.intValue());
                    }
                    case 7: {
                        return BooleanConstant.fromValue((long)left.shortValue() < right.longValue());
                    }
                }
                break;
            }
            case 10: {
                switch (rightId) {
                    case 2: {
                        return BooleanConstant.fromValue(left.intValue() < right.charValue());
                    }
                    case 9: {
                        return BooleanConstant.fromValue((float)left.intValue() < right.floatValue());
                    }
                    case 8: {
                        return BooleanConstant.fromValue((double)left.intValue() < right.doubleValue());
                    }
                    case 3: {
                        return BooleanConstant.fromValue(left.intValue() < right.byteValue());
                    }
                    case 4: {
                        return BooleanConstant.fromValue(left.intValue() < right.shortValue());
                    }
                    case 10: {
                        return BooleanConstant.fromValue(left.intValue() < right.intValue());
                    }
                    case 7: {
                        return BooleanConstant.fromValue((long)left.intValue() < right.longValue());
                    }
                }
                break;
            }
            case 7: {
                switch (rightId) {
                    case 2: {
                        return BooleanConstant.fromValue(left.longValue() < (long)right.charValue());
                    }
                    case 9: {
                        return BooleanConstant.fromValue((float)left.longValue() < right.floatValue());
                    }
                    case 8: {
                        return BooleanConstant.fromValue((double)left.longValue() < right.doubleValue());
                    }
                    case 3: {
                        return BooleanConstant.fromValue(left.longValue() < (long)right.byteValue());
                    }
                    case 4: {
                        return BooleanConstant.fromValue(left.longValue() < (long)right.shortValue());
                    }
                    case 10: {
                        return BooleanConstant.fromValue(left.longValue() < (long)right.intValue());
                    }
                    case 7: {
                        return BooleanConstant.fromValue(left.longValue() < right.longValue());
                    }
                }
            }
        }
        return NotAConstant;
    }

    public static final Constant computeConstantOperationLESS_EQUAL(Constant left, int leftId, Constant right, int rightId) {
        switch (leftId) {
            case 2: {
                switch (rightId) {
                    case 2: {
                        return BooleanConstant.fromValue(left.charValue() <= right.charValue());
                    }
                    case 9: {
                        return BooleanConstant.fromValue((float)left.charValue() <= right.floatValue());
                    }
                    case 8: {
                        return BooleanConstant.fromValue((double)left.charValue() <= right.doubleValue());
                    }
                    case 3: {
                        return BooleanConstant.fromValue(left.charValue() <= right.byteValue());
                    }
                    case 4: {
                        return BooleanConstant.fromValue(left.charValue() <= right.shortValue());
                    }
                    case 10: {
                        return BooleanConstant.fromValue(left.charValue() <= right.intValue());
                    }
                    case 7: {
                        return BooleanConstant.fromValue((long)left.charValue() <= right.longValue());
                    }
                }
                break;
            }
            case 9: {
                switch (rightId) {
                    case 2: {
                        return BooleanConstant.fromValue(left.floatValue() <= (float)right.charValue());
                    }
                    case 9: {
                        return BooleanConstant.fromValue(left.floatValue() <= right.floatValue());
                    }
                    case 8: {
                        return BooleanConstant.fromValue((double)left.floatValue() <= right.doubleValue());
                    }
                    case 3: {
                        return BooleanConstant.fromValue(left.floatValue() <= (float)right.byteValue());
                    }
                    case 4: {
                        return BooleanConstant.fromValue(left.floatValue() <= (float)right.shortValue());
                    }
                    case 10: {
                        return BooleanConstant.fromValue(left.floatValue() <= (float)right.intValue());
                    }
                    case 7: {
                        return BooleanConstant.fromValue(left.floatValue() <= (float)right.longValue());
                    }
                }
                break;
            }
            case 8: {
                switch (rightId) {
                    case 2: {
                        return BooleanConstant.fromValue(left.doubleValue() <= (double)right.charValue());
                    }
                    case 9: {
                        return BooleanConstant.fromValue(left.doubleValue() <= (double)right.floatValue());
                    }
                    case 8: {
                        return BooleanConstant.fromValue(left.doubleValue() <= right.doubleValue());
                    }
                    case 3: {
                        return BooleanConstant.fromValue(left.doubleValue() <= (double)right.byteValue());
                    }
                    case 4: {
                        return BooleanConstant.fromValue(left.doubleValue() <= (double)right.shortValue());
                    }
                    case 10: {
                        return BooleanConstant.fromValue(left.doubleValue() <= (double)right.intValue());
                    }
                    case 7: {
                        return BooleanConstant.fromValue(left.doubleValue() <= (double)right.longValue());
                    }
                }
                break;
            }
            case 3: {
                switch (rightId) {
                    case 2: {
                        return BooleanConstant.fromValue(left.byteValue() <= right.charValue());
                    }
                    case 9: {
                        return BooleanConstant.fromValue((float)left.byteValue() <= right.floatValue());
                    }
                    case 8: {
                        return BooleanConstant.fromValue((double)left.byteValue() <= right.doubleValue());
                    }
                    case 3: {
                        return BooleanConstant.fromValue(left.byteValue() <= right.byteValue());
                    }
                    case 4: {
                        return BooleanConstant.fromValue(left.byteValue() <= right.shortValue());
                    }
                    case 10: {
                        return BooleanConstant.fromValue(left.byteValue() <= right.intValue());
                    }
                    case 7: {
                        return BooleanConstant.fromValue((long)left.byteValue() <= right.longValue());
                    }
                }
                break;
            }
            case 4: {
                switch (rightId) {
                    case 2: {
                        return BooleanConstant.fromValue(left.shortValue() <= right.charValue());
                    }
                    case 9: {
                        return BooleanConstant.fromValue((float)left.shortValue() <= right.floatValue());
                    }
                    case 8: {
                        return BooleanConstant.fromValue((double)left.shortValue() <= right.doubleValue());
                    }
                    case 3: {
                        return BooleanConstant.fromValue(left.shortValue() <= right.byteValue());
                    }
                    case 4: {
                        return BooleanConstant.fromValue(left.shortValue() <= right.shortValue());
                    }
                    case 10: {
                        return BooleanConstant.fromValue(left.shortValue() <= right.intValue());
                    }
                    case 7: {
                        return BooleanConstant.fromValue((long)left.shortValue() <= right.longValue());
                    }
                }
                break;
            }
            case 10: {
                switch (rightId) {
                    case 2: {
                        return BooleanConstant.fromValue(left.intValue() <= right.charValue());
                    }
                    case 9: {
                        return BooleanConstant.fromValue((float)left.intValue() <= right.floatValue());
                    }
                    case 8: {
                        return BooleanConstant.fromValue((double)left.intValue() <= right.doubleValue());
                    }
                    case 3: {
                        return BooleanConstant.fromValue(left.intValue() <= right.byteValue());
                    }
                    case 4: {
                        return BooleanConstant.fromValue(left.intValue() <= right.shortValue());
                    }
                    case 10: {
                        return BooleanConstant.fromValue(left.intValue() <= right.intValue());
                    }
                    case 7: {
                        return BooleanConstant.fromValue((long)left.intValue() <= right.longValue());
                    }
                }
                break;
            }
            case 7: {
                switch (rightId) {
                    case 2: {
                        return BooleanConstant.fromValue(left.longValue() <= (long)right.charValue());
                    }
                    case 9: {
                        return BooleanConstant.fromValue((float)left.longValue() <= right.floatValue());
                    }
                    case 8: {
                        return BooleanConstant.fromValue((double)left.longValue() <= right.doubleValue());
                    }
                    case 3: {
                        return BooleanConstant.fromValue(left.longValue() <= (long)right.byteValue());
                    }
                    case 4: {
                        return BooleanConstant.fromValue(left.longValue() <= (long)right.shortValue());
                    }
                    case 10: {
                        return BooleanConstant.fromValue(left.longValue() <= (long)right.intValue());
                    }
                    case 7: {
                        return BooleanConstant.fromValue(left.longValue() <= right.longValue());
                    }
                }
            }
        }
        return NotAConstant;
    }

    public static final Constant computeConstantOperationMINUS(Constant left, int leftId, Constant right, int rightId) {
        switch (leftId) {
            case 2: {
                switch (rightId) {
                    case 2: {
                        return IntConstant.fromValue(left.charValue() - right.charValue());
                    }
                    case 9: {
                        return FloatConstant.fromValue((float)left.charValue() - right.floatValue());
                    }
                    case 8: {
                        return DoubleConstant.fromValue((double)left.charValue() - right.doubleValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(left.charValue() - right.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(left.charValue() - right.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(left.charValue() - right.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue((long)left.charValue() - right.longValue());
                    }
                }
                break;
            }
            case 9: {
                switch (rightId) {
                    case 2: {
                        return FloatConstant.fromValue(left.floatValue() - (float)right.charValue());
                    }
                    case 9: {
                        return FloatConstant.fromValue(left.floatValue() - right.floatValue());
                    }
                    case 8: {
                        return DoubleConstant.fromValue((double)left.floatValue() - right.doubleValue());
                    }
                    case 3: {
                        return FloatConstant.fromValue(left.floatValue() - (float)right.byteValue());
                    }
                    case 4: {
                        return FloatConstant.fromValue(left.floatValue() - (float)right.shortValue());
                    }
                    case 10: {
                        return FloatConstant.fromValue(left.floatValue() - (float)right.intValue());
                    }
                    case 7: {
                        return FloatConstant.fromValue(left.floatValue() - (float)right.longValue());
                    }
                }
                break;
            }
            case 8: {
                switch (rightId) {
                    case 2: {
                        return DoubleConstant.fromValue(left.doubleValue() - (double)right.charValue());
                    }
                    case 9: {
                        return DoubleConstant.fromValue(left.doubleValue() - (double)right.floatValue());
                    }
                    case 8: {
                        return DoubleConstant.fromValue(left.doubleValue() - right.doubleValue());
                    }
                    case 3: {
                        return DoubleConstant.fromValue(left.doubleValue() - (double)right.byteValue());
                    }
                    case 4: {
                        return DoubleConstant.fromValue(left.doubleValue() - (double)right.shortValue());
                    }
                    case 10: {
                        return DoubleConstant.fromValue(left.doubleValue() - (double)right.intValue());
                    }
                    case 7: {
                        return DoubleConstant.fromValue(left.doubleValue() - (double)right.longValue());
                    }
                }
                break;
            }
            case 3: {
                switch (rightId) {
                    case 2: {
                        return IntConstant.fromValue(left.byteValue() - right.charValue());
                    }
                    case 9: {
                        return FloatConstant.fromValue((float)left.byteValue() - right.floatValue());
                    }
                    case 8: {
                        return DoubleConstant.fromValue((double)left.byteValue() - right.doubleValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(left.byteValue() - right.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(left.byteValue() - right.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(left.byteValue() - right.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue((long)left.byteValue() - right.longValue());
                    }
                }
                break;
            }
            case 4: {
                switch (rightId) {
                    case 2: {
                        return IntConstant.fromValue(left.shortValue() - right.charValue());
                    }
                    case 9: {
                        return FloatConstant.fromValue((float)left.shortValue() - right.floatValue());
                    }
                    case 8: {
                        return DoubleConstant.fromValue((double)left.shortValue() - right.doubleValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(left.shortValue() - right.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(left.shortValue() - right.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(left.shortValue() - right.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue((long)left.shortValue() - right.longValue());
                    }
                }
                break;
            }
            case 10: {
                switch (rightId) {
                    case 2: {
                        return IntConstant.fromValue(left.intValue() - right.charValue());
                    }
                    case 9: {
                        return FloatConstant.fromValue((float)left.intValue() - right.floatValue());
                    }
                    case 8: {
                        return DoubleConstant.fromValue((double)left.intValue() - right.doubleValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(left.intValue() - right.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(left.intValue() - right.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(left.intValue() - right.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue((long)left.intValue() - right.longValue());
                    }
                }
                break;
            }
            case 7: {
                switch (rightId) {
                    case 2: {
                        return LongConstant.fromValue(left.longValue() - (long)right.charValue());
                    }
                    case 9: {
                        return FloatConstant.fromValue((float)left.longValue() - right.floatValue());
                    }
                    case 8: {
                        return DoubleConstant.fromValue((double)left.longValue() - right.doubleValue());
                    }
                    case 3: {
                        return LongConstant.fromValue(left.longValue() - (long)right.byteValue());
                    }
                    case 4: {
                        return LongConstant.fromValue(left.longValue() - (long)right.shortValue());
                    }
                    case 10: {
                        return LongConstant.fromValue(left.longValue() - (long)right.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue(left.longValue() - right.longValue());
                    }
                }
            }
        }
        return NotAConstant;
    }

    public static final Constant computeConstantOperationMULTIPLY(Constant left, int leftId, Constant right, int rightId) {
        switch (leftId) {
            case 2: {
                switch (rightId) {
                    case 2: {
                        return IntConstant.fromValue(left.charValue() * right.charValue());
                    }
                    case 9: {
                        return FloatConstant.fromValue((float)left.charValue() * right.floatValue());
                    }
                    case 8: {
                        return DoubleConstant.fromValue((double)left.charValue() * right.doubleValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(left.charValue() * right.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(left.charValue() * right.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(left.charValue() * right.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue((long)left.charValue() * right.longValue());
                    }
                }
                break;
            }
            case 9: {
                switch (rightId) {
                    case 2: {
                        return FloatConstant.fromValue(left.floatValue() * (float)right.charValue());
                    }
                    case 9: {
                        return FloatConstant.fromValue(left.floatValue() * right.floatValue());
                    }
                    case 8: {
                        return DoubleConstant.fromValue((double)left.floatValue() * right.doubleValue());
                    }
                    case 3: {
                        return FloatConstant.fromValue(left.floatValue() * (float)right.byteValue());
                    }
                    case 4: {
                        return FloatConstant.fromValue(left.floatValue() * (float)right.shortValue());
                    }
                    case 10: {
                        return FloatConstant.fromValue(left.floatValue() * (float)right.intValue());
                    }
                    case 7: {
                        return FloatConstant.fromValue(left.floatValue() * (float)right.longValue());
                    }
                }
                break;
            }
            case 8: {
                switch (rightId) {
                    case 2: {
                        return DoubleConstant.fromValue(left.doubleValue() * (double)right.charValue());
                    }
                    case 9: {
                        return DoubleConstant.fromValue(left.doubleValue() * (double)right.floatValue());
                    }
                    case 8: {
                        return DoubleConstant.fromValue(left.doubleValue() * right.doubleValue());
                    }
                    case 3: {
                        return DoubleConstant.fromValue(left.doubleValue() * (double)right.byteValue());
                    }
                    case 4: {
                        return DoubleConstant.fromValue(left.doubleValue() * (double)right.shortValue());
                    }
                    case 10: {
                        return DoubleConstant.fromValue(left.doubleValue() * (double)right.intValue());
                    }
                    case 7: {
                        return DoubleConstant.fromValue(left.doubleValue() * (double)right.longValue());
                    }
                }
                break;
            }
            case 3: {
                switch (rightId) {
                    case 2: {
                        return IntConstant.fromValue(left.byteValue() * right.charValue());
                    }
                    case 9: {
                        return FloatConstant.fromValue((float)left.byteValue() * right.floatValue());
                    }
                    case 8: {
                        return DoubleConstant.fromValue((double)left.byteValue() * right.doubleValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(left.byteValue() * right.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(left.byteValue() * right.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(left.byteValue() * right.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue((long)left.byteValue() * right.longValue());
                    }
                }
                break;
            }
            case 4: {
                switch (rightId) {
                    case 2: {
                        return IntConstant.fromValue(left.shortValue() * right.charValue());
                    }
                    case 9: {
                        return FloatConstant.fromValue((float)left.shortValue() * right.floatValue());
                    }
                    case 8: {
                        return DoubleConstant.fromValue((double)left.shortValue() * right.doubleValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(left.shortValue() * right.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(left.shortValue() * right.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(left.shortValue() * right.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue((long)left.shortValue() * right.longValue());
                    }
                }
                break;
            }
            case 10: {
                switch (rightId) {
                    case 2: {
                        return IntConstant.fromValue(left.intValue() * right.charValue());
                    }
                    case 9: {
                        return FloatConstant.fromValue((float)left.intValue() * right.floatValue());
                    }
                    case 8: {
                        return DoubleConstant.fromValue((double)left.intValue() * right.doubleValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(left.intValue() * right.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(left.intValue() * right.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(left.intValue() * right.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue((long)left.intValue() * right.longValue());
                    }
                }
                break;
            }
            case 7: {
                switch (rightId) {
                    case 2: {
                        return LongConstant.fromValue(left.longValue() * (long)right.charValue());
                    }
                    case 9: {
                        return FloatConstant.fromValue((float)left.longValue() * right.floatValue());
                    }
                    case 8: {
                        return DoubleConstant.fromValue((double)left.longValue() * right.doubleValue());
                    }
                    case 3: {
                        return LongConstant.fromValue(left.longValue() * (long)right.byteValue());
                    }
                    case 4: {
                        return LongConstant.fromValue(left.longValue() * (long)right.shortValue());
                    }
                    case 10: {
                        return LongConstant.fromValue(left.longValue() * (long)right.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue(left.longValue() * right.longValue());
                    }
                }
            }
        }
        return NotAConstant;
    }

    public static final Constant computeConstantOperationOR(Constant left, int leftId, Constant right, int rightId) {
        switch (leftId) {
            case 5: {
                return BooleanConstant.fromValue(left.booleanValue() | right.booleanValue());
            }
            case 2: {
                switch (rightId) {
                    case 2: {
                        return IntConstant.fromValue(left.charValue() | right.charValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(left.charValue() | right.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(left.charValue() | right.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(left.charValue() | right.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue((long)left.charValue() | right.longValue());
                    }
                }
                break;
            }
            case 3: {
                switch (rightId) {
                    case 2: {
                        return IntConstant.fromValue(left.byteValue() | right.charValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(left.byteValue() | right.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(left.byteValue() | right.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(left.byteValue() | right.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue((long)left.byteValue() | right.longValue());
                    }
                }
                break;
            }
            case 4: {
                switch (rightId) {
                    case 2: {
                        return IntConstant.fromValue(left.shortValue() | right.charValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(left.shortValue() | right.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(left.shortValue() | right.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(left.shortValue() | right.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue((long)left.shortValue() | right.longValue());
                    }
                }
                break;
            }
            case 10: {
                switch (rightId) {
                    case 2: {
                        return IntConstant.fromValue(left.intValue() | right.charValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(left.intValue() | right.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(left.intValue() | right.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(left.intValue() | right.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue((long)left.intValue() | right.longValue());
                    }
                }
                break;
            }
            case 7: {
                switch (rightId) {
                    case 2: {
                        return LongConstant.fromValue(left.longValue() | (long)right.charValue());
                    }
                    case 3: {
                        return LongConstant.fromValue(left.longValue() | (long)right.byteValue());
                    }
                    case 4: {
                        return LongConstant.fromValue(left.longValue() | (long)right.shortValue());
                    }
                    case 10: {
                        return LongConstant.fromValue(left.longValue() | (long)right.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue(left.longValue() | right.longValue());
                    }
                }
            }
        }
        return NotAConstant;
    }

    public static final Constant computeConstantOperationOR_OR(Constant left, int leftId, Constant right, int rightId) {
        return BooleanConstant.fromValue(left.booleanValue() || right.booleanValue());
    }

    public static final Constant computeConstantOperationPLUS(Constant left, int leftId, Constant right, int rightId) {
        switch (leftId) {
            case 1: {
                if (rightId != 11) break;
                return StringConstant.fromValue(String.valueOf(left.stringValue()) + right.stringValue());
            }
            case 5: {
                if (rightId != 11) break;
                return StringConstant.fromValue(String.valueOf(left.stringValue()) + right.stringValue());
            }
            case 2: {
                switch (rightId) {
                    case 2: {
                        return IntConstant.fromValue(left.charValue() + right.charValue());
                    }
                    case 9: {
                        return FloatConstant.fromValue((float)left.charValue() + right.floatValue());
                    }
                    case 8: {
                        return DoubleConstant.fromValue((double)left.charValue() + right.doubleValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(left.charValue() + right.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(left.charValue() + right.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(left.charValue() + right.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue((long)left.charValue() + right.longValue());
                    }
                    case 11: {
                        return StringConstant.fromValue(String.valueOf(left.stringValue()) + right.stringValue());
                    }
                }
                break;
            }
            case 9: {
                switch (rightId) {
                    case 2: {
                        return FloatConstant.fromValue(left.floatValue() + (float)right.charValue());
                    }
                    case 9: {
                        return FloatConstant.fromValue(left.floatValue() + right.floatValue());
                    }
                    case 8: {
                        return DoubleConstant.fromValue((double)left.floatValue() + right.doubleValue());
                    }
                    case 3: {
                        return FloatConstant.fromValue(left.floatValue() + (float)right.byteValue());
                    }
                    case 4: {
                        return FloatConstant.fromValue(left.floatValue() + (float)right.shortValue());
                    }
                    case 10: {
                        return FloatConstant.fromValue(left.floatValue() + (float)right.intValue());
                    }
                    case 7: {
                        return FloatConstant.fromValue(left.floatValue() + (float)right.longValue());
                    }
                    case 11: {
                        return StringConstant.fromValue(String.valueOf(left.stringValue()) + right.stringValue());
                    }
                }
                break;
            }
            case 8: {
                switch (rightId) {
                    case 2: {
                        return DoubleConstant.fromValue(left.doubleValue() + (double)right.charValue());
                    }
                    case 9: {
                        return DoubleConstant.fromValue(left.doubleValue() + (double)right.floatValue());
                    }
                    case 8: {
                        return DoubleConstant.fromValue(left.doubleValue() + right.doubleValue());
                    }
                    case 3: {
                        return DoubleConstant.fromValue(left.doubleValue() + (double)right.byteValue());
                    }
                    case 4: {
                        return DoubleConstant.fromValue(left.doubleValue() + (double)right.shortValue());
                    }
                    case 10: {
                        return DoubleConstant.fromValue(left.doubleValue() + (double)right.intValue());
                    }
                    case 7: {
                        return DoubleConstant.fromValue(left.doubleValue() + (double)right.longValue());
                    }
                    case 11: {
                        return StringConstant.fromValue(String.valueOf(left.stringValue()) + right.stringValue());
                    }
                }
                break;
            }
            case 3: {
                switch (rightId) {
                    case 2: {
                        return IntConstant.fromValue(left.byteValue() + right.charValue());
                    }
                    case 9: {
                        return FloatConstant.fromValue((float)left.byteValue() + right.floatValue());
                    }
                    case 8: {
                        return DoubleConstant.fromValue((double)left.byteValue() + right.doubleValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(left.byteValue() + right.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(left.byteValue() + right.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(left.byteValue() + right.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue((long)left.byteValue() + right.longValue());
                    }
                    case 11: {
                        return StringConstant.fromValue(String.valueOf(left.stringValue()) + right.stringValue());
                    }
                }
                break;
            }
            case 4: {
                switch (rightId) {
                    case 2: {
                        return IntConstant.fromValue(left.shortValue() + right.charValue());
                    }
                    case 9: {
                        return FloatConstant.fromValue((float)left.shortValue() + right.floatValue());
                    }
                    case 8: {
                        return DoubleConstant.fromValue((double)left.shortValue() + right.doubleValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(left.shortValue() + right.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(left.shortValue() + right.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(left.shortValue() + right.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue((long)left.shortValue() + right.longValue());
                    }
                    case 11: {
                        return StringConstant.fromValue(String.valueOf(left.stringValue()) + right.stringValue());
                    }
                }
                break;
            }
            case 10: {
                switch (rightId) {
                    case 2: {
                        return IntConstant.fromValue(left.intValue() + right.charValue());
                    }
                    case 9: {
                        return FloatConstant.fromValue((float)left.intValue() + right.floatValue());
                    }
                    case 8: {
                        return DoubleConstant.fromValue((double)left.intValue() + right.doubleValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(left.intValue() + right.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(left.intValue() + right.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(left.intValue() + right.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue((long)left.intValue() + right.longValue());
                    }
                    case 11: {
                        return StringConstant.fromValue(String.valueOf(left.stringValue()) + right.stringValue());
                    }
                }
                break;
            }
            case 7: {
                switch (rightId) {
                    case 2: {
                        return LongConstant.fromValue(left.longValue() + (long)right.charValue());
                    }
                    case 9: {
                        return FloatConstant.fromValue((float)left.longValue() + right.floatValue());
                    }
                    case 8: {
                        return DoubleConstant.fromValue((double)left.longValue() + right.doubleValue());
                    }
                    case 3: {
                        return LongConstant.fromValue(left.longValue() + (long)right.byteValue());
                    }
                    case 4: {
                        return LongConstant.fromValue(left.longValue() + (long)right.shortValue());
                    }
                    case 10: {
                        return LongConstant.fromValue(left.longValue() + (long)right.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue(left.longValue() + right.longValue());
                    }
                    case 11: {
                        return StringConstant.fromValue(String.valueOf(left.stringValue()) + right.stringValue());
                    }
                }
                break;
            }
            case 11: {
                switch (rightId) {
                    case 2: {
                        return StringConstant.fromValue(String.valueOf(left.stringValue()) + String.valueOf(right.charValue()));
                    }
                    case 9: {
                        return StringConstant.fromValue(String.valueOf(left.stringValue()) + String.valueOf(right.floatValue()));
                    }
                    case 8: {
                        return StringConstant.fromValue(String.valueOf(left.stringValue()) + String.valueOf(right.doubleValue()));
                    }
                    case 3: {
                        return StringConstant.fromValue(String.valueOf(left.stringValue()) + String.valueOf(right.byteValue()));
                    }
                    case 4: {
                        return StringConstant.fromValue(String.valueOf(left.stringValue()) + String.valueOf(right.shortValue()));
                    }
                    case 10: {
                        return StringConstant.fromValue(String.valueOf(left.stringValue()) + String.valueOf(right.intValue()));
                    }
                    case 7: {
                        return StringConstant.fromValue(String.valueOf(left.stringValue()) + String.valueOf(right.longValue()));
                    }
                    case 11: {
                        return StringConstant.fromValue(String.valueOf(left.stringValue()) + right.stringValue());
                    }
                    case 5: {
                        return StringConstant.fromValue(String.valueOf(left.stringValue()) + right.booleanValue());
                    }
                }
            }
        }
        return NotAConstant;
    }

    public static final Constant computeConstantOperationREMAINDER(Constant left, int leftId, Constant right, int rightId) {
        switch (leftId) {
            case 2: {
                switch (rightId) {
                    case 2: {
                        return IntConstant.fromValue(left.charValue() % right.charValue());
                    }
                    case 9: {
                        return FloatConstant.fromValue((float)left.charValue() % right.floatValue());
                    }
                    case 8: {
                        return DoubleConstant.fromValue((double)left.charValue() % right.doubleValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(left.charValue() % right.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(left.charValue() % right.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(left.charValue() % right.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue((long)left.charValue() % right.longValue());
                    }
                }
                break;
            }
            case 9: {
                switch (rightId) {
                    case 2: {
                        return FloatConstant.fromValue(left.floatValue() % (float)right.charValue());
                    }
                    case 9: {
                        return FloatConstant.fromValue(left.floatValue() % right.floatValue());
                    }
                    case 8: {
                        return DoubleConstant.fromValue((double)left.floatValue() % right.doubleValue());
                    }
                    case 3: {
                        return FloatConstant.fromValue(left.floatValue() % (float)right.byteValue());
                    }
                    case 4: {
                        return FloatConstant.fromValue(left.floatValue() % (float)right.shortValue());
                    }
                    case 10: {
                        return FloatConstant.fromValue(left.floatValue() % (float)right.intValue());
                    }
                    case 7: {
                        return FloatConstant.fromValue(left.floatValue() % (float)right.longValue());
                    }
                }
                break;
            }
            case 8: {
                switch (rightId) {
                    case 2: {
                        return DoubleConstant.fromValue(left.doubleValue() % (double)right.charValue());
                    }
                    case 9: {
                        return DoubleConstant.fromValue(left.doubleValue() % (double)right.floatValue());
                    }
                    case 8: {
                        return DoubleConstant.fromValue(left.doubleValue() % right.doubleValue());
                    }
                    case 3: {
                        return DoubleConstant.fromValue(left.doubleValue() % (double)right.byteValue());
                    }
                    case 4: {
                        return DoubleConstant.fromValue(left.doubleValue() % (double)right.shortValue());
                    }
                    case 10: {
                        return DoubleConstant.fromValue(left.doubleValue() % (double)right.intValue());
                    }
                    case 7: {
                        return DoubleConstant.fromValue(left.doubleValue() % (double)right.longValue());
                    }
                }
                break;
            }
            case 3: {
                switch (rightId) {
                    case 2: {
                        return IntConstant.fromValue(left.byteValue() % right.charValue());
                    }
                    case 9: {
                        return FloatConstant.fromValue((float)left.byteValue() % right.floatValue());
                    }
                    case 8: {
                        return DoubleConstant.fromValue((double)left.byteValue() % right.doubleValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(left.byteValue() % right.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(left.byteValue() % right.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(left.byteValue() % right.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue((long)left.byteValue() % right.longValue());
                    }
                }
                break;
            }
            case 4: {
                switch (rightId) {
                    case 2: {
                        return IntConstant.fromValue(left.shortValue() % right.charValue());
                    }
                    case 9: {
                        return FloatConstant.fromValue((float)left.shortValue() % right.floatValue());
                    }
                    case 8: {
                        return DoubleConstant.fromValue((double)left.shortValue() % right.doubleValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(left.shortValue() % right.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(left.shortValue() % right.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(left.shortValue() % right.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue((long)left.shortValue() % right.longValue());
                    }
                }
                break;
            }
            case 10: {
                switch (rightId) {
                    case 2: {
                        return IntConstant.fromValue(left.intValue() % right.charValue());
                    }
                    case 9: {
                        return FloatConstant.fromValue((float)left.intValue() % right.floatValue());
                    }
                    case 8: {
                        return DoubleConstant.fromValue((double)left.intValue() % right.doubleValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(left.intValue() % right.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(left.intValue() % right.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(left.intValue() % right.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue((long)left.intValue() % right.longValue());
                    }
                }
                break;
            }
            case 7: {
                switch (rightId) {
                    case 2: {
                        return LongConstant.fromValue(left.longValue() % (long)right.charValue());
                    }
                    case 9: {
                        return FloatConstant.fromValue((float)left.longValue() % right.floatValue());
                    }
                    case 8: {
                        return DoubleConstant.fromValue((double)left.longValue() % right.doubleValue());
                    }
                    case 3: {
                        return LongConstant.fromValue(left.longValue() % (long)right.byteValue());
                    }
                    case 4: {
                        return LongConstant.fromValue(left.longValue() % (long)right.shortValue());
                    }
                    case 10: {
                        return LongConstant.fromValue(left.longValue() % (long)right.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue(left.longValue() % right.longValue());
                    }
                }
            }
        }
        return NotAConstant;
    }

    public static final Constant computeConstantOperationRIGHT_SHIFT(Constant left, int leftId, Constant right, int rightId) {
        switch (leftId) {
            case 2: {
                switch (rightId) {
                    case 2: {
                        return IntConstant.fromValue(left.charValue() >> right.charValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(left.charValue() >> right.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(left.charValue() >> right.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(left.charValue() >> right.intValue());
                    }
                    case 7: {
                        return IntConstant.fromValue(left.charValue() >> (int)right.longValue());
                    }
                }
                break;
            }
            case 3: {
                switch (rightId) {
                    case 2: {
                        return IntConstant.fromValue(left.byteValue() >> right.charValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(left.byteValue() >> right.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(left.byteValue() >> right.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(left.byteValue() >> right.intValue());
                    }
                    case 7: {
                        return IntConstant.fromValue(left.byteValue() >> (int)right.longValue());
                    }
                }
                break;
            }
            case 4: {
                switch (rightId) {
                    case 2: {
                        return IntConstant.fromValue(left.shortValue() >> right.charValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(left.shortValue() >> right.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(left.shortValue() >> right.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(left.shortValue() >> right.intValue());
                    }
                    case 7: {
                        return IntConstant.fromValue(left.shortValue() >> (int)right.longValue());
                    }
                }
                break;
            }
            case 10: {
                switch (rightId) {
                    case 2: {
                        return IntConstant.fromValue(left.intValue() >> right.charValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(left.intValue() >> right.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(left.intValue() >> right.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(left.intValue() >> right.intValue());
                    }
                    case 7: {
                        return IntConstant.fromValue(left.intValue() >> (int)right.longValue());
                    }
                }
                break;
            }
            case 7: {
                switch (rightId) {
                    case 2: {
                        return LongConstant.fromValue(left.longValue() >> right.charValue());
                    }
                    case 3: {
                        return LongConstant.fromValue(left.longValue() >> right.byteValue());
                    }
                    case 4: {
                        return LongConstant.fromValue(left.longValue() >> right.shortValue());
                    }
                    case 10: {
                        return LongConstant.fromValue(left.longValue() >> right.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue(left.longValue() >> (int)right.longValue());
                    }
                }
            }
        }
        return NotAConstant;
    }

    public static final Constant computeConstantOperationUNSIGNED_RIGHT_SHIFT(Constant left, int leftId, Constant right, int rightId) {
        switch (leftId) {
            case 2: {
                switch (rightId) {
                    case 2: {
                        return IntConstant.fromValue(left.charValue() >>> right.charValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(left.charValue() >>> right.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(left.charValue() >>> right.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(left.charValue() >>> right.intValue());
                    }
                    case 7: {
                        return IntConstant.fromValue(left.charValue() >>> (int)right.longValue());
                    }
                }
                break;
            }
            case 3: {
                switch (rightId) {
                    case 2: {
                        return IntConstant.fromValue(left.byteValue() >>> right.charValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(left.byteValue() >>> right.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(left.byteValue() >>> right.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(left.byteValue() >>> right.intValue());
                    }
                    case 7: {
                        return IntConstant.fromValue(left.byteValue() >>> (int)right.longValue());
                    }
                }
                break;
            }
            case 4: {
                switch (rightId) {
                    case 2: {
                        return IntConstant.fromValue(left.shortValue() >>> right.charValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(left.shortValue() >>> right.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(left.shortValue() >>> right.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(left.shortValue() >>> right.intValue());
                    }
                    case 7: {
                        return IntConstant.fromValue(left.shortValue() >>> (int)right.longValue());
                    }
                }
                break;
            }
            case 10: {
                switch (rightId) {
                    case 2: {
                        return IntConstant.fromValue(left.intValue() >>> right.charValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(left.intValue() >>> right.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(left.intValue() >>> right.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(left.intValue() >>> right.intValue());
                    }
                    case 7: {
                        return IntConstant.fromValue(left.intValue() >>> (int)right.longValue());
                    }
                }
                break;
            }
            case 7: {
                switch (rightId) {
                    case 2: {
                        return LongConstant.fromValue(left.longValue() >>> right.charValue());
                    }
                    case 3: {
                        return LongConstant.fromValue(left.longValue() >>> right.byteValue());
                    }
                    case 4: {
                        return LongConstant.fromValue(left.longValue() >>> right.shortValue());
                    }
                    case 10: {
                        return LongConstant.fromValue(left.longValue() >>> right.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue(left.longValue() >>> (int)right.longValue());
                    }
                }
            }
        }
        return NotAConstant;
    }

    public static final Constant computeConstantOperationXOR(Constant left, int leftId, Constant right, int rightId) {
        switch (leftId) {
            case 5: {
                return BooleanConstant.fromValue(left.booleanValue() ^ right.booleanValue());
            }
            case 2: {
                switch (rightId) {
                    case 2: {
                        return IntConstant.fromValue(left.charValue() ^ right.charValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(left.charValue() ^ right.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(left.charValue() ^ right.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(left.charValue() ^ right.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue((long)left.charValue() ^ right.longValue());
                    }
                }
                break;
            }
            case 3: {
                switch (rightId) {
                    case 2: {
                        return IntConstant.fromValue(left.byteValue() ^ right.charValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(left.byteValue() ^ right.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(left.byteValue() ^ right.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(left.byteValue() ^ right.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue((long)left.byteValue() ^ right.longValue());
                    }
                }
                break;
            }
            case 4: {
                switch (rightId) {
                    case 2: {
                        return IntConstant.fromValue(left.shortValue() ^ right.charValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(left.shortValue() ^ right.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(left.shortValue() ^ right.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(left.shortValue() ^ right.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue((long)left.shortValue() ^ right.longValue());
                    }
                }
                break;
            }
            case 10: {
                switch (rightId) {
                    case 2: {
                        return IntConstant.fromValue(left.intValue() ^ right.charValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(left.intValue() ^ right.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(left.intValue() ^ right.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(left.intValue() ^ right.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue((long)left.intValue() ^ right.longValue());
                    }
                }
                break;
            }
            case 7: {
                switch (rightId) {
                    case 2: {
                        return LongConstant.fromValue(left.longValue() ^ (long)right.charValue());
                    }
                    case 3: {
                        return LongConstant.fromValue(left.longValue() ^ (long)right.byteValue());
                    }
                    case 4: {
                        return LongConstant.fromValue(left.longValue() ^ (long)right.shortValue());
                    }
                    case 10: {
                        return LongConstant.fromValue(left.longValue() ^ (long)right.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue(left.longValue() ^ right.longValue());
                    }
                }
            }
        }
        return NotAConstant;
    }

    public double doubleValue() {
        throw new ShouldNotImplement(Messages.bind(Messages.constant_cannotCastedInto, new String[]{this.typeName(), "double"}));
    }

    public float floatValue() {
        throw new ShouldNotImplement(Messages.bind(Messages.constant_cannotCastedInto, new String[]{this.typeName(), "float"}));
    }

    public boolean hasSameValue(Constant otherConstant) {
        if (this == otherConstant) {
            return true;
        }
        int typeID = this.typeID();
        if (typeID != otherConstant.typeID()) {
            return false;
        }
        switch (typeID) {
            case 5: {
                return this.booleanValue() == otherConstant.booleanValue();
            }
            case 3: {
                return this.byteValue() == otherConstant.byteValue();
            }
            case 2: {
                return this.charValue() == otherConstant.charValue();
            }
            case 8: {
                return this.doubleValue() == otherConstant.doubleValue();
            }
            case 9: {
                return this.floatValue() == otherConstant.floatValue();
            }
            case 10: {
                return this.intValue() == otherConstant.intValue();
            }
            case 4: {
                return this.shortValue() == otherConstant.shortValue();
            }
            case 7: {
                return this.longValue() == otherConstant.longValue();
            }
            case 11: {
                String value = this.stringValue();
                return value == null ? otherConstant.stringValue() == null : value.equals(otherConstant.stringValue());
            }
        }
        return false;
    }

    public int intValue() {
        throw new ShouldNotImplement(Messages.bind(Messages.constant_cannotCastedInto, new String[]{this.typeName(), "int"}));
    }

    public long longValue() {
        throw new ShouldNotImplement(Messages.bind(Messages.constant_cannotCastedInto, new String[]{this.typeName(), "long"}));
    }

    public short shortValue() {
        throw new ShouldNotImplement(Messages.bind(Messages.constant_cannotConvertedTo, new String[]{this.typeName(), "short"}));
    }

    public String stringValue() {
        throw new ShouldNotImplement(Messages.bind(Messages.constant_cannotConvertedTo, new String[]{this.typeName(), "String"}));
    }

    public String toString() {
        if (this == NotAConstant) {
            return "(Constant) NotAConstant";
        }
        return super.toString();
    }

    public abstract int typeID();

    public String typeName() {
        switch (this.typeID()) {
            case 10: {
                return "int";
            }
            case 3: {
                return "byte";
            }
            case 4: {
                return "short";
            }
            case 2: {
                return "char";
            }
            case 9: {
                return "float";
            }
            case 8: {
                return "double";
            }
            case 5: {
                return "boolean";
            }
            case 7: {
                return "long";
            }
            case 11: {
                return "java.lang.String";
            }
        }
        return "unknown";
    }
}

