/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.antlr;

import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.GenericsType;
import org.codehaus.groovy.ast.InnerClassNode;
import org.codehaus.groovy.ast.MixinNode;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ListExpression;

public class EnumHelper {
    private static final int FS = 24;
    private static final int PUBLIC_FS = 25;

    public static ClassNode makeEnumNode(String name, int modifiers, ClassNode[] interfaces, ClassNode outerClass) {
        ClassNode enumClass;
        modifiers = modifiers | 0x10 | 0x4000;
        if (outerClass == null) {
            enumClass = new ClassNode(name, modifiers, null, interfaces, MixinNode.EMPTY_ARRAY);
        } else {
            name = outerClass.getName() + "$" + name;
            enumClass = new InnerClassNode(outerClass, name, modifiers, null, interfaces, MixinNode.EMPTY_ARRAY);
        }
        GenericsType gt = new GenericsType(enumClass);
        ClassNode superClass = ClassHelper.makeWithoutCaching("java.lang.Enum");
        superClass.setGenericsTypes(new GenericsType[]{gt});
        enumClass.setSuperClass(superClass);
        superClass.setRedirect(ClassHelper.Enum_Type);
        return enumClass;
    }

    public static FieldNode addEnumConstant(ClassNode enumClass, String name, Expression init) {
        int modifiers = 16409;
        if (init != null && !(init instanceof ListExpression)) {
            ListExpression list = new ListExpression();
            list.addExpression(init);
            init = list;
        }
        FieldNode fn = new FieldNode(name, modifiers, enumClass.getPlainNodeReference(), enumClass, init);
        enumClass.addField(fn);
        return fn;
    }
}

