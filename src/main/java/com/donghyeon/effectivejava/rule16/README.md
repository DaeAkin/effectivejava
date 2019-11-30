# 규칙16 계승하는 대신 구성하라

계승은 코드의 재사용을 도와주는 강력한 도구이지만, 항상 최선이진 않습니다.

상위 클래스와 하위 클래스 구현을 같은 프로그래머가 통제하는 단일 패키지에서 사용하면 안전하고,

계승을 고려해 설계되고 그에 맞는 문서를 잘 갖춘 클래스에 사용하는 것도 안전합니다.

이 규칙에서의 계승은 구현계승의 의미인데,

interface가 다른 interface를 extends를 하는 상황말고, 클래스가 다른 클래스를 extends 하는 상황을 말합니다.

계승은 아이러니하게도 캡슐화 원칙을 위반합니다.

하위 클래스가 정상적으로 작동하기 위해서는 상위 클래스의 구현에 의존 할 수 밖에 없습니다.

그러나 상위 클래스의 구현이 릴리즈 되면서 코드내용이 수정될 수 있는데, 그러다 보면 코드변경이 전혀 없는 하위 클래스가 망가질 수 있습니다.

HashSet의 객체가 원소를 몇번 삽입했는지 추적할 수 있는 InstrumentedHashSet 클래스를 만들어 보겠습니다.

### InstrumentedHashSet

```java 
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
```



### 테스트하기

이 코드의 테스트코드를 작성해보겠습니다.

```java
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
```

ArrayList에 addAll에 사용될 리스트를 만들고 .addAll()를 호출해서 전부다 요소를 넣어보겠습니다.

예상된 삽입 횟수는 3개의 요소를 넣었으니 3개를 넣었다고 생각하지만

실제의 삽입 횟수는 6이 호출됩니다.

### 결과

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

따라서 우리가 구현한 InstrumentedHashSet 클래스는 깨지기 쉬운(fragile) 클래스일 수 밖에 없습니다.

### 어떻게할까?

이 문제를 피하기 위해서는 기존 클래스를 계승하는 대, 새로운 클래스에 기존 클래스 객체를 참조하는 private 필드를 하나 두면 됩니다.

이런 설계 기법을 구성(composition)이라고 부르는데, 기존 클래스가 새 클래스의 일부가 되기 때문입니다.

새 클래스는 기존클래스에필요한 메소드만 호출해서 그 결과를 반환하면 되는데, 이런 기법을 전달이라고 하고,

전달 기법을 사용해 구현된 ㅁ



