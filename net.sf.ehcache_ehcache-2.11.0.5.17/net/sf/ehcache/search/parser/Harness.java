/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.parser;

import java.io.StringReader;
import net.sf.ehcache.search.parser.EhcacheSearchParser;
import net.sf.ehcache.search.parser.ParseModel;

public class Harness {
    public static void main(String[] args) throws Exception {
        EhcacheSearchParser parser = new EhcacheSearchParser(new StringReader("select *  where  ('name' = 'tom' and (not ('age' = (class foo.bar.Baz)'10' or  'foo' > 11 )) and 'zip' = '21104') group by key order by 'name' desc limit 10"));
        ParseModel model = parser.QueryStatement();
        System.out.println(model);
    }
}

