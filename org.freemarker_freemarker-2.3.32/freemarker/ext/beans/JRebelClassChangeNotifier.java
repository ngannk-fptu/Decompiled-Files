/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.zeroturnaround.javarebel.ClassEventListener
 *  org.zeroturnaround.javarebel.ReloaderFactory
 */
package freemarker.ext.beans;

import freemarker.ext.beans.ClassChangeNotifier;
import freemarker.ext.beans.ClassIntrospector;
import java.lang.ref.WeakReference;
import org.zeroturnaround.javarebel.ClassEventListener;
import org.zeroturnaround.javarebel.ReloaderFactory;

class JRebelClassChangeNotifier
implements ClassChangeNotifier {
    JRebelClassChangeNotifier() {
    }

    static void testAvailability() {
        ReloaderFactory.getInstance();
    }

    @Override
    public void subscribe(ClassIntrospector classIntrospector) {
        ReloaderFactory.getInstance().addClassReloadListener((ClassEventListener)new ClassIntrospectorCacheInvalidator(classIntrospector));
    }

    private static class ClassIntrospectorCacheInvalidator
    implements ClassEventListener {
        private final WeakReference ref;

        ClassIntrospectorCacheInvalidator(ClassIntrospector w) {
            this.ref = new WeakReference<ClassIntrospector>(w);
        }

        public void onClassEvent(int eventType, Class pClass) {
            ClassIntrospector ci = (ClassIntrospector)this.ref.get();
            if (ci == null) {
                ReloaderFactory.getInstance().removeClassReloadListener((ClassEventListener)this);
            } else if (eventType == 1) {
                ci.remove(pClass);
            }
        }
    }
}

