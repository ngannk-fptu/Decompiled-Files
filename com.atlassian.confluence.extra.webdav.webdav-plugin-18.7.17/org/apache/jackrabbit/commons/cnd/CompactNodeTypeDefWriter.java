/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.cnd;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeTypeDefinition;
import javax.jcr.nodetype.PropertyDefinition;
import javax.jcr.version.OnParentVersionAction;
import org.apache.jackrabbit.commons.cnd.Lexer;
import org.apache.jackrabbit.commons.query.qom.Operator;
import org.apache.jackrabbit.util.ISO9075;
import org.apache.jackrabbit.util.Text;

public class CompactNodeTypeDefWriter {
    private static final String INDENT = "  ";
    private static final String ANY = "*";
    private final NamespaceMapping nsMapping;
    private Writer out;
    private Writer nsWriter;
    private final Set<String> usedNamespaces = new HashSet<String>();

    public CompactNodeTypeDefWriter(Writer out, Session session, boolean includeNS) {
        this(out, new DefaultNamespaceMapping(session), includeNS);
    }

    public CompactNodeTypeDefWriter(Writer out, NamespaceMapping nsMapping, boolean includeNS) {
        this.nsMapping = nsMapping;
        if (includeNS) {
            this.out = new StringWriter();
            this.nsWriter = out;
        } else {
            this.out = out;
            this.nsWriter = null;
        }
    }

    public static void write(Collection<NodeTypeDefinition> defs, Session session, Writer out) throws IOException {
        CompactNodeTypeDefWriter w = new CompactNodeTypeDefWriter(out, session, true);
        for (NodeTypeDefinition def : defs) {
            w.write(def);
        }
        w.close();
    }

    public static void write(Collection<NodeTypeDefinition> defs, NamespaceMapping nsMapping, Writer out) throws IOException {
        CompactNodeTypeDefWriter w = new CompactNodeTypeDefWriter(out, nsMapping, true);
        for (NodeTypeDefinition def : defs) {
            w.write(def);
        }
        w.close();
    }

    public void write(NodeTypeDefinition ntd) throws IOException {
        NodeDefinition[] ndefs;
        this.writeName(ntd);
        this.writeSupertypes(ntd);
        this.writeOptions(ntd);
        PropertyDefinition[] pdefs = ntd.getDeclaredPropertyDefinitions();
        if (pdefs != null) {
            for (PropertyDefinition pd : pdefs) {
                this.writePropDef(pd);
            }
        }
        if ((ndefs = ntd.getDeclaredChildNodeDefinitions()) != null) {
            for (NodeDefinition nd : ndefs) {
                this.writeNodeDef(nd);
            }
        }
        this.out.write("\n\n");
    }

    public void writeNamespaceDeclaration(String prefix) throws IOException {
        if (this.nsWriter != null && !this.usedNamespaces.contains(prefix)) {
            this.usedNamespaces.add(prefix);
            this.nsWriter.write("<'");
            this.nsWriter.write(prefix);
            this.nsWriter.write("'='");
            this.nsWriter.write(this.escape(this.nsMapping.getNamespaceURI(prefix)));
            this.nsWriter.write("'>\n");
        }
    }

    public void close() throws IOException {
        if (this.nsWriter != null) {
            this.nsWriter.write("\n");
            this.out.close();
            this.nsWriter.write(((StringWriter)this.out).getBuffer().toString());
            this.out = this.nsWriter;
            this.nsWriter = null;
        }
        this.out.flush();
        this.out = null;
    }

    private void writeName(NodeTypeDefinition ntd) throws IOException {
        this.out.write(91);
        this.writeJcrName(ntd.getName());
        this.out.write(93);
    }

    private void writeSupertypes(NodeTypeDefinition ntd) throws IOException {
        TreeSet<String> supertypes = new TreeSet<String>();
        for (String name : ntd.getDeclaredSupertypeNames()) {
            if (name.equals("nt:base")) continue;
            supertypes.add(name);
        }
        if (!supertypes.isEmpty()) {
            String delim = " > ";
            for (String name : supertypes) {
                this.out.write(delim);
                this.writeJcrName(name);
                delim = ", ";
            }
        }
    }

    private void writeOptions(NodeTypeDefinition ntd) throws IOException {
        String pin;
        LinkedList<String> options = new LinkedList<String>();
        if (ntd.isAbstract()) {
            options.add(Lexer.ABSTRACT[0]);
        }
        if (ntd.hasOrderableChildNodes()) {
            options.add(Lexer.ORDERABLE[0]);
        }
        if (ntd.isMixin()) {
            options.add(Lexer.MIXIN[0]);
        }
        if (!ntd.isQueryable()) {
            options.add(Lexer.NOQUERY[0]);
        }
        if ((pin = ntd.getPrimaryItemName()) != null) {
            options.add(Lexer.PRIMARYITEM[0]);
        }
        for (int i = 0; i < options.size(); ++i) {
            if (i == 0) {
                this.out.write("\n  ");
            } else {
                this.out.write(" ");
            }
            this.out.write((String)options.get(i));
        }
        if (pin != null) {
            this.out.write(" ");
            this.writeJcrName(pin);
        }
    }

    private void writePropDef(PropertyDefinition pd) throws IOException {
        List<String> defaultOps;
        ArrayList<String> opts;
        String[] qops;
        this.out.write("\n  - ");
        this.writeJcrName(pd.getName());
        this.out.write(" ");
        this.out.write(40);
        this.out.write(PropertyType.nameFromValue(pd.getRequiredType()).toLowerCase());
        this.out.write(41);
        this.writeDefaultValues(pd.getDefaultValues());
        if (pd.isMandatory()) {
            this.out.write(" ");
            this.out.write(Lexer.MANDATORY[0]);
        }
        if (pd.isAutoCreated()) {
            this.out.write(" ");
            this.out.write(Lexer.AUTOCREATED[0]);
        }
        if (pd.isProtected()) {
            this.out.write(" ");
            this.out.write(Lexer.PROTECTED[0]);
        }
        if (pd.isMultiple()) {
            this.out.write(" ");
            this.out.write(Lexer.MULTIPLE[0]);
        }
        if (pd.getOnParentVersion() != 1) {
            this.out.write(" ");
            this.out.write(OnParentVersionAction.nameFromValue(pd.getOnParentVersion()).toLowerCase());
        }
        if (!pd.isFullTextSearchable()) {
            this.out.write(" ");
            this.out.write(Lexer.NOFULLTEXT[0]);
        }
        if (!pd.isQueryOrderable()) {
            this.out.write(" ");
            this.out.write(Lexer.NOQUERYORDER[0]);
        }
        if ((qops = pd.getAvailableQueryOperators()) != null && qops.length > 0 && !(opts = new ArrayList<String>(Arrays.asList(qops))).containsAll(defaultOps = Arrays.asList(Operator.getAllQueryOperators()))) {
            this.out.write(" ");
            this.out.write(Lexer.QUERYOPS[0]);
            this.out.write(" '");
            String delim = "";
            for (String opt : opts) {
                this.out.write(delim);
                delim = ", ";
                if (opt.equals("jcr.operator.equal.to")) {
                    this.out.write("=");
                    continue;
                }
                if (opt.equals("jcr.operator.not.equal.to")) {
                    this.out.write("<>");
                    continue;
                }
                if (opt.equals("jcr.operator.greater.than")) {
                    this.out.write(">");
                    continue;
                }
                if (opt.equals("jcr.operator.greater.than.or.equal.to")) {
                    this.out.write(">=");
                    continue;
                }
                if (opt.equals("jcr.operator.less.than")) {
                    this.out.write("<");
                    continue;
                }
                if (opt.equals("jcr.operator.less.than.or.equal.to")) {
                    this.out.write("<=");
                    continue;
                }
                if (!opt.equals("jcr.operator.like")) continue;
                this.out.write("LIKE");
            }
            this.out.write("'");
        }
        this.writeValueConstraints(pd.getValueConstraints(), pd.getRequiredType());
    }

    private void writeDefaultValues(Value[] dva) throws IOException {
        if (dva != null && dva.length > 0) {
            String delim = " = '";
            for (Value value : dva) {
                this.out.write(delim);
                try {
                    this.out.write(this.escape(value.getString()));
                }
                catch (RepositoryException e) {
                    this.out.write(this.escape(value.toString()));
                }
                this.out.write("'");
                delim = ", '";
            }
        }
    }

    private void writeValueConstraints(String[] constraints, int type) throws IOException {
        if (constraints != null && constraints.length > 0) {
            this.out.write(" ");
            this.out.write(60);
            this.out.write(" '");
            this.out.write(this.escape(constraints[0]));
            this.out.write("'");
            for (int i = 1; i < constraints.length; ++i) {
                this.out.write(", '");
                this.out.write(this.escape(constraints[i]));
                this.out.write("'");
            }
        }
    }

    private void writeNodeDef(NodeDefinition nd) throws IOException {
        this.out.write("\n  + ");
        this.writeJcrName(nd.getName());
        this.writeRequiredTypes(nd.getRequiredPrimaryTypeNames());
        this.writeDefaultType(nd.getDefaultPrimaryTypeName());
        if (nd.isMandatory()) {
            this.out.write(" ");
            this.out.write(Lexer.MANDATORY[0]);
        }
        if (nd.isAutoCreated()) {
            this.out.write(" ");
            this.out.write(Lexer.AUTOCREATED[0]);
        }
        if (nd.isProtected()) {
            this.out.write(" ");
            this.out.write(Lexer.PROTECTED[0]);
        }
        if (nd.allowsSameNameSiblings()) {
            this.out.write(" ");
            this.out.write(Lexer.MULTIPLE[0]);
        }
        if (nd.getOnParentVersion() != 1) {
            this.out.write(" ");
            this.out.write(OnParentVersionAction.nameFromValue(nd.getOnParentVersion()).toLowerCase());
        }
    }

    private void writeRequiredTypes(String[] reqTypes) throws IOException {
        if (reqTypes != null && reqTypes.length > 0) {
            String delim = " (";
            for (String reqType : reqTypes) {
                this.out.write(delim);
                this.writeJcrName(reqType);
                delim = ", ";
            }
            this.out.write(41);
        }
    }

    private void writeDefaultType(String defType) throws IOException {
        if (defType != null && !defType.equals(ANY)) {
            this.out.write(" = ");
            this.writeJcrName(defType);
        }
    }

    private void writeJcrName(String name) throws IOException {
        boolean quotesNeeded;
        String localName;
        if (name == null) {
            return;
        }
        String prefix = Text.getNamespacePrefix(name);
        if (!prefix.equals("")) {
            this.writeNamespaceDeclaration(prefix);
            prefix = prefix + ":";
        }
        String encLocalName = ANY.equals(localName = Text.getLocalName(name)) ? ANY : ISO9075.encode(Text.getLocalName(name));
        String resolvedName = prefix + encLocalName;
        boolean bl = quotesNeeded = name.indexOf(45) >= 0 || name.indexOf(43) >= 0;
        if (quotesNeeded) {
            this.out.write("'");
            this.out.write(resolvedName);
            this.out.write("'");
        } else {
            this.out.write(resolvedName);
        }
    }

    private String escape(String s) {
        StringBuffer sb = new StringBuffer(s);
        for (int i = 0; i < sb.length(); ++i) {
            if (sb.charAt(i) == '\\') {
                sb.insert(i, '\\');
                ++i;
                continue;
            }
            if (sb.charAt(i) != '\'') continue;
            sb.insert(i, '\'');
            ++i;
        }
        return sb.toString();
    }

    private static class DefaultNamespaceMapping
    implements NamespaceMapping {
        private final Session session;

        private DefaultNamespaceMapping(Session session) {
            this.session = session;
        }

        @Override
        public String getNamespaceURI(String prefix) {
            try {
                return this.session.getNamespaceURI(prefix);
            }
            catch (RepositoryException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static interface NamespaceMapping {
        public String getNamespaceURI(String var1);
    }
}

