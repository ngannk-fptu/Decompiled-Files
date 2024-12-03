/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import java.util.ArrayList;
import java.util.List;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Kit;
import org.mozilla.javascript.Node;
import org.mozilla.javascript.ObjArray;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Jump;
import org.mozilla.javascript.ast.Scope;
import org.mozilla.javascript.ast.ScriptNode;

public class NodeTransformer {
    private ObjArray loops;
    private ObjArray loopEnds;
    private boolean hasFinally;

    public final void transform(ScriptNode tree, CompilerEnvirons env) {
        this.transform(tree, false, env);
    }

    public final void transform(ScriptNode tree, boolean inStrictMode, CompilerEnvirons env) {
        boolean useStrictMode = inStrictMode;
        if (env.getLanguageVersion() >= 200 && tree.isInStrictMode()) {
            useStrictMode = true;
        }
        this.transformCompilationUnit(tree, useStrictMode);
        for (int i = 0; i != tree.getFunctionCount(); ++i) {
            FunctionNode fn = tree.getFunctionNode(i);
            this.transform(fn, useStrictMode, env);
        }
    }

    private void transformCompilationUnit(ScriptNode tree, boolean inStrictMode) {
        this.loops = new ObjArray();
        this.loopEnds = new ObjArray();
        this.hasFinally = false;
        boolean createScopeObjects = tree.getType() != 113 || ((FunctionNode)tree).requiresActivation();
        tree.flattenSymbolTable(!createScopeObjects);
        this.transformCompilationUnit_r(tree, tree, tree, createScopeObjects, inStrictMode);
    }

    private void transformCompilationUnit_r(ScriptNode tree, Node parent, Scope scope, boolean createScopeObjects, boolean inStrictMode) {
        Node node = null;
        block17: while (true) {
            Scope newScope;
            Node previous = null;
            if (node == null) {
                node = parent.getFirstChild();
            } else {
                previous = node;
                node = node.getNext();
            }
            if (node == null) break;
            int type = node.getType();
            if (createScopeObjects && (type == 133 || type == 136 || type == 161) && node instanceof Scope && (newScope = (Scope)node).getSymbolTable() != null) {
                Node let = new Node(type == 161 ? 162 : 157);
                Node innerLet = new Node(157);
                let.addChildToBack(innerLet);
                for (String name : newScope.getSymbolTable().keySet()) {
                    innerLet.addChildToBack(Node.newString(39, name));
                }
                newScope.setSymbolTable(null);
                Node oldNode = node;
                node = NodeTransformer.replaceCurrent(parent, previous, node, let);
                type = node.getType();
                let.addChildToBack(oldNode);
            }
            switch (type) {
                case 118: 
                case 134: 
                case 136: {
                    this.loops.push(node);
                    this.loopEnds.push(((Jump)node).target);
                    break;
                }
                case 127: {
                    this.loops.push(node);
                    Node leave = node.getNext();
                    if (leave.getType() != 3) {
                        Kit.codeBug();
                    }
                    this.loopEnds.push(leave);
                    break;
                }
                case 84: {
                    Jump jump = (Jump)node;
                    Node finallytarget = jump.getFinally();
                    if (finallytarget == null) break;
                    this.hasFinally = true;
                    this.loops.push(node);
                    this.loopEnds.push(finallytarget);
                    break;
                }
                case 3: 
                case 135: {
                    if (this.loopEnds.isEmpty() || this.loopEnds.peek() != node) break;
                    this.loopEnds.pop();
                    this.loops.pop();
                    break;
                }
                case 73: 
                case 169: {
                    ((FunctionNode)tree).addResumptionPoint(node);
                    break;
                }
                case 4: {
                    Node n;
                    boolean isGenerator;
                    boolean bl = isGenerator = tree.getType() == 113 && ((FunctionNode)tree).isGenerator();
                    if (isGenerator) {
                        node.putIntProp(20, 1);
                    }
                    if (!this.hasFinally) break;
                    Node unwindBlock = null;
                    for (int i = this.loops.size() - 1; i >= 0; --i) {
                        Node unwind;
                        n = (Node)this.loops.get(i);
                        int elemtype = n.getType();
                        if (elemtype != 84 && elemtype != 127) continue;
                        if (elemtype == 84) {
                            Node jsrtarget;
                            Jump jsrnode = new Jump(139);
                            jsrnode.target = jsrtarget = ((Jump)n).getFinally();
                            unwind = jsrnode;
                        } else {
                            unwind = new Node(3);
                        }
                        if (unwindBlock == null) {
                            unwindBlock = new Node(133, node.getLineno());
                        }
                        unwindBlock.addChildToBack(unwind);
                    }
                    if (unwindBlock == null) break;
                    Node returnNode = node;
                    Node returnExpr = returnNode.getFirstChild();
                    node = NodeTransformer.replaceCurrent(parent, previous, node, unwindBlock);
                    if (returnExpr == null || isGenerator) {
                        unwindBlock.addChildToBack(returnNode);
                        continue block17;
                    }
                    Node store = new Node(138, returnExpr);
                    unwindBlock.addChildToFront(store);
                    returnNode = new Node(65);
                    unwindBlock.addChildToBack(returnNode);
                    this.transformCompilationUnit_r(tree, store, scope, createScopeObjects, inStrictMode);
                    continue block17;
                }
                case 124: 
                case 125: {
                    Node n;
                    Jump jump = (Jump)node;
                    Jump jumpStatement = jump.getJumpStatement();
                    if (jumpStatement == null) {
                        Kit.codeBug();
                    }
                    int i = this.loops.size();
                    while (true) {
                        if (i == 0) {
                            throw Kit.codeBug();
                        }
                        if ((n = (Node)this.loops.get(--i)) == jumpStatement) break;
                        int elemtype = n.getType();
                        if (elemtype == 127) {
                            Node leave = new Node(3);
                            previous = NodeTransformer.addBeforeCurrent(parent, previous, node, leave);
                            continue;
                        }
                        if (elemtype != 84) continue;
                        Jump tryNode = (Jump)n;
                        Jump jsrFinally = new Jump(139);
                        jsrFinally.target = tryNode.getFinally();
                        previous = NodeTransformer.addBeforeCurrent(parent, previous, node, jsrFinally);
                    }
                    jump.target = type == 124 ? jumpStatement.target : jumpStatement.getContinue();
                    jump.setType(5);
                    break;
                }
                case 38: {
                    this.visitCall(node, tree);
                    break;
                }
                case 30: {
                    this.visitNew(node, tree);
                    break;
                }
                case 157: 
                case 162: {
                    Node child = node.getFirstChild();
                    if (child.getType() == 157) {
                        boolean createWith = tree.getType() != 113 || ((FunctionNode)tree).requiresActivation();
                        node = this.visitLet(createWith, parent, previous, node);
                        break;
                    }
                }
                case 126: 
                case 158: {
                    Node result = new Node(133);
                    Node cursor = node.getFirstChild();
                    while (cursor != null) {
                        Node n = cursor;
                        cursor = cursor.getNext();
                        if (n.getType() == 39) {
                            if (!n.hasChildren()) continue;
                            Node init = n.getFirstChild();
                            n.removeChild(init);
                            n.setType(49);
                            n = new Node(type == 158 ? 159 : 8, n, init);
                        } else if (n.getType() != 162) {
                            throw Kit.codeBug();
                        }
                        Node pop = new Node(137, n, node.getLineno());
                        result.addChildToBack(pop);
                    }
                    node = NodeTransformer.replaceCurrent(parent, previous, node, result);
                    break;
                }
                case 141: {
                    Scope defining = scope.getDefiningScope(node.getString());
                    if (defining == null) break;
                    node.setScope(defining);
                    break;
                }
                case 7: 
                case 32: {
                    Node child = node.getFirstChild();
                    if (type == 7) {
                        while (child.getType() == 26) {
                            child = child.getFirstChild();
                        }
                        if (child.getType() == 12 || child.getType() == 13) {
                            Node first = child.getFirstChild();
                            Node last = child.getLastChild();
                            if (first.getType() == 39 && first.getString().equals("undefined")) {
                                child = last;
                            } else if (last.getType() == 39 && last.getString().equals("undefined")) {
                                child = first;
                            }
                        }
                    }
                    if (child.getType() != 33) break;
                    child.setType(34);
                    break;
                }
                case 8: {
                    if (inStrictMode) {
                        node.setType(74);
                    }
                }
                case 31: 
                case 39: 
                case 159: {
                    String name;
                    Scope defining;
                    Node nameSource;
                    Node n;
                    if (createScopeObjects) break;
                    if (type == 39) {
                        nameSource = node;
                    } else {
                        nameSource = node.getFirstChild();
                        if (nameSource.getType() != 49) {
                            if (type == 31) break;
                            throw Kit.codeBug();
                        }
                    }
                    if (nameSource.getScope() != null || (defining = scope.getDefiningScope(name = nameSource.getString())) == null) break;
                    nameSource.setScope(defining);
                    if (type == 39) {
                        node.setType(55);
                        break;
                    }
                    if (type == 8 || type == 74) {
                        node.setType(56);
                        nameSource.setType(41);
                        break;
                    }
                    if (type == 159) {
                        node.setType(160);
                        nameSource.setType(41);
                        break;
                    }
                    if (type == 31) {
                        n = new Node(44);
                        node = NodeTransformer.replaceCurrent(parent, previous, node, n);
                        break;
                    }
                    throw Kit.codeBug();
                }
            }
            this.transformCompilationUnit_r(tree, node, node instanceof Scope ? (Scope)node : scope, createScopeObjects, inStrictMode);
        }
    }

    protected void visitNew(Node node, ScriptNode tree) {
    }

    protected void visitCall(Node node, ScriptNode tree) {
    }

    protected Node visitLet(boolean createWith, Node parent, Node previous, Node scopeNode) {
        Node result;
        boolean isExpression;
        Node vars = scopeNode.getFirstChild();
        Node body = vars.getNext();
        scopeNode.removeChild(vars);
        scopeNode.removeChild(body);
        boolean bl = isExpression = scopeNode.getType() == 162;
        if (createWith) {
            result = new Node(isExpression ? 163 : 133);
            result = NodeTransformer.replaceCurrent(parent, previous, scopeNode, result);
            ArrayList<Object> list = new ArrayList<Object>();
            Node objectLiteral = new Node(67);
            for (Node v = vars.getFirstChild(); v != null; v = v.getNext()) {
                Node current = v;
                if (current.getType() == 162) {
                    List destructuringNames = (List)current.getProp(22);
                    Node c = current.getFirstChild();
                    if (c.getType() != 157) {
                        throw Kit.codeBug();
                    }
                    body = isExpression ? new Node(92, c.getNext(), body) : new Node(133, new Node(137, c.getNext()), body);
                    if (destructuringNames != null) {
                        list.addAll(destructuringNames);
                        for (int i = 0; i < destructuringNames.size(); ++i) {
                            objectLiteral.addChildToBack(new Node(130, Node.newNumber(0.0)));
                        }
                    }
                    current = c.getFirstChild();
                }
                if (current.getType() != 39) {
                    throw Kit.codeBug();
                }
                list.add(ScriptRuntime.getIndexObject(current.getString()));
                Node init = current.getFirstChild();
                if (init == null) {
                    init = new Node(130, Node.newNumber(0.0));
                }
                objectLiteral.addChildToBack(init);
            }
            objectLiteral.putProp(12, list.toArray());
            Node newVars = new Node(2, objectLiteral);
            result.addChildToBack(newVars);
            result.addChildToBack(new Node(127, body));
            result.addChildToBack(new Node(3));
        } else {
            Scope scopeParent;
            result = new Node(isExpression ? 92 : 133);
            result = NodeTransformer.replaceCurrent(parent, previous, scopeNode, result);
            Node newVars = new Node(92);
            for (Node v = vars.getFirstChild(); v != null; v = v.getNext()) {
                Node current = v;
                if (current.getType() == 162) {
                    Node c = current.getFirstChild();
                    if (c.getType() != 157) {
                        throw Kit.codeBug();
                    }
                    body = isExpression ? new Node(92, c.getNext(), body) : new Node(133, new Node(137, c.getNext()), body);
                    Scope.joinScopes((Scope)current, (Scope)scopeNode);
                    current = c.getFirstChild();
                }
                if (current.getType() != 39) {
                    throw Kit.codeBug();
                }
                Node stringNode = Node.newString(current.getString());
                stringNode.setScope((Scope)scopeNode);
                Node init = current.getFirstChild();
                if (init == null) {
                    init = new Node(130, Node.newNumber(0.0));
                }
                newVars.addChildToBack(new Node(56, stringNode, init));
            }
            if (isExpression) {
                result.addChildToBack(newVars);
                scopeNode.setType(92);
                result.addChildToBack(scopeNode);
                scopeNode.addChildToBack(body);
                if (body instanceof Scope) {
                    scopeParent = ((Scope)body).getParentScope();
                    ((Scope)body).setParentScope((Scope)scopeNode);
                    ((Scope)scopeNode).setParentScope(scopeParent);
                }
            } else {
                result.addChildToBack(new Node(137, newVars));
                scopeNode.setType(133);
                result.addChildToBack(scopeNode);
                scopeNode.addChildrenToBack(body);
                if (body instanceof Scope) {
                    scopeParent = ((Scope)body).getParentScope();
                    ((Scope)body).setParentScope((Scope)scopeNode);
                    ((Scope)scopeNode).setParentScope(scopeParent);
                }
            }
        }
        return result;
    }

    private static Node addBeforeCurrent(Node parent, Node previous, Node current, Node toAdd) {
        if (previous == null) {
            if (current != parent.getFirstChild()) {
                Kit.codeBug();
            }
            parent.addChildToFront(toAdd);
        } else {
            if (current != previous.getNext()) {
                Kit.codeBug();
            }
            parent.addChildAfter(toAdd, previous);
        }
        return toAdd;
    }

    private static Node replaceCurrent(Node parent, Node previous, Node current, Node replacement) {
        if (previous == null) {
            if (current != parent.getFirstChild()) {
                Kit.codeBug();
            }
            parent.replaceChild(current, replacement);
        } else if (previous.next == current) {
            parent.replaceChildAfter(previous, replacement);
        } else {
            parent.replaceChild(current, replacement);
        }
        return replacement;
    }
}

