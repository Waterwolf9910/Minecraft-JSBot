package com.waterwolfies.js_bot;

import org.graalvm.polyglot.HostAccess.Export;
import org.graalvm.polyglot.HostAccess.Implementable;

@Implementable
public class test {
    private String b = "c";

    @Export
    public String getB() {
        return this.b;
    }

    @Export
    public void setB(String b) {
        this.b = b;
    }
}
