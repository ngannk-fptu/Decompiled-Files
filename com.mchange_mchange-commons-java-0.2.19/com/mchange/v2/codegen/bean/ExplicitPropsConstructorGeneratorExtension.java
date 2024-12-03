/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.codegen.bean;

import com.mchange.v2.codegen.CodegenUtils;
import com.mchange.v2.codegen.IndentedWriter;
import com.mchange.v2.codegen.bean.BeangenUtils;
import com.mchange.v2.codegen.bean.ClassInfo;
import com.mchange.v2.codegen.bean.GeneratorExtension;
import com.mchange.v2.codegen.bean.Property;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class ExplicitPropsConstructorGeneratorExtension
implements GeneratorExtension {
    static final MLogger logger = MLog.getLogger(ExplicitPropsConstructorGeneratorExtension.class);
    String[] propNames;
    boolean skips_silently = false;
    int ctor_modifiers = 1;

    public ExplicitPropsConstructorGeneratorExtension() {
    }

    public ExplicitPropsConstructorGeneratorExtension(String[] stringArray) {
        this.propNames = stringArray;
    }

    public String[] getPropNames() {
        return (String[])this.propNames.clone();
    }

    public void setPropNames(String[] stringArray) {
        this.propNames = (String[])stringArray.clone();
    }

    public boolean isSkipsSilently() {
        return this.skips_silently;
    }

    public void setsSkipsSilently(boolean bl) {
        this.skips_silently = bl;
    }

    @Override
    public Collection extraGeneralImports() {
        return Collections.EMPTY_SET;
    }

    @Override
    public Collection extraSpecificImports() {
        return Collections.EMPTY_SET;
    }

    @Override
    public Collection extraInterfaceNames() {
        return Collections.EMPTY_SET;
    }

    @Override
    public void generate(ClassInfo classInfo, Class clazz, Property[] propertyArray, Class[] classArray, IndentedWriter indentedWriter) throws IOException {
        HashMap<String, Property> hashMap = new HashMap<String, Property>();
        int n = propertyArray.length;
        for (int i = 0; i < n; ++i) {
            hashMap.put(propertyArray[i].getName(), propertyArray[i]);
        }
        ArrayList<Property> arrayList = new ArrayList<Property>(this.propNames.length);
        int n2 = this.propNames.length;
        for (n = 0; n < n2; ++n) {
            Property property = (Property)hashMap.get(this.propNames[n]);
            if (property == null) {
                logger.warning("Could not include property '" + this.propNames[n] + "' in explicit-props-constructor generated for bean class '" + classInfo.getClassName() + "' because the property is not defined for the bean. Skipping.");
                continue;
            }
            arrayList.add(property);
        }
        if (arrayList.size() > 0) {
            Property[] propertyArray2 = arrayList.toArray(new Property[arrayList.size()]);
            indentedWriter.print(CodegenUtils.getModifierString(this.ctor_modifiers));
            indentedWriter.print(classInfo.getClassName() + "( ");
            BeangenUtils.writeArgList(propertyArray2, true, indentedWriter);
            indentedWriter.println(" )");
            indentedWriter.println("{");
            indentedWriter.upIndent();
            int n3 = propertyArray2.length;
            for (n2 = 0; n2 < n3; ++n2) {
                indentedWriter.print("this." + propertyArray2[n2].getName() + " = ");
                String string = propertyArray2[n2].getDefensiveCopyExpression();
                if (string == null) {
                    string = propertyArray2[n2].getName();
                }
                indentedWriter.println(string + ';');
            }
            indentedWriter.downIndent();
            indentedWriter.println("}");
        }
    }
}

