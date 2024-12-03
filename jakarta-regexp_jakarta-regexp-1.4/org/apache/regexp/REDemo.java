/*
 * Decompiled with CFR 0.152.
 */
package org.apache.regexp;

import java.applet.Applet;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.CharArrayWriter;
import java.io.PrintWriter;
import org.apache.regexp.RE;
import org.apache.regexp.REDebugCompiler;

public class REDemo
extends Applet
implements TextListener {
    RE r = new RE();
    REDebugCompiler compiler = new REDebugCompiler();
    TextField fieldRE;
    TextField fieldMatch;
    TextArea outRE;
    TextArea outMatch;

    public void init() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        this.setLayout(gridBagLayout);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = 13;
        gridBagLayout.setConstraints(this.add(new Label("Regular expression:", 2)), gridBagConstraints);
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = 17;
        this.fieldRE = new TextField("\\[([:javastart:][:javapart:]*)\\]", 40);
        gridBagLayout.setConstraints(this.add(this.fieldRE), gridBagConstraints);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = -1;
        gridBagConstraints.anchor = 13;
        gridBagLayout.setConstraints(this.add(new Label("String:", 2)), gridBagConstraints);
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridx = -1;
        gridBagConstraints.anchor = 17;
        this.fieldMatch = new TextField("aaa([foo])aaa", 40);
        gridBagLayout.setConstraints(this.add(this.fieldMatch), gridBagConstraints);
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridx = -1;
        gridBagConstraints.fill = 1;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.weightx = 1.0;
        this.outRE = new TextArea();
        gridBagLayout.setConstraints(this.add(this.outRE), gridBagConstraints);
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridx = -1;
        this.outMatch = new TextArea();
        gridBagLayout.setConstraints(this.add(this.outMatch), gridBagConstraints);
        this.fieldRE.addTextListener(this);
        this.fieldMatch.addTextListener(this);
        this.textValueChanged(null);
    }

    void sayRE(String string) {
        this.outRE.setText(string);
    }

    void sayMatch(String string) {
        this.outMatch.setText(string);
    }

    String throwableToString(Throwable throwable) {
        String string = throwable.getClass().getName();
        String string2 = throwable.getMessage();
        if (string2 != null) {
            string = string + "\n" + string2;
        }
        return string;
    }

    void updateRE(String string) {
        try {
            this.r.setProgram(this.compiler.compile(string));
            CharArrayWriter charArrayWriter = new CharArrayWriter();
            this.compiler.dumpProgram(new PrintWriter(charArrayWriter));
            this.sayRE(charArrayWriter.toString());
            System.out.println(charArrayWriter);
        }
        catch (Exception exception) {
            this.r.setProgram(null);
            this.sayRE(this.throwableToString(exception));
        }
        catch (Throwable throwable) {
            this.r.setProgram(null);
            this.sayRE(this.throwableToString(throwable));
        }
    }

    void updateMatch(String string) {
        try {
            if (this.r.match(string)) {
                String string2 = "Matches.\n\n";
                int n = 0;
                while (n < this.r.getParenCount()) {
                    string2 = string2 + "$" + n + " = " + this.r.getParen(n) + "\n";
                    ++n;
                }
                this.sayMatch(string2);
            } else {
                this.sayMatch("Does not match");
            }
        }
        catch (Throwable throwable) {
            this.sayMatch(this.throwableToString(throwable));
        }
    }

    public void textValueChanged(TextEvent textEvent) {
        if (textEvent == null || textEvent.getSource() == this.fieldRE) {
            this.updateRE(this.fieldRE.getText());
        }
        this.updateMatch(this.fieldMatch.getText());
    }

    public static void main(String[] stringArray) {
        Frame frame = new Frame("RE Demo");
        frame.addWindowListener(new WindowAdapter(){

            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });
        REDemo rEDemo = new REDemo();
        frame.add(rEDemo);
        rEDemo.init();
        frame.pack();
        ((Component)frame).setVisible(true);
    }
}

