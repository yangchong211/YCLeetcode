# 组件通信注解框架实践
#### 目录介绍
- 01.为何需要组件间通信
- 02.实现同级组件通信方式
- 03.先看一个简单的案例
- 04.项目组件通信流程
- 05.逆向简化注册流程
- 06.这个注解是做什么的
- 07.注解是如何生成代码
- 08.如何定义注解处理器
- 09.项目库的设计和完善
- 10.封装该库有哪些特点
- 11.一些常见的报错问题
- 12.部分原理分析的说明



### 01.为何需要组件间通信
- 明确一个前提：各个业务组件之间不会是相互隔离而是必然存在一些交互的；
    - 业务复用：在Module A需要引用Module B提供的某个功能，比如需要版本更新业务逻辑，而我们一般都是使用强引用的Class显式的调用；
    - 业务复用：在Module A需要调用Module B提供的某个方法，例如别的Module调用用户模块退出登录的方法；
    - 业务获取参数：登陆环境下，在Module A，C，D，E多个业务组件需要拿到Module B登陆注册组件中用户信息id，name，info等参数访问接口数据；
- 这几种调用形式大家很容易明白，正常开发中大家也是毫不犹豫的调用。但是在组件化开发的时候却有很大的问题：
    - 由于业务组件之间没有相互依赖，组件Module B的Activity Class在自己的Module中，那Module A必然引用不到，这样无法调用类的功能方法；由此：必然需要一种支持组件化需求的交互方式，提供平行级别的组件间调用函数通信交互的功能。
- 项目库开源地址
    - https://github.com/yangchong211/YCLiveDataBus


### 02.实现同级组件通信方式
- 至于关于页面跳转
    - 那肯定是首选路由，比如阿里的ARouter。但是涉及到组件之间业务复用，业务逻辑的交互等等，就有点难搞了……那该怎么处理比较方便呢？
- 组件业务逻辑交互通信
    - 比如业务组件层划分
        - 组件A，组件B，组件C，组件D，组件E等等，这些业务组件并不是相互依赖，它们之间是相同的层级！
    - 举一个业务案例
        - 比如有个选择用户学员的弹窗，代码写到了组件A中，这个时候组件C和组件D需要复用组件A中的弹窗，该业务逻辑如何处理？
        - 比如组件E是我的用户相关的业务逻辑，App登陆后，组件B和组件C需要用到用户的id去请求接口，这个时候如何获取组件E中用户id呢？
    - 该层级下定义一个公共通信组件
        - 接口通信组件【被各个业务组件依赖】，该相同层级的其他业务组件都需要依赖这个通信组件。这个时候各个模块都可以拿到通信组件的类……
- 需要具备的那些特点
    - 使用简单方便，避免同级组件相互依赖。代码入侵性要低，支持业务交互，自动化等特性。



### 03.先看一个简单的案例
- 先说一下业务场景
    - 版本更新业务组件(处理更新弹窗，apk下载，apk的md5校验，安装等逻辑，还涉及到一些业务逻辑，比如更新模式普通或者强更，还有渠道，还有时间段等)
    - 主模块首页，我的组件，设置中心组件等多个module组件中都会用到版本更新功能，除了主模块外，其他组件没有依赖版本更新组件，那么如何调用里面的更新弹窗业务逻辑呢？
- 创建一个接口通信组件
    - 如上所示，各个同级的业务组件，A，B，C，D等都依赖该接口通信组件。那么这样就会拿到通信组件的类，为了实现通信交互。可以在该接口通信组件中定义接口并暴露抽象更新弹窗方法，那么在版本更新组件中写接口实现类。
    - 创建一个map集合，存储实现类的全路径，然后put到map集合中；这样可以get拿到实现类的路径，就可以利用反射创建实例对象。
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
    //在初始化时注入，建议放在application中设置，调用setImpl其实就是把路径字符串put到map集合中
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
- 反射创建接口的实现类对象
    ``` java
    String className = implsMap.get(key);
    try {
        return (T) Class.forName(className).newInstance();
    } catch (InstantiationException e) {
        e.printStackTrace();
    }
    ```
- 这种方式存在几个问题
    - 1.注入的时候要填写正确的包名，否则在运行期会出错，且不容易找到；
    - 2.针对接口实现类，不能混淆，否则会导致反射找不到具体的类，因为是根据类的全路径反射创建对象；所以每次写一个接口+实现类，都要在混淆文件中添加一下，比较麻烦……
    - 3.每次添加新的接口通信，都需要手动去注入到map集合，稍微有点麻烦，能否改为自动注册呢？
    - 4.每次还要在Transfer的类中，添加获取该接口对象的方法，能否自动一点？
    - 5.可能出现空指针，一旦忘记没有注入或者反射创建对象失败，则直接导致崩溃……



### 04.项目组件通信流程
- 具体实现方案
    - 比方说，主app中的首页有版本更新，业务组件用户中心的设置页面也有版本更新，而版本升级的逻辑是写在版本更新业务组件中。这个时候操作如下所示
    - ![image](https://img-blog.csdnimg.cn/20200426093500838.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L20wXzM3NzAwMjc1,size_16,color_FFFFFF,t_70)



### 05.逆向简化注册流程
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
- 关于get/put主要是存属什么呢
    ``` java
    /**
     * key表示的是自定义通信接口
     * value表示自定义通信接口的实现类
     */
    private Map<Class, Class> apiImplementMap = new HashMap<>();
    ```
- 代码混淆
    ``` java
    -keep class com.yc.api.**{*;}
    -keep public class * implements com.yc.api.** { *; }
    ```
- 不需要在额外添加通信接口实现类的混淆代码
    - 因为用到了反射，而且是用Class.forName(name)创建反射对象。所以必须保证name路径是正确的，否则找不到类。
    - 该库，你定义的实现类已经继承了我定义的接口，因为针对继承com.yc.api.**的子类，会忽略混淆。已经处理……所以不需要你额外处理混淆问题！



### 06.这个注解是做什么的
- 这个注解有什么用呢
    - 框架会在项目的编译器扫描所有添加@RouteImpl注解的XxxImpl接口实现类，然后传入接口类的class对象。这样就可以通过注解拿到接口和接口的实现类……
- apt编译后生成的代码
    - build--->generated--->ap_generated_sources--->debug---->out---->com.yc.api.contract
    - 这段代码什么意思：编译器生成代码，并且该类是继承自己自定义的接口，调用IRegister接口中的register方法，key是接口class，value是接口实现类class，直接在编译器把接口和实现类存储起来。用的时候直接取……
    ``` java
    public class IUpdateManager$$Contract implements IRouteContract {
      @Override
      public void register(IRegister register) {
        register.register(IUpdateManager.class, UpdateImpl.class);
      }
    }
    ```
    - ![image](https://img-blog.csdnimg.cn/20210305111650620.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L20wXzM3NzAwMjc1,size_16,color_FFFFFF,t_70)



### 07.注解是如何生成代码
- 如何拿到注解标注的类，看个案例
    ``` java
    @RouteImpl(IUserInfoManager.class)
    public class Test implements IUserInfoManager {
        @Override
        public String getUserId() {
            return null;
        }
    }
    
    private void test(){
        //这个地方先写个假的业务代码，实际apt中是通过roundEnvironment对象拿到注解标记的类
        Class c = Test.class;
        //Set<? extends Element> annotated = roundEnvironment.getElementsAnnotatedWith(typeElement);
        //找到修饰了注解RouteImpl的类
        RouteImpl annotation = (RouteImpl) c.getAnnotation(RouteImpl.class);
        if (annotation != null) {
            try {
                //获取ContentView的属性值
                Class value = annotation.value();
                String name = value.getName();
                System.out.println("注解标记的类名"+name);
            } catch (RuntimeException e) {
                e.printStackTrace();
                System.out.println("注解标记的类名"+e.getMessage());
            }
        }
    }
    ```
- 手动编程还是自动生成
    - 在代码的编写过程中自己手动实现，也可以通过apt生成。作为一个框架，当然是自动解析RouteImpl注解然后生成这些类文件更好了。要想自动生成代码的映射关系，那么便要了解apt和javapoet了。



### 08.如何定义注解处理器
- apt工具了解一下
    - APT是Annotation Processing Tool的简称,即注解处理工具。它是在编译期对代码中指定的注解进行解析，然后做一些其他处理（如通过javapoet生成新的Java文件）。
- 定义注解处理器
    - 用来在编译期扫描加入@RouteImpl注解的类，然后做处理。这也是apt最核心的一步，新建RouteImplProcessor 继承自 AbstractProcessor,然后实现process方法。在项目编译期会执行RouterProcessor的process()方法，我们便可以在这个方法里处理RouteImpl注解了。
- 初始化自定义Processor
    ``` java
    @AutoService(Processor.class)
    public class RouteImplProcessor extends AbstractProcessor {
    
    }
    ```
- 在init方法中初始化获取文件生成器信息
    ``` java
    /**
     * 初始化方法
     * @param processingEnvironment                 获取信息
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        //文件生成器 类/资源
        filer = processingEnv.getFiler();
        //节点工具类 (类、函数、属性都是节点)
        elements = processingEnv.getElementUtils();
    }
    ```
- 在process方法中拿到注解标记的类信息
    ``` java
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        for (TypeElement typeElement : set) {
            Set<? extends Element> annotated = roundEnvironment.getElementsAnnotatedWith(typeElement);
            for (Element apiImplElement : annotated) {
                //被 RouteImpl 注解的节点集合
                RouteImpl annotation = apiImplElement.getAnnotation(RouteImpl.class);
                if (annotation == null || !(apiImplElement instanceof TypeElement)) {
                    continue;
                }
                ApiContract<ClassName> apiNameContract = ElementTool.getApiClassNameContract(elements,
                        annotationValueVisitor,(TypeElement) apiImplElement);
                if (RouteConstants.LOG){
                    System.out.println("RouteImplProcessor--------process-------apiNameContract---"+apiNameContract);
                }
            }
        }
        return true;
    }
    ```
- 然后生成代码，主要是指定生成代码路径，然后创建typeSpec注解生成代码。
    - 这个javapoet工具，目前还紧紧是套用ARouter，创建类名，添加接口，添加注解，添加方法，添加修饰符，添加函数体等等。也就是说将一个类代码拆分成n个部分，然后逆向拼接到一起。最后去write写入代码……
    ``` java
    //生成注解类相关代码
    TypeSpec typeSpec = buildClass(apiNameContract);
    String s = typeSpec.toString();
    if (RouteConstants.LOG){
        System.out.println("RouteImplProcessor--------process-------typeSpec---"+s);
    }
    try {
        //指定路径：com.yc.api.contract
        JavaFile.builder(RouteConstants.PACKAGE_NAME_CONTRACT, typeSpec)
                .build()
                .writeTo(filer);
    } catch (IOException e) {
        e.printStackTrace();
    }
    ```
- 来看看怎么创建注解类
    - 大概思路就是，将我们平时的类，拆分，然后拼接成实体。ParameterSpec是创建参数的实现，MethodSpec是函数的生成实现等等……
    ```
    private TypeSpec buildClass(ApiContract<ClassName> apiNameContract) {
        String simpleName = apiNameContract.getApi().simpleName();
        //获取 com.yc.api.IRouteContract 信息，也就是IRouteContract接口的路径
        TypeElement typeElement = elements.getTypeElement(RouteConstants.INTERFACE_NAME_CONTRACT);
        ClassName className = ClassName.get(typeElement);
        String name = simpleName + RouteConstants.SEPARATOR + RouteConstants.CONTRACT;
        //这里面又有添加方法注解，添加修饰符，添加参数规格，添加函数题，添加返回值等等
        MethodSpec methodSpec = buildMethod(apiNameContract);
        //创建类名
        return TypeSpec.classBuilder(name)
                //添加super接口
                .addSuperinterface(className)
                //添加修饰符
                .addModifiers(Modifier.PUBLIC)
                //添加方法【然后这里面又有添加方法注解，添加修饰符，添加参数规格，添加函数题，添加返回值等等】
                .addMethod(methodSpec)
                //创建
                .build();
    }
    ```



### 09.项目库的设计和完善
- ModuleBus主要由三部分组成，包括对外提供的api调用模块、注解模块以及编译时通过注解生产相关的类模块。
    - api-compiler 编译期解析注解信息并生成相应类以便进行注入的模块
    - api-manager 注解的声明和信息存储类的模块，以及开发调用的api功能和具体实现
- 编译生成代码发生在编译器
    - 编译期是在项目编译的时候，这个时候还没有开始打包，也就是没有生成apk呢！框架在这个时期根据注解去扫描所有文件，然后生成路由映射文件。这些文件都会统一打包到apk里！
- 无需初始化操作
    - 先看ARouter，会有初始化，主要是收集路由映射关系文件，在程序启动的时候扫描这些生成的类文件，然后获取到映射关系信息，保存起来。这个封装库不需要初始化，简化步骤，在获取的时候如果没有则在put操作map集合。具体看代码！




### 10.封装该库有哪些特点
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




### 11.一些常见的报错问题
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



### 12.部分原理分析的说明
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


### 项目地址：https://github.com/yangchong211/YCLiveDataBus
