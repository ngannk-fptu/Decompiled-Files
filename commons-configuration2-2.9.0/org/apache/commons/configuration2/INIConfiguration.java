/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.configuration2.BaseHierarchicalConfiguration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.SubnodeConfiguration;
import org.apache.commons.configuration2.convert.ListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.apache.commons.configuration2.tree.InMemoryNodeModel;
import org.apache.commons.configuration2.tree.InMemoryNodeModelSupport;
import org.apache.commons.configuration2.tree.NodeHandler;
import org.apache.commons.configuration2.tree.NodeHandlerDecorator;
import org.apache.commons.configuration2.tree.NodeSelector;
import org.apache.commons.configuration2.tree.TrackedNodeModel;

public class INIConfiguration
extends BaseHierarchicalConfiguration
implements FileBasedConfiguration {
    protected static final String COMMENT_CHARS = "#;";
    protected static final String SEPARATOR_CHARS = "=:";
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final String QUOTE_CHARACTERS = "\"'";
    private static final String LINE_CONT = "\\";
    private String separatorUsedInOutput = " = ";
    private String separatorUsedInInput = "=:";
    private String commentCharsUsedInInput = "#;";
    private boolean sectionInLineCommentsAllowed;

    public INIConfiguration() {
    }

    public INIConfiguration(HierarchicalConfiguration<ImmutableNode> c) {
        super(c);
    }

    private INIConfiguration(boolean sectionInLineCommentsAllowed) {
        this.sectionInLineCommentsAllowed = sectionInLineCommentsAllowed;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getSeparatorUsedInOutput() {
        this.beginRead(false);
        try {
            String string = this.separatorUsedInOutput;
            return string;
        }
        finally {
            this.endRead();
        }
    }

    public void setSeparatorUsedInOutput(String separator) {
        this.beginWrite(false);
        try {
            this.separatorUsedInOutput = separator;
        }
        finally {
            this.endWrite();
        }
    }

    public String getSeparatorUsedInInput() {
        this.beginRead(false);
        try {
            String string = this.separatorUsedInInput;
            return string;
        }
        finally {
            this.endRead();
        }
    }

    public void setSeparatorUsedInInput(String separator) {
        this.beginRead(false);
        try {
            this.separatorUsedInInput = separator;
        }
        finally {
            this.endRead();
        }
    }

    public String getCommentLeadingCharsUsedInInput() {
        this.beginRead(false);
        try {
            String string = this.commentCharsUsedInInput;
            return string;
        }
        finally {
            this.endRead();
        }
    }

    public void setCommentLeadingCharsUsedInInput(String separator) {
        this.beginRead(false);
        try {
            this.commentCharsUsedInInput = separator;
        }
        finally {
            this.endRead();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void write(Writer writer) throws ConfigurationException, IOException {
        PrintWriter out = new PrintWriter(writer);
        boolean first = true;
        String separator = this.getSeparatorUsedInOutput();
        this.beginRead(false);
        try {
            for (ImmutableNode node : ((ImmutableNode)this.getModel().getNodeHandler().getRootNode()).getChildren()) {
                if (INIConfiguration.isSectionNode(node)) {
                    if (!first) {
                        out.println();
                    }
                    out.print("[");
                    out.print(node.getNodeName());
                    out.print("]");
                    out.println();
                    node.forEach(child -> this.writeProperty(out, child.getNodeName(), child.getValue(), separator));
                } else {
                    this.writeProperty(out, node.getNodeName(), node.getValue(), separator);
                }
                first = false;
            }
            out.println();
            out.flush();
        }
        finally {
            this.endRead();
        }
    }

    @Override
    public void read(Reader in) throws ConfigurationException, IOException {
        BufferedReader bufferedReader = new BufferedReader(in);
        LinkedHashMap<String, ImmutableNode.Builder> sectionBuilders = new LinkedHashMap<String, ImmutableNode.Builder>();
        ImmutableNode.Builder rootBuilder = new ImmutableNode.Builder();
        this.createNodeBuilders(bufferedReader, rootBuilder, sectionBuilders);
        ImmutableNode rootNode = INIConfiguration.createNewRootNode(rootBuilder, sectionBuilders);
        this.addNodes(null, rootNode.getChildren());
    }

    private static ImmutableNode createNewRootNode(ImmutableNode.Builder rootBuilder, Map<String, ImmutableNode.Builder> sectionBuilders) {
        sectionBuilders.forEach((k, v) -> rootBuilder.addChild(v.name((String)k).create()));
        return rootBuilder.create();
    }

    private void createNodeBuilders(BufferedReader in, ImmutableNode.Builder rootBuilder, Map<String, ImmutableNode.Builder> sectionBuilders) throws IOException {
        ImmutableNode.Builder sectionBuilder = rootBuilder;
        String line = in.readLine();
        while (line != null) {
            if (!this.isCommentLine(line = line.trim())) {
                if (this.isSectionLine(line)) {
                    int length = this.sectionInLineCommentsAllowed ? line.indexOf("]") : line.length() - 1;
                    String section = line.substring(1, length);
                    sectionBuilder = sectionBuilders.get(section);
                    if (sectionBuilder == null) {
                        sectionBuilder = new ImmutableNode.Builder();
                        sectionBuilders.put(section, sectionBuilder);
                    }
                } else {
                    String key;
                    String value = "";
                    int index = this.findSeparator(line);
                    if (index >= 0) {
                        key = line.substring(0, index);
                        value = this.parseValue(line.substring(index + 1), in);
                    } else {
                        key = line;
                    }
                    key = key.trim();
                    if (key.isEmpty()) {
                        key = " ";
                    }
                    this.createValueNodes(sectionBuilder, key, value);
                }
            }
            line = in.readLine();
        }
    }

    private void createValueNodes(ImmutableNode.Builder sectionBuilder, String key, String value) {
        this.getListDelimiterHandler().split(value, false).forEach(v -> sectionBuilder.addChild(new ImmutableNode.Builder().name(key).value(v).create()));
    }

    private void writeProperty(PrintWriter out, String key, Object value, String separator) {
        out.print(key);
        out.print(separator);
        out.print(this.escapeValue(value.toString()));
        out.println();
    }

    private String parseValue(String val, BufferedReader reader) throws IOException {
        boolean lineContinues;
        StringBuilder propertyValue = new StringBuilder();
        String value = val.trim();
        do {
            int i;
            boolean quoted = value.startsWith("\"") || value.startsWith("'");
            boolean stop = false;
            boolean escape = false;
            char quote = quoted ? value.charAt(0) : (char)'\u0000';
            StringBuilder result = new StringBuilder();
            char lastChar = '\u0000';
            for (i = quoted ? 1 : 0; i < value.length() && !stop; ++i) {
                char c = value.charAt(i);
                if (quoted) {
                    if ('\\' == c && !escape) {
                        escape = true;
                    } else if (!escape && quote == c) {
                        stop = true;
                    } else {
                        if (escape && quote == c) {
                            escape = false;
                        } else if (escape) {
                            escape = false;
                            result.append('\\');
                        }
                        result.append(c);
                    }
                } else if (this.isCommentChar(c) && Character.isWhitespace(lastChar)) {
                    stop = true;
                } else {
                    result.append(c);
                }
                lastChar = c;
            }
            String v = result.toString();
            if (!quoted) {
                lineContinues = INIConfiguration.lineContinues(v = v.trim());
                if (lineContinues) {
                    v = v.substring(0, v.length() - 1).trim();
                }
            } else {
                lineContinues = this.lineContinues(value, i);
            }
            propertyValue.append(v);
            if (!lineContinues) continue;
            propertyValue.append(LINE_SEPARATOR);
            value = reader.readLine();
        } while (lineContinues && value != null);
        return propertyValue.toString();
    }

    private static boolean lineContinues(String line) {
        String s = line.trim();
        return s.equals(LINE_CONT) || s.length() > 2 && s.endsWith(LINE_CONT) && Character.isWhitespace(s.charAt(s.length() - 2));
    }

    private boolean lineContinues(String line, int pos) {
        String s;
        if (pos >= line.length()) {
            s = line;
        } else {
            int end;
            for (end = pos; end < line.length() && !this.isCommentChar(line.charAt(end)); ++end) {
            }
            s = line.substring(pos, end);
        }
        return INIConfiguration.lineContinues(s);
    }

    private boolean isCommentChar(char c) {
        return this.getCommentLeadingCharsUsedInInput().indexOf(c) >= 0;
    }

    private int findSeparator(String line) {
        int index = INIConfiguration.findSeparatorBeforeQuote(line, INIConfiguration.findFirstOccurrence(line, QUOTE_CHARACTERS));
        if (index < 0) {
            index = INIConfiguration.findFirstOccurrence(line, this.getSeparatorUsedInInput());
        }
        return index;
    }

    private static int findFirstOccurrence(String line, String separators) {
        int index = -1;
        for (int i = 0; i < separators.length(); ++i) {
            char sep = separators.charAt(i);
            int pos = line.indexOf(sep);
            if (pos < 0 || index >= 0 && pos >= index) continue;
            index = pos;
        }
        return index;
    }

    private static int findSeparatorBeforeQuote(String line, int quoteIndex) {
        int index;
        for (index = quoteIndex - 1; index >= 0 && Character.isWhitespace(line.charAt(index)); --index) {
        }
        if (index >= 0 && SEPARATOR_CHARS.indexOf(line.charAt(index)) < 0) {
            index = -1;
        }
        return index;
    }

    private String escapeValue(String value) {
        return String.valueOf(this.getListDelimiterHandler().escape(this.escapeComments(value), ListDelimiterHandler.NOOP_TRANSFORMER));
    }

    private String escapeComments(String value) {
        String commentChars = this.getCommentLeadingCharsUsedInInput();
        boolean quoted = false;
        for (int i = 0; i < commentChars.length(); ++i) {
            char c = commentChars.charAt(i);
            if (value.indexOf(c) == -1) continue;
            quoted = true;
            break;
        }
        if (quoted) {
            return '\"' + value.replace("\"", "\\\"") + '\"';
        }
        return value;
    }

    protected boolean isCommentLine(String line) {
        if (line == null) {
            return false;
        }
        return line.isEmpty() || this.getCommentLeadingCharsUsedInInput().indexOf(line.charAt(0)) >= 0;
    }

    protected boolean isSectionLine(String line) {
        if (line == null) {
            return false;
        }
        return this.sectionInLineCommentsAllowed ? INIConfiguration.isNonStrictSection(line) : INIConfiguration.isStrictSection(line);
    }

    private static boolean isStrictSection(String line) {
        return line.startsWith("[") && line.endsWith("]");
    }

    private static boolean isNonStrictSection(String line) {
        return line.startsWith("[") && line.contains("]");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Set<String> getSections() {
        LinkedHashSet<String> sections = new LinkedHashSet<String>();
        boolean globalSection = false;
        boolean inSection = false;
        this.beginRead(false);
        try {
            for (ImmutableNode node : ((ImmutableNode)this.getModel().getNodeHandler().getRootNode()).getChildren()) {
                if (INIConfiguration.isSectionNode(node)) {
                    inSection = true;
                    sections.add(node.getNodeName());
                    continue;
                }
                if (inSection || globalSection) continue;
                globalSection = true;
                sections.add(null);
            }
        }
        finally {
            this.endRead();
        }
        return sections;
    }

    public SubnodeConfiguration getSection(String name) {
        if (name == null) {
            return this.getGlobalSection();
        }
        try {
            return (SubnodeConfiguration)this.configurationAt(name, true);
        }
        catch (ConfigurationRuntimeException iex) {
            InMemoryNodeModel parentModel = this.getSubConfigurationParentModel();
            NodeSelector selector = parentModel.trackChildNodeWithCreation(null, name, this);
            return this.createSubConfigurationForTrackedNode(selector, this);
        }
    }

    private SubnodeConfiguration getGlobalSection() {
        InMemoryNodeModel parentModel = this.getSubConfigurationParentModel();
        NodeSelector selector = new NodeSelector(null);
        parentModel.trackNode(selector, this);
        GlobalSectionNodeModel model = new GlobalSectionNodeModel(this, selector);
        SubnodeConfiguration sub = new SubnodeConfiguration(this, model);
        this.initSubConfigurationForThisParent(sub);
        return sub;
    }

    private static boolean isSectionNode(ImmutableNode node) {
        return node.getValue() == null;
    }

    private static class GlobalSectionNodeModel
    extends TrackedNodeModel {
        public GlobalSectionNodeModel(InMemoryNodeModelSupport modelSupport, NodeSelector selector) {
            super(modelSupport, selector, true);
        }

        @Override
        public NodeHandler<ImmutableNode> getNodeHandler() {
            return new NodeHandlerDecorator<ImmutableNode>(){

                @Override
                public List<ImmutableNode> getChildren(ImmutableNode node) {
                    List<ImmutableNode> children = super.getChildren(node);
                    return this.filterChildrenOfGlobalSection(node, children);
                }

                @Override
                public List<ImmutableNode> getChildren(ImmutableNode node, String name) {
                    List<ImmutableNode> children = super.getChildren(node, name);
                    return this.filterChildrenOfGlobalSection(node, children);
                }

                @Override
                public int getChildrenCount(ImmutableNode node, String name) {
                    List<ImmutableNode> children = name != null ? super.getChildren(node, name) : super.getChildren(node);
                    return this.filterChildrenOfGlobalSection(node, children).size();
                }

                @Override
                public ImmutableNode getChild(ImmutableNode node, int index) {
                    List<ImmutableNode> children = super.getChildren(node);
                    return this.filterChildrenOfGlobalSection(node, children).get(index);
                }

                @Override
                public int indexOfChild(ImmutableNode parent, ImmutableNode child) {
                    List<ImmutableNode> children = super.getChildren(parent);
                    return this.filterChildrenOfGlobalSection(parent, children).indexOf(child);
                }

                @Override
                protected NodeHandler<ImmutableNode> getDecoratedNodeHandler() {
                    return GlobalSectionNodeModel.super.getNodeHandler();
                }

                private List<ImmutableNode> filterChildrenOfGlobalSection(ImmutableNode node, List<ImmutableNode> children) {
                    List<ImmutableNode> filteredList = node == this.getRootNode() ? children.stream().filter(child -> !INIConfiguration.isSectionNode(child)).collect(Collectors.toList()) : children;
                    return filteredList;
                }
            };
        }
    }

    public static class Builder {
        private boolean sectionInLineCommentsAllowed;

        public Builder setSectionInLineCommentsAllowed(boolean sectionInLineCommentsAllowed) {
            this.sectionInLineCommentsAllowed = sectionInLineCommentsAllowed;
            return this;
        }

        public INIConfiguration build() {
            return new INIConfiguration(this.sectionInLineCommentsAllowed);
        }
    }
}

