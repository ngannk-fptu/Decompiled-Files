/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config.generator.model;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.ehcache.config.generator.model.AbstractDepthFirstVisitor;
import net.sf.ehcache.config.generator.model.NodeAttribute;
import net.sf.ehcache.config.generator.model.NodeElement;

public class XMLGeneratorVisitor
extends AbstractDepthFirstVisitor {
    private static final String SPACER = "    ";
    private final Map<OutputBehavior, Boolean> enabledOutputBehaviors = new HashMap<OutputBehavior, Boolean>();
    private final PrintWriter out;
    private int indent;
    private NodeElement rootElement;
    private boolean visitedFirstElement;

    public XMLGeneratorVisitor(PrintWriter out) {
        this.out = out;
        this.enableAllOutputBehaviors();
    }

    public void enableAllOutputBehaviors() {
        for (OutputBehavior behavior : OutputBehavior.values()) {
            this.enableOutputBehavior(behavior);
        }
    }

    public void disableAllOutputBehaviors() {
        this.enabledOutputBehaviors.clear();
    }

    public void enableOutputBehavior(OutputBehavior behavior) {
        this.enabledOutputBehaviors.put(behavior, Boolean.TRUE);
    }

    public void disableOutputBehavior(OutputBehavior behavior) {
        this.enabledOutputBehaviors.remove((Object)behavior);
    }

    public boolean isOutputBehaviorEnabled(OutputBehavior behavior) {
        Boolean enabled = this.enabledOutputBehaviors.get((Object)behavior);
        return enabled != null && enabled != false;
    }

    private void print(String string) {
        this.out.print(this.spacer() + string);
    }

    private void printWithoutSpacer(String string) {
        this.out.print(string);
    }

    private void newLine() {
        this.out.println(this.spacer());
    }

    private String spacer() {
        StringBuilder sb = new StringBuilder(SPACER.length() * this.indent);
        for (int i = 0; i < this.indent; ++i) {
            sb.append(SPACER);
        }
        return sb.toString();
    }

    private void indentForward() {
        ++this.indent;
    }

    private void indentBackward() {
        --this.indent;
    }

    @Override
    protected void startElement(NodeElement element) {
        if (this.isOutputBehaviorEnabled(OutputBehavior.NEWLINE_FOR_EACH_ELEMENT) && this.visitedFirstElement) {
            this.newLine();
        }
        this.print("<" + element.getName());
        if (!this.visitedFirstElement) {
            this.rootElement = element;
            this.visitedFirstElement = true;
        }
    }

    @Override
    protected void startAttributes(NodeElement element) {
        if (this.isOutputBehaviorEnabled(OutputBehavior.NEWLINE_FOR_EACH_ATTRIBUTE)) {
            this.indentForward();
        }
    }

    @Override
    protected void visitAttributes(NodeElement element, List<NodeAttribute> attributes) {
        for (NodeAttribute attribute : attributes) {
            this.visitAttribute(element, attribute);
        }
    }

    protected void visitAttribute(NodeElement element, NodeAttribute attribute) {
        String value = attribute.getValue();
        if (!this.isOutputBehaviorEnabled(OutputBehavior.OUTPUT_OPTIONAL_ATTRIBUTES_WITH_DEFAULT_VALUES) && attribute.isOptional() && value != null && value.equals(attribute.getDefaultValue())) {
            return;
        }
        if (value == null) {
            value = attribute.getDefaultValue();
        }
        if (value != null) {
            this.printWithoutSpacer(" ");
            String line = attribute.getName() + "=\"" + value + "\"";
            if (this.isOutputBehaviorEnabled(OutputBehavior.NEWLINE_FOR_EACH_ATTRIBUTE)) {
                this.newLine();
                this.print(line);
            } else {
                this.printWithoutSpacer(line);
            }
        }
    }

    @Override
    protected void endAttributes(NodeElement element) {
        String end = element.getInnerContent() == null && !element.hasChildren() ? "/>" : ">";
        this.printWithoutSpacer(end);
        if (this.isOutputBehaviorEnabled(OutputBehavior.NEWLINE_FOR_EACH_ATTRIBUTE)) {
            this.indentBackward();
        }
    }

    @Override
    protected void visitElement(NodeElement element) {
        if (element.getInnerContent() != null) {
            this.indentForward();
            this.newLine();
            this.print(element.getInnerContent());
            this.indentBackward();
        }
    }

    @Override
    protected void startChildren(NodeElement element) {
        if (this.isOutputBehaviorEnabled(OutputBehavior.INDENT_CHIlD_ELEMENTS)) {
            this.indentForward();
        }
    }

    @Override
    protected void endChildren(NodeElement element) {
        if (this.isOutputBehaviorEnabled(OutputBehavior.INDENT_CHIlD_ELEMENTS)) {
            this.indentBackward();
        }
    }

    @Override
    protected void endElement(NodeElement element) {
        if (element.getInnerContent() != null || element.hasChildren()) {
            if (this.isOutputBehaviorEnabled(OutputBehavior.NEWLINE_FOR_EACH_ELEMENT)) {
                this.newLine();
            }
            this.print("</" + element.getName() + ">");
        }
        if (element.equals(this.rootElement) && this.isOutputBehaviorEnabled(OutputBehavior.NEWLINE_AT_END)) {
            this.newLine();
        }
    }

    public static enum OutputBehavior {
        INDENT_CHIlD_ELEMENTS,
        NEWLINE_FOR_EACH_ELEMENT,
        NEWLINE_FOR_EACH_ATTRIBUTE,
        OUTPUT_OPTIONAL_ATTRIBUTES_WITH_DEFAULT_VALUES,
        NEWLINE_AT_END;

    }
}

