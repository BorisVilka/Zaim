package com.app.hroshidozarplatni.screens;



import android.animation.Animator;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.hroshidozarplatni.MyApp;
import com.app.hroshidozarplatni.adapters.CRCUaPropositionAdapter;
import com.app.hroshidozarplatni.api.CRCUaAPIClient;
import com.app.hroshidozarplatni.models.AdapterModel;
import com.app.hroshidozarplatni.models.CRCUaProposition;
import com.app.hroshidozarplatni.models.TitleModel;
import com.app.hroshidozarplatni.utils.Const;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.app.hroshidozarplatni.R;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CRCUaPropositionsScreen extends CRCUaBaseScreen {

    private final static int SMALL_POPUP_ANIM = 200;

    private SwipeRefreshLayout swipeContainer;
    private RecyclerView rvItems;
    private CRCUaPropositionAdapter adapter;


    private ProgressBar progressBar;
    private TextView tvError;

    private View fab;
    private View smallPopup;
    private View smallPopupClose;

    private List<AdapterModel> data = new ArrayList<>();

    private final Handler handler = new Handler();
    private Runnable showPopoupRunnable;
//    private AppEventsLogger logger;

    private boolean cached = false;

    @Override
    int getLayout() {
        return R.layout.propositions_screen;
    }

    @Override
    void initView() {
//        if (FacebookSdk.isInitialized()) {
//            logger = AppEventsLogger.newLogger(this);
//        }

//        boolean showStep3 = Hawk.get(Const.KEY_SHOW_STEP3, false);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.get("url") != null) {
            String url = bundle.getString("url");
            if (!TextUtils.isEmpty(url)) {

//                FBLogger.logOfferClicked(logger, "notification", url);

                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                } catch (Exception e) {
                }

                finish();
                return;
            }
        }


        swipeContainer = findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(this::getPropositions);
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        rvItems = findViewById(R.id.rvItems);
        rvItems.setHasFixedSize(true);

        rvItems.setAdapter(adapter = new CRCUaPropositionAdapter(data, this::onPropositionClick));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (adapter.getItemViewType(position) == AdapterModel.Type.TYPE_HEADER.ordinal()) {
                    return gridLayoutManager.getSpanCount();
                } else if (adapter.getItemViewType(position) == AdapterModel.Type.TYPE_FAV_ITEM.ordinal()) {
                    return gridLayoutManager.getSpanCount();
                } else if (adapter.getItemViewType(position) == AdapterModel.Type.TYPE_FOOTER.ordinal()) {
                    return gridLayoutManager.getSpanCount();
                } else {
                    return 1;
                }
            }
        });

        rvItems.setLayoutManager(gridLayoutManager);

        progressBar = findViewById(R.id.progress);
        tvError = findViewById(R.id.tvError);

        View policy = findViewById(R.id.policy);
        policy.setOnClickListener(v ->
                startActivity(new Intent(CRCUaPropositionsScreen.this, CRCUaPolicyScreen.class)));

        showPopoupRunnable = this::showSmallPopup;

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            showInfoDialog(null, null, getString(R.string.clear), null, null);
            handler.removeCallbacks(showPopoupRunnable);
        });

        Animation anim = AnimationUtils.loadAnimation(fab.getContext(), R.anim.shake);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                handler.postDelayed(() -> fab.startAnimation(anim), 10000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        smallPopup = findViewById(R.id.smallPopup);
        smallPopup.setClickable(true);

        smallPopupClose = findViewById(R.id.smallPopupClose);
        smallPopupClose.setOnClickListener(v -> hideSmallPopup());

        handler.postDelayed(() -> fab.startAnimation(anim), 10000);
        handler.postDelayed(showPopoupRunnable, 30000);
        handler.postDelayed(() ->getPropositions(),5000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Hawk.put(Const.KEY_SHOW_STEP3, false);
    }

    private void getPropositions() {
        String appId = Const.getAppId();
        Log.d("TAG",MyApp.sub1+" "+MyApp.sub2+" "+MyApp.sub3);
        if(MyApp.af_status!=null && MyApp.af_status.equals("Non-organic")) {
            Disposable disposable = CRCUaAPIClient.getInstance().getPropositions("com.app.hroshidozarplatni",MyApp.appsflyer_id, MyApp.sub1,MyApp.sub2,MyApp.sub3,"pokSnDpm4Lsh3uekEyn4SQ",MyApp.idfa)
                    .flatMap(crcPropositions -> {
                        ArrayList<AdapterModel> adapterModels = new ArrayList<>();

                        ArrayList<CRCUaProposition> favData = new ArrayList<>();
                        ArrayList<CRCUaProposition> defData = new ArrayList<>();

//                    int i = 0;
                        for (CRCUaProposition proposition : crcPropositions) {
//                        if (proposition.isFavorite() || (i++ < 3)) {
                            if (proposition.isFavorite()) {
//                            proposition.test = true;
                                favData.add(proposition);
                            } else {
                                defData.add(proposition);
                            }
                        }

                        if (favData.size() > 0) {
                            Collections.sort(favData, (o1, o2) -> Integer.compare(o1.getAppSort(), o2.getAppSort()));
                            adapterModels.add(new TitleModel(getString(R.string.orgs_best)));
                            adapterModels.addAll(favData);
                        }
                        if (defData.size() > 0) {
                            Collections.sort(defData, (o1, o2) -> Integer.compare(o1.getAppSort(), o2.getAppSort()));
                            adapterModels.add(new TitleModel(getString(R.string.orgs_all)));
                            adapterModels.addAll(defData);
                        }

                        if (adapterModels.size() > 0) {
                            adapterModels.add(() -> AdapterModel.Type.TYPE_FOOTER);
                        }

                        return Single.just(adapterModels);
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            result -> {
                                data.clear();
                                data.addAll(result);

                                adapter.notifyDataSetChanged();

                                swipeContainer.setRefreshing(false);
                                progressBar.setVisibility(View.GONE);
                                rvItems.setVisibility(View.VISIBLE);
                                tvError.setVisibility(View.GONE);
                            }, throwable -> {
                                Log.e("API_ERROR", throwable.toString());

                                swipeContainer.setRefreshing(false);
                                progressBar.setVisibility(View.GONE);
                                rvItems.setVisibility(View.GONE);
                                tvError.setVisibility(View.VISIBLE);
                            });
        }   else {
            Disposable disposable = CRCUaAPIClient.getInstance().getPropositions1("com.app.hroshidozarplatni",MyApp.af_status)
                    .flatMap(crcPropositions -> {
                        ArrayList<AdapterModel> adapterModels = new ArrayList<>();

                        ArrayList<CRCUaProposition> favData = new ArrayList<>();
                        ArrayList<CRCUaProposition> defData = new ArrayList<>();

//                    int i = 0;
                        for (CRCUaProposition proposition : crcPropositions) {
//                        if (proposition.isFavorite() || (i++ < 3)) {
                            if (proposition.isFavorite()) {
//                            proposition.test = true;
                                favData.add(proposition);
                            } else {
                                defData.add(proposition);
                            }
                        }

                        if (favData.size() > 0) {
                            Collections.sort(favData, (o1, o2) -> Integer.compare(o1.getAppSort(), o2.getAppSort()));
                            adapterModels.add(new TitleModel(getString(R.string.orgs_best)));
                            adapterModels.addAll(favData);
                        }
                        if (defData.size() > 0) {
                            Collections.sort(defData, (o1, o2) -> Integer.compare(o1.getAppSort(), o2.getAppSort()));
                            adapterModels.add(new TitleModel(getString(R.string.orgs_all)));
                            adapterModels.addAll(defData);
                        }

                        if (adapterModels.size() > 0) {
                            adapterModels.add(() -> AdapterModel.Type.TYPE_FOOTER);
                        }

                        return Single.just(adapterModels);
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            result -> {
                                data.clear();
                                data.addAll(result);

                                adapter.notifyDataSetChanged();

                                swipeContainer.setRefreshing(false);
                                progressBar.setVisibility(View.GONE);
                                rvItems.setVisibility(View.VISIBLE);
                                tvError.setVisibility(View.GONE);
                            }, throwable -> {
                                Log.e("API_ERROR", throwable.toString());

                                swipeContainer.setRefreshing(false);
                                progressBar.setVisibility(View.GONE);
                                rvItems.setVisibility(View.GONE);
                                tvError.setVisibility(View.VISIBLE);
                            });
        }
    }

    private void onPropositionClick(CRCUaProposition CRCUaProposition) {
//        boolean showPermissionInfo = Hawk.get(Const.KEY_SHOW_PERMISSION_INFO, false);
//        if (showPermissionInfo) {
//            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
//            alertDialog.setMessage(getString(R.string.permission_text));
//            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(android.R.string.ok),
//                    (dialog, which) -> {
//                        Hawk.put(Const.KEY_SHOW_PERMISSION_INFO, false);
//                        startRequest(CRCProposition);
//                        dialog.dismiss();
//                    });
//            alertDialog.show();
//        } else {

        //String eventParameters = "{\"purchase\":\"взять кредит\"}";


        startRequest(CRCUaProposition);
//        }
    }

    private void startRequest(CRCUaProposition CRCUaProposition) {
//        Disposable disposable = new RxPermissions(this)
//                .request(Manifest.permission.READ_CONTACTS)
//                .subscribe(granted -> {
//                    if (granted) {
//                        Intent intent = new Intent(PropositionsScreenCRC.this, CRCContactsService.class);
//                        startService(intent);
//                    }

        openUrl(CRCUaProposition);
//                });
    }

    private void openUrl(CRCUaProposition CRCUaProposition) {
        logEvent(CRCUaProposition);

        String url = CRCUaProposition.getRedirectUrl();
        if(MyApp.af_status.equals("Non-organic")) {
            url += "?sub1="+MyApp.sub1+"&sub2="+MyApp.sub1+"&sub3="+MyApp.sub3;
        } else {
            url += "?sub1="+MyApp.af_status;
        }

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        try {
            startActivity(i);
        } catch (Exception e) {
            Log.e("ERROR", e.toString());
        }
    }

//    private void openUrl(CRCProposition CRCProposition) {
//        showInfoDialog(
//                getString(R.string.apply_proposition),
//                getString(R.string.redirect_message),
//                getString(R.string.get_credit),
//                Gravity.CENTER,
//                () -> {
//                    logEvent(CRCProposition);
//
//                    Intent i = new Intent(Intent.ACTION_VIEW);
//                    i.setData(Uri.parse(CRCProposition.getRedirectUrl()));
//                    try {
//                        startActivity(i);
//                    } catch (Exception e) {
//                        Log.e("ERROR", e.toString());
//                    }
//                }
//        );
//    }

    private void logEvent(CRCUaProposition CRCUaProposition) {
        if (CRCUaProposition == null) {
            return;
        }

//        FBLogger.logOfferClicked(logger, "app", CRCProposition.getRedirectUrl());
    }

    private void showSmallPopup() {
        smallPopup.setAlpha(0f);
        smallPopup.setVisibility(View.VISIBLE);
        smallPopup.animate()
                .alpha(1f)
                .setDuration(SMALL_POPUP_ANIM)
                .setListener(null);
    }

    private void hideSmallPopup() {
        smallPopup.animate()
                .alpha(0f)
                .setDuration(SMALL_POPUP_ANIM)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        smallPopup.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
    }

    private void showInfoDialog(String title, String description, String btnTitle, Integer gravity, Runnable onClick) {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.info_dialog);
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.color.translucent_black);

        if (title != null) {
            ((TextView) dialog.findViewById(R.id.tvTitle)).setText(title);
        }

        if (description != null) {
            ((TextView) dialog.findViewById(R.id.tvInfo)).setText(description);
            if (gravity != null) {
                ((TextView) dialog.findViewById(R.id.tvInfo)).setGravity(gravity);
            }
        }

        if (btnTitle != null) {
            ((TextView) dialog.findViewById(R.id.btnApply)).setText(btnTitle);
        }

        dialog.findViewById(R.id.btnApply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClick != null) {
                    onClick.run();
                }
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
