/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v1.xml.DomParseUtils
 *  com.mchange.v2.codegen.IndentedWriter
 *  com.mchange.v2.codegen.bean.BeanExtractingGeneratorExtension
 *  com.mchange.v2.codegen.bean.BeangenUtils
 *  com.mchange.v2.codegen.bean.ClassInfo
 *  com.mchange.v2.codegen.bean.CompleteConstructorGeneratorExtension
 *  com.mchange.v2.codegen.bean.GeneratorExtension
 *  com.mchange.v2.codegen.bean.IndirectingSerializableExtension
 *  com.mchange.v2.codegen.bean.ParsedPropertyBeanDocument
 *  com.mchange.v2.codegen.bean.Property
 *  com.mchange.v2.codegen.bean.PropertyReferenceableExtension
 *  com.mchange.v2.codegen.bean.PropsToStringGeneratorExtension
 *  com.mchange.v2.codegen.bean.SimpleClassInfo
 *  com.mchange.v2.codegen.bean.SimplePropertyBeanGenerator
 */
package com.mchange.v2.c3p0.codegen;

import com.mchange.v1.xml.DomParseUtils;
import com.mchange.v2.c3p0.codegen.C3P0ImplUtilsParentLoggerGeneratorExtension;
import com.mchange.v2.codegen.IndentedWriter;
import com.mchange.v2.codegen.bean.BeanExtractingGeneratorExtension;
import com.mchange.v2.codegen.bean.BeangenUtils;
import com.mchange.v2.codegen.bean.ClassInfo;
import com.mchange.v2.codegen.bean.CompleteConstructorGeneratorExtension;
import com.mchange.v2.codegen.bean.GeneratorExtension;
import com.mchange.v2.codegen.bean.IndirectingSerializableExtension;
import com.mchange.v2.codegen.bean.ParsedPropertyBeanDocument;
import com.mchange.v2.codegen.bean.Property;
import com.mchange.v2.codegen.bean.PropertyReferenceableExtension;
import com.mchange.v2.codegen.bean.PropsToStringGeneratorExtension;
import com.mchange.v2.codegen.bean.SimpleClassInfo;
import com.mchange.v2.codegen.bean.SimplePropertyBeanGenerator;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class BeangenDataSourceGenerator {
    public static void main(String[] argv) {
        try {
            if (argv.length != 2) {
                System.err.println("java " + BeangenDataSourceGenerator.class.getName() + " <infile.xml> <OutputFile.java>");
                return;
            }
            File outFile = new File(argv[1]);
            File parentDir = outFile.getParentFile();
            if (!parentDir.exists()) {
                System.err.println("Warning: making parent directory: " + parentDir);
                parentDir.mkdirs();
            }
            DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = fact.newDocumentBuilder();
            Document doc = db.parse(new File(argv[0]));
            ParsedPropertyBeanDocument parsed = new ParsedPropertyBeanDocument(doc);
            BufferedWriter w = new BufferedWriter(new FileWriter(outFile));
            SimplePropertyBeanGenerator gen = new SimplePropertyBeanGenerator();
            gen.setGeneratorName(BeangenDataSourceGenerator.class.getName());
            IndirectingSerializableExtension idse = new IndirectingSerializableExtension("com.mchange.v2.naming.ReferenceIndirector"){

                protected void generateExtraSerInitializers(ClassInfo info, Class superclassType, Property[] props, Class[] propTypes, IndentedWriter iw) throws IOException {
                    if (BeangenUtils.hasBoundProperties((Property[])props)) {
                        iw.println("this.pcs = new PropertyChangeSupport( this );");
                    }
                    if (BeangenUtils.hasConstrainedProperties((Property[])props)) {
                        iw.println("this.vcs = new VetoableChangeSupport( this );");
                    }
                }

                protected void writeIndirectStoreObject(Property prop, Class propType, IndentedWriter iw) throws IOException {
                    iw.println("com.mchange.v2.log.MLog.getLogger( this.getClass() ).log(com.mchange.v2.log.MLevel.FINE, \"Direct serialization provoked a NotSerializableException! Trying indirect.\", nse);");
                    super.writeIndirectStoreObject(prop, propType, iw);
                }
            };
            gen.addExtension((GeneratorExtension)idse);
            gen.addExtension((GeneratorExtension)new C3P0ImplUtilsParentLoggerGeneratorExtension());
            PropsToStringGeneratorExtension tsge = new PropsToStringGeneratorExtension();
            tsge.setExcludePropertyNames(Arrays.asList("userOverridesAsString", "overrideDefaultUser", "overrideDefaultPassword"));
            gen.addExtension((GeneratorExtension)tsge);
            PropertyReferenceableExtension prex = new PropertyReferenceableExtension();
            prex.setUseExplicitReferenceProperties(true);
            prex.setFactoryClassName("com.mchange.v2.c3p0.impl.C3P0JavaBeanObjectFactory");
            gen.addExtension((GeneratorExtension)prex);
            BooleanInitIdentityTokenConstructortorGeneratorExtension biitcge = new BooleanInitIdentityTokenConstructortorGeneratorExtension();
            gen.addExtension((GeneratorExtension)biitcge);
            if (parsed.getClassInfo().getClassName().equals("WrapperConnectionPoolDataSourceBase")) {
                gen.addExtension((GeneratorExtension)new WcpdsExtrasGeneratorExtension());
            }
            if (BeangenDataSourceGenerator.unmodifiableShadow(doc)) {
                gen.addExtension((GeneratorExtension)new UnmodifiableShadowGeneratorExtension());
            }
            gen.generate(parsed.getClassInfo(), parsed.getProperties(), (Writer)w);
            ((Writer)w).flush();
            ((Writer)w).close();
            System.err.println("Processed: " + argv[0]);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean unmodifiableShadow(Document doc) {
        Element docElem = doc.getDocumentElement();
        return DomParseUtils.uniqueChild((Element)docElem, (String)"unmodifiable-shadow") != null;
    }

    static class UnmodifiableShadowGeneratorExtension
    implements GeneratorExtension {
        BeanExtractingGeneratorExtension bege = new BeanExtractingGeneratorExtension();
        CompleteConstructorGeneratorExtension ccge;

        UnmodifiableShadowGeneratorExtension() {
            this.bege.setExtractMethodModifiers(2);
            this.bege.setConstructorModifiers(1);
            this.ccge = new CompleteConstructorGeneratorExtension();
        }

        public Collection extraGeneralImports() {
            HashSet out = new HashSet();
            out.addAll(this.bege.extraGeneralImports());
            out.addAll(this.ccge.extraGeneralImports());
            return out;
        }

        public Collection extraSpecificImports() {
            HashSet out = new HashSet();
            out.addAll(this.bege.extraSpecificImports());
            out.addAll(this.ccge.extraSpecificImports());
            return out;
        }

        public Collection extraInterfaceNames() {
            return Collections.EMPTY_SET;
        }

        public void generate(ClassInfo info, Class superclassType, Property[] props, Class[] propTypes, IndentedWriter iw) throws IOException {
            SimpleClassInfo innerInfo = new SimpleClassInfo(info.getPackageName(), 9, "UnmodifiableShadow", info.getSuperclassName(), info.getInterfaceNames(), info.getGeneralImports(), info.getSpecificImports());
            SimplePropertyBeanGenerator innerGen = new SimplePropertyBeanGenerator();
            innerGen.setInner(true);
            innerGen.setForceUnmodifiable(true);
            innerGen.addExtension((GeneratorExtension)this.bege);
            innerGen.addExtension((GeneratorExtension)this.ccge);
            innerGen.generate((ClassInfo)innerInfo, props, (Writer)iw);
        }
    }

    static class WcpdsExtrasGeneratorExtension
    implements GeneratorExtension {
        WcpdsExtrasGeneratorExtension() {
        }

        public Collection extraGeneralImports() {
            return Collections.EMPTY_SET;
        }

        public Collection extraSpecificImports() {
            HashSet<String> out = new HashSet<String>();
            out.add("com.mchange.v2.c3p0.ConnectionCustomizer");
            out.add("javax.sql.PooledConnection");
            out.add("java.sql.SQLException");
            return out;
        }

        public Collection extraInterfaceNames() {
            return Collections.EMPTY_SET;
        }

        public void generate(ClassInfo info, Class superclassType, Property[] props, Class[] propTypes, IndentedWriter iw) throws IOException {
            iw.println("protected abstract PooledConnection getPooledConnection( ConnectionCustomizer cc, String idt) throws SQLException;");
            iw.println("protected abstract PooledConnection getPooledConnection(String user, String password, ConnectionCustomizer cc, String idt) throws SQLException;");
        }
    }

    static class BooleanInitIdentityTokenConstructortorGeneratorExtension
    implements GeneratorExtension {
        BooleanInitIdentityTokenConstructortorGeneratorExtension() {
        }

        public Collection extraGeneralImports() {
            return Collections.EMPTY_SET;
        }

        public Collection extraSpecificImports() {
            HashSet<String> out = new HashSet<String>();
            out.add("com.mchange.v2.c3p0.C3P0Registry");
            return out;
        }

        public Collection extraInterfaceNames() {
            return Collections.EMPTY_SET;
        }

        public void generate(ClassInfo info, Class superclassType, Property[] props, Class[] propTypes, IndentedWriter iw) throws IOException {
            BeangenUtils.writeExplicitDefaultConstructor((int)2, (ClassInfo)info, (IndentedWriter)iw);
            iw.println();
            iw.println("public " + info.getClassName() + "( boolean autoregister )");
            iw.println("{");
            iw.upIndent();
            iw.println("if (autoregister)");
            iw.println("{");
            iw.upIndent();
            iw.println("this.identityToken = C3P0ImplUtils.allocateIdentityToken( this );");
            iw.println("C3P0Registry.reregister( this );");
            iw.downIndent();
            iw.println("}");
            iw.downIndent();
            iw.println("}");
        }
    }
}

