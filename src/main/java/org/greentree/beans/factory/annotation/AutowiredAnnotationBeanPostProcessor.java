package org.greentree.beans.factory.annotation;

import org.greentree.BeansException;
import org.greentree.beans.factory.AutowireCapableBeanFactory;
import java.lang.reflect.Field;

/**
 * 这个类是干嘛的？
 * 处理Autowired注解
 */
public class AutowiredAnnotationBeanPostProcessor implements BeanPostProcessor {

    private AutowireCapableBeanFactory beanFactory;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            boolean isAutowired = field.isAnnotationPresent(Autowired.class);
            if (isAutowired) {
                String filedName = field.getName();
                Object autowiredBean = this.beanFactory.getBean(filedName);
                try {
                    field.setAccessible(true);
                    field.set(bean, autowiredBean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return null;
    }

    public AutowireCapableBeanFactory getBeanFactory() {
        return beanFactory;
    }

    public void setBeanFactory(AutowireCapableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

}
