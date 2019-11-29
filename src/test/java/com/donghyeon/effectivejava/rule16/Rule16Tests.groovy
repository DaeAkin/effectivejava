package com.donghyeon.effectivejava.rule16

import spock.lang.Specification;


class Rule16Tests extends Specification {

    def "HashSet이 생성된 후 얼마나 많은 요소가 추가되었는지 확인하는 테스트"() {
        given:
        List<Integer> addAll당할리스트 = Arrays.asList(1, 5, 3)
        HashSet<Integer> 규칙16커스텀해쉬셋 = new InstrumentedHashSet<>();

        when:
        규칙16커스텀해쉬셋.addAll(addAll당할리스트)
        // 총 3개 원소를 집어 넣었어요.
        // 그럼 삽입횟수는 3개겠죠?

        then:
        println("삽입횟수 : " + 규칙16커스텀해쉬셋.getAddCount())
        규칙16커스텀해쉬셋.getAddCount() == 3
    }
}
