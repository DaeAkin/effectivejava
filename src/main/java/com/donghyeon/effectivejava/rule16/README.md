# 규칙16 계승하는 대신 구성하라

계승은 코드의 재사용을 도와주는 강력한 도구이지만, 항상 최선이진 않습니다.

상위 클래스와 하위 클래스 구현을 같은 프로그래머가 통제하는 단일 패키지에서 사용하면 안전하고,

계승을 고려해 설계되고 그에 맞는 문서를 잘 갖춘 클래스에 사용하는 것도 안전합니다.

이 규칙에서의 계승은 구현계승의 의미인데,

interface가 다른 interface를 extends를 하는 상황말고, 클래스가 다른 클래스를 extends 하는 상황을 말합니다.

계승은 아이러니하게도 캡슐화 원칙을 위반합니다.

하위 클래스가 정상적으로 작동하기 위해서는 상위 클래스의 구현에 의존 할 수 밖에 없습니다.

그러나 상위 클래스의 구현이 릴리즈 되면서 코드내용이 수정될 수 있는데, 그러다 보면 코드변경이 전혀 없는 하위 클래스가 망가질 수 있습니다.

HashSet의 객체가 원소를 몇번 삽입했는지 추적할 수 있는 InstrumentedSet 클래스를 만들어 보겠습니다.

## InstrumentedSet

```java 
/**
 * HashSet을 계승받아 HashSet객체가 생성된 이후에
 * 얼마나 많은 요소가 추가되었는지 확인하는 클래스
 */
public class InstrumentedSet<E> extends HashSet<E> {
    //삽입 횟수
    private int addCount = 0;

    public InstrumentedSet() {
        super();
    }

    public InstrumentedSet(int initialCapacity, float loadFactor) {
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
```



## 테스트하기

이 코드의 테스트코드를 작성해보겠습니다.

```java
class Rule16Tests extends Specification {

    def "HashSet이 생성된 후 얼마나 많은 요소가 추가되었는지 확인하는 테스트"() {
        given:
        List<Integer> addAll당할리스트 = Arrays.asList(1, 5, 3)
        HashSet<Integer> 규칙16커스텀해쉬셋 = new InstrumentedSet<>();

        when:
        규칙16커스텀해쉬셋.addAll(addAll당할리스트)
        // 총 3개 원소를 집어 넣었어요.
        // 그럼 삽입횟수는 3개겠죠?

        then:
        println("삽입횟수 : " + 규칙16커스텀해쉬셋.getAddCount())
        규칙16커스텀해쉬셋.getAddCount() == 3
    }
}
```

ArrayList에 addAll에 사용될 리스트를 만들고 .addAll()를 호출해서 전부다 요소를 넣어보겠습니다.

예상된 삽입 횟수는 3개의 요소를 넣었으니 3개를 넣었다고 생각하지만

실제의 삽입 횟수는 6이 호출됩니다.

## 결과

```
삽입횟수 : 6

Condition not satisfied:

규칙16커스텀해쉬셋.getAddCount() == 3
|          |             |
[1, 3, 5]  6             false
```



무엇이 문제였을까요? 

HashSet의 코드들을 잘 override를 해서 작성한 것 같은데 말이죠.

결과가 이렇게 나온 이유를 알기 위해서는 HashSet의 구현체의 코드를 들여다봐야 합니다.

HashSet의 addAll()은 내부적으로 HashSet의 add() 함수를 이용하기 때문에 이런 결과가 나온 것입니다.

제대로 동작하기 위해서는 하위 클래스에서 작성했던 addAll() 함수를 삭제해서 교정을 해주면 됩니다. 

그러나 이 HashSet 함수가 릴리즈를 거듭나면서 바뀔 가능성이 있기 때문에, 

따라서 우리가 구현한 InstrumentedSet 클래스는 깨지기 쉬운(fragile) 클래스일 수 밖에 없습니다.

## 어떻게할까?

이 문제를 피하기 위해서는 기존 클래스를 계승하는 대, 새로운 클래스에 기존 클래스 객체를 참조하는 private 필드를 하나 두면 됩니다.

이런 설계 기법을 구성(composition)이라고 부르는데, 기존 클래스가 새 클래스의 일부가 되기 때문입니다.

새 클래스는 기존클래스에필요한 메소드만 호출해서 그 결과를 반환하면 되는데, 이런 기법을 전달이라고 하고,

전달 기법을 사용해 구현된 메서드를 전달 메서드라고 부릅니다. 구성 기법을 통해 구현된 클래스는 견고합니다.

기존 클래스의 구현 세부사항에 종속되지 않기 때문입니다. 

## InstrumentedSet

```java
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
```

## ForwardingSet

```java
package com.donghyeon.effectivejava.rule16;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * 전달 클래스!
 */
public class ForwardingSet<E> implements Set {
    private final Set<E> s;

    public ForwardingSet(Set<E> s) {
        this.s = s;
    }

    @Override
    public int size() {
        return s.size();
    }

    @Override
    public boolean isEmpty() {
        return s.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return s.contains(o);
    }

    @Override
    public Iterator iterator() {
        return s.iterator();
    }

    @Override
    public Object[] toArray() {
        return s.toArray();
    }

    @Override
    public boolean add(Object o) {
        return s.add((E) o);
    }

    @Override
    public boolean remove(Object o) {
        return s.remove(o);
    }

    @Override
    public boolean addAll(Collection c) {
        return s.addAll(c);
    }

    @Override
    public void clear() {
        s.clear();
    }

    @Override
    public boolean removeAll(Collection c) {
        return s.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection c) {
        return s.retainAll(c);
    }

    @Override
    public boolean containsAll(Collection c) {
        return s.containsAll(c);
    }

    @Override
    public Object[] toArray(Object[] a) {
        return s.toArray();
    }
}

```

![](https://github.com/DaeAkin/effectivejava/blob/master/src/main/java/com/donghyeon/effectivejava/rule16/image/%EC%A0%84%EB%8B%AC%ED%81%B4%EB%9E%98%EC%8A%A4%EC%84%A4%EB%AA%85.jpeg?raw=true)

InstrumentedSet을 이렇게 설계할 수 있는 것은 HashSet이 제공해야할 기능을 규정하는 Set이라는 인터페이스가 있기 때문입니다. 이런 설계는 안정적일 뿐 아니라 유연성도 아주 높습니다.

InstrumentedSet 클래스는 Set 인터페이스를 구 현하며 Set 객체를 인자로 받는 생성자를 하나 갖고 있습니다. 
결국 이 클래스는 어떤 Set 객체를 인자로 받아, 필요한 기능을 갖춘 다른 Set 객체로 변환하는 구실을 합니다.

계승을 이용한 접근법은 한 클래스에만 적용이 가능하고, 상위 클래스 생성자마다 별도의 생성자를 구현해야 합니다.

하지만 이런 기법을 사용하면 어떤 Set 구현도 원하는 대로 수정할 수 있고, 이미 있는 생성자도 그대로 사용할 수 있습니다.

```java
Set<Date> s = new InstrumentedSet<Date>(new TreeSet<Date>)(cmp));
```



InstrumentedSet과 같은 클래스를 `포장 클래스`(wrapper class)라고 부는데, 다른 Set 객체를 포장하고 있기 때문입니다.
또한 이런 구현 기법은 장식자(decorator) 패턴이라고도 부르는데, 기존 Set 객체에 기능을 덧 붙여 장식하는 구실을 하기 때문입니다.
때로는 구성과 전달 기법을 아울러서 막연하게 위임(delegation)이라고 부르기도 합니다.

그런데 기술적으로 보자면, 포장 객체가 자기 자신을 포장된 객체에 전달하지 않으면 위임이라고 부를 수 없습니다.

## 마무리

계승은 강력한 도구이지만 캡슐화 원칙을 침해하므로 문제를 발생시킬 소지가 있습니다.
상위 클래스와 하위 클래스 사이에 IS-A 관계가 있을 때만 사용하는 것이 좋습니다.

IS-A 관계가 성립해도, 하위 클래스가 상위 클래스와 다른 패키지에 있거나 계승을 고려해 만들어진 상위 클래스가 아니라면 하위클래스는 깨지기 쉽습니다. 이런 문제를 피하려면 구성과 전달 기법을 사용하는 것이 좋습니다.