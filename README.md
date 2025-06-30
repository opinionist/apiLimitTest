api에 limit을 걸어서 서버에 부하가 덜 오게 test 하는 repository

하는 방법
스파이크 어레스트 VS 레이트 리미트
(순간 많은 트레픽 차단) (지속적인 트레픽 차단)

레이트 리미트
1. filter를 생성한다. (JWT같은 인증 한 이후에 넣는게 좋다. -> 토큰이 없으면 에러가 발생하기 때문)
2. Bucket을 생성한다. (레이트 리미트는 Bucket Token 알고리즘을 사용하기 때문)
- 2-1. BucketList도 생성한다. (유저별로 api TEST를 하기 위해)
3. Security를 활용해서 USER의 정보를 받는다. (만약 USER가 없으면 넘긴다.) -> 만약 USER가 없는데 같은 API를 자주 보내면?에 대한 생각을 할 필요 있다고 판단. IP를 받을 수 있지만... 같은 API를 동시 사용하는 사람들에게 피해를 줄 수 있다고 판단됨.
ex)-> 학교, PC방
- 3-1. User의 슈퍼키를 받아서 List에 넣는다.
4. 버킷의 토큰을 하나 사용한 이후 필터를 넘긴다.
5. 만약 토큰이 없으면 차단한다.
6. 만약 토큰이 있으면 API를 실행한다.
추가
-   
spring:
    cloud:
    gateway:
    server:
    webflux:
    routes:
    - id : my-api
    uri: http://localhost:8090
    filters:
    - name: RequestRateLimiter
    args:
    redis-rate-limiter.replenishRate: 10
    redis-rate-limiter.burstCapacity: 20

    data:
    redis:
    host: localhost
    port: 6379

이런식으로 해보려고 했으나 webService와 spring MVC, spring security와 webflux가 충돌이 나서
이 방법은 사용하지 않기로 했다. (나중에 다시 시도해볼 예정)

