package com.cxli.review.activity;

/**
 * Created by cx.li on 2015/12/10.
 */
  public interface HttpCallBackListener {

        void onFinish(String response);

        void onFinish(byte[] bytes);
        void onError(Exception e);
}

