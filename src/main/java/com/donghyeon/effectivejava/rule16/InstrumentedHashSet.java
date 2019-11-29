package com.donghyeon.effectivejava.rule16;

import java.util.Collection;
import java.util.HashSet;

/**
 * HashSet을 계승받아 HashSet객체가 생성된 이후에
 * 얼마나 많은 요소가 추가되었는지 확인하는 클래스
 */
public class InstrumentedHashSet<E> extends HashSet<E> {
    //삽입 횟수
    private int addCount = 0;

    public InstrumentedHashSet() {
        super();
    }

    public InstrumentedHashSet(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    @Override
    public boolean add(E e) {
        addCount++;
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c);
    }

    public int getAddCount() {
        return addCount;
    }
}