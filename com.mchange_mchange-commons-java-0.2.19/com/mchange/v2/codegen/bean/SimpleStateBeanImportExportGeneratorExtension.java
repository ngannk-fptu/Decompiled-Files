/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.codegen.bean;

import com.mchange.v2.codegen.IndentedWriter;
import com.mchange.v2.codegen.bean.BeangenUtils;
import com.mchange.v2.codegen.bean.ClassInfo;
import com.mchange.v2.codegen.bean.GeneratorExtension;
import com.mchange.v2.codegen.bean.Property;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

public class SimpleStateBeanImportExportGeneratorExtension
implements GeneratorExtension {
    int ctor_modifiers = 1;

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
        int n;
        int n2 = propertyArray.length;
        Property[] propertyArray2 = new Property[n2];
        for (n = 0; n < n2; ++n) {
            propertyArray2[n] = new SimplePropertyMask(propertyArray[n]);
        }
        indentedWriter.println("protected static class SimpleStateBean implements ExportedState");
        indentedWriter.println("{");
        indentedWriter.upIndent();
        for (n = 0; n < n2; ++n) {
            propertyArray2[n] = new SimplePropertyMask(propertyArray[n]);
            BeangenUtils.writePropertyMember(propertyArray2[n], indentedWriter);
            indentedWriter.println();
            BeangenUtils.writePropertyGetter(propertyArray2[n], indentedWriter);
            indentedWriter.println();
            BeangenUtils.writePropertySetter(propertyArray2[n], indentedWriter);
        }
        indentedWriter.downIndent();
        indentedWriter.println("}");
    }

    static class SimplePropertyMask
    implements Property {
        Property p;

        SimplePropertyMask(Property property) {
            this.p = property;
        }

        @Override
        public int getVariableModifiers() {
            return 2;
        }

        @Override
        public String getName() {
            return this.p.getName();
        }

        @Override
        public String getSimpleTypeName() {
            return this.p.getSimpleTypeName();
        }

        @Override
        public String getDefensiveCopyExpression() {
            return null;
        }

        @Override
        public String getDefaultValueExpression() {
            return null;
        }

        @Override
        public int getGetterModifiers() {
            return 1;
        }

        @Override
        public int getSetterModifiers() {
            return 1;
        }

        @Override
        public boolean isReadOnly() {
            return false;
        }

        @Override
        public boolean isBound() {
            return false;
        }

        @Override
        public boolean isConstrained() {
            return false;
        }
    }
}

