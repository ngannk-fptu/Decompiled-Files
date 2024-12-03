/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.swing;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.ranges.DocumentRange;
import org.w3c.dom.ranges.Range;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.render.Box;
import org.xhtmlrenderer.render.InlineLayoutBox;
import org.xhtmlrenderer.render.InlineText;
import org.xhtmlrenderer.simple.XHTMLPanel;
import org.xhtmlrenderer.swing.ScalableXHTMLPanel;
import org.xhtmlrenderer.util.Util;
import org.xhtmlrenderer.util.XRLog;

public class SelectionHighlighter
implements MouseMotionListener,
MouseListener {
    private static final String PARA_EQUIV = "&!<p2equiv!";
    private XHTMLPanel panel;
    private ViewModelInfo dotInfo;
    private ViewModelInfo markInfo;
    protected EventListenerList listenerList = new EventListenerList();
    protected transient ChangeEvent changeEvent = null;
    private DocumentRange docRange;
    private Range lastSelectionRange = null;
    private DocumentTraversal docTraversal;
    private Map elementBoxMap;
    private Map textInlineMap;
    private String lastHighlightedString = "";
    private TransferHandler handler;
    private Document document;
    public static final String copyAction = "Copy";

    public void addChangeListener(ChangeListener l) {
        this.listenerList.add(ChangeListener.class, l);
    }

    public void removeChangeListener(ChangeListener l) {
        this.listenerList.remove(ChangeListener.class, l);
    }

    protected void fireStateChanged() {
        Object[] listeners = this.listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] != ChangeListener.class) continue;
            if (this.changeEvent == null) {
                this.changeEvent = new ChangeEvent(this);
            }
            ((ChangeListener)listeners[i + 1]).stateChanged(this.changeEvent);
        }
    }

    public void install(XHTMLPanel panel) {
        this.panel = panel;
        if (!this.checkDocument()) {
            return;
        }
        panel.setTransferHandler(this.handler);
        panel.addMouseListener(this);
        panel.addMouseMotionListener(this);
    }

    public void deinstall(XHTMLPanel panel) {
        if (panel.getTransferHandler() == this.handler) {
            panel.setTransferHandler(null);
        }
        panel.removeMouseListener(this);
        panel.removeMouseMotionListener(this);
    }

    private boolean checkDocument() {
        if (this.document != this.panel.getDocument() || this.textInlineMap == null) {
            this.document = this.panel.getDocument();
            this.textInlineMap = null;
            this.dotInfo = null;
            this.markInfo = null;
            this.lastSelectionRange = null;
            try {
                this.docRange = (DocumentRange)((Object)this.panel.getDocument());
                this.docTraversal = (DocumentTraversal)((Object)this.panel.getDocument());
                if (this.document != null && this.createMaps()) {
                    return true;
                }
                try {
                    Thread.sleep(10L);
                }
                catch (InterruptedException e) {
                    return false;
                }
            }
            catch (ClassCastException cce) {
                XRLog.layout(Level.WARNING, "Document instance cannot create ranges: no selection possible");
                return false;
            }
        }
        return true;
    }

    public void setDot(ViewModelInfo pos) {
        this.dotInfo = pos;
        this.markInfo = pos;
        this.fireStateChanged();
        this.updateHighlights();
        this.updateSystemSelection();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (!e.isConsumed() && SwingUtilities.isLeftMouseButton(e)) {
            this.moveCaret(this.convertMouseEventToScale(e));
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int nclicks = e.getClickCount();
        if (SwingUtilities.isLeftMouseButton(e) && !e.isConsumed()) {
            this.adjustCaretAndFocus(e);
            MouseEvent newE = this.convertMouseEventToScale(e);
            this.adjustCaretAndFocus(newE);
            if (nclicks == 2) {
                this.selectWord(newE);
            }
        }
    }

    void adjustCaretAndFocus(MouseEvent e) {
        this.adjustCaret(e);
        this.adjustFocus(false);
    }

    private void adjustCaret(MouseEvent e) {
        if ((e.getModifiers() & 1) != 0 && this.dotInfo != null) {
            this.moveCaret(e);
        } else {
            this.positionCaret(e);
        }
    }

    private void positionCaret(MouseEvent e) {
        ViewModelInfo pos = this.infoFromPoint(e);
        if (pos != null) {
            this.setDot(pos);
        }
    }

    private void adjustFocus(boolean inWindow) {
        if (this.panel != null && this.panel.isEnabled() && this.panel.isRequestFocusEnabled()) {
            if (inWindow) {
                this.panel.requestFocusInWindow();
            } else {
                this.panel.requestFocus();
            }
        }
    }

    private void selectWord(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    public XHTMLPanel getComponent() {
        return this.panel;
    }

    protected void moveCaret(MouseEvent e) {
        ViewModelInfo pos = this.infoFromPoint(e);
        if (pos != null) {
            this.moveDot(pos);
        }
    }

    public void selectAll() {
        Node n;
        if (this.getComponent() == null || this.getComponent().getWidth() == 0 || this.getComponent().getHeight() == 0) {
            return;
        }
        this.checkDocument();
        NodeIterator nodeIterator = this.docTraversal.createNodeIterator(this.document.getDocumentElement(), 4, null, false);
        Text firstText = null;
        Text lastText = null;
        while ((n = nodeIterator.nextNode()) != null) {
            if (!this.textInlineMap.containsKey(n)) continue;
            lastText = (Text)n;
            if (firstText != null) continue;
            firstText = lastText;
        }
        if (firstText == null) {
            return;
        }
        Range r = this.docRange.createRange();
        r.setStart(firstText, 0);
        ViewModelInfo firstPoint = new ViewModelInfo(r, (InlineText)((List)this.textInlineMap.get(firstText)).get(0));
        r = this.docRange.createRange();
        try {
            r.setStart(lastText, lastText.getLength());
        }
        catch (Exception e) {
            r.setStart(lastText, Math.max(0, lastText.getLength() - 1));
        }
        List l = (List)this.textInlineMap.get(firstText);
        ViewModelInfo lastPoint = new ViewModelInfo(r, (InlineText)l.get(l.size() - 1));
        this.setDot(firstPoint);
        this.moveDot(lastPoint);
    }

    public void moveDot(ViewModelInfo pos) {
        this.dotInfo = pos;
        if (this.markInfo == null) {
            this.markInfo = pos;
        }
        this.fireStateChanged();
        this.updateHighlights();
        this.updateSystemSelection();
        InlineText iT = this.dotInfo.text;
        InlineLayoutBox iB = iT.getParent();
        this.adjustVisibility(new Rectangle(iB.getAbsX() + iT.getX(), iB.getAbsY(), 1, iB.getBaseline()));
    }

    private void updateHighlights() {
        ArrayList modified = new ArrayList();
        StringBuffer hlText = new StringBuffer();
        if (this.dotInfo == null) {
            this.getComponent().getRootBox().clearSelection(modified);
            this.getComponent().repaint();
            this.lastHighlightedString = "";
            return;
        }
        Range range = this.getSelectionRange();
        if (this.lastSelectionRange != null && range.compareBoundaryPoints((short)0, this.lastSelectionRange) == 0 && range.compareBoundaryPoints((short)2, this.lastSelectionRange) == 0) {
            return;
        }
        this.lastHighlightedString = "";
        this.lastSelectionRange = range.cloneRange();
        if (range.compareBoundaryPoints((short)1, range) == 0) {
            this.getComponent().getRootBox().clearSelection(modified);
        } else {
            InlineText t2;
            boolean endBeforeStart = this.markInfo.range.compareBoundaryPoints((short)0, this.dotInfo.range) >= 0;
            this.getComponent().getRootBox().clearSelection(modified);
            InlineText t1 = endBeforeStart ? this.dotInfo.text : this.markInfo.text;
            InlineText inlineText = t2 = !endBeforeStart ? this.dotInfo.text : this.markInfo.text;
            if (t1 == null || t2 == null) {
                XRLog.general(Level.FINE, "null text node");
            }
            final Range acceptRange = this.docRange.createRange();
            final Range tr = range;
            NodeFilter f = new NodeFilter(){

                @Override
                public short acceptNode(Node n) {
                    acceptRange.setStart(n, 0);
                    if (tr.getStartContainer() == n) {
                        return 1;
                    }
                    if ((acceptRange.compareBoundaryPoints((short)0, tr) < 0 || acceptRange.compareBoundaryPoints((short)3, tr) > 0) && n != tr.getStartContainer() && n != tr.getEndContainer()) {
                        return 3;
                    }
                    return 1;
                }
            };
            NodeIterator nodeIterator = this.docTraversal.createNodeIterator(range.getCommonAncestorContainer(), 13, f, false);
            boolean lastNodeWasBox = false;
            Node n = nodeIterator.nextNode();
            while (n != null) {
                if (n.getNodeType() == 1) {
                    Box box = this.getBoxForElement((Element)n);
                    if (box instanceof BlockBox && !lastNodeWasBox) {
                        hlText.append(PARA_EQUIV);
                        lastNodeWasBox = true;
                    } else {
                        lastNodeWasBox = false;
                    }
                } else {
                    lastNodeWasBox = false;
                    Text t = (Text)n;
                    List iTs = this.getInlineTextsForText(t);
                    if (iTs != null) {
                        int selTxtSt = t == range.getStartContainer() ? range.getStartOffset() : 0;
                        int selTxtEnd = t == range.getEndContainer() ? range.getEndOffset() : t.getNodeValue().length();
                        hlText.append(t.getNodeValue().substring(selTxtSt, selTxtEnd));
                        for (InlineText iT : iTs) {
                            iT.setSelectionStart((short)Math.max(0, Math.min(selTxtSt, iT.getEnd()) - iT.getStart()));
                            iT.setSelectionEnd((short)Math.max(0, Math.min(iT.getEnd(), selTxtEnd) - iT.getStart()));
                        }
                    }
                }
                n = nodeIterator.nextNode();
            }
        }
        String s = this.normalizeSpaces(hlText.toString());
        this.getComponent().repaint();
        this.lastHighlightedString = Util.replace(s, PARA_EQUIV, "\n\n");
    }

    public String normalizeSpaces(String s) {
        if (s == null) {
            return null;
        }
        StringBuffer buf = new StringBuffer();
        StringCharacterIterator iter = new StringCharacterIterator(s);
        boolean inWhitespace = false;
        char c = iter.first();
        while (c != '\uffff') {
            if (Character.isWhitespace(c)) {
                if (!inWhitespace) {
                    buf.append(' ');
                    inWhitespace = true;
                }
            } else {
                inWhitespace = false;
                buf.append(c);
            }
            c = iter.next();
        }
        return buf.toString();
    }

    private Box getElementContainerBox(InlineText t) {
        Box b = t.getParent();
        while (b.getElement() == null) {
            b = b.getParent();
        }
        return b;
    }

    private boolean createMaps() {
        if (this.panel.getRootBox() == null) {
            return false;
        }
        this.textInlineMap = new LinkedHashMap();
        this.elementBoxMap = new HashMap();
        Stack<Box> s = new Stack<Box>();
        s.push(this.panel.getRootBox());
        while (!s.empty()) {
            Box b = (Box)s.pop();
            Element element = b.getElement();
            if (element != null && !this.elementBoxMap.containsKey(element)) {
                this.elementBoxMap.put(element, b);
            }
            if (b instanceof InlineLayoutBox) {
                InlineLayoutBox ilb = (InlineLayoutBox)b;
                for (Object o : ilb.getInlineChildren()) {
                    if (o instanceof InlineText) {
                        InlineText t = (InlineText)o;
                        Text txt = t.getTextNode();
                        if (!this.textInlineMap.containsKey(txt)) {
                            this.textInlineMap.put(txt, new ArrayList());
                        }
                        ((List)this.textInlineMap.get(txt)).add(t);
                        continue;
                    }
                    s.push((Box)o);
                }
                continue;
            }
            Iterator childIterator = b.getChildIterator();
            while (childIterator.hasNext()) {
                s.push((Box)childIterator.next());
            }
        }
        return true;
    }

    private List getInlineTextsForText(Text t) {
        return (List)this.textInlineMap.get(t);
    }

    private Box getBoxForElement(Element elt) {
        return (Box)this.elementBoxMap.get(elt);
    }

    private void updateSystemSelection() {
        Clipboard clip;
        if (this.dotInfo != this.markInfo && this.panel != null && (clip = this.panel.getToolkit().getSystemSelection()) != null) {
            String selectedText = this.lastHighlightedString;
            try {
                clip.setContents(new StringSelection(selectedText), null);
            }
            catch (IllegalStateException illegalStateException) {
                // empty catch block
            }
        }
    }

    void copy() {
        Clipboard clip;
        if (this.dotInfo != this.markInfo && this.panel != null && (clip = this.panel.getToolkit().getSystemClipboard()) != null) {
            String selectedText = this.lastHighlightedString;
            try {
                clip.setContents(new StringSelection(selectedText), null);
            }
            catch (IllegalStateException illegalStateException) {
                // empty catch block
            }
        }
    }

    List getInlineLayoutBoxes(Box b, boolean ignoreChildElements) {
        Stack<Box> boxes = new Stack<Box>();
        ArrayList<InlineLayoutBox> ilbs = new ArrayList<InlineLayoutBox>();
        boxes.push(b);
        while (!boxes.empty()) {
            b = (Box)boxes.pop();
            if (b instanceof InlineLayoutBox) {
                ilbs.add((InlineLayoutBox)b);
                continue;
            }
            Iterator it = b.getChildIterator();
            while (it.hasNext()) {
                Box child = (Box)it.next();
                if (ignoreChildElements && child.getElement() != null) continue;
                boxes.push(child);
            }
        }
        return ilbs;
    }

    ViewModelInfo infoFromPoint(MouseEvent e) {
        this.checkDocument();
        Range r = this.docRange.createRange();
        InlineText fndTxt = null;
        Box box = this.panel.getRootLayer().find(this.panel.getLayoutContext(), e.getX(), e.getY(), true);
        if (box == null) {
            return null;
        }
        Object elt = null;
        int offset = 0;
        InlineLayoutBox ilb = null;
        boolean containsWholeIlb = false;
        if (box instanceof InlineLayoutBox) {
            ilb = (InlineLayoutBox)box;
        } else {
            while (ilb == null) {
                List ilbs = this.getInlineLayoutBoxes(box, false);
                for (int i = ilbs.size() - 1; i >= 0; --i) {
                    InlineLayoutBox ilbt = (InlineLayoutBox)ilbs.get(i);
                    if (ilbt.getAbsY() > e.getY() || ilbt.getAbsX() > e.getX()) continue;
                    if ((ilb == null || ilbt.getAbsY() > ilb.getAbsY() || ilbt.getAbsY() == ilb.getAbsY() && ilbt.getX() > ilb.getX()) && ilbt.isContainsVisibleContent()) {
                        boolean hasDecentTextNode = false;
                        int x = ilbt.getAbsX();
                        for (Object o : ilbt.getInlineChildren()) {
                            InlineText txt;
                            if (!(o instanceof InlineText) || (txt = (InlineText)o).getTextNode() == null) continue;
                            hasDecentTextNode = true;
                            break;
                        }
                        if (hasDecentTextNode) {
                            ilb = ilbt;
                        }
                    }
                    containsWholeIlb = true;
                }
                if (ilb != null) continue;
                if (box.getParent() == null) {
                    return null;
                }
                box = box.getParent();
            }
        }
        int x = ilb.getAbsX();
        InlineText lastItxt = null;
        for (Object o : ilb.getInlineChildren()) {
            if (!(o instanceof InlineText)) continue;
            InlineText txt = (InlineText)o;
            if (txt.getTextNode() != null) {
                if (e.getX() >= x + txt.getX() && e.getX() < x + txt.getX() + txt.getWidth() || containsWholeIlb) {
                    fndTxt = txt;
                    break;
                }
                if (e.getX() < x + txt.getX() && lastItxt != null) {
                    fndTxt = lastItxt;
                    break;
                }
            }
            lastItxt = txt;
        }
        LayoutContext lc = this.panel.getLayoutContext();
        if (fndTxt == null) {
            return null;
        }
        String txt = fndTxt.getMasterText();
        CalculatedStyle style = ilb.getStyle();
        if (containsWholeIlb) {
            offset = fndTxt.getEnd();
        } else {
            int w;
            for (offset = fndTxt.getStart(); offset < fndTxt.getEnd() && (w = this.getTextWidth(lc, style, txt.substring(fndTxt.getStart(), offset + 1))) + x + fndTxt.getX() <= e.getX(); ++offset) {
            }
        }
        Text node = fndTxt.getTextNode();
        try {
            r.setStart(node, offset);
        }
        catch (Exception ex) {
            r.setStart(node, node.getLength() - 1);
        }
        return new ViewModelInfo(r, fndTxt);
    }

    private int getTextWidth(LayoutContext c, CalculatedStyle cs, String s) {
        return c.getTextRenderer().getWidth(c.getFontContext(), c.getFont(cs.getFont(c)), s);
    }

    public Range getSelectionRange() {
        if (this.dotInfo == null || this.dotInfo.range == null) {
            return null;
        }
        Range r = this.docRange.createRange();
        if (this.markInfo.range.compareBoundaryPoints((short)0, this.dotInfo.range) <= 0) {
            r.setStart(this.markInfo.range.getStartContainer(), this.markInfo.range.getStartOffset());
            r.setEnd(this.dotInfo.range.getStartContainer(), this.dotInfo.range.getStartOffset());
        } else {
            r.setStart(this.dotInfo.range.getStartContainer(), this.dotInfo.range.getStartOffset());
            r.setEnd(this.markInfo.range.getStartContainer(), this.markInfo.range.getStartOffset());
        }
        return r;
    }

    protected void adjustVisibility(Rectangle nloc) {
        if (this.panel == null) {
            return;
        }
        if (SwingUtilities.isEventDispatchThread()) {
            this.panel.scrollRectToVisible(nloc);
        } else {
            SwingUtilities.invokeLater(new SafeScroller(nloc));
        }
    }

    protected MouseEvent convertMouseEventToScale(MouseEvent e) {
        if (!e.isConsumed() && this.panel instanceof ScalableXHTMLPanel) {
            Point newP = ((ScalableXHTMLPanel)this.panel).convertFromScaled(e.getX(), e.getY());
            MouseEvent newE = new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiersEx(), (int)newP.getX(), (int)newP.getY(), e.getClickCount(), e.isPopupTrigger(), e.getButton());
            return newE;
        }
        return e;
    }

    public void setHandler(TransferHandler handler) {
        this.handler = handler;
    }

    class SafeScroller
    implements Runnable {
        Rectangle r;

        SafeScroller(Rectangle r) {
            this.r = r;
        }

        @Override
        public void run() {
            if (SelectionHighlighter.this.panel != null) {
                SelectionHighlighter.this.panel.scrollRectToVisible(this.r);
            }
        }
    }

    public static class CopyAction
    extends AbstractAction {
        private SelectionHighlighter caret;

        public CopyAction() {
            super(SelectionHighlighter.copyAction);
        }

        public void install(SelectionHighlighter caret) {
            this.caret = caret;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (this.caret != null) {
                this.caret.copy();
            }
        }
    }

    public class ViewModelInfo {
        Range range;
        InlineText text;

        ViewModelInfo(Range range, InlineText text) {
            this.range = range;
            this.text = text;
        }

        public String toString() {
            return this.range.getStartContainer() + ":" + this.range.getStartOffset();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof ViewModelInfo)) {
                return false;
            }
            ViewModelInfo that = (ViewModelInfo)o;
            if (!this.range.equals(that.range)) {
                return false;
            }
            return this.text.equals(that.text);
        }

        public int hashCode() {
            int result = this.range.hashCode();
            result = 31 * result + this.text.hashCode();
            return result;
        }

        public boolean canCopy() {
            return SelectionHighlighter.this.lastHighlightedString.length() != 0;
        }
    }
}

