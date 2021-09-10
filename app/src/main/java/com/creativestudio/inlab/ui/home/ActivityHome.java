package com.creativestudio.inlab.ui.home;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.creativestudio.inlab.R;
import com.creativestudio.inlab.model.ModelDynamicNotices;
import com.creativestudio.inlab.model.ModelNewChanges;

import com.creativestudio.inlab.model.modelliveclassdata.ModelLiveClassData;
import com.creativestudio.inlab.model.modellogin.ModelLogin;

import com.creativestudio.inlab.model.modeltopscore.ModelTopScorer;

import com.creativestudio.inlab.ui.base.BaseActivity;
import com.creativestudio.inlab.ui.extraclass.ActivityExtraClass;
import com.creativestudio.inlab.ui.galary.galleryvideos.ActivityVimeoVideo;
import com.creativestudio.inlab.ui.home.adapter.HomeAdapter;
import com.creativestudio.inlab.ui.assignment.ActivityAssignment;
import com.creativestudio.inlab.ui.live.ActivityLive;
import com.creativestudio.inlab.ui.login.ActivityLogin;
import com.creativestudio.inlab.ui.mcq.practicepaper.ActivityPracticePaper;
import com.creativestudio.inlab.ui.mcq.practiceresult.ActivityPracticeResult;
import com.creativestudio.inlab.ui.noticeAnnouncement.ActivityForFragments;
import com.creativestudio.inlab.ui.settingdashboard.ActivitySettingDashboard;
import com.creativestudio.inlab.ui.UpcomingExams.ActivityVacancyOrUpcomingExam;
import com.creativestudio.inlab.ui.video.ActivityYoutubeVideo;
import com.creativestudio.inlab.utils.AppConsts;
import com.creativestudio.inlab.utils.ProjectUtils;
import com.creativestudio.inlab.utils.sharedpref.SharedPref;
import com.creativestudio.inlab.utils.widgets.CustomSmallText;
import com.creativestudio.inlab.utils.widgets.CustomTextBold;
import com.creativestudio.inlab.utils.widgets.CustomTextExtraBold;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


import de.hdodenhof.circleimageview.CircleImageView;

public class ActivityHome extends BaseActivity {

    Context mContext;
    ArrayList<String> listData;
    RecyclerView rvHome;
    RecyclerView rvNotice;
    RecyclerView rvRank;
    SharedPref sharedPref;
    CircleImageView profile;
    ModelLogin modelLogin;
    String numberMeeting = "";
    String passwordMeeting = "";
    String sdkKey = "";
    String secretKey = "";
    CustomTextExtraBold tvHeader;
    CustomSmallText extraCount;
    CustomSmallText mockCount;
    CustomSmallText vacancyCount;
    CustomSmallText extra;
    CustomSmallText mockPapers;
    CustomSmallText vacancy;
    CustomSmallText welcomeHead;
    CustomSmallText tvTodayDate;
    CustomSmallText tvTop;
    CustomSmallText tvLiveClasses;
    CustomSmallText tvTopic;
    CustomSmallText tvYourScore;
    SwipeRefreshLayout swipe;
    LinearLayout llLive, llExams, llMockPapers, llExtraClass;
    CustomSmallText nameTv;
    ImageView liveClassImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_newhome);
        mContext = ActivityHome.this;
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUser(AppConsts.STUDENT_DATA);

        try {
            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            int verCode = pInfo.versionCode;
            checkVersion(verCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        vacancy = findViewById(R.id.vacancy);
        tvLiveClasses = findViewById(R.id.tvLiveclasses);
        extraCount = findViewById(R.id.extraCount);
        mockCount = findViewById(R.id.mockCount);
        vacancyCount = findViewById(R.id.vacancyCount);
        nameTv = findViewById(R.id.nameTv);
        llLive = findViewById(R.id.llLive);
        liveClassImage = findViewById(R.id.liveClassImage);
        tvTop = findViewById(R.id.tvTop);
        tvTopic = findViewById(R.id.tvTopic);
        tvYourScore = findViewById(R.id.tvYourScore);
        tvTodayDate = findViewById(R.id.tvTodayDate);
        welcomeHead = findViewById(R.id.welcomhead);
        swipe = findViewById(R.id.swipe);


        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (ProjectUtils.checkConnection(mContext)) {

                    apiMeetingData();
                    newUpdates();
                    topScorer();
                    checkLogin();
                    addDataToList();

                    swipe.setRefreshing(false);
                } else {
                    swipe.setRefreshing(false);
                    Toast.makeText(mContext, getResources().getString(R.string.NoInternetConnection), Toast.LENGTH_SHORT).show();
                }
            }
        });
        rvNotice = findViewById(R.id.rvNotis);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvNotice.setLayoutManager(linearLayoutManager);
        extra = findViewById(R.id.extra);
        mockPapers = findViewById(R.id.mockPapers);

        init();

    }


    private void checkLogin() {

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {

                            return;
                        }

                        if (modelLogin != null) {
                            if (modelLogin.getStudentData() != null) {

                                AndroidNetworking.post(AppConsts.BASE_URL + AppConsts.CHECK_LOGIN)
                                        .addBodyParameter(AppConsts.STUDENT_ID, modelLogin.getStudentData().getStudentId())
                                        .addBodyParameter(AppConsts.BATCH_ID, modelLogin.getStudentData().getBatchId())
                                        .addBodyParameter(AppConsts.TOKEN, task.getResult().getToken())
                                        .setTag(AppConsts.CHECK_LOGIN)
                                        .setPriority(Priority.IMMEDIATE)
                                        .build()
                                        .getAsString(new StringRequestListener() {
                                            @Override
                                            public void onResponse(String response) {


                                                try {

                                                    JSONObject jsonObject = new JSONObject(response);

                                                    if ("false".equalsIgnoreCase(jsonObject.getString("status"))) {

                                                        sharedPref.clearAllPreferences();
                                                        Intent loginScreen = new Intent(mContext, ActivityLogin.class);
                                                        loginScreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        startActivity(loginScreen);
                                                        finish();
                                                    }


                                                } catch (Exception e) {

                                                }
                                            }

                                            @Override
                                            public void onError(ANError anError) {

                                            }
                                        });

                            } else {

                                Intent intent = new Intent(mContext, ActivityLogin.class);
                                intent.putExtra(AppConsts.IS_SPLASH, "true");
                                startActivity(intent);
                            }
                        } else {

                            Intent intent = new Intent(mContext, ActivityLogin.class);
                            intent.putExtra(AppConsts.IS_SPLASH, "true");
                            startActivity(intent);

                        }

                    }
                });


    }

    void newUpdates() {


        AndroidNetworking.post(AppConsts.BASE_URL + AppConsts.API_HOME_DB_NEW_CHANGES)
                .addBodyParameter(AppConsts.STUDENT_ID, "" + modelLogin.getStudentData().getStudentId())
                .addBodyParameter(AppConsts.BATCH_ID, "" + modelLogin.getStudentData().getBatchId())
                .addBodyParameter(AppConsts.IS_ADMIN, "" + modelLogin.getStudentData().getAdminId())
                .build()
                .getAsObject(ModelNewChanges.class, new ParsedRequestListener<ModelNewChanges>() {
                    @Override
                    public void onResponse(ModelNewChanges response) {

                        try {
                            tvTodayDate = findViewById(R.id.tvTodayDate);
                            welcomeHead = findViewById(R.id.welcomhead);
                            if (response.getStatus().equalsIgnoreCase("true")) {

                                tvTodayDate.setVisibility(View.VISIBLE);
                                welcomeHead.setVisibility(View.VISIBLE);
                                vacancyCount.setText("" + response.getVacancy().size());
                                extraCount.setText("" + response.getExtraClass().size());
                                mockCount.setText("" + response.getMockPaper().size());

                                vacancy.setText(getResources().getString(R.string.Exams));
                                extra.setText(getResources().getString(R.string.ExtraClass));
                                mockPapers.setText(getResources().getString(R.string.MockPaper));

                                ArrayList<ModelDynamicNotices> arrayList = new ArrayList<>();

                                String gender = "";
                                if (response.getExtraClass().size() > 0) {
                                    if (response.getExtraClass().get(0).getTeachGender().equalsIgnoreCase("male")) {
                                        gender = " " + getResources().getString(R.string.Sir);
                                    } else {
                                        gender = " " + getResources().getString(R.string.Madam);
                                    }
                                    arrayList.add(new ModelDynamicNotices(getResources().getString(R.string.ExtraClass) + " ( " + response.getExtraClass().size() + " )",
                                            "" + response.getExtraClass().get(0).getDescription(),
                                            getResources().getString(R.string.By) + " : " + response.getExtraClass().get(0).getName() + gender + "\n" + getResources().getString(R.string.Date) + " : " +
                                                    " " + response.getExtraClass().get(0).getDate(), "", "",""));
                                }
                                if (response.getHomeWork().size() > 0) {


                                    arrayList.add(new ModelDynamicNotices(getResources().getString(R.string.Assignment) + " ( " + response.getHomeWork().size() + " )", "" +
                                            response.getHomeWork().get(0).getDescription(),
                                            getResources().getString(R.string.Subject) + " : " + response.getHomeWork().get(0).getSubjectName() + "\n" + getResources().getString(R.string.By) + " : " + response.getHomeWork().get(0).
                                                    getName() + gender + "\n" + getResources().getString(R.string.Date) + " :" + response.getHomeWork().get(0).getDate(), "", "",""));
                                }

                                if (response.getVideoLecture().size() > 0) {

                                        arrayList.add(new ModelDynamicNotices(getResources().getString(R.string.VideoLectures) + " ( " + response.getVideoLecture().size() + " )",
                                                getResources().getString(R.string.Subject) + " : " + response.getVideoLecture().get(0).getSubject() +"\n"+ getResources().getString(R.string.Chapter) + " : " + response.getVideoLecture().get(0).getTopic(),
                                                getResources().getString(R.string.Title_of_video) + " : " + response.getVideoLecture().get(0).getTitle(), "" + response.getVideoLecture().get(0).getUrl(), "" + response.getVideoLecture().get(0).getVideoId(),response.getVideoLecture().get(0).getVideoType()));

                                }
                                if (response.getVacancy().size() > 0) {
                                    arrayList.add(new ModelDynamicNotices(getResources().getString(R.string.Upcoming_Exams) + " ( " + response.getVacancy().size() + " )",

                                            getResources().getString(R.string.Exams) + "  : " + response.getVacancy().get(0).getTitle() + "\n" + response.getVacancy().get(0).getDescription(),
                                            "\n" + getResources().getString(R.string.Mode_of_exam) + " :" +
                                                    " " + response.getVacancy().get(0).getMode() + "\n" + getResources().getString(R.string.Date) + " " + response.getVacancy().get(0).getStartDate(), "", "",""));
                                }
                                if (response.getNotices().size() > 0) {
                                    arrayList.add(new ModelDynamicNotices(getResources().getString(R.string.Notices) + " ( " + response.getNotices().size() + " )", "" + response.getNotices().get(0).getTitle(),
                                            "" + response.getNotices().get(0).getDescription(), "", "",""));
                                }
                                if (response.getPracticePaper().size() > 0) {
                                    arrayList.add(new ModelDynamicNotices(getResources().getString(R.string.Practice_Paper) + " ( " + response.getPracticePaper().size() + " )", "" + response.getPracticePaper().get(0).getName(),
                                            getResources().getString(R.string.TotalQuestions) + " : " + response.getPracticePaper().get(0).getTotalQuestion() + "" +
                                                    "\n" + getResources().getString(R.string.Duration) + " : " + response.getPracticePaper().get(0).getTimeDuration(), "", "",""));
                                }
                                if (response.getMockPaper().size() > 0) {
                                    arrayList.add(new ModelDynamicNotices(getResources().getString(R.string.MockPaper) + "  ( " + response.getMockPaper().size() + " )", "" + response.getMockPaper().get(0).getName(), getResources().getString(R.string.TotalQuestions) + "" +
                                            " : " + response.getMockPaper().get(0).getTotalQuestion() + "\n" + getResources().getString(R.string.Duration) +
                                            "\n : " + response.getMockPaper().get(0).getTimeDuration() + "\n" + getResources().getString(R.string.Date) + "/" + getResources().getString(R.string.Time) + "  " + response.getMockPaper().get(0).getMockSheduledDate() + " " + response.getMockPaper().get(0).getMockSheduledTime(), "", "",""));
                                }
                                AdapterList adapterList = new AdapterList(arrayList);
                                rvNotice.setAdapter(adapterList);

                                if (arrayList.size() == 0) {
                                    if (tvTodayDate != null && welcomeHead != null) {
                                        welcomeHead.setVisibility(View.GONE);
                                        tvTodayDate.setVisibility(View.GONE);
                                    }

                                } else {
                                    if (tvTodayDate != null && welcomeHead != null) {
                                        welcomeHead.setVisibility(View.VISIBLE);
                                        tvTodayDate.setVisibility(View.VISIBLE);
                                    }
                                }

                            } else {
                                if (tvTodayDate != null && welcomeHead != null) {
                                    welcomeHead.setVisibility(View.GONE);
                                    tvTodayDate.setVisibility(View.GONE);
                                }

                            }
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                        Toast.makeText(mContext, "" + getResources().getString(R.string.Try_again), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void checkVersion(int verCode) {
        AndroidNetworking.get(AppConsts.BASE_URL + AppConsts.API_WELCOME)
                .setTag(AppConsts.API_WELCOME)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject mainJson = new JSONObject(response);
                            if (mainJson.getString("status").equalsIgnoreCase("true")) {
                                if (verCode != Integer.parseInt(mainJson.getString("version").trim())) {
                                    openAppUpdateDialog();
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }

    private void openAppUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(false);
        builder.setMessage(getResources().getString(R.string.Please_update_your_app_you_are_using_older_version))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        final String appPackageName = getPackageName(); // package name of the app
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException e) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();

    }


    public String getCurrentDate(String outputFormat) {
        DateFormat dateFormat = new SimpleDateFormat(outputFormat);
        Date date = new Date();
        return dateFormat.format(date);
    }


    private void init() {

        welcomeHead = findViewById(R.id.welcomhead);
        rvRank = findViewById(R.id.rvRank);
        tvTodayDate = findViewById(R.id.tvTodayDate);
        llExams = findViewById(R.id.llExams);
        llMockPapers = findViewById(R.id.llMockPapers);
        llExtraClass = findViewById(R.id.llExtraClass);

        llExams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(mContext, ActivityVacancyOrUpcomingExam.class));
            }
        });
        llMockPapers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(mContext, ActivityPracticePaper.class).putExtra(AppConsts.EXAM_TYPE, "mock_test"));

            }
        });
        llExtraClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(mContext, ActivityExtraClass.class));
            }
        });
        newUpdates();
        Calendar cal = Calendar.getInstance();
        String format = new SimpleDateFormat("E, MMM d, yyyy").format(cal.getTime());

        tvTodayDate.setText("" + format);
        rvHome = (RecyclerView) findViewById(R.id.rvHome);
        profile = (CircleImageView) findViewById(R.id.profile);
        tvHeader = (CustomTextExtraBold) findViewById(R.id.tvHeader);
        rvHome.setLayoutManager(new GridLayoutManager(mContext, 2));
        rvRank.setLayoutManager(new GridLayoutManager(mContext, 3));
        profile = (CircleImageView) findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ActivitySettingDashboard.class);
                startActivity(intent);
            }
        });
        try {
            tvHeader.setTextSize(18f);
            tvHeader.setText("" + modelLogin.getStudentData().getFullName());

        } catch (Exception e) {
        }
        if (!modelLogin.getStudentData().getImage().isEmpty()) {
            Picasso.get().load(modelLogin.getStudentData().getImage()).placeholder(R.drawable.ic_profile).into(profile);
        }

        addDataToList();


        topScorer();
    }


    @Override
    protected void onResume() {
        super.onResume();

        modelLogin = sharedPref.getUser(AppConsts.STUDENT_DATA);

        apiMeetingData();
        apiLiveClass();

        try {
            tvHeader.setTextSize(18f);
            tvHeader.setText("" + modelLogin.getStudentData().getFullName());

        } catch (Exception e) {
        }
        if (!modelLogin.getStudentData().getImage().isEmpty()) {
            Picasso.get().load("" + modelLogin.getStudentData().getImage()).placeholder(R.drawable.ic_profile).into(profile);
        }
        newUpdates();

        if (rvHome != null) {
            addDataToList();
        }

    }


    void topScorer() {


        AndroidNetworking.post(AppConsts.BASE_URL + AppConsts.API_GET_TOP_SCORER)
                .addBodyParameter(AppConsts.BATCH_ID, modelLogin.getStudentData().getBatchId())
                .build()
                .getAsObject(ModelTopScorer.class, new ParsedRequestListener<ModelTopScorer>() {
                    @Override
                    public void onResponse(ModelTopScorer response) {

                        if (AppConsts.TRUE.equals(response.getStatus())) {
                            tvTop.setVisibility(View.VISIBLE);
                            tvYourScore.setVisibility(View.VISIBLE);
                            AdapterListTopScorer adapterList = new AdapterListTopScorer(response.getTopThree(), response.getFilesUrl());
                            rvRank.setAdapter(adapterList);

                            tvYourScore.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(mContext, ActivityPracticeResult.class);
                                    intent.putExtra(AppConsts.EXAM_TYPE, "mock_test");
                                    startActivity(intent);
                                }
                            });
                        } else {
                            tvTop.setVisibility(View.GONE);
                            tvYourScore.setVisibility(View.GONE);
                            ArrayList<ModelTopScorer.TopThree> strings = new ArrayList<>();
                            AdapterListTopScorer adapterList = new AdapterListTopScorer(strings, "");
                            rvRank.setAdapter(adapterList);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });

    }

    void apiMeetingData() {

        AndroidNetworking.post(AppConsts.BASE_URL + AppConsts.API_LIVE_CLASS_DATA)
                .addBodyParameter(AppConsts.BATCH_ID, modelLogin.getStudentData().getBatchId())
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {


                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (AppConsts.TRUE.equals("" + jsonObject.getString("status"))) {
                                JSONArray jsonArray = new JSONArray("" + jsonObject.getString("data"));
                                JSONObject jsonObject1 = new JSONObject("" + jsonArray.get(0));
                                if (jsonObject1.has("meetingNumber")) {
                                    numberMeeting = "" + jsonObject1.getString("meetingNumber");
                                    passwordMeeting = "" + jsonObject1.getString("password");
                                    sdkKey = "" + jsonObject1.getString("sdkKey");
                                    secretKey = "" + jsonObject1.getString("sdkSecret");
                                }
                                apiLiveClass();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {


                    }
                });
    }

    void apiLiveClass() {


        AndroidNetworking.post(AppConsts.BASE_URL + AppConsts.API_CHECK_ACTIVE_LIVE_CLASS)
                .addBodyParameter(AppConsts.BATCH_ID, modelLogin.getStudentData().getBatchId())
                .build()
                .getAsObject(ModelLiveClassData.class, new ParsedRequestListener<ModelLiveClassData>() {
                    @Override
                    public void onResponse(ModelLiveClassData response) {

                        if (AppConsts.TRUE.equals(response.getStatus())) {
                            llLive.setVisibility(View.VISIBLE);
                            tvLiveClasses.setVisibility(View.VISIBLE);
                            if (response.getLiveClass() != null) {
                                nameTv.setText("" + response.getLiveClass().getName());
                                tvTopic.setText("" + response.getLiveClass().getSubjectName() + " (" + response.getLiveClass().getChapterName() + ")");
                                Picasso.get().load(response.getFilesUrl() + response.getLiveClass().getTeachImage()).placeholder(R.drawable.noimage).into(liveClassImage);
                                llLive.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        if (!numberMeeting.isEmpty()) {
                                            mContext.startActivity(new Intent(mContext, ActivityLive.class).
                                                    putExtra("meetingId", "" + numberMeeting).putExtra("meetingPassword", "" + passwordMeeting).putExtra("sdkKey", "" + sdkKey)
                                                    .putExtra("sdkSecret", "" + secretKey));
                                        }
                                    }
                                });
                            }

                        } else {
                            llLive.setVisibility(View.GONE);
                            tvLiveClasses.setVisibility(View.GONE);
                        }


                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });


    }


    private void addDataToList() {
        listData = new ArrayList<>();

        listData.add(0, getResources().getString(R.string.Announcements));
        listData.add(1, getResources().getString(R.string.Live_class));
        listData.add(getResources().getString(R.string.mcq));
        listData.add(getResources().getString(R.string.Upcoming_Exams));
        listData.add(getResources().getString(R.string.VideoLectures));
        listData.add(getResources().getString(R.string.Assignment));
        listData.add(getResources().getString(R.string.ExtraClass));
//        listData.add(getResources().getString(R.string.Payment));
//        listData.add(getResources().getString(R.string.DoubtClasses));
        listData.add(getResources().getString(R.string.Attendance));


        HomeAdapter homeAdapter = new HomeAdapter(mContext, listData);

        rvHome.setAdapter(homeAdapter);

    }

    private void exitAppDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(getResources().getString(R.string.Are_you_sure_you_want_to_close_this_app))
                .setCancelable(false)
                .setTitle(getResources().getString(R.string.app_name))
                .setPositiveButton(getResources().getString(R.string.Yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.No), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });


        AlertDialog alertDialog = builder.create();


        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                if (modelLogin.getStudentData().getLanguageName().equalsIgnoreCase("arabic")) {
                    alertDialog.findViewById(android.R.id.message).setTextDirection(View.TEXT_DIRECTION_RTL);
                }

                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));

            }
        });
        alertDialog.show();


    }

    @Override
    public void onBackPressed() {

        exitAppDialog();
    }








    /* -----------------------------------------------CountDownTimer Methods------------------------------------------------------------ */

    public class AdapterList extends RecyclerView.Adapter<HolderAdapterQuestionList> {

        ArrayList<ModelDynamicNotices> list;
        View view;

        public AdapterList(ArrayList<ModelDynamicNotices> list) {
            this.list = list;


        }


        @Override
        public HolderAdapterQuestionList onCreateViewHolder(ViewGroup viewGroup, int i) {
            view = LayoutInflater.from(mContext).inflate(R.layout.dynamic, viewGroup, false);

            return new HolderAdapterQuestionList(view);

        }

        @Override
        public void onBindViewHolder(HolderAdapterQuestionList holder, int i) {

            holder.title.setText("" + list.get(i).getName());
            holder.description.setText("" + list.get(i).getDescp());
            holder.nameOther.setText("" + list.get(i).getOther());


            holder.rlDynamic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (list.get(i).getName().contains(getResources().getString(R.string.ExtraClass))) {
                        mContext.startActivity(new Intent(mContext, ActivityExtraClass.class).putExtra("idNotice", "" + list.get(i).getId()));
                    }
                    if (list.get(i).getName().contains(getResources().getString(R.string.Assignment))) {
                        mContext.startActivity(new Intent(mContext, ActivityAssignment.class).putExtra("date", "" + holder.nameOther.getText()).putExtra("idNotice", "" + list.get(i).getId()));
                    }
                    if (list.get(i).getName().contains(getResources().getString(R.string.VideoLectures))) {

                        if (list.get(i).getVideoType().equalsIgnoreCase("youtube")) {
                            mContext.startActivity(new Intent(mContext, ActivityYoutubeVideo.class).putExtra("firebase/notice", "" + list.get(i).getOther()).putExtra("vId", "" + list.get(i).getVideoId()));
                        } else {
                            mContext.startActivity(new Intent(mContext, ActivityVimeoVideo.class).putExtra("firebase/notice", "" + list.get(i).getOther()).putExtra("WEB_URL", "" + list.get(i).getId()).putExtra("type",list.get(i).getVideoType()));
                        }
                    }
                    if (list.get(i).getName().contains(getResources().getString(R.string.Upcoming_Exams))) {
                        mContext.startActivity(new Intent(mContext, ActivityVacancyOrUpcomingExam.class).putExtra("idNotice", "" + list.get(i).getId()));
                    }
                    if (list.get(i).getName().contains(getResources().getString(R.string.Notices))) {
                        mContext.startActivity(new Intent(mContext, ActivityForFragments.class).putExtra("idNotice", "" + list.get(i).getId()));
                    }
                    if (list.get(i).getName().contains("" + getResources().getString(R.string.Practice_Paper))) {
                        mContext.startActivity(new Intent(mContext, ActivityPracticePaper.class).putExtra(AppConsts.EXAM_TYPE, "practice").putExtra("idNotice", "" + list.get(i).getId()));
                    }
                    if (list.get(i).getName().contains(getResources().getString(R.string.MockPaper))) {
                        mContext.startActivity(new Intent(mContext, ActivityPracticePaper.class).putExtra(AppConsts.EXAM_TYPE, "mock_test").putExtra("idNotice", "" + list.get(i).getId()));
                    }

                }
            });
        }


        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

    }

    public class HolderAdapterQuestionList extends RecyclerView.ViewHolder {

        CustomSmallText description;
        CustomSmallText nameOther;
        CustomTextBold title;
        TextView seeAllTv;
        RelativeLayout rlDynamic;

        public HolderAdapterQuestionList(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            welcomeHead = itemView.findViewById(R.id.welcomhead);
            seeAllTv = itemView.findViewById(R.id.seeAllTv);
            description = itemView.findViewById(R.id.descrip);
            nameOther = itemView.findViewById(R.id.nameOther);
            rlDynamic = itemView.findViewById(R.id.rlDynamic);

        }
    }


    public class AdapterListTopScorer extends RecyclerView.Adapter<HolderAdapterTopScoreList> {

        ArrayList<ModelTopScorer.TopThree> listTopScore;
        View view;
        String url;

        public AdapterListTopScorer(ArrayList<ModelTopScorer.TopThree> list, String url) {
            this.listTopScore = list;
            this.url = url;


        }


        @Override
        public HolderAdapterTopScoreList onCreateViewHolder(ViewGroup viewGroup, int i) {
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_top_scorer, viewGroup, false);
            return new HolderAdapterTopScoreList(view);

        }

        @Override
        public void onBindViewHolder(HolderAdapterTopScoreList holder, int i) {
            if (!url.isEmpty()) {
                try {
                    holder.tvName.setText("" + listTopScore.get(i).getName());
                    holder.tvPaperName.setText("" + listTopScore.get(i).getPaperName());
                    double percent = Double.parseDouble(listTopScore.get(i).getPercentage());


                    holder.tvPercent.setText("" + new DecimalFormat("##.##").format(percent) + " %");
                    int countRank = i + 1;
                    if (countRank == 1) {
                        holder.rankText.setText("" + countRank + "st");
                    }
                    if (countRank == 2) {
                        holder.rankText.setText("2nd");
                    }
                    if (countRank == 3) {
                        holder.rankText.setText("3rd");
                    }
                    Picasso.get().load(url + listTopScore.get(i).getImage()).placeholder(R.drawable.ic_profile).into(holder.profileTopScore);
                } catch (Exception e) {

                }

            }
        }


        @Override
        public int getItemCount() {
            return listTopScore.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

    }

    public class HolderAdapterTopScoreList extends RecyclerView.ViewHolder {
        CustomSmallText tvName;
        CustomSmallText tvPaperName;
        CustomSmallText rankText;
        CustomSmallText tvPercent;
        CircleImageView profileTopScore;

        public HolderAdapterTopScoreList(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPaperName = itemView.findViewById(R.id.tvPaperName);
            tvPercent = itemView.findViewById(R.id.tvPercent);
            rankText = itemView.findViewById(R.id.rankText);
            profileTopScore = itemView.findViewById(R.id.profileTopScore);


        }
    }


}
