package org.example;

import org.example.calculator.Calculator;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;

public class Application {
    private static ApplicationContext applicationContext;
    private static DefaultListableBeanFactory defaultListableBeanFactory;
    private static String jarAddress = "/Users/liujiakun/Code/IdeaProject/dynamic-deploy/calculator-udf/target/calculator-udf-1.0-SNAPSHOT.jar";
    private static String jarPath = "file:" + jarAddress;

    public static void main(String[] args) throws InterruptedException {
        applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        defaultListableBeanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
//        while (true) {
        try {
//            hotDeployWithReflect();
                hotDeployWithSpring();
                delete();
        } catch (Exception e) {
            e.printStackTrace();
            Thread.sleep(1000 * 10);
        }
//        }
    }

    /**
     * 热加载Calculator接口的实现 反射方式热部署
     *
     * @throws Exception
     */
    public static void hotDeployWithReflect() throws Exception {
        URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{new URL(jarPath)}, Thread.currentThread().getContextClassLoader());
        Class clazz = urlClassLoader.loadClass("org.example.udf.calculator.impl.CalculatorImpl");
        Calculator calculator = (Calculator) clazz.newInstance();

        int result = calculator.add(1, 2);
        System.out.println(result);
    }

    /**
     * 加入jar包后 动态注册bean到spring容器，包括bean的依赖
     */
    public static void hotDeployWithSpring() throws Exception {
        Set<String> classNameSet = DeployUtils.readJarFile(jarAddress);
        URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{new URL(jarPath)}, Thread.currentThread().getContextClassLoader());
        for (String className : classNameSet) {
            Class clazz = urlClassLoader.loadClass(className);
            if (DeployUtils.isSpringBeanClass(clazz)) {
                BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
                defaultListableBeanFactory.registerBeanDefinition(DeployUtils.transformName(className), beanDefinitionBuilder.getBeanDefinition());
            }
        }
        Calculator bean = applicationContext.getBean(Calculator.class);
        System.out.println(bean.calculate(1,5));
        System.out.println(bean.add(1,5));
    }


    /**
     * 删除jar包时 需要在spring容器删除注入
     */
    public static void delete() throws Exception {
        Set<String> classNameSet = DeployUtils.readJarFile(jarAddress);
        URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{new URL(jarPath)}, Thread.currentThread().getContextClassLoader());
        for (String className : classNameSet) {
            Class clazz = urlClassLoader.loadClass(className);
            if (DeployUtils.isSpringBeanClass(clazz)) {
                defaultListableBeanFactory.removeBeanDefinition(DeployUtils.transformName(className));
            }
        }
    }
}
