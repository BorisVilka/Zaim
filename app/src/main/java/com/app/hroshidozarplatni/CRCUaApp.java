package com.app.hroshidozarplatni;

import androidx.multidex.MultiDexApplication;

import com.orhanobut.hawk.Hawk;


public class CRCUaApp extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Hawk.init(getApplicationContext()).build();

    }

//    private void showFBInfo() {
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                String hashKey = new String(Base64.encode(md.digest(), 0));
//                Log.i("FB", "printHashKey() Hash Key: " + hashKey);
//            }
//        } catch (NoSuchAlgorithmException e) {
//            Log.e("FB", "printHashKey()", e);
//        } catch (Exception e) {
//            Log.e("FB", "printHashKey()", e);
//        }
//    }
}
