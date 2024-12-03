/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.util.TextUtils
 *  org.apache.commons.lang.StringUtils
 *  org.apache.html.dom.HTMLDocumentImpl
 *  org.apache.xerces.xni.parser.XMLDocumentFilter
 *  org.cyberneko.html.filters.Writer
 *  org.cyberneko.html.parsers.DOMFragmentParser
 */
package com.atlassian.renderer.wysiwyg.converter;

import com.atlassian.renderer.IconManager;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.WikiStyleRenderer;
import com.atlassian.renderer.util.NodeUtil;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.renderer.v2.macro.Macro;
import com.atlassian.renderer.v2.macro.MacroManager;
import com.atlassian.renderer.v2.macro.ResourceAwareMacroDecorator;
import com.atlassian.renderer.wysiwyg.ListContext;
import com.atlassian.renderer.wysiwyg.NodeContext;
import com.atlassian.renderer.wysiwyg.Styles;
import com.atlassian.renderer.wysiwyg.WysiwygConverter;
import com.atlassian.renderer.wysiwyg.WysiwygMacroHelper;
import com.atlassian.renderer.wysiwyg.WysiwygNodeConverter;
import com.atlassian.renderer.wysiwyg.converter.BlockQuoteConverter;
import com.atlassian.renderer.wysiwyg.converter.BreakConverter;
import com.atlassian.renderer.wysiwyg.converter.CommentConverter;
import com.atlassian.renderer.wysiwyg.converter.Converter;
import com.atlassian.renderer.wysiwyg.converter.DivConverter;
import com.atlassian.renderer.wysiwyg.converter.ExternallyDefinedConverter;
import com.atlassian.renderer.wysiwyg.converter.FontConverter;
import com.atlassian.renderer.wysiwyg.converter.FormatConverter;
import com.atlassian.renderer.wysiwyg.converter.HeadingConverter;
import com.atlassian.renderer.wysiwyg.converter.HorizontalRuleConverter;
import com.atlassian.renderer.wysiwyg.converter.IgnoreNodeAndChildren;
import com.atlassian.renderer.wysiwyg.converter.IgnoreNodeAndConvertChildText;
import com.atlassian.renderer.wysiwyg.converter.ImageConverter;
import com.atlassian.renderer.wysiwyg.converter.LinkConverter;
import com.atlassian.renderer.wysiwyg.converter.ListConverter;
import com.atlassian.renderer.wysiwyg.converter.ListItemConverter;
import com.atlassian.renderer.wysiwyg.converter.ParagraphConverter;
import com.atlassian.renderer.wysiwyg.converter.PreformattingConverter;
import com.atlassian.renderer.wysiwyg.converter.Separation;
import com.atlassian.renderer.wysiwyg.converter.SpanConverter;
import com.atlassian.renderer.wysiwyg.converter.TableBodyConverter;
import com.atlassian.renderer.wysiwyg.converter.TableCellConverter;
import com.atlassian.renderer.wysiwyg.converter.TableConverter;
import com.atlassian.renderer.wysiwyg.converter.TableRowConverter;
import com.atlassian.renderer.wysiwyg.converter.TextConverter;
import com.atlassian.renderer.wysiwyg.converter.TypeBasedSeparation;
import com.opensymphony.util.TextUtils;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.parsers.FactoryConfigurationError;
import org.apache.commons.lang.StringUtils;
import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.cyberneko.html.filters.Writer;
import org.cyberneko.html.parsers.DOMFragmentParser;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DefaultWysiwygConverter
implements WysiwygConverter {
    public static final String TEXT_SEPARATOR = "TEXTSEP";
    public static final String TEXT_SEPERATOR = "TEXTSEP";
    private static final String MATCH_NON_BREAKING_SPACES = "\u00a0+";
    public static final String CURSOR_PLACEMENT_CLASS = "atl_conf_pad";
    public static final String CURSOR_PLACEMENT_PARAGRAPH = "<p class=\"atl_conf_pad\"> </p>";
    private MacroManager macroManager;
    protected static boolean debug = false;
    private IconManager iconManager;
    protected WikiStyleRenderer renderer;
    protected Set<String> macrosToIgnore = new HashSet<String>();
    private final List<com.atlassian.renderer.v2.components.TextConverter> textConverterComponents;
    private static List<? extends Converter> CONVERTERS = Collections.unmodifiableList(Arrays.asList(CommentConverter.INSTANCE, IgnoreNodeAndConvertChildText.INSTANCE, TextConverter.INSTANCE, ExternallyDefinedConverter.INSTANCE, BreakConverter.INSTANCE, ParagraphConverter.INSTANCE, FormatConverter.INSTANCE, SpanConverter.INSTANCE, FontConverter.INSTANCE, ListConverter.INSTANCE, ListItemConverter.INSTANCE, TableConverter.INSTANCE, TableBodyConverter.INSTANCE, TableRowConverter.INSTANCE, TableCellConverter.TD, TableCellConverter.TH, DivConverter.INSTANCE, HeadingConverter.INSTANCE, ImageConverter.INSTANCE, LinkConverter.INSTANCE, PreformattingConverter.INSTANCE, HorizontalRuleConverter.INSTANCE, IgnoreNodeAndChildren.INSTANCE, BlockQuoteConverter.INSTANCE));
    public static final String VALID_START = "(?<![}\\p{L}\\p{Nd}\\\\])";
    public static final String VALID_END = "(?![{\\p{L}\\p{Nd}])";
    private static final String PHRASE_CLEANUP_REGEX = "\\{((?:\\?\\?)|(?:\\*)|(?:\\^)|(?:~)|(?:_)|(?:-)|(?:\\+)|(?:\\{\\{)|(?:\\}\\}))\\}";

    public DefaultWysiwygConverter() {
        this.macrosToIgnore.add("color");
        this.textConverterComponents = Collections.emptyList();
    }

    public DefaultWysiwygConverter(List<com.atlassian.renderer.v2.components.TextConverter> textConverterComponents) {
        this.macrosToIgnore.add("color");
        this.textConverterComponents = textConverterComponents;
    }

    public void setWikiStyleRenderer(WikiStyleRenderer renderer) {
        this.renderer = renderer;
    }

    public void setMacroManager(MacroManager macroManager) {
        this.macroManager = macroManager;
    }

    public String getSeparator(String current, NodeContext nodeContext) {
        String sep;
        String prevType;
        Node previous = nodeContext.getPreviousSibling();
        if (previous == null) {
            prevType = null;
        } else if (NodeUtil.isTextNode(previous)) {
            prevType = "text";
        } else {
            if (WysiwygMacroHelper.getMacroName(previous) != null) {
                String attributeName = "wikihastrailingnewline";
                return NodeUtil.getBooleanAttributeValue(previous, attributeName, false) ? "\n" : "";
            }
            if (WysiwygMacroHelper.isMacroBody(previous) || WysiwygMacroHelper.isMacroTag(previous)) {
                prevType = "text";
            } else if (DefaultWysiwygConverter.isUserNewline(previous)) {
                prevType = "userNewline";
            } else if (DefaultWysiwygConverter.isForcedNewline(previous)) {
                prevType = "forcedNewline";
            } else if (NodeUtil.isList(previous)) {
                prevType = "list";
            } else {
                prevType = previous.getNodeName().toLowerCase();
                if (FormatConverter.STYLE_NODE_TYPES.contains(prevType)) {
                    prevType = "text";
                } else if (DefaultWysiwygConverter.isHeading(prevType)) {
                    prevType = "heading";
                } else if (DefaultWysiwygConverter.isEmoticon(previous, prevType)) {
                    prevType = "emoticon";
                }
            }
        }
        String debugStr1 = "";
        String debugStr2 = "";
        if (debug) {
            debugStr1 = "[" + prevType + "-" + current;
            debugStr2 = nodeContext.isInTable() + "," + nodeContext.isInListItem() + "]";
        }
        Separation separation = TypeBasedSeparation.getSeparation(prevType, current);
        if (nodeContext.isInHeading()) {
            sep = separation.getSeparator();
            if (sep != null) {
                sep = sep.replace("\n", "");
            }
        } else {
            sep = nodeContext.isInTable() ? separation.getTableSeparator() : (nodeContext.isInListItem() ? separation.getListSeparator() : separation.getSeparator());
        }
        if (sep == null) {
            return debugStr1 + debugStr2;
        }
        return debugStr1 + sep + debugStr2;
    }

    @Override
    public String getSep(Node previous, String current, boolean inTable, boolean inList) {
        NodeContext.Builder contextBuilder = new NodeContext.Builder(previous).inTable(inTable).inListItem(inList);
        return this.getSeparator(current, contextBuilder.build());
    }

    private static boolean isEmoticon(Node node, String nodeName) {
        return nodeName.equals("img") && NodeUtil.attributeContains(node, "src", "/images/icons/emoticons/");
    }

    @Override
    public String convertChildren(NodeContext nodeContext) {
        StringBuffer wikiText = new StringBuffer();
        if (nodeContext.getNode() != null && nodeContext.getNode().getChildNodes() != null) {
            NodeContext childContext = nodeContext.getFirstChildNodeContextPreservingPreviousSibling();
            while (childContext != null) {
                String converted = this.convertNode(childContext);
                if (StringUtils.isNotEmpty((String)converted)) {
                    wikiText.append(converted);
                    childContext = nodeContext.getNodeContextForNextChild(childContext);
                    continue;
                }
                childContext = nodeContext.getNodeContextForNextChildPreservingPreviousSibling(childContext);
            }
        }
        return wikiText.toString();
    }

    @Override
    public String convertChildren(Node node, Styles styles, ListContext listContext, boolean inTable, boolean inListItem, boolean ignoreText, boolean escapeWikiMarkup, Node previousSibling) {
        NodeContext.Builder contextBuilder = new NodeContext.Builder(node);
        contextBuilder.previousSibling(previousSibling);
        contextBuilder.styles(new Styles(node, styles));
        contextBuilder.listContext(listContext);
        contextBuilder.inTable(inTable).inListItem(inListItem).ignoreText(ignoreText).escapeWikiMarkup(escapeWikiMarkup);
        return this.convertChildren(contextBuilder.build());
    }

    @Override
    public String convertNode(NodeContext nodeContext) {
        for (Converter converter : CONVERTERS) {
            if (!converter.canConvert(nodeContext)) continue;
            String converted = converter.convertNode(nodeContext, this);
            return converted == null ? "" : converted;
        }
        return DefaultWysiwygConverter.getRawChildText(nodeContext.getNode(), true);
    }

    public static boolean isUserNewline(Node node) {
        return node != null && node.getNodeName() != null && node.getNodeName().toLowerCase().equals("p") && node.getAttributes() != null && node.getAttributes().getNamedItem("user") != null && DefaultWysiwygConverter.containsNoUserContent(node);
    }

    private static boolean containsNoUserContent(Node node) {
        String rawText = DefaultWysiwygConverter.getRawChildTextWithoutReplacement(node);
        return StringUtils.isBlank((String)rawText) || rawText.trim().matches(MATCH_NON_BREAKING_SPACES);
    }

    WysiwygNodeConverter findNodeConverter(String converterName) {
        if (converterName.startsWith("macro:")) {
            String[] parts = converterName.split(":");
            if (parts.length != 2) {
                throw new RuntimeException("Illegal node converter name:'" + converterName + "'");
            }
            Macro m = this.macroManager.getEnabledMacro(parts[1]);
            if (m instanceof ResourceAwareMacroDecorator) {
                m = ((ResourceAwareMacroDecorator)m).getMacro();
            }
            if (!(m instanceof WysiwygNodeConverter)) {
                throw new RuntimeException("Macro '" + parts[1] + "' implemented by " + m.getClass() + " does not implement WysiwygNodeConverter.");
            }
            return (WysiwygNodeConverter)((Object)m);
        }
        throw new RuntimeException("Unrecognized node converter name:'" + converterName + "'");
    }

    static boolean isHeading(String name) {
        return name.startsWith("h") && name.length() == 2 && Character.isDigit(name.charAt(1));
    }

    static boolean isForcedNewline(Node node) {
        return node != null && node.getNodeName() != null && node.getNodeName().toLowerCase().equals("br") && node.getAttributes() != null && "atl-forced-newline".equals(NodeUtil.getAttribute(node, "class"));
    }

    public static String getRawChildText(Node node, boolean stripNewlines) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < node.getChildNodes().getLength(); ++i) {
            Node n = node.getChildNodes().item(i);
            if (NodeUtil.isTextNode(n)) {
                String s = n.getNodeValue();
                if (stripNewlines) {
                    s = s.replaceAll("(\n|\r)", " ").trim();
                }
                sb.append(s);
            } else if (DefaultWysiwygConverter.getNodeName(n).equals("br")) {
                sb.append("\n");
            }
            sb.append(DefaultWysiwygConverter.getRawChildText(n, stripNewlines));
            if (!DefaultWysiwygConverter.getNodeName(n).equals("p")) continue;
            sb.append("\n");
        }
        return sb.toString();
    }

    public static String getRawChildTextWithoutReplacement(Node node) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < node.getChildNodes().getLength(); ++i) {
            Node n = node.getChildNodes().item(i);
            if (NodeUtil.isTextNode(n)) {
                String s = n.getNodeValue();
                sb.append(s);
            }
            sb.append(DefaultWysiwygConverter.getRawChildTextWithoutReplacement(n));
        }
        return sb.toString();
    }

    private static String getNodeName(Node node) {
        return node.getNodeName().toLowerCase();
    }

    public void setIconManager(IconManager iconManager) {
        this.iconManager = iconManager;
    }

    @Override
    public String getMacroInfoHtml(RenderContext context, String name, int xOffset, int yOffset) {
        return "<img alt=\"" + name + "\" style=\"float:left;margin-right:-32;opacity:0.75;position:relative;left:" + xOffset + "px;top:" + yOffset + "px;\" src=\"" + context.getSiteRoot() + "/includes/js/editor/plugins/confluence/info.png\"/>";
    }

    @Override
    public String convertXHtmlToWikiMarkup(String xhtml) {
        if (!TextUtils.stringSet((String)xhtml)) {
            return "";
        }
        try {
            xhtml = RenderUtils.stripCarriageReturns(xhtml);
            DOMFragmentParser parser = new DOMFragmentParser();
            HTMLDocumentImpl document = new HTMLDocumentImpl();
            DocumentFragment fragment = document.createDocumentFragment();
            xhtml = xhtml.replaceAll("<\\?xml.*?/>", "");
            InputSource inputSource = new InputSource(new StringReader(xhtml));
            try {
                parser.setFeature("http://cyberneko.org/html/features/balance-tags/document-fragment", true);
                if (debug) {
                    parser.setProperty("http://cyberneko.org/html/properties/filters", (Object)new XMLDocumentFilter[]{new Writer()});
                }
                parser.parse(inputSource, fragment);
            }
            catch (SAXException e) {
                throw new RuntimeException(e);
            }
            StringBuffer wikiText = new StringBuffer();
            NodeContext nodeContext = new NodeContext.Builder(fragment).build();
            wikiText.append(this.convertNode(nodeContext));
            if (debug) {
                return wikiText.toString();
            }
            String s = wikiText.toString().replaceAll("[\\s&&[^\n]]*\n", "\n").trim();
            s = s.replaceAll(" (TEXTSEP)+", " ");
            s = s.replaceAll("\n(TEXTSEP)+", "\n");
            s = s.replaceAll("^(TEXTSEP)+", "");
            s = s.replaceAll("\\[(TEXTSEP)+", "[");
            s = s.replaceAll("(TEXTSEP)+ ", " ");
            s = s.replaceAll("(TEXTSEP)+", " ");
            s = s.replaceAll(" \n", "\n");
            s = s.replaceAll("(?<![}\\p{L}\\p{Nd}\\\\])\\{((?:\\?\\?)|(?:\\*)|(?:\\^)|(?:~)|(?:_)|(?:-)|(?:\\+)|(?:\\{\\{)|(?:\\}\\}))\\}", "$1");
            s = s.replaceAll("\\{((?:\\?\\?)|(?:\\*)|(?:\\^)|(?:~)|(?:_)|(?:-)|(?:\\+)|(?:\\{\\{)|(?:\\}\\}))\\}(?![{\\p{L}\\p{Nd}])", "$1");
            return s;
        }
        catch (FactoryConfigurationError e) {
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String convertWikiMarkupToXHtml(RenderContext ctx, String wikiMarkup) {
        ctx.setRenderingForWysiwyg(true);
        wikiMarkup = RenderUtils.stripCarriageReturns(wikiMarkup);
        String s = this.renderer.convertWikiToXHtml(ctx, wikiMarkup);
        s = s.replaceAll("<p class=\"atl_conf_pad\"> </p>\\s*<p", "<p");
        return s;
    }

    @Override
    public String getAttribute(Node node, String name) {
        return NodeUtil.getAttribute(node, name);
    }

    protected List<com.atlassian.renderer.v2.components.TextConverter> getTextConverterComponents() {
        return this.textConverterComponents;
    }

    MacroManager getMacroManager() {
        return this.macroManager;
    }

    IconManager getIconManager() {
        return this.iconManager;
    }
}

