/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import org.apache.axis.encoding.DefaultJAXRPC11TypeMappingImpl;
import org.apache.axis.encoding.DefaultSOAPEncodingTypeMappingImpl;
import org.apache.axis.encoding.DefaultTypeMappingImpl;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.TypeMappingDelegate;
import org.apache.axis.encoding.TypeMappingImpl;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.utils.Messages;

public class TypeMappingRegistryImpl
implements TypeMappingRegistry {
    private HashMap mapTM = new HashMap();
    private TypeMappingDelegate defaultDelTM;
    private boolean isDelegated = false;

    public TypeMappingRegistryImpl(TypeMappingImpl tm) {
        this.defaultDelTM = new TypeMappingDelegate(tm);
    }

    public TypeMappingRegistryImpl() {
        this(true);
    }

    public TypeMappingRegistryImpl(boolean registerDefaults) {
        if (registerDefaults) {
            this.defaultDelTM = DefaultTypeMappingImpl.getSingletonDelegate();
            TypeMappingDelegate del = new TypeMappingDelegate(new DefaultSOAPEncodingTypeMappingImpl());
            this.register("http://schemas.xmlsoap.org/soap/encoding/", del);
        } else {
            this.defaultDelTM = new TypeMappingDelegate(TypeMappingDelegate.placeholder);
        }
    }

    public void delegate(TypeMappingRegistry secondaryTMR) {
        if (this.isDelegated || secondaryTMR == null || secondaryTMR == this) {
            return;
        }
        this.isDelegated = true;
        String[] keys = secondaryTMR.getRegisteredEncodingStyleURIs();
        TypeMappingDelegate otherDefault = ((TypeMappingRegistryImpl)secondaryTMR).defaultDelTM;
        if (keys != null) {
            block2: for (int i = 0; i < keys.length; ++i) {
                try {
                    String nsURI = keys[i];
                    TypeMappingDelegate tm = (TypeMappingDelegate)this.mapTM.get(nsURI);
                    if (tm == null) {
                        tm = (TypeMappingDelegate)this.createTypeMapping();
                        tm.setSupportedEncodings(new String[]{nsURI});
                        this.register(nsURI, tm);
                    }
                    if (tm == null) continue;
                    TypeMappingDelegate del = (TypeMappingDelegate)((TypeMappingRegistryImpl)secondaryTMR).mapTM.get(nsURI);
                    while (del.next != null) {
                        TypeMappingDelegate nu = new TypeMappingDelegate(del.delegate);
                        tm.setNext(nu);
                        if (del.next == otherDefault) {
                            nu.setNext(this.defaultDelTM);
                            continue block2;
                        }
                        del = del.next;
                        tm = nu;
                    }
                    continue;
                }
                catch (Exception e) {
                    // empty catch block
                }
            }
        }
        if (this.defaultDelTM.delegate != TypeMappingDelegate.placeholder) {
            this.defaultDelTM.setNext(otherDefault);
        } else {
            this.defaultDelTM.delegate = otherDefault.delegate;
        }
    }

    public javax.xml.rpc.encoding.TypeMapping register(String namespaceURI, javax.xml.rpc.encoding.TypeMapping mapping) {
        if (mapping == null || !(mapping instanceof TypeMappingDelegate)) {
            throw new IllegalArgumentException(Messages.getMessage("badTypeMapping"));
        }
        if (namespaceURI == null) {
            throw new IllegalArgumentException(Messages.getMessage("nullNamespaceURI"));
        }
        TypeMappingDelegate del = (TypeMappingDelegate)mapping;
        TypeMappingDelegate old = (TypeMappingDelegate)this.mapTM.get(namespaceURI);
        if (old == null) {
            del.setNext(this.defaultDelTM);
        } else {
            del.setNext(old);
        }
        this.mapTM.put(namespaceURI, del);
        return old;
    }

    public void registerDefault(javax.xml.rpc.encoding.TypeMapping mapping) {
        if (mapping == null || !(mapping instanceof TypeMappingDelegate)) {
            throw new IllegalArgumentException(Messages.getMessage("badTypeMapping"));
        }
        if (this.defaultDelTM.getNext() != null) {
            throw new IllegalArgumentException(Messages.getMessage("defaultTypeMappingSet"));
        }
        this.defaultDelTM = (TypeMappingDelegate)mapping;
    }

    public void doRegisterFromVersion(String version) {
        if (version == null || version.equals("1.0") || version.equals("1.2")) {
            TypeMappingImpl.dotnet_soapenc_bugfix = false;
        } else {
            if (version.equals("1.1")) {
                TypeMappingImpl.dotnet_soapenc_bugfix = true;
                return;
            }
            if (version.equals("1.3")) {
                this.defaultDelTM = new TypeMappingDelegate(DefaultJAXRPC11TypeMappingImpl.getSingleton());
            } else {
                throw new RuntimeException(Messages.getMessage("j2wBadTypeMapping00"));
            }
        }
        this.registerSOAPENCDefault(new TypeMappingDelegate(DefaultSOAPEncodingTypeMappingImpl.getSingleton()));
    }

    private void registerSOAPENCDefault(TypeMappingDelegate mapping) {
        TypeMappingDelegate del;
        if (!this.mapTM.containsKey("http://schemas.xmlsoap.org/soap/encoding/")) {
            this.mapTM.put("http://schemas.xmlsoap.org/soap/encoding/", mapping);
        } else {
            del = (TypeMappingDelegate)this.mapTM.get("http://schemas.xmlsoap.org/soap/encoding/");
            while (del.getNext() != null && !(del.delegate instanceof DefaultTypeMappingImpl)) {
                del = del.getNext();
            }
            del.setNext(this.defaultDelTM);
        }
        if (!this.mapTM.containsKey("http://www.w3.org/2003/05/soap-encoding")) {
            this.mapTM.put("http://www.w3.org/2003/05/soap-encoding", mapping);
        } else {
            del = (TypeMappingDelegate)this.mapTM.get("http://www.w3.org/2003/05/soap-encoding");
            while (del.getNext() != null && !(del.delegate instanceof DefaultTypeMappingImpl)) {
                del = del.getNext();
            }
            del.setNext(this.defaultDelTM);
        }
        mapping.setNext(this.defaultDelTM);
    }

    public javax.xml.rpc.encoding.TypeMapping getTypeMapping(String namespaceURI) {
        TypeMapping del = (TypeMappingDelegate)this.mapTM.get(namespaceURI);
        if (del == null) {
            del = (TypeMapping)this.getDefaultTypeMapping();
        }
        return del;
    }

    public TypeMapping getOrMakeTypeMapping(String encodingStyle) {
        TypeMappingDelegate del = (TypeMappingDelegate)this.mapTM.get(encodingStyle);
        if (del == null || del.delegate instanceof DefaultTypeMappingImpl) {
            del = (TypeMappingDelegate)this.createTypeMapping();
            del.setSupportedEncodings(new String[]{encodingStyle});
            this.register(encodingStyle, del);
        }
        return del;
    }

    public javax.xml.rpc.encoding.TypeMapping unregisterTypeMapping(String namespaceURI) {
        return (TypeMappingDelegate)this.mapTM.remove(namespaceURI);
    }

    public boolean removeTypeMapping(javax.xml.rpc.encoding.TypeMapping mapping) {
        String[] ns = this.getRegisteredEncodingStyleURIs();
        boolean rc = false;
        for (int i = 0; i < ns.length; ++i) {
            if (this.getTypeMapping(ns[i]) != mapping) continue;
            rc = true;
            this.unregisterTypeMapping(ns[i]);
        }
        return rc;
    }

    public javax.xml.rpc.encoding.TypeMapping createTypeMapping() {
        TypeMappingImpl impl = new TypeMappingImpl();
        TypeMappingDelegate del = new TypeMappingDelegate(impl);
        del.setNext(this.defaultDelTM);
        return del;
    }

    public String[] getRegisteredEncodingStyleURIs() {
        Set s = this.mapTM.keySet();
        if (s != null) {
            String[] rc = new String[s.size()];
            int i = 0;
            Iterator it = s.iterator();
            while (it.hasNext()) {
                rc[i++] = (String)it.next();
            }
            return rc;
        }
        return null;
    }

    public void clear() {
        this.mapTM.clear();
    }

    public javax.xml.rpc.encoding.TypeMapping getDefaultTypeMapping() {
        return this.defaultDelTM;
    }
}

