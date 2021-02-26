package com.yc.api.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import com.yc.api.ApiConstants;
import com.yc.api.ApiImpl;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleAnnotationValueVisitor8;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.yc.api.ApiImpl")
public class ApiImplProcessor extends AbstractProcessor {

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
        if (set==null || set.size()==0){
            return false;
        }
        for (TypeElement typeElement : set) {
            Set<? extends Element> annotated = roundEnvironment.getElementsAnnotatedWith(typeElement);
            for (Element apiImplElement : annotated) {
                ApiImpl annotation = apiImplElement.getAnnotation(ApiImpl.class);
                if (annotation == null || !(apiImplElement instanceof TypeElement)) {
                    continue;
                }
                ApiContract<ClassName> apiNameContract = getApiClassNameContract((TypeElement) apiImplElement);
                try {
                    JavaFile.builder(ApiConstants.PACKAGE_NAME_CONTRACT, buildClass(apiNameContract))
                            .build()
                            .writeTo(filer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    private ApiContract<ClassName> getApiClassNameContract(TypeElement apiImplElement) {
        String apiClassSymbol = null;
        List<? extends AnnotationMirror> annotationMirrors = apiImplElement.getAnnotationMirrors();
        for (AnnotationMirror annotationMirror : annotationMirrors) {
            Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = annotationMirror.getElementValues();
            for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : elementValues.entrySet()) {
                apiClassSymbol = entry.getValue().accept(annotationValueVisitor, null);
            }
        }
        ClassName apiName = ClassName.get(elements.getTypeElement(apiClassSymbol));
        ClassName apiImplName = ClassName.get(apiImplElement);
        return new ApiContract<>(apiName, apiImplName);
    }

    private TypeSpec buildClass(ApiContract<ClassName> apiNameContract) {
        return TypeSpec.classBuilder(apiNameContract.getApi().simpleName() + ApiConstants.SEPARATOR + ApiConstants.CONTRACT)
                .addSuperinterface(ClassName.get(elements.getTypeElement(ApiConstants.INTERFACE_NAME_CONTRACT)))
                .addModifiers(Modifier.PUBLIC)
                .addMethod(buildMethod(apiNameContract))
                .build();
    }

    private MethodSpec buildMethod(ApiContract<ClassName> apiNameContract) {
        return MethodSpec.methodBuilder(ApiConstants.METHOD_NAME_REGISTER)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(buildParameterSpec())
                .addStatement(ApiConstants.INSTANCE_NAME_REGISTER + "." + ApiConstants.METHOD_NAME_REGISTER + "($T.class, $T.class)", apiNameContract.getApi(), apiNameContract.getApiImpl())
                .build();
    }

    private ParameterSpec buildParameterSpec() {
        return ParameterSpec
                .builder(ClassName.get(elements.getTypeElement(ApiConstants.INTERFACE_TYPE_REGISTER)), ApiConstants.INSTANCE_NAME_REGISTER)
                .build();
    }

    public class MyAnAnnotationValueVisitor extends SimpleAnnotationValueVisitor8<String, Void> {
        @Override
        public String visitType(TypeMirror typeMirror, Void o) {
            return typeMirror.toString();
        }
    }
}
