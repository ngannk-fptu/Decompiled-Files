/*
 * Decompiled with CFR 0.152.
 */
package groovy.ui.text;

import groovy.ui.text.AutoIndentAction;
import groovy.ui.text.FindReplaceUtility;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.Calendar;
import java.util.regex.Pattern;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.Utilities;

public class TextEditor
extends JTextPane
implements Pageable,
Printable {
    public static final String FIND = "Find...";
    public static final String FIND_NEXT = "Find Next";
    public static final String FIND_PREVIOUS = "Find Previous";
    public static final String REPLACE = "Replace...";
    public static final String AUTO_INDENT = "AutoIndent";
    private static final String TABBED_SPACES = "    ";
    private static final Pattern TAB_BACK_PATTERN = Pattern.compile("^(([\t])|(    )|(   )|(  )|( ))", 8);
    private static final Pattern LINE_START = Pattern.compile("^", 8);
    private static final JTextPane PRINT_PANE = new JTextPane();
    private static final Dimension PRINT_SIZE = new Dimension();
    private static boolean isOvertypeMode;
    private Caret defaultCaret;
    private Caret overtypeCaret;
    private static final PageFormat PAGE_FORMAT;
    private int numPages;
    private MouseAdapter mouseAdapter = new MouseAdapter(){
        Cursor cursor;

        @Override
        public void mouseEntered(MouseEvent me) {
            if (TextEditor.this.contains(me.getPoint())) {
                this.cursor = TextEditor.this.getCursor();
                Cursor curs = Cursor.getPredefinedCursor(2);
                TextEditor.this.getRootPane().getLayeredPane().setCursor(curs);
            } else {
                TextEditor.this.getRootPane().getLayeredPane().setCursor(this.cursor);
            }
        }

        @Override
        public void mouseExited(MouseEvent me) {
            TextEditor.this.getRootPane().getLayeredPane().setCursor(null);
        }
    };
    private boolean unwrapped;
    private boolean tabsAsSpaces;
    private boolean multiLineTab;

    public TextEditor() {
        this(false);
    }

    public TextEditor(boolean tabsAsSpaces) {
        this(tabsAsSpaces, false);
    }

    public TextEditor(boolean tabsAsSpaces, boolean multiLineTab) {
        this(multiLineTab, tabsAsSpaces, false);
    }

    public TextEditor(boolean tabsAsSpaces, boolean multiLineTab, boolean unwrapped) {
        this.tabsAsSpaces = tabsAsSpaces;
        this.multiLineTab = multiLineTab;
        this.unwrapped = unwrapped;
        ActionMap aMap = this.getActionMap();
        Action action = null;
        do {
            action = action == null ? aMap.get("delete-previous") : null;
            aMap.remove("delete-previous");
        } while ((aMap = aMap.getParent()) != null);
        aMap = this.getActionMap();
        InputMap iMap = this.getInputMap();
        KeyStroke keyStroke = KeyStroke.getKeyStroke(8, 0, false);
        iMap.put(keyStroke, "delete");
        keyStroke = KeyStroke.getKeyStroke(8, 1, false);
        iMap.put(keyStroke, "delete");
        aMap.put("delete", action);
        action = new FindAction();
        aMap.put(FIND, action);
        keyStroke = KeyStroke.getKeyStroke(70, 2, false);
        iMap.put(keyStroke, FIND);
        aMap.put(FIND_NEXT, FindReplaceUtility.FIND_ACTION);
        keyStroke = KeyStroke.getKeyStroke(114, 0, false);
        iMap.put(keyStroke, FIND_NEXT);
        aMap.put(FIND_PREVIOUS, FindReplaceUtility.FIND_ACTION);
        keyStroke = KeyStroke.getKeyStroke(114, 1, false);
        iMap.put(keyStroke, FIND_PREVIOUS);
        action = new TabAction();
        aMap.put("TextEditor-tabAction", action);
        keyStroke = KeyStroke.getKeyStroke(9, 0, false);
        iMap.put(keyStroke, "TextEditor-tabAction");
        action = new ShiftTabAction();
        aMap.put("TextEditor-shiftTabAction", action);
        keyStroke = KeyStroke.getKeyStroke(9, 1, false);
        iMap.put(keyStroke, "TextEditor-shiftTabAction");
        action = new ReplaceAction();
        this.getActionMap().put(REPLACE, action);
        keyStroke = KeyStroke.getKeyStroke(72, 2, false);
        do {
            iMap.remove(keyStroke);
        } while ((iMap = iMap.getParent()) != null);
        this.getInputMap().put(keyStroke, REPLACE);
        action = new AutoIndentAction();
        this.getActionMap().put(AUTO_INDENT, action);
        keyStroke = KeyStroke.getKeyStroke(10, 0, false);
        this.getInputMap().put(keyStroke, AUTO_INDENT);
        this.setAutoscrolls(true);
        this.defaultCaret = this.getCaret();
        this.overtypeCaret = new OvertypeCaret();
        this.overtypeCaret.setBlinkRate(this.defaultCaret.getBlinkRate());
    }

    @Override
    public void addNotify() {
        super.addNotify();
        this.addMouseListener(this.mouseAdapter);
        FindReplaceUtility.registerTextComponent(this);
    }

    @Override
    public int getNumberOfPages() {
        Paper paper = PAGE_FORMAT.getPaper();
        this.numPages = (int)Math.ceil(this.getSize().getHeight() / paper.getImageableHeight());
        return this.numPages;
    }

    @Override
    public PageFormat getPageFormat(int pageIndex) throws IndexOutOfBoundsException {
        return PAGE_FORMAT;
    }

    @Override
    public Printable getPrintable(int param) throws IndexOutOfBoundsException {
        return this;
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int page) throws PrinterException {
        if (page < this.numPages) {
            Paper paper = pageFormat.getPaper();
            PRINT_PANE.setDocument(this.getDocument());
            PRINT_PANE.setFont(this.getFont());
            PRINT_SIZE.setSize(paper.getImageableWidth(), this.getSize().getHeight());
            PRINT_PANE.setSize(PRINT_SIZE);
            double y = -((double)page * paper.getImageableHeight()) + paper.getImageableY();
            ((Graphics2D)graphics).translate(paper.getImageableX(), y);
            PRINT_PANE.print(graphics);
            ((Graphics2D)graphics).translate(0.0, -y);
            Rectangle rect = graphics.getClipBounds();
            graphics.setClip(rect.x, 0, rect.width, (int)paper.getHeight() + 100);
            Calendar cal = Calendar.getInstance();
            String header = cal.getTime().toString().trim();
            String name = this.getName() == null ? System.getProperty("user.name").trim() : this.getName().trim();
            String pageStr = String.valueOf(page + 1);
            Font font = Font.decode("Monospaced 8");
            graphics.setFont(font);
            FontMetrics fm = graphics.getFontMetrics(font);
            int width = SwingUtilities.computeStringWidth(fm, header);
            ((Graphics2D)graphics).drawString(header, (float)(paper.getImageableWidth() / 2.0 - (double)(width / 2)), (float)paper.getImageableY() / 2.0f + (float)fm.getHeight());
            ((Graphics2D)graphics).translate(0.0, paper.getImageableY() - (double)fm.getHeight());
            double height = paper.getImageableHeight() + paper.getImageableY() / 2.0;
            width = SwingUtilities.computeStringWidth(fm, name);
            ((Graphics2D)graphics).drawString(name, (float)(paper.getImageableWidth() / 2.0 - (double)(width / 2)), (float)height - (float)(fm.getHeight() / 2));
            ((Graphics2D)graphics).translate(0, fm.getHeight());
            width = SwingUtilities.computeStringWidth(fm, pageStr);
            ((Graphics2D)graphics).drawString(pageStr, (float)(paper.getImageableWidth() / 2.0 - (double)(width / 2)), (float)height - (float)(fm.getHeight() / 2));
            return 0;
        }
        return 1;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        boolean bool = super.getScrollableTracksViewportWidth();
        if (this.unwrapped) {
            Container parent = this.getParent();
            TextUI ui = this.getUI();
            int uiWidth = ui.getPreferredSize((JComponent)this).width;
            bool = parent == null || uiWidth < parent.getSize().width;
        }
        return bool;
    }

    public boolean isMultiLineTabbed() {
        return this.multiLineTab;
    }

    public static boolean isOvertypeMode() {
        return isOvertypeMode;
    }

    public boolean isTabsAsSpaces() {
        return this.tabsAsSpaces;
    }

    public boolean isUnwrapped() {
        return this.unwrapped;
    }

    @Override
    protected void processKeyEvent(KeyEvent e) {
        super.processKeyEvent(e);
        if (e.getID() == 402 && e.getKeyCode() == 155 && e.getModifiersEx() == 0) {
            this.setOvertypeMode(!TextEditor.isOvertypeMode());
        }
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        this.removeMouseListener(this.mouseAdapter);
        FindReplaceUtility.unregisterTextComponent(this);
    }

    @Override
    public void replaceSelection(String text) {
        if (TextEditor.isOvertypeMode()) {
            int pos = this.getCaretPosition();
            if (this.getSelectedText() == null && pos < this.getDocument().getLength()) {
                this.moveCaretPosition(pos + 1);
            }
        }
        super.replaceSelection(text);
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        if (this.unwrapped) {
            Dimension size = this.getPreferredSize();
            super.setBounds(x, y, Math.max(size.width, width), Math.max(size.height, height));
        } else {
            super.setBounds(x, y, width, height);
        }
    }

    public void isMultiLineTabbed(boolean multiLineTab) {
        this.multiLineTab = multiLineTab;
    }

    public void isTabsAsSpaces(boolean tabsAsSpaces) {
        this.tabsAsSpaces = tabsAsSpaces;
    }

    public void setOvertypeMode(boolean isOvertypeMode) {
        TextEditor.isOvertypeMode = isOvertypeMode;
        int pos = this.getCaretPosition();
        this.setCaret(TextEditor.isOvertypeMode() ? this.overtypeCaret : this.defaultCaret);
        this.setCaretPosition(pos);
    }

    public void setUnwrapped(boolean unwrapped) {
        this.unwrapped = unwrapped;
    }

    static {
        PrinterJob job = PrinterJob.getPrinterJob();
        PAGE_FORMAT = job.defaultPage();
    }

    private static class OvertypeCaret
    extends DefaultCaret {
        private OvertypeCaret() {
        }

        @Override
        public void paint(Graphics g) {
            if (this.isVisible()) {
                try {
                    JTextComponent component = this.getComponent();
                    Rectangle r = component.getUI().modelToView(component, this.getDot());
                    Color c = g.getColor();
                    g.setColor(component.getBackground());
                    g.setXORMode(component.getCaretColor());
                    r.setBounds(r.x, r.y, g.getFontMetrics().charWidth('w'), g.getFontMetrics().getHeight());
                    g.fillRect(r.x, r.y, r.width, r.height);
                    g.setPaintMode();
                    g.setColor(c);
                }
                catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected synchronized void damage(Rectangle r) {
            if (r != null) {
                JTextComponent component = this.getComponent();
                this.x = r.x;
                this.y = r.y;
                Font font = component.getFont();
                this.width = component.getFontMetrics(font).charWidth('w');
                this.height = r.height;
                this.repaint();
            }
        }
    }

    private class TabAction
    extends AbstractAction {
        private TabAction() {
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            try {
                String text;
                Document doc = TextEditor.this.getDocument();
                String string = text = TextEditor.this.tabsAsSpaces ? TextEditor.TABBED_SPACES : "\t";
                if (TextEditor.this.multiLineTab && TextEditor.this.getSelectedText() != null) {
                    int end = Utilities.getRowEnd(TextEditor.this, TextEditor.this.getSelectionEnd());
                    TextEditor.this.setSelectionEnd(end);
                    Element el = Utilities.getParagraphElement(TextEditor.this, TextEditor.this.getSelectionStart());
                    int start = el.getStartOffset();
                    TextEditor.this.setSelectionStart(start);
                    String toReplace = TextEditor.this.getSelectedText();
                    toReplace = LINE_START.matcher(toReplace).replaceAll(text);
                    TextEditor.this.replaceSelection(toReplace);
                    TextEditor.this.select(start, start + toReplace.length());
                } else {
                    int pos = TextEditor.this.getCaretPosition();
                    doc.insertString(pos, text, null);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class ShiftTabAction
    extends AbstractAction {
        private ShiftTabAction() {
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            try {
                if (TextEditor.this.multiLineTab && TextEditor.this.getSelectedText() != null) {
                    int end = Utilities.getRowEnd(TextEditor.this, TextEditor.this.getSelectionEnd());
                    TextEditor.this.setSelectionEnd(end);
                    Element el = Utilities.getParagraphElement(TextEditor.this, TextEditor.this.getSelectionStart());
                    int start = el.getStartOffset();
                    TextEditor.this.setSelectionStart(start);
                    String text = TextEditor.this.tabsAsSpaces ? TAB_BACK_PATTERN.matcher(TextEditor.this.getSelectedText()).replaceAll("") : TextEditor.this.getSelectedText().replaceAll("^\t", "");
                    TextEditor.this.replaceSelection(text);
                    TextEditor.this.select(start, start + text.length());
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class ReplaceAction
    extends AbstractAction {
        private ReplaceAction() {
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            FindReplaceUtility.showDialog(true);
        }
    }

    private static class FindAction
    extends AbstractAction {
        private FindAction() {
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            FindReplaceUtility.showDialog();
        }
    }
}

