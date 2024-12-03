/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.antlr.treewalker;

import java.util.ArrayList;
import java.util.Collections;
import org.codehaus.groovy.antlr.GroovySourceAST;
import org.codehaus.groovy.antlr.treewalker.TraversalHelper;
import org.codehaus.groovy.antlr.treewalker.Visitor;

public class SourceCodeTraversal
extends TraversalHelper {
    public SourceCodeTraversal(Visitor visitor) {
        super(visitor);
    }

    @Override
    public void setUp(GroovySourceAST t) {
        super.setUp(t);
        this.unvisitedNodes = new ArrayList();
        this.traverse(t);
        Collections.sort(this.unvisitedNodes);
    }

    private void traverse(GroovySourceAST t) {
        GroovySourceAST sibling;
        GroovySourceAST child;
        if (t == null) {
            return;
        }
        if (this.unvisitedNodes != null) {
            this.unvisitedNodes.add(t);
        }
        if ((child = (GroovySourceAST)t.getFirstChild()) != null) {
            this.traverse(child);
        }
        if ((sibling = (GroovySourceAST)t.getNextSibling()) != null) {
            this.traverse(sibling);
        }
    }

    @Override
    protected void accept(GroovySourceAST currentNode) {
        if (currentNode != null && this.unvisitedNodes != null && !this.unvisitedNodes.isEmpty()) {
            GroovySourceAST t = currentNode;
            if (!this.unvisitedNodes.contains(currentNode)) {
                return;
            }
            this.push(t);
            switch (t.getType()) {
                case 97: {
                    this.accept_FirstChild_v_SecondChild_v_ThirdChild_v(t);
                    break;
                }
                case 32: 
                case 158: {
                    this.accept_FirstChild_v_SecondChildsChildren_v(t);
                    break;
                }
                case 66: {
                    this.accept_v_FirstChild_2ndv_SecondChild_v___LastChild_v(t);
                    break;
                }
                case 20: 
                case 33: 
                case 48: 
                case 70: 
                case 72: 
                case 73: 
                case 75: 
                case 77: {
                    this.accept_v_FirstChild_v_SecondChild_v___LastChild_v(t);
                    break;
                }
                case 47: {
                    this.accept_v_FirstChild_SecondChild_v_ThirdChild_v(t);
                    break;
                }
                case 24: {
                    this.accept_SecondChild_v_ThirdChild_v(t);
                    break;
                }
                case 6: 
                case 7: 
                case 8: 
                case 9: 
                case 21: 
                case 28: 
                case 29: 
                case 60: 
                case 62: {
                    this.accept_v_AllChildren_v(t);
                    break;
                }
                case 67: 
                case 124: 
                case 162: 
                case 163: 
                case 164: 
                case 165: 
                case 166: 
                case 167: 
                case 168: 
                case 169: 
                case 170: 
                case 171: 
                case 172: 
                case 173: 
                case 178: 
                case 179: 
                case 180: 
                case 181: 
                case 184: {
                    if (t.childAt(1) != null) {
                        this.accept_FirstChild_v_RestOfTheChildren(t);
                        break;
                    }
                    this.accept_v_FirstChild_v_RestOfTheChildren(t);
                    break;
                }
                case 68: {
                    this.accept_FirstSecondAndThirdChild_v_v_ForthChild(t);
                    break;
                }
                case 13: 
                case 14: 
                case 15: 
                case 16: 
                case 22: 
                case 46: 
                case 54: 
                case 61: 
                case 64: 
                case 89: 
                case 90: 
                case 100: 
                case 102: 
                case 103: 
                case 113: 
                case 114: 
                case 125: 
                case 134: 
                case 142: 
                case 148: 
                case 149: 
                case 154: 
                case 155: 
                case 156: 
                case 175: 
                case 176: 
                case 177: 
                case 185: 
                case 186: 
                case 187: 
                case 188: 
                case 189: 
                case 191: 
                case 192: 
                case 194: {
                    this.accept_FirstChild_v_RestOfTheChildren(t);
                    break;
                }
                case 27: 
                case 45: {
                    if (t.getNumberOfChildren() == 2 && t.childAt(1) != null && t.childAt(1).getType() == 50) {
                        this.accept_FirstChild_v_SecondChild(t);
                        break;
                    }
                    GroovySourceAST lastChild = t.childAt(t.getNumberOfChildren() - 1);
                    if (lastChild != null && lastChild.getType() == 50) {
                        this.accept_FirstChild_v_RestOfTheChildren_v_LastChild(t);
                        break;
                    }
                    this.accept_FirstChild_v_RestOfTheChildren_v(t);
                    break;
                }
                case 23: 
                case 139: {
                    this.accept_v_FirstChildsFirstChild_v_RestOfTheChildren(t);
                    break;
                }
                case 137: {
                    this.accept_v_FirstChildsFirstChild_v_Child2_Child3_v_Child4_v___v_LastChild(t);
                    break;
                }
                case 50: {
                    if (t.childAt(0) != null && t.childAt(0).getType() == 51) {
                        this.accept_v_AllChildren_v(t);
                        break;
                    }
                    this.accept_v_FirstChild_v_RestOfTheChildren_v(t);
                    break;
                }
                case 59: 
                case 140: 
                case 141: 
                case 159: {
                    this.accept_v_FirstChild_v_RestOfTheChildren_v(t);
                    break;
                }
                case 5: 
                case 65: 
                case 121: 
                case 147: 
                case 151: 
                case 153: {
                    this.accept_v_FirstChild_v_RestOfTheChildren(t);
                    break;
                }
                case 74: {
                    this.accept_v_Siblings_v(t);
                    break;
                }
                default: {
                    this.accept_v_FirstChild_v(t);
                }
            }
            this.pop();
        }
    }
}

