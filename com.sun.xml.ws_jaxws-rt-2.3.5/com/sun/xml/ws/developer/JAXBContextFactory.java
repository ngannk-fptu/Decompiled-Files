/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.xml.bind.api.JAXBRIContext
 *  com.sun.xml.bind.api.TypeReference
 *  javax.xml.bind.JAXBException
 */
package com.sun.xml.ws.developer;

import com.sun.istack.NotNull;
import com.sun.xml.bind.api.JAXBRIContext;
import com.sun.xml.bind.api.TypeReference;
import com.sun.xml.ws.api.model.SEIModel;
import java.util.List;
import javax.xml.bind.JAXBException;

public interface JAXBContextFactory {
    public static final JAXBContextFactory DEFAULT = new JAXBContextFactory(){

        @Override
        @NotNull
        public JAXBRIContext createJAXBContext(@NotNull SEIModel sei, @NotNull List<Class> classesToBind, @NotNull List<TypeReference> typeReferences) throws JAXBException {
            return JAXBRIContext.newInstance((Class[])classesToBind.toArray(new Class[classesToBind.size()]), typeReferences, null, (String)sei.getTargetNamespace(), (boolean)false, null);
        }
    };

    @NotNull
    public JAXBRIContext createJAXBContext(@NotNull SEIModel var1, @NotNull List<Class> var2, @NotNull List<TypeReference> var3) throws JAXBException;
}

