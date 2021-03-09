package com.yc.api.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import com.yc.api.RouteConstants;
import com.yc.api.RouteImpl;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

@AutoService(Processor.class)
@SupportedAnnotationTypes(RouteConstants.INTERFACE_NAME_ROUTE_IMPL)
public class RouteImplProcessor extends AbstractProcessor {

    private Filer filer;       // File util, write class file into disk.
    private Elements elements;
    private MyAnAnnotationValueVisitor annotationValueVisitor;

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
        annotationValueVisitor = new MyAnAnnotationValueVisitor();
    }


    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        /*
         * 1. set：携带getSupportedAnnotationTypes()中的注解类型，一般不需要用到。
         * 2. roundEnvironment：processor将扫描到的信息存储到roundEnvironment中，从这里取出所有使用注解的字段。
         */
        if (set==null || set.size()==0){
            return false;
        }
        for (TypeElement typeElement : set) {
            Set<? extends Element> annotated = roundEnvironment.getElementsAnnotatedWith(typeElement);
            for (Element apiImplElement : annotated) {
                RouteImpl annotation = apiImplElement.getAnnotation(RouteImpl.class);
                if (annotation == null || !(apiImplElement instanceof TypeElement)) {
                    continue;
                }
                RouteContract<ClassName> apiNameContract = ElementTool.getApiClassNameContract(elements,
                        annotationValueVisitor,(TypeElement) apiImplElement);
                try {
                    JavaFile.builder(RouteConstants.PACKAGE_NAME_CONTRACT, buildClass(apiNameContract))
                            .build()
                            .writeTo(filer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    private TypeSpec buildClass(RouteContract<ClassName> apiNameContract) {
        //获取接口的路径
        String simpleName = apiNameContract.getApi().simpleName();
        if (RouteConstants.LOG){
            System.out.println("RouteImplProcessor--------buildClass-------simpleName---"+simpleName);
        }
        //获取 com.yc.api.IRouteContract 信息，也就是IRouteContract接口的路径
        TypeElement typeElement = elements.getTypeElement(RouteConstants.INTERFACE_NAME_CONTRACT);
        ClassName className = ClassName.get(typeElement);
        if (RouteConstants.LOG){
            System.out.println("RouteImplProcessor--------buildClass-------className---"+className);
        }
        //自定义注解生成类的类名
        //例如：
        String name = simpleName + RouteConstants.SEPARATOR + RouteConstants.CONTRACT;
        if (RouteConstants.LOG){
            System.out.println("RouteImplProcessor--------buildClass-------name---"+name);
        }
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

    private MethodSpec buildMethod(RouteContract<ClassName> apiNameContract) {
        ClassName api = apiNameContract.getApi();
        ClassName apiImpl = apiNameContract.getApiImpl();
        if (RouteConstants.LOG){
            System.out.println("RouteImplProcessor--------buildMethod-------api---"+api + "----apiImpl---" + apiImpl);
        }
        String format = RouteConstants.INSTANCE_NAME_REGISTER + "." +
                RouteConstants.METHOD_NAME_REGISTER + "($T.class, $T.class)";
        if (RouteConstants.LOG){
            System.out.println("RouteImplProcessor--------buildMethod-------format---"+format);
        }
        ParameterSpec parameterSpec = buildParameterSpec();
        return MethodSpec.methodBuilder(RouteConstants.METHOD_NAME_REGISTER)
                //添加注解
                .addAnnotation(Override.class)
                //添加修饰符
                .addModifiers(Modifier.PUBLIC)
                //添加参数规格
                .addParameter(parameterSpec)
                //添加函数体
                .addStatement(format, api,apiImpl)
                .build();
    }

    private ParameterSpec buildParameterSpec() {
        //获取 com.yc.api.IRegister 信息，也就是IRegister接口的路径
        TypeElement typeElement = elements.getTypeElement(RouteConstants.INTERFACE_TYPE_REGISTER);
        ClassName className = ClassName.get(typeElement);
        if (RouteConstants.LOG){
            System.out.println("RouteImplProcessor--------ParameterSpec-------className---"+className);
        }
        return ParameterSpec
                .builder(className, RouteConstants.INSTANCE_NAME_REGISTER)
                .build();
    }

}
