package com.cxli.coolweather.app.util;

/**
 * Created by cx.li on 2015/12/10.
 */
  public interface HttpCallBackListener {

        void onFinish(String response);
        void onError(Exception e);
}

