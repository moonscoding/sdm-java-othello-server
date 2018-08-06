
<div class="pull-right">  업데이트 :: 2018.07.27 </div><br>

---

<!-- @import "[TOC]" {cmd="toc" depthFrom=1 depthTo=6 orderedList=false} -->
<!-- code_chunk_output -->

* [환경](#환경)
	* [기본환경](#기본환경)
	* [라이브러리](#라이브러리)
* [주요기능](#주요기능)
* [추가기능](#추가기능)
	* [자바](#자바)
	* [서버](#서버)

<!-- /code_chunk_output -->

### 환경

#### 기본환경

- Max OS X
- Java 1.8 기준
- IntelliJ

#### 라이브러리

- JsonParser (X) - 패킷최소화
- Junit

### 주요기능

#### 패턴

- NIO NonBlocking (Selector)
- 싱클톤 구조
- 멀티룸 구조
	- 클라이언트에게 방상태를 업데이트 해주는 방법
		- [방법1] 새로고침방법, 누르지 않고 이미 만료된 방에 접근시 오류 메세지를 전달
		- [방법2] 업데이트방법, 일정 시간을 정해두고 지속정으로 상태를 업데이트
		- 현재 프로젝트는 [방법2]로 구현

#### 서버 관련

- Queue를 이용한 데이터 전송 (Socket)
- 일정 시간동안 누적된 데이터 상태 전송 (서버캐싱 & 업데이트)
- 바이트 단위의 프로토콜정의

### 추가기능

#### 자바

- Exception
- Junit

#### 서버

- Logger API 개발
- 부하 테스트 ( 스트레스 테스트 )
- 실패패킷처리 ( 오류가 있거나 이미 큐가 가득 찼을때 )

---

**Created by SDM**

e-mail :: jm921106@gmail.com
