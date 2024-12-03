/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.Interceptor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BenchmarkInterceptor
implements Interceptor {
    protected Map calls = new LinkedHashMap();

    public Map getCalls() {
        return this.calls;
    }

    public void reset() {
        this.calls = new HashMap();
    }

    @Override
    public Object beforeInvoke(Object object, String methodName, Object[] arguments) {
        if (!this.calls.containsKey(methodName)) {
            this.calls.put(methodName, new LinkedList());
        }
        ((List)this.calls.get(methodName)).add(System.currentTimeMillis());
        return null;
    }

    @Override
    public Object afterInvoke(Object object, String methodName, Object[] arguments, Object result) {
        ((List)this.calls.get(methodName)).add(System.currentTimeMillis());
        return result;
    }

    @Override
    public boolean doInvoke() {
        return true;
    }

    public List statistic() {
        LinkedList<Object[]> result = new LinkedList<Object[]>();
        Iterator iter = this.calls.keySet().iterator();
        while (iter.hasNext()) {
            Object[] line = new Object[3];
            result.add(line);
            line[0] = iter.next();
            List times = (List)this.calls.get(line[0]);
            line[1] = times.size() / 2;
            int accTime = 0;
            Iterator it = times.iterator();
            while (it.hasNext()) {
                Long start = (Long)it.next();
                Long end = (Long)it.next();
                accTime = (int)((long)accTime + (end - start));
            }
            line[2] = (long)accTime;
        }
        return result;
    }
}

