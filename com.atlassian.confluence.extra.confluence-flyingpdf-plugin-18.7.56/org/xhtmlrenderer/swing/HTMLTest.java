/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.swing;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URL;
import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import org.xhtmlrenderer.simple.XHTMLPanel;
import org.xhtmlrenderer.swing.DOMInspector;
import org.xhtmlrenderer.swing.LinkListener;
import org.xhtmlrenderer.util.Uu;
import org.xhtmlrenderer.util.XRLog;

public class HTMLTest
extends JFrame {
    private static final long serialVersionUID = 1L;
    private final XHTMLPanel panel = new XHTMLPanel();
    private static final String BASE_TITLE = "Flying Saucer";

    public HTMLTest(String[] args) {
        super(BASE_TITLE);
        int width = 360;
        int height = 500;
        this.panel.setPreferredSize(new Dimension(width, height));
        JScrollPane scroll = new JScrollPane(this.panel);
        scroll.setVerticalScrollBarPolicy(22);
        scroll.setHorizontalScrollBarPolicy(32);
        scroll.setPreferredSize(new Dimension(width, height));
        this.panel.addMouseTrackingListener(new LinkListener());
        if (args.length > 0) {
            this.loadDocument(args[0]);
        }
        this.getContentPane().add("Center", scroll);
        JMenuBar mb = new JMenuBar();
        JMenu file = new JMenu("File");
        mb.add(file);
        file.setMnemonic('F');
        file.add(new QuitAction());
        JMenu view = new JMenu("View");
        mb.add(view);
        view.setMnemonic('V');
        view.add(new RefreshPageAction());
        view.add(new ReloadPageAction());
        JMenu debug = new JMenu("Debug");
        mb.add(debug);
        debug.setMnemonic('D');
        JMenu debugShow = new JMenu("Show");
        debug.add(debugShow);
        debugShow.setMnemonic('S');
        debugShow.add(new JCheckBoxMenuItem(new BoxOutlinesAction()));
        debugShow.add(new JCheckBoxMenuItem(new LineBoxOutlinesAction()));
        debugShow.add(new JCheckBoxMenuItem(new InlineBoxesAction()));
        debugShow.add(new JCheckBoxMenuItem(new FontMetricsAction()));
        JMenu anti = new JMenu("Anti Aliasing");
        anti.add(new JCheckBoxMenuItem(new AntiAliasedAction("None", -1)));
        anti.add(new JCheckBoxMenuItem(new AntiAliasedAction("Low (Default)", 25)));
        anti.add(new JCheckBoxMenuItem(new AntiAliasedAction("Medium", 12)));
        anti.add(new JCheckBoxMenuItem(new AntiAliasedAction("Highest", 0)));
        debug.add(anti);
        debug.add(new ShowDOMInspectorAction());
        this.setJMenuBar(mb);
    }

    public void addFileLoadAction(JMenu menu, String display, final String file) {
        menu.add(new AbstractAction(display){
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent evt) {
                HTMLTest.this.loadDocument(file);
            }
        });
    }

    private void loadDocument(final String uri) {
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                try {
                    long st = System.currentTimeMillis();
                    URL url = null;
                    url = uri.startsWith("http://") ? new URL(uri) : new File(uri).toURL();
                    System.err.println("loading " + url.toString() + "!");
                    HTMLTest.this.panel.setDocument(url.toExternalForm());
                    long el = System.currentTimeMillis() - st;
                    XRLog.general("loadDocument(" + url.toString() + ") in " + el + "ms, render may take longer");
                    HTMLTest.this.setTitle("Flying Saucer-  " + HTMLTest.this.panel.getDocumentTitle() + "  (" + url.toString() + ")");
                }
                catch (Exception ex) {
                    Uu.p(ex);
                }
                HTMLTest.this.panel.repaint();
            }
        });
    }

    public static void main(String[] args) throws Exception {
        HTMLTest frame = new HTMLTest(args);
        frame.setDefaultCloseOperation(3);
        frame.pack();
        frame.setVisible(true);
    }

    static class ReloadPageAction
    extends AbstractAction {
        private static final long serialVersionUID = 1L;

        ReloadPageAction() {
            super("Reload Page");
            this.putValue("MnemonicKey", new Integer(80));
            this.putValue("AcceleratorKey", KeyStroke.getKeyStroke(116, 2));
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            System.out.println("Reload Page triggered");
        }
    }

    static class RefreshPageAction
    extends AbstractAction {
        private static final long serialVersionUID = 1L;

        RefreshPageAction() {
            super("Refresh Page");
            this.putValue("MnemonicKey", new Integer(82));
            this.putValue("AcceleratorKey", KeyStroke.getKeyStroke("F5"));
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            System.out.println("Refresh Page triggered");
        }
    }

    class ShowDOMInspectorAction
    extends AbstractAction {
        private static final long serialVersionUID = 1L;
        private DOMInspector inspector;
        private JFrame inspectorFrame;

        ShowDOMInspectorAction() {
            super("DOM Tree Inspector");
            this.putValue("MnemonicKey", new Integer(68));
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            if (this.inspectorFrame == null) {
                this.inspectorFrame = new JFrame("DOM Tree Inspector");
            }
            if (this.inspector == null) {
                this.inspector = new DOMInspector(((HTMLTest)HTMLTest.this).panel.doc, HTMLTest.this.panel.getSharedContext(), HTMLTest.this.panel.getSharedContext().getCss());
                this.inspectorFrame.getContentPane().add(this.inspector);
                this.inspectorFrame.pack();
                this.inspectorFrame.setSize(400, 600);
                this.inspectorFrame.setVisible(true);
            } else {
                this.inspector.setForDocument(((HTMLTest)HTMLTest.this).panel.doc, HTMLTest.this.panel.getSharedContext(), HTMLTest.this.panel.getSharedContext().getCss());
            }
            this.inspectorFrame.setVisible(true);
        }
    }

    class AntiAliasedAction
    extends AbstractAction {
        private static final long serialVersionUID = 1L;
        int fontSizeThreshold;

        AntiAliasedAction(String text, int fontSizeThreshold) {
            super(text);
            this.fontSizeThreshold = fontSizeThreshold;
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            HTMLTest.this.panel.getSharedContext().getTextRenderer().setSmoothingThreshold(this.fontSizeThreshold);
            HTMLTest.this.panel.repaint();
        }
    }

    class FontMetricsAction
    extends AbstractAction {
        private static final long serialVersionUID = 1L;

        FontMetricsAction() {
            super("Show Font Metrics");
            this.putValue("MnemonicKey", new Integer(70));
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            HTMLTest.this.panel.getSharedContext().setDebug_draw_font_metrics(!HTMLTest.this.panel.getSharedContext().debugDrawFontMetrics());
            HTMLTest.this.panel.repaint();
        }
    }

    class InlineBoxesAction
    extends AbstractAction {
        private static final long serialVersionUID = 1L;

        InlineBoxesAction() {
            super("Show Inline Boxes");
            this.putValue("MnemonicKey", new Integer(73));
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            HTMLTest.this.panel.getSharedContext().setDebug_draw_inline_boxes(!HTMLTest.this.panel.getSharedContext().debugDrawInlineBoxes());
            HTMLTest.this.panel.repaint();
        }
    }

    class LineBoxOutlinesAction
    extends AbstractAction {
        private static final long serialVersionUID = 1L;

        LineBoxOutlinesAction() {
            super("Show Line Box Outlines");
            this.putValue("MnemonicKey", new Integer(76));
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            HTMLTest.this.panel.getSharedContext().setDebug_draw_line_boxes(!HTMLTest.this.panel.getSharedContext().debugDrawLineBoxes());
            HTMLTest.this.panel.repaint();
        }
    }

    class BoxOutlinesAction
    extends AbstractAction {
        private static final long serialVersionUID = 1L;

        BoxOutlinesAction() {
            super("Show Box Outlines");
            this.putValue("MnemonicKey", new Integer(66));
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            HTMLTest.this.panel.getSharedContext().setDebug_draw_boxes(!HTMLTest.this.panel.getSharedContext().debugDrawBoxes());
            HTMLTest.this.panel.repaint();
        }
    }

    static class QuitAction
    extends AbstractAction {
        private static final long serialVersionUID = 1L;

        QuitAction() {
            super("Quit");
            this.putValue("MnemonicKey", new Integer(81));
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            System.exit(0);
        }
    }
}

