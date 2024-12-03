/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.ASTFactory
 *  antlr.Token
 *  antlr.collections.AST
 */
package org.hibernate.hql.internal.ast;

import antlr.ASTFactory;
import antlr.Token;
import antlr.collections.AST;
import java.lang.reflect.Constructor;
import org.hibernate.hql.internal.antlr.HqlSqlTokenTypes;
import org.hibernate.hql.internal.ast.HqlSqlWalker;
import org.hibernate.hql.internal.ast.tree.AggregateNode;
import org.hibernate.hql.internal.ast.tree.BetweenOperatorNode;
import org.hibernate.hql.internal.ast.tree.BinaryArithmeticOperatorNode;
import org.hibernate.hql.internal.ast.tree.BinaryLogicOperatorNode;
import org.hibernate.hql.internal.ast.tree.BooleanLiteralNode;
import org.hibernate.hql.internal.ast.tree.CastFunctionNode;
import org.hibernate.hql.internal.ast.tree.CollectionFunction;
import org.hibernate.hql.internal.ast.tree.ConstructorNode;
import org.hibernate.hql.internal.ast.tree.CountNode;
import org.hibernate.hql.internal.ast.tree.DeleteStatement;
import org.hibernate.hql.internal.ast.tree.DotNode;
import org.hibernate.hql.internal.ast.tree.FkRefNode;
import org.hibernate.hql.internal.ast.tree.FromClause;
import org.hibernate.hql.internal.ast.tree.FromElement;
import org.hibernate.hql.internal.ast.tree.IdentNode;
import org.hibernate.hql.internal.ast.tree.ImpliedFromElement;
import org.hibernate.hql.internal.ast.tree.InLogicOperatorNode;
import org.hibernate.hql.internal.ast.tree.IndexNode;
import org.hibernate.hql.internal.ast.tree.InitializeableNode;
import org.hibernate.hql.internal.ast.tree.InsertStatement;
import org.hibernate.hql.internal.ast.tree.IntoClause;
import org.hibernate.hql.internal.ast.tree.IsNotNullLogicOperatorNode;
import org.hibernate.hql.internal.ast.tree.IsNullLogicOperatorNode;
import org.hibernate.hql.internal.ast.tree.JavaConstantNode;
import org.hibernate.hql.internal.ast.tree.LiteralNode;
import org.hibernate.hql.internal.ast.tree.MapEntryNode;
import org.hibernate.hql.internal.ast.tree.MapKeyNode;
import org.hibernate.hql.internal.ast.tree.MapValueNode;
import org.hibernate.hql.internal.ast.tree.MethodNode;
import org.hibernate.hql.internal.ast.tree.NullNode;
import org.hibernate.hql.internal.ast.tree.OrderByClause;
import org.hibernate.hql.internal.ast.tree.ParameterNode;
import org.hibernate.hql.internal.ast.tree.QueryNode;
import org.hibernate.hql.internal.ast.tree.ResultVariableRefNode;
import org.hibernate.hql.internal.ast.tree.SearchedCaseNode;
import org.hibernate.hql.internal.ast.tree.SelectClause;
import org.hibernate.hql.internal.ast.tree.SelectExpressionImpl;
import org.hibernate.hql.internal.ast.tree.SessionFactoryAwareNode;
import org.hibernate.hql.internal.ast.tree.SimpleCaseNode;
import org.hibernate.hql.internal.ast.tree.SqlFragment;
import org.hibernate.hql.internal.ast.tree.SqlNode;
import org.hibernate.hql.internal.ast.tree.UnaryArithmeticNode;
import org.hibernate.hql.internal.ast.tree.UnaryLogicOperatorNode;
import org.hibernate.hql.internal.ast.tree.UpdateStatement;

public class SqlASTFactory
extends ASTFactory
implements HqlSqlTokenTypes {
    private HqlSqlWalker walker;

    public SqlASTFactory(HqlSqlWalker walker) {
        this.walker = walker;
    }

    public Class getASTNodeType(int tokenType) {
        switch (tokenType) {
            case 46: 
            case 91: {
                return QueryNode.class;
            }
            case 51: {
                return UpdateStatement.class;
            }
            case 13: {
                return DeleteStatement.class;
            }
            case 30: {
                return InsertStatement.class;
            }
            case 31: {
                return IntoClause.class;
            }
            case 23: {
                return FromClause.class;
            }
            case 141: {
                return FromElement.class;
            }
            case 142: {
                return ImpliedFromElement.class;
            }
            case 15: {
                return DotNode.class;
            }
            case 83: {
                return IndexNode.class;
            }
            case 111: 
            case 148: {
                return IdentNode.class;
            }
            case 158: {
                return ResultVariableRefNode.class;
            }
            case 150: {
                return SqlFragment.class;
            }
            case 86: {
                return MethodNode.class;
            }
            case 78: {
                return CastFunctionNode.class;
            }
            case 17: 
            case 28: {
                return CollectionFunction.class;
            }
            case 145: {
                return SelectClause.class;
            }
            case 152: {
                return SelectExpressionImpl.class;
            }
            case 74: {
                return AggregateNode.class;
            }
            case 12: {
                return CountNode.class;
            }
            case 76: {
                return ConstructorNode.class;
            }
            case 101: 
            case 102: 
            case 103: 
            case 104: 
            case 105: 
            case 130: 
            case 133: {
                return LiteralNode.class;
            }
            case 20: 
            case 50: {
                return BooleanLiteralNode.class;
            }
            case 106: {
                return JavaConstantNode.class;
            }
            case 42: {
                return OrderByClause.class;
            }
            case 122: 
            case 123: 
            case 124: 
            case 125: 
            case 126: {
                return BinaryArithmeticOperatorNode.class;
            }
            case 96: 
            case 97: {
                return UnaryArithmeticNode.class;
            }
            case 77: {
                return SimpleCaseNode.class;
            }
            case 57: {
                return SearchedCaseNode.class;
            }
            case 132: 
            case 156: {
                return ParameterNode.class;
            }
            case 35: 
            case 89: 
            case 108: 
            case 115: 
            case 117: 
            case 118: 
            case 119: 
            case 120: {
                return BinaryLogicOperatorNode.class;
            }
            case 27: 
            case 88: {
                return InLogicOperatorNode.class;
            }
            case 10: 
            case 87: {
                return BetweenOperatorNode.class;
            }
            case 85: {
                return IsNullLogicOperatorNode.class;
            }
            case 84: {
                return IsNotNullLogicOperatorNode.class;
            }
            case 19: {
                return UnaryLogicOperatorNode.class;
            }
            case 71: {
                return MapKeyNode.class;
            }
            case 72: {
                return MapValueNode.class;
            }
            case 73: {
                return MapEntryNode.class;
            }
            case 40: {
                return NullNode.class;
            }
            case 22: {
                return FkRefNode.class;
            }
        }
        return SqlNode.class;
    }

    protected AST createUsingCtor(Token token, String className) {
        AST t;
        try {
            Class<?> c = Class.forName(className);
            Class[] tokenArgType = new Class[]{Token.class};
            Constructor<?> ctor = c.getConstructor(tokenArgType);
            if (ctor != null) {
                t = (AST)ctor.newInstance(token);
                this.initializeSqlNode(t);
            } else {
                t = this.create(c);
            }
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Invalid class or can't make instance, " + className);
        }
        return t;
    }

    private void initializeSqlNode(AST t) {
        if (t instanceof InitializeableNode) {
            InitializeableNode initializeableNode = (InitializeableNode)t;
            initializeableNode.initialize(this.walker);
        }
        if (t instanceof SessionFactoryAwareNode) {
            ((SessionFactoryAwareNode)t).setSessionFactory(this.walker.getSessionFactoryHelper().getFactory());
        }
    }

    protected AST create(Class c) {
        AST t;
        try {
            t = (AST)c.newInstance();
            this.initializeSqlNode(t);
        }
        catch (Exception e) {
            this.error("Can't create AST Node " + c.getName());
            return null;
        }
        return t;
    }
}

