/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.tagext.FunctionInfo
 *  org.apache.tomcat.util.security.PrivilegedGetTccl
 */
package org.apache.jasper.compiler;

import java.security.AccessController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.servlet.jsp.tagext.FunctionInfo;
import org.apache.jasper.Constants;
import org.apache.jasper.JasperException;
import org.apache.jasper.compiler.ELNode;
import org.apache.jasper.compiler.Node;
import org.apache.tomcat.util.security.PrivilegedGetTccl;

public class ELFunctionMapper {
    private int currFunc = 0;
    private StringBuilder ds;
    private StringBuilder ss;

    public static void map(Node.Nodes page) throws JasperException {
        ELFunctionMapper map = new ELFunctionMapper();
        map.ds = new StringBuilder();
        map.ss = new StringBuilder();
        ELFunctionMapper eLFunctionMapper = map;
        Objects.requireNonNull(eLFunctionMapper);
        page.visit(eLFunctionMapper.new ELFunctionVisitor());
        String ds = map.ds.toString();
        if (ds.length() > 0) {
            Node.Root root = page.getRoot();
            Node.Declaration unused = new Node.Declaration(map.ss.toString(), null, root);
            unused = new Node.Declaration("static {\n" + ds + "}\n", null, root);
        }
    }

    private class ELFunctionVisitor
    extends Node.Visitor {
        private final Map<String, String> gMap = new HashMap<String, String>();

        private ELFunctionVisitor() {
        }

        @Override
        public void visit(Node.ParamAction n) throws JasperException {
            this.doMap(n.getValue());
            this.visitBody(n);
        }

        @Override
        public void visit(Node.IncludeAction n) throws JasperException {
            this.doMap(n.getPage());
            this.visitBody(n);
        }

        @Override
        public void visit(Node.ForwardAction n) throws JasperException {
            this.doMap(n.getPage());
            this.visitBody(n);
        }

        @Override
        public void visit(Node.SetProperty n) throws JasperException {
            this.doMap(n.getValue());
            this.visitBody(n);
        }

        @Override
        public void visit(Node.UseBean n) throws JasperException {
            this.doMap(n.getBeanName());
            this.visitBody(n);
        }

        @Override
        public void visit(Node.PlugIn n) throws JasperException {
            this.doMap(n.getHeight());
            this.doMap(n.getWidth());
            this.visitBody(n);
        }

        @Override
        public void visit(Node.JspElement n) throws JasperException {
            Node.JspAttribute[] attrs = n.getJspAttributes();
            for (int i = 0; attrs != null && i < attrs.length; ++i) {
                this.doMap(attrs[i]);
            }
            this.doMap(n.getNameAttribute());
            this.visitBody(n);
        }

        @Override
        public void visit(Node.UninterpretedTag n) throws JasperException {
            Node.JspAttribute[] attrs = n.getJspAttributes();
            for (int i = 0; attrs != null && i < attrs.length; ++i) {
                this.doMap(attrs[i]);
            }
            this.visitBody(n);
        }

        @Override
        public void visit(Node.CustomTag n) throws JasperException {
            Node.JspAttribute[] attrs = n.getJspAttributes();
            for (int i = 0; attrs != null && i < attrs.length; ++i) {
                this.doMap(attrs[i]);
            }
            this.visitBody(n);
        }

        @Override
        public void visit(Node.ELExpression n) throws JasperException {
            this.doMap(n.getEL());
        }

        private void doMap(Node.JspAttribute attr) throws JasperException {
            if (attr != null) {
                this.doMap(attr.getEL());
            }
        }

        private void doMap(ELNode.Nodes el) throws JasperException {
            if (el == null) {
                return;
            }
            class Fvisitor
            extends ELNode.Visitor {
                private final List<ELNode.Function> funcs = new ArrayList<ELNode.Function>();
                private final Set<String> keySet = new HashSet<String>();

                Fvisitor() {
                }

                @Override
                public void visit(ELNode.Function n) throws JasperException {
                    String key = n.getPrefix() + ":" + n.getName();
                    if (this.keySet.add(key)) {
                        this.funcs.add(n);
                    }
                }
            }
            Fvisitor fv = new Fvisitor();
            el.visit(fv);
            List functions = fv.funcs;
            if (functions.size() == 0) {
                return;
            }
            String decName = this.matchMap(functions);
            if (decName != null) {
                el.setMapName(decName);
                return;
            }
            decName = this.getMapName();
            ELFunctionMapper.this.ss.append("private static org.apache.jasper.runtime.ProtectedFunctionMapper " + decName + ";\n");
            ELFunctionMapper.this.ds.append("  " + decName + "= ");
            ELFunctionMapper.this.ds.append("org.apache.jasper.runtime.ProtectedFunctionMapper");
            String funcMethod = null;
            if (functions.size() == 1) {
                funcMethod = ".getMapForFunction";
            } else {
                ELFunctionMapper.this.ds.append(".getInstance();\n");
                funcMethod = "  " + decName + ".mapFunction";
            }
            for (ELNode.Function f : functions) {
                FunctionInfo funcInfo = f.getFunctionInfo();
                String fnQName = f.getPrefix() + ":" + f.getName();
                if (funcInfo == null) {
                    ELFunctionMapper.this.ds.append(funcMethod + "(null, null, null, null);\n");
                } else {
                    ELFunctionMapper.this.ds.append(funcMethod + "(\"" + fnQName + "\", " + this.getCanonicalName(funcInfo.getFunctionClass()) + ".class, " + '\"' + f.getMethodName() + "\", new Class[] {");
                    String[] params = f.getParameters();
                    for (int k = 0; k < params.length; ++k) {
                        int iArray;
                        if (k != 0) {
                            ELFunctionMapper.this.ds.append(", ");
                        }
                        if ((iArray = params[k].indexOf(91)) < 0) {
                            ELFunctionMapper.this.ds.append(params[k] + ".class");
                            continue;
                        }
                        String baseType = params[k].substring(0, iArray);
                        ELFunctionMapper.this.ds.append("java.lang.reflect.Array.newInstance(");
                        ELFunctionMapper.this.ds.append(baseType);
                        ELFunctionMapper.this.ds.append(".class,");
                        int aCount = 0;
                        for (int jj = iArray; jj < params[k].length(); ++jj) {
                            if (params[k].charAt(jj) != '[') continue;
                            ++aCount;
                        }
                        if (aCount == 1) {
                            ELFunctionMapper.this.ds.append("0).getClass()");
                            continue;
                        }
                        ELFunctionMapper.this.ds.append("new int[" + aCount + "]).getClass()");
                    }
                    ELFunctionMapper.this.ds.append("});\n");
                }
                this.gMap.put(fnQName + ':' + f.getUri(), decName);
            }
            el.setMapName(decName);
        }

        private String matchMap(List<ELNode.Function> functions) {
            String mapName = null;
            for (ELNode.Function f : functions) {
                String temName = this.gMap.get(f.getPrefix() + ':' + f.getName() + ':' + f.getUri());
                if (temName == null) {
                    return null;
                }
                if (mapName == null) {
                    mapName = temName;
                    continue;
                }
                if (temName.equals(mapName)) continue;
                return null;
            }
            return mapName;
        }

        private String getMapName() {
            return "_jspx_fnmap_" + ELFunctionMapper.this.currFunc++;
        }

        private String getCanonicalName(String className) throws JasperException {
            Class<?> clazz;
            ClassLoader tccl;
            Thread currentThread = Thread.currentThread();
            if (Constants.IS_SECURITY_ENABLED) {
                PrivilegedGetTccl pa = new PrivilegedGetTccl(currentThread);
                tccl = (ClassLoader)AccessController.doPrivileged(pa);
            } else {
                tccl = currentThread.getContextClassLoader();
            }
            try {
                clazz = Class.forName(className, false, tccl);
            }
            catch (ClassNotFoundException e) {
                throw new JasperException(e);
            }
            return clazz.getCanonicalName();
        }
    }
}

