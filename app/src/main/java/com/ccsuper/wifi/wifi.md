# Android WIFI

**主要流程：启动服务》打开WIFI》扫描WIFI》获取扫描结果》连接WIFI》显示IP网关》设置更新**

### 1、WifiManager

- 暴露给应用层的一个管理类
  (1) 提供系统中已配置过的WIFI列表
  (2) 提供当前已连接的WIFI信息
  (3) 提供扫描功能
  (4) 定义了各种wifi连接、扫描、状态相关的常用广播

### 2、WifiService

- 继承自SystemService，方便SystemService统一启动其中的生命周期回调函数。
  (1) 实例化WifiServiceImpl类
  (2) 实例化WifiInjector类

### 3、WifiServiceImpl

- 继承至BaseWifiService(其继承至IWifiManager.Stub)
  (1) 实现了Android Framework中的wifi相关服务的核心函数，通过Binder跨进程通信由上层的WifiManager拿到Binder对象调用对应的API

### 4、WifiInjector

- 是一个wifi相关类的依赖注入器，通过它可以获得各种wifi类的实例并且能够模拟注入

### 5、ClientModeImpl

- 与之前android版本的wifiStateMachine类似。主要用来处理wifi连接和断开的状态。
  (1) 一些连接、断开状态由WifiServiceImpl发送消息至ClientModeImpl来实现
  (2)
  目前只包含了8个状态DefaultState、ConnectModeState、L2ConnectedState、DisconnectedState、DisconnectingState、RoamingState、ObtainingIpState、ConnectedState

### 6、WifiNative

- 与底层真正**操作wifi模块的守护进程**直接发送消息，请求wifi模块扫描、pno扫描、连接、断开以及这些操作之后的状态返回。通过回掉来通知上层底层是否成功或者其它结果。

### 7、ScanRequestProxy

- 此类管理外部应用程序的所有扫描请求，在WifiServiceImpl和WifiScanningServiceImpl之间作一个代理类，主要职责有：
  (1) 启动、禁用扫描
  (2) 作为一个扫描代理介于WifiServiceImpl和WifiScanningServiceImpl之间
  (3) 缓存扫描结果，可用**getScanResult()**获取结果
  (4) 当有了新的扫描结果时，发送SCAN_RESULT_AVAILABLE_ACTION广播
  (5) 限制非设置应用的扫描请求

### 8、WifiScanner

- 和WifiManager在同一个位置被初始化，暴露给上层应用的Wifi扫描管理类。通过异步通信实现扫描功能和结果的返回。

### 9、WifiScanningService

- 继承至SystemService，方便SystemService统一启动生命周期回调函数。实例化了WifiScanningServiceImpl。

### 10、WifiScanningServiceImpl

- 主要是对IWifiScanner的实现，体现了三种扫描方式：单次扫描、后台扫描、PNO扫描，并且三种扫描对应三个不同的状态机。
  (1) WifiSingleScanStateMachine
  (2) WifiBackgroundScanStateMachine
  (3) WifiPnoScanStateMachine

### 11、WifiFrameworkInitializer

- 用于注册wifi相关服务，WifiManager和WifiScanner都是在此被初实例化，并且获得相对应的远端服务的Binder对象。

### 12、WifiMonitor

- 用于监听wpa_suppliant和wificond上报上来的事件，并在framework中将这些事件广播出去。
  (1) wifi模块中的其它类如ClientModeImpl会将自己的Handler注册到WifiMonitor中
  (2) WifiMonitor在接收到上报上来的事件后给对应的handler发送消息，拥有handler的对象类根据实际业务作响应处理

### 13、WifiConnectivityManager

- 用于管理所有的wifi连接的扫描活动，当屏幕开启或关闭、wifi连接或断开、或者有扫描需求时，都会启动一次扫描，并将扫描结果传递给wifi框架选择并评分,
- 然后wifi框架选择出最优的热点最为推荐


