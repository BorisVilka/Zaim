package com.app.hroshidozarplatni.api;

import com.app.hroshidozarplatni.models.CRCUaProposition;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CRCUaAPIService {

//    @POST("api-reg")
//    Single<CRCResponse> sendRegister(@Query("app_id") String appId, @Body CRCRegister object);
//
//    @POST("api-contacts")
//    Single<CRCResponse> sendContacts(@Query("app_id") String appId, @Body CRCPhoneContacts object);

    @GET("api-offers")
    Single<List<CRCUaProposition>> getPropositions(@Query("app_id") String appI1d, @Query("appsflyer_id") String appId,@Query("sub1") String sub1,@Query("sub2") String sub2,@Query("sub3") String sub3,@Query("key") String key,@Query("idfa") String idfa);

    @GET("api-offers")
    Single<List<CRCUaProposition>> getPropositions1(@Query("app_id") String appI1d,@Query("sub1") String sub1);


}
