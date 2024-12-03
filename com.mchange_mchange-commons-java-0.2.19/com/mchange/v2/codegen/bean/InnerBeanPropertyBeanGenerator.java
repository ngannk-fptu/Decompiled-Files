/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.codegen.bean;

import com.mchange.v2.codegen.CodegenUtils;
import com.mchange.v2.codegen.bean.BeangenUtils;
import com.mchange.v2.codegen.bean.CloneableExtension;
import com.mchange.v2.codegen.bean.Property;
import com.mchange.v2.codegen.bean.PropertyBeanGenerator;
import com.mchange.v2.codegen.bean.SerializableExtension;
import com.mchange.v2.codegen.bean.SimplePropertyBeanGenerator;
import com.mchange.v2.codegen.bean.SimplePropertyMask;
import com.mchange.v2.codegen.bean.WrapperClassInfo;
import java.io.IOException;
import java.lang.reflect.Modifier;

public class InnerBeanPropertyBeanGenerator
extends SimplePropertyBeanGenerator {
    String innerBeanClassName;
    int inner_bean_member_modifiers = 4;
    int inner_bean_accessor_modifiers = 4;
    int inner_bean_replacer_modifiers = 4;
    String innerBeanInitializationExpression = null;

    public void setInnerBeanClassName(String string) {
        this.innerBeanClassName = string;
    }

    public String getInnerBeanClassName() {
        return this.innerBeanClassName;
    }

    private String defaultInnerBeanInitializationExpression() {
        return "new " + this.innerBeanClassName + "()";
    }

    private String findInnerBeanClassName() {
        return this.innerBeanClassName == null ? "InnerBean" : this.innerBeanClassName;
    }

    private String findInnerBeanInitializationExpression() {
        return this.innerBeanInitializationExpression == null ? this.defaultInnerBeanInitializationExpression() : this.innerBeanInitializationExpression;
    }

    private int findInnerClassModifiers() {
        int n = 8;
        if (Modifier.isPublic(this.inner_bean_accessor_modifiers) || Modifier.isPublic(this.inner_bean_replacer_modifiers)) {
            n |= 1;
        } else if (Modifier.isProtected(this.inner_bean_accessor_modifiers) || Modifier.isProtected(this.inner_bean_replacer_modifiers)) {
            n |= 4;
        } else if (Modifier.isPrivate(this.inner_bean_accessor_modifiers) && Modifier.isPrivate(this.inner_bean_replacer_modifiers)) {
            n |= 2;
        }
        return n;
    }

    private void writeSyntheticInnerBeanClass() throws IOException {
        int n = this.props.length;
        Property[] propertyArray = new Property[n];
        for (int i = 0; i < n; ++i) {
            propertyArray[i] = new SimplePropertyMask(this.props[i]){

                @Override
                public int getVariableModifiers() {
                    return 130;
                }
            };
        }
        WrapperClassInfo wrapperClassInfo = new WrapperClassInfo(this.info){

            @Override
            public String getClassName() {
                return "InnerBean";
            }

            @Override
            public int getModifiers() {
                return InnerBeanPropertyBeanGenerator.this.findInnerClassModifiers();
            }
        };
        this.createInnerGenerator().generate(wrapperClassInfo, propertyArray, this.iw);
    }

    protected PropertyBeanGenerator createInnerGenerator() {
        SimplePropertyBeanGenerator simplePropertyBeanGenerator = new SimplePropertyBeanGenerator();
        simplePropertyBeanGenerator.setInner(true);
        simplePropertyBeanGenerator.addExtension(new SerializableExtension());
        CloneableExtension cloneableExtension = new CloneableExtension();
        cloneableExtension.setExceptionSwallowing(true);
        simplePropertyBeanGenerator.addExtension(cloneableExtension);
        return simplePropertyBeanGenerator;
    }

    @Override
    protected void writeOtherVariables() throws IOException {
        this.iw.println(CodegenUtils.getModifierString(this.inner_bean_member_modifiers) + ' ' + this.findInnerBeanClassName() + " innerBean = " + this.findInnerBeanInitializationExpression() + ';');
        this.iw.println();
        this.iw.println(CodegenUtils.getModifierString(this.inner_bean_accessor_modifiers) + ' ' + this.findInnerBeanClassName() + " accessInnerBean()");
        this.iw.println("{ return innerBean; }");
    }

    @Override
    protected void writeOtherFunctions() throws IOException {
        this.iw.print(CodegenUtils.getModifierString(this.inner_bean_replacer_modifiers) + ' ' + this.findInnerBeanClassName() + " replaceInnerBean( " + this.findInnerBeanClassName() + " innerBean )");
        if (this.constrainedProperties()) {
            this.iw.println(" throws PropertyVetoException");
        } else {
            this.iw.println();
        }
        this.iw.println("{");
        this.iw.upIndent();
        this.iw.println("beforeReplaceInnerBean();");
        this.iw.println("this.innerBean = innerBean;");
        this.iw.println("afterReplaceInnerBean();");
        this.iw.downIndent();
        this.iw.println("}");
        this.iw.println();
        boolean bl = Modifier.isAbstract(this.info.getModifiers());
        this.iw.print("protected ");
        if (bl) {
            this.iw.print("abstract ");
        }
        this.iw.print("void beforeReplaceInnerBean()");
        if (this.constrainedProperties()) {
            this.iw.print(" throws PropertyVetoException");
        }
        if (bl) {
            this.iw.println(';');
        } else {
            this.iw.println(" {} //hook method for subclasses");
        }
        this.iw.println();
        this.iw.print("protected ");
        if (bl) {
            this.iw.print("abstract ");
        }
        this.iw.print("void afterReplaceInnerBean()");
        if (bl) {
            this.iw.println(';');
        } else {
            this.iw.println(" {} //hook method for subclasses");
        }
        this.iw.println();
        BeangenUtils.writeExplicitDefaultConstructor(1, this.info, this.iw);
        this.iw.println();
        this.iw.println("public " + this.info.getClassName() + "(" + this.findInnerBeanClassName() + " innerBean)");
        this.iw.println("{ this.innerBean = innerBean; }");
    }

    @Override
    protected void writeOtherClasses() throws IOException {
        if (this.innerBeanClassName == null) {
            this.writeSyntheticInnerBeanClass();
        }
    }

    @Override
    protected void writePropertyVariable(Property property) throws IOException {
    }

    @Override
    protected void writePropertyGetter(Property property, Class clazz) throws IOException {
        String string = property.getSimpleTypeName();
        String string2 = "boolean".equals(string) ? "is" : "get";
        String string3 = string2 + BeangenUtils.capitalize(property.getName());
        this.iw.print(CodegenUtils.getModifierString(property.getGetterModifiers()));
        this.iw.println(' ' + property.getSimpleTypeName() + ' ' + string3 + "()");
        this.iw.println('{');
        this.iw.upIndent();
        this.iw.println(string + ' ' + property.getName() + " = innerBean." + string3 + "();");
        String string4 = this.getGetterDefensiveCopyExpression(property, clazz);
        if (string4 == null) {
            string4 = property.getName();
        }
        this.iw.println("return " + string4 + ';');
        this.iw.downIndent();
        this.iw.println('}');
    }

    @Override
    protected void writePropertySetter(Property property, Class clazz) throws IOException {
        String string = property.getSimpleTypeName();
        String string2 = "boolean".equals(string) ? "is" : "get";
        String string3 = this.getSetterDefensiveCopyExpression(property, clazz);
        if (string3 == null) {
            string3 = property.getName();
        }
        String string4 = "innerBean." + string2 + BeangenUtils.capitalize(property.getName()) + "()";
        String string5 = "innerBean.set" + BeangenUtils.capitalize(property.getName()) + "( " + string3 + " );";
        BeangenUtils.writePropertySetterWithGetExpressionSetStatement(property, string4, string5, this.iw);
    }
}

