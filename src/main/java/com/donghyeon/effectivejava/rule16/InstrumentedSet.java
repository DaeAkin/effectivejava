package com.donghyeon.effectivejava.rule16;

import java.util.Collection;
import java.util.Set;

/**
 * HashSet을 계승받아 HashSet객체가 생성된 이후에
 * 얼마나 많은 요소가 추가되었는지 확인하는 클래스
 */
public class InstrumentedSet<E> extends ForwardingSet<E> {
    //삽입 횟수
    private int addCount = 0;

    public InstrumentedSet(Set<E> s) {
        super(s);
    }

    @Override
    public boolean add(Object e) {
        addCount++;
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection c) {
        addCount += c.size();
        return super.addAll(c);
    }

    public int getAddCount() {
        return addCount;
    }
}