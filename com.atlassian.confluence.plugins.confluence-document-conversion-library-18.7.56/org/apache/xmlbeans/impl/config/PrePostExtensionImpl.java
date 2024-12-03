/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
 *  com.github.javaparser.ast.body.MethodDeclaration
 *  com.github.javaparser.ast.type.PrimitiveType
 */
package org.apache.xmlbeans.impl.config;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.PrimitiveType;
import org.apache.xmlbeans.PrePostExtension;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.config.BindingConfigImpl;
import org.apache.xmlbeans.impl.config.InterfaceExtensionImpl;
import org.apache.xmlbeans.impl.config.NameSet;
import org.apache.xmlbeans.impl.config.Parser;
import org.apache.xmlbeans.impl.xb.xmlconfig.Extensionconfig;

public class PrePostExtensionImpl
implements PrePostExtension {
    private static final String[] PARAMTYPES_STRING = new String[]{"int", "org.apache.xmlbeans.XmlObject", "javax.xml.namespace.QName", "boolean", "int"};
    private static final String SIGNATURE = "(" + String.join((CharSequence)", ", PARAMTYPES_STRING) + ")";
    private NameSet _xbeanSet;
    private ClassOrInterfaceDeclaration _delegateToClass;
    private String _delegateToClassName;
    private MethodDeclaration _preSet;
    private MethodDeclaration _postSet;

    static PrePostExtensionImpl newInstance(Parser loader, NameSet xbeanSet, Extensionconfig.PrePostSet prePostXO) {
        if (prePostXO == null) {
            return null;
        }
        PrePostExtensionImpl result = new PrePostExtensionImpl();
        result._xbeanSet = xbeanSet;
        result._delegateToClassName = prePostXO.getStaticHandler();
        result._delegateToClass = InterfaceExtensionImpl.validateClass(loader, result._delegateToClassName, prePostXO);
        if (result._delegateToClass == null) {
            BindingConfigImpl.warning("Handler class '" + prePostXO.getStaticHandler() + "' not found on classpath, skip validation.", prePostXO);
            return result;
        }
        if (!result.lookAfterPreAndPost(loader, prePostXO)) {
            return null;
        }
        return result;
    }

    private boolean lookAfterPreAndPost(Parser loader, XmlObject loc) {
        assert (this._delegateToClass != null) : "Delegate to class handler expected.";
        boolean valid = true;
        this._preSet = InterfaceExtensionImpl.getMethod(this._delegateToClass, "preSet", PARAMTYPES_STRING);
        if (this._preSet != null && !this._preSet.getType().equals((Object)PrimitiveType.booleanType())) {
            BindingConfigImpl.warning("Method '" + this._delegateToClass.getNameAsString() + ".preSet" + SIGNATURE + "' should return boolean to be considered for a preSet handler.", loc);
            this._preSet = null;
        }
        this._postSet = InterfaceExtensionImpl.getMethod(this._delegateToClass, "postSet", PARAMTYPES_STRING);
        if (this._preSet == null && this._postSet == null) {
            BindingConfigImpl.error("prePostSet handler specified '" + this._delegateToClass.getNameAsString() + "' but no preSet" + SIGNATURE + " or postSet" + SIGNATURE + " methods found.", loc);
            valid = false;
        }
        return valid;
    }

    public NameSet getNameSet() {
        return this._xbeanSet;
    }

    public boolean contains(String fullJavaName) {
        return this._xbeanSet.contains(fullJavaName);
    }

    @Override
    public boolean hasPreCall() {
        return this._preSet != null;
    }

    @Override
    public boolean hasPostCall() {
        return this._postSet != null;
    }

    @Override
    public String getStaticHandler() {
        return this._delegateToClassName;
    }

    public String getHandlerNameForJavaSource() {
        return this._delegateToClass == null ? null : this._delegateToClass.getNameAsString();
    }

    boolean hasNameSetIntersection(PrePostExtensionImpl ext) {
        return !NameSet.EMPTY.equals(this._xbeanSet.intersect(ext._xbeanSet));
    }
}

