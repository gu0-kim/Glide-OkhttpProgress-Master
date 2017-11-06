package com.gu.glideokhttpprogressmaster.okhttp;

import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author developergu
 * @version v1.0.0
 * @since 2017/11/6
 */
public class OkhttpClientManager {

  private static List<WeakReference<OnProgressListener>> listeners =
      Collections.synchronizedList(new ArrayList<WeakReference<OnProgressListener>>());
  private static OkHttpClient okHttpClient;

  public static OkHttpClient getProgressClient() {
    if (okHttpClient == null) {
      okHttpClient =
          new OkHttpClient.Builder()
              .addNetworkInterceptor(
                  chain -> {
                    Request request = chain.request();
                    Response response = chain.proceed(request);
                    return response
                        .newBuilder()
                        .body(new ProgressResponseBody(response.body(), LISTENER))
                        .build();
                  })
              .build();
    }
    return okHttpClient;
  }

  private static final OnProgressListener LISTENER =
      (bytesRead, totalBytes, isDone, exception) -> {
        if (listeners == null || listeners.size() == 0) return;

        for (int i = 0; i < listeners.size(); i++) {
          WeakReference<OnProgressListener> listener = listeners.get(i);
          OnProgressListener progressListener = listener.get();
          if (progressListener == null) {
            listeners.remove(i);
          } else {
            progressListener.onProgress(bytesRead, totalBytes, isDone, exception);
          }
        }
      };

  public static void addProgressListener(OnProgressListener progressListener) {
    if (progressListener == null) return;

    if (findProgressListener(progressListener) == null) {
      listeners.add(new WeakReference<>(progressListener));
    }
  }

  public static void removeProgressListener(OnProgressListener progressListener) {
    if (progressListener == null) return;

    WeakReference<OnProgressListener> listener = findProgressListener(progressListener);
    if (listener != null) {
      boolean res = listeners.remove(listener);
      Log.e("TAG", "removeProgressListener: res is " + res);
    }
  }

  private static WeakReference<OnProgressListener> findProgressListener(
      OnProgressListener listener) {
    if (listener == null) return null;
    if (listeners == null || listeners.size() == 0) return null;

    for (int i = 0; i < listeners.size(); i++) {
      WeakReference<OnProgressListener> progressListener = listeners.get(i);
      if (progressListener.get() == listener) {
        return progressListener;
      }
    }
    return null;
  }
}
