package com.gu.glideokhttpprogressmaster;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.gu.glideokhttpprogressmaster.glidemode.GlideApp;
import com.gu.glideokhttpprogressmaster.okhttp.OkhttpClientManager;
import com.gu.glideokhttpprogressmaster.okhttp.OnProgressListener;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import uk.co.senab.photoview.PhotoView;

public class MainActivity extends AppCompatActivity {

  private PublishSubject<Integer> mbus;
  private CompositeDisposable cd;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ProgressBar pb = findViewById(R.id.pb);
    cd = new CompositeDisposable();
    mbus = PublishSubject.create();
    cd.add(
        mbus.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(pb::setProgress));

    PhotoView photoView = findViewById(R.id.photo_view);
    final ProgressListener listener = new ProgressListener();
    OkhttpClientManager.addProgressListener(listener);
    String url =
        "http://img-arch.pconline.com.cn/images/upload/upc/tx/photoblog/1311/17/c0/28704978_28704978_1384619788578.jpg";
    GlideApp.with(this)
        .load(url)
        .listener(
            new RequestListener<Drawable>() {
              @Override
              public boolean onLoadFailed(
                  @Nullable GlideException e,
                  Object model,
                  Target<Drawable> target,
                  boolean isFirstResource) {
                OkhttpClientManager.removeProgressListener(listener);
                return false;
              }

              @Override
              public boolean onResourceReady(
                  Drawable resource,
                  Object model,
                  Target<Drawable> target,
                  DataSource dataSource,
                  boolean isFirstResource) {
                OkhttpClientManager.removeProgressListener(listener);
                return false;
              }
            })
        .into(photoView);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (!cd.isDisposed()) {
      cd.dispose();
    }
  }

  class ProgressListener implements OnProgressListener {
    @Override
    public void onProgress(
        long bytesRead, long totalBytes, boolean isDone, GlideException exception) {
      Log.e(
          "TAG",
          "onProgress: ------bytesRead=" + bytesRead + "------totalBytes=" + totalBytes + "------");
      mbus.onNext((int) ((float) bytesRead / totalBytes * 100));
    }
  }
}
