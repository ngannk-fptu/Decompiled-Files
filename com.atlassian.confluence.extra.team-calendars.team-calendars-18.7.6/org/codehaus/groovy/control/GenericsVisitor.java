/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.control;

import org.codehaus.groovy.ast.ClassCodeVisitorSupport;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.GenericsType;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.control.SourceUnit;

public class GenericsVisitor
extends ClassCodeVisitorSupport {
    private SourceUnit source;

    public GenericsVisitor(SourceUnit source) {
        this.source = source;
    }

    @Override
    protected SourceUnit getSourceUnit() {
        return this.source;
    }

    @Override
    public void visitClass(ClassNode node) {
        boolean error = this.checkWildcard(node);
        if (error) {
            return;
        }
        this.checkGenericsUsage(node.getUnresolvedSuperClass(false), node.getSuperClass());
        ClassNode[] interfaces = node.getInterfaces();
        for (int i = 0; i < interfaces.length; ++i) {
            this.checkGenericsUsage(interfaces[i], interfaces[i].redirect());
        }
        node.visitContents(this);
    }

    @Override
    public void visitField(FieldNode node) {
        ClassNode type = node.getType();
        this.checkGenericsUsage(type, type.redirect());
    }

    @Override
    public void visitMethod(MethodNode node) {
        Parameter[] parameters;
        for (Parameter param : parameters = node.getParameters()) {
            ClassNode paramType = param.getType();
            this.checkGenericsUsage(paramType, paramType.redirect());
        }
        ClassNode returnType = node.getReturnType();
        this.checkGenericsUsage(returnType, returnType.redirect());
    }

    private boolean checkWildcard(ClassNode cn) {
        ClassNode sn = cn.getUnresolvedSuperClass(false);
        if (sn == null) {
            return false;
        }
        GenericsType[] generics = sn.getGenericsTypes();
        if (generics == null) {
            return false;
        }
        boolean error = false;
        for (int i = 0; i < generics.length; ++i) {
            if (!generics[i].isWildcard()) continue;
            this.addError("A supertype may not specify a wildcard type", sn);
            error = true;
        }
        return error;
    }

    private void checkGenericsUsage(ClassNode n, ClassNode cn) {
        if (n.isGenericsPlaceHolder()) {
            return;
        }
        GenericsType[] nTypes = n.getGenericsTypes();
        GenericsType[] cnTypes = cn.getGenericsTypes();
        if (nTypes == null) {
            return;
        }
        if (cnTypes == null) {
            this.addError("The class " + n.getName() + " refers to the class " + cn.getName() + " and uses " + nTypes.length + " parameters, but the referred class takes no parameters", n);
            return;
        }
        if (nTypes.length != cnTypes.length) {
            this.addError("The class " + n.getName() + " refers to the class " + cn.getName() + " and uses " + nTypes.length + " parameters, but the referred class needs " + cnTypes.length, n);
            return;
        }
        for (int i = 0; i < nTypes.length; ++i) {
            ClassNode cnType;
            ClassNode nType = nTypes[i].getType();
            if (nType.isDerivedFrom(cnType = cnTypes[i].getType()) || cnType.isInterface() && nType.implementsInterface(cnType)) continue;
            this.addError("The type " + nTypes[i].getName() + " is not a valid substitute for the bounded parameter <" + GenericsVisitor.getPrintName(cnTypes[i]) + ">", n);
        }
    }

    private static String getPrintName(GenericsType gt) {
        String ret = gt.getName();
        ClassNode[] upperBounds = gt.getUpperBounds();
        ClassNode lowerBound = gt.getLowerBound();
        if (upperBounds != null) {
            ret = ret + " extends ";
            for (int i = 0; i < upperBounds.length; ++i) {
                ret = ret + GenericsVisitor.getPrintName(upperBounds[i]);
                if (i + 1 >= upperBounds.length) continue;
                ret = ret + " & ";
            }
        } else if (lowerBound != null) {
            ret = ret + " super " + GenericsVisitor.getPrintName(lowerBound);
        }
        return ret;
    }

    private static String getPrintName(ClassNode cn) {
        String ret = cn.getName();
        GenericsType[] gts = cn.getGenericsTypes();
        if (gts != null) {
            ret = ret + "<";
            for (int i = 0; i < gts.length; ++i) {
                if (i != 0) {
                    ret = ret + ",";
                }
                ret = ret + GenericsVisitor.getPrintName(gts[i]);
            }
            ret = ret + ">";
        }
        return ret;
    }
}

