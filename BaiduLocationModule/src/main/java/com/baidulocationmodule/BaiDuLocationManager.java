package com.baidulocationmodule;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 * Created by yangc on 2017/9/16.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated: 高度定位帮助类
 */

public class BaiDuLocationManager {
    private static final String TAG = BaiDuLocationManager.class.getName();
    private MyLocationsListener locationListener;
    private LocationClient mLocationClient = null;
    private LocationClientOption mOption, DIYoption;
    private BDAbstractLocationListener myListener;

    public static BaiDuLocationManager getSingleton() {
        return Holder.holder;
    }

    private static class Holder {
        static BaiDuLocationManager holder = new BaiDuLocationManager();
    }

    private BaiDuLocationManager() {

    }

    /***
     * 初始化
     *
     * @param context 上下文
     **/
    public void init(@NonNull Context context) {
        mLocationClient = new LocationClient(context.getApplicationContext());
        mLocationClient.setLocOption(getDefaultLocationClientOption());
        mLocationClient.registerLocationListener(new MyLocationListener());
    }

    public void start(@NonNull MyLocationsListener listener) {
        if (mLocationClient != null) {
            locationListener = listener;
            myListener = new MyLocationListener();
            mLocationClient.registerLocationListener(myListener);
            mLocationClient.start();
        }
    }

    /**
     * 停止定位
     **/
    public boolean stop() {
        if (mLocationClient != null) {
            mLocationClient.unRegisterLocationListener(myListener);
            if (mLocationClient.isStarted())
                mLocationClient.stop();
            myListener = null;
            locationListener = null;
        }
        return true;
    }

    /***
     * 如果
     * 销毁服务 调用此方法后，一切示例都被被设置null
     * 再次使用必须调用 init();反发
     **/
    public void onDestroy() {
        if (mLocationClient != null) {
            stop();
            mLocationClient = null;//销毁定位客户端，同时销毁本地定位服务。
            mLocationClient = null;
            mOption = null;
            Holder.holder = null;
        }
    }

    /***
     * @return DefaultLocationClientOption
     */
    public LocationClientOption getDefaultLocationClientOption() {
        if (mOption == null) {
            mOption = new LocationClientOption();
            mOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
            mOption.setCoorType(Utils.CoorType_BD09LL);//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
            mOption.setScanSpan(3000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
            mOption.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
            mOption.setIsNeedLocationDescribe(true);//可选，设置是否需要地址描述
            mOption.setNeedDeviceDirect(false);//可选，设置是否需要设备方向结果
            mOption.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
            mOption.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
            mOption.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
            mOption.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
            mOption.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
            mOption.setIsNeedAltitude(false);//可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用

        }
        return mOption;
    }

    /**
     * 信息
     ***/
    public class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            Log.d(TAG, "onReceiveLocation:" + location.getAddrStr());
            if (locationListener != null) {
                locationListener.onSucceed(location.getAddrStr());
            }
            //获取定位结果
     /*       location.getTime();    //获取定位时间
            location.getLocationID();    //获取定位唯一ID，v7.2版本新增，用于排查定位问题
            location.getLocType();    //获取定位类型
            location.getLatitude();    //获取纬度信息
            location.getLongitude();    //获取经度信息
            location.getRadius();    //获取定位精准度
            location.getAddrStr();    //获取地址信息
            location.getCountry();    //获取国家信息
            location.getCountryCode();    //获取国家码
            location.getCity();    //获取城市信息
            location.getCityCode();    //获取城市码
            location.getDistrict();    //获取区县信息
            location.getStreet();    //获取街道信息
            location.getStreetNumber();    //获取街道码
            location.getLocationDescribe();    //获取当前位置描述信息
            location.getPoiList();    //获取当前位置周边POI信息

            location.getBuildingID();    //室内精准定位下，获取楼宇ID
            location.getBuildingName();    //室内精准定位下，获取楼宇名称
            location.getFloor();    //室内精准定位下，获取当前位置所处的楼层信息

            if (location.getLocType() == BDLocation.TypeGpsLocation) {

                //当前为GPS定位结果，可获取以下信息
                location.getSpeed();    //获取当前速度，单位：公里每小时
                location.getSatelliteNumber();    //获取当前卫星数
                location.getAltitude();    //获取海拔高度信息，单位米
                location.getDirection();    //获取方向信息，单位度
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                //当前为网络定位结果，可获取以下信息
                location.getOperators();    //获取运营商信息
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {
                //当前为网络定位结果
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                if (locationListener != null) {
                    locationListener.onFailed(location.getLocType(), "定位失败");
                }
                //当前网络定位失败
                //可将定位唯一ID、IMEI、定位失败时间反馈至loc-bugs@baidu.com
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                if (locationListener != null) {
                    locationListener.onFailed(location.getLocType(), "定位失败");
                }
                //当前网络不通
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                //当前缺少定位依据，可能是用户没有授权，建议弹出提示框让用户开启权限
                //可进一步参考onLocDiagnosticMessage中的错误返回码

            }
            */
        }

        /**
         * 回调定位诊断信息，开发者可以根据相关信息解决定位遇到的一些问题
         * 自动回调，相同的diagnosticType只会回调一次
         *
         * @param locType           当前定位类型
         * @param diagnosticType    诊断类型（1~9）
         * @param diagnosticMessage 具体的诊断信息释义
         */
        public void onLocDiagnosticMessage(int locType, int diagnosticType, String diagnosticMessage) {
            Log.d(TAG, "onLocDiagnosticMessage" + diagnosticMessage);

            if (locationListener != null) {
                locationListener.onFailed(diagnosticType, diagnosticMessage);
            }
      /*      if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_BETTER_OPEN_GPS) {
                //建议打开GPS
            } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_BETTER_OPEN_WIFI) {
                //建议打开wifi，不必连接，这样有助于提高网络定位精度！
            } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_NEED_CHECK_LOC_PERMISSION) {
                //定位权限受限，建议提示用户授予APP定位权限！
            } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_NEED_CHECK_NET) {
                //网络异常造成定位失败，建议用户确认网络状态是否异常！
            } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_NEED_CLOSE_FLYMODE) {
                //手机飞行模式造成定位失败，建议用户关闭飞行模式后再重试定位！
            } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_NEED_INSERT_SIMCARD_OR_OPEN_WIFI) {
                //无法获取任何定位依据，建议用户打开wifi或者插入sim卡重试！
            } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_NEED_OPEN_PHONE_LOC_SWITCH) {
                //无法获取有效定位依据，建议用户打开手机设置里的定位开关后重试！
            } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_SERVER_FAIL) {
                //百度定位服务端定位失败
                //建议反馈location.getLocationID()和大体定位时间到loc-bugs@baidu.com
            } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_FAIL_UNKNOWN) {
                //无法获取有效定位依据，但无法确定具体原因
                //建议检查是否有安全软件屏蔽相关定位权限
                //或调用LocationClient.restart()重新启动后重试！
            }*/
        }
    }

}
