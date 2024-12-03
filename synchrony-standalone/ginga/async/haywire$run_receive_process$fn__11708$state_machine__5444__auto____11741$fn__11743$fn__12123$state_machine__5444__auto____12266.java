/*
 * Decompiled with CFR 0.152.
 */
package ginga.async;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.RT;
import clojure.lang.Util;
import clojure.lang.Var;
import ginga.async.haywire$run_receive_process$fn__11708$state_machine__5444__auto____11741$fn__11743$fn__12123$state_machine__5444__auto____12266$fn__12271;
import java.util.concurrent.atomic.AtomicReferenceArray;

public final class haywire$run_receive_process$fn__11708$state_machine__5444__auto____11741$fn__11743$fn__12123$state_machine__5444__auto____12266
extends AFunction {
    Object G__11791;
    Object G__11807;
    Object G__11796;
    Object G__11801;
    Object G__11764;
    Object G__11752;
    Object G__11749;
    Object G__11771;
    Object G__11792;
    Object G__11814;
    Object G__11772;
    Object G__11757;
    Object G__11777;
    Object G__11787;
    Object G__11793;
    Object G__11753;
    Object G__11813;
    Object G__11781;
    Object G__11762;
    Object G__11780;
    Object G__11754;
    Object G__11748;
    Object G__11804;
    Object G__11760;
    Object G__11766;
    Object G__11786;
    Object G__11809;
    Object G__11765;
    Object G__11800;
    Object G__11778;
    Object G__11751;
    Object G__11759;
    Object G__11810;
    Object G__11799;
    Object G__11755;
    Object G__11811;
    Object G__11812;
    Object G__11794;
    Object G__11798;
    Object G__11750;
    Object G__11774;
    Object G__11806;
    Object G__11797;
    Object G__11746;
    Object G__11756;
    Object G__11773;
    Object G__11768;
    Object G__11769;
    Object G__11775;
    Object G__11785;
    Object G__11808;
    Object G__11767;
    Object G__11784;
    Object G__11802;
    Object G__11779;
    Object G__11745;
    Object G__11789;
    Object G__11795;
    Object G__11815;
    Object G__11782;
    Object G__11770;
    Object G__11758;
    Object G__11761;
    Object G__11783;
    Object G__11805;
    Object G__11776;
    Object G__11803;
    Object G__11763;
    Object G__11788;
    Object G__11790;
    Object G__11747;
    public static final Var const__1 = RT.var("clojure.core.async.impl.ioc-macros", "aset-object");
    public static final Object const__2 = 0L;
    public static final Object const__3 = 1L;
    public static final Keyword const__5 = RT.keyword(null, "recur");

    public haywire$run_receive_process$fn__11708$state_machine__5444__auto____11741$fn__11743$fn__12123$state_machine__5444__auto____12266(Object object, Object object2, Object object3, Object object4, Object object5, Object object6, Object object7, Object object8, Object object9, Object object10, Object object11, Object object12, Object object13, Object object14, Object object15, Object object16, Object object17, Object object18, Object object19, Object object20, Object object21, Object object22, Object object23, Object object24, Object object25, Object object26, Object object27, Object object28, Object object29, Object object30, Object object31, Object object32, Object object33, Object object34, Object object35, Object object36, Object object37, Object object38, Object object39, Object object40, Object object41, Object object42, Object object43, Object object44, Object object45, Object object46, Object object47, Object object48, Object object49, Object object50, Object object51, Object object52, Object object53, Object object54, Object object55, Object object56, Object object57, Object object58, Object object59, Object object60, Object object61, Object object62, Object object63, Object object64, Object object65, Object object66, Object object67, Object object68, Object object69, Object object70, Object object71) {
        this.G__11791 = object;
        this.G__11807 = object2;
        this.G__11796 = object3;
        this.G__11801 = object4;
        this.G__11764 = object5;
        this.G__11752 = object6;
        this.G__11749 = object7;
        this.G__11771 = object8;
        this.G__11792 = object9;
        this.G__11814 = object10;
        this.G__11772 = object11;
        this.G__11757 = object12;
        this.G__11777 = object13;
        this.G__11787 = object14;
        this.G__11793 = object15;
        this.G__11753 = object16;
        this.G__11813 = object17;
        this.G__11781 = object18;
        this.G__11762 = object19;
        this.G__11780 = object20;
        this.G__11754 = object21;
        this.G__11748 = object22;
        this.G__11804 = object23;
        this.G__11760 = object24;
        this.G__11766 = object25;
        this.G__11786 = object26;
        this.G__11809 = object27;
        this.G__11765 = object28;
        this.G__11800 = object29;
        this.G__11778 = object30;
        this.G__11751 = object31;
        this.G__11759 = object32;
        this.G__11810 = object33;
        this.G__11799 = object34;
        this.G__11755 = object35;
        this.G__11811 = object36;
        this.G__11812 = object37;
        this.G__11794 = object38;
        this.G__11798 = object39;
        this.G__11750 = object40;
        this.G__11774 = object41;
        this.G__11806 = object42;
        this.G__11797 = object43;
        this.G__11746 = object44;
        this.G__11756 = object45;
        this.G__11773 = object46;
        this.G__11768 = object47;
        this.G__11769 = object48;
        this.G__11775 = object49;
        this.G__11785 = object50;
        this.G__11808 = object51;
        this.G__11767 = object52;
        this.G__11784 = object53;
        this.G__11802 = object54;
        this.G__11779 = object55;
        this.G__11745 = object56;
        this.G__11789 = object57;
        this.G__11795 = object58;
        this.G__11815 = object59;
        this.G__11782 = object60;
        this.G__11770 = object61;
        this.G__11758 = object62;
        this.G__11761 = object63;
        this.G__11783 = object64;
        this.G__11805 = object65;
        this.G__11776 = object66;
        this.G__11803 = object67;
        this.G__11763 = object68;
        this.G__11788 = object69;
        this.G__11790 = object70;
        this.G__11747 = object71;
    }

    @Override
    public Object invoke(Object state_12122) {
        Object ret_value__5446__auto__12525;
        while (true) {
            Object old_frame__5445__auto__12524;
            Object object = old_frame__5445__auto__12524 = Var.getThreadBindingFrame();
            old_frame__5445__auto__12524 = null;
            ret_value__5446__auto__12525 = ((IFn)new haywire$run_receive_process$fn__11708$state_machine__5444__auto____11741$fn__11743$fn__12123$state_machine__5444__auto____12266$fn__12271(this.G__11791, this.G__11807, this.G__11796, this.G__11801, this.G__11764, this.G__11752, this.G__11749, this.G__11771, this.G__11792, this.G__11814, this.G__11772, this.G__11757, this.G__11777, object, state_12122, this.G__11787, this.G__11793, this.G__11753, this.G__11813, this.G__11781, this.G__11762, this.G__11780, this.G__11754, this.G__11748, this.G__11804, this.G__11760, this.G__11766, this.G__11786, this.G__11809, this.G__11765, this.G__11800, this.G__11778, this.G__11751, this.G__11759, this.G__11810, this.G__11799, this.G__11755, this.G__11811, this.G__11812, this.G__11794, this.G__11798, this.G__11750, this.G__11774, this.G__11806, this.G__11797, this.G__11746, this.G__11756, this.G__11773, this.G__11768, this.G__11769, this.G__11775, this.G__11785, this.G__11808, this.G__11767, this.G__11784, this.G__11802, this.G__11779, this.G__11745, this.G__11789, this.G__11795, this.G__11815, this.G__11782, this.G__11770, this.G__11758, this.G__11761, this.G__11783, this.G__11805, this.G__11776, this.G__11803, this.G__11763, this.G__11788, this.G__11790, this.G__11747)).invoke();
            if (!Util.identical(ret_value__5446__auto__12525, const__5)) break;
            Object object2 = state_12122;
            state_12122 = null;
            state_12122 = object2;
        }
        Object var3_3 = null;
        return ret_value__5446__auto__12525;
    }

    @Override
    public Object invoke() {
        AtomicReferenceArray statearr_12267 = new AtomicReferenceArray(RT.intCast(104L));
        ((IFn)const__1.getRawRoot()).invoke(statearr_12267, const__2, this);
        ((IFn)const__1.getRawRoot()).invoke(statearr_12267, const__3, const__3);
        Object var1_1 = null;
        return statearr_12267;
    }
}

