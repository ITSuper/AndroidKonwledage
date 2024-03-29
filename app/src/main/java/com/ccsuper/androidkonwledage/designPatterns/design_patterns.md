#### 一、状态模式
##1、定义：将对象的 状态转换逻辑 分布到状态对象中，来实现状态转换的设计模式。
--将对象的行为与状态分离，使得在修改对象状态时，不需要修改对象的行为方法。同时状态模式将状态转换逻辑包含在各个类中
--来简化代码，避免出现大量判断的出现，从而提高代码的可读性。
##2、核心角色：
--环境Context:它定义了客户端所感兴趣的接口，并维护了一个当前的状态。
--抽象状态State：定义一个接口，封装环境对象中对不同状态的行为。
--具体状态Concrete State:实现了抽象状态接口，封装不同状态下对环境对象的响应行为。
##3、优点
--结构清晰、封装性好
--扩展性好
--易于维护和调试
##4、缺点
--导致系统中类和对象的增加
--使用条件苛刻：适用于状态不多、状态切换少的情况
##5、适用场景
--行为随状态改变而改变：行为是由状态决定的，当一个对象状态发生改变时，对应的行为也发生改变
--条件、分支多的场景：