#스칼라 인수 인계 사항

## 라이브러리
1. 웹  
2. 비동기  
3. 코덱 라이브러리

## 자료구조
### 리스트
Nil: 빈 리스트  
Array: 뮤터블함, 빠름.  
```scala
1 :: 2 :: 3 :: 4 :: Nil // List[Int] = List(1, 2, 3, 4)

List(List(1, 2), List(3, 4)).flatten // List[Int] = List(1, 2, 3, 4)

// List[(Int, Int)] = List((1,5), (1,6), (2,5), (2,6), (3,5), (3,6))
for {
  x <- List(1, 2, 3, 4)
  y <- List(5, 6)
} yield(x, y)

List(1, 2, 3).flatMap{x =>
  List(5, 6).map{y =>
    (x, y)
  }
}

// List[(Int, Int)] = List((1,5), (1,6), (2,5), (2,6), (3,5), (3,6))
for {
  x <- List(1, 2, 3, 4) if x % 2 > 0
  y <- List(5, 6)
} yield(x, y)

List(1, 2, 3).filter(x => x % 2 > 0).flatMap{x =>
  List(5, 6).map{y =>
    (x, y)
  }
}
```

### 옵션
```scala
val xOp: Option[Int] = Some(1)
val yOp: Option[Int] = Some(2)

for {
  x <- xOp
  y <- yOp
} yield (x, y)
```

### 이더
어떤 익셉션이 떨어졌는지가 궁금하다.  

#### 퓨처  
비동기 처리할 때 씀.  
DB 처리할 때? 대부분 쓴다.  

#### 모나드  
컨텍스트가 있고, 그 컨텍스트 안에 있는 걸 조작하는 애들을 또감싸고 뭐시기 하는 그러한 패턴.  

익스큐선 컨텍스트 -> 어떤 스레드가 작업을 할 지... 위해 import  

### implicit  
프로토타입 같은 애인데 묵시적  
오류를 뱉지 않고 묵시적인 애 뒤져서 맞는 애 있으면 걔 실행. 

### 케이스 클래스
```scala
case class Foo(id: Int, name: String)
val foo = Foo(1, "foo")
val Foo(id, name) = Foo(1, "foo")
println(s"$id $name")
```

### Monix (비동기 관련, ReactiveX)
monix-eval: (evaluation) 이벨  
Cats, Scalaz 하스켈? 본격적인 함수형? 겁나 어렵다고 함.  
Incubator, full level? 프로젝트의 레벨, 승격이 돼야 정식으로 됨.  
아파치에도 이런 식으로 프로젝트의 등급으로 운영된다 함.  

**Unit** 자바의 void와 유사??  

ExecutionContext: 쓰레드 풀? 어느 쓰레드에게 할당할지?에 관한 것...?  

Monix의 Task는 선언 하면 실앵을 안 함.  
Future의 Future는 선언 하면 실행까지 하게 됨.  

Future는 무조건 새로운 스레드에다가 함.  
따라서 간단한 거는 같은 스레드에다가 하는 게 좋음.  
그러기 위해서는 Task를 써야함!  
중간에 캔슬을 해야하는 거라던가 기타 등등을 Task에서는 할 수 있음.  
Task가 쪼끔? 성능 측면에서 관리하기 용이해보임.  

Task.now는 바로 실행  
타입이 Task인데 어떤 애는 나중에 어떤 애는 바로 해줘야 해서  
Task로 감싸줘야 할 때

Task.defer 그 안에 있는 내용을 태스크로 감싸줌? 팩토리 패턴?  

Task.fork 무조건 다른 쓰레드에서 실행하게 끔

## 리커젼? 재귀함수?  
자바에서 재귀함수는 스택이 계속 쌓여서 오버플로우 발생!  
함수형 언어에서는 재귀함수를 컴팡일러가 반복문으로 바꿔줌!  
@tailrec를 안 써도 반복문으로 바꿔주는 데  
얘가 로직상으로 검사해줘서 반복문으로 바꿀 수 있는지 검사해줌!  

map을 쓰면 map 안에 map 안에 해서 오바  
flatMap을 써서 flatten하게 만들어줘서 하나의 맵으로, 스택 세이프하게!  

10초 동안 최대 기다림, 그 이후에는 타입 익셉션  
하지만 스레드를 블락 시켜서 프로덕션에서는 비추!  
사실 테스트에서도 잘 쓰지는 않음!  
Await.result(future, 10.seconds)  

Task.chooseFirstOf(source, fallback) 둘 중 하나! 실행완료되면 하나 종료.  


Future.sequence는 리스트 별로 퓨처가 있을 때 각각 콜백을 달 수 없으니  
그걸 퓨처 안의 리스트로 바꿔서 콜백을 하나만 달 수 있게끔?  
여러 퓨쳐를 하나의 퓨쳐로...  
하지만 캔슬이 없으니까 하나가 실패해도 전체로보면 실패인데 일단 나머지 실행들은 함.  

Task.gzip 머시기 그거는 하나가 실패ㅏ면 다 캔슬  
쓸데없는 실행을 안 함 굳.  

## COEVAL
태스크랑 비슷한데 sync  
Function0 인풋이 없는 파라미터  
스택 세이프!!  
힙에다 쌓아서 스택 오버플로우 니은니응  

## Stream(자료구조)  
Egar evaluation? 선언이 되는 순간 실행  
Stream: lazy, 일종의 시퀀스  
헤드에 대한 값을 가지고 있고, 두번 째 요청에 대한 계산 방법  
짝수의 스트림? 필요한만큼만 땡겨쓰는 무한한 자료구조 ! 가 가능해짐  

sealed? 몇 가지 중에 하나! 두 가지면 안 됨!  
정수 sealed? 면 양의 정수 0 음의 정수 중 하나여야만 함!  

우리 쪽에서는...??  

## NIO(Non Blocking I/O)  
블락킹일 때는 연결 할 때마다 스레드를 만들음.  
대부분 가만히 지켜보고 있는 애들한테 스레드를 모두 할당해주기는 아까움.  
따라서 NIO는 일 할 때만 스레드를 돌리면 된다고 보면 됨.  

Promise, 생산자와 소비자 관점에서 봐야함.  

남은 거 Fintch?(rest api, http), s-codec


## Finch  
HTTP 서버 만들 때 쓰는 라이브러리

### case class
자료 레코드??  
원시값처럼 값이 똑같으면 똑같은 애로 취급.  

### Shapeless
자스의 어레이처럼 리스트 안에 타입들이 다르게 해주는 라이브러리?  
HNil이 HList 혼성 리스트 라고 보면 됨.  

## 파라미터
Int :: String 둘 다 있어야 함.
Int :+: String 하나만 있으면 됨.  

## toServiceAs  
엔드포인트를 서비스로 바꿔줘야함.  
그래야 그 서비스를 핀치에게 물릴 수 있다??  

# scodec (메인, 핵심적으로 봐야함!)  
![가이드](http://scodec.org/guide/Core+Algebra.html)  
bits, core

bitsVector, byteVector  
Vector[Byte] 와 같은 콜렉션 구조인데 내부적으로 더 빠르게 하고자 뭐시기를 했다고 하고,  
추가적인 메소드들도 구현돼있음.  

bitsVector는 인덱싱이 바이트 대신 비트!  

json을 받아서 case class로 바꿔야 스칼라에서 쓰기 편함.  
전문 통신에서는 대부분 바이트로 통신을 함.(롯데가 그렇게 정했다고 함.)  
그래서 그 규격에 맞게 case class를 바이트로 바꾸고,  
롯데에서 보낸 바이트 데이터를 case class로 바꾸기 위해 코덱을 씀!  

Attempt? 바꿀 수도 있고, 못 바꿀 수도 있고...

## 디코더
첫 번째 비트가 0이면(int) 디코더A  
1이면(string) 디코더B  
... 일 때 flatMap을 쓴다고 함.  

## 인코더
sizeBound: 바이트, 비트의 리밋?을 정해준다함!  
디코더와는 반대임.  
데이터를 비트 벡터로 바꿔주는 거임.  
인코더 A에서 비트 벡터로 제공해줌.  
인코더 B에서 A로 가야 비트벡터로 가는 인코더A를 인코더 B로 바꿀 수 있음.  

Econtramap, A를 B로 못 바꿀 수도 있을 때 스는 아이.  

플랫맵은 인코더에서 의미가 없다고 함. 논리적으로...

## 코덱  
인코더이면서 디코더이면 코덱,  
case class에서 apply는 인자를 받아서 클래스를 반환  

인코더 A이면서 디코더 B이면 GenCodec  
원래 둘은 동기화? 시킨다고 보면 됨.  
fuse를 썼을 때 타입이 같다면 젠코덱을 코덱으로 바꿔줌.  
=:= 는 두 개가 똑같다는 연산자...??

## 비트벡터 바이트벡터  
total: 에러가 나도 오류가 안남.  
greedy: 남는 비트 없이 다 씀.  

# VPN 설정  
nmtui: 네트워크 매니저 터미널 UI

# 공유기 설정
* 1888: 서버 ssh 터널링? 포트  
* 8081: 프록시 서버 포트(우리꺼)  
* 1194: 롯데 쪽 vpn 장비?  
* 9990: 스칼라 라이브러리 관리자 페이지  


