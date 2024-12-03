/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.css;

import cz.vutbr.web.css.CSSProperty;
import cz.vutbr.web.css.Term;
import cz.vutbr.web.css.TermAngle;
import cz.vutbr.web.css.TermColor;
import cz.vutbr.web.css.TermIdent;
import cz.vutbr.web.css.TermLength;
import cz.vutbr.web.css.TermLengthOrPercent;
import cz.vutbr.web.css.TermList;
import java.util.List;

public interface TermFunction
extends TermList {
    public String getFunctionName();

    public TermFunction setFunctionName(String var1);

    public boolean isValid();

    public List<List<Term<?>>> getSeparatedArgs(Term<?> var1);

    public List<Term<?>> getSeparatedValues(Term<?> var1, boolean var2);

    public List<Term<?>> getValues(boolean var1);

    public static interface CubicBezier
    extends TimingFunction {
        public float getX1();

        public float getY1();

        public float getX2();

        public float getY2();
    }

    public static interface Frames
    extends TimingFunction {
        public int getFrames();
    }

    public static interface Steps
    extends TimingFunction {
        public int getNumberOfSteps();

        public Direction getDirection();

        public static enum Direction {
            JUMP_START("jump-start"),
            JUMP_END("jump-end"),
            JUMP_BOTH("jump-both"),
            JUMP_NONE("jump-none"),
            START("start"),
            END("end");

            private final String text;

            private Direction(String text) {
                this.text = text;
            }

            public String toString() {
                return this.text;
            }
        }
    }

    public static interface TimingFunction
    extends TermFunction {
    }

    public static interface Repeat
    extends GridFunction {
        public Unit getNumberOfRepetitions();

        public List<Term<?>> getRepeatedTerms();

        public static class Unit {
            private int _numberOfRepetition;
            private boolean _isAutoFit;
            private boolean _isAutoFill;

            public static Unit createWithNRepetitions(int n) {
                return new Unit(n, false, false);
            }

            public static Unit createWithAutoFit() {
                return new Unit(-1, true, false);
            }

            public static Unit createWithAutoFill() {
                return new Unit(-1, false, true);
            }

            private Unit(int numberOfRepetitions, boolean isAutoFit, boolean isAutoFill) {
                this._numberOfRepetition = numberOfRepetitions;
                this._isAutoFit = isAutoFit;
                this._isAutoFill = isAutoFill;
            }

            public Unit setNumberOfRepetition(int n) {
                if (n <= 0) {
                    throw new IllegalArgumentException("Number of repetitions must be positive.");
                }
                this._isAutoFit = false;
                this._isAutoFill = false;
                this._numberOfRepetition = n;
                return this;
            }

            public Unit setAutoFit() {
                this._isAutoFit = true;
                this._isAutoFill = false;
                this._numberOfRepetition = -1;
                return this;
            }

            public Unit setAutoFill() {
                this._isAutoFit = false;
                this._isAutoFill = true;
                this._numberOfRepetition = -1;
                return this;
            }

            public int getNumberOfRepetitions() {
                return this._numberOfRepetition;
            }

            public boolean isAutoFit() {
                return this._isAutoFit;
            }

            public boolean isAutoFill() {
                return this._isAutoFill;
            }
        }
    }

    public static interface MinMax
    extends GridFunction {
        public Unit getMin();

        public Unit getMax();

        public static class Unit {
            private TermLengthOrPercent _lenght;
            private boolean _isMinContent;
            private boolean _isMaxContent;
            private boolean _isAuto;

            public static Unit createWithLenght(TermLengthOrPercent lenght) {
                return new Unit(lenght, false, false, false);
            }

            public static Unit createWithMinContent() {
                return new Unit(null, true, false, false);
            }

            public static Unit createWithMaxContent() {
                return new Unit(null, false, true, false);
            }

            public static Unit createWithAuto() {
                return new Unit(null, false, false, true);
            }

            private Unit(TermLengthOrPercent lenght, boolean isMinContent, boolean isMaxContent, boolean isAuto) {
                this._lenght = lenght;
                this._isMinContent = isMinContent;
                this._isMaxContent = isMaxContent;
                this._isAuto = isAuto;
            }

            public TermLengthOrPercent getLenght() {
                return this._lenght;
            }

            public void setLenght(TermLengthOrPercent lenght) {
                this._lenght = lenght;
                this._isMinContent = false;
                this._isMaxContent = false;
                this._isAuto = false;
            }

            public boolean isIsMinContent() {
                return this._isMinContent;
            }

            public void setIsMinContent(boolean isMinContent) {
                this._lenght = null;
                this._isMinContent = isMinContent;
                this._isMaxContent = false;
                this._isAuto = false;
            }

            public boolean isIsMaxContent() {
                return this._isMaxContent;
            }

            public void setIsMaxContent(boolean isMaxContent) {
                this._lenght = null;
                this._isMinContent = false;
                this._isMaxContent = isMaxContent;
                this._isAuto = false;
            }

            public boolean isIsAuto() {
                return this._isAuto;
            }

            public void setIsAuto(boolean isAuto) {
                this._lenght = null;
                this._isMinContent = false;
                this._isMaxContent = false;
                this._isAuto = isAuto;
            }
        }
    }

    public static interface FitContent
    extends GridFunction {
        public TermLengthOrPercent getMaximum();
    }

    public static interface GridFunction
    extends TermFunction {
    }

    public static interface Attr
    extends TermFunction {
        public String getName();
    }

    public static interface Counters
    extends CounterFunction {
        public String getName();

        public CSSProperty.ListStyleType getStyle();

        public String getSeparator();
    }

    public static interface Counter
    extends CounterFunction {
        public String getName();

        public CSSProperty.ListStyleType getStyle();
    }

    public static interface CounterFunction
    extends TermFunction {
    }

    public static interface Sepia
    extends FilterFunction {
        public float getAmount();
    }

    public static interface Saturate
    extends FilterFunction {
        public float getAmount();
    }

    public static interface Opacity
    extends FilterFunction {
        public float getAmount();
    }

    public static interface Invert
    extends FilterFunction {
        public float getAmount();
    }

    public static interface HueRotate
    extends FilterFunction {
        public TermAngle getAngle();
    }

    public static interface Grayscale
    extends FilterFunction {
        public float getAmount();
    }

    public static interface DropShadow
    extends FilterFunction {
        public TermLength getOffsetX();

        public TermLength getOffsetY();

        public TermLength getBlurRadius();

        public TermColor getColor();
    }

    public static interface Contrast
    extends FilterFunction {
        public float getAmount();
    }

    public static interface Brightness
    extends FilterFunction {
        public float getAmount();
    }

    public static interface Blur
    extends FilterFunction {
        public TermLength getRadius();
    }

    public static interface FilterFunction
    extends TermFunction {
    }

    public static interface RadialGradient
    extends Gradient {
        public TermIdent getShape();

        public TermLengthOrPercent[] getSize();

        public TermIdent getSizeIdent();

        public TermLengthOrPercent[] getPosition();

        public List<Gradient.ColorStop> getColorStops();
    }

    public static interface LinearGradient
    extends Gradient {
        public TermAngle getAngle();

        public List<Gradient.ColorStop> getColorStops();
    }

    public static interface Gradient
    extends TermFunction {
        public boolean isRepeating();

        public static interface ColorStop {
            public TermColor getColor();

            public TermLengthOrPercent getLength();
        }
    }

    public static interface TranslateZ
    extends TransformFunction {
        public TermLengthOrPercent getTranslate();
    }

    public static interface TranslateY
    extends TransformFunction {
        public TermLengthOrPercent getTranslate();
    }

    public static interface TranslateX
    extends TransformFunction {
        public TermLengthOrPercent getTranslate();
    }

    public static interface Translate3d
    extends TransformFunction {
        public TermLengthOrPercent getTranslateX();

        public TermLengthOrPercent getTranslateY();

        public TermLengthOrPercent getTranslateZ();
    }

    public static interface Translate
    extends TransformFunction {
        public TermLengthOrPercent getTranslateX();

        public TermLengthOrPercent getTranslateY();
    }

    public static interface SkewY
    extends TransformFunction {
        public TermAngle getSkew();
    }

    public static interface SkewX
    extends TransformFunction {
        public TermAngle getSkew();
    }

    public static interface Skew
    extends TransformFunction {
        public TermAngle getSkewX();

        public TermAngle getSkewY();
    }

    public static interface ScaleZ
    extends TransformFunction {
        public float getScale();
    }

    public static interface ScaleY
    extends TransformFunction {
        public float getScale();
    }

    public static interface ScaleX
    extends TransformFunction {
        public float getScale();
    }

    public static interface Scale3d
    extends TransformFunction {
        public float getScaleX();

        public float getScaleY();

        public float getScaleZ();
    }

    public static interface Scale
    extends TransformFunction {
        public float getScaleX();

        public float getScaleY();
    }

    public static interface RotateZ
    extends TransformFunction {
        public TermAngle getAngle();
    }

    public static interface RotateY
    extends TransformFunction {
        public TermAngle getAngle();
    }

    public static interface RotateX
    extends TransformFunction {
        public TermAngle getAngle();
    }

    public static interface Rotate3d
    extends TransformFunction {
        public float getX();

        public float getY();

        public float getZ();

        public TermAngle getAngle();
    }

    public static interface Rotate
    extends TransformFunction {
        public TermAngle getAngle();
    }

    public static interface Perspective
    extends TransformFunction {
        public TermLength getDistance();
    }

    public static interface Matrix3d
    extends TransformFunction {
        public float[] getValues();
    }

    public static interface Matrix
    extends TransformFunction {
        public float[] getValues();
    }

    public static interface TransformFunction
    extends TermFunction {
    }
}

