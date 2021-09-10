package com.creativestudio.inlab.ui.live;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.creativestudio.inlab.R;
import com.creativestudio.inlab.model.modellogin.ModelLogin;
import com.creativestudio.inlab.utils.AppConsts;
import com.creativestudio.inlab.utils.ProjectUtils;
import com.creativestudio.inlab.utils.sharedpref.SharedPref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import us.zoom.sdk.JoinMeetingOptions;
import us.zoom.sdk.JoinMeetingParams;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.MeetingServiceListener;
import us.zoom.sdk.MeetingStatus;
import us.zoom.sdk.MeetingViewsOptions;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKInitParams;
import us.zoom.sdk.ZoomSDKInitializeListener;
import us.zoom.sdk.ZoomSDKRawDataMemoryMode;

public class ActivityLive extends AppCompatActivity implements ZoomSDKInitializeListener, MeetingServiceListener {
    Context mContext;
    MeetingService meetingService;
    ZoomSDKInitParams initParams;
    String domain = "zoom.us";//can't change it
    String sdkKey = "";
    String secretKey = "";
    ZoomSDK sdk;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    String numberMeeting = "";
    String passwordMeeting = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);
        mContext = ActivityLive.this;
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUser(AppConsts.STUDENT_DATA);


        if (getIntent().hasExtra("meetingPassword")) {
            passwordMeeting = getIntent().getStringExtra("meetingPassword");
            numberMeeting = getIntent().getStringExtra("meetingId");
            sdkKey = getIntent().getStringExtra("sdkKey");
            secretKey = getIntent().getStringExtra("sdkSecret");
        }
        Log.v("saloni123123","saloni  ---  "+passwordMeeting+"  "+numberMeeting+"  "+sdkKey+"  "+secretKey);
        if (numberMeeting.isEmpty()) {
            apiMeetingData();
        }
        sdk = ZoomSDK.getInstance();


        if (sdk.isInitialized()) {
            ZoomSDK.getInstance().getMeetingService().addListener(this);
            ZoomSDK.getInstance().getMeetingSettingsHelper().enable720p(true);
        }


        initParams = new ZoomSDKInitParams();

        initParams.enableLog = true;
        initParams.enableGenerateDump = true;

        initParams.logSize = 50;
        initParams.domain = "" + domain;

        initParams.appSecret = "" + secretKey;
        initParams.appKey = "" + sdkKey;

        initParams.videoRawDataMemoryMode = ZoomSDKRawDataMemoryMode.ZoomSDKRawDataMemoryModeStack;

        sdk.initialize(mContext, this, initParams);


    }

    void apiMeetingData() {

        ProjectUtils.showProgressDialog(mContext, false, getResources().getString(R.string.Loading___));
        AndroidNetworking.post(AppConsts.BASE_URL + AppConsts.API_LIVE_CLASS_DATA)
                .addBodyParameter(AppConsts.BATCH_ID, modelLogin.getStudentData().getBatchId())
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        ProjectUtils.pauseProgressDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (AppConsts.TRUE.equals("" + jsonObject.getString("status"))) {
                                JSONArray jsonArray = new JSONArray("" + jsonObject.getString("data"));
                                JSONObject jsonObject1 = new JSONObject("" + jsonArray.get(0));

                                numberMeeting = "" + jsonObject1.getString("meetingId");
                                passwordMeeting = "" + jsonObject1.getString("meetingPassword");
                                sdkKey = "" + jsonObject1.getString("sdkKey");
                                secretKey = "" + jsonObject1.getString("sdkSecret");

                            } else {
                                Toast.makeText(mContext, getResources().getString(R.string.NoClassAvailable), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        ProjectUtils.pauseProgressDialog();

                    }
                });
    }


    @Override
    public void onZoomSDKInitializeResult(int errorCode, int internalErrorCode) {


        meetingService = sdk.getMeetingService();

        JoinMeetingParams params = new JoinMeetingParams();
        String disName = "" + modelLogin.getStudentData().getFullName();
        params.displayName = "" + disName;
        params.meetingNo = numberMeeting;
        params.password = passwordMeeting;


        JoinMeetingOptions opts = new JoinMeetingOptions();

        opts.no_driving_mode = true;
        opts.no_invite = true;
        opts.meeting_views_options = MeetingViewsOptions.NO_TEXT_PASSWORD
                + MeetingViewsOptions.NO_TEXT_MEETING_ID;

        if (meetingService != null) {
            meetingService.joinMeetingWithParams(this, params, opts);

        }

        onBackPressed();


    }


    @Override
    public void onZoomAuthIdentityExpired() {

    }


    @Override
    public void onMeetingStatusChanged(MeetingStatus meetingStatus, int errorCode,
                                       int internalErrorCode) {

    }


}
