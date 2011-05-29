package com.yfzhang.utils.thread;

/**
 * Asynchronous task. Implementation should be provided by caller in run() method.
 * 
 * @author yifanz
 *
 * @param <R> result type returned by 
 * {@link com.yfzhang.utils.thread.Task#onTaskFinish(int, boolean, Object, TaskCallback)
 * onTaskFinish()} in {@link com.yfzhang.utils.thread.Task Task}.
 * @see {@link com.yfzhang.utils.thread.TaskCallback TaskCallback}
 */
public abstract class Task<R> implements Runnable {
	private TaskResponseListener<R> listener;
	
	final public void setTaskResponseListener(TaskResponseListener<R> listener) {
		this.listener = listener;
	}
	
	final protected void onTaskFinish(final int id, final boolean interrupted, final R result, final TaskCallback<R> callback) {
		listener.onTaskFinish(id, interrupted, result, callback);
	}
}
