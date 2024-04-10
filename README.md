# Java to Kotlin

## 컬렉션
### 가변 컬렉션
> Java 에서 만드는 컬렉션은 기본적으로 가변 컬렉션이다.  
> add(), remove() 등의 메서드를 이용해서 컬렉션의 요소를 넣고 빼고 할 수 있다.
> ```java
> // new ArrayList<>(); 는 가변 컬렉션을 반환한다.
> var list = new ArrayList<Integer>();
> list.add(1);
> list.remove(3);
> 
> // List.of(); 는 불변 컬렉션을 반환한다.
> var immutableList = List.of(1, 2, 3);
> immutableList.add(4); // 오류 발생
> 
> // Collections.unmodifiableList() 를 이용해 가변 컬렉션을 넣어 불변 컬렉션을 반환한다.
> var unmodifiableList = Collections.unmodifiableList(list);
> unmodifiableList.add(4); // 오류 발생
> ```
> 
> 가변 컬렉션은 예상치 못한 버그를 일으킬 수 있다.  
> 예를 들어 가변 컬렉션을 메서드의 파라미터로 넘기는 경우 메서드 내부에서 가변 컬렉션을 변경하게 되면 해당 가변 컬렉션을 참조하는 객체에도 영향을
> 미치게된다. 
> ```java
> var room = new ChatRoom(members);
> ...
> // 채팅방의 멤버가 가변 컬렉션인 경우
> Member firstOne = getFirstSorted(room.getMembers());
> ...
> 
> private Member getFirstSorted(List<Member> members) {
>    // 파라미터로 받은 members 내의 정보를 조작한다.
>    // List.sort 는 원본에 해당하는 리스트를 정렬한다.
>    // 이는 위의 ChatRoom 객체 내의 members 정보도 같이 조작이된 상태로 변한 것을 의미한다.
>    members.sort(comparing(Member::getName).reversed());
>    return members.get(0);
> } 
> ```
> 
> 가변 컬렉션의 변경성 여부를 막기 위해서는 room.getMembers() 에서 불변 컬랙션을 반환하도록 하는 방법이 있다.
> ```java
> public class ChatRoom {
>     private final List<Member> members;
> 
>     public ChatRoom(List<Member> members) {
>         // 파라미터의 참조가 아닌 신규 List 객체로 방어적 복사를 수행한다.
>         // 다만 List 객체 내의 각각의 요소(Element)에 대한 참조는 동일하다.
>         // 그래서 각각 요소에 대한 값 변경이 일어나지 않도록 List 내의 객체는 불변 객체를 사용하는 것이 부수 효과(Side effect)를 줄일 수 있다.
>         this.members = new ArrayList<>(members);
>     }
>   
>     public List<Member> getMembers() {
>         // 이렇게 방어적 복사를 수행하기 위해서 한 번 더 생각하고 적절한 조치를 취하는 과정은 지겹고 실수하기도 쉽다.
>         return Collections.unmodifiableList(this.members);
>     }
> }
> ```
> 이렇게하면 컴파일 타임에서 오류를 내보내지는 않지만 런타임에 불변 컬렉션을 조작했다는 예외를 던져준다.  
> 다만 이렇게 방어적 코드를 짜기 위해서 한 번 더 생각하고 적절한 조치를 취하는 과정은 지겹고 실수하기도 쉽다.  
> 이런 조치를 매번 취하는 대신에 다음의 관습을 만들어서 수행하는 것이 좋다.  
> 
> 코드는 항상 자신이 새로 방금 생성한 컬렉션만 갱신해야 한다. 참조를 결과로 반환하자마자 이를 불변 객체인 것처럼 취급해야 한다.
> 즉, 생성하되 변경하지는 말라.
> 
> 물론 이 규칙에도 예외가 있다. 보통은 성능을 위해 컬렉션을 가변 컬렉션으로 공유해야 하는 경우가 있다.  
> 이런 경우 이름을 주의 깊게 붙이고 공유 범위를 최대한 제한하라. 한 함수 안에서만 이런 가변 컬렉션을 사용하는 게 가장 이상적이고, 
> 클래스의 비공개 메서드 사이에 공유하는 것은 받아들일만 하지만, 모듈 경계를 벗어나 가변 컬렉션을 공유하는 게 합리적인 경우는 아주 드물다.
> 
> **코틀린 컬렉션**  
> 자바와 달리 코틀린과 코틀린 표준 라이브러리는 불변 컬렉션이 기본이다.  
> ```kotlin
> // 리스트를 만드는 listOf 는 불변 컬렉션을 반환한다.
> // 불변 컬렉션인 list 변수는 add 메서드를 사용할 수 없다. (컴파일 오류 발생)
> val list = listOf(1, 2, 3)
> // list.add(4)
> 
> // 가변 컬렉션을 생성하려면 mutableListOf 함수처럼 앞에 가변(mutable)을 붙여줘야한다.
> // 가변 컬렉션인 mutableList 는 add 메서드를 사용할 수 있다. 
> val mutableList = mutableListOf(1, 2, 3)
> mutableList.add(4)
> ```
> 
> 코틀린은 자바와 다르게 Collection<E>, List<E> 인터페이스가 기본적으로 불변 컬렉션이고, 해당 인터페이스를 확장한게
> MutableCollection<E>, MutableList<E> 이다.  
> 
> 여기서 문제가 되는 것은 MutableList<E> 가 List<E> 를 확장하는 것이기 때문에 파라미터 타입이 List<E> 인 경우 
> 불변 컬렉션(List<E>) 및 가변 컬렉션(MutableList<E>)가 모두 올 수 있다는 것이다.  
> 
> 그래서 만약 List<E> 를 멤버 변수로 갖는 클래스가 있을 때 해당 멤버 변수가 변하지 않음을 보장할 수 없다.(Deep copy 를 하면 보장할 순 있다.)
> ```kotlin
> // ChatRoom 의 멤버 변수인 members 는 불변 컬렉션인 List<Member> 객체이다.
> class ChatRoom(val members: List<Member>) {
> }
> 
> val mutableMembers = mutableListOf(
>     Member("멤버1"),
>     Member("멤버2"),
>     Member("멤버3")
> )
> // 불변 컬렉션인 members 에 가변 컬렉션인 mutableMembers 를 넣는 것이 가능하다.
> val chatRoom = ChatRoom(members = mutableMembers)
> println("chatRoom = ${chatRoom.members}")
>
> // 다만 chatRoom.members 의 원본인 mutableMembers 에 변이가 일어나면 
> // 가변으로 선언한 chatroom.members 로 속절 없이 변경된다. 
> mutableMembers.add(Member("멤버4"))
> println("chatRoom = ${chatRoom.members}")
> ```
> 가변 컬렉션이 불변 컬렉션을 확장(상속)하기 때문에 불변 컬렉션 인터페이스를 사용하는 것만으로는 불변 컬렉션을 달성할 수는 없다!
> 
> 코틀린은 컬렉션이 가변이건 불변이건 컬렉션의 변이를 일으키지 않는 방향으로 설계되어 있다.

---

## 계산과 동작
### 개요
> 함수를 나누는 더 유용한 방법은 계산(calculation) 과 동작(action)으로 나누는 것이다.

### 계산
> 함수가 계산이 되기 위해서는 입력이 같을 때 항상 같은 출력을 반환해야만 한다.
> 함수는 불변 값을 반환해야만 한다. 가변 값, 예를 들어서 객체의 가변 상태를 반환해주기만 하는 함수의 경우는 가변 값을 반환하기 때문에
> 이는 계산이 아닌 동작이다.  
> ```kotlin
> fun fullName(customer: Customer) = "${customer.givenName} ${customer.familyName}"
> ```
> fullName 은 계산이다. 이 암수는 같은 Customer 에 대해 항상 같은 결과를 돌려준다.  

### 동작
> 동작은 호출된 시점이나 얼마나 많이 실행됐느냐에 따라 결과가 달라진다.  
> 어떤 경우든 외부에서 관찰가능한 부수 효과가 있는 함수는 계산이 아니라 동작이다.
> void 나 Unit 을 반환하는 함수는 거의 항상 동작이다. 왜냐하면 이런 함수가 무슨 일을 한다면 부수 효과를 통해서만 영향을 끼칠 수 있기
> 때문이다. 
> 
> 동작이 있으면 코드를 자유롭게 리팩터링할 수 없다. 리팩터링으로 인해서 각 동작이 호출되는 시점이나 호출 여부가 달라질 수 있기 때문이다.  
> 읽기와 쓰기 동작의 순서를 바꾸면 동시성 버그가 생긴다. 연속적인 두 동작이 있는데 첫 번째 동작이 성공한 후 두 번째 동작이 실패하면 오류 처리가
> 더 복잡해진다.  
> 
> 반대로 계산은 언제 호출해도 관계가 없고, 여러번 반복해 호출해도 아무 영향이 없다. 

---

