/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.contentstream;

import java.awt.geom.Point2D;
import java.io.IOException;
import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.color.SetNonStrokingColor;
import org.apache.pdfbox.contentstream.operator.color.SetNonStrokingColorN;
import org.apache.pdfbox.contentstream.operator.color.SetNonStrokingColorSpace;
import org.apache.pdfbox.contentstream.operator.color.SetNonStrokingDeviceCMYKColor;
import org.apache.pdfbox.contentstream.operator.color.SetNonStrokingDeviceGrayColor;
import org.apache.pdfbox.contentstream.operator.color.SetNonStrokingDeviceRGBColor;
import org.apache.pdfbox.contentstream.operator.color.SetStrokingColor;
import org.apache.pdfbox.contentstream.operator.color.SetStrokingColorN;
import org.apache.pdfbox.contentstream.operator.color.SetStrokingColorSpace;
import org.apache.pdfbox.contentstream.operator.color.SetStrokingDeviceCMYKColor;
import org.apache.pdfbox.contentstream.operator.color.SetStrokingDeviceGrayColor;
import org.apache.pdfbox.contentstream.operator.color.SetStrokingDeviceRGBColor;
import org.apache.pdfbox.contentstream.operator.graphics.AppendRectangleToPath;
import org.apache.pdfbox.contentstream.operator.graphics.BeginInlineImage;
import org.apache.pdfbox.contentstream.operator.graphics.ClipEvenOddRule;
import org.apache.pdfbox.contentstream.operator.graphics.ClipNonZeroRule;
import org.apache.pdfbox.contentstream.operator.graphics.CloseAndStrokePath;
import org.apache.pdfbox.contentstream.operator.graphics.CloseFillEvenOddAndStrokePath;
import org.apache.pdfbox.contentstream.operator.graphics.CloseFillNonZeroAndStrokePath;
import org.apache.pdfbox.contentstream.operator.graphics.ClosePath;
import org.apache.pdfbox.contentstream.operator.graphics.CurveTo;
import org.apache.pdfbox.contentstream.operator.graphics.CurveToReplicateFinalPoint;
import org.apache.pdfbox.contentstream.operator.graphics.CurveToReplicateInitialPoint;
import org.apache.pdfbox.contentstream.operator.graphics.DrawObject;
import org.apache.pdfbox.contentstream.operator.graphics.EndPath;
import org.apache.pdfbox.contentstream.operator.graphics.FillEvenOddAndStrokePath;
import org.apache.pdfbox.contentstream.operator.graphics.FillEvenOddRule;
import org.apache.pdfbox.contentstream.operator.graphics.FillNonZeroAndStrokePath;
import org.apache.pdfbox.contentstream.operator.graphics.FillNonZeroRule;
import org.apache.pdfbox.contentstream.operator.graphics.LegacyFillNonZeroRule;
import org.apache.pdfbox.contentstream.operator.graphics.LineTo;
import org.apache.pdfbox.contentstream.operator.graphics.MoveTo;
import org.apache.pdfbox.contentstream.operator.graphics.ShadingFill;
import org.apache.pdfbox.contentstream.operator.graphics.StrokePath;
import org.apache.pdfbox.contentstream.operator.markedcontent.BeginMarkedContentSequence;
import org.apache.pdfbox.contentstream.operator.markedcontent.BeginMarkedContentSequenceWithProperties;
import org.apache.pdfbox.contentstream.operator.markedcontent.EndMarkedContentSequence;
import org.apache.pdfbox.contentstream.operator.state.Concatenate;
import org.apache.pdfbox.contentstream.operator.state.Restore;
import org.apache.pdfbox.contentstream.operator.state.Save;
import org.apache.pdfbox.contentstream.operator.state.SetFlatness;
import org.apache.pdfbox.contentstream.operator.state.SetGraphicsStateParameters;
import org.apache.pdfbox.contentstream.operator.state.SetLineCapStyle;
import org.apache.pdfbox.contentstream.operator.state.SetLineDashPattern;
import org.apache.pdfbox.contentstream.operator.state.SetLineJoinStyle;
import org.apache.pdfbox.contentstream.operator.state.SetLineMiterLimit;
import org.apache.pdfbox.contentstream.operator.state.SetLineWidth;
import org.apache.pdfbox.contentstream.operator.state.SetMatrix;
import org.apache.pdfbox.contentstream.operator.state.SetRenderingIntent;
import org.apache.pdfbox.contentstream.operator.text.BeginText;
import org.apache.pdfbox.contentstream.operator.text.EndText;
import org.apache.pdfbox.contentstream.operator.text.MoveText;
import org.apache.pdfbox.contentstream.operator.text.MoveTextSetLeading;
import org.apache.pdfbox.contentstream.operator.text.NextLine;
import org.apache.pdfbox.contentstream.operator.text.SetCharSpacing;
import org.apache.pdfbox.contentstream.operator.text.SetFontAndSize;
import org.apache.pdfbox.contentstream.operator.text.SetTextHorizontalScaling;
import org.apache.pdfbox.contentstream.operator.text.SetTextLeading;
import org.apache.pdfbox.contentstream.operator.text.SetTextRenderingMode;
import org.apache.pdfbox.contentstream.operator.text.SetTextRise;
import org.apache.pdfbox.contentstream.operator.text.SetWordSpacing;
import org.apache.pdfbox.contentstream.operator.text.ShowText;
import org.apache.pdfbox.contentstream.operator.text.ShowTextAdjusted;
import org.apache.pdfbox.contentstream.operator.text.ShowTextLine;
import org.apache.pdfbox.contentstream.operator.text.ShowTextLineAndSpace;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;

public abstract class PDFGraphicsStreamEngine
extends PDFStreamEngine {
    private final PDPage page;

    protected PDFGraphicsStreamEngine(PDPage page) {
        this.page = page;
        this.addOperator(new CloseFillNonZeroAndStrokePath());
        this.addOperator(new FillNonZeroAndStrokePath());
        this.addOperator(new CloseFillEvenOddAndStrokePath());
        this.addOperator(new FillEvenOddAndStrokePath());
        this.addOperator(new BeginInlineImage());
        this.addOperator(new BeginText());
        this.addOperator(new CurveTo());
        this.addOperator(new Concatenate());
        this.addOperator(new SetStrokingColorSpace());
        this.addOperator(new SetNonStrokingColorSpace());
        this.addOperator(new SetLineDashPattern());
        this.addOperator(new DrawObject());
        this.addOperator(new EndText());
        this.addOperator(new FillNonZeroRule());
        this.addOperator(new LegacyFillNonZeroRule());
        this.addOperator(new FillEvenOddRule());
        this.addOperator(new SetStrokingDeviceGrayColor());
        this.addOperator(new SetNonStrokingDeviceGrayColor());
        this.addOperator(new SetGraphicsStateParameters());
        this.addOperator(new ClosePath());
        this.addOperator(new SetFlatness());
        this.addOperator(new SetLineJoinStyle());
        this.addOperator(new SetLineCapStyle());
        this.addOperator(new SetStrokingDeviceCMYKColor());
        this.addOperator(new SetNonStrokingDeviceCMYKColor());
        this.addOperator(new LineTo());
        this.addOperator(new MoveTo());
        this.addOperator(new SetLineMiterLimit());
        this.addOperator(new EndPath());
        this.addOperator(new Save());
        this.addOperator(new Restore());
        this.addOperator(new AppendRectangleToPath());
        this.addOperator(new SetStrokingDeviceRGBColor());
        this.addOperator(new SetNonStrokingDeviceRGBColor());
        this.addOperator(new SetRenderingIntent());
        this.addOperator(new CloseAndStrokePath());
        this.addOperator(new StrokePath());
        this.addOperator(new SetStrokingColor());
        this.addOperator(new SetNonStrokingColor());
        this.addOperator(new SetStrokingColorN());
        this.addOperator(new SetNonStrokingColorN());
        this.addOperator(new ShadingFill());
        this.addOperator(new NextLine());
        this.addOperator(new SetCharSpacing());
        this.addOperator(new MoveText());
        this.addOperator(new MoveTextSetLeading());
        this.addOperator(new SetFontAndSize());
        this.addOperator(new ShowText());
        this.addOperator(new ShowTextAdjusted());
        this.addOperator(new SetTextLeading());
        this.addOperator(new SetMatrix());
        this.addOperator(new SetTextRenderingMode());
        this.addOperator(new SetTextRise());
        this.addOperator(new SetWordSpacing());
        this.addOperator(new SetTextHorizontalScaling());
        this.addOperator(new CurveToReplicateInitialPoint());
        this.addOperator(new SetLineWidth());
        this.addOperator(new ClipNonZeroRule());
        this.addOperator(new ClipEvenOddRule());
        this.addOperator(new CurveToReplicateFinalPoint());
        this.addOperator(new ShowTextLine());
        this.addOperator(new ShowTextLineAndSpace());
        this.addOperator(new BeginMarkedContentSequence());
        this.addOperator(new BeginMarkedContentSequenceWithProperties());
        this.addOperator(new EndMarkedContentSequence());
    }

    protected final PDPage getPage() {
        return this.page;
    }

    public abstract void appendRectangle(Point2D var1, Point2D var2, Point2D var3, Point2D var4) throws IOException;

    public abstract void drawImage(PDImage var1) throws IOException;

    public abstract void clip(int var1) throws IOException;

    public abstract void moveTo(float var1, float var2) throws IOException;

    public abstract void lineTo(float var1, float var2) throws IOException;

    public abstract void curveTo(float var1, float var2, float var3, float var4, float var5, float var6) throws IOException;

    public abstract Point2D getCurrentPoint() throws IOException;

    public abstract void closePath() throws IOException;

    public abstract void endPath() throws IOException;

    public abstract void strokePath() throws IOException;

    public abstract void fillPath(int var1) throws IOException;

    public abstract void fillAndStrokePath(int var1) throws IOException;

    public abstract void shadingFill(COSName var1) throws IOException;
}

