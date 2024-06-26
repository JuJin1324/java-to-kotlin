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

## 최상위 함수
### 개요
> 자바에서는 모든 변수와 함수가 클래스 내부에 존재해야한다. 하지만 코틀린은 클래스 외부에 함수가 존재할 수 있다. 이를 `최상위 함수`라고 한다.

### 코틀린에서 최상위 함수
> 자바에서의 정적(static) 메서드들을 코틀린에서는 기본적으로 최상위 함수로 정의한다.  
> 다만 인터페이스를 구현하는 능력이 필요하거나 함수들을 좀 더 밀접하게 묶고 싶은 경우에만 함수를 최상위 함수 대신 싱글턴 객체안의
> 메서드로 정의하는 쪽을 택해야 한다.  
> 클래스 안에서 정적 행동 방식과 비정적 행동 방식을 조합해야 하는 경우나, MyType.of(...) 스타일의 팩터리 메서드를 작성해야 하는
> 경우에만 동반 객체(companion object)의 메서드로 함수를 정의해야 한다.  

### 자바와의 호환
> 자바에서는 최상위 함수를 인식하지 못한다.  
> 자바에서는 코틀린에서 정의된 최상위 함수를 사용하기 위해서는 해당 최상위 함수가 속한 파일과 뒤에 Kt 를 붙인 이름을 클래스처럼 사용해야한다.  
> 예시) Shortlists.kt 파일에 최상위 함수 sorted 를 정의했다고 하면 자바에서는 다음의 방식으로 사용할 수 있다.  
> ```java
> var reordered = ShortlistsKt.sorted(...);
> ```
> 
> 뒤에 Kt 라는 이름이 거슬린다면 커스텀한 이름으로 변경해주는 애너테이션인 @file:JvmName() 을 코틀린 파일 최상단에 붙인다.  
> Shortlists.kt
> ```kotlin
> @file:JvmName("Shortlists")
> package travelator
> ...
> ```

---

## 단일식 함수
### 정의
> 다중식 함수는 중괄호 사이에 정의하고 return 키워드를 사용해 함수의 결과를 정의하는 함수이다.
> ```kotlin
> fun add(a: Int, b: Int): Int {
>     return a + b
> }
> ```
> 단일식 함수는 식이 하나로 이뤄져 있어서 return 을 쓰는 대신 등호 뒤에 반환할 값을 계산하는 식을 덧붙이는 형태이다.
> ```kotlin
> fun add(a: Int, b: Int): Int = a + b
> ```

### 단일식 함수를 계산에만 사용하라
> 우리가 단일식 함수를 계산에만 사용하는 관습을 택한다면, 단일식 함수를 사용하는 의도를 다른 사람들에게 알릴 수 있는 수단이 생긴다. 
> 단일식 함수를 보면 그 함수가 동작이 아님을 알 수 있으므로 해당 함수를 더 안전하게 리팩터링할 수 있다. 
> 실직적으로 이 말은 단일식 함수가 Unit 을 반환하거나 가변 상태를 읽거나 쓰지 말아야 한다는 뜻이다. 가변 상태를 읽거나 쓰는 것에는
> I/O 를 수행하는 행위도 포함된다.

---

## 확장 함수
### 함수와 메서드
> 객체 지향 프로그래밍은 메시지를 객체에 보내서 문제를 해결하는 기술이다. 메서드를 호출하면 런타임 시스템이 올바른 메서드가 호출되도록 처리해 주고,
> 메서드가 인스턴스의 상태에 접근할 수 있게 해 준다.  
> myString 의 길이를 알고 싶은가? 그 객체에 myString.length() 라고 메시지를 보내라.
> 
> 반대로 함수형 프로그래밍에서는 값을 사용해 함수를 호출함으로써 문제를 해결한다.
> myString 의 길이를 알고 싶으면 length(myString)처럼 함수에 값을 넘긴다.   
> 함수는 타입 위에 정의되지 않고, 함수의 파라미터와 결과가 타입을 소유한다.  

### 확장 함수
> 코틀린 확장 함수는 메서드처럼 보이지만 실제로는 그냥 함수다. 
> 이름이 암시하듯이 확장 함수는 어떤 타입에 적용할 수 있는 연산을 확장할 수 있게 해 준다.
> 
> 다음과 같이 확장 함수를 정의하면, 메서드인 것처럼 이 함수를 호출할 수 있다.
> ```kotlin
> fun Customer.nameForMarketing() = "${familyName.uppercase()}, $givenName"
> 
> val s = customer.nameForMarketing()
> ```
> 확장 함수가 자신이 확장하는 클래스의 비공개 멤버 필드에 대한 접근 권한을 부여받지 않는다는 사실에 유의하라. 
> 확장 함수는 자신이 정의된 영역에 있는 일반 함수와 똑같은 권한을 가진다.

### 확장 프로퍼티
> 코틀린은 확장 프로퍼티도 지원한다. 확장 프로퍼티는 프로퍼티(실제로는 메서드임)처럼 호출할 수 있는 정적 함수다. 
> 확장 프로퍼티는 실제 클래스에 필드를 추가할 수 없으므로 확장 프로퍼티에 데이터를 저장할 수는 없고, 값을 계산해 돌려줄 수만 있다.
> ```kotlin
> val Customer.nameForMarketing get() = "${familyName.uppercase()}, $givenName"
> ```

### 다형성
> 확장 함수 호출이 메서드 호출처럼 보이지만, 실제로 확장 함수는 객체에 메시지를 보내는 것과 같지 않다. 
> 다형적인 메서드 호출의 경우 코틀린은 실행 시점에 수신 객체의 동적 타입을 사용해 어떤 메서드를 실행할지 결정하다.
> 확장의 경우 코틀린은 컴파일 시점에 수신 객체의 정적 타입을 바탕으로 어떤 함수를 호출할지 결정한다.
> 다형적으로 확장 함수를 사용해야 할 필요가 있다면 보통은 확장 함수에서 다형적인 메서드를 호출하는 방식으로 이런 필요를 만족시킨다.

### 변환 함수명
> Java 에서 변환 메서드들을 모아놓은 유틸리티 클래스에서의 변환 메서드의 이름은 xxxFrom 으로 짓는 것이 좋다.  
> 예시로 JSON 에서 Customer 객체로 변환하는 정적 메서드는 다음과 같이 한다.
> ```java
> static Customer customerFrom(JsonNode node) {
>     ...
> }
> ```
> 다만 유틸리티 클래스를 사용하여 변환하는 경우 함수를 합성하기 시작하면 가독성이 떨어지게 된다.
> ```java  
> var marketingLength = nameForMarketing(customerFrom(node)).legnth();
> ```
> 코틀린에서는 JsonNode 에 변환 함수를 확장 함수로 추가할 수 있으며, nameForMarketing 함수를 Customer 객체에 확장 함수로 추가하여
> 아래의 함수 합성을 일으키지 않고 가독성있게 표현할 수 있다.
>
> 코틀린에서는 확장 함수를 사용해서 JsonNode 에 Customer 객체 변환 함수를 만들어줄 수 있기 때문에
> customerFrom 이 아닌 toCustomer 메서드를 추가해서 다음의 형태로 호출한다.
> ```kotlin
> fun JsonNode.toCustomer(): Customer = ...
> 
> val marketingLength = jsonNode.toCustomer().nameForMarketing().length
> ```

### 널이 될 수 있는 파라미터
> null 이 될 수 있는 가능성이 있는 객체에 메시지를 보내려면 안전한 호출 연산자 `?.` 를 쓸 수 있다. 하지만
> 안전한 호출 연산자는 파라미터에서는 도움이 되지 않는다. 널이 될 수 있는 참조를 널이 아닌 파라미터를 취하는 함수에 전달할 때는 조건문으로
> 호출을 감싸야 한다.
> ```kotlin
> val greeting: String? = when (customer) {
>     null -> null
>     else -> greetingForCustomer(customer)
> }
> ```
> 
> let, apply, also 등의 코틀린 영역 함수가 이럴 때 도움이 된다. 특히 let 은 수신 개게를 람다의 파라미터로 바꿔준다.
> ```kotlin
> val greeting: String? = customer?.let { greetingForCustomer(it) }
> ```
> 여기서 `?.` 는 customer 가 null 이 아닐 때만 let 이 호출되도록 보장한다. 따라서 람다의 파라미터인 it 은 결코 널이 될 수 없고,
> 람다의 본문 안에서 (널이 아닌 파라미터를 받는) 함수에 전달할 수 있다. ?.let 은 단일 인자를 위한 안전한 호출 연산이라고 생각할 수 있다.
> 
> 내포된 호출을 let 을 사용해 파이프라인으로 만든 호출로 펼치면 추가된 모든 메커니즘이 문법적인 잡음을 일으키며, 코드의 의도를 흐릿하게 만든다.
> ```kotlin
> val reminder: String? = customer
>   ?.let {nextTripForCustomer(it) }
>   ?.let { timeUtilDepartureOfTrip(it, currentTime) }
>   ?.let { durationToUserFriendlyText(it) }
>   ?.let { it + " until your next trip!" }
> ```
> 문제가 되는 파라미터를 확장 함수의 수신 객체로 만들면 호출을 직접 연결해서 애플리케이션 로직이 드러나게 할 수 있다.
> ```kotlin
> val reminder: String? = customer
>   ?.nextTrip()
>   ?.timeUntilDeparture(currentTime)
>   ?.toUserFriendlyText()
>   ?.plus(" until your next trip!")
> ```

### 널이 될 수 있는 수신 객체
> 확장 함수의 수신 객체는 실제로는 파라미터이기 때문에 null 일 수도 있다. 따라서 anObject.method() 와 
> anObject.extensionFunction() 은 겉보기에는 동등한 호출로 보이지만, anObject 가 null 이면 method 가 절대로 호출될 수 없는
> 반면 extensionFunction 은 수신 객체의 타입이 널이 될 수 있는 타입인 경우 anObject 가 null 이어도 호출될 수 있다.
> 
> 이런 특성을 사용해 앞에서 본 파이프라인에서 안내문을 생성하는 각 단계를 Trip? 의 확장으로 추출할 수 있다.
> ```kotlin
> fun Trip?.reminderAt(currentTime: ZonedDateTime): String? = 
>     this?.timeUntilDeparture(currentTime)
>         ?.toUserFriendlyTexT()
>         ?.plus(" until your next trip!")
> 
> val reminder: String? = customer.nextTrip().reminderAt(currentTime)
> ```
> 확장 안에서 this 를 역참조할 때 안전한 호출 연산자를 사용했다는 점에 유의하라. this 가 메서드 안에서는 결코 null 일 수 없지만
> 확장 함수의 안에서는 null 일 수 있다.
> 
> 반면 이제는 호출하는 함수의 널 가능성 흐름을 더 알아보기 어려워졌다. 타입 검사를 통과하지만, 확장을 호출하는 코드에서는
> 널 가능성이 눈에 보이지 않기 때문이다.  
> 
> Trip?.reminderAt 에는 또 다른 두드러진 단점이 존재한다. 널이 아닌 Trip 에 대해 이 확장을 호출해도 반환 타입이 항상 널이
> 될 수 있는 String? 타입이 된다는 점이다. 이런 경우 코드를 다음과 같이 써야 한다.
> ```kotlin
> val trip: Trip = ...
> val reminder: String = trip.reminderAt(currentTime) ?: error("Should never happen")
> ```
> `fun Trip?.reminderAt(currentTime: ZonedDateTime): String?` 처럼 널이 될 수 있는 타입(여기서는 Trip?)의 확장 함수를 
> 작성한다면 수신 객체가 널일 때 null 을 반환하는 코드를 작성해서는 안된다.  
> 널을 반환하는 확장 함수가 필요하다면 확장을 널이 될 수 없는 타입(Trip)의 확장으로 정의하고 확장 호출 시 안전한 호출 연산자(.?)를 사용하라.
> ```kotlin
> // 널이 될 수 있는 타입인 경우 null 을 반환할 수 있는 String? 이어서는 안된다.
> fun Trip?.reminderAt(currentTime: ZonedDateTime): String = 
>     this?.timeUntilDeparture(currentTime)
>         ?.toUserFriendlyTexT()
>         ?.plus(" until your next trip!")
>         ?: "Start planning your next trip. The world's your oyster!"
> ```

### 확장 함수 사용 용례
> 객체의 동작은 객체 내의 메서드를 구현해서 사용한다.  
> 객체의 확장 함수는 해당 객체를 이용한 계산이 필요한 경우 확장 함수로 정의하여 사용한다.  

---

## 필드, 접근자, 프로퍼티
### 필드 접근
> 자바 클래스에서 멤버 필드를 공개로 설정하고 외부 클라이언트에서 공개 필드에 직접 접근하도록 프로그램을 짜지 않는 이유는  
> 필드 사이에 변해서는 안 되는 관계를 유지할 수 없다. 그리고 직접 필드 접근은 다형적이지 않기 때문에 하위 클래스가 필드 구현을
> 변경할 수 없다.
> 
> 코틀린 언어는 필드 직접 접근을 지원하지 않는다. 코틀린에서는 접근자(Getter)를 필드인 것처럼 접근할 수 있다.

### 계산된 프로퍼티
> 언제 계산된 프로퍼티를 선택해야만 하고 언제 메서드를 선택해야만 할까?
> 
> 좋은 대략적인 규칙은 같은 타입에 속한 다른 프로퍼티에만 의존하고 계산 비용이 싼 경우에는 계산된 프로퍼티를 택하라는 것이다.

---

## 연산자
### plus 연산자
> ```kotlin
> class Money private constructor(
>     val amount: BigDecimal,
>     val currency: Currency
> ){
>     ...
>     fun add(that: Money): Money {
>         require(currency == that.currency) {
>             "cannot add Money values of different currencies"
>         } 
>         return Money(amount.add(that.amount), currency)
>     } 
> }
> 
> // 클라이언트에서 사용
> val grossPrice = netPrice.add(netPrice.mul(taxRate))
> ```
> 위와 같이 인스턴스를 인자로 받아서 새로 만든 인스턴스를 반환하는 add 메서드를 코틀린에서는 클라이언트에서 
> operator 연산자인 `+` 연산자를 사용하도록 변경할 수 있다.  
> ```kotlin
> ...
> operator fun plus(that: Money): Money {
>     require(currency == that.currency) {
>         "cannot add Money values of different currencies"
>     } 
>     return Money(amount.add(that.amount), currency) 
> }
> 
> // 클라이언트에서 사용
> val grossPrice = netPrice + netPrice * taxtRate
> ```

### 기존 자바 클래스를 위한 연산자
> 위의 Money 클래스는 amount 프로퍼티를 자바 표준 라이브러리의 BigDecimal 로 표현한다. 
> 코틀린 표준 라이브러리에는 자바 표준 라이브러리가 제공하는 클래스에 대한 연산자를 정의하는 확장 함수가 들어있다.
> 그래서 위의 코드에서 `amount.add(that.amount)` 를 `amount + that.amount` 으로 변경할 수 있다.  

### invoke operator
> 코틀린에서는 불변 객체 생성시 자바에서 처럼 of 이름의 팩토리 메서드를 사용하는 대신 invoke operator 함수를 정의하여 사용한다.
> invoke operator 함수를 정의하면 코틀린 생성자 처럼 사용할 수 있다. 
> 
> ```kotlin
> val proposal = Money(BigDecimal("9.99"), GBP)
> 
> val proposal = Money.Companion.invoke(BigDecimal("9.99"), GBP)
> ```
> 위 두 호출은 모두 같다.
> 
> ```kotlin
> class Money private constructor(
>     val amount: BigDecimal,
>     val currency: Currency
> ) {
>     ...
>     
>     companion object {
>         @JvmStatic
>         fun of(amount: BigDecimal, currency: Currency) =
>             invoke(amount, currency)
> 
>         operator fun invoke(amount: BigDecimal, currency: Currency) =
>             Money(
>                 amount.setSacle(currency.defaultFractionDigits),
>                 currency
>             ) 
>     }
> }
> ```
> of 메서드는 자바 메서드와의 호환을 위해서 만들었으며 프로젝트에서 해당 클래스를 사용하는 자바 클라이언트 코드가 없는 경우에는 
> of 메서드를 제거한다.

---

## 자바 스트림에서 이터러블이나 시퀀스로
### 자바 스트림
> ```java
> public static List<String> translateWordsUntilStop(List<String> strings) {
>     return strings
>         .stream
>         .map(word -> translate(word))
>         .takeWhile(translation -> !translation.equalsIgnoreCase("STOP"))
>         .collect(toList());
> }
> ```
> 자바 스트림은 지연 계산을 수행한다. strings.map(...).takeWhile(...) 은 아무 일도 하지 않고 collect 같은 최종 연산이 값을
> 빨아낼 수 있는 파이프라인만 설치한다. 지연 계산은 뒤쪽 파이프라인 단계가 앞쪽 단계에서 수행해야 하는 작업의 양을 제한한다는 뜻이다.
> 
> 이 코드는 collect 가 파이프라인을 통해 값을 빨아들이고, takeWhile 은 술어 (조건식 람다)가 false 를 반환하면 이전 파이프라인
> 단계에서 값을 빨아들이는 일을 중단하기 때문에 이 코드가 작동한다.
> 
> 값을 빨아들이는 연산에 대해 이야기하자면, 컬렉션이 작은 경우 스트림이 놀랍도록 느리다. 스트림은 모든 CPU 코어를 최대한 활용하고 싶을 정도로
> 대규모 데이터를 처리하는 경우에 유용하다. 그래서 장바구니에 있는 다섯 가지 상품의 가격 합계를 계산할 때는 적당하지 않다.
> 
> 코틀린은 병렬 연산을 구현하려고 시도하지 않고 두 가지 추상화만을 제공한다. 첫 번째는 컬렉션 변환과 축약에 유용한 이터러블(iterable) 이고,
> 두 번째는 지연 계산을 제공하는 시퀀스(sequence) 이다.

### 코틀린 이터러블
> 코틀린은 컬렉션 연산을 정의하기 위해 새로운 인터페이스를 정의하는 대신 Iterable 에 대한 확장 함수를 제공한다.
> ```java
> // java 코드
> public static double averageNonBlankLength(List<String> strings) {
>     return strings
>         .stream()
>         .filter(s -> !s.isBlank())
>         .mapToInt(String::length)
>         .sum()
>         / (double) strings.size();
> }
> ```
> ```kotlin
> fun averageNonBlankLength(strings: List<String>): Double = 
>     (strings
>           .filter { it.isNotBlank() }
>           .map(String::length)
>           .sum()
>           / strings.size.toDouble()
>     ) 
> ```
> 여기 있는 filter 는 Iterable 에 대한 확장 함수다. 하지만 다른 Stream 을 반환하는 Stream.filter 와 달리 코틀린 filter 는
> List 를 반환한다.(List 도 Iterable 이기 때문에 함수 호출을 계속 연쇄할 수 있다.)
> map 도 List 를 반환하므로 이 식은 메모리에 2개의 리스트(filter 로 처리된 리스트와 map 으로 처리된 리스트)를 추가로 만든다.
> 
> 이 코틀린 코드는 컬렉션 크기가 크지 않다면 빠르게 작동한다. 이런 성격은 컬렉션이 긴 경우에만 빨라지는 자바 스트림과는 반대다.
> 컬렉션이 크다면 코틀린에서는 시퀀스로 전환할 수 있다.

### 코틀린 시퀀스
> 코틀린 Sequence 추상화는 자바 스트림과 같은 지연 계산을 제공한다. Sequence 에 대한 map 연산은 다른 Sequence 를 반환한다.
> 시퀀스 연쇄의 연산은 어떤 최종 연산이 파이프라인 평가를 요구할 때 이뤄진다. Collection 이나 Iterable 심지어 Iterator 에도
> 시퀀스 변환을 위한 asSequence() 확장 함수가 들어있다.
> ```kotlin
> fun averageNonBlankLength(strings: List<String>): Double = 
>     (strings
>           .asSequence()
>           .filter { it.isNotBlank() }
>           .map(String::length)
>           .sum()
>           / strings.size.toDouble()
>     )  
> ```
> Iterable 에 대한 연산은 즉시 계산이지만 Sequence 에 대한 연산은 지연 계산이기 때문에 이 둘을 아무 불이익 없이
> 바꿔 사용할 수는 없다. 이들이 비슷한 API 를 제공한다는 점은 이 예제와 같은 상황에서 이터러블과 시퀀스를 서로 전환하고 싶을 때
> 소스 코드를 거의 바꾸지 않아도 된다는 사실을 의미한다.
>
> 위의 함수의 시퀀스 버전에서는 각 단계의 결과를 저장하기 위한 중간 리스트 생성 비용이 들지 않는다. 하지만 원소 갯수가 적다면
> 파이프라인을 만들고 실행하는 비용이 리스트를 생성하는 비용보다 더 비싸진다. 예제의 경우 Int 길이를 여전히 Integer 로
> 박싱해야 하지만 모든 리스트의 원소를 박싱하지는 않고 한 번에 하나씩만 하게 된다. 게다가 API 설계자들은 박시을 피하기 위해
> 더 영리한 해법을 제공한다.
> ```kotlin
> fun averageNonBlankLength(strings: List<String>): Double = 
>     (strings
>           .asSequence()
>           .filter { it.isNotBlank() }
>           .sumOf(String::length)
>           / strings.size.toDouble()
>     )   
> ```
> 이 예제의 경우 sumOf 가 그런 영리한 해법이다. sumOf 는 Int 를 반환하는 함수를 파라미터로 받는 방식으로 박싱을 피한다.
> sumOf 가 최종 연산이라서 다른 시퀀스나 컬렉션을 반환하지 않기 때문에 이런 식의 성능 향상이 가능하다.

### 다중 이터레이션
> 자바 스트림에서 다음 코드는 사용을 피해야 한다.
> ```java
> public static double averageNonBlankLength(List<String> strings) {
>     return averageNonBlankLength(strings.stream());
> }
> 
> public static double averageNonBlankLength(Stream<String> strings) {
>     return strings
>         .filter(s -> !s.isBlank())
>         .mapToInt(String::length)
>         .sum()
>         / (double) strings.count(); 
> }
> ```
> Stream 에는 size 프로퍼티가 없지만 count() 가 같은 결과를 돌려주기 때문에 count() 를 사용한다.
> 이 코드를 실행하면 IllegalStateException 예외가 발생한다.
> 
> 문제는 Stream 은 모든 원소를 소비하면 (여기서는 sum 이 모든 원소를 소비한다) 다시 처음으로 돌아가 count 를 통해
> 원소 갯수를 셀 수는 없다.
> 
> 이제 Sequence 에 대해 같은 일을 진행해 보자.
> ```kotlin
> // 정상 작동
> fun averageNonBlankLength(strings: List<String>): Double = 
>     averageNonBlankLength(strings.asSequence())
>
> // 오류 발생
> fun averageNonBlankLength(strings: Iterator<String>) : Double =
>     averageNonBlankLength(strings.asSequence())
> 
> fun averageNonBlankLength(strings: Sequence<String>) : Double =
>     (strings
>           .filter { it.isNotBlank() }
>           .sumOf(String::length)
>           / strings.count().toDouble()
>     )    
> ```
> 몇몇 시퀀스는 여러 번 이터레이션해도 안전하다는 점이 드러난다. 예를 들어 메모리에 저장된 컬렉션에 따라 뒷받침되는 시퀀스의 경우
> 여러 번 이터레이션해도 안전하다. 하지만 이제는 Sequence 가 Iterator 에 따라 뒷받침되기 때문에 (sum 에 의한) 첫 번째 실행은
> Iterator.hasNext() 가 false 를 반환할 때까지 진행된다. 하지만 (count 로 원소 갯수를 세기 위해) 이 Sequence 를 다시 
> 실행하려고 할 때 Iterator 의 상태가 변경되지 않기 때문에 hasNext() 가 바로 false 를 반환한다. 이로 인해 strings.count() 가
> 0을 반환하면 함수가 항상 Infinity 를 반환하게 된다.
> 
> 이런 동작은 바람직하지 않기 때문에 이터레이터를 둘러싸는 시퀀스는 의도적으로 Sequence.constrainOnce() 로 졔약해서 이런 동작을
> 막는다. constrainOnce() 는 시퀀스를 두 번 소비하려고 시도하면 IllegalStateException 을 던진다.
> 
> 위의 예제는 맨 마지막에 count 를 쓰는 대신, 첫 번째 이터레이션을 수행하면서 개수를 세는 방식으로 문제를 해결할 수 있다.
> ```kotlin
> fun averageNonBlankLength(strings: Sequence<String>) : Double {
>     var count = 0
>     return (strings
>           .onEach { count++ }
>           .filter { it.isNotBlank() }
>           .sumOf(String::length)
>           / count.toDouble()) 
> }
> ```
> Stream 과 Sequence 의 핵심은 이들이 크기가 정해져 있지 않은 큰 데이터집합을 다룰 수 있게 해 준다는 점에 있다.
> 그런데 이런 데이터 집합의 원소 개수를 하나하나 세서 전체 크기를 알아내는 과정은 그리 효율적이지 못하다.
> 게다가 항상 개수를 셀 수 있는 것도 아니다. 보통 Sequence 를 한 번 이상 이터레이션할 수 있는 경우라 해도 실제로는 애초에 우리가
> Sequence 를 사용해야만 했던 이유로 인해 개수를 세는 과정이 비효율적일 가능성이 크다.

### 스트림, 이터러블 시퀀스 사이에 선택하기
> 처음에는 자바의 스트림 코드를 이터러블로 변환한다. 그리고 만약 이터러블이 큰 컬렉션에 대해 너무 느리다는 사실이 판명되면(또는
> 메모리를 너무 많이 잡아먹는다는 사실이 판명되면) 이터러블을 시퀀스로 바꾼다. 시퀀스로 바꾸는 것만으로 충분하지 않다면 스트림으로
> 바꿔서 병렬성의 이점을 살리도록 시도해볼 수 있다.
> 
> 코틀린 표준 라이브러리에는 Stream<T>.asSequence() 와 Sequence<T>.asStream() 확장 함수가 들어 있어서 일을 하는 도중에
> 스트림으로 바꿔탈 수 있게 해준다.

### 계산과 동작
> 계산의 경우 호출 위치를 변경해도 계산 자체의 결과나 그 계산을 사용하는 다른 코드의 결과에 영향을 끼치지 못하기 때문에
> 리팩터링을 해도 안전하다. 반대로 동작을 이터러블에서 시퀀스로 옮기거나 반대 방향으로 옮기면 호출되는 순서가 달라질 수 있고
> 그에 따라 프로그램의 출력도 달라질 수 있다.

---

## 캡슐화한 컬렉션에서 타입 별명으로
### 자바의 캡슐화한 컬렉션
> 자바에는 확장 함수가 없기 때문에 컬렉션과 관련된 메서드를 붙이고 싶으면 컬렉션을 감싸는 객체를 만들어서 해당 컬렉션과 관련된 메서드를
> 추가해서 사용하는 방식으로 한다.
> 
> 보통 이런 클래스들은 다형적인 동작이 없으므로 메서드를 정적 메서드로 정의할 수도 있다. 하지만 자바에서는 보통 정적 메서드의 발견 가능성이
> 메서드보다 훨씬 좋지 않기 때문에 일반 메서드를 사용한다.
> 
> 자바 컬렉션 인터페이스는 객체 지향이라는 뿌리를 따라서 근본적으로 가변적이다. 반면 코틀린은 컬렉션을 값 타입으로 다룬다. 
> 공유된 컬렉션을 변경하면 온갖 문제에 직면하게 된다. 
> 
> 컬렉션을 캡슐화하지 않으면, 클라이언트 코드는 컬렉션을 캡슐화한 도메인 클래스 안에 연산을 정의하지 않고 
> 풍부한 컬렉션 API 를 통해 필요한 연산을 정의할 수 있다.
> 
> ```kotlin
> typealias Route = List<Journey>
> ```

--- 

## 인터페이스에서 함수로
### 객체 지향 캡슐화
> ```kotlin
> data class Email(
>     val to: EmailAddress,
>     val from: EmailAddress,
>     val subject: String,
>     val body: String
> )
> ```
> 이메일을 보내기 위해서 객체 지향 방식으로 다음과 같이 구현한다.
> ```kotlin
> class EmailSender(
>     private val serverAddress: InetAddress,
>     private val username: String,
>     private val password: String
> ) {
>     fun send(email: Email) {
>         sendEmail(
>             email,
>             serverAddress,
>             username,
>             password
>         )
>     }
> }
> 
> // 클라이언트
> val sender: EmailSender = EmailSender(
>     inetAddress("smtp.github.com"),
>     "username",
>     "password"
> )
> 
> sender.send(
>     Email(
>         to = parse("support@github.com"),
>         from = parse("support@gmail.com"),
>         subject = "Thanks",
>         body = "..."
>     )
> )
> ```
> 이메일을 보낼 때마다 serverAddress, username, password 를 넣지 않기 위해서 EmailSender 클래스에 캡슐화 하였다.
> 그래서 이메일을 전송하기 위해서 send 메서드를 호출할 때 email 정보만 넘기면 된다. 
>
> ```kotlin
> interface ISendEmail {
>     fun send(email: Email)
> }
> 
> class EmailSender(
>     ...
> ): ISendEmail {
>     override fun send(email: Email) {
>         sendEmail(
>             email,
>             serverAddress,
>             username,
>             password
>         )
>     } 
> }
> ```
> 인터페이스를 추출하는 경우도 자주 있다.

### 함수형 캡슐화
> 함수형으로 `부분 적용`은 함수의 인자 중 일부를 고정시키면서 인자가 더 적은 새 함수를 만들어내는 기법이다.  
> 부분 적용을 적용하여 함수형 캡슐화를 달성한다.
> 
> ```kotlin
> fun createEmailSender(
>     serverAddress: InetAddress,
>     username: String,
>     password: String 
> ): (Email) -> Unit = {
>     email -> 
>         sendEmail(
>             email,
>             serverAddress,
>             username,
>             password
>         )
> }
> 
> // 클라이언트
> val sender: (Email) -> Unit = createEmailSender(
>     inetAddress("smtp.github.com"),
>     "username",
>     "password"
> )
> 
> sender(   // 혹은 sender.invoke
>     Email(
>         to = parse("support@github.com"),
>         from = parse("support@gmail.com"),
>         subject = "Thanks",
>         body = "..."
>     )
> )
> ```

### 객체에 인터페이스 대신 함수를 넘겨야하는 이유
> 인터페이스에 메서드가 추가될 수 있으며 객체가 사용하는 함수 이상의 메서드들이 인터페이스에 정의되어 있는경우 쓸모없는 추가 구현이 필요해짐. 
> 그래서 객체가 필요로하는 함수만 받도록함. 또한 객체지향에서는 기능의 단위인 함수를 넘길 수 없어서 어쩔수 없이 함수가 1개인 인터페이스를 넘기지만 
> 함수형 언어에서는 그럴 필요없이 함수를 넘기면 됨.

--- 

## 코틀린 추상 클래스와 인터페이스
### 추상 클래스
> 추상 클래스 는 대략적인 설계의 명세와 공통의 기능을 구현한 클래스이다. 즉, 구체적이지 않은 것이다. 
> 추상 클래스 를 상속하는 하위 클래스 는 추상 클래스의 내용을 더 구체화 해야 한다.
> ```kotlin
> abstract class Vehicle(val name : String, val color : String, val weight : Double) {
>     // abstract 로 정의한 추상 프로퍼티이므로 하위 클래스에서 반드시 재정의해야한다.
>     abstract var maxSpeed : Double 
>     
>     // 초기값을 갖는 일반 프로퍼티 (인터페이스에서는 불가능)	
>     var year = "2021"
>    
>     // abstract 로 정의한 추상 메소드이므로 하위 클래스에서 반드시 재정의해야한다.
>     abstract fun start()
>     abstract fun stop()
>     
>     fun displaySpecs() {
>     	  println("Name : $name, Color : $color, Weight : $weight, Year : $year, MaxSpeed : $maxSpeed")
>     }
> }
> ```
> Vehicle 클래스 는 abstract로 정의한 추상 클래스이므로 기본적인 설계만 정의하고 있다. 
> abstract 를 사용한 maxSpeed, start(), stop()은 반드시 하위 클래스에서 재정의 해줘야한다.

### 인터페이스
> 인터페이스 역시 대략적인 설계 명세를 구현하고 인터페이스 를 상속하는 하위 클래스 에서 이를 구체화하는 것은 동일하다. 
> 하지만 인터페이스에서는 프로퍼티의 상태 정보를 저장할 수 없다.
> ```kotlin
> interface Vehicle {
> 	val name : String
> 	val color : String
> 	val weight : Double
> }
> 
> interface Pet {
> val name : String = "puppy" // 불가능!
> }
> ```
> Pet Interface 의 경우 정의와 동시에 초기화까지 해주고 있는데 인터페이스 에선 불가능하다.
> 하지만 예외가 있다. val 로 선언한 프로퍼티 는 게터 를 통해서 필요한 내용을 구현할 수 있다.
> ```kotlin
> interface Pet {
>   var category : String // 추상 프로퍼티
>   val message : String  // val 로 선언하면 게터의 구현이 가능하다.
>       get() = "I'm cutty"
>   
>   fun feeding()  // 추상 메소드
>   fun patting() { // 구현부를 포함할 수 있다. 구현부를 포함하면 일반 메소드
>   	println("Keep patting")
>   }
> }
> ```
> 게터 가 사용 가능하지만 그렇다고 보조필드 가 사용가능한 것은 아니다.

### 봉인된 클래스(Sealed class)
> Kotlin 에서는 when 이라는 키워드를 사용하여 조건을 나눌 수 있다. 
> 이렇게 조건을 나누면서도 조건마다 값을 반환할 수 있는데, when 이 값을 반환해야 하는 경우라면, 예외없이 모든 경우를 다뤄야 한다.
> 다음과 같은 경우를 보자.  
> Example1. when을 통한 분기 처리와 값 반환
> ```kotlin
> interface Fruit
> class Apple:Fruit
> class Banana:Fruit
> 
> fun main(){
>     val fruits = arrayListOf(Apple(), Banana())
>     val someFruit = when(fruits[0]){
>         is Apple -> "Apple"
>         is Banana -> "Banana"
>         else -> "No Fruit"    //빠지면 컴파일 오류
>     }
>     println(someFruit)
> }
> //결과 : Apple
> ```
> Example1에서는 someFruit 에 when 을 통하여 문자열 값을 넣어주고 있다. 
> Fruit 을 상속받는 것은 실제로 Apple 과 Banana 뿐이지만, Example1의 when 에서 else가 빠지면 컴파일 오류로 실행되지 않는다. 
> Sealed class 는 `Fruit의 자식 클래스는 Apple과 Banana가 전부야!`라는 사실을 컴파일러에게 알려주는 역할을 수행하며, 
> 이것이 Sealed class 의 핵심 기능이기도 하다.
> 
> Sealed class를 관리하는 방법에는 두 가지가 있다.
> 하나는 Sealed class 의 중첩 클래스로 포함시키는 것과, 다른 하나는 중첩 클래스로 포함시키지 않는 것이다. 
> 두 경우 모두 Sealed class 에 포함시킬 클래스는 Sealed class 의 자식 클래스로 정의한다.
> ```kotlin
> // Example2-1. nested class 로 Sealed class 에 포함시키기
> sealed class Fruit{
>     class Apple: Fruit()
>     class Banana: Fruit()
> }
> 
> fun main(){
>     val fruits = arrayListOf(Fruit.Apple(), Fruit.Banana())
>     val someFruit = when(fruits[0]) {
>         is Fruit.Apple -> "Apple"
>         is Fruit.Banana -> "Banana"
>     }
>     println(someFruit)
> }
> //결과 : Apple
> 
> // Example2-2. nested class를 이용하지 않고 Sealed class에 포함시키기
> sealed class Fruit
> class Apple:Fruit()
> class Banana:Fruit()
> 
> fun main(){
>     val fruits = arrayListOf(Apple(), Banana())
>     val someFruit = when(fruits[0]) {
>         is Apple -> "Apple"
>         is Banana -> "Banana"
>     }
>     println(someFruit)
> }
> //결과 : Apple
> ```
> 물론 두 가지 경우를 혼합하여도 실행은 되는데, 특별한 경우가 아니면 한 가지의 방법만 선택하는 것이 유지, 보수에 좋을 것이라 생각한다. 
> 중첩 클래스를 이용하면 분기할 클래스 앞에 Sealed class 의 이름을 적어주어야 하는 것이 차이점이다.
> 
> **Sealed class 특징**  
> * 기본 가시성은 public, open 이다.  
> * Sealed class 는 추상 클래스이고, 객체화할 수 없으며 private 생성자를 갖고 있다.
> * Sealed class 의 하위 클래스들은 동일한 파일에 정의되어야 한다. 서로 다른 파일에서 정의할 수 없다.
> * Sealed interface 도 있다.(2020.02.22 기준으로 kotlin 1.5 시험 버전에서 사용 가능한 상태이다.)
> * Sealed class 를 사용하면 타입 분기로 사용되는 when 이 반드시 값을 반환해야 하고, Sealed class 타입을 인자로 받을 때, 
> 나눠지는 조건은 Sealed class 의 하위 클래스 타입 전부이어야하며, else 가 없어도 정상 작동한다.

### 참조사이트
> [[코틀린 완전정복] 추상 클래스와 인터페이스](https://velog.io/@k906506/Kotlin-추상-클래스-VS-인터페이스)  
> [[Kotlin] 클래스 9 — 봉인된 클래스(sealed class), 열거형 클래스(enum class)](https://medium.com/depayse/kotlin-클래스-9-봉인된-클래스-sealed-class-열거형-클래스-enum-class-44a49465a3d)  

---

## 코루틴
### 정의
> 실행의 지연과 재개를 허용함으로써, 비선점적 멀티태스킹을 위한 서브 루틴을 일반화한 컴퓨터 프로그램 구성요소

### 비선점적 멀티태스킹이란?
> 비선점형 : 하나의 프로세스가 CPU를 할당받으면 종료되기 전까지 다른 프로세스가 CPU를 강제로 차지할 수 없습니다. (코루틴)  
> 선점형 : 하나의 프로세스가 다른 프로세스 대신에 프로세서(CPU)를 강제로 차지할 수 있습니다. (쓰레드)  
> 따라서 코루틴은 병행성은 제공하지만, 병렬성은 제공하지 않습니다.
> * 병행성(=동시성, Concurrency) : 논리적으로 병렬로 작업이 실행되는 것처럼 보이는 것
> * 병렬성(Parallelism) : 물리적으로 병렬로 작업이 실행되는 것
> 
> 선점형으로 비동기 처리 시에는 하기와 같은 몇 가지 문제점이 있었습니다.
> * 코드의 복잡성 : 독립적인 쓰레드 안에서 각 루틴이 동작하기에 동시성을 제어하는 복잡한 코드가 필요하며 이는 코드 흐름 파악을 어렵게 합니다.
> * 비용 : 무분별하게 쓰레드를 사용하면 Context switching 리소스 비용으로 프로그램 성능이 저하됩니다.
> 
> 이러한 문제를 해결하기 위해 다양한 방법이 나오는데, 코루틴은 이런 단점을 해결해 주는 언어적 지원 방법이며 비선점적 멀티태스킹을 사용하여 
> 해결하고 있습니다. 코루틴은 쓰레드가 아닌 쓰레드 내 동작하는 하나의 작업 단위이며 정의된 다양한 구성요소의 집합인 
> Context 를 오버라이드 하며 실행됩니다. 따라서 쓰레드 내 Context switching 없이 여러 코루틴을 실행, 중단, 재개하는 상호작용을 통해 
> 병행성을 갖기에 쓰레드와 메모리 사용이 줄어들고 개발자가 직접 작업을 스케줄링 할 수 있도록 합니다.

### 서브루틴이란?
> 프로그램은 여러 루틴의 조합으로 진행되는데, 메인 루틴과 서브 루틴으로 나뉩니다.
> * 메인 루틴 : 프로그램 전체의 개괄적인 동작으로 main 함수에 의해 수행되는 흐름
> * 서브 루틴 : 반복적인 기능을 모은 동작으로 main 함수 내에서 실행되는 개별 함수의 흐름
>
> 서브 루틴은 메모리에 기능을 모아 놓고, 호출 시 저장된 메모리로 이동한 뒤 실행 후 반환문을 통해 원래 호출 위치로 돌아옵니다.
> 코루틴은 서브 루틴과 비슷하지만 큰 차이점이 있습니다.
> 서브 루틴은 단일 진입 지점에서 시작 후 단일 반환 지점에서 종료되는 것에 반해, 코루틴은 진입 후 반환문이 없더라도 임의 지점에서 실행 중 동작을 중단하고 이후 해당 지점에서부터 실행을 재개합니다. (진입과 반환이 여러 개입니다.)
> 이는 내부적으로 Continuation Passing Style(CPS, 연속 전달 방식)과 State machine 을 이용하여 동작합니다.
> 코루틴에서 함수 호출 시 연산 결과 및 다음 수행 작업과 같은 제어 정보를 가진 일종의 콜백 함수인 Continuation 을 전달하며 
> 각 함수의 작업이 완료되면 Continuation 을 호출합니다. 이를 통해 상태를 연속적으로 전달하며 컨텍스트를 유지하고 코루틴 실행 관리를 위한 
> State machine 에 따라 코드 블록을 구분해 실행합니다.

### 코루틴(Coroutine)은 왜 쓰이는가?
> 코틀린 언어를 개발한 Jetbrain 에서는 멀티 쓰레딩 문제를 간소화된 비동기 작업 방식으로 해결할 수 있도록 코루틴을 개발했습니다.
> 코루틴 예제 (비선점형)
> ```kotlin
> fun main() {
>     // 메인 루틴
>     // 새 코루틴을 실행하고 완료 전까지 현재 쓰레드를 blocking 하는 코루틴 빌더 함수로 전체 작업 완료 전까지 다른 작업 할당이 불가합니다.
>     runBlocking {
>         // 서브 루틴
>         // 새 코루틴을 실행하고 완료 전까지 현재 쓰레드를 blocking 하지 않는 코루틴 빌더 함수로 다른 작업 할당이 가능합니다.
>         val goToWork = launch {
>             goToWorkCoroutine()
>         }
>         val playMusicWhileGoingToWork = launch {
>             playMusicCoroutine()
>         }
>         goToWork.join() // goToWork 코루틴 실행이 끝날 때까지 대기합니다.
>         playMusicWhileGoingToWork.cancel() // playMusicWhileGoingToWork 코루틴을 종료합니다.
>     
>         startWorkCoroutine()
>         val leaveWork = launch {
>             leaveWorkCoroutine()
>         }
>         val playMusicWhileLeaveWork = launch {
>             playMusicCoroutine()
>         }
>         leaveWork.join()
>         playMusicWhileLeaveWork.cancel()
>     }
> }
> 
> // 실행 중 쓰레드는 블로킹하지 않으면서 실행 중 코루틴은 일시 중단할 수 있는 함수입니다.
> suspend fun goToWorkCoroutine() {
>     println("Go to work")
>     delay(1000) // 현재 루틴을 잠시 대기시키는 함수로 선언 위치에 따라 메인 쓰레드를 블로킹할 수도 있습니다.
> }
> 
> suspend fun playMusicCoroutine() {
>     println("Play music")
>     while (true) {
>         println("Listening")
>         delay(500)
>     }
> }
> 
> suspend fun leaveWorkCoroutine() {
>     println("Leave work")
>     delay(1000)
> }
> 
> suspend fun startWorkCoroutine() {
>     println("Start work")
>     println("Working")
>     delay(2000)
> }
> ```
> suspend 함수는 코루틴이 일시 중단하는 함수이다. 정확히 말하면 코루틴 함수가 실행되는 과정에서 suspend 키워드를 가진 함수를 만나게 되면 더이상 
> 아래코드를 실행하지 않고 멈추고 코루틴을 block-> 탈출한다. 탈출 후 메인 쓰레드(runBlocking 밖)의 다른 코드들은 실행이된다. 
> runBlocking 안의 코루틴은 계속 실행 된다. 그 다음 단계는 메인 쓰레드의 다른 코드들이 실행이 되더라도 runBlocking 내의 진행중이던
> suspend 함수가 끝나면 다시 코루틴으로 진입해서 아까 멈춘 부분 아래부터 계속 실행이된다.  
> 
> 실행 결과
> 비동기로 처리하기에 순서의 차이는 있지만 전체적인 콘솔 로그는 다음과 같습니다.
> ```text
> Go to work
> Play music
> Listening
> Listening
> Start work
> Working
> Leave work
> Play music
> Listening
> Listening
> ```
> 이처럼 코루틴을 사용하면 비동기로 루틴을 실행하고 일반적인 서브 루틴과 다르게 실행 중간에 중단과 임의 시점에 재개가 가능하여 
> 루틴 간 협력을 통해 비선점적 멀티태스킹이 가능해집니다.

### 동시성 프로그래밍 지원
> 동시성 프로그래밍이란 2개 이상의 프로세스가 동시에 계산을 진행하는 상태를 말합니다.
> 두 개 이상의 실행 쓰레드가 필요하나 단일 코어에서는 병렬적으로 실행하는 것이 물리적으로 불가하기에 쓰레드 실행이 효율적이도록 교차 배치합니다.
> 동시성 프로그래밍을 쓰레드와 코루틴 간 비교해 보면 다음과 같습니다.
> 
> 쓰레드는 OS가 CPU 상태에 따라 쓰레드 작업을 스케줄링 하기에 쓰레드 간 교체 시 Context switching 비용이 발생합니다.
> 코루틴은 하나의 쓰레드 내 코루틴 간 관계 정의를 통해 중단 및 재개하기에 Context switching 비용으로 인한 오버헤드 없이 언어 레벨에서 스케줄링이 가능하게 합니다.
> 따라서 코루틴에서 메인 쓰레드를 차단하지 않으면서 현재 코루틴 실행을 일시 중지할 수 있는 기능을 제공하기에 경량 쓰레드로도 부릅니다.

### 쉽고 가독성 있게 작성할 수 있는 비동기 처리
> 비동기 코드 작성 시 정상적인 비동기 결과를 받아서 처리하는 코드 외에 비정상적으로 완료되지 못하는 케이스를 예외 처리할 수 있는 부수적인 액세서리 코드 작성이 필요합니다.
> 코루틴은 예측 가능한 프로그래밍을 할 수 있다는 점에서 동기 코드 진행 흐름과 동일하게 예외 처리가 가능하며 디버깅 측면에서도 큰 장점이 있습니다.
> 또한 멀티 쓰레드와 비교해서 서로에게 영향 주기 위해 쓰레드 간 통신과 콜백 구조로 코드가 흐르지 않기에 코드 흐름 파악이 쉽습니다.
> OS가 CPU 상태에 따라 쓰레드 작업을 스케줄링 하지 않고 개발자가 직접 작업을 스케줄링 하기에 비동기 코드 작성이 간단합니다.

### 참조사이트
> [코루틴(Coroutine)에 대하여](https://dev.gmarket.com/82)  
