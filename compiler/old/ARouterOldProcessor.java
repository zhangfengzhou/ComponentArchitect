package com.luckyboy.compiler;

import com.google.auto.service.AutoService;
import com.luckyboy.annotation.ARouter;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;


@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.luckyboy.annotation.ARouter"})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedOptions("content")
public class ARouterOldProcessor extends AbstractProcessor {

    // 操作Element 工具类
    private Elements elementUtils;

    // type(类信息) 工具类
    private Types typeUtils;

    // 用来输出警告，错误等信息
    private Messager messager;

    // 文件生成器
    private Filer filer;

    // 初始化工作 文件生辰器
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();

        String content = processingEnvironment.getOptions().get("content");
        messager.printMessage(Diagnostic.Kind.WARNING, content);
    }


    /**
     * 相当于main函数 开始处理注解
     * 注解处理器的核心方法 处理具体的注解 生成Java文件
     *
     * @param set              使用了支持处理注解的节点集合（类 上面写了注解）
     * @param roundEnvironment 当前或者是之前的运行环境 可以通过该对象查找到的注解
     * @return true 表示后续处理器不会再处理（已经处理完成）
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        // 没有任何节点使用注解
        if (set.isEmpty()) {
            return false;
        }
        // 获取项目中所有使用了ARouter注解的节点
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(ARouter.class);
        // 遍历素有的类节点
        for (Element element : elements) {
            // 类节点之上 就是包节点
            String packageName = elementUtils.getPackageOf(element).getQualifiedName().toString();
            // 获取简单类名
            String className = element.getSimpleName().toString();
            messager.printMessage(Diagnostic.Kind.WARNING, "被注解的类名 " + className);
            // 最终生成我们想要生成的类文件 如： MainActivity$$Router
            // 可以先写一个模拟的类 然后根据模拟的类来生成文件

            String finalClassName = className + "$$Router";
            try {
                JavaFileObject sourceFile = filer.createSourceFile(packageName + "." + finalClassName);
                Writer writer = sourceFile.openWriter();
                // 设置包名
                writer.write("package " + packageName + ";\n");

                writer.write("public class " + finalClassName + " {\n");

                writer.write("public static Class<?> findTargetClass(String path) {\n");

                // 获取类之上@ARouter注解的path值
                ARouter aRouter = element.getAnnotation(ARouter.class);

                writer.write("if (path.equalsIgnoreCase(\"" + aRouter.path() + "\")) {\n");

                writer.write("return " + className + ".class;\n }\n");

                writer.write("return null;\n");

                writer.write("}\n}");

                // 非常重要
                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 返回false 就表示其他注解处理器任然可以处理@ARouter注解
        // 返回true 表示其他注解处理器就不能再处理@ARouter注解
        return false;
    }


    // 获取支持的注解类型
//    @Override
//    public Set<String> getSupportedAnnotationTypes() {
//        return super.getSupportedAnnotationTypes();
//    }


    // 需要什么JDK版本来编译生成文件
//    @Override
//    public SourceVersion getSupportedSourceVersion() {
//        return super.getSupportedSourceVersion();
//    }


//    @Override
//    public Set<String> getSupportedOptions() {
//        return super.getSupportedOptions();
//    }


}
