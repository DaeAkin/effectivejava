package com.donghyeon.effectivejava.rule17;

import java.time.LocalDateTime;

public class Sub extends Super{
    
    private final LocalDateTime localDateTime;

    public Sub() {
        System.out.println("Sub()");
        localDateTime = LocalDateTime.now();
    }

    @Override
    public void overrideMe() {
        System.out.println("sub overrideMe()");
        System.out.println(localDateTime.toLocalDate());
    }
}
