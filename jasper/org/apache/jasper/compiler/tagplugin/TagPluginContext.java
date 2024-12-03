/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jasper.compiler.tagplugin;

public interface TagPluginContext {
    public boolean isScriptless();

    public boolean isAttributeSpecified(String var1);

    public String getTemporaryVariableName();

    public void generateImport(String var1);

    public void generateDeclaration(String var1, String var2);

    public void generateJavaSource(String var1);

    public boolean isConstantAttribute(String var1);

    public String getConstantAttribute(String var1);

    public void generateAttribute(String var1);

    public void generateBody();

    public void dontUseTagPlugin();

    public TagPluginContext getParentContext();

    public void setPluginAttribute(String var1, Object var2);

    public Object getPluginAttribute(String var1);

    public boolean isTagFile();
}

