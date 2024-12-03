/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.tagext.FunctionInfo
 */
package org.apache.sling.scripting.jsp.jasper.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import javax.servlet.jsp.tagext.FunctionInfo;
import org.apache.sling.scripting.jsp.jasper.JasperException;
import org.apache.sling.scripting.jsp.jasper.compiler.Compiler;
import org.apache.sling.scripting.jsp.jasper.compiler.ELNode;
import org.apache.sling.scripting.jsp.jasper.compiler.Node;

public class ELFunctionMapper {
    private int currFunc = 0;
    StringBuffer ds;
    StringBuffer ss;

    public static void map(Compiler compiler, Node.Nodes page) throws JasperException {
        ELFunctionMapper map = new ELFunctionMapper();
        map.ds = new StringBuffer();
        map.ss = new StringBuffer();
        page.visit(map.new ELFunctionVisitor());
        String ds = map.ds.toString();
        if (ds.length() > 0) {
            Node.Root root = page.getRoot();
            new Node.Declaration(map.ss.toString(), null, root);
            new Node.Declaration("static {\n" + ds + "}\n", null, root);
        }
    }

    class ELFunctionVisitor
    extends Node.Visitor {
        private HashMap<String, String> gMap = new HashMap();

        ELFunctionVisitor() {
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
                ArrayList<ELNode.Function> funcs = new ArrayList();
                HashMap<String, String> keyMap = new HashMap();

                Fvisitor() {
                }

                @Override
                public void visit(ELNode.Function n) throws JasperException {
                    String key = n.getPrefix() + ":" + n.getName();
                    if (!this.keyMap.containsKey(key)) {
                        this.keyMap.put(key, "");
                        this.funcs.add(n);
                    }
                }
            }
            Fvisitor fv = new Fvisitor();
            el.visit(fv);
            ArrayList<ELNode.Function> functions = fv.funcs;
            if (functions.size() == 0) {
                return;
            }
            String decName = this.matchMap(functions);
            if (decName != null) {
                el.setMapName(decName);
                return;
            }
            decName = this.getMapName();
            ELFunctionMapper.this.ss.append("static private org.apache.sling.scripting.jsp.jasper.runtime.ProtectedFunctionMapper " + decName + ";\n");
            ELFunctionMapper.this.ds.append("  " + decName + "= ");
            ELFunctionMapper.this.ds.append("org.apache.sling.scripting.jsp.jasper.runtime.ProtectedFunctionMapper");
            String funcMethod = null;
            if (functions.size() == 1) {
                funcMethod = ".getMapForFunction";
            } else {
                ELFunctionMapper.this.ds.append(".getInstance();\n");
                funcMethod = "  " + decName + ".mapFunction";
            }
            for (int i = 0; i < functions.size(); ++i) {
                ELNode.Function f = functions.get(i);
                FunctionInfo funcInfo = f.getFunctionInfo();
                String key = f.getPrefix() + ":" + f.getName();
                ELFunctionMapper.this.ds.append(funcMethod + "(\"" + key + "\", " + funcInfo.getFunctionClass() + ".class, " + '\"' + f.getMethodName() + "\", new Class[] {");
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
                this.gMap.put(f.getPrefix() + ':' + f.getName() + ':' + f.getUri(), decName);
            }
            el.setMapName(decName);
        }

        private String matchMap(ArrayList functions) {
            String mapName = null;
            for (int i = 0; i < functions.size(); ++i) {
                ELNode.Function f = (ELNode.Function)functions.get(i);
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
    }
}

