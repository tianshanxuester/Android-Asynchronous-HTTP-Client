package com.yfzhang.utils.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.os.Handler;

/**
 * Manages threading for asynchronous {@link com.yfzhang.utils.Task Tasks}.
 * 
 * @author yifanz
 *
 * @param <R> result type returned by 
 * {@link com.yfzhang.utils.thread.Task#onTaskFinish(int, boolean, Object, TaskCallback)
 * onTaskFinish()} in {@link com.yfzhang.utils.thread.Task Task}.
 */
public class TaskManager<R> implements TaskResponseListener<R>{
	private Handler caller;
	private ExecutorService manager;
	private Future<?> pendingTask;
	private Runnable preparedTask;
	
	/**
	 * Creates a new instance of TaskManager. Requires and handle on caller's
	 * thread.
	 * 
	 * @param caller {@link android.os.Handler Handler} for caller's thread
	 */
	public TaskManager(Handler caller) {
		this.caller = caller;
		manager = Executors.newSingleThreadExecutor();
	}
	
	/**
	 * Submits a task into the task queue.
	 * @param task
	 * @param delayMillis
	 */
	public void updateQueue(Task<R> task, long delayMillis) {
		if(preparedTask != null)
			caller.removeCallbacks(preparedTask);
		preparedTask = prepareTask(task);
		caller.postDelayed(preparedTask, delayMillis);
	}
	
	private Runnable prepareTask(final Task<R> task) {
		return new Runnable() {
			public void run() {
				if(pendingTask != null)
					pendingTask.cancel(true);
				task.setTaskResponseListener(TaskManager.this);
				pendingTask = manager.submit(task);
			}
		};
	}
	
	public void terminateAll() {
		if(preparedTask != null)
			caller.removeCallbacks(preparedTask);
		manager.shutdownNow();
	}
	
	/**
	 * Executes Task's callback on caller's thread.
	 */
	public void onTaskFinish(final int id, final boolean interrupted, final R result, final TaskCallback<R> callback) {
		caller.post(new Runnable() {
			public void run() {
				callback.executeCallback(id, interrupted, result);
			}			
		});
	}
}
