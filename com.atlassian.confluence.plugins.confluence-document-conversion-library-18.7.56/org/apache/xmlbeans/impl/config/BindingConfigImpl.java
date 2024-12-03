/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.BindingConfig;
import org.apache.xmlbeans.InterfaceExtension;
import org.apache.xmlbeans.PrePostExtension;
import org.apache.xmlbeans.UserType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.config.InterfaceExtensionImpl;
import org.apache.xmlbeans.impl.config.NameSet;
import org.apache.xmlbeans.impl.config.NameSetBuilder;
import org.apache.xmlbeans.impl.config.Parser;
import org.apache.xmlbeans.impl.config.PrePostExtensionImpl;
import org.apache.xmlbeans.impl.config.UserTypeImpl;
import org.apache.xmlbeans.impl.schema.StscState;
import org.apache.xmlbeans.impl.xb.xmlconfig.ConfigDocument;
import org.apache.xmlbeans.impl.xb.xmlconfig.Extensionconfig;
import org.apache.xmlbeans.impl.xb.xmlconfig.Nsconfig;
import org.apache.xmlbeans.impl.xb.xmlconfig.Qnameconfig;
import org.apache.xmlbeans.impl.xb.xmlconfig.Qnametargetenum;
import org.apache.xmlbeans.impl.xb.xmlconfig.Usertypeconfig;

public class BindingConfigImpl
extends BindingConfig {
    private final Map<Object, String> _packageMap = new LinkedHashMap<Object, String>();
    private final Map<Object, String> _prefixMap = new LinkedHashMap<Object, String>();
    private final Map<Object, String> _suffixMap = new LinkedHashMap<Object, String>();
    private final Map<Object, String> _packageMapByUriPrefix = new LinkedHashMap<Object, String>();
    private final Map<Object, String> _prefixMapByUriPrefix = new LinkedHashMap<Object, String>();
    private final Map<Object, String> _suffixMapByUriPrefix = new LinkedHashMap<Object, String>();
    private final Map<QName, String> _qnameTypeMap = new LinkedHashMap<QName, String>();
    private final Map<QName, String> _qnameDocTypeMap = new LinkedHashMap<QName, String>();
    private final Map<QName, String> _qnameElemMap = new LinkedHashMap<QName, String>();
    private final Map<QName, String> _qnameAttMap = new LinkedHashMap<QName, String>();
    private final List<InterfaceExtensionImpl> _interfaceExtensions = new ArrayList<InterfaceExtensionImpl>();
    private final List<PrePostExtensionImpl> _prePostExtensions = new ArrayList<PrePostExtensionImpl>();
    private final Map<QName, UserTypeImpl> _userTypes = new LinkedHashMap<QName, UserTypeImpl>();
    private final File[] _javaFiles;
    private final File[] _classpath;
    private Parser _parser;

    public static BindingConfig forConfigDocuments(ConfigDocument.Config[] configs, File[] javaFiles, File[] classpath) {
        return new BindingConfigImpl(configs, javaFiles, classpath);
    }

    private BindingConfigImpl(ConfigDocument.Config[] configs, File[] javaFiles, File[] classpath) {
        this._javaFiles = javaFiles != null ? (File[])javaFiles.clone() : new File[]{};
        this._classpath = classpath != null ? (File[])classpath.clone() : new File[]{};
        for (ConfigDocument.Config config : configs) {
            Usertypeconfig[] utypes;
            Extensionconfig[] ext;
            Qnameconfig[] qnc;
            Nsconfig[] nsa;
            for (Nsconfig nsconfig : nsa = config.getNamespaceArray()) {
                BindingConfigImpl.recordNamespaceSetting(nsconfig.getUri(), nsconfig.getPackage(), this._packageMap);
                BindingConfigImpl.recordNamespaceSetting(nsconfig.getUri(), nsconfig.getPrefix(), this._prefixMap);
                BindingConfigImpl.recordNamespaceSetting(nsconfig.getUri(), nsconfig.getSuffix(), this._suffixMap);
                BindingConfigImpl.recordNamespacePrefixSetting(nsconfig.getUriprefix(), nsconfig.getPackage(), this._packageMapByUriPrefix);
                BindingConfigImpl.recordNamespacePrefixSetting(nsconfig.getUriprefix(), nsconfig.getPrefix(), this._prefixMapByUriPrefix);
                BindingConfigImpl.recordNamespacePrefixSetting(nsconfig.getUriprefix(), nsconfig.getSuffix(), this._suffixMapByUriPrefix);
            }
            for (Qnameconfig qnameconfig : qnc = config.getQnameArray()) {
                List applyto = qnameconfig.xgetTarget().xgetListValue();
                QName name = qnameconfig.getName();
                String javaname = qnameconfig.getJavaname();
                for (Object o : applyto) {
                    Qnametargetenum a = (Qnametargetenum)o;
                    switch (a.getEnumValue().intValue()) {
                        case 1: {
                            this._qnameTypeMap.put(name, javaname);
                            break;
                        }
                        case 2: {
                            this._qnameDocTypeMap.put(name, javaname);
                            break;
                        }
                        case 3: {
                            this._qnameElemMap.put(name, javaname);
                            break;
                        }
                        case 4: {
                            this._qnameAttMap.put(name, javaname);
                        }
                    }
                }
            }
            for (Extensionconfig extensionconfig : ext = config.getExtensionArray()) {
                this.recordExtensionSetting(extensionconfig);
            }
            for (Usertypeconfig utype : utypes = config.getUsertypeArray()) {
                this.recordUserTypeSetting(utype);
            }
        }
        this.secondPhaseValidation();
    }

    void addInterfaceExtension(InterfaceExtensionImpl ext) {
        if (ext == null) {
            return;
        }
        this._interfaceExtensions.add(ext);
    }

    void addPrePostExtension(PrePostExtensionImpl ext) {
        if (ext == null) {
            return;
        }
        this._prePostExtensions.add(ext);
    }

    void secondPhaseValidation() {
        HashMap<InterfaceExtensionImpl.MethodSignatureImpl, InterfaceExtensionImpl.MethodSignatureImpl> methodSignatures = new HashMap<InterfaceExtensionImpl.MethodSignatureImpl, InterfaceExtensionImpl.MethodSignatureImpl>();
        for (InterfaceExtensionImpl extension : this._interfaceExtensions) {
            InterfaceExtensionImpl.MethodSignatureImpl[] methods;
            for (InterfaceExtensionImpl.MethodSignatureImpl ms : methods = (InterfaceExtensionImpl.MethodSignatureImpl[])extension.getMethods()) {
                if (methodSignatures.containsKey(ms)) {
                    InterfaceExtensionImpl.MethodSignatureImpl ms2 = (InterfaceExtensionImpl.MethodSignatureImpl)methodSignatures.get(ms);
                    if (!ms.getReturnType().equals(ms2.getReturnType())) {
                        BindingConfigImpl.error("Colliding methods '" + ms.getSignature() + "' in interfaces " + ms.getInterfaceName() + " and " + ms2.getInterfaceName() + ".", null);
                    }
                    return;
                }
                methodSignatures.put(ms, ms);
            }
        }
        for (int i = 0; i < this._prePostExtensions.size() - 1; ++i) {
            PrePostExtensionImpl a = this._prePostExtensions.get(i);
            for (int j = 1; j < this._prePostExtensions.size(); ++j) {
                PrePostExtensionImpl b = this._prePostExtensions.get(j);
                if (!a.hasNameSetIntersection(b)) continue;
                BindingConfigImpl.error("The applicable domain for handler '" + a.getHandlerNameForJavaSource() + "' intersects with the one for '" + b.getHandlerNameForJavaSource() + "'.", null);
            }
        }
    }

    private static void recordNamespaceSetting(Object key, String value, Map<Object, String> result) {
        if (value == null) {
            return;
        }
        if (key == null) {
            result.put("", value);
        } else if (key instanceof String && "##any".equals(key)) {
            result.put(key, value);
        } else if (key instanceof List) {
            ((List)key).forEach(o -> result.put("##local".equals(o) ? "" : o, value));
        }
    }

    private static void recordNamespacePrefixSetting(List list, String value, Map<Object, String> result) {
        if (value == null) {
            return;
        }
        if (list == null) {
            return;
        }
        list.forEach(o -> result.put(o, value));
    }

    private void recordExtensionSetting(Extensionconfig ext) {
        NameSet xbeanSet = null;
        Object key = ext.getFor();
        if (key instanceof String && "*".equals(key)) {
            xbeanSet = NameSet.EVERYTHING;
        } else if (key instanceof List) {
            NameSetBuilder xbeanSetBuilder = new NameSetBuilder();
            for (Object o : (List)key) {
                String xbeanName = (String)o;
                xbeanSetBuilder.add(xbeanName);
            }
            xbeanSet = xbeanSetBuilder.toNameSet();
        }
        if (xbeanSet == null) {
            BindingConfigImpl.error("Invalid value of attribute 'for' : '" + key + "'.", ext);
        }
        Extensionconfig.Interface[] intfXO = ext.getInterfaceArray();
        Extensionconfig.PrePostSet ppXO = ext.getPrePostSet();
        Parser loader = this.parserInstance();
        if (intfXO.length > 0 || ppXO != null) {
            for (Extensionconfig.Interface anInterface : intfXO) {
                this.addInterfaceExtension(InterfaceExtensionImpl.newInstance(loader, xbeanSet, anInterface));
            }
            this.addPrePostExtension(PrePostExtensionImpl.newInstance(loader, xbeanSet, ppXO));
        }
    }

    private void recordUserTypeSetting(Usertypeconfig usertypeconfig) {
        Parser loader = this.parserInstance();
        UserTypeImpl userType = UserTypeImpl.newInstance(loader, usertypeconfig);
        this._userTypes.put(userType.getName(), userType);
    }

    private Parser parserInstance() {
        if (this._parser == null) {
            this._parser = new Parser(this._javaFiles, this._classpath);
        }
        return this._parser;
    }

    private String lookup(Map<Object, String> map, Map<Object, String> mapByUriPrefix, String uri) {
        String result;
        if (uri == null) {
            uri = "";
        }
        if ((result = map.get(uri)) != null) {
            return result;
        }
        if (mapByUriPrefix != null && (result = this.lookupByUriPrefix(mapByUriPrefix, uri)) != null) {
            return result;
        }
        return map.get("##any");
    }

    private String lookupByUriPrefix(Map<Object, String> mapByUriPrefix, String uri) {
        if (uri == null) {
            return null;
        }
        if (!mapByUriPrefix.isEmpty()) {
            String uriprefix = null;
            for (Object o : mapByUriPrefix.keySet()) {
                if (!(o instanceof String)) continue;
                String nextprefix = (String)o;
                if (uriprefix != null && nextprefix.length() < uriprefix.length() || !uri.startsWith(nextprefix)) continue;
                uriprefix = nextprefix;
            }
            if (uriprefix != null) {
                return mapByUriPrefix.get(uriprefix);
            }
        }
        return null;
    }

    static void warning(String s, XmlObject xo) {
        StscState.get().error(s, 1, xo);
    }

    static void error(String s, XmlObject xo) {
        StscState.get().error(s, 0, xo);
    }

    @Override
    public String lookupPackageForNamespace(String uri) {
        return this.lookup(this._packageMap, this._packageMapByUriPrefix, uri);
    }

    @Override
    public String lookupPrefixForNamespace(String uri) {
        return this.lookup(this._prefixMap, this._prefixMapByUriPrefix, uri);
    }

    @Override
    public String lookupSuffixForNamespace(String uri) {
        return this.lookup(this._suffixMap, this._suffixMapByUriPrefix, uri);
    }

    public String lookupJavanameForQName(QName qname) {
        String result = this._qnameTypeMap.get(qname);
        return result != null ? result : this._qnameDocTypeMap.get(qname);
    }

    @Override
    public String lookupJavanameForQName(QName qname, int kind) {
        switch (kind) {
            case 1: {
                return this._qnameTypeMap.get(qname);
            }
            case 2: {
                return this._qnameDocTypeMap.get(qname);
            }
            case 3: {
                return this._qnameElemMap.get(qname);
            }
            case 4: {
                return this._qnameAttMap.get(qname);
            }
        }
        return null;
    }

    @Override
    public UserType lookupUserTypeForQName(QName qname) {
        return qname == null ? null : (UserType)this._userTypes.get(qname);
    }

    @Override
    public UserType[] getUserTypes() {
        return this._userTypes.values().toArray(new UserType[0]);
    }

    @Override
    public InterfaceExtension[] getInterfaceExtensions() {
        return this._interfaceExtensions.toArray(new InterfaceExtension[0]);
    }

    @Override
    public InterfaceExtension[] getInterfaceExtensions(String fullJavaName) {
        return (InterfaceExtension[])this._interfaceExtensions.stream().filter(i -> i.contains(fullJavaName)).toArray(InterfaceExtension[]::new);
    }

    @Override
    public PrePostExtension[] getPrePostExtensions() {
        return this._prePostExtensions.toArray(new PrePostExtension[0]);
    }

    @Override
    public PrePostExtension getPrePostExtension(String fullJavaName) {
        return this._prePostExtensions.stream().filter(p -> p.contains(fullJavaName)).findFirst().orElse(null);
    }
}

