/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.cnd;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.jcr.NamespaceRegistry;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.ValueFactory;
import javax.jcr.Workspace;
import javax.jcr.nodetype.InvalidNodeTypeDefinitionException;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeDefinition;
import javax.jcr.nodetype.NodeTypeExistsException;
import javax.jcr.nodetype.NodeTypeIterator;
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.nodetype.NodeTypeTemplate;
import org.apache.jackrabbit.commons.cnd.CompactNodeTypeDefReader;
import org.apache.jackrabbit.commons.cnd.ParseException;
import org.apache.jackrabbit.commons.cnd.TemplateBuilderFactory;

public final class CndImporter {
    private CndImporter() {
    }

    public static NodeType[] registerNodeTypes(Reader cnd, Session session) throws InvalidNodeTypeDefinitionException, NodeTypeExistsException, UnsupportedRepositoryOperationException, ParseException, RepositoryException, IOException {
        Workspace wsp = session.getWorkspace();
        return CndImporter.registerNodeTypes(cnd, "cnd input stream", wsp.getNodeTypeManager(), wsp.getNamespaceRegistry(), session.getValueFactory(), false);
    }

    public static NodeType[] registerNodeTypes(Reader cnd, Session session, boolean reregisterExisting) throws InvalidNodeTypeDefinitionException, NodeTypeExistsException, UnsupportedRepositoryOperationException, ParseException, RepositoryException, IOException {
        Workspace wsp = session.getWorkspace();
        return CndImporter.registerNodeTypes(cnd, "cnd input stream", wsp.getNodeTypeManager(), wsp.getNamespaceRegistry(), session.getValueFactory(), reregisterExisting);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static NodeType[] registerNodeTypes(Reader cnd, String systemId, NodeTypeManager nodeTypeManager, NamespaceRegistry namespaceRegistry, ValueFactory valueFactory, boolean reregisterExisting) throws ParseException, InvalidNodeTypeDefinitionException, NodeTypeExistsException, UnsupportedRepositoryOperationException, RepositoryException, IOException {
        try {
            TemplateBuilderFactory factory = new TemplateBuilderFactory(nodeTypeManager, valueFactory, namespaceRegistry);
            CompactNodeTypeDefReader<NodeTypeTemplate, NamespaceRegistry> cndReader = new CompactNodeTypeDefReader<NodeTypeTemplate, NamespaceRegistry>(cnd, systemId, factory);
            HashMap<String, NodeTypeTemplate> templates = new HashMap<String, NodeTypeTemplate>();
            for (NodeTypeTemplate nodeTypeTemplate : cndReader.getNodeTypeDefinitions()) {
                templates.put(nodeTypeTemplate.getName(), nodeTypeTemplate);
            }
            ArrayList<NodeTypeTemplate> toRegister = new ArrayList<NodeTypeTemplate>(templates.size());
            for (NodeTypeTemplate ntt : templates.values()) {
                if (!reregisterExisting && nodeTypeManager.hasNodeType(ntt.getName())) continue;
                CndImporter.ensureNtBase(ntt, templates, nodeTypeManager);
                toRegister.add(ntt);
            }
            NodeTypeIterator nodeTypeIterator = nodeTypeManager.registerNodeTypes(toRegister.toArray(new NodeTypeTemplate[toRegister.size()]), true);
            NodeType[] nodeTypeArray = CndImporter.toArray(nodeTypeIterator);
            return nodeTypeArray;
        }
        finally {
            cnd.close();
        }
    }

    private static void ensureNtBase(NodeTypeTemplate ntt, Map<String, NodeTypeTemplate> templates, NodeTypeManager nodeTypeManager) throws RepositoryException {
        if (!ntt.isMixin() && !"nt:base".equals(ntt.getName())) {
            String[] supertypes = ntt.getDeclaredSupertypeNames();
            if (supertypes.length == 0) {
                ntt.setDeclaredSuperTypeNames(new String[]{"nt:base"});
            } else {
                boolean needsNtBase = true;
                for (String name : supertypes) {
                    NodeTypeDefinition std = templates.get(name);
                    if (std == null) {
                        std = nodeTypeManager.getNodeType(name);
                    }
                    if (std == null || std.isMixin()) continue;
                    needsNtBase = false;
                }
                if (needsNtBase) {
                    String[] withNtBase = new String[supertypes.length + 1];
                    withNtBase[0] = "nt:base";
                    System.arraycopy(supertypes, 0, withNtBase, 1, supertypes.length);
                    ntt.setDeclaredSuperTypeNames(withNtBase);
                }
            }
        }
    }

    private static NodeType[] toArray(NodeTypeIterator nodeTypes) {
        ArrayList<NodeType> nts = new ArrayList<NodeType>();
        while (nodeTypes.hasNext()) {
            nts.add(nodeTypes.nextNodeType());
        }
        return nts.toArray(new NodeType[nts.size()]);
    }
}

