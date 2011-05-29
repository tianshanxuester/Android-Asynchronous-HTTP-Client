package com.yfzhang.utils.thread;

/**
 * Callback definition for method to be invoked when 
 * {@link com.yfzhang.utils.thread.Task Task} is complete. Executed on Task thread.
 * 
 * @author yifanz
 *
 * @param <R> result type returned by 
 * {@link com.yfzhang.utils.thread.Task#onTaskFinish(int, boolean, Object, TaskCallback)
 * onTaskFinish()} in {@link com.yfzhang.utils.thread.Task Task}.
 * @see {@link com.yfzhang.utils.thread.TaskManager TaskManager}
 */
interface TaskResponseListener<R> {
	/**
	 * Method executed when Task is complete.
	 * 
	 * @param id Task ID, provided by caller
	 * @param interrupted boolean whether Task thread was interrupted
	 * @param result object returned when Task completes
	 * @param callback {@link com.yfzhang.utils.thread.TaskCallback
	 * TaskCallback} provided by caller
	 */
	public abstract void onTaskFinish(final int id, final boolean interrupted, final R result, final TaskCallback<R> callback);
}
