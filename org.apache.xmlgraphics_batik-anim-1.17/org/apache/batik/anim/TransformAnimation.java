/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.anim;

import org.apache.batik.anim.SimpleAnimation;
import org.apache.batik.anim.dom.AnimatableElement;
import org.apache.batik.anim.timing.TimedElement;
import org.apache.batik.anim.values.AnimatableTransformListValue;
import org.apache.batik.anim.values.AnimatableValue;

public class TransformAnimation
extends SimpleAnimation {
    protected short type;
    protected float[] keyTimes2;
    protected float[] keyTimes3;

    public TransformAnimation(TimedElement timedElement, AnimatableElement animatableElement, int calcMode, float[] keyTimes, float[] keySplines, boolean additive, boolean cumulative, AnimatableValue[] values, AnimatableValue from, AnimatableValue to, AnimatableValue by, short type) {
        super(timedElement, animatableElement, calcMode == 2 ? 1 : calcMode, calcMode == 2 ? null : keyTimes, keySplines, additive, cumulative, values, from, to, by);
        int i;
        this.calcMode = calcMode;
        this.type = type;
        if (calcMode != 2) {
            return;
        }
        int count = this.values.length;
        float[] cumulativeDistances2 = null;
        float[] cumulativeDistances3 = null;
        switch (type) {
            case 4: {
                cumulativeDistances3 = new float[count];
                cumulativeDistances3[0] = 0.0f;
            }
            case 2: 
            case 3: {
                cumulativeDistances2 = new float[count];
                cumulativeDistances2[0] = 0.0f;
            }
        }
        float[] cumulativeDistances1 = new float[count];
        cumulativeDistances1[0] = 0.0f;
        for (int i2 = 1; i2 < this.values.length; ++i2) {
            switch (type) {
                case 4: {
                    cumulativeDistances3[i2] = cumulativeDistances3[i2 - 1] + ((AnimatableTransformListValue)this.values[i2 - 1]).distanceTo3(this.values[i2]);
                }
                case 2: 
                case 3: {
                    cumulativeDistances2[i2] = cumulativeDistances2[i2 - 1] + ((AnimatableTransformListValue)this.values[i2 - 1]).distanceTo2(this.values[i2]);
                }
            }
            cumulativeDistances1[i2] = cumulativeDistances1[i2 - 1] + ((AnimatableTransformListValue)this.values[i2 - 1]).distanceTo1(this.values[i2]);
        }
        switch (type) {
            case 4: {
                float totalLength = cumulativeDistances3[count - 1];
                this.keyTimes3 = new float[count];
                this.keyTimes3[0] = 0.0f;
                for (i = 1; i < count - 1; ++i) {
                    this.keyTimes3[i] = cumulativeDistances3[i] / totalLength;
                }
                this.keyTimes3[count - 1] = 1.0f;
            }
            case 2: 
            case 3: {
                float totalLength = cumulativeDistances2[count - 1];
                this.keyTimes2 = new float[count];
                this.keyTimes2[0] = 0.0f;
                for (i = 1; i < count - 1; ++i) {
                    this.keyTimes2[i] = cumulativeDistances2[i] / totalLength;
                }
                this.keyTimes2[count - 1] = 1.0f;
            }
        }
        float totalLength = cumulativeDistances1[count - 1];
        this.keyTimes = new float[count];
        this.keyTimes[0] = 0.0f;
        for (i = 1; i < count - 1; ++i) {
            this.keyTimes[i] = cumulativeDistances1[i] / totalLength;
        }
        this.keyTimes[count - 1] = 1.0f;
    }

    @Override
    protected void sampledAtUnitTime(float unitTime, int repeatIteration) {
        AnimatableTransformListValue nextValue1;
        AnimatableTransformListValue value1;
        AnimatableTransformListValue nextValue2;
        AnimatableTransformListValue value2;
        if (this.calcMode != 2 || this.type == 5 || this.type == 6) {
            super.sampledAtUnitTime(unitTime, repeatIteration);
            return;
        }
        AnimatableTransformListValue value3 = null;
        AnimatableTransformListValue nextValue3 = null;
        float interpolation1 = 0.0f;
        float interpolation2 = 0.0f;
        float interpolation3 = 0.0f;
        if (unitTime != 1.0f) {
            int keyTimeIndex;
            switch (this.type) {
                case 4: {
                    for (keyTimeIndex = 0; keyTimeIndex < this.keyTimes3.length - 1 && unitTime >= this.keyTimes3[keyTimeIndex + 1]; ++keyTimeIndex) {
                    }
                    value3 = (AnimatableTransformListValue)this.values[keyTimeIndex];
                    nextValue3 = (AnimatableTransformListValue)this.values[keyTimeIndex + 1];
                    interpolation3 = (unitTime - this.keyTimes3[keyTimeIndex]) / (this.keyTimes3[keyTimeIndex + 1] - this.keyTimes3[keyTimeIndex]);
                }
            }
            for (keyTimeIndex = 0; keyTimeIndex < this.keyTimes2.length - 1 && unitTime >= this.keyTimes2[keyTimeIndex + 1]; ++keyTimeIndex) {
            }
            value2 = (AnimatableTransformListValue)this.values[keyTimeIndex];
            nextValue2 = (AnimatableTransformListValue)this.values[keyTimeIndex + 1];
            interpolation2 = (unitTime - this.keyTimes2[keyTimeIndex]) / (this.keyTimes2[keyTimeIndex + 1] - this.keyTimes2[keyTimeIndex]);
            for (keyTimeIndex = 0; keyTimeIndex < this.keyTimes.length - 1 && unitTime >= this.keyTimes[keyTimeIndex + 1]; ++keyTimeIndex) {
            }
            value1 = (AnimatableTransformListValue)this.values[keyTimeIndex];
            nextValue1 = (AnimatableTransformListValue)this.values[keyTimeIndex + 1];
            interpolation1 = (unitTime - this.keyTimes[keyTimeIndex]) / (this.keyTimes[keyTimeIndex + 1] - this.keyTimes[keyTimeIndex]);
        } else {
            value2 = value3 = (AnimatableTransformListValue)this.values[this.values.length - 1];
            value1 = value3;
            nextValue3 = null;
            nextValue2 = null;
            nextValue1 = null;
            interpolation3 = 1.0f;
            interpolation2 = 1.0f;
            interpolation1 = 1.0f;
        }
        AnimatableTransformListValue accumulation = this.cumulative ? (AnimatableTransformListValue)this.values[this.values.length - 1] : null;
        switch (this.type) {
            case 4: {
                this.value = AnimatableTransformListValue.interpolate((AnimatableTransformListValue)this.value, value1, value2, value3, nextValue1, nextValue2, nextValue3, interpolation1, interpolation2, interpolation3, accumulation, repeatIteration);
                break;
            }
            default: {
                this.value = AnimatableTransformListValue.interpolate((AnimatableTransformListValue)this.value, value1, value2, nextValue1, nextValue2, interpolation1, interpolation2, accumulation, repeatIteration);
            }
        }
        if (this.value.hasChanged()) {
            this.markDirty();
        }
    }
}

