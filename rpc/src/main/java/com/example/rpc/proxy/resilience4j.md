# 熔断测试
- 使用http://localhost:9090/testRestTemplateRpcRsi调用
- 先开启client客户端和server2 服务正常调用
- 再开启server1（接口实现方法中设置了延时5s，模拟超时）,服务部分发生请求超时异常，并捕获，同时返回兜底方案响应
- 当请求次数超过5次，失败阈值超过10%,客户端熔断器开启,进入熔断状态,请求不会请求到服务端，并返回客户端的兜底方案
- 下线 server1，当时间超过熔断器设置的打开到半开状态后，服务正常返回


# resilience4J配置信息
|配置属性|默认值|描述|
| :------ | ------ | ------ |
failureRateThreshold|50（%）|当故障率等于或大于阈值时，CircuitBreaker转换为打开状态并开始短路呼叫。
slowCallRateThreshold|100|配置百分比阈值。当通话时长大于10分钟时，CircuitBreaker会认为通话缓慢slowCallDurationThreshold当慢速呼叫的百分比等于或大于阈值时，CircuitBreaker转换为打开并开始短路呼叫。
slowCallDurationThreshold|60（s）|配置持续时间阈值，在该阈值以上，呼叫将被视为慢速并增加慢速呼叫的速率。
permittedNumberOfCallsInHalfOpenState|10|配置当CircuitBreaker半开时允许的呼叫数。
slideWindowType|COUNT_BASED|配置滑动窗口的类型，该窗口用于在CircuitBreaker关闭时记录呼叫结果。 滑动窗口可以基于计数或基于时间。如果滑动窗口为COUNT_BASED，slidingWindowSize则记录并汇总最近的调用。 如果滑动窗口是TIME_BASED，则slidingWindowSize记录并汇总最近几秒的调用。
slideWindowSize|100|配置滑动窗口的大小，该窗口用于记录CircuitBreaker关闭时的呼叫结果。
minimumNumberOfCalls|10|配置CircuitBreaker可以计算错误率之前所需的最小呼叫数（每个滑动窗口时段）。例如，如果minimumNumberOfCalls为10，则在计算失败率之前，必须至少记录10个呼叫。如果仅记录了9个呼叫，则即使所有9个呼叫均失败，CircuitBreaker也不会转换为打开。
waitDurationInOpenState|60（s）|从打开状态转为半开状态等待的时间
recordExceptions|empty|需要记录的异常列表
ignoreExceptions|empty|需要忽略的异常列表
recordException|throwable-> true默认情况下，所有异常都记录为失败。|用于评估是否应将异常记录为失败。如果异常应计为失败，则必须返回true。如果异常应被视为成功，则必须返回false，除非该异常被显式忽略ignoreExceptions。
ignoreException|throwable->false默认情况下，不会忽略任何异常。|用于评估是否应忽略异常，并且该异常既不算作失败也不算成功。如果应忽略异常，则必须返回true。否则必须返回false。
automaticTransitionFromOpenToHalfOpenEnabled|false|如果置为true，当等待时间结束会自动由打开变为半开，若置为false，则需要一个请求进入来触发熔断器状态转换
