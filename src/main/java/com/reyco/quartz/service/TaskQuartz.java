package com.reyco.quartz.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

public class TaskQuartz {
	
	/**
	 * 存放任务队列
	 */
	private Map<String, ScheduledFuture<?>> futuresMap;
	
	/**
	 * 线程池
	 */
	private ThreadPoolTaskScheduler threadPoolTaskScheduler;
	
	public ThreadPoolTaskScheduler getThreadPoolTaskScheduler() {
		return threadPoolTaskScheduler;
	}
	public void setThreadPoolTaskScheduler(ThreadPoolTaskScheduler threadPoolTaskScheduler) {
		this.threadPoolTaskScheduler = threadPoolTaskScheduler;
	}
	public TaskQuartz(ThreadPoolTaskScheduler threadPoolTaskScheduler) {
		super();
		this.threadPoolTaskScheduler = threadPoolTaskScheduler;
		futuresMap = new ConcurrentHashMap<>(128);
	}
	/**
	 * 开启任务
	 * 
	 * @param runnable
	 *            任务
	 * @param cron
	 *            表达式
	 * @param key
	 *            任务名
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public Boolean startTask(Runnable runnable, String cron, String key)
			throws InterruptedException, ExecutionException {
		Boolean addTaskFlag = addTask(runnable, cron, key);
		if (addTaskFlag) {
			return true;
		}
		return false;
	}

	/**
	 * 停止任务
	 * 
	 * @param key
	 * @return
	 */
	public Boolean stopTask(String key) {
		return removeTask(key);
	}

	/**
	 * 添加定时任务
	 * 
	 * @param task
	 *            任务
	 * @param cron
	 *            表达式
	 * @param key
	 *            任务名
	 * 
	 * @return
	 */
	public Boolean addTask(Runnable task, String cron, String key) {
		if (futuresMap.containsKey(key)) {
			System.out.println("添加任务key名： " + key + "重复");
			return false;
		}
		ScheduledFuture<?> future = threadPoolTaskScheduler.schedule(task, new CronTrigger(cron));
		if (null != future) {
			ScheduledFuture<?> oldScheduledFuture = futuresMap.put(key, future);
			return true;
		}
		return false;
	}

	/**
	 * 移除定时任务
	 * 
	 * @param key
	 *            任务名
	 * @return
	 */
	public boolean removeTask(String key) {
		ScheduledFuture<?> toBeRemovedFuture = futuresMap.remove(key);
		if (toBeRemovedFuture != null) {
			toBeRemovedFuture.cancel(true);
			System.out.println("移除任务：key=" + key);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * <pre>
	 * 更新定时任务 有可能会出现：
     *	 1、旧的任务不存在，此时直接添加新任务； 
	 *   2、旧的任务存在，先删除旧的任务，再添加新的任务
	 * </pre>
	 * @param task
	 *            任务
	 * @param cron
	 *            表达式
	 * @param key
	 *            任务名称
	 * 
	 * @return
	 */
	public boolean updateTask(Runnable task, String cron, String key) {
		ScheduledFuture<?> toBeRemovedFuture = futuresMap.remove(key);
		// 存在则删除旧的任务
		if (toBeRemovedFuture != null) {
			toBeRemovedFuture.cancel(true);
		}
		Boolean flag = addTask(task, cron, key);
		if (flag) {
			return true;
		}
		return false;
	}
}
