
# kakaopay 사전과제

> 카카오페이 뿌리기 기능 구현하기 


# 개발환경
-  JDK 1.8
- JAVA
- Spring-boot 2.3.1
- gradle
- JPA
- JUnit
- H2 Database

## 빌드 및 실행방법

```java
$ git clone https://github.com/yjs8237/kakaopay_work.git
$ cd {target_directory}
$ ./gradlew clean build
$ java -jar {jar명}.jar
```

## 기능 요구사항

### 1. 뿌리기 API
- 뿌릴 금액, 뿌릴 인원을 요청값으로 받아 token 을 발급하고 token 을 응답값으로 내려준다
- 뿌릴 금액을 인원수에 맞게 분배하여 저장
- token 은 3자리 문자열로 구성되며 예측이 불가능하도록 구현


### 2. 받기 API
- 뿌리기 시 발급된 token을 요청값으로 받아 token 에 해당하는 할당되지 않은 분배건을 할당 한다.
- 분배받은 금액을 응답값으로 내려준다
- 뿌리기당 한 사용자는 한번만 받을 수 있다.
- 자신이 뿌리기 한 건은 자신이 받을 수 없다.
- 뿌리기가 호출된 대화방과 동일한 대화방에 속한 사용자만이 받을 수 있다
- 뿌리기는 10분간만 유효하다.

### 3. 조회 API
- 뿌리기 시 발급된 token 을 요청값으로 받아 token 에 해당하는 뿌리기 건의 현재 상태를 응답 값으로 내려준다.
- 뿌린 시각, 뿌린 금액, 받기 완료된 금액, 받기 완료된 정보 ([받은 금액, 받은 사용자 아이디] 리스트) 
- 뿌린 사람 자신만 조회를 할 수 있습니다. 다른사람의 뿌리기건이나 유효하지 않은 token에 대해서는 조회 실패 응답이 내려가야 합니다. 
-  뿌린 건에 대한 조회는 7일 동안 할 수 있습니다. 


## ERD
![enter image description here](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https://k.kakaocdn.net/dn/bI12H4/btqFbClgSOE/TYtZzeIBFg4Vv4D9yod3B1/img.png)

|테이블명| 역할 |
|:--:|:--:|
|tb_memeber  |회원 테이블   |
|tb_chat_room|대화방 테이블   |
|tb_member_room_map|회원과 대화방 매핑 테이블   |
|tb_money|뿌리기 테이블|
|tb_money_result|뿌리기 상태 테이블|

---


## 사전 데이터 준비

API 테스트를 위하여 어플리케이션 로딩 시점에 기본 사전 데이터를 생성

- 사용자 사전 데이터 (10명) 와 대화방 사전데이터 (대화방 2개) 를 생성하고 
- 1번부터 5번까지의 ID를 가진 사용자는 대화방 1번에 참여 , 6번부터 10번까지의 ID를 가진 사용자는 대화방 2번에 참여된 상태의 데이터를 사전에 생성


> 아래 회원번호와 대화방번호가 API 호출 시 헤더 정보에 포함되게 된다.


|회원번호 (USER-ID) | 대화방번호 (ROOM-ID) | 대화방 생성 여부 | 
|:--:|:--:|:--:|
|1|1   | O |
|2|1   |  |
|3|1   | |
|4|1||
|5|1||
|6|2|O|
|7|2||
|8|2||
|9|2||
|10|2||


--- 

<br/>
<br/>


## Token 발행

> 뿌리기 등록이 완료되면 3자리의 문자열로 구성된 token 값을 발행하여야 한다. 

- JAVA UUID 사용 
- UUID 랜덤 데이터 기준으로 앞 3자리의 문자열을 token 값으로 사용

--- 

<br/>
<br/>

## API 테스트

> API 

|구분 | Method | URL | Request Body (PathVariable) |
|:--:|:--:|:--:| :--:|
|뿌리기 API|POST   | /api/v1.0/money | {"peopleCnt" : 인원수 , "money" : 뿌리기금액 } | 
|받기 API|POST   |/api/v1.0/money/recieve  | { "token" : "토큰" } | 
|조회 API|GET   |/api/v1.0/money/{token} | 토큰 |



<br/>
<br/>


### 뿌리기 API 테스트 

> 사전 생성 데이터 중 1번 ID 의 회원으로 1번 대화방의 뿌리기 데이터 생성 

- URL : {server-ip}:8080/api/v1.0/money
- Method : POST
- Header
	-  X-USER-ID  : 1
	- X-ROOM-ID : 1

- Request Body
```java
{
	"peopleCnt" : 10, //인원수 
	"money" : 1000000 // 뿌리기 금액
}
```
- Response Body 
```java
{
	"code": 0,
	"message": "완료되었습니다.",
	"data": {
		"token": "62f"
	}
}
```
<br/>


> Postman 사용 테스트 

- 헤더 (사용자 ID : 1 , 대화방 ID : 1)

![enter image description here](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https://k.kakaocdn.net/dn/moGBq/btqE9KZAnfK/KKV5A22lL4kGmXIWhkdTz1/img.png)

<br/>

> peopleCnt (요청 인원 수) : 10 명
> money (뿌리기금액) :  50,000 원  
> Response token 발급 


![enter image description here](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https://k.kakaocdn.net/dn/qo8WQ/btqFbU0k4FT/AxZAciWY8IVamSd8xPDWck/img.png)


<br/>
<br/>


### 받기 API 테스트 

> 사전 생성 데이터 중 3번 ID 의 회원으로 1번 대화방의 뿌리기 받기 시도 

- URL : {server-ip}:8080/api/v1.0/money/recieve
- Method : POST
- Header
	-  X-USER-ID  : 3
	- X-ROOM-ID : 1

- Request Body
```java
{
	"token" : "abc" // 뿌리기 API 를 통해 발급받은 토큰
}
```
- Response Body 
```java
{
	"code": 0,
	"message": "완료되었습니다.",
	"data": {
		"recvMoney": 4823 // 받기 금액
	}
}
```
<br/>


> Postman 사용 테스트 

- 헤더 (사용자 ID : 3 , 대화방 ID : 1)

![enter image description here](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https://k.kakaocdn.net/dn/brAZox/btqFaTuuihQ/fkPz3uDrrZChJOkRYh5izK/img.png)

> token : "6cb"
> Response 받기 금액 


![enter image description here](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https://k.kakaocdn.net/dn/otqLr/btqFaCGMA4V/KK11HuFFfKzS6oFWyi5o70/img.png)


<br/>
<br/>



### 조회 API 테스트 

> 사전 생성 데이터 중 1번 ID 의 회원으로 1번 대화방의 뿌리기 데이터 조회 시도 

- URL : {server-ip}:8080/api/v1.0/money/{token}
- Method : GET
- Header
	-  X-USER-ID  : 1
	- X-ROOM-ID : 1

- PathVariable
```java
	"token" : "abc" // 뿌리기 API 를 통해 발급받은 토큰
```
- Response Body 
```java
{
	"code": 0,
	"message": "완료되었습니다.",
	"data": {
		"regDate": "2020-06-27T10:51:57.926",	// 뿌리기 등록 일시
		"money": 50000, // 뿌리기 금액
		"totalRecvMoney": 4823,	// 해당 뿌리기 건의 총 받은 금액
		"recvList": [	// 받기 내역 리스트 
			{
				"recvMoney": 4823,	// 받은 금액
				"recvMemberId": 3	// 받은 사용자 ID
			}
		]
	}
}
```
<br/>



> Postman 사용 테스트 

- 헤더 (사용자 ID : 1 , 대화방 ID : 1)

![enter image description here](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https://k.kakaocdn.net/dn/beSIuL/btqFbVdR7IY/5AjPiP3Np9mFwLgDKkPXkK/img.png)

> token : "6cb"
> Response 조회 내용 

![enter image description here](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https://k.kakaocdn.net/dn/bLzU0i/btqFbjfd4ai/iH6vgjFYEyPvN29nlQjXnk/img.png)


<br/>
<br/>


## To-do 리스트

- Security 적용 (인증 인가)
- 뿌리기 등록시 발급받은 토큰을 해당 대화방 참여 사용자게에 Push 되도록 구현 필요 
- 대화방 생성자가 뿌리기 등록 시 등록 횟수 제한조건 필터링 기능 필요
