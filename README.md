# 组件化项目通信库
## 目录介绍
- 00.组件化项目通信库
- 01.组件事件通知库
- 02.组件之间的通信库
- 03.一些问题反馈记录
- 04.该库对应知识点
- 05.其他封装库介绍



### 00.组件化项目通信库
- 组件事件通知
    - 该库代码量少，利用LiveData实现事件总线，替代EventBus。充分利用了生命周期感知功能，可以在activities, fragments, 或者 services生命周期是活跃状态时更新这些组件。支持发送普通事件，也可以发送粘性事件；还可以发送延迟消息，以及轮训延迟消息等等。
- 组件之间的通信
    - 组件之间的通信，接口+实现类，使用注解生成代码方式，无需手动注册，将使用步骤简单化，支持组件间以暴露接口提供服务的方式进行通信。



### 01.组件事件通知库
#### 1.1 组件事件通知对比
- **对比结果如下所示**
    | 事件总线 | 发送粘性事件 | 是否有序接收消息 | 延迟发送 | 组建生命周期感知 | 跨线程发事件 |
    | :------ | :--------- | :------------- | :------ | :-------------- | :-------- | 
    | LiveDataBus | true  | true         | true         |true         |false         |
    | EventBus | true     | true          | false       |false        |false         |
    | RxBus | true        | false       | false         |false        |false         |
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


#### 1.2 LiveDataBus的组成
- **消息**： 消息可以是任何的 Object，可以定义不同类型的消息，如 Boolean、String。也可以定义自定义类型的消息。
- **消息通道**： LiveData 扮演了消息通道的角色，不同的消息通道用不同的名字区分，名字是 String 类型的，可以通过名字获取到一个 LiveData 消息通道。
- **消息总线**： 消息总线通过单例实现，不同的消息通道存放在一个 HashMap 中。
- **订阅**： 订阅者通过 with() 获取消息通道，然后调用 observe() 订阅这个通道的消息。
- **发布**： 发布者通过 with() 获取消息通道，然后调用 setValue() 或者 postValue() 发布消息。



#### 1.3 如何使用事件通知api
- 依赖代码如下所示
    ```
    implementation 'cn.yc:LiveBusLib:1.0.3'
    ```

##### 1.3.1 最简单常见的发布/订阅事件消息
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


##### 1.3.2 Forever模式订阅和取消订阅消息【一直会收到通知】
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



##### 1.3.3 发送粘性事件消息
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


##### 1.3.4 如何发送延迟消息
- 该lib拥有延迟发送消息事件的功能，发送事件消息代码如下
    ```
    //延迟5秒发送事件消息
    LiveDataBus.get().with(Constant.LIVE_BUS).postValueDelay("test_data",5000);
    ```


##### 1.3.5 如何发送轮训延迟消息
- 这种场景主要应用在购物类的需求中，比如一个活动页面，每个5秒刷新一下接口数据更新页面活动
    ```
    //开始轮训
    LiveDataBus.get().with(Constant.LIVE_BUS5).postValueInterval("test_data",3000, "doubi");
    
    //停止轮训
    LiveDataBus.get().with(Constant.LIVE_BUS5).stopPostInterval("doubi");
    ```



### 02.组件之间的通信库
#### 2.1 老版组件通信实践
- 比如业务组件划分
    - 组件A，组件B，组件C，接口通信组件【被各个业务组件依赖】
- 通信组件几个主要类
    - BusinessTransfer，主要是map集合中get获取和put添加接口类的对象，利用反射机制创建实例对象。
    - IUpdateManager，该类是版本更新接口类，定义更新抽象方法
    - UpdateManagerImpl，该类是IUpdateManager接口实现类，主要是具体业务逻辑的实现
- 主要实现的代码如下所示
    ``` java
    //接口
    public interface IUpdateManager extends Serializable {
  
        void checkUpdate(UpdateManagerCallBack updateManagerCallBack);
    
        interface UpdateManagerCallBack {
            void updateCallBack(boolean isNeedUpdate);
        }
    }
    
    //接口实现类
    public class UpdateManagerImpl implements IUpdateManager {
        @Override
        public void checkUpdate(UpdateManagerCallBack updateManagerCallBack) {
            try {
                IConfigService configService = DsxxjServiceTransfer.$().getConfigureService();
                String data = configService.getConfig(KEY_APP_UPDATE);
                if (TextUtils.isEmpty(data)) {
                    if (updateManagerCallBack != null) {
                        updateManagerCallBack.updateCallBack(false);
                    }
                    return;
                }
                ForceUpdateEntity xPageUpdateEntity = JSON.parseObject(data, ForceUpdateEntity.class);
                ForceUpdateManager.getInstance().checkForUpdate(xPageUpdateEntity, updateManagerCallBack);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    //如何使用
    //在初始化时注入
    BusinessTransfer businessTransfer = BusinessTransfer.$();
    businessTransfer.setImpl(BusinessTransfer.BUSINESS_IMPL_UPDATE_MANAGER,
            PACKAGE_NAME + ".base.businessimpl.UpdateManagerImpl");
    
    //版本更新
    BusinessTransfer.$().getUpdate().checkUpdate(new IUpdateManager.UpdateManagerCallBack() {
        @Override
        public void updateCallBack(boolean isNeedUpdate) {
            
        }
    });
    ```
- 这种方式存在几个问题
    - 1.注入的时候要填写正确的包名，否则在运行期会出错，且不容易找到；
    - 2.针对接口实现类，不能混淆，否则会导致反射找不到具体的类，因为是根据类的全路径反射创建对象；
    - 3.每次添加新的接口通信，都需要手动去注入到map集合，稍微有点麻烦，能否改为自动注册呢？
    - 4.每次还要在Transfer的类中，添加获取该接口对象的方法，能否自动一点？
    - 5.可能出现空指针，一旦忘记没有注入或者反射创建对象失败，则直接导致崩溃……



#### 2.2 封装库有何特点
- 注解生成代码自动注册
    - 使用apt注解在编译阶段生成服务接口与实现的映射注册帮助类，其实这部分就相当于是替代了之前在application初始化注入的步骤，获取服务时自动使用帮助类完成注册，不必手动调用注册方法。
- 避免空指针崩溃
    - 无服务实现注册时，使用空对象模式 + 动态代理的设计提前暴露调用错误，主要抛出异常，在测试时就发现问题，防止空指针异常。
- 代码入侵性低
    - 无需改动之前的代码，只需要在之前的接口和接口实现类按照约定添加注解规范即可。其接口+接口实现类还是用之前的，完全无影响……
- 按照你需要来加载
    - 首次获取接口服务的时候，用反射生成映射注册帮助类的实例，再返回实现的实例。
- 丰富的代码案例
    - 代码案例丰富，提供丰富的案例，然后多个业务场景，尽可能完善好demo。


#### 2.3 如何使用该库Api
- 在module项目中添加依赖
    ``` java
    implementation project(path: ':module-manager')
    annotationProcessor project(path: ':module-compiler')
    ```
- 在module通信组件中定义接口，注意需要继承IRouteApi接口
    ``` java
    public interface IUpdateManager extends IRouteApi {

        void checkUpdate(UpdateManagerCallBack updateManagerCallBack);
    
        interface UpdateManagerCallBack {
            void updateCallBack(boolean isNeedUpdate);
        }
    
    }
    ```
- 在需要实现服务的组件中写接口实现类，注意需要添加注解
    ``` java
    @RouteImpl(IUpdateManager.class)
    public class UpdateImpl implements IUpdateManager {
        @Override
        public void checkUpdate(UpdateManagerCallBack updateManagerCallBack) {
            //省略
        }
    }
    ```
- 如何获取服务的实例对象
    ``` java
    //无返回值的案例
    //设置监听
    IUpdateManager iUpdateManager = TransferManager.getInstance().getApi(IUpdateManager.class);
    iUpdateManager.checkUpdate(new IUpdateManager.UpdateManagerCallBack() {
       @Override
       public void updateCallBack(boolean isNeedUpdate) {
           
       }
    });
    
    //有返回值的案例
    userApi = TransferManager.getInstance().getApi(IUserManager.class);
    String userInfo = userApi.getUserInfo();
    ```






### 05.其他封装库介绍
- [1.开源博客汇总](https://github.com/yangchong211/YCBlogs)
- [2.组件化实践项目](https://github.com/yangchong211/LifeHelper)
- [3.视频播放器封装库](https://github.com/yangchong211/YCVideoPlayer)
- [4.状态切换管理器封装库](https://github.com/yangchong211/YCStateLayout)
- [5.复杂RecyclerView封装库](https://github.com/yangchong211/YCRefreshView)
- [6.弹窗封装库](https://github.com/yangchong211/YCDialog)
- [7.版本更新封装库](https://github.com/yangchong211/YCUpdateApp)
- [8.状态栏封装库](https://github.com/yangchong211/YCStatusBar)
- [9.轻量级线程池封装库](https://github.com/yangchong211/YCThreadPool)
- [10.轮播图封装库](https://github.com/yangchong211/YCBanner)
- [11.音频播放器](https://github.com/yangchong211/YCAudioPlayer)
- [12.画廊与图片缩放控件](https://github.com/yangchong211/YCGallery)
- [13.Python多渠道打包](https://github.com/yangchong211/YCWalleHelper)
- [14.整体侧滑动画封装库](https://github.com/yangchong211/YCSlideView)
- [15.Python爬虫妹子图](https://github.com/yangchong211/YCMeiZiTu)
- [17.自定义进度条](https://github.com/yangchong211/YCProgress)
- [18.自定义折叠和展开布局](https://github.com/yangchong211/YCExpandView)
- [19.商品详情页分页加载](https://github.com/yangchong211/YCShopDetailLayout)
- [20.在任意View控件上设置红点控件](https://github.com/yangchong211/YCRedDotView)
- [21.仿抖音一次滑动一个页面播放视频库](https://github.com/yangchong211/YCScrollPager)



### 06.关于LICENSE
```
Copyright 2017 yangchong211（github.com/yangchong211）

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```





















