package cn.pyethel.remind.quartz;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.stereotype.Component;

/**
 * date: 2020/12/13 15:33
 *
 * @author pyethel
 */
@Component
public class JobFactory extends AdaptableJobFactory {
    /**
     * AutowireCapableBeanFactory接口是BeanFactory的子类，可以连接和填充那些生命周期不被Spring管理的已存在的bean实例
     */
    private final AutowireCapableBeanFactory capableBeanFactory;

    public JobFactory(AutowireCapableBeanFactory capableBeanFactory) {
        this.capableBeanFactory = capableBeanFactory;
    }

    /**
     * 创建Job实例
     * @param bundle b
     * @return o
     * @throws Exception e
     */
    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        // 实例化对象
        Object jobInstance = super.createJobInstance(bundle);
        // 进行注入（Spring管理该Bean）
        capableBeanFactory.autowireBean(jobInstance);
        //返回对象
        return jobInstance;
    }
}