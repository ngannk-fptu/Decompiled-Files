/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.layout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.constants.MarginBoxName;
import org.xhtmlrenderer.css.constants.PageElementPosition;
import org.xhtmlrenderer.css.extend.ContentFunction;
import org.xhtmlrenderer.css.newmatch.CascadedStyle;
import org.xhtmlrenderer.css.newmatch.PageInfo;
import org.xhtmlrenderer.css.parser.FSFunction;
import org.xhtmlrenderer.css.parser.PropertyValue;
import org.xhtmlrenderer.css.sheet.PropertyDeclaration;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.css.style.EmptyStyle;
import org.xhtmlrenderer.css.style.FSDerivedValue;
import org.xhtmlrenderer.layout.CounterFunction;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.layout.Styleable;
import org.xhtmlrenderer.layout.WhitespaceStripper;
import org.xhtmlrenderer.newtable.TableBox;
import org.xhtmlrenderer.newtable.TableCellBox;
import org.xhtmlrenderer.newtable.TableColumn;
import org.xhtmlrenderer.newtable.TableRowBox;
import org.xhtmlrenderer.newtable.TableSectionBox;
import org.xhtmlrenderer.render.AnonymousBlockBox;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.render.Box;
import org.xhtmlrenderer.render.FloatedBoxData;
import org.xhtmlrenderer.render.InlineBox;

public class BoxBuilder {
    public static final int MARGIN_BOX_VERTICAL = 1;
    public static final int MARGIN_BOX_HORIZONTAL = 2;
    private static final int CONTENT_LIST_DOCUMENT = 1;
    private static final int CONTENT_LIST_MARGIN_BOX = 2;

    public static BlockBox createRootBox(LayoutContext c, Document document) {
        Element root = document.getDocumentElement();
        CalculatedStyle style = c.getSharedContext().getStyle(root);
        BlockBox result = style.isTable() || style.isInlineTable() ? new TableBox() : new BlockBox();
        result.setStyle(style);
        result.setElement(root);
        c.resolveCounters(style);
        c.pushLayer(result);
        if (c.isPrint()) {
            if (!style.isIdent(CSSName.PAGE, IdentValue.AUTO)) {
                c.setPageName(style.getStringProperty(CSSName.PAGE));
            }
            c.getRootLayer().addPage(c);
        }
        return result;
    }

    public static void createChildren(LayoutContext c, BlockBox parent) {
        ArrayList children = new ArrayList();
        ChildBoxInfo info = new ChildBoxInfo();
        BoxBuilder.createChildren(c, parent, parent.getElement(), children, info, false);
        boolean parentIsNestingTableContent = BoxBuilder.isNestingTableContent(parent.getStyle().getIdent(CSSName.DISPLAY));
        if (!parentIsNestingTableContent && !info.isContainsTableContent()) {
            BoxBuilder.resolveChildren(c, parent, children, info);
        } else {
            BoxBuilder.stripAllWhitespace(children);
            if (parentIsNestingTableContent) {
                BoxBuilder.resolveTableContent(c, parent, children, info);
            } else {
                BoxBuilder.resolveChildTableContent(c, parent, children, info, IdentValue.TABLE_CELL);
            }
        }
    }

    public static TableBox createMarginTable(LayoutContext c, PageInfo pageInfo, MarginBoxName[] names, int height, int direction) {
        if (!pageInfo.hasAny(names)) {
            return null;
        }
        Element source = c.getRootLayer().getMaster().getElement();
        ChildBoxInfo info = new ChildBoxInfo();
        CalculatedStyle pageStyle = new EmptyStyle().deriveStyle(pageInfo.getPageStyle());
        CalculatedStyle tableStyle = pageStyle.deriveStyle(CascadedStyle.createLayoutStyle(new PropertyDeclaration[]{new PropertyDeclaration(CSSName.DISPLAY, new PropertyValue(IdentValue.TABLE), true, 1), new PropertyDeclaration(CSSName.WIDTH, new PropertyValue(2, 100.0f, "100%"), true, 1)}));
        TableBox result = (TableBox)BoxBuilder.createBlockBox(tableStyle, info, false);
        result.setMarginAreaRoot(true);
        result.setStyle(tableStyle);
        result.setElement(source);
        result.setAnonymous(true);
        result.setChildrenContentType(2);
        CalculatedStyle tableSectionStyle = pageStyle.createAnonymousStyle(IdentValue.TABLE_ROW_GROUP);
        TableSectionBox section = (TableSectionBox)BoxBuilder.createBlockBox(tableSectionStyle, info, false);
        section.setStyle(tableSectionStyle);
        section.setElement(source);
        section.setAnonymous(true);
        section.setChildrenContentType(2);
        result.addChild(section);
        TableRowBox row = null;
        if (direction == 2) {
            CalculatedStyle tableRowStyle = pageStyle.createAnonymousStyle(IdentValue.TABLE_ROW);
            row = (TableRowBox)BoxBuilder.createBlockBox(tableRowStyle, info, false);
            row.setStyle(tableRowStyle);
            row.setElement(source);
            row.setAnonymous(true);
            row.setChildrenContentType(2);
            row.setHeightOverride(height);
            section.addChild(row);
        }
        int cellCount = 0;
        boolean alwaysCreate = names.length > 1 && direction == 2;
        for (int i = 0; i < names.length; ++i) {
            TableCellBox cell;
            CascadedStyle cellStyle = pageInfo.createMarginBoxStyle(names[i], alwaysCreate);
            if (cellStyle == null || (cell = BoxBuilder.createMarginBox(c, cellStyle, alwaysCreate)) == null) continue;
            if (direction == 1) {
                CalculatedStyle tableRowStyle = pageStyle.createAnonymousStyle(IdentValue.TABLE_ROW);
                row = (TableRowBox)BoxBuilder.createBlockBox(tableRowStyle, info, false);
                row.setStyle(tableRowStyle);
                row.setElement(source);
                row.setAnonymous(true);
                row.setChildrenContentType(2);
                row.setHeightOverride(height);
                section.addChild(row);
            }
            row.addChild(cell);
            ++cellCount;
        }
        if (direction == 1 && cellCount > 0) {
            TableRowBox r;
            int rHeight = 0;
            Iterator i = section.getChildIterator();
            while (i.hasNext()) {
                r = (TableRowBox)i.next();
                r.setHeightOverride(height / cellCount);
                rHeight += r.getHeightOverride();
            }
            i = section.getChildIterator();
            while (i.hasNext() && rHeight < height) {
                r = (TableRowBox)i.next();
                r.setHeightOverride(r.getHeightOverride() + 1);
                ++rHeight;
            }
        }
        return cellCount > 0 ? result : null;
    }

    private static TableCellBox createMarginBox(LayoutContext c, CascadedStyle cascadedStyle, boolean alwaysCreate) {
        boolean hasContent = true;
        PropertyDeclaration contentDecl = cascadedStyle.propertyByName(CSSName.CONTENT);
        CalculatedStyle style = new EmptyStyle().deriveStyle(cascadedStyle);
        if (style.isDisplayNone() && !alwaysCreate) {
            return null;
        }
        if (style.isIdent(CSSName.CONTENT, IdentValue.NONE) || style.isIdent(CSSName.CONTENT, IdentValue.NORMAL)) {
            hasContent = false;
        }
        if (style.isAutoWidth() && !alwaysCreate && !hasContent) {
            return null;
        }
        ArrayList children = new ArrayList();
        ChildBoxInfo info = new ChildBoxInfo();
        info.setContainsTableContent(true);
        info.setLayoutRunningBlocks(true);
        TableCellBox result = new TableCellBox();
        result.setAnonymous(true);
        result.setStyle(style);
        result.setElement(c.getRootLayer().getMaster().getElement());
        if (hasContent && !style.isDisplayNone()) {
            children.addAll(BoxBuilder.createGeneratedMarginBoxContent(c, c.getRootLayer().getMaster().getElement(), (PropertyValue)contentDecl.getValue(), style, info));
            BoxBuilder.stripAllWhitespace(children);
        }
        if (children.size() == 0 && style.isAutoWidth() && !alwaysCreate) {
            return null;
        }
        BoxBuilder.resolveChildTableContent(c, result, children, info, IdentValue.TABLE_CELL);
        return result;
    }

    private static void resolveChildren(LayoutContext c, BlockBox owner, List children, ChildBoxInfo info) {
        if (children.size() > 0) {
            if (info.isContainsBlockLevelContent()) {
                BoxBuilder.insertAnonymousBlocks(c.getSharedContext(), owner, children, info.isLayoutRunningBlocks());
                owner.setChildrenContentType(2);
            } else {
                WhitespaceStripper.stripInlineContent(children);
                if (children.size() > 0) {
                    owner.setInlineContent(children);
                    owner.setChildrenContentType(1);
                } else {
                    owner.setChildrenContentType(4);
                }
            }
        } else {
            owner.setChildrenContentType(4);
        }
    }

    private static boolean isAllProperTableNesting(IdentValue parentDisplay, List children) {
        for (Styleable child : children) {
            if (BoxBuilder.isProperTableNesting(parentDisplay, child.getStyle().getIdent(CSSName.DISPLAY))) continue;
            return false;
        }
        return true;
    }

    private static void resolveChildTableContent(LayoutContext c, BlockBox parent, List children, ChildBoxInfo info, IdentValue target) {
        ArrayList<Styleable> childrenForAnonymous = new ArrayList<Styleable>();
        ArrayList<Styleable> childrenWithAnonymous = new ArrayList<Styleable>();
        IdentValue nextUp = BoxBuilder.getPreviousTableNestingLevel(target);
        for (Styleable styleable : children) {
            if (BoxBuilder.matchesTableLevel(target, styleable.getStyle().getIdent(CSSName.DISPLAY))) {
                childrenForAnonymous.add(styleable);
                continue;
            }
            if (childrenForAnonymous.size() > 0) {
                BoxBuilder.createAnonymousTableContent(c, (BlockBox)childrenForAnonymous.get(0), nextUp, childrenForAnonymous, childrenWithAnonymous);
                childrenForAnonymous = new ArrayList();
            }
            childrenWithAnonymous.add(styleable);
        }
        if (childrenForAnonymous.size() > 0) {
            BoxBuilder.createAnonymousTableContent(c, (BlockBox)childrenForAnonymous.get(0), nextUp, childrenForAnonymous, childrenWithAnonymous);
        }
        if (nextUp == IdentValue.TABLE) {
            BoxBuilder.rebalanceInlineContent(childrenWithAnonymous);
            info.setContainsBlockLevelContent(true);
            BoxBuilder.resolveChildren(c, parent, childrenWithAnonymous, info);
        } else {
            BoxBuilder.resolveChildTableContent(c, parent, childrenWithAnonymous, info, nextUp);
        }
    }

    private static boolean matchesTableLevel(IdentValue target, IdentValue value) {
        if (target == IdentValue.TABLE_ROW_GROUP) {
            return value == IdentValue.TABLE_ROW_GROUP || value == IdentValue.TABLE_HEADER_GROUP || value == IdentValue.TABLE_FOOTER_GROUP || value == IdentValue.TABLE_CAPTION;
        }
        return target == value;
    }

    private static void rebalanceInlineContent(List content) {
        HashMap<Element, InlineBox> boxesByElement = new HashMap<Element, InlineBox>();
        for (Styleable styleable : content) {
            if (!(styleable instanceof InlineBox)) continue;
            InlineBox iB = (InlineBox)styleable;
            Element elem = iB.getElement();
            if (!boxesByElement.containsKey(elem)) {
                iB.setStartsHere(true);
            }
            boxesByElement.put(elem, iB);
        }
        for (InlineBox iB : boxesByElement.values()) {
            iB.setEndsHere(true);
        }
    }

    private static void stripAllWhitespace(List content) {
        int start = 0;
        int current = 0;
        boolean started = false;
        for (current = 0; current < content.size(); ++current) {
            Styleable styleable = (Styleable)content.get(current);
            if (!styleable.getStyle().isLayedOutInInlineContext()) {
                if (started) {
                    int before = content.size();
                    WhitespaceStripper.stripInlineContent(content.subList(start, current));
                    int after = content.size();
                    current -= before - after;
                }
                started = false;
                continue;
            }
            if (started) continue;
            started = true;
            start = current;
        }
        if (started) {
            WhitespaceStripper.stripInlineContent(content.subList(start, current));
        }
    }

    private static void resolveTableContent(LayoutContext c, BlockBox parent, List children, ChildBoxInfo info) {
        IdentValue parentDisplay = parent.getStyle().getIdent(CSSName.DISPLAY);
        IdentValue next = BoxBuilder.getNextTableNestingLevel(parentDisplay);
        if (next == null && parent.isAnonymous() && BoxBuilder.containsOrphanedTableContent(children)) {
            BoxBuilder.resolveChildTableContent(c, parent, children, info, IdentValue.TABLE_CELL);
        } else if (next == null || BoxBuilder.isAllProperTableNesting(parentDisplay, children)) {
            if (parent.isAnonymous()) {
                BoxBuilder.rebalanceInlineContent(children);
            }
            BoxBuilder.resolveChildren(c, parent, children, info);
        } else {
            ArrayList<Styleable> childrenForAnonymous = new ArrayList<Styleable>();
            ArrayList<Styleable> childrenWithAnonymous = new ArrayList<Styleable>();
            for (Styleable child : children) {
                IdentValue childDisplay = child.getStyle().getIdent(CSSName.DISPLAY);
                if (BoxBuilder.isProperTableNesting(parentDisplay, childDisplay)) {
                    if (childrenForAnonymous.size() > 0) {
                        BoxBuilder.createAnonymousTableContent(c, parent, next, childrenForAnonymous, childrenWithAnonymous);
                        childrenForAnonymous = new ArrayList();
                    }
                    childrenWithAnonymous.add(child);
                    continue;
                }
                childrenForAnonymous.add(child);
            }
            if (childrenForAnonymous.size() > 0) {
                BoxBuilder.createAnonymousTableContent(c, parent, next, childrenForAnonymous, childrenWithAnonymous);
            }
            info.setContainsBlockLevelContent(true);
            BoxBuilder.resolveChildren(c, parent, childrenWithAnonymous, info);
        }
    }

    private static boolean containsOrphanedTableContent(List children) {
        for (Styleable child : children) {
            IdentValue display = child.getStyle().getIdent(CSSName.DISPLAY);
            if (display != IdentValue.TABLE_HEADER_GROUP && display != IdentValue.TABLE_ROW_GROUP && display != IdentValue.TABLE_FOOTER_GROUP && display != IdentValue.TABLE_ROW) continue;
            return true;
        }
        return false;
    }

    private static boolean isParentInline(BlockBox box) {
        CalculatedStyle parentStyle = box.getStyle().getParent();
        return parentStyle != null && parentStyle.isInline();
    }

    private static void createAnonymousTableContent(LayoutContext c, BlockBox source, IdentValue next, List childrenForAnonymous, List childrenWithAnonymous) {
        ChildBoxInfo nested = BoxBuilder.lookForBlockContent(childrenForAnonymous);
        IdentValue anonDisplay = BoxBuilder.isParentInline(source) && next == IdentValue.TABLE ? IdentValue.INLINE_TABLE : next;
        CalculatedStyle anonStyle = source.getStyle().createAnonymousStyle(anonDisplay);
        BlockBox anonBox = BoxBuilder.createBlockBox(anonStyle, nested, false);
        anonBox.setStyle(anonStyle);
        anonBox.setAnonymous(true);
        anonBox.setElement(source.getElement());
        BoxBuilder.resolveTableContent(c, anonBox, childrenForAnonymous, nested);
        if (next == IdentValue.TABLE) {
            childrenWithAnonymous.add(BoxBuilder.reorderTableContent(c, (TableBox)anonBox));
        } else {
            childrenWithAnonymous.add(anonBox);
        }
    }

    private static BlockBox reorderTableContent(LayoutContext c, TableBox table) {
        CalculatedStyle anonStyle;
        LinkedList<Box> topCaptions = new LinkedList<Box>();
        Box header = null;
        LinkedList<Box> bodies = new LinkedList<Box>();
        Box footer = null;
        LinkedList<Box> bottomCaptions = new LinkedList<Box>();
        Iterator i = table.getChildIterator();
        while (i.hasNext()) {
            Box b = (Box)i.next();
            IdentValue display = b.getStyle().getIdent(CSSName.DISPLAY);
            if (display == IdentValue.TABLE_CAPTION) {
                IdentValue side = b.getStyle().getIdent(CSSName.CAPTION_SIDE);
                if (side == IdentValue.BOTTOM) {
                    bottomCaptions.add(b);
                    continue;
                }
                topCaptions.add(b);
                continue;
            }
            if (display == IdentValue.TABLE_HEADER_GROUP && header == null) {
                header = b;
                continue;
            }
            if (display == IdentValue.TABLE_FOOTER_GROUP && footer == null) {
                footer = b;
                continue;
            }
            bodies.add(b);
        }
        table.removeAllChildren();
        if (header != null) {
            ((TableSectionBox)header).setHeader(true);
            table.addChild(header);
        }
        table.addAllChildren(bodies);
        if (footer != null) {
            ((TableSectionBox)footer).setFooter(true);
            table.addChild(footer);
        }
        if (topCaptions.size() == 0 && bottomCaptions.size() == 0) {
            return table;
        }
        if (table.getStyle().isFloated()) {
            CascadedStyle cascadedStyle = CascadedStyle.createLayoutStyle(new PropertyDeclaration[]{CascadedStyle.createLayoutPropertyDeclaration(CSSName.DISPLAY, IdentValue.BLOCK), CascadedStyle.createLayoutPropertyDeclaration(CSSName.FLOAT, table.getStyle().getIdent(CSSName.FLOAT))});
            anonStyle = table.getStyle().deriveStyle(cascadedStyle);
        } else {
            anonStyle = table.getStyle().createAnonymousStyle(IdentValue.BLOCK);
        }
        BlockBox anonBox = new BlockBox();
        anonBox.setStyle(anonStyle);
        anonBox.setAnonymous(true);
        anonBox.setFromCaptionedTable(true);
        anonBox.setElement(table.getElement());
        anonBox.setChildrenContentType(2);
        anonBox.addAllChildren(topCaptions);
        anonBox.addChild(table);
        anonBox.addAllChildren(bottomCaptions);
        if (table.getStyle().isFloated()) {
            anonBox.setFloatedBoxData(new FloatedBoxData());
            table.setFloatedBoxData(null);
            CascadedStyle original = c.getSharedContext().getCss().getCascadedStyle(table.getElement(), false);
            CascadedStyle modified = CascadedStyle.createLayoutStyle(original, new PropertyDeclaration[]{CascadedStyle.createLayoutPropertyDeclaration(CSSName.FLOAT, IdentValue.NONE)});
            table.setStyle(table.getStyle().getParent().deriveStyle(modified));
        }
        return anonBox;
    }

    private static ChildBoxInfo lookForBlockContent(List styleables) {
        ChildBoxInfo result = new ChildBoxInfo();
        for (Styleable s : styleables) {
            if (s.getStyle().isLayedOutInInlineContext()) continue;
            result.setContainsBlockLevelContent(true);
            break;
        }
        return result;
    }

    private static IdentValue getNextTableNestingLevel(IdentValue display) {
        if (display == IdentValue.TABLE || display == IdentValue.INLINE_TABLE) {
            return IdentValue.TABLE_ROW_GROUP;
        }
        if (display == IdentValue.TABLE_HEADER_GROUP || display == IdentValue.TABLE_ROW_GROUP || display == IdentValue.TABLE_FOOTER_GROUP) {
            return IdentValue.TABLE_ROW;
        }
        if (display == IdentValue.TABLE_ROW) {
            return IdentValue.TABLE_CELL;
        }
        return null;
    }

    private static IdentValue getPreviousTableNestingLevel(IdentValue display) {
        if (display == IdentValue.TABLE_CELL) {
            return IdentValue.TABLE_ROW;
        }
        if (display == IdentValue.TABLE_ROW) {
            return IdentValue.TABLE_ROW_GROUP;
        }
        if (display == IdentValue.TABLE_HEADER_GROUP || display == IdentValue.TABLE_ROW_GROUP || display == IdentValue.TABLE_FOOTER_GROUP) {
            return IdentValue.TABLE;
        }
        return null;
    }

    private static boolean isProperTableNesting(IdentValue parent, IdentValue child) {
        return parent == IdentValue.TABLE && (child == IdentValue.TABLE_HEADER_GROUP || child == IdentValue.TABLE_ROW_GROUP || child == IdentValue.TABLE_FOOTER_GROUP || child == IdentValue.TABLE_CAPTION) || (parent == IdentValue.TABLE_HEADER_GROUP || parent == IdentValue.TABLE_ROW_GROUP || parent == IdentValue.TABLE_FOOTER_GROUP) && child == IdentValue.TABLE_ROW || parent == IdentValue.TABLE_ROW && child == IdentValue.TABLE_CELL || parent == IdentValue.INLINE_TABLE && (child == IdentValue.TABLE_HEADER_GROUP || child == IdentValue.TABLE_ROW_GROUP || child == IdentValue.TABLE_FOOTER_GROUP);
    }

    private static boolean isNestingTableContent(IdentValue display) {
        return display == IdentValue.TABLE || display == IdentValue.INLINE_TABLE || display == IdentValue.TABLE_HEADER_GROUP || display == IdentValue.TABLE_ROW_GROUP || display == IdentValue.TABLE_FOOTER_GROUP || display == IdentValue.TABLE_ROW;
    }

    private static boolean isAttrFunction(FSFunction function) {
        List params;
        if (function.getName().equals("attr") && (params = function.getParameters()).size() == 1) {
            PropertyValue value = (PropertyValue)params.get(0);
            return value.getPrimitiveType() == 21;
        }
        return false;
    }

    public static boolean isElementFunction(FSFunction function) {
        if (function.getName().equals("element")) {
            List params = function.getParameters();
            if (params.size() < 1 || params.size() > 2) {
                return false;
            }
            boolean ok = true;
            PropertyValue value1 = (PropertyValue)params.get(0);
            boolean bl = ok = value1.getPrimitiveType() == 21;
            if (ok && params.size() == 2) {
                PropertyValue value2 = (PropertyValue)params.get(1);
                ok = value2.getPrimitiveType() == 21;
            }
            return ok;
        }
        return false;
    }

    private static CounterFunction makeCounterFunction(FSFunction function, LayoutContext c, CalculatedStyle style) {
        if (function.getName().equals("counter")) {
            List params = function.getParameters();
            if (params.size() < 1 || params.size() > 2) {
                return null;
            }
            PropertyValue value = (PropertyValue)params.get(0);
            if (value.getPrimitiveType() != 21) {
                return null;
            }
            String s = value.getStringValue();
            if (s.equals("page") || s.equals("pages")) {
                return null;
            }
            String counter = value.getStringValue();
            IdentValue listStyleType = IdentValue.DECIMAL;
            if (params.size() == 2) {
                value = (PropertyValue)params.get(1);
                if (value.getPrimitiveType() != 21) {
                    return null;
                }
                IdentValue identValue = IdentValue.valueOf(value.getStringValue());
                if (identValue != null) {
                    value.setIdentValue(identValue);
                    listStyleType = identValue;
                }
            }
            int counterValue = c.getCounterContext(style).getCurrentCounterValue(counter);
            return new CounterFunction(counterValue, listStyleType);
        }
        if (function.getName().equals("counters")) {
            List params = function.getParameters();
            if (params.size() < 2 || params.size() > 3) {
                return null;
            }
            PropertyValue value = (PropertyValue)params.get(0);
            if (value.getPrimitiveType() != 21) {
                return null;
            }
            String counter = value.getStringValue();
            value = (PropertyValue)params.get(1);
            if (value.getPrimitiveType() != 19) {
                return null;
            }
            String separator = value.getStringValue();
            IdentValue listStyleType = IdentValue.DECIMAL;
            if (params.size() == 3) {
                value = (PropertyValue)params.get(2);
                if (value.getPrimitiveType() != 21) {
                    return null;
                }
                IdentValue identValue = IdentValue.valueOf(value.getStringValue());
                if (identValue != null) {
                    value.setIdentValue(identValue);
                    listStyleType = identValue;
                }
            }
            List counterValues = c.getCounterContext(style).getCurrentCounterValues(counter);
            return new CounterFunction(counterValues, separator, listStyleType);
        }
        return null;
    }

    private static String getAttributeValue(FSFunction attrFunc, Element e) {
        PropertyValue value = (PropertyValue)attrFunc.getParameters().get(0);
        return e.getAttribute(value.getStringValue());
    }

    private static List createGeneratedContentList(LayoutContext c, Element element, PropertyValue propValue, String peName, CalculatedStyle style, int mode, ChildBoxInfo info) {
        List values = propValue.getValues();
        if (values == null) {
            return Collections.EMPTY_LIST;
        }
        ArrayList<Styleable> result = new ArrayList<Styleable>(values.size());
        for (PropertyValue value : values) {
            FSDerivedValue dv;
            ContentFunction contentFunction = null;
            FSFunction function = null;
            String content = null;
            short type = value.getPrimitiveType();
            if (type == 19) {
                content = value.getStringValue();
            } else if (value.getPropertyValueType() == 7) {
                if (mode == 1 && BoxBuilder.isAttrFunction(value.getFunction())) {
                    content = BoxBuilder.getAttributeValue(value.getFunction(), element);
                } else {
                    CounterFunction cFunc = null;
                    if (mode == 1) {
                        cFunc = BoxBuilder.makeCounterFunction(value.getFunction(), c, style);
                    }
                    if (cFunc != null) {
                        content = cFunc.evaluate();
                        contentFunction = null;
                        function = null;
                    } else if (mode == 2 && BoxBuilder.isElementFunction(value.getFunction())) {
                        BlockBox target = BoxBuilder.getRunningBlock(c, value);
                        if (target != null) {
                            result.add(target.copyOf());
                            info.setContainsBlockLevelContent(true);
                        }
                    } else {
                        contentFunction = c.getContentFunctionFactory().lookupFunction(c, value.getFunction());
                        if (contentFunction != null) {
                            function = value.getFunction();
                            if (contentFunction.isStatic()) {
                                content = contentFunction.calculate(c, function);
                                contentFunction = null;
                                function = null;
                            } else {
                                content = contentFunction.getLayoutReplacementText();
                            }
                        }
                    }
                }
            } else if (type == 21 && (dv = style.valueByName(CSSName.QUOTES)) != IdentValue.NONE) {
                String[] quotes;
                IdentValue ident = value.getIdentValue();
                if (ident == IdentValue.OPEN_QUOTE) {
                    quotes = style.asStringArray(CSSName.QUOTES);
                    content = quotes[0];
                } else if (ident == IdentValue.CLOSE_QUOTE) {
                    quotes = style.asStringArray(CSSName.QUOTES);
                    content = quotes[1];
                }
            }
            if (content == null) continue;
            InlineBox iB = new InlineBox(content, null);
            iB.setContentFunction(contentFunction);
            iB.setFunction(function);
            iB.setElement(element);
            iB.setPseudoElementOrClass(peName);
            iB.setStartsHere(true);
            iB.setEndsHere(true);
            result.add(iB);
        }
        return result;
    }

    public static BlockBox getRunningBlock(LayoutContext c, PropertyValue value) {
        List params = value.getFunction().getParameters();
        String ident = ((PropertyValue)params.get(0)).getStringValue();
        PageElementPosition position = null;
        if (params.size() == 2) {
            position = PageElementPosition.valueOf(((PropertyValue)params.get(1)).getStringValue());
        }
        if (position == null) {
            position = PageElementPosition.FIRST;
        }
        BlockBox target = c.getRootDocumentLayer().getRunningBlock(ident, c.getPage(), position);
        return target;
    }

    private static void insertGeneratedContent(LayoutContext c, Element element, CalculatedStyle parentStyle, String peName, List children, ChildBoxInfo info) {
        CascadedStyle peStyle = c.getCss().getPseudoElementStyle(element, peName);
        if (peStyle != null) {
            PropertyDeclaration contentDecl = peStyle.propertyByName(CSSName.CONTENT);
            PropertyDeclaration counterResetDecl = peStyle.propertyByName(CSSName.COUNTER_RESET);
            PropertyDeclaration counterIncrDecl = peStyle.propertyByName(CSSName.COUNTER_INCREMENT);
            CalculatedStyle calculatedStyle = null;
            if (contentDecl != null || counterResetDecl != null || counterIncrDecl != null) {
                calculatedStyle = parentStyle.deriveStyle(peStyle);
                if (calculatedStyle.isDisplayNone()) {
                    return;
                }
                if (calculatedStyle.isIdent(CSSName.CONTENT, IdentValue.NONE)) {
                    return;
                }
                if (calculatedStyle.isIdent(CSSName.CONTENT, IdentValue.NORMAL) && (peName.equals("before") || peName.equals("after"))) {
                    return;
                }
                if (calculatedStyle.isTable() || calculatedStyle.isTableRow() || calculatedStyle.isTableSection()) {
                    CascadedStyle newPeStyle = CascadedStyle.createLayoutStyle(peStyle, new PropertyDeclaration[]{CascadedStyle.createLayoutPropertyDeclaration(CSSName.DISPLAY, IdentValue.BLOCK)});
                    calculatedStyle = parentStyle.deriveStyle(newPeStyle);
                }
                c.resolveCounters(calculatedStyle);
            }
            if (contentDecl != null) {
                CSSPrimitiveValue propValue = contentDecl.getValue();
                children.addAll(BoxBuilder.createGeneratedContent(c, element, peName, calculatedStyle, (PropertyValue)propValue, info));
            }
        }
    }

    private static List createGeneratedContent(LayoutContext c, Element element, String peName, CalculatedStyle style, PropertyValue property, ChildBoxInfo info) {
        if (style.isDisplayNone() || style.isIdent(CSSName.DISPLAY, IdentValue.TABLE_COLUMN) || style.isIdent(CSSName.DISPLAY, IdentValue.TABLE_COLUMN_GROUP)) {
            return Collections.EMPTY_LIST;
        }
        List inlineBoxes = BoxBuilder.createGeneratedContentList(c, element, property, peName, style, 1, null);
        if (style.isInline()) {
            for (InlineBox iB : inlineBoxes) {
                iB.setStyle(style);
                iB.applyTextTransform();
            }
            return inlineBoxes;
        }
        CalculatedStyle anon = style.createAnonymousStyle(IdentValue.INLINE);
        for (InlineBox iB : inlineBoxes) {
            iB.setStyle(anon);
            iB.applyTextTransform();
            iB.setElement(null);
        }
        BlockBox result = BoxBuilder.createBlockBox(style, info, true);
        result.setStyle(style);
        result.setInlineContent(inlineBoxes);
        result.setElement(element);
        result.setChildrenContentType(1);
        result.setPseudoElementOrClass(peName);
        if (!style.isLayedOutInInlineContext()) {
            info.setContainsBlockLevelContent(true);
        }
        return new ArrayList<BlockBox>(Collections.singletonList(result));
    }

    private static List createGeneratedMarginBoxContent(LayoutContext c, Element element, PropertyValue property, CalculatedStyle style, ChildBoxInfo info) {
        List result = BoxBuilder.createGeneratedContentList(c, element, property, null, style, 2, info);
        CalculatedStyle anon = style.createAnonymousStyle(IdentValue.INLINE);
        for (Styleable s : result) {
            if (!(s instanceof InlineBox)) continue;
            InlineBox iB = (InlineBox)s;
            iB.setElement(null);
            iB.setStyle(anon);
            iB.applyTextTransform();
        }
        return result;
    }

    private static BlockBox createBlockBox(CalculatedStyle style, ChildBoxInfo info, boolean generated) {
        if (style.isFloated() && !style.isAbsolute() && !style.isFixed()) {
            BlockBox result = style.isTable() || style.isInlineTable() ? new TableBox() : new BlockBox();
            result.setFloatedBoxData(new FloatedBoxData());
            return result;
        }
        if (style.isSpecifiedAsBlock()) {
            return new BlockBox();
        }
        if (!generated && (style.isTable() || style.isInlineTable())) {
            return new TableBox();
        }
        if (style.isTableCell()) {
            info.setContainsTableContent(true);
            return new TableCellBox();
        }
        if (!generated && style.isTableRow()) {
            info.setContainsTableContent(true);
            return new TableRowBox();
        }
        if (!generated && style.isTableSection()) {
            info.setContainsTableContent(true);
            return new TableSectionBox();
        }
        if (style.isTableCaption()) {
            info.setContainsTableContent(true);
            return new BlockBox();
        }
        return new BlockBox();
    }

    private static void addColumns(LayoutContext c, TableBox table, TableColumn parent) {
        SharedContext sharedContext = c.getSharedContext();
        boolean found = false;
        for (Node working = parent.getElement().getFirstChild(); working != null; working = working.getNextSibling()) {
            Element element;
            CalculatedStyle style;
            if (working.getNodeType() != 1 || !(style = sharedContext.getStyle(element = (Element)working)).isIdent(CSSName.DISPLAY, IdentValue.TABLE_COLUMN)) continue;
            found = true;
            TableColumn col = new TableColumn(element, style);
            col.setParent(parent);
            table.addStyleColumn(col);
        }
        if (!found) {
            table.addStyleColumn(parent);
        }
    }

    private static void addColumnOrColumnGroup(LayoutContext c, TableBox table, Element e, CalculatedStyle style) {
        if (style.isIdent(CSSName.DISPLAY, IdentValue.TABLE_COLUMN)) {
            table.addStyleColumn(new TableColumn(e, style));
        } else {
            BoxBuilder.addColumns(c, table, new TableColumn(e, style));
        }
    }

    private static InlineBox createInlineBox(String text, Element parent, CalculatedStyle parentStyle, Text node) {
        InlineBox result = new InlineBox(text, node);
        if (parentStyle.isInline() && !(parent.getParentNode() instanceof Document)) {
            result.setStyle(parentStyle);
            result.setElement(parent);
        } else {
            result.setStyle(parentStyle.createAnonymousStyle(IdentValue.INLINE));
        }
        result.applyTextTransform();
        return result;
    }

    private static void createChildren(LayoutContext c, BlockBox blockParent, Element parent, List children, ChildBoxInfo info, boolean inline) {
        SharedContext sharedContext = c.getSharedContext();
        CalculatedStyle parentStyle = sharedContext.getStyle(parent);
        BoxBuilder.insertGeneratedContent(c, parent, parentStyle, "before", children, info);
        Node working = parent.getFirstChild();
        boolean needStartText = inline;
        boolean needEndText = inline;
        if (working != null) {
            InlineBox previousIB = null;
            do {
                InlineBox iB;
                Styleable child = null;
                short nodeType = working.getNodeType();
                if (nodeType == 1) {
                    TableBox table;
                    Node valueAttribute;
                    Element element = (Element)working;
                    CalculatedStyle style = sharedContext.getStyle(element);
                    if (style.isDisplayNone()) continue;
                    Integer start = null;
                    if ("ol".equals(working.getNodeName())) {
                        Node startAttribute = working.getAttributes().getNamedItem("start");
                        if (startAttribute != null) {
                            try {
                                start = new Integer(Integer.parseInt(startAttribute.getNodeValue()) - 1);
                            }
                            catch (NumberFormatException numberFormatException) {}
                        }
                    } else if ("li".equals(working.getNodeName()) && (valueAttribute = working.getAttributes().getNamedItem("value")) != null) {
                        try {
                            start = new Integer(Integer.parseInt(valueAttribute.getNodeValue()) - 1);
                        }
                        catch (NumberFormatException numberFormatException) {
                            // empty catch block
                        }
                    }
                    c.resolveCounters(style, start);
                    if (style.isIdent(CSSName.DISPLAY, IdentValue.TABLE_COLUMN) || style.isIdent(CSSName.DISPLAY, IdentValue.TABLE_COLUMN_GROUP)) {
                        if (blockParent == null || !blockParent.getStyle().isTable() && !blockParent.getStyle().isInlineTable()) continue;
                        table = (TableBox)blockParent;
                        BoxBuilder.addColumnOrColumnGroup(c, table, element, style);
                        continue;
                    }
                    if (style.isInline()) {
                        if (needStartText) {
                            needStartText = false;
                            InlineBox iB2 = BoxBuilder.createInlineBox("", parent, parentStyle, null);
                            iB2.setStartsHere(true);
                            iB2.setEndsHere(false);
                            children.add(iB2);
                            previousIB = iB2;
                        }
                        BoxBuilder.createChildren(c, null, element, children, info, true);
                        if (inline) {
                            if (previousIB != null) {
                                previousIB.setEndsHere(false);
                            }
                            needEndText = true;
                        }
                    } else {
                        BlockBox block;
                        child = BoxBuilder.createBlockBox(style, info, false);
                        child.setStyle(style);
                        child.setElement(element);
                        if (style.isListItem()) {
                            block = (BlockBox)child;
                            block.setListCounter(c.getCounterContext(style).getCurrentCounterValue("list-item"));
                        }
                        if (style.isTable() || style.isInlineTable()) {
                            table = (TableBox)child;
                            table.ensureChildren(c);
                            child = BoxBuilder.reorderTableContent(c, table);
                        }
                        if (!info.isContainsBlockLevelContent() && !style.isLayedOutInInlineContext()) {
                            info.setContainsBlockLevelContent(true);
                        }
                        if ((block = (BlockBox)child).getStyle().mayHaveFirstLine()) {
                            block.setFirstLineStyle(c.getCss().getPseudoElementStyle(element, "first-line"));
                        }
                        if (block.getStyle().mayHaveFirstLetter()) {
                            block.setFirstLetterStyle(c.getCss().getPseudoElementStyle(element, "first-letter"));
                        }
                        block.ensureChildren(c);
                    }
                } else if (nodeType == 3 || nodeType == 4) {
                    needStartText = false;
                    needEndText = false;
                    Text textNode = (Text)working;
                    child = BoxBuilder.createInlineBox(textNode.getData(), parent, parentStyle, textNode);
                    iB = child;
                    iB.setEndsHere(true);
                    if (previousIB == null) {
                        iB.setStartsHere(true);
                    } else {
                        previousIB.setEndsHere(false);
                    }
                    previousIB = iB;
                } else if (nodeType == 5) {
                    EntityReference entityReference = (EntityReference)working;
                    child = BoxBuilder.createInlineBox(entityReference.getTextContent(), parent, parentStyle, null);
                    iB = child;
                    iB.setEndsHere(true);
                    if (previousIB == null) {
                        iB.setStartsHere(true);
                    } else {
                        previousIB.setEndsHere(false);
                    }
                    previousIB = iB;
                }
                if (child == null) continue;
                children.add(child);
            } while ((working = working.getNextSibling()) != null);
        }
        if (needStartText || needEndText) {
            InlineBox iB = BoxBuilder.createInlineBox("", parent, parentStyle, null);
            iB.setStartsHere(needStartText);
            iB.setEndsHere(needEndText);
            children.add(iB);
        }
        BoxBuilder.insertGeneratedContent(c, parent, parentStyle, "after", children, info);
    }

    private static void insertAnonymousBlocks(SharedContext c, Box parent, List children, boolean layoutRunningBlocks) {
        ArrayList<Styleable> inline = new ArrayList<Styleable>();
        LinkedList<InlineBox> parents = new LinkedList<InlineBox>();
        ArrayList savedParents = null;
        for (Styleable child : children) {
            if (!(!child.getStyle().isLayedOutInInlineContext() || layoutRunningBlocks && child.getStyle().isRunning())) {
                inline.add(child);
                if (!child.getStyle().isInline()) continue;
                InlineBox iB = (InlineBox)child;
                if (iB.isStartsHere()) {
                    parents.add(iB);
                }
                if (!iB.isEndsHere()) continue;
                parents.removeLast();
                continue;
            }
            if (inline.size() > 0) {
                BoxBuilder.createAnonymousBlock(c, parent, inline, savedParents);
                inline = new ArrayList();
                savedParents = new ArrayList(parents);
            }
            parent.addChild((Box)child);
        }
        BoxBuilder.createAnonymousBlock(c, parent, inline, savedParents);
    }

    private static void createAnonymousBlock(SharedContext c, Box parent, List inline, List savedParents) {
        WhitespaceStripper.stripInlineContent(inline);
        if (inline.size() > 0) {
            AnonymousBlockBox anon = new AnonymousBlockBox(parent.getElement());
            anon.setStyle(parent.getStyle().createAnonymousStyle(IdentValue.BLOCK));
            anon.setAnonymous(true);
            if (savedParents != null && savedParents.size() > 0) {
                anon.setOpenInlineBoxes(savedParents);
            }
            parent.addChild(anon);
            anon.setChildrenContentType(1);
            anon.setInlineContent(inline);
        }
    }

    private static class ChildBoxInfo {
        private boolean _containsBlockLevelContent;
        private boolean _containsTableContent;
        private boolean _layoutRunningBlocks;

        public boolean isContainsBlockLevelContent() {
            return this._containsBlockLevelContent;
        }

        public void setContainsBlockLevelContent(boolean containsBlockLevelContent) {
            this._containsBlockLevelContent = containsBlockLevelContent;
        }

        public boolean isContainsTableContent() {
            return this._containsTableContent;
        }

        public void setContainsTableContent(boolean containsTableContent) {
            this._containsTableContent = containsTableContent;
        }

        public boolean isLayoutRunningBlocks() {
            return this._layoutRunningBlocks;
        }

        public void setLayoutRunningBlocks(boolean layoutRunningBlocks) {
            this._layoutRunningBlocks = layoutRunningBlocks;
        }
    }
}

