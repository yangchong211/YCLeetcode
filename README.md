# liveData实现事件总线
#### 目录介绍
- 01.先提出问题思考
- 02.该liveDataBus优势
- 03.EventBus使用原理
- 04.RxBus使用原理
- 05.三者之间优缺点对比
- 06.LiveDataBus的组成
- 07.LiveDataBus原理图
- 08.该库使用api方法
- 09.事件消息系列博客
- 10.遇到问题思考汇总


### 01.先提出问题思考



### 02.该liveDataBus优势
- 1.该LiveDataBus的实现比较简单，支持发送普通事件，也支持发送粘性事件；
- 2.该LiveDataBus支持发送延迟事件消息，也可以用作轮训延迟事件(比如商城类项目某活动页面5秒钟刷一次接口数据)，支持stop轮训操作
- 3.该LiveDataBus可以减小APK包的大小，由于LiveDataBus只依赖Android官方Android Architecture Components组件的LiveData；
- 4.该LiveDataBus具有生命周期感知，这个是一个很大的优势。不需要反注册，避免了内存泄漏等问题；



### 03.EventBus使用原理
- 框架的核心思想，就是消息的发布和订阅，使用订阅者模式实现，其原理图大概如下所示。
    - ![image](https://github.com/yangchong211/YCLiveDataBus/blob/master/image/eventbus1.png)
- 发布和订阅之间的依赖关系，其原理图大概如下所示。
    - ![image](https://github.com/yangchong211/YCLiveDataBus/blob/master/image/eventbus2.png)
- 订阅/发布模式和观察者模式之间有着微弱的区别，个人觉得订阅/发布模式是观察者模式的一种增强版。两者区别如下所示。
    - ![image摘自网络](https://github.com/yangchong211/YCLiveDataBus/blob/master/image/eventbus3.png)
- LiveDataBus的组成
    - 消息： 消息可以是任何的 Object，可以定义不同类型的消息，如 Boolean、String。也可以定义自定义类型的消息。
    - 消息通道： LiveData 扮演了消息通道的角色，不同的消息通道用不同的名字区分，名字是 String 类型的，可以通过名字获取到一个 LiveData 消息通道。
    - 消息总线： 消息总线通过单例实现，不同的消息通道存放在一个 HashMap 中。
    - 订阅： 订阅者通过 with() 获取消息通道，然后调用 observe() 订阅这个通道的消息。
    - 发布： 发布者通过 with() 获取消息通道，然后调用 setValue() 或者 postValue() 发布消息。


### 04.RxBus使用原理



### 05.三者之间优缺点对比
- 对比结果如下所示
    | 事件总线 | 发送粘性事件 | 是否有序接收消息 | 延迟发送 | 组建生命周期感知 | 跨线程发事件 |
    | :------ | :--------- | :------------- | :------ | :-------------- | :-------- | 
    | LiveDataBus | true  | true         | true         |true         |false         |
    | EventBus | true     | true          | false       |false        |true         |
    | RxBus | true        | false       | false         |false        |true         |
- EventBus 是业界知名的通信类总线库，存在许多被人诟病的缺点：
    - 需要手动的注册和反注册，稍不小心可能会造成内存泄露。
    - 使用 EventBus 出错时难以跟踪出错的事件源。
    - 每个事件都要定义一个事件类，容易造成类膨胀。
    - 无法感知activity或者fragment生命周期，页面不可见时就处理收到消息逻辑
- 通过 LiveData 实现的 LiveDataBus 的具有以下优点：
    - 具有生命周期感知能力，不用手动注册和反注册。
    - 具有唯一的可信事件源。
    - 以字符串区分每一个事件，避免类膨胀。
    - LiveData 为 Android 官方库，更加可靠。



### 06.LiveDataBus的组成
- 消息： 消息可以是任何的 Object，可以定义不同类型的消息，如 Boolean、String。也可以定义自定义类型的消息。
- 消息通道： LiveData 扮演了消息通道的角色，不同的消息通道用不同的名字区分，名字是 String 类型的，可以通过名字获取到一个 LiveData 消息通道。
- 消息总线： 消息总线通过单例实现，不同的消息通道存放在一个 HashMap 中。
- 订阅： 订阅者通过 with() 获取消息通道，然后调用 observe() 订阅这个通道的消息。
- 发布： 发布者通过 with() 获取消息通道，然后调用 setValue() 或者 postValue() 发布消息。



### 07.LiveDataBus原理图
#### 7.1 订阅和注册的流程图
- ![image](https://img-blog.csdnimg.cn/20200305173032750.jpg)


#### 7.1 订阅注册原理图
![image](https://img-blog.csdnimg.cn/2020030517313021.jpg)




### 08.该库使用api方法
#### 8.1 最简单常见的发布/订阅事件消息
- 订阅事件，该种使用方式不需要取消订阅
    ```
    LiveDataBus.get()
            .with(Constant.YC_BUS, String.class)
            .observe(this, new Observer<String>() {
                @Override
                public void onChanged(@Nullable String newText) {
                    // 更新数据
                    BusLogUtils.d("接收消息--------yc_bus---2-"+newText);
                }
            });
    ```
- 那么如何发布事件消息呢，代码如下
    ```
    LiveDataBus.get().with(Constant.YC_BUS).setValue("test_yc_data");
    LiveDataBus.get().with(Constant.YC_BUS).postValue("test_yc_data");
    ```


#### 8.2 Forever模式订阅和取消订阅消息【一直会收到通知】
- 订阅事件，该种使用方式不需要取消订阅
    ```
    private Observer<String> observer = new Observer<String>() {
        @Override
        public void onChanged(@Nullable String s) {
            Toast.makeText(ThirdActivity.this, s, Toast.LENGTH_SHORT).show();
        }
    };
    
    LiveDataBus.get()
            .with(Constant.LIVE_BUS, String.class)
            .observeForever(observer);
    ```
- 这个时候需要手动移除observer观察者
    ```
    LiveDataBus.get()
           .with(Constant.LIVE_BUS, String.class)
           .removeObserver(observer);
    ```
- 发送消息同上一样，需要注意，发送这种消息，需要手动移除观察者，它表示不管在什么状态下都会收到发出的消息事件。



#### 8.3 发送粘性事件消息
- 订阅事件，该种使用方式不需要取消订阅
    ```
    LiveDataBus.get()
            .with(Constant.YC_BUS, String.class)
            .observeSticky(this, new Observer<String>() {
                @Override
                public void onChanged(@Nullable String s) {
                    // 更新数据
                    BusLogUtils.d("接收消息--------yc_bus---8----"+s);
                }
            });
    ```
- 那么如何发布事件消息呢，代码如下
    ```
    LiveDataBus.get().with(Constant.YC_BUS).setValue("test_yc_data");
    LiveDataBus.get().with(Constant.YC_BUS).postValue("test_yc_data");
    ```
- observeForever模式订阅消息，需要调用removeObserver取消订阅
    ```
    LiveDataBus.get()
            .with(Constant.YC_BUS, String.class)
            .observeStickyForever(observer);
    ```


#### 8.4 如何发送延迟消息
- 该lib拥有延迟发送消息事件的功能，发送事件消息代码如下
    ```
    //延迟5秒发送事件消息
    LiveDataBus.get().with(Constant.LIVE_BUS).postValueDelay("test_data",5000);
    ```


#### 8.5 如何发送轮训延迟消息
- 这种场景主要应用在购物类的需求中，比如一个活动页面，每个5秒刷新一下接口数据更新页面活动
    ```
    //开始轮训
    LiveDataBus.get().with(Constant.LIVE_BUS5).postValueInterval("test_data",3000, "doubi");
    
    //停止轮训
    LiveDataBus.get().with(Constant.LIVE_BUS5).stopPostInterval("doubi");
    ```



### 09.事件消息系列博客
- [01.EventBus](https://github.com/yangchong211/YCLiveDataBus/blob/master/read/01.EventBus.md)
    - 01.EventBus简单介绍
    - 02.EventBus简单使用
    - 03.EventBus优缺点
    - 04.什么是发布/订阅模式
    - 05.EventBus实现原理
    - 06.EventBus重大问题
- [02.RxBus](https://github.com/yangchong211/YCLiveDataBus/blob/master/read/02.RxBus.md)
    - 01.RxBus是什么
    - 02.RxBus原理是什么
    - 03.RxBus简单实现
    - 04.RxBus优质库
    - 05.简单使用代码案例
- [03.LiveData简单介绍](https://github.com/yangchong211/YCLiveDataBus/blob/master/read/03.LiveData简单介绍.md)
    - 01.LiveData是什么东西
    - 02.为何要使用LiveData
    - 03.使用LiveData的优势
    - 04.使用LiveData的步骤
    - 05.简单使用LiveData
    - 06.observe()和observerForever()
    - 07.理解活跃状态更新数据
    - 08.setValue和postValue
- [04.LiveDataBus](https://github.com/yangchong211/YCLiveDataBus/blob/master/read/04.LiveDataBus.md)
    - 01.为何使用liveData
    - 02.LiveDataBus的组成
    - 03.LiveDataBus原理图
    - 04.简单的实现案例代码
    - 05.遇到的问题和分析思路
    - 06.使用反射解决遇到问题
    - 07.使用postValue的bug
    - 08.如何发送延迟事件消息
    - 09.如何发送轮训延迟事件
- [05.EventBus源码分析](https://github.com/yangchong211/YCLiveDataBus/blob/master/read/05.EventBus源码分析.md)
    - 01.EventBus注册源码解析
    - 02.EventBus事件分发解析
    - 03.EventBus取消注册解析
    - 04.总结一下EventBus的工作原理
- [06.RxBus源码分析](https://github.com/yangchong211/YCLiveDataBus/blob/master/read/06.RxBus源码分析.md)
    - 01.后续更新
- [07.LiveData源码分析](https://github.com/yangchong211/YCLiveDataBus/blob/master/read/07.LiveData源码分析.md)
    - 01.LiveData的原理介绍
    - 02.然后思考一些问题
    - 03.observe订阅源码分析
    - 04.setValue发送源码分析
    - 05.LiveData源码总结
    - 06.LiveData流程图绘制
- [08.Lifecycle源码分析](https://github.com/yangchong211/YCLiveDataBus/blob/master/read/08.Lifecycle源码分析.md)
- [09.观察者模式](https://github.com/yangchong211/YCLiveDataBus/blob/master/read/09.观察者模式.md)
- [10.事件总线封装库](https://github.com/yangchong211/YCLiveDataBus/blob/master/read/10.事件总线封装库.md)
- [11.问题思考大汇总](https://github.com/yangchong211/YCLiveDataBus/blob/master/read/11.问题思考大汇总.md)



### 10.遇到问题思考汇总


### 参考内容
- https://github.com/bennidi/mbassador
- https://github.com/zalando/nakadi
- https://github.com/JeremyLiao/SmartEventBus
- https://github.com/pwittchen/NetworkEvents
- https://github.com/sunyatas/NetStatusBus

























