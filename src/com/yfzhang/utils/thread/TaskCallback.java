package com.yfzhang.utils.thread;

/**
 * Callback definition for method to be executed when 
 * {@link com.yfzhang.utils.thread.Task Task} is finished. Implementation
 * should be provided by caller. Executed on caller's thread.
 * 
 * @author yifanz
 *
 * @param <R> result type returned by 
 * {@link com.yfzhang.utils.thread.Task#onTaskFinish(int, boolean, Object, TaskCallback)
 * onTaskFinish()} in {@link com.yfzhang.utils.thread.Task Task}.
 * @see {@link com.yfzhang.utils.thread.Task Task}
 */
public interface TaskCallback<R> {
	/**
	 * Method executed when Task completes. Implementation should be provided by
	 * caller.
	 * 
	 * @param id Task ID, provided by caller
	 * @param interrupted boolean whether Task thread was interrupted
	 * @param result object returned when Task completes
	 */
	public abstract void executeCallback(int id, boolean interrupted, R result);
}
