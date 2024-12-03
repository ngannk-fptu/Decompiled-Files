/*
 * Decompiled with CFR 0.152.
 */
package groovy.xml;

import groovy.lang.Buildable;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;

public class Entity
implements Buildable,
GroovyObject {
    public static final Entity nbsp;
    public static final Entity iexcl;
    public static final Entity cent;
    public static final Entity pound;
    public static final Entity curren;
    public static final Entity yen;
    public static final Entity brvbar;
    public static final Entity sect;
    public static final Entity uml;
    public static final Entity copy;
    public static final Entity ordf;
    public static final Entity laquo;
    public static final Entity not;
    public static final Entity shy;
    public static final Entity reg;
    public static final Entity macr;
    public static final Entity deg;
    public static final Entity plusmn;
    public static final Entity sup2;
    public static final Entity sup3;
    public static final Entity acute;
    public static final Entity micro;
    public static final Entity para;
    public static final Entity middot;
    public static final Entity cedil;
    public static final Entity sup1;
    public static final Entity ordm;
    public static final Entity raquo;
    public static final Entity frac14;
    public static final Entity frac12;
    public static final Entity frac34;
    public static final Entity iquest;
    public static final Entity Agrave;
    public static final Entity Aacute;
    public static final Entity Acirc;
    public static final Entity Atilde;
    public static final Entity Auml;
    public static final Entity Aring;
    public static final Entity AElig;
    public static final Entity Ccedil;
    public static final Entity Egrave;
    public static final Entity Eacute;
    public static final Entity Ecirc;
    public static final Entity Euml;
    public static final Entity Igrave;
    public static final Entity Iacute;
    public static final Entity Icirc;
    public static final Entity Iuml;
    public static final Entity ETH;
    public static final Entity Ntilde;
    public static final Entity Ograve;
    public static final Entity Oacute;
    public static final Entity Ocirc;
    public static final Entity Otilde;
    public static final Entity Ouml;
    public static final Entity times;
    public static final Entity Oslash;
    public static final Entity Ugrave;
    public static final Entity Uacute;
    public static final Entity Ucirc;
    public static final Entity Uuml;
    public static final Entity Yacute;
    public static final Entity THORN;
    public static final Entity szlig;
    public static final Entity agrave;
    public static final Entity aacute;
    public static final Entity acirc;
    public static final Entity atilde;
    public static final Entity auml;
    public static final Entity aring;
    public static final Entity aelig;
    public static final Entity ccedil;
    public static final Entity egrave;
    public static final Entity eacute;
    public static final Entity ecirc;
    public static final Entity euml;
    public static final Entity igrave;
    public static final Entity iacute;
    public static final Entity icirc;
    public static final Entity iuml;
    public static final Entity eth;
    public static final Entity ntilde;
    public static final Entity ograve;
    public static final Entity oacute;
    public static final Entity ocirc;
    public static final Entity otilde;
    public static final Entity ouml;
    public static final Entity divide;
    public static final Entity oslash;
    public static final Entity ugrave;
    public static final Entity uacute;
    public static final Entity ucirc;
    public static final Entity uuml;
    public static final Entity yacute;
    public static final Entity thorn;
    public static final Entity yuml;
    public static final Entity lt;
    public static final Entity gt;
    public static final Entity amp;
    public static final Entity apos;
    public static final Entity quot;
    public static final Entity OElig;
    public static final Entity oelig;
    public static final Entity Scaron;
    public static final Entity scaron;
    public static final Entity Yuml;
    public static final Entity circ;
    public static final Entity tilde;
    public static final Entity ensp;
    public static final Entity emsp;
    public static final Entity thinsp;
    public static final Entity zwnj;
    public static final Entity zwj;
    public static final Entity lrm;
    public static final Entity rlm;
    public static final Entity ndash;
    public static final Entity mdash;
    public static final Entity lsquo;
    public static final Entity rsquo;
    public static final Entity sbquo;
    public static final Entity ldquo;
    public static final Entity rdquo;
    public static final Entity bdquo;
    public static final Entity dagger;
    public static final Entity Dagger;
    public static final Entity permil;
    public static final Entity lsaquo;
    public static final Entity rsaquo;
    public static final Entity euro;
    private final Object entity;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public Entity(String name) {
        MetaClass metaClass;
        CallSite[] callSiteArray = Entity.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
        GStringImpl gStringImpl = new GStringImpl(new Object[]{name}, new String[]{"&", ";"});
        this.entity = gStringImpl;
    }

    public Entity(int name) {
        MetaClass metaClass;
        CallSite[] callSiteArray = Entity.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
        GStringImpl gStringImpl = new GStringImpl(new Object[]{name}, new String[]{"&#", ";"});
        this.entity = gStringImpl;
    }

    @Override
    public void build(GroovyObject builder) {
        CallSite[] callSiteArray = Entity.$getCallSiteArray();
        callSiteArray[0].call(callSiteArray[1].callGetProperty(builder), this.entity);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != Entity.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    @Override
    public /* synthetic */ MetaClass getMetaClass() {
        MetaClass metaClass = this.metaClass;
        if (metaClass != null) {
            return metaClass;
        }
        this.metaClass = this.$getStaticMetaClass();
        return this.metaClass;
    }

    @Override
    public /* synthetic */ void setMetaClass(MetaClass metaClass) {
        this.metaClass = metaClass;
    }

    @Override
    public /* synthetic */ Object invokeMethod(String string, Object object) {
        return this.getMetaClass().invokeMethod((Object)this, string, object);
    }

    @Override
    public /* synthetic */ Object getProperty(String string) {
        return this.getMetaClass().getProperty(this, string);
    }

    @Override
    public /* synthetic */ void setProperty(String string, Object object) {
        this.getMetaClass().setProperty(this, string, object);
    }

    static {
        Object object = Entity.$getCallSiteArray()[2].callConstructor(Entity.class, "nbsp");
        nbsp = (Entity)ScriptBytecodeAdapter.castToType(object, Entity.class);
        Object object2 = Entity.$getCallSiteArray()[3].callConstructor(Entity.class, "iexcl");
        iexcl = (Entity)ScriptBytecodeAdapter.castToType(object2, Entity.class);
        Object object3 = Entity.$getCallSiteArray()[4].callConstructor(Entity.class, "cent");
        cent = (Entity)ScriptBytecodeAdapter.castToType(object3, Entity.class);
        Object object4 = Entity.$getCallSiteArray()[5].callConstructor(Entity.class, "pound");
        pound = (Entity)ScriptBytecodeAdapter.castToType(object4, Entity.class);
        Object object5 = Entity.$getCallSiteArray()[6].callConstructor(Entity.class, "curren");
        curren = (Entity)ScriptBytecodeAdapter.castToType(object5, Entity.class);
        Object object6 = Entity.$getCallSiteArray()[7].callConstructor(Entity.class, "yen");
        yen = (Entity)ScriptBytecodeAdapter.castToType(object6, Entity.class);
        Object object7 = Entity.$getCallSiteArray()[8].callConstructor(Entity.class, "brvbar");
        brvbar = (Entity)ScriptBytecodeAdapter.castToType(object7, Entity.class);
        Object object8 = Entity.$getCallSiteArray()[9].callConstructor(Entity.class, "sect");
        sect = (Entity)ScriptBytecodeAdapter.castToType(object8, Entity.class);
        Object object9 = Entity.$getCallSiteArray()[10].callConstructor(Entity.class, "uml");
        uml = (Entity)ScriptBytecodeAdapter.castToType(object9, Entity.class);
        Object object10 = Entity.$getCallSiteArray()[11].callConstructor(Entity.class, "copy");
        copy = (Entity)ScriptBytecodeAdapter.castToType(object10, Entity.class);
        Object object11 = Entity.$getCallSiteArray()[12].callConstructor(Entity.class, "ordf");
        ordf = (Entity)ScriptBytecodeAdapter.castToType(object11, Entity.class);
        Object object12 = Entity.$getCallSiteArray()[13].callConstructor(Entity.class, "laquo");
        laquo = (Entity)ScriptBytecodeAdapter.castToType(object12, Entity.class);
        Object object13 = Entity.$getCallSiteArray()[14].callConstructor(Entity.class, "not");
        not = (Entity)ScriptBytecodeAdapter.castToType(object13, Entity.class);
        Object object14 = Entity.$getCallSiteArray()[15].callConstructor(Entity.class, "shy");
        shy = (Entity)ScriptBytecodeAdapter.castToType(object14, Entity.class);
        Object object15 = Entity.$getCallSiteArray()[16].callConstructor(Entity.class, "reg");
        reg = (Entity)ScriptBytecodeAdapter.castToType(object15, Entity.class);
        Object object16 = Entity.$getCallSiteArray()[17].callConstructor(Entity.class, "macr");
        macr = (Entity)ScriptBytecodeAdapter.castToType(object16, Entity.class);
        Object object17 = Entity.$getCallSiteArray()[18].callConstructor(Entity.class, "deg");
        deg = (Entity)ScriptBytecodeAdapter.castToType(object17, Entity.class);
        Object object18 = Entity.$getCallSiteArray()[19].callConstructor(Entity.class, "plusmn");
        plusmn = (Entity)ScriptBytecodeAdapter.castToType(object18, Entity.class);
        Object object19 = Entity.$getCallSiteArray()[20].callConstructor(Entity.class, "sup2");
        sup2 = (Entity)ScriptBytecodeAdapter.castToType(object19, Entity.class);
        Object object20 = Entity.$getCallSiteArray()[21].callConstructor(Entity.class, "sup3");
        sup3 = (Entity)ScriptBytecodeAdapter.castToType(object20, Entity.class);
        Object object21 = Entity.$getCallSiteArray()[22].callConstructor(Entity.class, "acute");
        acute = (Entity)ScriptBytecodeAdapter.castToType(object21, Entity.class);
        Object object22 = Entity.$getCallSiteArray()[23].callConstructor(Entity.class, "micro");
        micro = (Entity)ScriptBytecodeAdapter.castToType(object22, Entity.class);
        Object object23 = Entity.$getCallSiteArray()[24].callConstructor(Entity.class, "para");
        para = (Entity)ScriptBytecodeAdapter.castToType(object23, Entity.class);
        Object object24 = Entity.$getCallSiteArray()[25].callConstructor(Entity.class, "middot");
        middot = (Entity)ScriptBytecodeAdapter.castToType(object24, Entity.class);
        Object object25 = Entity.$getCallSiteArray()[26].callConstructor(Entity.class, "cedil");
        cedil = (Entity)ScriptBytecodeAdapter.castToType(object25, Entity.class);
        Object object26 = Entity.$getCallSiteArray()[27].callConstructor(Entity.class, "sup1");
        sup1 = (Entity)ScriptBytecodeAdapter.castToType(object26, Entity.class);
        Object object27 = Entity.$getCallSiteArray()[28].callConstructor(Entity.class, "ordm");
        ordm = (Entity)ScriptBytecodeAdapter.castToType(object27, Entity.class);
        Object object28 = Entity.$getCallSiteArray()[29].callConstructor(Entity.class, "raquo");
        raquo = (Entity)ScriptBytecodeAdapter.castToType(object28, Entity.class);
        Object object29 = Entity.$getCallSiteArray()[30].callConstructor(Entity.class, "frac14");
        frac14 = (Entity)ScriptBytecodeAdapter.castToType(object29, Entity.class);
        Object object30 = Entity.$getCallSiteArray()[31].callConstructor(Entity.class, "frac12");
        frac12 = (Entity)ScriptBytecodeAdapter.castToType(object30, Entity.class);
        Object object31 = Entity.$getCallSiteArray()[32].callConstructor(Entity.class, "frac34");
        frac34 = (Entity)ScriptBytecodeAdapter.castToType(object31, Entity.class);
        Object object32 = Entity.$getCallSiteArray()[33].callConstructor(Entity.class, "iquest");
        iquest = (Entity)ScriptBytecodeAdapter.castToType(object32, Entity.class);
        Object object33 = Entity.$getCallSiteArray()[34].callConstructor(Entity.class, "Agrave");
        Agrave = (Entity)ScriptBytecodeAdapter.castToType(object33, Entity.class);
        Object object34 = Entity.$getCallSiteArray()[35].callConstructor(Entity.class, "Aacute");
        Aacute = (Entity)ScriptBytecodeAdapter.castToType(object34, Entity.class);
        Object object35 = Entity.$getCallSiteArray()[36].callConstructor(Entity.class, "Acirc");
        Acirc = (Entity)ScriptBytecodeAdapter.castToType(object35, Entity.class);
        Object object36 = Entity.$getCallSiteArray()[37].callConstructor(Entity.class, "Atilde");
        Atilde = (Entity)ScriptBytecodeAdapter.castToType(object36, Entity.class);
        Object object37 = Entity.$getCallSiteArray()[38].callConstructor(Entity.class, "Auml");
        Auml = (Entity)ScriptBytecodeAdapter.castToType(object37, Entity.class);
        Object object38 = Entity.$getCallSiteArray()[39].callConstructor(Entity.class, "Aring");
        Aring = (Entity)ScriptBytecodeAdapter.castToType(object38, Entity.class);
        Object object39 = Entity.$getCallSiteArray()[40].callConstructor(Entity.class, "AElig");
        AElig = (Entity)ScriptBytecodeAdapter.castToType(object39, Entity.class);
        Object object40 = Entity.$getCallSiteArray()[41].callConstructor(Entity.class, "Ccedil");
        Ccedil = (Entity)ScriptBytecodeAdapter.castToType(object40, Entity.class);
        Object object41 = Entity.$getCallSiteArray()[42].callConstructor(Entity.class, "Egrave");
        Egrave = (Entity)ScriptBytecodeAdapter.castToType(object41, Entity.class);
        Object object42 = Entity.$getCallSiteArray()[43].callConstructor(Entity.class, "Eacute");
        Eacute = (Entity)ScriptBytecodeAdapter.castToType(object42, Entity.class);
        Object object43 = Entity.$getCallSiteArray()[44].callConstructor(Entity.class, "Ecirc");
        Ecirc = (Entity)ScriptBytecodeAdapter.castToType(object43, Entity.class);
        Object object44 = Entity.$getCallSiteArray()[45].callConstructor(Entity.class, "Euml");
        Euml = (Entity)ScriptBytecodeAdapter.castToType(object44, Entity.class);
        Object object45 = Entity.$getCallSiteArray()[46].callConstructor(Entity.class, "Igrave");
        Igrave = (Entity)ScriptBytecodeAdapter.castToType(object45, Entity.class);
        Object object46 = Entity.$getCallSiteArray()[47].callConstructor(Entity.class, "Iacute");
        Iacute = (Entity)ScriptBytecodeAdapter.castToType(object46, Entity.class);
        Object object47 = Entity.$getCallSiteArray()[48].callConstructor(Entity.class, "Icirc");
        Icirc = (Entity)ScriptBytecodeAdapter.castToType(object47, Entity.class);
        Object object48 = Entity.$getCallSiteArray()[49].callConstructor(Entity.class, "Iuml");
        Iuml = (Entity)ScriptBytecodeAdapter.castToType(object48, Entity.class);
        Object object49 = Entity.$getCallSiteArray()[50].callConstructor(Entity.class, "ETH");
        ETH = (Entity)ScriptBytecodeAdapter.castToType(object49, Entity.class);
        Object object50 = Entity.$getCallSiteArray()[51].callConstructor(Entity.class, "Ntilde");
        Ntilde = (Entity)ScriptBytecodeAdapter.castToType(object50, Entity.class);
        Object object51 = Entity.$getCallSiteArray()[52].callConstructor(Entity.class, "Ograve");
        Ograve = (Entity)ScriptBytecodeAdapter.castToType(object51, Entity.class);
        Object object52 = Entity.$getCallSiteArray()[53].callConstructor(Entity.class, "Oacute");
        Oacute = (Entity)ScriptBytecodeAdapter.castToType(object52, Entity.class);
        Object object53 = Entity.$getCallSiteArray()[54].callConstructor(Entity.class, "Ocirc");
        Ocirc = (Entity)ScriptBytecodeAdapter.castToType(object53, Entity.class);
        Object object54 = Entity.$getCallSiteArray()[55].callConstructor(Entity.class, "Otilde");
        Otilde = (Entity)ScriptBytecodeAdapter.castToType(object54, Entity.class);
        Object object55 = Entity.$getCallSiteArray()[56].callConstructor(Entity.class, "Ouml");
        Ouml = (Entity)ScriptBytecodeAdapter.castToType(object55, Entity.class);
        Object object56 = Entity.$getCallSiteArray()[57].callConstructor(Entity.class, "times");
        times = (Entity)ScriptBytecodeAdapter.castToType(object56, Entity.class);
        Object object57 = Entity.$getCallSiteArray()[58].callConstructor(Entity.class, "Oslash");
        Oslash = (Entity)ScriptBytecodeAdapter.castToType(object57, Entity.class);
        Object object58 = Entity.$getCallSiteArray()[59].callConstructor(Entity.class, "Ugrave");
        Ugrave = (Entity)ScriptBytecodeAdapter.castToType(object58, Entity.class);
        Object object59 = Entity.$getCallSiteArray()[60].callConstructor(Entity.class, "Uacute");
        Uacute = (Entity)ScriptBytecodeAdapter.castToType(object59, Entity.class);
        Object object60 = Entity.$getCallSiteArray()[61].callConstructor(Entity.class, "Ucirc");
        Ucirc = (Entity)ScriptBytecodeAdapter.castToType(object60, Entity.class);
        Object object61 = Entity.$getCallSiteArray()[62].callConstructor(Entity.class, "Uuml");
        Uuml = (Entity)ScriptBytecodeAdapter.castToType(object61, Entity.class);
        Object object62 = Entity.$getCallSiteArray()[63].callConstructor(Entity.class, "Yacute");
        Yacute = (Entity)ScriptBytecodeAdapter.castToType(object62, Entity.class);
        Object object63 = Entity.$getCallSiteArray()[64].callConstructor(Entity.class, "THORN");
        THORN = (Entity)ScriptBytecodeAdapter.castToType(object63, Entity.class);
        Object object64 = Entity.$getCallSiteArray()[65].callConstructor(Entity.class, "szlig");
        szlig = (Entity)ScriptBytecodeAdapter.castToType(object64, Entity.class);
        Object object65 = Entity.$getCallSiteArray()[66].callConstructor(Entity.class, "agrave");
        agrave = (Entity)ScriptBytecodeAdapter.castToType(object65, Entity.class);
        Object object66 = Entity.$getCallSiteArray()[67].callConstructor(Entity.class, "aacute");
        aacute = (Entity)ScriptBytecodeAdapter.castToType(object66, Entity.class);
        Object object67 = Entity.$getCallSiteArray()[68].callConstructor(Entity.class, "acirc");
        acirc = (Entity)ScriptBytecodeAdapter.castToType(object67, Entity.class);
        Object object68 = Entity.$getCallSiteArray()[69].callConstructor(Entity.class, "atilde");
        atilde = (Entity)ScriptBytecodeAdapter.castToType(object68, Entity.class);
        Object object69 = Entity.$getCallSiteArray()[70].callConstructor(Entity.class, "auml");
        auml = (Entity)ScriptBytecodeAdapter.castToType(object69, Entity.class);
        Object object70 = Entity.$getCallSiteArray()[71].callConstructor(Entity.class, "aring");
        aring = (Entity)ScriptBytecodeAdapter.castToType(object70, Entity.class);
        Object object71 = Entity.$getCallSiteArray()[72].callConstructor(Entity.class, "aelig");
        aelig = (Entity)ScriptBytecodeAdapter.castToType(object71, Entity.class);
        Object object72 = Entity.$getCallSiteArray()[73].callConstructor(Entity.class, "ccedil");
        ccedil = (Entity)ScriptBytecodeAdapter.castToType(object72, Entity.class);
        Object object73 = Entity.$getCallSiteArray()[74].callConstructor(Entity.class, "egrave");
        egrave = (Entity)ScriptBytecodeAdapter.castToType(object73, Entity.class);
        Object object74 = Entity.$getCallSiteArray()[75].callConstructor(Entity.class, "eacute");
        eacute = (Entity)ScriptBytecodeAdapter.castToType(object74, Entity.class);
        Object object75 = Entity.$getCallSiteArray()[76].callConstructor(Entity.class, "ecirc");
        ecirc = (Entity)ScriptBytecodeAdapter.castToType(object75, Entity.class);
        Object object76 = Entity.$getCallSiteArray()[77].callConstructor(Entity.class, "euml");
        euml = (Entity)ScriptBytecodeAdapter.castToType(object76, Entity.class);
        Object object77 = Entity.$getCallSiteArray()[78].callConstructor(Entity.class, "igrave");
        igrave = (Entity)ScriptBytecodeAdapter.castToType(object77, Entity.class);
        Object object78 = Entity.$getCallSiteArray()[79].callConstructor(Entity.class, "iacute");
        iacute = (Entity)ScriptBytecodeAdapter.castToType(object78, Entity.class);
        Object object79 = Entity.$getCallSiteArray()[80].callConstructor(Entity.class, "icirc");
        icirc = (Entity)ScriptBytecodeAdapter.castToType(object79, Entity.class);
        Object object80 = Entity.$getCallSiteArray()[81].callConstructor(Entity.class, "iuml");
        iuml = (Entity)ScriptBytecodeAdapter.castToType(object80, Entity.class);
        Object object81 = Entity.$getCallSiteArray()[82].callConstructor(Entity.class, "eth");
        eth = (Entity)ScriptBytecodeAdapter.castToType(object81, Entity.class);
        Object object82 = Entity.$getCallSiteArray()[83].callConstructor(Entity.class, "ntilde");
        ntilde = (Entity)ScriptBytecodeAdapter.castToType(object82, Entity.class);
        Object object83 = Entity.$getCallSiteArray()[84].callConstructor(Entity.class, "ograve");
        ograve = (Entity)ScriptBytecodeAdapter.castToType(object83, Entity.class);
        Object object84 = Entity.$getCallSiteArray()[85].callConstructor(Entity.class, "oacute");
        oacute = (Entity)ScriptBytecodeAdapter.castToType(object84, Entity.class);
        Object object85 = Entity.$getCallSiteArray()[86].callConstructor(Entity.class, "ocirc");
        ocirc = (Entity)ScriptBytecodeAdapter.castToType(object85, Entity.class);
        Object object86 = Entity.$getCallSiteArray()[87].callConstructor(Entity.class, "otilde");
        otilde = (Entity)ScriptBytecodeAdapter.castToType(object86, Entity.class);
        Object object87 = Entity.$getCallSiteArray()[88].callConstructor(Entity.class, "ouml");
        ouml = (Entity)ScriptBytecodeAdapter.castToType(object87, Entity.class);
        Object object88 = Entity.$getCallSiteArray()[89].callConstructor(Entity.class, "divide");
        divide = (Entity)ScriptBytecodeAdapter.castToType(object88, Entity.class);
        Object object89 = Entity.$getCallSiteArray()[90].callConstructor(Entity.class, "oslash");
        oslash = (Entity)ScriptBytecodeAdapter.castToType(object89, Entity.class);
        Object object90 = Entity.$getCallSiteArray()[91].callConstructor(Entity.class, "ugrave");
        ugrave = (Entity)ScriptBytecodeAdapter.castToType(object90, Entity.class);
        Object object91 = Entity.$getCallSiteArray()[92].callConstructor(Entity.class, "uacute");
        uacute = (Entity)ScriptBytecodeAdapter.castToType(object91, Entity.class);
        Object object92 = Entity.$getCallSiteArray()[93].callConstructor(Entity.class, "ucirc");
        ucirc = (Entity)ScriptBytecodeAdapter.castToType(object92, Entity.class);
        Object object93 = Entity.$getCallSiteArray()[94].callConstructor(Entity.class, "uuml");
        uuml = (Entity)ScriptBytecodeAdapter.castToType(object93, Entity.class);
        Object object94 = Entity.$getCallSiteArray()[95].callConstructor(Entity.class, "yacute");
        yacute = (Entity)ScriptBytecodeAdapter.castToType(object94, Entity.class);
        Object object95 = Entity.$getCallSiteArray()[96].callConstructor(Entity.class, "thorn");
        thorn = (Entity)ScriptBytecodeAdapter.castToType(object95, Entity.class);
        Object object96 = Entity.$getCallSiteArray()[97].callConstructor(Entity.class, "yuml");
        yuml = (Entity)ScriptBytecodeAdapter.castToType(object96, Entity.class);
        Object object97 = Entity.$getCallSiteArray()[98].callConstructor(Entity.class, "lt");
        lt = (Entity)ScriptBytecodeAdapter.castToType(object97, Entity.class);
        Object object98 = Entity.$getCallSiteArray()[99].callConstructor(Entity.class, "gt");
        gt = (Entity)ScriptBytecodeAdapter.castToType(object98, Entity.class);
        Object object99 = Entity.$getCallSiteArray()[100].callConstructor(Entity.class, "amp");
        amp = (Entity)ScriptBytecodeAdapter.castToType(object99, Entity.class);
        Object object100 = Entity.$getCallSiteArray()[101].callConstructor(Entity.class, "apos");
        apos = (Entity)ScriptBytecodeAdapter.castToType(object100, Entity.class);
        Object object101 = Entity.$getCallSiteArray()[102].callConstructor(Entity.class, "quot");
        quot = (Entity)ScriptBytecodeAdapter.castToType(object101, Entity.class);
        Object object102 = Entity.$getCallSiteArray()[103].callConstructor(Entity.class, "OElig");
        OElig = (Entity)ScriptBytecodeAdapter.castToType(object102, Entity.class);
        Object object103 = Entity.$getCallSiteArray()[104].callConstructor(Entity.class, "oelig");
        oelig = (Entity)ScriptBytecodeAdapter.castToType(object103, Entity.class);
        Object object104 = Entity.$getCallSiteArray()[105].callConstructor(Entity.class, "Scaron");
        Scaron = (Entity)ScriptBytecodeAdapter.castToType(object104, Entity.class);
        Object object105 = Entity.$getCallSiteArray()[106].callConstructor(Entity.class, "scaron");
        scaron = (Entity)ScriptBytecodeAdapter.castToType(object105, Entity.class);
        Object object106 = Entity.$getCallSiteArray()[107].callConstructor(Entity.class, "Yuml");
        Yuml = (Entity)ScriptBytecodeAdapter.castToType(object106, Entity.class);
        Object object107 = Entity.$getCallSiteArray()[108].callConstructor(Entity.class, "circ");
        circ = (Entity)ScriptBytecodeAdapter.castToType(object107, Entity.class);
        Object object108 = Entity.$getCallSiteArray()[109].callConstructor(Entity.class, "tilde");
        tilde = (Entity)ScriptBytecodeAdapter.castToType(object108, Entity.class);
        Object object109 = Entity.$getCallSiteArray()[110].callConstructor(Entity.class, "ensp");
        ensp = (Entity)ScriptBytecodeAdapter.castToType(object109, Entity.class);
        Object object110 = Entity.$getCallSiteArray()[111].callConstructor(Entity.class, "emsp");
        emsp = (Entity)ScriptBytecodeAdapter.castToType(object110, Entity.class);
        Object object111 = Entity.$getCallSiteArray()[112].callConstructor(Entity.class, "thinsp");
        thinsp = (Entity)ScriptBytecodeAdapter.castToType(object111, Entity.class);
        Object object112 = Entity.$getCallSiteArray()[113].callConstructor(Entity.class, "zwnj");
        zwnj = (Entity)ScriptBytecodeAdapter.castToType(object112, Entity.class);
        Object object113 = Entity.$getCallSiteArray()[114].callConstructor(Entity.class, "zwj");
        zwj = (Entity)ScriptBytecodeAdapter.castToType(object113, Entity.class);
        Object object114 = Entity.$getCallSiteArray()[115].callConstructor(Entity.class, "lrm");
        lrm = (Entity)ScriptBytecodeAdapter.castToType(object114, Entity.class);
        Object object115 = Entity.$getCallSiteArray()[116].callConstructor(Entity.class, "rlm");
        rlm = (Entity)ScriptBytecodeAdapter.castToType(object115, Entity.class);
        Object object116 = Entity.$getCallSiteArray()[117].callConstructor(Entity.class, "ndash");
        ndash = (Entity)ScriptBytecodeAdapter.castToType(object116, Entity.class);
        Object object117 = Entity.$getCallSiteArray()[118].callConstructor(Entity.class, "mdash");
        mdash = (Entity)ScriptBytecodeAdapter.castToType(object117, Entity.class);
        Object object118 = Entity.$getCallSiteArray()[119].callConstructor(Entity.class, "lsquo");
        lsquo = (Entity)ScriptBytecodeAdapter.castToType(object118, Entity.class);
        Object object119 = Entity.$getCallSiteArray()[120].callConstructor(Entity.class, "rsquo");
        rsquo = (Entity)ScriptBytecodeAdapter.castToType(object119, Entity.class);
        Object object120 = Entity.$getCallSiteArray()[121].callConstructor(Entity.class, "sbquo");
        sbquo = (Entity)ScriptBytecodeAdapter.castToType(object120, Entity.class);
        Object object121 = Entity.$getCallSiteArray()[122].callConstructor(Entity.class, "ldquo");
        ldquo = (Entity)ScriptBytecodeAdapter.castToType(object121, Entity.class);
        Object object122 = Entity.$getCallSiteArray()[123].callConstructor(Entity.class, "rdquo");
        rdquo = (Entity)ScriptBytecodeAdapter.castToType(object122, Entity.class);
        Object object123 = Entity.$getCallSiteArray()[124].callConstructor(Entity.class, "bdquo");
        bdquo = (Entity)ScriptBytecodeAdapter.castToType(object123, Entity.class);
        Object object124 = Entity.$getCallSiteArray()[125].callConstructor(Entity.class, "dagger");
        dagger = (Entity)ScriptBytecodeAdapter.castToType(object124, Entity.class);
        Object object125 = Entity.$getCallSiteArray()[126].callConstructor(Entity.class, "Dagger");
        Dagger = (Entity)ScriptBytecodeAdapter.castToType(object125, Entity.class);
        Object object126 = Entity.$getCallSiteArray()[127].callConstructor(Entity.class, "permil");
        permil = (Entity)ScriptBytecodeAdapter.castToType(object126, Entity.class);
        Object object127 = Entity.$getCallSiteArray()[128].callConstructor(Entity.class, "lsaquo");
        lsaquo = (Entity)ScriptBytecodeAdapter.castToType(object127, Entity.class);
        Object object128 = Entity.$getCallSiteArray()[129].callConstructor(Entity.class, "rsaquo");
        rsaquo = (Entity)ScriptBytecodeAdapter.castToType(object128, Entity.class);
        Object object129 = Entity.$getCallSiteArray()[130].callConstructor(Entity.class, "euro");
        euro = (Entity)ScriptBytecodeAdapter.castToType(object129, Entity.class);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "leftShift";
        stringArray[1] = "unescaped";
        stringArray[2] = "<$constructor$>";
        stringArray[3] = "<$constructor$>";
        stringArray[4] = "<$constructor$>";
        stringArray[5] = "<$constructor$>";
        stringArray[6] = "<$constructor$>";
        stringArray[7] = "<$constructor$>";
        stringArray[8] = "<$constructor$>";
        stringArray[9] = "<$constructor$>";
        stringArray[10] = "<$constructor$>";
        stringArray[11] = "<$constructor$>";
        stringArray[12] = "<$constructor$>";
        stringArray[13] = "<$constructor$>";
        stringArray[14] = "<$constructor$>";
        stringArray[15] = "<$constructor$>";
        stringArray[16] = "<$constructor$>";
        stringArray[17] = "<$constructor$>";
        stringArray[18] = "<$constructor$>";
        stringArray[19] = "<$constructor$>";
        stringArray[20] = "<$constructor$>";
        stringArray[21] = "<$constructor$>";
        stringArray[22] = "<$constructor$>";
        stringArray[23] = "<$constructor$>";
        stringArray[24] = "<$constructor$>";
        stringArray[25] = "<$constructor$>";
        stringArray[26] = "<$constructor$>";
        stringArray[27] = "<$constructor$>";
        stringArray[28] = "<$constructor$>";
        stringArray[29] = "<$constructor$>";
        stringArray[30] = "<$constructor$>";
        stringArray[31] = "<$constructor$>";
        stringArray[32] = "<$constructor$>";
        stringArray[33] = "<$constructor$>";
        stringArray[34] = "<$constructor$>";
        stringArray[35] = "<$constructor$>";
        stringArray[36] = "<$constructor$>";
        stringArray[37] = "<$constructor$>";
        stringArray[38] = "<$constructor$>";
        stringArray[39] = "<$constructor$>";
        stringArray[40] = "<$constructor$>";
        stringArray[41] = "<$constructor$>";
        stringArray[42] = "<$constructor$>";
        stringArray[43] = "<$constructor$>";
        stringArray[44] = "<$constructor$>";
        stringArray[45] = "<$constructor$>";
        stringArray[46] = "<$constructor$>";
        stringArray[47] = "<$constructor$>";
        stringArray[48] = "<$constructor$>";
        stringArray[49] = "<$constructor$>";
        stringArray[50] = "<$constructor$>";
        stringArray[51] = "<$constructor$>";
        stringArray[52] = "<$constructor$>";
        stringArray[53] = "<$constructor$>";
        stringArray[54] = "<$constructor$>";
        stringArray[55] = "<$constructor$>";
        stringArray[56] = "<$constructor$>";
        stringArray[57] = "<$constructor$>";
        stringArray[58] = "<$constructor$>";
        stringArray[59] = "<$constructor$>";
        stringArray[60] = "<$constructor$>";
        stringArray[61] = "<$constructor$>";
        stringArray[62] = "<$constructor$>";
        stringArray[63] = "<$constructor$>";
        stringArray[64] = "<$constructor$>";
        stringArray[65] = "<$constructor$>";
        stringArray[66] = "<$constructor$>";
        stringArray[67] = "<$constructor$>";
        stringArray[68] = "<$constructor$>";
        stringArray[69] = "<$constructor$>";
        stringArray[70] = "<$constructor$>";
        stringArray[71] = "<$constructor$>";
        stringArray[72] = "<$constructor$>";
        stringArray[73] = "<$constructor$>";
        stringArray[74] = "<$constructor$>";
        stringArray[75] = "<$constructor$>";
        stringArray[76] = "<$constructor$>";
        stringArray[77] = "<$constructor$>";
        stringArray[78] = "<$constructor$>";
        stringArray[79] = "<$constructor$>";
        stringArray[80] = "<$constructor$>";
        stringArray[81] = "<$constructor$>";
        stringArray[82] = "<$constructor$>";
        stringArray[83] = "<$constructor$>";
        stringArray[84] = "<$constructor$>";
        stringArray[85] = "<$constructor$>";
        stringArray[86] = "<$constructor$>";
        stringArray[87] = "<$constructor$>";
        stringArray[88] = "<$constructor$>";
        stringArray[89] = "<$constructor$>";
        stringArray[90] = "<$constructor$>";
        stringArray[91] = "<$constructor$>";
        stringArray[92] = "<$constructor$>";
        stringArray[93] = "<$constructor$>";
        stringArray[94] = "<$constructor$>";
        stringArray[95] = "<$constructor$>";
        stringArray[96] = "<$constructor$>";
        stringArray[97] = "<$constructor$>";
        stringArray[98] = "<$constructor$>";
        stringArray[99] = "<$constructor$>";
        stringArray[100] = "<$constructor$>";
        stringArray[101] = "<$constructor$>";
        stringArray[102] = "<$constructor$>";
        stringArray[103] = "<$constructor$>";
        stringArray[104] = "<$constructor$>";
        stringArray[105] = "<$constructor$>";
        stringArray[106] = "<$constructor$>";
        stringArray[107] = "<$constructor$>";
        stringArray[108] = "<$constructor$>";
        stringArray[109] = "<$constructor$>";
        stringArray[110] = "<$constructor$>";
        stringArray[111] = "<$constructor$>";
        stringArray[112] = "<$constructor$>";
        stringArray[113] = "<$constructor$>";
        stringArray[114] = "<$constructor$>";
        stringArray[115] = "<$constructor$>";
        stringArray[116] = "<$constructor$>";
        stringArray[117] = "<$constructor$>";
        stringArray[118] = "<$constructor$>";
        stringArray[119] = "<$constructor$>";
        stringArray[120] = "<$constructor$>";
        stringArray[121] = "<$constructor$>";
        stringArray[122] = "<$constructor$>";
        stringArray[123] = "<$constructor$>";
        stringArray[124] = "<$constructor$>";
        stringArray[125] = "<$constructor$>";
        stringArray[126] = "<$constructor$>";
        stringArray[127] = "<$constructor$>";
        stringArray[128] = "<$constructor$>";
        stringArray[129] = "<$constructor$>";
        stringArray[130] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[131];
        Entity.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(Entity.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = Entity.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

