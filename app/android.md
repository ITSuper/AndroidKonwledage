[Android必看面试题目](http://www.hunt007.com/wiki/36149.html)

# 一、Activity相关
 
## 1、生命周期流转

四个状态：running paused stopped killed running:当前显示在屏幕上的Activity，用户可见，可交互 paused:依旧用户可见，但是已经失去焦点，不可交互
stopped:用户不可见，也不可交互 killed:等待被系统回收

onCreate() >>onStart()>>onResume()>>onRestart()>>onPause()>>onStop()>>onDestory()

Activity A切换到B执行方法： A:onCreate()>>onStart()>>onResume()>>onPause()
B:onCreate()>>onStart()>>onResume()
A:onStop()>>onDestory()

A切换到B(B是以DialogActivity形式存在的)执行方法： A:onCreate()>>onStart()>>onResume()>>onPause()
B:onCreate()>>onStart()>>onResume()

当用户按下home后，再返回，执行的方法： onPause()>>onStop()>>home>>onRestart()>>onResume()

Activity中的**onSaveInstanceState和onRestoreInstanceState()**
onSaveInstanceState(Bundle outState):
保存有ID组件的状态，在系统销毁Activity时。调用时机是在onStop之前，但是不确定在onPause之前还是之后。用户主动销毁Activity不会调用。
onRestoreInstanceState(Bundle outState):
在系统销毁Activity后，重新创建时可以调用。该方法保存的 Bundle 对象在 Activity 恢复的时候也会通过参数传递到 onCreate() 方法中;

## 2、有关横竖屏切换生命周期的变化：                             

横竖屏切换会根据**configChanges**配置来决定是否销毁Activity.

不配置configChanges属性 设置android:configChanges="orientation"设置android:configChanges="orientation|keyboardHidden"(3.2系统之前的系统不会执行生命周期方法了)
以上三种配置，横竖屏切换时Activity均会销毁重建，Activity的生命周期都会重新执行一次

配置 android:configChanges="orientation|keyboardHidden|screenSize"不会销毁重建
   
## 3、setContentView方法

将Activity对应的xml布局添加到根布局中。调用mWindow.setContentView，添加到decorView中。

## 4、ViewRootImpl\WindowManagerService


相关资料文章：
[Activity的生命周期](https://blog.csdn.net/xiajun2356033/article/details/78741121)
[Android 横竖屏切换总结](https://www.jianshu.com/p/52aa3a2c0417)


# 二、Fragment相关 

## 1、对应生命周期

activity:  ----------------onCreate------------------------ | onStart | onResume onPause onStop | onDestroy ---------------------- 

fragment:  onAttach onCreate onCreateView onActivityCreated | onStart | onResume onPause onStop | onDestroy onDestroyView onDetach

## 2、fragment如何懒加载

(1)Support时代：在setUserVisibleHint()方法中判断是否对用户可见。
(2)ViewPager
(2)ViewPager2

## 3、fragment的add、show、hide、replace

add:添加，一般配合hide使用。不会清空容器(FragmentTransaction)里面的内容
replace:替换。会清空容器里所有内容，只保留一个fragment显示。
hide:隐藏
show:显示

相关资料文章：
[Fragment生命周期](https://juejin.cn/post/6844903752114126855)
[Fragment全解析系列（一）：那些年踩过的坑](https://www.jianshu.com/p/d9143a92ad94)

# 三、启动模式（以及使用场景）

standard:   标准模式 不管有没有实例都会重新创建一个实例 场景：多数普通的Activity界面

singleTop:  栈顶模式 如果实例位于任务栈顶，则不重新创建。若不位于栈顶则重新创建实例 场景：消息接收界面例如QQ弹出的消息界面

singleTask:  栈内唯一模式 若当前任务栈中存在实例，那么将此实例之上的其它实例全部退出，使得此实例位于栈顶 场景：首页主界面

singleInstance:  新栈唯一模式 每次都会新建一个任务栈，并且在这个栈中只存在一个实例 场景：与主界面分离的页面如闹钟提醒

**注意**:
以上所谓的启动模式是不包含设置了 intent flag的情况下。在设置了intent flag时，启动的方式和结果可能和使用的启动模式不一致。
activity的启动和启动模式、intent flag、taskAffinity、以及是否是Activity直接启动另一个Activity相关，并不是单独由启动模式决定的。

相关资料文章：
[Android面试官装逼失败之：Activity的启动模式](https://juejin.cn/post/6844903494470598669)

Flag :
intent.flag_activity_new_task\flag_activity_clear_task\flag_activity_clear_top\flag_activity_single_top

# 四、APP启动过程

## 1、系统相关成员：

init进程： Android系统启动后首先唤醒的进程
Zygote进程：所有Android进程的父进程。 
SystemServer进程：系统服务进程，负责系统中大大小小的各种事务。唤起 ActivityManagerService、PackageManagerService、WindowManagerService 
ActivityManagerService:负责四大组件的启动、切换、调度以及应用进程的管理和调度等。对于一些进程的启动，会通过Binder通信机制传递给AMS，再处理给Zygote进程。 
PackageManagerService :负责应用包的一些操作，如安装、卸载、解析AndroidManifest.xml、扫描文件等。 
WindowManagerService  :   负责窗口相关的操作，如窗口启动、添加、移除等。
Launcher              :   桌面应用

## 2、点击桌面图标启动流程：

(1)、Launcher接收点击事件，调用StartActivity准备启动目标Activity，同事调用checkStartActivityResult检查目标Activity。
内部核心逻辑通过AIDL Binder通信通知ATMS(ActivityTaskManagerService)处理打开对应APP. 【Launcher StartActivity】
(2)、ATMS接收到消息后，会使Launcher进入Paused状态。 【ATMS StartActivity】
(3)、ATMS判断目标APP对应的进程是否已经启动。如果已经启动则直接打开对应Activity，
如果没有启动则需创建对应进程。
如何判断对应进程是否启动？
答：ATMS内部维护已经启动的相关进程，通过processName(一般为包名)和UUID去查找是否已经存在。 如何创建新的进程？
答：通过Zygote进程，调用fork方法新建对应进程，并返回新进程的pid。 【Zygote fork进程】
(4)、创建进程时，会通过反射调用ActivityThread的main方法，创建新的ActivityThread。在main方法中创建了主线程的Looper对象，并开始loop循环。
【ActivityThread main:1、attach;2、handleBindApplication;3、attachBaseContext;4、installContentProviders;5、Application
onCreate】
(5)、在ActivityThread的main方法中通过bindApplication启动Application 【ActivityThread loop】
(6)、启动Application后会创建上下文context，并启动Activity。 【Activity 进入生命周期】

## 3、启动优化：

(1)、闪屏页优化
(2)、MultiDex优化    
(3)、第三方库懒加载
(4)、WebView优化
(5)、线程优化
(6)、系统调用优化

**思考:MultiDex.install的原理？**
答：MUltiDex.install 调用了doInstallation()方法。 主要步骤有： 1、获取加载dex的classloader; 2、清除旧dex文件目录； 3、创建新的dex文件目录；
4、获取所有dex文件； 5、通过反射将所有dex文件写入dexElements中 6、通过classloader加载dexElements中所有的dex文件

相关资料文章:
[女儿拿着小天才电话手表问我App启动流程](https://juejin.cn/post/6867744083809419277)
[今日头条启动优化](https://juejin.cn/post/6844903958113157128)

# 五.Service相关

-基础定义：Service是一个在后台执行长时间运行操作而不用提供用户界面的应用组件，可由其他组件启动，即使用户切换到其他应用程序，Service 仍然在后台继续运行。

## 1、Service两种启动方式

startService:  onCreate()>>onStartCommand()>>onDestroy()
调用stopService(intent)停止service。一旦服务开启，其生命周期与调用者无关，调用者不能调用service中的方法。

bindService:   onCreate()>>onBind()>>onUnbind()>>onDestroy()
调用unbindService(serviceConnection)停止service。生命周期与调用者绑定，多个组件和Service绑定时，当所有组件销毁时Service才会停止。 

## 2、Service注册

在AndroidManifest.xml文件中使用<Service>节点进行注册。使用android:exported属性设置 是否运行外部应用获取 。

## 3、如何调用服务里面的方法

bind方式启动时：
    在服务类内部创建一个内部类，可以间接调用服务中的方法 实现onbind方法，返回的就是这个内部类 在activity中绑定service bindService(intent,serviceConnection,_)
在绑定成功的回调onServiceConnection中会传递一个IBinder对象 强制类型转换为自定义的接口类型，调用接口里面的方法\
start方式启动时：
    通过Bundle携带参数。在service类中的onStartCommand方法中接收，通过接收bundle携带的参数判断调用方法。

## 4、intentService  (Android 8.0 推荐使用JobIntentService)

intentService可以看作是service和handlerThread的结合，在完成任务后会自动停止，intentService是继承自service处理异步请求的一个类，在内部有一个工作线程来处理耗时操作。 完成任务后会自动停止不需要手动停止。
如果启动多次，每一个耗时操作会以工作队列的方式在intentService的onHandlerIntent回调中执行。
 
## 5、前台服务    

- 定义：前台服务是用户认可的且在系统内存不足时不允许系统销毁的服务，运行的优先级比普通后台服务高。

## 6、保证Service不被后台销毁

(1)在onStartCommand方法中返回 START_STICKY
```
    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        flags =  START_STICKY;
        retrun super.onStartCommand(intent,flags,startId);
    }
```
(2)通过前台服务来提升Service的优先级
```
 @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext());
        Intent intent1 = new Intent(this, MainActivity.class);
        builder.setContentIntent(PendingIntent.getActivity(this, 0, intent1, 0))
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                .setContentTitle("测试ContentTitle")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("测试ContentText")
                .setWhen(System.currentTimeMillis());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("CHANNEL_ONE_ID", "CHANNEL_ONE_NAME", NotificationManager.IMPORTANCE_MIN);
            channel.enableLights(false);
            channel.setShowBadge(false);
            channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
            builder.setChannelId("CHANNEL_ONE_ID");
        }
        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_SOUND;

        startForeground(100, notification);
        return super.onStartCommand(intent, flags, startId);
    }

```
(3)在onDestroy中发送广播重新启动service      

  


相关资料文章：
[Android Service两种启动方式的区别](https://www.jianshu.com/p/2fb6eb14fdec)
[Android Service和IntentService知识点详细总结](https://juejin.cn/post/6844903477777285134#comment)
![Android Service体系图](https://upload-images.jianshu.io/upload_images/5377834-d9e4aa24cca57df0.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

# 六.Broadcast Receiver

BroadCastReceiver是对发送出来的broadcast进行过滤、接收和响应的组件。在执行完onReceiver()方法后，生命周期结束。

## 1、类型

标准广播：sendBroadcast()发送，完全异步执行，所有广播几乎在同一时间接收到，没有先后顺序，效率高，无法被截断 

有序广播：sendOrderedBroadcast()发送，同步执行，同一时间只有一个广播接收器接收到这个消息，有先后顺序，如果消息被截断，那么后面的广播接收器就无法接收到这条消息

## 2、注册

静态注册： 在manifest.xml文件中进行注册，使用<<receiver>>声明，并在标签内用<<intent-filter>>配置过滤器。 这种形式的广播接收器生命周期伴随着整个应用。
    优先级由<<intent-filter>>中priority决定，数值越到优先级越高。 

```html

   <receiver android:name=".MyBroadcastReceiver">
       <intent-filter android:proiorty="100">
           <action android:name=""/>
       </intent-filter>
   </receiver>

```    

动态注册： 在代码中定义并设置好一个IntentFilter对象。调用registerReceiver()进行注册。
    
-区别：
1、动态广播的系统优先级比静态广播高；
2、静态广播的生存周期比动态广播长。可用于监听手机电量低、手机开机等。

## 3、LocalBroadcastReceiver\LocalBroadcastManager
                                                 
系统广播：可用于应用间、应用与系统间、应用内部的广播接收。
本地广播：只有应用内部的广播才能接收到。LocalBroadcastManager对本地广播进行注册和发送。

localBroadManager.sendBroadcast();
localBroadManager.register()

相关资料文章：
[Android之BroadcastReceiver总结](https://juejin.cn/post/6844903518701092878)


# 七.Content Provider

## 1、定义&作用

内容提供器是用于不同程序之间共享数据的功能，它提供了一套完整的机制。允许一个程序访问另一个程序的数据，同时保证被访问数据安全性。
其为数据存储和读取提供了统一的接口，使用表的形式对数据进行封装。

## 2、种类

## 3、使用

通过URI(uniform resource identifier 统一资源标识符)访问对应ContentProvider。

外部进程通过ContentResolver访问对应ContentProvider。
 
```

    ContentResolver resolver  = getContentResolver();
    
    Uri uri = Uri.parse("content://com.demo.myprovider/user");
    
    Cursor cursor = resolver.query(uri,null,null,null,"userid desc");

```
## 4、原理
 
android中binder原理


相关资料文章：
[关于ContentProvider的知识都在这里了！](https://www.jianshu.com/p/ea8bc4aaf057)
[四大组件-Content Provider详解](https://blog.csdn.net/qq_45515432/article/details/119715680)

# 八.Intent相关

## 1、定义
    
Intent意图：是一个消息传递对象，可以通过它携带数据，来向其它组件发起请求操作。例如：启动Activity、启动或者绑定Service、发送广播等。

## 2、分类

显示意图：直接指定调用组件的名称，一般用于应用内部。
    打开内部activity、启动or绑定Service、发送广播
隐示意图：没有指定组件的名称，而是声明action行为，从而允许其它应用的组件来处理。
    打开拨号界面\打开指定网页
```
    Intent intent = new Intent();
    intent.setAction(Intent.ACTION_VIEW);
    intent.setData(Uri.parse("tel:123456789"));  // intent.setData(Uri.parse("www.baidu.com"));
    startActivity(intent);
```

## 3、intent-filter

(1)、<action>:指定组件要完成的操作，系统内置：MAIN、VIEW、SEND等等。
(2)、<category>:为action增加额外的附加类别信息，常见有：DEFAULT、LAUNCHER等。
(3)、<data>&<type>:为action提供要操作的数据。
    
```html
    
    <activity android:name = ".view.ShareActivity">
        <intent-filter>
            <action android:name = "android.intent.action.SEND"/>
            <category android:name = "android.intent.category.DEFAULT"/>
            <data android:mineType = "text/plain"/>
        </intent-filter>
    </activity>

```
  
相关资料文章：
[Android 基础知识5：Intent 和 Intent 过滤器](https://juejin.cn/post/6844903977931243533#heading-5)

# 九.网络编程相关

## 1、网络基础
    
网络体系结构分层 TCP/IP模型 ：(自上往下)
    应用层（HTTP、FTP、DNS、SMTP等等）
    运输层（TCP、UDP）
    网络层（IP）
    数据链路层（ARP）
    物理层

(1)、应用层：如Http协议，它实际定义了如何包装和解析数据，应用层采用HTTP协议后，会按照协议包装数据，如按照请求行、请求头、请求体包装。
包装好后将数据传输至运输层。
(2)、运输层：这一层指定了将数据以何种方式传送到端口号。涉及到如何建立连接？如何保证数据不丢失？如何调节流量控制和拥塞控制？等。
(3)、网络层：这一层指定了将数据传送到那个IP地址。涉及到最优路线，路由选择算法等。
(4)、数据链路层：如ARP协议，负责把IP地址解析成对应的MAC地址，即硬件地址，这样就能找到对应的唯一机器。
(5)、物理层：提供二进制传输服务，也就是真正开始通过传输介质（有线、无线）开始进行数据的传输了。

## 2、Http & Https

(1)、无连接：Http约定了每次连接只处理一个请求，一次请求完成后就断开连接。主要是为了缓解服务器的压力，减少资源占用。
(2)、无状态：每个请求之间都是独立的，对于之前的请求事务没有记忆的能力。
(3)、HTTP缓存：主要通过Header中的Cache-Control和ETag来实现。
        Cache-Control: private\public\max-age\no-cache\no-store
        ETag:即用来进行对比缓存，ETag是服务端资源的一个标识码
(4)、对称加密：客户端和服务端双方统一采用一个密钥进行加密解密。
(5)、非对称加密：公钥加密的信息只能用私钥解开，私钥加密的信息只能被公钥解开。只有服务端存在私钥，公钥传递给客户端。
(6)、HTTPS:即http + ssl ，采用了非对称加密的方式包装数据。
(7)、Keep-Alive模式：又称为持久连接、连接重用。使客户端到服务端的连接持续有效，当出现后续请求后不用再次建立连接。

**客户端与服务端通信流程**：
（1）客户端发送https请求
（2）服务端配置一套证书（相当于公钥和私钥）。收到请求后向客户端发送证书（公钥）。
（3）客户端收到证书后验证是否有效.验证完毕后，产生一个随机值，利用证书对随机值加密后向服务端发送
（4）服务端收到加密数据后利用证书（私钥）解密（这里非对称加密过程完成）。拿到随机数后作为密钥，通过密钥加密数据后向客户端发送。
（5）客户端收到数据后也用随机值作为密钥进行解密数据（这里是对称加密）

## 3、Socket

- Socket简单介绍
    * Socket就是为网络服务提供的一种机制
    * 通信的两端都有Socket
    * 网络通信其实就是Socket间的通信
    * 数据在两个Socket间通过IO传输
    * 玩Socket主要就是记住流程，代码查文档就行
    * Socket的简单使用的话应该都会，两个端各建立一个Socket，服务端的叫ServerSocket，然后建立连接即可。

## 4、TCP & UDP

TCP:1、必须建立连接，形成传输数据的通道。
    2、在连接中可进行大量数据的传输。
    3、通过三次握手完成连接，是可靠协议。
    4、必须建立连接，效率低。

UDP:1、面向无连接
    2、每个数据包大小在64k以内
    3、无需建立连接，是不可靠协议
    4、效率高

## 5、三次握手、四次挥手
   
三次握手流程：
（1）、客户端 发送SYN = 1 Seq = X 表示请求建立连接，Seq = X 是客户端生成的随机数;
（2）、服务端 收到请求后，发送 SYN = 1 ACK = X+1  Seq = Y 表示回复客户端建立连接的请求，Seq = Y 是服务端生成的随机数；
（3）、客户端 收到回复后，再次发送 ACK = Y + 1 Seq = Z
![三次握手流程](https://upload-images.jianshu.io/upload_images/4432347-dcf7d168c55a1ee4.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
  

思考：为什么需要三次握手？
    答：前两次握手是建立一个连接必须的。**第三次握手是为了防止已经失效的连接请求报文段突然又传到服务端，因而产生错误**
     
四次挥手流程：
（1）、客户端 发送FIN = 1 ACK = Z Seq = X
（2）、服务端 收到后回复 ACK = X + 1 Seq = Z
（3）、服务端 发送FIN = 1 ACK = X Seq = Y 
（4）、客户端 收到后回复 ACK = Y Seq = X
![四次挥手流程](https://upload-images.jianshu.io/upload_images/4432347-2e0f20b0eb55a04a.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

思考：为什么需要四次挥手？
    答：第2 ACK回复消息 和 第3 FIN终止消息并不是同时发出，先发送回复ACK再发送FIN终止消息。这也很好理解，当客户端要求断开连接时，此时服务端可能还有未发送完的数据，
    所以先ACK，然后等数据发送完再FIN。这样就变成了四次握手了。

相关资料文章：
[HTTPS和HTTP的区别](https://www.cnblogs.com/ganchuanpu/p/9361424.html)
[Android技能树 — 网络小结(3)之HTTP/HTTPS](https://www.jianshu.com/p/4142299b8477)


# 十.事件分发机制
 
思考：当发生手指触摸、点击、移动等事件时，这些事件是如何被转换成手机系统可识别的代码事件（event）? 这个过程中有那些系统组件参与？
    答：(1)-首先，在Android系统中将所有的输入事件定义为InputEvent，根据输入类型又进一步分为KeyEvent(键盘输入事件) 和 MotionEvent(屏幕触摸事件)
           -硬件接收到事件后，通过InputManager进行识别，并通过ViewRootImpl将事件分发给当前激活的窗口。
           -在应用窗口中有对应的PhoneWindow\DecorView实例，它们通过与InputManagerService通信来接收分发的事件。
        (2)参与的系统组件有：SystemServer、WindowManager、InputManager等。

-InputEvent:输入事件。根据类型又可划分为KeyEvent和MotionEvent
-SystemServer:系统服务。由Zygote进程对齐进行初始化。SystemServer启动后对AMS、WMS、PMS等关键服务进行初始化。
-WindowManagerService:窗口管理服务。管理窗口添加、移除等操作。对InputManagerService初始化。
-InputManagerService:输入管理服务。启动了InputManager输入管理器。
-InputManager:输入管理器。与硬件交互识别转换输入事件。
-ViewRootImpl:作为整个控件树的根部，它是View树正常运作的动力所在，控件的测量、布局、绘制以及输入事件的分发都由ViewRootImpl控制。
                ViewRootImpl作为链接WindowManager和DecorView的纽带，同时实现了ViewParent接口。

思考：代码事件是如何被分发？被处理？被拦截等？
    答：Android系统的事件分发使用了递归传递的思想，通过责任链的方式将事件自顶向下传递，找到事件的消费者后，再自低向上返回结果。

**由外到内传递，由内到外处理**
事件传递:Activity>>ViewGroup>>View

三个重要的方法：
**dispatchTouchEvent :分发事件**
**onInterceptTouchEvent:拦截事件**
**onTouchEvent:处理事件**
1.当onInterceptTouchEvent返回ture时，若onTouchEvent返回true，后续事件将不再经过该ViewGroup的onInterceptTouchEvent方法，
直接交由该ViewGroup的onTouchEvent方法处理；若onTouchEvent方法返回false，后续事件都将交由父ViewGroup处理，
不再经过该ViewGroup的onInterceptTouchEvent方法和onTouchEvent方法。
2.当onInterceptTouchEvent返回false时，事件继续向子View分发；
3.对于子View，当onTouchEvent返回true，父ViewGroup派发过来的touch事件已被该View消费，后续事件不会再向上传递给父ViewGroup，
后续的touch事件都将继续传递给子View。
4.对于子View，onTouchEvent返回false，表明该View并不消费父ViewGroup传递来的down事件，而是向上传递给父ViewGroup来处理；
后续的move、up等事件将不再传递给该View，直接由父ViewGroup处理掉。
5.onTouch先于onTouchEvent调用，onClick事件是在onTouchEvent中ACTION_UP中触发的。

事件分发序列：
    针对MotionEvent添加了一个Action以描述该事件的行为。
    ·ACTION_DOWN:手指触摸到屏幕
    ·ACTION_MOVE:手指在屏幕上移动
    ·ACTION_UP:手指离开屏幕
    ·其它ACTION_CANCEL...

当用户发生一次触摸屏幕的事件时，必然会产生一个事件序列，例如：ACTION_DOWN、ACTION_MOVE....ACTION_MOVE、ACTION_UP。其中ACTION_MOVE发生的次数不确定为0到n，
但是ACTION_DOWN和ACTION_UP发生次数则为1。当ACTION_DOWN事件从ViewGroup中分发到消费事件的子View中时，ViewGroup会保存该消费View，后续发生的ACTION_MOVE、ACTION_UP会直接跳过递归
将后续事件直接传递给保存的子View。
   
相关资料文章：
[反思|Android 事件分发机制的设计与实现](https://juejin.cn/post/6844903926446161927)
[关于反思系列(Thinking in Android)](https://github.com/qingmei2/blogs/blob/master/src/%E5%8F%8D%E6%80%9D%E7%B3%BB%E5%88%97/%E5%8F%8D%E6%80%9D%7C%E7%B3%BB%E5%88%97%E7%9B%AE%E5%BD%95.md)
[浅谈Android事件分发机制](https://blog.csdn.net/salmon_zhang/article/details/76746159)
[Android事件分发机制完全解析](https://blog.csdn.net/guolin_blog/article/details/9097463)

# 十一.Handler机制

### 1、定义&作用

定义：Android中线程之间消息传递、异步通信的机制。
作用：将工作线程的消息传递到UI主线程中，从而是实现工作线程对主线程的更新，避免线程操作的不安全。

### 2、原理
  
相关组件：
· Handler:消息处理者，添加消息Message到MessageQueue中，再通过Looper循环取出消息。
    主要方法：handler.post()、handler.sendMessage()、handler.dispatchMessage()、handler.handleMessage()
· Message:存储需要操作的数据
· MessageQueue：存放消息的数据结构
    主要方法:queue.enqueueMessage()
· Looper：消息循环
    主要方法：looper.prepare()、looper.loop()


在主线程中创建Looper和MessageQueue，通过handler.sendMessage或者handler.post发送消息进入MessageQueue。
Looper不断的循环从消息队列中取出消息，发送给消息创建者handler。handler接收消息，在handleMessage方法中处理。
![27C8A243-B380-4249-A873-FBEAC76DCA09.png](https://upload-images.jianshu.io/upload_images/5377834-252afbd33f14ea9d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

 
相关资料文章：
[Android异步通信：手把手带你深入分析 Handler机制源码](https://www.jianshu.com/p/b4d745c7ff7a)
[Android异步通信：这是一份Handler消息传递机制的使用教程](https://www.jianshu.com/p/e172a2d58905)
[Android 多线程：你的 Handler 内存泄露 了吗？](https://juejin.cn/post/6844903555367698446)



# 十二.自定义view

- 如图所示
    - ![img](http://upload-images.jianshu.io/upload_images/3985563-5f3c64af676d9aee.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
- View的绘制是从上往下一层层迭代下来的。DecorView-->ViewGroup（--->ViewGroup）-->View ，按照这个流程从上往下，依次measure(测量),layout(布局),draw(绘制)。
    - ![img](http://upload-images.jianshu.io/upload_images/3985563-a7ace6f9221c9d79.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

- View分类:(1)单一视图 (2)视图组ViewGroup 
  无论是measure过程、layout过程、draw过程都是从树的根节点开始(即树形结构的顶端)，view的位置是相对于父view而言的。
 

### 1、onMeasure 测量
作用：决定view的大小

- onMeasure 过程 
  顶层ViewGroup->measure->onMeasure->measureChildren->子View->measure->onMeasure->测量完毕 
  ViewGroup的测量过程需要重写onMeasure方法，根据布局的特性重写。
    
- measureSpec测量规格 
  measureSpec(测量规格 32位的int值) = mode(测量模式 高2位 31.32位) + size(具体大小 低30位)
  MeasureSpec.getMode()获取mode 
  MeasureSpec.getSize()获取size 
  MeasureSpec.makeMeasureSpec(size,mode)根据传入的size和mode返回对应的measureSpec

- mode测量模式分为三类：
  (1)UNSPECIFIED：未指明大小，父视图不约束子视图
  (2)EXACTLY：明确大小，父视图为子视图明确指定一个确切的尺寸 使用match_parent或具体数值
  (3)AT_MOST：最大尺寸，父视图为子视图指定一个最大尺寸 wrap_content

### 2、onLayout 布局
作用：获取四个顶点，决定View的位置。

- 布局过程 
  布局过程也是自上而下，不同的是ViewGroup先调用onLayout让自己布局，然后再让子View布局，而onMeasure是先测量子View的大小再确定自身大小。
  (1).单视图不需要实现该方法，视图组需要实现onLayout()来对子view进行布局
  (2).对于单视图确定位置是在基类layout()中方法确定的，对于视图组自身位置也是layout()方法确定。layout主要用来确定子view的位置
  (3).layout方法中确定位置的方法是setFrame和setOpticalFrame
  (4).在视图组中，复写onLayout方法。在其中遍历子view,对于每个view依次调用layout->onLayout

### 3、onDraw 绘制
作用：显示内容

- 绘制过程
  绘制背景、绘制内容、绘制子view、绘制装饰器 
  draw -> drawBackground -> onDraw -> dispatchDraw -> onDrawScrollBars
  自定义View可重写onDraw以绘制不同内容
             
### 4、invalidate/postInvalidate/requestLayout
  
- invalidate\postInvalidate
  作用：都是调用onDraw方法达到重新绘制的目的
  区别：invalidate只能在主线程中调用，postInvalidate能在子线程中调用，postInvalidate内部使用了handler\message机制最终还是掉用invalidate方法。
  
- requestLayout
  作用：调用onMeasure\onLayout重新测量和布局，有可能调用onDraw重新绘制。
  
## 5、ViewRoot/DecorView

- ViewRoot(实际是ViewRootImpl)：连接WindowManagerService和DecorView(最层级的View)的桥梁。View的三大流程均是通过ViewRootImpl来实现的。
- DecorView：顶级View，本身是一个FrameLayout。分为标题栏和内容栏，内容栏的id是R.android.id.content。Activity中的setContentView()方法最终是通过window.setContentView() 
  添加到DecorView的内容栏中。

## 6、View的绘制流程

- View的绘制流程是从ViewRootImpl的performTraversals方法开始：
- performTraversals会依次调用performMeasure\performLayout\performDraw，分别完成顶层View的测量、布局、绘制。
  过程中会对子View完成测量、布局、绘制。
      
## 7、getMeasuredHeight 和 getHeight 方法的区别（同理getMeasuredWith/getWith）

- 1、getMeasuredWidth是在onMeasure之后，getWidth是在onLayout之后。
  2、getMeasuredHeight方法返回的是测量后View的高度，与屏幕无关。getHeight返回的是屏幕显示的高度。当View没有超出屏幕时，他们的值
  是相等的，但当View超出屏幕显示时，getMeasuredHeight的值等于getHeight的值加上超出的高度。
  
- 为什么有时候用getWidth()或者getMeasureWidth()得到0? 
  View的绘制周期和Activity的生命周期不一致，所以在onCreate\onStart\onResume中调用方法都无法保证View测量、布局完成，所以获取的结果为0.
  


相关资料文章：
[Android 绘制原理浅析【干货】](https://juejin.cn/post/6844903903926960142)


# 十三.多线程编程

## 1、为何有多线程？

- 主线程(UI线程)
    - 在Android当中, 当应用启动的时候,系统会给应用分配一个进程,顺便一提,大部分应用都是单进程的,不过也可以通过设置来使不同组件运行在不同的进程中，
      在创建进程的同时会创建一个线程，应用的大部分操作都会在这个线程中运行。所以称为主线程，同时所有的UI控件相关的操作也要求在这个线程中操作，所以也称为UI线程。
- 为何会有子线程
    - 因为所有的UI控件的操作都在UI线程中执行，如果在UI线程中执行耗时操作，例如网络请求等，就会阻塞UI线程，导致系统报ANR(Application Not Response)错误。
      因此对于耗时操作需要创建工作线程来执行而不能直接在UI线程中执行。这样就需要在应用中使用多线程，但是Android提供的UI工具包并不是线程安全的，也就是说不能直接在
      工作线程中访问UI控件，否则会导致不能预测的问题， 因此需要额外的机制来进行线程交互，主要是让其他线程可以访问UI线程。
      
## 2、AsyncTask

- 定义：轻量级的异步任务类，内部封装了线程池、handler    
   
- 缺点：1、使用的是默认的线程池
    2、与Activity的生命周期不一致，在执行完doInBackground后才完结。
    3、容易造成内存泄漏，AsyncTask持有Activity的引用。
    4、当Activity生命周期异常后，AsyncTask结果丢失
  目前AsyncTask已被遗弃，推荐使用协程
  
## 3、ThreadPool

- 线程池规则：
    - 当线程池中的核心线程数未达到最大时，启动一个核心线程去执行任务。
    - 如果核心线程数达到最大，任务会安排到任务队列中等待。
    - 核心线程达到最大、任务队列已满，启动一个非核心线程执行任务。
    - 核心线程最大、任务队列已满、非核心线程达到最大，线程池拒绝执行任务。
    
- 优点：
    - 降低线程创建和销毁的系统开销
    - 线程复用，提高系统吞吐量
    - 执行大量异步任务时，提高性能
    - 提供了相关管理的API，使用方便
 
   
- execute :
     ``` 
    service.execute(new Runnable() {
    	public void run() {
    		System.out.println("execute方式");
    	}
    });
    ```
- submit  :
  ```
    Future<Integer> future = service.submit(new Callable<Integer>() {
    
    	@Override
    	public Integer call() throws Exception {
    		System.out.println("submit方式");
    		return 2;
    	}
    });
    try {
    	Integer number = future.get();
    } catch (ExecutionException e) {
    	e.printStackTrace();
    }
    ```
- 线程池关闭：
    - 调用线程池的`shutdown()`或`shutdownNow()`方法来关闭线程池
    - shutdown原理：将线程池状态设置成SHUTDOWN状态，然后中断所有没有正在执行任务的线程。
    - shutdownNow原理：将线程池的状态设置成STOP状态，然后中断所有任务(包括正在执行的)的线程，并返回等待执行任务的列表。
    - **中断采用interrupt方法，所以无法响应中断的任务可能永远无法终止。** 
      但调用上述的两个关闭之一，isShutdown()方法返回值为true，当所有任务都已关闭，表示线程池关闭完成，则isTerminated()方法返回值为true。
      当需要立刻中断所有的线程，不一定需要执行完任务，可直接调用shutdownNow()方法。
      
## 4、IntentService

- 定义：可以在内部开启子线程执行耗时任务的服务。

- 原理：继承至Service，内部封装了handler、looper、thread等用于子线程与主线程的交互。

## 5、TreadLocal

- 定义：
  存储一个数据，对指定的线程可见。

- 原理：
  通过使用当前线程的TreadLocalMap对set(value)的value进行存储，key为当前的TreadLocal。
  get()方法通过当前TreadLocal为key，在当前线程的TreadLocalMap中取值。
  
- ThreadLocalMap
  在Tread类中有TreadLocalMap的成员变量。TreadLocalMap是一种存储K-V的数据结构，内部使用的是哈希表结构。

## 6、JVM内存模型和Java内存模型(Java Memory Model)

- 内存分区：
    ·程序计数器
        - 记录各个线程执行的字节码的地址，保证代码分支、循环、跳转、异常、线程恢复等正常执行。
    ·本地方法栈
        - 存储与本地native方法交互的字节码。
    ·虚拟机栈
        - 内部使用的 栈帧 结构：(1)局部变量表(2)操作数栈(3)动态连接(4)返回地址
    ·方法区
        - 存储类信息、元数据、常量池
    ·堆
        - 存储实例对象，GC作用的主要区域
  
- JMM是什么？
  JMM定义了共享内存系统中程序读写操作行为的规范，JMM规定所有的变量都存储在主内存中，每条线程还有自己的工作内存。线程中的工作内存保存了被线程使用的变量的主内存副本，线程对变量的操作都必须在工作内存中进行，
  不能直接读写主内存中的变量，不同线程之间也无法直接访问对方工作内存中的变量副本，线程间变量值的传递需要通过主内存来完成。
  
- JMM如何实现？
  在java中提供了一系列和并发处理相关的关键字，例如volatile、synchronized、final、concurrent包等，开发者可以直接使用这些关键字进行开发，不用关心
  底层的编译器优化、缓存一致性等问题。

## 7、volatile

- 轻量级线程同步，保证操作数据的可见性、有序性，不保证原子性

## 8、synchronized

- 修饰普通方法、静态方法、代码块（this\object）      

## 9、ReentrantLock

- reentrantLock 可重入锁
  对比Synchronized那有些区别和优点？
  ·在语法上Synchronized是原生语法提供。在用法上可修饰方法、代码块。而reentrantLock需配合try-catch使用，并且需要手动释放锁。
  ·reentrantLock 等待可中断，是乐观锁，而Synchronized是独占锁，悲观锁。
  ·reentrantLock 可以添加多个锁条件
  ·reentrantLock 公平锁，多个线程等待同一个锁时，必须按照申请锁的时间顺序获得锁。reentrantLock也可通过构造方法设置为非公平锁。
 
## 10、Atomic原子类

- 定义：适用于单个元素，能够保证一个基本数据类型、对象、或者数组的原子性。           
    原子更新基本类型：AtomicInteger、AtomicBoolean、AtomicLong
    原子更新引用类型：AtomicReference、AtomicStampedReference、AtomicMarkableReference
    原子更新数组类型：AtomicLongArray、AtomicIntegerArray、AtomicReferenceArray
    原子更新对象属性：AtomicIntegerFieldUpdater、AtomicLongFieldUpdater、AtomicReferenceFieldUpdater
    

- 实现原理：
    - CAS(compare and swap):比较并交换  compareAndSwap(v,n,e)


相关资料文章：
[ThreadLocal原理其实很简单](https://juejin.cn/post/6986301941269659656)
[Java并发包中的Atomic原子类](https://juejin.cn/post/6977993272538955806)
        
# 十四.跨进程通信(IPC inter-process communication)

- linux系统中使用到的IPC机制有：管道、共享内存、socket、binder(android)等。
 
## 1、Binder机制
 
- Android系统为什么要选用Binder机制作为进程通信？ 
  答：
  ·binder数据传递只需要拷贝一次，效率较高
  ·安全性能高，通过给应用分配UID来鉴别应用的身份
  **Android中的binder机制是一种高效率、安全性能高的进程通信方式**
  
- 实现原理：
  · 进程隔离：系统为确保自身的安全稳定，将系统内核空间和用户空间分离开来。用户空间的进程要进行交互需要通过内核空间来驱动整个过程。
  · C/S结构：Binder作为一个Service的实体，对象提供一系列的方法来实现服务端和客户端之间的请求，只要client拿到这个引用就可以进行通信。
  （binder对象是一个可以跨进程引用的对象，它的实体位于一个进程中，而它的引用却遍布系统的各个进程中）
  · 通信模型：
    · Server : 跨进程服务端，运行在某个进程，通过Binder驱动在ServiceManager中注册
    · Client : 跨进程客户端，运行在某个进程，通过Binder驱动获取ServiceManager中的服务
    · ServiceManager : 提供服务的注册和查询，运行在SystemServer进程
    · Binder驱动：前三者位于用户空间，binder驱动位于内核空间，其实现方式和驱动差不多，负责进程之间Binder通信的建立，Binder在进程中的传递，Binder引用计数管理，
        数据包在进程之间的传递和交互。
  · 内存映射：Memory Map，将用户空间的一块内存地址映射到内核空间，映射关系建立后，用户对这块内存的修改可以直接反应到内核空间中。
  减少了数据拷贝的次数，实现用户空间和内核空间的高效互动。
    
  (1)Binder驱动在内核空间中创建了一个数据接收缓存区  
  (2)并在内核空间开辟了一个内核缓存区，建立内核缓存区和数据接收缓存区的映射关系，以及数据接收缓存区和用户空间地址的映射关系。
  (3)发送方进程通过copy_to_user函数将数据发送到内核缓存区，由于内核缓存区与数据接收区存在映射，而数据接收缓存区和用户空间地址映射，所以相当于把数据发送到
  接收方的用户空间。

## 2、AIDL(android interface definition language) Android接口定义语言
- 定义：Android接口定义语言，是一套模板代码，让某个service与多个应用程序组件之间跨进程通信。


相关资料文章：
[Android Binder原理（一）](https://juejin.cn/post/6844903976819752968)

           
# 十五.图片相关（bitmap加载、处理、缓存）

## 1、Bitmap加载（本地、网络）
- 原生方法加载网络图片
```
 private Bitmap returnBitmapFormUrl(String url) {
        long l1 = System.currentTimeMillis();
        Bitmap bitmap = null;
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        URL myUrl = null;
        try {
            myUrl = new URL(url);
            connection = (HttpURLConnection) myUrl.openConnection();
            connection.setReadTimeout(6000);
            connection.setConnectTimeout(6000);
            connection.setDoInput(true);
            connection.connect();
            inputStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            l1 = System.currentTimeMillis();
        }
        return bitmap;
    }
```
- 使用三方插件Glide

## 2、Bitmap处理（保存、压缩）
- 保存图片至相册
```
private void saveImageToFile(Context context, Bitmap bitmap) {
        File appDir = new File(Environment.getExternalStorageState(), "test");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.parse("file://"+file.getAbsolutePath()));
        context.sendBroadcast(intent);
    }
```
  
- 图片压缩
    - Bitmap.compress() 质量压缩，不会对内存产生影响
    - BitmapFactory.Options.inSampleSize 内存压缩
    
## 3、大图加载
             
- BitmapFactory.decodeStream(): 获取图片

- BitmapFactory.Option(): 设置图片相关参数（设置显示大小、缩放比例等）
   option.inSimpleSize = 2
  
- BitmapRegionDecoder: 图片局部展示

## 4、图片缓存

- LurCache
    内部采用LinkedHashMap存储数据，其最重要的方法trimToSize是用来移除最少使用的缓存和使用最久的缓存，并添加最新的缓存到队列中。
  
## 5、图片占用内存

- getByteCount/getAllocationByteCount
  
- with * height * 单个像素内存大小
  
## 6、如何避免加载图片出现OOM?

- 加载图片前，获取图片占用内存大小，决定是否压缩    
- 对比源图片宽高和控件宽高，决定是否缩放 BitmapFactory.Options的inJustDecodeBounds属性设置为true，解析一次图片获取图片宽高
- 采用局部加载BitmapRegionDecoder
- 多张图片的采用弱引用
   
# 十五.流行框架
## 1、RxJava
- 定义：基于事件流、实现异步操作的库。用到了多种设计模式如：装饰器模式、单例模式、观察者模式(扩展)、代理模式
  
- 为何要使用装饰模式？
  - 能保证原有类功能完整的情况下提供额外的功能。
    
- 四个角色：
    - observable:被观察者，发送事件
    - observer  :观察者，接收事件
    - subscribe :订阅事件
    - event     :事件
    
- 简单使用：
  ```
    Observable.create(new ObservableOnSubscribe<Integer>() {
        @Override
        public void subscribe(ObservableEmitter<Integer> e) {
            Log.e(TAG, "subscribe");
            Log.e(TAG, "currentThread name: " + Thread.currentThread().getName());
            e.onNext(1);
            e.onNext(2);
            e.onNext(3);
            e.onComplete();
        }
    })
    .subscribeOn(Schedulers.io())  //线程调度 订阅在IO线程 消费在主线程
    .observeOn(AndroidSchedulers.mainThread());
    .subscribe(new Observer<Integer>() {
        @Override
        public void onSubscribe(Disposable d) {
            Log.e(TAG, "onSubscribe");
        }
    
        @Override
        public void onNext(Integer integer) {
            Log.e(TAG, "onNext: " + integer);
        }
    
        @Override
        public void onError(Throwable e) {
            Log.e(TAG, "onError: " + e.getMessage());
        }
    
        @Override
        public void onComplete() {
            Log.e(TAG, "onComplete");
        }
    });
    ```          
  
- 事件种类：
    - onNext
    - onError
    - onComplete
    
- 线程调度：
    subscribeOn() 指定被观察者生产事件的线程  observerOn() 指定观察者接收事件的线程
    - 多次调用subscribeOn()，只会执行第一次调用。为什么？多次指定observerOn()，则每次指定都会切换一次线程。
    
    - 线程调度原理？
      - subscribeOn():将生产事件包装成runnable放在线程池中异步执行。
      - observerOn() :在主线程中创建Handler对象，发送消息，将处理事件带回主线程中执行。
    
- 源码分析：
    - 创建被观察者：
      Observable.create(new ObservableOnSubscribe())，将参数observableOnSubscribe传递给ObservableCreate，再通过
      RxJavaPlugins.onAssembly(new ObservableCreate<>(source))返回Observer对象。
      
    - 创建观察者：
      new Observer()，Observer是一个接口，需要实现接口中的所有方法：onSubscribe\onNext\onError\onComplete
      
    - 订阅：subscribe(new Observer())
      核心在ObservableCreate() -> subscribeActual() -> onSubscribe()方法&&创建了CreateEmitter()对象，
      CreateEmitter实现了Emitter中的onNext\onError\onComplete， 在这些方法中回调了Observer接口的对应的方法。
      ```
      public final class ObservableCreate<T> extends Observable<T> {
            final ObservableOnSubscribe<T> source;

        public ObservableCreate(ObservableOnSubscribe<T> source) {
            this.source = source;
        }

        @Override
        protected void subscribeActual(Observer<? super T> observer) {
            //1、创建CreateEmitter
            CreateEmitter<T> parent = new CreateEmitter<>(observer);
            //2、回调onSubscribe()
            observer.onSubscribe(parent);

                try {          
                    //3、执行Observable中的subscribe方法
                    source.subscribe(parent);
                } catch (Throwable ex) {
                    Exceptions.throwIfFatal(ex);
                     parent.onError(ex);
                }
        }
      ```
        
    - 线程调度：
      - Schedulers: 内部包含多种Scheduler，如：IO\SINGLE\COMPUTATION\NEW_THREAD用以不同类型任务的执行。
      - Scheduler : 是一个抽象类，有多种实现类。IoScheduler\SingleScheduler\NewThreadScheduler
      - subscribeOn(Schedulers.IO):创建一个ObservableSubscribeOn(observable,scheduler)对象。**使用代理模式，ObservableSubscribeOn代理了observable**。
      - observerOn(AndroidSchedulers.mainThread):创建了ObservableObserverOn(observable,scheduler,..)对象。
      - AndroidSchedulers.mainThread : 是一个HandlerScheduler(new Handler(Looper.getMainLooper()), true)，主线程Handler对象。
    
    RxJava将需要执行的任务包装成Runnable交给Scheduler调度，其中用到了线程池和Handler。

- 操作符：
    - just\fromArray\fromIterable\timer
    - map\flatMap\buffer
    - mergeArray\merge
    
- 背压策略：背压是指被观察者发送事件的速度大于观察者处理的速度，需要通知被观察者降低发送事件的速度的策略。

## 2、Glide 
- 定义：图片加载库

- 简单使用：
  ``` 
        Glide.with(this)
             .load(url)
             //transition(TransitionOptions transitionOptions)
             .transition(DrawableTransitionOptions.withCrossFade())
             .into(imageView);
  ```
- 三级缓存：
    - 本地缓存 DiskLruCache
    - 内存缓存 LruCache : LruCache使用get和put来完成缓存的添加和移除。当缓存满了后，将最近最少使用的对象移除。底层使用LinkedHashMap实现。
    - 网络缓存
    - 首先从内存中寻找图片，若不存在，则从磁盘中寻找，若不存在，则从网络异步加载。
    
  
- 源码解析：
  - Glide.with(context):
  ```
    @NonNull
    public static RequestManager with(@NonNull Context context) {
        return getRetriever(context).get(context);
    }
    
    @NonNull
    public RequestManager get(@NonNull Context context) {
        if (context == null) {
          throw new IllegalArgumentException("You cannot start a load on a null Context");
        } else if (Util.isOnMainThread() && !(context instanceof Application)) {
          if (context instanceof FragmentActivity) {
            return get((FragmentActivity) context);
          } else if (context instanceof Activity) {
            return get((Activity) context);
          } else if (context instanceof ContextWrapper) {
            return get(((ContextWrapper) context).getBaseContext());
          }
        }
        
        return getApplicationManager(context);
    }
  ```
  调用RequestManager的get方法。
  ```
    @NonNull
    public RequestManager get(@NonNull FragmentActivity activity) {
        if (Util.isOnBackgroundThread()) {
          return get(activity.getApplicationContext());
        } else {
          assertNotDestroyed(activity);
          FragmentManager fm = activity.getSupportFragmentManager();
          return supportFragmentGet(
              activity, fm, /*parentHint=*/ null, isActivityVisible(activity));
        }
    }
  ```
  - 这里做了一个判断，如果Glide在子线程中使用或者传入的context是ApplicationContext，那么就与全局的ApplicationContext绑定，如果不是那么创建一个无界面的fragment
      ，即SupportRequestManagerFragment，让请求和你的activity的生命周期同步
       
  - 在with方法中，主要是创建了一个requestManager的类，这个是网络请求的管理类，主要负责将所有的类的生命周期与调用其的组件的生命周期相绑定，
      这也是Glide与其他图片加载框架不一样的地方，它是和组件的生命周期相绑定的。    

- Glide遇到的问题&&优化
  - 问题：
    - 1、列表中加载大量图片导致OOM
      解决：（1）尽量减小图片的体积（2）跳过内存缓存，使用磁盘缓存（3）只加载当前屏幕可视范围内的图片
        
    - 2、列表滑动、更新数据时图片闪烁
      解决：去除Glide加载动画
 - 优化：低内存优化
   - 实现方法：
     （1）在Application中重写onLowMemory\onTrimMemory方法
     （2）在onLowMemory方法中调用Glide.clearMemory清除Glide缓存
     （3）在onTrimMemory方法中调用Glide.trimMemory方法
     
- Glide对象池：
  - 在应用中Glide会多次调用加载图片，过程中会创建相关的类，如果不使用对象池，那么这些类会被重复的创建和销毁，会频繁的触发GC，甚至出现内存抖动。
    Glide使用对象池的机制，对这种频繁需要创建和销毁的对象保存在一个对象池中。每次用到该对象时，就取对象池空闲的对象，并对它进行初始化操作，从而提高框架的性能。
  - BitmapPool:针对图片对象进行缓存  
 

## 3、EventBus
- 定义：是一款发布\订阅事件总线框架，基于单例、观察者模式，将事件发送者和接收者分开，简化了组件之间的通信。
    使用到的设计模式有：单例模式、观察者模式、builder模式、
    内部使用了线程池、handler进行事件发送和线程切换，

     
- 三要素：
  Event     :事件，可以是任意类型。
  Subscriber:订阅者，通过注解@subscribe标记接收事件的方法，对事件进行处理。
  Publisher :发布者，可以在任意线程中调用post()进行发送事件。
  
- 四种线程模型：
  (1)POSTING   :默认，事件在那个线程发布就在那个线程接收。
  (2)MAIN      :主线程，事件接收在主线程。
  (3)BACKGROUND:子线程，事件接收在子线程，不能执行更新UI的操作。
  (4)ASYNC     :无论事件发布在那个线程，事件接收都在子线程，不能执行更新UI的操作。
  
- 简单使用流程：
  - 定义事件：
    ```
    public class MessageEvent {
    }
    ```
  - 注册EventBus:在接收事件的组件中（一般是Activity）
    ```
    EventBus.getDefault().register(this);
    ```
  - 发送事件：可在任意组件或者线程中
    ```
    EventBus.getDefault().post(messageEvent);
    ```
  - 接收事件：接收事件名称可任取，但是必须加上@Subscribe注解，threadMode = ThreadMode.MAIN是指定线程，如不指定则为默认POSTING
    ```
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleEvent(MessageEvent messageEvent) {

    }
    ```
  - 取消订阅：
    ```
    EventBus.getDefault().unregister(this);
    ```       
    
- 优缺点分析：
  - 优点：使用方便，减小组件通信的复杂度，减小耦合。
  - 缺点：逻辑分散，可读性差，不利于维护。
    
- 内存泄漏分析：
  - 如果不进行取消订阅，会不会造成内存泄漏？为什么？
    答：会，如果接收事件的方法中持有当前组件的引用，在当前组件生命周期结束后，继续发送事件则会造成内存泄漏。
    
- 源码分析：
    - getDefault(): 使用了单例模式+builder模式
    单例模式获取defaultInstance
    ```
    /** Convenience singleton for apps using a process-wide EventBus instance. */
    public static EventBus getDefault() {
        if (defaultInstance == null) {
            synchronized (EventBus.class) {
                if (defaultInstance == null) {
                    defaultInstance = new EventBus();
                }
            }
        }
        return defaultInstance;
    }
    ```
    builder模式
    ```
    private static final EventBusBuilder DEFAULT_BUILDER = new EventBusBuilder();
    
    public EventBus() {
        this(DEFAULT_BUILDER);
    }
    
    EventBus(EventBusBuilder builder) {
        logger = builder.getLogger();
        subscriptionsByEventType = new HashMap<>();
        typesBySubscriber = new HashMap<>();
        stickyEvents = new ConcurrentHashMap<>();
        mainThreadSupport = builder.getMainThreadSupport();
        mainThreadPoster = mainThreadSupport != null ? mainThreadSupport.createPoster(this) : null;
        backgroundPoster = new BackgroundPoster(this);
        asyncPoster = new AsyncPoster(this);
        indexCount = builder.subscriberInfoIndexes != null ? builder.subscriberInfoIndexes.size() : 0;
        subscriberMethodFinder = new SubscriberMethodFinder(builder.subscriberInfoIndexes,
                builder.strictMethodVerification, builder.ignoreGeneratedIndex);
        logSubscriberExceptions = builder.logSubscriberExceptions;
        logNoSubscriberMessages = builder.logNoSubscriberMessages;
        sendSubscriberExceptionEvent = builder.sendSubscriberExceptionEvent;
        sendNoSubscriberEvent = builder.sendNoSubscriberEvent;
        throwSubscriberException = builder.throwSubscriberException;
        eventInheritance = builder.eventInheritance;
        executorService = builder.executorService;
    }
    ```    
    - register(): 
    ```
      public void register(Object subscriber) { 
        //1、获取订阅者类对象
        Class<?> subscriberClass = subscriber.getClass();
        //2、通过findSubscriberMethods找到订阅方法。
        List<SubscriberMethod> subscriberMethods = subscriberMethodFinder.findSubscriberMethods(subscriberClass);
        synchronized (this) {
            for (SubscriberMethod subscriberMethod : subscriberMethods) {
                //3、调用subscribe
                subscribe(subscriber, subscriberMethod);
            }
        }
    }
   ```
   - findSubscriberMethods(subscriberClass)：从订阅类中查找订阅方法，订阅类和订阅方法是存储在一个Map集合里面，
        key为订阅类，value为订阅方法集合，ConcurrentHashMap支持多线程操作线程安全。
    ```
    List<SubscriberMethod> findSubscriberMethods(Class<?> subscriberClass) {
        //首先从缓存中读取
        List<SubscriberMethod> subscriberMethods = METHOD_CACHE.get(subscriberClass);
        if (subscriberMethods != null) {
            return subscriberMethods;
        }
        //是否忽略注解器生成的MyEventBusIndex类
        if (ignoreGeneratedIndex) {
            //利用反射来获取订阅类中的订阅方法信息
            subscriberMethods = findUsingReflection(subscriberClass);
        } else {
            //从注解器生成的MyEventBusIndex类中获得订阅类的订阅方法信息
            subscriberMethods = findUsingInfo(subscriberClass);
        }
    
        //在获得subscriberMethods以后，如果订阅者中不存在@Subscribe注解并且为public的订阅方法，则会抛出异常。
        if (subscriberMethods.isEmpty()) {
            throw new EventBusException("Subscriber " + subscriberClass
                    + " and its super classes have no public methods with the @Subscribe annotation");
        } else {
            //保存进缓存
            METHOD_CACHE.put(subscriberClass, subscriberMethods);
            return subscriberMethods;
        }
    }
    //METHOD_CACHE，是一个map集合，键是class类型
    Map<Class<?>, List<SubscriberMethod>> METHOD_CACHE = new ConcurrentHashMap<>();
    ```
   - subscribe(subscriber,subscribeMethod): 首先将订阅方法按照订阅优先级存放在 CopyOnWriteArrayList<Subscription> 中，
      然后将订阅的事件放在名为subscriptionsByEventType的HashMap中，key为订阅类，value为订阅事件。
    ```
    private void subscribe(Object subscriber, SubscriberMethod subscriberMethod) {
        //获取订阅方法的参数类型 
        Class<?> eventType = subscriberMethod.eventType;
        //根据订阅者和订阅方法构造一个订阅事件
        Subscription newSubscription = new Subscription(subscriber, subscriberMethod);
        //获取当前订阅事件中Subscription的List集合
        CopyOnWriteArrayList<Subscription> subscriptions = subscriptionsByEventType.get(eventType);
        //该事件对应的Subscription的List集合不存在，则重新创建并保存在subscriptionsByEventType中
        if (subscriptions == null) {
            subscriptions = new CopyOnWriteArrayList<>();
            subscriptionsByEventType.put(eventType, subscriptions);
        } else {
        //订阅者已经注册则抛出EventBusException
            if (subscriptions.contains(newSubscription)) {
                throw new EventBusException("Subscriber " + subscriber.getClass() + " already registered to event "
                        + eventType);
            }
        }
    
        //遍历订阅事件，找到比subscriptions中订阅事件小的位置，然后插进去
        int size = subscriptions.size();
        for (int i = 0; i <= size; i++) {
            if (i == size || subscriberMethod.priority > subscriptions.get(i).subscriberMethod.priority) {
                subscriptions.add(i, newSubscription);
                break;
            }
        }
    
         //通过订阅者获取该订阅者所订阅事件的集合
        List<Class<?>> subscribedEvents = typesBySubscriber.get(subscriber);
        if (subscribedEvents == null) {
            subscribedEvents = new ArrayList<>();
            typesBySubscriber.put(subscriber, subscribedEvents);
        }
    
        //将当前的订阅事件添加到subscribedEvents中
        subscribedEvents.add(eventType);
        if (subscriberMethod.sticky) {
            if (eventInheritance) {
            //粘性事件的处理
                Set<Map.Entry<Class<?>, Object>> entries = stickyEvents.entrySet();
                for (Map.Entry<Class<?>, Object> entry : entries) {
                    Class<?> candidateEventType = entry.getKey();
                    if (eventType.isAssignableFrom(candidateEventType)) {
                        Object stickyEvent = entry.getValue();
                        checkPostStickyEventToSubscription(newSubscription, stickyEvent);
                    }
                }
            } else {
                Object stickyEvent = stickyEvents.get(eventType);
                checkPostStickyEventToSubscription(newSubscription, stickyEvent);
            }
        }
    }
    ```     
   - post(event):将事件添加到PostThreadState（使用了ThreadLocal，只对当前线程可见）的队列中，对队列中的事件依次调用了
       postSingleEvent()来发送事件。
     
    ```
    /** Posts the given event to the event bus. */
    public void post(Object event) {
        //获取当前线程的postingState
        PostingThreadState postingState = currentPostingThreadState.get();
        //取得当前线程的事件队列
        List<Object> eventQueue = postingState.eventQueue;
        //将该事件添加到当前的事件队列中等待分发
        eventQueue.add(event);
        if (!postingState.isPosting) {
            //判断是否是在主线程post
            postingState.isMainThread = Looper.getMainLooper() == Looper.myLooper();
            postingState.isPosting = true;
            if (postingState.canceled) {
                throw new EventBusException("Internal error. Abort state was not reset");
            }
            try {
                while (!eventQueue.isEmpty()) {
                    //分发事件
                    postSingleEvent(eventQueue.remove(0), postingState);
                }
            } finally {
                postingState.isPosting = false;
                postingState.isMainThread = false;
            }
        }
    }
    ```            
   - postSingleEvent(event,state):
     当eventInheritance为true时，则通过lookupAllEventTypes找到所有的父类事件并存在List中，然后通过postSingleEventForEventType方法对事件逐一处理
   ```
    事件分发
    private void postSingleEvent(Object event, PostingThreadState postingState) throws Error {
        //得到事件类型
        Class<?> eventClass = event.getClass();
        boolean subscriptionFound = false;
        //是否触发订阅了该事件(eventClass)的父类,以及接口的类的响应方法.
        if (eventInheritance) {
            List<Class<?>> eventTypes = lookupAllEventTypes(eventClass);
            int countTypes = eventTypes.size();
            for (int h = 0; h < countTypes; h++) {
                Class<?> clazz = eventTypes.get(h);
                subscriptionFound |= postSingleEventForEventType(event, postingState, clazz);
            }
        } else {
            subscriptionFound = postSingleEventForEventType(event, postingState, eventClass);
        }
        if (!subscriptionFound) {
            if (logNoSubscriberMessages) {
                Log.d(TAG, "No subscribers registered for event " + eventClass);
            }
            if (sendNoSubscriberEvent && eventClass != NoSubscriberEvent.class &&
                    eventClass != SubscriberExceptionEvent.class) {
                post(new NoSubscriberEvent(this, event));
            }
        }
    }
   ```
   - postSingleEventForEventType(event,state,eventClass): 从subscriptionsByEventType中根据eventClass取出所有的订阅者CopyOnWriteArrayList<Subscription>集合，
     然后调用postToSubscription进行处理。
    ```
    private boolean postSingleEventForEventType(Object event, PostingThreadState postingState, Class<?> eventClass) {
        CopyOnWriteArrayList<Subscription> subscriptions;
        synchronized (this) {
            //根据事件类型获取所有的订阅者
            subscriptions = subscriptionsByEventType.get(eventClass);
        }
        //向每个订阅者分发事件
        if (subscriptions != null && !subscriptions.isEmpty()) {
            for (Subscription subscription : subscriptions) {
                postingState.event = event;
                postingState.subscription = subscription;
                boolean aborted = false;
                try {
                    //对事件进行处理
                    postToSubscription(subscription, event, postingState.isMainThread);
                    aborted = postingState.canceled;
                } finally {
                    postingState.event = null;
                    postingState.subscription = null;
                    postingState.canceled = false;
                }
                if (aborted) {
                    break;
                }
            }
            return true;
        }
        return false;
    }
    ```
   - postToSubscription(subscriptions,event,isMainTread): 根据指定的线程模型来判断执行 invokeSubscriber(subscription, event) 还是调用poster.enqueue()方法。
    ```
    private void postToSubscription(Subscription subscription, Object event, boolean isMainThread) {
        switch (subscription.subscriberMethod.threadMode) {
            case POSTING://默认的 ThreadMode，表示在执行 Post 操作的线程直接调用订阅者的事件响应方法，
            //不论该线程是否为主线程（UI 线程）。
                invokeSubscriber(subscription, event);
                break;
            case MAIN://在主线程中执行响应方法。
                if (isMainThread) {
                    invokeSubscriber(subscription, event);
                } else {
                    mainThreadPoster.enqueue(subscription, event);
                }
                break;
            case BACKGROUND://在后台线程中执行响应方法。
                if (isMainThread) {
                    backgroundPoster.enqueue(subscription, event);
                } else {
                    invokeSubscriber(subscription, event);
                }
                break;
            case ASYNC://不论发布线程是否为主线程，都使用一个空闲线程来处理。
                asyncPoster.enqueue(subscription, event);
                break;
            default:
                throw new IllegalStateException("Unknown thread mode: " + subscription.subscriberMethod.threadMode);
        }
    }
    ```     
   - unregister()：
    ```
    public synchronized void unregister(Object subscriber) {
        //获取订阅者的所有订阅的事件类型
        List<Class<?>> subscribedTypes = typesBySubscriber.get(subscriber);
        if (subscribedTypes != null) {
            for (Class<?> eventType : subscribedTypes) {
                //从事件类型的订阅者集合中移除订阅者
                unsubscribeByEventType(subscriber, eventType);
            }
            typesBySubscriber.remove(subscriber);
        } else {
            Log.w(TAG, "Subscriber to unregister was not registered before: " + subscriber.getClass());
        }
    }
    ```
    ```
    private void unsubscribeByEventType(Object subscriber, Class<?> eventType) {
        //获取事件类型的所有订阅者
        List<Subscription> subscriptions = subscriptionsByEventType.get(eventType);
        //遍历订阅者集合，将解除的订阅者移除
        if (subscriptions != null) {
            int size = subscriptions.size();
            for (int i = 0; i < size; i++) {
                Subscription subscription = subscriptions.get(i);
                if (subscription.subscriber == subscriber) {
                    subscription.active = false;
                    subscriptions.remove(i);
                    i--;
                    size--;
                }
            }
        }
    }
    ```      

- 相关流程总结：
    - 订阅流程：register() -> findSubscriberMethods() -> subscribe()
      （1）获取订阅类对象，通过类对象找到所有订阅方法的集合
      （2）查找订阅方法时，首先从一个名为METHOED_CACHE的concurrentHashMap中查找，如果没有则利用反射查找并保存。
      （3）将所有订阅方法，以eventType为key，订阅关系Subscription为value，存储在subscriptionByEventType的HashMap中。
      （4）再以subscriber为key，eventType为value，存储在typeBySubscriber中
      
    - 发布流程：post() -> postSingleEvent() -> postSingleEventForEventType() -> postToSubscribe()
      （1）将事件添加到PostingThreadState的队列中，依次调用postSingleEvent(event)，PostingTreadState是一个ThreadLocal对象。
      （2）在postSingleEvent方法中，从subscriptionByEventType中取出所有的订阅者Subscription。
      （3）判断Subscription所指定的线程模型，是否直接执行invokeSubscriber()还是调用poster.enqueue()方法。
      （4）invokeSubscriber()方法利用反射调用了具体的订阅方法，而poster.enqueue()方法使用不同的poster，调用线程池执行run方法，
          在run方法中执行invokeSubscriber()
    
    - 取消流程：unregister() -> unsubscribeByEventType() -> remove()
      （1）从typesBySubscriber中获取订阅类中所有的订阅事件
      （2）从subscriptionByEventType中移除所有的订阅关系Subscription
    
相关资料文章：
[Android 开发框架 EventBus 原理解析](https://www.cnblogs.com/renhui/articles/13943921.html)


## 4、Retrofit
- 定义：基于OkHttp实现的网络请求框架。使用面向接口的方式进行网络请求，利用动态生成的代理类封装了网络接口请求的底层。
    使用到的设计模式：单例模式、builder模式、策略模式、工厂模式、适配器模式

- 为什么要使用Retrofit?
  - 优点：
      请求方法的参数注解可以定制；
      支持同步、异步、rxjava、concurrent
      超级解耦
      支持不同的反序列化工具来解析数据
        
- 简单使用：
  ```
  OkHttpClient client = builder.addInterceptor(new LogInterceptor("HTTP")).build();
  Gson gson = new GsonBuilder()
            .setLenient()
            .create();
  mRetrofit = new Retrofit
            .Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .client(client)
            .build();
  ```  

- 源码分析：
  - builder(): 
    获取platform，实际是返回的new Android()，提供handler与主线程通信
    ```
    //第一步
    public Builder() {
    this(Platform.get());
    }
    //第二步，追踪到Platform类中
    private static final Platform PLATFORM = findPlatform();
    static Platform get() {
        return PLATFORM;
    }
    private static Platform findPlatform() {
    try {
      Class.forName("android.os.Build");
      if (Build.VERSION.SDK_INT != 0) {
        //此处表示：如果是Android平台，就创建并返回一个Android对象
        return new Android();
      }
    } catch (ClassNotFoundException ignored) {
    }
    try {
      Class.forName("java.util.Optional");
      return new Java8();
    } catch (ClassNotFoundException ignored) {
    }
    return new Platform();
    }
    ```
    
  - baseUrl(url): 
    设置baseUrl
    ```
    public Builder baseUrl(HttpUrl baseUrl) {
      checkNotNull(baseUrl, "baseUrl == null");
      List<String> pathSegments = baseUrl.pathSegments();
      if (!"".equals(pathSegments.get(pathSegments.size() - 1))) {
        throw new IllegalArgumentException("baseUrl must end in /: " + baseUrl);
      }
      this.baseUrl = baseUrl;
      return this;
    }
    ```   
    
  - addConverterFactory(CustomGsonConverterFactory.create(jsonBuilder.create())):
    添加对象序列化和反序列化的转换器工厂
    ```
    public Builder addConverterFactory(Converter.Factory factory) {
    //ConverterFactory 放入到 converterFactories 数组中
    converterFactories.add(checkNotNull(factory, "factory == null"));
    return this;
    }
    ```      
    
  - addCallAdapterFactory(RxJava2CallAdapterFactory.create())：
    添加一个调用适配器工厂，用于支持服务方法返回类型。将默认的网络请求执行器（OkHttpCall）转换成适合被不同平台来调用的网络请求执行器形式。
    ```
    public Builder addCallAdapterFactory(CallAdapter.Factory factory) {
      callAdapterFactories.add(checkNotNull(factory, "factory == null"));
      return this;
    }
    ```                 
    
  - client(okHttpClient):
    用于请求的HTTP客户端，指定用于创建{@link Call}实例的自定义调用工厂。
    ```
    public Builder client(OkHttpClient client) {
      return callFactory(checkNotNull(client, "client == null"));
    }
    
    public Builder callFactory(okhttp3.Call.Factory factory) {
      this.callFactory = checkNotNull(factory, "factory == null");
      return this;
    }
    ```        
    
  - build():
    通过前面的配置创建Retrofit对象
    ```
    public Retrofit build() {
      if (baseUrl == null) {
        throw new IllegalStateException("Base URL required.");
      }
    
      okhttp3.Call.Factory callFactory = this.callFactory;
      if (callFactory == null) {
        callFactory = new OkHttpClient();
      }
    
      Executor callbackExecutor = this.callbackExecutor;
      if (callbackExecutor == null) {
        callbackExecutor = platform.defaultCallbackExecutor();
      }
    
      // Make a defensive copy of the adapters and add the default Call adapter.
      List<CallAdapter.Factory> callAdapterFactories = new ArrayList<>(this.callAdapterFactories);
      callAdapterFactories.add(platform.defaultCallAdapterFactory(callbackExecutor));
    
      // Make a defensive copy of the converters.
      List<Converter.Factory> converterFactories =
          new ArrayList<>(1 + this.converterFactories.size());
    
      // Add the built-in converter factory first. This prevents overriding its behavior but also
      // ensures correct behavior when using converters that consume all types.
      converterFactories.add(new BuiltInConverters());
      converterFactories.addAll(this.converterFactories);
    
      return new Retrofit(callFactory, baseUrl, unmodifiableList(converterFactories),
          unmodifiableList(callAdapterFactories), callbackExecutor, validateEagerly);
    }
    ```          
    
  - create(service.class):
    首先判断参数service是否是一个接口。然后通过动态代理模式，在InvocationHandler类中生成网络请求接口的代理类。
    ```
    public <T> T create(final Class<T> service) {
    Utils.validateServiceInterface(service);
    if (validateEagerly) {
      eagerlyValidateMethods(service);
    }
    return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[] { service },
        new InvocationHandler() {
          private final Platform platform = Platform.get();
    
          @Override public Object invoke(Object proxy, Method method, @Nullable Object[] args)
              throws Throwable {
            // If the method is a method from Object then defer to normal invocation.
            if (method.getDeclaringClass() == Object.class) {
              return method.invoke(this, args);
            }
            if (platform.isDefaultMethod(method)) {
              return platform.invokeDefaultMethod(method, service, proxy, args);
            }
            
            //读取网络请求接口里的方法，并根据前面配置好的属性配置serviceMethod对象
            ServiceMethod<Object, Object> serviceMethod =
                (ServiceMethod<Object, Object>) loadServiceMethod(method);
            //根据配置好的serviceMethod对象创建okHttpCall对象 
            OkHttpCall<Object> okHttpCall = new OkHttpCall<>(serviceMethod, args);
            //调用OkHttp，并根据okHttpCall返回rejava的Observe对象或者返回Call
            return serviceMethod.adapt(okHttpCall);
          }
        });
    }
    ``` 
  - loadServiceMethod(method):
    从缓存中获取对应方法的ServiceMethod，调用parseAnnotations解析注解
    ```
    ServiceMethod<?> loadServiceMethod(Method method) {
        ServiceMethod<?> result = serviceMethodCache.get(method);
        if (result != null) return result;

        synchronized (serviceMethodCache) {
        result = serviceMethodCache.get(method);
        if (result == null) {
            result = ServiceMethod.parseAnnotations(this, method);
            serviceMethodCache.put(method, result);
            }
        }
        return result;
     }
    ```
  - parseAnnotations(method):
    创建RequestFactory，再调用HttpServiceMethod.parseAnnotations解析注解。
    ```
    static <T> ServiceMethod<T> parseAnnotations(Retrofit retrofit, Method method) {
        RequestFactory requestFactory = RequestFactory.parseAnnotations(retrofit, method);

        Type returnType = method.getGenericReturnType();
        if (Utils.hasUnresolvableType(returnType)) {
            throw methodError(
                 method,
                "Method return type must not include a type variable or wildcard: %s",
                returnType);
        }   
        if (returnType == void.class) {
            throw methodError(method, "Service methods cannot return void.");
        }

        return HttpServiceMethod.parseAnnotations(retrofit, method, requestFactory);
    }
    ```  
      
- 相关流程总结：
    - retrofit创建过程：
      （1）获取平台类型对象（getPlatform ->new Android()）
      （2） 网络请求的url地址（baseUrl）
      （3）网络请求工厂（callFactory）                      默认使用OkHttpCall
      （4）网络请求适配器工厂的集合（adapterFactories）      本质是配置了网络请求适配器工厂- 默认是ExecutorCallAdapterFactory
      （5）数据转换器工厂的集合（converterFactories）        本质是配置了数据转换器工厂
      （6）回调方法执行器（callbackExecutor）               默认回调方法执行器作用是：切换线程（子线程 - 主线程）
    
    - apiService调用过程：
      （1）通过动态代理生成代理类，在InvocationHandler中调用loadServiceMethod方法
      （2）在loadServiceMethod中，从缓存中获取请求接口对应的ServiceMethod，调用parseAnnotations解析注解。
      （3）在parseAnnotations中，生成RequestFactory请求工厂，再调用HttpServiceMethod.parseAnnotations解析注解。
      （4）在HttpServiceMethod.parseAnnotations解析对应的注解后，返回请求体包装类SuspendForBody
    
## 5、ARouter
- 定义：路由框架

-
# 十五.ActivityThread

### 1、主线程初始化
### 2、looper初始化
### 3、application初始化
### 4、phoneWindow\decorView\ViewRootImpl


### 十.基本排序算法

[https://www.jianshu.com/p/c3670e7aa1b0](https://www.jianshu.com/p/c3670e7aa1b0)

**选择排序 冒泡排序 快速排序**  合并排序 堆排序



# 10.MVC、MVP、MVVM各种框架的区别

[https://www.jianshu.com/p/78e0a508b1c6](https://www.jianshu.com/p/78e0a508b1c6)

# 11.常用设计模式

单例模式、builder模式、策略模式、观察者模式



# 18.Android性能优化

[https://blog.csdn.net/xiangzhihong8/article/details/92800490](https://blog.csdn.net/xiangzhihong8/article/details/92800490)

# 19.Android webView如何与Js交互？遇到什么问题？

[https://blog.csdn.net/jerrywu145/article/details/57512366](https://blog.csdn.net/jerrywu145/article/details/57512366)
交互：1.首先对webView进行设置，是否支持Js调用、是否支持缩放等等 2.设置监听setWebChromClient监听加载网页过程
3.在监听方法中的onProgressChanged中，判断网页加载完成后调用web.loadurl('''javascript:call()')对Js方法进行调用 遇到的问题：调用不成功？
解决：检查是否设置了支持Js调用、检查知否加入了 @JavascriptInterface 注解、检查是否在网页完全加载成功后调用的JS方法

# 20.Android中滑动冲突问题的解决

1.布局中嵌套recycleView 2.recycleView多级嵌套
[https://www.jianshu.com/p/c5ccf0c38186](https://www.jianshu.com/p/c5ccf0c38186)

3.viewPager嵌套
[https://www.jianshu.com/p/4cbd0f8341fb](https://www.jianshu.com/p/4cbd0f8341fb)

使用CoordinatorLayout包裹

# 21.Android中的context的理解

[https://www.jianshu.com/p/08b447a7e28a](https://www.jianshu.com/p/08b447a7e28a)
[https://www.cnblogs.com/Jason-Jan/p/8465664.html](https://www.cnblogs.com/Jason-Jan/p/8465664.html)
context是一个抽象类，它提供了应用的环境信息。我们可通过它启动Activity、发送广播、接收Intent信息等。Activity、server就是一个context。
![687474703a2f2f75706c6f61642d696d616765732e6a69616e7368752e696f2f75706c6f61645f696d616765732f313138373233372d316234633063643331666430313933662e706e673f696d6167654d6f6772322f6175746f2d6f7269656e742f7374726970253743696.png](https://upload-images.jianshu.io/upload_images/5377834-4a255c0d4859b5f4.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

  
