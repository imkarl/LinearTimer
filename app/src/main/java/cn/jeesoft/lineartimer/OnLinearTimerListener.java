package cn.jeesoft.lineartimer;

/**
 * 计时监听器
 * @author king
 */
public interface OnLinearTimerListener {

	/**
	 * 更新计时进度
	 * @param time 当前时间戳
	 * @param count 当前计时次数
	 */
	public void onProgress(long time, int count);
	/**
	 * 计时器意外终止
	 * @param time 当前时间戳
	 * @param count 当前计时次数
	 */
	public void onStop(long time, int count);
	/**
	 * 计时完成
	 * @param time 当前时间戳
	 */
	public void onFinish(long time);
	
}
