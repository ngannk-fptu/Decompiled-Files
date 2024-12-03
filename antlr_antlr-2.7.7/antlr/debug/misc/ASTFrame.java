/*
 * Decompiled with CFR 0.152.
 */
package antlr.debug.misc;

import antlr.ASTFactory;
import antlr.CommonAST;
import antlr.collections.AST;
import antlr.debug.misc.JTreeASTModel;
import antlr.debug.misc.JTreeASTPanel;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

public class ASTFrame
extends JFrame {
    static final int WIDTH = 200;
    static final int HEIGHT = 300;

    public ASTFrame(String string, AST aST) {
        super(string);
        MyTreeSelectionListener myTreeSelectionListener = new MyTreeSelectionListener();
        JTreeASTPanel jTreeASTPanel = new JTreeASTPanel(new JTreeASTModel(aST), null);
        Container container = this.getContentPane();
        container.add((Component)jTreeASTPanel, "Center");
        this.addWindowListener(new WindowAdapter(){

            public void windowClosing(WindowEvent windowEvent) {
                Frame frame = (Frame)windowEvent.getSource();
                frame.setVisible(false);
                frame.dispose();
            }
        });
        this.setSize(200, 300);
    }

    public static void main(String[] stringArray) {
        ASTFactory aSTFactory = new ASTFactory();
        CommonAST commonAST = (CommonAST)aSTFactory.create(0, "ROOT");
        commonAST.addChild((CommonAST)aSTFactory.create(0, "C1"));
        commonAST.addChild((CommonAST)aSTFactory.create(0, "C2"));
        commonAST.addChild((CommonAST)aSTFactory.create(0, "C3"));
        ASTFrame aSTFrame = new ASTFrame("AST JTree Example", commonAST);
        aSTFrame.setVisible(true);
    }

    class MyTreeSelectionListener
    implements TreeSelectionListener {
        MyTreeSelectionListener() {
        }

        public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
            TreePath treePath = treeSelectionEvent.getPath();
            System.out.println("Selected: " + treePath.getLastPathComponent());
            Object[] objectArray = treePath.getPath();
            for (int i = 0; i < objectArray.length; ++i) {
                System.out.print("->" + objectArray[i]);
            }
            System.out.println();
        }
    }
}

