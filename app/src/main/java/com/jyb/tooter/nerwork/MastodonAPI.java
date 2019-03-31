package com.jyb.tooter.nerwork;


import com.jyb.tooter.entity.AccessToken;
import com.jyb.tooter.entity.Account;
import com.jyb.tooter.entity.Media;
import com.jyb.tooter.entity.Notification;
import com.jyb.tooter.entity.OAuth;
import com.jyb.tooter.entity.Relationship;
import com.jyb.tooter.entity.Status;
import com.jyb.tooter.entity.StatusContext;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MastodonAPI {

    String ENDPOINT_AUTHORIZE = "/oauth/authorize";

    @FormUrlEncoded
    @POST("api/v1/apps")
    Call<OAuth> oauth(
            @Field("client_name") String clientName,
            @Field("redirect_uris") String redirectUris,
            @Field("scopes") String scopes,
            @Field("website") String website);
    @FormUrlEncoded
    @POST("oauth/token")
    Call<AccessToken> token(
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret,
            @Field("redirect_uri") String redirectUri,
            @Field("code") String code,
            @Field("grant_type") String grantType
    );

    @GET("api/v1/timelines/home")
    Call<List<Status>> timelineHome(
            @Query("local") Boolean local,
            @Query("only_media") Boolean media,
            @Query("max_id") String maxId,
            @Query("since_id") String sinceId,
            @Query("limit") Integer limit);
    @GET("api/v1/timelines/home")
    Call<List<Status>> timelineHome(
            @Query("max_id") String maxId,
            @Query("since_id") String sinceId,
            @Query("limit") Integer limit);
    @GET("api/v1/timelines/public")
    Call<List<Status>> timelinePublic(
            @Query("local") Boolean local,
            @Query("max_id") String maxId,
            @Query("since_id") String sinceId,
            @Query("limit") Integer limit);
    @GET("api/v1/timelines/tag/{hashtag}")
    Call<List<Status>> hashtagTimeline(
            @Path("hashtag") String hashtag,
            @Query("local") Boolean local,
            @Query("max_id") String maxId,
            @Query("since_id") String sinceId,
            @Query("limit") Integer limit);

    @GET("api/v1/notifications")
    Call<List<Notification>> notifications(
            @Query("max_id") String maxId,
            @Query("since_id") String sinceId,
            @Query("limit") Integer limit);
    @POST("api/v1/notifications/clear")
    Call<ResponseBody> clearNotifications();
    @GET("api/v1/notifications/{id}")
    Call<Notification> notification(@Path("id") String notificationId);

    @Multipart
    @POST("api/v1/media")
    Call<Media> uploadMedia(@Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("api/v1/statuses")
    Call<Status> postStatus(
            @Field("status") String text,
            @Field("in_reply_to_id") String inReplyToId,
            @Field("spoiler_text") String spoilerText,
            @Field("visibility") String visibility,
            @Field("sensitive") Boolean sensitive,
            @Field("media_ids[]") List<String> mediaIds);
    @GET("api/v1/statuses/{id}")
    Call<Status> status(@Path("id") String statusId);
    @GET("api/v1/statuses/{id}/context")
    Call<StatusContext> statusContext(@Path("id") String statusId);
    @GET("api/v1/statuses/{id}/reblogged_by")
    Call<List<Account>> statusRebloggedBy(
            @Path("id") String statusId,
            @Query("max_id") String maxId,
            @Query("since_id") String sinceId,
            @Query("limit") Integer limit);
    @GET("api/v1/statuses/{id}/favourited_by")
    Call<List<Account>> statusFavouritedBy(
            @Path("id") String statusId,
            @Query("max_id") String maxId,
            @Query("since_id") String sinceId,
            @Query("limit") Integer limit);
    @DELETE("api/v1/statuses/{id}")
    Call<ResponseBody> deleteStatus(@Path("id") String statusId);
    @POST("api/v1/statuses/{id}/reblog")
    Call<Status> reblogStatus(@Path("id") String statusId);
    @POST("api/v1/statuses/{id}/unreblog")
    Call<Status> unreblogStatus(@Path("id") String statusId);
    @POST("api/v1/statuses/{id}/favourite")
    Call<Status> favouriteStatus(@Path("id") String statusId);
    @POST("api/v1/statuses/{id}/unfavourite")
    Call<Status> unfavouriteStatus(@Path("id") String statusId);

    @GET("api/v1/accounts/verify_credentials")
    Call<Account> accountVerifyCredentials();
    @GET("api/v1/accounts/search")
    Call<List<Account>> searchAccounts(
            @Query("q") String q,
            @Query("resolve") Boolean resolve,
            @Query("limit") Integer limit);
    @GET("api/v1/accounts/{id}")
    Call<Account> account(@Path("id") String accountId);
    @GET("api/v1/accounts/{id}/statuses")
    Call<List<Status>> accountStatuses(
            @Path("id") String accountId,
            @Query("max_id") String maxId,
            @Query("since_id") String sinceId,
            @Query("limit") Integer limit);
    @GET("api/v1/accounts/{id}/followers")
    Call<List<Account>> accountFollowers(
            @Path("id") String accountId,
            @Query("max_id") String maxId,
            @Query("since_id") String sinceId,
            @Query("limit") Integer limit);
    @GET("api/v1/accounts/{id}/following")
    Call<List<Account>> accountFollowing(
            @Path("id") String accountId,
            @Query("max_id") String maxId,
            @Query("since_id") String sinceId,
            @Query("limit") Integer limit);
    @POST("api/v1/accounts/{id}/follow")
    Call<Relationship> followAccount(@Path("id") String accountId);
    @POST("api/v1/accounts/{id}/unfollow")
    Call<Relationship> unfollowAccount(@Path("id") String accountId);
    @POST("api/v1/accounts/{id}/block")
    Call<Relationship> blockAccount(@Path("id") String accountId);
    @POST("api/v1/accounts/{id}/unblock")
    Call<Relationship> unblockAccount(@Path("id") String accountId);
    @POST("api/v1/accounts/{id}/mute")
    Call<Relationship> muteAccount(@Path("id") String accountId);
    @POST("api/v1/accounts/{id}/unmute")
    Call<Relationship> unmuteAccount(@Path("id") String accountId);

    @GET("api/v1/accounts/relationships")
    Call<List<Relationship>> relationships(@Query("id[]") List<String> accountIds);

    @GET("api/v1/blocks")
    Call<List<Account>> blocks(
            @Query("max_id") String maxId,
            @Query("since_id") String sinceId,
            @Query("limit") Integer limit);

    @GET("api/v1/mutes")
    Call<List<Account>> mutes(
            @Query("max_id") String maxId,
            @Query("since_id") String sinceId,
            @Query("limit") Integer limit);

    @GET("api/v1/favourites")
    Call<List<Status>> favourites(
            @Query("max_id") String maxId,
            @Query("since_id") String sinceId,
            @Query("limit") Integer limit);

    @GET("api/v1/follow_requests")
    Call<List<Account>> followRequests(
            @Query("max_id") String maxId,
            @Query("since_id") String sinceId,
            @Query("limit") Integer limit);
    @POST("api/v1/follow_requests/{id}/authorize")
    Call<Relationship> authorizeFollowRequest(@Path("id") String accountId);
    @POST("api/v1/follow_requests/{id}/reject")
    Call<Relationship> rejectFollowRequest(@Path("id") String accountId);

    @FormUrlEncoded
    @POST("api/v1/reports")
    Call<ResponseBody> report(@Field("account_id") String accountId, @Field("status_ids[]") List<String> statusIds, @Field("comment") String comment);

}
