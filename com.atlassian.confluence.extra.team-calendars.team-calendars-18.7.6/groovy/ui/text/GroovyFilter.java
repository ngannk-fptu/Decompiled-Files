/*
 * Decompiled with CFR 0.152.
 */
package groovy.ui.text;

import groovy.ui.text.StructuredSyntaxDocumentFilter;
import java.awt.Color;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.Segment;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class GroovyFilter
extends StructuredSyntaxDocumentFilter {
    private static final Action AUTO_TAB_ACTION = new AutoTabAction();
    public static final String COMMENT = "comment";
    public static final String SLASH_STAR_COMMENT = "/\\*(?s:.)*?(?:\\*/|\\z)";
    public static final String SLASH_SLASH_COMMENT = "//.*";
    public static final String QUOTES = "(?ms:\"{3}.*?(?:\"{3}|\\z))|(?:\"{1}.*?(?:\"|\\Z))";
    public static final String SINGLE_QUOTES = "(?ms:'{3}(?!'{1,3}).*?(?:'{3}|\\z))|(?:'{1}.*?(?:'|\\z))";
    public static final String SLASHY_QUOTES = "(?:/[^/*].*?(?<!\\\\)/|(?ms:\\$/.*?(?:/\\$|\\z)))";
    public static final String DIGIT = "DIGIT";
    public static final String DECIMAL_INTEGER_LITERAL = "(?:0|[1-9](?:[_0-9]*[0-9])?)[lL]?";
    public static final String HEX_INTEGER_LITERAL = "0[xX][0-9a-fA-F](?:[0-9a-fA-F_]*[0-9a-fA-F])?";
    public static final String OCTAL_INTEGER_LITERAL = "0[0-7](?:[_0-7]*[0-7])?";
    public static final String BINARY_INTEGER_LITERAL = "0[bB][01](?:[_01]*[01])?";
    public static final String DECIMAL_FLOATING_POINT_LITERAL = "(?:0|[1-9](?:[_0-9]*[0-9])?)?\\.?[0-9](?:[_0-9]*[0-9])?(?:[eE][+-]?[0-9]+(?:[_0-9]*[0-9])?)?[fFdD]?";
    public static final String HEXADECIMAL_FLOATING_POINT_LITERAL = "0[xX](?:[0-9a-fA-F](?:[0-9a-fA-F_]*[0-9a-fA-F])?)?\\.?(?:[0-9a-fA-F_]*[0-9a-fA-F])?(?:[pP][+-]?[0-9]+(?:[_0-9]*[0-9])?)?[fFdD]?";
    public static final String IDENT = "[\\w\\$&&[\\D]][\\w\\$]*";
    public static final String OPERATION = "[\\w\\$&&[\\D]][\\w\\$]* *\\(";
    public static final String LEFT_PARENS = "\\(";
    private static final Color COMMENT_COLOR = Color.LIGHT_GRAY.darker().darker();
    public static final String RESERVED_WORD = "reserved";
    public static final String[] RESERVED_WORDS = new String[]{"\\babstract\\b", "\\bassert\\b", "\\bdefault\\b", "\\bif\\b", "\\bprivate\\b", "\\bthis\\b", "\\bboolean\\b", "\\bdo\\b", "\\bimplements\\b", "\\bprotected\\b", "\\bthrow\\b", "\\bbreak\\b", "\\bdouble\\b", "\\bimport\\b", "\\bpublic\\b", "\\bthrows\\b", "\\bbyte\\b", "\\belse\\b", "\\binstanceof\\b", "\\breturn\\b", "\\btransient\\b", "\\bcase\\b", "\\bextends\\b", "\\bint\\b", "\\bshort\\b", "\\btry\\b", "\\bcatch\\b", "\\bfinal\\b", "\\binterface\\b", "\\benum\\b", "\\bstatic\\b", "\\bvoid\\b", "\\bchar\\b", "\\bfinally\\b", "\\blong\\b", "\\bstrictfp\\b", "\\bvolatile\\b", "\\bclass\\b", "\\bfloat\\b", "\\bnative\\b", "\\bsuper\\b", "\\bwhile\\b", "\\bconst\\b", "\\bfor\\b", "\\bnew\\b", "\\bswitch\\b", "\\bcontinue\\b", "\\bgoto\\b", "\\bpackage\\b", "\\bdef\\b", "\\bas\\b", "\\bin\\b", "\\bsynchronized\\b", "\\bnull\\b", "\\btrait\\b"};

    public GroovyFilter(DefaultStyledDocument doc) {
        super(doc);
        this.init();
    }

    private void init() {
        StyleContext styleContext = StyleContext.getDefaultStyleContext();
        Style defaultStyle = styleContext.getStyle("default");
        Style comment = styleContext.addStyle(COMMENT, defaultStyle);
        StyleConstants.setForeground(comment, COMMENT_COLOR);
        StyleConstants.setItalic(comment, true);
        Style quotes = styleContext.addStyle(QUOTES, defaultStyle);
        StyleConstants.setForeground(quotes, Color.MAGENTA.darker().darker());
        Style charQuotes = styleContext.addStyle(SINGLE_QUOTES, defaultStyle);
        StyleConstants.setForeground(charQuotes, Color.GREEN.darker().darker());
        Style slashyQuotes = styleContext.addStyle(SLASHY_QUOTES, defaultStyle);
        StyleConstants.setForeground(slashyQuotes, Color.ORANGE.darker());
        Style digit = styleContext.addStyle(DIGIT, defaultStyle);
        StyleConstants.setForeground(digit, Color.RED.darker());
        Style operation = styleContext.addStyle(OPERATION, defaultStyle);
        StyleConstants.setBold(operation, true);
        Style ident = styleContext.addStyle(IDENT, defaultStyle);
        Style reservedWords = styleContext.addStyle(RESERVED_WORD, defaultStyle);
        StyleConstants.setBold(reservedWords, true);
        StyleConstants.setForeground(reservedWords, Color.BLUE.darker().darker());
        Style leftParens = styleContext.addStyle(IDENT, defaultStyle);
        this.getRootNode().putStyle(SLASH_STAR_COMMENT, comment);
        this.getRootNode().putStyle(SLASH_SLASH_COMMENT, comment);
        this.getRootNode().putStyle(QUOTES, quotes);
        this.getRootNode().putStyle(SINGLE_QUOTES, charQuotes);
        this.getRootNode().putStyle(SLASHY_QUOTES, slashyQuotes);
        this.getRootNode().putStyle(new String[]{HEX_INTEGER_LITERAL, OCTAL_INTEGER_LITERAL, BINARY_INTEGER_LITERAL, DECIMAL_FLOATING_POINT_LITERAL, HEXADECIMAL_FLOATING_POINT_LITERAL, DECIMAL_INTEGER_LITERAL}, digit);
        this.getRootNode().putStyle(OPERATION, operation);
        StructuredSyntaxDocumentFilter.LexerNode node = this.createLexerNode();
        node.putStyle(RESERVED_WORDS, reservedWords);
        node.putStyle(LEFT_PARENS, leftParens);
        this.getRootNode().putChild(OPERATION, node);
        this.getRootNode().putStyle(IDENT, ident);
        node = this.createLexerNode();
        node.putStyle(RESERVED_WORDS, reservedWords);
        this.getRootNode().putChild(IDENT, node);
    }

    public static void installAutoTabAction(JTextComponent tComp) {
        tComp.getActionMap().put("GroovyFilter-autoTab", AUTO_TAB_ACTION);
        KeyStroke keyStroke = KeyStroke.getKeyStroke(10, 0, false);
        tComp.getInputMap().put(keyStroke, "GroovyFilter-autoTab");
    }

    private static class AutoTabAction
    extends AbstractAction {
        private StyledDocument doc;
        private final Segment segment = new Segment();
        private final StringBuilder buffer = new StringBuilder();

        private AutoTabAction() {
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            JTextComponent tComp = (JTextComponent)ae.getSource();
            if (tComp.getDocument() instanceof StyledDocument) {
                this.doc = (StyledDocument)tComp.getDocument();
                try {
                    this.doc.getText(0, this.doc.getLength(), this.segment);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                int offset = tComp.getCaretPosition();
                int index = this.findTabLocation(offset);
                this.buffer.delete(0, this.buffer.length());
                this.buffer.append('\n');
                if (index > -1) {
                    for (int i = 0; i < index + 4; ++i) {
                        this.buffer.append(' ');
                    }
                }
                try {
                    this.doc.insertString(offset, this.buffer.toString(), this.doc.getDefaultRootElement().getAttributes());
                }
                catch (BadLocationException ble) {
                    ble.printStackTrace();
                }
            }
        }

        public int findTabLocation(int offset) {
            int index;
            boolean cont = true;
            while (offset > -1 && cont) {
                Element el = this.doc.getCharacterElement(offset);
                Object color = el.getAttributes().getAttribute(StyleConstants.Foreground);
                if (!COMMENT_COLOR.equals(color)) {
                    cont = this.segment.array[offset] != '{' && this.segment.array[offset] != '}';
                }
                offset -= cont ? 1 : 0;
            }
            if (offset > -1 && this.segment.array[offset] == '{') {
                while (offset > -1 && !Character.isWhitespace(this.segment.array[offset--])) {
                }
            }
            int n = index = offset < 0 || this.segment.array[offset] == '}' ? -4 : 0;
            if (offset > -1) {
                Element top = this.doc.getDefaultRootElement();
                offset = top.getElement(top.getElementIndex(offset)).getStartOffset();
                while (Character.isWhitespace(this.segment.array[offset++])) {
                    ++index;
                }
            }
            return index;
        }
    }
}

