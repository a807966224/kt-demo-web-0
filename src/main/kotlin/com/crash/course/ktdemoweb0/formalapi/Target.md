# Target

## 0. Kt Project Structure
    1. Gradle Plugins


## 1. Understanding Basic Kt Syntax
    1. Datetime

````
目前可以先使用Java的时间操作进行处理，因为Kt的这个Clock一直在测试中，目前不建议生产使用

@OptIn(ExperimentalTime::class)
fun Instant.format(
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): String {
    val t = toLocalDateTime(timeZone)
    return "%04d-%02d-%02d %02d:%02d:%02d %03d".format(
        t.year, t.month.number, t.day,
        t.hour, t.minute, t.second, t.nanosecond / 1_000_000
    )
}
````

    2. Collection
    3. Exception
    4. BigDecimal
    5. Class Or Obj
````
接口，抽象类，枚举，数据类，类

对象，伴生对象，数据对象

作用域函数，let（参数检查后执行），with，apply（初始化），also（额外操作后返回对象本身），run（执行）



高级知识
Nothing，所有类的子类，不会返回任何值

Java与Kt混合用的时候，Java返回的是T!，这个情况可能会存在NPE

协程是编译器的状态机，挂起函数会被编译为状态编号，Continuation参数，when(state)跳转

监管协程 & 协程

coroutineScope vs supervisorScope
维度	coroutineScope	supervisorScope
子协程异常	取消全部	只取消自己
使用场景	强一致性	容错并行

````

## 2. Kotlin & SpringBoot
    1. DB Storage
    2. Consul / Nacos已支持SpringBoot4
    3. Rpc


````
api -> suspend fun
application -> suspend fun
domain -> 禁止 suspend



com.example.order
├── OrderApplication.kt
│
├── order                // 订单领域（一个业务能力）
│   ├── api              // Controller / 对外接口
│   │   └── OrderController.kt
│   │
│   ├── application      // 应用层（用例 / 协调者）
│   │   └── OrderService.kt
│   │
│   ├── domain           // 领域模型（纯 Kotlin）
│   │   ├── Order.kt
│   │   ├── OrderItem.kt
│   │   └── OrderRepository.kt   // 领域接口
│   │
│   └── infrastructure   // 技术实现
│       ├── persistence
│       │   └── JpaOrderRepository.kt
│       └── messaging
│           └── OrderEventPublisher.kt
│
├── common                // 跨领域通用能力
│   ├── exception
│   ├── security
│   ├── web
│   └── util
│
└── config                // Spring 配置
    ├── WebConfig.kt
    ├── JacksonConfig.kt
    └── CoroutineConfig.kt


````