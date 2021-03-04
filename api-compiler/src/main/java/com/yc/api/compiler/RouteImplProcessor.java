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
@SupportedAnnotationTypes("com.yc.api.RouteImpl")
public class RouteImplProcessor extends AbstractProcessor {

    private Filer filer;       // File util, write class file into disk.
    private Elements elements;
    private MyAnAnnotationValueVisitor annotationValueVisitor;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnv.getFiler();                  // Generate class.
        elements = processingEnv.getElementUtils();      // Get class meta.
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
        String simpleName = apiNameContract.getApi().simpleName();
        TypeElement typeElement = elements.getTypeElement(RouteConstants.INTERFACE_NAME_CONTRACT);
        ClassName className = ClassName.get(typeElement);
        return TypeSpec.classBuilder(simpleName + RouteConstants.SEPARATOR + RouteConstants.CONTRACT)
                .addSuperinterface(className)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(buildMethod(apiNameContract))
                .build();
    }

    private MethodSpec buildMethod(RouteContract<ClassName> apiNameContract) {
        ClassName api = apiNameContract.getApi();
        ClassName apiImpl = apiNameContract.getApiImpl();
        return MethodSpec.methodBuilder(RouteConstants.METHOD_NAME_REGISTER)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(buildParameterSpec())
                .addStatement(RouteConstants.INSTANCE_NAME_REGISTER + "." +
                        RouteConstants.METHOD_NAME_REGISTER + "($T.class, $T.class)", api,apiImpl )
                .build();
    }

    private ParameterSpec buildParameterSpec() {
        TypeElement typeElement = elements.getTypeElement(RouteConstants.INTERFACE_TYPE_REGISTER);
        ClassName className = ClassName.get(typeElement);
        return ParameterSpec
                .builder(className, RouteConstants.INSTANCE_NAME_REGISTER)
                .build();
    }

}
