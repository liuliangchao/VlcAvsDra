package com.liulc.vlc.avsdra;
import android.util.Log;

public class LogUtils {

	public static void logld(String content, Object... args) {
		for (int i = 0; i < Thread.currentThread().getStackTrace().length; i++) {
			String realContent = getContent(content, i, args);
			Log.d("default", realContent);
		}
	}

	public static void logld(String tag, String content, Object... args) {
		Log.d(tag, getContent(content, 4, args));
	}

	public static void logli(String content, Object... args) {
		for (int i = 0; i < Thread.currentThread().getStackTrace().length; i++) {
			String realContent = getContent(content, i, args);
			Log.i("default", realContent);
		}
	}

	public static void logli(String tag, String content, Object... args) {
		Log.i(tag, getContent(content, 4, args));
	}

	public static void logle(String content, Object... args) {
		for (int i = 0; i < Thread.currentThread().getStackTrace().length; i++) {
			String realContent = getContent(content, i, args);
			Log.e("default", realContent);
		}
	}

	public static void logle(String tag, String content, Object... args) {
		Log.e(tag, getContent(content, 4, args));
	}

	private static String getNameFromTrace(StackTraceElement[] traceElements, int place) {
		StringBuilder taskName = new StringBuilder();
		//判断调用栈的层级，大于place的才打印Log输出
		if (traceElements != null && traceElements.length > place) {
			StackTraceElement traceElement = traceElements[place];
			taskName.append(traceElement.getMethodName());
			taskName.append("(").append(traceElement.getFileName()).append(":").append(traceElement.getLineNumber()).append(")");
		}
		return taskName.toString();
	}

	private static String getContent(String msg, int place, Object... args) {
		try {
			String sourceLinks = getNameFromTrace(Thread.currentThread().getStackTrace(), place);
			return sourceLinks + String.format(msg, args);
		} catch (Throwable throwable) {
			return msg;
		}
	}
}

