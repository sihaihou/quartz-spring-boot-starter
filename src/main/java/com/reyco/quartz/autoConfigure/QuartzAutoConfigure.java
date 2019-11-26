package com.reyco.quartz.autoConfigure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import com.reyco.quartz.service.TaskQuartz;


@Configuration
@ConditionalOnClass(TaskQuartz.class)
public class QuartzAutoConfigure {
	
	@Autowired
	private ThreadPoolTaskScheduler threadPoolTaskScheduler;
	
	/**
	 *  如果没有指定线程池大小，默认大小为1
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
		if(null == threadPoolTaskScheduler ) {
			threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
			threadPoolTaskScheduler.setPoolSize(1);
		}
		return threadPoolTaskScheduler;
	}
	@Bean
	@ConditionalOnMissingBean
	public TaskQuartz createTaskQuartz() {
		return new TaskQuartz(threadPoolTaskScheduler);
	}
	
	
}
