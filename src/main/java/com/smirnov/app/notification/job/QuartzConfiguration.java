package com.smirnov.app.notification.job;

import lombok.RequiredArgsConstructor;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class QuartzConfiguration {

    private final ApplicationContext applicationContext;
    private final Long EXECUTION_INTERVAL = TimeUnit.MINUTES.toMillis(1);

    @Bean
    public SpringBeanJobFactory createSpringBeanJobFactory() {
        return new SpringBeanJobFactory() {
            @Override
            protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {

                Object job = super.createJobInstance(bundle);

                applicationContext.getAutowireCapableBeanFactory()
                        .autowireBean(job);

                return job;
            }
        };
    }

    @Bean
    public SchedulerFactoryBean createSchedulerFactory(SpringBeanJobFactory springBeanJobFactory, Trigger trigger) {

        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
        schedulerFactory.setTriggers(trigger);

        springBeanJobFactory.setApplicationContext(applicationContext);
        schedulerFactory.setJobFactory(springBeanJobFactory);

        return schedulerFactory;
    }

    @Bean
    public SimpleTriggerFactoryBean createSimpleTriggerFactoryBean(JobDetail jobDetail) {

        SimpleTriggerFactoryBean simpleTriggerFactory = new SimpleTriggerFactoryBean();

        simpleTriggerFactory.setJobDetail(jobDetail);
        simpleTriggerFactory.setStartDelay(0);
        simpleTriggerFactory.setRepeatInterval(EXECUTION_INTERVAL);

        return simpleTriggerFactory;
    }

    @Bean
    public JobDetailFactoryBean createJobDetailFactoryBean() {

        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();

        jobDetailFactory.setJobClass(NotificationJob.class);

        return jobDetailFactory;
    }
}