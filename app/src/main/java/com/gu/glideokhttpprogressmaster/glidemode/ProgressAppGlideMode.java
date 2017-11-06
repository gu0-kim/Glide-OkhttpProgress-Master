package com.gu.glideokhttpprogressmaster.glidemode;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.gu.glideokhttpprogressmaster.okhttp.OkhttpClientManager;

import java.io.InputStream;

/**
 * @author developergu
 * @version v1.0.0
 * @since 2017/11/6
 */
@GlideModule
public class ProgressAppGlideMode extends AppGlideModule {
  @Override
  public void registerComponents(Context context, Glide glide, Registry registry) {
    super.registerComponents(context, glide, registry);
    registry.replace(
        GlideUrl.class,
        InputStream.class,
        new OkHttpUrlLoader.Factory(OkhttpClientManager.getProgressClient()));
  }
}
