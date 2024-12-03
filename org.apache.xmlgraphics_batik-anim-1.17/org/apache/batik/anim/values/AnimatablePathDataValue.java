/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.anim.values;

import java.util.Arrays;
import org.apache.batik.anim.dom.AnimationTarget;
import org.apache.batik.anim.values.AnimatableValue;

public class AnimatablePathDataValue
extends AnimatableValue {
    protected short[] commands;
    protected float[] parameters;
    protected static final char[] PATH_COMMANDS = new char[]{' ', 'z', 'M', 'm', 'L', 'l', 'C', 'c', 'Q', 'q', 'A', 'a', 'H', 'h', 'V', 'v', 'S', 's', 'T', 't'};
    protected static final int[] PATH_PARAMS = new int[]{0, 0, 2, 2, 2, 2, 6, 6, 4, 4, 7, 7, 1, 1, 1, 1, 4, 4, 2, 2};

    protected AnimatablePathDataValue(AnimationTarget target) {
        super(target);
    }

    public AnimatablePathDataValue(AnimationTarget target, short[] commands, float[] parameters) {
        super(target);
        this.commands = commands;
        this.parameters = parameters;
    }

    @Override
    public AnimatableValue interpolate(AnimatableValue result, AnimatableValue to, float interpolation, AnimatableValue accumulation, int multiplier) {
        AnimatablePathDataValue res;
        AnimatablePathDataValue toValue = (AnimatablePathDataValue)to;
        AnimatablePathDataValue accValue = (AnimatablePathDataValue)accumulation;
        boolean hasTo = to != null;
        boolean hasAcc = accumulation != null;
        boolean canInterpolate = hasTo && toValue.parameters.length == this.parameters.length && Arrays.equals(toValue.commands, this.commands);
        boolean canAccumulate = hasAcc && accValue.parameters.length == this.parameters.length && Arrays.equals(accValue.commands, this.commands);
        AnimatablePathDataValue base = !canInterpolate && hasTo && (double)interpolation >= 0.5 ? toValue : this;
        int cmdCount = base.commands.length;
        int paramCount = base.parameters.length;
        if (result == null) {
            res = new AnimatablePathDataValue(this.target);
            res.commands = new short[cmdCount];
            res.parameters = new float[paramCount];
            System.arraycopy(base.commands, 0, res.commands, 0, cmdCount);
        } else {
            res = (AnimatablePathDataValue)result;
            if (res.commands == null || res.commands.length != cmdCount) {
                res.commands = new short[cmdCount];
                System.arraycopy(base.commands, 0, res.commands, 0, cmdCount);
                res.hasChanged = true;
            } else if (!Arrays.equals(base.commands, res.commands)) {
                System.arraycopy(base.commands, 0, res.commands, 0, cmdCount);
                res.hasChanged = true;
            }
        }
        for (int i = 0; i < paramCount; ++i) {
            float newValue = base.parameters[i];
            if (canInterpolate) {
                newValue += interpolation * (toValue.parameters[i] - newValue);
            }
            if (canAccumulate) {
                newValue += (float)multiplier * accValue.parameters[i];
            }
            if (res.parameters[i] == newValue) continue;
            res.parameters[i] = newValue;
            res.hasChanged = true;
        }
        return res;
    }

    public short[] getCommands() {
        return this.commands;
    }

    public float[] getParameters() {
        return this.parameters;
    }

    @Override
    public boolean canPace() {
        return false;
    }

    @Override
    public float distanceTo(AnimatableValue other) {
        return 0.0f;
    }

    @Override
    public AnimatableValue getZeroValue() {
        short[] cmds = new short[this.commands.length];
        System.arraycopy(this.commands, 0, cmds, 0, this.commands.length);
        float[] params = new float[this.parameters.length];
        return new AnimatablePathDataValue(this.target, cmds, params);
    }

    @Override
    public String toStringRep() {
        StringBuffer sb = new StringBuffer();
        int k = 0;
        for (short command : this.commands) {
            sb.append(PATH_COMMANDS[command]);
            for (int j = 0; j < PATH_PARAMS[command]; ++j) {
                sb.append(' ');
                sb.append(this.parameters[k++]);
            }
        }
        return sb.toString();
    }
}

