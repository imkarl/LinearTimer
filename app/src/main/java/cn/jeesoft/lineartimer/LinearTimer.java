package cn.jeesoft.lineartimer;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 线性计时器（整秒对准）
 * @author king
 */
public class LinearTimer {
	
	private static boolean isStop = true;
	private static long mLastTime;
	private static Map<OnLinearTimerListener, Long> mListeners = new HashMap<OnLinearTimerListener, Long>();

    private static final int ACTION_ON_PROGRESS = 1;
    private static final int ACTION_ON_STOP = 2;
    private static final int ACTION_ON_FINISH = 3;
    private static Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            OnLinearTimerListener listener = (OnLinearTimerListener) ((Object[])msg.obj)[0];
            long time = (long) ((Object[])msg.obj)[1];
            int count = msg.arg1;

            switch (msg.what) {
                case ACTION_ON_PROGRESS:
                    listener.onProgress(time, count);
                    break;
                case ACTION_ON_STOP:
                    listener.onStop(time, count);
                    break;
                case ACTION_ON_FINISH:
                    listener.onFinish(time);
                    removeListener(listener);
                    break;
            }
        }
    };

    private static void sendHandlerAction(int action, OnLinearTimerListener listener, long time, int count) {
        Message message = new Message();
        message.what = action;
        message.arg1 = count;

        Object[] obj = new Object[2];
        obj[0] = listener;
        obj[1] = time;
        message.obj = obj;

        mHandler.sendMessage(message);
    }
	
	private static Runnable run = new Runnable() {
		@Override
		public void run() {
			while (true) {
				final long currentTime = System.currentTimeMillis();
				int delayed = 1000;
	
				// 时间对准（以整秒为基线）
				// 修正时间差，整秒对齐
				if ((currentTime - mLastTime - 1000) >= 0) {
					delayed = delayed - (int)(currentTime % 1000);
				} else {
					delayed = delayed + (int)(1000 - (currentTime % 1000));
				}

                // 间隔执行时长
                //Log.e("LinearTimer", "delayed="+delayed);

				// 回调监听者计时变化
				for (Entry<OnLinearTimerListener, Long> entry : mListeners.entrySet()) {
					int count = (int) (entry.getValue()/1000 - currentTime/1000);
                    if (currentTime - mLastTime >= 500) {
                        count++;
                    }

					OnLinearTimerListener listener = entry.getKey();
					// 更新进度
                    sendHandlerAction(ACTION_ON_PROGRESS, listener, currentTime, count);
					// 判断是否执行完成
					if (count <= 1) {
                        sendHandlerAction(ACTION_ON_FINISH, listener, currentTime, count);
					}
				}
				
				// 判断是否终止
				if (isStop) {
					// 如果终止，通知监听者
					for (Entry<OnLinearTimerListener, Long> entry : mListeners.entrySet()) {
						int count = (int) ((entry.getValue()-currentTime)/1000);
						OnLinearTimerListener listener = entry.getKey();
                        sendHandlerAction(ACTION_ON_STOP, listener, currentTime, count);
					}
					mListeners.clear();
					return;
				}

				mLastTime = (currentTime/1000)*1000;
				if (currentTime - mLastTime >= 500) {
					mLastTime += 1000;
				}
				
				// 1s定时执行
				try {
					Thread.sleep(delayed);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	};

    /**
     * 开始计时器
     */
	public static void start() {
		if (isStop) {
			mLastTime = System.currentTimeMillis();
			if (mLastTime % 1000 > 0) {
				mLastTime -= 1000;
			}
			isStop = false;
			new Thread(run).start();
		}
	}
	
	/**
	 * 结束计时器
	 */
	public static void stop() {
		isStop = true;
	}

	/**
	 * 判断当前是否开启计时
	 * @return
	 */
	public static boolean isStart() {
		return !isStop;
	}

	/**
	 * 添加计时监听器
	 * 【如果已经添加过监听器，则覆盖之前的设置】
	 * @param count 计时次数
	 * @param listener
	 */
	public static void addCountListener(int count, OnLinearTimerListener listener) {
		mListeners.put(listener, System.currentTimeMillis()+(count*1000));
	}
	/**
	 * 添加计时监听器
	 * 【如果已经添加过监听器，则覆盖之前的设置】
	 * @param stopTime 计时结束时间
	 * @param listener
	 */
	public static void addTimeListener(long stopTime, OnLinearTimerListener listener) {
		if (stopTime/1000 <= System.currentTimeMillis()/1000) {
			throw new IllegalArgumentException("'stopTime' can't be less than the current time.");
		}
		if (listener == null) {
			throw new IllegalArgumentException("'listener' can't be NULL.");
		}
		mListeners.put(listener, (stopTime/1000*1000));
	}
	
	/**
	 * 删除计时监听器
	 * @param listener
	 */
	public static void removeListener(OnLinearTimerListener listener) {
		mListeners.remove(listener);
	}

}
