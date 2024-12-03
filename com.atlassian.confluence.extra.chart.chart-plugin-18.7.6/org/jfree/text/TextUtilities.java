/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.text;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.text.BreakIterator;
import org.jfree.base.BaseBoot;
import org.jfree.text.TextBlock;
import org.jfree.text.TextFragment;
import org.jfree.text.TextLine;
import org.jfree.text.TextMeasurer;
import org.jfree.ui.TextAnchor;
import org.jfree.util.Log;
import org.jfree.util.LogContext;
import org.jfree.util.ObjectUtilities;

public class TextUtilities {
    protected static final LogContext logger = Log.createContext(class$org$jfree$text$TextUtilities == null ? (class$org$jfree$text$TextUtilities = TextUtilities.class$("org.jfree.text.TextUtilities")) : class$org$jfree$text$TextUtilities);
    private static boolean useDrawRotatedStringWorkaround;
    private static boolean useFontMetricsGetStringBounds;
    static /* synthetic */ Class class$org$jfree$text$TextUtilities;

    private TextUtilities() {
    }

    public static TextBlock createTextBlock(String text, Font font, Paint paint) {
        if (text == null) {
            throw new IllegalArgumentException("Null 'text' argument.");
        }
        TextBlock result = new TextBlock();
        String input = text;
        boolean moreInputToProcess = text.length() > 0;
        boolean start = false;
        while (moreInputToProcess) {
            int index = input.indexOf("\n");
            if (index > 0) {
                String line = input.substring(0, index);
                if (index < input.length() - 1) {
                    result.addLine(line, font, paint);
                    input = input.substring(index + 1);
                    continue;
                }
                moreInputToProcess = false;
                continue;
            }
            if (index == 0) {
                if (index < input.length() - 1) {
                    input = input.substring(index + 1);
                    continue;
                }
                moreInputToProcess = false;
                continue;
            }
            result.addLine(input, font, paint);
            moreInputToProcess = false;
        }
        return result;
    }

    public static TextBlock createTextBlock(String text, Font font, Paint paint, float maxWidth, TextMeasurer measurer) {
        return TextUtilities.createTextBlock(text, font, paint, maxWidth, Integer.MAX_VALUE, measurer);
    }

    public static TextBlock createTextBlock(String text, Font font, Paint paint, float maxWidth, int maxLines, TextMeasurer measurer) {
        TextBlock result = new TextBlock();
        BreakIterator iterator = BreakIterator.getLineInstance();
        iterator.setText(text);
        int current = 0;
        int lines = 0;
        int length = text.length();
        while (current < length && lines < maxLines) {
            int next = TextUtilities.nextLineBreak(text, current, maxWidth, iterator, measurer);
            if (next == -1) {
                result.addLine(text.substring(current), font, paint);
                return result;
            }
            result.addLine(text.substring(current, next), font, paint);
            ++lines;
            for (current = next; current < text.length() && text.charAt(current) == '\n'; ++current) {
            }
        }
        if (current < length) {
            TextLine lastLine = result.getLastLine();
            TextFragment lastFragment = lastLine.getLastTextFragment();
            String oldStr = lastFragment.getText();
            String newStr = "...";
            if (oldStr.length() > 3) {
                newStr = oldStr.substring(0, oldStr.length() - 3) + "...";
            }
            lastLine.removeFragment(lastFragment);
            TextFragment newFragment = new TextFragment(newStr, lastFragment.getFont(), lastFragment.getPaint());
            lastLine.addFragment(newFragment);
        }
        return result;
    }

    private static int nextLineBreak(String text, int start, float width, BreakIterator iterator, TextMeasurer measurer) {
        int end;
        int current = start;
        float x = 0.0f;
        boolean firstWord = true;
        int newline = text.indexOf(10, start);
        if (newline < 0) {
            newline = Integer.MAX_VALUE;
        }
        while ((end = iterator.next()) != -1) {
            if (end > newline) {
                return newline;
            }
            if ((x += measurer.getStringWidth(text, current, end)) > width) {
                if (firstWord) {
                    while (measurer.getStringWidth(text, start, end) > width) {
                        if (--end > start) continue;
                        return end;
                    }
                    return end;
                }
                end = iterator.previous();
                return end;
            }
            firstWord = false;
            current = end;
        }
        return -1;
    }

    public static Rectangle2D getTextBounds(String text, Graphics2D g2, FontMetrics fm) {
        Rectangle2D bounds;
        if (useFontMetricsGetStringBounds) {
            bounds = fm.getStringBounds(text, g2);
            LineMetrics lm = fm.getFont().getLineMetrics(text, g2.getFontRenderContext());
            bounds.setRect(bounds.getX(), bounds.getY(), bounds.getWidth(), lm.getHeight());
        } else {
            double width = fm.stringWidth(text);
            double height = fm.getHeight();
            if (logger.isDebugEnabled()) {
                logger.debug("Height = " + height);
            }
            bounds = new Rectangle2D.Double(0.0, -fm.getAscent(), width, height);
        }
        return bounds;
    }

    public static Rectangle2D drawAlignedString(String text, Graphics2D g2, float x, float y, TextAnchor anchor) {
        Rectangle2D.Double textBounds = new Rectangle2D.Double();
        float[] adjust = TextUtilities.deriveTextBoundsAnchorOffsets(g2, text, anchor, textBounds);
        ((Rectangle2D)textBounds).setRect(x + adjust[0], y + adjust[1] + adjust[2], ((RectangularShape)textBounds).getWidth(), ((RectangularShape)textBounds).getHeight());
        g2.drawString(text, x + adjust[0], y + adjust[1]);
        return textBounds;
    }

    private static float[] deriveTextBoundsAnchorOffsets(Graphics2D g2, String text, TextAnchor anchor, Rectangle2D textBounds) {
        float[] result = new float[3];
        FontRenderContext frc = g2.getFontRenderContext();
        Font f = g2.getFont();
        FontMetrics fm = g2.getFontMetrics(f);
        Rectangle2D bounds = TextUtilities.getTextBounds(text, g2, fm);
        LineMetrics metrics = f.getLineMetrics(text, frc);
        float ascent = metrics.getAscent();
        result[2] = -ascent;
        float halfAscent = ascent / 2.0f;
        float descent = metrics.getDescent();
        float leading = metrics.getLeading();
        float xAdj = 0.0f;
        float yAdj = 0.0f;
        if (anchor == TextAnchor.TOP_CENTER || anchor == TextAnchor.CENTER || anchor == TextAnchor.BOTTOM_CENTER || anchor == TextAnchor.BASELINE_CENTER || anchor == TextAnchor.HALF_ASCENT_CENTER) {
            xAdj = (float)(-bounds.getWidth()) / 2.0f;
        } else if (anchor == TextAnchor.TOP_RIGHT || anchor == TextAnchor.CENTER_RIGHT || anchor == TextAnchor.BOTTOM_RIGHT || anchor == TextAnchor.BASELINE_RIGHT || anchor == TextAnchor.HALF_ASCENT_RIGHT) {
            xAdj = (float)(-bounds.getWidth());
        }
        if (anchor == TextAnchor.TOP_LEFT || anchor == TextAnchor.TOP_CENTER || anchor == TextAnchor.TOP_RIGHT) {
            yAdj = -descent - leading + (float)bounds.getHeight();
        } else if (anchor == TextAnchor.HALF_ASCENT_LEFT || anchor == TextAnchor.HALF_ASCENT_CENTER || anchor == TextAnchor.HALF_ASCENT_RIGHT) {
            yAdj = halfAscent;
        } else if (anchor == TextAnchor.CENTER_LEFT || anchor == TextAnchor.CENTER || anchor == TextAnchor.CENTER_RIGHT) {
            yAdj = -descent - leading + (float)(bounds.getHeight() / 2.0);
        } else if (anchor == TextAnchor.BASELINE_LEFT || anchor == TextAnchor.BASELINE_CENTER || anchor == TextAnchor.BASELINE_RIGHT) {
            yAdj = 0.0f;
        } else if (anchor == TextAnchor.BOTTOM_LEFT || anchor == TextAnchor.BOTTOM_CENTER || anchor == TextAnchor.BOTTOM_RIGHT) {
            yAdj = -metrics.getDescent() - metrics.getLeading();
        }
        if (textBounds != null) {
            textBounds.setRect(bounds);
        }
        result[0] = xAdj;
        result[1] = yAdj;
        return result;
    }

    public static void setUseDrawRotatedStringWorkaround(boolean use) {
        useDrawRotatedStringWorkaround = use;
    }

    public static void drawRotatedString(String text, Graphics2D g2, double angle, float x, float y) {
        TextUtilities.drawRotatedString(text, g2, x, y, angle, x, y);
    }

    public static void drawRotatedString(String text, Graphics2D g2, float textX, float textY, double angle, float rotateX, float rotateY) {
        if (text == null || text.equals("")) {
            return;
        }
        AffineTransform saved = g2.getTransform();
        AffineTransform rotate = AffineTransform.getRotateInstance(angle, rotateX, rotateY);
        g2.transform(rotate);
        if (useDrawRotatedStringWorkaround) {
            TextLayout tl = new TextLayout(text, g2.getFont(), g2.getFontRenderContext());
            tl.draw(g2, textX, textY);
        } else {
            g2.drawString(text, textX, textY);
        }
        g2.setTransform(saved);
    }

    public static void drawRotatedString(String text, Graphics2D g2, float x, float y, TextAnchor textAnchor, double angle, float rotationX, float rotationY) {
        if (text == null || text.equals("")) {
            return;
        }
        float[] textAdj = TextUtilities.deriveTextBoundsAnchorOffsets(g2, text, textAnchor);
        TextUtilities.drawRotatedString(text, g2, x + textAdj[0], y + textAdj[1], angle, rotationX, rotationY);
    }

    public static void drawRotatedString(String text, Graphics2D g2, float x, float y, TextAnchor textAnchor, double angle, TextAnchor rotationAnchor) {
        if (text == null || text.equals("")) {
            return;
        }
        float[] textAdj = TextUtilities.deriveTextBoundsAnchorOffsets(g2, text, textAnchor);
        float[] rotateAdj = TextUtilities.deriveRotationAnchorOffsets(g2, text, rotationAnchor);
        TextUtilities.drawRotatedString(text, g2, x + textAdj[0], y + textAdj[1], angle, x + textAdj[0] + rotateAdj[0], y + textAdj[1] + rotateAdj[1]);
    }

    public static Shape calculateRotatedStringBounds(String text, Graphics2D g2, float x, float y, TextAnchor textAnchor, double angle, TextAnchor rotationAnchor) {
        if (text == null || text.equals("")) {
            return null;
        }
        float[] textAdj = TextUtilities.deriveTextBoundsAnchorOffsets(g2, text, textAnchor);
        if (logger.isDebugEnabled()) {
            logger.debug("TextBoundsAnchorOffsets = " + textAdj[0] + ", " + textAdj[1]);
        }
        float[] rotateAdj = TextUtilities.deriveRotationAnchorOffsets(g2, text, rotationAnchor);
        if (logger.isDebugEnabled()) {
            logger.debug("RotationAnchorOffsets = " + rotateAdj[0] + ", " + rotateAdj[1]);
        }
        Shape result = TextUtilities.calculateRotatedStringBounds(text, g2, x + textAdj[0], y + textAdj[1], angle, x + textAdj[0] + rotateAdj[0], y + textAdj[1] + rotateAdj[1]);
        return result;
    }

    private static float[] deriveTextBoundsAnchorOffsets(Graphics2D g2, String text, TextAnchor anchor) {
        float[] result = new float[2];
        FontRenderContext frc = g2.getFontRenderContext();
        Font f = g2.getFont();
        FontMetrics fm = g2.getFontMetrics(f);
        Rectangle2D bounds = TextUtilities.getTextBounds(text, g2, fm);
        LineMetrics metrics = f.getLineMetrics(text, frc);
        float ascent = metrics.getAscent();
        float halfAscent = ascent / 2.0f;
        float descent = metrics.getDescent();
        float leading = metrics.getLeading();
        float xAdj = 0.0f;
        float yAdj = 0.0f;
        if (anchor == TextAnchor.TOP_CENTER || anchor == TextAnchor.CENTER || anchor == TextAnchor.BOTTOM_CENTER || anchor == TextAnchor.BASELINE_CENTER || anchor == TextAnchor.HALF_ASCENT_CENTER) {
            xAdj = (float)(-bounds.getWidth()) / 2.0f;
        } else if (anchor == TextAnchor.TOP_RIGHT || anchor == TextAnchor.CENTER_RIGHT || anchor == TextAnchor.BOTTOM_RIGHT || anchor == TextAnchor.BASELINE_RIGHT || anchor == TextAnchor.HALF_ASCENT_RIGHT) {
            xAdj = (float)(-bounds.getWidth());
        }
        if (anchor == TextAnchor.TOP_LEFT || anchor == TextAnchor.TOP_CENTER || anchor == TextAnchor.TOP_RIGHT) {
            yAdj = -descent - leading + (float)bounds.getHeight();
        } else if (anchor == TextAnchor.HALF_ASCENT_LEFT || anchor == TextAnchor.HALF_ASCENT_CENTER || anchor == TextAnchor.HALF_ASCENT_RIGHT) {
            yAdj = halfAscent;
        } else if (anchor == TextAnchor.CENTER_LEFT || anchor == TextAnchor.CENTER || anchor == TextAnchor.CENTER_RIGHT) {
            yAdj = -descent - leading + (float)(bounds.getHeight() / 2.0);
        } else if (anchor == TextAnchor.BASELINE_LEFT || anchor == TextAnchor.BASELINE_CENTER || anchor == TextAnchor.BASELINE_RIGHT) {
            yAdj = 0.0f;
        } else if (anchor == TextAnchor.BOTTOM_LEFT || anchor == TextAnchor.BOTTOM_CENTER || anchor == TextAnchor.BOTTOM_RIGHT) {
            yAdj = -metrics.getDescent() - metrics.getLeading();
        }
        result[0] = xAdj;
        result[1] = yAdj;
        return result;
    }

    private static float[] deriveRotationAnchorOffsets(Graphics2D g2, String text, TextAnchor anchor) {
        float[] result = new float[2];
        FontRenderContext frc = g2.getFontRenderContext();
        LineMetrics metrics = g2.getFont().getLineMetrics(text, frc);
        FontMetrics fm = g2.getFontMetrics();
        Rectangle2D bounds = TextUtilities.getTextBounds(text, g2, fm);
        float ascent = metrics.getAscent();
        float halfAscent = ascent / 2.0f;
        float descent = metrics.getDescent();
        float leading = metrics.getLeading();
        float xAdj = 0.0f;
        float yAdj = 0.0f;
        if (anchor == TextAnchor.TOP_LEFT || anchor == TextAnchor.CENTER_LEFT || anchor == TextAnchor.BOTTOM_LEFT || anchor == TextAnchor.BASELINE_LEFT || anchor == TextAnchor.HALF_ASCENT_LEFT) {
            xAdj = 0.0f;
        } else if (anchor == TextAnchor.TOP_CENTER || anchor == TextAnchor.CENTER || anchor == TextAnchor.BOTTOM_CENTER || anchor == TextAnchor.BASELINE_CENTER || anchor == TextAnchor.HALF_ASCENT_CENTER) {
            xAdj = (float)bounds.getWidth() / 2.0f;
        } else if (anchor == TextAnchor.TOP_RIGHT || anchor == TextAnchor.CENTER_RIGHT || anchor == TextAnchor.BOTTOM_RIGHT || anchor == TextAnchor.BASELINE_RIGHT || anchor == TextAnchor.HALF_ASCENT_RIGHT) {
            xAdj = (float)bounds.getWidth();
        }
        if (anchor == TextAnchor.TOP_LEFT || anchor == TextAnchor.TOP_CENTER || anchor == TextAnchor.TOP_RIGHT) {
            yAdj = descent + leading - (float)bounds.getHeight();
        } else if (anchor == TextAnchor.CENTER_LEFT || anchor == TextAnchor.CENTER || anchor == TextAnchor.CENTER_RIGHT) {
            yAdj = descent + leading - (float)(bounds.getHeight() / 2.0);
        } else if (anchor == TextAnchor.HALF_ASCENT_LEFT || anchor == TextAnchor.HALF_ASCENT_CENTER || anchor == TextAnchor.HALF_ASCENT_RIGHT) {
            yAdj = -halfAscent;
        } else if (anchor == TextAnchor.BASELINE_LEFT || anchor == TextAnchor.BASELINE_CENTER || anchor == TextAnchor.BASELINE_RIGHT) {
            yAdj = 0.0f;
        } else if (anchor == TextAnchor.BOTTOM_LEFT || anchor == TextAnchor.BOTTOM_CENTER || anchor == TextAnchor.BOTTOM_RIGHT) {
            yAdj = metrics.getDescent() + metrics.getLeading();
        }
        result[0] = xAdj;
        result[1] = yAdj;
        return result;
    }

    public static Shape calculateRotatedStringBounds(String text, Graphics2D g2, float textX, float textY, double angle, float rotateX, float rotateY) {
        if (text == null || text.equals("")) {
            return null;
        }
        FontMetrics fm = g2.getFontMetrics();
        Rectangle2D bounds = TextUtilities.getTextBounds(text, g2, fm);
        AffineTransform translate = AffineTransform.getTranslateInstance(textX, textY);
        Shape translatedBounds = translate.createTransformedShape(bounds);
        AffineTransform rotate = AffineTransform.getRotateInstance(angle, rotateX, rotateY);
        Shape result = rotate.createTransformedShape(translatedBounds);
        return result;
    }

    public static boolean getUseFontMetricsGetStringBounds() {
        return useFontMetricsGetStringBounds;
    }

    public static void setUseFontMetricsGetStringBounds(boolean use) {
        useFontMetricsGetStringBounds = use;
    }

    public static boolean isUseDrawRotatedStringWorkaround() {
        return useDrawRotatedStringWorkaround;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        try {
            boolean isJava14 = ObjectUtilities.isJDK14();
            String configRotatedStringWorkaround = BaseBoot.getInstance().getGlobalConfig().getConfigProperty("org.jfree.text.UseDrawRotatedStringWorkaround", "auto");
            useDrawRotatedStringWorkaround = configRotatedStringWorkaround.equals("auto") ? !isJava14 : configRotatedStringWorkaround.equals("true");
            String configFontMetricsStringBounds = BaseBoot.getInstance().getGlobalConfig().getConfigProperty("org.jfree.text.UseFontMetricsGetStringBounds", "auto");
            useFontMetricsGetStringBounds = configFontMetricsStringBounds.equals("auto") ? isJava14 : configFontMetricsStringBounds.equals("true");
        }
        catch (Exception e) {
            useDrawRotatedStringWorkaround = true;
            useFontMetricsGetStringBounds = true;
        }
    }
}

