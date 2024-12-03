/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.cnd;

import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.commons.cnd.DefinitionBuilderFactory;
import org.apache.jackrabbit.commons.cnd.Lexer;
import org.apache.jackrabbit.commons.cnd.ParseException;

public class CompactNodeTypeDefReader<T, N> {
    private final List<T> nodeTypeDefs = new LinkedList<T>();
    private final Lexer lexer;
    private String currentToken;
    private final DefinitionBuilderFactory<T, N> factory;

    public CompactNodeTypeDefReader(Reader r, String systemId, DefinitionBuilderFactory<T, N> factory) throws ParseException {
        this(r, systemId, null, factory);
    }

    public CompactNodeTypeDefReader(Reader r, String systemId, N nsMapping, DefinitionBuilderFactory<T, N> factory) throws ParseException {
        this.factory = factory;
        this.lexer = new Lexer(r, systemId);
        if (nsMapping != null) {
            factory.setNamespaceMapping(nsMapping);
        }
        this.nextToken();
        this.parse();
    }

    public String getSystemId() {
        return this.lexer.getSystemId();
    }

    public List<T> getNodeTypeDefinitions() {
        return this.nodeTypeDefs;
    }

    public N getNamespaceMapping() {
        return this.factory.getNamespaceMapping();
    }

    private void parse() throws ParseException {
        while (!this.currentTokenEquals("eof") && this.doNameSpace()) {
        }
        try {
            while (!this.currentTokenEquals("eof")) {
                DefinitionBuilderFactory.AbstractNodeTypeDefinitionBuilder<T> ntd = this.factory.newNodeTypeDefinitionBuilder();
                ntd.setOrderableChildNodes(false);
                ntd.setMixin(false);
                ntd.setAbstract(false);
                ntd.setQueryable(true);
                this.doNodeTypeName(ntd);
                this.doSuperTypes(ntd);
                this.doOptions(ntd);
                this.doItemDefs(ntd);
                this.nodeTypeDefs.add(ntd.build());
            }
        }
        catch (RepositoryException e) {
            this.lexer.fail(e);
        }
    }

    private boolean doNameSpace() throws ParseException {
        if (!this.currentTokenEquals('<')) {
            return false;
        }
        this.nextToken();
        String prefix = this.currentToken;
        this.nextToken();
        if (!this.currentTokenEquals('=')) {
            this.lexer.fail("Missing = in namespace decl.");
        }
        this.nextToken();
        String uri = this.currentToken;
        this.nextToken();
        if (!this.currentTokenEquals('>')) {
            this.lexer.fail("Missing > in namespace decl.");
        }
        try {
            this.factory.setNamespace(prefix, uri);
        }
        catch (RepositoryException e) {
            this.lexer.fail("Error setting namespace mapping " + this.currentToken, e);
        }
        this.nextToken();
        return true;
    }

    private void doNodeTypeName(DefinitionBuilderFactory.AbstractNodeTypeDefinitionBuilder<T> ntd) throws ParseException {
        if (!this.currentTokenEquals('[')) {
            this.lexer.fail("Missing '[' delimiter for beginning of node type name");
        }
        this.nextToken();
        try {
            ntd.setName(this.currentToken);
        }
        catch (RepositoryException e) {
            this.lexer.fail("Error setting node type name " + this.currentToken, e);
        }
        this.nextToken();
        if (!this.currentTokenEquals(']')) {
            this.lexer.fail("Missing ']' delimiter for end of node type name, found " + this.currentToken);
        }
        this.nextToken();
    }

    private void doSuperTypes(DefinitionBuilderFactory.AbstractNodeTypeDefinitionBuilder<T> ntd) throws ParseException {
        if (this.currentTokenEquals('>')) {
            do {
                this.nextToken();
                try {
                    ntd.addSupertype(this.currentToken);
                }
                catch (RepositoryException e) {
                    this.lexer.fail("Error setting super type of " + ntd.getName() + " to " + this.currentToken, e);
                }
                this.nextToken();
            } while (this.currentTokenEquals(','));
        }
    }

    private void doOptions(DefinitionBuilderFactory.AbstractNodeTypeDefinitionBuilder<T> ntd) throws ParseException {
        boolean hasOption = true;
        try {
            while (hasOption) {
                if (this.currentTokenEquals(Lexer.ORDERABLE)) {
                    this.nextToken();
                    ntd.setOrderableChildNodes(true);
                    continue;
                }
                if (this.currentTokenEquals(Lexer.MIXIN)) {
                    this.nextToken();
                    ntd.setMixin(true);
                    continue;
                }
                if (this.currentTokenEquals(Lexer.ABSTRACT)) {
                    this.nextToken();
                    ntd.setAbstract(true);
                    continue;
                }
                if (this.currentTokenEquals(Lexer.NOQUERY)) {
                    this.nextToken();
                    ntd.setQueryable(false);
                    continue;
                }
                if (this.currentTokenEquals(Lexer.QUERY)) {
                    this.nextToken();
                    ntd.setQueryable(true);
                    continue;
                }
                if (this.currentTokenEquals(Lexer.PRIMARYITEM)) {
                    this.nextToken();
                    ntd.setPrimaryItemName(this.currentToken);
                    this.nextToken();
                    continue;
                }
                hasOption = false;
            }
        }
        catch (RepositoryException e) {
            this.lexer.fail("Error setting option of " + ntd.getName() + " to " + this.currentToken, e);
        }
    }

    private void doItemDefs(DefinitionBuilderFactory.AbstractNodeTypeDefinitionBuilder<T> ntd) throws ParseException {
        while (this.currentTokenEquals('-') || this.currentTokenEquals('+')) {
            if (this.currentTokenEquals('-')) {
                try {
                    DefinitionBuilderFactory.AbstractPropertyDefinitionBuilder<T> pd = ntd.newPropertyDefinitionBuilder();
                    try {
                        pd.setAutoCreated(false);
                        pd.setDeclaringNodeType(ntd.getName());
                        pd.setMandatory(false);
                        pd.setMultiple(false);
                        pd.setOnParentVersion(1);
                        pd.setProtected(false);
                        pd.setRequiredType(1);
                        pd.setFullTextSearchable(true);
                        pd.setQueryOrderable(true);
                    }
                    catch (RepositoryException e) {
                        this.lexer.fail("Error setting property definitions of " + pd.getName() + " to " + this.currentToken, e);
                    }
                    this.nextToken();
                    this.doPropertyDefinition(pd, ntd);
                    pd.build();
                }
                catch (RepositoryException e) {
                    this.lexer.fail("Error building property definition for " + ntd.getName(), e);
                }
                continue;
            }
            if (!this.currentTokenEquals('+')) continue;
            try {
                DefinitionBuilderFactory.AbstractNodeDefinitionBuilder<T> nd = ntd.newNodeDefinitionBuilder();
                try {
                    nd.setAllowsSameNameSiblings(false);
                    nd.setAutoCreated(false);
                    nd.setDeclaringNodeType(ntd.getName());
                    nd.setMandatory(false);
                    nd.setOnParentVersion(1);
                    nd.setProtected(false);
                }
                catch (RepositoryException e) {
                    this.lexer.fail("Error setting node definitions of " + nd.getName() + " to " + this.currentToken, e);
                }
                this.nextToken();
                this.doChildNodeDefinition(nd, ntd);
                nd.build();
            }
            catch (RepositoryException e) {
                this.lexer.fail("Error building node definition for " + ntd.getName(), e);
            }
        }
    }

    private void doPropertyDefinition(DefinitionBuilderFactory.AbstractPropertyDefinitionBuilder<T> pd, DefinitionBuilderFactory.AbstractNodeTypeDefinitionBuilder<T> ntd) throws ParseException {
        try {
            pd.setName(this.currentToken);
        }
        catch (RepositoryException e) {
            this.lexer.fail("Invalid property name '" + this.currentToken, e);
        }
        this.nextToken();
        this.doPropertyType(pd);
        this.doPropertyDefaultValue(pd);
        this.doPropertyAttributes(pd, ntd);
        this.doPropertyValueConstraints(pd);
    }

    private void doPropertyType(DefinitionBuilderFactory.AbstractPropertyDefinitionBuilder<T> pd) throws ParseException {
        if (!this.currentTokenEquals('(')) {
            return;
        }
        this.nextToken();
        try {
            if (this.currentTokenEquals(Lexer.STRING)) {
                pd.setRequiredType(1);
            } else if (this.currentTokenEquals(Lexer.BINARY)) {
                pd.setRequiredType(2);
            } else if (this.currentTokenEquals(Lexer.LONG)) {
                pd.setRequiredType(3);
            } else if (this.currentTokenEquals(Lexer.DECIMAL)) {
                pd.setRequiredType(12);
            } else if (this.currentTokenEquals(Lexer.DOUBLE)) {
                pd.setRequiredType(4);
            } else if (this.currentTokenEquals(Lexer.BOOLEAN)) {
                pd.setRequiredType(6);
            } else if (this.currentTokenEquals(Lexer.DATE)) {
                pd.setRequiredType(5);
            } else if (this.currentTokenEquals(Lexer.NAME)) {
                pd.setRequiredType(7);
            } else if (this.currentTokenEquals(Lexer.PATH)) {
                pd.setRequiredType(8);
            } else if (this.currentTokenEquals(Lexer.URI)) {
                pd.setRequiredType(11);
            } else if (this.currentTokenEquals(Lexer.REFERENCE)) {
                pd.setRequiredType(9);
            } else if (this.currentTokenEquals(Lexer.WEAKREFERENCE)) {
                pd.setRequiredType(10);
            } else if (this.currentTokenEquals(Lexer.UNDEFINED)) {
                pd.setRequiredType(0);
            } else {
                this.lexer.fail("Unkown property type '" + this.currentToken + "' specified");
            }
        }
        catch (RepositoryException e) {
            this.lexer.fail("Error setting property type of " + pd.getName() + " to " + this.currentToken, e);
        }
        this.nextToken();
        if (!this.currentTokenEquals(')')) {
            this.lexer.fail("Missing ')' delimiter for end of property type");
        }
        this.nextToken();
    }

    private void doPropertyAttributes(DefinitionBuilderFactory.AbstractPropertyDefinitionBuilder<T> pd, DefinitionBuilderFactory.AbstractNodeTypeDefinitionBuilder<T> ntd) throws ParseException {
        try {
            while (this.currentTokenEquals(Lexer.PROP_ATTRIBUTE)) {
                if (this.currentTokenEquals(Lexer.PRIMARY)) {
                    ntd.setPrimaryItemName(pd.getName());
                } else if (this.currentTokenEquals(Lexer.AUTOCREATED)) {
                    pd.setAutoCreated(true);
                } else if (this.currentTokenEquals(Lexer.MANDATORY)) {
                    pd.setMandatory(true);
                } else if (this.currentTokenEquals(Lexer.PROTECTED)) {
                    pd.setProtected(true);
                } else if (this.currentTokenEquals(Lexer.MULTIPLE)) {
                    pd.setMultiple(true);
                } else if (this.currentTokenEquals(Lexer.COPY)) {
                    pd.setOnParentVersion(1);
                } else if (this.currentTokenEquals(Lexer.VERSION)) {
                    pd.setOnParentVersion(2);
                } else if (this.currentTokenEquals(Lexer.INITIALIZE)) {
                    pd.setOnParentVersion(3);
                } else if (this.currentTokenEquals(Lexer.COMPUTE)) {
                    pd.setOnParentVersion(4);
                } else if (this.currentTokenEquals(Lexer.IGNORE)) {
                    pd.setOnParentVersion(5);
                } else if (this.currentTokenEquals(Lexer.ABORT)) {
                    pd.setOnParentVersion(6);
                } else if (this.currentTokenEquals(Lexer.NOFULLTEXT)) {
                    pd.setFullTextSearchable(false);
                } else if (this.currentTokenEquals(Lexer.NOQUERYORDER)) {
                    pd.setQueryOrderable(false);
                } else if (this.currentTokenEquals(Lexer.QUERYOPS)) {
                    this.doPropertyQueryOperators(pd);
                }
                this.nextToken();
            }
        }
        catch (RepositoryException e) {
            this.lexer.fail("Error setting property attribute of " + pd.getName() + " to " + this.currentToken, e);
        }
    }

    private void doPropertyQueryOperators(DefinitionBuilderFactory.AbstractPropertyDefinitionBuilder<T> pd) throws ParseException {
        if (!this.currentTokenEquals(Lexer.QUERYOPS)) {
            return;
        }
        this.nextToken();
        String[] ops = this.currentToken.split(",");
        LinkedList<String> queryOps = new LinkedList<String>();
        for (String op : ops) {
            String s = op.trim();
            if (s.equals("=")) {
                queryOps.add("jcr.operator.equal.to");
                continue;
            }
            if (s.equals("<>")) {
                queryOps.add("jcr.operator.not.equal.to");
                continue;
            }
            if (s.equals("<")) {
                queryOps.add("jcr.operator.less.than");
                continue;
            }
            if (s.equals("<=")) {
                queryOps.add("jcr.operator.less.than.or.equal.to");
                continue;
            }
            if (s.equals(">")) {
                queryOps.add("jcr.operator.greater.than");
                continue;
            }
            if (s.equals(">=")) {
                queryOps.add("jcr.operator.greater.than.or.equal.to");
                continue;
            }
            if (s.equals("LIKE")) {
                queryOps.add("jcr.operator.like");
                continue;
            }
            this.lexer.fail("'" + s + "' is not a valid query operator");
        }
        try {
            pd.setAvailableQueryOperators(queryOps.toArray(new String[queryOps.size()]));
        }
        catch (RepositoryException e) {
            this.lexer.fail("Error query operators for " + pd.getName() + " to " + this.currentToken, e);
        }
    }

    private void doPropertyDefaultValue(DefinitionBuilderFactory.AbstractPropertyDefinitionBuilder<T> pd) throws ParseException {
        if (!this.currentTokenEquals('=')) {
            return;
        }
        do {
            this.nextToken();
            try {
                pd.addDefaultValues(this.currentToken);
            }
            catch (RepositoryException e) {
                this.lexer.fail("Error adding default value for " + pd.getName() + " to " + this.currentToken, e);
            }
            this.nextToken();
        } while (this.currentTokenEquals(','));
    }

    private void doPropertyValueConstraints(DefinitionBuilderFactory.AbstractPropertyDefinitionBuilder<T> pd) throws ParseException {
        if (!this.currentTokenEquals('<')) {
            return;
        }
        do {
            this.nextToken();
            try {
                pd.addValueConstraint(this.currentToken);
            }
            catch (RepositoryException e) {
                this.lexer.fail("Error adding value constraint for " + pd.getName() + " to " + this.currentToken, e);
            }
            this.nextToken();
        } while (this.currentTokenEquals(','));
    }

    private void doChildNodeDefinition(DefinitionBuilderFactory.AbstractNodeDefinitionBuilder<T> nd, DefinitionBuilderFactory.AbstractNodeTypeDefinitionBuilder<T> ntd) throws ParseException {
        try {
            nd.setName(this.currentToken);
        }
        catch (RepositoryException e) {
            this.lexer.fail("Invalid child node name '" + this.currentToken, e);
        }
        this.nextToken();
        this.doChildNodeRequiredTypes(nd);
        this.doChildNodeDefaultType(nd);
        this.doChildNodeAttributes(nd, ntd);
    }

    private void doChildNodeRequiredTypes(DefinitionBuilderFactory.AbstractNodeDefinitionBuilder<T> nd) throws ParseException {
        if (!this.currentTokenEquals('(')) {
            return;
        }
        do {
            this.nextToken();
            try {
                nd.addRequiredPrimaryType(this.currentToken);
            }
            catch (RepositoryException e) {
                this.lexer.fail("Error setting required primary type of " + nd.getName() + " to " + this.currentToken, e);
            }
            this.nextToken();
        } while (this.currentTokenEquals(','));
        this.nextToken();
    }

    private void doChildNodeDefaultType(DefinitionBuilderFactory.AbstractNodeDefinitionBuilder<T> nd) throws ParseException {
        if (!this.currentTokenEquals('=')) {
            return;
        }
        this.nextToken();
        try {
            nd.setDefaultPrimaryType(this.currentToken);
        }
        catch (RepositoryException e) {
            this.lexer.fail("Error setting default primary type of " + nd.getName() + " to " + this.currentToken, e);
        }
        this.nextToken();
    }

    private void doChildNodeAttributes(DefinitionBuilderFactory.AbstractNodeDefinitionBuilder<T> nd, DefinitionBuilderFactory.AbstractNodeTypeDefinitionBuilder<T> ntd) throws ParseException {
        try {
            while (this.currentTokenEquals(Lexer.NODE_ATTRIBUTE)) {
                if (this.currentTokenEquals(Lexer.PRIMARY)) {
                    ntd.setPrimaryItemName(nd.getName());
                } else if (this.currentTokenEquals(Lexer.AUTOCREATED)) {
                    nd.setAutoCreated(true);
                } else if (this.currentTokenEquals(Lexer.MANDATORY)) {
                    nd.setMandatory(true);
                } else if (this.currentTokenEquals(Lexer.PROTECTED)) {
                    nd.setProtected(true);
                } else if (this.currentTokenEquals(Lexer.SNS)) {
                    nd.setAllowsSameNameSiblings(true);
                } else if (this.currentTokenEquals(Lexer.COPY)) {
                    nd.setOnParentVersion(1);
                } else if (this.currentTokenEquals(Lexer.VERSION)) {
                    nd.setOnParentVersion(2);
                } else if (this.currentTokenEquals(Lexer.INITIALIZE)) {
                    nd.setOnParentVersion(3);
                } else if (this.currentTokenEquals(Lexer.COMPUTE)) {
                    nd.setOnParentVersion(4);
                } else if (this.currentTokenEquals(Lexer.IGNORE)) {
                    nd.setOnParentVersion(5);
                } else if (this.currentTokenEquals(Lexer.ABORT)) {
                    nd.setOnParentVersion(6);
                }
                this.nextToken();
            }
        }
        catch (RepositoryException e) {
            this.lexer.fail("Error setting child node attribute of " + nd.getName() + " to " + this.currentToken, e);
        }
    }

    private void nextToken() throws ParseException {
        this.currentToken = this.lexer.getNextToken();
    }

    private boolean currentTokenEquals(String[] s) {
        for (String value : s) {
            if (!this.currentToken.equalsIgnoreCase(value)) continue;
            return true;
        }
        return false;
    }

    private boolean currentTokenEquals(char c) {
        return this.currentToken.length() == 1 && this.currentToken.charAt(0) == c;
    }

    private boolean currentTokenEquals(String s) {
        return this.currentToken.equals(s);
    }
}

