/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.context;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.extend.ContentFunction;
import org.xhtmlrenderer.css.parser.FSFunction;
import org.xhtmlrenderer.css.parser.PropertyValue;
import org.xhtmlrenderer.css.style.CssContext;
import org.xhtmlrenderer.layout.CounterFunction;
import org.xhtmlrenderer.layout.InlineBoxing;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.Box;
import org.xhtmlrenderer.render.InlineLayoutBox;
import org.xhtmlrenderer.render.InlineText;
import org.xhtmlrenderer.render.LineBox;
import org.xhtmlrenderer.render.RenderingContext;

public class ContentFunctionFactory {
    private List _functions = new ArrayList();

    public ContentFunctionFactory() {
        this._functions.add(new PageCounterFunction());
        this._functions.add(new PagesCounterFunction());
        this._functions.add(new TargetCounterFunction());
        this._functions.add(new LeaderFunction());
    }

    public ContentFunction lookupFunction(LayoutContext c, FSFunction function) {
        for (ContentFunction f : this._functions) {
            if (!f.canHandle(c, function)) continue;
            return f;
        }
        return null;
    }

    public void registerFunction(ContentFunction function) {
        this._functions.add(function);
    }

    private static class LeaderFunction
    implements ContentFunction {
        private LeaderFunction() {
        }

        @Override
        public boolean isStatic() {
            return false;
        }

        @Override
        public String calculate(RenderingContext c, FSFunction function, InlineText text) {
            InlineLayoutBox iB = text.getParent();
            LineBox lineBox = iB.getLineBox();
            boolean dynamic = false;
            Iterator childIterator = lineBox.getChildIterator();
            while (childIterator.hasNext()) {
                Box child = (Box)childIterator.next();
                if (child == iB) {
                    dynamic = true;
                    continue;
                }
                if (!dynamic || !(child instanceof InlineLayoutBox)) continue;
                ((InlineLayoutBox)child).lookForDynamicFunctions(c);
            }
            if (dynamic) {
                int totalLineWidth = InlineBoxing.positionHorizontally((CssContext)c, lineBox, 0);
                lineBox.setContentWidth(totalLineWidth);
            }
            PropertyValue param = (PropertyValue)function.getParameters().get(0);
            String value = param.getStringValue();
            if (param.getPrimitiveType() == 21) {
                if (value.equals("dotted")) {
                    value = ". ";
                } else if (value.equals("solid")) {
                    value = "_";
                } else if (value.equals("space")) {
                    value = " ";
                }
            }
            StringBuffer tmp = new StringBuffer(100 * value.length());
            for (int i = 0; i < 100; ++i) {
                tmp.append(value);
            }
            float valueWidth = (float)c.getTextRenderer().getWidth(c.getFontContext(), iB.getStyle().getFSFont(c), tmp.toString()) / 100.0f;
            int spaceWidth = c.getTextRenderer().getWidth(c.getFontContext(), iB.getStyle().getFSFont(c), " ");
            int leaderWidth = iB.getContainingBlockWidth() - iB.getLineBox().getWidth() + text.getWidth();
            int count = (int)((float)(leaderWidth - 2 * spaceWidth) / valueWidth);
            StringBuffer buf = new StringBuffer(count * value.length() + 2);
            buf.append(' ');
            for (int i = 0; i < count; ++i) {
                buf.append(value);
            }
            buf.append(' ');
            String leaderString = buf.toString();
            int leaderStringWidth = c.getTextRenderer().getWidth(c.getFontContext(), iB.getStyle().getFSFont(c), leaderString);
            iB.setMarginLeft(c, leaderWidth - leaderStringWidth);
            return leaderString;
        }

        @Override
        public String calculate(LayoutContext c, FSFunction function) {
            return null;
        }

        @Override
        public String getLayoutReplacementText() {
            return " . ";
        }

        @Override
        public boolean canHandle(LayoutContext c, FSFunction function) {
            List parameters;
            if (c.isPrint() && function.getName().equals("leader") && (parameters = function.getParameters()).size() == 1) {
                PropertyValue param = (PropertyValue)parameters.get(0);
                return param.getPrimitiveType() == 19 || param.getPrimitiveType() == 21 && (param.getStringValue().equals("dotted") || param.getStringValue().equals("solid") || param.getStringValue().equals("space"));
            }
            return false;
        }
    }

    private static class TargetCounterFunction
    implements ContentFunction {
        private TargetCounterFunction() {
        }

        @Override
        public boolean isStatic() {
            return false;
        }

        @Override
        public String calculate(RenderingContext c, FSFunction function, InlineText text) {
            String anchor;
            Box target;
            String uri = text.getParent().getElement().getAttribute("href");
            if (uri != null && uri.startsWith("#") && (target = c.getBoxById(anchor = uri.substring(1))) != null) {
                int pageNo = c.getRootLayer().getRelativePageNo(c, target.getAbsY());
                return CounterFunction.createCounterText(IdentValue.DECIMAL, pageNo + 1);
            }
            return "";
        }

        @Override
        public String calculate(LayoutContext c, FSFunction function) {
            return null;
        }

        @Override
        public String getLayoutReplacementText() {
            return "999";
        }

        @Override
        public boolean canHandle(LayoutContext c, FSFunction function) {
            List parameters;
            if (c.isPrint() && function.getName().equals("target-counter") && ((parameters = function.getParameters()).size() == 2 || parameters.size() == 3)) {
                FSFunction f = ((PropertyValue)parameters.get(0)).getFunction();
                if (f == null || f.getParameters().size() != 1 || ((PropertyValue)f.getParameters().get(0)).getPrimitiveType() != 21 || !((PropertyValue)f.getParameters().get(0)).getStringValue().equals("href")) {
                    return false;
                }
                PropertyValue param = (PropertyValue)parameters.get(1);
                return param.getPrimitiveType() == 21 && param.getStringValue().equals("page");
            }
            return false;
        }
    }

    private static class PagesCounterFunction
    extends PageNumberFunction
    implements ContentFunction {
        private PagesCounterFunction() {
        }

        @Override
        public String calculate(RenderingContext c, FSFunction function, InlineText text) {
            int value = c.getRootLayer().getRelativePageCount(c);
            return CounterFunction.createCounterText(this.getListStyleType(function), value);
        }

        @Override
        public boolean canHandle(LayoutContext c, FSFunction function) {
            return c.isPrint() && this.isCounter(function, "pages");
        }
    }

    private static class PageCounterFunction
    extends PageNumberFunction
    implements ContentFunction {
        private PageCounterFunction() {
        }

        @Override
        public String calculate(RenderingContext c, FSFunction function, InlineText text) {
            int value = c.getRootLayer().getRelativePageNo(c) + 1;
            return CounterFunction.createCounterText(this.getListStyleType(function), value);
        }

        @Override
        public boolean canHandle(LayoutContext c, FSFunction function) {
            return c.isPrint() && this.isCounter(function, "page");
        }
    }

    private static abstract class PageNumberFunction
    implements ContentFunction {
        private PageNumberFunction() {
        }

        @Override
        public boolean isStatic() {
            return false;
        }

        @Override
        public String calculate(LayoutContext c, FSFunction function) {
            return null;
        }

        @Override
        public String getLayoutReplacementText() {
            return "999";
        }

        protected IdentValue getListStyleType(FSFunction function) {
            PropertyValue pValue;
            IdentValue iValue;
            IdentValue result = IdentValue.DECIMAL;
            List parameters = function.getParameters();
            if (parameters.size() == 2 && (iValue = IdentValue.valueOf((pValue = (PropertyValue)parameters.get(1)).getStringValue())) != null) {
                result = iValue;
            }
            return result;
        }

        protected boolean isCounter(FSFunction function, String counterName) {
            List parameters;
            if (function.getName().equals("counter") && ((parameters = function.getParameters()).size() == 1 || parameters.size() == 2)) {
                PropertyValue param = (PropertyValue)parameters.get(0);
                if (param.getPrimitiveType() != 21 || !param.getStringValue().equals(counterName)) {
                    return false;
                }
                return parameters.size() != 2 || (param = (PropertyValue)parameters.get(1)).getPrimitiveType() == 21;
            }
            return false;
        }
    }
}

