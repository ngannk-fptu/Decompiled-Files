/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.render;

import java.awt.BasicStroke;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.parser.FSRGBColor;
import org.xhtmlrenderer.css.style.BorderRadiusCorner;
import org.xhtmlrenderer.css.style.derived.BorderPropertySet;
import org.xhtmlrenderer.extend.OutputDevice;
import org.xhtmlrenderer.render.RenderingContext;

public class BorderPainter {
    public static final int TOP = 1;
    public static final int LEFT = 2;
    public static final int BOTTOM = 4;
    public static final int RIGHT = 8;
    public static final int ALL = 15;

    public static Path2D generateBorderBounds(Rectangle bounds, BorderPropertySet border, boolean inside) {
        Path2D path = BorderPainter.generateBorderShape(bounds, 1, border, false, inside ? 1.0f : 0.0f, 1.0f);
        path.append(BorderPainter.generateBorderShape(bounds, 8, border, false, inside ? 1.0f : 0.0f, 1.0f), true);
        path.append(BorderPainter.generateBorderShape(bounds, 4, border, false, inside ? 1.0f : 0.0f, 1.0f), true);
        path.append(BorderPainter.generateBorderShape(bounds, 2, border, false, inside ? 1.0f : 0.0f, 1.0f), true);
        return path;
    }

    public static Path2D generateBorderShape(Rectangle bounds, int side, BorderPropertySet border, boolean drawInterior) {
        return BorderPainter.generateBorderShape(bounds, side, border, drawInterior, 0.0f, 1.0f);
    }

    public static Path2D generateBorderShape(Rectangle bounds, int side, BorderPropertySet border, boolean drawInterior, float scaledOffset) {
        return BorderPainter.generateBorderShape(bounds, side, border, drawInterior, scaledOffset, 1.0f);
    }

    public static Path2D generateBorderShape(Rectangle bounds, int side, BorderPropertySet border, boolean drawInterior, float scaledOffset, float widthScale) {
        float defaultAngle;
        RelativeBorderProperties props = new RelativeBorderProperties(bounds, border = border.normalizedInstance(new Rectangle(bounds.width, bounds.height)), 0.0f, side, 1.0f + scaledOffset, widthScale);
        float sideWidth = props.isDimmensionsSwapped() ? (float)bounds.height - (1.0f + scaledOffset) * widthScale * (border.top() + border.bottom()) : (float)bounds.width - (1.0f + scaledOffset) * widthScale * (border.left() + border.right());
        Path2D.Float path = new Path2D.Float();
        float fullAngle = 90.0f;
        float angle = defaultAngle = fullAngle / 2.0f;
        float widthSum = props.getTop() + props.getLeft();
        if (widthSum != 0.0f) {
            angle = fullAngle * props.getTop() / widthSum;
        }
        BorderPainter.appendPath(path, 0.0f - props.getLeft(), 0.0f - props.getTop(), props.getLeftCorner().left(), props.getLeftCorner().right(), 90.0f + angle, -angle - 1.0f, props.getTop(), props.getLeft(), scaledOffset, true, widthScale);
        angle = defaultAngle;
        widthSum = props.getTop() + props.getRight();
        if (widthSum != 0.0f) {
            angle = fullAngle * props.getTop() / widthSum;
        }
        BorderPainter.appendPath(path, sideWidth + props.getRight(), 0.0f - props.getTop(), props.getRightCorner().right(), props.getRightCorner().left(), 90.0f, -angle - 1.0f, props.getTop(), props.getRight(), scaledOffset, false, widthScale);
        if (drawInterior) {
            BorderPainter.appendPath(path, sideWidth, 0.0f, props.getRightCorner().right(), props.getRightCorner().left(), 90.0f - angle, angle + 1.0f, props.getTop(), props.getRight(), scaledOffset + 1.0f, false, widthScale);
            angle = defaultAngle;
            widthSum = props.getTop() + props.getLeft();
            if (widthSum != 0.0f) {
                angle = fullAngle * props.getTop() / widthSum;
            }
            BorderPainter.appendPath(path, 0.0f, 0.0f, props.getLeftCorner().left(), props.getLeftCorner().right(), 90.0f, angle + 1.0f, props.getTop(), props.getLeft(), scaledOffset + 1.0f, true, widthScale);
            path.closePath();
        }
        ((Path2D)path).transform(AffineTransform.getTranslateInstance((!props.isDimmensionsSwapped() ? (float)(-bounds.width) / 2.0f : (float)(-bounds.height) / 2.0f) + (scaledOffset + 1.0f) * props.getLeft(), (props.isDimmensionsSwapped() ? (float)(-bounds.width) / 2.0f : (float)(-bounds.height) / 2.0f) + (scaledOffset + 1.0f) * props.getTop()));
        ((Path2D)path).transform(AffineTransform.getRotateInstance(props.getRotation()));
        ((Path2D)path).transform(AffineTransform.getTranslateInstance((float)bounds.width / 2.0f + (float)bounds.x, (float)bounds.height / 2.0f + (float)bounds.y));
        return path;
    }

    private static void appendPath(Path2D path, float xOffset, float yOffset, float radiusVert, float radiusHoriz, float startAngle, float distance, float topWidth, float sideWidth, float scaleOffset, boolean left, float widthScale) {
        float innerWidth = 2.0f * radiusHoriz - scaleOffset * sideWidth - scaleOffset * sideWidth;
        float innerHeight = 2.0f * radiusVert - scaleOffset * topWidth - scaleOffset * topWidth;
        if (innerWidth > 0.0f && innerHeight > 0.0f) {
            Arc2D.Float arc = new Arc2D.Float(xOffset - (left ? 0.0f : innerWidth), yOffset, innerWidth, innerHeight, startAngle, distance, 0);
            path.append(arc, true);
        } else if (path.getCurrentPoint() == null) {
            path.moveTo(xOffset, yOffset);
        } else {
            path.lineTo(xOffset, yOffset);
        }
    }

    public static void paint(Rectangle bounds, int sides, BorderPropertySet border, RenderingContext ctx, int xOffset, boolean bevel) {
        if ((sides & 1) == 1 && border.noTop()) {
            --sides;
        }
        if ((sides & 2) == 2 && border.noLeft()) {
            sides -= 2;
        }
        if ((sides & 4) == 4 && border.noBottom()) {
            sides -= 4;
        }
        if ((sides & 8) == 8 && border.noRight()) {
            sides -= 8;
        }
        if ((sides & 1) == 1 && border.topColor() != FSRGBColor.TRANSPARENT) {
            BorderPainter.paintBorderSide(ctx.getOutputDevice(), border, bounds, sides, 1, border.topStyle(), xOffset, bevel);
        }
        if ((sides & 4) == 4 && border.bottomColor() != FSRGBColor.TRANSPARENT) {
            BorderPainter.paintBorderSide(ctx.getOutputDevice(), border, bounds, sides, 4, border.bottomStyle(), xOffset, bevel);
        }
        if ((sides & 2) == 2 && border.leftColor() != FSRGBColor.TRANSPARENT) {
            BorderPainter.paintBorderSide(ctx.getOutputDevice(), border, bounds, sides, 2, border.leftStyle(), xOffset, bevel);
        }
        if ((sides & 8) == 8 && border.rightColor() != FSRGBColor.TRANSPARENT) {
            BorderPainter.paintBorderSide(ctx.getOutputDevice(), border, bounds, sides, 8, border.rightStyle(), xOffset, bevel);
        }
    }

    private static void paintBorderSide(OutputDevice outputDevice, BorderPropertySet border, Rectangle bounds, int sides, int currentSide, IdentValue borderSideStyle, int xOffset, boolean bevel) {
        if (borderSideStyle == IdentValue.RIDGE || borderSideStyle == IdentValue.GROOVE) {
            BorderPropertySet bd2 = new BorderPropertySet((int)(border.top() / 2.0f), (int)(border.right() / 2.0f), (int)(border.bottom() / 2.0f), (int)(border.left() / 2.0f));
            BorderPropertySet borderA = null;
            BorderPropertySet borderB = null;
            if (borderSideStyle == IdentValue.RIDGE) {
                borderA = border;
                borderB = border.darken(borderSideStyle);
            } else {
                borderA = border.darken(borderSideStyle);
                borderB = border;
            }
            BorderPainter.paintBorderSideShape(outputDevice, bounds, bd2, borderA, borderB, 0.0f, 1.0f, sides, currentSide, bevel);
            BorderPainter.paintBorderSideShape(outputDevice, bounds, border, borderB, borderA, 1.0f, 0.5f, sides, currentSide, bevel);
        } else if (borderSideStyle == IdentValue.OUTSET) {
            BorderPainter.paintBorderSideShape(outputDevice, bounds, border, border, border.darken(borderSideStyle), 0.0f, 1.0f, sides, currentSide, bevel);
        } else if (borderSideStyle == IdentValue.INSET) {
            BorderPainter.paintBorderSideShape(outputDevice, bounds, border, border.darken(borderSideStyle), border, 0.0f, 1.0f, sides, currentSide, bevel);
        } else if (borderSideStyle == IdentValue.SOLID) {
            outputDevice.setStroke(new BasicStroke(1.0f));
            if (currentSide == 1) {
                outputDevice.setColor(border.topColor());
                outputDevice.fill(BorderPainter.generateBorderShape(bounds, 1, border, true, 0.0f, 1.0f));
            }
            if (currentSide == 8) {
                outputDevice.setColor(border.rightColor());
                outputDevice.fill(BorderPainter.generateBorderShape(bounds, 8, border, true, 0.0f, 1.0f));
            }
            if (currentSide == 4) {
                outputDevice.setColor(border.bottomColor());
                outputDevice.fill(BorderPainter.generateBorderShape(bounds, 4, border, true, 0.0f, 1.0f));
            }
            if (currentSide == 2) {
                outputDevice.setColor(border.leftColor());
                outputDevice.fill(BorderPainter.generateBorderShape(bounds, 2, border, true, 0.0f, 1.0f));
            }
        } else if (borderSideStyle == IdentValue.DOUBLE) {
            BorderPainter.paintDoubleBorder(outputDevice, border, bounds, sides, currentSide, bevel);
        } else {
            int thickness = 0;
            if (currentSide == 1) {
                thickness = (int)border.top();
            }
            if (currentSide == 4) {
                thickness = (int)border.bottom();
            }
            if (currentSide == 8) {
                thickness = (int)border.right();
            }
            if (currentSide == 2) {
                thickness = (int)border.left();
            }
            if (borderSideStyle == IdentValue.DASHED) {
                BorderPainter.paintPatternedRect(outputDevice, bounds, border, border, new float[]{8.0f + (float)(thickness * 2), 4.0f + (float)thickness}, sides, currentSide, xOffset);
            }
            if (borderSideStyle == IdentValue.DOTTED) {
                BorderPainter.paintPatternedRect(outputDevice, bounds, border, border, new float[]{thickness, thickness}, sides, currentSide, xOffset);
            }
        }
    }

    private static void paintDoubleBorder(OutputDevice outputDevice, BorderPropertySet border, Rectangle bounds, int sides, int currentSide, boolean bevel) {
        BorderPainter.paintSolid(outputDevice, bounds, border, 0.0f, 0.33333334f, sides, currentSide, bevel);
        BorderPainter.paintSolid(outputDevice, bounds, border, 2.0f, 0.33333334f, sides, currentSide, bevel);
    }

    private static void paintPatternedRect(OutputDevice outputDevice, Rectangle bounds, BorderPropertySet border, BorderPropertySet color, float[] pattern, int sides, int currentSide, int xOffset) {
        Stroke old_stroke = outputDevice.getStroke();
        Path2D path = BorderPainter.generateBorderShape(bounds, currentSide, border, false, 0.5f, 1.0f);
        Area clip = new Area(BorderPainter.generateBorderShape(bounds, currentSide, border, true, 0.0f, 1.0f));
        Shape old_clip = outputDevice.getClip();
        if (old_clip != null) {
            clip.intersect(new Area(old_clip));
        }
        outputDevice.setClip(clip);
        if (currentSide == 1) {
            outputDevice.setColor(color.topColor());
            outputDevice.setStroke(new BasicStroke(2 * (int)border.top(), 0, 2, 0.0f, pattern, xOffset));
            outputDevice.drawBorderLine(path, 1, (int)border.top(), false);
        } else if (currentSide == 2) {
            outputDevice.setColor(color.leftColor());
            outputDevice.setStroke(new BasicStroke(2 * (int)border.left(), 0, 2, 0.0f, pattern, 0.0f));
            outputDevice.drawBorderLine(path, 2, (int)border.left(), false);
        } else if (currentSide == 8) {
            outputDevice.setColor(color.rightColor());
            outputDevice.setStroke(new BasicStroke(2 * (int)border.right(), 0, 2, 0.0f, pattern, 0.0f));
            outputDevice.drawBorderLine(path, 8, (int)border.right(), false);
        } else if (currentSide == 4) {
            outputDevice.setColor(color.bottomColor());
            outputDevice.setStroke(new BasicStroke(2 * (int)border.bottom(), 0, 2, 0.0f, pattern, xOffset));
            outputDevice.drawBorderLine(path, 4, (int)border.bottom(), false);
        }
        outputDevice.setClip(old_clip);
        outputDevice.setStroke(old_stroke);
    }

    private static void paintBorderSideShape(OutputDevice outputDevice, Rectangle bounds, BorderPropertySet border, BorderPropertySet high, BorderPropertySet low, float offset, float scale, int sides, int currentSide, boolean bevel) {
        if (currentSide == 1) {
            BorderPainter.paintSolid(outputDevice, bounds, high, offset, scale, sides, currentSide, bevel);
        } else if (currentSide == 4) {
            BorderPainter.paintSolid(outputDevice, bounds, low, offset, scale, sides, currentSide, bevel);
        } else if (currentSide == 8) {
            BorderPainter.paintSolid(outputDevice, bounds, low, offset, scale, sides, currentSide, bevel);
        } else if (currentSide == 2) {
            BorderPainter.paintSolid(outputDevice, bounds, high, offset, scale, sides, currentSide, bevel);
        }
    }

    private static void paintSolid(OutputDevice outputDevice, Rectangle bounds, BorderPropertySet border, float offset, float scale, int sides, int currentSide, boolean bevel) {
        if (currentSide == 1) {
            outputDevice.setColor(border.topColor());
            if ((int)border.top() == 1) {
                Path2D line = BorderPainter.generateBorderShape(bounds, currentSide, border, false, offset, scale);
                outputDevice.draw(line);
            } else {
                Path2D line = BorderPainter.generateBorderShape(bounds, currentSide, border, true, offset, scale);
                outputDevice.fill(line);
            }
        } else if (currentSide == 4) {
            outputDevice.setColor(border.bottomColor());
            if ((int)border.bottom() == 1) {
                Path2D line = BorderPainter.generateBorderShape(bounds, currentSide, border, false, offset, scale);
                outputDevice.draw(line);
            } else {
                Path2D line = BorderPainter.generateBorderShape(bounds, currentSide, border, true, offset, scale);
                outputDevice.fill(line);
            }
        } else if (currentSide == 8) {
            outputDevice.setColor(border.rightColor());
            if ((int)border.right() == 1) {
                Path2D line = BorderPainter.generateBorderShape(bounds, currentSide, border, false, offset, scale);
                outputDevice.draw(line);
            } else {
                Path2D line = BorderPainter.generateBorderShape(bounds, currentSide, border, true, offset, scale);
                outputDevice.fill(line);
            }
        } else if (currentSide == 2) {
            outputDevice.setColor(border.leftColor());
            if ((int)border.left() == 1) {
                Path2D line = BorderPainter.generateBorderShape(bounds, currentSide, border, false, offset, scale);
                outputDevice.draw(line);
            } else {
                Path2D line = BorderPainter.generateBorderShape(bounds, currentSide, border, true, offset, scale);
                outputDevice.fill(line);
            }
        }
    }

    private static class RelativeBorderProperties {
        private final float _top;
        private final float _left;
        private final float _right;
        private final BorderRadiusCorner _leftCorner;
        private final BorderRadiusCorner _rightCorner;
        private final double _rotation;
        private final boolean _dimmensionsSwapped;

        public RelativeBorderProperties(Rectangle bounds, BorderPropertySet props, float borderScaleOffset, int side, float scaledOffset, float widthScale) {
            if ((side & 1) == 1) {
                this._top = props.top() * widthScale;
                this._left = props.left() * widthScale;
                this._right = props.right() * widthScale;
                this._leftCorner = props.getTopLeft();
                this._rightCorner = props.getTopRight();
                this._rotation = 0.0;
                this._dimmensionsSwapped = false;
            } else if ((side & 8) == 8) {
                this._top = props.right() * widthScale;
                this._left = props.top() * widthScale;
                this._right = props.bottom() * widthScale;
                this._leftCorner = props.getTopRight();
                this._rightCorner = props.getBottomRight();
                this._rotation = 1.5707963267948966;
                this._dimmensionsSwapped = true;
            } else if ((side & 4) == 4) {
                this._top = props.bottom() * widthScale;
                this._left = props.right() * widthScale;
                this._right = props.left() * widthScale;
                this._leftCorner = props.getBottomRight();
                this._rightCorner = props.getBottomLeft();
                this._rotation = Math.PI;
                this._dimmensionsSwapped = false;
            } else if ((side & 2) == 2) {
                this._top = props.left() * widthScale;
                this._left = props.bottom() * widthScale;
                this._right = props.top() * widthScale;
                this._leftCorner = props.getBottomLeft();
                this._rightCorner = props.getTopLeft();
                this._rotation = 4.71238898038469;
                this._dimmensionsSwapped = true;
            } else {
                throw new IllegalArgumentException("No side found");
            }
        }

        public BorderRadiusCorner getRightCorner() {
            return this._rightCorner;
        }

        public BorderRadiusCorner getLeftCorner() {
            return this._leftCorner;
        }

        public float getTop() {
            return this._top;
        }

        public float getLeft() {
            return this._left;
        }

        public float getRight() {
            return this._right;
        }

        private double getRotation() {
            return this._rotation;
        }

        private boolean isDimmensionsSwapped() {
            return this._dimmensionsSwapped;
        }
    }
}

