/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.lowagie.text.pdf.parser;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.CMapAwareDocumentFont;
import com.lowagie.text.pdf.PRIndirectReference;
import com.lowagie.text.pdf.PRStream;
import com.lowagie.text.pdf.PRTokeniser;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfContentParser;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfLiteral;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.parser.ContentOperator;
import com.lowagie.text.pdf.parser.FinalText;
import com.lowagie.text.pdf.parser.GraphicsState;
import com.lowagie.text.pdf.parser.Matrix;
import com.lowagie.text.pdf.parser.ParsedText;
import com.lowagie.text.pdf.parser.TextAssembler;
import com.lowagie.text.pdf.parser.TextAssemblyBuffer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PdfContentStreamHandler {
    private Map<String, ContentOperator> operators;
    private Stack<GraphicsState> gsStack;
    private Matrix textMatrix;
    private Matrix textLineMatrix;
    private Stack<List<TextAssemblyBuffer>> textFragmentStreams = new Stack();
    private Stack<String> contextNames = new Stack();
    private List<TextAssemblyBuffer> textFragments = new ArrayList<TextAssemblyBuffer>();
    private TextAssembler renderListener;

    public PdfContentStreamHandler(TextAssembler renderListener) {
        this.renderListener = renderListener;
        this.installDefaultOperators();
        this.reset();
    }

    @Nonnull
    private static Matrix getMatrix(List<PdfObject> operands) {
        float a = ((PdfNumber)operands.get(0)).floatValue();
        float b = ((PdfNumber)operands.get(1)).floatValue();
        float c = ((PdfNumber)operands.get(2)).floatValue();
        float d = ((PdfNumber)operands.get(3)).floatValue();
        float e = ((PdfNumber)operands.get(4)).floatValue();
        float f = ((PdfNumber)operands.get(5)).floatValue();
        return new Matrix(a, b, c, d, e, f);
    }

    public void registerContentOperator(ContentOperator operator) {
        String operatorString = operator.getOperatorName();
        if (this.operators.containsKey(operatorString)) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("operator.1.already.registered", operatorString));
        }
        this.operators.put(operatorString, operator);
    }

    protected void installDefaultOperators() {
        this.operators = new HashMap<String, ContentOperator>();
        this.registerContentOperator(new PushGraphicsState());
        this.registerContentOperator(new PopGraphicsState());
        this.registerContentOperator(new ModifyCurrentTransformationMatrix());
        this.registerContentOperator(new ProcessGraphicsStateResource());
        SetTextCharacterSpacing tcOperator = new SetTextCharacterSpacing();
        this.registerContentOperator(tcOperator);
        SetTextWordSpacing twOperator = new SetTextWordSpacing();
        this.registerContentOperator(twOperator);
        this.registerContentOperator(new SetTextHorizontalScaling());
        SetTextLeading tlOperator = new SetTextLeading();
        this.registerContentOperator(tlOperator);
        this.registerContentOperator(new SetTextFont());
        this.registerContentOperator(new SetTextRenderMode());
        this.registerContentOperator(new SetTextRise());
        this.registerContentOperator(new BeginText());
        this.registerContentOperator(new EndText());
        TextMoveStartNextLine tdOperator = new TextMoveStartNextLine();
        this.registerContentOperator(tdOperator);
        this.registerContentOperator(new TextMoveStartNextLineWithLeading(tdOperator, tlOperator));
        this.registerContentOperator(new TextSetTextMatrix());
        TextMoveNextLine tstarOperator = new TextMoveNextLine(tdOperator);
        this.registerContentOperator(tstarOperator);
        ShowText tjOperator = new ShowText();
        this.registerContentOperator(new ShowText());
        MoveNextLineAndShowText tickOperator = new MoveNextLineAndShowText(tstarOperator, tjOperator);
        this.registerContentOperator(tickOperator);
        this.registerContentOperator(new MoveNextLineAndShowTextWithSpacing(twOperator, tcOperator, tickOperator));
        this.registerContentOperator(new ShowTextArray());
        this.registerContentOperator(new BeginMarked());
        this.registerContentOperator(new BeginMarkedDict());
        this.registerContentOperator(new EndMarked());
        this.registerContentOperator(new Do());
    }

    @Nonnull
    public Optional<ContentOperator> lookupOperator(String operatorName) {
        return Optional.ofNullable(this.operators.get(operatorName));
    }

    public void invokeOperator(PdfLiteral operator, List<PdfObject> operands, PdfDictionary resources) {
        String operatorName = operator.toString();
        this.lookupOperator(operatorName).ifPresent(contentOperator -> contentOperator.invoke(operands, this, resources));
    }

    void popContext() {
        String contextName = this.contextNames.pop();
        List<TextAssemblyBuffer> newBuffer = this.textFragmentStreams.pop();
        this.renderListener.reset();
        for (TextAssemblyBuffer fragment : this.textFragments) {
            fragment.accumulate(this.renderListener, contextName);
        }
        FinalText contextResult = this.renderListener.endParsingContext(contextName);
        Optional.ofNullable(contextResult).map(FinalText::getText).filter(text -> !text.isEmpty()).ifPresent(text -> newBuffer.add(contextResult));
        this.textFragments = newBuffer;
    }

    void pushContext(@Nullable String newContextName) {
        this.contextNames.push(newContextName);
        this.textFragmentStreams.push(this.textFragments);
        this.textFragments = new ArrayList<TextAssemblyBuffer>();
    }

    @Nonnull
    GraphicsState graphicsState() {
        return this.gsStack.peek();
    }

    public void reset() {
        if (this.gsStack == null || this.gsStack.isEmpty()) {
            this.gsStack = new Stack();
        }
        this.gsStack.add(new GraphicsState());
        this.textMatrix = null;
        this.textLineMatrix = null;
    }

    protected Matrix getCurrentTextMatrix() {
        return this.textMatrix;
    }

    protected Matrix getCurrentTextLineMatrix() {
        return this.textLineMatrix;
    }

    void applyTextAdjust(float tj) {
        float adjustBy = -tj / 1000.0f * this.graphicsState().getFontSize() * this.graphicsState().getHorizontalScaling();
        this.textMatrix = new Matrix(adjustBy, 0.0f).multiply(this.textMatrix);
    }

    public CMapAwareDocumentFont getCurrentFont() {
        return this.graphicsState().getFont();
    }

    void displayPdfString(PdfString string) {
        ParsedText renderInfo = new ParsedText(string, this.graphicsState(), this.textMatrix);
        if (this.contextNames.peek() != null) {
            this.textFragments.add(renderInfo);
        }
        this.textMatrix = new Matrix(renderInfo.getUnscaledTextWidth(this.graphicsState()), 0.0f).multiply(this.textMatrix);
    }

    @Nonnull
    public String getResultantText() {
        if (this.contextNames.size() > 0) {
            throw new RuntimeException("can't get text with unprocessed stack items");
        }
        StringBuilder res = new StringBuilder();
        for (TextAssemblyBuffer fragment : this.textFragments) {
            res.append(fragment.getText());
        }
        return res.toString();
    }

    private class Do
    implements ContentOperator {
        private Do() {
        }

        @Override
        @Nonnull
        public String getOperatorName() {
            return "Do";
        }

        @Override
        public void invoke(List<PdfObject> operands, PdfContentStreamHandler handler, PdfDictionary resources) {
            PdfObject firstOperand = operands.get(0);
            if (firstOperand instanceof PdfName) {
                PdfName name = (PdfName)firstOperand;
                PdfDictionary dictionary = resources.getAsDict(PdfName.XOBJECT);
                if (dictionary == null) {
                    return;
                }
                PdfStream stream = (PdfStream)dictionary.getDirectObject(name);
                PdfName subType = stream.getAsName(PdfName.SUBTYPE);
                if (PdfName.FORM.equals(subType)) {
                    byte[] data;
                    PdfDictionary resources2 = stream.getAsDict(PdfName.RESOURCES);
                    try {
                        data = this.getContentBytesFromPdfObject(stream);
                    }
                    catch (IOException ex) {
                        throw new ExceptionConverter(ex);
                    }
                    new PushGraphicsState().invoke(operands, handler, resources);
                    this.processContent(data, resources2);
                    new PopGraphicsState().invoke(operands, handler, resources);
                }
            }
        }

        private void processContent(byte[] contentBytes, PdfDictionary resources) {
            try {
                PdfContentParser pdfContentParser = new PdfContentParser(new PRTokeniser(contentBytes));
                ArrayList<PdfObject> operands = new ArrayList<PdfObject>();
                while (!pdfContentParser.parse(operands).isEmpty()) {
                    PdfLiteral operator = (PdfLiteral)operands.get(operands.size() - 1);
                    PdfContentStreamHandler.this.invokeOperator(operator, operands, resources);
                }
            }
            catch (Exception e) {
                throw new ExceptionConverter(e);
            }
        }

        private byte[] getContentBytesFromPdfObject(PdfObject object) throws IOException {
            switch (object.type()) {
                case 10: {
                    return this.getContentBytesFromPdfObject(PdfReader.getPdfObject(object));
                }
                case 7: {
                    return PdfReader.getStreamBytes((PRStream)PdfReader.getPdfObject(object));
                }
                case 5: {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    for (PdfObject element : ((PdfArray)object).getElements()) {
                        baos.write(this.getContentBytesFromPdfObject(element));
                    }
                    return baos.toByteArray();
                }
            }
            throw new IllegalStateException("Unsupported type: " + object.getClass().getCanonicalName());
        }
    }

    private static class EndMarked
    implements ContentOperator {
        private EndMarked() {
        }

        @Override
        @Nonnull
        public String getOperatorName() {
            return "EMC";
        }

        @Override
        public void invoke(List<PdfObject> operands, PdfContentStreamHandler handler, PdfDictionary resources) {
            handler.popContext();
        }
    }

    private static class BeginMarkedDict
    implements ContentOperator {
        private BeginMarkedDict() {
        }

        @Override
        @Nonnull
        public String getOperatorName() {
            return "BDC";
        }

        @Override
        public void invoke(List<PdfObject> operands, PdfContentStreamHandler handler, PdfDictionary resources) {
            PdfObject firstOperand = operands.get(0);
            String tagName = firstOperand.toString().substring(1).toLowerCase(Locale.ROOT);
            if ("artifact".equals(tagName) || "placedpdf".equals(tagName) || handler.contextNames.peek() == null) {
                tagName = null;
            } else if ("l".equals(tagName)) {
                tagName = "ul";
            }
            PdfDictionary attrs = this.getBDCDictionary(operands, resources);
            if (attrs != null && tagName != null) {
                PdfString alternateText = attrs.getAsString(PdfName.E);
                if (alternateText != null) {
                    handler.pushContext(tagName);
                    handler.textFragments.add(new FinalText(alternateText.toString()));
                    handler.popContext();
                    handler.pushContext(null);
                    return;
                }
                if (attrs.get(PdfName.TYPE) != null) {
                    tagName = "";
                }
            }
            handler.pushContext(tagName);
        }

        private PdfDictionary getBDCDictionary(List<PdfObject> operands, PdfDictionary resources) {
            PdfObject pdfObject = operands.get(1);
            if (pdfObject.isName()) {
                PdfDictionary properties = resources.getAsDict(PdfName.PROPERTIES);
                PdfIndirectReference ir = properties.getAsIndirectObject((PdfName)pdfObject);
                pdfObject = ir != null ? ir.getIndRef() : properties.getAsDict((PdfName)pdfObject);
            }
            return (PdfDictionary)pdfObject;
        }
    }

    private static class BeginMarked
    implements ContentOperator {
        private BeginMarked() {
        }

        @Override
        @Nonnull
        public String getOperatorName() {
            return "BMC";
        }

        @Override
        public void invoke(List<PdfObject> operands, PdfContentStreamHandler handler, PdfDictionary resources) {
            PdfName tagName = (PdfName)operands.get(0);
            String realName = tagName.toString().substring(1).toLowerCase(Locale.ROOT);
            if ("artifact".equals(realName) || "placedpdf".equals(realName)) {
                handler.pushContext(null);
            } else {
                handler.pushContext(realName);
            }
        }
    }

    static class SetTextWordSpacing
    implements ContentOperator {
        SetTextWordSpacing() {
        }

        @Override
        @Nonnull
        public String getOperatorName() {
            return "Tw";
        }

        @Override
        public void invoke(List<PdfObject> operands, PdfContentStreamHandler handler, PdfDictionary resources) {
            PdfNumber wordSpace = (PdfNumber)operands.get(0);
            handler.graphicsState().setWordSpacing(wordSpace.floatValue());
        }
    }

    static class SetTextHorizontalScaling
    implements ContentOperator {
        SetTextHorizontalScaling() {
        }

        @Override
        @Nonnull
        public String getOperatorName() {
            return "Tz";
        }

        @Override
        public void invoke(List<PdfObject> operands, PdfContentStreamHandler handler, PdfDictionary resources) {
            PdfNumber scale = (PdfNumber)operands.get(0);
            handler.graphicsState().setHorizontalScaling(scale.floatValue());
        }
    }

    static class SetTextLeading
    implements ContentOperator {
        SetTextLeading() {
        }

        @Override
        @Nonnull
        public String getOperatorName() {
            return "TL";
        }

        @Override
        public void invoke(List<PdfObject> operands, PdfContentStreamHandler handler, PdfDictionary resources) {
            PdfNumber leading = (PdfNumber)operands.get(0);
            handler.graphicsState().setLeading(leading.floatValue());
        }
    }

    static class SetTextRise
    implements ContentOperator {
        SetTextRise() {
        }

        @Override
        @Nonnull
        public String getOperatorName() {
            return "Ts";
        }

        @Override
        public void invoke(List<PdfObject> operands, PdfContentStreamHandler handler, PdfDictionary resources) {
            PdfNumber rise = (PdfNumber)operands.get(0);
            handler.graphicsState().setRise(rise.floatValue());
        }
    }

    static class SetTextRenderMode
    implements ContentOperator {
        SetTextRenderMode() {
        }

        @Override
        @Nonnull
        public String getOperatorName() {
            return "Tr";
        }

        @Override
        public void invoke(List<PdfObject> operands, PdfContentStreamHandler handler, PdfDictionary resources) {
            PdfNumber render = (PdfNumber)operands.get(0);
            handler.graphicsState().setRenderMode(render.intValue());
        }
    }

    static class TextMoveStartNextLine
    implements ContentOperator {
        TextMoveStartNextLine() {
        }

        @Override
        @Nonnull
        public String getOperatorName() {
            return "Td";
        }

        @Override
        public void invoke(List<PdfObject> operands, PdfContentStreamHandler handler, PdfDictionary resources) {
            float tx = ((PdfNumber)operands.get(0)).floatValue();
            float ty = ((PdfNumber)operands.get(1)).floatValue();
            Matrix translationMatrix = new Matrix(tx, ty);
            handler.textMatrix = translationMatrix.multiply(handler.textLineMatrix);
            handler.textLineMatrix = handler.textMatrix;
        }
    }

    static class TextMoveNextLine
    implements ContentOperator {
        private final TextMoveStartNextLine moveStartNextLine;

        public TextMoveNextLine(TextMoveStartNextLine moveStartNextLine) {
            this.moveStartNextLine = moveStartNextLine;
        }

        @Override
        @Nonnull
        public String getOperatorName() {
            return "T*";
        }

        @Override
        public void invoke(List<PdfObject> operands, PdfContentStreamHandler handler, PdfDictionary resources) {
            ArrayList<PdfObject> tdoperands = new ArrayList<PdfObject>(2);
            tdoperands.add(0, new PdfNumber(0));
            tdoperands.add(1, new PdfNumber(-handler.graphicsState().getLeading()));
            this.moveStartNextLine.invoke(tdoperands, handler, resources);
        }
    }

    static class ShowText
    implements ContentOperator {
        ShowText() {
        }

        @Override
        @Nonnull
        public String getOperatorName() {
            return "Tj";
        }

        @Override
        public void invoke(List<PdfObject> operands, PdfContentStreamHandler handler, PdfDictionary resources) {
            PdfString string = (PdfString)operands.get(0);
            handler.displayPdfString(string);
        }
    }

    static class TextMoveStartNextLineWithLeading
    implements ContentOperator {
        private final TextMoveStartNextLine moveStartNextLine;
        private final SetTextLeading setTextLeading;

        public TextMoveStartNextLineWithLeading(TextMoveStartNextLine moveStartNextLine, SetTextLeading setTextLeading) {
            this.moveStartNextLine = moveStartNextLine;
            this.setTextLeading = setTextLeading;
        }

        @Override
        @Nonnull
        public String getOperatorName() {
            return "TD";
        }

        @Override
        public void invoke(List<PdfObject> operands, PdfContentStreamHandler handler, PdfDictionary resources) {
            float ty = ((PdfNumber)operands.get(1)).floatValue();
            ArrayList<PdfObject> tlOperands = new ArrayList<PdfObject>(1);
            tlOperands.add(0, new PdfNumber(-ty));
            this.setTextLeading.invoke(tlOperands, handler, resources);
            this.moveStartNextLine.invoke(operands, handler, resources);
        }
    }

    static class TextSetTextMatrix
    implements ContentOperator {
        TextSetTextMatrix() {
        }

        @Override
        @Nonnull
        public String getOperatorName() {
            return "Tm";
        }

        @Override
        public void invoke(List<PdfObject> operands, PdfContentStreamHandler handler, PdfDictionary resources) {
            handler.textLineMatrix = PdfContentStreamHandler.getMatrix(operands);
            handler.textMatrix = handler.textLineMatrix;
        }
    }

    static class SetTextFont
    implements ContentOperator {
        SetTextFont() {
        }

        @Override
        @Nonnull
        public String getOperatorName() {
            return "Tf";
        }

        @Override
        public void invoke(List<PdfObject> operands, PdfContentStreamHandler handler, PdfDictionary resources) {
            PdfName fontResourceName = (PdfName)operands.get(0);
            float size = ((PdfNumber)operands.get(1)).floatValue();
            PdfDictionary fontsDictionary = resources.getAsDict(PdfName.FONT);
            PdfObject pdfObject = fontsDictionary.get(fontResourceName);
            CMapAwareDocumentFont font = new CMapAwareDocumentFont((PRIndirectReference)pdfObject);
            handler.graphicsState().setFont(font);
            handler.graphicsState().setFontSize(size);
        }
    }

    static class SetTextCharacterSpacing
    implements ContentOperator {
        SetTextCharacterSpacing() {
        }

        @Override
        @Nonnull
        public String getOperatorName() {
            return "Tc";
        }

        @Override
        public void invoke(List<PdfObject> operands, PdfContentStreamHandler handler, PdfDictionary resources) {
            PdfNumber charSpace = (PdfNumber)operands.get(0);
            handler.graphicsState().setCharacterSpacing(charSpace.floatValue());
        }
    }

    static class PushGraphicsState
    implements ContentOperator {
        PushGraphicsState() {
        }

        @Override
        @Nonnull
        public String getOperatorName() {
            return "q";
        }

        @Override
        public void invoke(List<PdfObject> operands, PdfContentStreamHandler handler, PdfDictionary resources) {
            GraphicsState gs = (GraphicsState)handler.gsStack.peek();
            GraphicsState copy = new GraphicsState(gs);
            handler.gsStack.push(copy);
        }
    }

    static class ProcessGraphicsStateResource
    implements ContentOperator {
        ProcessGraphicsStateResource() {
        }

        @Override
        @Nonnull
        public String getOperatorName() {
            return "gs";
        }

        @Override
        public void invoke(List<PdfObject> operands, PdfContentStreamHandler handler, PdfDictionary resources) {
            PdfName dictionaryName = (PdfName)operands.get(0);
            PdfDictionary extGState = resources.getAsDict(PdfName.EXTGSTATE);
            if (extGState == null) {
                throw new IllegalArgumentException(MessageLocalization.getComposedMessage("resources.do.not.contain.extgstate.entry.unable.to.process.operator.1", this.getOperatorName()));
            }
            PdfDictionary gsDic = extGState.getAsDict(dictionaryName);
            if (gsDic == null) {
                throw new IllegalArgumentException(MessageLocalization.getComposedMessage("1.is.an.unknown.graphics.state.dictionary", dictionaryName));
            }
            PdfArray fontParameter = gsDic.getAsArray(PdfName.FONT);
            if (fontParameter != null) {
                PdfObject pdfObject = fontParameter.getPdfObject(0);
                CMapAwareDocumentFont font = new CMapAwareDocumentFont((PRIndirectReference)pdfObject);
                float size = fontParameter.getAsNumber(1).floatValue();
                handler.graphicsState().setFont(font);
                handler.graphicsState().setFontSize(size);
            }
        }
    }

    static class PopGraphicsState
    implements ContentOperator {
        PopGraphicsState() {
        }

        @Override
        @Nonnull
        public String getOperatorName() {
            return "Q";
        }

        @Override
        public void invoke(List<PdfObject> operands, PdfContentStreamHandler handler, PdfDictionary resources) {
            handler.gsStack.pop();
        }
    }

    static class MoveNextLineAndShowTextWithSpacing
    implements ContentOperator {
        private final SetTextWordSpacing setTextWordSpacing;
        private final SetTextCharacterSpacing setTextCharacterSpacing;
        private final MoveNextLineAndShowText moveNextLineAndShowText;

        public MoveNextLineAndShowTextWithSpacing(SetTextWordSpacing setTextWordSpacing, SetTextCharacterSpacing setTextCharacterSpacing, MoveNextLineAndShowText moveNextLineAndShowText) {
            this.setTextWordSpacing = setTextWordSpacing;
            this.setTextCharacterSpacing = setTextCharacterSpacing;
            this.moveNextLineAndShowText = moveNextLineAndShowText;
        }

        @Override
        @Nonnull
        public String getOperatorName() {
            return "\"";
        }

        @Override
        public void invoke(List<PdfObject> operands, PdfContentStreamHandler handler, PdfDictionary resources) {
            PdfNumber aw = (PdfNumber)operands.get(0);
            PdfNumber ac = (PdfNumber)operands.get(1);
            PdfString string = (PdfString)operands.get(2);
            ArrayList<PdfObject> twOperands = new ArrayList<PdfObject>(1);
            twOperands.add(0, aw);
            this.setTextWordSpacing.invoke(twOperands, handler, resources);
            ArrayList<PdfObject> tcOperands = new ArrayList<PdfObject>(1);
            tcOperands.add(0, ac);
            this.setTextCharacterSpacing.invoke(tcOperands, handler, resources);
            ArrayList<PdfObject> tickOperands = new ArrayList<PdfObject>(1);
            tickOperands.add(0, string);
            this.moveNextLineAndShowText.invoke(tickOperands, handler, resources);
        }
    }

    static class MoveNextLineAndShowText
    implements ContentOperator {
        private final TextMoveNextLine textMoveNextLine;
        private final ShowText showText;

        public MoveNextLineAndShowText(TextMoveNextLine textMoveNextLine, ShowText showText) {
            this.textMoveNextLine = textMoveNextLine;
            this.showText = showText;
        }

        @Override
        @Nonnull
        public String getOperatorName() {
            return "'";
        }

        @Override
        public void invoke(List<PdfObject> operands, PdfContentStreamHandler handler, PdfDictionary resources) {
            this.textMoveNextLine.invoke(new ArrayList<PdfObject>(0), handler, resources);
            this.showText.invoke(operands, handler, resources);
        }
    }

    static class ModifyCurrentTransformationMatrix
    implements ContentOperator {
        ModifyCurrentTransformationMatrix() {
        }

        @Override
        @Nonnull
        public String getOperatorName() {
            return "cm";
        }

        @Override
        public void invoke(List<PdfObject> operands, PdfContentStreamHandler handler, PdfDictionary resources) {
            Matrix matrix = PdfContentStreamHandler.getMatrix(operands);
            GraphicsState graphicsState = (GraphicsState)handler.gsStack.peek();
            graphicsState.multiplyCtm(matrix);
        }
    }

    static class EndText
    implements ContentOperator {
        EndText() {
        }

        @Override
        @Nonnull
        public String getOperatorName() {
            return "ET";
        }

        @Override
        public void invoke(List<PdfObject> operands, PdfContentStreamHandler handler, PdfDictionary resources) {
            handler.textMatrix = null;
            handler.textLineMatrix = null;
        }
    }

    static class BeginText
    implements ContentOperator {
        BeginText() {
        }

        @Override
        @Nonnull
        public String getOperatorName() {
            return "BT";
        }

        @Override
        public void invoke(List<PdfObject> operands, PdfContentStreamHandler handler, PdfDictionary resources) {
            handler.textMatrix = new Matrix();
            handler.textLineMatrix = handler.textMatrix;
        }
    }

    static class ShowTextArray
    implements ContentOperator {
        ShowTextArray() {
        }

        @Override
        @Nonnull
        public String getOperatorName() {
            return "TJ";
        }

        @Override
        public void invoke(List<PdfObject> operands, PdfContentStreamHandler handler, PdfDictionary resources) {
            PdfArray array = (PdfArray)operands.get(0);
            for (PdfObject entryObj : array.getElements()) {
                if (entryObj instanceof PdfString) {
                    handler.displayPdfString((PdfString)entryObj);
                    continue;
                }
                float tj = ((PdfNumber)entryObj).floatValue();
                handler.applyTextAdjust(tj);
            }
        }
    }
}

