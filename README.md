#### 目录介绍
- 01.该库是做什么用的
- 02.项目组件通信流程
- 03.老版组件通信实践
- 04.封装库有何特点
- 05.如何使用该库Api
- 06.关于封装思路分析
- 07.关于其他知识点
- 08.关于添加混淆代码
- 09.关于几个重要问题




### 01.该库是做什么用的
- 组件之间的通信，接口+实现类，使用注解生成代码方式，无需手动注册，将使用步骤简单化，支持组件间以暴露接口提供服务的方式进行通信。



### 02.项目组件通信流程
- 组件通信需求分析
    - 比如业务组件层划分
        - 组件A，组件B，组件C，组件D，组件E等等，这些业务组件并不是相互依赖，它们之间是相同的层级！
    - 举一个业务案例
        - 比如有个选择用户学员的弹窗，代码写到了组件A中，这个时候组件C和组件D需要复用组件A中的弹窗，该业务逻辑如何处理？
        - 比如组件E是我的用户相关的业务逻辑，App登陆后，组件B和组件C需要用到用户的id去请求接口，这个时候如何获取组件E中用户id呢？
    - 该层级下定义一个公共通信组件
        - 接口通信组件【被各个业务组件依赖】，该相同层级的其他业务组件都需要依赖这个通信组件。这个时候各个模块都可以拿到通信组件的类……
- 具体实现方案
    - 比方说，主app中的首页有版本更新，业务组件用户中心的设置页面也有版本更新，而版本升级的逻辑是写在版本更新业务组件中。这个时候操作如下所示
    - ![image](https://img-blog.csdnimg.cn/20200426093500838.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L20wXzM3NzAwMjc1,size_16,color_FFFFFF,t_70)



### 03.老版组件通信实践
- 通信组件几个主要类
    - BusinessTransfer，主要是map集合中get获取和put添加接口类的对象，利用反射机制创建实例对象。**该类放到通信组件中**。
    - IUpdateManager，该类是版本更新接口类，定义更新抽象方法。**该类放到通信组件中**。
    - UpdateManagerImpl，该类是IUpdateManager接口实现类，主要是具体业务逻辑的实现。**该类放到具体实现库代码中，比如我的组件**。
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
    ```
- 那么如何调用呢？可以在各个组件中调用，代码如下所示……
    ``` java
    //版本更新
    BusinessTransfer.$().getUpdate().checkUpdate(new IUpdateManager.UpdateManagerCallBack() {
        @Override
        public void updateCallBack(boolean isNeedUpdate) {
            
        }
    });
    ```
- 这种方式存在几个问题
    - 1.注入的时候要填写正确的包名，否则在运行期会出错，且不容易找到；
    - 2.针对接口实现类，不能混淆，否则会导致反射找不到具体的类，因为是根据类的全路径反射创建对象；所以每次写一个接口+实现类，都要在混淆文件中添加一下，比较麻烦……
    - 3.每次添加新的接口通信，都需要手动去注入到map集合，稍微有点麻烦，能否改为自动注册呢？
    - 4.每次还要在Transfer的类中，添加获取该接口对象的方法，能否自动一点？
    - 5.可能出现空指针，一旦忘记没有注入或者反射创建对象失败，则直接导致崩溃……



### 04.封装库有何特点
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
- 该库注解生成代码在编译器
    - 在编译器生成代码，并且该类是继承自己自定义的接口，存储的是map集合，key是接口class，value是接口实现类class，直接在编译器把接口和实现类存储起来。用的时候直接取……



### 05.如何使用该库Api
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
- 看一下编译器生成的代码
    - build--->generated--->ap_generated_sources--->debug---->out---->com.yc.api.contract
    - 这段代码什么意思：编译器生成代码，并且该类是继承自己自定义的接口，存储的是map集合，key是接口class，value是接口实现类class，直接在编译器把接口和实现类存储起来。用的时候直接取……
    ``` java
    public class IUpdateManager$$Contract implements IRouteContract {
      @Override
      public void register(IRegister register) {
        register.register(IUpdateManager.class, UpdateImpl.class);
      }
    }
    ```
    - ![image](https://img-blog.csdnimg.cn/20210305111650620.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L20wXzM3NzAwMjc1,size_16,color_FFFFFF,t_70)



### 06.关于封装思路分析
- ModuleBus主要由三部分组成，包括对外提供的api调用模块、注解模块以及编译时通过注解生产相关的类模块。
    - api-compiler 编译期解析注解信息并生成相应类以便进行注入的模块
    - api-manager 注解的声明和信息存储类的模块，以及开发调用的api功能和具体实现
- 编译生成代码发生在编译器
    - 编译期是在项目编译的时候，这个时候还没有开始打包，也就是没有生成apk呢！框架在这个时期根据注解去扫描所有文件，然后生成路由映射文件。这些文件都会统一打包到apk里！
- 无需初始化操作
    - 先看ARouter，会有初始化，主要是收集路由映射关系文件，在程序启动的时候扫描这些生成的类文件，然后获取到映射关系信息，保存起来。这个封装库不需要初始化，简化步骤，在获取的时候如果没有则在put操作map集合。具体看代码！





### 07.关于其他知识点
- Didn't find class "com.yc.api.contract.IUserManager$$Contract" on path
    - 注解生成的代码失败导致出现这个问题。为什么会出现这种情况？修改gradle的构建版本……
    ``` java
    public class IUpdateManager$$Contract implements IApiContract {
      @Override
      public void register(IRegister register) {
        register.register(IUpdateManager.class, UpdateImpl.class);
      }
    }
    ```
- 关于apt编译器不能生成代码的问题，可能会有这么一些关键点
    - 第一查看module的依赖，如果没有依赖请先添加依赖
    ``` java
    implementation project(path: ':api-manager')
    annotationProcessor project(path: ':api-compiler')
    ```
    - 第二查看写完wirter的流没有关闭，会造成生成文件，但文件内容为空，或者不全；
    - 第三可能是Android Gradle及构建版本问题，我的是3.4.1 + 5.2.1，会出现不兼容的情况，大神建议3.3.2 ＋ 4.10.1以下都可以。听了建议降低版本果然构建编译，新的文件生成了。



### 08.关于添加混淆代码
- 代码混淆
    ``` java
    -keep class com.yc.api.**{*;}
    -keep public class * implements com.yc.api.** { *; }
    ```
- 不需要在额外添加通信接口实现类的混淆代码
    - 为什么，因为针对继承com.yc.api.**的子类，会忽略混淆。已经处理……



### 09.关于几个重要问题
- 注解是如何生成代码的？也就是javapoet原理……
    - 这个javapoet工具，目前还紧紧是套用ARouter，创建类名，添加接口，添加注解，添加方法，添加修饰符，添加函数体等等。也就是说将一个类代码拆分成n个部分，然后逆向拼接到一起。最后去write写入代码……
    - 但是，怎么拼接和并且创建.java文件的原理，待完善。目前处于会用……
- Class.forName(name)反射如何找到name路径的这个类，从jvm层面分析？
    - 待完善
- new和Class.forName("").newInstance()创建对象有何区别？
    ``` java
    A a = (A)Class.forName("com.yc.demo.impl.UpdateImpl").newInstance();
    A a = new A()；
    ```
    - 它们的区别在于创建对象的方式不一样，前者(newInstance)是使用类加载机制，后者(new)是创建一个新类。
    - 为什么会有两种创建对象方式？
        - 主要考虑到软件的可伸缩、可扩展和可重用等软件设计思想。
    - 从JVM的角度上看：
        - 我们使用关键字new创建一个类的时候，这个类可以没有被加载。但是使用newInstance()方法的时候，就必须保证：1、这个类已经加载；2、这个类已经连接了。
        - 而完成上面两个步骤的正是Class的静态方法forName()所完成的，这个静态方法调用了启动类加载器，即加载 java API的那个加载器。
        - 现在可以看出，newInstance()实际上是把new这个方式分解为两步，即首先调用Class加载方法加载某个类，然后实例化。 这样分步的好处是显而易见的。我们可以在调用class的静态加载方法forName时获得更好的灵活性，提供给了一种降耦的手段。
    - 区别
        - 首先，newInstance( )是一个方法，而new是一个关键字；其次，Class下的newInstance()的使用有局限，因为它生成对象只能调用无参的构造函数，而使用 new关键字生成对象没有这个限制。



### 10.其他封装库介绍
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



### 11.关于LICENSE
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
