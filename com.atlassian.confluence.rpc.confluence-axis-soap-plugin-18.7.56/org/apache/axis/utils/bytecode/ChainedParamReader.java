/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.utils.bytecode;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.axis.utils.bytecode.ParamReader;

public class ChainedParamReader {
    private List chain = new ArrayList();
    private List clsChain = new ArrayList();
    private Map methodToParamMap = new HashMap();

    public ChainedParamReader(Class cls) throws IOException {
        ParamReader reader = new ParamReader(cls);
        this.chain.add(reader);
        this.clsChain.add(cls);
    }

    public String[] getParameterNames(Constructor ctor) {
        return ((ParamReader)this.chain.get(0)).getParameterNames(ctor);
    }

    public String[] getParameterNames(Method method) {
        if (this.methodToParamMap.containsKey(method)) {
            return (String[])this.methodToParamMap.get(method);
        }
        String[] ret = null;
        Iterator it = this.chain.iterator();
        while (it.hasNext()) {
            ParamReader reader = (ParamReader)it.next();
            ret = reader.getParameterNames(method);
            if (ret == null) continue;
            this.methodToParamMap.put(method, ret);
            return ret;
        }
        Class cls = (Class)this.clsChain.get(this.chain.size() - 1);
        while (cls.getSuperclass() != null) {
            Class superClass = cls.getSuperclass();
            try {
                ParamReader _reader = new ParamReader(superClass);
                this.chain.add(_reader);
                this.clsChain.add(cls);
                ret = _reader.getParameterNames(method);
                if (ret == null) continue;
                this.methodToParamMap.put(method, ret);
                return ret;
            }
            catch (IOException e) {
                return null;
            }
        }
        this.methodToParamMap.put(method, ret);
        return null;
    }
}

