package com.donghyeon.effectivejava.rule17;

public class Super {
    public Super() {
        System.out.println("Super()");
        overrideMe();
    }
    public void overrideMe() {
        System.out.println("Super overrideMe()");
    }
}
