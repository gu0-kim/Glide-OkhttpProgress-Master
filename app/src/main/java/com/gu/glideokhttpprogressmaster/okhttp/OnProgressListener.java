package com.gu.glideokhttpprogressmaster.okhttp;

import com.bumptech.glide.load.engine.GlideException;

public interface OnProgressListener {
  void onProgress(long bytesRead, long totalBytes, boolean isDone, GlideException exception);
}
